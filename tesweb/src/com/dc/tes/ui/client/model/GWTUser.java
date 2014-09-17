package com.dc.tes.ui.client.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.dc.tes.ui.client.common.TypeTranslate;
import com.extjs.gxt.ui.client.data.BaseModelData;

public class GWTUser  extends BaseModelData implements Serializable,IDistValidate{
	/**
	 * 序列化版本号
	 */
	private static final long serialVersionUID = -3807351568519244088L;
	
	public static String N_id = "id";
	public static String N_name = "name";
	public static String N_description = "description";
	public static String N_password = "password";
	public static String N_isAdmin = "isAdmin";
	public static String N_isAdmin_CHS = "isAdminCHS";
	public static String N_flag = "flag";
	
	public static String N_CreatedUserId = "createdUserId";
	public static String N_CreatedTime = "createdTime";
	public static String N_LastModifiedTime = "lastModifiedTime";
	public static String N_LastModifiedUserId = "lastModifiedUserId";
	
	public GWTUser()
	{
		this("","","","",1,1);
	}
	
	public GWTUser(String id, String name)
	{
		this.set(N_id, id);
		this.set(N_name, name);
	}
	
	public GWTUser(String id,String name,String description,String password,int isAdmin,int flag)
	{
		this.set(N_id, id);
		this.set(N_flag, flag);
		SetValue(name, description, password, isAdmin);
	}
	
	public void SetValue(String name, String description, String password, int isAdmin)
	{
		this.set(N_name, name);
		this.set(N_description, description);
		this.set(N_password, password);
		this.set(N_isAdmin, isAdmin);
		this.set(N_isAdmin_CHS, TypeTranslate.Int_Admin_CHS(isAdmin));
	}
	
	public String getUserID()
	{
		return get(N_id);
	}
	public String getUserName()
	{
		return get(N_name);
	}
	public String getDescription()
	{
		return get(N_description);
	}
	public String getPassword()
	{
		return get(N_password);
	}
	public int getIsAdmin()
	{
		return Integer.valueOf(get(N_isAdmin).toString());
	}
	public int getFlag()
	{
		return Integer.valueOf(get(N_flag).toString());
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
	
	
	public String toString()
	{
		return getUserID();
	}
	
	public boolean IsNew()
	{
		return getUserID().isEmpty();
	}

	@Override
	public Map<String, Object> GetFieldValuePair(String validateValue) {
		Map<String, Object> fieldValuePair = new HashMap<String,Object>();
		fieldValuePair.put(N_name, validateValue);
		return fieldValuePair;
	}

	@Override
	public String GetTableName() {
		return "User";
	}
	
}
