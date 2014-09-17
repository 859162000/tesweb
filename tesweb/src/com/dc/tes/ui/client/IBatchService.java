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
import com.google.gwt.user.client.rpc.RemoteService;

public interface IBatchService extends RemoteService {
	/**
	 * 获得批次信息列表
	 * @param sysId          系统标识
	 * @param searchKey      模糊查询字符
	 * @param config         分页配置信息
	 * @return               批次信息列表
	 */
	PagingLoadResult<GWTBatchNo> GetList(String sysId, String searchKey, PagingLoadConfig config);

	/**
	 * 删除选定的批次
	 * @param selection  
	 */
	void DeleteBatch(List<GWTBatchNo> selection);
	
	/**
	 * 获得该批次下的所有案例
	 * @param batchID
	 * @param searchCondition
	 * @param loadConfig
	 * @return
	 */
	PagingLoadResult<GWTCase> GetDetailList(String batchID, String searchCondition, boolean isDisplayAll,
			PagingLoadConfig loadConfig);
	
	void DeleteBatchCascade(List<GWTBatchNo> selection);
	
	/**
	 * 获得该批次下的所有卡信息
	 * @param importBatchNo
	 * @return
	 */
	List<GWTCard> GetCardList(String importBatchNo);
	
	/**
	 * 获得系统下的所有交易类型
	 * @param sysId
	 * @return
	 */
	List<GWTTransaction> GetTranInfoList(String sysId);
	
	/**
	 * 获得该批次下的所有卡信息
	 * @param batchID
	 * @param searchCondition
	 * @param loadConfig
	 * @return
	 */
	PagingLoadResult<GWTCard> GetCardList(String batchID, String searchCondition,
			PagingLoadConfig loadConfig);
	
	/**
	 * 删除选中的卡信息
	 * @param selection
	 */
	void DeleteCard(List<GWTCard> selection);
	
	/**
	 * 保存卡信息
	 * @param gwtCard
	 */
	boolean SaveCard(GWTSimuSystem sysInfo, GWTCard gwtCard);
	
	/**
	 * 获得该批次下的所有业务流
	 *  @param batchID
	 * @param searchCondition
	 * @param loadConfig
	 * @return
	 */
	PagingLoadResult<GWTCaseFlow> GetCaseFlowList(String batchID, String searchCondition,
			PagingLoadConfig loadConfig);
	
	/**
	 * 删除选中的业务流
	 * @param selection
	 * @param isCasCade 是否级联删除业务流下的案例信息？
	 */
	void DeleteCaseFlow(List<GWTCaseFlow> selection, boolean isCasCade);
	
	/**
	 * 获得业务流下的所有案例
	 * @param caseFlow
	 * @param searchKey
	 * @param config
	 * @return
	 */
	PagingLoadResult<GWTCase> GetFlowCases(GWTCaseFlow caseFlow,  PagingLoadConfig config);
	
	
}
