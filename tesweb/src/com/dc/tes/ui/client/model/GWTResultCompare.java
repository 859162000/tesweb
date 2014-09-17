package com.dc.tes.ui.client.model;

import java.io.Serializable;

import com.extjs.gxt.ui.client.data.BaseModelData;

public class GWTResultCompare extends BaseModelData implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7101658543397370604L;
	public static String N_CaseName = "caseName";
	public static String N_ParamName = "paramName";
	public static String N_ParamDesc = "paramDesc";
	public static String N_ParamType = "paramType";
	public static String N_CompareCondition = "compareCondition";
	public static String N_ExpVal = "expVal";
	public static String N_RealVal = "realVal";
	public static String n_RealSql = "realSql";
	public static String N_IsEqual = "isEqual";
	
	
	public GWTResultCompare(){};
	public GWTResultCompare(String caseName, String paramName, 
			String paramDesc, String paramType, String compareCondition, String expVal,
			String realVal, String isEqual){
		this.set(N_CaseName, caseName);
		this.set(N_ParamName, paramName);
		this.set(N_ParamDesc, paramDesc);
		this.set(N_ParamType, paramType);
		this.set(N_CompareCondition, compareCondition);
		this.set(N_ExpVal, expVal);
		this.set(N_RealVal, realVal);
		this.set(N_IsEqual, isEqual);		
	}
	
	public String GetCaseName(){
		return this.get(N_CaseName);
	}
	
	public void SetCaseName(String caseName){
		this.set(N_CaseName, caseName);
	}
	
	public String GetParamName(){
		return this.get(N_ParamName);
	}
	
	public void SetParamName(String paramName){
		this.set(N_ParamName, paramName);
	}
	
	public String GetParamDesc(){
		return this.get(N_ParamDesc);
	}
	
	public void SetParamDesc(String paramDesc){
		this.set(N_ParamDesc, paramDesc);
	}
	
	public String GetParamType(){
		return this.get(N_ParamType);
	}
	
	public void SetParamType(String paramType){
		this.set(N_ParamType, paramType);
	}
	
	public void SetCompareCondition(String compareCondition){
		this.set(N_CompareCondition, compareCondition);
	}
	
	public String GetCompareCondition(){
		return this.get(N_CompareCondition);
	}
	public String GetExpVal(){
		return this.get(N_ExpVal);
	}
	
	public void SetExpVal(String expVal){
		this.set(N_ExpVal, expVal);
	}
	
	public String GetRealVal(){
		return this.get(N_RealVal);
	}
	
	public void SetRealVal(String realVal){
		this.set(N_RealVal, realVal);
	}
	
	public void SetRealSql(String realSql){
		this.set(n_RealSql, realSql);
	}
	
	public String GetRealSql(){
		return this.get(n_RealSql);
	}
	
	public String GetIsEqual(){
		return this.get(N_IsEqual);
	}
	
	public void SetIsEqual(String isEqual){
		this.set(N_IsEqual, isEqual);
	}
}
