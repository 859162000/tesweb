package com.dc.tes.ui.client.model;

import java.io.Serializable;

import com.extjs.gxt.ui.client.data.BaseModelData;

public class GWTChannel extends BaseModelData implements Serializable {

	private static final long serialVersionUID = -901003913293727115L;
	
	public static String N_ChannelId = "channelId";
	public static String N_ChannelName = "name";
	public static String N_IP = "ip";
	public static String N_Port = "port";
	public static String N_AdapterConfig = "adapterConfig";
	public static String N_Adapter = "adapter";
	public static String N_TransRecognizer = "transRecognizer";
	public static String N_RecognizerCfgInfo = "recognizerCfgInfo";
	public static String N_Pack = "pack";
	public static String N_UnPack = "unpack";
	public static String N_IsSysDefault = "default";
	
	public static String N_CreatedUserId = "createdUserId";
	public static String N_CreatedTime = "createdTime";
	public static String N_LastModifiedTime = "lastModifiedTime";
	public static String N_LastModifiedUserId = "lastModifiedUserId";
	
	public GWTChannel(){
		set(N_ChannelId, null);
		set(N_ChannelName, "");
		set(N_IP, "");
		set(N_Port, "");
		set(N_AdapterConfig, "");
		set(N_Adapter, null);
		set(N_TransRecognizer, null);
		set(N_RecognizerCfgInfo, "");
		set(N_Pack, null);
		set(N_UnPack, null);
		set(N_IsSysDefault, false);
	}
	
	public void SetCreatedUserId(String createdUserId){
		this.set(N_CreatedUserId, createdUserId);
	}
	
	public String GetCreatedUserId(){
		return this.get(N_CreatedUserId);
	}
	
	public void SetCreatedTime(String createdTime){
		this.set(N_CreatedTime, createdTime);
	}
	
	public String GetCreatedTime(){
		return this.get(N_CreatedTime);
	}
	
	public void SetLastModifiedTime(String lastModifiedTime){
		this.set(N_LastModifiedTime, lastModifiedTime);
	}
	
	public String GetLastModifiedTime(){
		return this.get(N_LastModifiedTime);
	}
	
	public void SetLastModifiedUserId(String lastModifiedUserId){
		this.set(N_LastModifiedUserId, lastModifiedUserId);
	}
	
	public String GetLastModifiedUserId(){
		return this.get(N_LastModifiedUserId);
	}


}
