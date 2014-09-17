package com.dc.tes.adapter.lib;

import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.thread.QueuedThreadPool;

import com.dc.tes.adapter.IRequestAdapter;
import com.dc.tes.adapter.util.ConfigHelper;
import com.dc.tes.adapterlib.*;



/*----------------------------------------------
 该适配器注册，核心返回的信息如下:
 #jetty 启动服务 端口号; 如果不配置该项，默认为 9999 
 jettyPort = 10000

 #jetty 监听的最小线程数;如果不配置该项，默认为 10 
 miniThreadNum = 100
 #jetty servlet 监听的 url地址;如果不配置该项，默认为 /tes/httpadapter 
 servletUrl = /tes1/httpadapter1

 #jetty servlet 监听的 url 根地址;如果不配置该项，默认为 / 
 servletRootUrl = /web

 # 是否调用安全服务处理接收报文(外围系统==>适配器),解密操作,0否 1是
 dynamic_in = 0

 # 是否调用安全服务处理返回报文(适配器==>外围系统),加密操作,0否 1是
 dynamic_out = 1

 # 处理接受报文的插件类名 HttpReplyFactory\SoapReplyFactory\TcpReplyFactory\HttpRequestFactory\SoapRequestFactory\TcpRequestFactory 中的一种
 dynamic_name = HttpReplyFactory
 ----------------------------------------------*/

/**
 * HTTP 服务端 适配器 (被测系统==>该适配器==>核心)
 * 
 * @author 王春佳
 * 
 * @see 1、接收到被测系统的请求报文，尚未进行 解码(Decoder)
 */
public class HTTPRecorderLib extends Thread {

	private static Log log = LogFactory.getLog(HTTPRecorderLib.class);

	protected static IRequestAdapter m_adapterPluginInstance = null;
	
	public static boolean m_isServiceStarted = false;
	
	private static Server server = null;

	// jetty 启动监听端口，默认为 9999
	private static int jettyPort = 9999;

	// jetty 监听的最小线程数;如果不配置该项，默认为 10
	private static int miniThreadNum = 1;

	// jetty servlet 监听的 url地址;如果不配置该项，默认为 /tes/httpadapter
	private static String servletUrl = "/tes/httpadapter";

	// jetty servlet 监听的 url 根地址;如果不配置该项，默认为 /
	private static String servletRootUrl = "/";

	// 是否调用安全服务处理接收报文,解密操作,0否 1是
	private static int dynamic_in = -1;

	// 是否调用安全服务处理返回报文,加密操作,0否 1是
	private static int dynamic_out = -1;

	// 处理加解密报文的插件类名
	private static String dynamic_name = "";

	//是否去掉报文头
	public static boolean m_delprefix = false;
	//报文头长度
	public static int m_prefixlen = 0;

	private static Properties m_config_props = null;
	
	// 包名
	private final static String secureFactoryPackage = "com.dc.tes.adapter.secure.factory.";

	
	/**
	 * 根据 上下文 配置信息，启动 jetty服务，监听被测系统请求
	 */
	
	public HTTPRecorderLib(Properties props, IRequestAdapter api) {
		super();
		m_config_props = props;
		m_adapterPluginInstance = api;
		Init(props);
	}
	
	public void run() {
		HttpServerStart();
	}
	
	public static void HttpServerStart() {
		// 创建 jetty 服务
		
		server = new Server(jettyPort);
		QueuedThreadPool threadPool = new QueuedThreadPool();
		threadPool.setMinThreads(miniThreadNum);
		server.setThreadPool(threadPool);
		
		// 创建 servlet 句柄
		ServletContextHandler servlet = new ServletContextHandler(ServletContextHandler.SESSIONS);
		servlet.setContextPath("/");
        ServletHolder servletHolder = new ServletHolder(new HTTPReplyAdapterServlet(m_config_props, m_adapterPluginInstance));
		servletHolder.setClassName(HTTPReplyAdapterServlet.class.getName());
		//servlet.addServlet(servletHolder, servletRootUrl);
		servlet.addServlet(servletHolder, servletRootUrl);
        
		server.setHandler(servlet);

		try {
			server.start(); 
			if (server.isStarted()) {
				System.out.println("Servlet服务启动成功");
			}
			log.info("启动jetty服务成功...");
			log.info("jetty监听端口为:" + jettyPort);
			log.info("jetty最少处理线程为:" + miniThreadNum);
			log.info("jetty servlet监听地址为:" + "127.0.0.1"); // servletUrl
			log.info("jetty servlet根地址为:" + servletRootUrl);
			log.info("安全服务处理接收报文,解密操作,0否 1是;值为:"	+ HTTPRecorderLib.dynamic_in);
			log.info("安全服务处理返回报文,加密操作,0否 1是;值为:"	+ HTTPRecorderLib.dynamic_out);
			log.info("处理加解密报文的插件类名为:" + HTTPRecorderLib.dynamic_name);
			if (server.isRunning()) {
				System.out.println("Servlet服务isRunning...");
			}
			System.out.println("Servlet服务状态：" + server.getState());
			m_isServiceStarted = true;
			server.join();
		} catch (Exception ex) {
			log.error("启动jetty服务失败...");
			log.error(ex.getLocalizedMessage());
			System.out.print("启动jetty服务失败：" + ex.getMessage());
		}
	}

