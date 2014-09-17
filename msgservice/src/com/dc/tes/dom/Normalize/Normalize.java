package com.dc.tes.dom.Normalize;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import com.dc.tes.dom.MsgContainerUtils;
import com.dc.tes.dom.MsgDocument;
import com.dc.tes.dom.util.MsgLoader;

public class Normalize {

	/**
	 * use MsgContainerUtils.Normalize(doc, template) instead.
	 */
	@Deprecated
	public static MsgDocument NormalizeService(final MsgDocument doc, MsgDocument template) {
		return MsgContainerUtils.Normalize(doc, template);
	}

	public static void main(String args[]) {
		System.out.println("aa");
		StringBuffer data = new StringBuffer();
		data.append("<?xml version=\"1.0\" encoding=\"gb2312\"?>\n");
		data.append("<msg>\n");
		data.append("<field name=\"field\" desc=\"field\" optional=\"true\" isarray=\"true\" len=\"20\" type=\"string\">11mmmmm</field>\n");
		data.append("<field name=\"BkSeq333\" desc=\"银行流水号\" optional=\"true\" isarray=\"false\" len=\"20\" type=\"string\">11112937198268767123</field>\n");
		data.append("<field name=\"BkSeq\" desc=\"银行流水号\" optional=\"true\" isarray=\"false\" len=\"20\" type=\"string\">11112937198268767123</field>\n");
		data.append("<struct name=\"head3\" desc=\"head3\" isarray=\"true\">\n");
		data.append("<struct name=\"head2\" desc=\"head2\" isarray=\"false\">\n");
		data.append("<field name=\"field\" desc=\"field\" optional=\"true\" isarray=\"false\" len=\"20\" type=\"string\">111a</field>\n");
		data.append("</struct>\n");
		data.append("</struct>\n");
		data.append("<field name=\"field\" desc=\"field\" optional=\"true\" isarray=\"true\" len=\"20\" type=\"string\">11mmmmm</field>\n");
		data.append("</msg>\n");

		/*
		StringBuffer data2 = new StringBuffer();
		data2.append("<?xml version=\"1.0\" encoding=\"gb2312\"?>\n");
		data2.append("<msg>\n");
		data2.append("<field name=\"field\" desc=\"field\" optional=\"true\" isarray=\"false\" len=\"20\" type=\"string\">mmmmm</field>\n");
		data2.append("<field name=\"BkSeq333\" desc=\"银行流水号\" optional=\"true\" isarray=\"false\" len=\"20\" type=\"string\">12937198268767123</field>\n");
		data2.append("<field name=\"BkSeq\" desc=\"银行流水号\" optional=\"true\" isarray=\"false\" len=\"20\" type=\"string\">12937198268767123</field>\n");
		data2.append("<struct name=\"head3\" desc=\"head3\" isarray=\"true\">\n");
		data2.append("<struct name=\"head2\" desc=\"head2\" isarray=\"false\">\n");
		data2.append("<field name=\"field\" desc=\"field\" optional=\"true\" isarray=\"false\" len=\"20\" type=\"string\">a</field>\n");
		data2.append("</struct>\n");
		data2.append("</struct>\n");
		data2.append("</msg>\n");
		System.out.println(data);
		InputStream dinputStream2 = new ByteArrayInputStream(data2.toString().getBytes());
		MsgDocument doc2 = MsgLoader.Load(dinputStream2);
		System.out.println(data2);
		InputStream dinputStream = new ByteArrayInputStream(data.toString().getBytes());
		MsgDocument doc = MsgLoader.Load(dinputStream);
*/
		System.out.println(data);
		InputStream dinputStream = new ByteArrayInputStream(data.toString().getBytes());
		MsgDocument doc = MsgLoader.Load(dinputStream);
		//doc 期望,doc2 真实的
		//MsgDocument cr = Normalize.NormalizeService(doc, doc2);
		//System.out.println(cr.toString());

	}
}
