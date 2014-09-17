package com.dc.tes.data.model;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.dc.tes.data.model.tag.BeanIdName;

/**
 * Interfacedef entity. @author MyEclipse Persistence Tools
 */
@BeanIdName("interfaceId")
public class InterfaceDef implements java.io.Serializable {

	// Fields

	/**
	 * 
	 */
	private static final long serialVersionUID = -8884072190245236879L;
	private Integer interfaceId;
	private String systemId;
	private String interfaceName;
	private String chineseName;
	private Integer interfaceLen;
	private Integer fieldCount;
	private String importUserId;
	private Date importTime;
	private String memo;
	

	// Constructors

	/** default constructor */
	public InterfaceDef() {
	}

	/** minimal constructor */
	public InterfaceDef(Integer interfaceId, String systemId,
			String importUserId, Date importTime) {
		this.interfaceId = interfaceId;
		this.setSystemId(systemId);
		this.importUserId = importUserId;
		this.importTime = importTime;
	}

	/** full constructor */
	public InterfaceDef(Integer interfaceId, String systemId,
			String interfaceName, String chineseName, Integer interfaceLen, Integer fieldCount,
			String importUserId, Date importTime, String memo) {
		this.interfaceId = interfaceId;
		this.setSystemId(systemId);
		this.interfaceName = interfaceName;
		this.chineseName = chineseName;
		this.interfaceLen = interfaceLen;
		this.fieldCount = fieldCount;
		this.importUserId = importUserId;
		this.importTime = importTime;
		this.memo = memo;
	}

	// Property accessors

	public Integer getInterfaceId() {
		return this.interfaceId;
	}

	public void setInterfaceId(Integer interfaceId) {
		this.interfaceId = interfaceId;
	}


	public String getInterfaceName() {
		return this.interfaceName;
	}

	public void setInterfaceName(String interfaceName) {
		this.interfaceName = interfaceName;
	}

	public Integer getInterfaceLen() {
		return this.interfaceLen;
	}

	public void setInterfaceLen(Integer interfaceLen) {
		this.interfaceLen = interfaceLen;
	}

	public Integer getFieldCount() {
		return this.fieldCount;
	}

	public void setFieldCount(Integer fieldCount) {
		this.fieldCount = fieldCount;
	}

	public String getImportUserId() {
		return this.importUserId;
	}

	public void setImportUserId(String importUserId) {
		this.importUserId = importUserId;
	}

	public Date getImportTime() {
		return this.importTime;
	}

	public void setImportTime(Date importTime) {
		this.importTime = importTime;
	}

	public String getMemo() {
		return this.memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

	public void setSystemId(String systemId) {
		this.systemId = systemId;
	}

	public String getSystemId() {
		return systemId;
	}

	public void setChineseName(String chineseName) {
		this.chineseName = chineseName;
	}

	public String getChineseName() {
		return chineseName;
	}

}