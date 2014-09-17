package com.dc.tes.data.model;
// default package



import java.util.Date;

import com.dc.tes.data.model.tag.BeanIdName;


/**
 * ExecuteLog entity. @author MyEclipse Persistence Tools
 */
@BeanIdName("id")
public class ExecuteLog  implements java.io.Serializable {


    // Fields    

     /**
	 * 
	 */
	private static final long serialVersionUID = -5539685443452913529L;
	private Integer id;
	private Integer systemId;
    private Integer userId;
    private Integer executeSetId;
    private String executeBatchNo;
    private String description;
    private String createTime;
    private Date beginRunTime;
    private Date endRunTime;
    private String runDuration;
    private Integer passFlag;
    private Integer executeSetDirId;
    private String executeSetName;
    //2013.3.27
    private Integer roundId;
    //2013.6.7   添加日志类型     0：执行集； 1：用例；2：步骤
    private Integer type;
    // Constructors

    /** default constructor */
    public ExecuteLog() {
    }

	/** minimal constructor */
    public ExecuteLog(Integer systemId, Integer userId, String executeBatchNo, String createTime) {
    	this.systemId = systemId;
        this.userId = userId;
        this.executeBatchNo = executeBatchNo;
        this.createTime = createTime;
    }
    
    /** full constructor */
    public ExecuteLog(Integer systemId, Integer userId, Integer executeSetId, String executeBatchNo, String description, String createTime) {
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

	public Date getBeginRunTime() {
		return beginRunTime;
	}

	public void setBeginRunTime(Date beginRunTime) {
		this.beginRunTime = beginRunTime;
	}

	public Date getEndRunTime() {
		return endRunTime;
	}

	public void setEndRunTime(Date endRunTime) {
		this.endRunTime = endRunTime;
	}

	public void setRunDuration(String runDuration) {
		this.runDuration = runDuration;
	}

	public String getRunDuration() {
		return runDuration;
	}
	
	public void setPassFlag(Integer passFlag) {
		this.passFlag = passFlag;
	}

	public Integer getPassFlag() {
		return passFlag;
	}

	public void setExecuteSetDirId(Integer executeSetDirId) {
		this.executeSetDirId = executeSetDirId;
	}

	public Integer getExecuteSetDirId() {
		return executeSetDirId;
	}

	public void setExecuteSetName(String executeSetName) {
		this.executeSetName = executeSetName;
	}

	public String getExecuteSetName() {
		return executeSetName;
	}

	public void setRoundId(Integer roundId) {
		this.roundId = roundId;
	}

	public Integer getRoundId() {
		return roundId;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public Integer getType() {
		return type;
	}

}