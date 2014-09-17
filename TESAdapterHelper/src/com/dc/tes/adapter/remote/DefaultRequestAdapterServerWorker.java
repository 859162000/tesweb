package com.dc.tes.adapter.remote;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.dc.tes.adapter.IRequestAdapter;
import com.dc.tes.adapter.IRequestAdapterWorker;
import com.dc.tes.net.jre14.Message;
import com.dc.tes.net.jre14.MessageType;
import com.dc.tes.net.jre14.ReplyMessage;
import com.dc.tes.adapterlib.*;

/**
 * TES框架中的工作线程，处理TES通过适配器转发消息的中转调度，通过主动调用发起端适配器接口实现。
 * 
 * @author guhb,王春佳
 * 
 * @see 多线程调用发起端适配器接口实例,向被测系统发送请求报文
 * 
 */
public class DefaultRequestAdapterServerWorker extends Thread {
	
	private static final Log log = LogFactory.getLog(DefaultRequestAdapterServerWorker.class);

	protected String m_localChannelName = "";
	private boolean m_isRecording = false;
	Properties m_adapter_config_props = null;
	//private byte[] m_realMsg = null; // 需要转发给被测系统的真实报文
	
	/**
	 * 与TES通信的socket
	 */
	private Socket m_socket;

	/**
	 * 发起端适配器对象
	 */
	protected IRequestAdapter m_adapterInstance = null;

	private static Object lock = new Object();
	
	/**
	 * 初始化一条用于处理远程适配器请求的线程
	 * 
	 * @param socket
	 *            在其上进行操作的Socket
	 * @param props
	 */
	
	public DefaultRequestAdapterServerWorker(String channel, Socket socket,	IRequestAdapter sa) {
		m_localChannelName = channel;
		this.m_socket = socket;
		m_adapterInstance = sa;
	}
	
	public DefaultRequestAdapterServerWorker(String channel, Socket socket,	IRequestAdapter sa, boolean isRecording) {
		m_localChannelName = channel;
		this.m_socket = socket;
		m_adapterInstance = sa;
		this.m_isRecording = isRecording; 
	}
	
