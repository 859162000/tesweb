package com.dc.tes.ui.client.page;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.dc.tes.ui.client.AppContext;
import com.dc.tes.ui.client.ICaseStatisticsService;
import com.dc.tes.ui.client.ICaseStatisticsServiceAsync;
import com.dc.tes.ui.client.MainPage;
import com.dc.tes.ui.client.common.ServiceHelper;
import com.dc.tes.ui.client.control.ConfigToolBar;
import com.dc.tes.ui.client.control.FormContentPanel;
import com.dc.tes.ui.client.control.GridContentPanel;
import com.dc.tes.ui.client.control.PostFormPanel;
import com.dc.tes.ui.client.model.GWTCaseRunStatistics;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.extjs.gxt.ui.client.data.RpcProxy;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FormEvent;
import com.extjs.gxt.ui.client.event.GridEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.toolbar.FillToolItem;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class CaseRunStatisticsPage extends BasePage {
	
	private boolean isFactorChange;
	private ICaseStatisticsServiceAsync staticticsService = null;
	/**
	 * 列表控件
	 */
	GridContentPanel<GWTCaseRunStatistics> panel;
	
	/**
	 * 详细信息控件
	 */
	FormContentPanel<GWTCaseRunStatistics> detailPanel;
	/**
	 * 工具条
	 */
	ConfigToolBar bottomBar;
	
	public CaseRunStatisticsPage(boolean isFactorChange){
		this.isFactorChange = isFactorChange;
	}
	
	@Override
	protected void onRender(Element parent, int index) {
		super.onRender(parent, index);
		panel = new GridContentPanel<GWTCaseRunStatistics>();
		staticticsService = ServiceHelper.GetDynamicService("caseStatistics", ICaseStatisticsService.class);
		
		RpcProxy<PagingLoadResult<GWTCaseRunStatistics>> proxy = new RpcProxy<PagingLoadResult<GWTCaseRunStatistics>>() {
			@Override
			public void load(Object loadConfig,
					AsyncCallback<PagingLoadResult<GWTCaseRunStatistics>> callback) {
				staticticsService.getCaseRunStatisticsList(GetSystemID(), panel.GetSearchCondition(),
						(PagingLoadConfig) loadConfig, isFactorChange, callback);					
			}						
		};
		panel.setProxy(proxy);
		panel.setColumns(GetColumnConfig());
		panel.DrowGridView();
		panel.DrowSearchBar();
		bottomBar = new ConfigToolBar();
		bottomBar.initPageToolBar(panel.getLoader());	
		bottomBar.AddButton("btnStatisticsExport", new Button("导出统计信息"),
				MainPage.ICONS.WebDown(), CaseStatisticsExportHandler());
		bottomBar.AddWidget(new FillToolItem());
		bottomBar.AddDelBtn("btnDel", DelHandler());
		InitBtnConfigBar(bottomBar);
		panel.setBottomBar(bottomBar);
		
		panel.getDataGrid().addListener(Events.CellDoubleClick, doDoubleClickHandler());
		add(panel);

		detailPanel = new FormContentPanel<GWTCaseRunStatistics>();
		detailPanel.setBindInfo(GetDetailHashMap());
		panel.setDetailForm(detailPanel);
		add(detailPanel);
	
	}


	private SelectionListener<ButtonEvent> CaseStatisticsExportHandler() {
		// TODO Auto-generated method stub
		return new SelectionListener<ButtonEvent>() {
			public void componentSelected(ButtonEvent ce) {
				final PostFormPanel formPanel = new PostFormPanel();
				List<GWTCaseRunStatistics> list = panel.getDataGrid().getSelectionModel().getSelectedItems();
				String id = "";
				for(GWTCaseRunStatistics statistics : list){
					if(!id.isEmpty()){
						id += "a";
					}
					id += statistics.getCaseRunStatisticsID();
				}
				mask("正在获取统计信息,请稍后...");
				formPanel
						.setEncoding(FormPanel.Encoding.MULTIPART);
				formPanel.setMethod(FormPanel.Method.POST);
				formPanel.setAction("CaseStatisticsExport?"
						+ "id="
						+ id);
				formPanel.submit();
				formPanel.addListener(Events.Submit, new Listener<FormEvent>(){

					@Override
					public void handleEvent(FormEvent be) {
						// TODO Auto-generated method stub
						unmask();
						TESWindows.ShowDownLoad(be.getResultHtml());
					}
					
				});
			}
		};
	}

	private Listener<MessageBoxEvent> DelHandler() {
		return new Listener<MessageBoxEvent>() {
			public void handleEvent(MessageBoxEvent be) {
				Button msgBtn = be.getButtonClicked();
				if (msgBtn.getText().equalsIgnoreCase("Yes")) {
					staticticsService.deleteCaseRunStatistics(panel.getSelection(), 
							new AsyncCallback<Void>() {
								public void onFailure(Throwable caught) {
									caught.printStackTrace();
									MessageBox.alert("错误提示", "删除失败", null);
								}

								public void onSuccess(Void obj) {
									panel.reloadGrid();
								}
							});
				}
			}
		};
	}

	private Listener<GridEvent<GWTCaseRunStatistics>> doDoubleClickHandler() {
		// TODO Auto-generated method stub
		return new Listener<GridEvent<GWTCaseRunStatistics>>() {

			@Override
			public void handleEvent(GridEvent<GWTCaseRunStatistics> be) {
				// TODO Auto-generated method stub
				GWTCaseRunStatistics model = be.getModel();
				String tabId = "caseRunStatistics" + model.getCaseRunStatisticsID() + isFactorChange;
				String tabTitle = "[" + model.get(GWTCaseRunStatistics.N_StatMonth) + "]" + (isFactorChange? "变更":"执行") + "统计详情";
				BasePage page = new CaseRunUserStatsPage(model, isFactorChange);
				AppContext.GetEntryPoint().AddTabItem(tabId, tabTitle, page);
			}
		};
	}

	private Map<String, String> GetDetailHashMap() {
		// TODO Auto-generated method stub
		Map<String, String> detailMap = new LinkedHashMap<String, String>();

		detailMap.put(GWTCaseRunStatistics.N_StatMonth, "统计月份");
		detailMap.put(GWTCaseRunStatistics.N_StatStartDay, "开始日期");
		detailMap.put(GWTCaseRunStatistics.N_StatEndDay, "截止日期");
		if(!isFactorChange){
			detailMap.put(GWTCaseRunStatistics.N_TotalRunCaseFlowCount, "执行用例总数");
			detailMap.put(GWTCaseRunStatistics.N_TotalPassedCaseFlowCount, "通过用例总数");
			detailMap.put(GWTCaseRunStatistics.N_CaseFlowPassRate, "用例通过率");
			detailMap.put(GWTCaseRunStatistics.N_TotalRunCaseCount, "执行案例总数");
			detailMap.put(GWTCaseRunStatistics.N_TotalRunUserCount, "执行用户总数");
		}else{
			detailMap.put(GWTCaseRunStatistics.N_CreatedTransactionCount, "新建交易个数");
			detailMap.put(GWTCaseRunStatistics.N_CreatedCaseFlowCount, "新建用例个数");
			detailMap.put(GWTCaseRunStatistics.N_CreatedCaseCount, "新建案例个数");
			detailMap.put(GWTCaseRunStatistics.N_CreatedSysParamCount, "新建参数个数");
			detailMap.put(GWTCaseRunStatistics.N_ModifiedTransactionCount, "修改交易个数");
			detailMap.put(GWTCaseRunStatistics.N_ModifiedCaseFlowCount, "修改用例个数");
			detailMap.put(GWTCaseRunStatistics.N_ModifiedCaseCount, "修改案例个数");
			detailMap.put(GWTCaseRunStatistics.N_ModifiedSysParamCount, "修改参数个数");
		}
		//detailMap.put(GWTCaseRunStatistics.N_StatUserId, "统计者");
		detailMap.put(GWTCaseRunStatistics.N_StatTime, "统计时间");
		detailMap.put(GWTCaseRunStatistics.N_Memo, "备注");
		
		return detailMap;
	}


	private List<ColumnConfig> GetColumnConfig() {
		// TODO Auto-generated method stub
		List<ColumnConfig> columns = new ArrayList<ColumnConfig>();

		columns.add(new ColumnConfig(GWTCaseRunStatistics.N_StatMonth, "统计月份", 60));
		columns.add(new ColumnConfig(GWTCaseRunStatistics.N_StatStartDay, "开始日期", 70));
		columns.add(new ColumnConfig(GWTCaseRunStatistics.N_StatEndDay, "截止日期", 70));
		if(!isFactorChange){
			columns.add(new ColumnConfig(GWTCaseRunStatistics.N_TotalRunCaseFlowCount, "执行用例总数", 80));
			columns.add(new ColumnConfig(GWTCaseRunStatistics.N_TotalPassedCaseFlowCount, "通过用例总数", 80));
			columns.add(new ColumnConfig(GWTCaseRunStatistics.N_CaseFlowPassRate, "用例通过率", 80));
			columns.add(new ColumnConfig(GWTCaseRunStatistics.N_TotalRunCaseCount, "执行案例总数", 80));
			columns.add(new ColumnConfig(GWTCaseRunStatistics.N_TotalRunUserCount, "执行用户总数", 80));
		}else{
			columns.add(new ColumnConfig(GWTCaseRunStatistics.N_CreatedTransactionCount, "新建交易个数", 80));
			columns.add(new ColumnConfig(GWTCaseRunStatistics.N_CreatedCaseFlowCount, "新建用例个数", 80));
			columns.add(new ColumnConfig(GWTCaseRunStatistics.N_CreatedCaseCount, "新建案例个数", 80));
			columns.add(new ColumnConfig(GWTCaseRunStatistics.N_CreatedSysParamCount, "新建参数个数", 80));
			columns.add(new ColumnConfig(GWTCaseRunStatistics.N_ModifiedTransactionCount, "修改交易个数", 80));
			columns.add(new ColumnConfig(GWTCaseRunStatistics.N_ModifiedCaseFlowCount, "修改用例个数", 80));
			columns.add(new ColumnConfig(GWTCaseRunStatistics.N_ModifiedCaseCount, "修改案例个数", 80));
			columns.add(new ColumnConfig(GWTCaseRunStatistics.N_ModifiedSysParamCount, "修改参数个数", 80));
		}
	//	columns.add(new ColumnConfig(GWTCaseRunStatistics.N_StatUserId, "统计者", 50));
		columns.add(new ColumnConfig(GWTCaseRunStatistics.N_StatTime, "统计时间", 120));
		return columns;
	}
	
}
