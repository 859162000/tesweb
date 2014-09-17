package com.dc.tes.ui.client.model;

import java.io.Serializable;

import com.extjs.gxt.ui.client.data.BaseModelData;

public class GWTLoginLog extends BaseModelData implements Serializable {

	private static final long serialVersionUID = 2261162550977118664L;

	public static String N_ID = "id";
	public static String N_SystemID = "systemId";
	public static String N_UserID = "userId";
	public static String N_UserName = "userName";
	public static String N_IpAddress = "ipAddress";
	public static String N_MachineName = "machineName";
	public static String N_LoginCount = "loginCount";
	public static String N_LoginTime = "loginTime";
	public static String N_LogoutTime = "logoutTime";
	public static String N_Memo = "memo";
	
	public static String N_Duration = "duration";
	
	public GWTLoginLog() {
		
	}
	
	public GWTLoginLog(String id, String systemId, String userId, String userName, String ipAddress,
			String machineName, String loginCount, String loginTime, String logoutTime, String memo) {
		this.set(N_ID, id);
		this.set(N_SystemID, systemId);
		this.set(N_UserID, userId);
		this.set(N_UserName, userName);
		this.set(N_IpAddress, ipAddress);
		this.set(N_MachineName, machineName);
		this.set(N_LoginCount, loginCount);
		this.set(N_LoginTime, loginTime);
		this.set(N_LogoutTime,logoutTime);
		this.set(N_Memo, memo);
	}
	
	public void setDuration(String duration) {
		this.set(N_Duration, duration);
	}

	public String getID() {
		// TODO Auto-generated method stub
		return this.get(N_ID).toString();
	}
	
	
	
}
