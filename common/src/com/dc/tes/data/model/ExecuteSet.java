package com.dc.tes.data.model;

import java.io.Serializable;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;

import com.dc.tes.data.model.tag.BeanIdName;

/**
 * ExecuteSet: 任务队列 JavaBean 映射
 * 
 * @author huangzx
 * 
 */

@BeanIdName("id")
public class ExecuteSet implements Serializable {

	private static final long serialVersionUID = 3073522734118940058L;

	private String id; // ID 
	private String name; // 任务队列 名称
	private String importBatchNo; //导入批次号
	private String description; //描述信息

	private String systemId; //关联系统ID

	private Set<ExecuteSetTaskItem> taskItems = new LinkedHashSet<ExecuteSetTaskItem>(); //关联的任务队列任务
	private Set<ExecuteSetLogStat> executeSetLogs = new LinkedHashSet<ExecuteSetLogStat>(); //关联的任务执行历史
	
	private String createdUserId;
	private Date createdTime; 
	private Date lastModifiedTime;
	private String lastModifiedUserId;
	
	/**
	 * 获取 关联 任务队列任务 集合
	 * 
	 * @return 任务队列任务
	 */
	public Set<ExecuteSetTaskItem> getTaskItems() {
		return taskItems;
	}

	/**
	 * 设置 关联 任务队列任务 集合
	 * 
	 * @param taskItems
	 *            任务队列任务 集合
	 */
	public void setTaskItems(Set<ExecuteSetTaskItem> taskItems) {
		this.taskItems = taskItems;
	}
	
	/**
	 * 设置 任务执行历史 集合
	 * 
	 * @param executeSetLogs
	 *            任务执行历史 集合
	 */
	public void setExecuteSetLogs(Set<ExecuteSetLogStat> executeSetLogs) {
		this.executeSetLogs = executeSetLogs;
	}
	
	/**
	 * 获取 任务执行历史 集合
	 * 
	 * @return 任务执行历史
	 */
	public Set<ExecuteSetLogStat> getExecuteSetLogs() {
		return executeSetLogs;
	}

	/**
	 * 获取 任务队列ID
	 * 
	 * @return 任务队列ID
	 */
	public String getId() {
		return id;
	}

	/**
	 * 设置 任务队列ID
	 * 
	 * @param id
	 *            任务队列ID
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * 获取 任务队列 名称
	 * 
	 * @return 任务队列 名称
	 */
	public String getName() {
		return name;
	}

	/**
	 * 设置 任务队列 名称
	 * 
	 * @param name
	 *            任务队列 名称
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * 获取 任务队列 名称
	 * 
	 * @return 任务队列 批次号
	 */
	public String getImportBatchNo() {
		return importBatchNo;
	}

	/**
	 * 设置 任务队列 批次号
	 * 
	 * @param name
	 *            任务队列 批次号
	 */
	public void setImportBatchNo(String importBatchNo) {
		this.importBatchNo = importBatchNo;
	}
	
	/**
	 * 获取 任务队列描述信息
	 * 
	 * @return 任务队列描述信息
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * 设置 任务队列描述信息
	 * 
	 * @param description
	 *            任务队列描述信息
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * 获取 关联系统ID
	 * 
	 * @return 关联系统ID
	 */
	public String getSystemId() {
		return systemId;
	}

	/**
	 * 设置 关联系统ID
	 * 
	 * @param systemid
	 *            关联系统ID
	 */
	public void setSystemId(String systemid) {
		this.systemId = systemid;
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