	/**
	 * 关闭 jetty 服务
	 */
	public static void Stop() {
		if (server != null) {
			try {
				server.stop();
				log.info("关闭jetty服务成功,该服务的监听端口为:" + jettyPort);
			} catch (Exception e) {
				log.error("关闭jetty服务失败,该服务的监听端口为:" + jettyPort);
				log.error(e.getLocalizedMessage());
				System.exit(1);
			}
		}
		m_isServiceStarted = false;
	}

	/**
	 * 初始化环境数据
	 * 
	 * @return 永久为ture,即使未找到任何配置,也可以启动 jetty
	 */
	public static boolean Init(Properties props) {

		// 校验必要的初始化信息 是否存在
		//String[] keys = new String[] { "jettyPort", "miniThreadNum", "servletUrl", "dynamic_in", "dynamic_out", "dynamic_name" };
		String[] keys = new String[] { "targetUrl", "targetServlet", "method", "dynamic_in", "dynamic_out", "dynamic_name" };
		if (!ConfigHelper.chkProperKey(props, keys)) {
			return false;
		}

		String targetUrl = (String) props.get("targetUrl");
		String url = targetUrl.toLowerCase().replace("http:", "");
		int iPos = url.indexOf(":");
		if (iPos < 0) {
			System.out.println("配置信息targetUrl中必须包含有端口信息，请检查配置！");
			return false;
		}
		String strListenPort = url.substring(iPos+1);
		if (strListenPort == null || strListenPort.isEmpty()) {
			System.out.println("配置信息targetUrl中必须包含有端口信息，请检查配置！");
			return false;
		}
		strListenPort = strListenPort.trim();
		jettyPort = Integer.parseInt(strListenPort);
		miniThreadNum = 1; //Integer.parseInt((String) props.get("miniThreadNum"));
		servletUrl = props.getProperty("servletUrl");
		targetUrl = "http://99.6.80.5:" + strListenPort;
		//props.setProperty("servletUrl", servletUrl);
		props.setProperty("targetUrl", targetUrl);
		servletRootUrl = props.getProperty("targetServlet");

		dynamic_in = Integer.parseInt((String) props.getProperty("dynamic_in"));
		dynamic_out = Integer.parseInt((String) props.getProperty("dynamic_out"));
		dynamic_name = secureFactoryPackage + props.getProperty("dynamic_name");

		if(props.containsKey("DELPREFIX")) {
			m_delprefix = Boolean.parseBoolean((String)props.getProperty("DELPREFIX"));
		}
		
		if(props.containsKey("PREFIXLEN")) {
			m_prefixlen = Integer.parseInt((String)props.getProperty("PREFIXLEN"));
		}

		return true;
	}

	
	// 测试 服务端适配器 临时函数
	public static void main(String args[]) {
		 Properties props = new Properties();
		 props.put("jettyPort", "10000");
		 props.put("miniThreadNum", "100");
		 props.put("servletUrl", "/tes1/httpadapter1");
		 props.put("servletRootUrl", "/web");
		 props.put("dynamic_in", "0");
		 props.put("dynamic_out", "0");
		 props.put("dynamic_name", "HttpReplyFactory");
				 
		 String[] keys = new String[] { "jettyPort", "miniThreadNum",
		 "servletUrl", "dynamic_in", "dynamic_out", "dynamic_name" };
		 if (!ConfigHelper.chkProperKey(props, keys))
			 System.out.println("---有一个key 不存在");
		 else{
			 System.out.println("都存在");
		 }
		 HttpServerStart();
	}


}
