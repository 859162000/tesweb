package com.dc.tes.ui.client;

import java.util.List;

import com.dc.tes.ui.client.model.GWTTestRound;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.google.gwt.user.client.rpc.RemoteService;

public interface ITestRoundService extends RemoteService {
	
	PagingLoadResult<GWTTestRound> GetTestRoundList(
			String systemID, String searchKey, PagingLoadConfig config);

	Boolean SaveOrUpdateTestRound(GWTTestRound gwtTestRound);
	
	Boolean DeleteTestRound(List<GWTTestRound> gwtTestRounds);
	
	List<GWTTestRound> GetTestRounds(String systemID);
}
