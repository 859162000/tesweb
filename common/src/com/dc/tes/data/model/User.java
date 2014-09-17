package com.dc.tes.data.model;

import java.io.Serializable;
import java.util.Date;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import com.dc.tes.data.model.tag.BeanIdName;

/**
 * USER:用户 JavaBean映射类
 * 
 * @author huangzx
 * 
 */
@BeanIdName("id")
public class User implements Serializable {

	private static final long serialVersionUID = 1L;

	private String id; // 数据库 USER 表 ID
	private String name; // 用户名 数据库唯一性约束
	private String password; // 用户 密码
	private String description; // 用户 说明
	private int isAdmin; // 是否是 管理员
	private int flag; // 状态位
	
	private String createdUserId;
	private Date createdTime; 
	private Date lastModifiedTime;
	private String lastModifiedUserId;

	// 用户与系统
	/*
	 * private Set User2System = new HashSet(0);
	 * 
	 * public Set getUser2System() { return User2System; }
	 * 
	 * public void setUser2System(Set user2System) { User2System = user2System;
	 * }
	 */

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}

	public User() {

	}

	public User(String id, String name, String description, String password, int isAdmin, int flag) {
		this.id = id;
		this.name = name;
		this.description = description;
		this.password = password;
		this.isAdmin = isAdmin;
		this.flag = flag;
	}

	/**
	 * 获取用户ID
	 * 
	 * @return id:用户ID
	 */
	public String getId() {
		return id;
	}

	/**
	 * 设置用户ID,此字段由数据库自动生成
	 * 
	 * @param id
	 *            :用户ID
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * 获取用户登录名称
	 * 
	 * @return name：登陆名称
	 */
	public String getName() {
		return name;
	}

	/**
	 * 设置用户登录名称
	 * 
	 * @param name
	 *            ：登陆名称
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * 获取用户密码
	 * 
	 * @return password：用户密码
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * 设置用户密码
	 * 
	 * @param password
	 *            ：用户密码
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * 获取用户说明
	 * 
	 * @return description：用户说明
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * 设置用户说明
	 * 
	 * @param description
	 *            ：用户说明
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	
	/**
	 * 获取管理员标志
	 * 
	 * @return isAdmin： 0—是 1—否
	 */
	public int getIsAdmin() {
		return isAdmin;
	}

	/**
	 * 设置管理员标志
	 * 
	 * @param isAdmin ：
	 *            0—是 1—否
	 */
	public void setIsAdmin(int isAdmin) {
		this.isAdmin = isAdmin;
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
	 * 重构 equals 方法
	 */
	public boolean equals(User newUser) {
		return this.getId().equals(newUser.getId());
	}

	public void setLastModifiedTime(Date lastModifiedTime) {
		this.lastModifiedTime = lastModifiedTime;
	}

	public Date getLastModifiedTime() {
		return lastModifiedTime;
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

	public void setLastModifiedUserId(String lastModifiedUserId) {
		this.lastModifiedUserId = lastModifiedUserId;
	}

	public String getLastModifiedUserId() {
		return lastModifiedUserId;
	}

}
