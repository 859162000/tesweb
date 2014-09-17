package com.dc.tes.ui.client.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.extjs.gxt.ui.client.data.BaseModelData;
/**
 * 持久化参数
 * @author scckobe
 *
 */
public class GWTPersistentData extends BaseModelData implements Serializable,IDistValidate {
	private static final long serialVersionUID = 7501192247764305117L;

	/**
	 * 持久化数据表 ID
	 */
	public static String N_ID = "id";
	/**
	 * 关联的系统 ID
	 */
	public static String N_SystemID = "systemid";
	/**
	 * 参数名称
	 */
	public static String N_Parameter = "parameter";
	/**
	 * 当前值
	 */
	public static String N_Curvalue = "curvalue";
	/**
	 * 参数类型
	 */
	public static String N_Type = "type";
	/**
	 * 参数类型中文
	 */
	public static String N_TypeStr = "typeStr";
	
	public GWTPersistentData()
	{
		this("","","","",0);
	}
	
	public GWTPersistentData(String systemID)
	{
		this("",systemID,"","",1);
	}
	
	public GWTPersistentData(String id, String systemID,String parameter,String curvalue,int type)
	{
		this.set(N_ID, id);
		this.set(N_SystemID, systemID);
		SetValue(parameter, curvalue, type);
	}
	
	public void SetValue(String parameter,String curvalue,int type)
	{
		this.set(N_Parameter, parameter);
		this.set(N_Curvalue, curvalue);
		this.set(N_Type,type);
		this.set(N_TypeStr,(this.getType() == 0) ? "字符" : "数字");
	}
	
	public String getID()
	{
		return get(N_ID).toString();
	}
	
	public String getSystemID()
	{
		return get(N_SystemID).toString();
	}
	
	public String getParameter()
	{
		return get(N_Parameter).toString();
	}
	
	public String getCurvalue()
	{
		return get(N_Curvalue).toString();
	}
	
	public int getType()
	{
		return Integer.parseInt(get(N_Type).toString());
	}
	
	public boolean IsNew()
	{
		return getID().isEmpty();
	}
	
	@Override
	public Map<String, Object> GetFieldValuePair(String validateValue) {
		Map<String, Object> fieldValuePair = new HashMap<String,Object>();
		fieldValuePair.put(N_SystemID, get(N_SystemID));
		fieldValuePair.put(N_Parameter, validateValue);
		return fieldValuePair;
	}

	@Override
	public String GetTableName() {
		return "PersistentData";
	}
}
