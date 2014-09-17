package com.dc.tes.data.model;
// default package



import java.util.Date;

import com.dc.tes.data.model.tag.BeanIdName;


/**
 * CaseInstance entity. @author MyEclipse Persistence Tools
 */
@BeanIdName("id")
public class CaseInstance  implements java.io.Serializable {

	/**
	 * 
	 */
	 private static final long serialVersionUID = 5775631177390098806L;
     private Integer id;
     private Integer executeLogId;
     private Integer caseId;
     private Integer transactionId;
     private String caseName;
     private String caseNo;
     private String importBatchNo;
     private String amount;
     private Integer cardId;
     private String cardNumber;
     
     private String dbHost;
     private CaseFlowInstance caseFlowInstance;
     private String field37;
     private String field38;
     private Integer casePassFlag;
     private Integer receivedReplayFlag;
     private String value4NextCase;
     private String requestXml;
     private String responseXml;
     private String expectedXml;
     private String responseMsg;
     private String requestMsg;
     //记录下来自任务队列的信息
     private String scriptName;
     private String tag;
     
     private Date beginRunTime;
     private Date endRunTime;
     private Integer sequence;
     private Integer breakPointFlag;
     private String errorMsg;
    // Constructors

    /** default constructor */
    public CaseInstance() {
    }

	/** minimal constructor */
    public CaseInstance(Integer executeLogId, Case _case, CaseFlowInstance caseFlowInstance) {
        this.executeLogId = executeLogId;
        this.caseFlowInstance = caseFlowInstance;
    }
    
    /** full constructor */
    public CaseInstance(Integer executeLogId,  String dbHost, CaseFlowInstance caseFlowInstance, String field37, Integer casePassFlag, Integer receivedReplayFlag, String value4NextCase, String xmlContent, String oracleContent, String responContent, String requesContent) {
    	this.executeLogId = executeLogId;

        this.dbHost = dbHost;
        this.caseFlowInstance = caseFlowInstance;
        this.field37 = field37;
        this.casePassFlag = casePassFlag;
        this.receivedReplayFlag = receivedReplayFlag;
        this.value4NextCase = value4NextCase;
        this.requestXml = xmlContent;
        this.expectedXml = oracleContent;
        this.responseMsg = responContent;
        this.requestMsg = requesContent;
    }

   
    // Property accessors

    public Integer getId() {
        return this.id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getExecuteLogId() {
        return this.executeLogId;
    }
    
    public void setExecuteLogId(Integer executeLogId) {
        this.executeLogId = executeLogId;
    }



    public Integer getCaseId() {
		return caseId;
	}

	public void setCaseId(Integer caseId) {
		this.caseId = caseId;
	}

	public Integer getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(Integer transactionId) {
		this.transactionId = transactionId;
	}

	public String getCaseName() {
		return caseName;
	}

	public void setCaseName(String caseName) {
		this.caseName = caseName;
	}

	public String getCaseNo() {
		return caseNo;
	}

	public void setCaseNo(String caseNo) {
		this.caseNo = caseNo;
	}

	public String getImportBatchNo() {
		return importBatchNo;
	}

	public void setImportBatchNo(String importBatchNo) {
		this.importBatchNo = importBatchNo;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public Integer getCardId() {
		return cardId;
	}

	public void setCardId(Integer cardId) {
		this.cardId = cardId;
	}

	public String getCardNumber() {
		return cardNumber;
	}

	public void setCardNumber(String cardNumber) {
		this.cardNumber = cardNumber;
	}

	public String getDbHost() {
        return this.dbHost;
    }
    
    public void setDbHost(String dbHost) {
        this.dbHost = dbHost;
    }

    public CaseFlowInstance getCaseFlowInstance() {
        return this.caseFlowInstance;
    }
    
    public void setCaseFlowInstance(CaseFlowInstance caseFlowInstance) {
    	this.caseFlowInstance = caseFlowInstance;
    }

    public String getField37() {
        return this.field37;
    }
    
    public void setField37(String field37) {
        this.field37 = field37;
    }

    public String getField38() {
        return this.field38;
    }
    
    public void setField38(String field38) {
        this.field38 = field38;
    }
    
    public Integer getCasePassFlag() {
        return this.casePassFlag;
    }
    
    public void setCasePassFlag(Integer casePassFlag) {
        this.casePassFlag = casePassFlag;
    }

    public Integer getReceivedReplayFlag() {
        return this.receivedReplayFlag;
    }
    
    public void setReceivedReplayFlag(Integer receivedReplayFlag) {
        this.receivedReplayFlag = receivedReplayFlag;
    }

    public String getValue4NextCase() {
        return this.value4NextCase;
    }
    
    public void setValue4NextCase(String value4NextCase) {
        this.value4NextCase = value4NextCase;
    }

    public String getRequestXml() {
        return this.requestXml;
    }
    
    public void setRequestXml(String requestXml) {
        this.requestXml = requestXml;
    }

    public String getExpectedXml() {
        return this.expectedXml;
    }
    
    public void setExpectedXml(String expectedXml) {
        this.expectedXml = expectedXml;
    }

    public String getResponseMsg() {
        return this.responseMsg;
    }
    
    public void setResponseMsg(String responseMsg) {
        this.responseMsg = responseMsg;
    }

    public String getResponseXml() {
        return this.responseXml;
    }
    
    public void setResponseXml(String responseXml) {
        this.responseXml = responseXml;
    }
    
	/**
	 * @param scriptName the scriptName to set
	 */
	public void setScriptName(String scriptName) {
		this.scriptName = scriptName;
	}

	/**
	 * @return the scriptName
	 */
	public String getScriptName() {
		return scriptName;
	}

	/**
	 * @param tag the tag to set
	 */
	public void setTag(String tag) {
		this.tag = tag;
	}

	/**
	 * @return the tag
	 */
	public String getTag() {
		return tag;
	}

	public void setBeginRunTime(Date beginRunTime) {
		this.beginRunTime = beginRunTime;
	}

	public Date getBeginRunTime() {
		return beginRunTime;
	}

	public void setEndRunTime(Date endRunTime) {
		this.endRunTime = endRunTime;
	}

	public Date getEndRunTime() {
		return endRunTime;
	}

	public void setSequence(Integer sequence) {
		this.sequence = sequence;
	}

	public Integer getSequence() {
		if (sequence == null)
			return 0;
		return sequence;
	}

	public void setBreakPointFlag(Integer breakPointFlag) {
		this.breakPointFlag = breakPointFlag;
	}

	public Integer getBreakPointFlag() {
		return breakPointFlag;
	}

	public void setRequestMsg(String requestMsg) {
		this.requestMsg = requestMsg;
	}

	public String getRequestMsg() {
		return requestMsg;
	}

	/**
	 * @param errorMsg the errorMsg to set
	 */
	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}

	/**
	 * @return the errorMsg
	 */
	public String getErrorMsg() {
		return errorMsg;
	}

}