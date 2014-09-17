package com.dc.tes.dom.util;

import java.util.LinkedHashMap;
import java.util.Map;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.ext.DefaultHandler2;

import com.dc.tes.dom.MsgDocument;
import com.dc.tes.exception.MsgErr;
import com.dc.tes.exception.TESException;

/**
 * 报文XML的SaxHandler
 * 
 * @author lijic
 * 
 */
class DocHandler extends DefaultHandler2 {
	/**
	 * 报文构造器实例
	 */
	private DocBuilder m_builder = new DocBuilder();

	/**
	 * xml节点的值 该值通常表示域的值
	 */
	private String m_value;
	/**
	 * xml节点的属性列表
	 */
	private Map<String, String> m_atts;

	/**
	 * 获取读取出的MsgDocument对象
	 * 
	 * @return 读取出的文档对象
	 */
	public MsgDocument getDocument() {
		return this.m_builder.Export();
	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		this.m_value += new String(ch, start, length);
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
		this.m_value = "";

		if (localName.equals("msg"))
			return;
		else if (localName.equals("field")) {
			this.m_atts = convertAttrs(atts);
		} else if (localName.equals("struct"))
			this.m_builder.BeginStru(this.convertAttrs(atts));
		else
			throw new TESException(MsgErr.Dom.UnknownElementName, localName);
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		if (localName.equals("msg"))
			return;
		else if (localName.equals("field"))
			this.m_builder.Field(this.m_value, this.m_atts);
		else if (localName.equals("struct"))
			this.m_builder.EndStru();
		else
			throw new TESException(MsgErr.Dom.UnknownElementName, localName);
	}

	/**
	 * 工具函数 用于将xml属性列表转为一个Map
	 */
	private Map<String, String> convertAttrs(Attributes atts) {
		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
		for (int i = 0; i < atts.getLength(); i++)
			map.put(atts.getLocalName(i), atts.getValue(i));
		return map;
	}
}
