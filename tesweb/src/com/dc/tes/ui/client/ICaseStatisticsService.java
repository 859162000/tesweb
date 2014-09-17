package com.dc.tes.ui.client;

import java.util.List;

import com.dc.tes.ui.client.model.GWTCaseRunStatistics;
import com.dc.tes.ui.client.model.GWTCaseRunUserStats;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.google.gwt.user.client.rpc.RemoteService;

public interface ICaseStatisticsService extends RemoteService {

	PagingLoadResult<GWTCaseRunStatistics> getCaseRunStatisticsList(String systemId,
			String searchKey, PagingLoadConfig config, boolean isFactorChange);
	
	void deleteCaseRunStatistics(List<GWTCaseRunStatistics> statisticsList);
	
	PagingLoadResult<GWTCaseRunUserStats> getCaseRunUserStatList(GWTCaseRunStatistics statistics,
			String searchKey, PagingLoadConfig config, boolean isFactorChange);
}
