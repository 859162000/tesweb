package com.dc.tes.adapter.remote;
  
import java.io.ByteArrayInputStream;
import java.net.Socket;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.dc.tes.adapter.helper.IAdapterHelper;
import com.dc.tes.adapter.util.ConfigHelper;
import com.dc.tes.net.jre14.Message;
import com.dc.tes.net.jre14.MessageType;
import com.dc.tes.net.jre14.ReplyMessage;

/**
 * "远程通道"方式, 通信层与核心进行数据交互 抽象公共类
 * 
 * @author guhb,王春佳
 *
 * @see 功能：向核心注册、注销
 */
public class AbstractAdapterHelper implements IAdapterHelper{
	
	private static Log log = LogFactory.getLog(AbstractAdapterHelper.class);

	//配置信息名值对
	protected Properties m_props = null;

	//本地配置文件，存放适配器端与核心通信的参数
	protected String m_coreIP = "";
	protected int m_corePort = -1;
	protected String m_localChannelName = "";
	protected String m_localChannelType = "";
	protected String m_localChannelIP = "127.0.0.1"; //监听TES的本地IP, 对发起端需要重置
	protected int m_localChannelPort = -1; //监听TES的本地端口, 对发起端需要重置
	
	protected final Message m_msgOfUnReg = new Message(MessageType.UNREG);
	
	protected final Message m_msgOfReg = new Message(MessageType.REG);
	private ReplyMessage m_msgOfRegReply = null;
	
	
	protected final Message m_msgOfData = new Message(MessageType.MESSAGE);
	protected ReplyMessage m_msgOfDataReply = null;
	

	/**
	 * 构造函数 只允许继承构造
	 * 
	 * @param props 本地配置参数
	 * @throws Exception 
	 */
	protected AbstractAdapterHelper(Properties props){		
		m_props = props;
		
		m_localChannelName = props.getProperty("CHANNELNAME");
		if(props.containsKey("SIMTYPE"))
			m_localChannelType = props.getProperty("SIMTYPE");
		
		m_coreIP = props.getProperty("coreIP");
		if(null == m_localChannelName
				|| null == m_coreIP)
			log.error("配置文件信息缺少必要信息!");
		try{
			m_corePort = Integer.parseInt(props.getProperty("corePort"));
			if(m_localChannelType.toLowerCase().endsWith("c")) //发起端
				if(props.containsKey("host"))
					m_localChannelIP = props.getProperty("host");
				if(props.containsKey("UpPort"))
					m_localChannelPort = Integer.parseInt(props.getProperty("UpPort"));
		}
		catch(Exception ex){
			log.error("配置文件信息中端口号非法!" + ex.getMessage());
		}
	}
	
	
	public void unReg2TES(){		
		try{	// 建立到适配器的连接
			Socket socket = new Socket(m_coreIP, m_corePort);

			m_msgOfUnReg.put("CHANNELNAME", m_localChannelName.getBytes(ConfigHelper.getEncoding()));
			AbstractAdapterHelper.SendToCore(socket, m_msgOfUnReg.Export(), m_coreIP, m_corePort);
		}catch(Exception e){
			log.error("0x0D17：向核心发送注销消息失败！["+e.getMessage()+"]");
		}
	}
	
	public byte[] reg2TES(){		
		try{	// 建立到适配器的连接
			Socket socket = new Socket(m_coreIP, m_corePort);
			
			m_msgOfReg.put("CHANNELNAME", m_localChannelName.getBytes(ConfigHelper.getEncoding()));
			m_msgOfReg.put("SIMTYPE", m_localChannelType.getBytes(ConfigHelper.getEncoding()));
			if(m_localChannelType.toLowerCase().endsWith("c")) //发起端
			{
				m_msgOfReg.put("HOST", m_localChannelIP);
				m_msgOfReg.put("PORT", m_localChannelPort);
			}
			m_msgOfRegReply = AbstractAdapterHelper.SendToCore(socket, m_msgOfReg.Export(), m_coreIP, m_corePort);
		}catch(Exception e){
			m_msgOfRegReply = new ReplyMessage(MessageType.MESSAGE);
			m_msgOfRegReply.setEx(e, "向核心发送注册消息失败");
			
			log.error("0x0D17：向核心发送注册消息失败！["+e.getMessage()+"]");
		}
		
		
		//给适配器返回应答信息
		if(m_msgOfRegReply.isOK()){
			return m_msgOfRegReply.configInfo();
		}
		else{
			return m_msgOfRegReply.errorInfo();
		}
	}
	
	/**
	 * 向指定地址发送报文，报文内容需要使用者自己构造。
	 * 
	 * @param message	要发送给目标主机的真实报文
	 * @param IP	远端主机的IP地址
	 * @param port	远端主机的端口
	 * @return	远端主机返回的报文，出错则返回错误内容
	 * @throws Exception 任何报文转换或者通信中发生的异常
	 */
	protected final static ReplyMessage SendToCore(Socket socket, byte[] message, String coreIP, int corePort) throws Exception{
		
		byte[] recvbuf = null;
		log.debug("开始向：[" + coreIP + ":" + corePort + "]发送报文：" + new Message(new ByteArrayInputStream((message))));
		System.out.println(new String(message, "gb2312"));
		// 向核心发报文
		socket.getOutputStream().write(message);
		// 从核心取响应
		ReplyMessage recvMsg = new ReplyMessage(socket.getInputStream());

		recvbuf = recvMsg.Export();

		log.debug("发送完毕.收到核心响应数据: " + new String(recvbuf, "utf-8"));
		
		return recvMsg;
	}
}
