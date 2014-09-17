package com.dc.tes.ui.client.page;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.dc.tes.ui.client.AppContext;
import com.dc.tes.ui.client.ICaseService;
import com.dc.tes.ui.client.ICaseServiceAsync;
import com.dc.tes.ui.client.ISysDynamicParameter;
import com.dc.tes.ui.client.ISysDynamicParameterAsync;
import com.dc.tes.ui.client.MainPage;
import com.dc.tes.ui.client.common.ServiceHelper;
import com.dc.tes.ui.client.common.TypeTranslate;
import com.dc.tes.ui.client.control.ConfigToolBar;
import com.dc.tes.ui.client.control.FormContentPanel;
import com.dc.tes.ui.client.control.GridContentPanel;
import com.dc.tes.ui.client.control.PostFormPanel;
import com.dc.tes.ui.client.control.PostPanelAsync;
import com.dc.tes.ui.client.control.PostPanelAsync.SubmitCompleteEvent;
import com.dc.tes.ui.client.control.Result.ReusltFactory;
import com.dc.tes.ui.client.enums.VersionType;
import com.dc.tes.ui.client.model.GWTCase;
import com.dc.tes.ui.client.model.GWTCaseParamExpectedValue;
import com.dc.tes.ui.client.model.GWTTransaction;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.SelectionMode;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.extjs.gxt.ui.client.data.RpcProxy;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.event.FormEvent;
import com.extjs.gxt.ui.client.event.GridEvent;
import com.extjs.gxt.ui.client.event.IconButtonEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MenuEvent;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.store.TreeStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.HtmlContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.button.IconButton;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.FileUploadField;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.Radio;
import com.extjs.gxt.ui.client.widget.form.RadioGroup;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.grid.CellEditor;
import com.extjs.gxt.ui.client.widget.grid.CheckColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.extjs.gxt.ui.client.widget.toolbar.FillToolItem;
import com.extjs.gxt.ui.client.widget.treegrid.EditorTreeGrid;
import com.extjs.gxt.ui.client.widget.treegrid.TreeGridCellRenderer;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * 案例列表管理页面
 *  列表中的案例数据列丰富为可直接查看，案例数据查看窗体修改，添加注释
 */
public class CasePage extends BasePage {
	/**
	 * 案例服务
	 */
	private ICaseServiceAsync caseService = null;
	
	private ISysDynamicParameterAsync sysParaService = null;
	
	/**
	 * 案例对应的交易信息
	 */
	private GWTTransaction tranInfo = null;
	/**
	 * 客户端交易
	 */
	private boolean isClientSimu = true;
	/**
	 * 正在编辑的案例
	 */
	private GWTCase EditCase = null;
	
	public static String SERVERNAME = "case";
	
	/**
	 * 参数预期值列表控件
	 */
	EditorTreeGrid<ModelData> treeGrid; 
	/**
	 * 案例预期值的store
	 */
	TreeStore<ModelData> store = new TreeStore<ModelData>();
	
	/**
	 * 编辑过的参数预期值位置
	 */
	Set<Integer> changedIndex;
	/**
	 * 列表控件
	 */
	GridContentPanel<GWTCase> panel;
	/**
	 * 详细信息控件
	 */
	FormContentPanel<GWTCase> detailPanel;
	/**
	 * 工具条
	 */
	ConfigToolBar bottomBar;
	/**
	 * 上传窗体
	 */
	UploadWin upWindow;

	public CasePage(GWTTransaction tranInfo,boolean isClientSimu) {
		this.tranInfo = tranInfo;
		this.isClientSimu = isClientSimu;
	}
	
//	public CasePage(String tranID,String tranCode,boolean isClientSimu) {
////		this.tranID = tranID;
////		this.tranCode = tranCode;
////		this.isClientSimu = isClientSimu;
//	}

