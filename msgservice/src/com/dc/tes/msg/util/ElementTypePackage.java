package com.dc.tes.msg.util;

import java.util.Arrays;
import java.util.List;

import org.w3c.dom.Element;

import com.dc.tes.dom.MsgArray;
import com.dc.tes.dom.MsgContainer;
import com.dc.tes.dom.MsgDocument;
import com.dc.tes.dom.MsgField;
import com.dc.tes.dom.MsgItem;
import com.dc.tes.dom.MsgStruct;

/**
 * 报文元素层次类型包
 * 
 * @author lijic
 * 
 */
public class ElementTypePackage {
	/**
	 * 该包中保存的所有元素层次类型
	 */
	public final ElementType[] types;

	@SuppressWarnings("unchecked")
	public ElementTypePackage(List<Element> lst) {
		this.types = new ElementType[] {
				new ElementType("Document", MsgDocument.class),
				new ElementType("Field", MsgField.class, MsgDocument.class),
				new ElementType("FieldInStru", MsgField.class, MsgStruct.class),
				new ElementType("FieldInArray", MsgField.class, MsgArray.class),
				new ElementType("FieldInStruArray", MsgField.class, MsgStruct.class, MsgArray.class),
				new ElementType("Stru", MsgStruct.class),
				new ElementType("StruInArray", MsgStruct.class, MsgArray.class),
				new ElementType("Array", MsgArray.class),
				new ElementType("ArrayOfStru", MsgArray.class), };
	}

	/**
	 * 检查一个给定的元素对应于包中的哪个元素层次类型
	 * 
	 * @param item
	 *            元素
	 * @param container
	 *            元素的容器
	 * @return 返回包中与给定元素相匹配的元素层次类型对象 如果包中没有适合的层次类型 则返回null
	 */
	public ElementType CheckType(MsgItem item, MsgContainer container) {
		ElementType t = null;
		int len = -1;
		for (ElementType type : this.types)
			if (type.Check(item, container))
				if (type.parents.length > len) {
					t = type;
					len = type.parents.length;
				}
		return t;
	}

	@Override
	public String toString() {
		return Arrays.toString(this.types);
	}
}
