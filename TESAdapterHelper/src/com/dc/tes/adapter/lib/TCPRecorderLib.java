package com.dc.tes.adapter.lib;


import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Properties;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.dc.tes.adapter.IRequestAdapter;
import com.dc.tes.adapter.remote.DefaultRequestAdapterServerWorker;
import com.dc.tes.adapterlib.TCPRequestAdapterPlugin;


public class TCPRecorderLib extends Thread {

	public final static Log log = LogFactory.getLog(TCPRecorderLib.class);
	
	protected static IRequestAdapter m_adapterPluginInstance = null;
	
	/**
	 * 绑定的监听端口
	 */
	protected int m_lsrPort = -1;
	
	private ServerSocket m_lsrSocket = null; 
	/**
	 * 服务器运行状态, =true表示运行态
	 */
	private boolean m_serverState = true;
	
	private String m_targetIP = null;
	private int m_targetPort = -1;
	
	public Socket m_socket = null;

	/*
	private int m_MAX_LEN = 1024*1024*1; //报文缓冲区长度
	
	//是否为定长报文，大于零的值表示为定长报文，且报文长度为该值，小于等于零的值表示为变长报文
	private int m_ISFIX=0;
	//表示为了要获取变长报文的长度信息需要预先接收的长度
	private int m_LEN4LEN=10;
	//报文长度信息在报文中的开始位置
	private int m_LENSTART=0;
	//报文前缀内容，长度=LEN4LEN-LENSTART
	private String m_PREFIX="";
	//报文长度信息的长度
	private int m_LENLEN=10;
	//预先接收的字节长度是否包含在报文长度中
	private boolean m_ISTOTALLEN = true;
	
	//返回报文是否需要适配器端添加报文头(动态长度字节)
	private boolean m_NEEDPREFIX = false; 
	//变成报文长度信息不够长时应填充的字符
	private char m_Padding = '0';
	//报文长度信息对其方式
	private boolean m_Align = true; //false -> true: 4930 -> 0493
	
	private String m_ENCODING = "utf-8"; */
	
	private static Properties m_config_props = null;
	
	
	/*
	 * private int m_timeOut = 10*60*1000; //阻塞读取超时,毫秒，缺省10分钟

	//是否为长连接形式
	private int m_ISLAST=0;

	//是否需要返回报文，主要针对异步通讯模式下，如果异步模式下无需在只读连接下返回则设置为0
	private int m_NEEDBACK=1;

	//是否需要心跳测试
	private int m_NEEDPULSE = 0;
	
	private String m_secureFactoryPackage = "com.dc.tes.adapter.secure.factory.";
	*/
	
	public TCPRecorderLib(Properties props, IRequestAdapter api) {
		super();
		m_config_props = props;
		m_adapterPluginInstance = api;
		Init(props);
	}
	
