package com.dc.tes.ui.client;

import java.util.List;

import com.dc.tes.ui.client.model.GWTExecutePlan;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.google.gwt.user.client.rpc.RemoteService;

public interface IExecutePlanService extends RemoteService {
	
	PagingLoadResult<GWTExecutePlan> GetExecutePlanList(
			String systemID, String searchKey, PagingLoadConfig config);

	Boolean SaveOrUpdateExecutePlan(GWTExecutePlan gwtExecutePlan);
	
	Boolean DeleteExecutePlan(List<GWTExecutePlan> gwtExecutePlans);
	
	List<GWTExecutePlan> GetExecPlans(String systemID);
	
}
