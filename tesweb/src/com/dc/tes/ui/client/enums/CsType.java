package com.dc.tes.ui.client.enums;

public enum CsType {

	Client(0, "发起端"),
	Server(1, "接收端");
	
	private Integer dbValue = 0;
	private String chDesc;
	
	CsType(Integer dbValue, String desc){
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
	
	public static CsType valueOfDbValue(int dbvalue){
		for(CsType type : CsType.values()){
			if(type.getDbValue().equals(dbvalue))
				return type;
		}
		
		return null;
	}
	
	public static CsType valueOfChDesc(String desc){
		for(CsType type : CsType.values()){
			if(type.getChDesc().equals(desc))
				return type;
		}
		
		return null;
	}
}
