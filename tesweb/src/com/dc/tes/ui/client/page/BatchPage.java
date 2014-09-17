package com.dc.tes.ui.client.page;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.dc.tes.ui.client.AppContext;
import com.dc.tes.ui.client.IBatchService;
import com.dc.tes.ui.client.IBatchServiceAsync;
import com.dc.tes.ui.client.MainPage;
import com.dc.tes.ui.client.common.ServiceHelper;
import com.dc.tes.ui.client.control.ConfigToolBar;
import com.dc.tes.ui.client.control.FormContentPanel;
import com.dc.tes.ui.client.control.GridContentPanel;
import com.dc.tes.ui.client.enums.CsType;
import com.dc.tes.ui.client.model.GWTBatchNo;
import com.dc.tes.ui.client.model.GWTTransaction;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.extjs.gxt.ui.client.data.RpcProxy;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.GridEvent;
import com.extjs.gxt.ui.client.event.IconButtonEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.HtmlContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.button.IconButton;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.toolbar.FillToolItem;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.user.client.Element;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class BatchPage extends BasePage {
	private IBatchServiceAsync batchService = null;
	//private GWTBatchNo batchNo = null;
	//private Window window = null;
	private UploadWin upWindow = null;
	
	GridContentPanel<GWTBatchNo> panel;
	FormContentPanel<GWTBatchNo> detailPanel;
	ConfigToolBar bottomBar;
	
	@Override
	protected void onRender(Element parent, int index) {
		super.onRender(parent, index);
		
		batchService = ServiceHelper.GetDynamicService("batchNo", IBatchService.class);
		panel = new GridContentPanel<GWTBatchNo>();
		RpcProxy<PagingLoadResult<GWTBatchNo>> proxy = new RpcProxy<PagingLoadResult<GWTBatchNo>>() {
			@Override
			public void load(Object loadConfig,
					AsyncCallback<PagingLoadResult<GWTBatchNo>> callback) {
				batchService.GetList(GetSystemID(), panel.GetSearchCondition(),
						(PagingLoadConfig) loadConfig, callback);
			}
		};
		panel.setProxy(proxy);
		panel.setColumns(GetColumnConfig());
		panel.DrowSearchBar();
		panel.DrowGridView("", true, true);
		bottomBar = new ConfigToolBar();
		bottomBar.initPageToolBar(panel.getLoader());
		bottomBar.AddButton("btnCase", new Button("案例数据管理"), MainPage.ICONS
				.menuCase(), editCaseHandler());
		bottomBar.AddButton("btnCaseFlow", new Button("业务流管理"), MainPage.ICONS.Script(), caseFlowHandler());
		bottomBar.AddButton("btnCard", new Button("卡信息管理"), MainPage.ICONS.Card(), editCardHandler());
		bottomBar.AddButton("btnCaseUpload", new Button("批量上传案例"), MainPage.ICONS
				.WebUp(), caseUploadHandler());
		bottomBar.AddWidget(new FillToolItem());
		bottomBar.AddDelBtn("btnDel", DelHandler());
		InitBtnConfigBar(bottomBar);
		panel.setBottomBar(bottomBar);
		add(panel);
		
		detailPanel = new FormContentPanel<GWTBatchNo>();
		detailPanel.setBindInfo(GetDetailHashMap());
		panel.setDetailForm(detailPanel);
		add(detailPanel);
	}

	

	


	private SelectionListener<ButtonEvent> caseFlowHandler() {
		// TODO Auto-generated method stub
		return new SelectionListener<ButtonEvent>() {
			public void componentSelected(ButtonEvent ce) {
				GWTBatchNo batchNo = panel.getDataGrid()
				.getSelectionModel().getSelectedItems().get(0);
				Integer batchNoId = batchNo.GetID();
				String tabId = "batchNo" + "caseFlow" + batchNoId;
				String tabTitle ="[" + batchNo.GetImportBatchNO() + "]业务流列表";
				BasePage page = new CaseFlowPage(batchNo.GetImportBatchNO());
				AppContext.GetEntryPoint().AddTabItem(tabId, tabTitle, page);
				
			};
		};
	}






	private SelectionListener<ButtonEvent> editCardHandler() {
		return new SelectionListener<ButtonEvent>() {

			public void componentSelected(ButtonEvent ce) {
				GWTBatchNo batchNo = panel.getDataGrid()
				.getSelectionModel().getSelectedItems().get(0);
				Integer batchNoId = batchNo.GetID();
				String tabId = "batchNo" + batchNoId;
				String tabTitle ="[" + batchNo.GetImportBatchNO() + "]卡列表";
				BasePage page = new CardPage(batchNo.GetImportBatchNO());
				AppContext.GetEntryPoint().AddTabItem(tabId, tabTitle, page);
			
			};
		};
	}






	private List<ColumnConfig> GetColumnConfig() {
		// TODO Auto-generated method stub
		List<ColumnConfig> columns = new ArrayList<ColumnConfig>();
		
		columns.add(new ColumnConfig(GWTBatchNo.N_BatchNO, "批次号", 150));
		columns.add(new ColumnConfig(GWTBatchNo.N_UserID, "用户名", 120));
		columns.add(new ColumnConfig(GWTBatchNo.N_ImportTime, "导入时间", 180));
		columns.add(new ColumnConfig(GWTBatchNo.N_Desc, "描述", 250));
		return columns;
	}	
	
	private SelectionListener<ButtonEvent> caseUploadHandler(){
		return new SelectionListener<ButtonEvent>() {
			public void componentSelected(ButtonEvent ce) {
				upWindow = new UploadWin(panel.getLoader());
				upWindow.Show("批量上传案例数据(.xls)", 
						"正在批量上传案例数据,请稍后……",
						"YQMultiCaseUpload?sysId=" + GetSystemID() 
						+ "&isClientSimu=1" +"&userId="+GetUserID()+"&isAdmin="+IsAdmin());
			}
		};
	}
	
	private Listener<MessageBoxEvent> DelHandler() {
		// TODO Auto-generated method stub
		return new Listener<MessageBoxEvent>() {
			public void handleEvent(MessageBoxEvent be) {
				Button msgBtn = be.getButtonClicked();
				if (msgBtn.getText().equalsIgnoreCase("Yes")) {
					MessageBox.confirm("提示信息", "是否级联删除案例、业务流与任务队列？",
						new Listener<MessageBoxEvent>() {
							public void handleEvent(MessageBoxEvent be) {
								Button msgBtn = be.getButtonClicked();
								if (msgBtn.getText().equalsIgnoreCase("Yes")) {
									batchService.DeleteBatchCascade(panel.getSelection(),
									new AsyncCallback<Void>() {
										public void onFailure(Throwable caught) {
											caught.printStackTrace();
											MessageBox.alert("错误提示", "删除失败", null);
										}
		
										public void onSuccess(Void obj) {
											panel.reloadGrid();
										}
									});
								}else{
									batchService.DeleteBatch(panel.getSelection(),
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
					
							};
					});
				}
			}
		};
	}
	
	private Map<String, String> GetDetailHashMap() {
		// TODO Auto-generated method stub
		Map<String, String> detailMap = new LinkedHashMap<String, String>();
		detailMap.put(GWTBatchNo.N_BatchNO, "批次号:");
		detailMap.put(GWTBatchNo.N_ImportTime, "导入时间:");
		detailMap.put(GWTBatchNo.N_UserID, "导入用户:");
		detailMap.put(GWTBatchNo.N_Desc, "批次描述");
		return detailMap;
	}

	private SelectionListener<ButtonEvent> editCaseHandler() {
		return new SelectionListener<ButtonEvent>() {

			public void componentSelected(ButtonEvent ce) {
				GWTBatchNo batchNo = panel.getDataGrid()
				.getSelectionModel().getSelectedItems().get(0);
				Integer batchNoId = batchNo.GetID();
				String tabId = "batchNo" + "case" + batchNoId;
				String tabTitle ="[" + batchNo.GetImportBatchNO() + "]案例列表";
				BasePage page = new BatchDetailPage(batchNo.GetImportBatchNO());
				AppContext.GetEntryPoint().AddTabItem(tabId, tabTitle, page);
				
			};
		};
	}
	
	
	
}
