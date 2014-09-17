package com.dc.tes.ui.client.page;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;


import com.dc.tes.ui.client.IScriptFlowService;
import com.dc.tes.ui.client.IScriptFlowServiceAsync;
import com.dc.tes.ui.client.ICaseService;
import com.dc.tes.ui.client.ICaseServiceAsync;
import com.dc.tes.ui.client.IExecutePlanService;
import com.dc.tes.ui.client.IExecutePlanServiceAsync;
import com.dc.tes.ui.client.IExecuteSetExecutePlanServiceAsync;
import com.dc.tes.ui.client.IExecuteSetService;
import com.dc.tes.ui.client.IExecuteSetServiceAsync;
import com.dc.tes.ui.client.IExecuteSetExecutePlanService;
import com.dc.tes.ui.client.IQueueService;
import com.dc.tes.ui.client.IQueueServiceAsync;
import com.dc.tes.ui.client.ITestRoundService;
import com.dc.tes.ui.client.ITestRoundServiceAsync;
import com.dc.tes.ui.client.IUseCaseService;
import com.dc.tes.ui.client.IUseCaseServiceAsync;
import com.dc.tes.ui.client.common.ServiceHelper;
import com.dc.tes.ui.client.control.MyGrid;
import com.dc.tes.ui.client.control.MyGroupingView;
import com.dc.tes.ui.client.control.RequireTextField;
import com.dc.tes.ui.client.enums.RunState;
import com.dc.tes.ui.client.model.GWTScriptFlowLog;
import com.dc.tes.ui.client.model.GWTCaseDirectory;
import com.dc.tes.ui.client.model.GWTCaseFlow;
import com.dc.tes.ui.client.model.GWTCompareResult;
import com.dc.tes.ui.client.model.GWTExecutePlan;
import com.dc.tes.ui.client.model.GWTExecuteSetDirectory;
import com.dc.tes.ui.client.model.GWTPack_Struct;
import com.dc.tes.ui.client.model.GWTExecuteSetExecutePlan;
import com.dc.tes.ui.client.model.GWTQueueLog;
import com.dc.tes.ui.client.model.GWTQueueTask;
import com.dc.tes.ui.client.model.GWTStock;
import com.dc.tes.ui.client.model.GWTTestRound;
import com.extjs.gxt.themes.client.Slate;
import com.extjs.gxt.ui.client.GXT;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.data.BaseTreeLoader;
import com.extjs.gxt.ui.client.data.BaseTreeModel;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.RpcProxy;
import com.extjs.gxt.ui.client.data.TreeLoader;
import com.extjs.gxt.ui.client.data.TreeModel;
import com.extjs.gxt.ui.client.dnd.GridDragSource;
import com.extjs.gxt.ui.client.dnd.GridDropTarget;
import com.extjs.gxt.ui.client.dnd.TreePanelDragSource;
import com.extjs.gxt.ui.client.dnd.TreePanelDropTarget;
import com.extjs.gxt.ui.client.dnd.DND.Feedback;
import com.extjs.gxt.ui.client.dnd.DND.Operation;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.DNDEvent;
import com.extjs.gxt.ui.client.event.DNDListener;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.IconButtonEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MenuEvent;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.event.SelectionService;
import com.extjs.gxt.ui.client.store.GroupingStore;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.store.Store;
import com.extjs.gxt.ui.client.store.TreeStore;
import com.extjs.gxt.ui.client.store.TreeStoreModel;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.HtmlContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.TabItem;
import com.extjs.gxt.ui.client.widget.TabPanel;
import com.extjs.gxt.ui.client.widget.TabPanel.TabPosition;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.button.IconButton;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.form.StoreFilterField;
import com.extjs.gxt.ui.client.widget.form.TextArea;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.grid.GridGroupRenderer;
import com.extjs.gxt.ui.client.widget.grid.GroupColumnData;
import com.extjs.gxt.ui.client.widget.grid.RowNumberer;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.layout.FillLayout;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.extjs.gxt.ui.client.widget.toolbar.SeparatorToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.extjs.gxt.ui.client.widget.treepanel.TreePanel;
import com.extjs.gxt.ui.client.widget.treepanel.TreePanel.TreeNode;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Random;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Widget;


public class ExecuteSetPage extends BasePage {
	
	private IExecuteSetServiceAsync executeSetService = null;
	private IQueueServiceAsync queueService = null;
	private IExecuteSetExecutePlanServiceAsync execSetExecPlanService = null; 
	private IScriptFlowServiceAsync scriptFlowService = ServiceHelper.GetDynamicService("scriptFlow", IScriptFlowService.class);
	private IUseCaseServiceAsync useCaseService = null;
	private TreePanel<ModelData> esTreePanel;
	private TreePanel<ModelData> ucTreePanel;
	private TreeStore<ModelData> store;
	private TreeStore<ModelData> feedStore;
	private ListStore<GWTExecuteSetDirectory> esListStore;
	private GWTExecuteSetDirectory EditExecuteSet;
	private List<Component> btnList = new ArrayList<Component>();
	private TabPanel tPanel;
	private BorderLayout layout;
	
	/**
	 * 执行轮次ID
	 */
	private Integer roundId;
	/**
	 * 执行集队列中执行集中总数量
	 */
	private int totalCount;
	/**
	 * 执行集队列中当前正在执行的执行集编号
	 */
	private int currentCount;
	/**
	 * 是否是执行集队列执行
	 */
	private boolean isCycleExec;
	
	/**
	 * 执行批次号
	 */
	private String execBN;
	/**
	 * 执行队列列表窗口
	 */
	private Window ExecuteSetListWindow;
	/**
	 * 队列ID
	 */
	private String queueID;
	private Integer executeSetID;
	/**
	 * 日志窗口
	 */
	private Window logWindow;
	/**
	 * 停止按钮
	 */
	Button btnStop = new Button();
	/**
	 * 任务列表
	 */
	private MyGrid<ModelData> taskGrid = null;
	/**
	 * 执行方式
	 */
	private ComboBox<GWTStock> execType;
	
	/**
	 * 是否第一次执行
	 */
	boolean firstExec = true;	
	/**
	 * 任务顺序号
	 */
	int taskIndex = 0;
	/**
	 * 当前任务执行已次数
	 */
	int countIndex = 0;
	/**
	 * 总已执行次数
	 */
	int runCount = 0;
	/**
	 * 当前执行的任务
	 */
	GWTQueueTask runTask = null;
	/**
	 * 业务流日志
	 */
	GWTScriptFlowLog logInfo = new GWTScriptFlowLog();
	/**
	 * 当前任务日志
	 */
	GWTQueueLog currentLog = new GWTQueueLog();
	/**
	 * 页面执行状态
	 */
	RunState runState = RunState.NoStart;
	
	/**
	 * 当前选择的执行集的执行计划
	 */
	GWTExecuteSetExecutePlan EditExecSetExecPlan = null;
	
	String executeLogId = "";
	private MyGrid<GWTQueueLog> logGrid = null;
	
	
	@Override
	protected void onRender(Element parent, int index) {
		super.onRender(parent, index);
		executeSetService = ServiceHelper.GetDynamicService("executeSet", IExecuteSetService.class);
		queueService = ServiceHelper.GetDynamicService("queue", IQueueService.class);
		execSetExecPlanService = ServiceHelper.GetDynamicService("executeSetExecutePlan", IExecuteSetExecutePlanService.class);
		useCaseService = ServiceHelper.GetDynamicService("useCase",IUseCaseService.class);
		RpcProxy<List<ModelData>> proxy = new RpcProxy<List<ModelData>>() {
			@Override
			protected void load(Object loadConfig,
					AsyncCallback<List<ModelData>> callback){
				executeSetService.getExecuteSetTree(GetSysInfo(), 
						(GWTExecuteSetDirectory)loadConfig, callback);
			}
		}; 
		
		final TreeLoader<ModelData> loader = new BaseTreeLoader<ModelData>(proxy);
		store = new TreeStore<ModelData>(loader);
		
		useCaseLoader();
		layout = new BorderLayout();  
		this.setLayout(layout);
		
		BorderLayoutData westData = new BorderLayoutData(LayoutRegion.WEST, 190);
		westData.setMinSize(190);
		westData.setMaxSize(230);
		westData.setSplit(true);
		westData.setCollapsible(true);
		westData.setMargins(new Margins(0, 5, 0, 0));
		this.add(CreateESTreePanel(), westData);
		
		BorderLayoutData centerData = new BorderLayoutData(LayoutRegion.CENTER);
		centerData.setMargins(new Margins());
		this.add(CreateQueueTaskForm(), centerData);
		
		BorderLayoutData eastData = new BorderLayoutData(LayoutRegion.EAST, 200);
		eastData.setMinSize(200);
		eastData.setCollapsible(true);
		eastData.setMaxSize(280);
		eastData.setSplit(true);
		eastData.setMargins(new Margins(0, 5, 0, 0));
		eastData.setHidden(true);
		this.add(CreateUCTreePanel(), eastData);
		
		CreateExecuteSetWindow();
	}
	
	
	private void useCaseLoader() {
		
		RpcProxy<List<ModelData>> proxy = new RpcProxy<List<ModelData>>() {

			@Override
			protected void load(Object loadConfig,
					AsyncCallback<List<ModelData>> callback) {
				
				useCaseService.getUseCaseTree(GetSysInfo(),
						(GWTCaseDirectory) loadConfig, callback);
			}
		};
		final TreeLoader<ModelData> loader = new BaseTreeLoader<ModelData>(proxy);
		feedStore = new TreeStore<ModelData>(loader);
	}

