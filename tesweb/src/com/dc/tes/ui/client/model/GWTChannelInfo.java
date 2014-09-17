package com.dc.tes.ui.client.model;

import com.extjs.gxt.ui.client.data.BaseModelData;

public class GWTChannelInfo extends BaseModelData {

	private static final long serialVersionUID = 3306824167699197334L;
	
	public static String N_ChannelName = "channelName";
	public static String N_IsClient = "isClient";
	public static String N_ChannelType = "channelType";
	public static String N_Status = "status";
	public static String N_TranCount = "tranCount";
	
	public GWTChannelInfo(){}
	
	public GWTChannelInfo(String name, String isClient, String type, int status, int count){
		
		set(N_ChannelName, name);
		set(N_IsClient, isClient);
		set(N_ChannelType, type);
		set(N_Status, status);
		set(N_TranCount, count);
	}
}
