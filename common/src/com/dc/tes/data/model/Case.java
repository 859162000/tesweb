package com.dc.tes.data.model;

import java.io.Serializable;
import java.util.Date;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import com.dc.tes.data.model.tag.BeanIdName;

/**
 * Case:案例 JavaBean映射类
 * 
 * @author huangzx
 */
@BeanIdName("caseId")
public class Case implements Serializable {
	private static final long serialVersionUID = 1L;

	private String caseId; //案例ID
	private String caseName; //案例名称
	private byte[] requestMsg; //请求报文，二进制的
	private String requestXml; //组包前请求报文，XML
	private byte[] responseMsg; //应答报文，二进制的
	private String responseXml; //应答报文，XML
	private int isParseable; //是否可解析
	private int flag; //状态位
	private String transactionId; //相关联交易 ID
	//预期结果
	private String expectedXml; //预期结果
	private int isdefault; //是否默认案例 0-默认 1-否
	private String caseNo; //案例编号
	private Integer cardId;  //卡ID',
	private String importBatchNo;  //'案例导入时的批次号',
	private Float amount;  //'交易金额', 	
	private String description;  //案例说明',
	private CaseFlow caseFlow;
	private Integer sequence;
	private Integer breakPointFlag;
	
	private String createdUserId;
	private Date createdTime; 
	private Date lastModifiedTime;
	private String lastModifiedUserId;
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}

	/**
	 * 获取 案例关联的交易
	 * @return 案例关联的交易
	 */
//	public Transaction getTransaction() {
//		return transaction;
//	}

/**
 * 设置 案例关联的交易
 * @param transaction 案例关联的交易
 */
