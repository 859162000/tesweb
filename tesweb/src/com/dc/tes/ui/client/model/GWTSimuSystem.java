package com.dc.tes.ui.client.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.extjs.gxt.ui.client.data.BaseModelData;

public class GWTSimuSystem extends BaseModelData implements Serializable,IDistValidate {
	private static final long serialVersionUID = -3692493832596028062L;

	public static String N_SystemID = "systemId";
	public static String N_StateFlag = "flag";
	public static String N_SystemNo = "systemNo";
	public static String N_SystemName = "systemName";
	public static String N_IP = "IP";
	public static String N_Port = "Port";
	public static String N_Desc = "desc";
	public static String N_Channel = "Channel";
	public static String N_NeedSqlCheck	= "needSqlCheck";
	public static String N_TransactionTimeOut = "transactionTimeOut";
	public static String N_SqlGetMethod = "sqlGetMethod";
	public static String N_SqlGetDbAddr	= "sqlGetDbAddr";
	public static String N_Encoding4RequestMsg = "encoding4RequestMsg";
	public static String N_Encoding4ResponseMsg = "encoding4ResponseMsg";
	public static String N_IsClient = "isClientSimu";
	public static String N_IsSync = "isSyncComm";
	public static String N_UseSameResponseStruct = "useSameResponseStruct";
	public static String N_ResponseStruct = "responseStruct";
	public static String N_ResponseModeStr = "responseModeStr";
	public static String N_ResponseMode = "responseMode";
	public static String N_ReqUnPackerID = "reqMsgUnpackerId";
	public static String N_ResUnPackerID = "resMsgUnpackerId";
	public static String N_CreatedUserId = "createdUserId";
	public static String N_CreatedTime = "createdTime";
	public static String N_LastModifiedTime = "lastModifiedTime";
	public static String N_LastModifiedUserId = "lastModifiedUserId";
	
	
	public GWTSimuSystem()
	{
		this("","","","","",8080,"",1,0, 0, 0, "", "", "", 1, 1, 0, "0", "", "");
	}
	
	public GWTSimuSystem(String systemID,String systemNo,String systemName,
			String desc,String IP,int Port,String chanelName,int stateFlag,
			int needSqlCheck, int transactionTimeOut, int sqlGetMethod, 
			String sqlGetDbAddr, String encoding4RequestMsg, String encoding4ResponseMsg,
			int isClient, int isSync, int useSameStruct, String responseMode, 
			String reqMsgUnpackerId, String resMsgUnpackerId)
	{
		this.set(N_SystemID, systemID);
		this.set(N_StateFlag, stateFlag);
		SetValue(systemNo,systemName,desc,IP,Port,chanelName, 
				needSqlCheck, transactionTimeOut, sqlGetMethod,
				sqlGetDbAddr, encoding4RequestMsg, encoding4ResponseMsg,
				isClient, isSync, useSameStruct, responseMode,
				reqMsgUnpackerId, resMsgUnpackerId);
	}
	
	public void SetValue(String systemNo,String systemName,String desc,String IP,
			int Port,String chanelName, int needSqlCheck, int transactionTimeOut,
			int sqlGetMethod, String sqlGetDbAddr, String encoding4RequestMsg, String encoding4ResponseMsg, 
			int isClient, int isSync, int useSameStruct, String responseMode, 
			String reqMsgUnpackerId, String resMsgUnpackerId)
	{
		this.set(N_SystemNo, systemNo);
		this.set(N_SystemName, systemName);
		this.set(N_Desc, desc == null ? "" : desc);
		this.set(N_IP, IP);
		this.set(N_Port, Port);
		this.set(N_Channel, chanelName);
		this.set(N_NeedSqlCheck, needSqlCheck);
		this.set(N_TransactionTimeOut, transactionTimeOut);
		this.set(N_SqlGetMethod, sqlGetMethod);
		this.set(N_SqlGetDbAddr, sqlGetDbAddr);
		this.set(N_Encoding4RequestMsg, encoding4RequestMsg);
		this.set(N_Encoding4ResponseMsg, encoding4ResponseMsg);
		this.set(N_IsClient, isClient);
		this.set(N_IsSync, isSync);
		this.set(N_UseSameResponseStruct, useSameStruct);	
		
		this.set(N_ResponseMode, responseMode);
		
		String typeStr = "";
		if(responseMode != null && !responseMode.isEmpty()) {
			switch(Integer.parseInt(responseMode)) {
			case 0:typeStr = "使用默认案例的应答报文";break;
			case 1:typeStr = "使用交易的应答报文解析返回";break;
			case 2:typeStr = "根据录制报文匹配返回";break;
			case 3:typeStr = "根据案例实例匹配返回";break;
			}
		}
		this.set(N_ResponseModeStr, typeStr);
		this.set(N_ReqUnPackerID, reqMsgUnpackerId);
		this.set(N_ResUnPackerID, resMsgUnpackerId);
	}
	
