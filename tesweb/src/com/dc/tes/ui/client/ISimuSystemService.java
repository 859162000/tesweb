package com.dc.tes.ui.client;

import java.util.List;

import com.dc.tes.ui.client.model.GWTMsgType;
import com.dc.tes.ui.client.model.GWTSimuSystem;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.google.gwt.user.client.rpc.RemoteService;

public interface ISimuSystemService extends RemoteService {
	PagingLoadResult<GWTSimuSystem> GetList(String userID, String searchKey, PagingLoadConfig config);

	List<GWTSimuSystem> GetAllList();

	List<GWTSimuSystem> GetListByUserID(String userID);

	GWTSimuSystem GetInfo(String systemID);

	boolean Save(GWTSimuSystem obj, Integer loginLogId);

	void Delete(List<GWTSimuSystem> delList, Integer loginLogId);
	
	List<GWTMsgType> getUnPackerList();
}
