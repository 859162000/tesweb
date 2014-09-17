package com.dc.tes.ui.client;

import java.util.List;

import com.dc.tes.ui.client.model.GWTCaseFlow;
import com.dc.tes.ui.client.model.GWTScriptFlow;
import com.dc.tes.ui.client.model.GWTSimuSystem;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.google.gwt.user.client.rpc.RemoteService;

public interface IScriptFlowService extends RemoteService {
	/**
	 * 获得业务流列表
	 * @param sysId			系统标识
	 * @param searchKey		模糊查询字符
	 * @return				业务流信息列表
	 */
	PagingLoadResult<GWTScriptFlow> GetList(String sysId, String searchKey, PagingLoadConfig config);
	
	/**
	 * 保存业务流信息（新增、修改）
	 * @param scriptFlowInfo		业务流信息
	 */
	boolean SaveScriptFlow(GWTScriptFlow scriptFlowInfo);
	
	/**
	 * 删除业务流信息
	 * @param busiFlowInfo		被删除的业务流信息列表
	 */
	void DeleteScriptFlow(List<GWTScriptFlow> scriptFlowInfoList);
	
	
	/**
	 * 获得业务流脚本信息
	 * @param id	业务流标识
	 * @return		业务流脚本
	 */
	String GetScript(String id);
	
	/**
	 * 保存业务流脚本信息
	 * @param id	业务流标识
	 * @param Script	业务流脚本
	 */
	void UpdateScript(String id,String Script);
	
	/**
	 * 执行业务流脚本
	 * @param sysInfo	系统信息
	 * @param logID		执行标识
	 * @param flowid	业务流标识
	 * @param Script	业务流脚本
	 */
	void ExecScript(GWTSimuSystem sysInfo,String logID,String flowid,String Script,String executeLogId);
	
	/**
	 * 执行业务流脚本
	 * @param sysInfo	系统信息
	 * @param logID		执行标识
	 * @param flowid	业务流标识
	 */
	boolean ExecScript(GWTSimuSystem sysInfo,String logID,String flowid,String executeLogId);
	
	/**
	 * 获得业务流执行监控日志
	 * @param logID		执行标识
	 * @param flowid	业务流标识
	 * @return			监控日志
	 */
	ModelData GetExecLog(String logID,String flowid);
	
	
	/**
	 * 每次点执行按钮时，往执行队列表日志表中插入数据
	 * 
	 */	
	String Insert2DataBase(GWTScriptFlow scriptFlow, String userName);
	
	/**
	 * 
	 * @param busiFlow
	 * @param userName
	 * @return 执行日志与业务流延时，用 ; 分开
	 */
	String GetExecIDAndDelayTime(GWTCaseFlow busiFlow, String userName);
	
	/**
	 * 执行业务流脚本
	 * @param sysInfo	系统信息
	 * @param logID		执行标识
	 * @param flowid	业务流标识
	 * @param byApi	           是否通过调用API执行
	 */ 
	boolean ExecScript(GWTSimuSystem sysInfo,String logID,String flowid,String executeLogId, String userId,  boolean byApi);
	
	/**
	 * 执行业务流
	 */
	boolean ExecCaseFlow(GWTSimuSystem sysInfo,String flowID,String logID);
	
	
	int GetDelayTime(String systemID, String caseFlowID);
}
