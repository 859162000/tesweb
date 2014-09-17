package com.dc.tes.data.model;

import com.dc.tes.data.model.tag.BeanIdName;

// default package

/**
 * HisCaseInstanceFieldValue entity. @author MyEclipse Persistence Tools
 */
@BeanIdName("id")
public class HisCaseInstanceFieldValue implements java.io.Serializable {

	// Fields
	/**
	 * 
	 */
	private static final long serialVersionUID = -6490159118854227522L;
	private String id;
	private String caseInstanceId;
	private TransactionDynamicParameter transParameter;
	private String msgFieldName;
	private String msgFieldValue;

	// Constructors

	/** default constructor */
	public HisCaseInstanceFieldValue() {
	}

	/** minimal constructor */
	public HisCaseInstanceFieldValue(String tesCaseInstance) {
		this.caseInstanceId = tesCaseInstance;
	}

	/** full constructor */
	public HisCaseInstanceFieldValue(String tesCaseInstance, String sequence,
			TransactionDynamicParameter transParameter, String msgFieldName,
			String msgFieldValue) {
		this.caseInstanceId = tesCaseInstance;
		this.transParameter = transParameter;
		this.msgFieldName = msgFieldName;
		this.msgFieldValue = msgFieldValue;
	}

	// Property accessors

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCaseInstanceId() {
		return this.caseInstanceId;
	}

	public void setCaseInstanceId(String caseInstanceId) {
		this.caseInstanceId = caseInstanceId;
	}

	public String getmsgFieldName() {
		return this.msgFieldName;
	}

	public void setmsgFieldName(String msgFieldName) {
		this.msgFieldName = msgFieldName;
	}

	public String getmsgFieldValue() {
		return this.msgFieldValue;
	}

	public void setmsgFieldValue(String msgFieldValue) {
		this.msgFieldValue = msgFieldValue;
	}

	public void setTransParameter(TransactionDynamicParameter transParameter) {
		this.transParameter = transParameter;
	}

	public TransactionDynamicParameter getTransParameter() {
		return transParameter;
	}

}