package com.dc.tes.ui.client;

import java.util.List;

import com.dc.tes.ui.client.model.GWTUser;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface IUserServiceAsync {
	void GetUserList(String searchInfo, PagingLoadConfig config, AsyncCallback<PagingLoadResult<GWTUser>> callback);

	void GetUserByRole(int role, AsyncCallback<List<GWTUser>> callback);
	
	void GetUserInfo(String userID, AsyncCallback<GWTUser> callback);

	void GetUserInfo(String userName, String password, AsyncCallback<GWTUser> callback);

	void SaveUser(GWTUser userInfo, Integer loginLogId, AsyncCallback<Boolean> callback);
	
	void DeleteUser(List<GWTUser> userList, Integer loginLogId, AsyncCallback<?> callback);
	
	void UpdatePWD(String userID,String pwd,int roleType, Integer loginLogId, AsyncCallback<?> callback);
	
	void GetUserBySystem(String sys, AsyncCallback<List<GWTUser>> callback);
}
