package com.dc.tes.ui.client.model;

import java.io.Serializable;

import com.extjs.gxt.ui.client.data.BaseTreeModel;

public class GWTCaseParamExpectedValue extends BaseTreeModel implements Serializable {

	private static final long serialVersionUID = 7385999613958116620L;
	public static String N_ID = "id";
    public static String N_CaseID = "caseId";
    public static String N_TransParameterID = "transParameterID";
    public static String N_ExpectedValue = "expectedValue";
    
	public static String N_ParameterName = "name";
	public static String N_ParameterDesc = "parameterDesc";
	public static String N_ParameterType = "parameterType";

	public static String N_ParameterTypeStr = "parameterTypeStr";
	public static String N_ExpectedValueType = "expectedValueType";
	
	public static String N_ParentDirectoryID = "parentDirectoryID";
	
	public static String N_CreatedUserId = "createdUserId";
	public static String N_CreatedTime = "createdTime";
	public static String N_LastModifiedTime = "lastModifiedTime";
	public static String N_LastModifiedUserId = "lastModifiedUserId";
	
	public GWTCaseParamExpectedValue() {
		
	}
	
	public GWTCaseParamExpectedValue(String ID,String caseID,String parameterName, String parameterDesc,
			String parameterType, String transParameterID, String expectedValue, int expectedValueType, String parentDirectoryID) {
		// TODO Auto-generated constructor stub
		this.set(N_ID, ID);
		this.set(N_CaseID,caseID);
		this.set(N_ParameterName, parameterName);
		this.set(N_ParameterDesc, parameterDesc);
		this.set(N_ParameterType, parameterType);
		this.set(N_TransParameterID, transParameterID);
		this.set(N_ExpectedValue, expectedValue);
		this.set(N_ParentDirectoryID, parentDirectoryID);
		String typeStr = "";
		if(parameterType != null && !parameterType.isEmpty()) {
			switch(Integer.parseInt(parameterType)) {
			case 0:typeStr = "报文类参数";break;
			case 1:typeStr = "SQL参数";break;
			case 2:typeStr = "交易数据类参数";break;
			case 3:typeStr = "函数处理类参数";break;
			case 4:typeStr = "条件分支类参数";break;
			}
		}
		this.set(N_ParameterTypeStr, typeStr);
		
		this.set(N_ExpectedValueType,expectedValueType == 1? true:false);
		
	}
	
	public String getID() {
		return this.get(N_ID).toString();
	}
	
	public String getCaseID() {
		return this.get(N_CaseID).toString();
	}
	
	public String getTranParameterID() {
		return this.get(N_TransParameterID).toString();
	}
	
	public String getParameterName() {
		return this.get(N_ParameterName).toString();
	}
	
	public String getParameterType() {
		return this.get(N_ParameterTypeStr).toString();
	}
	
	public String getExpectedValue() {
		if(this.get(N_ExpectedValue) != null)
			return this.get(N_ExpectedValue).toString();
		return null;
	} 
	
	public String getParameterDesc() {
		return this.get(N_ParameterDesc).toString();
	} 
	
	public Boolean isExpectedValueVar() {
		return (Boolean)this.get(N_ExpectedValueType);
	}
	
	public String getParentDirectoryID() {
		return this.get(N_ParentDirectoryID).toString();
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

	
	public boolean IsNew()
	{
		return getID().isEmpty();
	}
	
	
}
