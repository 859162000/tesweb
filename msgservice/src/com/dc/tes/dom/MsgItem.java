package com.dc.tes.dom;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import com.dc.tes.dom.util.DPathUtils;
import com.dc.tes.exception.MsgErr;
import com.dc.tes.exception.TESException;
import com.dc.tes.msg.util.Value;

/**
 * 报文结构层次的基类
 * 
 * @author lijic
 * 
 */
public abstract class MsgItem {
	/**
	 * 当前报文元素的属性列表
	 */
	protected LinkedHashMap<String, Value> m_atts = new LinkedHashMap<String, Value>();
	/**
	 * 当前元素的父节点
	 */
	protected MsgContainer m_parent;

	// 遍历

	/**
	 * 遍历自身及自身的所有子节点 采用ISimpleForEachVisitor接口
	 * 
	 * @param action
	 *            当遍历出一个节点时要进行的操作
	 */
	public abstract void ForEach(ISimpleForEachVisitor visitor);

	/**
	 * 遍历自身及自身的所有子节点
	 * 
	 * @param visitor
	 *            当遍历出一个节点时要进行的操作
	 */
	public abstract void ForEach(IForEachVisitor visitor);

	// 属性

	/**
	 * 获取指定的属性值(不区分大小写)
	 * 
	 * @param name
	 *            属性的名称
	 * @return 如果存在这样的一个属性，则返回它的值，否则返回Value.empty
	 */
	public Value getAttribute(String name) {
		for (String n : this.m_atts.keySet())
			if (n.equalsIgnoreCase(name))
				return this.m_atts.get(n);

		return Value.empty;
	}

	/**
	 * 获取指定的属性值
	 * 
	 * @param name
	 *            属性的名称
	 * @param ignoreCase
	 *            是否忽略大小写
	 * @return 如果存在这样的一个属性，则返回它的值，否则返回Value.empty
	 */
	public Value getAttribute(String name, boolean ignoreCase) {
		if (ignoreCase)
			return this.getAttribute(name);
		else
			return this.m_atts.containsKey(name) ? this.m_atts.get(name) : Value.empty;
	}

	/**
	 * 设置元素的属性
	 * 
	 * @param name
	 *            属性名称
	 * @param value
	 *            属性的值
	 */
	public void setAttribute(String name, int value) {
		this.m_atts.put(name, new Value(value));
	}

	/**
	 * 设置元素的属性
	 * 
	 * @param name
	 *            属性名称
	 * @param value
	 *            属性的值
	 */
	public void setAttribute(String name, String value) {
		if (value == null)
			throw new TESException(MsgErr.Dom.NullAttribute, "attrib: " + name + " item: " + this);
		this.m_atts.put(name, new Value(value));
	}

	/**
	 * 设置元素的属性
	 * 
	 * @param name
	 *            属性名称
	 * @param value
	 *            属性的值
	 */
	public void setAttribute(String name, boolean value) {
		this.m_atts.put(name, new Value(value));
	}

	/**
	 * 设置元素的属性
	 * 
	 * @param name
	 *            属性名称
	 * @param value
	 *            属性的值
	 */
	public void setAttribute(String name, byte[] value) {
		if (value == null)
			throw new TESException(MsgErr.Dom.NullAttribute, "attrib: " + name + " item: " + this);
		this.m_atts.put(name, new Value(value));
	}

	/**
	 * 设置元素的属性
	 * 
	 * @param name
	 *            属性名称
	 * @param value
	 *            属性的值
	 */
	public void setAttribute(String name, Value value) {
		if (value == null)
			throw new TESException(MsgErr.Dom.NullAttribute, "attrib: " + name + " item: " + this);
		this.m_atts.put(name, new Value(value));
	}

	/**
	 * 获取此元素的所有属性
	 * 
	 * @return 返回此元素的所有属性的列表 对该列表的修改将直接影响节点本身
	 */
	public Map<String, Value> getAttributes() {
		return this.m_atts;
	}

	/**
	 * 设置元素的属性
	 * 
	 * @param attribs
	 *            属性列表 新属性将被附加到元素上
	 */
	public void setAttributes(Map<String, Value> attribs) {
		if (attribs == null)
			throw new TESException(MsgErr.Dom.NullAttributes, this.toString());

		for (String key : attribs.keySet())
			this.m_atts.put(key, new Value(attribs.get(key)));
	}

