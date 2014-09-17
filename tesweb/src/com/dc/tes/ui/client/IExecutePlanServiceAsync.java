package com.dc.tes.ui.client;

import java.util.List;

import com.dc.tes.ui.client.model.GWTExecutePlan;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface IExecutePlanServiceAsync {
	
	void GetExecutePlanList(
			String systemID, String searchKey, PagingLoadConfig config, AsyncCallback<PagingLoadResult<GWTExecutePlan>> callback);

	void SaveOrUpdateExecutePlan(GWTExecutePlan gwtExecutePlan, AsyncCallback<Boolean> callback);
	
	void DeleteExecutePlan(List<GWTExecutePlan> gwtExecutePlans, AsyncCallback<Boolean> callback);
	
	void GetExecPlans(String systemID, AsyncCallback<List<GWTExecutePlan>> callback);
	
}
