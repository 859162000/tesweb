package com.dc.tes.data.model;
// default package

import java.util.Date;

import com.dc.tes.data.model.tag.BeanIdName;


/**
 * FlowCases entity. @author MyEclipse Persistence Tools
 */
@BeanIdName("id")
public class FlowCases  implements java.io.Serializable {


     // Fields    

     /**
	 * 
     */
     private static final long serialVersionUID = -4555437649417335325L;
     private Integer id;
     private Integer caseId;
     private Integer caseFlowId;
     private Integer sequence;
     private Integer userId;
     private Date createTime;

    // Constructors

    /** default constructor */
    public FlowCases() {
    }

	/** minimal constructor */
    public FlowCases(Integer caseId, Integer caseFlowId, Integer sequence, Integer userId) {
        this.caseId = caseId;
        this.caseFlowId = caseFlowId;
        this.sequence = sequence;
        this.userId = userId;
    }
    
    /** full constructor */
    public FlowCases(Integer caseId, Integer caseFlowId, Integer sequence, Integer userId, Date createTime) {
        this.caseId = caseId;
        this.caseFlowId = caseFlowId;
        this.sequence = sequence;
        this.userId = userId;
        this.createTime = createTime;
    }

   
    // Property accessors

    public Integer getId() {
        return this.id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getCaseId() {
		return caseId;
	}

	public void setCaseId(Integer caseId) {
		this.caseId = caseId;
	}

	public Integer getCaseFlowId() {
		return caseFlowId;
	}

	public void setCaseFlowId(Integer caseFlowId) {
		this.caseFlowId = caseFlowId;
	}

	public Integer getSequence() {
        return this.sequence;
    }
    
    public void setSequence(Integer sequence) {
        this.sequence = sequence;
    }

    public Integer getUserId() {
        return this.userId;
    }
    
    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Date getCreateTime() {
        return this.createTime;
    }
    
    public void setCreateTime(Date createTime) {
        this.createTime = createTime; 
    }


}