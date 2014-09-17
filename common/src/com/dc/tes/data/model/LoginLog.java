package com.dc.tes.data.model;

import java.util.Date;

import com.dc.tes.data.model.tag.BeanIdName;

@BeanIdName("id")
public class LoginLog implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2635022235681151081L;
	// Fields

	private Integer id;
	private String systemId;
	private String userId;
	private String ipAddress;
	private String machineName;
	private Integer loginCount;
	private Date loginTime;
	private Date logoutTime;
	private String memo;

	// Constructors

	/** default constructor */
	public LoginLog() {
	}

	/** minimal constructor */
	public LoginLog(String systemid, String userid) {
		this.systemId = systemid;
		this.userId = userid;
	}

	/** full constructor */
	public LoginLog(String systemid, String userid, String ipaddress,
			String machinename, Integer logincount, Date logintime,
			Date logouttime, String memo) {
		this.systemId = systemid;
		this.userId = userid;
		this.ipAddress = ipaddress;
		this.machineName = machinename;
		this.loginCount = logincount;
		this.loginTime = logintime;
		this.logoutTime = logouttime;
		this.memo = memo;
	}

	// Property accessors

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getSystemId() {
		return this.systemId;
	}

	public void setSystemId(String systemid) {
		this.systemId = systemid;
	}

	public String getUserId() {
		return this.userId;
	}

	public void setUserId(String userid) {
		this.userId = userid;
	}

	public String getIpAddress() {
		return this.ipAddress;
	}

	public void setIpAddress(String ipaddress) {
		this.ipAddress = ipaddress;
	}

	public String getMachineName() {
		return this.machineName;
	}

	public void setMachineName(String machinename) {
		this.machineName = machinename;
	}

	public Integer getLoginCount() {
		return this.loginCount;
	}

	public void setLoginCount(Integer logincount) {
		this.loginCount = logincount;
	}

	public Date getLoginTime() {
		return this.loginTime;
	}

	public void setLoginTime(Date logintime) {
		this.loginTime = logintime;
	}

	public Date getLogoutTime() {
		return this.logoutTime;
	}

	public void setLogoutTime(Date logouttime) {
		this.logoutTime = logouttime;
	}

	public String getMemo() {
		return this.memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

}