package com.dc.tes.net;


public class DBEnum {
	
	public final class CaseFlow {
		 public static final String ID = "id";
	     public static final String SYSTEMID = "systemId";
	     public static final String CASEFLOWNAME = "caseFlowName";
	     public static final String CASEFLOWNO = "caseFlowNo";
	     public static final String USERID = "userId";
	     public static final String IMPORTBATCHNO = "importBatchNo";
	}
	
	public final class Case {
		public static final String ID = "caseId";
		public static final String CASEFLOW = "caseFlow";
		public static final String SEQUENCE = "sequence";
		
	}
	
	public final class CaseInstance {
		public static final String ID = "id";
		public static final String CASEID = "caseId";
		public static final String CASEINDEX = "field37";
		public static final String EXECUTELOGID = "executeLogId";
		public static final String CASEFLOWINSTANCE = "caseFlowInstance";
		public static final String RECEIVEDREPLAYFLAG = "receivedReplayFlag";
	}
	
	public final class ExecuteLog {
		public static final String ID = "id";
	}
	
	public final class Transaction {
		public static final String ID = "transactionId";
	}
	
	public final class CaseFlowInstance {
		public static final String CASEFLOWID = "caseFlowId";
		public static final String EXECUTELOGID = "executeLogId";
	}

}
