package com.dc.tes.adapterlib;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.dc.tes.adapter.IReplyAdapter;
import com.dc.tes.adapter.context.IAdapterEnvContext;
import com.dc.tes.adapter.context.IReplyAdapterEnvContext;
import com.dc.tes.adapter.helper.IReplyAdapterHelper;


public class TCPReplyAdapterPlugin implements IReplyAdapter{

	private static Log log = LogFactory.getLog(TCPReplyAdapterPlugin.class);
	
	private IReplyAdapterEnvContext m_TESEnv = null;
	private IReplyAdapterHelper m_adpHelper = null;
	
	private Thread m_serverThread = null;
	private ServerSocket m_lsrSocket = null;
	private int m_lsrPort = -1;
	private int m_timeOut = 100;
	
	private long m_TimeOfAcceptRequest = 0;

	private boolean m_serverState = false; //适配器是否启动，true=启动，false=未启动
	
	private Properties m_config_props = null;
	
	@Override
	public Properties GetAdapterConfigProperties() {
		return m_config_props;
	}
	
	public boolean Init(IAdapterEnvContext tesENV) {

		log.info("响应端适配器插件" + this.getClass().getName() + "被初始化……");
		//获取配置信息
		m_TESEnv = (IReplyAdapterEnvContext) tesENV;		
		m_adpHelper = m_TESEnv.getHelper();
		
		Properties props = ConfigHelper.getConfig(m_TESEnv.getEvnContext());
		m_config_props = props;
		
		if(!props.containsKey("PORT")){
			System.out.println("接收端适配器缺少必备配置项[PORT=监听被测系统请求的本地端口]");
			log.error("接收端适配器缺少必备配置项[PORT=监听被测系统请求的本地端口]");
			return false;
		}
		this.m_lsrPort = Integer.parseInt((String) props.get("PORT"));
		if(props.containsKey("TIMEOUT"))
			this.m_timeOut = Integer.parseInt((String) props.get("TIMEOUT"));
		
		log.debug("响应端适配器获得环境参数：" + props);
		
		log.info("响应端适配器插件" + this.getClass().getName() + "初始化完成.");
		return true;
	}
	
	/**
	 * 停止服务器
	 * 
	 * @throws IOException
	 */
	private void stopServer() {
		
		log.info("正在退出响应端适配器...");
		
		m_serverState = false;
		
		if (m_lsrSocket != null)
			try {
				m_lsrSocket.close();
			} catch (IOException e) {
				log.error("响应端适配器关闭失败.[" + e.getMessage() + "]");
				e.printStackTrace();
			}
			
		log.info("响应端适配器退出.");
	}

	
	/**
	 * 启动响应端适配器
	 * 
	 */
	private void startServer(){
		
		if(null != m_serverThread){
			log.error("同一适配器不允许多次启动.");
			return;
		}
		
		m_serverThread = new Thread(new Runnable() {
			public void run() {
				try {
					m_lsrSocket = new ServerSocket(m_lsrPort);
					m_lsrSocket.setSoTimeout(m_timeOut);
					m_serverState = true;
					while (m_serverState) {
						Socket socket = null;
						try {
							socket = m_lsrSocket.accept();
							log.debug("接收到连接请求来自：" + socket.getRemoteSocketAddress());						
							m_TimeOfAcceptRequest = System.currentTimeMillis();
							//启动单独线程处理到来的请求
							new TCPReplyAdapterWorkerThread(socket, m_TESEnv, m_adpHelper).start();
						}
						catch(SocketTimeoutException e){//超时后判断是否被终止
							System.out.println("TCP响应端适配器等待超时：" + e.getMessage());
							continue;
						}
						catch (Exception ex) { //accept出现异常
							if(!m_serverState){
								log.info("TCP适配器服务器主动停止服务." + ex.getMessage());
								System.out.println("TCP适配器服务器主动停止服务." + ex.getMessage());
								break;
							}
						}
					}//end while
				} 
				catch(IOException e){
					log.error("error: 响应端适配器创建本地监听失败.[" + e.getMessage() + "]");
					System.out.println(e.getMessage());
					e.printStackTrace();
				}
			}
		});
		
		m_serverThread.start();
	}
	
	
	
	public void Start() {

		startServer();
	}

	public void Stop() {

		stopServer();
	}


	public String AdapterType() {

		return "tcp.s";
	}

	public long TimeOfAcceptRequest() {

		return m_TimeOfAcceptRequest;
	}
	
}
