package com.dc.tes.adapterlib;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.dc.tes.adapter.IRequestAdapter;
import com.dc.tes.adapter.context.IAdapterEnvContext;
import com.dc.tes.adapter.context.IRequestAdapterEnvContext;
import com.dc.tes.adapter.secure.AbstractFactory;
import com.dc.tes.adapter.secure.IDecryptAdapterSecure;
import com.dc.tes.adapter.secure.IEncryptAdapterSecure;


public class UDPRequestAdapterPlugin implements IRequestAdapter {
	public final static Log log = LogFactory.getLog(UDPRequestAdapterPlugin.class);

	private IRequestAdapterEnvContext m_TESEnv = null;

	private String m_targetIP = null;
	private int m_targetPort = -1;
	
	private Socket m_socket = null;

	//是否为长连接形式
//	private int m_ISLAST=0;
	//是否为定长报文，大于零的值表示为定长报文，且报文长度为该值，小于等于零的值表示为变长报文
	//private int m_ISFIX=0;
	//表示为了要获取变长报文的长度信息需要预先接收的长度
	//private int m_LEN4LEN=10;
	//报文长度信息在报文中的开始位置
	//private int m_LENSTART=0;
	//报文长度信息的长度
	//private int m_LENLEN=10;
	//是否需要返回报文，主要针对异步通讯模式下，如果异步模式下无需在只读连接下返回则设置为0
//	private int m_NEEDBACK=1;
	//每次是否返回固定报文
//	private int m_FIXBACK=0;	
	

	// 是否调用安全服务处理接收报文,解密操作,0否 1是
	private boolean m_needDecrypt = false;
	
	// 是否调用安全服务处理返回报文,加密操作,0否 1是
	private boolean m_needEncrypt = false;

	// 处理加解密报文的插件类名 HttpReplyFactory\SoapReplyFactory\TcpReplyFactory\HttpRequestFactory\SoapRequestFactory\TcpRequestFactory 中的一种
	private String m_CryptClsName = "";

	// HttpReplyFactory\SoapReplyFactory\TcpReplyFactory\HttpRequestFactory\SoapRequestFactory\TcpRequestFactory 包名
	private String m_secureFactoryPackage = "com.dc.tes.adapter.secure.factory.";

	private Properties m_config_props = null;
	
	@Override
	public Properties GetAdapterConfigProperties() {
		return m_config_props;
	}
	
	public byte[] Send(byte[] msg) throws Exception {
		// TODO Auto-generated method stub
		log.debug("接收到待转发消息：" + new String(msg));
		if(log.isDebugEnabled())
			System.out.println("向被测系统发送消息：" + new String(msg));
		// 建立到被测系统的连接
		DatagramSocket outSocket = new DatagramSocket();

	
		

		byte[] inData = null;
		try{
			// 向被测系统发报文
			log.debug("开始向被测系统发出报文：" + new String(msg));
			
			//安全处理，加密
			byte[] outRawMsg = msg;
			if(this.m_needEncrypt){
				log.debug("开始加密……");
				IEncryptAdapterSecure encrypter = AbstractFactory.getInstance(
						this.m_CryptClsName)
						.getEncryptAdapterSecure();
				outRawMsg = encrypter.enCrypt(msg);
				if ( outRawMsg == null ){// 解密失败
					log.error("加密失败");
					return null;
				}
				log.debug("加密完毕.");
			}
			
			DatagramPacket packet = new DatagramPacket(outRawMsg, outRawMsg.length, 
					InetAddress.getByName(m_targetIP), m_targetPort);
			
			outSocket.send(packet);
			
			
			// 从被测系统取响应
			byte[] inRawMsg = recievePacket(outSocket);	
			
			//安全处理，解密
			inData = inRawMsg;
			if(this.m_needDecrypt){
				log.debug("开始解密……");
				IDecryptAdapterSecure decrypter = AbstractFactory.getInstance(
						this.m_CryptClsName)
						.getDecryptAdapterSecure();
				inData = decrypter.deCrypt(inRawMsg);
				if ( inData == null ){// 解密失败
					log.error("解密失败");
					return null;
				}
				log.debug("解密完毕.");
			}

			if(log.isDebugEnabled())
				System.out.println("收到被测系统应答消息：" + new String(inData));
		}
		catch(Exception ex){
			log.error("与北侧系统交互时发生异常：" + ex.getMessage());
			m_socket.close();
		}

		log.debug("接到被测系统响应：" + new String(inData));
		return inData;
	}
	/*
	 * 循环获得所有请求数据
	 */
	private byte[] recievePacket(DatagramSocket socket){
		int recvTimes = 0;
		int i=0;
		StringBuffer dataBuff = new StringBuffer(1024);
		while(i==0)	//无数据，则循环
		{
			byte[] buff =new byte[1024];
			DatagramPacket packet=new DatagramPacket(buff,buff.length);
			try {
				socket.receive(packet);
			} catch (IOException e) {
				log.error("error: 响应端适配器接收数据发生异常.[" + e.getMessage() + "]");
				return null;
			}
			
			recvTimes++;
			i=packet.getLength();
			if(i>0){
				byte[] inMsg = packet.getData();
				String recvMsg = new String(inMsg, 0, packet.getLength());
				
				dataBuff.append(recvMsg);
				if(log.isDebugEnabled())
					System.out.println("第" + recvTimes + "次接收到被测系统消息(：" + i + "字节)：" + recvMsg);
				
				i=0;//循环接收
			}
		}
		
		return dataBuff.toString().getBytes();
	}
	
