package com.dc.tes.ui.client;

import java.util.List;

import com.dc.tes.ui.client.model.GWTUser;
import com.dc.tes.ui.client.model.GWTUserSys;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface IUserSysServiceAsync {
	void GetUserList(String sysID,String searchInfo, PagingLoadConfig config, AsyncCallback<PagingLoadResult<GWTUserSys>> callback);

	void GetUserList(String sysID,String searchInfo, AsyncCallback<List<GWTUserSys>> callback);
	
	void RemoveRelation(List<GWTUserSys> delList, AsyncCallback<?> callback);
	
	void AddRelation(String sysID,String userID, AsyncCallback<?> callback);
	
	void SaveRelation(String systemID,List<GWTUser> saveList, AsyncCallback<?> callback);
}