	// 查询

	/**
	 * 选择一个节点
	 * 
	 * @param dpath
	 *            DPath
	 * @return 被选中的节点。如果没有这样的一个节点，则返回null
	 */
	public MsgItem SelectSingleNode(String dpath) {
		return DPathUtils.SelectSingleNode(this, dpath);
	}

	/**
	 * 选择一批节点
	 * 
	 * @param dpath
	 *            DPath
	 * @return 由被选中的节点组成的数组。如果没有这样的节点，则返回一个空数组
	 */
	public MsgItem[] SelectNodes(String dpath) {
		return DPathUtils.SelectNodes(this, dpath);
	}

	/**
	 * 选择一个域
	 * 
	 * @param dpath
	 *            DPath
	 * @return 被选中的域。如果没有这样的一个域，则返回null
	 */
	public MsgField SelectSingleField(String dpath) {
		MsgItem item = DPathUtils.SelectSingleNode(this, dpath);
		if (item != null && item instanceof MsgField)
			return (MsgField) item;
		else
			return null;
	}

	/**
	 * 选择一批节点
	 * 
	 * @param dpath
	 *            DPath
	 * @return 由被选中的域的数组。如果没有这样的域，则返回一个空数组
	 */
	public MsgField[] SelectFields(String dpath) {
		ArrayList<MsgField> fields = new ArrayList<MsgField>();
		for (MsgItem item : DPathUtils.SelectNodes(this, dpath))
			if (item instanceof MsgField)
				fields.add((MsgField) item);
		return fields.toArray(new MsgField[0]);
	}

	// 层级
	/**
	 * 获取此元素的父元素
	 * 
	 * @return 返回此元素的父元素
	 */
	public MsgContainer parent() {
		return this.m_parent;
	}

	/**
	 * 获取此元素的报文根元素
	 * 
	 * @return 返回此元素的报文根元素
	 */
	public MsgDocument root() {
		MsgItem item = this;
		while (item != item.parent())
			item = item.parent();
		return (MsgDocument) item;
	}

	/**
	 * 获取当前节点相对于所属文档的DPath
	 * 
	 * @return 返回当前节点相对于所属文档的DPath
	 */
	public String dpath() {
		MsgItem item = this;
		StringBuffer buffer = new StringBuffer();

		while (item != item.parent()) {
			if (buffer.length() != 0)
				buffer.insert(0, '.');

			if (item.parent() instanceof MsgArray) {
				for (int i = 0; i < ((MsgArray) item.parent()).size(); i++)
					if (((MsgArray) item.parent()).get(i) == item)
						buffer.insert(0, String.valueOf(i));
			} else
				buffer.insert(0, item.name().trim());
			item = item.parent();
		}

		return buffer.toString();
	}

	// 其它

	/**
	 * 获取当前节点的名称 该名称对应于name属性的值
	 * 
	 * @return 返回当前节点的名称
	 */
	public String name() {
		if (this.getAttribute("name") == null)
			throw new TESException(MsgErr.Dom.NoNameAttribute);
		return this.getAttribute("name").str;
	}

	/**
	 * 复制一份当前报文元素的副本
	 * 
	 * @return 返回一份当前报文元素的副本
	 */
	public abstract MsgItem Copy();

	/**
	 * 将当前报文元素以字符串形式展现
	 * 
	 * @return 报文元素的字符串表现形式
	 */
	@Override
	public String toString() {
		return this.toString(false, 0);
	}

	/**
	 * 将当前报文元素以字符串形式展现
	 * 
	 * @param internal
	 *            是否将标记为"内部使用"的属性打印出来
	 * @param indent
	 *            缩进
	 * @return 报文元素的字符串表现形式
	 */
	public abstract String toString(boolean internal, int indent);
	
	/**
	 * 8583遍历域
	 * @return MsgDocument
	 */
	public MsgDocument getDocument()
	{
		MsgItem item;
		for (item = this; item != item.parent(); item = item.parent());
		return (MsgDocument)item;
	}
}
