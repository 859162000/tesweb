package com.dc.tes.ui.client.model;

import java.io.Serializable;
import com.extjs.gxt.ui.client.data.BaseModelData;

public class GWTBatchNo extends BaseModelData implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -533275812680581060L;
	public static String N_ID = "id";
	public static String N_SystemID = "systemId";
	public static String N_UserID = "userId";
	public static String N_BatchNO = "batchNo";
	public static String N_Desc = "description";
	public static String N_ImportTime = "importTime";
	
	public GWTBatchNo(){
		
	}
	
	public GWTBatchNo(Integer sysId, String userId, String batchNo){
		this(0, sysId, userId, batchNo, "", "");
	}
	
	public GWTBatchNo(Integer id, Integer sysId, String userId, String batchNo, String desc, String importTime){
		set(N_ID, id);
		set(N_SystemID, sysId);
		//IDAL<User> userDAL = DALFactory.GetBeanDAL(User.class);
		//User user = userDAL.Get(Op.EQ("id", userId));
		set(N_UserID, userId);
		set(N_BatchNO, batchNo);
		set(N_Desc, desc);
		set(N_ImportTime, importTime);
	}
	
	public int GetID(){
		return Integer.parseInt(get(N_ID).toString());
	}
	
	public String GetSystemID(){
		return get(N_SystemID);
	}
	
	public String GetUserID(){
		return get(N_UserID);
	}

	public String GetImportBatchNO(){
		return get(N_BatchNO);
	}
	
	public String GetDescription(){
		return get(N_Desc);
	}
	
	public String GetImportTime(){
		return get(N_ImportTime);
	}
}
