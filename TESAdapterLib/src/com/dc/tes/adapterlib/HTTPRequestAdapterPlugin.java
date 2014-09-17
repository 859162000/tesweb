package com.dc.tes.adapterlib;

import java.io.UnsupportedEncodingException;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.dc.tes.Config;
import com.dc.tes.adapter.IRequestAdapter;
import com.dc.tes.adapter.context.IAdapterEnvContext;
import com.dc.tes.adapter.context.IRequestAdapterEnvContext;
import com.dc.tes.adapter.secure.AbstractFactory;
import com.dc.tes.adapter.secure.IDecryptAdapterSecure;
import com.dc.tes.adapter.secure.IEncryptAdapterSecure;
import com.dc.tes.data.IRuntimeDAL;
import com.dc.tes.data.RuntimeDAL;


/*-------------------------------------------
 该适配器注册，核心返回的信息如下:
 # targetUrl 目标系统URL地址 例如： http://127.0.0.1:10000
 targetUrl = 	http://127.0.0.1:10000

 # targetServlet 目标系统servlet地址 例如：/web/tes/httpadapter
 targetServlet = /web/tes/httpadapter

 # method 请求方式 目前只提供：GET 、 POST
 method = GET

 # 是否调用安全服务处理接收报文(外围系统==>适配器),解密操作,0否 1是
 dynamic_in = 0

 # 是否调用安全服务处理返回报文(适配器==>外围系统),加密操作,0否 1是
 dynamic_out = 1

 # 处理接受报文的插件类名 HttpReplyFactory\SoapReplyFactory\TcpReplyFactory\HttpRequestFactory\SoapRequestFactory\TcpRequestFactory 中的一种
 dynamic_name = HttpRequestFactory
 --------------------------------------------*/
/**
 * HTTP 客户端 适配器 (核心==>该适配器==>被测系统)
 * 
 * @author 王春佳
 * 
 * 
 * @see 1、超时问题,被测系统没有返回结果，尚未处理 2、发送到被测系统的报文尚未 进行 加码(Encoder)
 */
public class HTTPRequestAdapterPlugin implements IRequestAdapter {

	private static Log logger = LogFactory.getLog(HTTPRequestAdapterPlugin.class);

	private IRequestAdapterEnvContext m_TESEnv = null;

	/** 核心基础配置 */
	public static Config m_config;

	/** 运行时数据访问接口 */
	public static IRuntimeDAL da;

	// targetUrl 目标系统URL地址 例如： http://127.0.0.1:10000
	protected static String targetUrl = "";

	// targetServlet 目标系统servlet地址 例如：/web/tes/httpadapter
	protected static String targetServlet = "";

	// 请求方式 只支持：GET、POST方式
	private String method = "";

	// 支持的请求方式：GET、POST
	private static final String methodGet = "GET";
	private static final String methodPost = "POST";

	private static String m_ENCODING = "utf-8";
	
	//处于处于录制状态
	public static boolean m_RECORDING = false;
	public static int m_SystemId = 0;
	public static String m_SystemName = "";
	public static int m_NewRecordedCaseId = 0;
	public static int m_RecordUserId = 0;
	
	
	// 是否调用安全服务处理接收报文,解密操作,0否 1是
	private static int dynamic_in = -1;

	// 是否调用安全服务处理返回报文,加密操作,0否 1是
	private static int dynamic_out = -1;

	// 处理加解密报文的插件类名
	// HttpReplyFactory\SoapReplyFactory\TcpReplyFactory\HttpRequestFactory\SoapRequestFactory\TcpRequestFactory
	// 中的一种
	private static String dynamic_name = "";
	
	//是否去掉报文头
	public static boolean m_delprefix = false;
	//报文头长度
	public static int m_prefixlen = 0;

	private final static String secureFactoryPackage = "com.dc.tes.adapter.secure.factory.";

	private static Properties m_config_props = null;
	
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

