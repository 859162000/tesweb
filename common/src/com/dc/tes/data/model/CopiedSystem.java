package com.dc.tes.data.model;

import java.io.Serializable;


import com.dc.tes.data.model.tag.BeanIdName;

@BeanIdName("Id")
public class CopiedSystem implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -8557418527967805258L;

	private Integer Id;
	private String systemName;
	private String systemNo;
	private Integer oldSystemId;
	private Integer newSystemId;

	//Get
	public Integer getId() {
		return Id;
	}
	public String getSystemName() {
		return systemName;
	}
	public String getSystemNo() {
		return systemNo;
	}
	public Integer getOldSystemId() {
		return oldSystemId;
	}
	public Integer getNewSystemId() {
		return newSystemId;
	}
	//Get
	
	//Set
	public void setId(Integer id) {
		this.Id = id;
	}
	public void setSystemName(String systemName) {
		this.systemName = systemName;
	}
	public void setSystemNo(String systemNo) {
		this.systemNo = systemNo;
	}
	
	public void setOldSystemId(Integer oldSystemId) {
		this.oldSystemId = oldSystemId;
	}
	public void setNewSystemId(Integer newSystemId) {
		this.newSystemId = newSystemId;
	}
	//Set
	
}
