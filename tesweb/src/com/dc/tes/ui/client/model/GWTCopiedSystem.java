package com.dc.tes.ui.client.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.extjs.gxt.ui.client.data.BaseModelData;

public class GWTCopiedSystem extends BaseModelData implements Serializable, IDistValidate{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4967986176857603655L;

	public static String N_ID = "id";
	public static String N_SystemName = "systemName";
	public static String N_SystemNo = "systemNo";
	public static String N_OldSystemID = "oldSystemId";
	public static String N_NewSystemId = "newSystemId";
	public static String N_UserID = "userId";
	
	public GWTCopiedSystem(){}
	
	public GWTCopiedSystem(int Id){
		this.set(N_ID, Id);
	}
	
	public GWTCopiedSystem(String systemName, String systemNo, int oldSystemId){
		this.set(N_SystemName, systemName);
		this.set(N_SystemNo, systemNo);
		this.set(N_OldSystemID, oldSystemId);
	}
	
	public GWTCopiedSystem(String systemName, String systemNo, int oldSystemId, int newSystemId){
		this.set(N_SystemName, systemName);
		this.set(N_SystemNo, systemNo);
		this.set(N_OldSystemID, oldSystemId);
		this.set(N_NewSystemId, newSystemId);
	}
	
	public GWTCopiedSystem(int Id, String systemName, String systemNo, int oldSystemId, int newSystemId){
		this.set(N_ID, Id);
		this.set(N_SystemName, systemName);
		this.set(N_SystemNo, systemNo);
		this.set(N_OldSystemID, oldSystemId);
		this.set(N_NewSystemId, newSystemId);
	}
	
	public void SetValue(int oldSystemId) {
		this.set(N_OldSystemID, oldSystemId);
	}

	public void SetValue(String systemName, String systemNo) {
		this.set(N_SystemName, systemName);
		this.set(N_SystemNo, systemNo);
	}
	
	public void SetValue(String systemName, String systemNo, Integer oldSystemId) {
		this.set(N_SystemName, systemName);
		this.set(N_SystemNo, systemNo);
		this.set(N_OldSystemID, oldSystemId);
	}
	
	public Integer GetID(){
		return this.get(N_ID);
	}
	
	public Integer GetOldSystemID(){
		return this.get(N_OldSystemID);
	}
	
	public Integer GetNewSystemID(){
		return this.get(N_NewSystemId);
	}
	
	public String GetSystemName(){
		return this.get(N_SystemName);
	}

	public String GetSystemNo(){
		return this.get(N_SystemNo);
	}
	
	public boolean isNew(){
		return GetSystemName()==null;
	}
	
	@Override
	public String GetTableName() {
		return "Copied_System";
	}

	@Override
	public Map<String, Object> GetFieldValuePair(String validateValue) {
		// TODO Auto-generated method stub
		Map<String, Object> fieldValuePair = new HashMap<String,Object>();
		fieldValuePair.put(N_ID, get(N_ID));
		fieldValuePair.put(N_SystemName, validateValue);
		return fieldValuePair;
	}
}
