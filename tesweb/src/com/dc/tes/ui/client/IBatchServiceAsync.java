package com.dc.tes.ui.client;

import java.util.List;

import com.dc.tes.ui.client.model.GWTBatchNo;
import com.dc.tes.ui.client.model.GWTCard;
import com.dc.tes.ui.client.model.GWTCase;
import com.dc.tes.ui.client.model.GWTCaseFlow;
import com.dc.tes.ui.client.model.GWTSimuSystem;
import com.dc.tes.ui.client.model.GWTTransaction;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface IBatchServiceAsync {
	/**
	 * 获得批次信息列表
	 * @param sysId          系统标识
	 * @param searchKey      模糊查询字符
	 * @param config         分页配置信息
	 * @return               批次信息列表
	 */
	void GetList(String sysId, String searchKey, PagingLoadConfig config, AsyncCallback<PagingLoadResult<GWTBatchNo>> callback);

	/**
	 * 删除选定的批次
	 * @param selection  
	 */
	void DeleteBatch(List<GWTBatchNo> selection, AsyncCallback<?> callback);
	
	/**
	 * 获得该批次下的所有案例
	 * @param batchID
	 * @param searchCondition
	 * @param loadConfig
	 * @return
	 */
	void GetDetailList(String batchID, String searchCondition, boolean isDisplayAll,
			PagingLoadConfig loadConfig, AsyncCallback<PagingLoadResult<GWTCase>> callback);
	
	void DeleteBatchCascade(List<GWTBatchNo> selection, AsyncCallback<?> callback);
	
	/**
	 * 获得该批次下的所有卡信息
	 * @param importBatchNo
	 * @return
	 */
	void GetCardList(String importBatchNo, AsyncCallback<List<GWTCard>> callback);
	
	/**
	 * 获得系统下的所有交易类型
	 * @param sysId
	 * @return
	 */
	void GetTranInfoList(String sysId, AsyncCallback<List<GWTTransaction>> callback);
	
	/**
	 * 获得该批次下的所有卡信息
	 * @param batchID
	 * @param searchCondition
	 * @param loadConfig
	 * @return
	 */
	void GetCardList(String batchID, String searchCondition,
			PagingLoadConfig loadConfig, AsyncCallback<PagingLoadResult<GWTCard>> callback);
	
	/**
	 * 删除选中的卡信息
	 * @param selection
	 */
	void DeleteCard(List<GWTCard> selection, AsyncCallback<?> callback);
	
	/**
	 * 保存卡信息
	 * @param gwtCard
	 */
	void SaveCard(GWTSimuSystem sysInfo, GWTCard gwtCard, AsyncCallback<Boolean> callback);
	
	/**
	 * 获得该批次下的所有业务流
	 *  @param batchID
	 * @param searchCondition
	 * @param loadConfig
	 * @return
	 */
	void GetCaseFlowList(String batchID, String searchCondition,
			PagingLoadConfig loadConfig, AsyncCallback<PagingLoadResult<GWTCaseFlow>> callback);
	
	/**
	 * 删除选中的业务流
	 * @param selection
	 * @param isCasCade 是否级联删除业务流下的案例信息？
	 */
	void DeleteCaseFlow(List<GWTCaseFlow> selection, boolean isCasCade, AsyncCallback<?> callback);
	
	/**
	 * 获得业务流下的所有案例
	 * @param caseFlow
	 * @param searchKey
	 * @param config
	 * @return
	 */
	void GetFlowCases(GWTCaseFlow caseFlow,  PagingLoadConfig config, AsyncCallback<PagingLoadResult<GWTCase>> callback);
	
	
}
