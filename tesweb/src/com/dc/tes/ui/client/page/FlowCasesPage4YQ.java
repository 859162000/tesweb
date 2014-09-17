package com.dc.tes.ui.client.page;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.dc.tes.ui.client.IBatchService;
import com.dc.tes.ui.client.IBatchServiceAsync;
import com.dc.tes.ui.client.ICaseService;
import com.dc.tes.ui.client.ICaseServiceAsync;
import com.dc.tes.ui.client.ISysDynamicParameter;
import com.dc.tes.ui.client.ISysDynamicParameterAsync;
import com.dc.tes.ui.client.MainPage;
import com.dc.tes.ui.client.common.ServiceHelper;
import com.dc.tes.ui.client.control.ConfigToolBar;
import com.dc.tes.ui.client.control.FormContentPanel;
import com.dc.tes.ui.client.control.GridContentPanel;
import com.dc.tes.ui.client.control.PostFormPanel;
import com.dc.tes.ui.client.model.GWTCase;
import com.dc.tes.ui.client.model.GWTCaseFlow;
import com.dc.tes.ui.client.model.GWTCaseParamExpectedValue;
import com.dc.tes.ui.client.model.GWTTransaction;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.Style.SelectionMode;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.extjs.gxt.ui.client.data.RpcProxy;
import com.extjs.gxt.ui.client.dnd.DND;
import com.extjs.gxt.ui.client.dnd.GridDragSource;
import com.extjs.gxt.ui.client.dnd.GridDropTarget;
import com.extjs.gxt.ui.client.dnd.DND.Operation;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.DNDEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.event.FormEvent;
import com.extjs.gxt.ui.client.event.GridEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.store.TreeStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.TabItem;
import com.extjs.gxt.ui.client.widget.TabPanel;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.TextArea;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.extjs.gxt.ui.client.widget.grid.CellEditor;
import com.extjs.gxt.ui.client.widget.grid.CheckColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.extjs.gxt.ui.client.widget.toolbar.FillToolItem;
import com.extjs.gxt.ui.client.widget.treegrid.EditorTreeGrid;
import com.extjs.gxt.ui.client.widget.treegrid.TreeGridCellRenderer;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;


public class FlowCasesPage4YQ extends BasePage{
	private IBatchServiceAsync batchService = null;
	private ICaseServiceAsync caseService= null;
	private ISysDynamicParameterAsync sysParaService = null;
	private GWTCaseFlow caseFlow;
	private TabPanel tabPanel;
//	private String batchNo;
	public FlowCasesPage4YQ(TabPanel tp, GWTCaseFlow gwtCaseFlow) {
		// TODO Auto-generated constructor stub
		caseFlow = gwtCaseFlow;
//		batchNo = gwtCaseFlow.GetImportBatchNo();
		tabPanel = tp;
	}
	/**
	 * 列表控件
	 */
	GridContentPanel<GWTCase> panel;
	/**
	 * 详细信息控件
	 */
	FormContentPanel<GWTCase> detailPanel;
	
	/**
	 * 参数预期值列表控件
	 */
	EditorTreeGrid<ModelData> treeGrid; 
	
	Set<Integer> changedIndex;
	/**
	 * 工具条
	 */
	ConfigToolBar bottomBar;
	/**
	 * 正在编辑的案例
	 */
	
	UploadWin upWindow;
	
	/**
	 * 案例预期值的store
	 */
	TreeStore<ModelData> store = new TreeStore<ModelData>();
	
	private GWTCase EditCase = null;

