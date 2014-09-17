package com.dc.tes.adapter.remote;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.dc.tes.adapter.IRequestAdapter;
import com.dc.tes.adapter.helper.IRequestAdapterHelper;
import com.dc.tes.adapter.lib.HTTPRecorderLib;
import com.dc.tes.adapter.lib.MQRecorderLib;
import com.dc.tes.adapter.lib.TCPRecorderLib;

/**
 * 
 * "远程通道"方式,发起端适配器与核心进行数据交互
 *  
 * @author guhb,王春佳
 * 
 * @see 不同发起端适配器开启不同的监听地址、端口，监听核心发送的请求报文
 */
public class DefaultRequestAdapterHelper extends AbstractAdapterHelper implements IRequestAdapterHelper{

	private static final Log logger = LogFactory.getLog(DefaultRequestAdapterHelper.class);
		
	/**
	 * 绑定的网卡地址
	 */
	protected String m_lsrIP = null;

	/**
	 * 绑定的监听端口
	 */
	protected int m_lsrPort = -1;

	private Thread m_serverThread = null;
	/**
	 * 监听套接字
	 */
	protected ServerSocket m_lsrSocket = null;
	
	/**
	 * 服务器运行状态, =true表示运行态
	 */
	private boolean m_serverState = true;

	/**
	 * 发起端适配器插件实例
	 */
	protected IRequestAdapter m_adapterPluginInstance = null;
	
	//配置信息名值对
	protected Properties m_config_props = null;

	
	public void SetConfigProperty(Properties props) {
		m_config_props = props;
	}
	
	/**
	 * 初始化通讯服务器，启动监听
	 * @throws Exception
	 */
	public DefaultRequestAdapterHelper(Properties props, IRequestAdapter sa) throws Exception{		
		super(props);			
		if(null == sa){
			throw new Exception("发起端适配器插件不符合TES接入规范。");
		}
		m_adapterPluginInstance = sa;
		
		//取出发送端通信层需要的参数，例如启动监听的lsrIP, lsrPort
		if(props.containsKey("host"))
			this.m_lsrIP = m_props.getProperty("host");
		if(props.containsKey("UpPort"))
			this.m_lsrPort = Integer.parseInt(m_props.getProperty("UpPort"));
	}

	public void startServer(){
		
		if(null != m_serverThread){
			logger.error("同一适配器不允许多次启动.");
			return;
		}
		
		int iIsRecording = 0;
		if (m_config_props.containsKey("RECORDING")) {
			iIsRecording = Integer.parseInt((String) m_config_props.getProperty("RECORDING"));
		}
		final boolean isRecording = (iIsRecording == 1);

		m_serverThread = new Thread(new Runnable() {
			public void run() {
				if (!isRecording) {
					logger.info("开始监听" + m_lsrIP + ":" + m_lsrPort + "……");
					try {
						//适配器部署到多网卡主机时，只监听与TES通信的网卡地址
						m_lsrSocket = new ServerSocket(m_lsrPort, 0, InetAddress.getByName(m_lsrIP));
						//m_lsrSocket = new ServerSocket(m_lsrPort, 0, null);
						while (m_serverState) { //外部开关
							Socket socket = null;
							try {
								socket = m_lsrSocket.accept();
							} catch (IOException ex) {
								// 如果m_serverState为false 则表示这个accept()的异常是由ServerSocket被关闭引起的 这个异常是正常流程 不进行任何处理 否则需要将其抛出
								if (!m_serverState){
									logger.info("发起端适配器服务器主动停止服务." + ex.getMessage());
									return; //主动退出
								}
								else
									throw new RuntimeException(ex);
							}				
							// 处理请求 每个请求起一个新线程
							//new DefaultRequestAdapterServerWorker(isRecording, m_props.getProperty("ENCODING"), m_localChannelName, socket, m_adapterPluginInstance).start();
							new DefaultRequestAdapterServerWorker(m_localChannelName, socket, m_adapterPluginInstance).start();
						}
					}catch(IOException e){
						logger.error("发起端适配器创建本地监听失败.[" + e.getMessage() + "]");
						System.out.println("发起端适配器创建本地监听失败.[" + e.getMessage() + "]");
					}
				}
				else {
					String adapterType = m_adapterPluginInstance.AdapterType();
					if ("tcp.c".equals(adapterType)) {
						new TCPRecorderLib(m_config_props, m_adapterPluginInstance).start();
					}
					else if ("http.c".equals(adapterType) || "http.s".equals(adapterType)) {
						new HTTPRecorderLib(m_config_props, m_adapterPluginInstance).start();
					}
					else if ("mq.c".equals(adapterType) || "mq.s".equals(adapterType)) {
						new MQRecorderLib(m_config_props, m_adapterPluginInstance).start();
					}
					else if ("udp.c".equals(adapterType) || "udp.s".equals(adapterType)) {
						
					}
					else if ("soap.c".equals(adapterType) || "soap.s".equals(adapterType)) {
						
					}
				}
			}
		});
		
		m_serverThread.start();	//多线程 启动
	}
	
	public void stopServer(){
		m_serverState = false;
		
		if (m_lsrSocket != null)
			try {
				m_lsrSocket.close();
				logger.info("发起端监听服务器已关闭.");
			} catch (IOException e) {
				logger.info("发起端监听服务器关闭失败.[" + e.getMessage() + "]");
			}
	}
	
}
