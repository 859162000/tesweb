package com.dc.tes.ui.client.model;

import java.io.Serializable;

import com.extjs.gxt.ui.client.data.BaseModelData;

public class GWTUserSys extends BaseModelData implements Serializable{
	
	private static final long serialVersionUID = -3975919870802102445L;
	public static String N_UserID = "userid";
	public static String N_SysID = "systemid";
	public static String N_ID = "id";
	
	public GWTUserSys()
	{
		
	}
	
	public GWTUserSys(String id,String userID,String sysID)
	{
		this.set(N_ID, id);
		this.set(N_UserID, userID);
		this.set(N_SysID, sysID);
	}
	
	public String GetID()
	{
		return get(N_ID).toString();
	}
	
	public String GetUserID()
	{
		return get(N_UserID).toString();
	}
	
	public String GetSysID()
	{
		return get(N_SysID).toString();
	}
	
	public boolean IsNew()
	{
		return GetSysID() == "";
	}

}
