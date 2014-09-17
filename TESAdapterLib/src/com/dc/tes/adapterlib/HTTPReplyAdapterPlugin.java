package com.dc.tes.adapterlib;

import java.io.UnsupportedEncodingException;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.thread.QueuedThreadPool;

import com.dc.tes.adapter.IReplyAdapter;
import com.dc.tes.adapter.IRequestAdapter;
import com.dc.tes.adapter.context.IAdapterEnvContext;
import com.dc.tes.adapter.context.IReplyAdapterEnvContext;
import com.dc.tes.adapter.helper.IReplyAdapterHelper;
import com.dc.tes.adapter.secure.AbstractFactory;
import com.dc.tes.adapter.secure.IDecryptAdapterSecure;
import com.dc.tes.adapter.secure.IEncryptAdapterSecure;


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
public class HTTPReplyAdapterPlugin implements IReplyAdapter {

	private static Log log = LogFactory.getLog(HTTPReplyAdapterPlugin.class);

	private IReplyAdapterEnvContext m_TESEnv = null;
	private static IReplyAdapterHelper m_adpHelper = null;
	
	private Server server = null;

	// jetty 启动监听端口，默认为 9999
	private int jettyPort = 9999;

	// jetty 监听的最小线程数;如果不配置该项，默认为 10
	private int miniThreadNum = 10;

	// jetty servlet 监听的 url地址;如果不配置该项，默认为 /tes/httpadapter
	private String servletUrl = "/tes/httpadapter";

	// jetty servlet 监听的 url 根地址;如果不配置该项，默认为 /
	private String servletRootUrl = "/";

	// Get 请求参数 名称;如果不配置此项，则用 POST方式接受信息 采用 queryString方式, 本设置废弃
	// public static String parameterName = "";

	private String m_ENCODING = "utf-8";
	
	//处于处于录制状态
	private boolean m_RECORDING = false;
	private int m_SystemId = 0;
	private String m_SystemName = "";
	private int m_NewRecordedCaseId = 0;
	
	// 是否调用安全服务处理接收报文,解密操作,0否 1是
	private static int dynamic_in = -1;

	// 是否调用安全服务处理返回报文,加密操作,0否 1是
	private static int dynamic_out = -1;

	// 处理加解密报文的插件类名
	// HttpReplyFactory\SoapReplyFactory\TcpReplyFactory\HttpRequestFactory\SoapRequestFactory\TcpRequestFactory
	// 中的一种
	private static String dynamic_name = "";

	// HttpReplyFactory\SoapReplyFactory\TcpReplyFactory\HttpRequestFactory\SoapRequestFactory\TcpRequestFactory
	// 包名
	private final static String secureFactoryPackage = "com.dc.tes.adapter.secure.factory.";

	//是否去掉报文头
	public static boolean m_delprefix = false;
	//报文头长度
	public static int m_prefixlen = 0;
	
	/**
	 * 根据 上下文 配置信息，启动 jetty服务，监听被测系统请求
	 */

	private Properties m_config_props = null;
	
	@Override
	public Properties GetAdapterConfigProperties() {
		return m_config_props;
	}
	
	
	public void Start() {
		// 创建 jetty 服务
		
		server = new Server(this.jettyPort);
		QueuedThreadPool threadPool = new QueuedThreadPool();
		threadPool.setMinThreads(this.miniThreadNum);
		server.setThreadPool(threadPool);
		
     // 创建 servlet句柄
		ServletContextHandler servlet = new ServletContextHandler(ServletContextHandler.SESSIONS);
		servlet.setContextPath(this.servletRootUrl);
		ServletHolder servletHolder = null;
		if (!this.m_RECORDING) {
			servletHolder = new ServletHolder(new HTTPReplyAdapterServlet());
		}
		else {
			IRequestAdapter httpRequestAdapterPluginInstance = new HTTPRequestAdapterPlugin();
			servletHolder = new ServletHolder(new HTTPReplyAdapterServlet(m_config_props, httpRequestAdapterPluginInstance));
		}
		servletHolder.setClassName(HTTPReplyAdapterServlet.class.getName());
		servlet.addServlet(servletHolder, this.servletUrl);
        
		server.setHandler(servlet);

		try {
			server.start();
			if (server.isStarted()) {
				System.out.println("Servlet服务启动成功");
			}
			log.info("启动jetty服务成功...");
			log.info("jetty监听端口为:" + this.jettyPort);
			log.info("jetty最少处理线程为:" + this.miniThreadNum);
			log.info("jetty servlet监听地址为:" + this.servletUrl);
			log.info("jetty servlet根地址为:" + this.servletRootUrl);
			log.info("安全服务处理接收报文,解密操作,0否 1是;值为:"	+ HTTPReplyAdapterPlugin.dynamic_in);
			log.info("安全服务处理返回报文,加密操作,0否 1是;值为:"	+ HTTPReplyAdapterPlugin.dynamic_out);
			log.info("处理加解密报文的插件类名为:" + HTTPReplyAdapterPlugin.dynamic_name);
			if (server.isRunning()) {
				System.out.println("Servlet服务isRunning...");
			}
			System.out.println("Servlet服务状态：" + server.getState());
			server.join();
		} catch (Exception ex) {
			log.error("启动jetty服务失败...");
			log.error(ex.getLocalizedMessage());
			System.out.println("启动jetty服务失败...");
			System.out.println(ex.getMessage());
		}
	}

