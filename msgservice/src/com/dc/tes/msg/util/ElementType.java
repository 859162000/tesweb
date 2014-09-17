package com.dc.tes.msg.util;

import com.dc.tes.dom.MsgContainer;
import com.dc.tes.dom.MsgItem;

/**
 * 元素层次类型 表示报文元素在整篇报文中的层次类型，包括元素本身的类型、元素的父类型链、元素的子类型链等
 * 
 * @author lijic
 * 
 */
public class ElementType {
	/**
	 * 名称
	 */
	public final String name;
	/**
	 * 元素本身的类型
	 */
	public final Class<? extends MsgItem> clazz;
	/**
	 * 元素的父类型链
	 */
	public final Class<? extends MsgContainer>[] parents;

	/**
	 * 初始化报文元素层次类型
	 * 
	 * @param name
	 *            该层次类型的名称
	 * @param type
	 *            元素本身的类型
	 * @param parents
	 *            元素的父类型链
	 */
	public ElementType(String name, Class<? extends MsgItem> type, Class<? extends MsgContainer>... parents) {
		this.name = name;
		this.clazz = type;
		this.parents = parents;
	}

	/**
	 * 判断给定的报文元素是否符合该元素层次类型
	 * 
	 * @param item
	 *            被判断的报文元素
	 * @param container
	 *            被判断的报文元素的父节点
	 * @return 该元素是否符合该元素层次类型
	 */
	public boolean Check(MsgItem item, MsgContainer container) {
		// 判断item本身的类型
		if (item.getClass() != this.clazz)
			return false;

		// 判断parent链的类型
		for (Class<? extends MsgContainer> parent : this.parents)
			try {
				if (parent.isAssignableFrom(container.getClass()))
					container = container.parent();
				else
					return false;
			} catch (NullPointerException ex) {
				return false;
			}

		return true;
	}
	
	@Override
	public String toString() {
		return this.name;
	}
}
