package com.dc.tes.ui.client.page;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.dc.tes.ui.client.IOperationLogService;
import com.dc.tes.ui.client.IOperationLogServiceAsync;
import com.dc.tes.ui.client.common.ServiceHelper;
import com.dc.tes.ui.client.control.ConfigToolBar;
import com.dc.tes.ui.client.control.FormContentPanel;
import com.dc.tes.ui.client.control.GridContentPanel;
import com.dc.tes.ui.client.model.GWTOperationLog;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.extjs.gxt.ui.client.data.RpcProxy;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.toolbar.FillToolItem;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class OperationLogPage extends BasePage {
	
	IOperationLogServiceAsync operationLogService = ServiceHelper.GetDynamicService(
			"operationLog", IOperationLogService.class);
	GridContentPanel<GWTOperationLog> panel;
	ConfigToolBar configBar;
	FormContentPanel<GWTOperationLog> detailPanel;
	String loginLogID = "";
	
	public OperationLogPage(String loginLogID) {
		this.loginLogID = loginLogID;
	}
	
	@Override
	protected void onRender(Element parent, int index) {
		super.onRender(parent, index);
	
		panel = new GridContentPanel<GWTOperationLog>();
		
		RpcProxy<PagingLoadResult<GWTOperationLog>> proxy = new RpcProxy<PagingLoadResult<GWTOperationLog>>() {

			@Override
			protected void load(Object loadConfig,
					AsyncCallback<PagingLoadResult<GWTOperationLog>> callback) {
				// TODO Auto-generated method stub
				operationLogService.GetList(GetSystemID(), panel.GetSearchCondition(), 
						loginLogID,(PagingLoadConfig)loadConfig, callback);
			}

		};
		panel.setProxy(proxy);
		panel.setColumns(GetColumnConfig());
		panel.DrowSearchBar();
		panel.DrowGridView();
		
		configBar = new ConfigToolBar();
		configBar.initPageToolBar(panel.getLoader());
		configBar.AddWidget(new FillToolItem());
		InitBtnConfigBar(configBar);
		configBar.AddDelBtn("btnDel", DelHandler());
		panel.setBottomBar(configBar);
		add(panel);
		
		detailPanel = new FormContentPanel<GWTOperationLog>();
		detailPanel.setBindInfo(GetDetailHashMap());
		panel.setDetailForm(detailPanel);
		add(detailPanel);
	}

	private Listener<MessageBoxEvent> DelHandler() {
		// TODO Auto-generated method stub
		return new Listener<MessageBoxEvent>() {
			public void handleEvent(MessageBoxEvent be) {
				Button msgBtn = be.getButtonClicked();
				if (msgBtn.getText().equalsIgnoreCase("Yes")) {
					operationLogService.deleteOperationLog(panel.getSelection(), 
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

	private Map<String, String> GetDetailHashMap() {
		// TODO Auto-generated method stub
		Map<String, String> detailMap = new LinkedHashMap<String, String>();
		return detailMap;
	}

	private List<ColumnConfig> GetColumnConfig() {
		// TODO Auto-generated method stub
		List<ColumnConfig> columns = new ArrayList<ColumnConfig>();
		columns.add(new ColumnConfig(GWTOperationLog.N_UserName, "操作用户",100));
		columns.add(new ColumnConfig(GWTOperationLog.N_IduType_Chs, "操作类型",100));
		columns.add(new ColumnConfig(GWTOperationLog.N_OpType_Chs, "操作对象",100));
		columns.add(new ColumnConfig(GWTOperationLog.N_ObjName, "对象名称", 100));
		columns.add(new ColumnConfig(GWTOperationLog.N_OpField, "字段名称", 100));
		columns.add(new ColumnConfig(GWTOperationLog.N_OldValue, "旧值",100));
		columns.add(new ColumnConfig(GWTOperationLog.N_NewValue, "新值",100));
		return columns;
	}

}
