package com.dc.tes.ui.client.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.extjs.gxt.ui.client.data.BaseModelData;

public class GWTTransaction extends BaseModelData implements Serializable,IDistValidate {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4332206894026350899L;
	
	public static String N_TransID = "transactionId";
	public static String N_TranCode = "tranCode";
	public static String N_TranName = "tranName";
	public static String N_IsClientSimu = "isClientSimu";
	public static String N_Desc = "description";
	public static String N_Script = "script";
	public static String N_Category = "category";
	public static String N_SystemID = "systemId";
	public static String N_StateFlag = "flag";
	public static String N_Req = "req";
	public static String N_Resp = "resp";
	public static String N_Chanel = "Chanel";
	public static String N_TranCategoryID = "transactionCategoryId";
	public static String N_TranCategoryName = "transactionCategoryName";
	public static String N_SqlDelayTime = "sqlDelayTime";
	
	public static String N_CreatedUserId = "createdUserId";
	public static String N_CreatedTime = "createdTime";
	public static String N_LastModifiedTime = "lastModifiedTime";
	public static String N_LastModifiedUserId = "lastModifiedUserId";
	
	public GWTTransaction()
	{
	}
	
	public GWTTransaction(String sysId,int isclient){
		this("",sysId,isclient,"","","",0,"","","");
	}
	
	public GWTTransaction(String tranId, String sysId,int isclient, String tranCode, String tranName,
			String desc,int flag, String script,String category,String chanelName) {
		set(N_TransID,tranId);
		SetEditValue(tranCode,tranName,desc,chanelName);
		set(N_IsClientSimu,isclient);
		set(N_Script,script);
		set(N_Category,category);
		set(N_SystemID,sysId);
		set(N_StateFlag,flag);
		set(N_Req,false);
		set(N_Resp,false);
	}

	
	public void SetEditValue(String tranCode, String tranName,String desc,String chanelName)
	{
		set(N_TranCode,tranCode);
		set(N_TranName,tranName);
		set(N_Desc,desc == null ? "" : desc);
		this.set(N_Chanel, chanelName);
	}
	
	public void SetEditValue(String tranCode, String tranName,String desc,String chanelName,String tranID)
	{
		SetEditValue(tranCode,tranName,desc,chanelName);
		set(N_TransID,tranID);
	}
	
	public void SetPackConfig(String reqConfig,String respConfig)
	{
		set(N_Req,!(reqConfig == null || reqConfig.isEmpty()));
		set(N_Resp,!(respConfig == null || respConfig.isEmpty()));
	}
	
	public String getTranID() {
		return get(N_TransID);
	}
	
	public String getSystemID() {
		return get(N_SystemID);
	}
	
	public String getTranCode() {
		return get(N_TranCode);
	}
	
	public String getTranName() {
		return get(N_TranName);
	}
	
	public String getDesc() {
		return get(N_Desc);
	}
	
	public String getScript() {
		return get(N_Script);
	}
	public String getTranCategoryID(){
		return get(N_TranCategoryID);
	}
	
	public void setTranCategoryID(String tranCateID){
		this.set(N_TranCategoryID, tranCateID);
	}
	
	public String getTranCateName(){
		return get(N_TranCategoryName);
	}
	
	public void setTranCateName(String tranCateName){
		this.set(N_TranCategoryName, tranCateName);
	}
	public int GetFlag()
	{
		return Integer.valueOf(get(N_StateFlag).toString());
	}
	
	public int GetMode()
	{
		return Integer.valueOf(get(N_IsClientSimu).toString());
	}
	
	public String GetCategory()
	{
		return get(N_Category);
	}
	
	public boolean GetReq()
	{
		return Boolean.parseBoolean(get(N_Req).toString());
	}
	
	public boolean GetResp()
	{
		return Boolean.parseBoolean(get(N_Resp).toString());
	}
	
	public String GetChanel()
	{
		return get(N_Chanel);
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
		return getTranID().isEmpty();
	}
	
	public String GetSqlDelayTime(){
		return this.get(N_SqlDelayTime);
	}
	
	public void SetSqlDelayTime(String sqlDelayTime){
		this.set(N_SqlDelayTime, sqlDelayTime);
	}

	@Override
	public Map<String, Object> GetFieldValuePair(String validateValue) {
		Map<String, Object> fieldValuePair = new HashMap<String,Object>();
		fieldValuePair.put(N_SystemID, getSystemID());
		fieldValuePair.put(N_IsClientSimu, GetMode());
		//fieldValuePair.put(N_TranCode, validateValue);
		fieldValuePair.put(N_TranName, validateValue);
		return fieldValuePair;
	}

	@Override
	public String GetTableName() {
		return "Transaction";
	}

}