	public void SetSytemID(long systemID)
	{
		this.set(N_SystemID, systemID);
	}
	
	public String GetSystemID()
	{
		return get(N_SystemID);
	}
	
	public String GetSystemNo()
	{
		return  get(N_SystemNo);
	}
	
	public String GetSystemName()
	{
		return  get(N_SystemName);
	}
	
	public String GetDesc()
	{
		return  get(N_Desc);
	}
	
	public int GetFlag()
	{
		return Integer.parseInt((get(N_StateFlag).toString()));
	}
	
	public String GetIP()
	{
		return get(N_IP);
	}
	
	public int GetPort()
	{
		return Integer.valueOf(get(N_Port).toString());
	}
	
	public String GetChanel()
	{
		return get(N_Channel);
	}

	public int GetNeedSqlCheck(){
		return Integer.valueOf(get(N_NeedSqlCheck).toString());
	}
	
	public int GetTransactionTimeOut(){
		return Integer.valueOf(get(N_TransactionTimeOut).toString());
	}
	
	public int GetSqlGetMethod(){
		return Integer.valueOf(get(N_SqlGetMethod).toString());
	}
	
	public String GetSqlGetDbAddr(){
		return get(N_SqlGetDbAddr);
	}
	
	public String GetEncoding4RequestMsg(){
		return this.get(N_Encoding4RequestMsg);
	}
	
	public String GeteEnoding4ResponseMsg(){
		return this.get(N_Encoding4ResponseMsg);
	}
	
	public int GetIsClient() {
		return Integer.valueOf(this.get(N_IsClient).toString());
	}
	
	public int GetIsSync() {
		return Integer.valueOf(this.get(N_IsSync).toString());
	}
	
	public int GetUseSameStruct() {
		return Integer.valueOf(this.get(N_UseSameResponseStruct).toString());
	}
	
	public String GetResponseStruct() {
		return this.get(N_ResponseStruct);
	}
	
	public void SetResponseStruct(String responseStruct) {
		this.set(N_ResponseStruct, responseStruct);
	}
	
	public String GetResponseMode() {
		return this.get(N_ResponseMode);
	}
	
/*	public void SetResponseMode(String responseMode) {
		this.set(N_ResponseMode, responseMode);
		SetResponseModeStr(responseMode);
	}*/

	public String GetResponseModeStr() {
		return this.get(N_ResponseModeStr);
	}
	
	/*public void SetResponseModeStr(String responseMode) {
		String typeStr = "";
		if(responseMode != null && !responseMode.isEmpty()) {
			switch(Integer.parseInt(responseMode)) {
			case 0:typeStr = "使用默认案例的应答报文";break;
			case 1:typeStr = "使用交易的应答报文解析返回";break;
			case 2:typeStr = "根据录制报文匹配返回";break;
			case 3:typeStr = "根据案例实例匹配返回";break;
			}
		}
		this.set(N_ResponseModeStr, typeStr);
	}*/
	
	
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
	
	public String GetReqUnPackerID(){
		return this.get(N_ReqUnPackerID);
	}
	
	public String GetResUnPackerID(){
		return this.get(N_ResUnPackerID);
	}
	public String toString() {
		return GetSystemID();
	}
	
	public boolean IsNew() {
		return GetSystemID().isEmpty();
	}

	@Override
	public Map<String, Object> GetFieldValuePair(String validateValue) {
		Map<String, Object> fieldValuePair = new HashMap<String,Object>();
		fieldValuePair.put(N_SystemName, validateValue);
		return fieldValuePair;
	}

	@Override
	public String GetTableName() {
		return "SysType";
	}
}
