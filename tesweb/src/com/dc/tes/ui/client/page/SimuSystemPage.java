package com.dc.tes.ui.client.page;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.dc.tes.ui.client.AppContext;
import com.dc.tes.ui.client.ISimuSystemService;
import com.dc.tes.ui.client.ISimuSystemServiceAsync;
import com.dc.tes.ui.client.common.ServiceHelper;
import com.dc.tes.ui.client.control.ConfigToolBar;
import com.dc.tes.ui.client.control.FormContentPanel;
import com.dc.tes.ui.client.control.GridContentPanel;
import com.dc.tes.ui.client.model.GWTSimuSystem;

import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.extjs.gxt.ui.client.data.RpcProxy;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.toolbar.FillToolItem;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class SimuSystemPage extends BasePage {
	ISimuSystemServiceAsync systemService = null;
	GWTSimuSystem EditSystem = null;
	
	GridContentPanel<GWTSimuSystem> panel;
	FormContentPanel<GWTSimuSystem> detailPanel;
	ConfigToolBar configBar;
	String selectedCurrentMsg = "";
	
	public SimuSystemPage()
	{
	}
	
	@Override
	protected void onRender(Element parent, int index) {
		super.onRender(parent, index);

		systemService = ServiceHelper.GetDynamicService("simuSys", ISimuSystemService.class);
		panel = new GridContentPanel<GWTSimuSystem>();
		RpcProxy<PagingLoadResult<GWTSimuSystem>> proxy = new RpcProxy<PagingLoadResult<GWTSimuSystem>>() {
			@Override
			public void load(Object loadConfig,
					AsyncCallback<PagingLoadResult<GWTSimuSystem>> callback) {
				systemService.GetList(GetUserID(), panel.GetSearchCondition(), (PagingLoadConfig) loadConfig, callback);
			}
		};

		panel.setProxy(proxy);
		panel.setColumns(GetColumnConfig());
		panel.DrowSearchBar();
		panel.DrowGridView(GWTSimuSystem.N_Desc);
		
		configBar = new ConfigToolBar();
		configBar.initPageToolBar(panel.getLoader());
		configBar.AddWidget(new FillToolItem());
		if(IsAdmin())
			configBar.AddNewBtn("btnAdd",AddHandler());
		configBar.AddEditBtn("btnEdit",EditHandler());
		if(IsAdmin())
			configBar.AddDelBtn("btnDel",DelHandler());
		InitBtnConfigBar(configBar);
		panel.setBottomBar(configBar);
		add(panel);

		detailPanel = new FormContentPanel<GWTSimuSystem>();
		detailPanel.setBindInfo(GetDetailHashMap());
		panel.setDetailForm(detailPanel);
		add(detailPanel);
	}

	/**
	 * 获得Grid的列配置列表
	 * 
	 * @return Grid的列配置列表
	 */
	private List<ColumnConfig> GetColumnConfig() {
		List<ColumnConfig> columns = new ArrayList<ColumnConfig>();
		
		columns.add(new ColumnConfig(GWTSimuSystem.N_SystemName, "系统名称", 250));
		columns.add(new ColumnConfig(GWTSimuSystem.N_SystemNo, "系统号", 120));
		columns.add(new ColumnConfig(GWTSimuSystem.N_Channel, "通道名称", 120));
//		columns.add(new ColumnConfig(GWTSimuSystem.N_IP, "核心IP", 100));
//		columns.add(new ColumnConfig(GWTSimuSystem.N_Port, "核心端口", 80));
		ColumnConfig conf = new ColumnConfig(GWTSimuSystem.N_Desc, "备注", 100);
		conf.setSortable(false);
		columns.add(conf);
		
		return columns;
	}

	/**
	 * 获得详细信息绑定的Hash列表
	 * 
	 * @return Map<String,String> 对应 Map<对应绑定的值名称,字段显示名称>
	 */
	public Map<String, String> GetDetailHashMap() {
		Map<String, String> detailMap = new LinkedHashMap<String, String>();
		detailMap.put(GWTSimuSystem.N_SystemName, "系统名称");
		detailMap.put(GWTSimuSystem.N_Channel, "通道名称");
		detailMap.put(GWTSimuSystem.N_SystemNo, "系统号");
		detailMap.put(GWTSimuSystem.N_IP, "核心IP");
		detailMap.put(GWTSimuSystem.N_Port, "核心端口");
		detailMap.put(GWTSimuSystem.N_Desc, "备注");
		return detailMap;
	}
	
	private void CreateEditForm() {
		new SimuSystemDialog(EditSystem, panel.getLoader(), GetLoginLogID());
	}
	
	protected static void ReloadParent()
	{
		AppContext.GetEntryPoint().ReloadSystem();
	}

	private SelectionListener<ButtonEvent> AddHandler() {
		return new SelectionListener<ButtonEvent>() {
			public void componentSelected(ButtonEvent ce) {
				EditSystem = new GWTSimuSystem();
				CreateEditForm();
			}
		};
	}

	private SelectionListener<ButtonEvent> EditHandler() {
		return new SelectionListener<ButtonEvent>() {
			public void componentSelected(ButtonEvent ce) {
				List<GWTSimuSystem> selectedItems = panel.getDataGrid()
						.getSelectionModel().getSelectedItems();
				if (selectedItems.size() != 1) {
					MessageBox.alert("友情提示", "请选择一个案例进行编辑", null);
					return;
				}
				EditSystem = selectedItems.get(0);
//				if(EditSystem.GetSystemID().compareTo(GetSystemID()) == 0)
//				{
//					MessageBox.alert("友情提示","当前正在使用模拟系统【" + EditSystem.GetSystemName()+
//							"】,无法进行编辑！", null);
//					return;
//				}
				CreateEditForm();
			}
		};
	}

	private SelectionListener<ButtonEvent> DelHandler() {
		return new SelectionListener<ButtonEvent>() {
			public void componentSelected(ButtonEvent ce) {
				final List<GWTSimuSystem> delList = panel.getSelection();
				String CurrentID = GetSystemID();
				selectedCurrentMsg = "";
				for(int i = 0; i < delList.size(); i++)
				{
					if(CurrentID.compareTo(delList.get(i).toString()) == 0)
					{
						MessageBox.alert("友情提示", "当前正在使用模拟系统【" + delList.get(i).GetSystemName()+
								"】,无法进行删除！", null);
						return;
					}
				}
				MessageBox.confirm("提示信息", "是否确认删除",
						new Listener<MessageBoxEvent>() {
							public void handleEvent(MessageBoxEvent be) 
							{
								if (be.getButtonClicked().getText().compareTo(Message.messageBox_yes()) == 0)
									systemService.Delete(delList, GetLoginLogID(),
									new AsyncCallback<Void>() {
										public void onFailure(Throwable caught) {
											caught.printStackTrace();
											MessageBox.alert("错误提示","删除失败", null);
										}
		
										public void onSuccess(Void obj) {
											panel.reloadGrid();
											ReloadParent();
										}});
							}});
				}
		};
				
	}
}
