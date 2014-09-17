package com.dc.tes.msg.util;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dc.tes.exception.MsgErr;
import com.dc.tes.exception.TESException;
import com.dc.tes.util.HexStringUtils;
import com.dc.tes.util.type.BytePackage;

/**
 * 工具类 用于解析格式字符串
 * 
 * @author lijic
 * 
 */
public class FormatStringParser {
	/**
	 * 解析格式字符串，将其拆解为一系列FormatSegment
	 * 
	 * @param formatString
	 *            要被解析的格式字符串
	 * @param encoding
	 *            此格式字符串使用的编码
	 * @return 返回一个FormatSegment的列表
	 */
	public static List<FormatFragment> Parse(String formatString, Charset encoding) {
		if (formatString == null)
			throw new TESException(MsgErr.FormatStringSyntaxError, "<null>");

		ArrayList<FormatFragment> segments = new ArrayList<FormatFragment>();
		int paramId = 0;

		// 当前是否处于引用模式 在此模式下当前的字符将被转义
		boolean refMode = false;
		// 当前是否处于字节流模式 在此模式下当前的字符将被放入byteString作为表示字节流的字符串进行解析
		boolean byteStringMode = false;
		// 当前是否处于扩展属性模式 在此模式下当前的字符将被放入paramExString作为格式描述符的扩展属性解析
		boolean paramExMode = false;

		// 文本缓存区
		StringBuilder textString = new StringBuilder();
		// 字节缓存区
		StringBuilder byteString = new StringBuilder();
		// 参数的缓存区
		StringBuilder paramExString = new StringBuilder();

		for (int i = 0; i < formatString.length(); i++) {
			char c = formatString.charAt(i);

			// 如果当前是转义模式
			if (refMode)
				switch (c) {
				case '%': // 连续的两个%字符表示%字符
					segments.add(new FormatFragment(String.valueOf('%').getBytes(encoding)));
					refMode = false;
					break;
				case '{': // ｛表示字节流段的开始
					byteString = new StringBuilder();
					refMode = false;
					byteStringMode = true;
					break;
				case '[': // ［表示扩展属性的开始
					paramExString = new StringBuilder();
					refMode = false;
					paramExMode = true;
					break;
				default:// 其它字符表示格式描述符的本体 取一个字符，退出转义模式
					// 将参数段添加到段列表中
					segments.add(new FormatFragment(c, parseParamExString(paramExString.toString()), paramId++));

					paramExString = new StringBuilder();

					refMode = false;
				}
			// 当前不是转义模式
			else if (c == '%') { // %表示进入转义模式
				// 将之前的文本段添加到段列表中
				segments.add(new FormatFragment(textString.toString().getBytes(encoding)));
				textString = new StringBuilder();
				refMode = true;
			} else if (byteStringMode) // 如果当前是字节流段模式
				if (c == '}') { // ｝表示字节流段的结束
					// 将字节流段作为文本段添加到段列表中
					byte[] bytes = HexStringUtils.FromHexString(byteString.toString());
					segments.add(new FormatFragment(bytes));

					refMode = false;
					byteStringMode = false;
				} else
					// 其它字符表示字节流段的内容
					byteString.append(c);
			else if (paramExMode) // 如果当前是扩展属性模式
				if (c == ']') { // ］表示扩展属性的结束
					refMode = true;
					paramExMode = false;
				} else
					// 其它字符表示扩展属性的内容
					paramExString.append(c);
			else
				// 其它字符 这是最普通的情况 表示文本
				textString.append(c);
		}

		// 将最后一个文本段添加到段列表中
		segments.add(new FormatFragment(textString.toString().getBytes(encoding)));

		if (refMode || byteStringMode || paramExMode)
			throw new TESException(MsgErr.FormatStringSyntaxError, formatString);
		return segments;
	}

	/**
	 * 解析简单格式字符串（不含格式描述符）为一个字节数组
	 * 
	 * @param formatString
	 *            简单格式字符串
	 * @param encoding
	 *            该格式字符串使用的编码
	 * @return 此格式字符串描述的字节数组
	 */
	public static byte[] ComplieSimpleFormat(String formatString, Charset encoding) {
		if (formatString == null)
			throw new TESException(MsgErr.FormatStringSyntaxError, "<null>");
		if (formatString.replaceAll("%%", "").replaceAll("%\\{.*?\\}", "").indexOf('%') != -1)
			throw new TESException(MsgErr.SimpleFormatStringContainsParam, formatString);

		BytePackage p = new BytePackage();
		List<FormatFragment> segments = FormatStringParser.Parse(formatString, encoding);
		for (FormatFragment segment : segments)
			p.Append(segment.bytes);

		return p.getBytes();
	}

	/**
	 * 解析格式描述符的扩展属性字符串
	 * 
	 * @param paramEx
	 *            扩展属性字符串
	 * @return 扩展属性列表
	 */
	private static Map<String, String> parseParamExString(String paramEx) {
		HashMap<String, String> map = new HashMap<String, String>();

		if (paramEx == null || paramEx.length() == 0)
			return map;

		for (String p : paramEx.split(",")) {
			String n = p.substring(0, p.indexOf('='));
			String v = p.substring(p.indexOf('=') + 2, p.length() - 1);
			v = v.replace("''", "'");

			map.put(n, v);
		}
		return map;
	}

	/**
	 * 表示格式字符串中的片段
	 * 
	 * @author lijic
	 * 
	 */
	public static class FormatFragment {
		public final boolean isParamFragment;

		/**
		 * 当前文本片段中保存的字节数组
		 */
		public final byte[] bytes;

		/**
		 * 当前参数片段对应的数据类型字符
		 */
		public final char dataFormatChar;
		/**
		 * 当前参数片段对应的参数列表
		 */
		public final Map<String, String> params;

		/**
		 * 使用字节数组初始化一个片段的实例 这表示此片段是文本片段
		 * 
		 * @param bytes
		 *            段中的数据
		 */
		public FormatFragment(byte[] bytes) {
			this.isParamFragment = false;
			this.bytes = bytes;

			this.dataFormatChar = 0;
			this.params = null;
		}

		/**
		 * 使用参数信息初始化一个片段的实例 这表示此片段是参数片段
		 * 
		 * @param c
		 *            组包processor或拆包parser的数据类型字符
		 * @param params
		 *            参数列表
		 * @param paramId
		 *            参数序号
		 */
		public FormatFragment(char c, Map<String, String> params, int paramId) {
			this.isParamFragment = true;

			this.dataFormatChar = c;
			this.params = params;

			this.bytes = null;
		}
	}
}
