package com.dc.tes.data.model;

import java.io.Serializable;
import java.util.Date;

import com.dc.tes.data.model.tag.BeanIdName;


@BeanIdName("caseRunStatisticsId")
public class CaseRunStatistics implements Serializable {

	// Fields

	/**
	 * 
	 */
	private static final long serialVersionUID = 4894568646922303413L;
	private Integer caseRunStatisticsId;
	private Integer systemId;
	private String statMonth;
	private String statStartDay;
	private String statEndDay;
	private Integer totalRunCaseFlowCount;
	private Integer totalRunCaseCount;
	private Integer totalRunUserCount;
	private Integer totalPassedCaseFlowCount;
	private String caseFlowPassRate;
	
	private Integer createdTransactionCount;
	private Integer createdCaseFlowCount;
	private Integer createdCaseCount;
	private Integer createdSysParamCount;
	private Integer modifiedTransactionCount;
	private Integer modifiedCaseFlowCount;
	private Integer modifiedCaseCount;
	private Integer modifiedSysParamCount;
	
	private String statIpAddress;
	private String statHostName;
	private Integer statUserId;
	private int statStatus;
	private Date statTime;
	private Date firstRunTime;
	private Date lastRunTime;
	private String memo;


	// Constructors

	/** default constructor */
	public CaseRunStatistics() {
	}

	/** full constructor */
	public CaseRunStatistics(String statMonth, String statStartDay,
			String statEndDay, Integer totalRunCaseFlowCount,
			Integer totalRunCaseCount, Integer totalRunUserCount,
			Integer totalPassedCaseFlowCount, String caseFlowPassRate, String statIpAddress,
			String statHostName, Integer statUserId, Date statTime,
			Date firstRunTime, Date lastRunTime, String memo) {
		this.statMonth = statMonth;
		this.statStartDay = statStartDay;
		this.statEndDay = statEndDay;
		this.totalRunCaseFlowCount = totalRunCaseFlowCount;
		this.totalRunCaseCount = totalRunCaseCount;
		this.totalRunUserCount = totalRunUserCount;
		this.totalPassedCaseFlowCount = totalPassedCaseFlowCount;
		this.caseFlowPassRate = caseFlowPassRate;
		this.statIpAddress = statIpAddress;
		this.statHostName = statHostName;
		this.statUserId = statUserId;
		this.statTime = statTime;
		this.firstRunTime = firstRunTime;
		this.lastRunTime = lastRunTime;
		this.memo = memo;
	}

	// Property accessors

	public Integer getSystemId() {
		return this.systemId;
	}

	public void setSystemId(Integer systemId) {
		this.systemId = systemId;
	}

	
	public Integer getCaseRunStatisticsId() {
		return this.caseRunStatisticsId;
	}

	public void setCaseRunStatisticsId(Integer caseRunStatisticsId) {
		this.caseRunStatisticsId = caseRunStatisticsId;
	}

	public String getStatMonth() {
		return this.statMonth;
	}

	public void setStatMonth(String statMonth) {
		this.statMonth = statMonth;
	}

	public String getStatStartDay() {
		return this.statStartDay;
	}

	public void setStatStartDay(String statStartDay) {
		this.statStartDay = statStartDay;
	}

	public String getStatEndDay() {
		return this.statEndDay;
	}

	public void setStatEndDay(String statEndDay) {
		this.statEndDay = statEndDay;
	}

	public Integer getTotalRunCaseFlowCount() {
		return this.totalRunCaseFlowCount;
	}

	public void setTotalRunCaseFlowCount(Integer totalRunCaseFlowCount) {
		this.totalRunCaseFlowCount = totalRunCaseFlowCount;
	}

	public Integer getTotalRunCaseCount() {
		return this.totalRunCaseCount;
	}

	public void setTotalRunCaseCount(Integer totalRunCaseCount) {
		this.totalRunCaseCount = totalRunCaseCount;
	}

