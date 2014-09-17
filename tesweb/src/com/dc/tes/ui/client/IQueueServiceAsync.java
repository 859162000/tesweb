package com.dc.tes.ui.client;

import java.util.List;

import com.dc.tes.ui.client.model.GWTQueue;
import com.dc.tes.ui.client.model.GWTQueueTask;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface IQueueServiceAsync {
	/**
	 * 获得任务队列列表
	 * @param sysId			系统标识
	 * @param searchKey		模糊查询字符
	 * @return				任务队列信息列表
	 */
	void GetList(String sysId, String searchKey, PagingLoadConfig config, AsyncCallback<PagingLoadResult<GWTQueue>> callback);
	
	/**
	 * 获得任务队列列表
	 * @param sysId			系统标识
	 * @param searchKey		模糊查询字符
	 * @return				任务队列信息列表
	 */
	void GetList(String sysId, String searchKey, AsyncCallback<List<GWTQueue>> callback);
	
	/**
	 * 保存任务队列信息（新增、修改）
	 * @param busiFlowInfo		任务队列信息
	 * @return					是否保存成功
	 */
	void SaveQueue(GWTQueue queueInfo, Integer loginLogId, AsyncCallback<Boolean> callback);
	
	/**
	 * 删除任务队列信息
	 * @param busiFlowInfo		被删除的任务队列信息列表
	 */
	void DeleteQueue(List<GWTQueue> queueList, Integer loginLogId, AsyncCallback<?> callback);
	
	void GetQueueTask(String queueID, AsyncCallback<GWTQueueTask> callback);
	
	void SetQueueTask(String queueID,List<ModelData> taskList, Integer loginLogId, AsyncCallback<?> callback);
	
	void GetGWTQueueTaskList(String sysID,String queueID, AsyncCallback<GWTQueueTask> callback);
	
	/**
	 * 每次点执行按钮时，会往执行队列表日志表中插入数据
	 * @param execBN 执行批次 
	 */	
	void Insert2DataBase(String queueID, String userID, String execBN, AsyncCallback<String> callback);
	void Insert2DataBase(String queueID, Integer executeSetID, String userID, String execBN, Integer roundId, AsyncCallback<String> callback);
	
	void updateExecuteLogForSuspendRun(String execBN, AsyncCallback<?> callback);
	

}
