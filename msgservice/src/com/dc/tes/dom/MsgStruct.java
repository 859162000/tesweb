package com.dc.tes.dom;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.SystemUtils;

/**
 * 结构
 * 
 * @author lijic
 * 
 */
public class MsgStruct extends MsgContainer implements Map<String, MsgItem> {
	class SubItem {
		public String name;

		public MsgItem item;

		public SubItem(String name, MsgItem item) {
			super();
			this.name = name;
			this.item = item;
		}

		@Override
		public String toString() {
			return this.name + "->" + this.item;
		}
	}

	/**
	 * 结构中的各个子元素
	 */
	protected ArrayList<SubItem> m_items = new ArrayList<SubItem>();

	@Override
	public void ForEach(ISimpleForEachVisitor visitor) {
		if (!(this instanceof MsgDocument))
			visitor.Visit(ISimpleForEachVisitor.ForEachSource.StruStart, this);

		for (SubItem item : this.m_items.toArray(new SubItem[0]))
			item.item.ForEach(visitor);

		if (!(this instanceof MsgDocument))
			visitor.Visit(ISimpleForEachVisitor.ForEachSource.StruEnd, this);
	}

	@Override
	public void ForEach(IForEachVisitor visitor) {
		if (!(this instanceof MsgDocument))
			visitor.StruStart(this);

		for (SubItem item : this.m_items.toArray(new SubItem[0]))
			item.item.ForEach(visitor);

		if (!(this instanceof MsgDocument))
			visitor.StruEnd(this);
	}

	@Override
	public String toString(final boolean internal, int indent) {
		final StringBuffer buffer = new StringBuffer();

		String pad = StringUtils.rightPad("", indent);

		boolean printSelf = !(this instanceof MsgDocument);

		if (printSelf)
			buffer.append(pad).append("<struct").append(MsgContainerUtils.PrintAttributesList(this, internal)).append(">").append(SystemUtils.LINE_SEPARATOR);

		for (MsgItem item : this)
			buffer.append(item.toString(internal, indent + 2));

		if (printSelf)
			buffer.append(pad).append("</struct>").append(SystemUtils.LINE_SEPARATOR);
		return buffer.toString();
	}

	@Override
	public void clear() {
		this.m_items.clear();
	}

	@Override
	public boolean containsKey(Object key) {
		for (SubItem item : this.m_items)
			if (item.name.equals(key))
				return true;
		return false;
	}

	@Override
	public boolean contains(String name) {
		return this.containsKey(name);
	}

	@Override
	public boolean containsValue(Object value) {
		for (SubItem item : this.m_items)
			if (item.item.equals(value))
				return true;
		return false;
	}

	@Override
	public Set<Map.Entry<String, MsgItem>> entrySet() {
		HashMap<String, MsgItem> map = new HashMap<String, MsgItem>();
		for (SubItem item : this.m_items)
			map.put(item.name, item.item);
		return map.entrySet();
	}

	@Override
	public MsgItem get(Object key) {
		if (key instanceof Integer)
			return this.get(key);
		if (key instanceof String)
			return this.get((String) key);

		throw new ClassCastException();
	}

	@Override
	public MsgItem get(String key) {
		for (SubItem item : this.m_items)
			if (item.name.equals(key))
				return item.item;
			else if (item.item instanceof MsgStruct) {
				MsgItem subitem = ((MsgStruct)item.item).get(key);
				if(subitem != null)
					return subitem;
			}
		return null;
	}

	@Override
	public MsgItem get(int index) {
		if (index >= this.m_items.size())
			return null;
		return this.m_items.get(index).item;
	}

	@Override
	public boolean isEmpty() {
		return this.m_items.isEmpty();
	}

	@Override
	public int indexOf(Object o) {
		if (o instanceof MsgItem) {
			for (int i = 0; i < this.m_items.size(); i++)
				if (this.get(i) == o)
					return i;
			return -1;
		} else
			throw new ClassCastException();
	}

	@Override
	public Set<String> keySet() {
		HashMap<String, MsgItem> map = new HashMap<String, MsgItem>();
		for (SubItem item : this.m_items)
			map.put(item.name, item.item);
		return map.keySet();
	}

	@Override
	public MsgItem put(String key, MsgItem value) {
		value.m_parent = this;

		for (SubItem item : this.m_items)
			if (item.name.equals(key)) {
				MsgItem prevItem = item.item;
				item.item = value;
				return prevItem;
			}

		this.m_items.add(new SubItem(key, value));
		return null;
	}

	/**
	 * 在指定位置增加某个元素,用于动态修改响应报文模板时重复域的处理,免去模板需要设置为数组
	 * @param index 
	 * @param key
	 * @param value
	 */
	public void add(int index, String key, MsgItem value) {
		
		this.m_items.add(index, new SubItem(key, value));
		
	}
	
	@Override
	public void putAll(Map<? extends String, ? extends MsgItem> m) {
		for (Map.Entry<? extends String, ? extends MsgItem> item : m.entrySet())
			this.put(item.getKey(), item.getValue());
	}

	@Override
	public MsgItem remove(Object key) {
		if (key instanceof MsgItem) {
			for (int i = 0; i < this.m_items.size(); i++)
				if (this.m_items.get(i).item == key) {
					MsgItem prevItem = this.m_items.get(i).item;
					this.m_items.remove(i);
					return prevItem;
				}
			return null;
		}

		if (key instanceof String) {
			for (int i = 0; i < this.m_items.size(); i++)
				if (this.m_items.get(i).name.equals(key)) {
					MsgItem prevItem = this.m_items.get(i).item;
					this.m_items.remove(i);
					return prevItem;
				}
			return null;
		}

		if (key instanceof Integer) {
			MsgItem prevItem = this.m_items.get((Integer) key).item;
			this.m_items.remove((int) (Integer) key);
			return prevItem;
		}

		throw new ClassCastException(key.getClass().getName());
	}

	@Override
	public void removeAt(int index) {
		this.remove(index);
	}

	@Override
	public void removeItem(MsgItem item) {
		this.remove(item);
	}

	@Override
	public int size() {
		return this.m_items.size();
	}

	@Override
	public Collection<MsgItem> values() {
		HashMap<String, MsgItem> map = new HashMap<String, MsgItem>();
		for (SubItem item : this.m_items)
			map.put(item.name, item.item);
		return map.values();
	}

	@Override
	public Iterator<MsgItem> iterator() {
		ArrayList<MsgItem> items = new ArrayList<MsgItem>();
		for (SubItem item : this.m_items)
			items.add(item.item);
		return items.iterator();
	}

	@Override
	public MsgStruct Copy() {
		MsgStruct stru = new MsgStruct();
		stru.setAttributes(this.m_atts);

		for (SubItem subitem : this.m_items) {
			MsgItem mitem = subitem.item.Copy();
			mitem.m_parent = stru;
			stru.put(subitem.name, mitem);
		}
		return stru;
	}
	
	/**
	 * 用于拆包过程中查找指定名称的元素,这里通过SubItem.item的name属性查找,因为拆包过程中Subitem的name是数字
	 * @param name 
	 * @return
	 */	
	public MsgItem find(String name) {
		for (SubItem item : this.m_items) {
			if(item.item.name().equals(name))
				return item.item;
		}
		return null;
	}
}
