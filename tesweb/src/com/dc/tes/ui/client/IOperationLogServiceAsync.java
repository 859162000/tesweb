package com.dc.tes.ui.client;


import java.util.List;

import com.dc.tes.ui.client.model.GWTOperationLog;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface IOperationLogServiceAsync {
	
	void GetList(String systemID,String searchKey, String loginLogID, PagingLoadConfig config, AsyncCallback<PagingLoadResult<GWTOperationLog>> callback);

	void writeLoginLog(String userId, String systemId, Integer loginLogId, AsyncCallback<String> callback);
	
	void deleteOperationLog(List<GWTOperationLog> logs, AsyncCallback<?> callback);
}
