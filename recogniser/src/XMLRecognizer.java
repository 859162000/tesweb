import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;


public class XMLRecognizer {

	public static String RecogniseFromXml(byte[] bin) {
		
		String tranCode = "";
		String xmlStr = new String(bin);

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		Element theTranCode = null, root = null;
		try {
			factory.setIgnoringElementContentWhitespace(true);
			
			DocumentBuilder db = factory.newDocumentBuilder();

			final byte[] bytes = xmlStr.getBytes();
			final ByteArrayInputStream is = new ByteArrayInputStream(bytes);
			final InputSource source = new InputSource(is);

			Document xmldoc = db.parse(source);		
			root = xmldoc.getDocumentElement();
			theTranCode = (Element) selectSingleNode("/root/head/transid", root);
			//Element nameNode = (Element) theTranCode.getElementsByTagName("price").item(0);
			if (theTranCode != null) {
				tranCode = theTranCode.getFirstChild().getNodeValue();
			}
			else {
				System.out.println("获取 /root/head/transid 失败！");
			}
			//System.out.println(tranCode);
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return tranCode;
	}

	public static Node selectSingleNode(String express, Object source) {// 查找节点，并返回第一个符合条件节点
		Node result = null;
		XPathFactory xpathFactory = XPathFactory.newInstance();
		XPath xpath = xpathFactory.newXPath();
		try {
			result = (Node) xpath
					.evaluate(express, source, XPathConstants.NODE);
		} catch (XPathExpressionException e) {
			e.printStackTrace();
		}

		return result;
	}

}
