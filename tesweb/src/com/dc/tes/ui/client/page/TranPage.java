package com.dc.tes.ui.client.page;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.dc.tes.ui.client.AppContext;
import com.dc.tes.ui.client.IClientTransaction;
import com.dc.tes.ui.client.IClientTransactionAsync;
import com.dc.tes.ui.client.IComponent;
import com.dc.tes.ui.client.IComponentAsync;
import com.dc.tes.ui.client.ISysDynamicParameter;
import com.dc.tes.ui.client.ISysDynamicParameterAsync;
import com.dc.tes.ui.client.MainPage;
import com.dc.tes.ui.client.common.CookieManage;
import com.dc.tes.ui.client.common.ServiceHelper;
import com.dc.tes.ui.client.control.ConfigToolBar;
import com.dc.tes.ui.client.control.DistTextField;
import com.dc.tes.ui.client.control.FormContentPanel;
import com.dc.tes.ui.client.control.GridContentPanel;
import com.dc.tes.ui.client.control.PostFormPanel;
import com.dc.tes.ui.client.control.RequireTextField;
import com.dc.tes.ui.client.control.JSEdit;
import com.dc.tes.ui.client.enums.CsType;
import com.dc.tes.ui.client.enums.VersionType;
import com.dc.tes.ui.client.model.GWTAdapter;
import com.dc.tes.ui.client.model.GWTChannel;
import com.dc.tes.ui.client.model.GWTParameterDirectory;
import com.dc.tes.ui.client.model.GWTSimuSystem;
import com.dc.tes.ui.client.model.GWTStock;
import com.dc.tes.ui.client.model.GWTSysDynamicPara;
import com.dc.tes.ui.client.model.GWTTransaction;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.data.BaseModelData;
import com.extjs.gxt.ui.client.data.BaseTreeLoader;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.extjs.gxt.ui.client.data.RpcProxy;
import com.extjs.gxt.ui.client.data.TreeLoader;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FormEvent;
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
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.SimpleComboBox;
import com.extjs.gxt.ui.client.widget.form.TextArea;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.extjs.gxt.ui.client.widget.grid.CheckBoxSelectionModel;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.extjs.gxt.ui.client.widget.toolbar.FillToolItem;
import com.extjs.gxt.ui.client.widget.treepanel.TreePanel;
import com.extjs.gxt.ui.client.widget.treepanel.TreePanel.CheckCascade;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Random;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class TranPage extends BasePage {
	private IClientTransactionAsync tranService = null;
	private ISysDynamicParameterAsync sysParaService = null;
	private CsType type = CsType.Client;
	private GWTTransaction EditTran = null;
	private Window window = null;
	private UploadWin upWindow = null;
	private TreeStore<ModelData> feedStore;
	private TreePanel<ModelData> treePanel;
	
	GridContentPanel<GWTTransaction> panel;
	FormContentPanel<GWTTransaction> detailPanel;
	ConfigToolBar bottomBar;
	
	Grid<GWTSysDynamicPara> grid;
	CheckBoxSelectionModel<GWTSysDynamicPara> sm;
	
	public TranPage(CsType type) {
		this.type = type;
	}

	@Override
	protected void onRender(Element parent, int index) {
		super.onRender(parent, index);

		tranService = ServiceHelper.GetDynamicService("transerver", IClientTransaction.class);
		sysParaService = ServiceHelper.GetDynamicService("sysPara", ISysDynamicParameter.class);
		panel = new GridContentPanel<GWTTransaction>();
		RpcProxy<PagingLoadResult<GWTTransaction>> proxy = new RpcProxy<PagingLoadResult<GWTTransaction>>() {
			@Override
			public void load(Object loadConfig,
					AsyncCallback<PagingLoadResult<GWTTransaction>> callback) {
				tranService.GetList(GetSystemID(), type.getDbValue(), panel.GetSearchCondition(),
						(PagingLoadConfig) loadConfig, callback);
			}
		};

		panel.setProxy(proxy);
		panel.setColumns(GetColumnConfig());
		panel.DrowSearchBar();
		panel.DrowGridView(GWTTransaction.N_Desc);

		bottomBar = new ConfigToolBar();
		bottomBar.initPageToolBar(panel.getLoader());

		Button packStruct = new Button("报文结构维护");
		Menu menu = new Menu();
		MenuItem item = new MenuItem("请求报文结构定义");
		item.setId("EditResp");
		item.setIcon(MainPage.ICONS.ReqStruct());
		item.addSelectionListener(reqStructHandler());
		menu.add(item);

		item = new MenuItem("响应报文结构定义");
		item.setId("EditResq");
		item.setIcon(MainPage.ICONS.ResStruct());
		item.addSelectionListener(resStructHandler());
		menu.add(item);

		item = new MenuItem("上传交易报文结构");
		item.setId("TStrucUpload");
		item.setIcon(MainPage.ICONS.WebUp());
		item.addSelectionListener(UploadTranStruct());
		menu.add(item);

		item = new MenuItem("下载交易报文结构");
		item.setId("TStructDownLoad");
		item.setIcon(MainPage.ICONS.WebDown());
		item.addSelectionListener(DownloadTranStruct());
		menu.add(item);

		item = new MenuItem("下载报文模板");
		item.setId("TempalteDownLoad");
		item.setIcon(MainPage.ICONS.WebDown());
		item.addSelectionListener(packTemplateDownload());
		menu.add(item);

		packStruct.setMenu(menu);
		bottomBar.AddMenuButton("packStruct", packStruct, MainPage.ICONS
				.PackStruct(), null);

		bottomBar.AddButton("btnCase", new Button("案例数据管理"), MainPage.ICONS
				.menuCase(), editCaseHandler());
		bottomBar.AddButton("btnScript", new Button("编辑脚本"), MainPage.ICONS
				.Script(), editScriptHandler());
		//if(type == CsType.Client)
		bottomBar.AddButton("btnParamSetting", new Button("交易参数"), MainPage.ICONS
				.tranParam(), paramSetting());
	//	bottomBar.AddButton("btnCaseUpload", new Button("批量上传案例"), MainPage.ICONS
	//			.WebUp(), caseUploadHandler());
		bottomBar.AddWidget(new FillToolItem());
		bottomBar.AddNewBtn("btnAdd", AddHandler());
		bottomBar.AddEditBtn("btnEdit", EditHandler());
		bottomBar.AddDelBtn("btnDel", DelHandler());
		InitBtnConfigBar(bottomBar);
		panel.setBottomBar(bottomBar);
		add(panel);

		detailPanel = new FormContentPanel<GWTTransaction>();
		detailPanel.setBindInfo(GetDetailHashMap());
		panel.setDetailForm(detailPanel);
		add(detailPanel);
	}

	private SelectionListener<ButtonEvent> paramSetting() {
		// TODO Auto-generated method stub
		return new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				// TODO Auto-generated method stub
				final Window window = new Window();
				window.setSize(700, 400);
				window.setLayout(new FitLayout());
			    window.setPlain(true);  
			    window.setModal(true);  
			    ContentPanel cp = new ContentPanel();   
			    cp.setLayout(new FitLayout());  
			    cp.setSize(700, 400);  	    
			    cp.add(getParamTree());
			    cp.setHeaderVisible(false);
				window.add(cp);
				window.setHeading("交易参数列表");			
				
				Button btnOK = new Button("确定", new SelectionListener<ButtonEvent>() {

					@Override
					public void componentSelected(ButtonEvent ce) {
						// TODO Auto-generated method stub

						final List<ModelData> allLst = treePanel.getCheckedSelection();
						final List<GWTSysDynamicPara> lst = new ArrayList<GWTSysDynamicPara>();
						//筛选掉文件夹的类型
						for(ModelData raw : allLst) {
							if(raw instanceof GWTSysDynamicPara) {
								lst.add((GWTSysDynamicPara)raw);
							}
						}
						
						if(!panel.getSelection().isEmpty()) {
							if(lst.isEmpty()) {
								MessageBox.confirm("确定提示", "取消交易的所有参数？", new Listener<MessageBoxEvent>() {
	
									@Override
									public void handleEvent(MessageBoxEvent be) {
										// TODO Auto-generated method stub
										if(be.getButtonClicked().getText().equalsIgnoreCase("Yes")) {
											sysParaService.SaveTranDynamicPara(lst, panel.getDataGrid().getSelectionModel().getSelection(),
													GetUserID(),GetLoginLogID(),
													new AsyncCallback<Boolean>(){
	
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
										}
									}
								});
								
							} else {
								
								sysParaService.SaveTranDynamicPara(lst, panel.getDataGrid().getSelectionModel().getSelection(),
										GetUserID(), GetLoginLogID(),
										new AsyncCallback<Boolean>(){
		
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
							}
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

	
	//建立动态参数树
	private TreePanel<ModelData> getParamTree() {
		
		RpcProxy<List<ModelData>> proxy = new RpcProxy<List<ModelData>>() {

			@Override
			protected void load(Object loadConfig,
					AsyncCallback<List<ModelData>> callback) {
				// TODO Auto-generated method stub
				sysParaService.getSysParamTree(GetSysInfo(),
						(GWTParameterDirectory) loadConfig, callback);
			}
		};
		
		final TreeLoader<ModelData> loader = new BaseTreeLoader<ModelData>(proxy);
		feedStore = new TreeStore<ModelData>(loader);
		
		treePanel = new TreePanel<ModelData>(feedStore){
			
			public boolean isExpanded(ModelData model) {
				TreeNode node = findNode(model);
				if(node != null)
					return node.isExpanded();
				else
					return false;
			}
			  
			@Override
			public boolean hasChildren(ModelData parent) {
				if (parent instanceof GWTParameterDirectory) {
					return true;
				} else {
					return false;  
				}
			}	
			//修复勾选了父节点情况下,关闭后打开父节点时, 子节点被全选的BUG
		    protected void refresh(ModelData model) {
		        if (rendered) {
		            TreeNode node = findNode(model);
		            if (node != null && node.getElement() != null) {
		              view.onIconStyleChange(node, calculateIconStyle(model));
		              view.onJointChange(node, calcualteJoint(model));
		              view.onTextChange(node, getText(model));
		            }
		          }
			}			
			//两种情况都做有困难
//			@Override
//			protected void onCheckCascade(ModelData model, boolean checked) {
//		
//				//选了孩子父亲看情况选
//		        if (checked) { //当前节点勾选了
//			        //选了父亲就自动选孩子
//					for (ModelData child : store.getChildren(model)) {
//			            setChecked(child, checked);
//			        }
//		        	
//		        	ModelData p = store.getParent(model);
//		            if (p != null) {
//		              setChecked(p, true);
//		            }
//		          } else {    //取消勾选
//		        	  //孩子全部都取消勾选
//		        	  for (ModelData child : store.getChildren(model)) {
//		        		  setChecked(child, false);
//		        	  }
//		            
//		        	  //兄弟节点假如都没选的则父节点取消勾选
//		        	  ModelData p = store.getParent(model);
//		        	  boolean flag = true;
//		        	  while(p != null) {
//			        	  for (ModelData child : store.getChildren(p)) {
//			        		  if(isChecked(child)) {
//			        			  //兄弟节点还存在勾选的则取消
//			        			  flag = false;
//			        			  break;
//			        		  }
//			        	  }
//			        	  if(!flag)
//			        		  break;
//			        	  //兄弟节点都没勾选了
//			        	  setChecked(p, false);
//			        	  p = store.getParent(p);
//			        	  flag = true;
//		        	  }
//		          }
//			}
		};
		treePanel.setDisplayProperty("name");
		treePanel.setCheckable(true);
		treePanel.setCheckStyle(CheckCascade.CHILDREN);
		
		loader.addListener(loader.Load, new Listener<BaseEvent>() {

			@Override
			public void handleEvent(BaseEvent be) {
				// TODO Auto-generated method stub
				if(panel.getSelection().size()>0)
					treePanel.mask("加载中");
				sysParaService.GetGWTTranParamList(panel.getSelection().get(0).getTranID(),
						new AsyncCallback<List<ModelData>>() {

							@Override
							public void onFailure(Throwable caught) {
								// TODO Auto-generated method stub
								
							}

							@Override
							public void onSuccess(List<ModelData> result) {
								// TODO Auto-generated method stub
								if(panel.getSelection().size() == 1) {	
									for(ModelData model : result) {
										if(model instanceof GWTParameterDirectory)
										//if(((GWTParameterDirectory)model).GetParentDirID() == 0) {
											if(!treePanel.isExpanded(model))
												treePanel.setExpanded(model, true);
										//}
									}
								}
								treePanel.setCheckedSelection(result);	
								treePanel.unmask();
							}	
				});
			}	
		});
		
		return treePanel;
	}
	
	/*
	private TreeGrid<ModelData> getParamTree() {
		
		List<ColumnConfig> columns = new ArrayList<ColumnConfig>();  
		SM = new CheckBoxSelectionModel<ModelData>();  
		columns.add(SM.getColumn());
		ColumnConfig name = new ColumnConfig(GWTSysDynamicPara.N_ParameterName, "参数名称", 160);
		columns.add(name);
		name.setRenderer(new TreeGridCellRenderer<ModelData>());
		columns.add(new ColumnConfig(GWTSysDynamicPara.N_ParameterDesc, "参数描述", 160));
		columns.add(new ColumnConfig(GWTSysDynamicPara.N_ParameterTypeStr, "参数类型", 160));
		
		RpcProxy<List<ModelData>> proxy = new RpcProxy<List<ModelData>>() {

			@Override
			protected void load(Object loadConfig,
					AsyncCallback<List<ModelData>> callback) {
				// TODO Auto-generated method stub
				sysParaService.getSysParamTree(GetSysInfo(),
						(GWTParameterDirectory) loadConfig, callback);
			}
		};
		
		final TreeLoader<ModelData> loader = new BaseTreeLoader<ModelData>(proxy);
		feedStore = new TreeStore<ModelData>(loader);
		
		treeGrid = new TreeGrid<ModelData>(feedStore,new ColumnModel(columns)){
			@Override
			public boolean hasChildren(ModelData parent) {
				if (parent instanceof GWTParameterDirectory) {
					return true;
				} else {
					return false;  
				}
			}	
		};
		treeGrid.setSelectionModel(SM);
		SM.setSelectionMode(SelectionMode.SIMPLE);
		treeGrid.addPlugin(SM);

		return treeGrid;
			
	}

	/**
	 * 获得Grid的列配置列表
	 * 
	 * @return Grid的列配置列表
	 */
	private List<ColumnConfig> GetColumnConfig() {
		List<ColumnConfig> columns = new ArrayList<ColumnConfig>();

		columns.add(new ColumnConfig(GWTTransaction.N_TranCode, "交易码", 140));
		columns.add(new ColumnConfig(GWTTransaction.N_TranName, "交易名称", 200));
		columns.add(new ColumnConfig(GWTTransaction.N_Desc, "交易描述", 240));
		//第一版本不提供本功能
		if (AppContext.GetVersion() != VersionType.Pstub) {
			panel.SetHeight(265);
			columns.add(GetRenderColumn("请求报文", true));
			columns.add(GetRenderColumn("响应报文", false));
		}
	//	if(type == CsType.Client)
	//		columns.add(GetRunRenderColumn("执行"));

		return columns;
	}
	
	private ColumnConfig GetRunRenderColumn(String titile)
	{
		GridCellRenderer<GWTTransaction> runRender = new GridCellRenderer<GWTTransaction>() {
			@Override
			public Object render(final GWTTransaction model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<GWTTransaction> store, Grid<GWTTransaction> grid) {
				String iconID = "icon_run" + model.getTranID();

				HtmlContainer html = new HtmlContainer("<span>"
						+ "<span style = 'margin:0px;padding:0px;' id = '"
						+ iconID + "' ></span>" + "</span>");

				String iconName = "RunColumn";
				IconButton b = new IconButton(iconName);
				html.add(b, "#" + iconID);
				b.addSelectionListener(new SelectionListener<IconButtonEvent>() {
							@Override
							public void componentSelected(IconButtonEvent ce) {
								ResultCompare resultWin = new ResultCompare();
								GWTTransaction selectedItem = panel.getDataGrid()
								.getSelectionModel().getSelectedItems().get(0);
								resultWin.Show(GetSysInfo(),selectedItem,GetSelf());
							}
						});
				return html;
			}
		};
		ColumnConfig column = new ColumnConfig(GWTTransaction.N_Req, titile, 40);
		column.setAlignment(HorizontalAlignment.CENTER);
		column.setSortable(false);
		column.setResizable(false);
		column.setRenderer(runRender);
		return column;
	}

	private ColumnConfig GetRenderColumn(String titile, final boolean isReq) {
		GridCellRenderer<GWTTransaction> respRender = new GridCellRenderer<GWTTransaction>() {
			@Override
			public Object render(final GWTTransaction model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<GWTTransaction> store, Grid<GWTTransaction> grid) {
				String iconID = "icon" + (isReq ? "req" : "resp")
						+ model.getTranID();
				
				boolean haveConfig = isReq ? model.GetReq() : model.GetResp();
				String iconName = haveConfig ? "viewPackConfig" : "viewPackConfig_No";
				IconButton b = new IconButton(iconName);
				String toolTip = haveConfig ? "已定义" : "未定义";
				b.setToolTip(toolTip);
				HtmlContainer html = new HtmlContainer("<span>"
						+ "<span style = 'margin:0px;padding:0px;' id = '"
						+ iconID + "' ></span>" + "</span>");
				html.add(b, "#" + iconID);
				b
						.addSelectionListener(new SelectionListener<IconButtonEvent>() {
							@Override
							public void componentSelected(IconButtonEvent ce) {
								PackConfig(model.getTranID(), model
										.getTranCode(), isReq);
							}
						});
				return html;
			}
		};
		ColumnConfig column = new ColumnConfig(GWTTransaction.N_Req, titile, 70);
		column.setAlignment(HorizontalAlignment.CENTER);
		column.setSortable(false);
		column.setResizable(false);
		column.setRenderer(respRender);
		return column;
	}

	/**
	 * 获得详细信息绑定的Hash列表
	 * 
	 * @return Map<String,String> 对应 Map<对应绑定的值名称,字段显示名称>
	 */
	public Map<String, String> GetDetailHashMap() {

		Map<String, String> detailMap = new LinkedHashMap<String, String>();
		detailMap.put(GWTTransaction.N_TranCode, "交易码");
		detailMap.put(GWTTransaction.N_TranName, "交易名称");
		if(type == CsType.Client)
			detailMap.put(GWTTransaction.N_Chanel, "通道名称");
		detailMap.put(GWTTransaction.N_Desc, "交易描述");
		return detailMap;
	}

	private void CreateEditForm() {
		final Window window = new Window();

		window.setSize(500, type == CsType.Client ? 300 : 230);
		window.setPlain(true);
		window.setModal(true);
		window.setBlinkModal(false);
		window.setLayout(new FitLayout());

		final FormPanel formPanel = new FormPanel();
		formPanel.setBorders(false);
		formPanel.setBodyBorder(false);
		formPanel.setLabelWidth(55);
		formPanel.setPadding(5);
		formPanel.setHeaderVisible(false);

		String labelStyle = "width:70px;";
		FormData formdata = new FormData("95%");
		final RequireTextField tfTranCode = new RequireTextField("交易码");
		//final DistTextField tfTranCode = new DistTextField(EditTran, EditTran
		//		.getTranCode(), "交易码", "该系统已存在该交易码，不能重复，请重命名");
		tfTranCode.setLabelStyle(labelStyle);
		tfTranCode.setMaxLength(32);
		formPanel.add(tfTranCode, formdata);

		//final RequireTextField tfTranName = new RequireTextField("交易名称");
		final DistTextField tfTranName = new DistTextField(EditTran, 
				EditTran.getTranName(), "交易名称", "该系统已存在该交易名称，不能重复，请重命名");
		tfTranName.setLabelStyle(labelStyle);
		tfTranName.setMaxLength(64);
		formPanel.add(tfTranName, formdata);
		
		final ComboBox<GWTStock> tranType = new ComboBox<GWTStock>();
		tranType.setName("TranType");
		tranType.setFieldLabel("交易类别");
		tranType.setValueField(GWTStock.N_Name);
		tranType.setDisplayField(GWTStock.N_Name);
		tranType.setEditable(false);
		tranType.setLabelStyle(labelStyle);
		ListStore<GWTStock> store = new ListStore<GWTStock>();
		store.add(new GWTStock("POS类交易","1"));
		store.add(new GWTStock("ATM类交易","2"));
		store.add(new GWTStock("转账类交易","3"));
		store.add(new GWTStock("其它类交易","4"));
		tranType.setStore(store);
		tranType.setTriggerAction(TriggerAction.ALL);
		tranType.setValue(new GWTStock(EditTran.getTranCateName(),EditTran.getTranCategoryID()));
		tranType.setAllowBlank(false);
		formPanel.add(tranType,formdata); 
		
		
//		final TextField<String> tfChanel = new TextField<String>();
		int isSyncComm = CookieManage.GetIsSyncComm();
		final SimpleComboBox<String> tfChanel = new SimpleComboBox<String>();
		if(type == CsType.Client || isSyncComm == 0)
		{
			tfChanel.setInEditor(false);
			tfChanel.setEditable(false);
			tfChanel.setLabelStyle(labelStyle);
			tfChanel.setFieldLabel("通道名称");
			tfChanel.setAllowBlank(false);
			formPanel.add(tfChanel, formdata);

			// 获得通道列表
			IComponentAsync cService = ServiceHelper.GetDynamicService(
					"component", IComponent.class);
			cService.GetChannelListBySystemId(GetSystemID(),
					new AsyncCallback<List<GWTChannel>>() {
						@Override
						public void onFailure(Throwable caught) {
						}

						@Override
						public void onSuccess(List<GWTChannel> result) {
							for (GWTChannel chanel : result) {
								tfChanel.setRawValue(EditTran.GetChanel());
								BaseModelData adapter = (BaseModelData) chanel.get(GWTChannel.N_Adapter);
								if (adapter != null	&& (Integer) adapter.get(GWTAdapter.N_CsType) == CsType.Client.getDbValue().intValue())
									tfChanel.add(chanel.get(GWTChannel.N_ChannelName).toString());
							}
						}
					});			
		}
		final TextField<String> tfDelayTime = new TextField<String>();
		if(type == CsType.Client)
		{
			tfDelayTime.setLabelStyle(labelStyle);
			tfDelayTime.setFieldLabel("查询延时");
			tfDelayTime.setRegex("^[0-9]*$");
			tfDelayTime.getMessages().setRegexText("只能输入数字");
			tfDelayTime.setAllowBlank(false);
			formPanel.add(tfDelayTime, formdata);
		}
		
		final TextArea tfDesc = new TextArea();
		tfDesc.setLabelStyle(labelStyle);
		tfDesc.setHeight(70);
		tfDesc.setMaxLength(128);
		tfDesc.setFieldLabel("交易描述");
		formPanel.add(tfDesc, formdata);

		Button btnOK = new Button("确定", new SelectionListener<ButtonEvent>() {
			public void componentSelected(ButtonEvent ce) {
				if (!formPanel.isValid())
					return;
//				EditTran.SetEditValue(tfTranCode.getValue(), tfTranName
//						.getValue(), tfDesc.getValue(),tfChanel.getValue());
				EditTran.SetEditValue(tfTranCode.getValue(), tfTranName
						.getValue(), tfDesc.getValue(),tfChanel.getRawValue());
				EditTran.setTranCategoryID(tranType.getValue().getPos());
				EditTran.SetSqlDelayTime(type==CsType.Client?tfDelayTime.getValue():"0");
				tranService.SaveTran(GetSysInfo(),EditTran, GetLoginLogID(),
						new AsyncCallback<Boolean>() {
					public void onFailure(Throwable caught) {
						caught.printStackTrace();
						MessageBox.alert("错误信息", "保存失败", null);
					}

					@SuppressWarnings("deprecation")
					public void onSuccess(Boolean suc) {
						panel.loaderReLoad(EditTran.IsNew());
						if (suc)
							window.close();
						else {
							//tfTranCode.focus();
							//tfTranCode.EnforceValidate();
							tfTranName.focus();
							tfTranName.EnforceValidate();
						}
					}
				});
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
		if (EditTran.IsNew()) {
			window.setHeading("新增交易信息");
		} else {
			tfTranCode.setValue(EditTran.getTranCode());
			tfTranName.setValue(EditTran.getTranName());
			tfChanel.setRawValue(EditTran.GetChanel());
			tfDelayTime.setValue(EditTran.GetSqlDelayTime());
			tfDesc.setValue(EditTran.getDesc());
			window.setHeading("编辑交易信息");
		}
		window.show();
	}

	private SelectionListener<ButtonEvent> AddHandler() {
		return new SelectionListener<ButtonEvent>() {
			public void componentSelected(ButtonEvent ce) {
				EditTran = new GWTTransaction(GetSystemID(), type.getDbValue());
				CreateEditForm();
			}
		};
	}

	private SelectionListener<ButtonEvent> EditHandler() {
		return new SelectionListener<ButtonEvent>() {
			public void componentSelected(ButtonEvent ce) {
				List<GWTTransaction> selectedItems = panel.getDataGrid()
						.getSelectionModel().getSelectedItems();
				if (selectedItems.size() != 1) {
					MessageBox.alert("Alert", "请选择一个系统进行编辑", null);
					return;
				}
				EditTran = selectedItems.get(0);
				CreateEditForm();
			}
		};
	}

	private Listener<MessageBoxEvent> DelHandler() {
		return new Listener<MessageBoxEvent>() {
			public void handleEvent(MessageBoxEvent be) {
				Button msgBtn = be.getButtonClicked();
				if (msgBtn.getText().equalsIgnoreCase("Yes")) {
					if(!IsAdmin() && !IsPM()) {		
						MessageBox.alert("错误提示", "删除失败,你没有管理员权限!", null);
						return;
					}
					tranService.DeleteTran(GetSysInfo(),
							panel.getSelection(),
							GetLoginLogID(),
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

	private SelectionListener<MenuEvent> packTemplateDownload() {
		return new SelectionListener<MenuEvent>() {
			public void componentSelected(MenuEvent ce) {
				final PostFormPanel formPanel = new PostFormPanel();
				formPanel.mask("正在获取报文模板,请稍后...");
				formPanel.setEncoding(FormPanel.Encoding.MULTIPART);
				formPanel.setMethod(FormPanel.Method.POST);
				formPanel.setAction("PackTemplateDownload?"
						+ GWTSimuSystem.N_SystemID + "=" + GetSystemID() 
						+ "&isClientSimu=" + type.getDbValue());
				formPanel.submit();
				formPanel.addListener(Events.Submit, new Listener<FormEvent>(){

					@Override
					public void handleEvent(FormEvent be) {
						// TODO Auto-generated method stub
						formPanel.unmask();
						TESWindows.ShowDownLoad(be.getResultHtml());
					}
					
				});	
			}
		};
	}

	private SelectionListener<MenuEvent> UploadTranStruct() {
		return new SelectionListener<MenuEvent>() {
			public void componentSelected(MenuEvent ce) {
				ShowUpdateTranWindow();
			}
		};
	}

	private SelectionListener<MenuEvent> DownloadTranStruct() {
		return new SelectionListener<MenuEvent>() {
			public void componentSelected(MenuEvent ce) {
				ShowDownloadTypeWindow();
			}
		};
	}

	private void PackConfig(String tranId, String tranCode, boolean isReq) {
		String tabId = tranId + (isReq ? "Req" : "Resp");
		String tabTitle = (isReq ? "请求报文结构定义" : "响应报文结构定义") + "[" + tranCode
				+ "]";
		TranStructConfig page = new TranStructConfig(tranId, type == CsType.Client,!isReq);
		AppContext.GetEntryPoint().AddTabItem(tabId, tabTitle, page);
	}

	private SelectionListener<MenuEvent> reqStructHandler() {
		return new SelectionListener<MenuEvent>() {
			public void componentSelected(MenuEvent ce) {
				GWTTransaction selectedItem = panel.getDataGrid()
						.getSelectionModel().getSelectedItems().get(0);
				PackConfig(selectedItem.getTranID(),
						selectedItem.getTranCode(), true);
			}
		};
	}

	private SelectionListener<MenuEvent> resStructHandler() {
		return new SelectionListener<MenuEvent>() {
			public void componentSelected(MenuEvent ce) {
				GWTTransaction selectedItem = panel.getDataGrid()
						.getSelectionModel().getSelectedItems().get(0);
				PackConfig(selectedItem.getTranID(),
						selectedItem.getTranCode(), false);
			}
		};
	}

	private SelectionListener<ButtonEvent> editCaseHandler() {
		return new SelectionListener<ButtonEvent>() {

			public void componentSelected(ButtonEvent ce) {
				List<GWTTransaction> selectedItems = panel.getDataGrid()
						.getSelectionModel().getSelectedItems();
				EditTran = selectedItems.get(0);
				CasePage casePage = new CasePage(EditTran, type == CsType.Client);
				AppContext.GetEntryPoint().AddTabItem(
						"CaseInfo_" + EditTran.getTranID(),
						"案例数据列表[" + EditTran.getTranName() + "]", casePage);
			}
		};
	}
	
//	private SelectionListener<ButtonEvent> caseUploadHandler(){
//		return new SelectionListener<ButtonEvent>() {
//			public void componentSelected(ButtonEvent ce) {
//				upWindow = new UploadWin(panel.getLoader());
//				upWindow.Show("批量上传案例数据(.xls)", 
//						"正在批量上传案例数据,请稍后……",
//						"MultiCaseUpload?sysId=" + GetSystemID() 
//						+ "&isClientSimu=" + type.getDbValue()+"&userId="+GetUserID()+"&isAdmin="+IsAdmin(), true);
//			}
//		};
//	}
	
	private SelectionListener<ButtonEvent> editScriptHandler() {
		return new SelectionListener<ButtonEvent>() {
			public void componentSelected(ButtonEvent ce) {
				final Window window = new Window();
				window.setHeading("脚本编辑");
				window.setSize(500, 470);
				window.setPlain(true);
				window.setModal(true);
				window.setResizable(true);
				window.setBlinkModal(false);
				window.setLayout(new FitLayout());

				final JSEdit jsEdit = new JSEdit();
				window.add(jsEdit);

				final String tranID = panel.getDataGrid().getSelectionModel()
						.getSelectedItems().get(0).getTranID();
				tranService.GetScript(tranID,
						new AsyncCallback<String>() {
							@Override
							public void onFailure(Throwable caught) {
								MessageBox.alert("错误提示", "获取脚本失败", null);
							}

							@Override
							public void onSuccess(String result) {
								jsEdit.setValue(result);
								window.show();
							}
						});

				Button btnOK = new Button("确定",
						new SelectionListener<ButtonEvent>() {
							public void componentSelected(ButtonEvent ce) {
								tranService
										.UpdateScript(tranID,
												jsEdit.getValue(),
												new AsyncCallback<Void>() {
													@Override
													public void onFailure(
															Throwable caught) {
													}

													@SuppressWarnings("deprecation")
													@Override
													public void onSuccess(
															Void result) {
														window.close();
													}
												});
							}
						});
				window.addButton(btnOK);

				window.addButton(new Button("取消",
						new SelectionListener<ButtonEvent>() {
							@SuppressWarnings("deprecation")
							public void componentSelected(ButtonEvent ce) {
								window.close();
							}
						}));
			}
		};
	}

	/**
	 * 上传交易报文结构对话框
	 * 
	 */
	private void ShowUpdateTranWindow() {
		upWindow = new UploadWin(panel.getLoader());
		upWindow.Show("上传交易报文结构(.xls)", "正在上传报文结构Excel,请稍后……",
				"TranStructServletUpload?type=multi" +
				"&systemId=" + GetSystemID()
				+ "&loginLogId=" + GetLoginLogID()
				+ "&isClientSimu=" + type.getDbValue());
	}

	private boolean hasReq = true;
	private boolean hasRes = true;
	private boolean allowEmpty = true;

	private void ShowDownloadTypeWindow() {

		window = new Window();

		window.setHeading("下载报文结构选项");
		window.setIcon(MainPage.ICONS.WebDown());
		window.setSize(250, 180);
		// window.setClosable(false);
		window.setPlain(true);
		window.setModal(true);
		window.setLayout(new FitLayout());

		final FormPanel formPanel = new FormPanel();
		formPanel.setBorders(false);
		formPanel.setBodyBorder(false);
		formPanel.setLabelWidth(0);
		formPanel.setPadding(4);
		formPanel.setHeaderVisible(false);

		// 将上传文件提交到servlet
		formPanel.setEncoding(FormPanel.Encoding.MULTIPART);
		
		formPanel.setMethod(FormPanel.Method.POST);
		formPanel
			.addListener(Events.Submit, new Listener<FormEvent>(){

					@Override
					public void handleEvent(FormEvent be) {
						// TODO Auto-generated method stub
						formPanel.unmask();
						window.hide();
						TESWindows.ShowDownLoad(be.getResultHtml());
					}
				});
		
		String labelStyle = "width:200px;";

		final CheckBox cbReq = new CheckBox();
		cbReq.setFieldLabel("交易请求报文结构");
		cbReq.setAutoWidth(true);
		cbReq.setLabelStyle(labelStyle);
		cbReq.setValue(true);
		cbReq.addListener(Events.Change, new Listener<BaseEvent>() {
			@Override
			public void handleEvent(BaseEvent be) {
				hasReq = cbReq.getValue();
			}
		});
		formPanel.add(cbReq, new FormData());

		final CheckBox cbRes = new CheckBox();
		cbRes.setFieldLabel("交易响应报文结构");
		cbRes.setAutoWidth(true);
		cbRes.setLabelStyle(labelStyle);
		cbRes.setValue(true);
		cbRes.addListener(Events.Change, new Listener<BaseEvent>() {
			@Override
			public void handleEvent(BaseEvent be) {
				hasRes = cbRes.getValue();
			}
		});
		formPanel.add(cbRes, new FormData("90%"));

		final CheckBox cbAllowEmpty = new CheckBox();
		cbAllowEmpty.setFieldLabel("非空交易报文结构");
		cbAllowEmpty.setAutoWidth(true);
		cbAllowEmpty.setLabelStyle(labelStyle);
		cbAllowEmpty.setValue(false);
		cbAllowEmpty.addListener(Events.Change, new Listener<BaseEvent>() {
			@Override
			public void handleEvent(BaseEvent be) {
				allowEmpty = !cbAllowEmpty.getValue();
			}
		});
		formPanel.add(cbAllowEmpty, new FormData("90%"));

		CheckBox cb = new CheckBox();
		cb.setFieldLabel("包含交易下案例数据");
		cb.setAutoWidth(true);
		cb.setLabelStyle(labelStyle);
		cb.setValue(false);
		cb.setEnabled(false);
		formPanel.add(cb, new FormData("90%"));

		Button btnOK = new Button("下载", new SelectionListener<ButtonEvent>() {
			public void componentSelected(ButtonEvent ce) {
				
				if (hasReq == false && hasRes == false) {
					MessageBox.alert("报文结构类型未选择", "请勾选导出报文结构类型（请求/响应）。", null);
					return;
				}
				String postUrl = "TranStructServletDownload?type=multi";
				postUrl += "&sysId=" + GetSystemID();
				postUrl += "&isClientSimu=" + type.getDbValue();
				postUrl += "&hasReq=" + String.valueOf(hasReq);
				postUrl += "&hasRes=" + String.valueOf(hasRes);
				postUrl += "&allowEmpty=" + String.valueOf(allowEmpty);
				postUrl += "&rd=" + Random.nextInt();
				formPanel.setAction(postUrl);
				formPanel.submit();
				formPanel.mask("正在生成交易报文结构,请稍后……");

			}
		});

		Button btnCancel = new Button("取消",
				new SelectionListener<ButtonEvent>() {
					@SuppressWarnings("deprecation")
					public void componentSelected(ButtonEvent ce) {
						window.close();
					}
				});

		window.add(formPanel);
		window.addButton(btnOK);
		window.addButton(btnCancel);

		window.show();
	}
}
