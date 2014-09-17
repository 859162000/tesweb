package com.dc.tes.ui.client.page;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.dc.tes.ui.client.AppContext;
import com.dc.tes.ui.client.IBatchService;
import com.dc.tes.ui.client.IBatchServiceAsync;
import com.dc.tes.ui.client.IScriptFlowService;
import com.dc.tes.ui.client.IScriptFlowServiceAsync;
import com.dc.tes.ui.client.MainPage;
import com.dc.tes.ui.client.common.ServiceHelper;
import com.dc.tes.ui.client.control.ConfigToolBar;
import com.dc.tes.ui.client.control.ExecuteCaseFlow;
import com.dc.tes.ui.client.control.FormContentPanel;
import com.dc.tes.ui.client.control.GridContentPanel;
import com.dc.tes.ui.client.model.GWTScriptFlow;
import com.dc.tes.ui.client.model.GWTScriptFlowLog;
import com.dc.tes.ui.client.model.GWTScriptFlowLogDetail;
import com.dc.tes.ui.client.model.GWTCase;
import com.dc.tes.ui.client.model.GWTCaseFlow;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.extjs.gxt.ui.client.data.RpcProxy;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.toolbar.FillToolItem;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class CaseFlowPage extends BasePage{
	private IBatchServiceAsync batchService = null;
	private String batchNo;	
	private IScriptFlowServiceAsync busiFlowService = ServiceHelper.GetDynamicService("busiFlow", IScriptFlowService.class);
	private String executeLogId = "";
	private GWTScriptFlowLog logInfo = new GWTScriptFlowLog();
	private Grid<GWTScriptFlowLogDetail> logContainer;
	private String js = "";
	private GWTScriptFlow gwtScriptFlow;
	/**
	 * 列表控件
	 */
	GridContentPanel<GWTCaseFlow> panel;
	/**
	 * 详细信息控件
	 */
	FormContentPanel<GWTCaseFlow> detailPanel;
	/**
	 * 工具条
	 */
	ConfigToolBar bottomBar;
	public CaseFlowPage(String batchNO) {
		// TODO Auto-generated constructor stub
		batchNo = batchNO;
	}
	
	@Override
	protected void onRender(Element parent, int index) {
		super.onRender(parent, index);
		panel = new GridContentPanel<GWTCaseFlow>();
		batchService = ServiceHelper.GetDynamicService("batchNo", IBatchService.class);
		
		RpcProxy<PagingLoadResult<GWTCaseFlow>> proxy = new RpcProxy<PagingLoadResult<GWTCaseFlow>>() {
			@Override
			public void load(Object loadConfig,
					AsyncCallback<PagingLoadResult<GWTCaseFlow>> callback) {
				batchService.GetCaseFlowList(batchNo, panel
						.GetSearchCondition(), (PagingLoadConfig) loadConfig,
						callback);
			}						
		};
		panel.setProxy(proxy);
		panel.setColumns(GetColumnConfig());
		panel.DrowGridView();
		panel.DrowSearchBar();
		bottomBar = new ConfigToolBar();
		bottomBar.initPageToolBar(panel.getLoader());
		bottomBar.AddButton("btnCase", new Button("业务流案例"), MainPage.ICONS.menuCase(), CaseHandler());
		bottomBar.AddButton("btnExec",new Button("执行业务流"), MainPage.ICONS
				.Exec(), ExecHandler());		
		bottomBar.AddWidget(new FillToolItem());
	//	bottomBar.AddNewBtn("btnAdd", AddHandler());
	//	bottomBar.AddEditBtn("btnEdit", EditHandler());
		bottomBar.AddDelBtn("btnDel", DelHandler());
		InitBtnConfigBar(bottomBar);
		panel.setBottomBar(bottomBar);
		add(panel);

		detailPanel = new FormContentPanel<GWTCaseFlow>();
		detailPanel.setBindInfo(GetDetailHashMap());
		panel.setDetailForm(detailPanel);
		add(detailPanel);
	}

	private SelectionListener<ButtonEvent> CaseHandler() {
		// TODO Auto-generated method stub
		return new SelectionListener<ButtonEvent>() {

			public void componentSelected(ButtonEvent ce) {
				GWTCaseFlow caseFlow = panel.getDataGrid()
				.getSelectionModel().getSelectedItems().get(0);
				String tabId = "caseFlow" + caseFlow.GetID();
				String tabTitle ="[" + caseFlow.GetName() + "]案例列表";
				BasePage page = new FlowCasesPage(batchNo, caseFlow);
				AppContext.GetEntryPoint().AddTabItem(tabId, tabTitle, page);
				
			};
		};
	}

	private Map<String, String> GetDetailHashMap() {
		// TODO Auto-generated method stub
		Map<String, String> detailMap = new LinkedHashMap<String, String>();
		detailMap.put(GWTCaseFlow.N_CaseFlowNo, "业务流编号");
		detailMap.put(GWTCaseFlow.N_Name, "业务流名称");
		detailMap.put(GWTCaseFlow.N_CreateTime, "创建时间");
		detailMap.put(GWTCaseFlow.N_UserName, "创建用户");
		return detailMap;
	}

	private Listener<MessageBoxEvent> DelHandler() {
		// TODO Auto-generated method stub
		return new Listener<MessageBoxEvent>() {
			public void handleEvent(MessageBoxEvent be) {
				Button msgBtn = be.getButtonClicked();
				if (msgBtn.getText().equalsIgnoreCase("Yes")) {
					MessageBox.confirm("提示信息", "是否直接删除业务流下的案例信息？",
						new Listener<MessageBoxEvent>() {
							public void handleEvent(MessageBoxEvent be) {
								Button msgBtn = be.getButtonClicked();
								batchService.DeleteCaseFlow(panel.getSelection(),
										msgBtn.getText().equalsIgnoreCase("Yes"),
									new AsyncCallback<Void>() {
										public void onFailure(Throwable caught) {
											caught.printStackTrace();
											MessageBox.alert("错误提示", "删除失败", null);
										}
										public void onSuccess(Void obj) {
											panel.reloadGrid();
										}
									});
							};
					});
				}
			}
		};
	}

	private SelectionListener<ButtonEvent> EditHandler() {
		// TODO Auto-generated method stub
		return null;
	}

	private SelectionListener<ButtonEvent> AddHandler() {
		// TODO Auto-generated method stub
		return null;
	}

	private SelectionListener<ButtonEvent> ExecHandler() {
		// TODO Auto-generated method stub
		return new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				List<GWTCaseFlow> selectedItems = panel.getDataGrid()
				.getSelectionModel().getSelectedItems();
				if (selectedItems.size() != 1) {
					MessageBox.alert("Alert", "请选择一个案例进行编辑", null);
					return;
				}
				GWTCaseFlow caseFlow = selectedItems.get(0);
				ExecuteCaseFlow executeCaseFlow = new ExecuteCaseFlow(caseFlow);
				executeCaseFlow.ExecCaseFlow();
			}
		};
	}
	


	private List<ColumnConfig> GetColumnConfig() {
		// TODO Auto-generated method stub
		List<ColumnConfig> columns = new ArrayList<ColumnConfig>();

		columns.add(new ColumnConfig(GWTCaseFlow.N_CaseFlowNo, "业务流编号", 150));
		columns.add(new ColumnConfig(GWTCaseFlow.N_Name, "业务流名称", 150));
		columns.add(new ColumnConfig(GWTCaseFlow.N_CreateTime, "创建时间", 150));
		columns.add(new ColumnConfig(GWTCaseFlow.N_UserName, "创建用户", 150));
		
		return columns;
	}
	
	
	
	
	
	
	
}
