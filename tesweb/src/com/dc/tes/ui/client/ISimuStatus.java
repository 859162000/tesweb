package com.dc.tes.ui.client;

import java.util.List;

import com.dc.tes.ui.client.model.GWTRealTimeLogInfo;
import com.dc.tes.ui.client.model.GWTStatusSys;
import com.google.gwt.user.client.rpc.RemoteService;

public interface ISimuStatus extends RemoteService {

	List<GWTRealTimeLogInfo> GetRecentCaseList(String sysName);
	
	List<GWTRealTimeLogInfo> GetUsefulTranList(String sysName);
	
	GWTStatusSys GetSystemStatusInfo(String sysId);
	
	void ClearMoniData(String sysName);
}