	public boolean Init(IAdapterEnvContext tesENV) {
		
		log.debug("发起端适配器插件" + this.getClass().getName() + "被初始化……");
		
		m_TESEnv = (IRequestAdapterEnvContext) tesENV;
		
		Properties props = ConfigHelper.getConfig(m_TESEnv.getEvnContext());
		m_config_props = props;
		
//		this.m_ISLAST = Integer.parseInt((String) props.get("ISLAST"));
	//	this.m_ISFIX = Integer.parseInt((String) props.get("ISFIX"));
	//	this.m_LEN4LEN = Integer.parseInt((String) props.get("LEN4LEN"));
	//	this.m_LENSTART = Integer.parseInt((String) props.get("LENSTART"));
	//	this.m_LENLEN = Integer.parseInt((String) props.get("LENLEN"));
//		this.m_NEEDBACK = Integer.parseInt((String) props.get("NEEDBACK"));
//		this.m_FIXBACK = Integer.parseInt((String) props.get("FIXBACK"));
		

		this.m_needDecrypt = Integer.parseInt((String) props
				.getProperty("dynamic_in"))==1;
		this.m_needEncrypt = Integer.parseInt((String) props
				.getProperty("dynamic_out"))==1;
		this.m_CryptClsName = this.m_secureFactoryPackage
				+ props.getProperty("dynamic_name");
		
		
		m_targetIP = (String) props.get("targetIP");	
		m_targetPort = Integer.parseInt((String) props.get("targetPort"));	
		
		log.debug("发起端目标地址" + m_targetIP + "：" + m_targetPort);
		
		return true;
	}
	
	
	/**
	 * 向被测系统发信息
	 * @throws IOException 
	 * @throws IOException 
	 */
//	protected void sendToAUT(OutputStream out, byte[] msg) throws IOException{
//		//按照与被测系统之间的通信协议进行打包
//		byte[] realMsg = new byte[msg.length + m_LEN4LEN];
//		log.debug("发起端适配器添加报文头：" + TCPRequestAdapterPlugin.FixLength(String.valueOf(realMsg.length), m_LEN4LEN, '0', true));
//		System.arraycopy(TCPRequestAdapterPlugin.FixLength(String.valueOf(realMsg.length), m_LEN4LEN, '0', true).getBytes(), 0, realMsg, 0, m_LEN4LEN);
//		System.arraycopy(msg, 0, realMsg, m_LEN4LEN, msg.length);
//		
//		log.debug("向被测系统发送的真实报文：" +  new String(realMsg));
//		out.write(realMsg);
//	}