	@Override
	protected void onRender(Element parent, int index) {
		super.onRender(parent, index);
		panel = new GridContentPanel<GWTCase>();
		batchService = ServiceHelper.GetDynamicService("batchNo", IBatchService.class);
		caseService = ServiceHelper.GetDynamicService("case", ICaseService.class);
		sysParaService = ServiceHelper.GetDynamicService("sysPara", ISysDynamicParameter.class);
		
		RpcProxy<PagingLoadResult<GWTCase>> proxy = new RpcProxy<PagingLoadResult<GWTCase>>() {
			@Override
			public void load(Object loadConfig,
					AsyncCallback<PagingLoadResult<GWTCase>> callback) {
				batchService.GetFlowCases(caseFlow, (PagingLoadConfig) loadConfig,
						callback);
			}						
		};
		panel.setProxy(proxy);
		panel.setColumns(GetColumnConfig());
		panel.DrowGridView();
		new GridDragSource(panel.getDataGrid());
		GridDropTarget target = new GridDropTarget(panel.getDataGrid()){
			@Override
			protected void onDragDrop(DNDEvent e) {
				// TODO Auto-generated method stub
				super.onDragDrop(e);
				handleSaveSequence();
			}
		};
		target.setFeedback(DND.Feedback.INSERT);
		target.setAllowSelfAsSource(true);
		target.setOperation(Operation.MOVE);
		
		bottomBar = new ConfigToolBar();
		bottomBar.initPageToolBar(panel.getLoader());
		bottomBar.AddButton("btnCaseData",new Button("步骤报文编辑"), MainPage.ICONS
				.CaseDataEdit(), CaseDataHandler());
		Button btnEditParamExpectedValue = new Button("参数预期值编辑");
		bottomBar.AddButton("btnParamExpectedData", btnEditParamExpectedValue, MainPage.ICONS
				.caseParam(), paramExpectedDataHandler());
		Button btnEditExpectedMsgFields = new Button("参数预期值编辑");
		bottomBar.AddButton("btnOracleData", btnEditExpectedMsgFields, MainPage.ICONS
				.CaseOracleData(), CaseOracleDataHandler());
//		bottomBar.AddButton("caseExec",new Button("执行步骤"), MainPage.ICONS
//				.Exec(), ExecHandler());		
		bottomBar.AddWidget(new FillToolItem());
		bottomBar.AddNewBtn("btnAdd", AddHandler());
		bottomBar.AddEditBtn("btnEdit", EditHandler());
		bottomBar.AddDelBtn("btnDel", DelHandler());
		InitBtnConfigBar(bottomBar);
		panel.setBottomBar(bottomBar);
		add(panel);

		detailPanel = new FormContentPanel<GWTCase>();
		detailPanel.setBindInfo(GetDetailHashMap());
		panel.setDetailForm(detailPanel);
		add(detailPanel);
		addDoubleClickListener(panel);

//		if (iIsClientSimu == 0) { //接收端
//			btnEditParamExpectedValue.setVisible(false);
//			btnEditExpectedMsgFields.setVisible(false);
//		}
//		else {
//			btnEditParamExpectedValue.setVisible(true);
//			btnEditExpectedMsgFields.setVisible(true);
//		}
	}
	