	@Override
	protected void onRender(Element parent, int index) {
		super.onRender(parent, index);

		caseService = ServiceHelper.GetDynamicService(SERVERNAME, ICaseService.class);
		sysParaService = ServiceHelper.GetDynamicService("sysPara", ISysDynamicParameter.class);
		
		panel = new GridContentPanel<GWTCase>();
		RpcProxy<PagingLoadResult<GWTCase>> proxy = new RpcProxy<PagingLoadResult<GWTCase>>() {
			@Override
			public void load(Object loadConfig,
					AsyncCallback<PagingLoadResult<GWTCase>> callback) {
				if (tranInfo.getTranID().isEmpty())
					;
				else
					caseService.GetCaseList(tranInfo.getTranID(), panel.GetSearchCondition(),
							(PagingLoadConfig) loadConfig, callback);
			}
		};
		

		panel.setProxy(proxy);
		panel.setColumns(GetColumnConfig());
		panel.DrowGridView();

		bottomBar = new ConfigToolBar();
		bottomBar.initPageToolBar(panel.getLoader());
		
		Button caseStruct = new Button("案例结构维护");
		
		Menu menu = new Menu();
		MenuItem item = new MenuItem("下载案例数据");
		item.setId("CaseDataDownLoad");
		item.setIcon(MainPage.ICONS.WebDown());
		item.addSelectionListener(DownLoadDataHandler());
		menu.add(item);

		if(!isClientSimu){
			item = new MenuItem("上传案例数据");
			item.setId("CaseDataUpload");
			item.setIcon(MainPage.ICONS.WebUp());
			item.addSelectionListener(UploadDataHandler());
			menu.add(item);
		}
		caseStruct.setMenu(menu);
		bottomBar.AddMenuButton("caseStruct", caseStruct, MainPage.ICONS
				.PackStruct(), null);
		
		bottomBar.AddButton("btnCaseData",new Button("案例数据编辑"), MainPage.ICONS
				.CaseDataEdit(), CaseDataHandler());
		
		if(isClientSimu)
			bottomBar.AddButton("btnParamExpectedData",new Button("参数预期值编辑"), MainPage.ICONS
				.caseParam(), paramExpectedDataHandler());
		
//		if(isClientSimu)
//			bottomBar.AddButton("btnOracleData",new Button("预期结果编辑"), MainPage.ICONS
//				.CaseOracleData(), CaseOracleDataHandler());
//		if(isClientSimu)
//			bottomBar.AddButton("CaseExec",new Button("执行案例"), MainPage.ICONS
//					.Exec(), ExceHandler());
		
		addSetDefaultButton();
		bottomBar.AddWidget(new FillToolItem());
		//取消添加按钮
		if(!isClientSimu){
			bottomBar.AddNewBtn("btnAdd", AddHandler());
			bottomBar.AddEditBtn("btnEdit", EditHandler());
			bottomBar.AddDelBtn("btnDel", DelHandler());
		}
		InitBtnConfigBar(bottomBar);
		panel.setBottomBar(bottomBar);
		add(panel);

		detailPanel = new FormContentPanel<GWTCase>();
		detailPanel.setBindInfo(GetDetailHashMap());
		panel.setDetailForm(detailPanel);
		add(detailPanel);
	}

