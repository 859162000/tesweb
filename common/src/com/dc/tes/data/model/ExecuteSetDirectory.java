package com.dc.tes.data.model;

import java.io.Serializable;

import com.dc.tes.data.model.tag.BeanIdName;
@BeanIdName("id")
public class ExecuteSetDirectory implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 4461905519232302301L;
	private Integer id;
	private Integer systemId;
	private Integer parentDirId;
	private Integer sortIndex;
	private Integer objType;
	private Integer executeSetId; 
	private String name;
	private String path;
	private String desc;
	public Integer getId() {
		return id;
	}
	public Integer getSystemId() {
		return systemId;
	}
	public Integer getParentDirId() {
		return parentDirId;
	}
	public Integer getSortIndex() {
		return sortIndex;
	}
	
	public Integer getExecuteSetId() {
		return executeSetId;
	}
	public String getName() {
		return name;
	}
	public String getPath() {
		return path;
	}
	public String getDesc() {
		return desc;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public void setSystemId(Integer systemId) {
		this.systemId = systemId;
	}
	public void setParentDirId(Integer parentDirId) {
		this.parentDirId = parentDirId;
	}
	public void setSortIndex(Integer sortIndex) {
		this.sortIndex = sortIndex;
	}

	public void setExecuteSetId(Integer executeSetId) {
		this.executeSetId = executeSetId;
	}
	public void setName(String name) {
		this.name = name;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	public void setObjType(Integer objType) {
		this.objType = objType;
	}
	public Integer getObjType() {
		return objType;
	}
}
