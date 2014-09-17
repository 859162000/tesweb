package com.dc.tes.ui.client.model;

import java.io.Serializable;

import com.extjs.gxt.ui.client.data.BaseModelData;

public class GWTStatusSys extends BaseModelData implements Serializable {

	private static final long serialVersionUID = -648741042331775598L;
	
	public static String N_SYSNAME = "sysName";
	public static String N_IP = "ip";
	public static String N_PORT = "port";
	public static String N_CHANNEL = "channel";
	public static String N_CLIENTTRANCOUNT = "clientTranCount";
	public static String N_SERVERTRANCOUNT = "serverTranCount";
	public static String N_CLIENTCASECOUNT = "clientCaseCount";
	public static String N_SERVERCASECOUNT = "serverCaseCount";
	
	public static GWTStatusSys GetMockData(){
		GWTStatusSys item = new GWTStatusSys();
		item.set(N_SYSNAME, "CISS");
		item.set(N_IP, "192.168.0.10");
		item.set(N_PORT, "8089");
		item.set(N_CHANNEL, "SEND");
		item.set(N_CLIENTTRANCOUNT, "23");
		item.set(N_SERVERTRANCOUNT, "47");
		item.set(N_CLIENTCASECOUNT, "34");
		item.set(N_SERVERCASECOUNT, "131");
		
		return item;
	}
}
