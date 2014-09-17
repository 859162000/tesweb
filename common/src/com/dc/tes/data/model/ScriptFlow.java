package com.dc.tes.data.model;

import java.io.Serializable;
import java.util.Date;

import com.dc.tes.data.model.tag.BeanIdName;

/**
 * ScriptFlow：业务流  JavaBean映射
 * @author huangzx
 *
 */

@BeanIdName("id")
public class ScriptFlow implements Serializable{

	private static final long serialVersionUID = 6995871018570528803L;

	private String id;	//Id
	private String name;	//业务流名称
	private String description;	//描述
	private String srcipt;	//脚本
	
	private String systemid;//关联系统ID

	private String createdUserId;
	private Date createdTime; 
	private Date lastModifiedTime;
	private String lastModifiedUserId;
	
	/**
	 * 获取 业务流ID
	 * @return	业务流ID 
	 */
	public String getId() {
		return id;
	}

	/**
	 * 设置 业务流ID
	 * @param id	业务流ID
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * 获取 业务流名称
	 * @return	业务流名称
	 */
	public String getName() {
		return name;
	}

	/**
	 * 设置 业务流名称
	 * @param name	业务流名称
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * 获取 描述信息
	 * @return	描述信息
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * 设置 描述信息
	 * @param description 描述信息
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * 获取 脚本
	 * @return 脚本
	 */
	public String getSrcipt() {
		return srcipt;
	}

	/**
	 * 设置 脚本
	 * @param srcipt 脚本
	 */
	public void setSrcipt(String srcipt) {
		this.srcipt = srcipt;
	}

	/**
	 * 获取 关联系统ID
	 * @return 关联系统ID
	 */
	public String getSystemid() {
		return systemid;
	}

	/**
	 * 设置 关联系统ID
	 * @param systemid 关联系统ID
	 */
	public void setSystemid(String systemid) {
		this.systemid = systemid;
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

	/**
	 * @param lastModifiedUserId the lastModifiedUserId to set
	 */
	public void setLastModifiedUserId(String lastModifiedUserId) {
		this.lastModifiedUserId = lastModifiedUserId;
	}

	/**
	 * @return the lastModifiedUserId
	 */
	public String getLastModifiedUserId() {
		return lastModifiedUserId;
	}
	
	
}
