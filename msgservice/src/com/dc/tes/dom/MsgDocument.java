package com.dc.tes.dom;

import org.apache.commons.lang.SystemUtils;

/**
 * 文档
 * 
 * @author lijic
 * 
 */
public class MsgDocument extends MsgStruct {

	public MsgDocument() {
		super();
		this.m_parent = this;
	}

	/**
	 * 遍历自身及自身的所有子节点
	 * 
	 * @param visitor
	 *            当遍历出一个节点时要进行的操作
	 */
	@Override
	public void ForEach(ISimpleForEachVisitor visitor) {
		visitor.Visit(ISimpleForEachVisitor.ForEachSource.DocStart, this);

		super.ForEach(visitor);

		visitor.Visit(ISimpleForEachVisitor.ForEachSource.DocEnd, this);
	}

	@Override
	public void ForEach(IForEachVisitor visitor) {
		visitor.DocStart(this);

		super.ForEach(visitor);

		visitor.DocEnd(this);
	}

	@Override
	public MsgDocument Copy() {
		MsgDocument doc = new MsgDocument();
		doc.setAttributes(this.m_atts);

		for (SubItem subitem : this.m_items) {
			MsgItem mitem = subitem.item.Copy();
			mitem.m_parent = doc;
			doc.put(subitem.name, mitem);
		}
		return doc;
	}

	@Override
	public MsgContainer parent() {
		return this;
	}

	@Override
	public String toString(boolean internal, int indent) {
		StringBuffer buffer = new StringBuffer();
		buffer.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>").append(SystemUtils.LINE_SEPARATOR);
		buffer.append("<msg>").append(SystemUtils.LINE_SEPARATOR);
		buffer.append(super.toString(internal, 0));
		buffer.append("</msg>");
		return buffer.toString();
	}
	

}