//	public void setTransaction(Transaction transaction) {
//		this.transaction = transaction;
//	}


	/**
	 * 获取案例ID
	 * 
	 * @return caseId:案例ID
	 */
	public String getCaseId() {
		return caseId;
	}

	/**
	 * 设置案例ID,此字段由数据库自动生成
	 * 
	 * @param caseId:案例ID
	 */
	public void setCaseId(String caseId) {
		this.caseId = caseId;
	}

	/**
	 * 获取案例名称
	 * 
	 * @return caseName：案例名称
	 */
	public String getCaseName() {
		return caseName;
	}

	/**
	 * 设置案例名称
	 * 
	 * @param caseName：案例名称
	 */
	public void setCaseName(String caseName) {
		this.caseName = caseName;
	}

	/**
	 * 获取请求报文模版
	 * 
	 * @return requestMsg：请求报文模版
	 */
	public byte[] getRequestMsg() {
		return requestMsg;
	}

	/**
	 * 设置请求报文模版
	 * 
	 * @param requestMsg：请求报文模版
	 */
	public void setRequestMsg(byte[] requestMsg) {
		this.requestMsg = requestMsg;
	}

	/**
	 * 获取请求报文XML模版
	 * 
	 * @return requestXml：请求报文XML模版
	 */
	public String getRequestXml() {
		return requestXml;
	}

	/**
	 * 设置请求报文XML模版
	 * 
	 * @param requestXml：请求报文XML模版
	 */
	public void setRequestXml(String requestXml) {
		this.requestXml = requestXml;
	}

	/**
	 * 获取响应报文模版
	 * 
	 * @return responseMsg：响应报文模版
	 */
	public byte[] getResponseMsg() {
		return responseMsg;
	}

	/**
	 * 设置响应报文模版
	 * 
	 * @param responseMsg：响应报文模版
	 */
	public void setResponseMsg(byte[] responseMsg) {
		this.responseMsg = responseMsg;
	}

	/**
	 * 获取响应报文XML模版
	 * 
	 * @return responseXml：响应报文XML模版
	 */
	public String getResponseXml() {
		return responseXml;
	}

	/**
	 * 设置响应报文XML模版
	 * 
	 * @param responseXml：响应报文XML模版
	 */
	public void setResponseXml(String responseXml) {
		this.responseXml = responseXml;
	}

	/**
	 * 获取是否可解析标示
	 * 
	 * @return isParseable：0—不可解析 1—可以解析
	 */
	public int getIsParseable() {
		return isParseable;
	}

	/**
	 * 设置是否可解析标示
	 * 
	 * @param isParseable：0—不可解析
	 *            1—可以解析
	 */
	public void setIsParseable(int isParseable) {
		this.isParseable = isParseable;
	}

	/**
	 * 获取状态位
	 * 
	 * @return flag：0—正常 1—删除
	 */
	public int getFlag() {
		return flag;
	}

	/**
	 * 设置状态位
	 * 
	 * @param flag：0—正常
	 *            1—删除
	 */
	public void setFlag(int flag) {
		this.flag = flag;
	}

	/**
	 * 获取相关联交易ID
	 * 
	 * @return transactionId：交易ID
	 */
	public String getTransactionId() {
		return transactionId;
	}

	/**
	 * 设置相关联交易ID
	 * 
	 * @param transactionId：交易ID
	 */
	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}

	/**
	 * 获取 预期结果
	 * 
	 * @return oraclecontent:预期结果
	 */
	public String getExpectedXml() {
		return expectedXml;
	}

	/**
	 * 设置 预期结果
	 * 
	 * @param oraclecontent:预期结果
	 */
	public void setExpectedXml(String expectedXml) {
		this.expectedXml = expectedXml;
	}

	/**
	 * 获取 是否默认案例 0-默认 1-否
	 * @return 是否默认案例 0-默认 1-否
	 */
	public int getIsdefault() {
		return isdefault;
	}

	/**
	 * 设置 是否默认案例 0-默认 1-否
	 * @param isdefault 是否默认案例 0-默认 1-否
	 */
	public void setIsdefault(int isdefault) {
		this.isdefault = isdefault;
	}

	
	/**
	 * 获取案例编号',
	 * @return 案例编号
	 */
	public String getCaseNo() {
		return caseNo;
	}
	/**
	 *设置案例编号',
	 * @return 
	 */
	public void setCaseNo(String caseNo) {
		this.caseNo = caseNo;
	}
	/**
	 * 获取卡ID
	 * @return 卡ID
	 */
	public Integer getCardId() {
		return cardId;
	}
	/**
	 *设置案例编号',
	 * @return 
	 */
	public void setCardId(Integer cardId) {
		this.cardId = cardId;
	}
	/**
	 * 获取案例导入时的批次号
	 * @return 案例导入时的批次号
	 */
	public String getImportBatchNo() {
		return importBatchNo;
	}
	/**
	 *设置案例导入时的批次号
	 * @return 
	 */
	public void setImportBatchNo(String importBatchNo) {
		this.importBatchNo = importBatchNo;
	}
	/**
	 * 获取交易金额
	 * @return 交易金额
	 */
	public Float getAmount() {
		return amount;
	}
	/**
	 *设置交易金额
	 * @return 
	 */
	public void setAmount(Float amount) {
		this.amount = amount;
	}
	/**
	 * 获取39域预期值
	 * @return 39域预期值
	 */
	
	
	/**
	 * 获取案例说明
	 * @return 案例说明
	 */
	public String getDescription() {
		return description;
	}
	/**
	 *设置案例说明
	 * @return 
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	public CaseFlow getCaseFlow() {
		return caseFlow;
	}

	public void setCaseFlow(CaseFlow caseFlow) {
		this.caseFlow = caseFlow;
	}

	public Integer getSequence() {
		if (sequence == null)
			return 0;
		return sequence;
	}

	public void setSequence(Integer sequence) {
		this.sequence = sequence;
	}

	public void setBreakPointFlag(Integer breakPointFlag) {
		this.breakPointFlag = breakPointFlag;
	}

	public Integer getBreakPointFlag() {
		return breakPointFlag;
	}

	/**
	 * @param createdUserId the createdUserId to set
	 */
	public void setCreatedUserId(String createdUserId) {
		this.createdUserId = createdUserId;
	}

	/**
	 * @return the createdUserId
	 */
	public String getCreatedUserId() {
		return createdUserId;
	}

	public void setCreatedTime(Date createdTime) {
		this.createdTime = createdTime;
	}

	public Date getCreatedTime() {
		return createdTime;
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
