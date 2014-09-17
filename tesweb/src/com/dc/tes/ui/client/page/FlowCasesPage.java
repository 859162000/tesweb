package com.dc.tes.ui.client.page;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.dc.tes.ui.client.AppContext;
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
import com.dc.tes.ui.client.control.PostPanelAsync;
import com.dc.tes.ui.client.control.PostPanelAsync.SubmitCompleteEvent;
import com.dc.tes.ui.client.model.GWTCard;
import com.dc.tes.ui.client.model.GWTCase;
import com.dc.tes.ui.client.model.GWTCaseFlow;
import com.dc.tes.ui.client.model.GWTCaseParamExpectedValue;
import com.dc.tes.ui.client.model.GWTTransaction;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.Style.SelectionMode;
import com.extjs.gxt.ui.client.data.BaseListLoader;
import com.extjs.gxt.ui.client.data.ListLoadResult;
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
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.store.TreeStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.FieldSet;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.TextArea;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.extjs.gxt.ui.client.widget.grid.CellEditor;
import com.extjs.gxt.ui.client.widget.grid.CheckColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.EditorGrid;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.extjs.gxt.ui.client.widget.layout.FormLayout;
import com.extjs.gxt.ui.client.widget.toolbar.FillToolItem;
import com.extjs.gxt.ui.client.widget.treegrid.EditorTreeGrid;
import com.extjs.gxt.ui.client.widget.treegrid.TreeGridCellRenderer;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;

@Deprecated
public class FlowCasesPage extends BasePage{
	private IBatchServiceAsync batchService = null;
	private ICaseServiceAsync caseService= null;
	private ISysDynamicParameterAsync sysParaService = null;
	private GWTCaseFlow caseFlow;
	private String batchNo;
	public FlowCasesPage(String batch, GWTCaseFlow gwtCaseFlow) {
		// TODO Auto-generated constructor stub
		caseFlow = gwtCaseFlow;
		batchNo = batch;
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
	private GWTCase EditCase = null;

	UploadWin upWindow;
	
	/**
	 * 案例预期值的store
	 */
	TreeStore<ModelData> store = new TreeStore<ModelData>();
	
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
		bottomBar = new ConfigToolBar();
		bottomBar.initPageToolBar(panel.getLoader());
		bottomBar.AddButton("btnCaseData",new Button("案例报文编辑"), MainPage.ICONS
				.CaseDataEdit(), CaseDataHandler());
		bottomBar.AddButton("btnParamExpectedData",new Button("参数预期值编辑"), MainPage.ICONS
				.caseParam(), paramExpectedDataHandler());
		bottomBar.AddButton("caseExec",new Button("执行案例"), MainPage.ICONS
				.Exec(), ExecHandler());		
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
	
	}
	
