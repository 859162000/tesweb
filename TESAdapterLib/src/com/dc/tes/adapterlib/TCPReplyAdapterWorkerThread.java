package com.dc.tes.adapterlib;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.dc.tes.Config;
import com.dc.tes.adapter.IReplyAdapterWorker;
import com.dc.tes.adapter.context.IReplyAdapterEnvContext;
import com.dc.tes.adapter.helper.IReplyAdapterHelper;
import com.dc.tes.adapter.secure.AbstractFactory;
import com.dc.tes.adapter.secure.IDecryptAdapterSecure;
import com.dc.tes.adapter.secure.IEncryptAdapterSecure;
import com.dc.tes.data.IRuntimeDAL;
import com.dc.tes.data.RuntimeDAL;
import com.dc.tes.data.model.RecordedCase;
import com.dc.tes.data.model.SysType;


public class TCPReplyAdapterWorkerThread  extends Thread implements IReplyAdapterWorker{

	private static final Log log = LogFactory.getLog(TCPReplyAdapterWorkerThread.class);

	/** 核心基础配置 */
	public static Config m_config;

	/** 运行时数据访问接口 */
	public static IRuntimeDAL da;
	
	/**
	 * 与被测系统连接的Socket
	 */
	private Socket m_socket = null;

	private IReplyAdapterHelper m_adpHelper = null;
	
	private IReplyAdapterWorker worker = this;
	
	//与核心通信参数
	//=0,只把报文体给核心；
	//=1,把报文头和报文体都给核心
	private int m_flag = 0;
	
	
	// 是否调用安全服务处理接收报文,解密操作,0否 1是
	private boolean m_needDecrypt = false;
	
	// 是否调用安全服务处理返回报文,加密操作,0否 1是
	private boolean m_needEncrypt = false;

	// 处理加解密报文的插件类名 HttpReplyFactory\SoapReplyFactory\TcpReplyFactory\HttpRequestFactory\SoapRequestFactory\TcpRequestFactory 中的一种
	private String m_CryptClsName = "";

	// HttpReplyFactory\SoapReplyFactory\TcpReplyFactory\HttpRequestFactory\SoapRequestFactory\TcpRequestFactory 包名
	private String m_secureFactoryPackage = "com.dc.tes.adapter.secure.factory.";

	
	//阻塞读取超时,毫秒
	private int m_timeOut = 10*60*1000; 
	
	//是否为长连接形式
	private int m_ISLAST=0;
	//是否为定长报文，大于零的值表示为定长报文，且报文长度为该值，小于等于零的值表示为变长报文
	private int m_ISFIX=0;
	
	//表示为了要获取变长报文的长度信息需要预先接收的长度
	private int m_LEN4LEN=10;
	//报文长度信息在报文中的开始位置
	private int m_LENSTART=0;
	//报文长度信息的长度
	private int m_LENLEN=10;	
	//预先接收的字节长度是否包含在报文长度中
	private boolean m_ISTOTALLEN = true;//缺省值true保证向下兼容
	
	//是否需要返回报文，主要针对异步通讯模式下，如果异步模式下无需在只读连接下返回则设置为0
	private int m_NEEDBACK=1;
	//返回报文是否需要适配器端添加报文头(动态长度字节)
	private boolean m_NEEDPREFIX = true;  //缺省值true保证向下兼容
	private String m_PREFIX = null;
	
	private String m_ENCODING = "utf-8";
	
	//变成报文长度信息不够长时应填充的字符
	private char m_Padding = '0';
	//报文长度信息对其方式
	private boolean m_Align = true; //false -> true: 4930 -> 0493
	
	//处于处于录制状态
	private boolean m_RECORDING = false;
	private int m_SystemId = 0;
	private String m_SystemName = "";

	private SysType m_sysType;
	
	//是否有心跳
	private int m_NEEDPULSE=0;
	//心跳请求报文内容,紧跟在报文长度后面，在报文结束标志前面
	private String m_PULSE_INMSG="";	
	//心跳应答报文内容
	private String m_PULSE_OUTMSG="";	
	//校验和的字节长度（不包含在报文长度中）, 0表示不需要通信层截取，
	private int m_CHKLEN=0; 
	
