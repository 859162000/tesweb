package com.dc.tes.ui.client.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.extjs.gxt.ui.client.data.BaseModelData;

public class GWTTestRound extends BaseModelData implements Serializable, IDistValidate{
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4967986176857603655L;
	public static String N_RoundID = "roundId";
	public static String N_SystemId = "systemId";
	public static String N_RoundNo = "roundNo";
	public static String N_RoundName = "roundName";
	public static String N_Desc = "description";
	public static String N_StartDate = "startDate";
	public static String N_EndDate = "endDate";
	public static String N_CurrentRoundFlag = "currentRoundFlag";
	public static String N_CurrentRoundFlagStr = "currentRoundFlagStr";
	
	public static String N_CreatedUserId = "createdUserId";
	public static String N_CreatedTime = "createdTime";
	public static String N_LastModifiedTime = "lastModifiedTime";
	public static String N_LastModifiedUserId = "lastModifiedUserId";
	
	
	public GWTTestRound(){}
	
	public GWTTestRound(Integer id, String systemId){
		this.set(N_RoundID, id);
		this.set(N_SystemId, systemId);
	}
	
	public void SetValue(Integer roundNo, String roundName,
			String desc, String startdate, String endDate, Integer flag) {
		// TODO Auto-generated constructor stub
		this.set(N_RoundNo, roundNo);
		this.set(N_RoundName, roundName);
		this.set(N_Desc, desc);
		this.set(N_StartDate, startdate);
		this.set(N_EndDate, endDate);
		this.set(N_CurrentRoundFlag, flag);
		
	}
	
	private void setCurrentRoundFlagStr() {
		// TODO Auto-generated method stub
		if(GetCurrentRoundFlag() == 1){
			this.set(N_CurrentRoundFlagStr, "是");
		}else{
			this.set(N_CurrentRoundFlagStr, "否");
		}
	}

	public GWTTestRound(Integer roundID, String systemId, Integer roundNo, String roundName,
			String desc, String startdate, String endDate, Integer flag){
		this.set(N_RoundID, roundID);
		this.set(N_SystemId, systemId);
		SetValue(roundNo, roundName, desc, startdate, endDate, flag);
		setCurrentRoundFlagStr();
		
	}
	
	public Integer GetRoundID(){
		return this.get(N_RoundID)==null?null:Integer.parseInt(this.get(N_RoundID).toString());
	}
	
	public String GetSystemID(){
		return this.get(N_SystemId);
	}
	
	public Integer GetRoundNo(){
		return this.get(N_RoundNo)==null?null:Integer.parseInt(this.get(N_RoundNo).toString());
	}
	
	public String GetRoundName(){
		return this.get(N_RoundName);
	}
	
	public String GetDesc(){
		return this.get(N_Desc);
	}
	
	public String GetStartDate(){
		return this.get(N_StartDate);
	}
	
	public String GetEndDate(){
		return this.get(N_EndDate);
	}

	public Integer GetCurrentRoundFlag(){
		return this.get(N_CurrentRoundFlag)==null?null:Integer.parseInt(this.get(N_CurrentRoundFlag).toString());
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
	
	public boolean isNew(){
		return GetRoundID()==null;
	}
	
	@Override
	public String GetTableName() {
		// TODO Auto-generated method stub
		return "TestRound";
	}

	@Override
	public Map<String, Object> GetFieldValuePair(String validateValue) {
		// TODO Auto-generated method stub
		Map<String, Object> fieldValuePair = new HashMap<String,Object>();
		fieldValuePair.put(N_SystemId, get(N_SystemId));
		fieldValuePair.put(N_RoundName, validateValue);
		return fieldValuePair;
	}
}
