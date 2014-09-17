package com.dc.tes.ui.client;

import java.util.List;

import com.dc.tes.ui.client.model.GWTLoginLog;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.google.gwt.user.client.rpc.RemoteService;

public interface ILoginLogService extends RemoteService {
	
	/**
	 * 获得登录日志信息列表
	 * @param sysId			系统标识
	 * @param searchKey		模糊查询字符
	 * @return				执行结果信息列表
	 */
	PagingLoadResult<GWTLoginLog> GetList(String sysId, String searchKey, PagingLoadConfig config);

	void deleteLoginLog(List<GWTLoginLog> loginLogs);
	
}