	/**
	 * 从被测系统接收信息
	 * @throws IOException 
	 */
//	protected byte[] recvMsg() throws IOException {
//		byte[] inMsg = null;	//请求消息体
////		do{
//			if (m_ISFIX>0){
//				//定长报文处理流程
//				try{
//					inMsg = this.recv4len(m_ISFIX);				
//					log.debug("接收到的定长报文长度：["+inMsg.length+"]");
//					log.debug("接收到的定长报文内容：["+(new String(inMsg))+"]");
//				} catch(Exception e){
//					log.error("error: " + e.getMessage());
//					m_socket.close();
//				}
//			} else {
//				//变长报文处理流程
//				byte[] buff = null;
//				byte[] trans = new byte[this.m_LENLEN];
//				int lenlen = -1;	//报文中长度信息的长度
//				try{
//					//先收取长度信息
//					buff = this.recv4len(this.m_LEN4LEN);
//					log.debug("接收到被测系统的应答报文长度信息：" + new String(buff));
//					System.arraycopy(buff, this.m_LENSTART, trans, 0, this.m_LENLEN);
//					lenlen = Integer.parseInt((new String(trans)).trim());
//					if (lenlen<this.m_LEN4LEN){
//						log.error("0x0D09：报文长度信息长度["+lenlen+"]小于报文头长度["+this.m_LEN4LEN+"]");
//						m_socket.close();
//					}
//					
//				}catch(IndexOutOfBoundsException e){
//					log.error("0x0D06：获取报文长度信息时，数组写操作越界！["+e.getMessage()+"]");
//					m_socket.close();
//				}catch(ArrayStoreException e){
//					log.error("0x0D07：获取报文长度信息时，向数组中写入不匹配的类型！["+e.getMessage()+"]");
//					m_socket.close();
//				}catch(NullPointerException e){
//					log.error("0x0D08：获取报文长度信息时，操作的数组为空！["+e.getMessage()+"]");
//					m_socket.close();
//				}catch(NumberFormatException e){
//					log.error("0x0D09：获取报文长度信息时，报文中["+(new String(trans))+"]包含的长度信息有误！["+e.getMessage()+"]");
//					m_socket.close();
//				} catch(Exception e){
//					log.error(e.getMessage());
//					m_socket.close();
//				}		
//				try{
//					if (lenlen>=this.m_LEN4LEN){
//						//收取完整报文
//						try{
//							inMsg = this.recv4len(lenlen-this.m_LEN4LEN);
//						} catch(IndexOutOfBoundsException e){
//							log.error("0x0D06：获取报文信息时，数组写操作越界！["+e.getMessage()+"]");
//							m_socket.close();
//						}catch(ArrayStoreException e){
//							log.error("0x0D07：获取报文信息时，向数组中写入不匹配的类型！["+e.getMessage()+"]");
//							m_socket.close();
//						}catch(NullPointerException e){
//							log.error("0x0D08：获取报文信息时，操作的数组为空！["+e.getMessage()+"]");
//							m_socket.close();
//						}catch(NumberFormatException e){
//							log.error("0x0D09：获取报文信息时，报文中["+(new String(trans))+"]包含的长度信息有误！["+e.getMessage()+"]");
//							m_socket.close();
//						} catch(Exception e){
//							log.error(e.getMessage());
//							m_socket.close();
//						}
//					}
//				} catch(Exception e){
//					log.error(e.getMessage());
//					m_socket.close();
//				}
//			}
////		}while(1==this.m_ISLAST);//判断是否为长连接
//
//
//		return inMsg;
//	}
	
	/**
	 * 按照长度接收报文
	 * @param len 要接收的报文长度
	 * @return	接收到的报文
	 * @throws Exception
	 */
//	private byte[] recv4len(int len) throws Exception{
//		log.debug("接收"+len+"个字节的报文。");
//		byte[] trans = null;
//		try { 
//			int nsize, tsize, recvlen;
//			nsize = 0;
//			tsize = len;
//			trans = new byte[tsize];
//			InputStream in = m_socket.getInputStream();
//			while (nsize < tsize) { //当nsize==tsize正常退出
//				recvlen = in.read(trans, nsize, tsize - nsize);
//				if (recvlen < 0) {
//					log.error("剩余:" + (tsize - nsize) + "接收失败!");
//					throw new Exception();
//				}
//				else{
//					nsize += recvlen;
//					log.debug("本次收到"+recvlen+"字节");
//					log.debug("本次收到["+new String(trans)+"]");
//				}
//			}
//			log.debug("本次报文接收总长度："+tsize + " 实际接收长度:" + nsize);
//			log.debug("本次接收报文内容：" + new String(trans));
//			return trans;
//		} catch (Exception e) {
//			log.error("0x0D22：报文接收过程中出现异常！["+e.getMessage()+"]");
//			throw new Exception("报文接收过程中出现异常！["+e.getMessage()+"]");
//		}
//	}

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
		return "udp.c";
	}
}
