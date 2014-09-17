package com.dc.tes.ui.client;


import java.util.List;

import com.dc.tes.ui.client.model.GWTPack_Struct;
import com.dc.tes.ui.client.model.GWTResultCompare;
import com.dc.tes.ui.client.model.GWTResultDetailLog;
import com.dc.tes.ui.client.model.GWTResultLog;
import com.dc.tes.ui.client.model.GWTResultLogMsg;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface IResultServiceAsync{
	/**
	 * 获得执行结果信息列表
	 * @param sysId			系统标识
	 * @param searchKey		模糊查询字符
	 * @return				执行结果信息列表
	 */
	void GetList(String sysId, String searchKey, PagingLoadConfig config, AsyncCallback<PagingLoadResult<GWTResultLog>> callback);

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
	void GetDetailList(String executeLogId, String searchKey, PagingLoadConfig config, Integer passFlag, AsyncCallback<PagingLoadResult<GWTResultDetailLog>> callback);
	
	void GetResultListByCaseFlow(String caseFlowId, String searchKey, PagingLoadConfig config, AsyncCallback<PagingLoadResult<GWTResultDetailLog>> callback);
	
	void GetDetailList2(String caseFlowInstanceId, String searchKey, PagingLoadConfig config, AsyncCallback<PagingLoadResult<GWTResultDetailLog>> callback);

	void DeleteResult(List<GWTResultLog> selection, AsyncCallback<?> callback);
	
	void DeleteCaseFlowInstance(List<GWTResultDetailLog> selections, AsyncCallback<?> callback);
	
	void GetCompareList(GWTResultDetailLog gwtResultDetailLog, AsyncCallback<List<GWTResultCompare>> callback);
	
	/**
	 * 获得案例请求报文与响应报文 的报文内容
	 * @param caseId		案例标识
	 * @param gwtResultDetailLog	案例执行结果
	 * @return				请求报文与响应报文 的报文内容
	 */
	void GetResultContent(GWTResultDetailLog gwtResultDetailLog, AsyncCallback<List<GWTPack_Struct>> callback);
	
	/**
	 * 获取一个执行日志的详细执行结果信息，包括执行案例数，通过数，失败数，通过率等等。
	 * @param log   执行日志 
	 * @return    详细执行结果信息
	 */
	void GetResultLogMsg(GWTResultLog log, AsyncCallback<GWTResultLogMsg> callback);
	
	void GetTodayResultLogMsg(String systemID, AsyncCallback<GWTResultLogMsg> callback);
	
	
	/**
	 *  根据执行日志ID与CASEFLOWID获取CaseFlowInstance
	 * @param executeLogId  执行日志ID
	 * @param caseFlowId    
	 * @return CaseFlowInstance
	 */
	void GetCaseFlowInstance(String executeLogId, String caseFlowId, AsyncCallback<GWTResultDetailLog> callback);
	
	void GetResultLog(String executeLogId, AsyncCallback<GWTResultLog> callback);
	
	/**
	 * 获取案例执行的结果比对报文内容。
	 * @param gwtResultDetailLog
	 * @return GWTPack_Struct 包含预期值与实际值的XML报文
	 */
	void GetCompareResult(GWTResultDetailLog gwtResultDetailLog, AsyncCallback<GWTPack_Struct> callback);
	
	
}
