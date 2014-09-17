package com.dc.tes.ui.client;

import java.util.List;

import com.dc.tes.ui.client.model.GWTUser;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.google.gwt.user.client.rpc.RemoteService;

public interface IUserService extends RemoteService {
	PagingLoadResult<GWTUser> GetUserList(String searchInfo, PagingLoadConfig config);

	List<GWTUser> GetUserByRole(int role);
	
	GWTUser GetUserInfo(String userID);

	GWTUser GetUserInfo(String userName, String password);

	Boolean SaveUser(GWTUser userInfo, Integer loginLogId);
	
	void DeleteUser(List<GWTUser> userList, Integer loginLogId);
	
	void UpdatePWD(String userID,String pwd,int roleType, Integer loginLogId);
	
	List<GWTUser> GetUserBySystem(String sys);
}
