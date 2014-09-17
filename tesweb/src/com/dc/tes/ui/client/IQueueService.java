package com.dc.tes.ui.client;

import java.util.List;

import com.dc.tes.ui.client.model.GWTQueue;
import com.dc.tes.ui.client.model.GWTQueueTask;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.google.gwt.user.client.rpc.RemoteService;

public interface IQueueService extends RemoteService {
	/**
	 * 获得任务队列列表
	 * @param sysId			系统标识
	 * @param searchKey		模糊查询字符
	 * @return				任务队列信息列表
	 */
	PagingLoadResult<GWTQueue> GetList(String sysId, String searchKey, PagingLoadConfig config);
	
	/**
	 * 获得任务队列列表
	 * @param sysId			系统标识
	 * @param searchKey		模糊查询字符
	 * @return				任务队列信息列表
	 */
	List<GWTQueue> GetList(String sysId, String searchKey);
	
	/**
	 * 保存任务队列信息（新增、修改）
	 * @param busiFlowInfo		任务队列信息
	 * @return					是否保存成功
	 */
	boolean SaveQueue(GWTQueue queueInfo, Integer loginLogId);
	
	/**
	 * 删除任务队列信息
	 * @param busiFlowInfo		被删除的任务队列信息列表
	 */
	void DeleteQueue(List<GWTQueue> queueList, Integer loginLogId);
	
	GWTQueueTask GetQueueTask(String queueID);
	
	void SetQueueTask(String queueID,List<ModelData> taskList, Integer loginLogId);
	
	GWTQueueTask GetGWTQueueTaskList(String sysID,String queueID);
	
	/**
	 * 每次点执行按钮时，会往执行队列表日志表中插入数据
	 * @param execBN 执行批次 
	 */	
	String Insert2DataBase(String queueID, String userID, String execBN);
	String Insert2DataBase(String queueID, Integer executeSetID, String userID, String execBN, Integer roundId);
	
	void updateExecuteLogForSuspendRun(String execBN);
	

}
