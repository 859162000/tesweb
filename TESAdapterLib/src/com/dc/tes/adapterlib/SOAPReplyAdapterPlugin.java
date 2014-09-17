package com.dc.tes.adapterlib;

import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.thread.QueuedThreadPool;


import com.dc.tes.adapter.IReplyAdapter;
import com.dc.tes.adapter.context.IAdapterEnvContext;
import com.dc.tes.adapter.context.IReplyAdapterEnvContext;
import com.dc.tes.adapter.helper.IReplyAdapterHelper;
import com.dc.tes.adapter.secure.AbstractFactory;
import com.dc.tes.adapter.secure.IDecryptAdapterSecure;
import com.dc.tes.adapter.secure.IEncryptAdapterSecure;


/*-------------------------------------------
 该适配器注册，核心返回的信息如下:

 #jetty 启动服务 端口号; 如果不配置该项，默认为 8888 
 jettyPort = 10000

 #jetty 监听的最小线程数;如果不配置该项，默认为 10 
 miniThreadNum = 100

 #jetty servlet 监听的 url地址;如果不配置该项，默认为 /tes/soapadapter 
 servletUrl = /tes1/soapadapter1

 #jetty servlet 监听的 url 根地址;如果不配置该项，默认为 / 
 servletRootUrl = /web

 # 是否调用安全服务处理接收报文(外围系统==>适配器),解密操作,0否 1是
 dynamic_in = 0

 # 是否调用安全服务处理返回报文(适配器==>外围系统),加密操作,0否 1是
 dynamic_out = 1

 # 处理接受报文的插件类名 HttpReplyFactory\SoapReplyFactory\TcpReplyFactory\HttpRequestFactory\SoapRequestFactory\TcpRequestFactory 中的一种
 dynamic_name = SoapReplyFactory
 -------------------------------------------*/

/**
 * SOAP 服务端 适配器 (被测系统==>该适配器==>核心)
 * 
 * @author 王春佳
 * 
 */
public class SOAPReplyAdapterPlugin implements IReplyAdapter {

	private static Log logger = LogFactory.getLog(SOAPReplyAdapterPlugin.class);

	private IReplyAdapterEnvContext m_TESEnv = null;
	private static IReplyAdapterHelper m_adpHelper = null;
	private Server server = null;

	// jetty 启动监听端口，默认为 8888
	private int jettyPort = 8888;

	// jetty 监听的最小线程数;如果不配置该项，默认为 10
	private int miniThreadNum = 10;

	// jetty servlet 监听的 url地址;如果不配置该项，默认为 /tes/soapadapter
	private String servletUrl = "/tes/soapadapter";

	// jetty servlet 监听的 url 根地址;如果不配置该项，默认为 /
	private String servletRootUrl = "/";

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

	private Properties m_config_props = null;
	
	@Override
	public Properties GetAdapterConfigProperties() {
		return m_config_props;
	}
	
	
	// 启动 jetty 服务,监听 SOAP 请求
	public void Start() {
		// 创建 jetty 服务
		server = new Server(this.jettyPort);
		QueuedThreadPool threadPool = new QueuedThreadPool();
		threadPool.setMinThreads(this.miniThreadNum);
		server.setThreadPool(threadPool);
	     // 创建 servlet 句柄			
		ServletContextHandler servlet = new ServletContextHandler(ServletContextHandler.SESSIONS);
		servlet.setContextPath(this.servletRootUrl);
        ServletHolder servletHolder = new ServletHolder(new SOAPReplyAdapterServlet());
		servletHolder.setClassName(HTTPReplyAdapterServlet.class.getName());
		servlet.addServlet(servletHolder, this.servletUrl);
        
		server.setHandler(servlet);
		

		try {
			server.start();
			logger.info("启动jetty服务成功...");
			logger.info("jetty监听端口为:" + this.jettyPort);
			logger.info("jetty最少处理线程为:" + this.miniThreadNum);
			logger.info("jetty servlet监听地址为:" + this.servletUrl);
			logger.info("jetty servlet根地址为:" + this.servletRootUrl);
			logger.info("安全服务处理接收报文,解密操作,0否 1是;值为:"
					+ SOAPReplyAdapterPlugin.dynamic_in);
			logger.info("安全服务处理返回报文,加密操作,0否 1是;值为:"
					+ SOAPReplyAdapterPlugin.dynamic_out);
			logger.info("处理加解密报文的插件类名为:" + SOAPReplyAdapterPlugin.dynamic_name);
			server.join();
		} catch (Exception ex) {
			logger.error("启动jetty服务失败...");
			logger.error(ex.getLocalizedMessage());
		}

	}

