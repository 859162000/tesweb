package com.dc.tes.channel.remote;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.dc.tes.Config;
import com.dc.tes.channel.localchannel.ILocalChannel;
import com.dc.tes.data.model.SysType;
import com.dc.tes.util.RuntimeUtils;
import com.dc.tes.util.XmlUtils;

/**
 * 通道服务器
 * 
 * @author lijic
 * 
 */
public class ChannelServer {
	private static final Log log = LogFactory.getLog(ChannelServer.class);
	/**
	 * 远程通道列表
	 */
	static Map<String, IRemoteChannel> s_channels = new HashMap<String, IRemoteChannel>();
	static Map<String, ILocalChannel>  l_channels = new HashMap<String, ILocalChannel>();
	/**
	 * 监听套接字
	 */
	private static ServerSocket s_server;

	/**
	 * 服务器状态标志 如果为false则表示已经下达了停止服务器指令
	 */
	private static boolean s_serverFlag = true;

	/**
	 * 启动通道服务器 开始进行监听
	 * 
	 * @param channels
	 *            通道列表
	 */
	public static void Start(SysType instance, Config config) throws IOException, SAXException, ParserConfigurationException, NumberFormatException, XPathExpressionException {
		// 根据base.xml中的port配置初始化SocketServer
		Document doc;
		if (config.CONFIG_FROM_BASEXML)
			doc = XmlUtils.LoadXml(RuntimeUtils.OpenResource("base.xml"));
		else 
			doc = XmlUtils.LoadXml(instance.getBasecfg());
		
		int port = Integer.parseInt(XmlUtils.SelectNodeText(doc, "//config/port"));
		s_server = new ServerSocket(port);

		log.info("开始监听端口" + port + "...");

		new Thread(new Runnable() {
			@Override
			public void run() {
				while (s_serverFlag) {
					Socket socket = null;
					try {
						socket = s_server.accept();
					} catch (IOException ex) {
						// 如果s_serverFlag为false 则表示这个accept()的异常是由ServerSocket被关闭引起的 这个异常是正常流程 不进行任何处理 否则需要将其抛出
						if (!s_serverFlag)
							return;
						else
							throw new RuntimeException(ex);
					}

					// 处理请求 每个请求起一个新线程
					new ChannelServerProcessThread(socket).start();
				}
			}
		}, ChannelServer.class.getSimpleName()).start();
	}

	/**
	 * 停止服务器
	 * 
	 * @throws IOException
	 */
	public static void Stop() throws IOException {
		log.info("正在关闭通道服务器...");
		s_serverFlag = false;
		if (s_server != null)
			s_server.close();
		log.info("通道服务器已关闭.");
	}

	/**
	 * 将远程通道附加到通道服务器上
	 * 
	 * @param name
	 *            通道名称
	 * @param channel
	 *            要被注册的通道
	 */
	public static synchronized void AttachChannel(String name, IRemoteChannel channel) {
		s_channels.put(name, channel);
		log.debug("远程通道已附加到通道服务器：" + name + "[" + channel.getClass().getName() + "]");
	}

	/**
	 * 将远程通道从通道服务器上分离
	 * 
	 * @param channel
	 *            要被分离的通道的名称
	 */
	public static synchronized void DetachChannel(String name) {
		s_channels.remove(name);
		log.debug("远程通道已从通道服务器分离：" + name);
	}
	
	public static synchronized void AttachLocalChannel(String name, ILocalChannel channel) {
		l_channels.put(name, channel);
		log.debug("本地通道已附加到通道服务器：" + name + "[" + channel.getClass().getName() + "]");
	}

	public static synchronized void DetachLocalChannel(String name) {
		l_channels.remove(name);
		log.debug("本地通道已从通道服务器分离：" + name);
	}
	public static boolean LChannelcontainsKey(String name){
		return ChannelServer.l_channels.containsKey(name);
	}
	public static ILocalChannel LChannelGet(String name){
		return ChannelServer.l_channels.get(name);
	}
	public static int LChannleNum(){
		return ChannelServer.l_channels.size();
	}
}
