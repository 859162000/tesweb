package com.dc.tes.adapterlib;

import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.dc.tes.adapter.IRequestAdapter;
import com.dc.tes.adapter.context.IAdapterEnvContext;
import com.dc.tes.adapter.context.IRequestAdapterEnvContext;
import com.dc.tes.adapter.secure.AbstractFactory;
import com.dc.tes.adapter.secure.IDecryptAdapterSecure;
import com.dc.tes.adapter.secure.IEncryptAdapterSecure;


/*----------------------------------------------------
 该适配器注册，核心返回的注册信息如下:
 # method 请求方式，目前提供  SOAP\GET\POST 三种方式
 method = SOAP

 # host 地址
 host = http://www.webxml.com.cn

 # url 地址
 url = /webservices/qqOnlineWebService.asmx

 # content-type 类型  POST\SOAP方式需配置
 content-type = text/xml; charset=utf-8

 # SOAPAction ，如果是 SOAP1.1请求　该字段必须配置,值不能为空
 SOAPAction = http://WebXml.com.cn/qqCheckOnline
 
 # 是否调用安全服务处理接收报文(外围系统==>适配器),解密操作,0否 1是
 dynamic_in = 0

 # 是否调用安全服务处理返回报文(适配器==>外围系统),加密操作,0否 1是
 dynamic_out = 1

 # 处理接受报文的插件类名 HttpReplyFactory\SoapReplyFactory\TcpReplyFactory\HttpRequestFactory\SoapRequestFactory\TcpRequestFactory 中的一种
 dynamic_name = SoapRequestFactory
 ----------------------------------------------------*/

/**
 * SOAP 客户端 适配器 (核心==>该适配器==>被测系统)
 * 
 * @author 王春佳
 */
public class SOAPRequestAdapterPlugin implements IRequestAdapter {

	private static Log logger = LogFactory
			.getLog(SOAPRequestAdapterPlugin.class);

	private IRequestAdapterEnvContext m_TESEnv = null;

	// SOAP 请求方式
	private static final String methodGet = "GET";
	private static final String methodPost = "POST";
	private static final String methodSOAP = "SOAP";

	// method 请求方式
	private String method = "";

	// host 地址
	protected static String host = "";

	// url 地址
	protected static String url = "";

	// content-type 类型
	protected static String contentType = "";

	// SOAPAction ，如果是 SOAP1.1请求　该字段必须配置
	protected static String SOAPAction = "";
	
	// 是否调用安全服务处理接收报文,解密操作,0否 1是
	private static int dynamic_in = -1;

	// 是否调用安全服务处理返回报文,加密操作,0否 1是
	private static int dynamic_out = -1;

	// 处理加解密报文的插件类名
	// HttpReplyFactory\SoapReplyFactory\TcpReplyFactory\HttpRequestFactory\SoapRequestFactory\TcpRequestFactory
	// 中的一种
	private static String dynamic_name = "";
	
	private final static String secureFactoryPackage = "com.dc.tes.adapter.secure.factory.";

	private Properties m_config_props = null;
	
