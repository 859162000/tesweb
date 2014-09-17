package com.dc.tes.data.model;

import java.io.Serializable;
import java.util.Date;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import com.dc.tes.data.model.tag.BeanIdName;

/**
 * Transaction:交易 JavaBean映射类
 * 
 * @author huangzx
 * 
 */
@BeanIdName("transactionId")
public class Transaction implements Serializable {
	private static final long serialVersionUID = 1L;

	public static String N_TransID = "transactionId";
	public static String N_TranCode = "tranCode";
	public static String N_TranName = "tranName";
	public static String N_IsClientSimu = "isClientSimu";
	public static String N_Desc = "description";
	public static String N_Script = "script";
	public static String N_Category = "category";
	public static String N_SystemID = "systemId";
	public static String N_StateFlag = "flag";
	public static String N_Channel = "channel";
	public static String N_UserID = "userid";

	private String transactionId; // 交易ID
	private String tranCode; // 交易编码
	private String tranName; // 交易名称
	private int isClientSimu; // 客户端或服务器端 标示
	private String description; // 交易描述
	private String script; // 交易脚本
	private String requestStruct; // 请求报文结构
	private String responseStruct; // 响应报文结构
	private String category; // 交易分类
	private int flag; // 状态位
	// private Set<SysType> systemId ; //关联系统ID
	private String systemId; // 关联系统ID

	private String channel; // 通道名称
	
	private long maxdelaytime; //交易级延迟上限(单位:毫秒)
	private long mindelaytime; //交易级延迟下限(单位:毫秒)
	private String parameterGetSequence;
	private String transactionCategoryId;
	private int sqlDelayTime;
	
