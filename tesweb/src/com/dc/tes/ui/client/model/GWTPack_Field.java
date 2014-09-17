package com.dc.tes.ui.client.model;

import java.io.Serializable;

import com.extjs.gxt.ui.client.data.BaseTreeModel;

public class GWTPack_Field extends GWTPack_Base implements Serializable {
	private static final long serialVersionUID = 192816997848181449L;

	public GWTPack_Field() {
		super();
	}

	public GWTPack_Field(String name) {
		super(name);
	}
	
	public GWTPack_Field(String name, String desc, String length,
			String isarray, String fieldType) {
		super(name);
		set("desc", desc);
		set("fieldType", fieldType);
		set(m_length, length);
		set(m_isArray, isarray);
		set(m_data, "");
	}

	public GWTPack_Field(String name, BaseTreeModel[] children) {
		this(name);
		for (int i = 0; i < children.length; i++) {
			add(children[i]);
		}
	}

	public Integer getLength() {
		try
		{
			return Integer.parseInt((String) get("len"));
		}
		catch (Exception e) {
		}
		return 10;
	}

	public void setData(String data) {
		set(m_data, data);
	}
	
	public String getData() {
		return (String) get(m_data);
	}

	public String toString() {
		return getName();
	}
}
