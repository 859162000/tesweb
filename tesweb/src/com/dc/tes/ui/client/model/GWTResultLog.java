package com.dc.tes.ui.client.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.extjs.gxt.ui.client.data.BaseModelData;

public class GWTResultLog extends BaseModelData implements Serializable,
		IDistValidate {

	public static String N_ID ="id";
	public static String N_QueueListID ="queueListId";
	public static String N_ExecuteBatchNo = "executeBatchNo";
	public static String N_Description = "description";
	public static String N_UserID = "USERID";
	public static String N_SystemID = "SYSTEMID";
	public static String N_CreateTime = "createTime";
	public static String N_EndRunTime = "endRunTime";
	
	// add by jindg For ShowDetail
	public static String N_ExecuteSetName = "executeSetName";
	public static String N_UserName = "USERNAME";
	
	//add by xuat 2012.9.26
	public static String N_PassFlag = "passFlag";
	public static String N_RunDuration = "ranDuration";
	
	public static String N_Type = "type";
	
	public String getID()
	{
		return this.get(N_ID);
	}
	public String getQueueListID()
	{
		return this.get(N_QueueListID);
	}
	public String getExecuteBatchNo()
	{
		return this.get(N_ExecuteBatchNo);
	}
	public String getDesc()
	{
		return this.get(N_Description);
	}
	public String getUserID()
	{
		return this.get(N_UserID);
	}
	public String getSystemID()
	{
		return this.get(N_SystemID);
	}
	public String getExecuteSetName()
	{
		return this.get(N_ExecuteSetName);
	}
	
	public String getUserName()
	{
		return this.get(N_UserName);
	}
	public String getPassFlag(){
		return get(N_PassFlag);
	}
	
	public Integer getType(){
		return this.get(N_Type) == null ?
				null : Integer.parseInt(get(N_Type).toString());
	}
	
	public void setPassFlag(String flag){
		this.set(N_PassFlag, flag);
	}
	
	public void setEndRunTime(String time){
		this.set(N_EndRunTime, time);
	}
	public void setRunDuration(String flag){
		this.set(N_RunDuration, flag);
	}
	public GWTResultLog(){}
	
	public GWTResultLog(String id, String QUEUELISTID,String EXECUTEBATCHNO,
			String DESCRIPTION,String USERID,String SYSTEMID,String CREATETIME, 
			String endRunTime, Integer type)
	{
		this.set(N_ID, id);
		this.set(N_QueueListID, QUEUELISTID);
		this.set(N_ExecuteBatchNo, EXECUTEBATCHNO);
		this.set(N_Description, DESCRIPTION);
		this.set(N_UserID, USERID);
		this.set(N_SystemID, SYSTEMID);
		this.set(N_CreateTime, CREATETIME);
		this.set(N_EndRunTime, endRunTime);
		this.set(N_Type, type);
		//return this.get(N_SYSTEMID);
	}
	
	public GWTResultLog(String id, String QUEUELISTID,String EXECUTEBATCHNO,
			String DESCRIPTION,String USERID,String SYSTEMID,String CREATETIME
			,String QUEUELISTNAME, String USERNAME,  String endRunTime, Integer type)
	{
		this.set(N_ID, id);
		this.set(N_QueueListID, QUEUELISTID);
		this.set(N_ExecuteBatchNo, EXECUTEBATCHNO);
		this.set(N_Description, DESCRIPTION);
		this.set(N_UserID, USERID);
		this.set(N_SystemID, SYSTEMID);
		this.set(N_CreateTime, CREATETIME);
		this.set(N_ExecuteSetName, QUEUELISTNAME);
		this.set(N_UserName, USERNAME);
		this.set(N_EndRunTime, endRunTime);
		this.set(N_Type, type);
		//return this.get(N_SYSTEMID);
	}
	@Override
	public String GetTableName() {
		// TODO Auto-generated method stub
		return "test_excute_log";
	}

	@Override
	public Map<String, Object> GetFieldValuePair(String validateValue) {
		// TODO Auto-generated method stub
		Map<String, Object> fieldValuePair = new HashMap<String,Object>();
		fieldValuePair.put(N_SystemID, getSystemID());
		fieldValuePair.put(N_QueueListID, validateValue);
		return fieldValuePair;
	}

}