	protected void handleSaveSequence() {
		// TODO Auto-generated method stub
		List<GWTCase> cases = new ArrayList<GWTCase>();
		for(int i = 0; i < panel.getStore().getCount(); i++){
			GWTCase gwtCase = panel.getStore().getAt(i);
			gwtCase.set(GWTCase.N_Sequence, String.valueOf(i));
			cases.add(gwtCase);
		}
		caseService.updateCaseSequence(cases, new AsyncCallback<Void>() {

			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub
				caught.printStackTrace();
				MessageBox.alert("错误提示", "保存步骤顺序失败，请联系管理员！", null);
			}

			@Override
			public void onSuccess(Void result) {
				// TODO Auto-generated method stub	
				panel.reloadGrid();
			}
		});
	}
	
	private void addDoubleClickListener(final GridContentPanel<GWTCase> panel1) {
		// TODO Auto-generated method stub
		panel.getDataGrid().addListener(Events.CellDoubleClick, new Listener<GridEvent<GWTCase>>() {

			@Override
			public void handleEvent(final GridEvent<GWTCase> ce) {
				if(ce.getColIndex()==6){
					MessageBox.confirm("提示", "是否改变该步骤的断点标识？", new Listener<MessageBoxEvent>() {
	
						@Override
						public void handleEvent(MessageBoxEvent be) {
							// TODO Auto-generated method stub
							if(be.getButtonClicked().getText().equalsIgnoreCase("yes")){
								caseService.ChangeBreakPointFlag(ce.getModel(), new AsyncCallback<Boolean>() {
									
									@Override
									public void onFailure(Throwable caught) {
										// TODO Auto-generated method stub
										MessageBox.alert("错误提示", "改变断点标记失败", null);
									}
		
									@Override
									public void onSuccess(Boolean result) {
										// TODO Auto-generated method stub
										panel1.reloadGrid();
									}
									
								});
							}
						}
					});
				}else{
					openCaseDataPage();
				}
				
			}
		});
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
				    ContentPanel cp = new ContentPanel();  
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

								final PostFormPanel formPanel = new PostFormPanel();
								formPanel.setEncoding(FormPanel.Encoding.MULTIPART);
								formPanel.setMethod(FormPanel.Method.POST);
								formPanel.setAction("CaseExpectValueExport?"
												+ "caseID=" + panel.getSelection().get(0).GetCaseId()
												+ "&tranID=" + panel.getSelection().get(0).getN_tran().getTranID());
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
									+ "&tranID=" + panel.getSelection().get(0).getN_tran().getTranID()+"&systemID=" + GetSystemID());
							
							upWindow.setWindowHideEvent( new Listener<BaseEvent>() {

								@Override
								public void handleEvent(BaseEvent be) {
									// TODO Auto-generated method stub
									
									sysParaService.GetCaseParamTree(panel.getSelection().get(0).GetCaseId(),panel.getSelection().get(0).getN_tran().getTranID(),new AsyncCallback<List<ModelData>>() {

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
								
								sysParaService.SaveCaseParaExpectedValue(lst, GetLoginLogID(), 
										new AsyncCallback<Boolean>() {
		
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
    
		sysParaService.GetCaseParamTree(panel.getSelection().get(0).GetCaseId(),panel.getSelection().get(0).getN_tran().getTranID(),new AsyncCallback<List<ModelData>>() {

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
	 * 获得添加案例的Listener,并且打开添加对话框
	 * @return 添加案例的Listener
	 */
	private SelectionListener<ButtonEvent> AddHandler() {
		return new SelectionListener<ButtonEvent>() {
			public void componentSelected(ButtonEvent ce) {
				EditCase = new GWTCase("");
		//		EditCase.SetImportBatchNo(batchNo);
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
					MessageBox.alert("提示", "请选择一个步骤进行编辑", null);
					return;
				}
				EditCase = selectedItems.get(0);
				CreateEditForm();
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
	public Map<String, String> GetDetailHashMap() {
		Map<String, String> detailMap = new LinkedHashMap<String, String>();
		detailMap.put(GWTCase.N_caseNo, "步骤编号");
		detailMap.put(GWTCase.N_caseName, "步骤名称");
		detailMap.put(GWTCase.N_tranType, "交易类型");
		detailMap.put(GWTCase.N_Desc, "备注");
		detailMap.put(GWTCase.N_Sequence, "执行次序");
		detailMap.put(GWTCase.N_BreakPointFlagStr, "断点设置");
		return detailMap;
	}
	
	
	
	private ColumnConfig GetRenderColumn(String id, String name,
			int width) {
		GridCellRenderer<GWTCase> gridRender = new GridCellRenderer<GWTCase>(){
			@Override
			public Object render(GWTCase model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<GWTCase> store, Grid<GWTCase> grid) {
				// TODO Auto-generated method stub
				if(model.getBreakPointFlag().equals("0")){
					return "<span style='color:" + "black" + "'>" + model.getBreakPointFlagStr() + "</span>";
				}else{
					return "<span style='color:" + "red" + "'>" + model.getBreakPointFlagStr() + "</span>";
				}
			}		
		};
		ColumnConfig columnConfig = new ColumnConfig(id, name, width);
		columnConfig.setRenderer(gridRender);
		return columnConfig;
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
		columns.add(new ColumnConfig(GWTCase.N_caseName, "步骤名称", 200));
		columns.add(new ColumnConfig(GWTCase.N_tranType, "交易类型", 200));
		columns.add(new ColumnConfig(GWTCase.N_Sequence, "执行次序", 60));
		columns.add(GetRenderColumn(GWTCase.N_BreakPointFlagStr, "断点设置", 60));
		
		return columns;
	}
	
//	private SelectionListener<ButtonEvent> ExecHandler() {
//		return new SelectionListener<ButtonEvent>() {
//			public void componentSelected(ButtonEvent ce) {
//				ResultCompare resultWin = new ResultCompare();
//				GWTCase selectedItem = panel.getDataGrid()
//				.getSelectionModel().getSelectedItems().get(0);
//				resultWin.Show(GetSysInfo(), GetUserID(), selectedItem.GetTransactionID(), selectedItem.GetCaseId(),GetSelf());
//			}
//		};
//	}
	
	
	
	private void CreateEditForm() {
		final Window window = new Window();

		window.setSize(400, 240);
		window.setPlain(true);
		window.setModal(true);
		window.setBlinkModal(true);
		window.setBlinkModal(false);
		window.setLayout(new FitLayout());
			    

		final FormPanel formPanel = new FormPanel();
		// 设置样式
		formPanel.setBorders(false);
		formPanel.setBodyBorder(false);
		//formPanel.setLabelWidth(55);
		formPanel.setPadding(5);
		formPanel.setHeaderVisible(false);
		formPanel.setScrollMode(Scroll.AUTOY);

		FormData formdata = new FormData("90%");
		
//		final DistTextField tfCaseNo = new DistTextField(EditCase, EditCase
//				.GetCaseNo(), "步骤编号", "该用例已存在该编号的步骤，请重命名");
		final TextField<String> tfCaseNo = new TextField<String>();
		tfCaseNo.setFieldLabel("案例编号");
		tfCaseNo.setName(GWTCase.N_cardNo);
		tfCaseNo.setMaxLength(32);
		formPanel.add(tfCaseNo, formdata);
		
		final TextField<String> tfCaseName = new TextField<String>();
		tfCaseName.setName(GWTCase.N_caseName);
		tfCaseName.setFieldLabel("步骤名称");
		tfCaseName.setAllowBlank(false);
		tfCaseName.setValue(EditCase.GetCaseName());
		formPanel.add(tfCaseName, formdata);

		final ComboBox<GWTTransaction> tranBox = new ComboBox<GWTTransaction>();
		tranBox.setName(GWTCase.N_transactionId);
		tranBox.setFieldLabel("交易类型");
		tranBox.setAllowBlank(false);
		tranBox.setValueField(GWTTransaction.N_TransID);		
		tranBox.setDisplayField(GWTTransaction.N_TranName);
		tranBox.setTriggerAction(TriggerAction.ALL);
		tranBox.setMinChars(1);

		final ListStore<GWTTransaction> tranStore = new ListStore<GWTTransaction>(){
			//修改头匹配为模糊匹配
			@Override
			 public void applyFilters(String property) {
				    if (filters != null && filters.size() == 0) {
				      return;
				    }
				    filterProperty = property;
				    if (!filtersEnabled) {
				      snapshot = all;
				    }

				    filtersEnabled = true;
				    filtered = new ArrayList<GWTTransaction>();
				    for (GWTTransaction items : snapshot) {
				      if (filterBeginsWith != null) {
				        Object o = items.get(property);
				        if (o != null) {
				          if (!o.toString().toLowerCase().contains(filterBeginsWith.toLowerCase())) {
				            continue;
				          }
				        }
				      }
				      if (!isFiltered(items, property)) {
				        filtered.add(items);
				      }
				    }
				    all = filtered;

				    if (storeSorter != null) {
				      applySort(false);
				    }

				    fireEvent(Filter, createStoreEvent());
				  }
		};
		
		batchService.GetTranInfoList(GetSystemID(), new AsyncCallback<List<GWTTransaction>>() {

			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub
				caught.printStackTrace();
				MessageBox.alert("错误提示","获取交易列表失败", null);
			}

			@Override
			public void onSuccess(List<GWTTransaction> result) {
				// TODO Auto-generated method stub
				tranStore.add(result);

			}
		});
		
		tranBox.setStore(tranStore);
		final GWTTransaction transaction = new GWTTransaction(EditCase.GetTransactionID(), GetSystemID(), 0, "", EditCase.GetTranType(), "", 0, "", "", "");
		tranBox.setValue(transaction);
		tranBox.setLazyRender(false);
		formPanel.add(tranBox, formdata);
		
//		final TextField<String> tfSequence = new TextField<String>();
//		tfSequence.setName(GWTCase.N_Sequence);
//		tfSequence.setFieldLabel("执行次序");
//		tfSequence.setAllowBlank(false);
//		tfSequence.setRegex("^[0-9]*$");
//		tfSequence.getMessages().setRegexText("只能输入数字");
//		tfSequence.setValue(EditCase.GetSequence());
//		formPanel.add(tfSequence, formdata);
		
		final TextArea taDesc = new TextArea();
		taDesc.setFieldLabel("描述");
		taDesc.setPreventScrollbars(true);
		taDesc.setName(GWTCase.N_Desc);
		taDesc.setValue(EditCase.GetDesc());
		formPanel.add(taDesc, formdata);

		Button btnOK = new Button("确定", new SelectionListener<ButtonEvent>() {
			private void saveCase(){
				String sequence = EditCase.IsNew()?String.valueOf(panel.getStore().getCount()):EditCase.GetSequence();
				EditCase.SetEditValue(tfCaseNo.getValue(), tfCaseName.getValue(), tranBox.getValue().getTranID(),
						sequence, taDesc.getValue(),  caseFlow);
				caseService.SaveOrUpdateCase(EditCase, GetLoginLogID(), new AsyncCallback<Boolean>() {

					@Override
					public void onFailure(Throwable caught) {
						// TODO Auto-generated method stub
						caught.printStackTrace();
						MessageBox.alert("错误提示", "保存案例信息失败！", null);
					}
	
					@Override
					public void onSuccess(Boolean result) {
						// TODO Auto-generated method stub
						if(result){
							panel.loaderReLoad(EditCase.IsNew());
							window.hide();
						}else{
							//tfCaseNo.EnforceValidate();
							panel.loaderReLoad(EditCase.IsNew());
						}
					}
				});
			}
			public void componentSelected(ButtonEvent ce) {
				if (!formPanel.isValid())
					return;
				//当编辑案例时交易类型发生改变，提示是否确认修改。
				if(!EditCase.IsNew() && !EditCase.GetTransactionID().equals(tranBox.getValue().getTranID())){
					MessageBox.confirm("提示", "交易类型发生变化，确定修改将删除原有报文数据与参数预期值，是否继续？", 
							new Listener<MessageBoxEvent>() {

						@Override
						public void handleEvent(MessageBoxEvent be) {
							// TODO Auto-generated method stub
							Button msgBtn = be.getButtonClicked();
							if (msgBtn.getText().equalsIgnoreCase("Yes")) {
								saveCase();
							}else{
								tranBox.setValue(transaction);
							}
						}
					});
				}else{			
					saveCase();
				}
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
			window.setHeading("新增步骤信息");
		} else {
			tfCaseNo.setValue(EditCase.GetCaseNo());
			window.setHeading("编辑步骤信息");
		}
		window.show();
		
	}
	
	/**
	 * 获得打开案例数据编辑的Tab页面的Listener，并且打开tab页
	 * @return 案例数据编辑的Tab页面的Listener
	 */
	private SelectionListener<ButtonEvent> CaseDataHandler() {
		return new SelectionListener<ButtonEvent>() {
			public void componentSelected(ButtonEvent ce) {
				openCaseDataPage();
			}
		};
	}
	protected void openCaseDataPage() {
		// TODO Auto-generated method stub
		GWTCase selectedItem = panel.getDataGrid()
				.getSelectionModel().getSelectedItems().get(0);
		if(selectedItem.GetCaseParse() == 0)
		{
			MessageBox.alert("友情提示", "非组包方式的案例无法进行案例数据编辑", null);
			return;
		}
		String caseId = selectedItem.GetCaseId();
		TabItem tabItem = new TabItem("步骤报文编辑["
				+ selectedItem.GetCaseName() + "]");
		tabItem.setId(caseId + "caseDataEdit");
		tabItem.setClosable(true);
		tabItem.setLayout(new FitLayout());
		tabItem.setBorders(false);
		//tabItem.setScrollMode(Scroll.AUTO);
		BasePage page = new CaseDataPage(caseId,selectedItem.GetCaseName(),selectedItem.getN_tran(), true , true);
		tabItem.add(page);
		if(tabPanel!=null){
			if(tabPanel.getItemByItemId(tabItem.getId()) == null)
				tabPanel.add(tabItem);
			tabPanel.setSelection(tabPanel.getItemByItemId(tabItem.getId()));
		}
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
				TabItem tabItem = new TabItem("预期结果编辑["
						+ selectedItem.GetCaseName() + "]");
				tabItem.setId(caseId + "oracleDataEdit");
				tabItem.setClosable(true);
				tabItem.setLayout(new FitLayout());
				tabItem.setBorders(false);
				
				OracleCaseDataPage page = new OracleCaseDataPage(caseId,selectedItem.GetCaseName(),selectedItem.getN_tran());
				tabItem.add(page);
				if(tabPanel!=null){
					if(tabPanel.getItemByItemId(tabItem.getId()) == null)
						tabPanel.add(tabItem);
					tabPanel.setSelection(tabPanel.getItemByItemId(tabItem.getId()));
				}
			}
		};
	}
}
