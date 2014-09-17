package com.dc.tes.adapterlib;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.dc.tes.Config;
import com.dc.tes.adapter.IRequestAdapter;
import com.dc.tes.adapter.IRequestAdapterWorker;
import com.dc.tes.adapter.context.IAdapterEnvContext;
import com.dc.tes.adapter.context.IRequestAdapterEnvContext;
import com.dc.tes.adapter.secure.AbstractFactory;
import com.dc.tes.adapter.secure.IDecryptAdapterSecure;
import com.dc.tes.adapter.secure.IEncryptAdapterSecure;
import com.dc.tes.data.IRuntimeDAL;
import com.dc.tes.data.RuntimeDAL;


public class TCPRequestAdapterPlugin implements IRequestAdapter, IRequestAdapterWorker{
	
	public final static Log log = LogFactory.getLog(TCPRequestAdapterPlugin.class);

	private IRequestAdapterEnvContext m_TESEnv = null;

	/** 核心基础配置 */
	public static Config m_config;

	/** 运行时数据访问接口 */
	public static IRuntimeDAL da;
	
	public int m_MAX_LEN = 1024*1024*1; //报文缓冲区长度

	private String m_targetIP = null;
	private int m_targetPort = -1;
	
	private Socket m_socket = null;
	private int m_timeOut = 10*60*1000; //阻塞读取超时,毫秒，缺省10分钟

	//是否为长连接形式
	public int m_ISLAST=0;
	//是否为定长报文，大于零的值表示为定长报文，且报文长度为该值，小于等于零的值表示为变长报文
	public int m_ISFIX=0;
	//表示为了要获取变长报文的长度信息需要预先接收的长度
	public int m_LEN4LEN=10;
	//报文长度信息在报文中的开始位置
	public int m_LENSTART=0;
	//报文前缀内容，长度=LEN4LEN-LENSTART
	public String m_PREFIX="";
	//报文长度信息的长度
	public int m_LENLEN=10;
	//预先接收的字节长度是否包含在报文长度中
	public boolean m_ISTOTALLEN = true;
	
	//是否需要返回报文，主要针对异步通讯模式下，如果异步模式下无需在只读连接下返回则设置为0
	public int m_NEEDBACK=1;
	//返回报文是否需要适配器端添加报文头(动态长度字节)
	public boolean m_NEEDPREFIX = false; 
	//变成报文长度信息不够长时应填充的字符
	public char m_Padding = '0';
	//报文长度信息对其方式
	public boolean m_Align = true; //false -> true: 4930 -> 0493
	//是否需要心跳测试
	private int m_NEEDPULSE = 0;
	
	public String m_ENCODING = "utf-8";
	
	//处于处于录制状态
	private boolean m_RECORDING = false;
	private int m_SystemId = 0;
	private String m_SystemName = "";
	private int m_NewRecordedCaseId = 0;
	private int m_RecordUserId = 0;
	
	// 是否调用安全服务处理接收报文,解密操作,0否 1是
	private boolean m_needDecrypt = false;
	
	// 是否调用安全服务处理返回报文,加密操作,0否 1是
	private boolean m_needEncrypt = false;

	// 处理加解密报文的插件类名 HttpReplyFactory\SoapReplyFactory\TcpReplyFactory\HttpRequestFactory\SoapRequestFactory\TcpRequestFactory 中的一种
	private String m_CryptClsName = "";

	// HttpReplyFactory\SoapReplyFactory\TcpReplyFactory\HttpRequestFactory\SoapRequestFactory\TcpRequestFactory 包名
	private String m_secureFactoryPackage = "com.dc.tes.adapter.secure.factory.";

	private long m_TimeOfAcceptResponse = 0; //接收到被测系统请求的时间
	
	private Properties m_config_props = null;
	
	@Override
	public Properties GetAdapterConfigProperties() {
		return m_config_props;
	}
	
