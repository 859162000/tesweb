package com.dc.tes.data.model;

import java.util.Date;

import com.dc.tes.data.model.tag.BeanIdName;
// default package



/**
 * SystemDynamicParameter entity. @author MyEclipse Persistence Tools
 */
@BeanIdName("id")
public class SystemDynamicParameter  implements java.io.Serializable {


    // Fields    

     /**
	 * 
	 */
	private static final long serialVersionUID = -8522951351853767564L;
	private String id;
     private String systemId;
     private String name;
     private String desc;
     private String parameterExpression;
     private String compareCondition;
     private String defaultExpectedValue;
     private String parameterType;
     private int multiDataFlag;
     private String isValid;
     private int parameterHostType;
     
     private String parameterHostId;
     private String displayFlag;
     private int refetchFlag;
     private int refetchMethod;
     private int paramFromMsgSrc;
     private Integer directoryId;

     private Date createdTime;
     private String createdUserId;
 	 private Date lastModifiedTime;
 	 private String lastModifiedUserId;

    // Constructors

    /** default constructor */
    public SystemDynamicParameter() {
    }

	/** minimal constructor */
    public SystemDynamicParameter(String systemId) {
        this.systemId = systemId;
    }
    
    /** full constructor */
    public SystemDynamicParameter(String systemId, String parameterName, String parameterDesc, String parameterExpression, String defaultExpectedValue, String parameterType, String compareCondition, int multiDataFlag, String isValid, int parameterHostType, String parameterHostId, String displayFlag, int refetchFlag) {
        this.systemId = systemId;
        this.name = parameterName;
        this.desc = parameterDesc;
        this.parameterExpression = parameterExpression;
        this.defaultExpectedValue = defaultExpectedValue;
        this.parameterType = parameterType;
        this.compareCondition = compareCondition;
        this.multiDataFlag = multiDataFlag;
        this.isValid = isValid;
        this.parameterHostType = parameterHostType;
        this.parameterHostId = parameterHostId;
        this.displayFlag = displayFlag;
        this.refetchFlag = refetchFlag;
    }

   
    // Property accessors

    public String getId() {
        return this.id;
    }
    
    public void setId(String id) {
        this.id = id;
    }

    public String getSystemId() {
        return this.systemId;
    }
    
    public void setSystemId(String systemId) {
        this.systemId = systemId;
    }

    public String getName() {
        return this.name;
    }
    
    public void setName(String parameterName) {
        this.name = parameterName;
    }

    public String getDesc() {
        return this.desc;
    }
    
    public void setDesc(String parameterDesc) {
        this.desc = parameterDesc;
    }

    public String getParameterExpression() {
        return this.parameterExpression;
    }
    
    public void setParameterExpression(String parameterExpression) {
        this.parameterExpression = parameterExpression;
    }

    public String getDefaultExpectedValue() {
        return this.defaultExpectedValue;
    }
    
    public void setDefaultExpectedValue(String defaultValue) {
        this.defaultExpectedValue = defaultValue;
    }

    public String getParameterType() {
        return this.parameterType;
    }
    
    public void setParameterType(String parameterType) {
        this.parameterType = parameterType;
    }

    public int getMultiDataFlag() {
        return this.multiDataFlag;
    }
    
    public void setMultiDataFlag(int multiDataFlag) {
        this.multiDataFlag = multiDataFlag;
    }
    
    public String getIsValid() {
        return this.isValid;
    }
    
    public void setIsValid(String isValid) {
        this.isValid = isValid;
    }

    public String getCompareCondition() {
		return compareCondition;
	}

	public void setCompareCondition(String compareCondition) {
		this.compareCondition = compareCondition;
	}

    public int getParameterHostType() {
        return this.parameterHostType;
    }
    
    public void setParameterHostType(int parameterHostType) {
        this.parameterHostType = parameterHostType;
    }

    public String getParameterHostId() {
        return this.parameterHostId;
    }
    
    public void setParameterHostId(String parameterHostId) {
        this.parameterHostId = parameterHostId;
    }

    public String getDisplayFlag() {
        return this.displayFlag;
    }
    
    public void setDisplayFlag(String displayFlag) {
        this.displayFlag = displayFlag;
    }

    public int getRefetchFlag() {
        return this.refetchFlag;
    }
    
    public void setRefetchFlag(int refetchFlag) {
        this.refetchFlag = refetchFlag;
    }

	public void setRefetchMethod(int refetchMethod) {
		this.refetchMethod = refetchMethod;
	}

	public int getRefetchMethod() {
		return refetchMethod;
	}

	public void setParamFromMsgSrc(int paramFromMsgSrc) {
		this.paramFromMsgSrc = paramFromMsgSrc;
	}

	public int getParamFromMsgSrc() {
		return paramFromMsgSrc;
	}

	public void setDirectoryId(Integer directoryId) {
		this.directoryId = directoryId;
	}

	public Integer getDirectoryId() {
		return directoryId;
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