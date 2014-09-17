package com.dc.tes.ui.client.model;

import java.io.Serializable;
import java.util.Date;

import com.extjs.gxt.ui.client.data.BaseModelData;

public class GWTCaseRunUserStats extends BaseModelData implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5888412735174654100L;
	public static String N_CaseRunUserStatId = "caseRunUserStatId";
	public static String N_CaseRunStatisticsID = "caseRunStatistics";
	public static String N_RunUserId = "runUserId";
	public static String N_TotalRunCaseFlowCount = "totalRunCaseFlowCount";
	public static String N_TotalRunCaseCount = "totalRunCaseCount";
	public static String N_TotalPassedCaseFlowCount = "totalPassedCaseFlowCount";
	public static String N_CaseFlowPassRate = "caseFlowPassRate";
	public static String N_CreatedTransactionCount = "createdTransactionCount";
	public static String N_CreatedCaseFlowCount = "createdCaseFlowCount";
	public static String N_CreatedCaseCount = "createdCaseCount";
	public static String N_CreatedSysParamCount = "createdSysParamCount";
	public static String N_ModifiedTransactionCount = "modifiedTransactionCount";
	public static String N_ModifiedCaseFlowCount = "modifiedCaseFlowCount";
	public static String N_ModifiedCaseCount = "modifiedCaseCount";
	public static String N_ModifiedSysParamCount = "modifiedSysParamCount";
	public static String N_FirstRunTime = "firstRunTime";
	public static String N_LastRunTime = "lastRunTime";
	public static String N_Memo = "memo";
	
	public GWTCaseRunUserStats(){	
	}
	
	public GWTCaseRunUserStats(Integer caseRunStatisticsId,
			String runUserId, Integer totalRunCaseFlowCount,
			Integer totalRunCaseCount, Integer totalPassedCaseFlowCount, 
			String caseFlowPassRate, Date firstRunTime,
			Date lastRunTime, String memo) {
		this.set(N_CaseRunStatisticsID, caseRunStatisticsId);
		this.set(N_RunUserId, runUserId);
		this.set(N_TotalRunCaseFlowCount, totalRunCaseFlowCount);
		this.set(N_TotalRunCaseCount, totalRunCaseCount);
		this.set(N_TotalPassedCaseFlowCount, totalPassedCaseFlowCount);
		this.set(N_CaseFlowPassRate, caseFlowPassRate);
		this.set(N_FirstRunTime, firstRunTime);
		this.set(N_LastRunTime, lastRunTime);
		this.set(N_Memo, memo);
	}
	
	public GWTCaseRunUserStats(Integer caseRunStatisticsId, String opUser, Integer createdTransactionCount,
			Integer createdCaseFlowCount, Integer createdCaseCount,
			Integer createdSysParamCount, Integer modifiedTransactionCount,
			Integer modifiedCaseFlowCount, Integer modifiedCaseCount,
			Integer modifiedSysParamCount, String memo){
		this.set(N_CaseRunStatisticsID, caseRunStatisticsId);
		this.set(N_RunUserId, opUser);
		this.set(N_CreatedTransactionCount,createdTransactionCount);
		this.set(N_CreatedCaseFlowCount, createdCaseFlowCount);
		this.set(N_CreatedCaseCount, createdCaseCount);
		this.set(N_CreatedSysParamCount, createdSysParamCount);
		this.set(N_ModifiedTransactionCount, modifiedTransactionCount);
		this.set(N_ModifiedCaseFlowCount, modifiedCaseFlowCount);
		this.set(N_ModifiedCaseCount, modifiedCaseCount);
		this.set(N_ModifiedSysParamCount, modifiedSysParamCount);
		this.set(N_Memo, memo);
	}
	
}

