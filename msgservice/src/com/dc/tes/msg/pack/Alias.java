package com.dc.tes.msg.pack;

import java.nio.charset.Charset;
import java.util.ArrayList;

import org.w3c.dom.Element;

import com.dc.tes.msg.util.ElementType;
import com.dc.tes.msg.util.ElementTypePackage;
import com.dc.tes.msg.util.FormatStringParser;
import com.dc.tes.util.XmlUtils;

/**
 * 别名 用于表示报文数据中某个元素的某个属性的值与另一个值之间的映射<br />
 * 例如表示某个元素type="int"这个属性的值'int'与报文所需的'INTEGER'之间的映射
 * 
 * @author lijic
 * 
 */
class Alias {
	final ElementType[] usage;
	final String pattern;
	final byte[] value;

	/**
	 * 初始化一个别名
	 * 
	 * @param e
	 * @param types
	 *            .types
	 * @param encoding
	 */
	Alias(Element e, ElementTypePackage types, Charset encoding) {
		String usage = e.getAttribute("usage");
		if (usage.length() == 0)
			this.usage = types.types;
		else {
			ArrayList<ElementType> usages = new ArrayList<ElementType>();
			for (String _usage : usage.split("\\|"))
				for (ElementType type : types.types)
					if (type.name.equals(_usage))
						usages.add(type);
			this.usage = usages.toArray(new ElementType[0]);
		}

		this.pattern = e.getAttribute("pattern");
		this.value = FormatStringParser.ComplieSimpleFormat(XmlUtils.getNodeText(e), encoding);
	}
}
