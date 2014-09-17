package com.dc.tes.ui.client.page;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.dc.tes.ui.client.IExecutePlanService;
import com.dc.tes.ui.client.IExecutePlanServiceAsync;
import com.dc.tes.ui.client.IExecuteSetExecutePlanService;
import com.dc.tes.ui.client.IExecuteSetExecutePlanServiceAsync;
import com.dc.tes.ui.client.common.ServiceHelper;
import com.dc.tes.ui.client.control.ConfigToolBar;
import com.dc.tes.ui.client.control.FormContentPanel;
import com.dc.tes.ui.client.control.GridContentPanel;
import com.dc.tes.ui.client.model.GWTExecutePlan;
import com.dc.tes.ui.client.model.GWTExecuteSetExecutePlan;
import com.dc.tes.ui.client.model.GWTStock;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.extjs.gxt.ui.client.data.RpcProxy;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.extjs.gxt.ui.client.widget.toolbar.FillToolItem;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class ExecuteSetExecPlanPage extends BasePage {

	private GridContentPanel<GWTExecuteSetExecutePlan> panel;
	private GWTExecuteSetExecutePlan EditExecSetExecPlan;
	private FormContentPanel<GWTExecuteSetExecutePlan> detailPanel;
	private ConfigToolBar configBar;
	private IExecuteSetExecutePlanServiceAsync execPlanService;
	private String execPlanID = "";
	public ExecuteSetExecPlanPage(String execPlanId){
		execPlanID = execPlanId;
	}
	
	@Override
	protected void onRender(Element parent, int index){
		super.onRender(parent, index);
		execPlanService = ServiceHelper.GetDynamicService("executeSetExecutePlan", IExecuteSetExecutePlanService.class);
		panel = new GridContentPanel<GWTExecuteSetExecutePlan>();
		RpcProxy<PagingLoadResult<GWTExecuteSetExecutePlan>> proxy = new RpcProxy<PagingLoadResult<GWTExecuteSetExecutePlan>>() {

			@Override
			protected void load(Object loadConfig,
					AsyncCallback<PagingLoadResult<GWTExecuteSetExecutePlan>> callback) {
				// TODO Auto-generated method stub
				execPlanService.GetExecuteSetExecutePlanList(execPlanID, GetSystemID(), 
						panel.GetSearchCondition(), (PagingLoadConfig)loadConfig, callback);
			}
		};
		panel.setProxy(proxy);
		panel.setColumns(GetColumnConfig());
		panel.DrowSearchBar();
		panel.DrowGridView();
		
		configBar = new ConfigToolBar();
		configBar.initPageToolBar(panel.getLoader());
		configBar.AddWidget(new FillToolItem());
		configBar.AddEditBtn("btnEdit", EditHandler());
		configBar.AddDelBtn("btnDel", DelHandler());
		InitBtnConfigBar(configBar);
		panel.setBottomBar(configBar);
		add(panel);
		
		detailPanel = new FormContentPanel<GWTExecuteSetExecutePlan>();
		detailPanel.setBindInfo(GetDetailHashMap());
		panel.setDetailForm(detailPanel);
		add(detailPanel);
		
		
	}

	

	private Map<String, String> GetDetailHashMap() {
		// TODO Auto-generated method stub
		Map<String, String> detailMap = new LinkedHashMap<String, String>();
		detailMap.put(GWTExecuteSetExecutePlan.N_ExecuteSetName, "执行集名称");
		detailMap.put(GWTExecuteSetExecutePlan.N_ExecutePlanName, "执行计划名称");
		detailMap.put(GWTExecuteSetExecutePlan.N_UserName, "创建人");
		detailMap.put(GWTExecuteSetExecutePlan.N_AddTime, "创建时间");
		detailMap.put(GWTExecuteSetExecutePlan.N_BeginRunTime, "开始执行时间");
		detailMap.put(GWTExecuteSetExecutePlan.N_EndRunTime, "结束执行时间");
		detailMap.put(GWTExecuteSetExecutePlan.N_ScheduledRunStatusStr, "执行状态");
		return detailMap;
	}

	private Listener<MessageBoxEvent> DelHandler() {
		// TODO Auto-generated method stub
		return new Listener<MessageBoxEvent>() {
			public void handleEvent(MessageBoxEvent be) {
				Button msgBtn = be.getButtonClicked();
				if (msgBtn.getText().equalsIgnoreCase("Yes")) {
					execPlanService.DeleteExecuteSetExecutePlan(panel.getSelection(),
							new AsyncCallback<Boolean>() {
								public void onFailure(Throwable caught) {
									caught.printStackTrace();
									MessageBox.alert("错误提示", "删除失败", null);
								}

								public void onSuccess(Boolean obj) {
									panel.reloadGrid();
								}
							});
				}
			}
		};
	}

	private SelectionListener<ButtonEvent> EditHandler() {
		// TODO Auto-generated method stub
		return new SelectionListener<ButtonEvent>() {
			public void componentSelected(ButtonEvent ce) {

				List<GWTExecuteSetExecutePlan> selectedItems = panel.getDataGrid()
						.getSelectionModel().getSelectedItems();
				EditExecSetExecPlan = selectedItems.get(0);
				CreateEditForm();
			}

			private void CreateEditForm() {
				// TODO Auto-generated method stub
				final Window window = new Window();
				window.setHeading("编辑执行集计划任务");
				window.setScrollMode(Scroll.AUTOY);
				window.setWidth(300);
				window.setModal(true);
				window.setPlain(true);
				window.setLayout(new FitLayout());
				window.setHeight(145);
				
				final FormPanel fp = new FormPanel();
				fp.setHeaderVisible(false);
				fp.setBodyBorder(false);
				fp.setBorders(false);
				fp.setPadding(5);
				fp.setHeaderVisible(false);
				fp.setScrollMode(Scroll.AUTOY);
				FormData formdata = new FormData("90%");
				
				final ComboBox<GWTExecutePlan> cbExecPlan = new ComboBox<GWTExecutePlan>();
				cbExecPlan.setFieldLabel("执行计划");
				cbExecPlan.setDisplayField(GWTExecutePlan.N_Name);
				cbExecPlan.setValueField(GWTExecutePlan.N_ID);
				cbExecPlan.setEditable(false);
				final ListStore<GWTExecutePlan> store = new ListStore<GWTExecutePlan>();
				IExecutePlanServiceAsync execPlanService = ServiceHelper.GetDynamicService("executePlan", IExecutePlanService.class);
				execPlanService.GetExecPlans(GetSystemID(), new AsyncCallback<List<GWTExecutePlan>>() {
					
					@Override
					public void onSuccess(List<GWTExecutePlan> result) {
						// TODO Auto-generated method stub
						store.add(result);
						for(GWTExecutePlan plan : result){
							if(plan.GetID().equals(EditExecSetExecPlan.GetExecutePlanID())){
								cbExecPlan.setValue(plan);
								break;
							}
						}
					}
					
					@Override
					public void onFailure(Throwable caught) {
						// TODO Auto-generated method stub
						MessageBox.alert("错误提示", "获取执行计划列表失败！", null);
						caught.printStackTrace();
					}
				});
				cbExecPlan.setStore(store);
				cbExecPlan.setTriggerAction(TriggerAction.ALL);
				fp.add(cbExecPlan, formdata);
				
				final ComboBox<GWTStock> cbStatus = new ComboBox<GWTStock>();
				cbStatus.setFieldLabel("执行状态");
				cbStatus.setDisplayField(GWTStock.N_Name);
				cbStatus.setValueField(GWTStock.N_Pos);
				cbStatus.setEditable(false);
				final ListStore<GWTStock> statusStore = new ListStore<GWTStock>();
				statusStore.add(new GWTStock("正在执行", "-1"));
				statusStore.add(new GWTStock("未执行", "0"));
				statusStore.add(new GWTStock("执行完成", "2"));
				cbStatus.setStore(statusStore);
				cbStatus.setTriggerAction(TriggerAction.ALL);
				cbStatus.setValue(new GWTStock(EditExecSetExecPlan.GetScheduledRunStatusStr(), EditExecSetExecPlan.GetScheduledRunStatus()));
				fp.add(cbStatus, formdata);
				window.add(fp);
				Button btnOK = new Button("确定");
				btnOK.addSelectionListener(new SelectionListener<ButtonEvent>() {

					@Override
					public void componentSelected(ButtonEvent ce) {
						// TODO Auto-generated method stub
						GWTExecuteSetExecutePlan gwt = new GWTExecuteSetExecutePlan(EditExecSetExecPlan.GetID(), GetSystemID(), GetUserID(),
								EditExecSetExecPlan.GetExecuteSetID(), cbExecPlan.getValue().GetID(), cbStatus.getValue().getPos());
						IExecuteSetExecutePlanServiceAsync serv = ServiceHelper.GetDynamicService("executeSetExecutePlan", IExecuteSetExecutePlanService.class);
						serv.SaveOrUpdateExecuteSetExecutePlan(gwt, new AsyncCallback<Boolean>() {

							@Override
							public void onFailure(Throwable caught) {
								// TODO Auto-generated method stub
								MessageBox.alert("错误提示", "保存失败！", null);
								caught.printStackTrace();
							}
							

							@Override
							public void onSuccess(Boolean result) {
								// TODO Auto-generated method stub
								if(result){
									window.hide();
									panel.reloadGrid();
								}else{
									MessageBox.alert("错误提示", "修改失败，已存在该执行集的该项计划任务", null);
								}						
							}
						});
					}			
				});
				window.addButton(btnOK);
				window.addButton(new Button("取消", new SelectionListener<ButtonEvent>() {

					@Override
					public void componentSelected(ButtonEvent ce) {
						// TODO Auto-generated method stub
						window.hide();
					}
				}));
				window.show();
			}
			
		};
	}

	private List<ColumnConfig> GetColumnConfig() {
		// TODO Auto-generated method stub
		List<ColumnConfig> columns = new ArrayList<ColumnConfig>();
		columns.add(new ColumnConfig(GWTExecuteSetExecutePlan.N_ExecuteSetName, "执行集名称", 120));
		columns.add(new ColumnConfig(GWTExecuteSetExecutePlan.N_ExecutePlanName, "执行计划名称", 120));
		columns.add(new ColumnConfig(GWTExecuteSetExecutePlan.N_UserName, "创建人", 100));
		columns.add(new ColumnConfig(GWTExecuteSetExecutePlan.N_AddTime, "创建时间", 120));
		columns.add(new ColumnConfig(GWTExecuteSetExecutePlan.N_BeginRunTime, "开始执行时间", 120));
		columns.add(new ColumnConfig(GWTExecuteSetExecutePlan.N_EndRunTime, "结束执行时间", 120));
		columns.add(new ColumnConfig(GWTExecuteSetExecutePlan.N_ScheduledRunStatusStr, "执行状态", 80));
		return columns;
	}

}
