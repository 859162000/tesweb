package com.dc.tes.txcode;

import java.nio.charset.Charset;

import com.dc.tes.component.BaseComponent;
import com.dc.tes.component.tag.ComponentClass;
import com.dc.tes.component.tag.ComponentType;
import com.dc.tes.exception.TXCodeRegoniseFailException;
import com.dc.tes.msg.util.FormatStringParser;
import com.dc.tes.util.type.BytePackage;

/**
 * 基于左右边界的交易码识别组件
 * 
 * @author lijic
 * 
 */
@ComponentClass(type = ComponentType.TXCode)
public class BorderRecogniser extends BaseComponent<BorderRecogniserConfigObject> implements ITranCodeRecogniser {
	private Charset m_encoding;

	@Override
	protected void Initialize() throws Exception {
		this.m_encoding = Charset.forName(this.m_config.encoding);
	}

	@Override
	public String Recognise(byte[] bytes) throws Exception {
		// 将左右边界按照简单格式字符串的格式进行处理
		byte[] left = FormatStringParser.ComplieSimpleFormat(this.m_config.left, this.m_encoding);
		byte[] right = FormatStringParser.ComplieSimpleFormat(this.m_config.right, this.m_encoding);

		// 查找左边界 考虑了index参数
		BytePackage p = new BytePackage(bytes);
		int pos = 0;
		for (int i = 0; i <= this.m_config.index; i++)
			if ((pos = p.IndexOf(left, pos)) == -1)
				throw new TXCodeRegoniseFailException("在输入字节流中没有找到指定的左边界：" + this.m_config.left + " [index=" + this.m_config.index + "]");

		// 查找右边界
		pos += left.length;
		int pos2 = p.IndexOf(right, pos);
		if (pos2 == -1)
			throw new TXCodeRegoniseFailException("在输入字节流中没有找到指定的右边界：" + this.m_config.right + " [index=" + this.m_config.index + "]");

		// 将左右边界之间的字节作为交易码返回
		return new String(p.getBytes(pos, pos2 - pos), this.m_config.encoding);
	}
}