	private void addDoubleClickListener(final GridContentPanel<GWTCase> panel1) {
		// TODO Auto-generated method stub
		panel.getDataGrid().addListener(Events.CellDoubleClick, new Listener<GridEvent<GWTCase>>() {

			@Override
			public void handleEvent(GridEvent<GWTCase> be) {
				// TODO Auto-generated method stub
				caseService.ChangeBreakPointFlag(be.getModel(), new AsyncCallback<Boolean>() {
		
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
								
								sysParaService.SaveCaseParaExpectedValue(lst, GetLoginLogID(), new AsyncCallback<Boolean>() {
		
									@Override
									public void onFailure(Throwable caught) {
										// TODO Auto-generated method stub
										MessageBox.alert("错误提示", "保存失败", null);
									}
		
									@Override
									public void onSuccess(Boolean result) {
										// TODO Auto-generated method stub
										window.close();
									}
									
								});
							} else {
								window.close();
							}
							
						}		
					});			
					window.addButton(btnOK);
					window.addButton(new Button("取消", new SelectionListener<ButtonEvent>() {

						@Override
						public void componentSelected(ButtonEvent ce) {
							// TODO Auto-generated method stub
							window.close();	
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
			//	EditCase.SetImportBatchNo(batchNo);
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
		detailMap.put(GWTCase.N_caseNo, "案例编号");
		detailMap.put(GWTCase.N_caseName, "案例名称");
		detailMap.put(GWTCase.N_cardNo, "卡号");
		detailMap.put(GWTCase.N_tranType, "交易类型");
		detailMap.put(GWTCase.N_amount, "交易金额");
		detailMap.put(GWTCase.N_caseFlowNo, "业务流编号");
		detailMap.put(GWTCase.N_caseFlowName, "业务流名称");
		detailMap.put(GWTCase.N_Desc, "备注");
		detailMap.put(GWTCase.N_BreakPointFlagStr, "断点设置");
		return detailMap;
	}
	/**
	 * 获得Grid的列配置列表
	 * 
	 * @return Grid的列配置列表
	 */
	private List<ColumnConfig> GetColumnConfig() {
		List<ColumnConfig> columns = new ArrayList<ColumnConfig>();

		columns.add(new ColumnConfig(GWTCase.N_caseNo, "编号", 70));
		columns.add(new ColumnConfig(GWTCase.N_caseName, "案例名称", 150));
		columns.add(new ColumnConfig(GWTCase.N_cardNo, "卡号", 120));
		columns.add(new ColumnConfig(GWTCase.N_tranType, "交易类型", 100));
		columns.add(new ColumnConfig(GWTCase.N_amount, "交易金额", 70));
		columns.add(new ColumnConfig(GWTCase.N_caseFlowNo, "业务流编号", 100));
		columns.add(new ColumnConfig(GWTCase.N_caseFlowName, "业务流名称", 100));
		columns.add(new ColumnConfig(GWTCase.N_BreakPointFlagStr, "断点设置", 60));
		
		return columns;
	}
	
	private SelectionListener<ButtonEvent> ExecHandler() {
		return new SelectionListener<ButtonEvent>() {
			public void componentSelected(ButtonEvent ce) {
				ResultCompare resultWin = new ResultCompare();
				GWTCase selectedItem = panel.getDataGrid()
				.getSelectionModel().getSelectedItems().get(0);
				resultWin.Show(GetSysInfo(), GetUserID(), selectedItem.GetTransactionID(), selectedItem.GetCaseId(),GetSelf());
			}
		};
	}
	
	
	
	private void CreateEditForm() {
		final Window window = new Window();

		window.setSize(400, 300);
		//window.setPlain(true);
		window.setModal(true);
		window.setBlinkModal(true);
		window.setBlinkModal(false);
		window.setLayout(new FitLayout());
			    

		final PostPanelAsync formPanel = new PostPanelAsync();
		// 设置样式
		formPanel.setBorders(false);
		formPanel.setBodyBorder(false);
		//formPanel.setLabelWidth(55);
		formPanel.setPadding(5);
		formPanel.setHeaderVisible(false);
		formPanel.setScrollMode(Scroll.AUTOY);

		FormData formdata = new FormData("90%");
		
		
		FieldSet fieldSet1 = new FieldSet();
		fieldSet1.setHeading("基本案例信息");
		fieldSet1.setCollapsible(true);		
		FormLayout layout1 = new FormLayout();
		layout1.setLabelWidth(80);
		layout1.setDefaultWidth(240);
		fieldSet1.setLayout(layout1);
		
//		final DistTextField tfCaseNo = new DistTextField(EditCase, EditCase
//				.GetCaseNo(), "案例编号", "该批次已存在该编号的案例，请重命名");
		final TextField<String> tfCaseNo = new TextField<String>();
		tfCaseNo.setFieldLabel("案例编号");
		tfCaseNo.setName(GWTCase.N_cardNo);
		tfCaseNo.setMaxLength(32);
		fieldSet1.add(tfCaseNo, formdata);
		
		final TextField<String> tfCaseName = new TextField<String>();
		tfCaseName.setName(GWTCase.N_caseName);
		tfCaseName.setFieldLabel("案例名称");
		tfCaseName.setAllowBlank(false);
		tfCaseName.setValue(EditCase.GetCaseName());
		fieldSet1.add(tfCaseName, formdata);
		
		
		final ComboBox<GWTCard> comboBox = new ComboBox<GWTCard>();
		comboBox.setName(GWTCase.N_cardNo);					
		comboBox.setFieldLabel("卡号");
		comboBox.setValueField(GWTCard.N_cardNo);
		comboBox.setTriggerAction(TriggerAction.ALL);
		comboBox.setAllowBlank(false);
		comboBox.setDisplayField(GWTCard.N_cardNo);	
		comboBox.setMinChars(1);		
		final ListStore<GWTCard> cardStore = new ListStore<GWTCard>();
		batchService.GetCardList(batchNo, new AsyncCallback<List<GWTCard>>() {

			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub
				caught.printStackTrace();
				MessageBox.alert("错误提示","获取卡列表失败", null);
			}

			@Override
			public void onSuccess(List<GWTCard> result) {
				// TODO Auto-generated method stub
				cardStore.add(result);
			}
		});		
		
		comboBox.setStore(cardStore);
		GWTCard card = new GWTCard();
		card.setCardNo(EditCase.GetCardNo());
		comboBox.setValue(card);
		fieldSet1.add(comboBox, formdata);

		final ComboBox<GWTTransaction> tranBox = new ComboBox<GWTTransaction>();
		tranBox.setName(GWTCase.N_transactionId);
		tranBox.setFieldLabel("交易类型");
		tranBox.setAllowBlank(false);
		tranBox.setValueField(GWTTransaction.N_TransID);
		tranBox.setTriggerAction(TriggerAction.ALL);
		tranBox.setDisplayField(GWTTransaction.N_TranName);
		final ListStore<GWTTransaction> tranStore = new ListStore<GWTTransaction>(){
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
		GWTTransaction transaction = new GWTTransaction(EditCase.GetTransactionID(), GetSystemID(), 0, "", EditCase.GetTranType(), "", 0, "", "", "");
		tranBox.setValue(transaction);
		tranBox.setLazyRender(false);
		fieldSet1.add(tranBox, formdata);
		
	
		final TextField<String> tfAmount = new TextField<String>();
		tfAmount.setName(GWTCase.N_amount);
		tfAmount.setFieldLabel("交易金额");
		tfAmount.setAllowBlank(true);
		tfAmount.setValue(EditCase.GetAmount());
		fieldSet1.add(tfAmount, formdata);
			
		final TextArea taDesc = new TextArea();
		taDesc.setFieldLabel("描述");
		taDesc.setPreventScrollbars(true);
		taDesc.setName(GWTCase.N_Desc);
		taDesc.setValue(EditCase.GetDesc());
		fieldSet1.add(taDesc, formdata);
		
		formPanel.add(fieldSet1);
		
		

		
		formPanel.addSubmitCompleteHandler(new PostPanelAsync.SubmitCompleteHandler() {

				public void onSubmitComplete(SubmitCompleteEvent event) {
					String returnMsg = event.getResults();
					if(returnMsg.isEmpty())
					{
						panel.loaderReLoad(EditCase.IsNew());
						window.hide();
					}
					else
					{
						//tfCaseNo.EnforceValidate();
						panel.loaderReLoad(EditCase.IsNew());
						MessageBox.alert("友情提示", event.getResults(), null);
						
					}
				}
			});
			

		
		Button btnOK = new Button("确定", new SelectionListener<ButtonEvent>() {
			public void componentSelected(ButtonEvent ce) {
				if (!formPanel.isValid())
					return;
				
				formPanel.setSubmitMsg("保存案例信息,请稍后...");
				formPanel.setEncoding(FormPanel.Encoding.MULTIPART);
				formPanel.setMethod(FormPanel.Method.POST);
				formPanel.setAction("CaseRespServletUpload?" 
						+ GWTCase.N_caseId + "=" + EditCase.GetCaseId() + "&"
						+ GWTCase.N_caseNo + "=" + tfCaseNo.getValue() + "&"
						+ GWTCase.N_caseName + "=" + tfCaseName.getValue() + "&"
						+ GWTCase.N_cardNo + "=" + comboBox.getValue().getCardNo() + "&"
						+ GWTCase.N_transactionId + "=" + tranBox.getValue().getTranID() + "&"
						+ GWTCase.N_amount + "=" + tfAmount.getValue() + "&"
						+ GWTCase.N_caseFlowNo + "=" + caseFlow.GetCaseFlowNo() + "&"
						+ GWTCase.N_caseFlowName + "=" + caseFlow.GetName() + "&"
						+ GWTCase.N_Desc + "=" + taDesc.getValue() + "&"
					//	+ GWTCase.N_importBatchNo + "=" + batchNo + "&"
						+ GWTCase.N_isParseable + "=1" + "&"
						+ "sysId=" + GetSystemID() + "&"
						+ "userId=" + GetUserID()						
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
			tfCaseNo.setValue(EditCase.GetCaseNo());
			window.setHeading("编辑案例信息");
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
				
				int iIsClientSimu = GetIsClientSimu();
				boolean bIsClientSimu = true;
				if (iIsClientSimu == 0) {
					bIsClientSimu = false;
				}
				CaseDataPage page = new CaseDataPage(caseId,selectedItem.GetCaseName(),selectedItem.getN_tran(),bIsClientSimu,true);
				AppContext.GetEntryPoint().AddTabItem(tabId, tabTitle, page);
			}
		};
	}
}
