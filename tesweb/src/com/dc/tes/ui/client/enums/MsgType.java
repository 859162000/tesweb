package com.dc.tes.ui.client.enums;

public enum MsgType {

	Pack(0, "组包"),
	UnPack(1, "拆包");
	
	private Integer dbValue = 0;
	private String chDesc;
	
	MsgType(Integer dbValue, String desc){
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
	
	public static MsgType valueOfDbValue(int dbvalue){
		for(MsgType type : MsgType.values()){
			if(type.getDbValue().equals(dbvalue))
				return type;
		}
		
		return null;
	}
	
	public static MsgType valueOfChDesc(String desc){
		for(MsgType type : MsgType.values()){
			if(type.getChDesc().equals(desc))
				return type;
		}
		
		return null;
	}
}
