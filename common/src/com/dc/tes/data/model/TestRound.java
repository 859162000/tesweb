package com.dc.tes.data.model;

import java.io.Serializable;
import java.util.Date;

import com.dc.tes.data.model.tag.BeanIdName;

@BeanIdName("roundId")
public class TestRound implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -8557418527967805258L;
	private Integer roundId;
	private String systemId;
	private Integer roundNo;
	private String roundName;
	private String description;
	private Date startDate;
	private Date endDate;
	private int currentRoundFlag;
	
	private String createdUserId;
	private Date createdTime; 
	private Date lastModifiedTime;
	private String lastModifiedUserId;
	
	public Integer getRoundId() {
		return roundId;
	}
	public String getSystemId() {
		return systemId;
	}
	public Integer getRoundNo() {
		return roundNo;
	}
	public String getRoundName() {
		return roundName;
	}
	public String getDescription() {
		return description;
	}
	public Date getStartDate() {
		return startDate;
	}
	public Date getEndDate() {
		return endDate;
	}
	public int getCurrentRoundFlag() {
		return currentRoundFlag;
	}
	public void setRoundId(Integer roundId) {
		this.roundId = roundId;
	}
	public void setSystemId(String systemId) {
		this.systemId = systemId;
	}
	public void setRoundNo(Integer roundNo) {
		this.roundNo = roundNo;
	}
	public void setRoundName(String roundName) {
		this.roundName = roundName;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	public void setCurrentRoundFlag(int currentRoundFlag) {
		this.currentRoundFlag = currentRoundFlag;
	}
	public void setCreatedUserId(String createdUserId) {
		this.createdUserId = createdUserId;
	}
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
