package com.dc.tes.adapterlib;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * HTTP 客户端 适配器 doGet\doPost 请求
 * 
 * @author 王春佳
 * 
 */
public class HTTPRequestAdapterClient {

	private static Log logger = LogFactory.getLog(HTTPRequestAdapterClient.class);

	/**
	 * GET 方式向 被测系统发送 报文
	 * 
	 * @param msg
	 *            ：核心发送的请求报文
	 * @return 被测系统返回的报文(转发给核心)
	 */
	public static byte[] doGet(byte[] msg) {

		logger.info("HTTP 客户端 适配器 doGet 方法执行...");
		logger.info("核心发送的请求报文数据为:" + new String(msg));

		// 被测系统返回的 应答报文 (被测系统==>该适配器)
		byte[] responseByte = null;

		/*
		 * #targetUrl 例如: http://127.0.0.1:10000 #targetServlet
		 * 例如：/web/tes/httpadapter #GET方式 传递数据， url与 数据之间有 ? 符号 #核心发送的数据格式是
		 * 名/值对方式，例如 name=wang&value=123
		 */
		StringBuffer urlStr = new StringBuffer();

		urlStr.append(HTTPRequestAdapterPlugin.targetUrl);
		urlStr.append(HTTPRequestAdapterPlugin.targetServlet);

		urlStr.append('?');
		urlStr.append(new String(msg)); // 将核心发送的请求信息加入URL地址中

		URL url;
		try {
			// 建立连接
			url = new URL(urlStr.toString());
			HttpURLConnection httpUrlConnection = (HttpURLConnection) url.openConnection();

			// 设置连接属性
			httpUrlConnection.setDoInput(true);
			httpUrlConnection.setUseCaches(false);
			httpUrlConnection.setRequestMethod("GET");

			// 获得响应状态
			int responseCode = httpUrlConnection.getResponseCode();

			// 判断响应结果
			if (responseCode == HttpURLConnection.HTTP_OK) {

//				BufferedReader bufferReader = new BufferedReader(
//						new InputStreamReader(httpUrlConnection
//								.getInputStream()));
//
//				StringBuffer responseBuffer = new StringBuffer();// 响应数据结果
//				String readLine = ""; // 临时数据结果
//				while ((readLine = bufferReader.readLine()) != null) {
//					responseBuffer.append(readLine).append("\n");
//				}
//				bufferReader.close();
//
//				logger.info("被测系统返回的原始数据:" + responseBuffer.toString());
//				responseByte = responseBuffer.toString().getBytes();
//				logger.info("被测系统返回的原始字节流:" + new String(responseByte));
				
				InputStream in = httpUrlConnection.getInputStream();
				int count = httpUrlConnection.getContentLength();
				
				responseByte = new byte[count];
				in.read(responseByte);

				logger.info("被测系统返回的原始数据:" + new String(responseByte));

			} else {
				logger.error("获得被测系统响应状态失败:" + responseCode);
				logger.error("失败信息:" + httpUrlConnection.getResponseMessage());
				return null;
			}

		} catch (MalformedURLException e) {
			logger.error("URL字符串非法:" + urlStr);
			logger.error(e.getLocalizedMessage());
			return null;
		} catch (IOException e) {
			logger.error("创建连接失败");
			logger.error(e.getLocalizedMessage());
			return null;
		}

		logger.info("转发给核心的字节流:" + new String(responseByte));
		return responseByte;
	}

	/**
	 * POST 方式向 被测系统发送 报文
	 * 
	 * @param msg
	 *            : 核心发送的请求报文
	 * @return 被测系统返回的报文
	 */
	public static byte[] doPost(byte[] msg) {

		logger.info("HTTP 客户端 适配器 doPost 方法执行...");
		logger.info("核心发送的请求报文数据为:" + new String(msg));
		
		// 被测系统返回的 应答报文 (被测系统==>该适配器)
		byte[] responseByte = null;

		/*
		 * #targetUrl 例如: http://127.0.0.1:10000 #targetServlet
		 * 例如：/web/tes/httpadapter #POST 方式传递的数据放入 POST消息体中,任何报文格式都可以
		 */
		StringBuffer urlStr = new StringBuffer();

		urlStr.append(HTTPRequestAdapterPlugin.targetUrl);
		urlStr.append(HTTPRequestAdapterPlugin.targetServlet);

		URL url;
		try {
			// 建立连接
			url = new URL(urlStr.toString());
			HttpURLConnection httpUrlConnection = (HttpURLConnection) url.openConnection();

			// 设置连接属性
			httpUrlConnection.setDoOutput(true);
			httpUrlConnection.setDoInput(true);
			httpUrlConnection.setUseCaches(false);
			httpUrlConnection.setRequestMethod("POST");

			// 处理核心发送的请求报文
			byte[] requestByte = msg; // 此处可进行编码转换

			// 设置连接属性等信息
			httpUrlConnection.setRequestProperty("Content-length", String.valueOf(requestByte.length));

			// 设置报文输出
			OutputStream outputStream = httpUrlConnection.getOutputStream();
			outputStream.write(requestByte);
			outputStream.close();

			// 获得响应状态
			int responseCode = httpUrlConnection.getResponseCode();

			// 判断响应结果
			if (responseCode == HttpURLConnection.HTTP_OK) {

//				BufferedReader bufferReader = new BufferedReader(
//						new InputStreamReader(httpUrlConnection
//								.getInputStream()));
//
//				StringBuffer responseBuffer = new StringBuffer();// 响应数据结果
//				String readLine = ""; // 临时数据结果
//				while ((readLine = bufferReader.readLine()) != null) {
//					responseBuffer.append(readLine).append("\n");
//				}
//				bufferReader.close();
//
//				logger.info("被测系统返回的原始数据:" + responseBuffer.toString());
//				responseByte = responseBuffer.toString().getBytes();
//				logger.info("被测系统返回的原始字节流:" + new String(responseByte));
				
				InputStream in = httpUrlConnection.getInputStream();
				int count = httpUrlConnection.getContentLength();
				
				responseByte = new byte[count];
				in.read(responseByte);

				logger.info("被测系统返回的原始数据:" + new String(responseByte));
			} else {
				logger.error("获得被测系统响应状态失败:" + responseCode);
				logger.error("失败信息:" + httpUrlConnection.getResponseMessage());
				System.out.println("获得被测系统响应状态失败:" + responseCode);
				System.out.println("失败信息:" + httpUrlConnection.getResponseMessage());
				return null;
			}
		} catch (MalformedURLException e) {
			logger.error("URL字符串非法:" + urlStr);
			logger.error(e.getLocalizedMessage());
			System.out.println("URL字符串非法:" + urlStr);
			return null;
		} catch (IOException e) {
			logger.error("创建连接失败");
			logger.error(e.getLocalizedMessage());
			System.out.println("创建连接失败：" + e.getMessage());
			return null;
		}

		logger.info("转发给核心的字节流:" + new String(responseByte));
		return responseByte;
	}

}
