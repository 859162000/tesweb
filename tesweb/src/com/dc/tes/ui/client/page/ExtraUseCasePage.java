package com.dc.tes.ui.client.page;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.dc.tes.ui.client.IUseCaseService;
import com.dc.tes.ui.client.IUseCaseServiceAsync;
import com.dc.tes.ui.client.MainPage;
import com.dc.tes.ui.client.common.ServiceHelper;
import com.dc.tes.ui.client.control.ConfigToolBar;
import com.dc.tes.ui.client.control.FormContentPanel;
import com.dc.tes.ui.client.control.GridContentPanel;
import com.dc.tes.ui.client.control.PostFormPanel;
import com.dc.tes.ui.client.control.PostPanelAsync;
import com.dc.tes.ui.client.control.PostPanelAsync.SubmitCompleteEvent;
import com.dc.tes.ui.client.model.GWTCaseDirectory;
import com.dc.tes.ui.client.model.GWTCaseFlow;
import com.dc.tes.ui.client.model.GWTStock;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.data.BaseTreeLoader;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.extjs.gxt.ui.client.data.RpcProxy;
import com.extjs.gxt.ui.client.data.TreeLoader;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FormEvent;
import com.extjs.gxt.ui.client.event.GridEvent;
import com.extjs.gxt.ui.client.event.IconButtonEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.store.TreeStore;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.HorizontalPanel;
import com.extjs.gxt.ui.client.widget.HtmlContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.button.IconButton;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.extjs.gxt.ui.client.widget.form.DateField;
import com.extjs.gxt.ui.client.widget.form.DateTimePropertyEditor;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.extjs.gxt.ui.client.widget.toolbar.FillToolItem;
import com.extjs.gxt.ui.client.widget.treepanel.TreePanel;
import com.extjs.gxt.ui.client.widget.treepanel.TreePanel.CheckCascade;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Random;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class ExtraUseCasePage extends BasePage {
	private IUseCaseServiceAsync useCaseService = null;
	private List<GWTCaseDirectory> Directories = null;
	GWTCaseFlow EditCaseFlow;
	/**
	 * 列表控件
	 */
	GridContentPanel<GWTCaseFlow> panel;
	/**
	 * 工具条
	 */
	ConfigToolBar bottomBar;
	/**
	 * 详细信息控件
	 */
	FormContentPanel<GWTCaseFlow> detailPanel;	
	/**
	 * 用例信息窗口
	 */
	Window window;
	String[] conditions = new String[]{"", "", "", "", "", "", ""};
	Date[] dates = new Date[2];
	@Override
	protected void onRender(Element parent, int index) {
		super.onRender(parent, index);
		setLayout(new RowLayout());
		panel = new GridContentPanel<GWTCaseFlow>();
		useCaseService = ServiceHelper.GetDynamicService("useCase",
				IUseCaseService.class);
		RpcProxy<PagingLoadResult<GWTCaseFlow>> proxy = new RpcProxy<PagingLoadResult<GWTCaseFlow>>() {
			@Override
			protected void load(Object loadConfig,
					AsyncCallback<PagingLoadResult<GWTCaseFlow>> callback) {
				// TODO Auto-generated method stub
				useCaseService.getAllUseCases(GetSysInfo(), conditions, dates, Directories, (PagingLoadConfig)loadConfig, callback);
			}
		};
		panel.setProxy(proxy); 
		panel.setColumns(GetColumnConfig());
		panel.setStyleAttribute("height", "410");
		panel.DrowGridView(GWTCaseFlow.N_Name);
		panel.getDataGrid().addListener(Events.CellDoubleClick, new Listener<GridEvent<GWTCaseFlow>>() {

			@Override
			public void handleEvent(GridEvent<GWTCaseFlow> be) {
				// TODO Auto-generated method stub
				EditCaseFlow = be.getModel();
				CaseFlowInfoWindow caseFlowInfoWindow = new CaseFlowInfoWindow(window, EditCaseFlow, panel);
				caseFlowInfoWindow.ShowCaseFlowInfoWindow();
			}
		});
		panel.setTopComponent(DrawSearchBar());
		
		bottomBar = new ConfigToolBar();
		bottomBar.initPageToolBar(panel.getLoader());
		bottomBar.AddButton("btnCaseFlowExport",new Button("用例导出"), MainPage.ICONS
				.WebDown(), CaseFlowExportHandler());
				
		bottomBar.AddWidget(new FillToolItem());
		bottomBar.AddEditBtn("btnEdit", EditHandler());
		bottomBar.AddDelBtn("btnDel", DelHandler());
		InitBtnConfigBar(bottomBar);
		panel.setBottomBar(bottomBar);
		add(panel);		
	}


	


	private SelectionListener<ButtonEvent> DelHandler() {
		// TODO Auto-generated method stub
		return new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				final List<GWTCaseFlow> items = panel.getDataGrid().getSelectionModel().getSelectedItems(); 
				// TODO Auto-generated method stub
				MessageBox.confirm("提示信息", "是否确认删除",
						new Listener<MessageBoxEvent>() {
							public void handleEvent(MessageBoxEvent be) {
								Button msgBtn = be.getButtonClicked();
								if (msgBtn.getText().equalsIgnoreCase("Yes")) {
									List<ModelData> list = new ArrayList<ModelData>();
									list.addAll(items);
									useCaseService.deleteSelectedItem(list, GetLoginLogID(),
											new AsyncCallback<Boolean>() {

												@Override
												public void onFailure(
														Throwable caught) {
													// TODO Auto-generated
													// method stub
													caught.printStackTrace();
													MessageBox.alert("错误提示",
															"删除失败", null);
												}

												@Override
												public void onSuccess(
														Boolean result) {
													// TODO Auto-generated
													panel.getDataGrid().getStore().getLoader().load();
													
												}
											});
								}
							}
						});
			}
		};
	}

	private SelectionListener<ButtonEvent> EditHandler() {
		// TODO Auto-generated method stub
		return new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				// TODO Auto-generated method stub
				EditCaseFlow = panel.getDataGrid().getSelectionModel().getSelectedItem();
				CaseFlowInfoWindow caseFlowInfoWindow = new CaseFlowInfoWindow(window, EditCaseFlow, panel);
				caseFlowInfoWindow.CreateEditForm();
			}
		};
	}

	/**
	 * 导出用例数据
	 * @return
	 */
	private SelectionListener<ButtonEvent> CaseFlowExportHandler() {
		// TODO Auto-generated method stub
		return new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				// TODO Auto-generated method stub
				MessageBox.confirm("提示信息", "是否导出查询条件下的所有用例？",
						new Listener<MessageBoxEvent>() {
							public void handleEvent(MessageBoxEvent be) {
								Button msgBtn = be.getButtonClicked();
								if (msgBtn.getText().equalsIgnoreCase("Yes")) {
									DateTimeFormat dtf = DateTimeFormat.getFormat("yyyyMMdd");
									String startDate = dates[0]==null?"" : dtf.format(dates[0]);
									String endDate = dates[1]==null?"" : dtf.format(dates[1]);
									String pathID = "";
									if(Directories!=null)
										for(GWTCaseDirectory c : Directories){
											if(!pathID.isEmpty()){
												pathID += "a";
											}
											pathID += c.GetID();
										}
									final PostFormPanel formPanel = new PostFormPanel();
									formPanel
											.setEncoding(FormPanel.Encoding.MULTIPART);
									formPanel.setMethod(FormPanel.Method.POST);
									formPanel.setAction("UseCaseInfoExport?"
											+ "systemID=" + GetSystemID()
											+ "&caseFlowNo=" + conditions[0] 
											+ "&caseFlowName=" + conditions[1]
											+ "&designer=" + conditions[2]
											+ "&startDate=" + startDate
											+ "&endDate=" + endDate
											+ "&pathId=" + pathID
											+ "&rd=" + Random.nextInt()
											);
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
						});
			}
		};
	}

	private List<ColumnConfig> GetColumnConfig() {
		// TODO Auto-generated method stub
		List<ColumnConfig> columns = new ArrayList<ColumnConfig>();		
		columns.add(new ColumnConfig(GWTCaseFlow.N_CaseFlowNo, "用例编号", 150));
		columns.add(new ColumnConfig(GWTCaseFlow.N_Name, "用例名称", 200));
		columns.add(new ColumnConfig(GWTCaseFlow.N_Designer, "设计人", 120));
		columns.add(new ColumnConfig(GWTCaseFlow.N_Priority, "优先级", 80));
		columns.add(new ColumnConfig(GWTCaseFlow.N_CreateTime, "创建时间", 150));
		columns.add(GetRenderColumn("taskDetail", false, "详情", 40));
		return columns;
	}

	private ColumnConfig GetRenderColumn(final String iconType,
			final boolean fireExec, String title, int width) {
		GridCellRenderer<GWTCaseFlow> gridRender = new GridCellRenderer<GWTCaseFlow>() {
			@Override
			public Object render(final GWTCaseFlow model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<GWTCaseFlow> store, Grid<GWTCaseFlow> grid) {
				String iconID = "icon_" + iconType + model.GetID();

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
						EditCaseFlow = model;
						CaseFlowInfoWindow caseFlowInfoWindow = new CaseFlowInfoWindow(window, EditCaseFlow, panel);
						caseFlowInfoWindow.ShowCaseFlowInfoWindow();
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
	 * 搜索条件框
	 * @return
	 */
	private Component DrawSearchBar() {
		// TODO Auto-generated method stub
		FormPanel formPanel = new FormPanel();
		formPanel.setFrame(true);
		formPanel.setHeaderVisible(false);
		formPanel.setBodyBorder(false);
		formPanel.setBorders(false);
		formPanel.setLabelWidth(60);
		formPanel.setButtonAlign(HorizontalAlignment.LEFT);
		FormData formdata = new FormData();
		formdata.setWidth(250);
		
		final TextField<String> tfCaseFlowNo = new TextField<String>();
		tfCaseFlowNo.setFieldLabel("用例编号");
		tfCaseFlowNo.setName(GWTCaseFlow.N_CaseFlowNo);
		
		final TextField<String> tfCaseFlowName = new TextField<String>()	;
		tfCaseFlowName.setFieldLabel("用例名称");
		tfCaseFlowName.setName(GWTCaseFlow.N_Name);
		formPanel.add(getHPanel(tfCaseFlowNo, tfCaseFlowName));
		
		HorizontalPanel hPanel = new HorizontalPanel();
		hPanel.setWidth("100%");
		hPanel.setStyleAttribute("margin-top", "10px");
		hPanel.setStyleAttribute("margin-bottom", "5px");
		
		hPanel.add(getFormStyleLable("设计人"));
		final TextField<String> tfDesigner = new TextField<String>();
		tfDesigner.setName(GWTCaseFlow.N_Designer);
		tfDesigner.setWidth(250);	
		tfDesigner.setStyleAttribute("margin-right", "25px");
		hPanel.add(tfDesigner);
		
		
		hPanel.add(getFormStyleLable("创建时间"));		
	    final DateField startdate = new DateField();
	    startdate.setPropertyEditor(new DateTimePropertyEditor("yyyy-MM-dd"));  
	    startdate.setWidth(115);
	    hPanel.add(startdate);
	    
	    final LabelField label2 = new LabelField("至");
		label2.setWidth(20);
		label2.setStyleAttribute("margin-left", "5px");
		label2.setStyleName("x-form-item-label");
		label2.setStyleAttribute("font", "normal 12px tahoma, arial, helvetica, sans-serif");		
		hPanel.add(label2);
	    
	    final DateField enddate = new DateField();  
	    enddate.setPropertyEditor(new DateTimePropertyEditor("yyyy-MM-dd"));
	    enddate.setWidth(115);
	    hPanel.add(enddate);  
	    formPanel.add(hPanel);
	    
	    final HorizontalPanel hPanel2 = new HorizontalPanel();
		hPanel2.setWidth("100%");
		hPanel2.setStyleAttribute("margin-top", "10px");
		hPanel2.setStyleAttribute("margin-bottom", "5px");
		hPanel2.add(getFormStyleLable("优先级"));
	    final ComboBox<GWTStock> priorityBox = new ComboBox<GWTStock>(); 
	    priorityBox.setDisplayField(GWTStock.N_Name);
	    priorityBox.setValueField(GWTStock.N_Pos);
	    priorityBox.setWidth(250);
	    priorityBox.setStyleAttribute("margin-right", "35px");
	    ListStore<GWTStock> priStore = new ListStore<GWTStock>();
	    priStore.add(new GWTStock("不限制", ""));
	    priStore.add(new GWTStock("高", "高"));
	    priStore.add(new GWTStock("中", "中"));
	    priStore.add(new GWTStock("低", "低"));
	    priorityBox.setStore(priStore);
	    priorityBox.setValue(priStore.getAt(0));
	    priorityBox.setTriggerAction(TriggerAction.ALL);
	    hPanel2.add(priorityBox);
	    
	    hPanel2.add(getFormStyleLable("实例化"));
	    final ComboBox<GWTStock> initialBox = new ComboBox<GWTStock>();
	    initialBox.setDisplayField(GWTStock.N_Name);
	    initialBox.setValueField(GWTStock.N_Pos);
	    initialBox.setWidth(250);
	    //initialBox.setStyleAttribute("margin-right", "25px");
	    ListStore<GWTStock> initStore = new ListStore<GWTStock>();
	    initStore.add(new GWTStock("不限制", ""));
	    initStore.add(new GWTStock("未实例化", "0"));
	    initStore.add(new GWTStock("已实例化", "1"));	    
	    initialBox.setStore(initStore);
	    initialBox.setValue(initStore.getAt(0));
	    initialBox.setTriggerAction(TriggerAction.ALL);
	    hPanel2.add(initialBox);
	    formPanel.add(hPanel2);
	 
	    final HorizontalPanel hPanel3 = new HorizontalPanel();
		hPanel3.setWidth("100%");
		hPanel3.setStyleAttribute("margin-top", "10px");
		hPanel3.setStyleAttribute("margin-bottom", "5px");
		hPanel3.add(getFormStyleLable("是否通过"));
	    final ComboBox<GWTStock> cbPassFlag = new ComboBox<GWTStock>(); 
	    cbPassFlag.setDisplayField(GWTStock.N_Name);
	    cbPassFlag.setValueField(GWTStock.N_Pos);
	    cbPassFlag.setWidth(250);
	    cbPassFlag.setStyleAttribute("margin-right", "35px");
	    ListStore<GWTStock> passflagStore = new ListStore<GWTStock>();
	    passflagStore.add(new GWTStock("不限制", ""));
	    passflagStore.add(new GWTStock("是", "1"));
	    passflagStore.add(new GWTStock("否", "0"));
	    cbPassFlag.setStore(passflagStore);
	    cbPassFlag.setValue(passflagStore.getAt(0));
	    cbPassFlag.setTriggerAction(TriggerAction.ALL);
	    hPanel3.add(cbPassFlag);
	    
	    hPanel3.add(getFormStyleLable("暂时不用"));
	    final ComboBox<GWTStock> cbDisableFlag = new ComboBox<GWTStock>();
	    cbDisableFlag.setDisplayField(GWTStock.N_Name);
	    cbDisableFlag.setValueField(GWTStock.N_Pos);
	    cbDisableFlag.setWidth(250);
	    //initialBox.setStyleAttribute("margin-right", "25px");
	    ListStore<GWTStock> disableStore = new ListStore<GWTStock>();
	    disableStore.add(new GWTStock("不限制", ""));
	    disableStore.add(new GWTStock("是", "1"));
	    disableStore.add(new GWTStock("否", "0"));	    
	    cbDisableFlag.setStore(disableStore);
	    cbDisableFlag.setValue(disableStore.getAt(0));
	    cbDisableFlag.setTriggerAction(TriggerAction.ALL);
	    hPanel3.add(cbDisableFlag);
	    formPanel.add(hPanel3);
	      
	    
	    
	    final HorizontalPanel hPanel4 = new HorizontalPanel();
		hPanel4.setWidth("100%");
		hPanel4.setStyleAttribute("margin-top", "10px");
		hPanel4.setStyleAttribute("margin-bottom", "5px"); 
		
		hPanel4.add(getFormStyleLable("用例路径"));			
		final TextField<String> tfPath = new TextField<String>();
		tfPath.setWidth(450);
		tfPath.setReadOnly(true);
		tfPath.setStyleAttribute("margin-right", "5px");
		hPanel4.add(tfPath);
		
		final Button btnSelect = new Button("选择");
		btnSelect.addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				// TODO Auto-generated method stub
				DrawSelectPathWin(tfPath);				
				
			}			
		});
		hPanel4.add(btnSelect);
		
		final Button btnSearch = new Button("查    询");
		btnSearch.setWidth(100);
		btnSearch.setIcon(ICONS.search());
		btnSearch.setStyleAttribute("margin-left", "30px");
		btnSearch.addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				// TODO Auto-generated method stub
				conditions[0] = tfCaseFlowNo.getValue()==null?"":tfCaseFlowNo.getValue();
				conditions[1] = tfCaseFlowName.getValue()==null?"":tfCaseFlowName.getValue();
				conditions[2] = tfDesigner.getValue()==null?"":tfDesigner.getValue();
				conditions[3] = priorityBox.getValue().getPos();
				conditions[4] = initialBox.getValue().getPos();
				conditions[5] = cbPassFlag.getValue().getPos();
				conditions[6] = cbDisableFlag.getValue().getPos();
				dates[0] = startdate.getValue();
				dates[1] = enddate.getValue();
				panel.getDataGrid().fireEvent(Events.Attach);
			}
		});
		hPanel4.add(btnSearch);
		formPanel.add(hPanel4);
		return formPanel;
	}
	private void DrawSelectPathWin(final TextField<String> tfPath) {
		// TODO Auto-generated method stub		
		final Window window = new Window();
		window.setSize(350, 400);
		window.setScrollMode(Scroll.AUTOY);
		window.setModal(true);
		window.setPlain(true);
		window.setHeading("选择路径");
		window.setLayout(new FitLayout());
		
		ContentPanel contentPanel = new ContentPanel();
		contentPanel.setBodyBorder(false);
		contentPanel.setBorders(false);
		contentPanel.setHeaderVisible(false);
		RpcProxy<List<GWTCaseDirectory>> proxy = new RpcProxy<List<GWTCaseDirectory>>() {

			@Override
			protected void load(Object loadConfig,
					AsyncCallback<List<GWTCaseDirectory>> callback) {
				// TODO Auto-generated method stub
				useCaseService.getCaseDirectoryTree(GetSysInfo(), 
						(GWTCaseDirectory)loadConfig, callback);
				
			}
		};
		final TreeLoader<GWTCaseDirectory> loader = new BaseTreeLoader<GWTCaseDirectory>(proxy) ;
		TreeStore<GWTCaseDirectory> store = new TreeStore<GWTCaseDirectory>(loader);
		final TreePanel<GWTCaseDirectory> tree = new TreePanel<GWTCaseDirectory>(store){
			@Override
			public boolean hasChildren(GWTCaseDirectory parent) {
					return true;						
			}
		};  
	    tree.setDisplayProperty(GWTCaseDirectory.N_Name);  
	    tree.setWidth(335); 
	    tree.setHeight(330);
	    tree.setCheckable(true);   
	    tree.setCheckStyle(CheckCascade.NONE);
	    contentPanel.add(tree);
	    window.add(contentPanel);
	    Button btn_ok = new Button("确定", new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				// TODO Auto-generated method stub
				Directories = tree.getCheckedSelection();
				String s = "";
				for(GWTCaseDirectory d : Directories){
					if(!s.isEmpty()){
						s+="; ";
					}
					s+=d.GetPath();
				}
				tfPath.setValue(s);
				window.hide();
			}
		});
	    window.addButton(btn_ok);
	    window.addButton(new Button("取消", new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				// TODO Auto-generated method stub
				window.hide();
			}
		}));
	    
	    window.show();
	}
	

	@SuppressWarnings("rawtypes")
	private HorizontalPanel getHPanel(Field leftControl,Field rightControl)
	{
		HorizontalPanel hPanel = new HorizontalPanel();
		hPanel.setWidth("100%");
		hPanel.setStyleAttribute("margin-bottom", "5px");
		
		hPanel.add(getFormStyleLable(leftControl.getFieldLabel()));
		
		leftControl.setWidth(250);
		hPanel.add(leftControl);
		
		LabelField lbRight = getFormStyleLable(rightControl.getFieldLabel());
		lbRight.setStyleAttribute("margin-left", "50px");
		hPanel.add(lbRight);
		
		rightControl.setWidth(250);
		hPanel.add(rightControl);
		
		return hPanel;
	}
	
	private LabelField getFormStyleLable(String labelText)
	{
		LabelField label = new LabelField(labelText + ":");
		label.setWidth(75);
		label.setStyleName("x-form-item-label");
		label.setStyleAttribute("font", "normal 12px tahoma, arial, helvetica, sans-serif");
		return label;
	}
	
	

	
}
