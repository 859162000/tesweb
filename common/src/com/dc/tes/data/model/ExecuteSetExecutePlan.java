package com.dc.tes.data.model;

import java.io.Serializable;
import java.util.Date;

import com.dc.tes.data.model.tag.BeanIdName;

@BeanIdName("id")
public class ExecuteSetExecutePlan implements Serializable {

	/**
	 * 
	 */
	
	private static final long serialVersionUID = -9140197898606627723L;
	private Integer id;
	private String executeSetDirId;
	private Integer executePlanId;
	private String systemId;
	private String addUserId;
	private Date addTime;
	private int scheduledRunStatus;
	private Date beginRunTime;
	private Date endRunTime;
	private String scheduleRunMachine;
	private String scheduleRunMacName;
	
	public Integer getId() {
		return id;
	}
	public String getExecuteSetDirId() {
		return executeSetDirId;
	}
	public Integer getExecutePlanId() {
		return executePlanId;
	}
	public String getAddUserId() {
		return addUserId;
	}
	public Date getAddTime() {
		return addTime;
	}
	public Date getBeginRunTime() {
		return beginRunTime;
	}
	public Date getEndRunTime() {
		return endRunTime;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public void setExecuteSetDirId(String executeSetDirId) {
		this.executeSetDirId = executeSetDirId;
	}
	public void setExecutePlanId(Integer executePlanId) {
		this.executePlanId = executePlanId;
	}
	
	/**
	 * @param systemid the systemid to set
	 */
	public void setSystemId(String systemId) {
		this.systemId = systemId;
	}

	/**
	 * @return the systemid
	 */
	public String getSystemId() {
		return systemId;
	}

	public void setAddUserId(String addUserId) {
		this.addUserId = addUserId;
	}
	public void setAddTime(Date addTime) {
		this.addTime = addTime;
	}
	public void setBeginRunTime(Date beginRunTime) {
		this.beginRunTime = beginRunTime;
	}
	public void setEndRunTime(Date endRunTime) {
		this.endRunTime = endRunTime;
	}
	public void setScheduledRunStatus(int scheduledRunStatus) {
		this.scheduledRunStatus = scheduledRunStatus;
	}
	public int getScheduledRunStatus() {
		return scheduledRunStatus;
	}
	public void setScheduleRunMachine(String scheduleRunMachine) {
		this.scheduleRunMachine = scheduleRunMachine;
	}
	public String getScheduleRunMachine() {
		return scheduleRunMachine;
	}
	public void setScheduleRunMacName(String scheduleRunMacName) {
		this.scheduleRunMacName = scheduleRunMacName;
	}
	public String getScheduleRunMacName() {
		return scheduleRunMacName;
	}
}
