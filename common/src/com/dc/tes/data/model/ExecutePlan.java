package com.dc.tes.data.model;

import java.io.Serializable;
import java.util.Date;

import com.dc.tes.data.model.tag.BeanIdName;

@BeanIdName("id")
public class ExecutePlan implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -467974775855284543L;
	private Integer id;
	private String name;
	private String description;
	private String systemId;
	private String createdUserId;
	private Date createdTime;
	private int status;
	private int scheduleRunMode;
	private String scheduleRunWeekDay;
	private String scheduleRunHour;
	private Date lastModifiedTime;
	private String lastModifiedUserId;
	
	public Integer getId() {
		return id;
	}
	public String getName() {
		return name;
	}
	public String getDescription() {
		return description;
	}
	public String getSystemId() {
		return systemId;
	}
	public String getCreatedUserId() {
		return createdUserId;
	}
	public Date getCreatedTime() {
		return createdTime;
	}
	public int getStatus() {
		return status;
	}
	public int getScheduleRunMode() {
		return scheduleRunMode;
	}
	public String getScheduleRunWeekDay() {
		return scheduleRunWeekDay;
	}
	public String getScheduleRunHour() {
		return scheduleRunHour;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public void setName(String name) {
		this.name = name;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public void setSystemId(String systemId) {
		this.systemId = systemId;
	}
	public void setCreatedUserId(String createUserId) {
		this.createdUserId = createUserId;
	}
	public void setCreatedTime(Date createTime) {
		this.createdTime = createTime;
	}
	public void setStatus(int iStatus) {
		this.status = iStatus;
	}
	public void setScheduleRunMode(int scheduleRunMode) {
		this.scheduleRunMode = scheduleRunMode;
	}
	public void setScheduleRunWeekDay(String scheduleRunWeekDay) {
		this.scheduleRunWeekDay = scheduleRunWeekDay;
	}
	public void setScheduleRunHour(String scheduleRunHour) {
		this.scheduleRunHour = scheduleRunHour;
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
