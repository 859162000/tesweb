package com.dc.tes.ui.client;

import java.util.List;

import com.dc.tes.ui.client.model.GWTExecuteSetExecutePlan;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.google.gwt.user.client.rpc.RemoteService;

public interface IExecuteSetExecutePlanService extends RemoteService {
	PagingLoadResult<GWTExecuteSetExecutePlan> GetExecuteSetExecutePlanList(
			String execPlanID, String systemID, String searchKey, PagingLoadConfig config);

	Boolean SaveOrUpdateExecuteSetExecutePlan(GWTExecuteSetExecutePlan gwtExecutePlan);
	
	Boolean DeleteExecuteSetExecutePlan(List<GWTExecuteSetExecutePlan> gwtExecutePlans);

	/**
	 * 获取执行集的执行计划
	 * @param execSetId 执行集ID
	 * @return
	 */
	GWTExecuteSetExecutePlan GetExecSetExecPlan(String execSetId);
}
