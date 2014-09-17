package com.dc.tes.ui.client;

import java.util.List;

import com.dc.tes.ui.client.model.GWTHost;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface IDbHostServiceAsync {
	
	void GetGWTSysDynamicParaPageList(
			String systemID, String searchInfo, PagingLoadConfig config, AsyncCallback<PagingLoadResult<GWTHost>> callback);

	void SaveHost(GWTHost host, Integer loginLogId, AsyncCallback<Boolean> callback);
	
	void DeleteHost(List<GWTHost> host, Integer loginLogId, AsyncCallback<Boolean> callback);
	
	
}
