package com.dc.tes.ui.client.model;

import java.io.Serializable;

import com.dc.tes.ui.client.common.CookieManage;
import com.extjs.gxt.ui.client.data.BaseModelData;
import com.google.gwt.json.client.JSONObject;


/**
 * 菜单项所对应应该打开的Tab页面信息
 * @author scckobe
 *
 */
public class GWTMenuTab extends BaseModelData implements Serializable {

	private static final long serialVersionUID = 6662290045862333147L;
	
	public static String OpenClassName = "Name";
	public static String TabTitle = "TabTitle";
	public static String Closeable = "Closeable";
	public static String AppendTitleType = "AppendTitleType";
	
	public GWTMenuTab()
	{
	}
	
	public GWTMenuTab(JSONObject itemObj)
	{
		this.set(OpenClassName, itemObj.get(OpenClassName).isString().stringValue());
		this.set(TabTitle,itemObj.get(TabTitle).isString().stringValue());
		if(itemObj.get(Closeable) != null)
			this.set(Closeable,itemObj.get(Closeable).isBoolean().booleanValue());
		else
			this.set(Closeable,false);
		if(itemObj.get(AppendTitleType) != null)
			this.set(AppendTitleType,itemObj.get(AppendTitleType).isString().stringValue());
		else
			this.set(AppendTitleType,"");
	}
	
	public String GetOpenClassName()
	{
		return get(OpenClassName).toString();
	}
	
	public String GetTabTitle()
	{
		return get(TabTitle).toString() + GetAppendStr();
	}
	
	private String GetAppendStr()
	{
		if(get(AppendTitleType).toString().isEmpty())
			return "";
		else
			return "[" + CookieManage.GetSimuSystemName() + "]";
	}
	
	public boolean GetCanClose()
	{
		return Boolean.valueOf(get(Closeable).toString());
	}
}
