package com.dc.tes.data.model;
// default package


import java.util.Date;

import com.dc.tes.data.model.tag.BeanIdName;


/**
 * CaseImportBatch entity. @author MyEclipse Persistence Tools
 */
@BeanIdName("id")
public class CaseImportBatch  implements java.io.Serializable {


    // Fields    

     /**
	 * 
	 */
	private static final long serialVersionUID = -3068018812714211440L;
	private Integer id;
     private Integer systemId;
     private Integer userId;
     private String batchNo;
     private String description;
     private Date importTime;


    // Constructors

    /** default constructor */
    public CaseImportBatch() {
    }

	/** minimal constructor */
    public CaseImportBatch(Integer systemId, Integer userId, String importBatchNo, Date importTime) {
        this.systemId = systemId;
        this.userId = userId;
        this.batchNo = importBatchNo;
        this.importTime = importTime;
    }
    
    /** full constructor */
    public CaseImportBatch(Integer systemId, Integer userId, String importBatchNo, String description, Date importTime) {
		this.systemId = systemId;
	    this.userId = userId;
        this.batchNo = importBatchNo;
        this.description = description;
        this.importTime = importTime;
    }

   
    // Property accessors

    public Integer getId() {
        return this.id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }

  

    public Integer getSystemId() {
		return systemId;
	}

	public void setSystemId(Integer systemId) {
		this.systemId = systemId;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public String getBatchNo() {
        return this.batchNo;
    }
    
    public void setBatchNo(String importBatchNo) {
        this.batchNo = importBatchNo;
    }

    public String getDescription() {
        return this.description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }

    public Date getImportTime() {
        return this.importTime;
    }
    
    public void setImportTime(Date importTime) {
        this.importTime = importTime;
    }
   








}