package com.dc.tes.ui.client.model;

import java.io.Serializable;

import com.extjs.gxt.ui.client.data.BaseModelData;

public class GWTCompareResult extends BaseModelData implements Serializable{
	private static final long serialVersionUID = 1141551775661676642L;

	private static String N_BoolResult = "BoolResult";
	private static String N_ErrorMsg = "ErrorMsg";
	private static String N_CompareResult = "CompareResult";
	public GWTCompareResult()
	{
		set(N_BoolResult,false);
		set(N_CompareResult,null);
		set(N_ErrorMsg, "");
	}
	
	public void setBoolResult(boolean result)
	{
		set(N_BoolResult,result);
	}
	
	public boolean getBooleanResult()
	{
		try
		{
			return Boolean.parseBoolean(get(N_BoolResult).toString());
		}
		catch (Exception e) {
		}
		return false;
	}
	
	public void setCompareResult(GWTPack_Struct root)
	{
		set(N_CompareResult,root);
	}
	
	public GWTPack_Struct getCompareResult()
	{
		return (GWTPack_Struct)get(N_CompareResult);
	}
	
	public void setErrorMsg(String errorMsg)
	{
		set(N_ErrorMsg, errorMsg);
	}
	
	public String getErrorMsg()
	{
		return get(N_ErrorMsg);
	}
}
