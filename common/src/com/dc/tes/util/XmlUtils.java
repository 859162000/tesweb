package com.dc.tes.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.dc.tes.exception.CommonErr;
import com.dc.tes.exception.TESException;

/**
 * 工具类 提供与XML相关的一些工具函数
 * 
 * @author huangzx
 * 
 */
public class XmlUtils {
	private static DocumentBuilderFactory s_domFactory = DocumentBuilderFactory.newInstance();
	private static XPath x = XPathFactory.newInstance().newXPath();

	/**
	 * 从流中读取一篇XML文档
	 * 
	 * @param s
	 *            指向要读取的XML的流 该流在读取结束后将被关闭
	 * @return 读取出的XML文档对象
	 */
	public static Document LoadXml(InputStream s) {
		if (s == null)
			throw new TESException(CommonErr.Xml.LoadNullInputStream);

		try {
			return s_domFactory.newDocumentBuilder().parse(s);
		} catch (SAXException ex) {
			throw new TESException(CommonErr.Xml.SAXFail, ex);
		} catch (IOException ex) {
			throw new TESException(CommonErr.IO.IOReadFail, ex);
		} catch (Exception ex) {
			throw new TESException(CommonErr.Xml.LoadXmlFail, ex);
		} finally {
			try {
				s.close();
			} catch (IOException ex) {
				throw new TESException(CommonErr.IO.CloseInputStreamFail, ex);
			}
		}
	}

	/**
	 * 读取一个xml字符串 从中解析出一篇XML文档
	 * 
	 * @param xml
	 *            要读取的xml字符串
	 * @return 读取出的XML文档对象
	 */
	public static Document LoadXml(String xml) {
		if (xml == null || xml.length() == 0)
			throw new TESException(CommonErr.Xml.LoadNullString);

		try {
			return s_domFactory.newDocumentBuilder().parse(new InputSource(new StringReader(xml)));
		} catch (SAXException ex) {
			throw new TESException(CommonErr.Xml.SAXFail, xml, ex);
		} catch (IOException ex) {
			throw new TESException(CommonErr.IO.IOReadFail, ex);
		} catch (Exception ex) {
			throw new TESException(CommonErr.Xml.LoadXmlFail, ex);
		}
	}

	/**
	 * 将一篇XML文档写入到输出流中
	 * 
	 * @param doc
	 *            要写入到流中的XML文档对象
	 * @param s
	 *            要将XML输出到的流 该流在写入结束后将被关闭
	 */
	public static void SaveXml(Document doc, OutputStream s) {
		if (s == null)
			throw new TESException(CommonErr.Xml.WriteNullOutputStream);

		try {
			TransformerFactory.newInstance().newTransformer().transform(new DOMSource(doc), new StreamResult(s));
		} catch (Throwable ex) {
			throw new TESException(CommonErr.Xml.WriteXmlFail, ex);
		} finally {
			if (s != null)
				try {
					s.close();
				} catch (IOException ex) {
					throw new TESException(CommonErr.IO.CloseOutputStreamFail, ex);
				}
		}
	}

	/**
	 * 将一篇XML文档表示成字符串形式
	 * 
	 * @param doc
	 *            要表示成字符串的XML文档对象
	 * @return 表示该XML文档对象的xml字符串
	 */
	public static String SaveXml(Document doc) {
		StringWriter w = new StringWriter();
		try {
			TransformerFactory.newInstance().newTransformer().transform(new DOMSource(doc), new StreamResult(w));
		} catch (Throwable ex) {
			throw new TESException(CommonErr.Xml.WriteXmlFail, ex);
		}
		return w.toString();
	}

	/**
	 * 使用XPath定位XML文档中的节点
	 * 
	 * @param node
	 *            当前节点
	 * @param xpath
	 *            XPath
	 * @return 由XPath指定的节点
	 */
	@SuppressWarnings("unchecked")
	public static <T extends Node> T SelectNode(Node node, String xpath) {
		if (xpath == null)
			throw new TESException(CommonErr.Xml.XPathSyntaxError, "<null>");

		try {
			return (T) x.evaluate(xpath, node, XPathConstants.NODE);
		} catch (XPathExpressionException ex) {
			throw new TESException(CommonErr.Xml.XPathSyntaxError, xpath, ex);
		}
	}

