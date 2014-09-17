package com.dc.tes.channel.internal;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;

import com.dc.tes.Core;
import com.dc.tes.net.Message;
import com.dc.tes.util.RuntimeUtils;
import com.dc.tes.util.XmlUtils;

/**
 * 日志监控通道虚基类
 * 
 * @author lijic
 * 
 */
public abstract class AbstractLogChannel implements ILogChannel {
	protected static final Log log = LogFactory.getLog(AbstractLogChannel.class);
	protected static String host;
	protected static int port;

	/**
	 * 与监控服务相连的Socket
	 */
	protected Socket m_socket = null;
	/**
	 * 核心实例
	 */
	protected Core m_core;

	@Override
	public boolean getChannelState() {
		return host != null;
	}


	public boolean Connected(){
		if (this.m_socket == null) {
			try {
				this.m_socket = new Socket(host,port);
			}  catch (Exception e) {
				// TODO Auto-generated catch block
				log.error("日志监控通道启动失败", e);
				this.m_socket = null;
			}
			if (this.m_socket == null)
				return false;
		}
		return true;
	}
	
	
	public boolean reConnect() {
		
		if(this.m_socket == null) {
			ConnectMonitor();
			if(this.m_socket == null) 
				return false;
		}
		return true;
		
	}

	public boolean isConnected(){
		if (this.m_socket == null) {
			return false;
		}
		return true;
	}
	@Override
	public void Start(Core core) throws Exception {
		this.m_core = core;

		// 读配置文件
		Document doc = XmlUtils.LoadXml(RuntimeUtils.OpenResource("base.xml"));
		// 如果配置文件中存在monitor配置节
		if (XmlUtils.SelectNode(doc, "//config/monitor") != null) {
			// 读监控服务IP
			host = XmlUtils.SelectNodeText(doc, "//config/monitor/host");
			// 读监控服务端口
			port = Integer.parseInt(XmlUtils.SelectNodeText(doc,
					"//config/monitor/port"));
		} else {
			log.warn("日志监控通道未启动：在配置文件中未找到监控服务配置信息");
			return;
		}
		ConnectMonitor();
	}

	protected void ConnectMonitor() {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void Stop() throws Exception {
		if (this.m_socket != null)
			this.m_socket.close();
	}

	/**
	 * 工具函数 向监控服务发消息
	 * 
	 * @throws IOException
	 */
	protected synchronized boolean send(Message msg) {
		// 如果socket没有起,重新建立连接,如果仍然连接失败,则返回通讯失败
		if (!this.isConnected())
			return false;

		try {
			// 向监控服务送消息
			this.m_socket.getOutputStream().write(msg.Export());
			return true;
		} catch (IOException ex) {
			// 当通讯失败时要做一些清理工作
			try {
				// 如果通讯失败 则关闭socket
				log.error("通讯失败，关闭socket");
				this.m_socket.close();
			} catch (IOException ex1) {
				log.error("关闭与监控服务的通讯连接时发生异常", ex);
			}

			// 如果通讯失败 则将socket置为null 此动作表示该日志通道不再可用
			this.m_socket = null;
			
			//重试
			new Timer(true).schedule(new TimerTask() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					if(reConnect()) {
						this.cancel();
					}
				}
				
			},0, 1000 * 5);
			

			log.error("因为发生异常，日志监控通道已经关闭，正在重试...");
			ex.printStackTrace();
			return false;
		}
	}
}