	private int m_MAX_LEN = 1024*1024*1; //报文缓冲区长度
	
	private long m_TimeOfAcceptRequest = 0; //接收到被测系统请求的时间

	private int m_SenderPort = -1;
	private String m_SenderIp = "";
	private Socket m_SenderSocket = null;
	
	/**
	 * 初始化一条用于处理请求的线程
	 * 
	 * @param socket
	 *            在其上进行操作的Socket
	 * @param env 
	 * @param helper 
	 */
	public TCPReplyAdapterWorkerThread(Socket socket, IReplyAdapterEnvContext env, IReplyAdapterHelper helper) {
		this.m_socket = socket;		
		
		m_TimeOfAcceptRequest = System.currentTimeMillis();
		
		Properties props = ConfigHelper.getConfig(env.getEvnContext());
		if(props.containsKey("ISLAST"))
			this.m_ISLAST = Integer.parseInt((String) props.get("ISLAST"));
		if(props.containsKey("ISFIX"))
			this.m_ISFIX = Integer.parseInt((String) props.get("ISFIX"));
		if(props.containsKey("LEN4LEN"))
			this.m_LEN4LEN = Integer.parseInt((String) props.get("LEN4LEN"));
		if(props.containsKey("LENSTART"))
			this.m_LENSTART = Integer.parseInt((String) props.get("LENSTART"));
		if(props.containsKey("LENLEN"))
			this.m_LENLEN = Integer.parseInt((String) props.get("LENLEN"));		
		if(props.containsKey("ISTOTALLEN"))
			this.m_ISTOTALLEN = Integer.parseInt((String) props.get("ISTOTALLEN"))==1;
		
		if(props.containsKey("NEEDBACK"))
			this.m_NEEDBACK = Integer.parseInt((String) props.get("NEEDBACK"));
		if(props.containsKey("NEEDPREFIX"))
			this.m_NEEDPREFIX = Integer.parseInt((String) props.get("NEEDPREFIX"))==1;
		if(props.containsKey("PREFIX"))
			this.m_PREFIX = (String) props.get("PREFIX");
		if(props.containsKey("TIMEOUT"))
			this.m_timeOut = Integer.parseInt((String) props.get("TIMEOUT"));
		
		if(props.containsKey("PADDING")) {
			String padding = ((String) props.get("PADDING"));
			padding = padding.replaceAll("\"", "");
			this.m_Padding = padding.toCharArray()[0];
		}
		if(props.containsKey("ALIGN"))
			this.m_Align= Boolean.parseBoolean(((String) props.get("ALIGN")));
		
		if(props.containsKey("NEEDPULSE"))
			this.m_NEEDPULSE = Integer.parseInt((String) props.get("NEEDPULSE"));
		if(props.containsKey("PULSE_INMSG"))
		{
			this.m_PULSE_INMSG = (String) props.get("PULSE_INMSG");
			this.m_PULSE_INMSG = this.m_PULSE_INMSG.replaceAll("\"", "");
		}
		if(props.containsKey("PULSE_OUTMSG"))
		{
			this.m_PULSE_OUTMSG = (String) props.get("PULSE_OUTMSG");
			this.m_PULSE_OUTMSG = this.m_PULSE_OUTMSG.replaceAll("\"", "");
		}
		if(props.containsKey("CHKLEN"))
			this.m_CHKLEN = Integer.parseInt((String) props.get("CHKLEN"));
		
		if(props.containsKey("FLAG"))
			this.m_flag = Integer.parseInt((String) props.getProperty("FLAG"));
		
		if(props.containsKey("dynamic_in"))
			this.m_needDecrypt = Integer.parseInt((String) props.getProperty("dynamic_in"))==1;
		if(props.containsKey("dynamic_out"))
			this.m_needEncrypt = Integer.parseInt((String) props.getProperty("dynamic_out"))==1;
		if(props.containsKey("dynamic_name"))
			this.m_CryptClsName = this.m_secureFactoryPackage + props.getProperty("dynamic_name");


		if (props.containsKey("ENCODING"))
			this.m_ENCODING = (String) props.get("ENCODING");
		
		if(props.containsKey("RECORDING"))
			this.m_RECORDING = Integer.parseInt((String) props.getProperty("RECORDING"))==1;
		
		if(props.containsKey("SYSTEMID"))
			this.m_SystemId = Integer.parseInt((String) props.get("SYSTEMID"));
		if(props.containsKey("SYSTEMNAME")) {
			this.m_SystemName = (String) props.get("SYSTEMNAME");		
			try {
				this.m_SystemName = new String(m_SystemName.getBytes("ISO-8859-1"), "utf-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} 
		}
		
		if(props.containsKey("SENDERIP")) {
			this.m_SenderIp = (String) props.get("SENDERIP");
		}
		if(props.containsKey("SENDERPORT")) {
			this.m_SenderPort = Integer.parseInt((String) props.get("SENDERPORT"));
		}
		
		if (m_RECORDING && m_config == null) {
			InitDbConnection(this.m_SystemName);
			m_sysType = DbOp.getSysTypeBySystemId(m_SystemId);
		}

		m_adpHelper = helper;
	}
	

	public void run() {
		//处理被测系统的请求
		try {
			m_socket.setSoTimeout(m_timeOut);
			doWork();
		} catch (Exception ex) {
			log.error("处理与被测系统的消息通信时发生异常", ex);
			ex.printStackTrace();
		}	
		finally{
			try {
				if (this.m_ISLAST == 1 && !m_socket.isClosed()) { //长连接在这里关闭，短连接在Response完成之后关闭
					m_socket.close();
				}
			} catch (IOException ex1) {
				log.error("关闭远程通道Socket时发生异常", ex1);
			}
		}
	}
	
	private void doWork() throws Exception{
//		long inTime = System.currentTimeMillis();		//接到请求的时间
//		long outTime = -1;								//核心处理完成的时间
//		long usedTime = 0;								//核心处理时间
//		int delayTime = -1;								//系统返回的延时时间
		
		byte[] inRawMsg = null;	//请求消息体
//		byte[] outRawMsg = null;	//返回消息体
		do{
			if (m_ISFIX>0){
				//定长报文处理流程
					inRawMsg = this.recv4len(m_ISFIX);				
					log.debug("接收到的定长报文长度：["+inRawMsg.length+"]");
					log.debug("接收到的定长报文内容：["+(new String(inRawMsg))+"]");
					long t1 = m_TimeOfAcceptRequest;
					long t2= System.currentTimeMillis();
					long usedByAdapterPlugin = t2-t1;
					log.debug("接收定长报文耗时：" + usedByAdapterPlugin);
			} else {
				//变长报文处理流程
				byte[] buff = null;
				byte[] trans = new byte[this.m_LENLEN];
				int lenOfMsgBody = -1;	//报文中长度信息的长度
				try
				{
					//先收取长度信息 
					buff = this.recv4len(this.m_LEN4LEN);
					if (buff == null || buff.length == 0) {
						continue;
					}
					String strRecvBuff = new String(buff);
					log.debug("响应端适配器接收到长度头："  + strRecvBuff);
					System.arraycopy(buff, this.m_LENSTART, trans, 0, this.m_LENLEN);
					String strPacketLength = new String(trans);
					if (strPacketLength.trim().isEmpty()) {
						return;
					}
					lenOfMsgBody = Integer.parseInt((strPacketLength).trim()); //去除空格
					/*if (lenOfMsgBody == 0)
					{	//心跳报文"0000"，用来测试和保持连接的，报文内容为空
						continue;
					}*/
					
					// 此处判断假设：报文长度信息包含了报文头自身长度，需要根据具体情况修改
					if (m_ISTOTALLEN && lenOfMsgBody<this.m_LEN4LEN){
						log.error("0x0D09：报文长度信息长度["+lenOfMsgBody+"]小于报文头长度["+this.m_LEN4LEN+"]");
						m_socket.close();
					}	
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
				
				//修正剩余字节数长度
				if (m_ISTOTALLEN && lenOfMsgBody>=this.m_LEN4LEN) {
					lenOfMsgBody = lenOfMsgBody-this.m_LEN4LEN; 
				}
				
				log.debug("变长报文数据部分长度为:" + lenOfMsgBody);
				
				//收取完整报文
				try{
					if (lenOfMsgBody > 0)
					{
						inRawMsg = this.recv4len(lenOfMsgBody);
					}
					
					if(1 == this.m_flag){
						byte[] alldata = new byte[m_LEN4LEN+lenOfMsgBody];
						System.arraycopy(buff, 0, alldata, 0, this.m_LEN4LEN);
						System.arraycopy(inRawMsg, 0, alldata, this.m_LEN4LEN, lenOfMsgBody);
						log.debug("接收报文: " + alldata);
						inRawMsg = alldata; //给核心传递报文头+报文体
					}				
				}catch(Exception e){
					log.error("报文接收过程中发生异常：" + e.getMessage());
					throw new Exception("报文接收过程中发生异常：" + e.getMessage());
				}
			}//变长报文处理完毕

			//对长连接，判断为心跳请求
			String strOfinRawMsg = (inRawMsg==null? null: new String(inRawMsg));
			if(1==m_NEEDPULSE && (strOfinRawMsg==null || strOfinRawMsg.isEmpty() || strOfinRawMsg.equals(m_PULSE_INMSG))) {
				log.debug("收到心跳消息:" + strOfinRawMsg);
				if (1 == m_NEEDBACK) {
					byte[] responsMsg = m_PULSE_OUTMSG.getBytes();
					OutputStream out = m_socket.getOutputStream();
					log.debug("给被测系统返回报文:["+new String(responsMsg)+"]");
					out.write(responsMsg);
					out.flush();
				}
				continue; //不用转发给TES
			}
			
			//校验和
			if(m_CHKLEN > 0){
				byte[] checkCode = null;
				try{
					checkCode = this.recv4len(m_CHKLEN);
					//其它处理
				}catch(Exception e){
					log.error("报文接收过程中读取校验和发生异常：" + e.getMessage());
					throw new Exception("报文接收过程中读取校验和发生异常：" + e.getMessage());
				}	
			}
			
			//安全处理，解密
			byte[] decryptedData = inRawMsg;
			if(this.m_needDecrypt){
				log.debug("开始解密……");
				IDecryptAdapterSecure decrypter = AbstractFactory.getInstance(
						this.m_CryptClsName)
						.getDecryptAdapterSecure();
				decryptedData = decrypter.deCrypt(inRawMsg);
				if ( decryptedData == null ){// 解密失败
					log.error("解密失败");
					break;
				}
				log.debug("解密完毕.");
			}
			final byte[] decryptedDataFinal = decryptedData;
			long t1 = m_TimeOfAcceptRequest;
			long  t2= System.currentTimeMillis();
			long usedByAdapterPlugin = t2-t1;
			log.debug("转发给通信层之前耗时：" + usedByAdapterPlugin);
			//转发给TES
			System.out.println("接收到被测系统消息：" + new String(decryptedData));
			log.debug("接收到被测系统消息：" + new String(decryptedData));
			final String strResponseMsg = new String(decryptedData);
			final byte[] msgbyte2sender = decryptedData;
			new Thread(new Runnable() {

				@Override
				public void run() {
							
					if (!m_RECORDING) { //非录制状态
						//向核心转发
						//byte[] responseMsg = 
						m_adpHelper.sendToCoreWithMultiResponse(decryptedDataFinal, worker);
						//String strResponseMsg = new String(responseMsg);
						//System.out.println(strResponseMsg); //debug
						//Response(responseMsg);
					}
					else {//录制状态
						RecordedCase rc = DbOp.getLastInsertedRecordedCases(m_SystemId);
						if (rc != null) {
							DbOp.UpdateRecordedCase(rc, new String(strResponseMsg));
						}
						if (m_ISLAST == 0 || m_SenderSocket == null) {
							try {
								m_SenderSocket = new Socket(m_SenderIp, m_SenderPort);
								if (m_ISLAST == 1) {
									m_SenderSocket.setKeepAlive(true);
								}
							} catch (UnknownHostException e) {
								e.printStackTrace();
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
						try {
							byte[] realMsg = new byte[msgbyte2sender.length + m_LEN4LEN];		
							System.arraycopy(FixLength(String.valueOf(m_ISTOTALLEN ? realMsg.length : realMsg.length - m_LEN4LEN), m_LEN4LEN, m_Padding, m_Align).getBytes(), 0, realMsg, 0, m_LEN4LEN);
							if(m_LENSTART>0 && m_PREFIX.length()==m_LENSTART) {
								System.arraycopy(m_PREFIX, 0, realMsg, 0, m_LENSTART);
							}
							System.arraycopy(msgbyte2sender, 0, realMsg, m_LEN4LEN, msgbyte2sender.length);		
							m_SenderSocket.getOutputStream().write(realMsg);
							System.out.println("应答给发送方的消息为：" + new String(realMsg));
							if (m_ISLAST == 0) {
								m_SenderSocket.close();
							}
						} catch (IOException e) {
							e.printStackTrace();
						}
						finally {
							if (m_ISLAST == 0) { //短链接
								try {
									m_SenderSocket.close();
								} catch (IOException e) {
									e.printStackTrace();
								}	
							}
						}
					}
					if (m_ISLAST == 0) { //短链接
						try {
							m_socket.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
				
			}).start();
		
			//清空 inRawMsg
			inRawMsg = null;
		}
		while(1==this.m_ISLAST);//判断是否为长连接
		//m_socket.close();
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

	public void Response(byte[] outData) {
		boolean isError = false; //TES是否发生异常
		if(null == outData){
			log.error("TES处理返回响应端的信息表示模拟器发现异常！");
			outData = "TES_ERROR".getBytes();
			isError = true;
//			return; //让被测系统连接超时,  改成向被测系统返回提示信息
		}
		
	
		//安全处理，加密
		byte[] outRawMsg = outData;
		if(this.m_needEncrypt){
			log.debug("开始加密……");
			IEncryptAdapterSecure encrypter = AbstractFactory.getInstance(
					this.m_CryptClsName)
					.getEncryptAdapterSecure();
			outRawMsg = encrypter.enCrypt(outData);
			if ( outRawMsg == null ){// 解密失败
				log.error("加密失败");
				return;
			}
			log.debug("加密完毕.");
		}
		
		try {
			if(1==this.m_NEEDBACK){//需要同步应答
				System.out.println("向被测系统发送TES响应消息：" + new String(outData));
				
				byte[] realMsg = outRawMsg;
				if(m_NEEDPREFIX && m_ISFIX<=0){//对变长报文添加报文头
					//给返回消息内容添加包头
					log.debug("发起端适配器对变长报文添加报文头：" + TCPRequestAdapterPlugin.FixLength(String.valueOf(realMsg.length), m_LEN4LEN, '0', true));
					
					realMsg = new byte[outRawMsg.length + m_LEN4LEN];
					
					System.arraycopy(TCPReplyAdapterWorkerThread.FixLength(String.valueOf(realMsg.length), m_LEN4LEN, '0', true).getBytes(), 0, realMsg, 0, m_LEN4LEN);
					System.arraycopy(outRawMsg, 0, realMsg, m_LEN4LEN, outRawMsg.length);
				}
				
				log.debug("给被测系统分笔返回报文:["+new String(realMsg)+"]"); 
				System.out.println("向被测系统发送的真实的响应消息：" + new String(realMsg));
				//进行报文返回
				OutputStream out = m_socket.getOutputStream();
				out.write(realMsg);
				out.flush();
				if(isError){
					log.error("TES发现异常，关闭socket连接.");
					System.out.println("TES发现异常，错误提示信息");
					m_socket.close();
				}
			}
		} catch (IOException e) {
			log.error("0x0D22：报文返回给被测系统过程出现异常！["+e.getMessage()+"]");
			System.out.println("0x0D22：报文返回给被测系统过程出现异常！["+e.getMessage()+"]");
		}
	}

	public long TimeOfAcceptRequest() {
		// TODO Auto-generated method stub
		return m_TimeOfAcceptRequest;
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
		System.out.println();
	}
	

	protected static IRuntimeDAL createRuntimeDAL(String instanceName) throws Exception {
		
		return new RuntimeDAL(instanceName, m_config);
	}
	
}
