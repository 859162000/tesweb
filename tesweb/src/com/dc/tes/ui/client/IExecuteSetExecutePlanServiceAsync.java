package com.dc.tes.ui.client;

import java.util.List;

import com.dc.tes.ui.client.model.GWTExecuteSetExecutePlan;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface IExecuteSetExecutePlanServiceAsync {
	void GetExecuteSetExecutePlanList(
			String execPlanID, String systemID, String searchKey, PagingLoadConfig config, AsyncCallback<PagingLoadResult<GWTExecuteSetExecutePlan>> callback);

	void SaveOrUpdateExecuteSetExecutePlan(GWTExecuteSetExecutePlan gwtExecutePlan, AsyncCallback<Boolean> callback);
	
	void DeleteExecuteSetExecutePlan(List<GWTExecuteSetExecutePlan> gwtExecutePlans, AsyncCallback<Boolean> callback);

	/**
	 * 获取执行集的执行计划
	 * @param execSetId 执行集ID
	 * @return
	 */
	void GetExecSetExecPlan(String execSetId, AsyncCallback<GWTExecuteSetExecutePlan> callback);
}
