package com.dc.tes.msg.unpack;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.dc.tes.exception.MsgErr;
import com.dc.tes.exception.TESException;
import com.dc.tes.msg.unpack.filter.Filter;
import com.dc.tes.msg.util.ElementType;
import com.dc.tes.msg.util.ElementTypePackage;
import com.dc.tes.util.XmlUtils;

/**
 * 拆包规则定义
 * 
 * @author lijic
 * 
 */
public class UnpackSpecification {
	final boolean discardStru;

	/**
	 * 整篇报文的编码
	 */
	public final Charset encoding;
	/**
	 * 过滤器列表
	 */
	final Filter[] filters;
	/**
	 * 支持的报文层次类型
	 */
	final ElementTypePackage types;
	/**
	 * 应用于根节点的拆包规则单元
	 */
	final RuleUnit ruleRoot;
	/**
	 * 拆包规则单元列表
	 */
	final RuleUnit[] rules;
	/**
	 * 词法列表
	 */
	final Map<String, Lex> lexes;
	/**
	 * 拆包脚本列表
	 */
	final UnpackScript[] scripts;

	/**
	 * 初始化一篇拆包规则定义
	 */
	public UnpackSpecification(Document doc) {
		List<Element> lst;

		this.discardStru = "true".equalsIgnoreCase(XmlUtils.SelectNodeText(doc, "//Format/Property/discardStru"));

		// 编码
		String _encoding = XmlUtils.SelectNodeText(doc, "//Format/Document/@encoding");
		if (_encoding == null || _encoding.length() == 0)
			throw new TESException(MsgErr.Unpack.EncodingNotFound);
		try {
			this.encoding = Charset.forName(_encoding);
		} catch (Exception ex) {
			throw new TESException(MsgErr.Unpack.EncodingNotSupported, _encoding);
		}

		// 过滤器列表
		lst = XmlUtils.SelectNodes(doc, "//Format/Filter");
		ArrayList<Filter> filters = new ArrayList<Filter>();
		for (Element e : lst)
			filters.add(Filter.CreateFilter(e, encoding));
		this.filters = filters.toArray(new Filter[0]);

		// 元素层次类型
		lst = XmlUtils.SelectNodes(doc, "//Format/ElementType");
		this.types = new ElementTypePackage(lst);

		// 应用于根元素的拆包单元		
		this.ruleRoot = new RuleUnit((Element) XmlUtils.SelectNode(doc, "//Format/Document"), this.types, this.encoding);

		// 拆包规则单元
		StringBuffer xpath = new StringBuffer();
		for (ElementType type : this.types.types)
			if (!type.name.equals("Document"))
				xpath.append("|//Format/").append(type.name);
		lst = XmlUtils.SelectNodes(doc, xpath.substring(1).toString());
		ArrayList<RuleUnit> styles = new ArrayList<RuleUnit>();
		for (Element e : lst)
			styles.add(new RuleUnit(e, this.types, this.encoding));
		this.rules = styles.toArray(new RuleUnit[0]);

		// 读取词法配置
		lst = XmlUtils.SelectNodes(doc, "//Format/Lex");
		this.lexes = new HashMap<String, Lex>();
		for (Element e : lst)
			this.lexes.put(e.getAttribute("target"), new Lex(XmlUtils.getNodeText(e), this.encoding));

		// 读取脚本定义
		lst = XmlUtils.SelectNodes(doc, "//Format/Script");
		ArrayList<UnpackScript> scripts = new ArrayList<UnpackScript>();
		for (Element e : lst)
			scripts.add(new UnpackScript(e));
		this.scripts = scripts.toArray(new UnpackScript[0]);
	}
}