	/*
	 * 检查配置信息是否完整
	 * 
	 * 
	 */
	public boolean Init(IAdapterEnvContext tesENV) {
		
		log.debug("发起端适配器插件" + this.getClass().getName() + "被初始化……");
		
		m_TESEnv = (IRequestAdapterEnvContext) tesENV;
		
		Properties props = ConfigHelper.getConfig(m_TESEnv.getEvnContext());
		m_config_props = props; 
		
		if (props.containsKey("ISLAST"))
			this.m_ISLAST = Integer.parseInt((String) props.get("ISLAST"));
		if (props.containsKey("ISFIX"))
			this.m_ISFIX = Integer.parseInt((String) props.get("ISFIX"));
		if (props.containsKey("LEN4LEN"))
			this.m_LEN4LEN = Integer.parseInt((String) props.get("LEN4LEN"));
		if (props.containsKey("LENSTART"))
			this.m_LENSTART = Integer.parseInt((String) props.get("LENSTART"));
		if (props.containsKey("PREFIX"))
			this.m_PREFIX = (String) props.get("PREFIX");
		if (props.containsKey("LENLEN"))
			this.m_LENLEN = Integer.parseInt((String) props.get("LENLEN"));
		if (props.containsKey("ISTOTALLEN"))
			this.m_ISTOTALLEN = Integer.parseInt((String) props.get("ISTOTALLEN"))==1;
		if (props.containsKey("PADDING")) {
			String padding = ((String) props.get("PADDING"));
			padding = padding.replaceAll("\"", "");
			this.m_Padding = padding.toCharArray()[0];
		}
		if (props.containsKey("ALIGN"))
			this.m_Align= Boolean.parseBoolean(((String) props.get("ALIGN")));
		if (props.containsKey("NEEDBACK"))
			this.m_NEEDBACK = Integer.parseInt((String) props.get("NEEDBACK"));
		if (props.containsKey("NEEDPREFIX"))
			this.m_NEEDPREFIX = Integer.parseInt((String) props.get("NEEDPREFIX"))==1;
		if (props.containsKey("TIMEOUT"))
			this.m_timeOut = Integer.parseInt((String) props.get("TIMEOUT"));
		if (props.containsKey("NEEDPULSE"))
			this.m_NEEDPULSE = Integer.parseInt((String) props.get("NEEDPULSE"));
		
		if (props.containsKey("ENCODING"))
			this.m_ENCODING = (String) props.get("ENCODING");
		
		if (props.containsKey("RECORDING"))
			this.m_RECORDING = Integer.parseInt((String) props.getProperty("RECORDING"))==1;
		
		String strRecordUserName = "";
		if (m_RECORDING) {
			strRecordUserName = (String) props.getProperty("RECORDUSER");
		}
		
		if (props.containsKey("SYSTEMID"))
			this.m_SystemId = Integer.parseInt((String) props.get("SYSTEMID"));
		if (props.containsKey("SYSTEMNAME")) {
			this.m_SystemName = (String) props.get("SYSTEMNAME");		
			try {
				this.m_SystemName = new String(m_SystemName.getBytes("ISO-8859-1"), "utf-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} 
		}
		
		if (props.containsKey("dynamic_in"))
			this.m_needDecrypt = Integer.parseInt((String) props.getProperty("dynamic_in"))==1;

		if (props.containsKey("dynamic_out"))
			this.m_needEncrypt = Integer.parseInt((String) props.getProperty("dynamic_out"))==1;
		
		if (props.containsKey("dynamic_name"))
			this.m_CryptClsName = this.m_secureFactoryPackage + props.getProperty("dynamic_name");
		
		//必须有的配置项
		if (!props.containsKey("targetIP") ||!props.containsKey("targetPort")) {
			log.error("发起端适配器缺少必备配置项[targetIP或者targetPort]");
			return false;
		}
		m_targetIP = (String) props.get("targetIP");	
		m_targetPort = Integer.parseInt((String) props.get("targetPort"));	
		
		log.debug("发起端目标地址" + m_targetIP + ":" + m_targetPort);
		
		//长连接，在启动时自动建立到服务器的长连接
		if (1==m_ISLAST) {
			try	{
				m_socket = new Socket(m_targetIP, m_targetPort);
				m_socket.setSoTimeout(m_timeOut);
				m_socket.setKeepAlive(true);
				System.out.println("成功建立与被测进程[IP：" + m_targetIP + "，端口号：" + m_targetPort + "]的Socket长连接！");
				//添加心跳测试
				if (m_NEEDPULSE == 1)
					KeepAutTcpConnectionAlive();
			}
			catch(Exception ex){
				log.error("在TCP发送端启动时，试图建立到服务器的长连接失败：" + ex.getMessage());
			}
		}
		
		if (m_RECORDING && m_config == null) {
			InitDbConnection(this.m_SystemName);
			if (strRecordUserName != null && !strRecordUserName.isEmpty()) {
				m_RecordUserId = DbOp.getUserIdByUserName(strRecordUserName);
			}
			else {
				m_RecordUserId = DbOp.getAdminUserId();
			}
		}
		
		return true;
	}
	
	
	//把请求报文发送到AUT
	public byte[] Send(byte[] msg){
		
		if (msg == null || msg.length == 0) { //空报文不处理
			return null;
		}
		try {
			String strReqMsg = new String(msg, m_ENCODING);
			log.debug("接收到待转发消息：" + strReqMsg);
			System.out.println("接收到待转发消息：" + strReqMsg);
			if (this.m_RECORDING) {
				if (m_config == null) {
					InitDbConnection(this.m_SystemName);
				}
				//写入录制信息
				m_NewRecordedCaseId = DbOp.InsertRecordedCase(m_SystemId, m_RecordUserId, strReqMsg);
			}
		} catch(Exception ex) {
			ex.printStackTrace();
			return null;
		}

		//检查和建立连接
		try {
			if (0==m_ISLAST){
				// 建立到被测系统的短连接
				m_socket = new Socket(m_targetIP, m_targetPort);
				m_socket.setSoTimeout(m_timeOut);
			}
			else{ //长连接，断线重连
				if (m_socket == null || m_socket.isClosed()){
					m_socket = new Socket(m_targetIP, m_targetPort);
					m_socket.setSoTimeout(m_timeOut);
					m_socket.setKeepAlive(true);
				}
				try{
					m_socket.getOutputStream();
				}
				catch(IOException ex){
					log.error("与被测系统的连接已经关闭." + ex.getLocalizedMessage());
					m_socket = new Socket(m_targetIP, m_targetPort);
					m_socket.setSoTimeout(m_timeOut);
					m_socket.setKeepAlive(true);
					log.debug("重新建立到发起端目标地址" + m_targetIP + "：" + m_targetPort + "的长连接.");			
				}
			}
		}
		catch(SocketTimeoutException e){
			log.error("超时退出." + e.toString());
			return null;
		}
		catch (IOException e) {
			log.error("目标地址:" + m_targetIP + ":" + m_targetPort 
					+ "连接失败." + e.toString());
			return null;
		}

		//发送请求报文
		byte[] inData = null;
		try {
			// 向被测系统发报文
			log.debug("开始向被测系统发出报文：" + new String(msg, m_ENCODING));

			//安全处理，加密
			byte[] outRawMsg = msg;
			if (this.m_needEncrypt){
				log.debug("开始加密……");
				IEncryptAdapterSecure encrypter = AbstractFactory.getInstance(this.m_CryptClsName).getEncryptAdapterSecure();
				outRawMsg = encrypter.enCrypt(msg);
				if (outRawMsg == null){// 解密失败
					log.error("加密失败");
					return null;
				}
				log.debug("加密完毕.");
			}

			//发送出去
			try	{
				sendToAUT(m_socket.getOutputStream(), outRawMsg);
			}
			catch(Exception e) {
				try {
					m_socket.close();
				} catch (IOException e2) {
					e2.printStackTrace();
				}
				
				if (m_NEEDPULSE == 1)
					KeepAutTcpConnectionAlive();
				//再发一次
				//sendToAUT(m_socket.getOutputStream(), outRawMsg);
			}
						
			// 对"需要同步返回"的同步或异步连接请求，从被测系统取响应
			if (1==m_NEEDBACK)
			{
				byte[] inRawMsg = recvMsg();
				
				m_TimeOfAcceptResponse = System.currentTimeMillis();

				//安全处理，解密
				inData = inRawMsg;
				if (this.m_needDecrypt){
					log.debug("开始解密……");
					IDecryptAdapterSecure decrypter = AbstractFactory.getInstance(this.m_CryptClsName).getDecryptAdapterSecure();
					inData = decrypter.deCrypt(inRawMsg);
					if (inData == null){// 解密失败
						log.error("解密失败");
						return null;
					}
					log.debug("解密完毕.");
				}
				if (inData == null) {
					System.out.println("未收到来自被测系统的应答，请检查通讯配置！");
				}

				String strResponseMsg = new String(inData, m_ENCODING);
				System.out.println("收到被测系统应答消息：" + strResponseMsg);
				log.error("收到被测系统应答消息：" + strResponseMsg);
				if (this.m_RECORDING) {
					DbOp.UpdateRecordedCase(m_NewRecordedCaseId, strResponseMsg);
				}
			}		
			if (0==m_ISLAST) {
				// 关闭到被测系统的短连接
				m_socket.close();
			}
		}
		catch(Exception ex){
			log.error("与被测系统交互时发生异常：" + ex.getMessage());
			try {
				if (0==m_ISLAST){//短连接才关闭
					m_socket.close();
				}
				else if (m_socket.isClosed()) { //长连接且发现已经被关闭了，则重连
					m_socket = new Socket(m_targetIP, m_targetPort);
					m_socket.setSoTimeout(m_timeOut);
					m_socket.setKeepAlive(true);
					log.debug("出现异常之后，重新建立到发起端目标地址" + m_targetIP + "：" + m_targetPort + "的长连接.");
				}
			}
			catch(Exception e){
				log.error("socket关闭失败.");
			}
			return null;
		}
		
		if (0==m_ISLAST)
		{	//短连接
			if (inData != null)
				log.info("接到被测系统响应：" + new String(inData));
			else
				log.info("接到被测系统响应为空");
		}
	
		return inData;
	}

	
	/**
	 * 向被测系统发信息
	 * @throws IOException 
	 * @throws IOException 
	 */
	protected void sendToAUT(OutputStream out, byte[] msg) throws IOException{
	
		byte[] realMsg = msg;

		String msgStr = new String(msg, m_ENCODING);
		
		/*if ("<?xml".equals(msgStr.substring(0, 5))) {
			int iPosOfEncoding = msgStr.indexOf("encoding");
			String enCodingStr = msgStr.substring(iPosOfEncoding + "encoding".length());
			int iPosOfQuotationMark1  = enCodingStr.indexOf("\"");
			enCodingStr = enCodingStr.substring(iPosOfQuotationMark1 + 1);
			int iPosOfQuotationMark2  = enCodingStr.indexOf("\"");
			encoding = enCodingStr.substring(0, iPosOfQuotationMark2);
			realMsg = msgStr.getBytes(encoding);
			msg = realMsg;
		}*/
		if ((m_NEEDPREFIX || m_RECORDING) && m_ISFIX <= 0){ //变长报文 
			//按照与被测系统之间的通信协议进行打包			
			realMsg = new byte[msg.length + m_LEN4LEN];		
			log.debug("发起端适配器对变长报文添加报文头：" + FixLength(String.valueOf(m_ISTOTALLEN ? realMsg.length : realMsg.length - m_LEN4LEN), m_LEN4LEN, m_Padding, m_Align));
			System.arraycopy(FixLength(String.valueOf(m_ISTOTALLEN ? realMsg.length : realMsg.length - m_LEN4LEN), m_LEN4LEN, m_Padding, m_Align).getBytes(), 0, realMsg, 0, m_LEN4LEN);
			if (m_LENSTART>0 && m_PREFIX.length()==m_LENSTART) {
				System.arraycopy(m_PREFIX, 0, realMsg, 0, m_LENSTART);
			}
			System.arraycopy(msg, 0, realMsg, m_LEN4LEN, msg.length);
		}
		out.write(realMsg);
		String strMsgSended = new String(realMsg, m_ENCODING);
		log.debug("向被测系统发送的真实报文：" +  strMsgSended);
		System.out.println("向被测系统发送的真实报文：" + strMsgSended);
	}
	

	/**
	 * 从被测系统接收信息
	 * @throws IOException 
	 */
	protected byte[] recvMsg() throws IOException {
		byte[] inMsg = null;	//请求消息体
			
		if (m_ISFIX>0){
			//定长报文处理流程
			try{
				inMsg = this.recv4len(m_ISFIX);				
				log.debug("接收到的定长报文长度：["+inMsg.length+"]");
				log.debug("接收到的定长报文内容：["+(new String(inMsg))+"]");
				System.out.println("接收到的定长报文内容：["+(new String(inMsg))+"]"); //debug
			} catch(Exception e){
				log.error("error: " + e.getMessage());
				m_socket.close();
			}
		} else {
			//变长报文处理流程
			byte[] buff = null;
			byte[] trans = new byte[this.m_LENLEN];
			int lenlen = -1;	//报文中长度信息的长度
			try{
				//先收取长度信息
				buff = this.recv4len(this.m_LEN4LEN);
				log.debug("接收到被测系统的应答报文长度信息：" + new String(buff));
				System.out.println("接收到被测系统的应答报文长度信息：" + new String(buff));
				System.arraycopy(buff, this.m_LENSTART, trans, 0, this.m_LENLEN);
				lenlen = Integer.parseInt((new String(trans)).trim());
				if (m_ISTOTALLEN && lenlen<this.m_LEN4LEN){
					log.error("0x0D09：报文长度信息长度["+lenlen+"]小于报文头长度["+this.m_LEN4LEN+"]");
					System.out.println("0x0D09：报文长度信息长度["+lenlen+"]小于报文头长度["+this.m_LEN4LEN+"]");
					m_socket.close();
				}
			}catch(IndexOutOfBoundsException e){
				log.error("0x0D06：获取报文长度信息时，数组写操作越界！["+e.getMessage()+"]");
				System.out.println("0x0D06：获取报文长度信息时，数组写操作越界！["+e.getMessage()+"]");
				m_socket.close();
			}catch(ArrayStoreException e){
				log.error("0x0D07：获取报文长度信息时，向数组中写入不匹配的类型！["+e.getMessage()+"]");
				System.out.println("0x0D07：获取报文长度信息时，向数组中写入不匹配的类型！["+e.getMessage()+"]");
				m_socket.close();
			}catch(NullPointerException e){
				log.error("0x0D08：获取报文长度信息时，操作的数组为空！["+e.getMessage()+"]");
				System.out.println("0x0D08：获取报文长度信息时，操作的数组为空！["+e.getMessage()+"]");
				m_socket.close();
			}catch(NumberFormatException e){
				log.error("0x0D09：获取报文长度信息时，报文中["+(new String(trans))+"]包含的长度信息有误！["+e.getMessage()+"]");
				System.out.println("0x0D09：获取报文长度信息时，报文中["+(new String(trans))+"]包含的长度信息有误！["+e.getMessage()+"]");
				m_socket.close();
			} catch(Exception e){
				log.error(e.getMessage());
				e.printStackTrace();
				m_socket.close();
				return null;
			}		
			try{
				//修正剩余字节数长度
				if (m_ISTOTALLEN && lenlen>=this.m_LEN4LEN)
				{
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
	}
	
	
	/**
	 * 按照长度接收报文
	 * @param len 要接收的报文长度
	 * @return	接收到的报文
	 * @throws Exception
	 */
	private byte[] recv4len(int len) throws Exception{
		if (len > m_MAX_LEN){
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
//					throw new Exception();
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


	public String AdapterType() {
		// TODO Auto-generated method stub
		return "tcp.c";
	}

	
	public byte[] GetResponse() {
		byte[] inData = null;
		try{
			// 对"需要同步返回"的同步或异步连接请求，从被测系统取响应
			if (1==m_NEEDBACK)
			{
				byte[] inRawMsg = recvMsg();
				
				m_TimeOfAcceptResponse = System.currentTimeMillis();

				//安全处理，解密
				inData = inRawMsg;
				if (this.m_needDecrypt){
					log.debug("开始解密……");
					IDecryptAdapterSecure decrypter = AbstractFactory.getInstance(
							this.m_CryptClsName).getDecryptAdapterSecure();
					inData = decrypter.deCrypt(inRawMsg);
					if (inData == null){// 解密失败
						log.error("解密失败");
						return null;
					}
					log.debug("解密完毕.");
				}
				System.out.println("收到被测系统应答消息：" + new String(inData));
				log.error("收到被测系统应答消息：" + new String(inData));
			}		
		}
		catch(Exception ex){
			inData = null;
			log.error("接收被测系统分笔应答失败." + ex.getMessage());
		}
		
		return inData;
	}

	public boolean IsLast() {
		// TODO Auto-generated method stub
		return true;
	}

	public long TimeOfAcceptResponse() {
		// TODO Auto-generated method stub
		return m_TimeOfAcceptResponse;
	}
	
	protected void KeepAutTcpConnectionAlive() {

		log.info("长连接状态下，向被测程序发送心跳报文");
		try {
			// 建立socket连接（如果socket不存在的话）
			if (m_socket == null || m_socket.isClosed()){
				m_socket = new Socket(m_targetIP, m_targetPort);
				m_socket.setSoTimeout(m_timeOut);
				m_socket.setKeepAlive(true);
			}

			// 每隔25秒向AUT发一个心跳 第一跳在1000毫秒时进行
			new Timer(true).schedule(new TimerTask() {
				@Override
				public void run() {
					// 新建一个心跳消息
					String strSendBuffer = "0000";
					byte[] byte2Send = strSendBuffer.getBytes();
					try {
						m_socket.getOutputStream().write(byte2Send);
					} catch (Exception e) {
						log.error("向AUT发送心跳测试报文失败：" + e.getMessage());
						try {
							m_socket.close();
						} catch (IOException e2) {
							e2.printStackTrace();
						}
						if (m_socket == null || m_socket.isClosed()){
							//长连接且发现已经被关闭了，则重连
							try {
								m_socket = new Socket(m_targetIP, m_targetPort);
								m_socket.setSoTimeout(m_timeOut);
								m_socket.setKeepAlive(true);
							} catch (UnknownHostException e1) {
								e1.printStackTrace();
							} catch (Exception e1) {
								e1.printStackTrace();
							}
							log.debug("出现异常之后，重新建立到发起端目标地址" + m_targetIP + "：" + m_targetPort + "的长连接.");
						}
					}
				}
			}, 1000, 1000 * 25 * 1);
		} catch (IOException ex) {
			log.error("出现了未知异常：", ex);
			this.m_socket = null;
		}
	}
	
	
	public void InitDbConnection(String instanceName) {

		// 初始化核心基础配置
		log.info("初始化基础配置...");
		m_config = new Config();
		log.info("初始化基础配置成功.");
	
		// 初始化数据访问层
		log.info("初始化数据访问层...");
		try {
			da = createRuntimeDAL(instanceName);
		} catch (Exception e) {
			e.printStackTrace();
		}
		log.info("初始化数据访问层成功.");
		//System.out.println();
	}
	

	protected static IRuntimeDAL createRuntimeDAL(String instanceName) throws Exception {
		
		return new RuntimeDAL(instanceName, m_config);
	}


}
