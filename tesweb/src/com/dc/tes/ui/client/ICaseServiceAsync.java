package com.dc.tes.ui.client;

import java.util.List;

//import com.dc.tes.dom.MsgDocument;
import com.dc.tes.ui.client.model.GWTCase;
import com.dc.tes.ui.client.model.GWTCompareResult;
import com.dc.tes.ui.client.model.GWTPackNeed;
import com.dc.tes.ui.client.model.GWTPack_Struct;
import com.dc.tes.ui.client.model.GWTSimuSystem;
import com.dc.tes.ui.client.model.GWTTransaction;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.google.gwt.user.client.rpc.AsyncCallback;


public interface ICaseServiceAsync {
	/**
	 * 获得交易对应的案例数据列表
	 * @param tranID       交易标识
	 * @param searchKey    模糊查询字符
	 * @param config       列表分页配置
	 * @return             案例数据列表
	 */
	void GetCaseList(String tranID, String searchKey, PagingLoadConfig config, AsyncCallback<PagingLoadResult<GWTCase>> callback);

	/**
	 * 删除案例数据
	 * @param sysInfo	当前系统信息
	 * @param caseList  被删除的案例数据列表
	 */
	void DeleteCase(GWTSimuSystem sysInfo,List<GWTCase> caseList, Integer loginLogId, AsyncCallback<?> callback);
	
	/**
	 * 获得案例数据（组包之后的结果，或者直接上传的案例数据）
	 * @param caseID       案例标识
	 * @param charsetStr   字符集
	 * @return             案例数据、预期结果
	 */
	void GetRespData(String caseID,int isClientSimu, String charsetStr, AsyncCallback<String> callback);
	
	/**
	 * 获得案例数据(1.进行组包 2.根据组包的结果进行返回)
	 * @param needInfo		组包必备
	 * @param caseID		案例标识
	 * @param root			案例配置树
	 * @param charsetStr	字符集
	 * @return				案例数据、预期结果
	 */
	void GetRespData(GWTPackNeed needInfo,String caseID, int isClientSimu, GWTPack_Struct root, String charsetStr, AsyncCallback<String> callback);

	/**
	 * 获得案例数据、预期结果 的报文结构
	 * @param caseId		案例标识
	 * @param isCaseData	是否是案例数据 true:案例数据 false:预期结果
	 * @return				案例数据、预期结果 的报文结构
	 */
	void GetCaseContent(String caseId, boolean isCaseData, int isClientSimu, AsyncCallback<GWTPack_Struct> callback);
	
	/**
	 * 保存案例数据、预期结果
	 * @param needInfo		组包必备
	 * @param caseId		案例标识
	 * @param isCaseData	是否是案例数据 true:案例数据 false:预期结果
	 * @param root			案例数据、预期结果 的报文结构
	 * @return				如果案例数据，而且组包失败则返回提示信息，否则为空
	 */
	void SaveCaseContent(GWTPackNeed needInfo,String caseId, boolean isCaseData, int isClientSimu, GWTPack_Struct root, Integer loginLogId, AsyncCallback<String> callback);
	
	/**
	 * 选择当前交易的第一个案例，并执行案例				
	 * @param sysInfo		系统信息
	 * @param tranInfo		对应交易信息
	 * @return				案例预期比对结果
	 */
	void GetResultCompare(GWTSimuSystem sysInfo, GWTTransaction tranInfo, AsyncCallback<GWTCompareResult> callback);
	
	/**
	 * 案例执行
	 * @param sysInfo		系统信息
	 * @param tranInfo		对应交易信息
	 * @param caseID		案例标识
	 * @return				案例预期比对结果
	 */
	void GetResultCompare(GWTSimuSystem sysInfo,GWTTransaction tranInfo,String caseID, String executeLogId, AsyncCallback<GWTCompareResult> callback);
	
	/**
	 * 案例执行
	 * @param sysInfo		系统信息
	 * @param tranInfo		对应交易信息
	 * @param caseName		交易名称
	 * @param root			案例数据报文结构
	 * @return				案例预期比对结果
	 */
	void GetResultCompare(GWTSimuSystem sysInfo,GWTTransaction tranInfo,String caseName,GWTPack_Struct root, AsyncCallback<GWTCompareResult> callback);
	/**
	 * 执行时插入一条执行日志
	 * @param caseId		案例id
	 * @param userId		用户id
	 * @param sysId		        系统id
	 * @return				执行日志ID
	 */
	void Insert2DataBase(String caseId, String userId, String sysId, AsyncCallback<String> callback);
	
	/**
	 * 案例执行
	 * @param sysInfo		系统信息
	 * @param tranID		对应交易信息ID
	 * @param caseID		案例标识
	 * @return				案例预期比对结果
	 */
	void GetResultCompare(GWTSimuSystem sysInfo,String tranID,String caseID, String executeLogId, AsyncCallback<GWTCompareResult> callback);
	void GetResultCompare4BP(GWTSimuSystem sysInfo,String tranID,String caseID, String executeLogId, AsyncCallback<GWTCompareResult> callback);
	
	/**
	 * 改变案例的断点标记
	 * @param gwtCase
	 */
	void ChangeBreakPointFlag(GWTCase gwtCase, AsyncCallback<?> callback);
	
	void SaveOrUpdateCase(GWTCase gwtCase, Integer loginLogId, AsyncCallback<Boolean> callback);
	
	void GetXmlContent(String caseId, AsyncCallback<String> callback);
	
	void SaveXmlContent(String xmlContent, String caseID, int isClientSimu, GWTPackNeed needInfo, AsyncCallback<String> callback);
	
	/**
	 * 从CaseInstance获取响应报文来充当预期结果编辑的报文内容。
	 * @param caseId
	 * @return
	 */
	void GetExpectedXmlFromCaseInstance(String caseId, AsyncCallback<GWTPack_Struct> callback);

	/**
	 * 保存case次序
	 * @param cases
	 */
	void updateCaseSequence(List<GWTCase> cases, AsyncCallback<?> callback);
	
	/**
	 * 将当前案例设为默认案例
	 * @param tranID	案例所属交易标识
	 * @param caseID	案例标识
	 * @return          返回消息
	 */
	public void SetDefaultCase(String tranID,String caseID, AsyncCallback<String> callback);
	
	void ImportRecordedCaseData(GWTPackNeed needInfo, String msgStr, String caseID, int isClientSimu, GWTPack_Struct root, AsyncCallback<GWTPack_Struct> callback);
}
