package com.dc.tes.adapterlib;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.dc.tes.adapter.IRequestAdapter;
import com.dc.tes.adapter.remote.DefaultRequestAdapterServerWorker;


/**
 * HTTP 服务端 适配器 Servlet
 * 
 * @author 王春佳
 * 
 */
public class HTTPReplyAdapterServlet extends HttpServlet {

	private static final long serialVersionUID = 4892712849593552092L;
	
	private static Log logger = LogFactory.getLog(HTTPReplyAdapterServlet.class);
	
	protected IRequestAdapter m_adapterPluginInstance = null;
	
	private Properties m_config_props = null;
	
	private static int m_RecordUserId = 0;
	
	public HTTPReplyAdapterServlet() {
		super();
	}

	public HTTPReplyAdapterServlet(Properties props, IRequestAdapter api) {
		super();
		m_config_props = props;
		m_adapterPluginInstance = api;
	}

	
	public void destroy() {
		super.destroy(); 
	}
	
	/**
	 * HTTP服务端适配器接收到被测系统请求的时间点
	 */
	public static long TimeOfAcceptRequest = 0;

	/**
	 * GET方式处理 1、从被测系统接受消息 2、将该消息发送给核心 3、接收核心的返回消息，转发给被测系统
	 * 
	 * @see 1、仅处理1个特定参数的情况
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		logger.info("doGet方式执行中...");
		TimeOfAcceptRequest = System.currentTimeMillis();
		logger.debug("接收到被测系统请求的时间点:" + TimeOfAcceptRequest);

		byte[] requestByte = null; // 从被测系统接收的原始报文
		byte[] responseByte = null; // 核心返回的原始报文


		requestByte = request.getQueryString().getBytes("utf-8");
		logger.info("从被测系统接收的原始字节流请求数据为:" + new String(requestByte));

		responseByte = HTTPReplyAdapterPlugin.sendMsg2Core(requestByte);
		
		if (responseByte != null){
			logger.info("核心返回的响应原始字节流数据为:" + new String(responseByte));
			response.getOutputStream().write(responseByte);
		}
		else{
			logger.info("出现错误,不向外部系统转发:" + responseByte);
		}
	}

	/**
	 * POST方式
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		logger.info("doPost方式执行中...");
		TimeOfAcceptRequest = System.currentTimeMillis();
		logger.debug("接收到被测系统请求的时间点:" + TimeOfAcceptRequest);

		InputStream inputStream = request.getInputStream();

		// 报文长度
		int length = request.getContentLength();

		byte[] requestByte = new byte[length]; // 从被测系统接收的请求报文
		byte[] responseByte = null; // 核心返回的响应报文
		
		inputStream.read(requestByte);
		logger.info("从被测系统接收的原始字节流请求数据为:" + new String(requestByte));
		System.out.println(new String(requestByte));
		
		byte[] pureRequestByte = requestByte;
		if (HTTPRequestAdapterPlugin.m_delprefix) {
			byte[] temp = new byte[requestByte.length - HTTPRequestAdapterPlugin.m_prefixlen];
			System.arraycopy(requestByte, HTTPRequestAdapterPlugin.m_prefixlen, temp, 0, temp.length);
			pureRequestByte = temp;
			System.out.println("去除头部后的请求:"+new String(pureRequestByte));
		}
		
		int iSystemId = 0;
		boolean isRecording = false;
		if (m_config_props!= null && m_config_props.containsKey("RECORDING")) {
			isRecording = Integer.parseInt((String) m_config_props.getProperty("RECORDING"))==1;
			if (m_config_props.containsKey("SYSTEMID")) {
				iSystemId = Integer.parseInt((String) m_config_props.get("SYSTEMID"));
			}
			if (((HTTPRequestAdapterPlugin)m_adapterPluginInstance).m_RECORDING && 
					((HTTPRequestAdapterPlugin)m_adapterPluginInstance).m_config == null) {
				((HTTPRequestAdapterPlugin)m_adapterPluginInstance).InitDbConnection(((HTTPRequestAdapterPlugin)m_adapterPluginInstance).m_SystemName);
				if (m_config_props.containsKey("RECORDUSER")) {
					String strRecordUser = (String) m_config_props.get("RECORDUSER");
					if (strRecordUser != null && !strRecordUser.isEmpty()) {
						m_RecordUserId = DbOp.getUserIdByUserName(strRecordUser);
					}
					if (m_RecordUserId <=0 ) {
						m_RecordUserId = DbOp.getAdminUserId();
					}
				}
			}
		}	
		
		if (!isRecording) {
			responseByte = HTTPReplyAdapterPlugin.sendMsg2Core(pureRequestByte);
			if (responseByte != null){
				logger.info("核心返回的响应原始字节流数据为:" + new String(responseByte));
				System.out.println("核心返回的响应原始字节流数据为:" + new String(responseByte));
				response.getOutputStream().write(responseByte);
			}
			else{
				logger.info("出现错误,不向外部系统转发:" + responseByte);
			}
		}
		else {
			DefaultRequestAdapterServerWorker requestAdapterSvrWork =
					new DefaultRequestAdapterServerWorker(isRecording, m_config_props, m_adapterPluginInstance);
			responseByte = requestAdapterSvrWork.sendMsg(requestByte);
			if (responseByte != null){
				logger.info("核心返回的响应原始字节流数据为:" + new String(responseByte));
				System.out.println("核心返回的响应原始字节流数据为:" + new String(responseByte));
				byte[] responseByte2 = responseByte;
				if (HTTPRequestAdapterPlugin.m_delprefix) {
					responseByte2 = new byte[responseByte.length + HTTPRequestAdapterPlugin.m_prefixlen];
					
					System.arraycopy(FixLength(String.valueOf(responseByte2.length - HTTPRequestAdapterPlugin.m_prefixlen), HTTPRequestAdapterPlugin.m_prefixlen, '0', true).getBytes(), 0, responseByte2, 0, HTTPRequestAdapterPlugin.m_prefixlen);
					
					System.arraycopy(responseByte, 0, responseByte2, HTTPRequestAdapterPlugin.m_prefixlen, responseByte.length);		
					System.out.println("加上头部后的待转发请求报文:"+new String(responseByte2));
				}
				response.getOutputStream().write(responseByte2);
			}
			else{
				logger.info("出现错误,不向外部系统转发:" + responseByte);
			}

			byte[] pureResponseByte = responseByte;
			if (pureRequestByte != null && pureResponseByte != null) {
				DbOp.InsertRecordedCase(iSystemId, m_RecordUserId, new String(pureRequestByte), new String(pureResponseByte));
			}
			else if (pureResponseByte == null) {
				DbOp.InsertRecordedCase(iSystemId, m_RecordUserId, new String(pureRequestByte), "");
			}
		}
	}
	
	/**
	 * 工具函数 产生固定长度的字符串。
	 * 如果src的长度比length参数大，返回原始src，否则将在前（或后）填补padding字符。
	 * @param src 源字符串
	 * @param length 期望的长度
	 * @param padding 填补的字符
	 * @param leadingPad =true在最前面填补, =false在最后面填补。
	 * @return 填补以后的字符串
	 */
	public static String FixLength(String src, int length, char padding, boolean leadingPad) {
		if (src == null) {
			src = "";
		}
		if (length <= src.length()) {
			return src;
		}
		StringBuffer sb = new StringBuffer(src);
		for (int i = src.length(), j = length; i < j; i++) {
			if (leadingPad) {
				sb.insert(0, padding);
			} else {
				sb.append(padding);
			}
		}
		return sb.toString();
	}

	public void init() throws ServletException {
		//super.init(); //added by hzx
	}

}
