package com.dc.tes.data.model;
// default package

import java.util.Date;

import com.dc.tes.data.model.tag.BeanIdName;


/**
 * HisCommMsgLog entity. @author MyEclipse Persistence Tools
 */
@BeanIdName("id")
public class HisCommMsgLog  implements java.io.Serializable {


    // Fields    

     /**
	 * 
	 */
	private static final long serialVersionUID = 6238533625649809082L;
	private Integer id;
     private Integer executeLogId;
     private Integer caseInstanceId;
     private String transactionName;
     private String caseName;
     private String msgContent;
     private String head10;
     private Date sendTime;
     private String sendStatus;
     private String sendReport;
     private String direction;
     private Date createTime;


    // Constructors

    /** default constructor */
    public HisCommMsgLog() {
    }

	/** minimal constructor */
    public HisCommMsgLog(String direction) {
        this.direction = direction;
    }
    
    /** full constructor */
    public HisCommMsgLog(Integer executeLogId, Integer caseInstanceId, String transactionName, String caseName, String msgContent, String head10, Date sendTime, String sendStatus, String sendReport, String direction, Date createTime) {
        this.executeLogId = executeLogId;
        this.caseInstanceId = caseInstanceId;
        this.transactionName = transactionName;
        this.caseName = caseName;
        this.msgContent = msgContent;
        this.head10 = head10;
        this.sendTime = sendTime;
        this.sendStatus = sendStatus;
        this.sendReport = sendReport;
        this.direction = direction;
        this.createTime = createTime;
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

	public Integer getCaseInstanceId() {
		return caseInstanceId;
	}

	public void setCaseInstanceId(Integer caseInstanceId) {
		this.caseInstanceId = caseInstanceId;
	}

	public String getTransactionName() {
        return this.transactionName;
    }
    
    public void setTransactionName(String transactionName) {
        this.transactionName = transactionName;
    }

    public String getCaseName() {
        return this.caseName;
    }
    
    public void setCaseName(String caseName) {
        this.caseName = caseName;
    }

    public String getMsgContent() {
        return this.msgContent;
    }
    
    public void setMsgContent(String msgContent) {
        this.msgContent = msgContent;
    }

    public String getHead10() {
        return this.head10;
    }
    
    public void setHead10(String head10) {
        this.head10 = head10;
    }

    public Date getSendTime() {
        return this.sendTime;
    }
    
    public void setSendTime(Date sendTime) {
        this.sendTime = sendTime;
    }

    public String getSendStatus() {
        return this.sendStatus;
    }
    
    public void setSendStatus(String sendStatus) {
        this.sendStatus = sendStatus;
    }

    public String getSendReport() {
        return this.sendReport;
    }
    
    public void setSendReport(String sendReport) {
        this.sendReport = sendReport;
    }

    public String getDirection() {
        return this.direction;
    }
    
    public void setDirection(String direction) {
        this.direction = direction;
    }

    public Date getCreateTime() {
        return this.createTime;
    }
    
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
   
}