package com.dc.tes.ui.client.page;

import java.util.ArrayList;
import java.util.List;

import com.dc.tes.ui.client.IInterfaceService;
import com.dc.tes.ui.client.IInterfaceServiceAsync;
import com.dc.tes.ui.client.common.ServiceHelper;
import com.dc.tes.ui.client.control.ConfigToolBar;
import com.dc.tes.ui.client.control.GridContentPanel;
import com.dc.tes.ui.client.model.GWTInterfaceDef;
import com.dc.tes.ui.client.model.GWTPack_Struct;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.extjs.gxt.ui.client.data.RpcProxy;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.layout.FillLayout;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.toolbar.PagingToolBar;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class InterfaceSelectWin extends BasePage {

	IInterfaceServiceAsync interfaceService = ServiceHelper.GetDynamicService("interface", IInterfaceService.class);
	private GridContentPanel<GWTInterfaceDef> panel = null;
	/**
	 * 工具条
	 */
	ConfigToolBar bottomBar;
	private List<GWTInterfaceDef> selection = null;
	
	

	public InterfaceSelectWin(){
		
	}
	
	public void drawWindow(final AsyncCallback<GWTPack_Struct> callback){
		
		final Window window = new Window();
		window.setHeading("接口选择");
		window.setSize(453, 370);
		window.setModal(false);
		window.setPlain(true);
		window.setLayout(new FillLayout());
		
		panel = new GridContentPanel<GWTInterfaceDef>();
		RpcProxy<PagingLoadResult<GWTInterfaceDef>> proxy = new RpcProxy<PagingLoadResult<GWTInterfaceDef>>() {

			@Override
			protected void load(Object loadConfig,
					AsyncCallback<PagingLoadResult<GWTInterfaceDef>> callback) {
				interfaceService.GetInterfaceDefList(GetSystemID(), panel.GetSearchCondition(),
						(PagingLoadConfig)loadConfig, callback);
			}
		};
		
		panel.setProxy(proxy);
		panel.setColumns(GetColumnConfig());
		panel.DrowGridView();
		panel.DrowSearchBar();
		PagingToolBar toolBar = new PagingToolBar(10);  
		toolBar.setPageSize(10);
	    toolBar.bind(panel.getLoader());

		panel.setBottomComponent(toolBar);

		window.add(panel);
		Button btn_select = new Button("选择");
		btn_select.addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				// TODO Auto-generated method stub
				selection = panel.getSelection();
				interfaceService.translateInterfaceToXML(selection, new AsyncCallback<GWTPack_Struct>() {

					@Override
					public void onFailure(Throwable caught) {
						// TODO Auto-generated method stub
						caught.printStackTrace();
					}

					@Override
					public void onSuccess(GWTPack_Struct result) {
						// TODO Auto-generated method stub
						callback.onSuccess(result);
						window.hide();
					}
				});
			}
		});
		window.addButton(btn_select);
			
		window.addButton(new Button("取消", new SelectionListener<ButtonEvent>() {
			public void componentSelected(ButtonEvent ce) {
				window.hide();
			}
		}));
		window.show();
	}

	private List<ColumnConfig> GetColumnConfig() {
		// TODO Auto-generated method stub
		List<ColumnConfig> columns = new ArrayList<ColumnConfig>();

		columns.add(new ColumnConfig(GWTInterfaceDef.N_InterfaceName, "接口名称", 120));
		columns.add(new ColumnConfig(GWTInterfaceDef.N_ChineseName, "中文名称", 150));	
		columns.add(new ColumnConfig(GWTInterfaceDef.N_InterfaceLen, "接口长度", 60));
		columns.add(new ColumnConfig(GWTInterfaceDef.N_FieldCount, "字段数量", 60));
		return columns;
	}
	
	public List<GWTInterfaceDef> getSelection() {
		return selection;
	}

	public void setSelection(List<GWTInterfaceDef> selection) {
		this.selection = selection;
	}
}
