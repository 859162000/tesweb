package com.dc.tes.data.model;

import java.util.Date;
import com.dc.tes.data.model.tag.BeanIdName;


@BeanIdName("id")
public class FactorChangeStatistics implements java.io.Serializable {

	// Fields

	/**
	 * 
	 */
	private static final long serialVersionUID = -3672319826762056460L;
	private Integer id;
	private Integer systemId;
	private String statMonth;
	private String statStartDay;
	private String statEndDay;
	private Integer createdTransactionCount;
	private Integer createdCaseFlowCount;
	private Integer createdCaseCount;
	private Integer createdSysParamCount;
	private Integer modifiedTransactionCount;
	private Integer modifiedCaseFlowCount;
	private Integer modifiedCaseCount;
	private Integer modifiedSysParamCount;
	private Integer statUserId;
	private Date statTime;
	private String memo;

	// Constructors

	/** default constructor */
	public FactorChangeStatistics() {
	}

	/** minimal constructor */
	public FactorChangeStatistics(Integer systemId) {
		this.systemId = systemId;
	}

	/** full constructor */
	public FactorChangeStatistics(Integer systemId, String statMonth,
			String statStartDay, String statEndDay,
			Integer createdTransactionCount, Integer createdCaseFlowCount,
			Integer createdCaseCount, Integer createdSysParamCount,
			Integer modifiedTransactionCount, Integer modifiedCaseFlowCount,
			Integer modifiedCaseCount, Integer modifiedSysParamCount,
			Integer statUserId, Date statTime, String memo) {
		this.systemId = systemId;
		this.statMonth = statMonth;
		this.statStartDay = statStartDay;
		this.statEndDay = statEndDay;
		this.createdTransactionCount = createdTransactionCount;
		this.createdCaseFlowCount = createdCaseFlowCount;
		this.createdCaseCount = createdCaseCount;
		this.createdSysParamCount = createdSysParamCount;
		this.modifiedTransactionCount = modifiedTransactionCount;
		this.modifiedCaseFlowCount = modifiedCaseFlowCount;
		this.modifiedCaseCount = modifiedCaseCount;
		this.modifiedSysParamCount = modifiedSysParamCount;
		this.statUserId = statUserId;
		this.statTime = statTime;
		this.memo = memo;
	}

	// Property accessors

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getSystemId() {
		return this.systemId;
	}

	public void setSystemId(Integer systemId) {
		this.systemId = systemId;
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

	public Integer getStatUserId() {
		return this.statUserId;
	}

	public void setStatUserId(Integer statUserId) {
		this.statUserId = statUserId;
	}

	public Date getStatTime() {
		return this.statTime;
	}

	public void setStatTime(Date statTime) {
		this.statTime = statTime;
	}

	public String getMemo() {
		return this.memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}
}