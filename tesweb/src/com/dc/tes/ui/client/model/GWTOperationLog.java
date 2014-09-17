package com.dc.tes.ui.client.model;

import java.io.Serializable;

import com.dc.tes.ui.client.enums.IDUType;
import com.dc.tes.ui.client.enums.OpType;
import com.extjs.gxt.ui.client.data.BaseModelData;

public class GWTOperationLog extends BaseModelData implements Serializable {

	private static final long serialVersionUID = -1688465514139024359L;
	
	public static String N_ID = "id";
	public static String N_SystemID = "systemId";
	public static String N_UserID = "userId";
	public static String N_UserName = "userName";
	public static String N_LoginLogID = "loginLogId";
	public static String N_ObjID = "objId";
	public static String N_ObjName = "objName";
	public static String N_IduType = "iduType";
	public static String N_OpType = "opType";
	public static String N_OpField = "opField";
	public static String N_OldValue = "oldValue";
	public static String N_NewValue = "newValue";
	public static String N_Memo = "memo";
	
	public static String N_IduType_Chs = "iduTypeChs";
	public static String N_OpType_Chs = "opTypeChs";
	
	public GWTOperationLog() {
		
	}
	
	public GWTOperationLog(String id, String systemId, String userId, String userName, String loginLogId, String objId,
			String objName, String iduType, String opType, String opField, String oldValue, String newValue,
			String memo) {
		this.set(N_ID, id);
		this.set(N_SystemID, systemId);
		this.set(N_UserID, userId);
		this.set(N_UserName,userName);
		this.set(N_LoginLogID, loginLogId);
		this.set(N_ObjID, objId);
		this.set(N_ObjName, objName);
		this.set(N_IduType, iduType);
		this.set(N_OpType, opType);
		this.set(N_OpField, opField);
		this.set(N_OldValue, oldValue);
		this.set(N_NewValue, newValue);
		this.set(N_Memo, memo);
		this.set(N_IduType_Chs, IDUType.valueOfDbValue(Integer.parseInt(iduType)).getChDesc());
		this.set(N_OpType_Chs, OpType.valueOfDbValue(Integer.parseInt(opType)).getChDesc());
	}
	

}
