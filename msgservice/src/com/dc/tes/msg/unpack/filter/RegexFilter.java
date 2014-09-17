package com.dc.tes.msg.unpack.filter;

import java.nio.charset.Charset;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.w3c.dom.Element;

import com.dc.tes.exception.MsgErr;
import com.dc.tes.exception.TESException;
import com.dc.tes.util.XmlUtils;

/**
 * 以正则为依据的忽略片段
 * 
 * @author lijic
 * 
 */
class RegexFilter extends Filter {
	/**
	 * 正则表达式
	 */
	private final Pattern m_regex;
	/**
	 * 编组序号
	 */
	private final int m_group;
	/**
	 * 字节流的编码
	 */
	private final Charset m_encoding;

	/**
	 * 初始化一个以正则为依据的过滤器
	 */
	RegexFilter(Element e, Charset encoding) {
		// 正则表达式
		String regex = XmlUtils.SelectNodeText(e, "regex");
		if (regex == null)
			throw new TESException(MsgErr.Unpack.FilterArgumentNotFound, "regex");
		try {
			this.m_regex = Pattern.compile(regex);
		} catch (Exception ex) {
			throw new TESException(MsgErr.Unpack.LoadFilterArgumentError, "regex", ex);
		}

		// 编组序号
		String group = XmlUtils.SelectNodeText(e, "group");
		if (regex == null)
			throw new TESException(MsgErr.Unpack.FilterArgumentNotFound, "group");
		try {
			this.m_group = Integer.parseInt(group);
			if (this.m_group < 0)
				throw new TESException(MsgErr.Unpack.RegexGroupMustPositive, group);
		} catch (NumberFormatException ex) {
			throw new TESException(MsgErr.Unpack.RegexGroupUnparseable, group);
		}

		// 编码
		this.m_encoding = encoding;
	}

	@Override
	public byte[] Clean(byte[] bytes) {
		// 将字节流编码为字符串
		String msg = new String(bytes, this.m_encoding);

		// 用正则循环查找
		Matcher matcher = this.m_regex.matcher(msg);
		while (matcher.find()) {
			// 如果找到了匹配 则取出指定的编组
			String match = matcher.group(this.m_group);

			// 得到编组的起始位置			
			int pos = matcher.start(this.m_group);

			// 从字符串中砍掉找到的编组
			msg = msg.substring(0, pos) + msg.substring(pos + match.length());

			// 继续用正则对字符串进行查找
			matcher = this.m_regex.matcher(msg);
		}

		// 将过滤完的字符串编码返回
		return msg.getBytes(this.m_encoding);
	}
}
