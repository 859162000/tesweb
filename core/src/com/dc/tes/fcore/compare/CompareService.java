package com.dc.tes.fcore.compare;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.HashMap;

import com.dc.tes.dom.MsgContainerUtils;
import com.dc.tes.dom.MsgDocument;
import com.dc.tes.dom.util.MsgLoader;
import com.dc.tes.msg.util.Value;

/**
 * 
 * @author songljb
 * 
 */
public class CompareService {

	/**
	 * 结果比对CompareService.CompareDocument(MsgDocument expect,MsgDocument outcome)
	 * 
	 * @param expect
	 *            期望的结构(用户配置的)
	 * @param outcome
	 *            真实的结构(拆包后的数据)
	 * @return 比对结果的xml
	 */
	public static CompareResult CompareDocument(MsgDocument expect, MsgDocument outcome) {
		
		if (expect == null || outcome == null) {
			return null;
		}
		CompareResult cresult = new CompareResult();
		cresult.setDifference(0);
		MsgDocument result = outcome.Copy();
		CompareForEachVisitor cv = new CompareForEachVisitor();
		//遍历预期结果,记录用户有输入数据项的内容
		expect.ForEach(cv);
		cv.begcompare();
		//遍历真实结果,将期望结果添加到属性中
		result.ForEach(cv);

		//比对后,将期望内容在真实结构中有没有找到的项添加到比对结果中
		if (cv.m_comparefields.size() != 0) {
			for (String key : cv.m_comparefields.keySet()) {
				String value = cv.m_comparefields.get(key).value;
				String desc = cv.m_comparefields.get(key).desc;
				HashMap<String, Value> addmap = new HashMap<String, Value>();
				addmap.put("expect_result", new Value(value));
				addmap.put("desc", new Value(desc));
				MsgContainerUtils.PutValue(result, key, "", addmap);
			}
		}
		cresult.setCom_result(result);
		cresult.setDifference(cv.difference);		
		return cresult;
	}
	

	public static void main(String args[]) {
		System.out.println("aa");
		StringBuffer data = new StringBuffer();
		data.append("<?xml version=\"1.0\" encoding=\"gb2312\"?>\n");
		data.append("<msg>\n");
		data.append("<field name=\"BkSeq\" desc=\"银行流水号\" optional=\"true\" " + "isarray=\"false\" len=\"20\" type=\"string\">12937198268767123</field>\n");
		data.append("<struct name=\"head\" desc=\"head\" isarray=\"false\">\n");
		data.append("<struct name=\"head2\" desc=\"head2\" isarray=\"false\">\n");
		data.append("<field name=\"field\" desc=\"field\" optional=\"true\" " + "isarray=\"false\" len=\"20\" type=\"string\">fieldinstru</field>\n");
		data.append("</struct>\n");
		data.append("</struct>\n");
		data.append("<struct name=\"head3\" desc=\"head3\" isarray=\"false\">\n");
		data.append("<struct name=\"head2\" desc=\"head2\" isarray=\"false\">\n");
		data.append("<field name=\"field\" desc=\"field\" optional=\"true\" " + "isarray=\"false\" len=\"20\" type=\"string\">a</field>\n");
		data.append("</struct>\n");
		data.append("</struct>\n");
		data.append("</msg>\n");

		StringBuffer data2 = new StringBuffer();
		data2.append("<?xml version=\"1.0\" encoding=\"gb2312\"?>\n");
		data2.append("<msg>\n");
		data2.append("<field name=\"BkSeq333\" desc=\"银行流水号\" optional=\"true\" " + "isarray=\"false\" len=\"20\" type=\"string\">12937198268767123</field>\n");

		data2.append("<field name=\"BkSeq\" desc=\"银行流水号\" optional=\"true\" " + "isarray=\"false\" len=\"20\" type=\"string\">12937198268767123</field>\n");

		data2.append("<struct name=\"head3\" desc=\"head3\" isarray=\"false\">\n");
		data2.append("<struct name=\"head2\" desc=\"head2\" isarray=\"false\">\n");
		data2.append("<field name=\"field\" desc=\"field\" optional=\"true\" " + "isarray=\"false\" len=\"20\" type=\"string\">a</field>\n");
		data2.append("<field name=\"field44\" desc=\"field\" optional=\"true\" " + "isarray=\"false\" len=\"20\" type=\"string\">a</field>\n");
		data2.append("</struct>\n");
		data2.append("</struct>\n");

		data2.append("</msg>\n");
		InputStream dinputStream = new ByteArrayInputStream(data.toString().getBytes());
		MsgDocument doc = MsgLoader.Load(dinputStream);
		InputStream dinputStream2 = new ByteArrayInputStream(data2.toString().getBytes());
		MsgDocument doc2 = MsgLoader.Load(dinputStream2);
		
		//doc 期望,doc2 真实的
		CompareResult cr = CompareService.CompareDocument(doc, doc2);
		cr.toString();//比对结果
		cr.getDifference();// 不匹配的个数
	}
	
}
