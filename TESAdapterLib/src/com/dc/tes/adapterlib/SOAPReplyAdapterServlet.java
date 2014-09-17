package com.dc.tes.adapterlib;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;



/**
 * SOAP 服务端 适配器 Servlet
 * 
 * @author 王春佳
 * @see 接收 SOAP 请求，采用 doPost方式
 */
public class SOAPReplyAdapterServlet extends HttpServlet {

	private static final long serialVersionUID = -578102044826720914L;
	private static Log logger = LogFactory
			.getLog(SOAPReplyAdapterServlet.class);

	public SOAPReplyAdapterServlet() {
		super();
	}

	public void destroy() {
		super.destroy();
	}
	
	/**
	 * SOAP服务端适配器接收到被测系统请求的时间点
	 */
	protected static long TimeOfAcceptRequest = 0;

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		logger.error("接收SOAP客户端请求，跳转到doGet方法,应使用doPost方式接收");
		throw new ServletException("接收SOAP客户端请求，跳转到doGet方法,应使用doPost方式接收");
	}

	// 接收SOAP请求报文,转发给核心，将核心的响应报文 转发给 被测系统
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		logger.info("doPost方式执行中...");
		TimeOfAcceptRequest = System.currentTimeMillis();
		logger.debug("接收到被测系统请求的时间点为:" + TimeOfAcceptRequest);

		InputStream inputStream = request.getInputStream();

		// 报文长度
		int length = request.getContentLength();

		byte[] requestByte = new byte[length]; // 从被测系统接收的原始报文
		byte[] responseByte = null; // 核心返回的原始报文


		inputStream.read(requestByte);
		logger.info("原始字节流请求数据为:" + new String(requestByte));
		System.out.println(new String(requestByte, "GB2312"));

		responseByte = SOAPReplyAdapterPlugin.sendMsg2Core(requestByte);
		
		logger.info("核心返回的响应原始字节流数据为:" + new String(responseByte));
		
		response.setContentType("text/xml");
		response.getOutputStream().write(responseByte);
		
	}


	public void init() throws ServletException {

	}

}