		// ---------------------处理 核心发送的请求报文数据 开始...
		if (HTTPRequestAdapterPlugin.dynamic_out == 0) {// 不加密
			logger.info("不加密:核心发送的请求报文数据为:" + new String(msg));
		} else if (HTTPRequestAdapterPlugin.dynamic_out == 1) {// 加密
			IEncryptAdapterSecure iEncrypt = AbstractFactory.getInstance(
					HTTPRequestAdapterPlugin.dynamic_name).getEncryptAdapterSecure();
			msg = iEncrypt.enCrypt(msg);
			if (msg == null) {
				logger.error("加密失败");
				return null;
			}
			logger.info("加密:核心发送的请求报文数据为:" + new String(msg));
		} else {
			logger.error("安全服务处理返回报文,配置开关出错" + HTTPRequestAdapterPlugin.dynamic_out);
			return null;
		}
		// ---------------------处理 核心发送的请求报文数据 开始...

		if (methodGet.equals(this.method)) {// GET 方式 请求
			responseByte = HTTPRequestAdapterClient.doGet(msg);
		} else if (methodPost.equals(this.method)) { // POST方式请求
			responseByte = HTTPRequestAdapterClient.doPost(msg);
		} else { // 无法识别的方式，此分支不应出现,在Init已经屏蔽了错误的类型
			logger.fatal("请求方式无效:" + this.method);
		}
		
		if (responseByte == null) {
			return null;
		}

		// -------------------处理 从被测系统接收的原始报文 开始...
		if (HTTPRequestAdapterPlugin.dynamic_in == 0) {// 不解密
			logger.info("不解密:被测系统返回的原始数据,转发给核心:" + new String(responseByte));	
		} else if (HTTPRequestAdapterPlugin.dynamic_in == 1) {// 解密
			IDecryptAdapterSecure iDecrypt = AbstractFactory.getInstance(
					HTTPRequestAdapterPlugin.dynamic_name).getDecryptAdapterSecure();
			responseByte = iDecrypt.deCrypt(responseByte);
			logger.info("解密:解密后的原始数据,转发给核心:" + new String(responseByte));
		} else {
			logger.error("安全服务处理返回报文,配置开关出错" + HTTPRequestAdapterPlugin.dynamic_in);
			return null;
		}
		
		if(m_delprefix) {
			byte[] temp = new byte[responseByte.length-m_prefixlen];
			System.arraycopy(responseByte, m_prefixlen, temp, 0, temp.length);
			responseByte = temp;
			logger.info("去除头部后的正文:"+new String(responseByte));
		}
		String a = new String(responseByte,"GBK");
		logger.info("编码后:"+a);
		responseByte = a.getBytes("GBK");
		
