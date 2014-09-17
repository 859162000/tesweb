package com.dc.tes.ui.client.model;

import java.io.Serializable;

import com.extjs.gxt.ui.client.data.BaseTreeModel;

public class GWTPack_Struct extends GWTPack_Base implements Serializable {
	private static final long serialVersionUID = -83292866834936914L;

	public GWTPack_Struct() {
		super();
	}

	public GWTPack_Struct(String name) {
		super(name);
	}

	public GWTPack_Struct(String name, BaseTreeModel[] children) {
		this(name);
		for (int i = 0; i < children.length; i++) {
			add(children[i]);
		}
	}

	public void setFieldAttrList(GWTMsgAttribute[] attrs) {
		set("FieldAttribute", attrs);
	}
	
	public GWTMsgAttribute[] getFieldAttrList() {
		return (GWTMsgAttribute[])get("FieldAttribute");
	}
	
	public void setStructAttrList(GWTMsgAttribute[] attrs) {
		set("StructAttribute", attrs);
	}
	
	public GWTMsgAttribute[] getStructAttrList() {
		return (GWTMsgAttribute[])get("StructAttribute");
	}

	public String toString() {
		return getName();
	}
}
