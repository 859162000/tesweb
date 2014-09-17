package com.dc.tes.data.model;

import java.io.Serializable;
import java.util.Date;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import com.dc.tes.data.model.tag.BeanIdName;

/**
 * tes_cmbhost:主机 JavaBean映射类
 * 
 * @author 黄智祥
 * 
 */
@BeanIdName("hostid")
public class DbHost implements Serializable {

	private static final long serialVersionUID = 1L;

	private String hostid; // 数据库 tes_cmbhost 表 HOSTID
	private String dbHostName; // 主机名
	private String ipaddress; // ip地址
	private int portnum; // 端口号
	private String description; // 说明
	private String systemId;
	private int isLongConn;
	private String dbType;
	private String dbName;
	private String dbUser;
	private String dbPwd;
	private String osType;
	
	private String createdUserId;
	private Date createdTime; 
	private Date lastModifiedTime;
	private String lastModifiedUserId;

	// 主机与RP/RV
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

	public DbHost() {

	}

	public DbHost(String hostid, String dbHostName, String ipaddress, int portnum, String description) {
		this.hostid = hostid;
		this.dbHostName = dbHostName;
		this.ipaddress = ipaddress;
		this.portnum = portnum;
		this.description = description;
	}

	/**
	 * 获取用户ID
	 * 
	 * @return hostid:主机ID
	 */
	public String getHostid() {
		return hostid;
	}

	/**
	 * 设置用户ID,此字段由数据库自动生成
	 * 
	 * @param hostid
	 *            :主机ID
	 */
	public void setHostid(String hostid) {
		this.hostid = hostid;
	}

	/**
	 * 获取Cmbhost
	 * 
	 * @return DbHost
	 */
	public String getDbHostName() {
		return dbHostName;
	}

	/**
	 * 设置Cmbhost
	 * 
	 * @param dbHostName
	 *            ：DbHost
	 */
	public void setDbHostName(String dbHostName) {
		this.dbHostName = dbHostName;
	}

	/**
	 * 获取ip地址
	 * 
	 * @return ipaddress：ip地址
	 */
	public String getIpaddress() {
		return ipaddress;
	}

	/**
	 * 设置用户登录名称
	 * 
	 * @param ipaddress
	 *            ：ip地址
	 */
	public void setIpaddress(String ipaddress) {
		this.ipaddress = ipaddress;
	}

	/**
	 * 获取端口号
	 * 
	 * @return portnum： 端口号
	 */
	public int getPortnum() {
		return portnum;
	}

	/**
	 * 设置端口号
	 * 
	 * @param portnum ：
	 *            端口号
	 */
	public void setPortnum(int portnum) {
		this.portnum = portnum;
	}

	
	/**
	 * 获取说明
	 * 
	 * @return description：说明
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * 设置说明
	 * 
	 * @param description
	 *            ：说明
	 */
	public void setDescription(String description) {
		this.description = description;
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

	public void setIsLongConn(int isLongConn) {
		this.isLongConn = isLongConn;
	}

	public int getIsLongConn() {
		return isLongConn;
	}

	public String getDbType() {
		return dbType;
	}

	public void setDbType(String dbType) {
		this.dbType = dbType;
	}

	public String getDbUser() {
		return dbUser;
	}

	public void setDbUser(String dbUser) {
		this.dbUser = dbUser;
	}

	public String getDbPwd() {
		return dbPwd;
	}

	public void setDbPwd(String dbPwd) {
		this.dbPwd = dbPwd;
	}

	public String getDbName() {
		return dbName;
	}

	public void setDbName(String dbName) {
		this.dbName = dbName;
	}

	public void setOsType(String osType) {
		this.osType = osType;
	}

	public String getOsType() {
		return osType;
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


	/**
	 * 重构 equals 方法
	 */
	/*public boolean equals(DbHost newHost) {
		return this.getHostId().equals(newHost.getHostId());
	}*/

}
