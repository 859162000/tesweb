package com.dc.tes.data.model;

import com.dc.tes.data.model.tag.BeanIdName;

// default package

/**
 * CaseInstanceFieldValue entity. @author MyEclipse Persistence Tools
 */
@BeanIdName("id")
public class CaseInstanceFieldValue implements java.io.Serializable {

	// Fields
	/**
	 * 
	 */
	private static final long serialVersionUID = -6490159118854227522L;
	private int id;
	private int caseInstanceId;
	private TransactionDynamicParameter transParameter;
	private String msgFieldName;
	private String msgFieldValue;
	private String expectedValue;
	private Integer parameterType;

	// Constructors

	/** default constructor */
	public CaseInstanceFieldValue() {
	}

	/** minimal constructor */
	public CaseInstanceFieldValue(int tesCaseInstance) {
		this.caseInstanceId = tesCaseInstance;
	}

	/** full constructor */
	public CaseInstanceFieldValue(int tesCaseInstance, 
			TransactionDynamicParameter transParameter, String msgFieldName,
			String msgFieldValue, String expectedValue) {
		this.caseInstanceId = tesCaseInstance;
		this.transParameter = transParameter;
		this.msgFieldName = msgFieldName;
		this.msgFieldValue = msgFieldValue;
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

	public String getMsgFieldName() {
		return this.msgFieldName;
	}

	public void setMsgFieldName(String msgFieldName) {
		this.msgFieldName = msgFieldName;
	}

	public String getMsgFieldValue() {
		return this.msgFieldValue;
	}

	public void setMsgFieldValue(String msgFieldValue) {
		this.msgFieldValue = msgFieldValue;
	}

	public String getExpectedValue() {
		return this.expectedValue;
	}

	public void setExpectedValue(String expectedValue) {
		this.expectedValue = expectedValue;
	}

	public void setTransParameter(TransactionDynamicParameter transParameter) {
		this.transParameter = transParameter;
	}

	public TransactionDynamicParameter getTransParameter() {
		return transParameter;
	}

	public Integer getParameterType() {
		return this.parameterType;
	}

	public void setParameterType(Integer iParameterType) {
		this.parameterType = iParameterType;
	}

}