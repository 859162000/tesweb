package com.dc.tes.ui.client;

import java.util.List;

import com.dc.tes.ui.client.model.GWTCaseRunStatistics;
import com.dc.tes.ui.client.model.GWTCaseRunUserStats;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface ICaseStatisticsServiceAsync {

	void getCaseRunStatisticsList(String systemId,
			String searchKey, PagingLoadConfig config, boolean isFactorChange, AsyncCallback<PagingLoadResult<GWTCaseRunStatistics>> callback);
	
	void deleteCaseRunStatistics(List<GWTCaseRunStatistics> statisticsList, AsyncCallback<?> callback);
	
	void getCaseRunUserStatList(GWTCaseRunStatistics statistics,
			String searchKey, PagingLoadConfig config, boolean isFactorChange, AsyncCallback<PagingLoadResult<GWTCaseRunUserStats>> callback);
}
