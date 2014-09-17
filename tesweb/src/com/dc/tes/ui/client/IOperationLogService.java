package com.dc.tes.ui.client;


import java.util.List;

import com.dc.tes.ui.client.model.GWTOperationLog;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.google.gwt.user.client.rpc.RemoteService;

public interface IOperationLogService extends RemoteService {
	
	PagingLoadResult<GWTOperationLog> GetList(String systemID,String searchKey, String loginLogID, PagingLoadConfig config);

	String writeLoginLog(String userId, String systemId, Integer loginLogId);
	
	void deleteOperationLog(List<GWTOperationLog> logs);
}
