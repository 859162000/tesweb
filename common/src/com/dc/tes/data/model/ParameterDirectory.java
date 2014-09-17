package com.dc.tes.data.model;

import java.io.Serializable;
import com.dc.tes.data.model.tag.BeanIdName;

@BeanIdName("id")
public class ParameterDirectory implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5905342754190061523L;
	private Integer id;
	private String systemId;
	private Integer parentDirId;
	private Integer sortIndex;
	private String name;
	private String path;
	private String description;
	
	public Integer getId() {
		return id;
	}
	public String getSystemId() {
		return systemId;
	}
	public Integer getParentDirId() {
		return parentDirId;
	}
	public Integer getSortIndex() {
		return sortIndex;
	}
	public String getName() {
		return name;
	}
	public String getPath() {
		return path;
	}
	public String getDescription() {
		return description;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public void setSystemId(String systemId) {
		this.systemId = systemId;
	}
	public void setParentDirId(Integer parentDirId) {
		this.parentDirId = parentDirId;
	}
	public void setSortIndex(Integer sortIndex) {
		this.sortIndex = sortIndex;
	}
	public void setName(String name) {
		this.name = name;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public void setDescription(String description) {
		this.description = description;
	}
}
