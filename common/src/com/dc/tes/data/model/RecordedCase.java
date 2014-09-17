package com.dc.tes.data.model;

import java.io.Serializable;
import java.util.Date;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import com.dc.tes.data.model.tag.BeanIdName;

/**
 * recorded_case:主机 JavaBean映射类
 * 
 * @author 黄智祥
 * 
 */
@BeanIdName("id")
public class RecordedCase implements Serializable {

	private static final long serialVersionUID = 1533421657096928960L;

	private Integer id;
	private Integer systemId;
	private String requestMsg;
	private String responseMsg;
	private Integer responseFlag;
	private Integer isCased;
	private Integer recordUserId;
	private String memo;
	private String createTime;
	private Date recordTime;
	private Date lastModifiedTime;
	private String lastModifiedUserId;


	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}

	public RecordedCase() {

	}

	public RecordedCase(Integer id, String requestMsg, String responseMsg, Integer systemId, Integer responseFlag, Integer recordUserId, Integer isCased, String memo, Date recordTime) {
		this.id = id;
		this.requestMsg = requestMsg;
		this.responseMsg = responseMsg;
		this.systemId = systemId;
		this.responseFlag = responseFlag;
		this.recordUserId = recordUserId;
		this.isCased = isCased;
		this.memo = memo;
		this.recordTime = recordTime;
	}
	

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getRequestMsg() {
		return requestMsg;
	}

	public void setRequestMsg(String requestMsg) {
		this.requestMsg = requestMsg;
	}

	public String getResponseMsg() {
		return responseMsg;
	}

	public void setResponseMsg(String responseMsg) {
		this.responseMsg = responseMsg;
	}

	public Integer getSystemId() {
		return systemId;
	}

	public void setSystemId(Integer systemId) {
		this.systemId = systemId;
	}

	public Integer getResponseFlag() {
		return responseFlag;
	}

	public void setResponseFlag(Integer responseFlag) {
		this.responseFlag = responseFlag;
	}

	
	public void setRecordUserId(Integer recordUserId) {
		this.recordUserId = recordUserId;
	}

	public Integer getRecordUserId() {
		return recordUserId;
	}

	public void setRecordTime(Date recordTime) {
		this.recordTime = recordTime;
	}

	public Date getRecordTime() {
		return recordTime;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

	public void setIsCased(Integer isCased) {
		this.isCased = isCased;
	}

	public Integer getIsCased() {
		return isCased;
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
