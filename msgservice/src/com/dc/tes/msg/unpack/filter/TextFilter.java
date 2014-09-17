package com.dc.tes.msg.unpack.filter;

import java.nio.charset.Charset;

import org.w3c.dom.Element;

import com.dc.tes.exception.MsgErr;
import com.dc.tes.exception.TESException;
import com.dc.tes.msg.util.FormatStringParser;
import com.dc.tes.util.HexStringUtils;
import com.dc.tes.util.XmlUtils;
import com.dc.tes.util.type.BytePackage;

/**
 * 以纯文本为依据的过滤器
 * 
 * @author lijic
 * 
 */
public class TextFilter extends Filter {
	/**
	 * 要过滤掉的字节
	 */
	private final byte[] m_bytes;

	/**
	 * 初始化一个以纯文本为依据的过滤器
	 */
	TextFilter(Element e, Charset encoding) {
		// 要过滤掉的字节
		String bytes = XmlUtils.SelectNodeText(e, "bytes");
		if (bytes == null)
			throw new TESException(MsgErr.Unpack.FilterArgumentNotFound, "bytes");
		try {
			//this.m_bytes = FormatStringParser.ComplieSimpleFormat(bytes, encoding);
			//改为过滤16进制，兼容不可见的文本字符
			this.m_bytes = HexStringUtils.FromHexString(bytes);
		} catch (Exception ex) {
			throw new TESException(MsgErr.Unpack.LoadFilterArgumentError, "bytes", ex);
		}
	}

	@Override
	public byte[] Clean(byte[] bytes) {
		BytePackage p = new BytePackage(bytes);
		int pos;

		// 循环判断字节流中是否存在要被过滤掉的字节
		while ((pos = p.IndexOf(this.m_bytes, 0)) != -1)
			//将找到的字节替换，用Delete会使长度发生变化
			p.Replace(pos, this.m_bytes.length, " ".getBytes());
			//p.Delete(pos, this.m_bytes.length);

		// 返回过滤后的字节
		return p.getBytes();
	}
}