	// 关闭 jetty 服务
	public void Stop() {
		if (server != null) {
			try {
				server.stop();
				logger.info("关闭jetty服务成功,该服务的监听端口为:" + this.jettyPort);
			} catch (Exception e) {
				logger.error("关闭jetty服务失败,该服务的监听端口为:" + this.jettyPort);
				logger.error(e.getLocalizedMessage());
				System.exit(1);
			}
		}

	}

	// 初始化 SOAP 适配器 上下文
	// @return 永久为ture,即使未找到任何配置,也可以启动 jetty
	public boolean Init(IAdapterEnvContext tesENV) {
		logger.info("SOAP响应端适配器插件" + this.getClass().getName() + "被初始化.");

		// 获取配置信息
		m_TESEnv = (IReplyAdapterEnvContext) tesENV;
		m_adpHelper = m_TESEnv.getHelper();

		// 处理 核心返回的注册信息
		Properties props = ConfigHelper.getConfig(m_TESEnv.getEvnContext());
		m_config_props = props;

		// 校验必要的初始化信息 是否存在
		String[] keys = new String[] { "jettyPort", "miniThreadNum",
				"servletUrl", "servletRootUrl", "dynamic_in", "dynamic_out",
				"dynamic_name" };
		if (!ConfigHelper.chkProperKey(props, keys))
			return false;

		this.jettyPort = Integer.parseInt((String) props.get("jettyPort"));
		this.miniThreadNum = Integer.parseInt((String) props
				.get("miniThreadNum"));
		this.servletUrl = props.getProperty("servletUrl");
		this.servletRootUrl = props.getProperty("servletRootUrl");
		dynamic_in = Integer.parseInt((String) props
				.getProperty("dynamic_in"));
		dynamic_out = Integer.parseInt((String) props
				.getProperty("dynamic_out"));
		dynamic_name = secureFactoryPackage
				+ props.getProperty("dynamic_name");

		logger.info("SOAP响应端适配器插件" + this.getClass().getName() + "初始化完成.");
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
		if (SOAPReplyAdapterPlugin.dynamic_in == 0) {// 不解密
			logger.info("不解密:发送给核心的请求数据为:" + new String(requestByte));
			responseByte = SOAPReplyAdapterPlugin.m_adpHelper
					.sendToCore(requestByte);
		} else if (SOAPReplyAdapterPlugin.dynamic_in == 1) {// 解密
			IDecryptAdapterSecure iDecrypt = AbstractFactory.getInstance(
					SOAPReplyAdapterPlugin.dynamic_name)
					.getDecryptAdapterSecure();
			requestDecryptByte = iDecrypt.deCrypt(requestByte);
			if (requestDecryptByte == null) {// 解密失败
				logger.error("解密失败");
				return null;
			}
			logger.info("解密:发送给核心的请求数据为:" + new String(requestDecryptByte));
			responseByte = SOAPReplyAdapterPlugin.m_adpHelper
					.sendToCore(requestDecryptByte);
		} else {
			logger.error("安全服务处理接收报文,配置开关出错"
					+ SOAPReplyAdapterPlugin.dynamic_in);
			return null;
		}
		// -------------------处理 从被测系统接收的原始报文 结束...

		logger.info("响应原始字节流数据为:" + new String(responseByte));

		// 将 响应原始字节流 转发给被测系统 (正确的响应报文、错误消息、null都会直接转发给被测系统)
		if (responseByte == null) {
			logger.error("响应原始字节流数据非法" + responseByte);
			return null;
		}

		// -------------------处理 核心返回的原始报文 开始...
		if (SOAPReplyAdapterPlugin.dynamic_out == 0) {// 不加密
			logger.info("不加密:发送给外围系统的返回数据为:" + new String(responseByte));
			return responseByte;
		} else if (SOAPReplyAdapterPlugin.dynamic_out == 1) {// 加密
			IEncryptAdapterSecure iEncrypt = AbstractFactory.getInstance(
					SOAPReplyAdapterPlugin.dynamic_name)
					.getEncryptAdapterSecure();
			responseEncryptByte = iEncrypt.enCrypt(responseByte);
			if (responseEncryptByte == null) {// 加密失败
				logger.error("加密失败");
				return null;
			}
			logger.info("加密：发送给外围系统的返回数据为:" + new String(responseEncryptByte));
			return responseEncryptByte;
		} else {
			logger.error("安全服务处理接收报文,配置开关出错"
					+ SOAPReplyAdapterPlugin.dynamic_out);
			return null;
		}
		// -------------------处理 核心返回的原始报文 结束...
	}

	public String AdapterType() {
		return "soap.s";
	}

	public long TimeOfAcceptRequest() {
		return SOAPReplyAdapterServlet.TimeOfAcceptRequest;
	}

}
