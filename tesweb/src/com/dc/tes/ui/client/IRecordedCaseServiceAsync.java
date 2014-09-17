package com.dc.tes.ui.client;

import java.util.List;

import com.dc.tes.ui.client.model.GWTRecordedCase;
import com.dc.tes.ui.client.model.GWTUser;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface IRecordedCaseServiceAsync {
	
	void GetGWTRecordedCasePageList(String systemID, String searchInfo, PagingLoadConfig config, AsyncCallback<PagingLoadResult<GWTRecordedCase>> callback);

	void SaveRecordedCase(GWTRecordedCase host, Integer loginLogId, AsyncCallback<Boolean> callback);
	
	void DeleteRecordedCase(List<GWTRecordedCase> host, Integer loginLogId, AsyncCallback<Boolean> callback);
	
	void GetUserList(String sysId, AsyncCallback<List<GWTUser>> callback);
}
