package com.dc.tes.ui.client.model;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import com.extjs.gxt.ui.client.data.BaseModelData;

public class GWTRecordedCase extends BaseModelData implements Serializable,IDistValidate {

	private static final long serialVersionUID = 1820111209625071921L;
	
	public static String N_id = "id";
	public static String N_SystemId = "systemId";
	public static String N_RequestMsg = "requestMsg"; // 请求报文
	public static String N_ResponseMsg = "responseMsg"; // 应答报文
	public static String N_ResponseFlag = "responseFlag"; //是否收到了应答
	public static String N_ResponseFlagStr = "responseFlagStr";
	public static String N_RecordUserId = "recordUserId";
	public static String N_RecordUserName = "recordUserName";
	public static String N_RecordTime = "recordTime";
	public static String N_CreateTime = "createTime";
	public static String N_Memo = "memo"; // 说明
	public static String N_IsCased = "isCased";
	public static String N_IsCasedStr = "isCasedStr";
	public static String N_LastModifiedTime = "lastModifiedTime";
	public static String N_LastModifiedUserId = "lastModifiedUserId";
	
	public GWTRecordedCase() {
	}
	
	public GWTRecordedCase(int id) {		
		this(id,"","","","",0,0);	
	}
	
	public GWTRecordedCase(int id, String systemid) {		
		this(id,"","",systemid,"", 0, 0);	
	}

	public GWTRecordedCase(int id, String requestMsg, String responseMsg, String systemid, String memo, int responseFlag, int isCased) {
		this.set(N_id, id);
		this.set(N_SystemId, systemid);
		this.SetValue(requestMsg, responseMsg, memo, responseFlag, isCased);
	}
	
	public GWTRecordedCase(int id, String requestMsg, String responseMsg, String systemid, String memo, int responseFlag, int isCased, int recordUserId, Date recordTime, String createTime, String recordUserName) {
		this.set(N_id, id);
		this.set(N_SystemId, systemid);
		this.SetValue(requestMsg, responseMsg, memo, responseFlag, isCased);
		this.set(N_RecordTime, recordTime);
		this.set(N_CreateTime, createTime);
		this.set(N_RecordUserId, recordUserId);
		this.set(N_RecordUserName, recordUserName);		
	}
	
	public void SetValue(String requestMsg,String responseMsg, String memo, int responseFlag, int isCased) {
		this.set(N_RequestMsg, requestMsg);
		this.set(N_ResponseMsg, responseMsg);
		this.set(N_Memo, memo == null? "":memo);
		this.set(N_ResponseFlag, responseFlag);
		this.set(N_IsCased, isCased);
		this.set(N_ResponseFlagStr, responseFlag==1?"是":"否");
		this.set(N_IsCasedStr, isCased==1?"是":"否");	
	}
	
	public void SetValue(String memo, int isCased) {
		this.set(N_Memo, memo);
		this.set(N_IsCased, isCased);
	}

	
	public String getID() {
		return this.get(N_id).toString();
	}
	
	public boolean IsNew() {
		return getID().isEmpty();
	}
	
	public String getRequestMsg() {
		return this.get(N_RequestMsg).toString();
	}

	public String getResponseMsg() {
		return this.get(N_ResponseMsg).toString();
	}
		
	public String getSystemID() {
		return this.get(N_SystemId).toString();	
	}
	
	public String getMemo() {
		return this.get(N_Memo);
	}
	
	public Integer getResponseFlag(){
		return Integer.parseInt(get(N_ResponseFlag).toString());
	}
	
	public String getResponseFlagStr(){
		return getResponseFlag()==1 ? "是" : "否";
	}

	public Integer getIsCased(){
		return Integer.parseInt(get(N_IsCased).toString());
	}
	
	public String getIsCasedStr(){
		return getIsCased()==1 ? "是" : "否";
	}
	
	public String GetRecordUserId(){
		return get(N_RecordUserId).toString();
	}

	public void SetRecordUserId(int recordUserId){
		this.set(N_RecordUserId, recordUserId);
	}
	
	public String GetRecordUserName(){
		return this.get(N_RecordUserName);
	}
	
	void SetRecordUserName(String recordUserName){
		this.set(N_RecordUserName, recordUserName);
	}
	
	public String GetCreateTime(){
		return get(N_CreateTime).toString();
	}

	public String GetRecordTime(){
		return get(N_RecordTime).toString();
	}

	public void SetRecordTime(Date recordTime){
		this.set(N_RecordTime, recordTime);
	}
	
	public String GetLastModifiedTime(){
		return this.get(N_LastModifiedTime);
	}
	
	public void SetLastModifiedTime(String lastModifiedTime){
		this.set(N_LastModifiedTime, lastModifiedTime);
	}
	
	public String GetLastModifiedUserId(){
		return this.get(N_LastModifiedUserId);
	}
	
	public void SetLastModifiedUserId(String lastModifiedUserId){
		this.set(N_LastModifiedUserId, lastModifiedUserId);
	}
	
	@Override
	public String GetTableName() {
		return "RecordedCase";
	}

	@Override
	public Map<String, Object> GetFieldValuePair(String validateValue) {
		Map<String, Object> fieldValuePair = new HashMap<String,Object>();
		fieldValuePair.put(N_SystemId, get(N_SystemId));
		fieldValuePair.put(N_RequestMsg, validateValue);
		return fieldValuePair;
	}
	
	
}
