package com.dc.tes.ui.client.page;

import java.util.ArrayList;
import java.util.List;

import com.dc.tes.ui.client.AppContext;
import com.dc.tes.ui.client.IUseCaseService;
import com.dc.tes.ui.client.IUseCaseServiceAsync;
import com.dc.tes.ui.client.common.CookieManage;
import com.dc.tes.ui.client.common.ServiceHelper;
import com.dc.tes.ui.client.control.PostFormPanel;
import com.dc.tes.ui.client.control.RequireTextField;
import com.dc.tes.ui.client.control.StrongTreePanelDragSource;
import com.dc.tes.ui.client.model.GWTCase;
import com.dc.tes.ui.client.model.GWTCaseDirectory;
import com.dc.tes.ui.client.model.GWTCaseFlow;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.data.BaseTreeLoader;
import com.extjs.gxt.ui.client.data.BaseTreeModel;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.ModelKeyProvider;
import com.extjs.gxt.ui.client.data.RpcProxy;
import com.extjs.gxt.ui.client.data.TreeLoader;
import com.extjs.gxt.ui.client.data.TreeModel;
import com.extjs.gxt.ui.client.dnd.DND.Feedback;
import com.extjs.gxt.ui.client.dnd.TreePanelDropTarget;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.DNDEvent;
import com.extjs.gxt.ui.client.event.DNDListener;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FormEvent;
import com.extjs.gxt.ui.client.event.IconButtonEvent;
import com.extjs.gxt.ui.client.event.KeyListener;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MenuEvent;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.event.SelectionService;
import com.extjs.gxt.ui.client.event.TreePanelEvent;
import com.extjs.gxt.ui.client.store.Store;
import com.extjs.gxt.ui.client.store.TreeStore;
import com.extjs.gxt.ui.client.store.TreeStoreModel;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.TabItem;
import com.extjs.gxt.ui.client.widget.TabPanel;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.TabPanel.TabPosition;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.button.IconButton;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.StoreFilterField;
import com.extjs.gxt.ui.client.widget.form.TextArea;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.extjs.gxt.ui.client.widget.treepanel.TreePanel;
import com.extjs.gxt.ui.client.widget.treepanel.TreePanel.TreeNode;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Widget;


public class UseCasePage extends AbstractUseCaseForm {
	private IUseCaseServiceAsync useCaseService = null;
	private TreeStore<ModelData> feedStore;
	private TreePanel<ModelData> treePanel;
	GWTCaseFlow EditCaseFlow;
	BorderLayout layout = new BorderLayout();
	private GWTCaseDirectory EditCaseDirectory;
	TabPanel sourcePanel;
	TextField<String> filter;
	UploadWin upWindow = null;

	@Override
	protected void onRender(Element parent, int index) {
		super.onRender(parent, index);
		useCaseService = ServiceHelper.GetDynamicService("useCase",
				IUseCaseService.class);
		RpcProxy<List<ModelData>> proxy = new RpcProxy<List<ModelData>>() {

			@Override
			protected void load(Object loadConfig,
					AsyncCallback<List<ModelData>> callback) {
				// TODO Auto-generated method stub
				useCaseService.getUseCaseTree(GetSysInfo(),
						(GWTCaseDirectory) loadConfig, callback);
			}
		};
		final TreeLoader<ModelData> loader = new BaseTreeLoader<ModelData>(proxy);
		feedStore = new TreeStore<ModelData>(loader);
		this.setLayout(layout);

		// BorderLayoutData southData = new BorderLayoutData(LayoutRegion.SOUTH,
		// 25);
		// viewPort.add(ButtonToolWidget(), southData);
		BorderLayoutData westData = new BorderLayoutData(LayoutRegion.WEST, 190);
		westData.setMinSize(190);
		westData.setMaxSize(300);
		westData.setSplit(true);
		westData.setCollapsible(true);
		westData.setMargins(new Margins(0, 5, 5, 0));
		this.add(CreateTreePanel(), westData);

		BorderLayoutData centerData = new BorderLayoutData(LayoutRegion.CENTER);
		centerData.setMargins(new Margins());
		this.add(CreateDetailForm(), centerData);

		// add(viewPort);

	}