	public DefaultRequestAdapterServerWorker(boolean isRecording, Properties props, IRequestAdapter sa) {
		m_isRecording = isRecording;
		m_adapter_config_props = props;
		m_adapterInstance = sa;
	}

	
	/**
	 * 流程：接到从核心来的需要发给被测系统的报文，转发，接收应答报文，回复给TES
	 */
	public void run() {

		byte[] realMsg = null; // 从被测系统收到的真实应答报文
		ReplyMessage msgToTES = null; // 回复TES的报文
		
		try {
			if (!m_isRecording) {
				log.debug("从TES地址：" + this.m_socket.getRemoteSocketAddress()	+ " 接收消息...");
				// 接收需要转发的请求报文
				InputStream s = this.m_socket.getInputStream();
				Message msgFromTES = new Message(s);
				realMsg = msgFromTES.getBytes("REQMESSAGE");	
				log.debug("待转发的请求报文：" + msgFromTES);
				sendMsg(realMsg);
			}
			else{
				if (m_adapterInstance.AdapterType().startsWith("tcp")) { 
					RecvAndSendTcpMsg();
				}
				else if (m_adapterInstance.AdapterType().startsWith("mq")) {
					RecvAndSendMqMsg();
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			log.error("发起端适配器工作发生异常", ex);
			msgToTES = new ReplyMessage(MessageType.MESSAGE);
			msgToTES.setEx(ex, "发起端适配器工作发生异常");
			if (!m_isRecording) {
				Reply(msgToTES);
			}
			try {
				this.m_socket.close();
			} catch (IOException ex1) {
				log.error("关闭远程通道Socket时发生异常", ex1);
			}
		} finally {
			if ((!m_isRecording || ((TCPRequestAdapterPlugin)m_adapterInstance).m_ISLAST == 1) && !m_socket.isConnected()) {
				try {
					this.m_socket.close();
				} catch (IOException ex1) {
					log.error("再次尝试关闭远程通道Socket时发生异常", ex1);
				}
			}
		}
	}

	public byte[] sendMsg(byte[] realMsg) {
		
		byte[] recvMsg = null; // 从被测系统收到的真实应答报文
		
		try {
			// 调用发起端适配器实现的接口				
			synchronized(lock) { 
				recvMsg = m_adapterInstance.Send(realMsg);
			}
			// 表示适配器通信出现异常，详细信息
			// 异步返回不允许是null
			if (null != recvMsg) {
				//log.info("发起端适配器没有获得到被测系统的响应报文.");
				String recvMsgStr = new String(recvMsg, "utf-8");
				System.out.println("发起端适配器接收被测系统报文结果为：" + recvMsgStr);
				log.debug("TES适配器运行时收到发起端适配器返回的消息：" + recvMsgStr);
				//throw new Exception("发起端适配器接收被测系统报文结果为null");
			}

			if (!m_isRecording) {
				ReplyToTES(recvMsg);
			}
			else {
				ReplyToSender(recvMsg);
			}

			// 支持多笔应答包
			if (m_adapterInstance instanceof IRequestAdapterWorker) {
				IRequestAdapterWorker adpterWorker = (IRequestAdapterWorker) m_adapterInstance;
				while (!adpterWorker.IsLast()) {
					recvMsg = adpterWorker.GetResponse();
					
					// 表示适配器通信出现异常，详细信息
					// 异步返回不允许是null
					if (null == recvMsg) {
						log.error("发起端适配器无法获得被测系统分笔响应报文.");
						throw new Exception("发起端适配器接收被测系统分笔响应报文结果为null");
					}
					log.debug("TES适配器运行时收到发起端适配器分笔返回的消息：" + new String(recvMsg));
					if (!m_isRecording) {
						ReplyToTES(recvMsg);
					}
					else {
						ReplyToSender(recvMsg);
					}
				}
			}
		} catch (Exception ex) {
			// 不会到这里,因为Send不可能抛异常？
			log.error("发起端适配器插件向被测系统发送数据失败！[" + ex.getMessage() + "]");
		}
		log.debug("TES发起端适配器运行时完成向核心应答。");
		return recvMsg;
	}
	
	
	private void ReplyToSender(byte[] recvMsg) {
		if (m_adapterInstance.AdapterType().equals("tcp.c") &&
				TCPRequestAdapterPlugin.da.GetSystem().getIsSyncComm() == 1) { //同步的tcp通讯
			try {
				Reply2TcpSender(this.m_socket.getOutputStream(), recvMsg);
			}
			catch(Exception e) {
				e.printStackTrace();
			}
		}
		/*else if (m_adapterInstance.AdapterType().equals("mq.c")) {
			
		}*/
	}
	
	/**
	 * 向被测系统发信息
	 * @throws IOException 
	 * @throws IOException 
	 */
	public void Reply2TcpSender(OutputStream out, byte[] msg) throws IOException{
	
		int m_ISFIX = ((TCPRequestAdapterPlugin)m_adapterInstance).m_ISFIX;
		int m_LEN4LEN = ((TCPRequestAdapterPlugin)m_adapterInstance).m_LEN4LEN;
		int m_LENSTART = ((TCPRequestAdapterPlugin)m_adapterInstance).m_LENSTART;
		boolean m_ISTOTALLEN = ((TCPRequestAdapterPlugin)m_adapterInstance).m_ISTOTALLEN;
		boolean m_NEEDPREFIX = ((TCPRequestAdapterPlugin)m_adapterInstance).m_NEEDPREFIX;
		String m_PREFIX = ((TCPRequestAdapterPlugin)m_adapterInstance).m_PREFIX;
		String m_ENCODING = ((TCPRequestAdapterPlugin)m_adapterInstance).m_ENCODING;
		char m_Padding = ((TCPRequestAdapterPlugin)m_adapterInstance).m_Padding;
		boolean m_Align = ((TCPRequestAdapterPlugin)m_adapterInstance).m_Align;
		
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
	}

		
	private void ReplyToTES(byte[] recvMsg) {
		if (recvMsg == null) {
			return;
		}
		// 构造同步回复TES的报文
		ReplyMessage msgToTES = new ReplyMessage(MessageType.MESSAGE);
		msgToTES.put("RESMESSAGE", recvMsg);
		try {
			msgToTES.put("CHANNELNAME", this.m_localChannelName);
			log.debug("TES发起端适配器运行时向核心发送应答：" + new String(msgToTES.Export(), "GBK"));
			Reply(msgToTES);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 工具函数：应答TES的请求
	 * 
	 * @param msg
	 *            应答报文
	 */
	private void Reply(Message msg) {
		try {
			// 向socket中写入返回报文
			// 如果写的过程中出现异常 则把该异常抓掉 并记日志
			this.m_socket.getOutputStream().write(msg.Export());
		} catch (Exception ex) {
			log.error("向远程通道中写入时发生异常", ex);
		} finally {
			// 关掉socket
			try {
				this.m_socket.close();
			} catch (IOException ex) {
				log.error("关闭远程通道Socket连接时发生异常", ex);
			}
		}
	}
	
	public void RecvAndSendMqMsg() throws IOException {
		while (true) {
			byte[] msg2Send = ((MQRequestAdapterPlugin)m_adapterInstance).ReadFromRecorderMQ();
			if (msg2Send != null && msg2Send.length > 0) {
				try {
					((MQRequestAdapterPlugin)m_adapterInstance).Send(msg2Send);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			else {
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * 从被测系统接收信息
	 * @throws IOException 
	 */
	public void RecvAndSendTcpMsg() throws IOException {
		byte[] inMsg = null;	//请求消息体
		
		int m_ISLAST = ((TCPRequestAdapterPlugin)m_adapterInstance).m_ISLAST;
		int m_ISFIX = ((TCPRequestAdapterPlugin)m_adapterInstance).m_ISFIX;
		int m_LENLEN = ((TCPRequestAdapterPlugin)m_adapterInstance).m_LENLEN;
		int m_LEN4LEN = ((TCPRequestAdapterPlugin)m_adapterInstance).m_LEN4LEN;
		int m_LENSTART = ((TCPRequestAdapterPlugin)m_adapterInstance).m_LENSTART;
		boolean m_ISTOTALLEN = ((TCPRequestAdapterPlugin)m_adapterInstance).m_ISTOTALLEN;
		
		do {
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
				byte[] trans = new byte[m_LENLEN];
				int lenlen = -1;	//报文中长度信息的长度
				try{
					//先收取长度信息
					inLenMsg = this.recv4len(m_LEN4LEN);
					log.debug("接收到被测系统的应答报文长度信息：" + new String(inLenMsg));
					System.arraycopy(inLenMsg, m_LENSTART, trans, 0, m_LENLEN);
					lenlen = Integer.parseInt((new String(trans)).trim());
					if (m_ISTOTALLEN && lenlen<m_LEN4LEN){
						log.error("0x0D09：报文长度信息长度["+lenlen+"]小于报文头长度["+m_LEN4LEN+"]");
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
					return;
				}		
				try{
					//修正剩余字节数长度
					if (m_ISTOTALLEN && lenlen>=m_LEN4LEN) {
						lenlen = lenlen-m_LEN4LEN; 
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
					return;
				}
			}//变长报文处理完毕
			//return inMsg;
			sendMsg(inMsg);
		}
		while(1==m_ISLAST);//判断是否为长连接
	}
	
	
	/**
	 * 按照长度接收报文
	 * @param len 要接收的报文长度
	 * @return	接收到的报文
	 * @throws Exception
	 */
	private byte[] recv4len(int len) throws Exception{

		if(len > ((TCPRequestAdapterPlugin)m_adapterInstance).m_MAX_LEN){
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
