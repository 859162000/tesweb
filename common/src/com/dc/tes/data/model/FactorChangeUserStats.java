package com.dc.tes.data.model;

import com.dc.tes.data.model.tag.BeanIdName;

@BeanIdName("id")
public class FactorChangeUserStats implements java.io.Serializable {

	// Fields

	/**
	 * 
	 */
	private static final long serialVersionUID = 4087256335585705993L;
	private Integer id;
	private FactorChangeStatistics factorChangeStatistics;
	private Integer opUserId;
	private Integer createdTransactionCount;
	private Integer createdCaseFlowCount;
	private Integer createdCaseCount;
	private Integer createdSysParamCount;
	private Integer modifiedTransactionCount;
	private Integer modifiedCaseFlowCount;
	private Integer modifiedCaseCount;
	private Integer modifiedSysParamCount;
	private String memo;

	// Constructors

	/** default constructor */
	public FactorChangeUserStats() {
	}

	/** minimal constructor */
	public FactorChangeUserStats(FactorChangeStatistics factorChangeStatistics) {
		this.factorChangeStatistics = factorChangeStatistics;
	}

	/** full constructor */
	public FactorChangeUserStats(FactorChangeStatistics factorChangeStatistics,
			Integer opUserId, Integer createdTransactionCount,
			Integer createdCaseFlowCount, Integer createdCaseCount,
			Integer createdSysParamCount, Integer modifiedTransactionCount,
			Integer modifiedCaseFlowCount, Integer modifiedCaseCount,
			Integer modifiedSysParamCount, String memo) {
		this.factorChangeStatistics = factorChangeStatistics;
		this.opUserId = opUserId;
		this.createdTransactionCount = createdTransactionCount;
		this.createdCaseFlowCount = createdCaseFlowCount;
		this.createdCaseCount = createdCaseCount;
		this.createdSysParamCount = createdSysParamCount;
		this.modifiedTransactionCount = modifiedTransactionCount;
		this.modifiedCaseFlowCount = modifiedCaseFlowCount;
		this.modifiedCaseCount = modifiedCaseCount;
		this.modifiedSysParamCount = modifiedSysParamCount;
		this.memo = memo;
	}

	// Property accessors

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public FactorChangeStatistics getFactorChangeStatistics() {
		return this.factorChangeStatistics;
	}

	public void setFactorChangeStatistics(
			FactorChangeStatistics factorChangeStatistics) {
		this.factorChangeStatistics = factorChangeStatistics;
	}

	public Integer getOpUserId() {
		return this.opUserId;
	}

	public void setOpUserId(Integer opUserId) {
		this.opUserId = opUserId;
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

	public String getMemo() {
		return this.memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

}