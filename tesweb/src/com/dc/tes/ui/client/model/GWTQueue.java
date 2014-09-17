package com.dc.tes.ui.client.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.extjs.gxt.ui.client.data.BaseModelData;

public class GWTQueue extends BaseModelData implements Serializable,IDistValidate{
	private static final long serialVersionUID = -4268887741291350344L;

	public static String N_ID = "id";
	public static String N_Name = "name";
	public static String N_Desc = "description";
	public static String N_SystemID = "systemId";
	public static String N_IsSet = "isset";
	
	public static String N_CreatedUserId = "createdUserId";
	public static String N_CreatedTime = "createdTime";
	public static String N_LastModifiedTime = "lastModifiedTime";
	public static String N_LastModifiedUserId = "lastModifiedUserId";
	
	public GWTQueue()
	{
		this("");
	}
	
	public GWTQueue(String sysId){
		this("","","",sysId);
	}
	
	public GWTQueue(String id,String name, String desc,String sysId) {
		this.set(N_ID, id);
		this.set(N_SystemID, sysId);
		SetBasicInfo(name, desc);
	}
	
	public void SetBasicInfo(String name, String desc)
	{
		this.set(N_Name, name);
		this.set(N_Desc, desc);
	}
	
	public String getID()
	{
		return this.get(N_ID);
	}
	
	public String getSystemID()
	{
		return this.get(N_SystemID);
	}
	
	public String getName()
	{
		return this.get(N_Name);
	}
	
	public String getDesc()
	{
		return this.get(N_Desc);
	}
	
	public void setIsSet(String script)
	{
		this.set(N_IsSet,!(script == null || script.isEmpty()));
	}
	
	public boolean getIsSet()
	{
		return Boolean.valueOf(get(N_IsSet).toString());
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
	
	@Override
	public Map<String, Object> GetFieldValuePair(String validateValue) {
		Map<String, Object> fieldValuePair = new HashMap<String,Object>();
		fieldValuePair.put(N_SystemID, getSystemID());
		fieldValuePair.put(N_Name, validateValue);
		return fieldValuePair;
	}

	@Override
	public String GetTableName() {
		return "ExecuteSet";
	}
}