	//画执行集树
	private Widget CreateESTreePanel() {
		ContentPanel cp = new ContentPanel();
		cp.setHeading("执行集树");
		cp.setButtonAlign(HorizontalAlignment.CENTER);
		cp.setLayout(new FitLayout());
		cp.setScrollMode(Scroll.AUTO);
		cp.setBorders(false);
		
		esTreePanel = new TreePanel<ModelData>(store) {
			@Override
			public boolean hasChildren(ModelData parent) {
				if (parent instanceof GWTExecuteSetDirectory &&
						((GWTExecuteSetDirectory) parent).GetObjType()==0) {
					return true;
				} else {
					return false;
				}
			}
		};

		esTreePanel.getStyle().setLeafIcon(ICONS.menuTreeLeaf());
		esTreePanel.setDisplayProperty("name");
		esTreePanel.setWidth(200);
		esTreePanel.setAutoSelect(true);
		esTreePanel.setBorders(false);
		
		esTreePanel.setContextMenu(drawEsTreeContextMenu());
		
		// 添加单击事件
		SelectionService.get().addListener(EsTreeSelectChangeListener());
		SelectionService.get().unregister(esTreePanel.getSelectionModel());
		SelectionService.get().register(esTreePanel.getSelectionModel());
		
		TreePanelDragSource source = new TreePanelDragSource(esTreePanel);
		source.addDNDListener(new DNDListener() {
			@Override
			public void dragStart(DNDEvent e) {
				ModelData sel = esTreePanel.getSelectionModel().getSelectedItem();
				
				if (sel != null
						&& sel == esTreePanel.getStore().getRootItems().get(0)) {
					e.setCancelled(true);
					e.getStatus().setStatus(false);
					return;
				}
				super.dragStart(e);
			}
		});
		
		//拖动结果后需要对改动实时地进行持久化
		TreePanelDropTarget target = new TreePanelDropTarget(esTreePanel);
		target.addDNDListener(new DNDListener(){
			@SuppressWarnings("rawtypes")
			@Override
			public void dragDrop(DNDEvent e){			
				List<TreeStoreModel> sel = e.getData();
				if(sel != null){
					TreeNode parent = esTreePanel.findNode(e.getTarget());
					Integer id;
					if(parent == null){
						id = 0;
					}else
						id = ((GWTExecuteSetDirectory)parent.getModel()).GetID();
					for(TreeStoreModel item : sel){
							GWTExecuteSetDirectory gwt = (GWTExecuteSetDirectory)item.getModel();
							gwt.set(GWTExecuteSetDirectory.N_ParentDirID, id);
							executeSetService.saveOrUpdateExecuteSet(gwt, GetLoginLogID(),
									new AsyncCallback<GWTExecuteSetDirectory>() {

								@Override
								public void onFailure(Throwable caught) {
									
									MessageBox.alert("错误提示", "保存拖动失败", null);
									caught.getStackTrace();
								}

								@Override
								public void onSuccess(GWTExecuteSetDirectory result) {
									
									store.update(result);
								}
							});
				
						}
					super.dragDrop(e);	
					}
				}	
			
			@Override
			public void dragMove(DNDEvent e){
				List<TreeStoreModel> data = e.getData();
				if((Object)data.get(0) instanceof GWTQueueTask || !(data.get(0).getModel() instanceof GWTExecuteSetDirectory)){
					e.setCancelled(true);
				    e.getStatus().setStatus(false);
				    return;
				}
				super.dragMove(e);
			}
		
		});
		target.setAllowSelfAsSource(true);
		target.setFeedback(Feedback.BOTH);
		ToolBar toolBar = new ToolBar();
		IconButton filterBtn = new IconButton("icon-refresh");
		filterBtn.setToolTip("刷新");
		filterBtn.addSelectionListener(new SelectionListener<IconButtonEvent>() {

			@Override
			public void componentSelected(IconButtonEvent ce) {
				
				store.removeAll();
				store.getLoader().load();
			}
		
		});
		filterBtn.setWidth(20);
		toolBar.add(filterBtn);
		StoreFilterField<ModelData> treeFilter = GetNameFilter(true);
		toolBar.add(treeFilter);
		treeFilter.bind(store);
		cp.setTopComponent(toolBar);
		cp.add(esTreePanel);
		return cp;
	}
	//树节点选择响应事件
	
	private SelectionChangedListener<? extends ModelData> EsTreeSelectChangeListener() {
		
		return new SelectionChangedListener<TreeModel>() {
			@Override
			public void selectionChanged(
					SelectionChangedEvent<TreeModel> event) {
				List<TreeModel> sel = event.getSelection();
				if (sel.size() > 0) {
					TreeModel m = (TreeModel) event
							.getSelection().get(0);
					if(m!=null && m instanceof GWTExecuteSetDirectory){//如果选择的是目录
						final GWTExecuteSetDirectory sed = (GWTExecuteSetDirectory)m;
						if(sed.GetObjType()==0){ //目录						
							if(tPanel.getItemCount() != 0){
								tPanel.removeAll();
							}
							TabItem tabItem = drawDirectoryMsg(sed);
							tPanel.add(tabItem);
							tPanel.setSelection(tabItem);
							layout.hide(LayoutRegion.EAST);
							return;
							
						}else{ //如果选择的是执行集
							if(tPanel.getItemCount() != 0){
								tPanel.removeAll();
							}
							
							final TabItem tabItem = drawQueueTaskTab();
							tPanel.add(tabItem);
							tPanel.setSelection(tabItem);
							queueID = sed.GetObjectID().toString();
							executeSetID = sed.GetID();
							
							final ListStore<ModelData> store = taskGrid.getStore();
							if(sed.getTaskList()!=null){
								
								store.add(((BaseTreeModel)sed.getTaskList()).getChildren());
							
							} else {
								tabItem.mask("正在获取任务列表...");
								queueService.GetQueueTask(queueID, new AsyncCallback<GWTQueueTask>() {
									
									@Override
									public void onFailure(Throwable caught) {
										
										MessageBox.alert("错误提示", "获取数据失败，页面无法编辑", null);
									}
	
									@Override
									public void onSuccess(GWTQueueTask result) {
																			
										store.add(((BaseTreeModel)result).getChildren());
										sed.setTaskList(result);
										esListStore.update(sed);
										tabItem.unmask();
									}
								});
							}
							taskGrid.reconfigure(store, taskGrid.getColumnModel());
							firstExec = true;
							SetButton(true);	
							
							if(logWindow == null){
								CreateLogWindow();
							}
						}
					}
				}
			}
			
			//画任务队列的tabItem
			private TabItem drawQueueTaskTab() {
				
				TabItem tabItem = new TabItem("执行集信息");
				tabItem.setId("1");
				tabItem.setClosable(false);
				tabItem.setLayout(new FitLayout());
				tabItem.setBorders(false);
				ContentPanel cPanel = new ContentPanel();
				cPanel.setHeaderVisible(false);
				cPanel.setLayout(new FitLayout());
				cPanel.setBodyBorder(false);
				cPanel.setBorders(false);
				cPanel.setTopComponent(InitButtonToolWidget());
				cPanel.add(CreateTaskRegion());
				
				tabItem.add(cPanel);
				return tabItem;
			}
		};	
	}

