package com.dc.tes.ui.client;

import com.dc.tes.ui.client.model.GWTCase;
import com.dc.tes.ui.client.model.GWTCaseDirectory;
import com.dc.tes.ui.client.model.GWTCaseFlow;
import com.dc.tes.ui.client.model.GWTSimuSystem;
import com.extjs.gxt.ui.client.data.BaseTreeModel;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import java.util.Date;
import java.util.List;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface IUseCaseServiceAsync {
	
	/**
	 * 获得用例树节点
	 * @param system
	 * @param gwtCaseDirectory 需要获取节点的目录 当null时返回根目录
	 * @return
	 */
	void getUseCaseTree(GWTSimuSystem system, GWTCaseDirectory gwtCaseDirectory, AsyncCallback<List<ModelData>> callback);
	
	/**
	 * 新增或修改一个目录
	 * @param caseDirectory
	 * @return 新增或修改后的目录，方便新增目录是获取目录ID值
	 */
	void saveOrUpdateDirectory(GWTCaseDirectory caseDirectory, AsyncCallback<GWTCaseDirectory> callback);
	
	/**
	 * 删除选中的节点，可以是目录也可以是用例，当为包含目录时目录必须为空
	 * @param item
	 * @return 是否成功删除
	 */
	void deleteSelectedItem(List<ModelData> item, Integer loginLogId, AsyncCallback<Boolean> callback);
	
	/**
	 * 新增或修改一个用例
	 * @param gwtCaseFlow
	 * @return 返回新增或修改后的用例
	 */
	void saveOrUpdateCaseFlow(GWTCaseFlow gwtCaseFlow, Integer loginLogId, AsyncCallback<GWTCaseFlow> callback);
	
	/**
	 * 获取结点列表下的所有子节点的GWTCaseFlow
	 * @param system
	 * @param Nodes
	 * @return   List<GwtCaseFlow>
	 */
	void getAllChildDatas(GWTSimuSystem system, List<ModelData> Nodes, AsyncCallback<List<ModelData>> callback);
	
	/**
	 * 获取系统下的所有GWTCaseFlow
	 * @param system
	 * @return   List<GwtCaseFlow>
	 */
	void getAllUseCases(GWTSimuSystem system, String[] conditions, Date[] dates, List<GWTCaseDirectory> Nodes, PagingLoadConfig loadConfig, AsyncCallback<PagingLoadResult<GWTCaseFlow>> callback);
	
	/**
	 * 用例目录树
	 * @param system
	 * @param gwtCaseDirectory
	 * @return
	 */
	void getCaseDirectoryTree(GWTSimuSystem system, GWTCaseDirectory gwtCaseDirectory, AsyncCallback<List<GWTCaseDirectory>> callback);
	
	/**
	 * 取得CaseFlow下的第一个Case
	 * @param caseFlow
	 * @return
	 */
	void getFirstCase(GWTCaseFlow caseFlow, AsyncCallback<GWTCase> callback);
	
	void getSearchResult(String systemId, String searchKey, AsyncCallback<BaseTreeModel> callback);
}