	private SelectionListener<ButtonEvent> paramExpectedDataHandler() {
	// TODO Auto-generated method stub
		return new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				// TODO Auto-generated method stub
				final Window window = new Window();
				window.setSize(600, 550);
				window.setLayout(new FitLayout());
			    window.setPlain(true);  
			    window.setModal(true);  
			    final ContentPanel cp = new ContentPanel();  
			    cp.setFrame(true);  
			    cp.setLayout(new FitLayout());  
			    cp.setSize(600, 550);  
			    cp.add(getParamList());
			    cp.setHeaderVisible(false);
				window.add(cp);
				window.setHeading("案例参数预期值");					
				
				Button btnExport = new Button("导出");
				btnExport.addSelectionListener(new SelectionListener<ButtonEvent>() {

					@Override
					public void componentSelected(ButtonEvent ce) {
						// TODO Auto-generated method stub

							final FormPanel formPanel = new FormPanel();
							formPanel.setEncoding(FormPanel.Encoding.MULTIPART);
							formPanel.setMethod(FormPanel.Method.POST);
							formPanel.setAction("CaseExpectValueExport?"
											+ "caseID=" + panel.getSelection().get(0).GetCaseId()
											+ "&tranID=" + tranInfo.getTranID());
							formPanel.submit();
							
							formPanel.addListener(Events.Submit, new Listener<FormEvent>(){
								@Override
								public void handleEvent(FormEvent be) {
									// TODO Auto-generated method stub
									TESWindows.ShowDownLoad(be.getResultHtml());
								}						
							});
					}
				});
				window.addButton(btnExport);
				
				
				Button btnUpload = new Button("导入");
				btnUpload.addSelectionListener(new SelectionListener<ButtonEvent>() {

					@Override
					public void componentSelected(ButtonEvent ce) {
						// TODO Auto-generated method stub
						upWindow = new UploadWin();
						upWindow.Show(
								"上传案例预期值(.xls)",
								"正在上传案例预期值,请稍后……",
								"CaseExpectValueUpload?" + "caseID=" + panel.getSelection().get(0).GetCaseId()
								+ "&tranID=" + tranInfo.getTranID()+"&systemID=" + GetSystemID());
						
						upWindow.setWindowHideEvent( new Listener<BaseEvent>() {

							@Override
							public void handleEvent(BaseEvent be) {
								// TODO Auto-generated method stub
								
								sysParaService.GetCaseParamTree(panel.getSelection().get(0).GetCaseId(),tranInfo.getTranID(),new AsyncCallback<List<ModelData>>() {

									@Override
									public void onFailure(Throwable caught) {
										// TODO Auto-generated method stub
									}

									@Override
									public void onSuccess(List<ModelData> result) {
										// TODO Auto-generated method stub
										store.removeAll();
										store.add(result, true);
										treeGrid.expandAll();
										treeGrid.unmask();			
									}
									
								});
							}
							
						});
					}
					
				});
				window.addButton(btnUpload);
				
				Button btnOK = new Button("确定", new SelectionListener<ButtonEvent>() {

					@Override
					public void componentSelected(ButtonEvent ce) {
						// TODO Auto-generated method stub
						if(!changedIndex.isEmpty()) {
							List<GWTCaseParamExpectedValue> lst = new ArrayList<GWTCaseParamExpectedValue>();
							for(Integer index : changedIndex) {
								ModelData model = treeGrid.getStore().getAt(index);
								if(model instanceof GWTCaseParamExpectedValue)
									lst.add((GWTCaseParamExpectedValue)model);			
							}
							
							sysParaService.SaveCaseParaExpectedValue(lst, GetLoginLogID(), new AsyncCallback<Boolean>() {
	
								@Override
								public void onFailure(Throwable caught) {
									// TODO Auto-generated method stub
									MessageBox.alert("错误提示", "保存失败", null);
								}
	
								@Override
								public void onSuccess(Boolean result) {
									// TODO Auto-generated method stub
									window.hide();
								}
								
							});
						} else {
							window.hide();
						}
						
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

	
	//获取参数列表及预期值
	private EditorTreeGrid<ModelData> getParamList() {
		
		changedIndex = new HashSet<Integer>();
		
		final List<ColumnConfig> columns = new ArrayList<ColumnConfig>();
		ColumnConfig name = new ColumnConfig(GWTCaseParamExpectedValue.N_ParameterName, "参数名称", 160);
		name.setRenderer(new TreeGridCellRenderer<ModelData>());
		columns.add(name);
		columns.add(new ColumnConfig(GWTCaseParamExpectedValue.N_ParameterDesc, "参数描述", 100));
		columns.add(new ColumnConfig(GWTCaseParamExpectedValue.N_ParameterTypeStr, "参数类型", 100));
		
		ColumnConfig column = new ColumnConfig(GWTCaseParamExpectedValue.N_ExpectedValue, "参数预期值", 120);	
	    TextField<String> text = new TextField<String>();  
	    final CellEditor cellEditor = new CellEditor(text);
	    column.setEditor(cellEditor);
	    //对编辑过的参数预期值才进行保存
	    text.addListener(Events.Change, new Listener<FieldEvent>(){

			@Override
			public void handleEvent(FieldEvent be) {
				// TODO Auto-generated method stub
				int currentRow = cellEditor.row;
				changedIndex.add(currentRow);								
			}

		});	        
	    columns.add(column);
	    
	    CheckBox checkbox = new CheckBox();
	    final CellEditor checkBoxEditor = new CellEditor(checkbox);      
	   
	    final CheckColumnConfig checkColumn = new CheckColumnConfig(GWTCaseParamExpectedValue.N_ExpectedValueType, "变量？", 45) {
	    	 
	    	protected void onMouseDown(GridEvent<ModelData> ge) {
	    		 super.onMouseDown(ge);
	    		 int currentRow = ge.getRowIndex();
				 changedIndex.add(currentRow);
	    	 }
	    };  
	    checkColumn.setEditor(checkBoxEditor);  
	    
	    columns.add(checkColumn); 
	    	
		sysParaService.GetCaseParamTree(panel.getSelection().get(0).GetCaseId(),tranInfo.getTranID(),new AsyncCallback<List<ModelData>>() {

			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub			
			}

			@Override
			public void onSuccess(List<ModelData> result) {
				// TODO Auto-generated method stub
				store.removeAll();
				store.add(result, true);
				treeGrid.expandAll();
				treeGrid.unmask();			
			}
		});
		
		treeGrid = new EditorTreeGrid<ModelData>(store, new ColumnModel(columns));
		treeGrid.mask("加载中...");
		treeGrid.addPlugin(checkColumn); 
		treeGrid.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

		return treeGrid;
	}
	
	
	/**
	 * 获得Grid的列配置列表
	 * 
	 * @return Grid的列配置列表
	 */
	private List<ColumnConfig> GetColumnConfig() {
		List<ColumnConfig> columns = new ArrayList<ColumnConfig>();
		if(IsAdmin()){
			columns.add(new ColumnConfig(GWTCase.N_caseId, "caseId", 100));
		}
		columns.add(new ColumnConfig(GWTCase.N_caseNo, "编号", 100));
		columns.add(new ColumnConfig(GWTCase.N_caseName, "案例名称", 200));
		columns.add(new ColumnConfig(GWTCase.N_tranType, "交易类型", 100));
		if(isClientSimu){
			columns.add(new ColumnConfig(GWTCase.N_caseFlowNo, "用例编号", 100));
			columns.add(new ColumnConfig(GWTCase.N_caseFlowName, "用例名称", 100));
		}
	//	columns.add(new ColumnConfig(GWTCase.N_isParaseCHS, "可解析", 100));

		GridCellRenderer<GWTCase> change = new GridCellRenderer<GWTCase>() {

			public Object render(final GWTCase model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<GWTCase> store, Grid<GWTCase> grid) {
				String state = "组包";
				String color = "black";
				String tip = "预览组包结果";
				boolean enabled = true;
				if(model.GetCaseParse() == 0)
				{
					if(model.GetResponseData())
					{
						state = "已上传";
						color = "green";
						tip = "查看数据";
					}
					else{
						state = "未上传";
						color = "red";
						enabled = false;
					}
				}
				String iconID = "icon" + model.GetCaseId();
				
				HtmlContainer html = new HtmlContainer("<span>" +
						"<span style='float:left;width:16px;' id = '" + iconID + "' ></span>" +
						"<span style='float:left;color:" + color +"'>" + state + "</span>" +
						"</span>");
				if (enabled) {
					IconButton b = new IconButton("viewDetailIcon");
					b.setToolTip(tip);
					b.setEnabled(enabled);
					html.add(b, "#" + iconID);
					b.addSelectionListener(new SelectionListener<IconButtonEvent>(){
						@Override
						public void componentSelected(IconButtonEvent ce) {
							ReusltFactory result = new ReusltFactory(model.GetCaseId());
							result.setIsClientSimu(TypeTranslate.BooleanToInt(isClientSimu));
							result.setCharSet(isClientSimu?GetSysInfo().GetEncoding4RequestMsg()
									:GetSysInfo().GeteEnoding4ResponseMsg());
							result.Show();
//							if(model.GetResponseData())
//							{
//								ShowResponseData(model.GetCaseId());
//							}
//							else if(model.GetCaseParse() == 1)
//							{
//								TESWindows.ShowMsgView();
//							}
						}
					});
				}
				return  html;
			}
			
			
		};
		ColumnConfig column = new ColumnConfig("caseData", "案例数据", 100);
		column.setAlignment(HorizontalAlignment.LEFT);
		column.setSortable(false);
		column.setResizable(false);
		column.setRenderer(change);
		columns.add(column);
		if(!isClientSimu){
			GridCellRenderer<GWTCase> defaults = new GridCellRenderer<GWTCase>() {
				@Override
				public Object render(final GWTCase model, String property,
						ColumnData config, int rowIndex, int colIndex,
						ListStore<GWTCase> store, Grid<GWTCase> grid) {
					if(model.GetDefaultBool())
					{
						return "<div class = \"rowIcon defalutCase\" title = \"当前案例为默认案例\" ></div>";
					}
					return "";
				}
			};
			ColumnConfig cDefault = new ColumnConfig(GWTCase.N_default, "默认响应", 70);
			cDefault.setAlignment(HorizontalAlignment.CENTER);
			cDefault.setSortable(false);
			cDefault.setResizable(false);
			cDefault.setRenderer(defaults);
			columns.add(cDefault);
		}
		return columns;
	}

	/**
	 * 获得详细信息绑定的Hash列表
	 * 
	 * @return Map<String,String> 对应 Map<对应绑定的值名称,字段显示名称>
	 */
	public Map<String, String> GetDetailHashMap() {
		Map<String, String> detailMap = new LinkedHashMap<String, String>();
		detailMap.put(GWTCase.N_caseNo, "案例编号");
		detailMap.put(GWTCase.N_caseName, "案例名称");
		detailMap.put(GWTCase.N_tranType, "交易类型");
		if(isClientSimu){
			detailMap.put(GWTCase.N_caseFlowNo, "用例编号");
			detailMap.put(GWTCase.N_caseFlowName, "用例名称");
		}
		detailMap.put(GWTCase.N_Desc, "备注");
		return detailMap;
	}
	
	/**
	 * 添加和编辑案例对话框(最低版本)
	 */
	private void CreateEditFormSimple() {
		final Window window = new Window();

		window.setSize(350, 130);
		window.setPlain(true);
		window.setModal(true);
		window.setBlinkModal(true);
		window.setBlinkModal(false);
		window.setLayout(new FitLayout());

		final PostPanelAsync formPanel = new PostPanelAsync();
		// 设置样式
		formPanel.setBorders(false);
		formPanel.setBodyBorder(false);
		formPanel.setLabelWidth(55);
		formPanel.setPadding(5);
		formPanel.setHeaderVisible(false);

		String labelStyle = "width:70px;";
		FormData formdata = new FormData("93%");

//		final TextField tfCaseName = new TextField(EditCase, EditCase
//				.GetCaseName(), "案例名称");
		final TextField<String> tfCaseName = new TextField<String>();
		tfCaseName.setFieldLabel("案例名称");
		tfCaseName.setName("CaseName");
		tfCaseName.setMaxLength(32);
		tfCaseName.setLabelStyle(labelStyle);
		formPanel.add(tfCaseName, formdata);
	
		final FileUploadField fpRespContent = new FileUploadField();
		fpRespContent.setLabelStyle(labelStyle);
		fpRespContent.setFieldLabel("响应报文");
		fpRespContent.setName("respfile");
		formPanel.add(fpRespContent, formdata);
		
		formPanel.addSubmitCompleteHandler(new PostPanelAsync.SubmitCompleteHandler() {
				@SuppressWarnings("deprecation")
				public void onSubmitComplete(SubmitCompleteEvent event) {
					String returnMsg = event.getResults();
					if(returnMsg.isEmpty())
					{
						panel.loaderReLoad(EditCase.IsNew());
						window.close();
					}
					else
					{
						//tfCaseName.EnforceValidate();
						panel.loaderReLoad(EditCase.IsNew());
						MessageBox.alert("友情提示", event.getResults(), null);
						
					}
				}
			});
		
		Button btnOK = new Button("确定", new SelectionListener<ButtonEvent>() {
			public void componentSelected(ButtonEvent ce) {
				if (!formPanel.isValid())
					return;
				
				formPanel.setSubmitMsg(fpRespContent.getValue() != null ? "保存案例信息，并且上传案例文件,请稍后..." : "保存案例信息,请稍后...");
				formPanel.setEncoding(FormPanel.Encoding.MULTIPART);
				formPanel.setMethod(FormPanel.Method.POST);
				formPanel.setAction("CaseRespServletUpload?" 
						+ GWTCase.N_caseId + "=" + EditCase.GetCaseId() + "&"
						+ GWTCase.N_caseName + "=" + tfCaseName.getValue() + "&"
						+ GWTCase.N_isParseable + "=" + 0 + "&"
						+ GWTCase.N_transactionId + "=" + EditCase.GetTransactionID()
						);
				formPanel.submit();
			}
		});
		window.addButton(btnOK);
		
		window.addButton(new Button("取消", new SelectionListener<ButtonEvent>() {
			@SuppressWarnings("deprecation")
			public void componentSelected(ButtonEvent ce) {
				window.close();
			}
		}));
		window.add(formPanel);
		if (EditCase.IsNew()) {
			window.setHeading("新增案例信息");
		} else {
			tfCaseName.setValue(EditCase.GetCaseName());
			window.setHeading("编辑案例信息");
		}
		window.show();
	}
	
	private void CreateEditFormComplex() {
		final Window window = new Window();

		window.setSize(350, 200);
		window.setPlain(true);
		window.setModal(true);
		window.setBlinkModal(true);
		window.setBlinkModal(false);
		window.setLayout(new FitLayout());

		final FormPanel formPanel = new FormPanel();
		// 设置样式
		formPanel.setBorders(false);
		formPanel.setBodyBorder(false);
		formPanel.setLabelWidth(55);
		formPanel.setPadding(5);
		formPanel.setHeaderVisible(false);

		String labelStyle = "width:70px;";
		FormData formdata = new FormData("93%");
        
		final TextField<String> tfCaseNo = new TextField<String>();
		tfCaseNo.setFieldLabel("案例编号");
		tfCaseNo.setName("CaseNo");
		tfCaseNo.setMaxLength(32);
		tfCaseNo.setLabelStyle(labelStyle);
		formPanel.add(tfCaseNo, formdata);
		
//		final DistTextField tfCaseName = new DistTextField(EditCase, EditCase
//				.GetCaseName(), "案例名称", "该交易已存在该名称的案例，请重命名");
		final TextField<String> tfCaseName = new TextField<String>();
		tfCaseName.setFieldLabel("案例名称");
		tfCaseName.setName("CaseName");
		tfCaseName.setMaxLength(32);
		tfCaseName.setLabelStyle(labelStyle);
		formPanel.add(tfCaseName, formdata);

		boolean valueSet = EditCase.GetCaseParse() == 1;
		final RadioGroup typeGroup = new RadioGroup();
		typeGroup.setFieldLabel("案例数据");
		Radio dataType = new Radio();
		dataType.setBoxLabel("按报文结构填写");
		dataType.setValueAttribute("1");
		dataType.setValue(valueSet);
		typeGroup.add(dataType);
		
		dataType = new Radio();
		dataType.setBoxLabel("上传响应报文");
		dataType.setValueAttribute("0");
		dataType.setValue(!valueSet);
		typeGroup.add(dataType);
		
		typeGroup.setStyleAttribute("margin-left", "10px");
		formPanel.add(typeGroup, formdata);
		
		final FileUploadField fpRespContent = new FileUploadField();
		fpRespContent.setLabelStyle(labelStyle);
		fpRespContent.setFieldLabel("响应报文");
		fpRespContent.setName("respfile");
		fpRespContent.setReadOnly(valueSet);
		formPanel.add(fpRespContent, formdata);
		typeGroup.addListener(Events.Change , new Listener<FieldEvent>(){
					@Override
					public void handleEvent(FieldEvent be) {
						if(typeGroup.getValue().getValueAttribute().compareTo("0") == 0){
							fpRespContent.setReadOnly(false);
							fpRespContent.setValue("");
						}
						else{
							fpRespContent.setReadOnly(true);
						}
					}
				});
		
		Button btnOK = new Button("确定", new SelectionListener<ButtonEvent>() {
			public void componentSelected(ButtonEvent ce) {
				if (!formPanel.isValid())
					return;
				
				mask(fpRespContent.getValue() != null ? "保存案例信息，并且上传案例文件,请稍后..." : "保存案例信息,请稍后...");
				PostFormPanel postPanel = new PostFormPanel();
				postPanel.setEncoding(FormPanel.Encoding.MULTIPART);
				postPanel.setMethod(FormPanel.Method.POST);
				postPanel.setAction("CaseRespServletUpload?" 
						+ GWTCase.N_caseNo + "=" + tfCaseNo.getValue()	+ "&"
						+ GWTCase.N_caseId + "=" + EditCase.GetCaseId() + "&"
						+ GWTCase.N_caseName + "=" + tfCaseName.getValue() + "&"
						+ GWTCase.N_isParseable + "=" + typeGroup.getValue().getValueAttribute() + "&"
						+ GWTCase.N_transactionId + "=" + EditCase.GetTransactionID()
						);
				postPanel.submit();
				postPanel.addListener(Events.Submit, new Listener<FormEvent>() {

					@Override
					public void handleEvent(FormEvent fe) {
						// TODO Auto-generated method stub
						unmask();
						String returnMsg = fe.getResultHtml();
						if(returnMsg.isEmpty())
						{
							panel.loaderReLoad(EditCase.IsNew());
							window.hide();
						}
						else
						{
						//	tfCaseName.EnforceValidate();
							panel.loaderReLoad(EditCase.IsNew());
							MessageBox.alert("友情提示", fe.getResultHtml(), null);
							
						}
					}
				});
				window.hide();
			}
		});
		window.addButton(btnOK);
		
		window.addButton(new Button("取消", new SelectionListener<ButtonEvent>() {
			public void componentSelected(ButtonEvent ce) {			
				window.hide();
			}
		}));
		
		window.add(formPanel);
		
		if (EditCase.IsNew()) {
			window.setHeading("新增案例信息");
		} else {
			tfCaseNo.setValue(EditCase.GetCaseNo());
			tfCaseName.setValue(EditCase.GetCaseName());
			window.setHeading("编辑案例信息");
		}
		
		window.show();
	}
	
	/**
	 * 添加和编辑案例对话框
	 */
	private void CreateEditForm() {
		if(AppContext.GetVersion() == VersionType.Pstub)
			CreateEditFormSimple();
		else
			CreateEditFormComplex();
	}
	
//	if (!EditCase.IsNew() && !valueSet && EditCase.GetResponseData()) {
//	Button btnView = new Button("查看数据");
//	btnView.addSelectionListener(new SelectionListener<ButtonEvent>() {
//		@Override
//		public void componentSelected(ButtonEvent ce) {
//			ShowResponseData(EditCase.GetCaseId());
//		}
//	});
//	window.addButton(btnView);
//}
		
	/**
	 * 获得添加案例的Listener,并且打开添加对话框
	 * @return 添加案例的Listener
	 */
	private SelectionListener<ButtonEvent> AddHandler() {
		return new SelectionListener<ButtonEvent>() {
			public void componentSelected(ButtonEvent ce) {
				EditCase = new GWTCase(tranInfo.getTranID());
				CreateEditForm();
			}
		};
	}

	/**
	 * 获得编辑案例的Listener,并且打开编辑对话框
	 * @return 编辑案例的Listener
	 */
	private SelectionListener<ButtonEvent> EditHandler() {
		return new SelectionListener<ButtonEvent>() {
			public void componentSelected(ButtonEvent ce) {
				List<GWTCase> selectedItems = panel.getDataGrid()
						.getSelectionModel().getSelectedItems();
				if (selectedItems.size() != 1) {
					MessageBox.alert("Alert", "请选择一个案例进行编辑", null);
					return;
				}
				EditCase = selectedItems.get(0);
				CreateEditForm();
			}
		};
	}
	
	/**
	 * 获得打开案例数据编辑的Tab页面的Listener，并且打开tab页
	 * @return 案例数据编辑的Tab页面的Listener
	 */
	private SelectionListener<ButtonEvent> CaseDataHandler() {
		return new SelectionListener<ButtonEvent>() {
			public void componentSelected(ButtonEvent ce) {
				GWTCase selectedItem = panel.getDataGrid()
						.getSelectionModel().getSelectedItems().get(0);
				if(selectedItem.GetCaseParse() == 0)
				{
					MessageBox.alert("友情提示", "非组包方式的案例无法进行案例数据编辑", null);
					return;
				}
				String caseId = selectedItem.GetCaseId();
				String tabId = caseId + "caseDataEdit";
				String tabTitle = "案例数据编辑["
						+ selectedItem.GetCaseName() + "]";
				CaseDataPage page = new CaseDataPage(caseId,selectedItem.GetCaseName(),tranInfo,isClientSimu,true);
				AppContext.GetEntryPoint().AddTabItem(tabId, tabTitle, page);
			}
		};
	}
	
	/**
	 * 获得打开预期结果编辑的Tab页面的Listener，并且打开tab页
	 * @return 预期结果编辑的Tab页面的Listener
	 */
	private SelectionListener<ButtonEvent> CaseOracleDataHandler() {
		return new SelectionListener<ButtonEvent>() {
			public void componentSelected(ButtonEvent ce) {
				GWTCase selectedItem = panel.getDataGrid()
						.getSelectionModel().getSelectedItems().get(0);
				String caseId = selectedItem.GetCaseId();
				String tabId = caseId + "oracleDataEdit";
				String tabTitle = "预期结果编辑["
						+ selectedItem.GetCaseName() + "]";
				CaseDataPage page = new CaseDataPage(caseId,selectedItem.GetCaseName(),tranInfo,isClientSimu,false);
				AppContext.GetEntryPoint().AddTabItem(tabId, tabTitle, page);
			}
		};
	}
	
	private SelectionListener<ButtonEvent> ExceHandler() {
		return new SelectionListener<ButtonEvent>() {
			public void componentSelected(ButtonEvent ce) {
				ResultCompare resultWin = new ResultCompare();
				GWTCase selectedItem = panel.getDataGrid()
				.getSelectionModel().getSelectedItems().get(0);
				resultWin.Show(GetSysInfo(),GetUserID(), tranInfo.getTranID(), selectedItem.GetCaseId(),GetSelf());
			}
		};
	}
	
	private SelectionListener<MenuEvent> UploadDataHandler() {
		return new SelectionListener<MenuEvent>() {
			public void componentSelected(MenuEvent ce) {
				upWindow = new UploadWin(panel.getLoader());
				upWindow.Show("上传案例数据(.xls)", 
						"正在上传案例数据,请稍后……",
						"CaseStructServletUpload?type=multi" +
						"&sysId=" + GetSystemID() 
						+ "&isClientSimu=" + tranInfo.GetMode()
						+ "&tranId=" + tranInfo.getTranID());
			}
		};
	}
	
	private SelectionListener<MenuEvent> DownLoadDataHandler() {
		return new SelectionListener<MenuEvent>() {
			public void componentSelected(MenuEvent ce) {
				final PostPanelAsync formPanel = new PostPanelAsync(false);
				formPanel.setSubmitMsg("正在获取案例数据,请稍后...");
				
				String caseIDs = "";
				for(GWTCase caseInfo : panel.getSelection())
				{
//					if(caseInfo.GetCaseParse() == 0)
//					{
//						MessageBox.alert("友情提示", "只有通过组包方式的案例才能支持下载", null);
//						return;
//					}
					caseIDs += caseInfo.GetCaseId() + ",";
				}
				caseIDs = caseIDs.substring(0,caseIDs.length() - 1);
				
				formPanel.setAction("CaseStructServletDownLoad?type=multi" +
						"&isClientSimu=" + TypeTranslate.BooleanToInt(isClientSimu) + 
						"&caseId=" + caseIDs);
				formPanel.setEncoding(FormPanel.Encoding.MULTIPART);
				formPanel.setMethod(FormPanel.Method.POST);
				
				formPanel.addSubmitCompleteHandler(new PostPanelAsync.SubmitCompleteHandler() {
					public void onSubmitComplete(SubmitCompleteEvent event) {
							TESWindows.ShowDownLoad(event.getResults());
						}
				});
				formPanel.submit();
			}
		};
	}

	/**
	 * 删除案例的Listener
	 * 提示 是否删除
	 * 是   执行删除操作
	 * 否   返回
	 * @return 删除案例的Listener
	 */
	private Listener<MessageBoxEvent> DelHandler() {
		return new Listener<MessageBoxEvent>() {
			public void handleEvent(MessageBoxEvent be) {
				Button msgBtn = be.getButtonClicked();
				if (msgBtn.getText().equalsIgnoreCase("Yes")) {
					caseService.DeleteCase(GetSysInfo(),panel.getSelection(), GetLoginLogID(),
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
	
	private void addSetDefaultButton(){
		if(!isClientSimu)
			bottomBar.AddButton("btnDefault",new Button("设为默认案例"),ICONS.defaultCase(),new SelectionListener<ButtonEvent>(){
				@Override
				public void componentSelected(ButtonEvent ce) {
					final GWTCase editCase = panel.getSelection().get(0);
					if(editCase.GetDefaultBool())
						return;
					else
					{
						caseService.SetDefaultCase(editCase.GetTransactionID(), editCase.GetCaseId(), new AsyncCallback<String>()
								{
									@Override
									public void onFailure(Throwable caught) {
										MessageBox.alert("错误提示", "服务器通讯失败", null);
									}
	
									@Override
									public void onSuccess(String result) {
										if(result.isEmpty())
										{
											panel.reloadGrid();
										}
										else 
											MessageBox.alert("错误提示",result, null);
									}
									
								});
					}
			}});
	}
}
