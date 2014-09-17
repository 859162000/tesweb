package com.dc.tes.ui.client.model;

import java.io.Serializable;

import com.dc.tes.ui.client.common.TypeTranslate;
import com.extjs.gxt.ui.client.data.BaseModelData;

public class GWTCase extends BaseModelData implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -3660807497684146542L;
	
	public static String N_caseId = "caseId";
	public static String N_caseName = "caseName";
	public static String N_isParseable = "isParseable";
	public static String N_isParaseCHS = "isParseableCHS";
	public static String N_DataState = "dataState";
	public static String N_flag = "flag";
	public static String N_transactionId = "transactionId";
	public static String N_caseNo = "caseNo";
	public static String N_cardNo = "cardNo";
	public static String N_tranType = "tranType";
	public static String N_caseFlowNo = "caseFlowNo";
	public static String N_caseFlowName = "caseFlowName";
	public static String N_amount = "amount";
	public static String N_Desc = "description";
	public static String N_BreakPointFlag = "breakPointFlag";
	public static String N_BreakPointFlagStr = "breakPointFlagStr";
	public static String N_Sequence = "sequence";
	public static final String N_default = "isdefault";
	
	private GWTTransaction N_tran;
	private GWTCaseFlow caseFlow;
	
	public static String N_CreatedUserId = "createdUserId";
	public static String N_CreatedTime = "createdTime";
	public static String N_LastModifiedTime = "lastModifiedTime";
	public static String N_LastModifiedUserId = "lastModifiedUserId";
	
	public GWTCase()
	{
	}
	
	public GWTCase(String transactionId)
	{
		this("",transactionId,"",1,0,"","","","", "", "","","", 0);
		
	}
	
	public void SetEditValue(String caseNo, String caseName, String tranInfo, String sequence, String desc, GWTCaseFlow gwtCaseFlow){
		this.set(N_caseNo, caseNo);
		this.set(N_caseName, caseName);
		this.set(N_transactionId, tranInfo);
		this.set(N_Desc, desc);
		this.set(N_Sequence, sequence);
		this.caseFlow = gwtCaseFlow;
	}
	
	public GWTCase(String caseId,
			String transactionId ,
			String caseName,
			int isParseable,
			int flag,
			String caseNum,
			String cardNum,
			String tranType,
			String busiNum,
			String busiName,
			String amount,
			String sequence,
			String desc,
			int isDefault)
	{
		this.set(N_caseId, caseId);
		this.set(N_flag, flag);
		this.set(N_transactionId, transactionId);
		this.set(N_caseNo, caseNum);
		this.set(N_cardNo, cardNum);
		this.set(N_tranType, tranType);
		this.set(N_caseFlowNo, busiNum);
		this.set(N_caseFlowName, busiName);
		this.set(N_amount, amount);
		this.set(N_Sequence, sequence);
		this.set(N_Desc, desc);
		SetValue(caseName,isParseable, isDefault);
	}
	
	public GWTTransaction getN_tran() {
		return N_tran;
	}

	public void setN_tran(GWTTransaction n_tran) {
		N_tran = n_tran;
	}

	public String GetAmount() {
		return this.get(N_amount);
	}
	
	public String GetBusiNum(){
		return this.get(N_caseFlowNo);
	}
	
	public String GetBusiName(){
		return this.get(N_caseFlowName);
	}
	public String GetTranType()
	{
		return this.get(N_tranType);
	}
	
	public void SetTranType(String tranType)
	{
		this.set(N_tranType, tranType);
	}
	
	public String GetSequence(){
		return this.get(N_Sequence);
	}

	public String GetDesc() {
		return this.get(N_Desc);
	}

	public void SetValue(String caseName,int isParseable, int isDefault)
	{
		this.set(N_caseName, caseName);
		this.set(N_isParseable, isParseable);
		this.set(N_isParaseCHS,TypeTranslate.Int_Parse_CHS(isParseable));
		SetDefault(isDefault);
	}
	
	public String GetCaseId()
	{
		return this.get(N_caseId);
	}
	
	public String GetCaseName()
	{
		return this.get(N_caseName);
	}
	
	public int GetCaseParse()
	{
		return Integer.valueOf(get(N_isParseable).toString());
	}
	
	public String GetCaseNo(){
		return this.get(N_caseNo);
	}
	
	public String GetCardNo(){
		return this.get(N_cardNo);
	}

	public void SetResponseData(byte[] responseData)
	{
		if(responseData == null || responseData.length == 0)
			this.set("res",false);
		else
			this.set("res",true);
		SetDataState();
	}
	
	public boolean GetResponseData()
	{
		if(this.get("res") != null && !String.valueOf(this.get("res")).isEmpty())
			return (Boolean)this.get("res");
		else
			return false;
	}
	
	private void SetDataState()
	{
		String state = "组包";
		if(GetCaseParse() == 0)
		{
			if(GetResponseData())
				state = "已上传";
			else
				state = "未上传";
		}
		this.set(N_DataState, state);
	}
	

	public int GetFlag()
	{
		return Integer.valueOf(get(N_flag).toString());
	}
	
	public String GetTransactionID()
	{
		return this.get(N_transactionId);
	}
	
	public boolean IsNew()
	{
		return GetCaseId().isEmpty();
	}
//
//	@Override
//	public Map<String, Object> GetFieldValuePair(String validateValue) {
//		Map<String, Object> fieldValuePair = new HashMap<String,Object>();
//		fieldValuePair.put(N_importBatchNo, GetImportBatchNo());
//		fieldValuePair.put(N_caseName, validateValue);
//		return fieldValuePair;
//	}
//
//	@Override
//	public String GetTableName() {
//		return "Case";
//	}

	public GWTCaseFlow getCaseFlow() {
		return caseFlow;
	}

	public void setCaseFlow(GWTCaseFlow caseFlow) {
		this.caseFlow = caseFlow;
	}
	
	public void setBreakPointFlag(Integer breakPointFlag){
		this.set(N_BreakPointFlag, breakPointFlag);
		this.set(N_BreakPointFlagStr, getBreakPointFlagStr());
	}
	
	public String getBreakPointFlag(){
		return this.get(N_BreakPointFlag)==null? null:this.get(N_BreakPointFlag).toString();
	}
	
	public String getBreakPointFlagStr(){
		if(getBreakPointFlag()==null){
			return "";
		}
		int flag = Integer.parseInt(getBreakPointFlag());
		switch(flag){
		case 0: return "无";
		case 1: return "有";
		default: return "";
		}
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
	
	
	public boolean GetDefaultBool()
	{
		return GetDefault() == 1;
	}
	
	public int GetDefault()
	{
		return Integer.valueOf(get(N_default).toString());
	}
	
	public void SetDefault(int isDefault)
	{
		set(N_default,isDefault);
	}
}