	@Override
	public Properties GetAdapterConfigProperties() {
		return m_config_props;
	}
	
	
	/**
	 * 向被测系统发送数据
	 * 
	 * @param msg
	 *            : 核心发起的数据(待发送给被测系统)
	 * 
	 * @return 被测系统返回的数据
	 */
	public byte[] Send(byte[] msg) throws Exception {
		// 被测系统返回的 应答报文 (被测系统==>该适配器)
		byte[] responseByte = null;

		System.out.println(new String(msg, "GB2312")); //DEBUG
		
		// ---------------------处理 核心发送的请求报文数据 开始...
		if (SOAPRequestAdapterPlugin.dynamic_out == 0) {// 不加密
			logger.info("不加密:核心发送的请求报文数据为:" + new String(msg));
		} else if (SOAPRequestAdapterPlugin.dynamic_out == 1) {// 加密
			IEncryptAdapterSecure iEncrypt = AbstractFactory.getInstance(
					SOAPRequestAdapterPlugin.dynamic_name)
					.getEncryptAdapterSecure();
			msg = iEncrypt.enCrypt(msg);
			if (msg == null) {
				logger.error("加密失败");
				return null;
			}
			logger.info("加密:核心发送的请求报文数据为:" + new String(msg));
		} else {
			logger.error("安全服务处理返回报文,配置开关出错"
					+ SOAPRequestAdapterPlugin.dynamic_out);
			return null;
		}
		// ---------------------处理 核心发送的请求报文数据 开始...
		
		if (methodGet.equals(this.method)) {// GET 方式 发送
			responseByte = SOAPRequestAdapterClient.sendByGET(msg);
		} else if (methodPost.equals(this.method)) { // POST 方式发送
			responseByte = SOAPRequestAdapterClient.sendByPOST(msg);
		} else if (methodSOAP.equals(this.method)) { // SOAP 方式发送
			responseByte = SOAPRequestAdapterClient.sendBySOAP(msg);
		} else {
			logger.fatal("请求方式无效:" + this.method);
		}
		
		// -------------------处理 从被测系统接收的原始报文 开始...
		if (SOAPRequestAdapterPlugin.dynamic_in == 0) {// 不解密
			logger.info("不解密:被测系统返回的原始数据,转发给核心:" + new String(responseByte));
		} else if (SOAPRequestAdapterPlugin.dynamic_in == 1) {// 解密
			IDecryptAdapterSecure iDecrypt = AbstractFactory.getInstance(
					SOAPRequestAdapterPlugin.dynamic_name)
					.getDecryptAdapterSecure();
			responseByte = iDecrypt.deCrypt(responseByte);
			logger.info("解密:解密后的原始数据,转发给核心:" + new String(responseByte));
		} else {
			logger.error("安全服务处理返回报文,配置开关出错"
					+ SOAPRequestAdapterPlugin.dynamic_in);
			return null;
		}
		// -------------------处理 从被测系统接收的原始报文 结束...

		return responseByte;
	}

	/**
	 * 初始化环境数据
	 * 
	 * @return 获取核心返回注册信息任何一项失败，都初始化失败——false，则不执行Send操作
	 */
	public boolean Init(IAdapterEnvContext tesENV) {
		logger.info("SOAP客户端适配器插件" + this.getClass().getName() + "被初始化.");

		this.m_TESEnv = (IRequestAdapterEnvContext) tesENV;
		Properties props = ConfigHelper.getConfig(this.m_TESEnv.getEvnContext());
		m_config_props = props;

		// 校验必要的初始化信息 是否存在
		String[] keys = new String[] { "method", "host",
				"url", "content-type", "dynamic_in", "dynamic_out",
				"dynamic_name" };
		if (!ConfigHelper.chkProperKey(props, keys))
			return false;
		
		
		this.method = (String) props.get("method");
		host = (String) props.get("host");
		url = (String) props.get("url");
		contentType = (String) props
				.get("content-type");
		SOAPAction = (String) props.get("SOAPAction");

		// 校验 method 合法性
		if (("".equals(this.method)) || (this.method == null)) {
			logger.error("请求方式非法:" + this.method);
			logger.error("SOAP客户端适配器插件" + this.getClass().getName() + "初始化失败.");
			return false;
		} else {
			if (!methodGet.equals(this.method)
					&& !methodPost.equals(this.method)
					&& !methodSOAP.equals(this.method)) {
				logger.error("请求方式不在处理范围内,目前只支持GET、POST、SOAP方式:" + this.method);
				logger.error("SOAP客户端适配器插件" + this.getClass().getName()
						+ "初始化失败.");
				return false;
			}
		}
		logger.info("method 请求方式:" + method);

		// 校验 host 合法性
		if (("".equals(host)) || (host == null)) {
			logger.error("host地址非法:" + host);
			logger.error("SOAP客户端适配器插件" + this.getClass().getName() + "初始化失败.");
			return false;
		}
		logger.info("host 地址:" + host);

		// 校验 url 合法性
		if (("".equals(url)) || (url == null)) {
			logger.error("url地址非法:" + url);
			logger.error("SOAP客户端适配器插件" + this.getClass().getName() + "初始化失败.");
			return false;
		}
		logger.info("url 地址:" +url);

		// 校验 contentType 合法性
		// if (("".equals(contentType)) || (contentType == null)) {
		// logger.error("contentType内容非法:" + contentType);
		// logger.error("SOAP客户端适配器插件" + this.getClass().getName() + "初始化失败.");
		// return false;
		// }
		logger.info("content-type内容:" + contentType);

		// SOAP 无需 校验
		logger.info("SOAPAction内容:" + SOAPAction);
		
		dynamic_in = Integer.parseInt(String
				.valueOf(props.getProperty("dynamic_in")));
		logger.info("安全服务处理接收报文,解密操作,0否 1是;值为:"
				+ dynamic_in);

		dynamic_out = Integer.parseInt(String
				.valueOf(props.getProperty("dynamic_out")));
		logger.info("安全服务处理返回报文,加密操作,0否 1是;值为:"
				+ dynamic_out);

		dynamic_name = secureFactoryPackage + props
				.getProperty("dynamic_name");
		logger.info("处理加解密报文的插件类名为:" + dynamic_name);
		

		logger.info("SOAP客户端适配器插件" + this.getClass().getName() + "初始化完成.");
		return true;
	}


