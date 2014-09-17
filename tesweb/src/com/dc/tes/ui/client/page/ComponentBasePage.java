package com.dc.tes.ui.client.page;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.dc.tes.ui.client.IComponent;
import com.dc.tes.ui.client.IComponentAsync;
import com.dc.tes.ui.client.common.ServiceHelper;
import com.dc.tes.ui.client.control.ConfigToolBar;
import com.dc.tes.ui.client.control.FormContentPanel;
import com.dc.tes.ui.client.control.GridContentPanel;
import com.dc.tes.ui.client.enums.ComponentEnum;
import com.dc.tes.ui.client.model.GWTAdapter;
import com.dc.tes.ui.client.model.GWTComponent;
import com.dc.tes.ui.client.model.GWTMsgType;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.extjs.gxt.ui.client.data.RpcProxy;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.toolbar.FillToolItem;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class ComponentBasePage extends BasePage {

	IComponentAsync service = null;
	static String serviceName = "component";
	
	GridContentPanel<GWTComponent> panel;
	FormContentPanel<GWTComponent> detailPanel;
	ConfigToolBar configBar;
	ComponentEnum compType;
	
	public ComponentBasePage(ComponentEnum type){
		this.compType = type;
	}
	
	@Override
	protected void onRender(Element parent, int index) {
		super.onRender(parent, index);
		
		service = ServiceHelper.GetDynamicService(serviceName, IComponent.class);
		
		panel = new GridContentPanel<GWTComponent>();
		RpcProxy<PagingLoadResult<GWTComponent>> proxy = new RpcProxy<PagingLoadResult<GWTComponent>>() {
			@Override
			public void load(Object loadConfig,
					AsyncCallback<PagingLoadResult<GWTComponent>> callback) {
				service.GetComponentListByType(panel.GetSearchCondition(), (PagingLoadConfig) loadConfig, compType, callback);
			}
		};
		
		panel.setProxy(proxy);
		panel.setColumns(GetColumnConfig());
		panel.DrowSearchBar();
		if(compType == ComponentEnum.MsgType)
			panel.DrowGridView(GWTMsgType.N_Class);
		else
			panel.DrowGridView(GWTMsgType.N_Desc);
		
		configBar = new ConfigToolBar();
		configBar.initPageToolBar(panel.getLoader());
		configBar.AddWidget(new FillToolItem());
		configBar.AddNewBtn("btnAdd", new SelectionListener<ButtonEvent>(){

			@Override
			public void componentSelected(ButtonEvent ce) {
				CreateEditForm(null);
			}
		});
		configBar.AddEditBtn("btnEdit", new SelectionListener<ButtonEvent>(){

			@Override
			public void componentSelected(ButtonEvent ce) {
				GWTComponent selectComponent = panel.getDataGrid().getSelectionModel().getSelectedItem();
				if(selectComponent == null)
					return;
				CreateEditForm(selectComponent);
			}
		});
		configBar.AddDelBtn("btnDel", DelHandler());
		InitBtnConfigBar(configBar);
		panel.setBottomBar(configBar);
		add(panel);
		
		detailPanel = new FormContentPanel<GWTComponent>();
		detailPanel.setWidth(400);
		detailPanel.setBindInfo(GetDetailHashMap());
		panel.setDetailForm(detailPanel);
		add(detailPanel);
	}
	
	/**
	 * 获得Grid的列配置列表
	 * 
	 * @return Grid的列配置列表
	 */
	protected List<ColumnConfig> GetColumnConfig() {
		
		List<ColumnConfig> columns = new ArrayList<ColumnConfig>();
		
		columns.add(new ColumnConfig(GWTAdapter.N_Protocol, "通讯协议", 100));
		columns.add(new ColumnConfig(GWTAdapter.N_CsType, "类型", 120));
		columns.add(new ColumnConfig(GWTAdapter.N_PlugIn, "插件类", 250));
		columns.add(new ColumnConfig(GWTAdapter.N_Desc, "描述", 120));
		
		return columns;
	}
	
	/**
	 * 获得详细信息绑定的Hash列表
	 * 
	 * @return Map<String,String> 对应 Map<对应绑定的值名称,字段显示名称>
	 */
	protected Map<String, String> GetDetailHashMap() {
		return new LinkedHashMap<String, String>();
	}
	
	protected void CreateEditForm(final GWTComponent edit) {
		final Window window = new Window();
		window.show();
	}
	
	private Listener<MessageBoxEvent> DelHandler() {
		return new Listener<MessageBoxEvent>() {
			public void handleEvent(MessageBoxEvent be) {
				Button msgBtn = be.getButtonClicked();
				if (msgBtn.getText().equalsIgnoreCase("Yes")) {
					List<GWTComponent> selectComponent = panel.getDataGrid().getSelectionModel().getSelectedItems();
					if(selectComponent.size() == 0)
						return;
					service.DeletComponent(selectComponent, new AsyncCallback<Void>(){
						
						@Override
						public void onFailure(Throwable caught) {
							MessageBox.alert("删除数据失败", "删除数据失败，请与管理员联系", null);
						}

						@Override
						public void onSuccess(Void result) {
							panel.getLoader().load();
						}
					});
				}
			}
		};
	}
} 
