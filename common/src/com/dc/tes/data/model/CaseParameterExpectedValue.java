package com.dc.tes.data.model;
// default package

import java.util.Date;

import com.dc.tes.data.model.tag.BeanIdName;


/**
 * CaseParameterExpectedValue entity. @author MyEclipse Persistence Tools
 */
@BeanIdName("id")
public class CaseParameterExpectedValue  implements java.io.Serializable {


    /**
	 * 
	 */
	private static final long serialVersionUID = 6969603390993065200L;
	// Fields    

     private String id;
     private String caseId;
     private TransactionDynamicParameter transParameter;
     private String expectedValue;
     private int expectedValueType;
     private Date createdTime;
     private String createdUserId;
 	 private Date lastModifiedTime;
 	 private String lastModifiedUserId;

    // Constructors

    /** default constructor */
    public CaseParameterExpectedValue() {
    }

	/** minimal constructor */
    public CaseParameterExpectedValue(String caseId) {
        this.caseId = caseId;
    }
    
    /** full constructor */
    public CaseParameterExpectedValue(String caseId, TransactionDynamicParameter transParameter, String expectedValue) {
        this.caseId = caseId;
        this.transParameter = transParameter;
        this.expectedValue = expectedValue;
    }

   
    // Property accessors

    public String getId() {
        return this.id;
    }
    
    public void setId(String id) {
        this.id = id;
    }

    public String getCaseId() {
        return this.caseId;
    }
    
    public void setCaseId(String caseId) {
        this.caseId = caseId;
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

	public void setExpectedValueType(int expectedValueType) {
		this.expectedValueType = expectedValueType;
	}

	public int getExpectedValueType() {
		return expectedValueType;
	}

	public void setCreatedTime(Date createdTime) {
		this.createdTime = createdTime;
	}

	public Date getCreatedTime() {
		return createdTime;
	}

	public void setCreatedUserId(String createdUserId) {
		this.createdUserId = createdUserId;
	}

	public String getCreatedUserId() {
		return createdUserId;
	}

	public void setLastModifiedTime(Date lastModifiedTime) {
		this.lastModifiedTime = lastModifiedTime;
	}

	public Date getLastModifiedTime() {
		return lastModifiedTime;
	}

	public void setLastModifiedUserId(String lastModifiedUserId) {
		this.lastModifiedUserId = lastModifiedUserId;
	}

	public String getLastModifiedUserId() {
		return lastModifiedUserId;
	}










}