	public String AdapterType() {
		// TODO Auto-generated method stub
		return "soap.c";
	}

	// 模拟测试 入口, 模拟核心发送数据到 该适配器
//	public static void main(String args[]) {
//		// new SOAPRequestAdapterPlugin().testGet();
//		//new SOAPRequestAdapterPlugin().testPost();
//		new SOAPRequestAdapterPlugin().testSoap();
//		//new SOAPRequestAdapterPlugin().testSoap1();
//	}

	// 测试 get 发送 SOAP 请求 方法
//	public void testGet() {
//		this.method = "GET";
//		this.host = "http://www.webxml.com.cn";
//		this.url = "/webservices/qqOnlineWebService.asmx/qqCheckOnline";
//
//		byte[] msg = "qqCode=59228177".getBytes();
//
//		try {
//			byte[] res = Send(msg);
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	};

	// 测试 post 发送 SOAP 请求方法
//	public void testPost() {
//		this.method = "POST";
//		this.host = "http://www.webxml.com.cn";
//		this.url = "/webservices/qqOnlineWebService.asmx/qqCheckOnline";
//		this.contentType = "application/x-www-form-urlencoded";
//
//		byte[] msg = "qqCode=59228177".getBytes();
//
//		try {
//			byte[] res = Send(msg);
//			System.out.println("res is:" + new String(res));
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//
//	}
	
	// 测试 soap 发送的 SOAP 请求方法
//	public void testSoap(){
//		this.method = "SOAP";
//		this.host = "http://www.webxml.com.cn";
//		this.url = "/webservices/qqOnlineWebService.asmx";
//		//this.contentType = "text/xml; charset=utf-8";
//		this.SOAPAction = "http://WebXml.com.cn/qqCheckOnline";
//		
//		byte[] msg = "<?xml version=\"1.0\" encoding=\"utf-8\"?><soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\"><soap:Body><qqCheckOnline xmlns=\"http://WebXml.com.cn/\"> <qqCode>59228177</qqCode> </qqCheckOnline></soap:Body></soap:Envelope>".getBytes();
		
//		String s= "<?xml version=\"1.0\" encoding=\"utf-8\"?>" + 
//                  "<soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
//                  "<soap:Body>" +
//                  "<qqCheckOnline xmlns=\"http://WebXml.com.cn/\">" +
//                  "<qqCode>string</qqCode>" +
//                  "</qqCheckOnline>" +
//                  "</soap:Body>" +
//                  "</soap:Envelope>";
//		byte[] m = s.getBytes();
		
		// 从外部文件获取  SOAP 请求报文
		
//		SOAPRequestAdapterPlugin.class.getClassLoader();
//		URL url = ClassLoader.getSystemResource("SoapClient.xml");
//		File f = new File(url.getPath().toString());
//		if ((f.isFile()) && (f.exists())){
//			System.out.println("--文件正确" );
//		}
//		
//		byte[] tes = null;
//		try {
//			 tes = getBytesFromFile(f);
//		} catch (Exception e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
//		
//		int count = tes.length;
//		System.out.println("count is:" + count);	//334
//		
//		try {
//			byte[] res = Send(tes);
//			System.out.println("res is:" + new String(res));
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
//	}
	
	
	
	/**
	 * 文件内部转换为byte数组
	 * 
	 * @param f
	 *            : 文件
	 * @throws Exception
	 *             1、IO异常(IOException)
	 */
//	public static byte[] getBytesFromFile(File f) throws Exception {
//		if (f == null) {
//			logger.error("getBytesFromFile:从二进制文件获取内容失败,文件为null");
//			return null;
//		}
//		ByteArrayOutputStream out = new ByteArrayOutputStream(1000);
//		try {
//			FileInputStream stream = new FileInputStream(f);
//			byte[] b = new byte[1000];
//			int n;
//			while ((n = stream.read(b)) != -1)
//				out.write(b, 0, n);
//			stream.close();
//			out.close();
//			logger.debug("getBytesFromFile:从二进制文件获取内容成功!");
//		} finally {
//			if (out != null) {
//				out.close();
//			}
//		}
//		return out.toByteArray();
//	}
	
}



