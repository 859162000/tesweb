package com.dc.tes.data.model;

import com.dc.tes.data.model.tag.BeanIdName;

@BeanIdName("id")
public class OperationLog implements java.io.Serializable {

	// Fields

	/**
	 * 
	 */
	private static final long serialVersionUID = 1957613466484061271L;
	private Integer id;
	private String systemId;
	private String userId;
	private Integer loginLogId;
	private Integer objId;
	private String objName;
	private Integer iduType;
	private Integer opType;
	private String opField;
	private String oldValue;
	private String newValue;
	private String memo;

	// Constructors

	/** default constructor */
	public OperationLog() {
	}

	/** minimal constructor */
	public OperationLog(String systemId, String userId) {
		this.systemId = systemId;
		this.userId = userId;
	}

	/** full constructor */
	public OperationLog(String systemid, String userid, Integer loginlogid,
			Integer idutype, Integer optype, String oldvalue, String newvalue,
			String memo) {
		this.systemId = systemid;
		this.userId = userid;
		this.loginLogId = loginlogid;
		this.iduType = idutype;
		this.opType = optype;
		this.oldValue = oldvalue;
		this.newValue = newvalue;
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
		return systemId;
	}

	public String getUserId() {
		return userId;
	}

	public Integer getLoginLogId() {
		return loginLogId;
	}

	public Integer getIduType() {
		return iduType;
	}

	public Integer getOpType() {
		return opType;
	}

	public String getOldValue() {
		return oldValue;
	}

	public String getNewValue() {
		return newValue;
	}

	public String getMemo() {
		return memo;
	}

	public void setSystemId(String systemId) {
		this.systemId = systemId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public void setLoginLogId(Integer loginLogId) {
		this.loginLogId = loginLogId;
	}

	public void setIduType(Integer iduType) {
		this.iduType = iduType;
	}

	public void setOpType(Integer opType) {
		this.opType = opType;
	}

	public void setOldValue(String oldValue) {
		this.oldValue = oldValue;
	}

	public void setNewValue(String newValue) {
		this.newValue = newValue;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

	public void setOpField(String opField) {
		this.opField = opField;
	}

	public String getOpField() {
		return opField;
	}

	public void setObjName(String objName) {
		this.objName = objName;
	}

	public String getObjName() {
		return objName;
	}

	public void setObjId(Integer objId) {
		this.objId = objId;
	}

	public Integer getObjId() {
		return objId;
	}

}