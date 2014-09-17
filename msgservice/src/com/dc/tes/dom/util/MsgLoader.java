package com.dc.tes.dom.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;

import org.apache.commons.lang.NullArgumentException;
import org.apache.commons.lang.StringUtils;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import com.dc.tes.dom.MsgDocument;
import com.dc.tes.exception.CommonErr;
import com.dc.tes.exception.MsgErr;
import com.dc.tes.exception.TESException;

/**
 * 工具类 用于从给定输入中读取XML报文数据
 * 
 * @author lijic
 * 
 */
public class MsgLoader {
	/**
	 * 从输入流中读取报文数据
	 * 
	 * @param in
	 *            包含报文数据信息的输入流 该流在读取结束后将被关闭
	 * @return 读取出的报文数据
	 */
	public static MsgDocument Load(InputStream in) {
		if (in == null)
			throw new NullArgumentException("in");

		return load(new InputStreamReader(in));
	}

	/**
	 * 解析XML格式的报文数据
	 * 
	 * @param xml
	 *            包含报文数据信息的XML字符串
	 * @return 读取出的报文数据
	 */
	public static MsgDocument LoadXml(String xml) {
		if (StringUtils.isEmpty(xml))
			throw new TESException(MsgErr.Dom.NullTranstruct);

		return load(new StringReader(xml));
	}

	/**
	 * 工具函数 用于解析报文数据
	 * 
	 * @param r
	 *            读取器
	 * @return 读取出的报文数据
	 */
	private static MsgDocument load(Reader r) {
		InputSource source = new InputSource(r);

		MsgDocument doc;

		try {
			// 使用SAX进行解析
			XMLReader reader = XMLReaderFactory.createXMLReader();
			// 使用gts.dom.utils.DocHandler进行解析
			DocHandler handler = new DocHandler();

			// 进行解析
			reader.setContentHandler(handler);
			reader.parse(source);

			// 取解析好的MsgDocument
			doc = handler.getDocument();
		} catch (Exception ex) {
			throw new TESException(MsgErr.Dom.SaxFail, ex);
		} finally {
			// 关掉输入流
			try {
				r.close();
			} catch (IOException ex) {
				throw new TESException(CommonErr.IO.CloseInputStreamFail, ex);
			}
		}
		return doc;
	}
}
