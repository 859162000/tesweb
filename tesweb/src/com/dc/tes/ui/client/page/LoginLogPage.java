package com.dc.tes.ui.client.page;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.dc.tes.ui.client.AppContext;
import com.dc.tes.ui.client.ILoginLogService;
import com.dc.tes.ui.client.ILoginLogServiceAsync;
import com.dc.tes.ui.client.common.ServiceHelper;
import com.dc.tes.ui.client.control.ConfigToolBar;
import com.dc.tes.ui.client.control.FormContentPanel;
import com.dc.tes.ui.client.control.GridContentPanel;
import com.dc.tes.ui.client.model.GWTLoginLog;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.data.BasePagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.extjs.gxt.ui.client.data.RpcProxy;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.GridEvent;
import com.extjs.gxt.ui.client.event.IconButtonEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.HtmlContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.button.IconButton;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.toolbar.FillToolItem;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class LoginLogPage extends BasePage {
	
	ILoginLogServiceAsync loginLogService = ServiceHelper.GetDynamicService(
			"loginLog", ILoginLogService.class);
	
	GridContentPanel<GWTLoginLog> panel;
	ConfigToolBar configBar;
	PagingLoadConfig loadConfig;

	FormContentPanel<GWTLoginLog> detailPanel;
	
	public LoginLogPage() {
		
	}
	
	@Override
	protected void onRender(Element parent, int index) {
		super.onRender(parent, index);
		
		loadConfig = new BasePagingLoadConfig();
		loadConfig.setLimit(10);
		panel = new GridContentPanel<GWTLoginLog>();
		
		RpcProxy<PagingLoadResult<GWTLoginLog>> proxy = new RpcProxy<PagingLoadResult<GWTLoginLog>>() {
			@Override
			public void load(Object loadConfig,
					AsyncCallback<PagingLoadResult<GWTLoginLog>> callback) {
				loginLogService.GetList(GetSystemID(),
						panel.GetSearchCondition(),
						(PagingLoadConfig) loadConfig, callback);
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
		
		detailPanel = new FormContentPanel<GWTLoginLog>();
		detailPanel.setBindInfo(GetDetailHashMap());
		panel.setDetailForm(detailPanel);
		add(detailPanel);
		
		panel.getDataGrid().addListener(Events.CellDoubleClick, new Listener<GridEvent<GWTLoginLog>>() {

			@Override
			public void handleEvent(GridEvent<GWTLoginLog> be) {
				// TODO Auto-generated method stub
				openDetailPage(be.getModel());
			}
		});
	}

	private Listener<MessageBoxEvent> DelHandler() {
		// TODO Auto-generated method stub
		return new Listener<MessageBoxEvent>() {
			public void handleEvent(MessageBoxEvent be) {
				Button msgBtn = be.getButtonClicked();
				if (msgBtn.getText().equalsIgnoreCase("Yes")) {
					loginLogService.deleteLoginLog(panel.getSelection(), 
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
//		detailMap.put(GWTLoginLog.N_ID, "登录ID");
//		detailMap.put(GWTLoginLog.N_UserName, "登录用户");
		
		return detailMap;
	}

	private List<ColumnConfig> GetColumnConfig() {
		// TODO Auto-generated method stub
		List<ColumnConfig> columns = new ArrayList<ColumnConfig>();
		//columns.add(new ColumnConfig(GWTLoginLog.N_ID, "登录ID",50));
		columns.add(new ColumnConfig(GWTLoginLog.N_UserName, "登录用户",80));
		columns.add(new ColumnConfig(GWTLoginLog.N_LoginTime, "登入时间",130));
		columns.add(new ColumnConfig(GWTLoginLog.N_LogoutTime, "登出时间",130));
		columns.add(new ColumnConfig(GWTLoginLog.N_Duration, "在线时长",130));
		columns.add(new ColumnConfig(GWTLoginLog.N_IpAddress, "登录IP", 130));
		columns.add(GetRenderColumn("taskDetail", false, "详情", 40));
		return columns;
	}

	/**
	 * 获得 脚本定义、执行列
	 * 
	 * @param iconType
	 *            按钮样式名称
	 * @param fireExec
	 *            true:执行列 false:定义列
	 * @param title
	 *            列表头名称
	 * @param width
	 *            列宽
	 * @return 脚本定义、执行列
	 */
	 
	private ColumnConfig GetRenderColumn(final String iconType,
			final boolean fireExec, String title, int width) {
		GridCellRenderer<GWTLoginLog> gridRender = new GridCellRenderer<GWTLoginLog>() {
			@Override
			public Object render(final GWTLoginLog model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<GWTLoginLog> store, Grid<GWTLoginLog> grid) {
				String iconID = "icon_" + iconType + model.getID();

				boolean isSet = true;// model.getIsSet();
				String iconName = iconType + (isSet ? "" : "_No");
				IconButton b = new IconButton(iconName);
				if (fireExec && !isSet) {
					b.setEnabled(false);
					b.setStyleAttribute("cursor", "default");
				}

				HtmlContainer html = new HtmlContainer(
						"<span style = 'margin:0px;padding:0px;' id = '"
								+ iconID + "' ></span>");
				html.add(b, "#" + iconID);
				b.addSelectionListener(new SelectionListener<IconButtonEvent>() {
					@Override
					public void componentSelected(IconButtonEvent ce) {
						openDetailPage(model);												
					}

				});
				
				return html;
			}
		};
		
		ColumnConfig column = new ColumnConfig("", title, width);
		column.setAlignment(HorizontalAlignment.CENTER);
		column.setSortable(false);
		column.setResizable(false);
		column.setRenderer(gridRender);

		return column;
	}
	
	private void openDetailPage(GWTLoginLog model) {
		// TODO Auto-generated method stub
		if(model!=null){
			String tabTitle = "["
					+ model.getID() + "]" + "所有操作";

			BasePage page = new OperationLogPage(model.getID());
			AppContext.GetEntryPoint().AddTabItem(model.getID(), tabTitle, page);
		}
	}
}
