package com.dc.tes.ui.client.enums;

public enum IDUType {

	Insert(1, "新增"),
	Delete(2, "删除"),
	Update(3, "修改"),
	Import(4, "导入");
	
	private Integer dbValue = 0;
	private String chDesc;
	
	IDUType(Integer dbValue, String desc){
		this.setDbValue(dbValue);
		this.setChDesc(desc);
	}

	public void setChDesc(String chDesc) {
		this.chDesc = chDesc;
	}

	public String getChDesc() {
		return chDesc;
	}

	public void setDbValue(Integer dbValue) {
		this.dbValue = dbValue;
	}

	public Integer getDbValue() {
		return dbValue;
	}
	
	public static IDUType valueOfDbValue(int dbvalue){
		for(IDUType type : IDUType.values()){
			if(type.getDbValue().equals(dbvalue))
				return type;
		}
		
		return null;
	}
	
	public static IDUType valueOfChDesc(String desc){
		for(IDUType type : IDUType.values()){
			if(type.getChDesc().equals(desc))
				return type;
		}
		
		return null;
	}
}
