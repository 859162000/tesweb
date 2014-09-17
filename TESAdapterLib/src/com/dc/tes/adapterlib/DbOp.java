package com.dc.tes.adapterlib;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.dc.tes.data.DALFactory;
import com.dc.tes.data.IDAL;
import com.dc.tes.data.model.RecordedCase;
import com.dc.tes.data.model.SysType;
import com.dc.tes.data.model.User;
import com.dc.tes.data.op.Op;

public class DbOp {

	private static IDAL<RecordedCase> rcDAL = DALFactory.GetBeanDAL(RecordedCase.class);
	
	
	public static SysType getSysTypeBySystemId(int iSystemId) {
		
		SysType sysType = DALFactory.GetBeanDAL(SysType.class).Get(Op.EQ("systemId", String.valueOf(iSystemId)));

		return sysType;
	}

	
	public static int getUserIdByUserName(String userName) {
		
		User user = DALFactory.GetBeanDAL(User.class).Get(Op.EQ("name", userName));
		if (user != null) {
			String UserId = user.getId();
			return Integer.parseInt(UserId);
		}

		return -1;
	}

	public static int getAdminUserId() {
		
		return getUserIdByUserName("Admin");
	}

	public static RecordedCase getLastInsertedRecordedCases(int iSystemId) {
		
		List<RecordedCase> recordedCaseList = DALFactory.GetBeanDAL(RecordedCase.class).ListAll(Op.EQ("systemId", iSystemId), Op.EQ("responseFlag", 0));
		if (recordedCaseList == null) {
			return null;
		}
		RecordedCase maxRC = null;
		for (int i=0; i<recordedCaseList.size(); i++) {
			RecordedCase rc = recordedCaseList.get(i);
			if (maxRC == null) {
				maxRC = rc;
			}
			else if (rc.getId() > maxRC.getId()) {
				maxRC = rc; 
			}
		}
		return maxRC;
	}

	
	public static int InsertRecordedCase(int iSystemId, int iUserId, String strReqMsg) {

		try {
			IDAL<RecordedCase> rcDAL = DALFactory.GetBeanDAL(RecordedCase.class);
			// 插入当日收发日志表 要得到发送是否成功信息比较困难
			RecordedCase recordedCase = new RecordedCase();
			recordedCase.setRequestMsg(strReqMsg);
			recordedCase.setSystemId(iSystemId);
			recordedCase.setRecordUserId(iUserId);
			recordedCase.setResponseFlag(0);
			recordedCase.setIsCased(0);
			recordedCase.setCreateTime(GetDateTimeStr());
			rcDAL.Add(recordedCase);
			return recordedCase.getId();
		}
		catch(Exception e) {
			System.out.println(e.getMessage());
		}
		return -1;
	}
	
	public static int InsertRecordedCase(int iSystemId, int iUserId, String strReqMsg, String strResponseMsg) {

		try {
			IDAL<RecordedCase> rcDAL = DALFactory.GetBeanDAL(RecordedCase.class);
			// 插入当日收发日志表 要得到发送是否成功信息比较困难
			RecordedCase recordedCase = new RecordedCase();
			recordedCase.setRequestMsg(strReqMsg);
			recordedCase.setResponseMsg(strResponseMsg);
			recordedCase.setSystemId(iSystemId);
			recordedCase.setRecordUserId(iUserId);
			recordedCase.setResponseFlag(1);
			recordedCase.setIsCased(0);
			recordedCase.setCreateTime(GetDateTimeStr());
			rcDAL.Add(recordedCase);
			return recordedCase.getId();
		}
		catch(Exception e) {
			System.out.println(e.getMessage());
		}
		return -1;
	}

	public static String GetDateTimeStr() {

		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date dt = new Date();
		return sdf.format(dt);
	}
	
	public static void UpdateRecordedCase(int iRecordedCase, String strReqMsg) {

		RecordedCase recordedCase = rcDAL.Get(Op.EQ("id", iRecordedCase));
		recordedCase.setResponseFlag(1);
		recordedCase.setResponseMsg(strReqMsg);
		rcDAL.Edit(recordedCase);
	}

	
	public static void UpdateRecordedCase(RecordedCase recordedCase, String strReqMsg) {

		recordedCase.setResponseFlag(1);
		recordedCase.setResponseMsg(strReqMsg);
		rcDAL.Edit(recordedCase);
	}

}
