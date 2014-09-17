package com.dc.tes.ui.client;

import java.util.List;

import com.dc.tes.ui.client.model.GWTCaseParamExpectedValue;
import com.dc.tes.ui.client.model.GWTHost;
import com.dc.tes.ui.client.model.GWTParameterDirectory;
import com.dc.tes.ui.client.model.GWTSimuSystem;
import com.dc.tes.ui.client.model.GWTSysDynamicPara;
import com.dc.tes.ui.client.model.GWTTransaction;
import com.extjs.gxt.ui.client.data.BaseTreeModel;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * 系统动态参数管理接口
 * @author lujs
 *
 */
public interface ISysDynamicParameterAsync {
	
	/**
	 * 获得动态参数列表
	 * @param systemID    所关联 系统ID
	 * @param searchInfo  模糊参数值
	 * @param config      分页配置信息
	 * @return            当前页码下的参数列表
	 */
	void GetGWTSysDynamicParaPageList(
			String systemID, String searchInfo, PagingLoadConfig config, AsyncCallback<PagingLoadResult<GWTSysDynamicPara>> callback);

	/**
	 * 新增、更新参数信息
	 * @param dataInfo  动态参数信息
	 * @return          已更新或保存的GWTSysDynamicPara
	 */ 
	void SaveSysDynamicPara(GWTSysDynamicPara dataInfo, Integer loginLogId, AsyncCallback<GWTSysDynamicPara> callback);
	
	/**
	 * 删除动态参数信息
	 * @param dataList 被删除的动态参数列表
	 */
	void DeleteSysDynamicParaItems(List<BaseTreeModel> dataList, Integer loginLogId, AsyncCallback<Boolean> callback);

	/**
	 * 获取主机列表
	 * @param systemID
	 * @return
	 */
	void GetHostList(String systemID, AsyncCallback<List<GWTHost>> callback);
	
	void GetGWTSysDynamicParaList(String systemID, AsyncCallback<List<GWTSysDynamicPara>> callback);
	
	void GetGWTTranParamList(String tranID, AsyncCallback<List<ModelData>> callback);
	
	void GetGWTCaseParamList(String caseID, String tranID, AsyncCallback<List<GWTCaseParamExpectedValue>> callback);
	
	void SaveTranDynamicPara(List<GWTSysDynamicPara> dataList,
			List<GWTTransaction> selection,String userID, Integer loginLogId, AsyncCallback<Boolean> callback);
	
	void SaveCaseParaExpectedValue(List<GWTCaseParamExpectedValue> dataList, Integer loginLogId, AsyncCallback<Boolean> callback);
	
	void getSysDynamicParamTree(BaseTreeModel parent, String systemId, AsyncCallback<List<BaseTreeModel>> callback);
	
	void getSysParamTree(GWTSimuSystem system,
			GWTParameterDirectory gwtParameterDirectory, AsyncCallback<List<ModelData>> callback);
	
	void saveOrUpdateParamDirectory(GWTParameterDirectory parameterDirectory, AsyncCallback<GWTParameterDirectory> callback);
	
	void GetCaseParamTree(String caseID, String tranID, AsyncCallback<List<ModelData>> callback);
	
	void GetParamDirTree(GWTParameterDirectory dir, String systemId, AsyncCallback<List<GWTParameterDirectory>> callback);
	
	void GetSearchResult(String systemID, String searchText, AsyncCallback<BaseTreeModel> callback);
	
}
