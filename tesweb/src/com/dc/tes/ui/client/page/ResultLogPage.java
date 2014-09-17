package com.dc.tes.ui.client.page;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.dc.tes.ui.client.AppContext;
import com.dc.tes.ui.client.IResultService;
import com.dc.tes.ui.client.IResultServiceAsync;
import com.dc.tes.ui.client.ITestRoundService;
import com.dc.tes.ui.client.ITestRoundServiceAsync;
import com.dc.tes.ui.client.IUserService;
import com.dc.tes.ui.client.IUserServiceAsync;
import com.dc.tes.ui.client.MainPage;
import com.dc.tes.ui.client.common.ServiceHelper;
import com.dc.tes.ui.client.control.CascadeContentPanel;
import com.dc.tes.ui.client.control.ConfigToolBar;
import com.dc.tes.ui.client.control.PostFormPanel;
import com.dc.tes.ui.client.control.PostPanelAsync;
import com.dc.tes.ui.client.control.PostPanelAsync.SubmitCompleteEvent;
import com.dc.tes.ui.client.control.ResultDetailPanel;
import com.dc.tes.ui.client.model.GWTResultLog;
import com.dc.tes.ui.client.model.GWTStock;
import com.dc.tes.ui.client.model.GWTTestRound;
import com.dc.tes.ui.client.model.GWTUser;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.data.BasePagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.extjs.gxt.ui.client.data.RpcProxy;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FormEvent;
import com.extjs.gxt.ui.client.event.GridEvent;
import com.extjs.gxt.ui.client.event.IconButtonEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.DatePicker;
import com.extjs.gxt.ui.client.widget.HtmlContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.Popup;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.button.IconButton;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.toolbar.FillToolItem;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class ResultLogPage extends BasePage {
	IResultServiceAsync resultService = ServiceHelper.GetDynamicService(
			"result", IResultService.class);
	GWTResultLog resultLog = null;
	
	CascadeContentPanel<GWTResultLog> panel;
	ResultDetailPanel detailPanel;
	ConfigToolBar configBar;
	PagingLoadConfig loadConfig;
	Button dateSelectBtn = new Button("日志时间");

	public ResultLogPage() {

	}

	protected void onRender(Element parent, int index) {
		super.onRender(parent, index);
		loadConfig = new BasePagingLoadConfig();
		loadConfig.setLimit(10);
		panel = new CascadeContentPanel<GWTResultLog>();

		RpcProxy<PagingLoadResult<GWTResultLog>> proxy = new RpcProxy<PagingLoadResult<GWTResultLog>>() {
			@Override
			public void load(Object loadConfig,
					AsyncCallback<PagingLoadResult<GWTResultLog>> callback) {
				resultService.GetList(GetSystemID(), panel.GetSearchCondition(), (PagingLoadConfig) loadConfig, callback);
			}

		};
		panel.setProxy(proxy);
		panel.setColumns(GetColumnConfig());
		panel.DrowSearchBar();
		//添加根据状态过滤结果
		final ComboBox<GWTStock> cb = new ComboBox<GWTStock>();
		cb.setEditable(false);
		cb.setDisplayField(GWTStock.N_Name);
		cb.setValueField(GWTStock.N_Pos);
		final ListStore<GWTStock> ls = new ListStore<GWTStock>();
		ls.add(new GWTStock("所有状态", "-1"));
		ls.add(new GWTStock("通过", "1"));
		ls.add(new GWTStock("失败", "0"));
		ls.add(new GWTStock("正在执行中", "2"));
		ls.add(new GWTStock("未执行", "3"));
		ls.add(new GWTStock("执行中断", "4"));
		ls.add(new GWTStock("超时", "5"));
		ls.add(new GWTStock("中止执行", "6"));
		cb.setStore(ls);
		cb.setValue(ls.getAt(0));
		cb.setTriggerAction(TriggerAction.ALL);
		cb.addListener(Events.SelectionChange, new Listener<BaseEvent>(){

			@Override
			public void handleEvent(BaseEvent be) {
				// TODO Auto-generated method stub				
				loadConfig.set("flag", cb.getValue().getPos());			
				panel.getLoader().load(loadConfig);
			}
			
		});
		panel.getSearchBar().add(cb);
		
		//添加根据用户过滤结果
	    final ComboBox<GWTUser> user = new ComboBox<GWTUser>();
	    user.setEditable(false);
	    //user.setStyleAttribute("margin-left", "5px");
	    user.setDisplayField(GWTUser.N_name);
	    user.setValueField(GWTUser.N_id);
	    final ListStore<GWTUser> userList = new ListStore<GWTUser>();
	    userList.add(new GWTUser("-1", "所有用户", "","", 0, 0));
	    IUserServiceAsync userService = ServiceHelper.GetDynamicService("user", IUserService.class);
	    userService.GetUserBySystem(GetSysInfo().GetSystemID(), new AsyncCallback<List<GWTUser>>() {

			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub
				caught.printStackTrace();
			}

			@Override
			public void onSuccess(List<GWTUser> result) {
				// TODO Auto-generated method stub
				userList.add(result);
			}
		});
	    user.setStore(userList);
	    user.setValue(userList.getAt(0));
	    user.setTriggerAction(TriggerAction.ALL);
	    user.addListener(Events.SelectionChange, new Listener<BaseEvent>() {

			@Override
			public void handleEvent(BaseEvent be) {
				loadConfig.set("user", user.getValue().getUserID());
				panel.getLoader().load(loadConfig);
			}
		});
		panel.getSearchBar().add(user);
		
		final ComboBox<GWTTestRound> cbTestRound = new ComboBox<GWTTestRound>();
		cbTestRound.setHideLabel(true);
		cbTestRound.setWidth(100);
		cbTestRound.setEditable(false);
		cbTestRound.setValueField(GWTTestRound.N_RoundID);
		cbTestRound.setDisplayField(GWTTestRound.N_RoundName);
		final ListStore<GWTTestRound> roundStore = new ListStore<GWTTestRound>();
		GWTTestRound gwtTestRound = new GWTTestRound(-1, GetSystemID());
		gwtTestRound.set(GWTTestRound.N_RoundName, "所有轮次");
		roundStore.add(gwtTestRound);
		ITestRoundServiceAsync testRoundService = ServiceHelper.GetDynamicService("testRound", ITestRoundService.class);	
		testRoundService.GetTestRounds(GetSystemID(), new AsyncCallback<List<GWTTestRound>>() {

			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub
				caught.printStackTrace();
				MessageBox.alert("错误提示",
						"获取轮次列表失败！", null);
			}

			@Override
			public void onSuccess(List<GWTTestRound> result) {
				// TODO Auto-generated method stub
				for(GWTTestRound tr : result){
					if(tr.GetCurrentRoundFlag() == 1){
						GWTTestRound t = new GWTTestRound(tr.GetRoundID(), GetSystemID());
						t.set(GWTTestRound.N_RoundName, "当前轮次");
						roundStore.add(t);
						break;
					}
				}
				roundStore.add(result);
			}
		});
		cbTestRound.setStore(roundStore);
		cbTestRound.setValue(roundStore.getAt(0));
		cbTestRound.setTriggerAction(TriggerAction.ALL);
		cbTestRound.addSelectionChangedListener(new SelectionChangedListener<GWTTestRound>() {

			@Override
			public void selectionChanged(SelectionChangedEvent<GWTTestRound> se) {
				// TODO Auto-generated method stub
				loadConfig.set("roundId", cbTestRound.getValue().GetRoundID());
				panel.getLoader().load(loadConfig);
			}
		});
		panel.getSearchBar().add(cbTestRound);
		
		panel.DrowGridView("", true, true);
		panel.getDataGrid().addListener(Events.Attach, new Listener<GridEvent<GWTResultLog>>() {

			@Override
			public void handleEvent(GridEvent<GWTResultLog> be) {
				// TODO Auto-generated method stub
				cb.setValue(ls.getAt(0));
				user.setValue(userList.getAt(0));
				cbTestRound.setValue(roundStore.getAt(0));
			}		
		});
		configBar = new ConfigToolBar();
		
		configBar.initPageToolBar(panel.getLoader());
		configBar.AddButton("btnDate", dateSelectBtn, MainPage.ICONS.DatCom(), DateHandler());
		
		configBar.AddButton("btnFlowDownload", new Button("执行结果导出"), MainPage.ICONS.WebDown(), CaseFlowDownloadHandler());
		configBar.AddWidget(new FillToolItem());

		configBar.AddDelBtn("btnDel", DelHandler());
		InitBtnConfigBar(configBar);
		panel.setBottomBar(configBar);
		add(panel);

		detailPanel = new ResultDetailPanel();
		panel.setCascadePanel(detailPanel);
		add(detailPanel);
		
		panel.getDataGrid().addListener(Events.CellDoubleClick, new Listener<GridEvent<GWTResultLog>>() {
			@Override
			public void handleEvent(GridEvent<GWTResultLog> be) {
				// TODO Auto-generated method stub
				openDetailPage(be.getModel());
			}
		});
	}


	private SelectionListener<ButtonEvent> CaseFlowDownloadHandler() {
		// TODO Auto-generated method stub
		return new SelectionListener<ButtonEvent>() {
			public void componentSelected(ButtonEvent ce) {

				final GWTResultLog gwtResultLog = panel.getDataGrid()
				.getSelectionModel().getSelectedItems()
				.get(0);
				if(gwtResultLog.getQueueListID()==null || gwtResultLog.getQueueListID().isEmpty()){
					MessageBox.alert("提示", "非执行集执行结果无法导出", null);
					return;
				}
				MessageBox.confirm("提示信息", "是否导出该执行批次下的所有业务流执行结果？",
						new Listener<MessageBoxEvent>() {
							public void handleEvent(MessageBoxEvent be) {
								Button msgBtn = be.getButtonClicked();
								if (msgBtn.getText().equalsIgnoreCase("Yes")) {
									
									final PostFormPanel formPanel = new PostFormPanel();
									
									formPanel
											.setEncoding(FormPanel.Encoding.MULTIPART);
									formPanel.setMethod(FormPanel.Method.POST);
									formPanel.setAction("YQCaseFlowResultExport?"
											+ "executeLogId="
											+ gwtResultLog.getID());
									formPanel.addListener(Events.Submit, new Listener<FormEvent>(){

										@Override
										public void handleEvent(FormEvent be) {
											// TODO Auto-generated method stub
											TESWindows.ShowDownLoad(be.getResultHtml());
										}
										
									});									
									
									formPanel.submit();												
								}
							};
				});}};				
	}
	

	private SelectionListener<ButtonEvent> DateHandler() {
		// TODO Auto-generated method stub
		return new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				// TODO Auto-generated method stub
				Popup popUp = new Popup();
				final DatePicker datePicker = new DatePicker();
				datePicker.addListener(Events.Select,
						new Listener<ComponentEvent>() {

							public void handleEvent(ComponentEvent be) {
								String date = DateTimeFormat.getFormat(
										"yyyy-MM-dd").format(
										datePicker.getValue());								
								loadConfig.set("date", date);
								panel.getLoader().load(loadConfig);
								datePicker.hide();
							}

						});

				popUp.add(datePicker);
				popUp.show(dateSelectBtn.getElement(), "tl-br?");

			}

		};
	}

	/**
	 * 获得Grid的列配置列表
	 * 
	 * @return Grid的列配置列表
	 */
	private List<ColumnConfig> GetColumnConfig() {
		List<ColumnConfig> columns = new ArrayList<ColumnConfig>();
		columns.add(new ColumnConfig(GWTResultLog.N_ExecuteSetName, "执行集名称",130));
		columns.add(new ColumnConfig(GWTResultLog.N_CreateTime, "执行开始时间", 125));
		columns.add(new ColumnConfig(GWTResultLog.N_EndRunTime, "执行结束时间", 125));
		columns.add(new ColumnConfig(GWTResultLog.N_RunDuration, "执行时长", 80));
		columns.add(new ColumnConfig(GWTResultLog.N_UserName, "执行用户", 70));
		columns.add(new ColumnConfig(GWTResultLog.N_ExecuteBatchNo, "执行批次", 130));

		columns.add(GetRenderColumn(GWTResultLog.N_PassFlag, "执行结果", 70));
		columns.add(GetRenderColumn("taskDetail", false, "详情", 40));
	//	columns.add(GetRenderColumn("download", false, "下载", 40));
		// RunColumn
		// taskDetail
		return columns;
	}

	private ColumnConfig GetRenderColumn(String id, String name,
			int width) {
		GridCellRenderer<GWTResultLog> gridRender = new GridCellRenderer<GWTResultLog>(){

			@Override
			public Object render(GWTResultLog model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<GWTResultLog> store, Grid<GWTResultLog> grid) {
				// TODO Auto-generated method stub
				if(model.getPassFlag().isEmpty()){
					return "";
				}
				if(model.getPassFlag().equals("1"))
					return "<span style='color:" + "green" + "'>" + "通过" + "</span>";
				else if (model.getPassFlag().equals("0")){
					 return "<span style='color:" + "red" + "'>" + "失败" + "</span>";
				}else if (model.getPassFlag().equals("2")){
					 return "<span style='color:" + "blue" + "'>" + "正在执行中" + "</span>";
				}else if (model.getPassFlag().equals("3")){
					 return "<span style='color:" + "blue" + "'>" + "未执行" + "</span>";
				}else if (model.getPassFlag().equals("4")){
					 return "<span style='color:" + "blue" + "'>" + "执行中断" + "</span>";
				}else if (model.getPassFlag().equals("5")){
					 return "<span style='color:" + "red" + "'>" + "超时" + "</span>";
				}else if (model.getPassFlag().equals("6")){
					 return "<span style='color:" + "blue" + "'>" + "中止执行" + "</span>";
				}else if (model.getPassFlag().equals("7")){
					 return "<span style='color:" + "red" + "'>" + "异常终断" + "</span>";
				}else{
					return "<span style='color:" + "black" + "'>" + "失败" + "</span>";
				}
			}		
		};
		ColumnConfig columnConfig = new ColumnConfig(id, name, width);
		columnConfig.setRenderer(gridRender);
		return columnConfig;
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
		GridCellRenderer<GWTResultLog> gridRender = new GridCellRenderer<GWTResultLog>() {
			@Override
			public Object render(final GWTResultLog model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<GWTResultLog> store, Grid<GWTResultLog> grid) {
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
						String executeId = model.getID();			
						if (iconType == "download") // 如果是下载
						{
							// 此处为下载代码
							final PostPanelAsync formPanel = new PostPanelAsync(
									false);
							formPanel.setAction("CaseResultExport?ID="
									+ executeId);
							formPanel.setEncoding(FormPanel.Encoding.MULTIPART);
							formPanel.setMethod(FormPanel.Method.POST);
							formPanel.submit();
							formPanel
									.addSubmitCompleteHandler(new PostPanelAsync.SubmitCompleteHandler() {
										public void onSubmitComplete(
												SubmitCompleteEvent event) {
											TESWindows.ShowDownLoad(event
													.getResults());
										}
									});
							// formPanel = null;
						} else {
							openDetailPage(model);												
						}

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

	/**
	 * 获得详细信息绑定的Hash列表
	 * 
	 * @return Map<String,String> 对应 Map<对应绑定的值名称,字段显示名称>
	 */
	public Map<String, String> GetDetailHashMap() {

		Map<String, String> detailMap = new LinkedHashMap<String, String>();
		detailMap.put(GWTResultLog.N_QueueListID, "任务队列名称");
		detailMap.put(GWTResultLog.N_ExecuteBatchNo, "执行批次");
		return detailMap;
	}

	private Listener<MessageBoxEvent> DelHandler() {
		return new Listener<MessageBoxEvent>() {
			public void handleEvent(MessageBoxEvent be) {
				Button msgBtn = be.getButtonClicked();
				if (msgBtn.getText().equalsIgnoreCase("Yes")) {
					resultService.DeleteResult(panel.getSelection(),
							new AsyncCallback<Void>() {
								public void onFailure(Throwable caught) {
									caught.printStackTrace();
									MessageBox.alert("错误提示", "删除失败", null);
								}

								public void onSuccess(Void obj) {
									panel.getLoader().load(loadConfig);
								}
							});
				}
			}
		};
	}
	
	
	public static void openDetailPage(GWTResultLog model) {
		// TODO Auto-generated method stub
		if(model!=null){
			String executeId =model.getID();
			String tabId = "queueTask" + executeId;
			String tabTitle = "["
					+ model.getExecuteSetName() + "]" + "执行结果";
			boolean b = false;
			if((model.getType()!=null && model.getType()==2) ||
					(model.getQueueListID().isEmpty() && model.getExecuteBatchNo()!=null)){
				b = true;
			}
			BasePage page = new ResultDetailPage(model.getID(), b, -1);
			AppContext.GetEntryPoint().AddTabItem(tabId, tabTitle, page);
		}
	}
}
