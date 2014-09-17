package com.dc.tes.data.model;

import java.util.Date;

import com.dc.tes.data.model.tag.BeanIdName;


@BeanIdName("caseRunUserStatId")
public class CaseRunUserStats implements java.io.Serializable {

	// Fields

	/**
	 * 
	 */
	private static final long serialVersionUID = 4572680865142135698L;
	private Integer caseRunUserStatId;
	private CaseRunStatistics caseRunStatistics;
	private Integer runUserId;
	private Integer totalRunCaseFlowCount;
	private Integer totalRunCaseCount;
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
	private Date firstRunTime;
	private Date lastRunTime;
	private String memo;

	// Constructors

	/** default constructor */
	public CaseRunUserStats() {
	}

	/** full constructor */
	public CaseRunUserStats(CaseRunStatistics caseRunStatistics,
			Integer runUserId, Integer totalRunCaseFlowCount,
			Integer totalRunCaseCount, String caseFlowPassRate,
			Date firstRunTime, Date lastRunTime, String memo) {
		this.caseRunStatistics = caseRunStatistics;
		this.runUserId = runUserId;
		this.totalRunCaseFlowCount = totalRunCaseFlowCount;
		this.totalRunCaseCount = totalRunCaseCount;
		this.caseFlowPassRate = caseFlowPassRate;
		this.firstRunTime = firstRunTime;
		this.lastRunTime = lastRunTime;
		this.memo = memo;
	}

	// Property accessors

	public Integer getCaseRunUserStatId() {
		return this.caseRunUserStatId;
	}

	public void setCaseRunUserStatId(Integer caseRunUserStatId) {
		this.caseRunUserStatId = caseRunUserStatId;
	}

	public CaseRunStatistics getCaseRunStatistics() {
		return this.caseRunStatistics;
	}

	public void setCaseRunStatistics(CaseRunStatistics caseRunStatistics) {
		this.caseRunStatistics = caseRunStatistics;
	}

	public Integer getRunUserId() {
		return this.runUserId;
	}

	public void setRunUserId(Integer runUserId) {
		this.runUserId = runUserId;
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