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
import com.google.gwt.user.client.rpc.RemoteService;

/**
 * 系统动态参数管理接口
 * @author lujs
 *
 */
public interface ISysDynamicParameter extends RemoteService {
	
	/**
	 * 获得动态参数列表
	 * @param systemID    所关联 系统ID
	 * @param searchInfo  模糊参数值
	 * @param config      分页配置信息
	 * @return            当前页码下的参数列表
	 */
	PagingLoadResult<GWTSysDynamicPara> GetGWTSysDynamicParaPageList(
			String systemID, String searchInfo, PagingLoadConfig config);

	/**
	 * 新增、更新参数信息
	 * @param dataInfo  动态参数信息
	 * @return          已更新或保存的GWTSysDynamicPara
	 */ 
	GWTSysDynamicPara SaveSysDynamicPara(GWTSysDynamicPara dataInfo, Integer loginLogId);
	
	/**
	 * 删除动态参数信息
	 * @param dataList 被删除的动态参数列表
	 */
	Boolean DeleteSysDynamicParaItems(List<BaseTreeModel> dataList, Integer loginLogId);

	/**
	 * 获取主机列表
	 * @param systemID
	 * @return
	 */
	List<GWTHost> GetHostList(String systemID);
	
	List<GWTSysDynamicPara> GetGWTSysDynamicParaList(String systemID);
	
	List<ModelData> GetGWTTranParamList(String tranID);
	
	List<GWTCaseParamExpectedValue> GetGWTCaseParamList(String caseID, String tranID);
	
	Boolean SaveTranDynamicPara(List<GWTSysDynamicPara> dataList,
			List<GWTTransaction> selection,String userID, Integer loginLogId);
	
	Boolean SaveCaseParaExpectedValue(List<GWTCaseParamExpectedValue> dataList, Integer loginLogId);
	
	List<BaseTreeModel> getSysDynamicParamTree(BaseTreeModel parent, String systemId);
	
	List<ModelData> getSysParamTree(GWTSimuSystem system,
			GWTParameterDirectory gwtParameterDirectory);
	
	GWTParameterDirectory saveOrUpdateParamDirectory(GWTParameterDirectory parameterDirectory);
	
	List<ModelData> GetCaseParamTree(String caseID, String tranID);
	
	List<GWTParameterDirectory> GetParamDirTree(GWTParameterDirectory dir, String systemId);
	
	BaseTreeModel GetSearchResult(String systemID, String searchText);
	
}
