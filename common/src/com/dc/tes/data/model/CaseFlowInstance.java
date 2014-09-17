package com.dc.tes.data.model;

// default package

import java.util.Date;

import com.dc.tes.data.model.tag.BeanIdName;

/**
 * CaseFlowInstance entity. @author MyEclipse Persistence Tools
 */
@BeanIdName("id")
public class CaseFlowInstance implements java.io.Serializable {

	// Fields

	/**
	 * 
	 */
	private static final long serialVersionUID = 354025644789529743L;
	private Integer id;
	private Integer executeLogId;
	private Integer caseFlowId;
	private String caseFlowName;
	private String caseFlowNo;
	private Date createTime;
	private Integer caseFlowPassFlag;
	private Date beginTime;
	private Date endTime;
	private String runDuration;
	private Integer directoryId;
	//2013.3.27
	private String systemId;
	private Integer roundId;

	// Constructors

	/** default constructor */
	public CaseFlowInstance() {
	}

	/** minimal constructor */
	public CaseFlowInstance(Integer executeLogId, Integer caseFlowId,
			Date createTime) {
		this.executeLogId = executeLogId;
		this.caseFlowId = caseFlowId;
		this.createTime = createTime;
	}

	/** full constructor */
	public CaseFlowInstance(Integer executeLogId, Integer caseFlowId,
			Date createTime, Integer caseFlowPassFlag) {
		this.executeLogId = executeLogId;
		this.caseFlowId = caseFlowId;
		this.createTime = createTime;
		this.caseFlowPassFlag = caseFlowPassFlag;
	}

	// Property accessors

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getExecuteLogId() {
		return this.executeLogId;
	}

	public void setExecuteLogId(Integer executeLogId) {
		this.executeLogId = executeLogId;
	}

	public Integer getCaseFlowId() {
		return this.caseFlowId;
	}

	public void setCaseFlowId(Integer caseFlowId) {
		this.caseFlowId = caseFlowId;
	}

	public Date getCreateTime() {
		return this.createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Integer getCaseFlowPassFlag() {
		return this.caseFlowPassFlag;
	}

	public void setCaseFlowPassFlag(Integer caseFlowPassFlag) {
		this.caseFlowPassFlag = caseFlowPassFlag;
	}

	public Date getBeginTime() {
		return beginTime;
	}

	public void setBeginTime(Date beginTime) {
		this.beginTime = beginTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public void setRunDuration(String runDuration) {
		this.runDuration = runDuration;
	}

	public String getRunDuration() {
		return runDuration;
	}

	
	public String getCaseFlowName() {
		return caseFlowName;
	}

	public void setCaseFlowName(String caseFlowName) {
		this.caseFlowName = caseFlowName;
	}

	public String getCaseFlowNo() {
		return caseFlowNo;
	}

	public void setCaseFlowNo(String caseFlowNo) {
		this.caseFlowNo = caseFlowNo;
	}

	public void setDirectoryId(Integer directoryId) {
		this.directoryId = directoryId;
	}

	public Integer getDirectoryId() {
		return directoryId;
	}

	public void setSystemId(String systemId) {
		this.systemId = systemId;
	}

	public String getSystemId() {
		return systemId;
	}

	public void setRoundId(Integer roundId) {
		this.roundId = roundId;
	}

	public Integer getRoundId() {
		return roundId;
	}

}