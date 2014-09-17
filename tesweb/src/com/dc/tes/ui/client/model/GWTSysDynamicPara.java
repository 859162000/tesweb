package com.dc.tes.ui.client.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.extjs.gxt.ui.client.data.BaseTreeModel;

public class GWTSysDynamicPara extends BaseTreeModel implements Serializable,IDistValidate{




	private static final long serialVersionUID = 1629547179365086704L;
	
	public static String N_ID = "id";
	public static String N_SystemID = "systemId";
	public static String N_ParameterName = "name";
	public static String N_ParameterDesc = "desc";
	public static String N_ParameterExpression = "parameterExpression";
	public static String N_CompareCondition = "compareCondition";
	public static String N_DefaultExpectedValue = "defaultExpectedValue";
	public static String N_ParameterType = "parameterType";
	public static String N_IsValid = "isValid";
//	public static String N_IsKeyMsgField = "isKeyMsgField";
	public static String N_ParameterHostType = "parameterHostType";
	public static String N_ParameterHostId = "parameterHostId";
	public static String N_DisplayFlag = "displayFlag";
	public static String N_RefetchFlag = "refetchFlag";
	 
	public static String N_ParameterHostIP = "paramterHostIP";
	public static String N_ParameterHostPort = "paramterHostPort";
	
	public static String N_ParameterTypeStr = "parameterTypeStr";
	public static String N_DisplayFlagStr = "displayFlagStr";
	public static String N_RefetchFlagStr = "refetchFlagStr";
//	public static String N_IsKeyMsgFieldStr = "isKeyMsgFieldStr";
	public static String N_IsValidStr = "isValidStr";
	public static String N_CompareConditionStr = "compareConditionStr";
	public static String N_ParameterHostTypeStr = "parameterHostTypeStr";
	public static String N_RefetchMethod = "refetchMethod";
	public static String N_RefetchMethodStr = "refetchMethodStr";
	public static String N_ParamFromMsgSrc = "paramfrommsgsrc";
	public static String N_DirectoryID = "directoryId";

	public static String N_CreatedUserId = "createdUserId";
	public static String N_CreatedTime = "createdTime";
	public static String N_LastModifiedTime = "lastModifiedTime";
	public static String N_LastModifiedUserId = "lastModifiedUserId";
	
	public GWTSysDynamicPara() {
		
	}
	
	public GWTSysDynamicPara(String systemID) {
		// TODO Auto-generated constructor stub
		this("","","","","","","1","1",systemID,"","","","","","","","", 0);
		
	}
	
	public GWTSysDynamicPara(String id, String parameterName,
			String parameterDesc, String parameterType,
			String parameterDefaultValue,String parameterExpression, 
			String displayFlag, String isValid, String systemId,
			String refetchFlag, String compareCondition,
			String parameterHostType,String parameterHostId,
			String parameterHostIp,String parameterHostPort,
			String refetchMethod,String paramfrommsgsrc, Integer directoryId) {
		// TODO Auto-generated constructor stub
		this.set(N_ID, id);
		this.set(N_SystemID, systemId);
		this.set(N_ParameterHostIP, parameterHostIp);
		this.set(N_ParameterHostPort, parameterHostPort);
		
		this.SetValue(parameterName, parameterDesc, parameterType, 
				compareCondition, parameterHostType, 
				parameterHostId, displayFlag, isValid, 
				refetchFlag, parameterDefaultValue, parameterExpression
				,refetchMethod,paramfrommsgsrc, directoryId);
		
	}
	

	public void SetValue(String parameterName, String parameterDesc,String parameterType, String compareCondition,
			String parameterHostType, String parameterHostId, String displayFlag, String isValid,
			String refetchFlag, String parameterDefaultValue,String parameterExpression, String refetchMethod, 
			String paramfrommsgsrc, Integer directoryId) {
		// TODO Auto-generated method stub
		this.set(N_ParameterName, parameterName);
		this.set(N_ParameterDesc,parameterDesc);
		this.set(N_ParameterType, parameterType);
		this.set(N_ParameterHostId, parameterHostId);
		this.set(N_DirectoryID, directoryId);
		
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
		
		this.set(N_DefaultExpectedValue, parameterDefaultValue);
		this.set(N_ParameterExpression, parameterExpression);
//		this.set(N_IsKeyMsgField, isKeyMsgField);
//		this.set(N_IsKeyMsgFieldStr, (isKeyMsgField.equalsIgnoreCase("1"))?  "是":"否");
		this.set(N_DisplayFlag, displayFlag);
		this.set(N_DisplayFlagStr, (displayFlag.equalsIgnoreCase("1"))?  "是":"否");
		this.set(N_IsValid, isValid);
		this.set(N_IsValidStr, (isValid.equalsIgnoreCase("1"))?  "是":"否");
		this.set(N_RefetchFlag, refetchFlag);
		this.set(N_RefetchFlagStr, (refetchFlag.equalsIgnoreCase("1"))?  "是":"否");
		
		typeStr = "";
		this.set(N_CompareCondition, compareCondition);		
		if(compareCondition != null && !compareCondition.isEmpty()) {
			switch(Integer.parseInt(compareCondition)) {
			case 0:typeStr = "完全一样";break;
			case 1:typeStr = "实际值中包含有预期值";break;
			case 2:typeStr = "预期值中包含有实际值";break;
			}
		}
		this.set(N_CompareConditionStr, typeStr);
		
		typeStr = "";
		this.set(N_ParameterHostType, parameterHostType);
		if(parameterHostType != null && !parameterHostType.isEmpty()) {
			switch(Integer.parseInt(parameterHostType)) {
			case 0:typeStr = "默认机器";break;
			case 1:typeStr = "指定机器";break;
			case 2:typeStr = "卡所在机器";break;
			}
		}
		this.set(N_ParameterHostTypeStr, typeStr);
		this.set(N_RefetchMethod, refetchMethod);
		this.set(N_ParamFromMsgSrc, paramfrommsgsrc);
		
	}
	
