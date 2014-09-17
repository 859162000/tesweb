package com.dc.tes.ui.client;

import java.util.List;

import com.dc.tes.ui.client.model.GWTRealTimeLogInfo;
import com.dc.tes.ui.client.model.GWTStatusSys;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface ISimuStatusAsync {

	void GetRecentCaseList(String sysName, AsyncCallback<List<GWTRealTimeLogInfo>> callback);
	
	void GetUsefulTranList(String sysName, AsyncCallback<List<GWTRealTimeLogInfo>> callback);
	
	void GetSystemStatusInfo(String sysId, AsyncCallback<GWTStatusSys> callback);
	
	void ClearMoniData(String sysName, AsyncCallback<?> callback);
}
