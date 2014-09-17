package com.dc.tes.data.model;

import java.io.Serializable;

import com.dc.tes.data.model.tag.BeanIdName;
@BeanIdName("id")
public class CaseDirectory implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 313871104435746229L;
	private Integer id;
	private Integer systemId;
	private Integer parentDirId;
	private Integer sortIndex;
	private String name;
	private String path;
	private String description;
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getSystemId() {
		return systemId;
	}
	public void setSystemId(Integer systemId) {
		this.systemId = systemId;
	}
	public Integer getParentDirId() {
		return parentDirId;
	}
	public void setParentDirId(Integer parentDirId) {
		this.parentDirId = parentDirId;
	}
	public Integer getSortIndex() {
		return sortIndex;
	}
	public void setSortIndex(Integer sortIndex) {
		this.sortIndex = sortIndex;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
}
