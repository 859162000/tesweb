package com.dc.tes.data.model;
// default package


import com.dc.tes.data.model.tag.BeanIdName;


/**
 * HisExecuteLog entity. @author MyEclipse Persistence Tools
 */
@BeanIdName("id")
public class HisExecuteLog  implements java.io.Serializable {


    /**
	 * 
	 */
	private static final long serialVersionUID = 2492510654422216951L;
	// Fields    

     private Integer id;
     private Integer systemId;
     private Integer userId;
     private Integer executeSetId;
     private String executeBatchNo;
     private String description;
     private String createTime;
     private String beginRunTime;
     private String endRunTime;

    // Constructors

    /** default constructor */
    public HisExecuteLog() {
    }

	/** minimal constructor */
    public HisExecuteLog(Integer systemId, Integer userId, String executeBatchNo, String createTime) {
    	this.systemId = systemId;
        this.userId = userId;
        this.executeBatchNo = executeBatchNo;
        this.createTime = createTime;
    }
    
    /** full constructor */
    public HisExecuteLog(Integer systemId, Integer userId, Integer executeSetId, String executeBatchNo, String description, String createTime) {
    	this.systemId = systemId;
        this.userId = userId;
        this.executeSetId = executeSetId;
        this.executeBatchNo = executeBatchNo;
        this.description = description;
        this.createTime = createTime;
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


    public Integer getExecuteSetId() {
        return this.executeSetId;
    }
    
    public void setExecuteSetId(Integer executeSetId) {
        this.executeSetId = executeSetId;
    }

    public String getExecuteBatchNo() {
        return this.executeBatchNo;
    }
    
    public void setExecuteBatchNo(String executeBatchNo) {
        this.executeBatchNo = executeBatchNo;
    }

    public String getDescription() {
        return this.description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }

    public String getCreateTime() {
        return this.createTime;
    }
    
    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }
   
	public String getBeginRunTime() {
		return beginRunTime;
	}

	public void setBeginRunTime(String beginRunTime) {
		this.beginRunTime = beginRunTime;
	}

	public String getEndRunTime() {
		return endRunTime;
	}

	public void setEndRunTime(String endRunTime) {
		this.endRunTime = endRunTime;
	}







}