	public void run() {
		if (m_adapterPluginInstance.AdapterType().equals("tcp.c")) { //作为发送端的辅助工具，替发送端接收请求报文
			if (m_targetPort < 0 && TCPRequestAdapterPlugin.da.GetSystem().getIsSyncComm() == 1) {
				if(m_config_props.containsKey("targetPort")) {
					this.m_targetPort = Integer.parseInt(m_config_props.getProperty("targetPort"));
				}
			}
		}
		else {
			return;
		}
		
		log.info("开始监听" + ":" + m_targetPort + "……");
		try {
			//监听来自任意IP的请求报文
			m_lsrSocket = new ServerSocket(m_targetPort, 0, null);
			while (m_serverState) { //外部开关
				//Socket socket = null;
				try {
					this.m_socket = m_lsrSocket.accept();
					new DefaultRequestAdapterServerWorker(null, this.m_socket, m_adapterPluginInstance, true).start();
				} 
				catch(SocketTimeoutException e){//超时后判断是否被终止
//					System.out.println("TCP响应端适配器等待超时，继续……");
					continue;
				}
				catch (IOException ex) {
					// 如果m_serverState为false 则表示这个accept()的异常是由ServerSocket被关闭引起的 这个异常是正常流程 不进行任何处理 否则需要将其抛出
					if (!m_serverState){
						log.info("发起端适配器服务器主动停止服务." + ex.getMessage());
						return; //主动退出
					}
					else
						throw new RuntimeException(ex);
				}			
				/*byte[] realMsg = recvMsg();
				if (realMsg == null) {
					continue;
				}*/
				// 处理请求 每个请求起一个新线程
				//DefaultRequestAdapterServerWorker requestAdapterSvrWorker = 
				/*//把消息发送给真正的Tcp Server
				byte[] recvMsg = requestAdapterSvrWorker.sendMsg(realMsg);
				if (recvMsg != null && m_adapterPluginInstance.AdapterType().equals("tcp.c") &&
						TCPRequestAdapterPlugin.da.GetSystem().getIsSyncComm() == 1) {
					ReplyToSender(this.m_socket.getOutputStream(), recvMsg);
				}*/
				catch (Exception ex) { //accept出现异常
					if(!m_serverState){
						log.info("TCP适配器服务器主动停止服务." + ex.getMessage());
						break;
					}
				}
			}
		}
		catch(IOException e){
			log.error("发起端适配器创建本地监听失败.[" + e.getMessage() + "]");
			//System.out.println("发起端适配器创建本地监听失败.[" + e.getMessage() + "]");
			e.printStackTrace();
		}
	}

	
	/*
	 * 检查配置信息是否完整
	 * 
	 * 
	 */
	public boolean Init(Properties props) {
		
		m_config_props = props;
		
		/*if(props.containsKey("ISFIX"))
			this.m_ISFIX = Integer.parseInt((String) props.get("ISFIX"));
		if(props.containsKey("LEN4LEN"))
			this.m_LEN4LEN = Integer.parseInt((String) props.get("LEN4LEN"));
		if(props.containsKey("LENSTART"))
			this.m_LENSTART = Integer.parseInt((String) props.get("LENSTART"));
		if(props.containsKey("m_PREFIX"))
			this.m_PREFIX = (String) props.get("m_PREFIX");
		if(props.containsKey("LENLEN"))
			this.m_LENLEN = Integer.parseInt((String) props.get("LENLEN"));
		if(props.containsKey("ISTOTALLEN"))
			this.m_ISTOTALLEN = Integer.parseInt((String) props.get("ISTOTALLEN"))==1;
		if(props.containsKey("PADDING")) {
			String padding = ((String) props.get("PADDING"));
			padding = padding.replaceAll("\"", "");
			this.m_Padding = padding.toCharArray()[0];
		}
		if(props.containsKey("ALIGN"))
			this.m_Align= Boolean.parseBoolean(((String) props.get("ALIGN")));
		if(props.containsKey("NEEDPREFIX"))
			this.m_NEEDPREFIX = Integer.parseInt((String) props.get("NEEDPREFIX"))==1;
		if (props.containsKey("ENCODING"))
			this.m_ENCODING = (String) props.get("ENCODING");*/
		
		
		//必须有的配置项
		if(!props.containsKey("targetIP") ||!props.containsKey("targetPort")) {
			log.error("发起端适配器缺少必备配置项[targetIP或者targetPort]");
			return false;
		}
		m_targetIP = (String) props.get("targetIP");	
		m_targetPort = Integer.parseInt((String) props.get("targetPort"));	
		
		log.debug("发起端目标地址" + m_targetIP + ":" + m_targetPort);
		
		return true;
	}
	

