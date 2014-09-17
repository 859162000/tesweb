package com.dc.tes.adapterlib;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.dc.tes.adapter.context.IReplyAdapterEnvContext;
import com.dc.tes.adapter.helper.IReplyAdapterHelper;



public class MMLTCPReplyAdapterWorkerThread  extends Thread{
	private static final Log log = LogFactory.getLog(MMLTCPReplyAdapterWorkerThread.class);


	/**
	 * 与被测系统连接的Socket
	 */
	private Socket m_socket = null;

	private IReplyAdapterHelper m_adpHelper = null;
	

	//是否为长连接形式
	private int m_ISLAST=1;
	//是否为定长报文，大于零的值表示为定长报文，且报文长度为该值，小于等于零的值表示为变长报文
	private int m_ISFIX=0;
	//表示为了要获取变长报文的长度信息需要预先接收的长度
	private int m_LEN4LEN=10;
	//报文长度信息在报文中的开始位置
	private int m_LENSTART=0;
	//报文长度信息的长度
	private int m_LENLEN=10;
	//是否需要返回报文，主要针对异步通讯模式下，如果异步模式下无需在只读连接下返回则设置为0
	private int m_NEEDBACK=1;
	//每次是否返回固定报文
	private int m_FIXBACK=0;	
	
	private int m_MAX_LEN = 1024*1024*1;

	/**
	 * 初始化一条用于处理请求的线程
	 * 
	 * @param socket
	 *            在其上进行操作的Socket
	 * @param env 
	 * @param helper 
	 */
	public MMLTCPReplyAdapterWorkerThread(Socket socket, IReplyAdapterEnvContext env, IReplyAdapterHelper helper) {
		this.m_socket = socket;		
		
		Properties props = ConfigHelper.getConfig(env.getEvnContext());
		this.m_ISLAST = Integer.parseInt((String) props.get("ISLAST"));
		this.m_ISFIX = Integer.parseInt((String) props.get("ISFIX"));
		this.m_LEN4LEN = Integer.parseInt((String) props.get("LEN4LEN"));
		this.m_LENSTART = Integer.parseInt((String) props.get("LENSTART"));
		this.m_LENLEN = Integer.parseInt((String) props.get("LENLEN"));
		this.m_NEEDBACK = Integer.parseInt((String) props.get("NEEDBACK"));
		this.m_FIXBACK = Integer.parseInt((String) props.get("FIXBACK"));
		
		log.debug("响应端适配器工作线程环境：" + props);

		m_adpHelper = helper;
	}

	public void run() {
		//处理被测系统的请求
		try {
			doWork();
		} catch (Exception ex) {
			log.error("处理与被测系统的消息通信时发生异常", ex);
		}	
		finally{
			try {
				m_socket.close();
			} catch (IOException ex1) {
				log.error("关闭远程通道Socket时发生异常", ex1);
			}
		}
	}
	
