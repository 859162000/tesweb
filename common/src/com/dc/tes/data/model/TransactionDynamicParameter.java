package com.dc.tes.data.model;
// default package

import java.util.Date;

import com.dc.tes.data.model.tag.BeanIdName;


/**
 * TransactionDynamicParameter entity. @author MyEclipse Persistence Tools
 */
@BeanIdName("id")
public class TransactionDynamicParameter  implements java.io.Serializable {


    // Fields    

     /**
	 * 
	 */
	private static final long serialVersionUID = -7228945570943759551L;
	private String id;
     private String transactionId;
     private SystemDynamicParameter systemParameter;
     private String userId;
     private Date modifyTime;


    // Constructors

    /** default constructor */
    public TransactionDynamicParameter() {
    }

	/** minimal constructor */
    public TransactionDynamicParameter(String transactionId, String userId) {
        this.transactionId = transactionId;
        this.userId = userId;
    }
    
    /** full constructor */
    public TransactionDynamicParameter(String transactionId, SystemDynamicParameter systemParameter, String userId, String defaultExpectedValue, Date modifyTime) {
        this.transactionId = transactionId;
        this.systemParameter = systemParameter;
        this.userId = userId;
        this.modifyTime = modifyTime;
    }

   
    // Property accessors

    public String getId() {
        return this.id;
    }
    
    public void setId(String id) {
        this.id = id;
    }

    public String getTransactionId() {
        return this.transactionId;
    }
    
    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    

    public String getUserId() {
        return this.userId;
    }
    
    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Date getModifyTime() {
        return this.modifyTime;
    }
    
    public void setModifyTime(Date modifyTime) {
        this.modifyTime = modifyTime;
    }

	public void setSystemParameter(SystemDynamicParameter systemParameter) {
		this.systemParameter = systemParameter;
	}

	public SystemDynamicParameter getSystemParameter() {
		return systemParameter;
	}
   








}