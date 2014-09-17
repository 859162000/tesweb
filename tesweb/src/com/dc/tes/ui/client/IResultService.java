package com.dc.tes.ui.client;


import java.util.List;

import com.dc.tes.ui.client.model.GWTPack_Struct;
import com.dc.tes.ui.client.model.GWTResultCompare;
import com.dc.tes.ui.client.model.GWTResultDetailLog;
import com.dc.tes.ui.client.model.GWTResultLog;
import com.dc.tes.ui.client.model.GWTResultLogMsg;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.google.gwt.user.client.rpc.RemoteService;

public interface IResultService extends RemoteService{
	/**
	 * 获得执行结果信息列表
	 * @param sysId			系统标识
	 * @param searchKey		模糊查询字符
	 * @return				执行结果信息列表
	 */
	PagingLoadResult<GWTResultLog> GetList(String sysId, String searchKey, PagingLoadConfig config);

	/**
	 * 查看执行日志的详情案例结果
	 * @param executeLogId  执行日志ID
	 * @param searchKey     查询条件
	 * @param config       分页设置
	 * @param passFlag     案例结果状态  其中各状态如下：
	 *    -1：    显示所有
 	 *	  10：    显示其它状态（2，3，4）
	 *     0：    失败
	 *     1：    通过
	 *     2：    正在执行中
	 *     3：    未执行
	 *     4：    中断
	 *     5：     超时
	 * @return 详情案例结果列表
	 */
	PagingLoadResult<GWTResultDetailLog> GetDetailList(String executeLogId, String searchKey, PagingLoadConfig config, Integer passFlag);
	
	PagingLoadResult<GWTResultDetailLog> GetResultListByCaseFlow(String caseFlowId, String searchKey, PagingLoadConfig config);
	
	PagingLoadResult<GWTResultDetailLog> GetDetailList2(String caseFlowInstanceId, String searchKey, PagingLoadConfig config);

	void DeleteResult(List<GWTResultLog> selection);
	
	void DeleteCaseFlowInstance(List<GWTResultDetailLog> selections);
	
	List<GWTResultCompare> GetCompareList(GWTResultDetailLog gwtResultDetailLog);
	
	/**
	 * 获得案例请求报文与响应报文 的报文内容
	 * @param caseId		案例标识
	 * @param gwtResultDetailLog	案例执行结果
	 * @return				请求报文与响应报文 的报文内容
	 */
	List<GWTPack_Struct> GetResultContent(GWTResultDetailLog gwtResultDetailLog);
	
	/**
	 * 获取一个执行日志的详细执行结果信息，包括执行案例数，通过数，失败数，通过率等等。
	 * @param log   执行日志 
	 * @return    详细执行结果信息
	 */
	GWTResultLogMsg GetResultLogMsg(GWTResultLog log);
	
	GWTResultLogMsg GetTodayResultLogMsg(String systemID);
	
	
	/**
	 *  根据执行日志ID与CASEFLOWID获取CaseFlowInstance
	 * @param executeLogId  执行日志ID
	 * @param caseFlowId    
	 * @return CaseFlowInstance
	 */
	GWTResultDetailLog GetCaseFlowInstance(String executeLogId, String caseFlowId);
	
	GWTResultLog GetResultLog(String executeLogId);
	
	/**
	 * 获取案例执行的结果比对报文内容。
	 * @param gwtResultDetailLog
	 * @return GWTPack_Struct 包含预期值与实际值的XML报文
	 */
	GWTPack_Struct GetCompareResult(GWTResultDetailLog gwtResultDetailLog);
	
	
}
