package com.dc.tes.ui.client.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.extjs.gxt.ui.client.data.BaseTreeModel;

public class GWTCaseFlow extends BaseTreeModel implements IDistValidate, Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5424760056143955879L;
	public static String N_ID = "id";
	public static String N_CaseFlowNo = "caseFlowNo";
	public static String N_Name = "name";
	public static String N_Desc = "description";
	public static String N_SystemID = "systemId";
	public static String N_UserID = "createdUserId";
	public static String N_UserName = "userName";
	public static String N_CreateTime = "createdTime";
	public static String N_BreakPointFlag = "breakPointFlag";
	public static String N_CaseFlowPath = "caseFlowPath";
	public static String N_DirectoryID = "directoryId";
	public static String N_CaseFlowStep = "caseFlowStep";
	public static String N_PreConditions = "preConditions";
	public static String N_ExpectedResult = "expectedResult";
	public static String N_CaseType = "caseType";
	public static String N_CaseProperty = "caseProperty";
	public static String N_Priority = "priority";
	public static String N_Designer = "designer";
	public static String N_DesignTime = "designTime";
	public static String N_Memo = "memo";
	public static String N_PassFlag = "passFlag";
	public static String N_DisabledFlag = "disabledFlag";
	
	public static String N_LastModifiedTime = "lastModifiedTime";
	public static String N_LastModifiedUserId = "lastModifiedUserId";
	
	public GWTCaseFlow(){}
	
	public void SetEditValue(String caseFlowNo, String name, String description,  String systemId, String userId){
		this.set(N_CaseFlowNo, caseFlowNo);
		this.set(N_Name, name);
		this.set(N_SystemID, systemId);
		this.set(N_UserID, userId);
		this.set(N_Desc, description);
	}
	
	public GWTCaseFlow(String id, String name, String caseFlowNo, String description, 
			String systemId, String userId, String userName, String createTime){
		this.set(N_ID, id);
		this.set(N_Name, name);
		this.set(N_CaseFlowNo, caseFlowNo);
		this.set(N_Desc, description);
		this.set(N_SystemID, systemId);
		this.set(N_UserID, userId);
		this.set(N_UserName, userName);
		this.set(N_CreateTime, createTime);
	}
	public void SetExtraValue(String breakPointFlag, String caseFlowPath,
			String directoryId, String designer,String caseFlowStep,
			String preConditions, String expectedResult, String caseType, 
			String caseProperty, String priority, String designTime, String memo){
		this.set(N_BreakPointFlag, breakPointFlag);
		this.set(N_CaseFlowPath, caseFlowPath);
		this.set(N_DirectoryID, directoryId);
		this.set(N_Designer, designer);		
		this.set(N_CaseFlowStep, caseFlowStep);
		this.set(N_PreConditions, preConditions);
		this.set(N_ExpectedResult, expectedResult);
		this.set(N_CaseType, caseType);
		this.set(N_CaseProperty, caseProperty);
		this.set(N_Priority, priority);
		this.set(N_DesignTime, designTime);
		this.set(N_Memo, memo);
	}
	
	public void SetPassFlag(Integer flag){
		this.set(N_PassFlag, flag);
	}
	
	public void SetDisabledFlag(Integer flag){
		this.set(N_DisabledFlag, flag);
	}
	
	public String GetID(){
		return this.get(N_ID);
	}
	
	public String GetCaseFlowNo(){
		return this.get(N_CaseFlowNo);
	}
	
	public String GetName(){
		return this.get(N_Name);
	}
	
	public String GetUserName(){
		return this.get(N_UserName);
	}
	
	public String GetCreateTime(){
		return this.get(N_CreateTime);
	}
	public String GetDesc(){
		return this.get(N_Desc);
	}
	public Integer GetCaseFlowID(){
		return Integer.parseInt(this.get(N_ID).toString());
	}
	
	public String GetBreakPointFlag(){
		return this.get(N_BreakPointFlag);
	}
	public String GetCaseFlowPath(){
		return this.get(N_CaseFlowPath);
	}
	public void SetCaseFlowPath(String path){
		this.set(N_CaseFlowPath, path);
	}
	
	public String GetPreConditions(){
		return this.get(N_PreConditions);
	}
	
	public String GetDirectoryID(){
		return this.get(N_DirectoryID);
	}
	
	public void SetDirectoryID(String id){
		this.set(N_DirectoryID, id);
	}
	
	public String GetDesigner(){
		return this.get(N_Designer);
	}
	
	public String GetDesignTime(){
		return this.get(N_DesignTime);
	}
	
	public String GetMemo(){
		return this.get(N_Memo);
	}
	
	public String GetCaseFlowStep(){
		return this.get(N_CaseFlowStep);
	}
	
	public String GetExpectedResult(){
		return this.get(N_ExpectedResult);
	}
	
	public String GetCaseType(){
		return this.get(N_CaseType);
	}
	
	public String GetSystemID(){
		return this.get(N_SystemID);
	}
	public String GetCaseProperty(){
		return this.get(N_CaseProperty);
	}
	
	public String GetPriority(){
		return this.get(N_Priority);
	}
	
	public Integer GetPassFlag(){
		return this.get(N_PassFlag) == null? -1: Integer.parseInt(this.get(N_PassFlag).toString());
	}
	
	public Integer GetDisabledFlag(){
		return this.get(N_DisabledFlag) == null? -1: Integer.parseInt(this.get(N_DisabledFlag).toString());
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
	
	@Override
	public String GetTableName() {
		// TODO Auto-generated method stub
		return "CaseFlow";
	}

	@Override
	public Map<String, Object> GetFieldValuePair(String validateValue) {
		// TODO Auto-generated method stub
		Map<String, Object> fieldValuePair = new HashMap<String,Object>();
		fieldValuePair.put(N_DirectoryID, GetDirectoryID());
		fieldValuePair.put(N_CaseFlowNo, validateValue);
		return fieldValuePair;
	}
	
	public boolean IsNew(){
		return GetID() == null;
	}
}