    //执行集树右键菜单
	private Menu drawEsTreeContextMenu() {
		
		Menu contextMenu = new Menu();
		
		final MenuItem insertDir = new MenuItem();
		insertDir.setText("新增目录");
		insertDir.setIcon(ICONS.AddCom());
		insertDir.addSelectionListener(new SelectionListener<MenuEvent>() {
			public void componentSelected(MenuEvent ce) {
				final GWTExecuteSetDirectory folder = (GWTExecuteSetDirectory)esTreePanel.getSelectionModel()
						.getSelectedItem();
				GWTExecuteSetDirectory parent = new GWTExecuteSetDirectory();
				if(folder == null || folder.GetObjType() == 0){
					parent = (GWTExecuteSetDirectory)folder;
				}else{
					parent = (GWTExecuteSetDirectory)store.getParent(folder);
				}
				EditExecuteSet = new GWTExecuteSetDirectory();
				CreateDirEditForm(parent, true);
			}			
		});
		contextMenu.add(insertDir);
		
		final MenuItem insertRootDir = new MenuItem();
		insertRootDir.setText("新增根目录");
		insertRootDir.setIcon(ICONS.AddCom());
		insertRootDir.addSelectionListener(new SelectionListener<MenuEvent>() {
			public void componentSelected(MenuEvent ce) {
				EditExecuteSet = new GWTExecuteSetDirectory();
				CreateDirEditForm(null, true);
			}			
		});
		contextMenu.add(insertRootDir);
		
		final MenuItem upDateDir = new MenuItem();
		upDateDir.setText("修改目录");
		upDateDir.setIcon(ICONS.EditCom());
		upDateDir.addSelectionListener(new SelectionListener<MenuEvent>() {
			public void componentSelected(MenuEvent ce) {
				final GWTExecuteSetDirectory item = (GWTExecuteSetDirectory)esTreePanel.getSelectionModel()
						.getSelectedItem();
				EditExecuteSet = item;
				GWTExecuteSetDirectory parent = (GWTExecuteSetDirectory)store.getParent(item);
				CreateDirEditForm(parent, true);
			}			
		});
		contextMenu.add(upDateDir);
		
		final MenuItem insertESet = new MenuItem();
		insertESet.setText("新增执行集");
		insertESet.setIcon(ICONS.AddCom());
		insertESet.addSelectionListener(new SelectionListener<MenuEvent>() {
			public void componentSelected(MenuEvent ce) {
				final GWTExecuteSetDirectory folder = (GWTExecuteSetDirectory)esTreePanel.getSelectionModel()
						.getSelectedItem();
				GWTExecuteSetDirectory parent = new GWTExecuteSetDirectory();
				if(folder == null || folder.GetObjType() == 0){
					parent = (GWTExecuteSetDirectory)folder;
				}else{
					parent = (GWTExecuteSetDirectory)store.getParent(folder);
				}
				EditExecuteSet = new GWTExecuteSetDirectory();
				CreateDirEditForm(parent, false);
			}			
		});
		contextMenu.add(insertESet);
		
		final MenuItem upDateESet = new MenuItem();
		upDateESet.setText("修改执行集");
		upDateESet.setIcon(ICONS.EditCom());
		upDateESet.addSelectionListener(new SelectionListener<MenuEvent>() {
			public void componentSelected(MenuEvent ce) {
				final GWTExecuteSetDirectory item = (GWTExecuteSetDirectory)esTreePanel.getSelectionModel()
						.getSelectedItem();
				EditExecuteSet = item;
				GWTExecuteSetDirectory parent = (GWTExecuteSetDirectory)store.getParent(item);
				CreateDirEditForm(parent, false);
			}			
		});
		contextMenu.add(upDateESet);
		
		final MenuItem remove = new MenuItem();
		remove.setText("删除");
		remove.setIcon(ICONS.Remove());
		remove.addSelectionListener(new SelectionListener<MenuEvent>() {
			public void componentSelected(MenuEvent ce) {
				final List<ModelData> selected = esTreePanel.getSelectionModel().getSelectedItems();
				MessageBox.confirm("提示信息", "是否确认删除",
					new Listener<MessageBoxEvent>() {
						public void handleEvent(MessageBoxEvent be) {
							Button msgBtn = be.getButtonClicked();
							if (msgBtn.getText().equalsIgnoreCase("Yes")) {
								for (ModelData sel : selected) {
									if (((GWTExecuteSetDirectory)sel).GetObjType()==0) {
										Integer count = store
												.getChildren(sel).size();
										if (count != 0) {
											MessageBox.alert("错误提示","选中的文件夹【"
												+ ((GWTExecuteSetDirectory) sel).GetName()
												+ "】不为空，无法删除！",null);
											return;
										}
									}
								}
								final ModelData parent = store.getParent(selected.get(0));
								executeSetService.deleteSelectedItem(selected, GetLoginLogID(),
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
										public void onSuccess(Boolean result) {
											// TODO Auto-generated
											// method stub
											if (result) {
												for (ModelData sel : selected) {
													store.remove(sel);
												}
											} else {
												MessageBox.alert("错误提示",
														"选中文件夹不为空，删除失败",null);
											}
											esTreePanel.getSelectionModel().deselectAll();
											esTreePanel.getSelectionModel().select(parent, true);
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
				
				final ModelData item = esTreePanel.getSelectionModel()
						.getSelectedItem();
				if(item == null){
					upDateDir.setVisible(false);
					upDateESet.setVisible(false);
					remove.setVisible(false);
				}else{
					if (((GWTExecuteSetDirectory)item).GetObjType()==0) {
						upDateDir.setVisible(true);
						upDateESet.setVisible(false);
						remove.setVisible(true);
					} else {
						upDateDir.setVisible(false);
						upDateESet.setVisible(true);
						remove.setVisible(true);
					}
				}
			}
		});
			
		return contextMenu;
	}
	

	/**
	 * 创建用例树
	 * @return
	 */
	private Widget CreateUCTreePanel() {
		
		ContentPanel cp = new ContentPanel();
		cp.setHeading("测试用例树");
		cp.setButtonAlign(HorizontalAlignment.CENTER);
		cp.setLayout(new FitLayout());
		cp.setScrollMode(Scroll.AUTO);
		cp.setBorders(false);

		ucTreePanel = new TreePanel<ModelData>(feedStore){
			@Override
			public boolean hasChildren(ModelData parent) {
				if (parent instanceof GWTCaseDirectory) {
					return true;
				} else {
					return false;  
				}
			}

		};
		ucTreePanel.setBorders(true);
		ucTreePanel.getStyle().setLeafIcon(ICONS.menuCase());
		ucTreePanel.setDisplayProperty("name");
		ucTreePanel.setStateful(true);
		ucTreePanel.setWidth(200);
		ucTreePanel.setAutoSelect(true);
		ucTreePanel.setBorders(false);
		
		
		ToolBar toolBar = new ToolBar();
		IconButton filterBtn = new IconButton("icon-refresh");
		filterBtn.setToolTip("刷新");
		filterBtn.addSelectionListener(new SelectionListener<IconButtonEvent>() {

			@Override
			public void componentSelected(IconButtonEvent ce) {
				
				feedStore.removeAll();
				feedStore.getLoader().load();
			}
		
		});
		filterBtn.setWidth(20);
		toolBar.add(filterBtn);
		StoreFilterField<ModelData> treeFilter = GetNameFilter(true);
		toolBar.add(treeFilter);
		treeFilter.bind(feedStore);
		cp.setTopComponent(toolBar);
		SetDragSource("用例", ucTreePanel);
		
		Menu contextMenu = new Menu();
		
		final MenuItem addToListItem = new MenuItem();
		addToListItem.setText("添加到执行集");
		addToListItem.setIcon(ICONS.ResStruct());
		
		addToListItem.addSelectionListener(new SelectionListener<MenuEvent>() {
			@Override
			public void componentSelected(MenuEvent ce) {
				
				mask("正在添加用例至执行集，请稍候...");
				final List<ModelData> folder = ucTreePanel.getSelectionModel()
							.getSelectedItems();

				useCaseService.getAllChildDatas(GetSysInfo(), folder, new AsyncCallback<List<ModelData>>() {

					@Override
					public void onFailure(Throwable caught) {
						
						caught.printStackTrace();
						unmask();
						MessageBox.alert("错误提示", "添加用例到执行集失败！", null);
					}

					@Override
					public void onSuccess(List<ModelData> result) {
						
						List<ModelData> copyModels = new ArrayList<ModelData>();
						for (ModelData modelData : result) {
							ModelData qt =GWTQueueTask.CopyFromUC(modelData);
							if(taskGrid.getStore().findModel(GWTQueueTask.N_TaskID, ((GWTQueueTask)qt).getTaskID())==null)
								copyModels.add(qt);
						}
						taskGrid.getStore().add(copyModels);
						unmask();
					}
				});
			}
		});
		
		contextMenu.add(addToListItem);
		ucTreePanel.setContextMenu(contextMenu);
		cp.add(ucTreePanel);
		return cp;
	}

	private Widget CreateQueueTaskForm() {
		
		tPanel = new TabPanel();
		tPanel.setBorders(false);
		tPanel.setAutoWidth(true);
		tPanel.setBodyBorder(false);
		tPanel.setTabPosition(TabPosition.TOP);
		return tPanel;
	}
	

		/**
	 * 创建任务队列区域
		 * @return		任务队列区域部件
		 */
	private Widget CreateTaskRegion()
	{		
		ListStore<ModelData> store = new ListStore<ModelData>();
		ColumnModel cm = new ColumnModel(GetCommonColumnConfig());
		taskGrid = new MyGrid<ModelData>(store, cm)
		{
			@Override
			protected void onShowContextMenu(int x, int y) {
				List<ModelData> selectItem = getSelectionModel()
						.getSelectedItems();				
				// 正在运行、没选中行，菜单不可用
				if (selectItem.size() == 0 || runState == RunState.Running)
					return;
				
				super.onShowContextMenu(x, y);
			}

		};

			
		//taskGrid.setAutoExpandColumn(GWTQueueTask.N_Name);
		//taskGrid.getView().setForceFit(true);
		
		taskGrid.setContextMenu(DefineTreeContextMenu());
		taskGrid.setBorders(false);
		
		new GridDragSource(taskGrid) {
			@Override
			protected void onDragStart(DNDEvent e) {
				if (runState == RunState.Running) {
					e.setCancelled(true);
					return;
				}
				setStatusText("已选择 ({0}) 项任务");
				super.onDragStart(e);
			}
		};
		
		GridDropTarget target = new GridDropTarget(taskGrid)
		{			
			@SuppressWarnings("unchecked")
			@Override
			protected void showFeedback(DNDEvent event) {
				if (feedback == Feedback.INSERT) {
				      event.getStatus().setStatus(true);
				      Element row = grid.getView().findRow(event.getTarget()).cast();

				      if (row == null && grid.getStore().getCount() > 0) {
				        row = grid.getView().getRow(grid.getStore().getCount() - 1).cast();
				      }

				      if (row != null) {
				        int height = row.getOffsetHeight();
				        int mid = height / 2;
				        mid += row.getAbsoluteTop();
				        int y = event.getClientY();
				        boolean before = y < mid;
				        int idx = grid.getView().findRowIndex(row);
				        insertIndex = before ? idx : (event.getDragSource().getComponent() == grid) ? idx : idx + 1;
				        activeItem = grid.getStore().getAt(idx);
				        if(event.getDragSource().getComponent() == grid)
				        {
							ArrayList<ModelData> selectedList = (ArrayList<ModelData>)event.getDragSource().getData();
							for(ModelData data : selectedList)
							{
								if(data == activeItem)
								{
									 event.getStatus().setStatus(false);
									event.setCancelled(true);
									return;
								}
							}
				        }
						super.showFeedback(event);
				      } else {
				        insertIndex = 0;
				      }
				    } else {
				      event.setCancelled(false);
				    }
			}
			
			@Override
			protected void onDragDrop(DNDEvent e) {
				Object data = e.getData();
				List<ModelData> models = prepareDropData(data, true);
				if(e.getDragSource().getComponent() instanceof TreePanel)
				{
					List<ModelData> copyModels = new ArrayList<ModelData>();
					for (ModelData modelData : models) {
						ModelData qt =GWTQueueTask.CopyFromUC(modelData);
						if(taskGrid.getStore().findModel(GWTQueueTask.N_TaskID, ((GWTQueueTask)qt).getTaskID())==null)
							copyModels.add(qt);
						else 
							MessageBox.alert("提示", "无法在同一个执行集添加重复用例，‘"+
									((GWTQueueTask)qt).getTaskName()+"’添加失败！", null);
					}
					e.setData(copyModels);
				}
				super.onDragDrop(e);
				taskGrid.reconfigure(taskGrid.getStore(), taskGrid
						.getColumnModel());
			}
			
			@Override
			protected void onDragMove(DNDEvent e){
				List<ModelData> data = e.getData();
				if(data.get(0) instanceof GWTQueueTask){						
				}else if(data.get(0) instanceof TreeStoreModel){
					if(((TreeStoreModel)data.get(0)).getModel() instanceof GWTExecuteSetDirectory){
						e.setCancelled(true);
					    e.getStatus().setStatus(false);
					    return;
					}
				}
				super.onDragMove(e);

			}
		};
		
	
		target.setAllowSelfAsSource(true);   
		target.setFeedback(Feedback.INSERT);  
		
		return taskGrid;
		
	}


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
	/**
	 * 文件夹编辑框
	 * @param parent
	 * @param isDir
	 */
	private void CreateDirEditForm(final GWTExecuteSetDirectory parent,
			final boolean isDir) {
		
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
		
		final RequireTextField rtfName;
		if(isDir){
			rtfName = new RequireTextField("目录名称");
		}else{
			rtfName  = new RequireTextField("执行集名称");
		}
		rtfName.setName(GWTExecuteSetDirectory.N_Name);
		rtfName.focus();
		fp.add(rtfName, formdata);
		
		final TextArea taDesc = new TextArea();
		if(isDir){
			taDesc.setFieldLabel("目录描述");
		}else{
			taDesc.setFieldLabel("执行集描述");
		}
		taDesc.setName(GWTExecuteSetDirectory.N_Desc);
		fp.add(taDesc, formdata);
		
		window.add(fp);
		
		final boolean isNew = EditExecuteSet.IsNew();
		Button btn_save = new Button("保存",  new SelectionListener<ButtonEvent>(){

			@Override
			public void componentSelected(ButtonEvent ce) {
				
				if (!fp.isValid())
					return;
				EditExecuteSet.SetValue(GetSystemID(), rtfName.getValue(), isDir?0:1, parent==null ? null:parent.GetID(), taDesc.getValue());
				executeSetService.saveOrUpdateExecuteSet(EditExecuteSet, GetLoginLogID(),
						new AsyncCallback<GWTExecuteSetDirectory>() {

							@Override
							public void onFailure(
									Throwable caught) {
								// TODO Auto-generated method
								// stub
								caught.printStackTrace();
								MessageBox.alert("错误提示",
										"保存失败, 已存在同名执行集", null);
							}

							@Override
							public void onSuccess(
									GWTExecuteSetDirectory result) {
								// TODO Auto-generated method
								// stub
								if(isNew){
									if (parent == null) {
										store.add(result, false);
									} else {
										store.add(parent,result, false);
									}
									esTreePanel.setExpanded(result, true);
								}else{
									store.update(EditExecuteSet);
								}
								window.hide();
								esTreePanel.getSelectionModel().deselectAll();
								esTreePanel.getSelectionModel().select(result, true);
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
		
		if(isNew && isDir){
			if(parent == null)
				window.setHeading("新增根目录");
			else
				window.setHeading("新增目录");
		}else if( isNew && !isDir){
			window.setHeading("新增执行集");
		}else if(!isNew && isDir){
			window.setHeading("修改目录");
			taDesc.setValue(EditExecuteSet.GetDesc());
			rtfName.setValue(EditExecuteSet.GetName());
		}else{
			window.setHeading("修改执行集");
			taDesc.setValue(EditExecuteSet.GetDesc());
			rtfName.setValue(EditExecuteSet.GetName());
		}
		window.show();
	}


	/**
	 * 获得工具栏
	 * @return	工具栏
	 */
	private ToolBar InitButtonToolWidget() {
		ToolBar toolBar = new ToolBar();
		
		final Button btnOpenUcTree = new Button("添加用例", ICONS.AddCom());
		btnOpenUcTree.addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				
				if(btnOpenUcTree.getText().equals("添加用例")){
					layout.show(LayoutRegion.EAST);
					layout.expand(LayoutRegion.EAST);
					btnOpenUcTree.setText("关闭用例树");
					btnOpenUcTree.setIcon(ICONS.Remove());
				}else{
					layout.hide(LayoutRegion.EAST);
					btnOpenUcTree.setText("添加用例");
					btnOpenUcTree.setIcon(ICONS.AddCom());
				}
			}
		});
		toolBar.add(btnOpenUcTree);
		
		//清空队列按钮
		Button btnClear = new Button("清空队列", ICONS.DelCom(),ClearrHandler());
		btnClear.setEnabled(false);
		btnList.add(btnClear);
		toolBar.add(btnClear);
		
		// 保存按钮
		Button btnSave = new Button("保存",ICONS.Save(),
				SaveHandler());
		btnSave.setEnabled(false);
		btnList.add(btnSave);
		toolBar.add(btnSave);
		
		toolBar.add(new SeparatorToolItem());

		//执行方式
		execType = new ComboBox<GWTStock>();
		execType.setWidth(125);
		execType.setHideLabel(true);
		execType.setAllowBlank(false);
		execType.setDisplayField(GWTStock.N_Name);
		execType.setValueField(GWTStock.N_Pos);
		ListStore<GWTStock> execTypeList = new ListStore<GWTStock>();
		execTypeList.add(new GWTStock("XML报文(前置机式)", "0"));
		execTypeList.add(new GWTStock("调API(嵌入式)", "1"));
		execType.setStore(execTypeList);
		execType.setValue(execTypeList.getAt(0));
		execType.setTriggerAction(TriggerAction.ALL);
		execType.setEditable(true);
		btnList.add(execType);
		if(GetSystemName().equals("银企直连"))
			toolBar.add(execType);
		
		final ComboBox<GWTTestRound> cbTestRound = new ComboBox<GWTTestRound>();
		cbTestRound.setHideLabel(true);
		cbTestRound.setWidth(80);
		cbTestRound.setEditable(false);
		cbTestRound.setValueField(GWTTestRound.N_RoundID);
		cbTestRound.setDisplayField(GWTTestRound.N_RoundName);
		final ListStore<GWTTestRound> roundStore = new ListStore<GWTTestRound>();
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
						cbTestRound.setValue(t);
						break;
					}
				}
				roundStore.add(result);
			}
		});
		cbTestRound.setStore(roundStore);		
		cbTestRound.setTriggerAction(TriggerAction.ALL);
		cbTestRound.addSelectionChangedListener(new SelectionChangedListener<GWTTestRound>() {

			@Override
			public void selectionChanged(SelectionChangedEvent<GWTTestRound> se) {
				// TODO Auto-generated method stub
				roundId = cbTestRound.getValue().GetRoundID();
			}
		});
		toolBar.add(cbTestRound);
		
		//执行按钮
		Button btnExec = new Button("执行", ICONS.Exec(), ExecHandler());
		btnExec.setEnabled(false);
		btnList.add(btnExec);
		toolBar.add(btnExec);

		//执行按钮
		btnStop = new Button("停止", ICONS.Stop(), StopHandler());
		btnStop.setEnabled(false);
		toolBar.add(btnStop);
		
		Button btnLogShow = new Button("显示日志", ICONS.MoniView(), new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				
				logWindow.show();
			}
		});
		toolBar.add(btnLogShow);
		
		Button btnESetShow = new Button("执行集队列", ICONS.Log(), new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				
				ExecuteSetListWindow.show();
			}
		});
		toolBar.add(btnESetShow);		
		
		//添加队列执行计划
		toolBar.add(addExecPlan());
		
		toolBar.setStyleAttribute("border-left-style", "solid");
		toolBar.setStyleAttribute("border-left-color", "#99bbe8");
		toolBar.setStyleAttribute("border-left-width", "1px");
		
		return toolBar;
	}
	

