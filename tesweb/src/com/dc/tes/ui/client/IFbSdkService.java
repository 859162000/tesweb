package com.dc.tes.ui.client;

import com.dc.tes.ui.client.model.GWTSdkResult;
import com.google.gwt.user.client.rpc.RemoteService;

public interface IFbSdkService extends RemoteService {
	/**
	 * 登录企业银行
	 * @param userName
	 * @param passwd
	 * @param cardPasswd
	 * @param isCert 是否为证书卡登录 为true时cardPasswd才有效
	 * @return
	 */
	GWTSdkResult Login(String userName, String passwd, String cardPasswd, boolean isCert);
	
	/**
	 * 退出登录
	 * @return
	 */
	GWTSdkResult Logout();
	
	/**
	 * 查看帐户信息
	 * @return
	 */
	GWTSdkResult GetAccInfo(String sQuery);
	
	/**
	 * 查询交易信息
	 * @param sQuery
	 * @return
	 */
	GWTSdkResult GetTransInfo(String sQuery);
	
	/**
	 * 连接测试
	 * @return
	 */
	GWTSdkResult SetAlive();
	
	/**
	 * 用户信息
	 * @return
	 */
	GWTSdkResult GetUserInfo();
	
	/**
	 * 通知信息
	 */
	GWTSdkResult GetNewNotice();
	
	/**
	 * 重登录
	 * @return
	 */
	GWTSdkResult ReLogin();
}
