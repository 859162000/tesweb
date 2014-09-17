package com.dc.tes.msg.unpack.parser;

import java.io.UnsupportedEncodingException;
import java.util.Map;

import org.apache.commons.collections.MapUtils;

import com.dc.tes.exception.CommonErr;
import com.dc.tes.exception.TESException;
import com.dc.tes.msg.util.Value;
import com.dc.tes.util.PackUtils;

/**
 * 字符串的解析器 从字节流中按照字符串的格式解析数据
 * 
 * @author lijic
 * 
 */
@ParserTag('b')
public class PBCD extends Parser {
	@Override
	protected Value parse(byte[] bytes, int start, int length, Map<String, String> params) {
		if (length == -1)
			return null;

		//String encoding = MapUtils.getString(params, "encoding");
		//char fillingChar = MapUtils.getString(params, "fillingChar", " ").charAt(0);
		//boolean left_align = MapUtils.getString(params, "align", "left").equalsIgnoreCase("left");

		return new Value(PackUtils.ReadBCD2(bytes, start, length, false, 0));
	}

	@Override
	protected int convert(byte[] bytes, int start, int length, Map<String, String> params) {
		String encoding = MapUtils.getString(params, "encoding");

	//	try {
			//return new String(bytes, start, bytes.length - start).substring(0, length).getBytes(encoding).length;
			return (length + 1)/2;
	//	} catch (UnsupportedEncodingException ex) {
	//		throw new TESException(CommonErr.UnsupportedEncoding, encoding);
	//	}
	}
}
