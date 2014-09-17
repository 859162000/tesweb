package com.dc.tes.ui.client.model;

import java.io.Serializable;

import com.extjs.gxt.ui.client.data.BaseTreeModel;

public class MsgAttribute extends BaseTreeModel implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4662921376562458693L;
	
	private String m_name;
	private String m_displayName;
	private String m_listItems;
	private String m_defaultValue;
	private String m_width;

	public String getName() {
		return this.m_name;
	}

	public String getDisplayName() {
		return this.m_displayName;
	}

	public String getListItems() {
		return this.m_listItems;
	}

	public String getDefaultValue() {
		return this.m_defaultValue;
	}
	
	public String getWidth() {
		return this.m_width;
	}

	public MsgAttribute(String name, String displayName, String listItems, String defaultValue, String width) {
		super();
		this.m_name = name;
		this.m_displayName = displayName;
		this.m_listItems = listItems;
		this.m_defaultValue = defaultValue;
		this.m_width = width;
	}
}
