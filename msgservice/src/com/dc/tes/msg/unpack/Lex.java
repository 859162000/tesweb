package com.dc.tes.msg.unpack;

import java.nio.charset.Charset;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 词法单元 一个简单的基于字节数组的正则表达式分析机
 * 
 * @author lijic
 * 
 */
class Lex {
	/**
	 * 上次find()执行后 成功匹配的字节数量 当find()未执行或执行不成功时为-1
	 */
	private int m_length = -1;

	private Pattern pattern;
	private Charset encoding;

	/**
	 * 初始化一个Lex
	 * 
	 * @param lex
	 *            词法描述
	 * @param 编码
	 */
	Lex(String lex, Charset encoding) {
		this.pattern = Pattern.compile(lex);
		this.encoding = encoding;
	}

	/**
	 * 验证一段给定的字节流是否符合词法定义
	 * 
	 * @param bytes
	 *            待验证的字节流
	 * @return 返回给定的字节流是否符合本词法定义
	 */
	boolean match(byte[] bytes) {
		// 从第0字节开始寻找匹配
		int pos = find(bytes, 0);

		// 如果未找到匹配或匹配的起始位置不是0 则返回匹配失败
		if (pos != 0)
			return false;
		// 如果找到匹配 但匹配的长度与给定字节流的长度不相等 则返回匹配失败
		if (this.m_length != bytes.length)
			return false;

		return true;
	}

	/**
	 * 验证一段给定的字节流是否符合词法定义
	 * 
	 * @param bytes
	 *            待验证的字节流
	 * @param start
	 *            起始位置
	 * @param length
	 *            长度
	 * @return 返回给定的字节流是否符合本词法定义
	 */
	boolean match(byte[] bytes, int start, int length) {
		// 从start开始寻找匹配
		int pos = find(bytes, start);

		// 如果未找到匹配或匹配的起始位置不是start 则返回匹配失败
		if (pos != start)
			return false;
		// 如果找到匹配 但匹配的长度与给定的length不相等 则返回匹配失败		
		if (this.m_length != length)
			return false;

		return true;
	}

	/**
	 * 获取词法单元的长度 仅在find()成功返回后该函数才有意义
	 * 
	 * @return 返回在上次find()中成功匹配的字节数量
	 */
	int length() {
		return this.m_length;
	}

	/**
	 * 在给定的字节流中寻找指定的词法单元出现的字节位置
	 * 
	 * @param bytes
	 *            字节流
	 * @param start
	 *            寻找的起始字节位置
	 * @return 返回当前的词法单元在字节流中出现的位置 如果未出现则返回-1
	 */
	int find(byte[] bytes, int start) {
		String str = new String(bytes, start, bytes.length - start, this.encoding);
		Matcher matcher = this.pattern.matcher(str);
		if (matcher.find(0)) {
			this.m_length = matcher.end() - matcher.start();
			this.m_length = convert(bytes, start, this.m_length);

			return convert(bytes, 0, matcher.start()) + start;
		}
		return -1;
	}

	/**
	 * 工具函数 用于将使用正则得到的字符串长度/位置换算为字节长度/位置
	 */
	private int convert(byte[] bytes, int start, int len) {
		// 使用正则得到的长度是字符串长度 需要将其换算为字节长度
		return new String(bytes, start, bytes.length - start, encoding).substring(0, len).getBytes(encoding).length;
	}
}
