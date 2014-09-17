package com.dc.tes.data.model;

import com.dc.tes.data.model.tag.BeanIdName;

/**
 * Interfacefield entity. @author MyEclipse Persistence Tools
 */
@BeanIdName("fieldId")
public class InterfaceField implements java.io.Serializable {

	// Fields

	/**
	 * 
	 */
	private static final long serialVersionUID = -7011551392244727547L;
	private Integer fieldId;
	private Integer interfaceDefId;
	private Integer sequence;
	private String fieldName;
	private String chineseName;
	private String fieldTypeExpr;
	private String fieldType;
	private Integer fieldLen;
	private Integer decimalDigits;
	private String optional;
	private String defaultValue;
	private String memo;

	// Constructors

	public InterfaceField(Integer fieldId, Integer interfaceDefId,
			Integer sequence, String fieldName, String chineseName, String fieldTypeExpr,
			String fieldType, Integer fieldLen, Integer decimalDigits,
			String optional, String defaultValue, String memo) {
		super();
		this.fieldId = fieldId;
		this.interfaceDefId = interfaceDefId;
		this.sequence = sequence;
		this.fieldName = fieldName;
		this.chineseName = chineseName;
		this.fieldTypeExpr = fieldTypeExpr;
		this.fieldType = fieldType;
		this.fieldLen = fieldLen;
		this.decimalDigits = decimalDigits;
		this.optional = optional;
		this.defaultValue = defaultValue;
		this.memo = memo;
	}

	/** default constructor */
	public InterfaceField() {
	}

	/** minimal constructor */
	public InterfaceField(Integer fieldId) {
		this.fieldId = fieldId;
	}

	public Integer getFieldId() {
		return fieldId;
	}

	public Integer getSequence() {
		return sequence;
	}

	public String getFieldName() {
		return fieldName;
	}

	public String getFieldTypeExpr() {
		return fieldTypeExpr;
	}

	public String getFieldType() {
		return fieldType;
	}

	public Integer getFieldLen() {
		return fieldLen;
	}

	public Integer getDecimalDigits() {
		return decimalDigits;
	}

	public String getOptional() {
		return optional;
	}

	public String getDefaultValue() {
		return defaultValue;
	}

	public String getMemo() {
		return memo;
	}

	public void setFieldId(Integer fieldId) {
		this.fieldId = fieldId;
	}

	public void setSequence(Integer sequence) {
		this.sequence = sequence;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public void setFieldTypeExpr(String fieldTypeExpr) {
		this.fieldTypeExpr = fieldTypeExpr;
	}

	public void setFieldType(String fieldType) {
		this.fieldType = fieldType;
	}

	public void setFieldLen(Integer fieldLen) {
		this.fieldLen = fieldLen;
	}

	public void setDecimalDigits(Integer decimalDigits) {
		this.decimalDigits = decimalDigits;
	}

	public void setOptional(String optional) {
		this.optional = optional;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

	public void setChineseName(String chineseName) {
		this.chineseName = chineseName;
	}

	public String getChineseName() {
		return chineseName;
	}

	public void setInterfaceDefId(Integer interfaceDefId) {
		this.interfaceDefId = interfaceDefId;
	}

	public Integer getInterfaceDefId() {
		return interfaceDefId;
	}

	

}