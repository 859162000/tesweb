package com.dc.tes.txcode;

import java.nio.charset.Charset;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.dc.tes.component.BaseComponent;
import com.dc.tes.component.tag.ComponentClass;
import com.dc.tes.component.tag.ComponentType;

/**
 * 基于正则表达式的交易码识别组件
 * 
 * @author lijic
 * 
 */
@ComponentClass(type = ComponentType.TXCode)
public class RegexRecogniser extends BaseComponent<RegexRecogniserConfigObject> implements ITranCodeRecogniser {
	/**
	 * 用于识别交易的正则表达式
	 */
	private Pattern m_regex;
	private Charset m_encoding;

	@Override
	protected void Initialize() throws Exception {
		// 预先编译正则表达式 提高性能
		this.m_regex = Pattern.compile(this.m_config.regex);
		// 预先找出使用的编码 提高性能 同时便于尽早发现错误
		this.m_encoding = Charset.forName(this.m_config.encoding);
	}

	@Override
	public String Recognise(byte[] bytes) throws Exception {
		// 先使用指定的编码将字节流转换为字符串
		String msg = new String(bytes, this.m_encoding);

		// 使用正则表达式进行匹配
		Matcher matcher = this.m_regex.matcher(msg);

		// 找到第index个匹配项
		int pos = 0;
		for (int i = 0; i <= this.m_config.index; i++)
			if (matcher.find(pos))
				pos = matcher.end();

		// 返回第group个抓取到的组
		return matcher.group(this.m_config.group);
	}
}
