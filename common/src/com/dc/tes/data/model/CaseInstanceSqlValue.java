package com.dc.tes.data.model;

import com.dc.tes.data.model.tag.BeanIdName;

// default package

/**
 * CaseInstanceSqlValue entity. @author MyEclipse Persistence Tools
 */
@BeanIdName("id")
public class CaseInstanceSqlValue implements java.io.Serializable {

	// Fields

	/**
	 * 
	 */
	private static final long serialVersionUID = -6490159118854227522L;
	private int id;
	private int caseInstanceId;
	private int caseFlowStep;
	private int isCurrentStep;
	private int sequence;
	private TransactionDynamicParameter transParameter;
	private String realSql;
	private String realValue;
	private String parameterName;
	private String expectedValue;

	// Constructors

	/** default constructor */
	public CaseInstanceSqlValue() {
	}

	/** minimal constructor */
	public CaseInstanceSqlValue(int tesCaseInstance) {
		this.caseInstanceId = tesCaseInstance;
	}

	/** full constructor */
	public CaseInstanceSqlValue(int tesCaseInstance, int caseFlowStep,
			int isCurrentStep, int sequence,
			TransactionDynamicParameter transParameter, String realSql,
			String realValue, String expectedValue) {
		this.caseInstanceId = tesCaseInstance;
		this.caseFlowStep = caseFlowStep;
		this.isCurrentStep = isCurrentStep;
		this.sequence = sequence;
		this.transParameter = transParameter;
		this.realSql = realSql;
		this.realValue = realValue;
		this.expectedValue = expectedValue;
	}

	// Property accessors

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getCaseInstanceId() {
		return this.caseInstanceId;
	}

	public void setCaseInstanceId(int caseInstanceId) {
		this.caseInstanceId = caseInstanceId;
	}

	public int getCaseFlowStep() {
		return this.caseFlowStep;
	}

	public void setCaseFlowStep(int caseFlowStep) {
		this.caseFlowStep = caseFlowStep;
	}

	public int getIsCurrentStep() {
		return this.isCurrentStep;
	}

	public void setIsCurrentStep(int isCurrentStep) {
		this.isCurrentStep = isCurrentStep;
	}

	public int getSequence() {
		return this.sequence;
	}

	public void setSequence(int sequence) {
		this.sequence = sequence;
	}

	public String getRealSql() {
		return this.realSql;
	}

	public void setRealSql(String realSql) {
		this.realSql = realSql;
	}

	public String getRealValue() {
		return this.realValue;
	}

	public void setRealValue(String realValue) {
		this.realValue = realValue;
	}

	public void setTransParameter(TransactionDynamicParameter transParameter) {
		this.transParameter = transParameter;
	}

	public TransactionDynamicParameter getTransParameter() {
		return transParameter;
	}

	public void setParameterName(String parameterName) {
		this.parameterName = parameterName;
	}

	public String getParameterName() {
		return parameterName;
	}
	
	public String getExpectedValue() {
		return this.expectedValue;
	}

	public void setExpectedValue(String expectedValue) {
		this.expectedValue = expectedValue;
	}


}