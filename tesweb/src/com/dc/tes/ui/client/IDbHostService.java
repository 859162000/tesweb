package com.dc.tes.ui.client;

import java.util.List;

import com.dc.tes.ui.client.model.GWTHost;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.google.gwt.user.client.rpc.RemoteService;

public interface IDbHostService extends RemoteService {
	
	PagingLoadResult<GWTHost> GetGWTSysDynamicParaPageList(
			String systemID, String searchInfo, PagingLoadConfig config);

	Boolean SaveHost(GWTHost host, Integer loginLogId);
	
	Boolean DeleteHost(List<GWTHost> host, Integer loginLogId);
	
	
}
