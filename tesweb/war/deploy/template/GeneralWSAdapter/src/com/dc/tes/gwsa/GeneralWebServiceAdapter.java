package com.dc.tes.gwsa;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.DocumentException;

import com.dc.tes.adapterapi.Adapter2Tes;

/**
 * Web Service 适配器
 * 
 * @author 王春佳
 * 
 *         功能： 1、接受 SOAP XML 报文,选取有用的节点,将报文体转发给核心 2、接受核心返回的报文,添加报文头,返回给客户端
 * 
 *         限制： 1、一个XML节点只能有一个根节点,若选取的节点为2个同级节点,则不符合XML标准
 *         2、若核心返回的报文2个同级报文,适配器将响应报文插入 XML模版(soap头),无法确认插入顺序
 */

public class GeneralWebServiceAdapter extends HttpServlet {

	private static final long serialVersionUID = 1L;

	/**
	 * log4j 日志对象
	 */
	public static Log log = LogFactory.getLog(GeneralWebServiceAdapter.class);

	/**
	 * 构造函数
	 */
	public GeneralWebServiceAdapter() {
		super();
	}

	/**
	 * Servlet销毁时执行
	 */
	public void destroy() {
		super.destroy();
	}

	/**
	 * doGet方法，用于处理客户端 get 请求
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doPost(request, response);
	}

	/**
	 * doPost方法，用于处理客户端 post 请求
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// Enumeration<String> content = request.getParameterNames();
		// byte[] parseStr = null; //请求报文
		// byte[] resStr = null; //响应报文
		// while (content.hasMoreElements()) {
		// String name = content.nextElement();
		// String value = request.getParameter(name).trim();
		// try {
		// parseStr = SoapXmlUtil.genRequest(value.getBytes()); //
		//				
		// resStr = SoapXmlUtil.genResponse(parseStr); // 模拟从核心获取的数据
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
		// }
		// response.getOutputStream().write(resStr);

		Enumeration enu = request.getParameterNames();
		long inTime = System.currentTimeMillis(); // 接到请求的时间
		long outTime = -1; // 核心处理完成的时间
		long usedTime = 0; // 核心处理时间
		int delayTime = -1; // 系统返回的延时时间

		if (false == enu.hasMoreElements()) {
			log.error("0x0800：Post请求未带输入报文！");
			throw new ServletException("Post请求未带输入报文！");
		}

		String pName = (String) enu.nextElement(); // 获取第一个参数
		// byte[] reqMessage = request.getParameter(pName).getBytes();
		byte[] reqMessage;
		try {
			log
					.debug("原始请求报文:"
							+ new String(request.getParameter(pName).trim()
									.getBytes()));
			reqMessage = SoapXmlUtil.genRequest(request.getParameter(pName)
					.trim().getBytes());
			// reqMessage = request.getParameter(pName).trim().getBytes();
			log.debug("构建后,向核心发送的请求报文:" + new String(reqMessage));
		} catch (DocumentException e1) {
			e1.printStackTrace();
			throw new ServletException("构建请求报文出错！");
		}
		byte[] sendMessage = null;
		try {
			sendMessage = Adapter2Tes.messageInit();
			sendMessage = Adapter2Tes.addContent(sendMessage, "REQMESSAGE",
					reqMessage);
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new ServletException(e.getMessage());
		}

		byte[] backMessage = null;
		byte[] resMessage = null;
		try {
			backMessage = Adapter2Tes.sendContent(sendMessage);
		} catch (Exception e) {
			log.error("0x0800：与核心交互失败！[" + e.getMessage() + "]");
			throw new ServletException("0x0800：与核心交互失败！[" + e.getMessage()
					+ "]");
		}

		try {
			resMessage = Adapter2Tes.readContent(backMessage, "RESMESSAGE");
			if (resMessage.length == 0) {
				log.error("核心返回的响应报文为空!");
				throw new ServletException("核心返回的响应报文为空!");
			}
			delayTime = Integer.parseInt(new String(Adapter2Tes.readContent(
					backMessage, "DELAYTIME")));
			log.debug("核心返回的报文：[" + new String(resMessage) + "]");
			log.debug("核心返回的延时：[" + delayTime + "]");
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new ServletException(e.getMessage());
		}

		// 处理延时
		outTime = System.currentTimeMillis();
		usedTime = outTime - inTime;
		log.debug("核心的处理时间：[" + usedTime + "]");
		try {
			if (usedTime < delayTime) {
				log.debug("进行延时处理：[" + (delayTime - usedTime) + "]" + delayTime
						+ "-" + usedTime);
				Thread.sleep(delayTime - usedTime);
			}
		} catch (InterruptedException e) {
			log.error("0x0803：适配器延时处理失败！[" + e.getMessage() + "]");
			throw new ServletException("0x0803：适配器延时处理失败！[" + e.getMessage()
					+ "]");
		}

		// 进行请求响应
		// response.getOutputStream().write(resMessage);
		try {
			response.getOutputStream().write(
					SoapXmlUtil.genResponse(resMessage));
			log.debug("构建后的响应报文:"
					+ new String(SoapXmlUtil.genResponse(resMessage)));
		} catch (DocumentException e) {
			e.printStackTrace();
			throw new ServletException("构建响应报文失败");
		}
	}

	/**
	 * Servlet初始化,向核心注册
	 */
	public void init() throws ServletException {
		log.info("向核心注册开始...");
		byte[] regMessage = null;
		byte[] config = null;
		byte[] backMessage = null;
		try {
			regMessage = Adapter2Tes.messageInit();
			regMessage = Adapter2Tes.addContent(regMessage, "SIMTYPE", "S"
					.getBytes());
			backMessage = Adapter2Tes.reg2tes(regMessage);
		} catch (Exception e) {
			log.error("0x0D17：向核心注册失败！[" + e.getMessage() + "]");
			throw new ServletException("0x0D17：向核心注册失败！[" + e.getMessage()
					+ "]");
		}
		try {
			config = Adapter2Tes.readContent(backMessage, "CONFIGINFO");
		} catch (Exception e) {
			log.error("0x0D11：从核心返回报文中读取配置信息失败！[" + e.getMessage() + "]");
			throw new ServletException("0x0D11：从核心返回报文中读取配置信息失败！["
					+ e.getMessage() + "]");
		}
		InputStream inputStream = new ByteArrayInputStream(config);
		Properties p = new Properties();
		try {
			p.load(inputStream);
		} catch (IOException e) {
			log.error("0x0802：加载配置信息失败！[" + e.getMessage() + "]");
			throw new ServletException("0x0802：加载配置信息失败！[" + e.getMessage()
					+ "]");
		}
	}

}
