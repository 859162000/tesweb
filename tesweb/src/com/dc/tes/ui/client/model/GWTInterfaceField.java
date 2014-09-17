package com.dc.tes.ui.client.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.data.ChangeEventSource;

public class GWTInterfaceField extends BeanModel implements Serializable, IDistValidate{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6556959069224465523L;
	public static String N_FieldID = "fieldId";
	public static String N_InterfaceDefID = "interfaceDefId";
	public static String N_Sequence = "sequence";
	public static String N_FieldName = "fieldName";
	public static String N_ChineseName = "chineseName";
	public static String N_FieldTypeExpr = "fieldTypeExpr";
	public static String N_FieldType = "fieldType";
	public static String N_FieldLen = "fieldLen";
	public static String N_DecimalDigits = "decimalDigits";
	public static String N_Optional = "optional";
	public static String N_DefaultValue = "defaultValue";
	public static String N_Memo = "memo";
	
	public GWTInterfaceField(){}
	
	public GWTInterfaceField(Integer defID){
		this.set(N_InterfaceDefID, defID.toString());
		this.set(N_FieldName, "");
	}
	
	public GWTInterfaceField(Integer fieldId, Integer interfaceDefID,
			Integer sequence, String fieldName, String chineseName, String fieldTypeExpr,
			String fieldType, Integer fieldLen, Integer decimalDigits,
			String optional, String defaultValue, String memo){
		this.set(N_FieldID, fieldId);
		this.set(N_InterfaceDefID, interfaceDefID);
		this.SetValue(sequence, fieldName, chineseName, fieldTypeExpr, fieldType, fieldLen, 
				decimalDigits, optional, defaultValue, memo);
	}
	
	public void SetValue(Integer sequence, String fieldName, String chineseName, String fieldTypeExpr,
			String fieldType, Integer fieldLen, Integer decimalDigits,
			String optional, String defaultValue, String memo){
		this.set(N_Sequence, sequence);
		this.set(N_FieldName, fieldName);
		this.set(N_ChineseName, chineseName);
		this.set(N_FieldTypeExpr, fieldTypeExpr);
		this.set(N_FieldType, fieldType);
		this.set(N_FieldLen, fieldLen);
		this.set(N_DecimalDigits, decimalDigits);
		this.set(N_Optional, optional);
		this.set(N_DefaultValue, defaultValue);
		this.set(N_Memo, memo);
	}
	
	public void SetSequence(int i){
		this.set(N_Sequence, i);
	}
	
	public Integer GetFieldID(){
		return this.get(N_FieldID) == null ? null :
			Integer.parseInt(this.get(N_FieldID).toString());
	}
	
	public Integer GetInterfaceDefID(){
		return this.get(N_InterfaceDefID) == null ? null :
			Integer.parseInt(this.get(N_InterfaceDefID).toString());
	}
	
	public Integer GetSequence(){
		return this.get(N_Sequence) == null ? null :
			Integer.parseInt(this.get(N_Sequence).toString());
	}
	
	public String GetFieldName(){
		return this.get(N_FieldName);
	}
	
	public String GetFieldTypeExpr(){
		return this.get(N_FieldTypeExpr);
	}
	
	public String GetFieldType(){
		return this.get(N_FieldType);
	}
	
	public Integer GetFieldLen(){
		return this.get(N_FieldLen) == null ? null : 
			Integer.parseInt(this.get(N_FieldLen).toString());
	}
	
	public Integer GetDecimalDigits(){
		return this.get(N_DecimalDigits) == null ? null : 
			Integer.parseInt(this.get(N_DecimalDigits).toString());
	}
	
	public String GetOptional(){
		return this.get(N_Optional);
	}
	
	public String GetDefaultValue(){
		return this.get(N_DefaultValue);
	}
	
	public String GetMemo(){
		return this.get(N_Memo);
	}
	
	public String GetChineseName(){
		return this.get(N_ChineseName);
	}
	
	
	@Override
	public int hashCode() {
		return GetFieldID();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		GWTInterfaceField other = (GWTInterfaceField) obj;
		if (this.GetFieldID() == null) {
			if (other.GetFieldID() != null)
				return false;
		} else if (this.GetFieldID().intValue() != other.GetFieldID().intValue())
			return false;
		return true;
	}
	
	public boolean IsNew(){
		return GetFieldID() == null;
	}

	@Override
	public String GetTableName() {
		// TODO Auto-generated method stub
		return "InterfaceField";
	}

	@Override
	public Map<String, Object> GetFieldValuePair(String validateValue) {
		// TODO Auto-generated method stub
		Map<String, Object> fieldValuePair = new HashMap<String,Object>();
		fieldValuePair.put(N_InterfaceDefID, GetInterfaceDefID());
		fieldValuePair.put(N_FieldName, validateValue);
		return fieldValuePair;
	}
	
}