	private void doWork() throws Exception{
		long inTime = System.currentTimeMillis();		//接到请求的时间
		long outTime = -1;								//核心处理完成的时间
		long usedTime = 0;								//核心处理时间
		int delayTime = -1;								//系统返回的延时时间

		byte[] inMsg = null;	//请求消息体
		byte[] outMsg = null;	//返回消息体
		do{
			if (m_ISFIX>0){
				//定长报文处理流程
					inMsg = this.recv4len(m_ISFIX);				
					log.debug("接收到的定长报文长度：["+inMsg.length+"]");
					log.debug("接收到的定长报文内容：["+(new String(inMsg))+"]");
			} else {
				//变长报文处理流程
				byte[] buff = null;
				byte[] trans = new byte[this.m_LENLEN];
				int lenlen = -1;	//报文中长度信息的长度
				try{
					//1）先收取长度信息
					buff = this.recv4len(this.m_LEN4LEN);
					System.arraycopy(buff, this.m_LENSTART, trans, 0, this.m_LENLEN);
//					log.debug("响应端适配器接收到长度头："  + new String(trans));
					
					//消息开始标志用于确定消息的开始和结束，长度为4Byte。编码为：`SC`。
					if(!new String(buff, 0, 4).equals("`SC`"))
					{
						log.error("不符合MML报文格式，消息开始标志不正确!");
						continue;
					}
					
					//消息长度,长度值用16进制字符(0-F)表示的4位整数来表示。
					//消息长度取值范围为0到65000(0000-FDE8)
					lenlen = Integer.parseInt((new String(trans)).trim(), 16);
					log.debug("解析SC报文获得消息长度：" + lenlen);
					
				}catch(IndexOutOfBoundsException e){
					log.error("0x0D06：获取报文长度信息时，数组写操作越界！["+e.getMessage()+"]");
					throw new Exception("");
				}catch(ArrayStoreException e){
					log.error("0x0D07：获取报文长度信息时，向数组中写入不匹配的类型！["+e.getMessage()+"]");
					throw new Exception("");
				}catch(NullPointerException e){
					log.error("0x0D08：获取报文长度信息时，操作的数组为空！["+e.getMessage()+"]");
					throw new Exception("");
				}catch(NumberFormatException e){
					log.error("0x0D09：获取报文长度信息时，报文中["+(new String(trans))+"]包含的长度信息有误！["+e.getMessage()+"]");
					throw new Exception("");
				} 
				
				//2）收取完整报文
				try{
					inMsg = this.recv4len(lenlen);
				}catch(Exception e){
					log.error("报文接收过程中发生异常：" + e.getMessage());
					throw new Exception("报文接收过程中发生异常：" + e.getMessage());
				}	
				
				//如果长度为4，则判断为心跳请求
				if(4 == lenlen && new String(inMsg).equals("HBHB"))
				{
					log.debug("收到心跳消息:" + inMsg);
					byte[] responsMsg = new String("`SC`0004HBHB").getBytes();
					OutputStream out = m_socket.getOutputStream();
					log.debug("给被测系统返回报文:["+new String(responsMsg)+"]");
					out.write(responsMsg);
					out.flush();
					
					continue; //不用转发给TES
				}
				

				//3) 收取校验和8Byte
				byte[] checkCode = null;
				try{
					checkCode = this.recv4len(8);
				}catch(Exception e){
					log.error("报文接收过程中发生异常：" + e.getMessage());
					throw new Exception("报文接收过程中发生异常：" + e.getMessage());
				}	
				//

				//转发给TES
				if(log.isDebugEnabled())
					System.out.println("接收到被测系统消息：" + new String(inMsg));
				outMsg = m_adpHelper.sendToCore(inMsg);
				if(log.isDebugEnabled())
					System.out.println("向被测系统发送TES响应消息：" + new String(outMsg));
			}//

			//处理延时
			outTime = System.currentTimeMillis();
			usedTime = outTime - inTime;
			log.debug("核心的处理时间：["+usedTime+"]");
			if (usedTime < delayTime)
			{
				log.debug("进行延时处理：["+(delayTime-usedTime)+"]"+delayTime+"-"+usedTime);
				try {
					Thread.sleep(delayTime-usedTime);
				} catch (InterruptedException e) {
					log.error("0x0803：适配器延时处理失败！["+e.getMessage()+"]");
					throw new Exception("");
				}
			}

			try {
				if (this.m_NEEDBACK > 0){
					//进行报文返回
					OutputStream out = m_socket.getOutputStream();
					if (this.m_FIXBACK > 0){
//						log.debug("返回报文:["+new String(this.tpi.backmessage)+"]");
//						out.write(this.tpi.backmessage);
//						out.flush();
						log.error("目前不支持本地文件返回!");
					}
					else{						
						//核心组包时添加了报文头
						byte[] realMsg = outMsg;
						//给返回消息内容添加包头
//						byte[] realMsg = new byte[outMsg.length + m_LEN4LEN];
//						System.arraycopy(MMLTCPReplyAdapterWorkerThread.FixLength(String.valueOf(realMsg.length), m_LEN4LEN, '0', true).getBytes(), 0, realMsg, 0, m_LEN4LEN);
//						System.arraycopy(outMsg, 0, realMsg, m_LEN4LEN, outMsg.length);
						log.debug("给被测系统返回报文:["+new String(realMsg)+"]");
						out.write(realMsg);
						out.flush();
					}
				}
			} catch (IOException e) {
				log.error("0x0D22：报文返回给被测系统过程出现异常！["+e.getMessage()+"]");
				throw new Exception("");
			}
		}while(1==this.m_ISLAST);//判断是否为长连接

		m_socket.close();
	}
	
	

	/**
	 * 按照长度接收报文
	 * @param len 要接收的报文长度
	 * @return	接收到的报文
	 * @throws Exception
	 */
	private byte[] recv4len(int len) throws Exception{
		if(len > m_MAX_LEN){
			//防止后面的内存申请失败
			throw new Exception("试图接收的报文长度超过阈值：" + len);
		}
		
		
		log.debug("开始接收"+len+"个字节的报文。");
		
		byte[] trans = null;
		try { 
			int nsize, tsize, recvlen;
			nsize = 0;
			tsize = len;
			trans = new byte[tsize];
			InputStream in = m_socket.getInputStream();
			while (nsize < tsize) { //当nsize==tsize正常退出
				recvlen = in.read(trans, nsize, tsize - nsize);
				if (recvlen < 0) {
					log.error("剩余:" + (tsize - nsize) + "接收失败!");
					throw new Exception();
				}
				else{
					nsize += recvlen;
					log.debug("本次收到"+recvlen+"字节");
					log.debug("本次收到["+new String(trans)+"]");
				}
			}
			log.debug("本次报文接收总长度："+tsize + " 实际接收长度:" + nsize);
			log.debug("本次接收报文内容：" + new String(trans));
			return trans;
		} catch (IOException e) {
			log.error("0x0D22：报文接收过程中出现异常！["+e.getMessage()+"]");
			throw new Exception("0x0D22：报文接收过程中出现异常！["+e.getMessage()+"]");
		}
		
		
	}
	
	/**
	 * 工具函数 产生固定长度的字符串。
	 * 如果src的长度比length参数大，返回原始src，否则将在前（或后）填补padding字符。
	 * @param src 源字符串
	 * @param length 期望的长度
	 * @param padding 填补的字符
	 * @param leadingPad =true在最前面填补, =false在最后面填补。
	 * @return 填补以后的字符串
	 */
	public static String FixLength(String src, int length, char padding, boolean leadingPad) {
		if (src == null) {
			src = "";
		}
		if (length <= src.length()) {
			return src;
		}
		StringBuffer sb = new StringBuffer(src);
		for (int i = src.length(), j = length; i < j; i++) {
			if (leadingPad) {
				sb.insert(0, padding);
			} else {
				sb.append(padding);
			}
		}
		return sb.toString();
	} 
}
