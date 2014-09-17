package com.dc.tes.data.model;
// default package
import java.util.Date;

import com.dc.tes.data.model.tag.BeanIdName;


/**
 * HisCaseFlowInstance entity. @author MyEclipse Persistence Tools
 */
@BeanIdName("id")
public class HisCaseFlowInstance  implements java.io.Serializable {


    /**
	 * 
	 */
	private static final long serialVersionUID = -3453931909514043812L;
	// Fields    

     private Integer id;
     private Integer  executeLogId;
     private Integer caseFlowId;
     private Date createTime;
     private Integer caseFlowPassFlag;


    // Constructors

    /** default constructor */
    public HisCaseFlowInstance() {
    }

	/** minimal constructor */
    public HisCaseFlowInstance(Integer  executeLogId, Integer caseFlowId, Date createTime) {
        this.executeLogId = executeLogId;
        this.caseFlowId = caseFlowId;
        this.createTime = createTime;
    }
    
    /** full constructor */
    public HisCaseFlowInstance(Integer  executeLogId, Integer caseFlowId, Date createTime, Integer caseFlowPassFlag) {
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
		return executeLogId;
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
   








}