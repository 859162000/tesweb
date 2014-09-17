package com.dc.tes.adapterlib;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.dc.tes.adapter.IReplyAdapter;
import com.dc.tes.adapter.context.IAdapterEnvContext;
import com.dc.tes.adapter.context.IReplyAdapterEnvContext;
import com.dc.tes.adapter.helper.IReplyAdapterHelper;


/*
 *安徽移动：短号平台与营帐系统的接口规范使用MML规范
 *
 *与TCP适配器模板的区别：心跳信息由适配器返回，因为与一般交易报文的交易码识别规则不一致
 * 
 */
public class MMLTCPReplyAdapter  implements IReplyAdapter{
private static Log log = LogFactory.getLog(MMLTCPReplyAdapter.class);
	

	private IReplyAdapterEnvContext m_TESEnv = null;
	private IReplyAdapterHelper m_adpHelper = null;
	
	private ServerSocket m_lsrSocket = null;
	private int m_lsrPort = -1;
	

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
		if (m_lsrSocket != null)
			try {
				m_lsrSocket.close();
			} catch (IOException e) {
				log.error("响应端适配器关闭失败.[" + e.getMessage() + "]");
				e.printStackTrace();
			}
		log.info("响应端适配器已关闭.");
	}

	/**
	 * 启动响应端适配器
	 * 
	 */
	private void startServer(){
		try {
			this.m_lsrSocket = new ServerSocket(this.m_lsrPort);
			while (m_serverState) {
				Socket socket = null;
				try {
					socket = m_lsrSocket.accept();
					
					
				} catch (IOException ex) {
					// 如果m_serverState为false 则表示这个accept()的异常是由ServerSocket被关闭引起的 这个异常是正常流程 不进行任何处理 否则需要将其抛出
					if (!m_serverState)
						return;
					else
						throw new RuntimeException(ex);
				}
				
				log.debug("接收到连接请求来自：" + socket.getRemoteSocketAddress());
				
				//启动单独线程处理到来的请求
				new MMLTCPReplyAdapterWorkerThread(socket, m_TESEnv, m_adpHelper).start();
			}
		

		} catch (IOException e) {
			log.error("error: 响应端适配器发生异常退出.[" + e.getMessage() + "]");
		}
	}
	
	

	
	
	public void Start() {
		// TODO Auto-generated method stub
		startServer();
	}

	public void Stop() {
		// TODO Auto-generated method stub
		stopServer();
	}

	public String AdapterType() {
		// TODO Auto-generated method stub
		return "tcp.s";
	}

	public long TimeOfAcceptRequest() {
		return 0;
	}
}
