package com.dc.tes.ui.client;

import java.util.List;

import com.dc.tes.ui.client.model.GWTUser;
import com.dc.tes.ui.client.model.GWTUserSys;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.google.gwt.user.client.rpc.RemoteService;

public interface IUserSysService extends RemoteService {
	PagingLoadResult<GWTUserSys> GetUserList(String sysID,String searchInfo, PagingLoadConfig config);

	List<GWTUserSys> GetUserList(String sysID,String searchInfo);
	
	void RemoveRelation(List<GWTUserSys> delList);
	
	void AddRelation(String sysID,String userID);
	
	void SaveRelation(String systemID,List<GWTUser> saveList);
}
