package com.dc.tes.ui.client.model;

import java.io.Serializable;
import java.util.Map;

import com.extjs.gxt.ui.client.data.BaseModelData;

public class GWTSdkResult extends BaseModelData implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2046637470369323026L;
	public static String N_Data = "data";
	public static String N_ErrorID = "errorId";
	public static String N_ErrorMessage = "errorMessage";
	public static String N_IsError = "isError";
	private  Map<String, String> additionMsg;
	
	
	public GWTSdkResult(){}
	public GWTSdkResult(String data, Integer errorId, String errorMessage, boolean isError){
		this.set(N_Data, data);
		this.set(N_ErrorID, errorId);
		this.set(N_ErrorMessage, errorMessage);
		this.set(N_IsError, isError);
	}
	
	public String GetData(){
		return this.get(N_Data);
	}
	
	public Integer GetErrorID(){
		return Integer.parseInt(this.get(N_ErrorID).toString());
	}

	public String GetErrorMessage(){
		return this.get(N_ErrorMessage);
	}
	
	public boolean IsError(){
		return Boolean.parseBoolean(this.get(N_IsError).toString());
	}
	
	public void setAdditionMsg(Map<String, String> AdditionMsg) {
		additionMsg = AdditionMsg;
	}
	public Map<String, String> getAdditionMsg() {
		return additionMsg;
	}
	
}
