package com.dc.tes.data.model;

import com.dc.tes.data.model.tag.BeanIdName;
// default package



/**
 * HisCaseInstanceSqlValue entity. @author MyEclipse Persistence Tools
 */
@BeanIdName("id")
public class HisCaseInstanceSqlValue  implements java.io.Serializable {


    // Fields    

     /**
	 * 
	 */
	private static final long serialVersionUID = -873549936059933875L;
	private String id;
     private String caseInstanceId;
     private String caseFlowStep;
     private String isCurrentStep;
     private String sequence;
     private TransactionDynamicParameter transParameter;
     private String realSql;
     private String realValue;
     private String parameterName;

    // Constructors

    /** default constructor */
    public HisCaseInstanceSqlValue() {
    }

	/** minimal constructor */
    public HisCaseInstanceSqlValue(String caseInstanceId) {
        this.caseInstanceId = caseInstanceId;
    }
    
    /** full constructor */
    public HisCaseInstanceSqlValue(String caseInstanceId, String caseFlowStep, String isCurrentStep, String sequence, TransactionDynamicParameter transParameter, String realSql, String realValue) {
        this.caseInstanceId = caseInstanceId;
        this.caseFlowStep = caseFlowStep;
        this.isCurrentStep = isCurrentStep;
        this.sequence = sequence;
        this.transParameter = transParameter;
        this.realSql = realSql;
        this.realValue = realValue;
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

    public String getCaseFlowStep() {
        return this.caseFlowStep;
    }
    
    public void setCaseFlowStep(String caseFlowStep) {
        this.caseFlowStep = caseFlowStep;
    }

    public String getIsCurrentStep() {
        return this.isCurrentStep;
    }
    
    public void setIsCurrentStep(String isCurrentStep) {
        this.isCurrentStep = isCurrentStep;
    }

    public String getSequence() {
        return this.sequence;
    }
    
    public void setSequence(String sequence) {
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
   








}