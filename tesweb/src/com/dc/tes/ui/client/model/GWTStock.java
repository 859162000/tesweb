package com.dc.tes.ui.client.model;

import java.io.Serializable;

import com.extjs.gxt.ui.client.data.BaseModelData;

public class GWTStock extends BaseModelData implements Serializable{
	private static final long serialVersionUID = 6070828550323195529L;

	public static String N_Name = "name";
	public static String N_Pos = "pos";
	
	public GWTStock() {
	}
	
	
	
	public GWTStock(String name,String pos) {
		
		this.set(N_Name, name);
		this.set(N_Pos, pos);
	}
	
	public String getName() {	
		return get(N_Name).toString();
	}
	
	public String getPos() {
		return get(N_Pos).toString();
	}
}
