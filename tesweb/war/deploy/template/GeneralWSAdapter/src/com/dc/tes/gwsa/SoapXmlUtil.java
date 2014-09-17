package com.dc.tes.gwsa;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Namespace;
import org.dom4j.QName;
import org.dom4j.io.SAXReader;

public class SoapXmlUtil {

	// SOAP XML 中需要截取的 节点  根据需要配置
	private static final String usefulNodeName = "//soapenv:Body";//"//SOAP-ENV:Body";

	// SOAP XML NAMESPACE
	private static final String SOAP_ENV_Name = "SOAP-ENV";
	private static final String SOAP_ENV_Value = "http://schemas.xmlsoap.org/soap/envelope/";

	private static final String SOAP_ENC_Name = "SOAP-ENC";
	private static final String SOAP_ENC_Value = "http://schemas.xmlsoap.org/soap/encoding/";

	private static final String XMI_Name = "xsi";
	private static final String XMI_Value = "http://www.w3.org/2001/XMLSchema-instance";

	private static final String XSD_Name = "xsd";
	private static final String XSD_Value = "http://www.w3.org/2001/XMLSchema";

	/**
	 * 生成 请求报文,用来发送给核心
	 * 
	 * @param reqXml
	 *            : 原始请求报文
	 * @return : 构造后的请求报文
	 * @throws DocumentException
	 *             : DOM 异常
	 */
	public static byte[] genRequest(byte[] reqXml) throws DocumentException {
		Document doc = getDocFormStr(new String(reqXml));
		Element usefulE = getUsefulNode(doc, usefulNodeName);
		return SoapXmlUtil.createRequestStr(usefulE).getBytes();
	}

	/**
	 * 构造 请求报文字符串
	 * 
	 * @param usefulE
	 *            : 目标节点
	 * @return 请求报文字符串
	 * 
	 */
	private static String createRequestStr(Element usefulE) {
		Document doc = DocumentHelper.createDocument();
		doc.setRootElement((Element) usefulE.clone()); // 克隆目标Element
		return doc.asXML();
	}

	/**
	 * 根据节点名称,获取该节点
	 * 
	 * @param doc
	 *            : DOM结构
	 * @param nodeName
	 *            ：节点名称
	 * @return 目标节点
	 */
	private static Element getUsefulNode(Document doc, String nodeName) {
		Element usefulE = (Element) doc.selectSingleNode(nodeName);
		return usefulE;
	}

	/**
	 * 从外部XML文件获取 Document
	 * 
	 * @param filePath
	 *            :外部文件路径+名称
	 * @return 返回的Document
	 * @throws DocumentException
	 *             : DOM异常
	 * @throws IOException
	 *             : 文件IO异常
	 */
	private static Document getDocFromFile(String filePath)
			throws DocumentException, IOException {
		File file = new File(filePath);
		if (!file.isFile()) {
			throw new IOException("不是合法的文件!");
		}
		if (!file.exists()) {
			throw new IOException("文件不存在!");
		}

		Document doc = null;
		SAXReader saxReader = new SAXReader();
		doc = saxReader.read(file);
		return doc;
	}

	/**
	 * 从XML字符串中获取 Document
	 * 
	 * @param str
	 *            : XML字符串
	 * @return 返回的Document
	 * @throws DocumentException
	 *             : DOM异常
	 */
	private static Document getDocFormStr(String str) throws DocumentException {
		Document doc = DocumentHelper.parseText(str);
		return doc;
	}

	/**
	 * 生成 响应报文,返回给客户端(被测系统)
	 * 
	 * @param resXml
	 *            : 核心返回的响应报文
	 * @return 构造后的响应报文
	 * @throws DocumentException : DOM 异常
	 */
	public static byte[] genResponse(byte[] resXml) throws DocumentException {
		// 获取 核心响应报文 的根节点
		Document doc = getDocFormStr(new String(resXml).trim());
		Element root = doc.getRootElement();
		
		// 按照定义好的 响应报文结构 组合 核心返回的报文
		Document newDoc = createResponseStr();	//构造  响应报文结构体
		Element newE = newDoc.getRootElement();
		newE.add(root);
		
		return newDoc.asXML().getBytes();
	}

	/**
	 * 构建响应报文 框架
	 * 
	 * @return
	 * @see
	 */
	private static Document createResponseStr() {
		// 创建Document
		Document doc = DocumentHelper.createDocument();
	
		// 创建 NAMESAPCE
		Namespace SOAP_ENV_ns = new Namespace(SOAP_ENV_Name,
				SOAP_ENV_Value);
		Namespace SOAP_ENC_ns = new Namespace(SOAP_ENC_Name,
				SOAP_ENC_Value);
		Namespace xsi_ns = new Namespace(XMI_Name,
				XMI_Value);
		Namespace xsd_ns = new Namespace(XSD_Name,
				XSD_Value);

		QName envelopeQName = new QName("Envelope", SOAP_ENV_ns);
		Element envelopeElement = doc.addElement(envelopeQName);
		envelopeElement.add(SOAP_ENC_ns);
		envelopeElement.add(xsi_ns);
		envelopeElement.add(xsd_ns);
	
		return doc;
	}

	/**
	 * 从外部文件中读取二进制数据
	 * 
	 * @param filePath
	 *            :外部文件路径+名称
	 * @return 文件内容二进制流
	 * @throws IOException
	 *             : IO 异常
	 * @see 该方法用于构造 原始请求报文
	 */
	private static byte[] readFormFile(String filePath) throws IOException {
		File file = new File(filePath);
		if (!file.isFile()) {
			throw new IOException("不是合法的文件!");
		}
		if (!file.exists()) {
			throw new IOException("文件不存在!");
		}

		if (file == null) {
			return null;
		}
		ByteArrayOutputStream out = new ByteArrayOutputStream(1000);
		try {
			FileInputStream stream = new FileInputStream(file);
			byte[] b = new byte[1000];
			int n;
			while ((n = stream.read(b)) != -1)
				out.write(b, 0, n);
			stream.close();
			out.close();
		} finally {
			if (out != null) {
				out.close();
			}
		}
		return out.toByteArray();
	}

}