	public void SetDirectoryID(Integer id){
		this.set(N_DirectoryID, id);
	}
	
	public String getParameterHostTypeStr()
	{
		return get(N_ParameterHostTypeStr);
	}
	
	public String getCompareConditionStr()
	{
		return get(N_CompareConditionStr);
	}
	
	public String getParameterTypeStr()
	{
		return get(N_ParameterTypeStr);
	}

	public String getSystemID()
	{
		return get(N_SystemID).toString();
	}
	
	public String getID()
	{
		return get(N_ID).toString();
	}
	
	public String getParameterName()
	{
		return get(N_ParameterName);
	}
	
	public String getDisplayFlag()
	{
		return get(N_DisplayFlag);
	}
	
	public String getParameterDesc()
	{
		return get(N_ParameterDesc);
	}
	
	public String getParameterExpression()
	{
		return get(N_ParameterExpression);
	}
	
	public String getCompareCondition()
	{
		return get(N_CompareCondition);
	}
	
	public String getDefaultExpectedValue()
	{
		return get(N_DefaultExpectedValue);
	}
	
	public String getParameterType()
	{
		return get(N_ParameterType);
	}
	
	public String getIsValid()
	{
		return get(N_IsValid);
	}
	
//	public String getIsKeyMsgField()
//	{
//		return get(N_IsKeyMsgField).toString();
//	}
	
	public String getParameterHostType()
	{
		return get(N_ParameterHostType);
	}
	
	public String getParameterHostId()
	{
		if(get(N_ParameterHostId) != null)
			return get(N_ParameterHostId);
		else
			return null;
	}
	
	public String getParameterHostIP()
	{
		if(get(N_ParameterHostId) != null)
			return get(N_ParameterHostIP);
		else
		return null;
	}
	
	public String getParamFromMsgSrc() {
		return get(N_ParamFromMsgSrc);
	}
	
	public String getParameterHostPort()
	{
		if(get(N_ParameterHostId) != null)
			return get(N_ParameterHostPort);
		else
			return null;
	}
	
	public boolean IsNew()
	{
		return getID().isEmpty();
	}
	
	public String getRefetchFlag()
	{
		return get(N_RefetchFlag);
	}
	
	
	//add by xuat
	public String GetRefetchMethod(){
		return get(N_RefetchMethod);
	}
	
	public void SetRefetchMethod(String flag){
		this.set(N_RefetchMethod, flag);
	}
	
	public String GetRefetchMethodStr(){
		if(get(N_RefetchMethod)!=null && !get(N_RefetchMethod).toString().isEmpty()){
			int flag = Integer.parseInt(get(N_RefetchMethod).toString());
				switch (flag) {
				case 0: return "使用原有参数进行查询";
				case 1: return "使用新参数进行查询";
				case 2: return "新老参数各查询一遍";
				default:
					return "";
				}
			}
		return "";
	}
	
	public Integer GetDirectoryID(){
		return this.get(N_DirectoryID) == null? null : 
			Integer.parseInt(this.get(N_DirectoryID).toString());
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


	@Override
	public String GetTableName() {
		// TODO Auto-generated method stub
		return "SystemDynamicParameter";
	}

	@Override
	public Map<String, Object> GetFieldValuePair(String validateValue) {
		// TODO Auto-gen
		Map<String, Object> fieldValuePair = new HashMap<String,Object>();
		fieldValuePair.put(N_SystemID, get(N_SystemID));
		fieldValuePair.put(N_DirectoryID, get(N_DirectoryID));
		fieldValuePair.put(N_ParameterName, validateValue);
		return fieldValuePair;
	}

	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		if(obj != null && obj instanceof GWTSysDynamicPara) {
			GWTSysDynamicPara p = (GWTSysDynamicPara)obj;
			if(p.getID().equalsIgnoreCase(this.getID()))
				return true;
		}
		return false;	
	}
	

	@Override
	public int hashCode() {
		// TODO Auto-generated method stub
		return Integer.parseInt(getID());
	}

}
