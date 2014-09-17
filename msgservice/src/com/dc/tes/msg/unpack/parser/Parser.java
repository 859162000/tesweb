package com.dc.tes.msg.unpack.parser;

import java.util.HashMap;
import java.util.Map;

import com.dc.tes.exception.MsgErr;
import com.dc.tes.exception.TESException;
import com.dc.tes.msg.util.Value;
import com.dc.tes.util.InstanceCreater;

/**
 * 字节解析器 总体上相当于编译原理中的词法分析器 负责从字节流中取出一段字节 并将其解析为一个Value对象
 * 
 * @author lijic
 * 
 */
public abstract class Parser {
	/**
	 * 所有目前支持的Parser的实例的缓存
	 */
	private static HashMap<Character, Parser> s_parsers = new HashMap<Character, Parser>();

	/**
	 * 初始化Parser实例缓存
	 */
	static {
		Class<?>[] classes = {
				PBCD.class,
				PByte.class,
				PFloatBigEndian.class,
				PFloatSmallEndian.class,
				PIntBigEndian.class,
				PIntSmallEndian.class,
				PString.class };

		for (Class<?> cls : classes)
			if (cls.getSuperclass() == Parser.class && cls.isAnnotationPresent(ParserTag.class))
				s_parsers.put(cls.getAnnotation(ParserTag.class).value(), (Parser) InstanceCreater.CreateInstance(cls));
	}

	/**
	 * 根据数据长度计算出字节长度
	 * 
	 * @param bytes
	 *            报文字节流
	 * @param start
	 *            片段起始位置
	 * @param length
	 *            片段的数据长度
	 * @param dataFormatChar
	 *            数据类型标志
	 * @param params
	 *            扩展参数
	 * @return 计算出的与这段数据相匹配的字节长度
	 */
	public static int Convert(byte[] bytes, int start, int length, char dataFormatChar, Map<String, String> params) {
		if (length == -1)
			return -1;

		if (!s_parsers.containsKey(dataFormatChar))
			throw new TESException(MsgErr.Unpack.ParserNotFound, String.valueOf(dataFormatChar));

		return s_parsers.get(dataFormatChar).convert(bytes, start, length, params);
	}

	/**
	 * 将报文字节流中指定位置的字节片段解析为值
	 * 
	 * @param bytes
	 *            报文字节流
	 * @param start
	 *            片段起始位置
	 * @param length
	 *            片段的字节长度
	 * @param dataFormatChar
	 *            数据类型标志
	 * @param params
	 *            扩展参数
	 * @return 解析出的值 如果由于给定长度为-1导致无法解析则返回null
	 */
	public static Value Parse(byte[] bytes, int start, int length, char dataFormatChar, Map<String, String> params) {
		if (!s_parsers.containsKey(dataFormatChar))
			throw new TESException(MsgErr.Unpack.ParserNotFound, String.valueOf(dataFormatChar));

		return s_parsers.get(dataFormatChar).parse(bytes, start, length, params);
	}

	/**
	 * 在派生类中实现时 此类用于将报文字节流中指定位置的字节片段解析为值
	 */
	protected abstract Value parse(byte[] bytes, int start, int length, Map<String, String> params);

	/**
	 * 在派生类中重写时 此类用于根据数据长度计算出字节长度
	 */
	protected int convert(byte[] bytes, int start, int length, Map<String, String> params) {
		throw new TESException(MsgErr.Unpack.LenConvertNotSupported, "%" + this.getClass().getAnnotation(ParserTag.class).value());
	}
}
