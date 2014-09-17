package com.dc.tes.dom;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.SystemUtils;

/**
 * 数组
 * 
 * @author lijic
 * 
 */
public class MsgArray extends MsgContainer implements List<MsgItem> {
	protected ArrayList<MsgItem> m_items = new ArrayList<MsgItem>();

	@Override
	public void ForEach(ISimpleForEachVisitor visitor) {
		visitor.Visit(ISimpleForEachVisitor.ForEachSource.ArrayStart, this);

		for (MsgItem item : this.m_items.toArray(new MsgItem[0]))
			item.ForEach(visitor);

		visitor.Visit(ISimpleForEachVisitor.ForEachSource.ArrayEnd, this);
	}

	@Override
	public void ForEach(IForEachVisitor visitor) {
		visitor.ArrayStart(this);

		for (MsgItem item : this.m_items.toArray(new MsgItem[0]))
			item.ForEach(visitor);

		visitor.ArrayEnd(this);
	}

	@Override
	public String toString(boolean internal, int indent) {
		StringBuffer buffer = new StringBuffer();

		String pad = StringUtils.rightPad("", indent);

		buffer.append(pad).append("<!-- <array").append(MsgContainerUtils.PrintAttributesList(this, internal)).append("> -->").append(SystemUtils.LINE_SEPARATOR);
		for (MsgItem item : this.m_items)
			if (item != null)
				buffer.append(item.toString(internal, indent + 2));
		buffer.append(pad).append("<!-- </array> -->").append(SystemUtils.LINE_SEPARATOR);
		return buffer.toString();
	}

	@Override
	public void add(int index, MsgItem element) {
		this.verifyType(element);
		element.m_parent = this;
		this.m_items.add(index, element);
	}

	@Override
	public boolean add(MsgItem e) {
		this.verifyType(e);
		e.m_parent = this;
		return this.m_items.add(e);
	}

	@Override
	public boolean addAll(Collection<? extends MsgItem> c) {
		return this.addAll(0, c);
	}

	@Override
	public boolean addAll(int index, Collection<? extends MsgItem> c) {
		int i = 0;
		for (MsgItem item : c)
			if (++i > index)
				if (!this.add(item))
					return false;
		return true;
	}

	@Override
	public void clear() {
		this.m_items.clear();
	}

	@Override
	public boolean contains(Object o) {
		return this.m_items.contains(o);
	}

	@Override
	public boolean contains(String name) {
		try {
			return this.m_items.size() > Integer.parseInt(name);
		} catch (NumberFormatException ex) {
			return false;
		}
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return this.m_items.containsAll(c);
	}

	public void ensureCapacity(int minCapacity) {
		this.m_items.ensureCapacity(minCapacity);
	}

	@Override
	public MsgItem get(int index) {
		if (index >= this.m_items.size())
			return null;
		return this.m_items.get(index);
	}

	@Override
	public MsgItem get(String name) {
		int index = Integer.parseInt(String.valueOf(name));
		if (this.size() <= index)
			return null;
		else
			return this.m_items.get(index);
	}

	@Override
	public int indexOf(Object o) {
		if (o instanceof MsgItem)
			return this.m_items.indexOf(o);
		else
			throw new ClassCastException();
	}

	@Override
	public boolean isEmpty() {
		return this.m_items.isEmpty();
	}

	@Override
	public Iterator<MsgItem> iterator() {
		return this.m_items.iterator();
	}

	@Override
	public int lastIndexOf(Object o) {
		return this.m_items.lastIndexOf(o);
	}

	@Override
	public ListIterator<MsgItem> listIterator() {
		return this.m_items.listIterator();
	}

	@Override
	public ListIterator<MsgItem> listIterator(int index) {
		return this.m_items.listIterator(index);
	}

	@Override
	public MsgItem remove(int index) {
		return this.m_items.remove(index);
	}

	@Override
	public boolean remove(Object o) {
		if (o instanceof MsgItem)
			return this.m_items.remove(o);

		if (o instanceof Integer) {
			this.m_items.remove(((Integer) o).intValue());
			return true;
		}
		throw new ClassCastException("Cannot cast from " + o.getClass().getName() + " to int or MsgItem.");
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
	public boolean removeAll(Collection<?> c) {
		return this.m_items.removeAll(c);
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		return this.m_items.retainAll(c);
	}

	@Override
	public MsgItem set(int index, MsgItem element) {
		this.verifyType(element);

		if (this.m_items.size() <= index) {
			while (this.m_items.size() < index)
				this.m_items.add(null);
			this.m_items.add(index, element);
			return element;
		}
		element.m_parent = this;
		return this.m_items.set(index, element);
	}

	@Override
	public MsgItem put(String name, MsgItem item) {
		item.m_parent = this;
		return this.set(Integer.parseInt(name), item);
	}

	@Override
	public int size() {
		return this.m_items.size();
	}

	@Override
	public List<MsgItem> subList(int fromIndex, int toIndex) {
		return this.m_items.subList(fromIndex, toIndex);
	}

	@Override
	public Object[] toArray() {
		return this.m_items.toArray();
	}

	@Override
	public <T> T[] toArray(T[] a) {
		return this.m_items.toArray(a);
	}

	public void trimToSize() {
		this.m_items.trimToSize();
	}

	private void verifyType(MsgItem element) {
		if (this.m_items.size() != 0)
			for (int i = 0; i < this.m_items.size(); i++)
				if (this.m_items.get(i) != null && this.m_items.get(i).getClass() != element.getClass())
					throw new ClassCastException(String.format("Trying to put a %s into a Array containing %s", element.getClass().getSimpleName(), this.m_items.get(0).getClass().getSimpleName()));
	}

	@Override
	public MsgArray Copy() {
		MsgArray array = new MsgArray();
		array.setAttributes(this.m_atts);

		int i = 0;
		for (MsgItem item : this.m_items) {
			MsgItem itemp = item.Copy();
			itemp.m_parent = array;
			array.put(i + "", itemp);
			i++;
		}
		return array;
	}
}
