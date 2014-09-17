package com.dc.tes.ui.client.model;

import java.io.Serializable;

import com.extjs.gxt.ui.client.data.BaseModelData;

public class GWTExecuteSetExecutePlan extends BaseModelData implements
		Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7358999376842257485L;
	public static String N_ID = "id";
	public static String N_ExecuteSetID = "executeSetDirId";
	public static String N_ExecutePlanID = "executePlanId";
	public static String N_SystemID = "systemId";
	public static String N_AddUserID = "addUserId";
	public static String N_AddTime = "addTime";
	public static String N_ScheduledRunStatus = "scheduledRunStatus";
	public static String N_ScheduledRunStatusStr = "scheduledRunStatusStr";
	public static String N_BeginRunTime = "beginRunTime";
	public static String N_EndRunTime = "endRunTime";
	public static String N_ExecuteSetName = "executeSetName";
	public static String N_ExecutePlanName = "executePlanName";
	public static String N_UserName = "userName";
	
	public GWTExecuteSetExecutePlan(){}
	public GWTExecuteSetExecutePlan(String id, String systemId, String userId,
			String executeSetID, String execPlanID, String scheduledRunStatus){
		this.set(N_ID, id);
		this.set(N_SystemID, systemId);
		this.set(N_AddUserID, userId);
		this.set(N_ExecuteSetID, executeSetID);
		this.set(N_ExecutePlanID, execPlanID);
		this.set(N_ScheduledRunStatus, scheduledRunStatus);
	}
	
	public void SetValue(String executeSetId, String executePlanId, String systemId, 
			String userId, String scheduledRunStatus, String beginRunTime, String EndRunTime){
		this.set(N_ExecuteSetID, executeSetId);
		this.set(N_ExecutePlanID, executePlanId);
		this.set(N_SystemID, systemId);
		this.set(N_AddUserID, userId);
		this.set(N_ScheduledRunStatus, scheduledRunStatus);
		this.set(N_BeginRunTime, beginRunTime);
		this.set(N_EndRunTime, EndRunTime);
	}
	
	public GWTExecuteSetExecutePlan(String id, String executeSetId, String executePlanId, String systemId, 
			String userId, String scheduledRunStatus, String beginRunTime, String EndRunTime, String addTime){
		this.set(N_ID, id);
		this.set(N_AddTime, addTime);
		SetValue(executeSetId, executePlanId, systemId, userId, scheduledRunStatus, beginRunTime, EndRunTime);
		setScheduledRunStatusStr();
	}
	
	private void setScheduledRunStatusStr() {
		// TODO Auto-generated method stub
		switch(Integer.parseInt(GetScheduledRunStatus())){
		case -1:
			this.set(N_ScheduledRunStatusStr, "正在执行");
			break;
		case 0:
			this.set(N_ScheduledRunStatusStr, "未执行");
			break;
		case 2:
			this.set(N_ScheduledRunStatusStr, "执行完成");
			break;
		}
	}
	public String GetScheduledRunStatusStr(){
		return this.get(N_ScheduledRunStatusStr);
	}
	
	public void SetExecPlanName(String name){
		this.set(N_ExecutePlanName, name);
	}
	
	public void SetExecuteSetName(String name){
		this.set(N_ExecuteSetName, name);
	}
	
	public void SetUserName(String name){
		this.set(N_UserName, name);
	}
	
	public String GetID(){
		return this.get(N_ID);
	}
	
	public String GetExecuteSetID(){
		return this.get(N_ExecuteSetID);
	}
	
	public String GetExecutePlanID(){
		return this.get(N_ExecutePlanID);
	}
	
	public String GetSystemID(){
		return this.get(N_SystemID);
	}
	
	public String GetAddUserID(){
		return this.get(N_AddUserID);
	}
	
	public String GetAddTime(){
		return this.get(N_AddTime);
	}
	
	public String GetScheduledRunStatus(){
		return this.get(N_ScheduledRunStatus);
	}
	
	public String GetBeginRunTime(){
		return this.get(N_BeginRunTime);
	}
	
	public String GetEndRunTime(){
		return this.get(N_EndRunTime);
	}
	
	public Boolean isNew(){
		return GetID().isEmpty();
	}

}