	/**
	 * 关闭 jetty 服务
	 */
	public void Stop() {
		if (server != null) {
			try {
				server.stop();
				log.info("关闭jetty服务成功,该服务的监听端口为:" + this.jettyPort);
			} catch (Exception e) {
				log.error("关闭jetty服务失败,该服务的监听端口为:" + this.jettyPort);
				log.error(e.getLocalizedMessage());
				System.exit(1);
			}
		}
	}

	/**
	 * 初始化环境数据
	 * 
	 * @return 永久为ture,即使未找到任何配置,也可以启动 jetty
	 */
	public boolean Init(IAdapterEnvContext tesENV) {
		log.info("HTTP响应端适配器插件" + this.getClass().getName() + "被初始化.");

		// 获取配置信息
		m_TESEnv = (IReplyAdapterEnvContext) tesENV;
		m_adpHelper = m_TESEnv.getHelper();

		// 处理 核心返回的注册信息
		Properties props = ConfigHelper.getConfig(m_TESEnv.getEvnContext());
		m_config_props = props;

		// 校验必要的初始化信息 是否存在
		String[] keys = new String[] { "jettyPort", "miniThreadNum", "servletUrl", "dynamic_in", "dynamic_out", "dynamic_name" };
		if (!ConfigHelper.chkProperKey(props, keys)) {
			return false;
		}

		this.jettyPort = Integer.parseInt((String) props.get("jettyPort"));
		this.miniThreadNum = Integer.parseInt((String) props.get("miniThreadNum"));
		this.servletUrl = props.getProperty("servletUrl");
		this.servletRootUrl = props.getProperty("servletRootUrl");

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

		dynamic_in = Integer.parseInt((String) props.getProperty("dynamic_in"));
		dynamic_out = Integer.parseInt((String) props.getProperty("dynamic_out"));
		dynamic_name = secureFactoryPackage + props.getProperty("dynamic_name");

		if(props.containsKey("DELPREFIX"))
			m_delprefix = Boolean.parseBoolean((String)props.getProperty("DELPREFIX"));
		
		if(props.containsKey("PREFIXLEN"))
			m_prefixlen = Integer.parseInt((String)props.getProperty("PREFIXLEN"));
		
		log.info("HTTP响应端适配器插件" + this.getClass().getName() + "初始化完成.");
		return true;
	}

	
	/**
	 * 向核心发送 请求
	 * 
	 * @param msg
	 *            适配器接收外围系统的请求信息
	 * @return 核心返回的响应信息(加密\不加密)
	 * @see 1、此函数主要用于 封装加解密
	 */
	public static byte[] sendMsg2Core(byte[] msg) {
		byte[] requestByte = msg; // 从被测系统接收的原始报文
		byte[] responseByte = null; // 核心返回的原始报文
		byte[] requestDecryptByte = null; // 从被测系统接收的原始报文 解密
		byte[] responseEncryptByte = null; // 核心返回的原始报文 加密

		// -------------------处理 从被测系统接收的原始报文 开始...
		if (HTTPReplyAdapterPlugin.dynamic_in == 0) {// 不解密
			log.info("不解密:发送给核心的请求数据为:" + new String(requestByte));
			responseByte = HTTPReplyAdapterPlugin.m_adpHelper.sendToCore(requestByte);
		} else if (HTTPReplyAdapterPlugin.dynamic_in == 1) {// 解密
			IDecryptAdapterSecure iDecrypt = AbstractFactory.getInstance(
					HTTPReplyAdapterPlugin.dynamic_name).getDecryptAdapterSecure();
			requestDecryptByte = iDecrypt.deCrypt(requestByte);
			if (requestDecryptByte == null) {// 解密失败
				log.error("解密失败");
				return null;
			}
			log.info("解密:发送给核心的请求数据为:" + new String(requestDecryptByte));
			responseByte = HTTPReplyAdapterPlugin.m_adpHelper.sendToCore(requestDecryptByte);
		} else {
			log.error("安全服务处理接收报文,配置开关出错" + HTTPReplyAdapterPlugin.dynamic_in);
			return null;
		}
		// -------------------处理 从被测系统接收的原始报文 结束...

		// 将 响应原始字节流 转发给被测系统 (正确的响应报文、错误消息、null都会直接转发给被测系统)
		if (responseByte == null) {
			log.error("核心返回的响应原始字节流数据非法" + responseByte);
			return null;
		} 

		// -------------------处理 核心返回的原始报文 开始...
		if (HTTPReplyAdapterPlugin.dynamic_out == 0) {// 不加密
			log.info("不加密：发送给外围系统的返回数据为:" + new String(responseByte));
			return responseByte;
		} else if (HTTPReplyAdapterPlugin.dynamic_out == 1) {// 加密
			IEncryptAdapterSecure iEncrypt = AbstractFactory.getInstance(
					HTTPReplyAdapterPlugin.dynamic_name).getEncryptAdapterSecure();
			responseEncryptByte = iEncrypt.enCrypt(responseByte);
			if (responseEncryptByte == null) {// 加密失败
				log.error("加密失败");
				return null;
			}
			log.info("加密：发送给外围系统的返回数据为:" + new String(responseEncryptByte));
			return responseEncryptByte;
		} else {
			log.error("安全服务处理返回报文,配置开关出错" + HTTPReplyAdapterPlugin.dynamic_out);
			return null;
		}
		// -------------------处理 核心返回的原始报文 结束...

	}

	public String AdapterType() {
		// TODO Auto-generated method stub
		return "http.s";
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
		 
		 new HTTPReplyAdapterPlugin().Start();
	}

	public long TimeOfAcceptRequest() {
		return HTTPReplyAdapterServlet.TimeOfAcceptRequest;
	}

}
