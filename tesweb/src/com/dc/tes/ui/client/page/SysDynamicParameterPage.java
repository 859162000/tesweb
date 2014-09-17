package com.dc.tes.ui.client.page;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.dc.tes.ui.client.ISysDynamicParameter;
import com.dc.tes.ui.client.ISysDynamicParameterAsync;
import com.dc.tes.ui.client.MainPage;
import com.dc.tes.ui.client.common.ServiceHelper;
import com.dc.tes.ui.client.control.ConfigToolBar;
import com.dc.tes.ui.client.control.FormContentPanel;
import com.dc.tes.ui.client.control.RequireTextField;
import com.dc.tes.ui.client.control.StrongTreeGridDragSource;
import com.dc.tes.ui.client.control.TreeGridContentPanel;
import com.dc.tes.ui.client.model.GWTParameterDirectory;
import com.dc.tes.ui.client.model.GWTSysDynamicPara;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.data.BaseTreeModel;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.RpcProxy;
import com.extjs.gxt.ui.client.dnd.TreeGridDropTarget;
import com.extjs.gxt.ui.client.dnd.DND.Feedback;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.DNDEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MenuEvent;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.TreeStoreModel;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.TextArea;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.extjs.gxt.ui.client.widget.toolbar.FillToolItem;
import com.extjs.gxt.ui.client.widget.treegrid.TreeGrid;
import com.extjs.gxt.ui.client.widget.treegrid.TreeGridCellRenderer;
import com.extjs.gxt.ui.client.widget.treegrid.TreeGrid.TreeNode;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class SysDynamicParameterPage extends BasePage {
	
	ISysDynamicParameterAsync sysParaService = null;
	GWTSysDynamicPara EditPara = null;
	GWTParameterDirectory EditPrarmDir = null;

	TreeGridContentPanel<BaseTreeModel> panel;
	FormContentPanel<BaseTreeModel> detailPanel;
	ConfigToolBar configBar;
	UploadWin upWindow = null;
	public SysDynamicParameterPage() {
		
	}
	
	@Override
	protected void onRender(Element parent, int index) {
		super.onRender(parent, index);

		sysParaService = ServiceHelper.GetDynamicService("sysPara", ISysDynamicParameter.class);
		panel = new TreeGridContentPanel<BaseTreeModel>(){
			@Override
			public boolean HasChildren(BaseTreeModel parent) {
				return parent instanceof GWTParameterDirectory;
			};
		};
		RpcProxy<List<BaseTreeModel>> proxy = new RpcProxy<List<BaseTreeModel>>() {
			@Override
			public void load(Object loadConfig,
					AsyncCallback<List<BaseTreeModel>> callback) {
				sysParaService.getSysDynamicParamTree((BaseTreeModel)loadConfig, GetSystemID(),
						callback);
			}
		};

		panel.setProxy(proxy);
		panel.setColumns(GetColumnConfig());
		panel.DrowSearchBar();
		panel.DrowGridView(GWTSysDynamicPara.N_ParameterName, true, false);
		panel.getTreeGrid().getStyle().setLeafIcon(ICONS.PageWhite());
		panel.setContextMenu(drawContextMenu());
		configBar = new ConfigToolBar();
		
		configBar.AddButton("btnRefresh",new Button("刷新"), MainPage.ICONS
				.Refresh(), refreshHandler());
		
		configBar.AddButton("btnParamUpload",new Button("上传系统参数"), MainPage.ICONS
				.WebUp(), paramUploadHandler());
		configBar.AddWidget(new FillToolItem());
		configBar.AddNewBtn("btnAdd", AddHandler());
		configBar.AddEditBtn("btnEdit", EditHandler());
		configBar.AddDelBtn("btnDel", DelHandler());
		InitBtnConfigBar(configBar);
		panel.setBottomBar(configBar);
		add(panel);

		//处理拖拽操作
		handleDNDEvent(panel.getTreeGrid());
		
		detailPanel = new FormContentPanel<BaseTreeModel>();
		detailPanel.setBindInfo(GetDetailHashMap());
		panel.setDetailForm(detailPanel);
		add(detailPanel);
	
		
		panel.setSearchButtonEvent(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				// TODO Auto-generated method stub
				panel.getTreeGrid().mask();
				sysParaService.GetSearchResult(GetSystemID(), panel.GetSearchCondition(), new AsyncCallback<BaseTreeModel>() {

					@Override
					public void onFailure(Throwable caught) {
						// TODO Auto-generated method stub
						panel.getTreeGrid().unmask();
					}

					@Override
					public void onSuccess(BaseTreeModel result) {
						// TODO Auto-generated method stub
						panel.getTreeGrid().collapseAll();
						panel.getTreeGrid().setExpanded((BaseTreeModel) result.getChild(0), true);
						panel.getTreeGrid().unmask();
					}
					
				});
			}
			
		});
	}


	private SelectionListener<ButtonEvent> paramUploadHandler() {
		// TODO Auto-generated method stub
		return new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				// TODO Auto-generated method stub
				upWindow = new UploadWin(new IUserLoader() {
					@Override
					public void load() {
						// TODO Auto-generated method stub
						panel.getStore().getLoader().load();
					}
				});
				upWindow.Show(
						"批量上传系统动态参数(.xls)",
						"正在批量上传系统动态参数,请稍后……",
						"SystemDynamicParaUpload?sysId=" + GetSystemID()
						+ "&loginLogId=" + GetLoginLogID() 
						+ "&userId=" + GetUserID());
			}
			
		};
	}

	private SelectionListener<ButtonEvent> refreshHandler() {
		// TODO Auto-generated method stub
		return new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				// TODO Auto-generated method stub
				panel.reloadGrid();
			}
		};
	}

	private void handleDNDEvent(TreeGrid<BaseTreeModel> treeGrid) {
		// TODO Auto-generated method stub
		final StrongTreeGridDragSource source = new StrongTreeGridDragSource(treeGrid);  
		  
		TreeGridDropTarget target = new TreeGridDropTarget(treeGrid){
			@SuppressWarnings("rawtypes")
			@Override
			protected void handleAppendDrop(final DNDEvent event, final TreeNode item) {
				MessageBox.confirm("提示", "是否确定移动操作",
						new Listener<MessageBoxEvent>() {

							@Override
							public void handleEvent(MessageBoxEvent be) {
								// TODO Auto-generated method stub
								if (be.getButtonClicked().getText()
										.equalsIgnoreCase("yes")) {
									source.removeSource();
									List<ModelData> models = prepareDropData(event.getData(), false);
								    if (models.size() > 0) {
								      ModelData p = null;
								      if (item != null) {
								        p = item.getModel();
								        appendModel(p, models, treeGrid.getTreeStore().getChildCount(item.getModel()));
								      } else {
								        appendModel(p, models, treeGrid.getTreeStore().getRootItems().size());
								      }
								      saveDragDrop(event, item);
								    }
								}else{
									event.getStatus().setStatus(false);
								}
							}
				});
			  }
			
			@SuppressWarnings("rawtypes")
			@Override
			 protected void handleInsertDrop(final DNDEvent event, final TreeNode item, int index) {
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
								      int idx = treeGrid.getTreeStore().indexOf(item.getModel());
								      idx = status == 0 ? idx : idx + 1;
								      if (item.getParent() != null) {
								        ModelData p = item.getParent().getModel();
								        appendModel(p, sel, idx);
								      } else {
								        appendModel(null, sel, idx);
								      }
								    }
								    saveDragDrop(event, item.getParent());
								  }else{
									event.getStatus().setStatus(false);
								}
							}
				});
			}  
		};
	    target.setAllowSelfAsSource(true);  
	    target.setFeedback(Feedback.BOTH);
	}

	@SuppressWarnings("rawtypes")
	private void saveDragDrop(DNDEvent event, TreeNode parent) {
		// TODO Auto-generated method stub
		List<TreeStoreModel> sel = event.getData();
		Integer id;
		if(parent == null){
			id = 0;
		}else{
			id = ((GWTParameterDirectory) parent.getModel()).GetID();
		}
		if (sel != null) {
			for (TreeStoreModel item : sel) {
				// TreeNode parent = treePanel.findNode(e.getTarget());
				if (item.getModel() instanceof GWTSysDynamicPara) {
					GWTSysDynamicPara gwtSysParam = (GWTSysDynamicPara) item.getModel();
					gwtSysParam.set(GWTSysDynamicPara.N_DirectoryID, id);
					sysParaService.SaveSysDynamicPara(gwtSysParam, GetLoginLogID(),
							new AsyncCallback<GWTSysDynamicPara>() {

								@Override
								public void onFailure(Throwable caught) {
									// TODO Auto-generated method stub
									MessageBox.alert("错误提示", "保存拖动失败", null);
									caught.getStackTrace();
								}

								@Override
								public void onSuccess(GWTSysDynamicPara result) {
									// TODO Auto-generated method stub
									panel.getStore().update(result);
								}
							});
				} else {
					GWTParameterDirectory gwtParamDir = (GWTParameterDirectory) item
							.getModel();
					gwtParamDir.set(GWTParameterDirectory.N_ParentDirID, id);
					sysParaService.saveOrUpdateParamDirectory(gwtParamDir,
							new AsyncCallback<GWTParameterDirectory>() {

								@Override
								public void onFailure(Throwable caught) {
									// TODO Auto-generated method stub
									MessageBox.alert("错误提示", "保存拖动失败", null);
									caught.getStackTrace();
								}

								@Override
								public void onSuccess(GWTParameterDirectory result) {
									// TODO Auto-generated method stub
									panel.getStore().update(result);
								}
							});
				}

			}
		}
	}
	
	private List<ColumnConfig> GetColumnConfig() {
		List<ColumnConfig> columns = new ArrayList<ColumnConfig>();
		ColumnConfig name = new ColumnConfig(GWTSysDynamicPara.N_ParameterName, "参数名称",100);
		name.setRenderer(new TreeGridCellRenderer<BaseTreeModel>());  
		columns.add(name);
		
		columns.add(new ColumnConfig(GWTSysDynamicPara.N_ParameterDesc, "参数描述", 100));
		columns.add(new ColumnConfig(GWTSysDynamicPara.N_ParameterTypeStr, "参数类型", 80));
		//columns.add(new ColumnConfig(GWTSysDynamicPara.N_ParameterHostPort, "主机端口", 60));
		columns.add(new ColumnConfig(GWTSysDynamicPara.N_DisplayFlagStr, "可视", 40));
		columns.add(new ColumnConfig(GWTSysDynamicPara.N_CompareConditionStr, "比较条件", 80));
		columns.add(new ColumnConfig(GWTSysDynamicPara.N_ParameterHostTypeStr, "参数所在主机类型", 100));
		columns.add(new ColumnConfig(GWTSysDynamicPara.N_ParameterHostIP, "主机地址", 80));
		columns.add(new ColumnConfig(GWTSysDynamicPara.N_ParameterExpression, "参数表达式", 200));

		return columns;
	}


	public Map<String, String> GetDetailHashMap() {

		Map<String, String> detailMap = new LinkedHashMap<String, String>();
		detailMap.put(GWTSysDynamicPara.N_ParameterName, "参数名称");
		detailMap.put(GWTSysDynamicPara.N_ParameterDesc, "参数描述");
		detailMap.put(GWTSysDynamicPara.N_ParameterTypeStr, "参数类型");
		detailMap.put(GWTSysDynamicPara.N_ParameterHostTypeStr, "参数所在主机类型");
		detailMap.put(GWTSysDynamicPara.N_ParameterHostIP, "主机地址");
		//detailMap.put(GWTSysDynamicPara.N_ParameterHostPort, "主机端口");
		detailMap.put(GWTSysDynamicPara.N_DisplayFlagStr, "是否可视");
		detailMap.put(GWTSysDynamicPara.N_IsValidStr, "是否有效");
		detailMap.put(GWTSysDynamicPara.N_RefetchFlagStr, "是否需回溯获取");
		detailMap.put(GWTSysDynamicPara.N_CompareConditionStr, "比较条件");
		detailMap.put(GWTSysDynamicPara.N_DefaultExpectedValue, "参数默认预期值值");
		detailMap.put(GWTSysDynamicPara.N_ParameterExpression, "参数表达式");
		return detailMap;
	}
	
	
	void CreateEditForm(final BaseTreeModel folder){
		if (folder instanceof GWTParameterDirectory) {
			EditPara.SetDirectoryID(((GWTParameterDirectory) folder).GetID());			
		} else {
			EditPara.SetDirectoryID(((GWTSysDynamicPara)folder).GetDirectoryID());					
		}
		SysParamEditWindow.CreateEditForm(EditPara,  
				panel, GetSystemID(), GetLoginLogID(), false);
	}
	

	private SelectionListener<ButtonEvent> AddHandler() {
		return new SelectionListener<ButtonEvent>() {
			public void componentSelected(ButtonEvent ce) {
				doAddHandler();
			}
		};
	}
	
	private void doAddHandler(){
		final BaseTreeModel folder = panel.getTreeGrid().getSelectionModel()
				.getSelectedItem();
		EditPara = new GWTSysDynamicPara(GetSystemID());
		CreateEditForm(folder);
	}
	
	private SelectionListener<ButtonEvent> EditHandler() {
		return new SelectionListener<ButtonEvent>() {
			public void componentSelected(ButtonEvent ce) {

				BaseTreeModel selectedItem = panel.getTreeGrid()
						.getSelectionModel().getSelectedItems().get(0);
				if(selectedItem instanceof GWTSysDynamicPara){
					EditPara = (GWTSysDynamicPara)selectedItem;
					SysParamEditWindow.CreateEditForm(EditPara,  
							panel, GetSystemID(), GetLoginLogID(), false);
				}else if(selectedItem instanceof GWTParameterDirectory){
					EditPrarmDir = (GWTParameterDirectory)selectedItem;
					CreateDirEditForm(EditPrarmDir, false);
				}
			}
		};
	}

	
	/**
	 * 处理删除。 如果选择有目录，则判断目录是否为空，为空才可删除。
	 * @return
	 */
	private Listener<MessageBoxEvent> DelHandler() {
		return new Listener<MessageBoxEvent>() {
			public void handleEvent(MessageBoxEvent be) {
				Button msgBtn = be.getButtonClicked();
				if (msgBtn.getText().equalsIgnoreCase("Yes")) {
					final List<BaseTreeModel> selected = panel.getSelection();
					for (BaseTreeModel sel : selected) {
						if (sel instanceof GWTParameterDirectory) {
							Integer count = panel.getStore()
									.getChildren(sel).size();
							if (count != 0) {
								MessageBox
										.alert("错误提示",
												"选中的文件夹【"
														+ ((GWTParameterDirectory) sel)
																.GetName()
														+ "】不为空，无法删除！",
												null);
								return;
							}
						}
					}
					
					sysParaService.DeleteSysDynamicParaItems(selected, GetLoginLogID(),
							new AsyncCallback<Boolean>() {
								public void onFailure(Throwable caught) {
									caught.printStackTrace();
									MessageBox.alert("错误提示", "删除失败", null);
								}

								public void onSuccess(Boolean result) {
									if (result) {
										for (BaseTreeModel sel : selected) {								
											panel.getStore().remove(sel);
										}
									} else {
										MessageBox
												.alert("错误提示",
														"选中文件夹不为空，删除失败",
														null);
									}
									
								}
							});
				}
			}
		};
	}
	
	/**
	 * 文件夹编辑对话框
	 * @param parent
	 * @param isRoot
	 */
	private void CreateDirEditForm(final GWTParameterDirectory parent, boolean isRoot) {
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
		rtfName.setName(GWTParameterDirectory.N_Name);
		rtfName.focus();
		fp.add(rtfName, formdata);

		final TextArea taDesc = new TextArea();
		taDesc.setFieldLabel("目录描述");
		taDesc.setName(GWTParameterDirectory.N_Desc);
		fp.add(taDesc, formdata);

		window.add(fp);

		final boolean isNew = EditPrarmDir.IsNew();
		Button btn_save = new Button("保存",
				new SelectionListener<ButtonEvent>() {

					@Override
					public void componentSelected(ButtonEvent ce) {
						// TODO Auto-generated method stub
						if (!fp.isValid())
							return;
						EditPrarmDir.SetValue(GetSystemID(),  isNew ? parent.GetID()
								: EditPrarmDir.GetParentDirID(), Integer.valueOf(0), rtfName
								.getValue(),taDesc
								.getValue());
						sysParaService.saveOrUpdateParamDirectory(EditPrarmDir,
								new AsyncCallback<GWTParameterDirectory>() {

									@Override
									public void onFailure(Throwable caught) {
										// TODO Auto-generated method
										// stub
										caught.printStackTrace();
										MessageBox.alert("错误提示", "保存失败", null);
									}

									@Override
									public void onSuccess(
											GWTParameterDirectory result) {
										// TODO Auto-generated method
										// stub
										if (isNew) {
											if (parent.GetID()== 0) {
												panel.getStore().add(result, false);
											} else {
												panel.getStore().add(parent, result,
														false);
											}
											panel.getTreeGrid().setExpanded(parent, true);
										} else {
											panel.getStore().update(EditPrarmDir);
										}
										window.hide();
										panel.getTreeGrid().getSelectionModel()
												.deselectAll();
										panel.getTreeGrid().getSelectionModel().select(
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
			taDesc.setValue(EditPrarmDir.GetDesc());
			rtfName.setValue(EditPrarmDir.GetName());
		}
		window.show();
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
		insertDir.setText("新增文件夹");
		insertDir.setIcon(ICONS.NewStruct());
		insertDir.addSelectionListener(new SelectionListener<MenuEvent>() {
			public void componentSelected(MenuEvent ce) {
				final BaseTreeModel folder = panel.getTreeGrid().getSelectionModel()
						.getSelectedItem();
				GWTParameterDirectory parent = new GWTParameterDirectory();
				if (folder instanceof GWTParameterDirectory) {
					parent = (GWTParameterDirectory) folder;
				} else {
					parent = (GWTParameterDirectory) panel.getStore().getParent(folder);
				}
				EditPrarmDir = new GWTParameterDirectory();
				CreateDirEditForm(parent, false);

			}
		});
		contextMenu.add(insertDir);

		final MenuItem insertRootDir = new MenuItem();
		insertRootDir.setText("新增根文件夹");
		insertRootDir.setIcon(ICONS.NewStruct());
		insertRootDir.addSelectionListener(new SelectionListener<MenuEvent>() {
			public void componentSelected(MenuEvent ce) {
				GWTParameterDirectory parent = new GWTParameterDirectory();
				parent.set(GWTParameterDirectory.N_ID, "0");
				EditPrarmDir = new GWTParameterDirectory();
				CreateDirEditForm(parent, true);

			}
		});
		contextMenu.add(insertRootDir);

		final MenuItem insertCase = new MenuItem();
		insertCase.setText("新增系统参数");
		insertCase.setIcon(ICONS.AddCom());
		insertCase.addSelectionListener(new SelectionListener<MenuEvent>() {
			public void componentSelected(MenuEvent ce) {
				doAddHandler();
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
				final BaseTreeModel folder = panel.getTreeGrid().getSelectionModel()
						.getSelectedItem();
				if (folder != null && folder instanceof GWTParameterDirectory) {
					final MessageBox box = MessageBox.prompt("重命名文件夹",
							"请输入新的文件夹名称:");

					box.addCallback(new Listener<MessageBoxEvent>() {
						public void handleEvent(MessageBoxEvent be) {
							Button msgBtn = be.getButtonClicked();
							if (msgBtn.getText().equalsIgnoreCase("OK")) {
								final GWTParameterDirectory gwtParamDir = (GWTParameterDirectory) folder;
								gwtParamDir.set(GWTParameterDirectory.N_Name,
										be.getValue());
								sysParaService.saveOrUpdateParamDirectory(
										gwtParamDir,
										new AsyncCallback<GWTParameterDirectory>() {

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
													GWTParameterDirectory result) {
												// TODO Auto-generated method
												// stub
												panel.getStore()
														.update(gwtParamDir);
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
				MessageBox.confirm("删除提示", "是否确认删除？", DelHandler());
			}
		});
		contextMenu.add(remove);
		contextMenu.addListener(Events.BeforeShow, new Listener<BaseEvent>() {

			@Override
			public void handleEvent(BaseEvent be) {
				// TODO Auto-generated method stub
				final BaseTreeModel item = panel.getTreeGrid().getSelectionModel()
						.getSelectedItem();
				if (item instanceof GWTParameterDirectory) {
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
}
