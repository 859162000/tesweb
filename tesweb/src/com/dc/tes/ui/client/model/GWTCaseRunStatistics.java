package com.dc.tes.ui.client.model;

import java.io.Serializable;
import java.util.Date;

import com.extjs.gxt.ui.client.data.BaseModelData;

public class GWTCaseRunStatistics extends BaseModelData implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6928652655481362261L;
	public static String N_CaseRunStatisticsId = "caseRunStatisticsId";
	public static String N_SystemId = "systemId";
	public static String N_StatMonth = "statMonth";
	public static String N_StatStartDay = "statStartDay";
	public static String N_StatEndDay = "statEndDay";
	public static String N_TotalRunCaseFlowCount = "totalRunCaseFlowCount";
	public static String N_TotalRunCaseCount = "totalRunCaseCount";
	public static String N_TotalRunUserCount = "totalRunUserCount";
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
	
	public static String N_StatIpAddress = "statIpAddress";
	public static String N_StatHostName = "statHostName";
	public static String N_StatUserId = "statUserId";
	public static String N_StatStatus = "statStatus";
	public static String N_StatTime = "statTime";
	public static String N_FirstRunTime = "firstRunTime";
	public static String N_LastRunTime = "lastRunTime";
	public static String N_Memo = "memo";
	
	public GWTCaseRunStatistics(){}
	
	public GWTCaseRunStatistics(String caseRunStatisticsId, String statMonth, String statStartDay,
			String statEndDay, Integer totalRunCaseFlowCount,
			Integer totalRunCaseCount, Integer totalRunUserCount, Integer totalPassedCaseFlowCount,
			String caseFlowPassRate, String statIpAddress,
			String statHostName, Integer statUserId, Date statTime,
			Date firstRunTime, Date lastRunTime, String memo){
		this.set(N_CaseRunStatisticsId, caseRunStatisticsId);
		this.set(N_StatMonth, statMonth);
		this.set(N_StatStartDay, statStartDay);
		this.set(N_StatEndDay, statEndDay);
		this.set(N_TotalRunCaseFlowCount, totalRunCaseFlowCount);
		this.set(N_TotalRunCaseCount, totalRunCaseCount);
		this.set(N_TotalRunUserCount, totalRunUserCount);
		this.set(N_TotalPassedCaseFlowCount, totalPassedCaseFlowCount);
		this.set(N_CaseFlowPassRate, caseFlowPassRate);
		this.set(N_StatIpAddress, statIpAddress);
		this.set(N_StatHostName, statHostName);
		this.set(N_StatUserId, statUserId);
		this.set(N_StatTime, statTime);
		this.set(N_FirstRunTime, firstRunTime);
		this.set(N_LastRunTime, lastRunTime);
		this.set(N_Memo, memo);
	}
	
	public GWTCaseRunStatistics(String caseRunStatisticsId, String statMonth,
			String statStartDay, String statEndDay,
			Integer createdTransactionCount, Integer createdCaseFlowCount,
			Integer createdCaseCount, Integer createdSysParamCount,
			Integer modifiedTransactionCount, Integer modifiedCaseFlowCount,
			Integer modifiedCaseCount, Integer modifiedSysParamCount,
			Integer statUserId, Date statTime, String memo) {
		this.set(N_CaseRunStatisticsId, caseRunStatisticsId);
		this.set(N_StatMonth, statMonth);
		this.set(N_StatStartDay, statStartDay);
		this.set(N_StatEndDay, statEndDay);
		this.set(N_CreatedTransactionCount,createdTransactionCount);
		this.set(N_CreatedCaseFlowCount, createdCaseFlowCount);
		this.set(N_CreatedCaseCount, createdCaseCount);
		this.set(N_CreatedSysParamCount, createdSysParamCount);
		this.set(N_ModifiedTransactionCount, modifiedTransactionCount);
		this.set(N_ModifiedCaseFlowCount, modifiedCaseFlowCount);
		this.set(N_ModifiedCaseCount, modifiedCaseCount);
		this.set(N_ModifiedSysParamCount, modifiedSysParamCount);
		this.set(N_StatUserId, statUserId);
		this.set(N_StatTime, statTime);
		this.set(N_Memo, memo);
	}

	public Integer getCaseRunStatisticsID(){
		return Integer.parseInt(get(N_CaseRunStatisticsId).toString());
	}
}
