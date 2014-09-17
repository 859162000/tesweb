package com.dc.tes.ui.client.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.extjs.gxt.ui.client.data.BaseModelData;

public class GWTExecutePlan extends BaseModelData implements Serializable ,IDistValidate {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4182678768654917582L;
	public static String N_ID = "id";
	public static String N_Name = "name";
	public static String N_Desc = "description";
	public static String N_SystemID = "systemId";
	public static String N_CreateUserId = "createdUserId";
	public static String N_CreateTime = "createdTime";
	public static String N_ScheduleRunMode = "scheduleRunMode";
	public static String N_ScheduleRunModeStr = "scheduleRunModeStr";
	public static String N_ScheduleRunWeekDay = "scheduleRunWeekDay";
	public static String N_ScheduleRunHour = "scheduleRunHour";
	public static String N_Status = "status";
	public static String N_StatusStr = "statusStr";
	
	public static String N_LastModifiedTime = "lastModifiedTime";
	public static String N_LastModifiedUserId = "lastModifiedUserId";
	
	public GWTExecutePlan(){};
	
	public GWTExecutePlan(String id, String systemId, String name){
		this.set(N_ID, id);
		this.set(N_SystemID, systemId);
		this.set(N_Name, name);
	}

	public void SetValue(String name, String desc,
			String createUserId, String runMode, String runWeekday, 
			String runHour, String status){
		this.set(N_Name, name);
		this.set(N_Desc, desc);
		this.set(N_CreateUserId, createUserId);
		this.set(N_ScheduleRunMode, runMode);
		this.set(N_ScheduleRunWeekDay, runWeekday);
		this.set(N_ScheduleRunHour, runHour);
		this.set(N_Status, status);
		setStr();
	}
	

	public GWTExecutePlan(String id, String name, String desc, String systemId,
			String createUserId, String runMode, String runWeekday, 
			String runHour, String createTime, String status){
		this.set(N_ID, id);
		this.set(N_SystemID, systemId);
		SetValue(name, desc, createUserId, runMode, runWeekday, runHour, status);
		this.set(N_CreateTime, createTime);
	}

	public String GetID(){
		return this.get(N_ID);
	}
	
	public String GetName(){
		return this.get(N_Name);
	}
	
	public String GetDesc(){
		return this.get(N_Desc);
	}
	
	public String GetSystemID(){
		return this.get(N_SystemID);
	}
	
	public String GetCreateUserId(){
		return this.get(N_CreateUserId);
	}
	
	public String GetCreateTime(){
		return this.get(N_CreateTime);
	}
	
	public String GetScheduleRunMode(){
		return this.get(N_ScheduleRunMode);
	}
	
	public String GetScheduleRunWeekday(){
		return this.get(N_ScheduleRunWeekDay);
	}
	
	public String GetScheduleRunHour(){
		return this.get(N_ScheduleRunHour);
	}
	
	public Integer GetStatus(){
		return Integer.parseInt(this.get(N_Status).toString());
	}
	private void setStr() {
		String scheduleRunMode = this.GetScheduleRunMode();
		int flag = Integer.parseInt(scheduleRunMode);
		switch (flag) {
		case -1:
			set(N_ScheduleRunModeStr, "不执行");
			break;
		case 0:
			set(N_ScheduleRunModeStr, "核心启动时");
			break;
		case 1:
			set(N_ScheduleRunModeStr, "一次性");
			break;
		case 2:
			set(N_ScheduleRunModeStr, "每天");
			break;
		case 3:
			set(N_ScheduleRunModeStr, "每周");
			break;
			
		case 4:
			set(N_ScheduleRunModeStr, "每月");
			break;
		default:
			break;
		}
		int status = this.GetStatus();
		if(status == 0){
			this.set(N_StatusStr, "无效");
		}else{
			this.set(N_StatusStr, "有效");
		}
		
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
		return GetID().isEmpty();
	}

	@Override
	public String GetTableName() {
		// TODO Auto-generated method stub
		return "ExecutePlan";
	}

	@Override
	public Map<String, Object> GetFieldValuePair(String validateValue) {
		// TODO Auto-generated method stub
		Map<String, Object> fieldValuePair = new HashMap<String,Object>();
		fieldValuePair.put(N_SystemID, get(N_SystemID));
		fieldValuePair.put(N_Name, validateValue);
		return fieldValuePair;
	}
}