	private String createdUserId;
	private Date createdTime; 
	private Date lastModifiedTime;
	private String lastModifiedUserId;
	//private Set<Case> caseSet = new HashSet<Case>(0);	//关联的案例集合

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}

	/**
	 * 获取 关联的案例集合
	 * 
	 * @return 关联的案例集合
	 */
	//	public Set<Case> getCaseSet() {
	//		return caseSet;
	//	}
	/**
	 * 设置 关联的案例集合
	 * 
	 * @param caseSet
	 *            关联的案例集合
	 */
	//	public void setCaseSet(Set<Case> caseSet) {
	//		this.caseSet = caseSet;
	//	}
	/**
	 * 获取交易ID
	 * 
	 * @return transactionId：交易ID
	 */
	public String getTransactionId() {
		return transactionId;
	}

	/**
	 * 设置交易ID,此字段由数据库自动生成
	 * 
	 * @param transactionId
	 *            ：交易ID
	 */
	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}

	/**
	 * 获取交易编码
	 * 
	 * @return tranCode：交易编码
	 */
	public String getTranCode() {
		return tranCode;
	}

	/**
	 * 设置交易编码
	 * 
	 * @param tranCode
	 *            ：交易编码
	 */
	public void setTranCode(String tranCode) {
		this.tranCode = tranCode;
	}

	/**
	 * 获取交易名称
	 * 
	 * @return tranName：交易名称
	 */
	public String getTranName() {
		return tranName;
	}

	/**
	 * 设置交易名称
	 * 
	 * @param tranName
	 *            ：交易名称
	 */
	public void setTranName(String tranName) {
		this.tranName = tranName;
	}

	/**
	 * 获取客户端或服务器端标示
	 * 
	 * @return isClientSimu：0—客户端 1—服务器端
	 */
	public int getIsClientSimu() {
		return isClientSimu;
	}

	/**
	 * 设置客户端或服务器端标示
	 * 
	 * @param isClientSimu
	 *            ：1—客户端 0—服务器端
	 */
	public void setIsClientSimu(int isClientSimu) {
		this.isClientSimu = isClientSimu;
	}

	/**
	 * 获取交易描述
	 * 
	 * @return description：交易描述
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * 设置交易描述
	 * 
	 * @param description
	 *            ：交易描述
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * 获取交易脚本
	 * 
	 * @return script：交易脚本
	 */
	public String getScript() {
		return script;
	}

	/**
	 * 设置交易脚本
	 * 
	 * @param script
	 *            ：交易脚本
	 */
	public void setScript(String script) {
		this.script = script;
	}

	/**
	 * 获取请求报文结构
	 * 
	 * @return requestStruct：请求报文结构
	 */
	public String getRequestStruct() {
		return requestStruct;
	}

	/**
	 * 设置请求报文结构
	 * 
	 * @param requestStruct
	 *            ：请求报文结构
	 */
	public void setRequestStruct(String requestStruct) {
		this.requestStruct = requestStruct;
	}

	/**
	 * 获取响应报文结构
	 * 
	 * @return responseStruct：响应报文结构
	 */
	public String getResponseStruct() {
		return responseStruct;
	}

	/**
	 * 设置响应报文结构
	 * 
	 * @param responseStruct
	 *            ：响应报文结构
	 */
	public void setResponseStruct(String responseStruct) {
		this.responseStruct = responseStruct;
	}

	/**
	 * 获取交易分类
	 * 
	 * @return category：交易分类
	 */
	public String getCategory() {
		return category;
	}

	/**
	 * 设置交易分类
	 * 
	 * @param category
	 *            ：交易分类
	 */
	public void setCategory(String category) {
		this.category = category;
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
	 * @param flag
	 *            ：0—正常 1—删除
	 */
	public void setFlag(int flag) {
		this.flag = flag;
	}

	/**
	 * 获取交易相关联系统 ID
	 * 
	 * @return systemId：系统ID
	 */
	public String getSystemId() {
		return systemId;
	}

	/**
	 * 设置交易相关联系统 ID
	 * 
	 * @param systemId
	 *            ：系统ID
	 */
	public void setSystemId(String systemId) {
		this.systemId = systemId;
	}

	/**
	 * 获取 通道名称
	 * 
	 * @return 通道名称
	 */
	public String getChannel() {
		return channel;
	}

	/**
	 * 设置 通道名称
	 * 
	 * @param channel
	 *            通道名称
	 */
	public void setChannel(String channel) {
		this.channel = channel;
	}

	/**
	 * 获取 交易级延迟上限(单位:毫秒)
	 * @return 交易级延迟上限(单位:毫秒)
	 */
	public long getMaxdelaytime() {
		return maxdelaytime;
	}
	
	/**
	 * 设置 交易级延迟上限(单位:毫秒)
	 * @param updelaytime 交易级延迟上限(单位:毫秒)
	 */
	public void setMaxdelaytime(long maxdelaytime) {
		this.maxdelaytime = maxdelaytime;
	}

	/**
	 * 获取 交易级延迟下限(单位:毫秒)
	 * @return 交易级延迟下限(单位:毫秒)
	 */
	public long getMindelaytime() {
		return mindelaytime;
	}

	/**
	 * 设置 交易级延迟下限(单位:毫秒)
	 * @param downdelaytime 交易级延迟下限(单位:毫秒)
	 */
	public void setMindelaytime(long mindelaytime) {
		this.mindelaytime = mindelaytime;
	}

	public String getParameterGetSequence() {
		return parameterGetSequence;
	}

	public void setParameterGetSequence(String parameterGetSequence) {
		this.parameterGetSequence = parameterGetSequence;
	}

	public String getTransactionCategoryId() {
		return transactionCategoryId;
	}

	public void setTransactionCategoryId(String transactionCategoryId) {
		this.transactionCategoryId = transactionCategoryId;
	}

	public void setSqlDelayTime(int sqlDelayTime) {
		this.sqlDelayTime = sqlDelayTime;
	}

	public int getSqlDelayTime() {
		return sqlDelayTime;
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

	/**
	 * @param createdTime the createdTime to set
	 */
	public void setCreatedTime(Date createdTime) {
		this.createdTime = createdTime;
	}

	/**
	 * @return the createdTime
	 */
	public Date getCreatedTime() {
		return createdTime;
	}

	public void setLastModifiedUserId(String lastModifiedUserId) {
		this.lastModifiedUserId = lastModifiedUserId;
	}

	public String getLastModifiedUserId() {
		return lastModifiedUserId;
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
		
	
}
