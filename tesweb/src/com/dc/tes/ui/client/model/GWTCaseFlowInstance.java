
package com.dc.tes.ui.client.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.extjs.gxt.ui.client.data.BaseModelData;

public class GWTCaseFlowInstance extends BaseModelData implements Serializable,IDistValidate {

	private static final long serialVersionUID = -901003913293727115L;
	
	public static String N_ID = "ID";
	public static String N_CASEFLOWID = "CASEFLOWID";
	public static String N_EXECUTELOGID = "EXECUTELOGID";
	public static String N_CREATETIME = "CREATETIME";
	public static String N_CASEFLOWPASSFLAG = "CASEFLOWPASSFLAG";

	public GWTCaseFlowInstance(){
		set(N_ID, null);
		set(N_CASEFLOWID, "");
		set(N_EXECUTELOGID, "");
		set(N_CREATETIME, null);
		set(N_CASEFLOWPASSFLAG, "");
	}
	public GWTCaseFlowInstance(String id,String CASEFLOWID,String EXECUTELOGID,String CREATETIME,String CASEFLOWPASSFLAG)
	{
		set(N_ID, id);
		set(N_CASEFLOWID, CASEFLOWID);
		set(N_EXECUTELOGID, EXECUTELOGID);
		set(N_CREATETIME, CREATETIME);
		set(N_CASEFLOWPASSFLAG, CASEFLOWPASSFLAG);
	}
	
	public String getID()
	{
		return this.get(N_ID);
	}
	
	public String getCASEFLOWID()
	{
		return this.get(N_CASEFLOWID);
	}
	
	public String getEXECUTELOGID()
	{
		return this.get(N_EXECUTELOGID);
	}
	
	public String getCASEFLOWPASSFLAG()
	{
		return this.get(N_CASEFLOWPASSFLAG);
	}
	
	@Override
	public String GetTableName() {
		// TODO Auto-generated method stub
		return "CaseFlowInstance";
	}
	@Override
	public Map<String, Object> GetFieldValuePair(String validateValue) {
		// TODO Auto-generated method stub
		Map<String, Object> fieldValuePair = new HashMap<String,Object>();
		fieldValuePair.put(N_ID, getID());
		//fieldValuePair.put(N_Name, validateValue);
		return fieldValuePair;
	}
}
