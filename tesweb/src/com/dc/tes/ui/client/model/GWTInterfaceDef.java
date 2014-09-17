package com.dc.tes.ui.client.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.extjs.gxt.ui.client.data.BeanModel;

public class GWTInterfaceDef extends BeanModel implements Serializable ,IDistValidate {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4530608432744724872L;
	public static String N_InterfaceID = "interfaceId";
	public static String N_SystemID = "systemId";
	public static String N_InterfaceName = "interfaceName";
	public static String N_ChineseName = "chineseName";
	public static String N_InterfaceLen = "interfaceLen";
	public static String N_FieldCount = "fieldCount";
	public static String N_ImportUserID = "importUserId";
	public static String N_UserName = "userName";
	public static String N_ImportTime = "importTime";
	public static String N_Memo = "memo";
	
	public GWTInterfaceDef(){}
	
	public GWTInterfaceDef(String systemId){
		this.set(N_SystemID, systemId);
		this.set(N_InterfaceName, "");
	}
	
	public GWTInterfaceDef(Integer interfaceID, String systemId, String interfaceName, String chineseName,
			Integer interfaceLen, Integer fieldCount, String importUserId,
			String importTime, String memo){
		this.set(N_InterfaceID, interfaceID);
		this.set(N_SystemID, systemId);
		this.SetValue(interfaceName, chineseName, interfaceLen, fieldCount, importUserId, importTime, memo);
	}
	
	public void SetValue(String interfaceName, String chineseName, Integer interfaceLen, Integer fieldCount,
			String importUserId, String importTime, String memo){
		this.set(N_InterfaceName, interfaceName);
		this.set(N_ChineseName, chineseName);
		this.set(N_InterfaceLen, interfaceLen);
		this.set(N_FieldCount, fieldCount);
		this.set(N_ImportUserID, importUserId);
		this.set(N_ImportTime, importTime);
		this.set(N_Memo, memo);
	}
	
	public String GetSystemID(){
		return this.get(N_SystemID);
	}
	
	public Integer GetInterfaceID(){
		return this.get(N_InterfaceID) == null ? null :
			Integer.parseInt(this.get(N_InterfaceID).toString());
	}
	
	public String GetInterfaceName(){
		return this.get(N_InterfaceName);
	}
	
	public String GetChineseName(){
		return this.get(N_ChineseName);
	}
	
	public Integer GetInterfaceLen(){
		return this.get(N_InterfaceLen) == null ? null :
			Integer.parseInt(this.get(N_InterfaceLen).toString());
	}
	
	public Integer GetFieldCount(){
		return this.get(N_FieldCount) == null ? null :
			Integer.parseInt(get(N_FieldCount).toString());
	}
	
	public String GetImportUserID(){
		return this.get(N_ImportUserID);
	}
	
	public String GetImportTime(){
		return this.get(N_ImportTime);
	}
	
	public String GetMemo(){
		return this.get(N_Memo);
	}
	
	public String GetUserName(){
		return this.get(N_UserName);
	}
	
	public void SetUserName(String name){
		this.set(N_UserName, name);
	}
	
	public boolean IsNew(){
		return GetInterfaceID() == null;
	}
	
	@Override
	public int hashCode() {
		return GetInterfaceID();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		GWTInterfaceDef other = (GWTInterfaceDef) obj;
		if (GetInterfaceID() == null) {
			if (other.GetInterfaceID() != null)
				return false;
		} else if (!GetInterfaceID().equals(other.GetInterfaceID()))
			return false;
		return true;
	}

	@Override
	public String GetTableName() {
		// TODO Auto-generated method stub
		return "InterfaceDef";
	}

	@Override
	public Map<String, Object> GetFieldValuePair(String validateValue) {
		// TODO Auto-generated method stub
		Map<String, Object> fieldValuePair = new HashMap<String,Object>();
		fieldValuePair.put(N_SystemID, GetSystemID());
		fieldValuePair.put(N_InterfaceName, validateValue);
		return fieldValuePair;
	}
}
