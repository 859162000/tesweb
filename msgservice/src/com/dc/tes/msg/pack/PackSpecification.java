package com.dc.tes.msg.pack;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.dc.tes.exception.MsgErr;
import com.dc.tes.exception.TESException;
import com.dc.tes.msg.util.ElementType;
import com.dc.tes.msg.util.ElementTypePackage;
import com.dc.tes.util.XmlUtils;

/**
 * 组包样式定义
 * 
 * @author lijic
 * 
 */
public class PackSpecification {
	/**
	 * 整篇报文的编码
	 */
	public final Charset encoding;
	/**
	 * 清除报文中可空的空域
	 */
	public final boolean clearEmptyOptional;
	/**
	 * 支持的报文层次类型
	 */
	final ElementTypePackage types;
	/**
	 * 组包样式单元列表
	 */
	final StyleUnit[] styles;
	/**
	 * 针对特殊位置指定的组包样式单元
	 */
	final Map<String, StyleUnit> specials;
	/**
	 * 组包脚本列表
	 */
	final PackScript[] scripts;
	/**
	 * 别名列表
	 */
	final Alias[] alias;

	/**
	 * 初始化一篇组包样式定义
	 */
	public PackSpecification(Document doc) {
		List<Element> lst;

		// 编码
		String _encoding = XmlUtils.SelectNodeText(doc, "//Format/Document/@encoding");
		if (_encoding == null || _encoding.length() == 0)
			throw new TESException(MsgErr.Pack.EncodingNotFound);
		try {
			this.encoding = Charset.forName(_encoding);
		} catch (Exception ex) {
			throw new TESException(MsgErr.Pack.EncodingNotSupported, _encoding);
		}
		
		//空域
		String _clearEmptyOptional = XmlUtils.SelectNodeText(doc, "//Format/Document/@clearEmptyOptional");
		clearEmptyOptional = Boolean.parseBoolean(_clearEmptyOptional);
		
		// 元素层次类型
		lst = XmlUtils.SelectNodes(doc, "//Format/ElementType");
		this.types = new ElementTypePackage(lst);

		// 组包样式单元
		StringBuffer xpath = new StringBuffer();
		for (ElementType type : this.types.types)
			xpath.append("|//Format/").append(type.name);
		lst = XmlUtils.SelectNodes(doc, xpath.substring(1).toString());
		ArrayList<StyleUnit> styles = new ArrayList<StyleUnit>();
		for (Element e : lst)
			styles.add(new StyleUnit(e, this.types, this.encoding));
		this.styles = styles.toArray(new StyleUnit[0]);

		// 针对特殊位置指定的组包样式单元
		lst = XmlUtils.SelectNodes(doc, "//Format/Special");
		Map<String, StyleUnit> specials = new LinkedHashMap<String, StyleUnit>();
		for (Element e : lst) {
			String target = e.getAttribute("target");
			if (target == null || target.length() == 0)
				throw new TESException(MsgErr.Pack.SpecialNoTarget);
			specials.put(target, new StyleUnit(e, this.types, this.encoding));
		}
		this.specials = specials;

		// 脚本
		lst = XmlUtils.SelectNodes(doc, "//Format/Script");
		List<PackScript> scripts = new ArrayList<PackScript>();
		for (Element e : lst)
			scripts.add(new PackScript(e));
		this.scripts = scripts.toArray(new PackScript[0]);

		// 别名
		lst = XmlUtils.SelectNodes(doc, "//Format/Alias");
		List<Alias> alias = new ArrayList<Alias>();
		for (Element e : lst)
			alias.add(new Alias(e, this.types, this.encoding));
		this.alias = alias.toArray(new Alias[0]);
	}
}