		// -------------------处理 从被测系统接收的原始报文 结束...
		return responseByte;
	}

	/**
	 * 初始化环境数据
	 * 
	 * @return 获取核心返回注册信息任何一项失败，都初始化失败
	 */
	public boolean Init(IAdapterEnvContext tesENV) {
		logger.info("HTTP客户端适配器插件" + this.getClass().getName() + "被初始化.");
		
		this.m_TESEnv = (IRequestAdapterEnvContext) tesENV;
		// this.m_adpHelper = this.m_TESEnv.getHelper();

		Properties props = ConfigHelper.getConfig(this.m_TESEnv.getEvnContext());
		m_config_props = props;

		// 校验必要的初始化信息 是否存在
		String[] keys = new String[] { "targetUrl", "targetServlet", "method", "dynamic_in", "dynamic_out", "dynamic_name" };
		if (!ConfigHelper.chkProperKey(props, keys)) {
			return false;
		}

		this.method = (String) props.get("method");
		targetServlet = (String) props.get("targetServlet");
		targetUrl = (String) props.get("targetUrl");

		if (("".equals(this.method)) || (this.method == null)) {
			logger.error("请求方式非法:" + this.method);
			logger.error("HTTP客户端适配器插件" + this.getClass().getName() + "初始化失败.");
			return false;
		} else {
			if (!methodGet.equals(this.method) && !methodPost.equals(this.method)) {
				logger.error("请求方式不在处理范围内,目前只支持GET、POST方式:" + this.method);
				logger.error("HTTP客户端适配器插件" + this.getClass().getName() + "初始化失败.");
				return false;
			}
		}
		logger.info("请求方式为:" + this.method);

		if (("".equals(targetServlet)) || (targetServlet == null)) {
			logger.error("目标系统servlet地址非法:" + targetServlet);
			logger.error("HTTP客户端适配器插件" + this.getClass().getName() + "初始化失败.");
			return false;
		}
		logger.info("目标系统servlet地址为:" + targetServlet);

		if (("".equals(targetUrl)) || (targetUrl == null)) {
			logger.error("目标系统 URL非法:" + targetUrl);
			logger.error("HTTP客户端适配器插件" + this.getClass().getName() + "初始化失败.");
			return false;
		}
		logger.info("目标系统 URL为:" + targetUrl);

		if (props.containsKey("ENCODING")) {
			m_ENCODING = (String) props.get("ENCODING");
		}
		if(props.containsKey("RECORDING")) {
			m_RECORDING = Integer.parseInt((String) props.getProperty("RECORDING"))==1;
		}	
		
		if(props.containsKey("SYSTEMID")) {
			m_SystemId = Integer.parseInt((String) props.get("SYSTEMID"));
		}
		if(props.containsKey("SYSTEMNAME")) {
			m_SystemName = (String) props.get("SYSTEMNAME");		
			try {
				m_SystemName = new String(m_SystemName.getBytes("ISO-8859-1"), "utf-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} 
		}
		
		dynamic_in = Integer.parseInt(String.valueOf(props.getProperty("dynamic_in")));
		logger.info("安全服务处理接收报文,解密操作,0否 1是;值为:" + dynamic_in);

		dynamic_out = Integer.parseInt(String.valueOf(props.getProperty("dynamic_out")));
		logger.info("安全服务处理返回报文,加密操作,0否 1是;值为:" + dynamic_out);

		dynamic_name = secureFactoryPackage + props.getProperty("dynamic_name");
		logger.info("处理加解密报文的插件类名为:" + dynamic_name);

		if(props.containsKey("DELPREFIX")) {
			m_delprefix = Boolean.parseBoolean((String)props.getProperty("DELPREFIX"));
		}
		
		if(props.containsKey("PREFIXLEN")) {
			m_prefixlen = Integer.parseInt((String)props.getProperty("PREFIXLEN"));
		}
		
		logger.info("HTTP客户端适配器插件" + this.getClass().getName() + "初始化完成.");
		
		return true;
	}

	// 测试程序入口， 模拟 核心 发送数据 到 适配器
	public static void main(String args[]) {
		// doGet 客户端发起测试
		// byte[] requestByte = "name=2123&val=nihao".getBytes(); //模拟发送的数据
		//		
		// //手动设置 上下文
		// HTTPRequestAdapterPlugin ab = new HTTPRequestAdapterPlugin();
		// ab.method="GET";
		// ab.targetUrl="http://127.0.0.1:9999";
		// ab.targetServlet="/servlet/index";
		//		
		// try {
		// byte[] msg = ab.Send(requestByte);
		// System.out.println("msg is:" + new String(msg));
		// } catch (Exception e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }

		// doPost 客户端发起测试
		// byte[] requestByte = "nihaofdfdxml".getBytes(); //模拟发送的数据
		//		
		// //手动设置 上下文
		// HTTPRequestAdapterPlugin ab = new HTTPRequestAdapterPlugin();
		// ab.method="POST";
		// ab.targetUrl="http://127.0.0.1:9999";
		// ab.targetServlet="/servlet/index";
		//		
		// try {
		// byte[] msg = ab.Send(requestByte);
		// System.out.println("msg is:" + new String(msg));
		// } catch (Exception e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }

	}

	public String AdapterType() {
		// TODO Auto-generated method stub
		return "http.c";
	}

	public void InitDbConnection(String instanceName) {

		// 初始化核心基础配置
		logger.info("初始化基础配置...");
		m_config = new Config();
		logger.info("初始化基础配置成功.");
	
		// 初始化数据访问层
		logger.info("初始化数据访问层...");
		try {
			da = createRuntimeDAL(instanceName);
		} catch (Exception e) {
			e.printStackTrace();
		}
		logger.info("初始化数据访问层成功.");
		System.out.println();
	}
	

	protected static IRuntimeDAL createRuntimeDAL(String instanceName) throws Exception {
		
		return new RuntimeDAL(instanceName, m_config);
	}
	
}

