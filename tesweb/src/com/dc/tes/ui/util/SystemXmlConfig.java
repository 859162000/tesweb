package com.dc.tes.ui.util;

import java.util.ArrayList;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.lang.StringUtils;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.dc.tes.ui.client.model.GWTPack_Base;
import com.dc.tes.ui.client.model.MsgAttribute;

public class SystemXmlConfig implements ISystemConfig {
	private static XPathFactory s_factory = XPathFactory.newInstance();
	private static XPath x;
	static {
		x = s_factory.newXPath();
	}

	private String m_emptyReqStru;
	private String m_emptyRespStru;
	private ArrayList<MsgAttribute> m_reqFieldAttributes;
	private ArrayList<MsgAttribute> m_reqStructAttributes;
	private ArrayList<MsgAttribute> m_respFieldAttributes;
	private ArrayList<MsgAttribute> m_respStructAttributes;
	
	private String 	sysName;
	private int 	isClientSimu;
	
	/**
	 * 
	 * @param doc
	 * @param isClientSimu
	 * @param sysName
	 * @throws DOMException
	 * @throws XPathExpressionException
	 */
	public SystemXmlConfig(Document doc,int isClientSimu,String sysName) throws DOMException, XPathExpressionException {
		this.isClientSimu = isClientSimu;
		this.sysName = sysName;
		
		String pathBegin = "//conf/mode" + isClientSimu;
		this.m_emptyReqStru = ((Node) x.evaluate(pathBegin + "/resp/empty", doc, XPathConstants.NODE)).getTextContent().trim();
		this.m_reqFieldAttributes = this.loadAttributes((NodeList) x.evaluate(pathBegin + "/req/field/property", doc, XPathConstants.NODESET));
		this.m_reqFieldAttributes.add(getDefaultValueAttr());
		this.m_reqStructAttributes = this.loadAttributes((NodeList) x.evaluate(pathBegin + "/req/struct/property", doc, XPathConstants.NODESET));

		this.m_emptyRespStru = ((Node) x.evaluate(pathBegin + "/resp/empty", doc, XPathConstants.NODE)).getTextContent().trim();
		this.m_respFieldAttributes = this.loadAttributes((NodeList) x.evaluate(pathBegin + "/resp/field/property", doc, XPathConstants.NODESET));
		this.m_respFieldAttributes.add(getDefaultValueAttr());
		this.m_respStructAttributes = this.loadAttributes((NodeList) x.evaluate(pathBegin + "/resp/struct/property", doc, XPathConstants.NODESET));
	}
	
	private MsgAttribute getDefaultValueAttr()
	{
		return new MsgAttribute(GWTPack_Base.m_defaultValue, "默认值", "", "", "100");
	}

	private ArrayList<MsgAttribute> loadAttributes(NodeList lst) throws DOMException, XPathExpressionException {
		ArrayList<MsgAttribute> atts = new ArrayList<MsgAttribute>();

		for (int i = 0; i < lst.getLength(); i++) {
			Node node = lst.item(i);

			String name = GetNodeValue(node,"@name","");
			String displayName = GetNodeValue(node,"@displayName","");
			String listItems = GetNodeValue(node,"@list","");
			String defaultValue = GetNodeValue(node,"@default","默认值");
			String width = GetNodeValue(node,"@width","100");

			atts.add(new MsgAttribute(name, displayName, StringUtils.defaultString(listItems), StringUtils.defaultString(defaultValue), width));
		}

		return atts;
	}
	
	private String GetNodeValue(Node node,String propertyName,String defaultValue) throws XPathExpressionException
	{
		Node childNode = ((Node) x.evaluate(propertyName, node, XPathConstants.NODE));
		
		return childNode == null ? defaultValue : childNode.getNodeValue();
	}

	@Override
	public byte[] getEmptyReqStru() {
		return this.m_emptyReqStru.getBytes();
	}

	@Override
	public byte[] getEmptyRespStru() {
		return this.m_emptyRespStru.getBytes();
	}

	@Override
	public ArrayList<MsgAttribute> getReqFieldAttributes() {
		return this.m_reqFieldAttributes;
	}

	@Override
	public ArrayList<MsgAttribute> getReqStructAttributes() {
		return this.m_reqStructAttributes;
	}

	@Override
	public ArrayList<MsgAttribute> getRespFieldAttributes() {
		return this.m_respFieldAttributes;
	}

	@Override
	public ArrayList<MsgAttribute> getRespStructAttributes() {
		return this.m_respStructAttributes;
	}

	@Override
	public int getIsClientSimu() {
		return isClientSimu;
	}

	@Override
	public String getSystemName() {
		return sysName;
	}
}