	/**
	 * 从被测系统接收信息
	 * @throws IOException 
	 */
	/*public byte[] recvMsg() throws IOException {
		byte[] inMsg = null;	//请求消息体
			
		if (m_ISFIX>0){
			//定长报文处理流程
			try{
				inMsg = this.recv4len(m_ISFIX);				
				log.debug("接收到的定长报文长度：["+inMsg.length+"]");
				log.debug("接收到的定长报文内容：["+(new String(inMsg))+"]");
			} catch(Exception e){
				log.error("error: " + e.getMessage());
				m_socket.close();
			}
		} else {
			//变长报文处理流程
			byte[] inLenMsg = null;
			byte[] trans = new byte[this.m_LENLEN];
			int lenlen = -1;	//报文中长度信息的长度
			try{
				//先收取长度信息
				inLenMsg = this.recv4len(this.m_LEN4LEN);
				log.debug("接收到被测系统的应答报文长度信息：" + new String(inLenMsg));
				System.arraycopy(inLenMsg, this.m_LENSTART, trans, 0, this.m_LENLEN);
				lenlen = Integer.parseInt((new String(trans)).trim());
				if (m_ISTOTALLEN && lenlen<this.m_LEN4LEN){
					log.error("0x0D09：报文长度信息长度["+lenlen+"]小于报文头长度["+this.m_LEN4LEN+"]");
					m_socket.close();
				}
			}catch(IndexOutOfBoundsException e){
				log.error("0x0D06：获取报文长度信息时，数组写操作越界！["+e.getMessage()+"]");
				m_socket.close();
			}catch(ArrayStoreException e){
				log.error("0x0D07：获取报文长度信息时，向数组中写入不匹配的类型！["+e.getMessage()+"]");
				m_socket.close();
			}catch(NullPointerException e){
				log.error("0x0D08：获取报文长度信息时，操作的数组为空！["+e.getMessage()+"]");
				m_socket.close();
			}catch(NumberFormatException e){
				log.error("0x0D09：获取报文长度信息时，报文中["+(new String(trans))+"]包含的长度信息有误！["+e.getMessage()+"]");
				m_socket.close();
			} catch(Exception e){
				log.error(e.getMessage());
				m_socket.close();
				return null;
			}		
			try{
				//修正剩余字节数长度
				if (m_ISTOTALLEN && lenlen>=this.m_LEN4LEN) {
					lenlen = lenlen-this.m_LEN4LEN; 
				}
				//收取完整报文
				try{
					inMsg = this.recv4len(lenlen);
				}catch(Exception e){
					log.error("报文接收过程中发生异常：" + e.getMessage());
					throw new Exception("报文接收过程中发生异常：" + e.getMessage());
				}
			} catch(Exception e){
				log.error(e.getMessage());
				m_socket.close();
				return null;
			}
		}//变长报文处理完毕

		return inMsg;
	}*/
	
	
	/**
	 * 按照长度接收报文
	 * @param len 要接收的报文长度
	 * @return	接收到的报文
	 * @throws Exception
	 */
	/*private byte[] recv4len(int len) throws Exception{
		if(len > m_MAX_LEN){
			//防止后面的内存申请失败
			throw new Exception("试图发送的报文长度超过阈值：" + len);
		}
		
		log.debug("接收"+len+"个字节的报文。");
		byte[] trans = null;
		try { 
			int nsize, tsize, recvlen;
			nsize = 0;
			tsize = len;
			trans = new byte[tsize];
			InputStream in = m_socket.getInputStream();
			while (nsize < tsize) { //当nsize==tsize正常退出
				recvlen = in.read(trans, nsize, tsize - nsize);
				if (recvlen == -1) {
					log.warn("剩余:" + (tsize - nsize) + "接收失败!");
					break; //支持同步短连接的非定长无自描述长度头
				}
				else{
					nsize += recvlen;
					log.debug("本次收到"+recvlen+"字节["+new String(trans)+"]");
				}
			}
			log.debug("本次报文接收总长度："+tsize 
					+ " 实际接收长度:" + nsize 
					+ "["+new String(trans)+"]");
			return trans;
		} catch (Exception e) {
			log.error("0x0D22：报文接收过程中出现异常！["+e.getMessage()+"]");
			throw new Exception("报文接收过程中出现异常！["+e.getMessage()+"]");
		}
	}*/


	/**
	 * 向被测系统发信息
	 * @throws IOException 
	 * @throws IOException 
	 */
	/*public void ReplyToSender(OutputStream out, byte[] msg) throws IOException{
	
		byte[] realMsg = msg;

		if(m_NEEDPREFIX && m_ISFIX <= 0 ){ //变长报文 
			//按照与被测系统之间的通信协议进行打包			
			realMsg = new byte[msg.length + m_LEN4LEN];		
			log.debug("发起端适配器对变长报文添加报文头：" + FixLength(String.valueOf(m_ISTOTALLEN ? realMsg.length : realMsg.length - m_LEN4LEN), m_LEN4LEN, m_Padding, m_Align));
			System.arraycopy(FixLength(String.valueOf(m_ISTOTALLEN ? realMsg.length : realMsg.length - m_LEN4LEN), m_LEN4LEN, m_Padding, m_Align).getBytes(), 0, realMsg, 0, m_LEN4LEN);
			if(m_LENSTART>0 && m_PREFIX.length()==m_LENSTART)
				System.arraycopy(m_PREFIX, 0, realMsg, 0, m_LENSTART);
			System.arraycopy(msg, 0, realMsg, m_LEN4LEN, msg.length);
		}
		out.write(realMsg);
		String strMsgSended = new String(realMsg, m_ENCODING);
		log.debug("向发起方转发的真实报文：" +  strMsgSended);
		System.out.println(strMsgSended);
	}*/
	
	/**
	 * 工具函数 产生固定长度的字符串。
	 * 如果src的长度比length参数大，返回原始src，否则将在前（或后）填补padding字符。
	 * @param src 源字符串
	 * @param length 期望的长度
	 * @param padding 填补的字符
	 * @param leadingPad =true在最前面填补, =false在最后面填补。
	 * @return 填补以后的字符串
	 */
	/*public static String FixLength(String src, int length, char padding, boolean leadingPad) {
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
	}*/
	
}
