package com.dc.tes.data.model;
// default package

import java.util.Date;


import com.dc.tes.data.model.tag.BeanIdName;


/**
 * CaseFlow entity. @author MyEclipse Persistence Tools
 */
@BeanIdName("id")
public class CaseFlow  implements java.io.Serializable {


    // Fields    

     /**
	 * 
	 */
	private static final long serialVersionUID = 1533421657096928960L;
	/**
	 * 
	 */
	
	private Integer id;
     private Integer systemId;
     private String caseFlowName;
     private String caseFlowNo;
     private String description;
     private String importBatchNo;
     private Integer breakPointFlag;
     private String  caseFlowPath;
     private Integer directoryId;
     private Integer stepCount; //用例下步骤的个数
     private String caseFlowStep;
     private String preConditions;
     private String expectedResult;
     private String caseType;
     private String caseProperty;
     private String priority;
     private String designer;
     private String designTime;
     private String memo;
     private String scriptFlowId;
     //2013.3.27
     private Integer passFlag;
     private Integer disabledFlag;
     private String createdUserId;
     private Date createdTime;
 	 private Date lastModifiedTime;
 	 private String lastModifiedUserId;
     
     /** default constructor */
     public CaseFlow() {
     }

 	/** minimal constructor */
     public CaseFlow(Integer systemId, String userId) {
         this.systemId = systemId;
         this.createdUserId = userId;
     }
     
     /** full constructor */
     public CaseFlow(Integer systemId, String name, String caseFlowNo, String description, String userId, Date createTime, String importBatchNo) {
         this.systemId = systemId;
         this.caseFlowName = name;
         this.caseFlowNo = caseFlowNo;
         this.description = description;
         this.createdUserId = userId;
         this.createdTime = createTime;
         this.importBatchNo = importBatchNo;
     }

     public String getCaseFlowPath() {
		return caseFlowPath;
	}

	public Integer getDirectoryId() {
		return directoryId;
	}

	public String getCaseFlowStep() {
		return caseFlowStep;
	}

	public String getPreConditions() {
		return preConditions;
	}

	public String getExpectedResult() {
		return expectedResult;
	}

	public String getCaseType() {
		return caseType;
	}

	public String getCaseProperty() {
		return caseProperty;
	}

	public String getPriority() {
		return priority;
	}

	public void setCaseFlowPath(String caseFlowPath) {
		this.caseFlowPath = caseFlowPath;
	}

	public void setDirectoryId(Integer directoryId) {
		this.directoryId = directoryId;
	}

	public void setCaseFlowStep(String caseFlowStep) {
		this.caseFlowStep = caseFlowStep;
	}

	public void setPreConditions(String preConditions) {
		this.preConditions = preConditions;
	}

	public void setExpectedResult(String expectedResult) {
		this.expectedResult = expectedResult;
	}

	public void setCaseType(String caseType) {
		this.caseType = caseType;
	}

	public void setCaseProperty(String caseProperty) {
		this.caseProperty = caseProperty;
	}

	public void setPriority(String priority) {
		this.priority = priority;
	}

	

    // Constructors

   
   
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

	public String getCaseFlowName() {
        return this.caseFlowName;
    }
    
    public void setCaseFlowName(String name) {
        this.caseFlowName = name;
    }

    public String getCaseFlowNo() {
        return this.caseFlowNo;
    }
    
    public void setCaseFlowNo(String caseFlowNo) {
        this.caseFlowNo = caseFlowNo;
    }

    public String getDescription() {
        return this.description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }

    public String getCreatedUserId() {
        return this.createdUserId;
    }
    
    public void setCreatedUserId(String userId) {
        this.createdUserId = userId;
    }

    public Date getCreatedTime() {
        return this.createdTime;
    }
    
    public void setCreatedTime(Date createTime) {
        this.createdTime = createTime;
    }

    public String getImportBatchNo() {
        return this.importBatchNo;
    }
    
    public void setImportBatchNo(String importBatchNo) {
        this.importBatchNo = importBatchNo;
    }

	public void setBreakPointFlag(Integer breakPointFlag) {
		this.breakPointFlag = breakPointFlag;
	}

	public Integer getBreakPointFlag() {
		return breakPointFlag;
	}

	public void setDesigner(String designer) {
		this.designer = designer;
	}

	public String getDesigner() {
		return designer;
	}

	public void setScriptFlowId(String busiflowId) {
		this.scriptFlowId = busiflowId;
	}

	public String getScriptFlowId() {
		return scriptFlowId;
	}

	public void setStepCount(Integer stepCount) {
		this.stepCount = stepCount;
	}

	public Integer getStepCount() {
		return stepCount;
	}

	public void setPassFlag(Integer passFlag) {
		this.passFlag = passFlag;
	}

	public Integer getPassFlag() {
		return passFlag;
	}

	public void setDisabledFlag(Integer disabledFlag) {
		this.disabledFlag = disabledFlag;
	}

	public Integer getDisabledFlag() {
		return disabledFlag;
	}

	public void setDesignTime(String designTime) {
		this.designTime = designTime;
	}

	public String getDesignTime() {
		return designTime;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

	public String getMemo() {
		return memo;
	}

	/**
	 * @param lastModifiedTime the lastModifiedTime to set
	 */
	public void setLastModifiedTime(Date lastModifiedTime) {
		this.lastModifiedTime = lastModifiedTime;
	}

	/**
	 * @return the lastModifiedTime
	 */
	public Date getLastModifiedTime() {
		return lastModifiedTime;
	}

	/**
	 * @param lastModifiedUserId the lastModifiedUserId to set
	 */
	public void setLastModifiedUserId(String lastModifiedUserId) {
		this.lastModifiedUserId = lastModifiedUserId;
	}

	/**
	 * @return the lastModifiedUserId
	 */
	public String getLastModifiedUserId() {
		return lastModifiedUserId;
	}

}