	/**
	 * 用例树下的工具栏
	 * @return
	 */
	private ToolBar ButtonToolWidget() {
		// TODO Auto-generated method stub
		ToolBar toolBar = new ToolBar();
		Button upLoadUseCase = new Button("上传用例", ICONS.WebUp());
		upLoadUseCase
				.addSelectionListener(new SelectionListener<ButtonEvent>() {
					public void componentSelected(ButtonEvent ce) {
						upWindow = new UploadWin(new IUserLoader() {
							@Override
							public void load() {
								// TODO Auto-generated method stub
								feedStore.getLoader().load();
							}
						});
						upWindow.Show(
								"批量上传用例数据(.xls)",
								"正在批量上传用例数据,请稍后……",
								"YQMultiCaseUpload?systemId=" + GetSystemID()
										+ "&isClientSimu=0" + "&userId="
										+ GetUserID() 
										+ "&loginLogId=" + GetLoginLogID()
										+ "&isAdmin=" + IsAdmin());
					}
				});

		toolBar.add(upLoadUseCase);

		Button switchPage = new Button("综合查询", ICONS.MoniView());
		switchPage.addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				// TODO Auto-generated method stub
				SwitchPage();
			}
		});
		toolBar.add(switchPage);

		toolBar.setStyleAttribute("border-left-style", "solid");
		toolBar.setStyleAttribute("border-left-color", "#99bbe8");
		toolBar.setStyleAttribute("border-left-width", "1px");

		return toolBar;
	}

	protected Widget CreateDetailForm() {
		// TODO Auto-generated method stub
		sourcePanel = new TabPanel();
		sourcePanel.setBorders(false);
		sourcePanel.setBodyBorder(false);
		sourcePanel.setTabScroll(true);  
		sourcePanel.setTabPosition(TabPosition.TOP);

		return sourcePanel;
	}

	/**
	 * 画用例步骤的TabItem
	 * @param item
	 * @return
	 */
	protected TabItem CaseFlowInfo(GWTCaseFlow item) {
		// TODO Auto-generated method stub
		TabItem tabItem = new TabItem("用例步骤");
		tabItem.setId("0");
		tabItem.setClosable(false);
		tabItem.setLayout(new FitLayout());
		tabItem.setBorders(false);
		tabItem.setScrollMode(Scroll.AUTO);

		BasePage page = new FlowCasesPage4YQ(sourcePanel, item);
		tabItem.add(page);
		return tabItem;
	}


	private Widget CreateTreePanel() {
		// TODO Auto-generated method stub
		ContentPanel cp = new ContentPanel();
		cp.setHeading("测试用例树");
		cp.setButtonAlign(HorizontalAlignment.CENTER);
		cp.setLayout(new FitLayout());
		//cp.setScrollMode(Scroll.AUTO);
		cp.setBorders(false);

		treePanel = new TreePanel<ModelData>(feedStore){
			private boolean expandByPath = true;
			
			@Override
			protected void onExpand(ModelData arg0, TreeNode arg1, boolean arg2) {
				// TODO Auto-generated method stub
				super.onExpand(arg0, arg1, arg2);
				
				//用于搜索后展示
				if(expandByPath) {
					BaseTreeModel treeModel = (BaseTreeModel)arg0;
					for(ModelData model : treeModel.getChildren()) {
						if(model instanceof GWTCaseDirectory) {
							treePanel.setExpanded(model, true);
						}
					}
				}
			}

			@Override
			public boolean hasChildren(ModelData parent) {
				if (parent instanceof GWTCaseDirectory) {
					return true;
				} else {
					return false;  
				}
			}
			//案例树双击打开步骤报文编辑
			@SuppressWarnings({ "rawtypes", "unchecked" })
			@Override
			protected void onDoubleClick(TreePanelEvent tpe) {
			    TreeNode node = tpe.getNode();
			    if (node != null && node.getModel() instanceof GWTCaseDirectory) {
			      setExpanded(node.getModel(), !node.isExpanded());
			    }else if(node != null && node.getModel() instanceof GWTCaseFlow){
			    	useCaseService.getFirstCase((GWTCaseFlow)node.getModel(), new AsyncCallback<GWTCase>(){

						@Override
						public void onFailure(Throwable caught) {
							// TODO Auto-generated method stub
							caught.printStackTrace();
						}

						@Override
						public void onSuccess(GWTCase result) {
							// TODO Auto-generated method stub
							if(result != null){
								String caseId = result.GetCaseId();
								TabItem tabItem = new TabItem("步骤报文编辑["
										+ result.GetCaseName() + "]");
								tabItem.setId(caseId + "caseDataEdit");
								tabItem.setClosable(true);
								tabItem.setLayout(new FitLayout());
								tabItem.setBorders(false);
								//tabItem.setScrollMode(Scroll.AUTO);
								
								BasePage page = new CaseDataPage(caseId, result.GetCaseName(),result.getN_tran(),true,true);
								tabItem.add(page);
								if(sourcePanel!=null){
									if(sourcePanel.getItemByItemId(tabItem.getId()) == null)
										sourcePanel.add(tabItem);
									sourcePanel.setSelection(tabItem);
								}
							}
						}
			    		
			    	});
			    }
			    
			  }
		
		};
		
		treePanel.getStyle().setLeafIcon(ICONS.menuCase());
		treePanel.setDisplayProperty("name");
		// treePanel.setStateful(true);
		treePanel.setWidth(200);
		treePanel.setAutoSelect(true);
		treePanel.setBorders(false);
		treePanel.setContextMenu(drawContextMenu());
		// 添加单击事件
		SelectionService.get().unregister(treePanel.getSelectionModel());
		SelectionService.get().addListener(SelectChangeListener());
		SelectionService.get().register(treePanel.getSelectionModel());
		
		final StrongTreePanelDragSource source = new StrongTreePanelDragSource(
				treePanel);
		source.setGroup("useCase");
		source.addDNDListener(new DNDListener() {
			@Override
			public void dragStart(DNDEvent e) {
				ModelData sel = treePanel.getSelectionModel().getSelectedItem();
				if (sel == null) {
					e.setCancelled(true);
					e.getStatus().setStatus(false);
					return;
				}
				super.dragStart(e);
			}
		});
		// 拖动结果后需要对改动实时地进行持久化
		TreePanelDropTarget target = new TreePanelDropTarget(treePanel) {
			@SuppressWarnings("rawtypes")
			@Override
			protected void handleInsertDrop(final DNDEvent event,
					final TreeNode item, final int index) {
				// TODO Auto-generated method stub
				MessageBox.confirm("提示", "是否确定移动操作",
						new Listener<MessageBoxEvent>() {

							@SuppressWarnings("unchecked")
							@Override
							public void handleEvent(MessageBoxEvent be) {
								// TODO Auto-generated method stub
								if (be.getButtonClicked().getText()
										.equalsIgnoreCase("yes")) {
									source.removeSource();
									List sel = event.getData();
								    if (sel.size() > 0) {
								      int idx = -1;
								      if (item.getParent() == null) {
								        idx = tree.getStore().getRootItems().indexOf(item.getModel());
								      } else {
								        idx = activeItem.getParent().indexOf(item);
								      }

								      idx = status == 0 ? idx : idx + 1;
								      if (item.getParent() == null) {
								        appendModel(null, sel, idx);
								      } else {
								        ModelData p = item.getParent().getModel();
								        appendModel(p, sel, idx);
								      }
								    }
									saveDragDrop(event, item.getParent());
								} else {
									event.getStatus().setStatus(false);
								}
							}
						});

			}

			@SuppressWarnings("rawtypes")
			@Override
			protected void handleAppendDrop(final DNDEvent event,
					final TreeNode item) {
				// TODO Auto-generated method stub
				MessageBox.confirm("提示", "是否确定移动操作",
						new Listener<MessageBoxEvent>() {

							@SuppressWarnings({ "unchecked"})
							@Override
							public void handleEvent(MessageBoxEvent be) {
								// TODO Auto-generated method stub
								if (be.getButtonClicked().getText()
										.equalsIgnoreCase("yes")) {
									source.removeSource();
									List sel = prepareDropData(event.getData(), false);
								    if (sel.size() > 0) {
								      ModelData p = null;
								      if (item != null) {
								        p = item.getModel();
								        appendModel(p, sel, tree.getStore().getChildCount(item.getModel()));
								      } else {
								        appendModel(p, sel, tree.getStore().getRootItems().size());
								      }
								    }
									saveDragDrop(event, item);
								} else {
									event.getStatus().setStatus(false);
								}
							}
						});
			}
		};
		target.addDNDListener(new DNDListener() {
			@Override
			public void dragDrop(DNDEvent e) {
				super.dragDrop(e);
			}
		});
		target.setGroup("useCase");
		target.setAllowSelfAsSource(true);
		target.setFeedback(Feedback.BOTH);
		ToolBar toolBar = new ToolBar();
		IconButton filterBtn = new IconButton("icon-refresh");
		filterBtn.setToolTip("刷新");
		filterBtn
				.addSelectionListener(new SelectionListener<IconButtonEvent>() {

					@Override
					public void componentSelected(IconButtonEvent ce) {
						// TODO Auto-generated method stub
						feedStore.removeAll();
						feedStore.getLoader().load();
					}

				});
		filterBtn.setWidth(20);
		toolBar.add(filterBtn);
		
		filter = new TextField<String>();
		filter.addKeyListener(new KeyListener() {
			public void componentKeyPress(ComponentEvent event) {
				if(event.getKeyCode() == KeyCodes.KEY_ENTER) {
					treePanel.mask();
					String value = filter.getValue();
					useCaseService.getSearchResult(GetSystemID(), value, new AsyncCallback<BaseTreeModel>() {

						@Override
						public void onFailure(Throwable caught) {
							// TODO Auto-generated method stub
							treePanel.unmask();
						}

						@Override
						public void onSuccess(BaseTreeModel result) {
							// TODO Auto-generated method stub
							treePanel.collapseAll();
							treePanel.setExpanded(result.getChild(0), true);
							treePanel.unmask();
						}
					});					
				}
			}
		});
		
		feedStore.setKeyProvider(new ModelKeyProvider<ModelData>() {

			@Override
			public String getKey(ModelData model) {
				// TODO Auto-generated method stub
				return model.get("id");
			}
			
		});
		
//		StoreFilterField<ModelData> treeFilter = GetNameFilter(true);
		toolBar.add(filter);
//		treeFilter.bind(feedStore);
		cp.setTopComponent(toolBar);
		cp.setBottomComponent(ButtonToolWidget());
		cp.add(treePanel);
		return cp;
	}

	@SuppressWarnings("rawtypes")
	protected void saveDragDrop(DNDEvent e, TreeNode parent) {
		// TODO Auto-generated method stub
		List<TreeStoreModel> sel = e.getData();
		String id;
		if(parent == null){
			id = "0";
		}else{
			id = ((GWTCaseDirectory) parent.getModel()).GetID();
		}
		if (sel != null) {
			for (TreeStoreModel item : sel) {
				// TreeNode parent = treePanel.findNode(e.getTarget());
				if (item.getModel() instanceof GWTCaseFlow) {
					GWTCaseFlow gwtCaseFlow = (GWTCaseFlow) item.getModel();
					gwtCaseFlow.set(GWTCaseFlow.N_DirectoryID, id);
					useCaseService.saveOrUpdateCaseFlow(gwtCaseFlow, GetLoginLogID(),
							new AsyncCallback<GWTCaseFlow>() {

								@Override
								public void onFailure(Throwable caught) {
									// TODO Auto-generated method stub
									MessageBox.alert("错误提示", "保存拖动失败", null);
									caught.getStackTrace();
								}

								@Override
								public void onSuccess(GWTCaseFlow result) {
									// TODO Auto-generated method stub
									feedStore.update(result);
								}
							});
				} else {
					GWTCaseDirectory gwtCaseDirectory = (GWTCaseDirectory) item
							.getModel();
					gwtCaseDirectory.set(GWTCaseDirectory.N_ParentDirID, id);
					useCaseService.saveOrUpdateDirectory(gwtCaseDirectory,
							new AsyncCallback<GWTCaseDirectory>() {

								@Override
								public void onFailure(Throwable caught) {
									// TODO Auto-generated method stub
									MessageBox.alert("错误提示", "保存拖动失败", null);
									caught.getStackTrace();
								}

								@Override
								public void onSuccess(GWTCaseDirectory result) {
									// TODO Auto-generated method stub
									feedStore.update(result);
								}
							});
				}

			}
		}
	}


	private SelectionChangedListener<? extends ModelData> SelectChangeListener() {
		// TODO Auto-generated method stub
		return new SelectionChangedListener<TreeModel>() {
			@Override
			public void selectionChanged(SelectionChangedEvent<TreeModel> event) {
				List<TreeModel> sel = event.getSelection();
				if (sel.size() > 0) {
					final TreeModel m = (TreeModel) event.getSelection().get(0);
					if (m != null && m instanceof GWTCaseFlow) {
						if (sourcePanel.getItemCount() != 0) {
							sourcePanel.removeAll();
						}
						EditCaseFlow = (GWTCaseFlow)m;
						TabItem tabItem = BaseUseCaseInfo(sourcePanel, EditCaseFlow);
							
							
						sourcePanel.add(tabItem);
						sourcePanel.add(CaseFlowInfo((GWTCaseFlow) m));
						sourcePanel.repaint();
						sourcePanel.setSelection(tabItem);
						return;
					} else if (m != null && m instanceof GWTCaseDirectory) {
						if (sourcePanel.getItemCount() != 0) {
							sourcePanel.removeAll();
						}
						TabItem tabItem = drawDirectoryMsg((GWTCaseDirectory) m);
						sourcePanel.add(tabItem);
						sourcePanel.setSelection(tabItem);
						return;
					}

				}
			}

		};
	}

	/**
	 * 画用例树的右键菜单栏
	 * 
	 * @return
	 */
	private Menu drawContextMenu() {
		// TODO Auto-generated method stub
		Menu contextMenu = new Menu();

		final MenuItem insertDir = new MenuItem();
		insertDir.setText("新增目录");
		insertDir.setIcon(ICONS.AddCom());
		insertDir.addSelectionListener(new SelectionListener<MenuEvent>() {
			public void componentSelected(MenuEvent ce) {
				final ModelData folder = treePanel.getSelectionModel()
						.getSelectedItem();
				GWTCaseDirectory parent = new GWTCaseDirectory();
				if (folder instanceof GWTCaseDirectory) {
					parent = (GWTCaseDirectory) folder;
				} else {
					parent = (GWTCaseDirectory) feedStore.getParent(folder);
				}
				EditCaseDirectory = new GWTCaseDirectory();
				CreateDirEditForm(parent, false);

			}
		});
		contextMenu.add(insertDir);

		final MenuItem insertRootDir = new MenuItem();
		insertRootDir.setText("新增根目录");
		insertRootDir.setIcon(ICONS.AddCom());
		insertRootDir.addSelectionListener(new SelectionListener<MenuEvent>() {
			public void componentSelected(MenuEvent ce) {
				GWTCaseDirectory parent = new GWTCaseDirectory();
				parent.set(GWTCaseDirectory.N_ID, "0");
				EditCaseDirectory = new GWTCaseDirectory();
				CreateDirEditForm(parent, true);

			}
		});
		contextMenu.add(insertRootDir);

		final MenuItem insertCase = new MenuItem();
		insertCase.setText("新增测试用例");
		insertCase.setIcon(ICONS.AddCom());
		insertCase.addSelectionListener(new SelectionListener<MenuEvent>() {
			public void componentSelected(MenuEvent ce) {
				final ModelData folder = treePanel.getSelectionModel()
						.getSelectedItem();
				EditCaseFlow = new GWTCaseFlow();
				CreateEditForm(EditCaseFlow, folder);
			}
		});

		contextMenu.add(insertCase);
		final MenuItem rename = new MenuItem();
		rename.setText("重命名");
		rename.setIcon(ICONS.CaseDataEdit());
		rename.addSelectionListener(new SelectionListener<MenuEvent>() {

			@Override
			public void componentSelected(MenuEvent ce) {
				// TODO Auto-generated method stub
				final ModelData folder = treePanel.getSelectionModel()
						.getSelectedItem();
				if (folder != null && folder instanceof GWTCaseDirectory) {
					final MessageBox box = MessageBox.prompt("重命名文件夹",
							"请输入新的文件夹名称:");

					box.addCallback(new Listener<MessageBoxEvent>() {
						public void handleEvent(MessageBoxEvent be) {
							Button msgBtn = be.getButtonClicked();
							if (msgBtn.getText().equalsIgnoreCase("OK")) {
								final GWTCaseDirectory gwtCaseDirectory = (GWTCaseDirectory) folder;
								gwtCaseDirectory.set(GWTCaseDirectory.N_Name,
										be.getValue());
								useCaseService.saveOrUpdateDirectory(
										gwtCaseDirectory,
										new AsyncCallback<GWTCaseDirectory>() {

											@Override
											public void onFailure(
													Throwable caught) {
												// TODO Auto-generated method
												// stub
												caught.printStackTrace();
												MessageBox.alert("错误提示",
														"重命名失败", null);
											}

											@Override
											public void onSuccess(
													GWTCaseDirectory result) {
												// TODO Auto-generated method
												// stub
												feedStore
														.update(gwtCaseDirectory);
											}
										});
							}
						}
					});
				}
			}
		});
		contextMenu.add(rename);

		// 添加事件，当选择的是用例而不是文件夹时无法进行目录添加

		final MenuItem remove = new MenuItem();
		remove.setText("删除");
		remove.setIcon(ICONS.Remove());
		remove.addSelectionListener(new SelectionListener<MenuEvent>() {
			public void componentSelected(MenuEvent ce) {
				final List<ModelData> selected = treePanel.getSelectionModel()
						.getSelectedItems();
				MessageBox.confirm("提示信息", "是否确认删除",
						new Listener<MessageBoxEvent>() {
							public void handleEvent(MessageBoxEvent be) {
								Button msgBtn = be.getButtonClicked();
								if (msgBtn.getText().equalsIgnoreCase("Yes")) {
									for (ModelData sel : selected) {
										if (sel instanceof GWTCaseDirectory) {
											Integer count = feedStore
													.getChildren(sel).size();
											if (count != 0) {
												MessageBox
														.alert("错误提示",
																"选中的文件夹【"
																		+ ((GWTCaseDirectory) sel)
																				.GetName()
																		+ "】不为空，无法删除！",
																null);
												return;
											}
										}
									}
									final ModelData parent = feedStore
											.getParent(selected.get(0));
									useCaseService.deleteSelectedItem(selected, GetLoginLogID(),
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
													// method stub
													if (result) {
														for (ModelData sel : selected) {
															feedStore
																	.remove(sel);
														}
													} else {
														MessageBox
																.alert("错误提示",
																		"选中文件夹不为空，删除失败",
																		null);
													}
													treePanel
															.getSelectionModel()
															.deselectAll();
													treePanel
															.getSelectionModel()
															.select(parent,
																	true);
												}
											});
								}
							}
						});
			}
		});
		contextMenu.add(remove);
		contextMenu.addListener(Events.BeforeShow, new Listener<BaseEvent>() {

			@Override
			public void handleEvent(BaseEvent be) {
				// TODO Auto-generated method stub
				final ModelData item = treePanel.getSelectionModel()
						.getSelectedItem();
				if (item instanceof GWTCaseDirectory) {
					rename.setVisible(true);
					remove.setVisible(true);
				} else {
					rename.setVisible(false);
					remove.setVisible(true);
				}
			}
		});

		return contextMenu;
	}

	// 根据名称对树进行筛选
	private StoreFilterField<ModelData> GetNameFilter(final boolean b) {
		return new StoreFilterField<ModelData>() {

			@Override
			protected boolean doSelect(Store<ModelData> store,
					ModelData parent, ModelData child, String property,
					String filter) {
				String name = child.get("name").toString().toLowerCase();
				if (name.indexOf(filter.toLowerCase()) != -1) {
					return true;
				} else if (b && (child instanceof GWTCaseDirectory))
					return true;
				return false;
			}

		};
	}

	private TabItem drawDirectoryMsg(final GWTCaseDirectory item) {
		TabItem tabItem = new TabItem("目录信息");
		tabItem.setId("1");
		tabItem.setClosable(false);
		tabItem.setLayout(new FitLayout());
		tabItem.setBorders(false);
		tabItem.setScrollMode(Scroll.AUTO);

		FormPanel cp = new FormPanel();
		cp.setHeaderVisible(false);
		cp.setBodyBorder(false);
		cp.setBorders(false);
		cp.setScrollMode(Scroll.AUTO);
		cp.setFrame(false);

		String tableHead = "<table width=\"95%\" style=\"border:1px solid #cad9ea;color:#666; table-layout:fixed;"
				+ "empty-cells:show; border-collapse: collapse; margin:0 auto;font-size:12px;\">";
		String tdLabel = "<td width=\"14%\" style=\"font-size:12px; border:1px solid #cad9ea;padding:0 1em 0;background-color:#f5fafe; text-align:right;min-height:20px;\">";
		String tdContent = "<td width=\"36%\" style=\"font-size:12px; border:1px solid #cad9ea;padding:0 1em 0; min-height:20px;\">";
		String tdContentConb = "<td width=\"36%\" colspan=\"3\" style=\"font-size:12px; border:1px solid #cad9ea;padding:0 1em 0; min-height:20px;\">";
		String tdEnd = "</td>";
		String tableEnd = "</table>";

		StringBuffer htmlStr = new StringBuffer();
		htmlStr.append(tableHead);
		htmlStr.append("<tr>" + tdLabel + "目录名称" + tdEnd);
		htmlStr.append(tdContent + item.GetName() + tdEnd);
		htmlStr.append(tdLabel + "路径" + tdEnd);
		htmlStr.append(tdContent + item.GetPath() + tdEnd + "</tr>");
		htmlStr.append("<tr>" + tdLabel + "目录描述" + tdEnd);
		htmlStr.append(tdContentConb
				+ (item.GetDesc() == null ? "" : item.GetDesc().replace("\n",
						"<br/>")) + tdEnd + "</tr>");
		htmlStr.append(tableEnd);
		Html html = new Html(htmlStr.toString());

		cp.add(html);

		ToolBar toolBar = new ToolBar();

		Button btn_export = new Button("导出用例", ICONS.WebDown());
		btn_export.addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				// TODO Auto-generated method stub
				MessageBox.confirm("提示信息", "是否导出目录下（包括子目录）的所有用例？",
						new Listener<MessageBoxEvent>() {
							public void handleEvent(MessageBoxEvent be) {
								Button msgBtn = be.getButtonClicked();
								if (msgBtn.getText().equalsIgnoreCase("Yes")) {
									String pathID = item.GetID();
									final PostFormPanel formPanel = new PostFormPanel();
									formPanel.setEncoding(FormPanel.Encoding.MULTIPART);
									formPanel.setMethod(FormPanel.Method.POST);
									formPanel.setAction("UseCaseInfoExport?"
													+ "systemID=" + GetSystemID()
													+ "&caseFlowNo="
													+ "&caseFlowName="
													+ "&designer="
													+ "&startDate="
													+ "&endDate=" + "&pathId="
													+ pathID
													);
									formPanel.submit();
									formPanel.addListener(Events.Submit, new Listener<FormEvent>(){

										@Override
										public void handleEvent(FormEvent be) {
											// TODO Auto-generated method stub
											TESWindows.ShowDownLoad(be.getResultHtml());
										}
										
									});	
								}
							};
						});
			}
		});
		toolBar.add(btn_export);
		Button btn_update = new Button("修改目录信息");
		btn_update.setIcon(ICONS.EditCom());
		btn_update.addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				// TODO Auto-generated method stub
				EditCaseDirectory = item;
				CreateDirEditForm(EditCaseDirectory, false);
			}
		});
		toolBar.add(btn_update);
		Button btn_del = new Button("删除目录");
		btn_del.addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				// TODO Auto-generated method stub
				MessageBox.confirm("提示信息", "是否确认删除",
						new Listener<MessageBoxEvent>() {
							public void handleEvent(MessageBoxEvent be) {
								Button msgBtn = be.getButtonClicked();
								if (msgBtn.getText().equalsIgnoreCase("Yes")) {
									List<ModelData> list = new ArrayList<ModelData>();
									list.add(item);
									Integer count = feedStore.getChildren(item)
											.size();
									if (count != 0) {
										MessageBox.alert("错误提示",
												"选中的文件夹【" + item.GetName()
														+ "】不为空，无法删除！", null);
										return;
									}
									final GWTCaseDirectory parent = (GWTCaseDirectory) feedStore
											.getParent(item);
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
													// method stub
													if (result) {
														feedStore.remove(item);
													} else {
														MessageBox
																.alert("错误提示",
																		"选中文件夹不为空，删除失败",
																		null);
													}
													treePanel
															.getSelectionModel()
															.deselectAll();

													treePanel
															.getSelectionModel()
															.select(parent,
																	true);

												}
											});
								}
							}
						});
			}
		});

		btn_del.setIcon(ICONS.DelCom());
		toolBar.add(btn_del);

		toolBar.setAlignment(HorizontalAlignment.LEFT);
		toolBar.setStyleAttribute("border-left-style", "solid");
		toolBar.setStyleAttribute("border-left-color", "#99bbe8");
		toolBar.setStyleAttribute("border-left-width", "1px");
		cp.setTopComponent(toolBar);
		tabItem.add(cp);
		return tabItem;

	}

	private void CreateDirEditForm(final GWTCaseDirectory parent, boolean isRoot) {
		// TODO Auto-generated method stub
		final Window window = new Window();
		window.setScrollMode(Scroll.AUTOY);
		window.setWidth(400);
		window.setModal(true);
		window.setPlain(true);
		window.setLayout(new FitLayout());
		window.setHeight(180);

		final FormPanel fp = new FormPanel();
		fp.setHeaderVisible(false);
		fp.setBodyBorder(false);
		fp.setBorders(false);
		fp.setPadding(5);
		fp.setHeaderVisible(false);
		fp.setScrollMode(Scroll.AUTOY);
		FormData formdata = new FormData("90%");

		final RequireTextField rtfName = new RequireTextField("目录名称");
		rtfName.setName(GWTCaseDirectory.N_Name);
		rtfName.focus();
		fp.add(rtfName, formdata);

		final TextArea taDesc = new TextArea();
		taDesc.setFieldLabel("目录描述");
		taDesc.setName(GWTCaseDirectory.N_Desc);
		fp.add(taDesc, formdata);

		window.add(fp);

		final boolean isNew = EditCaseDirectory.IsNew();
		Button btn_save = new Button("保存",
				new SelectionListener<ButtonEvent>() {

					@Override
					public void componentSelected(ButtonEvent ce) {
						// TODO Auto-generated method stub
						if (!fp.isValid())
							return;
						EditCaseDirectory.SetValue(GetSystemID(), rtfName
								.getValue(), isNew ? parent.GetID()
								: EditCaseDirectory.GetParentDirID(), taDesc
								.getValue());
						useCaseService.saveOrUpdateDirectory(EditCaseDirectory,
								new AsyncCallback<GWTCaseDirectory>() {

									@Override
									public void onFailure(Throwable caught) {
										// TODO Auto-generated method
										// stub
										caught.printStackTrace();
										MessageBox.alert("错误提示", "保存失败", null);
									}

									@Override
									public void onSuccess(
											GWTCaseDirectory result) {
										// TODO Auto-generated method
										// stub
										if (isNew) {
											if (parent.GetID().equals("0")) {
												feedStore.add(result, false);
											} else {
												feedStore.add(parent, result,
														false);
											}
											treePanel.setExpanded(parent, true);
										} else {
											feedStore.update(EditCaseDirectory);
										}
										window.hide();
										treePanel.getSelectionModel()
												.deselectAll();
										treePanel.getSelectionModel().select(
												result, true);
									}
								});
					}
				});
		window.addButton(btn_save);

		window.addButton(new Button("取消", new SelectionListener<ButtonEvent>() {
			public void componentSelected(ButtonEvent ce) {
				window.hide();
			}
		}));

		if (isNew) {
			if (isRoot)
				window.setHeading("新增根目录");
			else
				window.setHeading("新增目录");
		} else {
			window.setHeading("修改目录");
			taDesc.setValue(EditCaseDirectory.GetDesc());
			rtfName.setValue(EditCaseDirectory.GetName());
		}
		window.show();
	}

	private void SwitchPage() {
		// TODO Auto-generated method stub
		TabItem tabItem = new TabItem("综合查询");
		tabItem.setId("3");
		tabItem.setClosable(true);
		tabItem.setLayout(new FitLayout());
		tabItem.setBorders(false);
		tabItem.setScrollMode(Scroll.AUTO);
		BasePage page = new ExtraUseCasePage();
		tabItem.add(page);
		AppContext.GetEntryPoint().AddTabItem("333", "综合查询", page);

	}
	
	@Override
	public void EditUseCaseSuccHandler(GWTCaseFlow result) {
		// TODO Auto-generated method stub
		final boolean isNew = EditCaseFlow.IsNew();
		final ModelData folder = treePanel.getSelectionModel()
		.getSelectedItem();
		if (isNew)
			feedStore
					.add(folder instanceof GWTCaseDirectory ? folder
							: feedStore
									.getParent(folder),
							result, false);
		else {
			feedStore.update(EditCaseFlow);
		}
		treePanel.setExpanded(folder, true);
		
		// 打开用例信息
		treePanel.getSelectionModel()
				.deselectAll();
		treePanel.getSelectionModel().select(
				result, true);

	}
	
	@Override
	public void DeleteUseCaseSuccHandler(Boolean result) {
		// TODO Auto-generated method stub
		final ModelData parent = feedStore
		.getParent(EditCaseFlow);

		if (result) {
			feedStore.remove(EditCaseFlow);
		} else {
			MessageBox
					.alert("错误提示",
							"选中文件夹不为空，删除失败",
							null);
		}
		treePanel
				.getSelectionModel()
				.deselectAll();
		treePanel
				.getSelectionModel()
				.select(parent,
						true);
	}
}
