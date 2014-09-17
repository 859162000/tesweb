package com.dc.tes.ui.client;

import java.util.List;

import com.dc.tes.ui.client.model.GWTRecordedCase;
import com.dc.tes.ui.client.model.GWTUser;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.google.gwt.user.client.rpc.RemoteService;

public interface IRecordedCaseService extends RemoteService {
	
	PagingLoadResult<GWTRecordedCase> GetGWTRecordedCasePageList(String systemID, String searchInfo, PagingLoadConfig config);

	Boolean SaveRecordedCase(GWTRecordedCase host, Integer loginLogId);
	
	Boolean DeleteRecordedCase(List<GWTRecordedCase> host, Integer loginLogId);
	
	List<GWTUser> GetUserList(String sysId);
}