	public Integer getTotalRunUserCount() {
		return this.totalRunUserCount;
	}

	public void setTotalRunUserCount(Integer totalRunUserCount) {
		this.totalRunUserCount = totalRunUserCount;
	}

	public Integer getTotalPassedCaseFlowCount() {
		return this.totalPassedCaseFlowCount;
	}

	public void setTotalPassedCaseFlowCount(Integer TotalPassedCaseFlowCount) {
		this.totalPassedCaseFlowCount = TotalPassedCaseFlowCount;
	}

	public String getCaseFlowPassRate() {
		return this.caseFlowPassRate;
	}

	public void setCaseFlowPassRate(String caseFlowPassRate) {
		this.caseFlowPassRate = caseFlowPassRate;
	}

	public Integer getCreatedTransactionCount() {
		return this.createdTransactionCount;
	}

	public void setCreatedTransactionCount(Integer createdTransactionCount) {
		this.createdTransactionCount = createdTransactionCount;
	}

	public Integer getCreatedCaseFlowCount() {
		return this.createdCaseFlowCount;
	}

	public void setCreatedCaseFlowCount(Integer createdCaseFlowCount) {
		this.createdCaseFlowCount = createdCaseFlowCount;
	}

	public Integer getCreatedCaseCount() {
		return this.createdCaseCount;
	}

	public void setCreatedCaseCount(Integer createdCaseCount) {
		this.createdCaseCount = createdCaseCount;
	}

	public Integer getCreatedSysParamCount() {
		return this.createdSysParamCount;
	}

	public void setCreatedSysParamCount(Integer createdSysParamCount) {
		this.createdSysParamCount = createdSysParamCount;
	}

	public Integer getModifiedTransactionCount() {
		return this.modifiedTransactionCount;
	}

	public void setModifiedTransactionCount(Integer modifiedTransactionCount) {
		this.modifiedTransactionCount = modifiedTransactionCount;
	}

	public Integer getModifiedCaseFlowCount() {
		return this.modifiedCaseFlowCount;
	}

	public void setModifiedCaseFlowCount(Integer modifiedCaseFlowCount) {
		this.modifiedCaseFlowCount = modifiedCaseFlowCount;
	}

	public Integer getModifiedCaseCount() {
		return this.modifiedCaseCount;
	}

	public void setModifiedCaseCount(Integer modifiedCaseCount) {
		this.modifiedCaseCount = modifiedCaseCount;
	}

	public Integer getModifiedSysParamCount() {
		return this.modifiedSysParamCount;
	}

	public void setModifiedSysParamCount(Integer modifiedSysParamCount) {
		this.modifiedSysParamCount = modifiedSysParamCount;
	}

	public String getStatIpAddress() {
		return this.statIpAddress;
	}

	public void setStatIpAddress(String statIpAddress) {
		this.statIpAddress = statIpAddress;
	}

	public String getStatHostName() {
		return this.statHostName;
	}

	public void setStatHostName(String statHostName) {
		this.statHostName = statHostName;
	}

	public Integer getStatUserId() {
		return this.statUserId;
	}

	public void setStatUserId(Integer statUserId) {
		this.statUserId = statUserId;
	}

	public int getStatStatus() {
		return this.statStatus;
	}

	public void setStatStatus(int statStatus) {
		this.statStatus = statStatus;
	}

	public Date getStatTime() {
		return this.statTime;
	}

	public void setStatTime(Date statTime) {
		this.statTime = statTime;
	}

	public Date getFirstRunTime() {
		return this.firstRunTime;
	}

	public void setFirstRunTime(Date firstRunTime) {
		this.firstRunTime = firstRunTime;
	}

	public Date getLastRunTime() {
		return this.lastRunTime;
	}

	public void setLastRunTime(Date lastRunTime) {
		this.lastRunTime = lastRunTime;
	}

	public String getMemo() {
		return this.memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

}