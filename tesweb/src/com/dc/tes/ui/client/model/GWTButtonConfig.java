package com.dc.tes.ui.client.model;

import java.io.Serializable;

import com.extjs.gxt.ui.client.data.BaseModelData;
import com.extjs.gxt.ui.client.data.ModelData;

public class GWTButtonConfig extends BaseModelData implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2007386929869352015L;
	
	public static String N_Name = "name";
	public static String N_Desc = "desc";
	public static String N_Visible = "visible";
	public static String N_Enable = "enable";
	public static String N_Type = "type";
	
	public GWTButtonConfig()
	{
		set(N_Name,"");
		set(N_Desc,"");
		set(N_Visible,false);
		set(N_Enable,false);
		set(N_Type,"");
	}
	
	public GWTButtonConfig(ModelData data)
	{
		set(N_Name,data.get(N_Name));
		set(N_Desc,data.get(N_Desc));
		set(N_Visible,data.get(N_Visible));
		set(N_Enable,data.get(N_Enable));
		set(N_Type,data.get(N_Type));
	}
	
//	public GWTButtonConfig(String name,String desc,boolean visible,boolean enable)
//	{
//		set(N_Name,name);
//		set(N_Desc,desc);
//		set(N_Visible,visible);
//		set(N_Enable,enable);
//	}
	
	public String GetName()
	{
		return get(N_Name);
	}
	
	public String GetDesc()
	{
		return get(N_Desc);
	}
	
	public boolean GetVisible()
	{
		try
		{
			return (Boolean)get(N_Visible);
		}
		catch(Exception e)
		{
			return false;
		}
	}
	
	public boolean GetEnable()
	{
		try
		{
			return (Boolean)get(N_Enable);
		}
		catch(Exception e)
		{
			return false;
		}
	}
	
	public String GetBtnType()
	{
		return get(N_Type);
	}
}
