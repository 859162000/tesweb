package com.dc.tes.adapterlib;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.dc.tes.adapter.IReplyAdapter;
import com.dc.tes.adapter.context.IAdapterEnvContext;
import com.dc.tes.adapter.context.IReplyAdapterEnvContext;
import com.dc.tes.adapter.helper.IReplyAdapterHelper;
import com.dc.tes.adapter.secure.AbstractFactory;
import com.dc.tes.adapter.secure.IDecryptAdapterSecure;
import com.dc.tes.adapter.secure.IEncryptAdapterSecure;



public class UDPReplyAdapterPlugin implements IReplyAdapter {
private static Log log = LogFactory.getLog(UDPReplyAdapterPlugin.class);
	

	private IReplyAdapterEnvContext m_TESEnv = null;
	private IReplyAdapterHelper m_adpHelper = null;
	
	// 是否调用安全服务处理接收报文,解密操作,0否 1是
	private boolean m_needDecrypt = false;
	
	// 是否调用安全服务处理返回报文,加密操作,0否 1是
	private boolean m_needEncrypt = false;

	// 处理加解密报文的插件类名 HttpReplyFactory\SoapReplyFactory\TcpReplyFactory\HttpRequestFactory\SoapRequestFactory\TcpRequestFactory 中的一种
	private String m_CryptClsName = "";

	// HttpReplyFactory\SoapReplyFactory\TcpReplyFactory\HttpRequestFactory\SoapRequestFactory\TcpRequestFactory 包名
	private String m_secureFactoryPackage = "com.dc.tes.adapter.secure.factory.";

	private DatagramSocket m_lsrSocket = null;
	private int m_lsrPort = -1;
	private int m_timeout = 0;
	
	private boolean m_serverState = true;

	private Properties m_config_props = null;
	
	@Override
	public Properties GetAdapterConfigProperties() {
		return m_config_props;
	}
	
	
	public boolean Init(IAdapterEnvContext tesENV) {
		// TODO Auto-generated method stub
		log.info("响应端适配器插件" + this.getClass().getName() + "被初始化……");
		//获取配置信息
		m_TESEnv = (IReplyAdapterEnvContext) tesENV;		
		m_adpHelper = m_TESEnv.getHelper();
		
		Properties props = ConfigHelper.getConfig(m_TESEnv.getEvnContext());
		m_config_props = props;
		
		this.m_lsrPort = Integer.parseInt((String) props.get("PORT"));
		this.m_timeout = Integer.parseInt((String) props.get("TIMEOUT"));
		

		this.m_needDecrypt = Integer.parseInt((String) props
				.getProperty("dynamic_in"))==1;
		this.m_needEncrypt = Integer.parseInt((String) props
				.getProperty("dynamic_out"))==1;
		this.m_CryptClsName = this.m_secureFactoryPackage
				+ props.getProperty("dynamic_name");
		
		log.info("响应端适配器插件" + this.getClass().getName() + "初始化完成.");
		return true;
	}
	
	/**
	 * 停止服务器
	 * 
	 * @throws IOException
	 */
	public void stopServer() {
		log.info("正在关闭响应端适配器...");
		m_serverState = false;
		if(m_lsrSocket != null)
			m_lsrSocket.close();
		log.info("响应端适配器已关闭.");
	}

	/**
	 * 启动响应端适配器
	 * 
	 */
	private void startServer(){
		try {
			
			while (m_serverState) {

				byte[] inRawMsg = null;	//请求消息体
				byte[] outRawMsg = null;	//返回消息体
				
				this.m_lsrSocket = new DatagramSocket(this.m_lsrPort);
				m_lsrSocket.setSoTimeout(m_timeout);
				
				byte[] buff =new byte[1024];
				DatagramPacket packet=new DatagramPacket(buff, buff.length);
			
				
				//请求端地址
				InetAddress partnerAddress = null;
				int partnerPort = -1;

				int recvTimes = 0;
				int i=1;
				StringBuffer dataBuff = new StringBuffer(1024);
				while(i!=0)	
				{
					m_lsrSocket.receive(packet);
					partnerAddress = packet.getAddress();
					partnerPort = packet.getPort();
					
					recvTimes++;
					i=packet.getLength();

					if(i>0){
						inRawMsg = packet.getData();
						String recvMsg = new String(inRawMsg, 0, packet.getLength());
						
						dataBuff.append(recvMsg);
						if(log.isDebugEnabled())
							System.out.println("第" + recvTimes + "次接收到被测系统消息(：" + i + "字节)：" + recvMsg);
						
						continue;
					}
				}
				
				//安全处理，解密
				byte[] decryptedData = dataBuff.toString().getBytes();
				if(this.m_needDecrypt){
					log.debug("开始解密……");
					IDecryptAdapterSecure decrypter = AbstractFactory.getInstance(
							this.m_CryptClsName)
							.getDecryptAdapterSecure();
					decryptedData = decrypter.deCrypt(dataBuff.toString().getBytes());
					if ( decryptedData == null ){// 解密失败
						log.error("解密失败");
						break;
					}
					log.debug("解密完毕.");
				}
				
				//转发给TES
				if(log.isDebugEnabled())
					System.out.println("接收到被测系统消息：" + new String(decryptedData));
				
				byte[] outData = m_adpHelper.sendToCore(decryptedData);
				

				//转发给被测系统（接收到的最后一个报文包中的源地址）
				if(log.isDebugEnabled())
					System.out.println("向被测系统发送TES响应消息：" + new String(outData));
				
				//安全处理，加密
				outRawMsg = outData;
				if(this.m_needEncrypt){
					log.debug("开始加密……");
					IEncryptAdapterSecure encrypter = AbstractFactory.getInstance(
							this.m_CryptClsName)
							.getEncryptAdapterSecure();
					outRawMsg = encrypter.enCrypt(outData);
					if ( outRawMsg == null ){// 解密失败
						log.error("加密失败");
						break;
					}
					log.debug("加密完毕.");
				}
				
				DatagramPacket respPacket=new DatagramPacket(outRawMsg, outRawMsg.length, partnerAddress, partnerPort);
				m_lsrSocket.send(respPacket);
				
				
				m_lsrSocket.close();
				
				//等待被关闭
				Thread.currentThread().sleep(500);
			}//end while			
		}
		catch (SocketException e) {
			log.error("响应端适配器监听端口失败[" + e.getMessage() + "]");
		}
		catch (SocketTimeoutException e) {
			log.error("响应端适配器监听端口超时[" + e.getMessage() + "]");
		}
		catch (IOException e) {
			log.error("error: 响应端适配器发生异常退出.[" + e.getMessage() + "]");
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
			stopServer();
		}
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
	
	public void Start() {

		startServer();
	}

	public void Stop() {

		stopServer();
	}


	public String AdapterType() {

		return "udp.s";
	}

	public long TimeOfAcceptRequest() {

		return 0;
	}
}
