package com.dc.tes.ui.client;

import java.util.List;

import com.dc.tes.ui.client.model.GWTPersistentData;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.google.gwt.user.client.rpc.RemoteService;

/**
 * 持久化参数管理接口
 * @author scckobe
 *
 */
public interface IPersistentDataService extends RemoteService{
	/**
	 * 获得持久化参数列表
	 * @param systemID    所关联 系统ID
	 * @param searchInfo  模糊参数值
	 * @param config      分页配置信息
	 * @return            当前页码下的参数列表
	 */
	PagingLoadResult<GWTPersistentData> GetGWTPersistentDataList(
			String systemID, String searchInfo, PagingLoadConfig config);

	/**
	 * 新增、更新参数信息
	 * @param dataInfo  持久化参数信息
	 * @return          是否更新成功  true:成功 false：失败
	 */ 
	Boolean SavePersistentData(GWTPersistentData dataInfo);
	
	/**
	 * 删除持久化参数信息
	 * @param dataList 被删除的持久化参数列表
	 */
	void DeletePersistentData(List<GWTPersistentData> dataList);
}
