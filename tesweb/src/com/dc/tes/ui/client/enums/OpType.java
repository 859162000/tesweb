package com.dc.tes.ui.client.enums;

public enum OpType {

	SysType(1, "系统"),
	User(2, "用户"),
	UserRSystem(3, "用户授权"),
	DbHost(5, "主机"),
	InterfaceDef(6, "接口"),
	SystemDynamicParameter(7, "系统参数"),
	TestRound(8, "轮次"),
	ExecutePlan(9, "执行计划"),
	Transaction(11, "交易"),
	RequestStruct(12, "请求报文结构"),
	ResponseStruct(13, "响应报文结构"),
	TransactionParameter(14, "交易参数"),
	CaseFlow(21, "测试用例"),
	Case(22, "用例步骤"),
	RequestMessage(23, "案例请求报文"),
	ExpectedMessage(24, "案例预期应答报文"),
	CaseParameterExpectValue(25, "案例参数预期值"),
	ExecuteSet(26, "执行集"),
	ExecuteSetItem(27, "执行集元素"),
	ScriptFlow(28, "可执行脚本"),
	RecordedCase(29, "录制的交易"),
	Adapter(31, "适配器"),
	MsgPacker(32, "组包组件"),
	MsgUnpacker(33, "拆包组件"),
	TransRecognizer(34, "交易识别码"),
	Channel(35, "通道");
	
	
	
	
	private Integer dbValue = 0;
	private String chDesc;
	
	OpType(Integer dbValue, String desc){
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
	
	public static OpType valueOfDbValue(int dbvalue){
		for(OpType type : OpType.values()){
			if(type.getDbValue().equals(dbvalue))
				return type;
		}
		
		return null;
	}
	
	public static OpType valueOfChDesc(String desc){
		for(OpType type : OpType.values()){
			if(type.getChDesc().equals(desc))
				return type;
		}
		
		return null;
	}
}
