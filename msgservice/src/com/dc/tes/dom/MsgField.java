package com.dc.tes.dom;

import org.apache.commons.lang.StringUtils;

import com.dc.tes.util.XmlUtils;

/**
 * 域
 * 
 * @author lijic
 * 
 */
public class MsgField extends MsgItem {
	private String m_value = "";

	/**
	 * 取域中的值
	 */
	public String value() {
		return this.m_value;
	}

	/**
	 * 设置域的值
	 */
	public void set(String value) {
		if (value == null)
			value = "";
		this.m_value = value;
	}

	@Override
	public void ForEach(ISimpleForEachVisitor visitor) {
		visitor.Visit(ISimpleForEachVisitor.ForEachSource.Field, this);
	}

	@Override
	public void ForEach(IForEachVisitor visitor) {
		visitor.Field(this);
	}

	@Override
	public String toString(boolean internal, int indent) {
		return StringUtils.rightPad("", indent) + "<field" + MsgContainerUtils.PrintAttributesList(this, internal) + ">" + XmlUtils.XmlEncode(this.m_value) + "</field>\r\n";
	}

	@Override
	public MsgField Copy() {
		MsgField field = new MsgField();
		field.setAttributes(this.m_atts);

		field.set(this.m_value);
		return field;
	}
}
