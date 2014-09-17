package com.dc.tes.msg.unpack.filter;

import java.nio.charset.Charset;

import org.w3c.dom.Element;

import com.dc.tes.exception.MsgErr;
import com.dc.tes.exception.TESException;
import com.dc.tes.msg.util.FormatStringParser;
import com.dc.tes.util.XmlUtils;
import com.dc.tes.util.type.BytePackage;

/**
 * 以字节边界为依据的过滤器
 * 
 * @author lijic
 * 
 */
class BorderFilter extends Filter {
	/**
	 * 左边界
	 */
	private final byte[] m_left;
	/**
	 * 右边界
	 */
	private final byte[] m_right;
	/**
	 * 是否连左边界一起砍掉
	 */
	private final boolean m_nestLeft;
	/**
	 * 是否连右边界一起砍掉
	 */
	private final boolean m_nestRight;

	/**
	 * 初始化一个以字节边界为依据的过滤器
	 */
	BorderFilter(Element e, Charset encoding) {
		// 左边界
		String left = XmlUtils.SelectNodeText(e, "left");
		if (left == null)
			throw new TESException(MsgErr.Unpack.FilterArgumentNotFound, "left");
		try {
			this.m_left = FormatStringParser.ComplieSimpleFormat(left, encoding);
		} catch (Exception ex) {
			throw new TESException(MsgErr.Unpack.LoadFilterArgumentError, "left", ex);
		}

		// 右边界
		String right = XmlUtils.SelectNodeText(e, "right");
		if (left == null)
			throw new TESException(MsgErr.Unpack.FilterArgumentNotFound, "right");
		try {
			this.m_right = FormatStringParser.ComplieSimpleFormat(right, encoding);
		} catch (Exception ex) {
			throw new TESException(MsgErr.Unpack.LoadFilterArgumentError, "right", ex);
		}

		// 是否连左边界一起砍掉
		String nestLeft = XmlUtils.SelectNodeText(e, "nestLeft");
		if (nestLeft == null)
			throw new TESException(MsgErr.Unpack.FilterArgumentNotFound, "nestLeft");
		this.m_nestLeft = nestLeft.equalsIgnoreCase("true");

		// 是否连右边界一起砍掉
		String nestRight = XmlUtils.SelectNodeText(e, "nestRight");
		if (nestRight == null)
			throw new TESException(MsgErr.Unpack.FilterArgumentNotFound, "nestRight");
		this.m_nestRight = nestRight.equalsIgnoreCase("true");
	}

	@Override
	public byte[] Clean(byte[] bytes) {
		BytePackage p = new BytePackage(bytes);

		int start = 0, end;
		// 反复循环，直到字节流中从start开始再也找不到与左边界匹配的字节
		while ((start = p.IndexOf(m_left, 0)) != -1) {
			// 如果字节流从[start+左边界的长度]开始可以找到与右边界匹配的字节 则令end等于右边界所处的位置 删掉这部分字节
			if ((end = p.IndexOf(m_right, start + this.m_left.length)) != -1) {
				// 如果未指定连左边界一起删掉 则将start右移左边界的长度 使start~end之间不包含左边界
				if (!this.m_nestLeft)
					start += m_left.length;

				// 如果指定了连右边界一起删掉 则将end右移右边界的长度 使start~end之间包含右边界
				if (this.m_nestRight)
					end += m_right.length;

				// 执行删除操作
				p.Delete(start, end - start);
			} else {
				// 只找到了左边界 未找到右边界
				break;
			}
		}

		// 返回删除后的字节流
		return p.getBytes();
	}
}