	/**
	 * 使用XPath定位XML文档中的节点并获取该节点的值 如果该节点不存在则返回null
	 * 
	 * @param node
	 *            当前节点
	 * @param xpath
	 *            XPath
	 * @return 由XPath指定的节点的值
	 */
	public static String SelectNodeText(Node node, String xpath) {
		return getNodeText(SelectNode(node, xpath));
	}

	/**
	 * 使用XPath定位XML文档中的节点列表
	 * 
	 * @param node
	 *            当前节点
	 * @param xpath
	 *            XPath
	 * @return 由XPath指定的节点列表
	 */
	@SuppressWarnings("unchecked")
	public static <T extends Node> List<T> SelectNodes(Node node, String xpath) {
		if (xpath == null)
			throw new TESException(CommonErr.Xml.XPathSyntaxError, "<null>");

		NodeList lst;
		try {
			lst = (NodeList) x.evaluate(xpath, node, XPathConstants.NODESET);
		} catch (XPathExpressionException ex) {
			throw new TESException(CommonErr.Xml.XPathSyntaxError, xpath, ex);
		}
		ArrayList<T> nodes = new ArrayList<T>();
		for (int i = 0; i < lst.getLength(); i++)
			nodes.add((T) lst.item(i));
		return nodes;
	}

	/**
	 * 使用XPath定位XML文档中的节点列表并获取这些节点的值
	 * 
	 * @param node
	 *            当前节点
	 * @param xpath
	 *            XPath
	 * @return 由XPath指定的节点列表的值组成的列表
	 */
	public static List<String> SelectNodeListText(Node node, String xpath) {
		ArrayList<String> lst = new ArrayList<String>();
		for (Node n : SelectNodes(node, xpath))
			lst.add(getNodeText(n));
		return lst;
	}

	/**
	 * 取XML节点的Attribute，如果没有则返回null
	 * 
	 * @param e
	 *            XML节点
	 * @param name
	 *            属性名称
	 * @return 属性的值或null
	 */
	public static String getAttributeOrNull(Element e, String name) {
		Node a = e.getAttributeNode(name);
		if (a == null)
			return null;
		else
			return a.getNodeValue();
	}

	/**
	 * 取XML节点的值（砍掉了注释节点，砍掉了首尾空格） 如果该节点不存在 则返回一个空字符串
	 * 
	 * @param node
	 *            XML节点
	 * @return 节点的值
	 */
	public static String getNodeText(Node node) {
		if (node == null)
			return null;

		StringBuffer buffer = new StringBuffer();

		Node n = node.getFirstChild();
		if (n == null)
			return null;

		do {
			if (n.getNodeType() == Node.TEXT_NODE)
				buffer.append(n.getNodeValue().replaceAll("\\s+", " "));
			if (n.getNodeType() == Node.CDATA_SECTION_NODE)
				buffer.append(n.getNodeValue());
		} while ((n = n.getNextSibling()) != null);

		return buffer.toString().trim();
	}

	/**
	 * 将一个字符串进行xml编码 处理掉字符串中的&lt; &gt; &amp; &quot;字符
	 * 
	 * @param str
	 *            要进行xml编码的字符串
	 * @return 进行过xml编码处理的字符串
	 */
	public static String XmlEncode(String str) {
		StringWriter writer = new StringWriter();
		if (str == null)
			return "";
		int len = str.length();
		for (int i = 0; i < len; i++) {
			char c = str.charAt(i);
			switch (c) {
			case '<':
				writer.write("&lt;");
				break;
			case '>':
				writer.write("&gt;");
				break;
			case '&':
				writer.write("&amp;");
				break;
			case '"':
				writer.write("&quot;");
				break;
			default:
				writer.write(c);
				break;
			}
		}
		return writer.toString();
	}
}
