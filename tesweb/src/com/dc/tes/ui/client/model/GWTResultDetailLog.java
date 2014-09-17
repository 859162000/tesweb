package com.dc.tes.ui.client.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.extjs.gxt.ui.client.data.BaseModelData;

public class GWTResultDetailLog extends BaseModelData implements Serializable,
IDistValidate {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// tes_case_instance
	public static String N_ID="id";			
	public static String N_CaseID = "caseId";		
	public static String N_ExecuteLogID = "executeLogId";
	public static String N_TransactionId = "transactionId";
	public static String N_CmbHost = "cmbHost";		
	public static String N_CaseFlowInstanceID = "caseFlowInstanceID";		
	public static String N_Field37 = "field37";		
	public static String N_CasePassFlag = "casePassFlag";		
	public static String N_ReceivedReplayFlag = "receivedReplayFlag";	 
	public static String N_Value4NextCase = "value4NextCase";		
	public static String N_XmlContent = "xmlContent";				
	public static String N_OracleContent = "oracleContent";		
	public static String N_ResponContent = "responContent";  
	public static String N_RequesContent = "requesContent";
	// tes_caseflow_instance
	public static String N_CaseFlowID="caseFlowId";
	public static String N_CaseFlowPassFlag="caseFlowPassFlag";
	public static String N_CaseFlowName="caseFlowName";
	public static String N_CaseFlowNo="caseFlowNo";
	
	public static String N_BreakPointFlag="breakPointFlag";
	public static String N_BreakPointFlagStr="breakPointFlagStr";
	public static String N_BeginRunTime = "beginRunTime";
	public static String N_EndRuntime = "endRunTime";

	public static String N_CaseName ="caseName";
	public static String N_CaseNo= "caseNo";
	public static String N_CardNo= "cardNo";
	public static String N_Amount= "amount";
	public static String N_TranName = "transactionName";
	public static String N_Sequence = "sequence";
	public static String N_RoundID = "roundId";
	public static String N_RoundName = "roundName";
	private GWTCaseFlow gwtCaseFlow;

	
	public GWTResultDetailLog(){}
	
	public GWTResultDetailLog(
			 String id,		                     
			 String caseId,  
			 String transactionId,
			 String executeLogId,                                
			 String cmbHost,                                             
			 String caseFlowInstanceId,                                                                   
			 String field37,                                                                                                 
			 String casePassFlag,                                    
			 String receivedReplayFlag,                           
			 String value4NextCase,                                 
			 String xmlContent,                                                                 
			 String oracleContent,                                  
			 String responContent,                                                                                                                            
			 String caseFlowId,            
			 String caseFlowPassFlag,  
			 String caseFlowName,
			 String caseFlowNo,
			 String caseName,              
			 String caseNo   ,
			 String cardNo,
			 String amount,
			 String breakPointFlag,
             String requesContent,
             String sequence
			 )
	 {
		this.set(N_ID,id);	
		this.set(N_CaseID,caseId);
		this.set(N_TransactionId, transactionId);
		this.set(N_ExecuteLogID,executeLogId);
		this.set(N_CmbHost,cmbHost);
		this.set(N_CaseFlowInstanceID,caseFlowInstanceId);
		this.set(N_Field37,field37);		
		this.set(N_CasePassFlag,casePassFlag);
		this.set(N_ReceivedReplayFlag,receivedReplayFlag);
		this.set(N_Value4NextCase,value4NextCase);
		this.set(N_XmlContent,xmlContent);
		this.set(N_OracleContent,oracleContent);
		this.set(N_ResponContent,responContent);
		this.set(N_RequesContent, requesContent);
		this.set(N_CaseFlowID,caseFlowId);
		this.set(N_CaseFlowPassFlag,caseFlowPassFlag);
		this.set(N_CaseFlowName,caseFlowName);
		this.set(N_CaseFlowNo,caseFlowNo);
		this.set(N_CaseName,caseName);
		this.set(N_CaseNo,caseNo);
		this.set(N_CardNo,cardNo);
		this.set(N_Amount,amount);
		this.set(N_BreakPointFlag, breakPointFlag);
		this.set(N_BreakPointFlagStr, breakPointFlag.equals("1")?"中断":"无");
		this.set(N_Sequence, sequence);
		
	}
	public String GetTranName(){
		return this.get(N_TranName);
	}
	
	public void SetTranName(String name){
		this.set(N_TranName, name);
	}
	public String getID() {
		return this.get(N_ID);
	}
	public String GetCaseID() {
		return this.get(N_CaseID);
	}
	
	public String getTransactionId(){
		return this.get(N_TransactionId);
	}
	
	public String GetExecuteLogID() {
		return this.get(N_ExecuteLogID);
	}
	public String getCMBHOST() {
		return this.get(N_CmbHost);
	}
	public String GetCaseFlowInstanceID() {
		return this.get(N_CaseFlowInstanceID);
	}
	
	public String GetSequence(){
		return this.get(N_Sequence);
	}
	public String getFIELD37() {
		return this.get(N_Field37);
	}
	public String getCASEPASSFLAG() {
		return this.get(N_CasePassFlag);
	}
	public String getRECEIVEDREPLAYFLAG() {
		return this.get(N_ReceivedReplayFlag);
	}
	public String getVALUE4NEXTCASE() {
		return this.get(N_Value4NextCase);
	}
	public String getXMLCONTENT() {
		return this.get(N_XmlContent);
	}

	public String getORACLECONTENT() {
		return this.get(N_OracleContent);
	}
	public String getRESPONCONTENT() {
		return this.get(N_ResponContent);
	}
	public String getREQUESCONTENT() {
		return this.get(N_RequesContent);
	}
	public String GetCaseFlowID() {
		return this.get(N_CaseFlowID);
	}
	public String GetCaseFlowPassFlag() {
		return this.get(N_CaseFlowPassFlag);
	}
	public String GetCaseFlowName() {
		return this.get(N_CaseFlowName);
	}
	public String GetCaseFlowNo() {
		return this.get(N_CaseFlowNo);
	}
	public String GetCaseName() {
		return this.get(N_CaseName);
	}
	public String getCASENO() {
		return this.get(N_CaseNo);
	}
	public String getCARDNUMBER() {
		return this.get(N_CardNo);
	}
	public String getAMOUNT() {
		return this.get(N_Amount);
	}
	
	public  String GetBreakPointFlag(){
		return this.get(N_BreakPointFlag);
	}
	public String GetBreakPointFlagStr(){
		return this.get(N_BreakPointFlagStr);
	}
	
	public String GetBeginRunTime(){
		return this.get(N_BeginRunTime);
	}
	
	public String GetEndRunTime(){
		return this.get(N_EndRuntime);
	}
	
	public void SetBeginRunTime(String date){
		this.set(N_BeginRunTime, date);
	}
	
	public void SetEndRunTime(String date){
		this.set(N_EndRuntime, date);
	}
	
	public void SetRoundID(Integer id){
		this.set(N_RoundID, id);
	}
	
	public String GetRoundID(){
		return this.get(N_RoundID);
	}
	
	public void SetRoundName(String roundName){
		this.set(N_RoundName, roundName);
	}
	
	public String GetRoundName(){
		return this.get(N_RoundName);
	}
	
	public String GetTableName() {
		// TODO Auto-generated method stub
		return "test_excute_log";
	}

	public Map<String, Object> GetFieldValuePair(String validateValue) {
		// TODO Auto-generated method stub
		Map<String, Object> fieldValuePair = new HashMap<String,Object>();
		fieldValuePair.put(N_ID, validateValue);
		return fieldValuePair;
	}

	public void setGwtCaseFlow(GWTCaseFlow gwtCaseFlow) {
		this.gwtCaseFlow = gwtCaseFlow;
	}

	public GWTCaseFlow getGwtCaseFlow() {
		return gwtCaseFlow;
	}
}
