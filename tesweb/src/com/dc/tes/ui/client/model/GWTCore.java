package com.dc.tes.ui.client.model;

import java.io.Serializable;

import com.extjs.gxt.ui.client.data.BaseModelData;

public class GWTCore  extends BaseModelData implements Serializable{

	private static final long serialVersionUID = 6802763272073442691L;
	
	public static String N_CoreName = "corename";
	public static String N_CoreFullName = "corefullname";
	public static String N_CreateTime = "createtime";
	public static String N_Path = "path";
	public static String N_Core = "core";
	public static String N_CorePath = "corepath";
	public static String N_Receiver = "receiver";
	public static String N_Sender = "sender";
	
	public GWTCore() {
		
	}
	
	public GWTCore(String corefullname, String path, String core, String corepath, String receiver, String sender) {
		this.set(N_CoreFullName, corefullname);
		String[] format = corefullname.split("_");
		if(format.length == 3) {
			this.set(N_CoreName, format[2]);
			this.set(N_CreateTime, format[0]+"_"+format[1]);
		} else {
			this.set(N_CoreName, corefullname);
			this.set(N_CreateTime, "");
		}
		
		this.set(N_Path, path);
		this.set(N_Core, core);
		this.set(N_CorePath, corepath);
		this.set(N_Receiver, receiver);
		this.set(N_Sender, sender);
	}
	
	public String getCoreName() {
		return this.get(N_CoreName);
	}
	
	public String getCreateTime() {
		return this.get(N_CreateTime);
	}
	
	public String getCore() {
		return this.get(N_Core);
	}
	
	public String getPath() {
		return this.get(N_Path);
	}
	
	public String getReceiver() {
		return this.get(N_Receiver);
	}
	
	public String getSender() {
		return this.get(N_Sender);
	}
	
	public String getCorepath() {
		return this.get(N_CorePath);
	}

}
