package com.dc.tes.ui.client;

import java.util.List;

import com.dc.tes.ui.client.model.GWTMsgType;
import com.dc.tes.ui.client.model.GWTSimuSystem;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface ISimuSystemServiceAsync {
	void GetList(String userID, String searchKey, PagingLoadConfig config, AsyncCallback<PagingLoadResult<GWTSimuSystem>> callback);

	void GetAllList(AsyncCallback<List<GWTSimuSystem>> callback);

	void GetListByUserID(String userID, AsyncCallback<List<GWTSimuSystem>> callback);

	void GetInfo(String systemID, AsyncCallback<GWTSimuSystem> callback);

	void Save(GWTSimuSystem obj, Integer loginLogId, AsyncCallback<Boolean> callback);

	void Delete(List<GWTSimuSystem> delList, Integer loginLogId, AsyncCallback<?> callback);
	
	void getUnPackerList(AsyncCallback<List<GWTMsgType>> callback);
}
