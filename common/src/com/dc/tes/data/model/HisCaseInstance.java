package com.dc.tes.data.model;

import com.dc.tes.data.model.tag.BeanIdName;
// default package



/**
 * HisCaseInstance entity. @author MyEclipse Persistence Tools
 */
@BeanIdName("id")
public class HisCaseInstance  implements java.io.Serializable {


    // Fields    

     /**
	 * 
	 */
	private static final long serialVersionUID = 7628365940615409309L;
	private Integer id;
     private Integer executeLogId;
     private Integer caseId;
     private String caseName;
     private String caseNo;
     private String transactionId;
     private Integer cardId;
     private String dbHost;
     private String importBatchNo;
     private String cardNumber;
   //  private String cardPwd;
     private Float amount;
     private Integer caseFlowInstanceId;
     private String field37;
     private Integer casePassFlag;
     private Integer receivedReplayFlag;
     private String value4NextCase;
     private String xmlContent;
     private String oracleContent;
     private String responContent;
     private String beginRunTime;
     private String endRunTime;

    // Constructors

    /** default constructor */
    public HisCaseInstance() {
    }

	/** minimal constructor */
    public HisCaseInstance(Integer executeLogId, Integer caseId, Integer caseFlowInstanceId) {
        this.executeLogId = executeLogId;
        this.caseId = caseId;
        this.caseFlowInstanceId = caseFlowInstanceId;
    }
    
    /** full constructor */
    public HisCaseInstance(Integer executeLogId, Integer caseId,
    		String dbHost, String importBatchNo, String cardNumber, 
    		Float amount, Integer caseFlowInstanceId,
    		String expectRtnCod, String returnCode, String field37, 
    		Integer casePassFlag, Integer receivedReplayFlag, String value4NextCase,
    		String xmlContent, String oracleContent, String responContent,
    		String beginRunTime, String endRunTime) {
    	this.executeLogId = executeLogId;
        this.caseId = caseId;
        this.dbHost = dbHost;
        this.importBatchNo = importBatchNo;
        this.cardNumber = cardNumber;
        this.amount = amount;
        this.caseFlowInstanceId = caseFlowInstanceId;
        this.field37 = field37;
        this.casePassFlag = casePassFlag;
        this.receivedReplayFlag = receivedReplayFlag;
        this.value4NextCase = value4NextCase;
        this.xmlContent = xmlContent;
        this.oracleContent = oracleContent;
        this.responContent = responContent;
        this.beginRunTime = beginRunTime;
        this.endRunTime = endRunTime;
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

	public Integer getCaseId() {
        return this.caseId;
    }
    
    public void setCaseId(Integer caseId) {
        this.caseId = caseId;
    }

    public String getDbHost() {
        return this.dbHost;
    }
    
    public void setDbHost(String dbHost) {
        this.dbHost = dbHost;
    }

    public String getImportBatchNo() {
        return this.importBatchNo;
    }
    
    public void setImportBatchNo(String importBatchNo) {
        this.importBatchNo = importBatchNo;
    }

    public String getCardNumber() {
        return this.cardNumber;
    }
    
    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

//    public String getCardPwd() {
//        return this.cardPwd;
//    }
//    
//    public void setCardPwd(String cardPwd) {
//        this.cardPwd = cardPwd;
//    }

    public Float getAmount() {
        return this.amount;
    }
    
    public void setAmount(Float amount) {
        this.amount = amount;
    }

    public Integer getCaseFlowInstanceId() {
        return this.caseFlowInstanceId;
    }
    
    public void setCaseFlowInstanceId(Integer caseFlowInstanceId) {
        this.caseFlowInstanceId = caseFlowInstanceId;
    }

    public String getField37() {
        return this.field37;
    }
    
    public void setField37(String field37) {
        this.field37 = field37;
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

    public String getXmlContent() {
        return this.xmlContent;
    }
    
    public void setXmlContent(String xmlContent) {
        this.xmlContent = xmlContent;
    }

    public String getOracleContent() {
        return this.oracleContent;
    }
    
    public void setOracleContent(String oracleContent) {
        this.oracleContent = oracleContent;
    }

    public String getResponContent() {
        return this.responContent;
    }
    
    public void setResponContent(String responContent) {
        this.responContent = responContent;
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

	public void setCaseName(String caseName) {
		this.caseName = caseName;
	}

	public String getCaseName() {
		return caseName;
	}

	public void setCaseNo(String caseNo) {
		this.caseNo = caseNo;
	}

	public String getCaseNo() {
		return caseNo;
	}

	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}

	public String getTransactionId() {
		return transactionId;
	}

	public void setCardId(Integer cardId) {
		this.cardId = cardId;
	}

	public Integer getCardId() {
		return cardId;
	}

}