	/**
	 * 添加队列执行计划
	 * @return
	 */
	private Component addExecPlan() {
		// TODO Auto-generated method stub
		Button btnExecPlan = new Button("计划执行", ICONS.Time());
		btnExecPlan.addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				// TODO Auto-generated method stub
				AddExecPlanForm();
			}
		});
		return btnExecPlan;
	}

	/**
	 * 添加队列执行计划窗口
	 */
	private void AddExecPlanForm() {
		// TODO Auto-generated method stub
		final Window window = new Window();
		window.setHeading("添加执行计划");
		window.setScrollMode(Scroll.AUTOY);
		window.setWidth(300);
		window.setModal(true);
		window.setPlain(true);
		window.setLayout(new FitLayout());
		window.setHeight(115);
		
		final FormPanel fp = new FormPanel();
		fp.setHeaderVisible(false);
		fp.setBodyBorder(false);
		fp.setBorders(false);
		fp.setPadding(5);
		fp.setHeaderVisible(false);
		fp.setScrollMode(Scroll.AUTOY);
		FormData formdata = new FormData("90%");
		
		final ComboBox<GWTExecutePlan> cbExecPlan = new ComboBox<GWTExecutePlan>();
		cbExecPlan.setFieldLabel("执行计划");
		cbExecPlan.setDisplayField(GWTExecutePlan.N_Name);
		cbExecPlan.setValueField(GWTExecutePlan.N_ID);
		cbExecPlan.setEditable(false);
		final ListStore<GWTExecutePlan> store = new ListStore<GWTExecutePlan>();
		IExecutePlanServiceAsync execPlanService = ServiceHelper.GetDynamicService("executePlan", IExecutePlanService.class);
		execPlanService.GetExecPlans(GetSystemID(), new AsyncCallback<List<GWTExecutePlan>>() {
			
			@Override
			public void onSuccess(List<GWTExecutePlan> result) {
				// TODO Auto-generated method stub
				GWTExecutePlan gwtExecutePlan = new GWTExecutePlan("-1", GetSystemID(),"不设置");
				store.add(gwtExecutePlan);
				store.add(result);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub
				MessageBox.alert("错误提示", "获取执行计划列表失败！", null);
				caught.printStackTrace();
			}
		});
		
		cbExecPlan.setStore(store);
		cbExecPlan.setTriggerAction(TriggerAction.ALL);
		fp.add(cbExecPlan, formdata);		
		execSetExecPlanService.GetExecSetExecPlan(executeSetID.toString(), new AsyncCallback<GWTExecuteSetExecutePlan>() {

			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub
				MessageBox.alert("错误提示", "获取执行集的执行计划失败！", null);
				caught.printStackTrace();
			}

			@Override
			public void onSuccess(GWTExecuteSetExecutePlan result) {
				// TODO Auto-generated method stub
				EditExecSetExecPlan = result;
				if(EditExecSetExecPlan == null){
					cbExecPlan.setValue(store.getAt(0));
				}else{
					GWTExecutePlan executePlan = null;
					for(GWTExecutePlan plan :store.getModels()){
						if(plan.GetID().equals(EditExecSetExecPlan.GetExecutePlanID())){
							executePlan = plan;
							break;
						}
					}
					cbExecPlan.setValue(executePlan);
				}
			}
		});  //获取当前执行集的执行计划 
		
		window.add(fp);
		Button btnOK = new Button("确定");
		btnOK.addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				// TODO Auto-generated method stub
				if(EditExecSetExecPlan == null){
					EditExecSetExecPlan = new GWTExecuteSetExecutePlan("", GetSystemID(), GetUserID(), executeSetID.toString(), 
							cbExecPlan.getValue().GetID(), "0");
				}else{
					EditExecSetExecPlan.set(GWTExecuteSetExecutePlan.N_ExecutePlanID, cbExecPlan.getValue().GetID());
					EditExecSetExecPlan.set(GWTExecuteSetExecutePlan.N_AddUserID, GetUserID());
				}
				execSetExecPlanService.SaveOrUpdateExecuteSetExecutePlan(EditExecSetExecPlan, new AsyncCallback<Boolean>() {

					@Override
					public void onFailure(Throwable caught) {
						// TODO Auto-generated method stub
						MessageBox.alert("错误提示", "保存失败！", null);
						caught.printStackTrace();
					}
					

					@Override
					public void onSuccess(Boolean result) {
						// TODO Auto-generated method stub
						if(result){
							MessageBox.alert("提示", "保存成功!", null);
							window.hide();
						}else{
							MessageBox.alert("错误提示", "添加失败，已存在该执行集的该项计划任务", null);
						}						
					}
				});
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

	/**
	 * 获得任务Grid的列配置列表
	 * 
	 * @return Grid的列配置列表
	 */
	private List<ColumnConfig> GetCommonColumnConfig() {
		List<ColumnConfig> columns = new ArrayList<ColumnConfig>();
		
		GridCellRenderer<GWTQueueTask> index = new GridCellRenderer<GWTQueueTask>() {
			@Override
			public Object render(final GWTQueueTask model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<GWTQueueTask> store, Grid<GWTQueueTask> grid) {
				return rowIndex + 1;
			}
		};
		ColumnConfig column = new ColumnConfig(GWTQueueTask.N_recount, "顺序", 35);
		HideColumnMenu(column);
		column.setRenderer(index);
		column.setResizable(true);
		columns.add(column);

		column = new ColumnConfig(GWTQueueTask.N_No, "编号", 170);
		HideColumnMenu(column);
		columns.add(column);
	
		column = new ColumnConfig(GWTQueueTask.N_TaskName, "名称", 350);//(交易码-案例名)
		HideColumnMenu(column);
		columns.add(column);
		
		
		GridCellRenderer<GWTQueueTask> state = new GridCellRenderer<GWTQueueTask>() {
			@Override
			public Object render(final GWTQueueTask model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<GWTQueueTask> store, Grid<GWTQueueTask> grid) {
				IconButton ic = new IconButton("taskStatu_"  + model.getStatus());

//				if (model.getStatus() != 0) {
//				ic.addSelectionListener(new SelectionListener<IconButtonEvent>() {
//
//							@Override
//							public void componentSelected(IconButtonEvent ce) {
//									List<GWTQueueLog> logList = new ArrayList<GWTQueueLog>();
//									int beginIndex = model.getBeginIndex();
//									boolean isRun = model.getStatus() == 1;
//									int endIndex =isRun ? beginIndex + countIndex : model.getEndIndex();
//									for(int i = beginIndex; i< endIndex; i++)
//									{
//										logList.add(logGrid.getStore().getAt(i));
//									}
//									logGrid.getSelectionModel().setSelection(logList);
//								
//							}
//						});
//				}
//				else
//				{
//					ic.setStyleAttribute("cursor", "point");
//				}
				
				return ic;
			}
		};
		column = new ColumnConfig(GWTQueueTask.N_recount, "状态", 40);
		HideColumnMenu(column);
		column.setRenderer(state);
		columns.add(column);
		
		return columns;
	}
	/**
	 * 隐藏Grid中列的排序，菜单
	 * @param column Grid列
	 */
	private void HideColumnMenu(ColumnConfig column)
	{
		column.setAlignment(HorizontalAlignment.LEFT);
		column.setSortable(false);
		column.setResizable(true);
		column.setMenuDisabled(true);
	}
	/**
	 * 定义上下文菜单
	 * 
	 * @return Menu
	 */
	private Menu DefineTreeContextMenu() {
		Menu contextMenu = new Menu();
		contextMenu.setWidth(140);

		contextMenu.add(new MenuItem("删除", ICONS.DelCom(),
				DeleteHandler()));

		return contextMenu;
	}
	
	/**
	 * 获得队列执行Handler
	 * 
	 * @return 队列执行Handler
	 */
	private SelectionListener<ButtonEvent> ExecHandler() {
		return new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				SaveTask(false);
				isCycleExec = false;
				logWindow.show();
				Exec(true);
			}
		};
	}
	
	/**
	 * 获得队列执行停止Handler
	 * 
	 * @return 队列执行停止Handler
	 */
	private SelectionListener<ButtonEvent> StopHandler() {
		return new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				runState = RunState.Suspend;
			}
		};
	}

	/**
	 * 获得队列保存Handler
	 * 
	 * @return 队列保存Handler
	 */
	private SelectionListener<ButtonEvent> SaveHandler() {
		return new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				SaveTask(true);
			}
		};
	}
	
	/**
	 * 保存任务列表
	 * @param alert	是否弹出提示信息
	 */
	private void SaveTask(final boolean alert)
	{
		queueService.SetQueueTask(queueID, taskGrid.getStore().getModels(),GetLoginLogID(),
				new AsyncCallback<Void>() {

					@Override
					public void onFailure(Throwable caught) {
						if(alert)
							MessageBox.alert("错误提示", "保存失败", null);
						
						//TODO:提示
					}

					@Override
					public void onSuccess(Void result) {
						GWTQueueTask gwt = GWTQueueTask.CreateFolderTask("", "");
						for(ModelData t: taskGrid.getStore().getModels()){
							gwt.add(t);
						}
						((GWTExecuteSetDirectory)esTreePanel.getSelectionModel().
								getSelectedItem()).setTaskList(gwt);
						if(alert)
							MessageBox.alert("友情提示", "保存成功", null);
					}

				});
	}
	/**
	 * 获得删除队列Handler（客户端删除）
	 * @return	删除队列Handler
	 */
	private SelectionListener<MenuEvent> DeleteHandler() {
		return new SelectionListener<MenuEvent>() {
			@Override
			public void componentSelected(MenuEvent ce) {
				DeleteTask(taskGrid.getSelectionModel().getSelectedItems());
			}
		};
	}
	
	/**
	 *  获得清空队列Handler（客户端删除） 
	 * @return	清空队列Handler
	 */
	private SelectionListener<ButtonEvent> ClearrHandler() {
		return new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				if(taskGrid.getStore().getModels().size() != 0)
				MessageBox.confirm("友情提示", "是否确认所有任务信息", new Listener<MessageBoxEvent>(){
					@Override
					public void handleEvent(MessageBoxEvent be) {
						if(be.getButtonClicked().getText().equalsIgnoreCase("yes"))
						{
							DeleteTask(taskGrid.getStore().getModels());
						}
					}});
				
			}
		};
	}
	
	/**
	 * 删除队列
	 * @param delList
	 */
	private void DeleteTask(List<ModelData> delList)
	{
		for(ModelData data : delList)
			taskGrid.getStore().remove(data);
		taskGrid.reconfigure(taskGrid.getStore(), taskGrid.getColumnModel());
	}
	
	
	
	
	/**
	 * 执行任务
	 * @param alert	若无队列，则是否弹出框
	 */
	private void Exec(boolean alert)
	{
		if(runState == RunState.Running)
			return;
		
		if(taskGrid.getStore().getModels().size() == 0)
		{
			if(alert)
				MessageBox.alert("友情提示", "队列为空，无法执行", null);
			else
			{
				logGrid.getStore().removeAll();
				logGrid.getView().setEmptyText("任务队列为空，无法执行");
				SetButton(true);
			}
			return;
		}
		
		runState = RunState.Running;
		SetButton(false);
		
		execBN = DateTimeFormat.getFormat("yyMMddHHmmss").format(new Date())
						+ Random.nextInt();
		queueService.Insert2DataBase(queueID, executeSetID, GetUserID(),execBN, roundId,
				new AsyncCallback<String>() {
			@Override
			public void onFailure(Throwable caught) {
				
				
				MessageBox.alert("友情提示", "新建执行日志记录失败", null);
			}

			@Override
			public void onSuccess(String result) {
				
				//保存 执行日志ID
				executeLogId = result;
				
				//清除之前日志
				taskIndex = 0;
				countIndex = 0;
				runCount = 0;
				logGrid.getStore().removeAll();
				
				runTask = null;
				
				GWTQueueTask firstTask = (GWTQueueTask) taskGrid.getStore().getModels().get(0);
				if (firstTask.getStatus() != 0) {
					firstExec = false;
				}
				
				for(ModelData task : taskGrid.getStore().getModels())
				{
					((GWTQueueTask)task).Start();
				}
				//第一次不需刷新
				if(!firstExec) {
					taskGrid.reconfigure(taskGrid.getStore(), taskGrid.getColumnModel());
				}
				
				RunNext();
			}
			
		});

	}
	/**
	 * 更新日志列表，主要用于滚动条的控件
	 */
	private void RefreshLogGrid()
	{
		
		logGrid.getView().getScrollState();
		
		((MyGroupingView)logGrid.getView()).setPreventScrollToTopOnRefresh(true);
		
		GWTQueueLog model = new GWTQueueLog();
		logGrid.getStore().add(model);
		logGrid.getStore().remove(model);
		((MyGroupingView)logGrid.getView()).setScrollBottom();
	}
	
	/**
	 * 更新任务列表，显示执行过程的滚动条
	 */
	private void RefreshTask()
	{
		taskGrid.refreshRow(taskIndex);	
	}
	
	/**
	 * 异步自递归执行队列任务
	 */
	private void RunNext()
	{
		RefreshLogGrid();
		
		//如果已执行完成
		if(taskIndex == taskGrid.getStore().getModels().size())
		{
			runState = RunState.Stop;
			TaskItemStop(IsError(), false);
			RefreshTask();
			if(isCycleExec){
				GWTExecuteSetDirectory m = esListStore.getAt(currentCount-1);
				m.SetStatus(3);
				esListStore.update(m);
				CycleExec(true);
			}else
				return;
		}
		//如果之前点击暂停
		if(runState == RunState.Suspend)
		{
			runTask.End();
			TaskItemStop(IsError(), false);
			RefreshTask();
			queueService.updateExecuteLogForSuspendRun(execBN, new AsyncCallback<Void>() {
				@Override
				public void onFailure(Throwable caught) {
					MessageBox.alert("错误提示", "修改执行结果状态失败！", null);
				}
				@Override
				public void onSuccess(Void result) {}
			});
			return;
		}
		
		runTask = (GWTQueueTask)taskGrid.getStore().getModels().get(taskIndex);
		runTask.Run();
		RefreshTask();
		
		if(countIndex < runTask.getRecCount())
		{
			if(countIndex == 0)
				runTask.setBeginIndex(runCount);
			runCount++;
			countIndex++;
			currentLog = new GWTQueueLog(runTask,taskIndex);
			//最多显示30条,缓解界面压力
			if(logGrid.getStore().getCount() > 30)
				logGrid.getStore().removeAll();
			
			logGrid.getStore().add(currentLog);
			
			if(runTask.isCase()) {
				RunCaseTask();
			}
			else {
				RunBusiTask();
			}
		}
		else
			TaskItemStop(IsError(),true);
	}
	

	public boolean IsError()
	{
		if(runTask == null)
			return false;
		else
			return runTask.IsError();
	}
	
	/**
	 * 执行案例数据
	 */
	private void RunCaseTask()
	{
		ICaseServiceAsync caseService = ServiceHelper.GetDynamicService(CasePage.SERVERNAME, ICaseService.class);
		caseService.GetResultCompare(GetSysInfo(),runTask.getParentTran(),runTask.getTaskID(), executeLogId,new AsyncCallback<GWTCompareResult>() {
			@Override
			public void onFailure(Throwable caught) {
				//打印日志，执行下一步
				currentLog.SetLogDetail(false, caught.getMessage(), null, true);
				TaskItemStop(true, true);
			}

			@Override
			public void onSuccess(GWTCompareResult result) {
				if(result.getBooleanResult())
				{
					currentLog.SetLogDetail(true, "", result.getCompareResult(), true);
					RunNext();
				}
				else
				{
					currentLog.SetLogDetail(false, result.getErrorMsg(), null, true);
					TaskItemStop(true, true);
				}
			}
		});
	}
	
	/**
	 * 执行业务流
	 */
	private void RunBusiTask()
	{

		
		scriptFlowService.GetDelayTime(GetSystemID(), runTask.getTaskID(), new AsyncCallback<Integer>() {

			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub
				MessageBox.alert("错误","计算超时异常",null);
			}

			@Override
			public void onSuccess(Integer result) {
				// TODO Auto-generated method stub
				final String logID = executeLogId;
				final int delayTime = result;
//				final boolean byApi = execType.getValue().getPos().equals("1")? true : false;
					
				final int scheduleTime = 1000;
				final Timer time = new Timer() {
					int i = 0;							
					@Override
					public void run() {
						i++;
						final Timer self = this;
						
						scriptFlowService.GetExecLog(logID, runTask.getTaskID(), new AsyncCallback<ModelData>(){
							@Override
							public void onFailure(Throwable caught) {
								PrintBusiLog(self,GWTScriptFlowLog.CreateExecptionLog("执行日志获取失败"),false);
							}

							@Override
							public void onSuccess(ModelData result) {
								if(result != null && result instanceof GWTScriptFlowLog)
									PrintBusiLog(self,(GWTScriptFlowLog)result,true);
								else if(i * scheduleTime == delayTime)
									PrintBusiLog(self,GWTScriptFlowLog.CreateDualLog(),false);
							}});
					}
				};
				
				scriptFlowService.ExecCaseFlow(GetSysInfo(), runTask.getTaskID(), logID, new AsyncCallback<Boolean>(){

					@Override
					public void onFailure(Throwable caught) {
						// TODO Auto-generated method stub
						PrintBusiLog(null,GWTScriptFlowLog.CreateExecptionLog("业务流执行失败"),false);
					}

					@Override
					public void onSuccess(Boolean result) {
						// TODO Auto-generated method stub
						PrintBusiLog(null,GWTScriptFlowLog.CreateBeginlLog(),true);
						time.scheduleRepeating(scheduleTime);
					}
					
				});
				
//				scriptFlowService.ExecScript(GetSysInfo(),logID, runTask.getTaskID(), executeLogId, GetUserID(), byApi, new AsyncCallback<Boolean>(){
//					@Override
//					public void onFailure(Throwable caught) {
//						PrintBusiLog(null,GWTScriptFlowLog.CreateExecptionLog("获取脚本失败"),false);
//					}
//
//					@Override
//					public void onSuccess(Boolean haveScript) {
//						if(haveScript)
//						{
//							PrintBusiLog(null,GWTScriptFlowLog.CreateBeginlLog(),true);
//							time.scheduleRepeating(scheduleTime);
//						}
//						else
//						{
//							PrintBusiLog(null,GWTScriptFlowLog.CreateExecptionLog("脚本为空或用例已设置为“暂时不用”，不执行"),false);
//						}
//					}}
//				);
			}
			
		});
		

	}
	
	/**
	 * 打印业务流日志
	 * @param time			日志监控计时器
	 * @param returnLog		日志报告
	 * @param taskContinue	是否执行下一次循环
	 */
	private void PrintBusiLog(Timer time ,GWTScriptFlowLog returnLog,boolean taskContinue)
	{
		logInfo = returnLog;
		
		boolean isSucess = currentLog.SetLogDetail(true,"", logInfo, false);
		if(!logInfo.isRunning() && time != null)
		{
			time.cancel();
			if(!isSucess)
			{
				TaskItemStop(true, true);
				return ;
			}
		}
		
		if(taskContinue)
		{
			if(logInfo.isRunning())
				RefreshLogGrid();  //刷新同个任务的日志
			else //正常的执行下一次循环(同个任务支持多次执行)         
				RunNext();
		}
		else  //不正常的执行下一个任务
			TaskItemStop(true, true);
	}
	
	/**
	 * 结束当前执行的任务项
	 * @param isError	是否是因为错误结束
	 * @param nextTask	是否执行下一个任务项
	 */
	private void TaskItemStop(boolean isError, boolean nextTask) {
		if (isError)
			runTask.EndError();
		else if(nextTask)
			runTask.End();
		
		runTask.setEndIndex(runCount);
		if (nextTask && runState == RunState.Running) {
			RefreshTask();
			taskIndex++;
			countIndex = 0;
			RunNext();
		}
		else{
			RefreshTask();
			RefreshLogGrid();
			SetButton(true);
		}
	}
	/**
	 * 页面按钮控制
	 * @param enabled	是否可用
	 */
	private void SetButton(boolean enabled)
	{
		for(int i = 0; i < btnList.size(); i++)
			btnList.get(i).setEnabled(enabled);
		btnStop.setEnabled(!enabled);
	}

	
	
	private TabItem drawDirectoryMsg(final GWTExecuteSetDirectory item){
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
		htmlStr.append(tdContent + item.GetPath() +tdEnd + "</tr>");
		htmlStr.append("<tr>" + tdLabel + "目录描述" + tdEnd);
		htmlStr.append(tdContentConb + (item.GetDesc()==null?"":item.GetDesc().replace("\n", "<br/>")) + tdEnd + "</tr>");
		htmlStr.append(tableEnd);
		Html html = new Html(htmlStr.toString());
		
		cp.add(html);  
		Button btn_update = new Button("修改目录信息");
		btn_update.setIcon(ICONS.EditCom());
		btn_update.addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				
				EditExecuteSet = item;
				GWTExecuteSetDirectory parent = (GWTExecuteSetDirectory)store.getParent(item);
				CreateDirEditForm(parent, true);
			}
		});
		ToolBar toolBar = new ToolBar();
		toolBar.add(btn_update);
		
		Button btn_del = new Button("删除目录");
		btn_del.addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				
				MessageBox.confirm("提示信息", "是否确认删除",
						new Listener<MessageBoxEvent>() {
							public void handleEvent(MessageBoxEvent be) {
								Button msgBtn = be.getButtonClicked();
								if (msgBtn.getText().equalsIgnoreCase("Yes")) {
									List<ModelData> list = new ArrayList<ModelData>();
									list.add(item);
									Integer count = store.getChildren(item).size();
									if (count != 0) {
										MessageBox.alert("错误提示","选中的文件夹【"
											+ item.GetName()
											+ "】不为空，无法删除！",null);
										return;
									}
									final GWTExecuteSetDirectory parent= (GWTExecuteSetDirectory)store.getParent(item);
									executeSetService.deleteSelectedItem(list, GetLoginLogID(),
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
														store.remove(item);
													} else {
														MessageBox
																.alert("错误提示",
																		"选中文件夹不为空，删除失败",
																		null);
													}
													esTreePanel.getSelectionModel().deselectAll();
													
													esTreePanel.getSelectionModel().select(parent, true);
												
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

	private void SetDragSource(final String name,final TreePanel<ModelData> sourceTree)
	{
		new TreePanelDragSource(sourceTree){
		    @Override
		    protected void onDragDrop(DNDEvent e) {
		    }
		    
		    @Override
			protected void onDragStart(DNDEvent e) {
		    	if(runState == RunState.Running)
		    	{
		    		e.setCancelled(true);
		    		return;
		    	}
		    	setStatusText("已选择 ({0}) 项" + name);
				ResetSelection();
				super.onDragStart(e);
			}
		    
		    private void ResetSelection()
		    {
		    	List<ModelData> sel = sourceTree.getSelectionModel().getSelectedItems();			
				sourceTree.getSelectionModel().setSelection(addtargetList(sel));
		    }
		    
		    
	    };
	}
	
	private List<ModelData> addtargetList(List<ModelData> sel){
    	List<ModelData> targList = new ArrayList<ModelData>();
    	for (ModelData child : sel) {
			if(child instanceof GWTCaseFlow)
				targList.add(child);
			if(child instanceof GWTCaseDirectory){
				//ucTreePanel.setExpanded(child, true, true);
				List<ModelData> lst = feedStore.getChildren(child);
				targList.addAll(addtargetList(lst));
			}
		}
    	return targList;
		
    }
	
	
	/**
	 * 创建日志区域
	 * @return		日志区域部件
	 */
	private void CreateLogWindow() {
		logWindow = new Window();
		logWindow.setHeading("执行日志记录");
		logWindow.setWidth(400);
		logWindow.setHeight(500);
		logWindow.setLayout(new FitLayout());
		logWindow.setScrollMode(Scroll.AUTOY);
		logWindow.setPlain(true);
		logWindow.setModal(false);
		
		GroupingStore<GWTQueueLog> store = new GroupingStore<GWTQueueLog>();
		store.setSortField(GWTQueueLog.N_Order);
	    store.groupBy(GWTQueueLog.N_GroupName);

	    ColumnConfig groupColumns = new ColumnConfig(GWTQueueLog.N_GroupName, "名称", 20);
	    HideColumnMenu(groupColumns);
	    
	    GridCellRenderer<GWTQueueLog> columnRender = new GridCellRenderer<GWTQueueLog>() {
			@Override
			public Object render(final GWTQueueLog model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<GWTQueueLog> store, Grid<GWTQueueLog> grid) {
				StringBuilder sb = new StringBuilder(500);
				sb.append("<div style = \"margin-left:10px;\">");
				List<String> logList = model.GetLogList();
				for(String log : logList)
				{
					sb.append("<div>" + log + "</div>");
				}
				if(model.getIsCase() && model.getIsEnd() && model.getIsSuccess())
				{
					String iconID = "icon_" + rowIndex + "_" + countIndex;
					sb.delete(sb.length()-6, sb.length());
					sb.append(",(" +
							"<span style='width:50px;' id = '" + iconID + "' ></span>)</div></div>");
					
					LabelField linkBtn = new LabelField("查看预期结果"){
						@Override
						protected void onRender(Element target, int index) {
							super.onRender(target, index);
							sinkEvents(Event.ONCLICK);
						}
						
						@Override
						protected void onClick(ComponentEvent ce) {
							fireEvent(Events.Select, ce);
						}
						 
					};
					linkBtn.addListener(Events.Select, new Listener<BaseEvent>(){
						@Override
						public void handleEvent(BaseEvent be) {
							GWTPack_Struct struct = model.getCompareResult();
							if(struct != null)
								new ResultCompare().ShowResult(struct,null);
							else
								MessageBox.alert("友情提示", "没有比对结果", null);
							
						}});
					linkBtn.setStyleAttribute("color", "blue");
					linkBtn.setStyleAttribute("cursor", "hand");
					HtmlContainer html = new HtmlContainer(sb.toString());
					html.add(linkBtn, "#" + iconID);
					
					return html;
				}
				else {
					sb.append("</div>");
					
					HtmlContainer html = new HtmlContainer(sb.toString());
					return html;
					
//					return sb.toString();
				}
			}
		};
		ColumnConfig rendercolumn = new ColumnConfig(GWTQueueLog.N_Type, "日志信息", 300);
		HideColumnMenu(rendercolumn);
		rendercolumn.setRenderer(columnRender);

	    List<ColumnConfig> config = new ArrayList<ColumnConfig>();
	    config.add(groupColumns);
	    config.add(rendercolumn);
	    ColumnModel cm = new ColumnModel(config);

	    MyGroupingView view = new MyGroupingView();
	    
	    view.setShowGroupedColumn(false);
	    view.setForceFit(true);
	    view.setGroupRenderer(new GridGroupRenderer() {
	      public String render(GroupColumnData data) {
				int index = Integer.valueOf(data.group);
				if (index == -1)
					return "";
				return "顺序号："
						+ (index + 1)
						+ ",任务名称："
						+ taskGrid.getStore().getAt(index).get(
								GWTQueueTask.N_TaskName);
			}
	    });
	    
	    logGrid = new MyGrid<GWTQueueLog>(store, cm);
	    logGrid.setView(view);
	    logGrid.setBorders(false);

	    ContentPanel logPanel = new ContentPanel();
	    logPanel.setBodyBorder(false);
	    logPanel.setBorders(false);
	    logPanel.setHeaderVisible(false);
	    logPanel.setLayout(new FillLayout());
	    logPanel.add(logGrid);
	    
		logWindow.add(logPanel);
	}
	
	/**
	 * 创建执行集队列窗口
	 */
	private void CreateExecuteSetWindow(){
		ExecuteSetListWindow = new Window();
		ExecuteSetListWindow.setHeight(400);
		ExecuteSetListWindow.setWidth(400);
		ExecuteSetListWindow.setPlain(true);
		ExecuteSetListWindow.setModal(false);
		ExecuteSetListWindow.setLayout(new FitLayout());
		ExecuteSetListWindow.setHeading("执行集队列");
		
		ContentPanel cPanel = new ContentPanel();
		cPanel.setHeaderVisible(false);
		cPanel.setScrollMode(Scroll.AUTOY);
		
		List<ColumnConfig> columns = new ArrayList<ColumnConfig>();
		RowNumberer columnNum = new RowNumberer();
		columns.add(columnNum);
		ColumnConfig column = new ColumnConfig(GWTExecuteSetDirectory.N_Name, "执行集名称", 100);
		columns.add(column);
		column = new ColumnConfig(GWTExecuteSetDirectory.N_Desc, "执行集描述", 150);
		columns.add(column);
		column = new ColumnConfig(GWTExecuteSetDirectory.N_StatusCHS, "执行状态", 60);
		columns.add(column);
		ColumnModel cm = new ColumnModel(columns);
		esListStore = new ListStore<GWTExecuteSetDirectory>();
		final Grid<GWTExecuteSetDirectory> grid = new Grid<GWTExecuteSetDirectory>(esListStore, cm);
		grid.setBorders(false);
		grid.setHeight(300);
		grid.setAutoWidth(true);
		grid.setAutoExpandColumn(GWTExecuteSetDirectory.N_Desc);
		Menu contextMenu = new Menu();
		contextMenu.setWidth(140);
		contextMenu.add(new MenuItem("删除", ICONS.DelCom(),
				new SelectionListener<MenuEvent>() {

					@Override
					public void componentSelected(MenuEvent ce) {
						
						List<GWTExecuteSetDirectory> sel = grid.getSelectionModel().getSelectedItems();
						for(GWTExecuteSetDirectory e : sel){
							esListStore.remove(e);
						}
					}
				}));
		grid.setContextMenu(contextMenu);
		GridDropTarget target = new GridDropTarget(grid){
			@Override
			public void onDragDrop(DNDEvent e){
				Object data = e.getData();
				List<ModelData> models = prepareDropData(data, true);
				if(e.getDragSource().getComponent() instanceof TreePanel)
				{
					List<ModelData> copyModels = new ArrayList<ModelData>();
					for(ModelData m: getEsChild(models)){
						if(esListStore.findModel((GWTExecuteSetDirectory)m)!=null)
							continue;
						else
							((GWTExecuteSetDirectory)m).SetStatus(1);
							copyModels.add(m);
					}
					e.setData(copyModels);
				}
				super.onDragDrop(e);				
			}
			@Override
			public void onDragMove(DNDEvent e){
				List<ModelData> data = e.getData();
				if(data.get(0) instanceof GWTQueueTask){	
					e.setCancelled(true);
				    e.getStatus().setStatus(false);
				    return;
				}else if(data.get(0) instanceof TreeStoreModel){
					if(!(((TreeStoreModel)data.get(0)).getModel() instanceof GWTExecuteSetDirectory)){
						e.setCancelled(true);
					    e.getStatus().setStatus(false);
					    return;
					}
				}
				super.onDragMove(e);
			}
			private List<ModelData> getEsChild(List<ModelData> md){
				List<ModelData> lst = new ArrayList<ModelData>();
				for(ModelData m : md){
					GWTExecuteSetDirectory es = (GWTExecuteSetDirectory)m;					
					if(es.GetObjType()==1){
						lst.add(es);
					}else if(es.GetObjType()==0){
						lst.addAll(getEsChild(store.getChildren(m)));
					}
				}
				return lst;
			}
		};
		target.setFeedback(Feedback.INSERT); 
		target.setOperation(Operation.COPY);
		ToolBar toolBar = new ToolBar();
		Button btnExec = new Button("执行", ICONS.Exec(),new SelectionListener<ButtonEvent>(){

			@Override
			public void componentSelected(ButtonEvent ce) {
				
				for(GWTExecuteSetDirectory m : esListStore.getModels()){
					m.SetStatus(1);
					esListStore.update(m);
				}
				totalCount = esListStore.getModels().size();
				DateTimeFormat dd = DateTimeFormat.getFormat("yyMMddHHmmss");
				execBN = dd.format(new Date())+ Random.nextInt();				
				currentCount = 0;
				isCycleExec = true;
				logWindow.show();
				CycleExec(true);
				
			}
			
		});
		toolBar.add(btnExec);
		Button btnStop = new Button("停止", ICONS.Stop(), StopHandler());
		toolBar.add(btnStop);
		cPanel.setTopComponent(toolBar);
		cPanel.add(grid);
		ExecuteSetListWindow.add(cPanel);
	}
	/**
	 * 循环执行任务
	 * @param execBN 
	 * @param alert	若无队列，则是否弹出框
	 */
	private void CycleExec(boolean alert)
	{
		if(currentCount == totalCount){
			MessageBox.alert("友情提示", "执行完成", null);
			return;
		}
		if(runState == RunState.Running)
			return;
		taskGrid.getStore().removeAll();
		ListStore<ModelData> store = taskGrid.getStore();
		List<GWTExecuteSetDirectory> lst = esListStore.getModels();
		GWTExecuteSetDirectory item = lst.get(currentCount++);
		item.SetStatus(2);
		esListStore.update(item);
		store.add(item.getTaskList().getChildren());
		taskGrid.reconfigure(store, taskGrid.getColumnModel());
		if(taskGrid.getStore().getModels().size() == 0)
		{
			if(alert)
				MessageBox.alert("友情提示", "队列为空，无法执行", null);
			else
			{
				logGrid.getStore().removeAll();
				logGrid.getView().setEmptyText("任务队列为空，无法执行");
				SetButton(true);
			}
			return;
		}
		
		runState = RunState.Running;
		SetButton(false);
		
		
		queueService.Insert2DataBase(item.GetObjectID().toString(),GetUserID(), execBN, new AsyncCallback<String>() {

			@Override
			public void onFailure(Throwable caught) {
				
				
				MessageBox.alert("友情提示", "新建新的执行日志记录失败", null);
			}

			@Override
			public void onSuccess(String result) {
				
				//保存 执行日志ID
				executeLogId = result;
				
				//清除之前日志
				taskIndex = 0;
				countIndex = 0;
				runCount = 0;
				logGrid.getStore().removeAll();
//				logGrid.recalculate();
				
				runTask = null;
				for(ModelData task : taskGrid.getStore().getModels())
				{
					((GWTQueueTask)task).Start();
				}
				RefreshTask();
				
				RunNext();
				
			}
			
		});

	}
}
