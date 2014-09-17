package com.dc.tes.gha;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.dc.tes.adapterapi.Adapter2Tes;
import java.util.Properties;

/**
 * 通用HTTP适配器
 * 
 * @author Conan
 * 
 * 
 * 修正内容： 
 *     1、接收参数改进 
 *        原有版本问题:原有版本只能接受GET或POST方式的第一个参数，来进行处理
 *        改进内容：
 *            1)如果用户请求没有参数，抛异常、记录日志
 *            2)如果用户请求有且仅有1个参数，则直接处理
 *            3)如果用户请求参数多余1个，则根据全局变更配置，处理全局变量配置的参数;
 *              若全局变更量配置的参数未找到，则默认读取第一个参数的内容处理
 *              
 * @modify 王春佳
 */

public class GeneralHttpAdapter extends HttpServlet {

	private static final long serialVersionUID = 1L;
	
	/**
	 * 报文的参数名称
	 * 例如 HTTP GET 方法：
	 *      http://127.0.0.1:8080/axisTest/HelloWord.jws?method=sayHello
	 *      其中的  parameter 为: method
	 */
	private String parameter = "";

	/**
	 * 日志对象
	 */
	public static Log log = LogFactory.getLog(GeneralHttpAdapter.class);

	public String encoding = null;

	/**
	 * 构造函数
	 */
	public GeneralHttpAdapter() {
		super();
	}

	/**
	 * 析构函数
	 */
	public void destroy() {
		super.destroy();
	}

	/**
	 * 处理HTTP GET请求的方法
	 * 
	 * 目前该方法虽然能顺利接收多参数的GET请求， 但是，只读取第一个参数发送给核心进行处理
	 * 
	 * @param request
	 *            客户端发送的请求
	 * @param response
	 *            返回给客户端的结果
	 * @throws ServletException
	 * @throws IOException
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		long inTime = System.currentTimeMillis(); // 接到请求的时间
		long outTime = -1; // 核心处理完成的时间
		long usedTime = 0; // 核心处理时间
		int delayTime = -1; // 系统返回的延时时间

		//*******************************变更 开始****************************
		
		// 1)如果用户请求没有参数，抛异常、记录日志
		Enumeration enu = request.getParameterNames();
		if (false == enu.hasMoreElements()) {
			log.error("0x0800：GET请求未带输入报文！");
			throw new ServletException("0x0800：GET请求未带输入报文！");
		}
		
		// 2)如果用户请求有且仅有1个参数，则直接处理
		String pName = "";
		int count = this.getEnuNumber(enu);
		if (count ==1){
			enu = request.getParameterNames();
			pName = (String) enu.nextElement();
		}else if(count > 1){
			// 3)如果用户请求参数多余1个，则根据全局变更配置，处理全局变量配置的参数;
			//             若全局变更量配置的参数未找到，则默认读取第一个参数的内容处理
			enu = request.getParameterNames();
			if ( this.judgeEnuContent(enu, parameter)){
				pName = parameter;
			}else{	// 未找到  全局变量配置参数,直接读取第一个参数
				enu = request.getParameterNames();
				pName = (String) enu.nextElement();
			}
		}
		
		if ("".equals(pName)){
			log.error("请求参数异常");
			throw new ServletException("请求参数异常");
		}
		log.info("请求参数为:" + pName);
		//*******************************变更 结束****************************		

		byte[] reqMessage = request.getParameter(pName).getBytes();
		

		byte[] resMessage = null;
		try {
			resMessage = new Adapter2Tes().SendToCore(reqMessage);
		} catch (Exception e) {
			log.error("0x0800：与核心交互失败！[" + e.getMessage() + "]");
			throw new ServletException("0x0800：与核心交互失败！[" + e.getMessage()
					+ "]");
		}

		delayTime = 0; //暂时不处理延时

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
		response.getOutputStream().write(resMessage);
	}

	/**
	 * 处理HTTP POST请求的方法 目前其实不做啥，就是单纯的调用doGet方法
	 * 
	 * @param request
	 *            客户端发送的请求
	 * @param response
	 *            返回给客户端的结果
	 * @throws ServletException
	 * @throws IOException
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		this.doGet(request, response);
	}

	/**
	 * 初始化方法： 完成向核心的注册
	 * 
	 * @throws ServletException
	 *             如果注册失败
	 */
	public void init() throws ServletException {
		
		byte[] config = null;
		try {
			config = new Adapter2Tes().Reg2TES();
		} catch (Exception e) {
			log.error("0x0D17：向核心注册失败！[" + e.getMessage() + "]");
			throw new ServletException("0x0D17：向核心注册失败！[" + e.getMessage()
					+ "]");
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
	
	/**
	 * 获取 枚举元素个数
	 * @param enu  枚举
	 * @return 枚举元素个数
	 */
	private int getEnuNumber(Enumeration enu){
		int count = 0;
		
		// enu 对象没有 元素
		if (enu.hasMoreElements() == false)
			return count;
		
		// 循环获取 enu 的元素个数
		while(enu.hasMoreElements()){
			enu.nextElement();
			count ++;
		}
		
		return count;
	}
	
	/**
	 * 判断 设定的参数名称在 枚举 列表中是否存在
	 * @param enu 枚举
	 * @param value 待判断的元素值
	 * @return false——不存在;true——成功
	 */
	private boolean judgeEnuContent(Enumeration enu, String value){
		boolean result = false;
		
		while(enu.hasMoreElements()){
			String tempStr = (String) enu.nextElement();
			if (tempStr.equals(value)){
				result = true;
				break;
			}else{
				continue;
			}
		}
		
		return result;
	}
	
}



