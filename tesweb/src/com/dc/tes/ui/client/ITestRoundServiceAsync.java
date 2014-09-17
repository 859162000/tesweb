package com.dc.tes.ui.client;

import java.util.List;

import com.dc.tes.ui.client.model.GWTTestRound;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface ITestRoundServiceAsync {
	
	void GetTestRoundList(
			String systemID, String searchKey, PagingLoadConfig config, AsyncCallback<PagingLoadResult<GWTTestRound>> callback);

	void SaveOrUpdateTestRound(GWTTestRound gwtTestRound, AsyncCallback<Boolean> callback);
	
	void DeleteTestRound(List<GWTTestRound> gwtTestRounds, AsyncCallback<Boolean> callback);
	
	void GetTestRounds(String systemID, AsyncCallback<List<GWTTestRound>> callback);
}
