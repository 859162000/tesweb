package com.dc.tes.dom.util;

import java.io.IOException;

import org.apache.commons.lang.StringEscapeUtils;

import com.dc.tes.dom.DefaultForEachVisitor;
import com.dc.tes.dom.MsgArray;
import com.dc.tes.dom.MsgDocument;
import com.dc.tes.dom.MsgField;
import com.dc.tes.dom.MsgStruct;

/**
 * 工具类，提供将MsgDocumnt序列化为XML或JSON字符串，以及从JSON或XML反序列化MsgDocument的功能
 * 
 * @author lijic 修改从json转document函数
 */
public class DomSerializer {
	/**
	 * 将MsgDocument序列化为XML形式
	 */
	public static String SerializeToXml(MsgDocument doc) {
		// MsgDocument的toString()方法产生的就是一篇格式良好的描述其自身的XML
		return doc.toString();
	}

	/**
	 * 从XML反序列化MsgDocument
	 * 
	 * @throws IOException
	 */
	public static MsgDocument DeserializeFromXml(String xml) throws IOException {
		return MsgLoader.LoadXml(xml);
	}

	/**
	 * 将MsgDocument序列化为JSON形式
	 */
	public static String SerializeToJson(MsgDocument doc) {
		final StringBuffer buffer = new StringBuffer();

		// 拼JSON字符串
		doc.ForEach(new DefaultForEachVisitor() {
			@Override
			public void DocStart(MsgDocument doc) {
				buffer.append("{");
			}

			@Override
			public void DocEnd(MsgDocument doc) {
				if (buffer.length() != 1)
					buffer.deleteCharAt(buffer.length() - 1);
				buffer.append("}");
			}

			@Override
			public void StruStart(MsgStruct stru) {
				if (stru.parent() instanceof MsgArray)
					buffer.append("{");
				else
					buffer.append(String.format("\"%s\":{", stru.name()));
			}

			@Override
			public void StruEnd(MsgStruct stru) {
				if (buffer.charAt(buffer.length() - 1) == ',')
					buffer.setCharAt(buffer.length() - 1, '}');
				else
					buffer.append('}');

				buffer.append(",");
			}

			@Override
			public void ArrayStart(MsgArray array) {
				buffer.append(String.format("\"%s\":[", array.get(0).name()));
			}

			@Override
			public void ArrayEnd(MsgArray array) {
				if (buffer.charAt(buffer.length() - 1) == ',')
					buffer.setCharAt(buffer.length() - 1, ']');
				else
					buffer.append(']');

				buffer.append(",");
			}

			@Override
			public void Field(MsgField field) {
				String v = StringEscapeUtils.escapeJavaScript(field.value());
				if (field.parent() instanceof MsgArray)
					buffer.append(String.format("\"%s\",", v));
				else
					buffer.append(String.format("\"%s\":\"%s\",", field.name(), v));
			}
		});

		return buffer.toString();
	}
}
