package com.dc.tes.msg.pack;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.w3c.dom.Element;

import com.dc.tes.exception.MsgErr;
import com.dc.tes.exception.TESException;
import com.dc.tes.msg.util.ElementType;
import com.dc.tes.msg.util.ElementTypePackage;
import com.dc.tes.msg.util.FormatStringParser;
import com.dc.tes.msg.util.FormatStringParser.FormatFragment;
import com.dc.tes.util.RuntimeUtils;
import com.dc.tes.util.XmlUtils;

/**
 * 组包样式单元
 * 
 * @author lijic
 * 
 */
public class StyleUnit {
	/**
	 * 段列表
	 */
	final Segment[] segments;
	/**
	 * 此组包样式单元对应的报文层次类型
	 */
	final ElementType type;
	/**
	 * 用途
	 */
	final String usage;

	/**
	 * 初始化一个组包样式单元
	 * 
	 * @param e
	 *            xml节点
	 * @param types
	 *            当前支持的所有报文层次类型
	 * @param encoding
	 *            报文默认编码
	 */
	StyleUnit(Element e, ElementTypePackage types, Charset encoding) {
		// 解析当前组包样式单元对应的报文层次类型
		ElementType type = null;
		for (ElementType t : types.types)
			if (t.name.equals(e.getNodeName()))
				type = t;
		this.type = type;

		// 解析当前组包样式单元的参数
		List<Param> params = new ArrayList<Param>();
		for (String expr : XmlUtils.SelectNodeListText(e, "param"))
			params.add(new Param(expr));

		// 解析当前组包样式单元的格式字符串	
		List<Segment> segments = new ArrayList<Segment>();
		int paramId = 0;
		String format = XmlUtils.SelectNodeText(e, "format");

		// 遍历FormatStringParser的输出结果 将FormatSegment列表转换为段列表
		for (FormatFragment fragment : FormatStringParser.Parse(format, RuntimeUtils.utf8))
			if (fragment.isParamFragment) {
				if (params.size() <= paramId)
					throw new TESException(MsgErr.Pack.NoEnoughParam, "format: " + format + " elementType: " + this.type);

				segments.add(new ParamSegment(fragment, params.get(paramId++), encoding));
			} else
				segments.add(new TextSegment(fragment));
		this.segments = segments.toArray(new Segment[0]);

		// 解析当前组包样式单元的用途
		this.usage = e.getAttribute("usage");
	}

	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append(this.type == null ? "_Special_" : this.type.toString());
		buffer.append(" format: " + Arrays.toString(this.segments));
		return buffer.toString();
	}

	public String getUsage() {
		return usage;
	}
}
