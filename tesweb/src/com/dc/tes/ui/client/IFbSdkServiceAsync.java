package com.dc.tes.ui.client;

import com.dc.tes.ui.client.model.GWTSdkResult;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface IFbSdkServiceAsync {
	/**
	 * 登录企业银行
	 * @param userName
	 * @param passwd
	 * @param cardPasswd
	 * @param isCert 是否为证书卡登录 为true时cardPasswd才有效
	 * @return
	 */
	void Login(String userName, String passwd, String cardPasswd, boolean isCert, AsyncCallback<GWTSdkResult> callback);
	
	/**
	 * 退出登录
	 * @return
	 */
	void Logout(AsyncCallback<GWTSdkResult> callback);
	
	/**
	 * 查看帐户信息
	 * @return
	 */
	void GetAccInfo(String sQuery, AsyncCallback<GWTSdkResult> callback);
	
	/**
	 * 查询交易信息
	 * @param sQuery
	 * @return
	 */
	void GetTransInfo(String sQuery, AsyncCallback<GWTSdkResult> callback);
	
	/**
	 * 连接测试
	 * @return
	 */
	void SetAlive(AsyncCallback<GWTSdkResult> callback);
	
	/**
	 * 用户信息
	 * @return
	 */
	void GetUserInfo(AsyncCallback<GWTSdkResult> callback);
	
	/**
	 * 通知信息
	 */
	void GetNewNotice(AsyncCallback<GWTSdkResult> callback);
	
	/**
	 * 重登录
	 * @return
	 */
	void ReLogin(AsyncCallback<GWTSdkResult> callback);
}
