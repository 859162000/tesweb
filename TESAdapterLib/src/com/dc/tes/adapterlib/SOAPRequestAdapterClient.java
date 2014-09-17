package com.dc.tes.adapterlib;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * SOAP 客户端适配器,发送 webservice SOAP请求
 * 
 * 分为三种请求方式：SOAP 请求\HTTP GET\HTTP POST三种方式
 * 
 * @author 王春佳
 * @see http://www.webxml.com.cn/webservices/qqOnlineWebService.asmx
 */
public class SOAPRequestAdapterClient {

	private static Log logger = LogFactory
			.getLog(SOAPRequestAdapterClient.class);

	/*------------------------------------------------
	 SOAP 1.1 请求报文如下：
		POST /webservices/qqOnlineWebService.asmx HTTP/1.1
		Host: www.webxml.com.cn
		Content-Type: text/xml; charset=utf-8
		Content-Length: length
		SOAPAction: "http://WebXml.com.cn/qqCheckOnline"
	
		<?xml version="1.0" encoding="utf-8"?>
		<soap:Envelope xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
		<soap:Body>
		<qqCheckOnline xmlns="http://WebXml.com.cn/">
		  <qqCode>string</qqCode>
		</qqCheckOnline>
		</soap:Body>
		</soap:Envelope>
	 
	 SOAP 1.2 请求报文如下：
		POST /webservices/qqOnlineWebService.asmx HTTP/1.1
		Host: www.webxml.com.cn
		Content-Type: application/soap+xml; charset=utf-8
		Content-Length: length
	
		<?xml version="1.0" encoding="utf-8"?>
		<soap12:Envelope xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:soap12="http://www.w3.org/2003/05/soap-envelope">
		<soap12:Body>
		<qqCheckOnline xmlns="http://WebXml.com.cn/">
		  <qqCode>string</qqCode>
		</qqCheckOnline>
		</soap12:Body>
		</soap12:Envelope>
	 -------------------------------------------------*/
	/**
	 * SOAP 方式 发送
	 * 
	 * @param msg
	 *            ：核心发送的请求报文
	 * @return 被测系统返回的报文
	 * 
	 * @see 如果 SOAPAction 为空 ，则不发送 SOAPAction
	 */
	public static byte[] sendBySOAP(byte[] msg) {

		logger.info("SOAP 客户端 适配器 sendBySOAP 方法执行...");
		logger.info("核心发送的请求报文数据为:" + new String(msg));
		
		
		// 被测系统返回的 应答报文 (被测系统==>该适配器)
		byte[] responseByte = null;
		
		StringBuffer urlStr = new StringBuffer();

		urlStr.append(SOAPRequestAdapterPlugin.host);
		urlStr.append(SOAPRequestAdapterPlugin.url);
		
		// 建立连接
		URL url;
		try {
			url = new URL(urlStr.toString());
			HttpURLConnection httpUrlConnection = (HttpURLConnection) url
			.openConnection();
			
			// 设置连接属性
			httpUrlConnection.setDoOutput(true);
			httpUrlConnection.setDoInput(true);
			httpUrlConnection.setUseCaches(false);
			httpUrlConnection.setRequestMethod("POST");
			
			//设置连接属性等信息
			httpUrlConnection.setRequestProperty("Content-length", String.valueOf(msg.length));
			httpUrlConnection.setRequestProperty("Content-Type", SOAPRequestAdapterPlugin.contentType);
			
			// 如果 SOAPAction 不为空,则 发送 SOAP 头
			if (!"".equals(SOAPRequestAdapterPlugin.SOAPAction)){
				httpUrlConnection.setRequestProperty("SOAPAction", SOAPRequestAdapterPlugin.SOAPAction);
			}
			
			// 设置报文输出
			OutputStream outputStream = httpUrlConnection.getOutputStream();
			outputStream.write(msg);
			outputStream.close();
			
			// 获得响应状态
			int responseCode = httpUrlConnection.getResponseCode();

			// 判断响应结果
			if (responseCode == HttpURLConnection.HTTP_OK) {

//				BufferedReader bufferReader = new BufferedReader(
//						new InputStreamReader(httpUrlConnection
//								.getInputStream()));
				
//				StringBuffer responseBuffer = new StringBuffer();// 响应数据结果
//				String readLine = ""; // 临时数据结果
//				while ((readLine = bufferReader.readLine()) != null) {
//					responseBuffer.append(readLine).append("\n");
//				}
//				bufferReader.close();
				
//				logger.info("被测系统返回的原始数据:" + responseBuffer.toString());
//				responseByte = responseBuffer.toString().getBytes();
//				logger.info("转发给核心的字节流:" + new String(responseByte));
				
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
		
		try {
			System.out.println(new String(responseByte, "GB2312")); //DEBUG
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		
		logger.info("转发给核心的字节流:" + new String(responseByte));
		return responseByte;

	}

	/*-------------------------------------------
	请求报文格式如下:
		GET /webservices/qqOnlineWebService.asmx/qqCheckOnline?qqCode=string HTTP/1.1
		Host: www.webxml.com.cn
	 --------------------------------------------*/
	/**
	 * GET 方式 发送
	 * 
	 * @param msg
	 *            ：核心发送的请求报文
	 * @return 被测系统返回的报文
	 */
	public static byte[] sendByGET(byte[] msg) {

		logger.info("SOAP 客户端 适配器 sendByGET 方法执行...");
		logger.info("核心发送的请求报文数据为:" + new String(msg));

		// 被测系统返回的 应答报文 (被测系统==>该适配器)
		byte[] responseByte = null;

		StringBuffer urlStr = new StringBuffer();

		urlStr.append(SOAPRequestAdapterPlugin.host);
		urlStr.append(SOAPRequestAdapterPlugin.url);

		urlStr.append('?');
		urlStr.append(new String(msg));

		try {
			// 建立连接
			URL url = new URL(urlStr.toString());
			HttpURLConnection httpUrlConnection = (HttpURLConnection) url
					.openConnection();
			
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
//				logger.info("转发给核心的字节流:" + new String(responseByte));
				
				InputStream in = httpUrlConnection.getInputStream();
				int count = httpUrlConnection.getContentLength();
				
				responseByte = new byte[count];
				in.read(responseByte);

				logger.info("被测系统返回的原始数据:" + new String(responseByte));
				
			}else{
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

	/*---------------------------------------------
	请求报文格式如下:
		POST /webservices/qqOnlineWebService.asmx/qqCheckOnline HTTP/1.1
		Host: www.webxml.com.cn
		Content-Type: application/x-www-form-urlencoded
		Content-Length: length
	
		qqCode=string
	----------------------------------------------*/
	/**
	 * POST 方式发送
	 * 
	 * @param msg
	 *            ：核心发送的请求报文
	 * @return 被测系统返回的报文
	 */
	public static byte[] sendByPOST(byte[] msg) {

		logger.info("SOAP 客户端 适配器 sendByPOST 方法执行...");
		logger.info("核心发送的请求报文数据为:" + new String(msg));

		// 被测系统返回的 应答报文 (被测系统==>该适配器)
		byte[] responseByte = null;
		
		StringBuffer urlStr = new StringBuffer();

		urlStr.append(SOAPRequestAdapterPlugin.host);
		urlStr.append(SOAPRequestAdapterPlugin.url);
		
		// 建立连接
		URL url;
		try {
			url = new URL(urlStr.toString());
			HttpURLConnection httpUrlConnection = (HttpURLConnection) url
			.openConnection();
			
			// 设置连接属性
			httpUrlConnection.setDoOutput(true);
			httpUrlConnection.setDoInput(true);
			httpUrlConnection.setUseCaches(false);
			httpUrlConnection.setRequestMethod("POST");
			
			//设置连接属性等信息
			httpUrlConnection.setRequestProperty("Content-length", String.valueOf(msg.length));
			httpUrlConnection.setRequestProperty("Content-Type", SOAPRequestAdapterPlugin.contentType);
			
			// 设置报文输出
			OutputStream outputStream = httpUrlConnection.getOutputStream();
			outputStream.write(msg);
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
//				logger.info("转发给核心的字节流:" + new String(responseByte));
				
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

}


