package com.dc.tes.ui.client.page;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.dc.tes.ui.client.AppContext;
import com.dc.tes.ui.client.IScriptFlowService;
import com.dc.tes.ui.client.IScriptFlowServiceAsync;
import com.dc.tes.ui.client.ICaseService;
import com.dc.tes.ui.client.ICaseServiceAsync;
import com.dc.tes.ui.client.IQueueService;
import com.dc.tes.ui.client.IQueueServiceAsync;
import com.dc.tes.ui.client.common.ServiceHelper;
import com.dc.tes.ui.client.enums.RunState;
import com.dc.tes.ui.client.model.GWTScriptFlowLog;
import com.dc.tes.ui.client.model.GWTCompareResult;
import com.dc.tes.ui.client.model.GWTPack_Struct;
import com.dc.tes.ui.client.model.GWTQueueLog;
import com.dc.tes.ui.client.model.GWTQueueTask;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.data.BaseTreeModel;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.ModelIconProvider;
import com.extjs.gxt.ui.client.dnd.GridDragSource;
import com.extjs.gxt.ui.client.dnd.GridDropTarget;
import com.extjs.gxt.ui.client.dnd.TreePanelDragSource;
import com.extjs.gxt.ui.client.dnd.DND.Feedback;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.DNDEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MenuEvent;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.GroupingStore;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.store.Store;
import com.extjs.gxt.ui.client.store.TreeStore;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.HtmlContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.TabItem;
import com.extjs.gxt.ui.client.widget.TabPanel;
import com.extjs.gxt.ui.client.widget.Viewport;
import com.extjs.gxt.ui.client.widget.TabPanel.TabPosition;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.button.IconButton;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.form.StoreFilterField;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.grid.GridGroupRenderer;
import com.extjs.gxt.ui.client.widget.grid.GroupColumnData;
import com.extjs.gxt.ui.client.widget.grid.GroupingView;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.layout.FillLayout;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.extjs.gxt.ui.client.widget.toolbar.SeparatorToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.extjs.gxt.ui.client.widget.treepanel.TreePanel;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Random;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.Widget;

/**
 * 队列任务定义、执行页面
 * @author scckobe
 *
 */
public class QueueTaskPage extends BasePage_Register {
	/**
	 * 异步服务
	 */
	IQueueServiceAsync queueService = ServiceHelper.GetDynamicService("queue", IQueueService.class);
	/**
	 * 队列ID
	 */
	private String queueID;
	/**
	 * 立即执行队列
	 */
	private boolean fireExec;
	/**
	 * 停止按钮
	 */
	Button btnStop = new Button();
	/**
	 * 案例数据树
	 */
	private TreePanel<ModelData> caseTree = null;
	/**
	 * 业务流树
	 */
	private TreePanel<ModelData> busiTree = null;
	/**
	 * 任务列表
	 */
	private Grid<ModelData> taskGrid = null;
	/**
	 * 日志列表
	 */
	private Grid<GWTQueueLog> logGrid = null;
	
	
	//运行时辅助属性
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
	
	String executeLogId = "";
	
	/**
	 * 构造函数
	 * @param queueID	队列ID
	 */
	public QueueTaskPage(String queueID) {
		this(queueID,false);
	}
	
	/**
	 * 构造函数
	 * @param queueID	队列ID
	 * @param fireExec	是否立即执行
	 */
	public QueueTaskPage(String queueID,boolean fireExec) {
		this.queueID = queueID;
		this.fireExec = fireExec;
	}

	@Override
	protected void onRender(Element parent, int index) {
		super.onRender(parent, index);

		Viewport viewPort = new Viewport();
		viewPort.setBorders(false);
		viewPort.setLayout(new BorderLayout());

		BorderLayoutData westData = new BorderLayoutData(LayoutRegion.WEST, 170);
		westData.setMinSize(170);
		westData.setMaxSize(230);
		westData.setSplit(true);
		viewPort.add(CreateSourceRegion(), westData);

		BorderLayoutData centerData = new BorderLayoutData(LayoutRegion.CENTER);
		centerData.setMargins(new Margins());
		viewPort.add(InitListControl(), centerData);

		add(viewPort);
		
		queueService.GetGWTQueueTaskList(GetSystemID(), queueID, new AsyncCallback<GWTQueueTask>(){

			@Override
			public void onFailure(Throwable caught) {
				MessageBox.alert("错误提示", "获取数据失败，页面无法编辑", null);
			}

			@Override
			public void onSuccess(GWTQueueTask result) {
				busiTree.getStore().add(result.getChild(0),true);
				busiTree.collapseAll();
				
				caseTree.getStore().add(((BaseTreeModel)result.getChild(1)).getChildren(),true);
				caseTree.collapseAll();
				
				ListStore<ModelData> store = taskGrid.getStore();
				store.add(((BaseTreeModel)result.getChild(2)).getChildren());
				taskGrid.reconfigure(store, taskGrid.getColumnModel());
				
				if(fireExec)
					Exec(false);
				else
					SetButton(true);
			}
		});
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

	/**
	 * 获得工具栏
	 * @return	工具栏
	 */
	private ToolBar InitButtonToolWidget() {
		ToolBar toolBar = new ToolBar();
		
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

		//执行按钮
		Button btnExec = new Button("执行", ICONS.Exec(), ExecHandler());
		btnExec.setEnabled(false);
		btnList.add(btnExec);
		toolBar.add(btnExec);

		//执行按钮
		btnStop = new Button("停止", ICONS.Stop(), StopHandler());
		btnStop.setEnabled(false);
		toolBar.add(btnStop);
		
		//执行按钮
//		Button btnTemp = new Button("刷新日志", ICONS.Stop(),new SelectionListener<ButtonEvent>(){
//
//			@Override
//			public void componentSelected(ButtonEvent ce) {
//				PageReconfigure();
//			}
//			
//		});
//		toolBar.add(btnTemp);
		
		toolBar.setStyleAttribute("border-left-style", "solid");
		toolBar.setStyleAttribute("border-left-color", "#99bbe8");
		toolBar.setStyleAttribute("border-left-width", "1px");
		
		return toolBar;
	}

	/**
	 * 创建资源元素的区域
	 * @return	资源元素的区域
	 */
	private Widget CreateSourceRegion() {
		TabPanel sourcePanel = new TabPanel();
		sourcePanel.setBorders(false);
		sourcePanel.setBodyBorder(false);
		
		sourcePanel.setTabPosition(TabPosition.BOTTOM);

		sourcePanel.add(CreateCaseItem());
		sourcePanel.add(CreateBusiItem());

		return sourcePanel;
	}

	/**
	 * 创建案例数据的TabItem
	 * @return				案例数据的TabItem
	 */
	private TabItem CreateCaseItem() {
		TreeStore<ModelData> treeStore = new TreeStore<ModelData>();
		caseTree = new TreePanel<ModelData>(treeStore);
		return CreateTabItem("s_CaseData","案例数据",false,
				caseTree,treeStore, new ModelIconProvider<ModelData>() {
					@Override
					public AbstractImagePrototype getIcon(
							ModelData item) {
						if(Integer.valueOf(item.get(GWTQueueTask.N_Type).toString()) == 0)
							return ICONS.menuCase();
						return EXTICONS.tree_folder();
					}
				});
	}
	
	/**
	 * 创建业务流的TabItem
	 * @return				业务流的TabItem
	 */
	private TabItem CreateBusiItem() {
		TreeStore<ModelData> treeStore = new TreeStore<ModelData>();
		busiTree = new TreePanel<ModelData>(treeStore);
		return CreateTabItem("s_BusiData","业务流",true,
				busiTree, treeStore,new ModelIconProvider<ModelData>() {
					@Override
					public AbstractImagePrototype getIcon(
							ModelData item) {
						if(((GWTQueueTask)item).getIsFolder())
							return EXTICONS.tree_folder();
						
						return ICONS.Script();
					}
				});
	}
	
	/**
	 * 创建树形TabItem
	 * @param id			Item的ID
	 * @param name			Item名称
	 * @param filterFolder	过滤器中，若无子节点，父节点是否仍显示
	 * @param tree			所添加的树
	 * @param store			所添加的树的数据存储
	 * @param iconProv		Icon提供行数
	 * @return				树形TabItem
	 */
	private TabItem CreateTabItem(String id,String name,boolean filterFolder,
			final TreePanel<ModelData> tree,final TreeStore<ModelData> store,ModelIconProvider<ModelData> iconProv)
	{
		TabItem tabItem = new TabItem(name);
		tabItem.setId(id);
		tabItem.setClosable(false);
		tabItem.setLayout(new FitLayout());
		tabItem.setBorders(false);
		tabItem.setScrollMode(Scroll.AUTO);

		ContentPanel treePanel = new ContentPanel();
		treePanel.setHeaderVisible(false);
		treePanel.setBodyBorder(false);
		treePanel.setBorders(false);
		treePanel.setScrollMode(Scroll.AUTO);
		
		ToolBar toolBar = new ToolBar();
		IconButton filterBtn = new IconButton("icon-filter");
		filterBtn.setWidth(20);
		toolBar.add(filterBtn);
		StoreFilterField<ModelData> treeFilter = GetNameFilter(filterFolder);
		toolBar.add(treeFilter);
		treePanel.setTopComponent(toolBar);
		
		tree.setDisplayProperty(GWTQueueTask.N_TaskName);
		tree.setIconProvider(iconProv);
		tree.setAutoLoad(true);
		tree.setAutoSelect(true);
		tree.setBorders(false);
		
		treeFilter.bind(store);
		SetDragSource(name,tree);
		
		treePanel.add(tree);
		tabItem.add(treePanel);
	    
		return tabItem;
	}
	
	/**
	 * 设置树形为拖拽源
	 * @param name			提示名称
	 * @param sourceTree	树形
	 */
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
				List<ModelData> targList = new ArrayList<ModelData>();
				for (ModelData checkChild : sel) {
					if (!((GWTQueueTask)checkChild).getIsFolder())
						targList.add(checkChild);
				}
				
				sourceTree.getSelectionModel().setSelection(targList);
		    }
	    };
	}
	
	/**
	 * 创建名称过滤器
	 * @param filterFolder	若无子节点，父节点是否仍显示
	 * @return				名称过滤器
	 */
	private StoreFilterField<ModelData> GetNameFilter(final boolean filterFolder)
	{
		return new StoreFilterField<ModelData>() {

			@Override
			protected boolean doSelect(Store<ModelData> store,
					ModelData parent, ModelData child, String property,
					String filter) {
				String name = child.get(GWTQueueTask.N_TaskName);
				if (name.indexOf(filter.toLowerCase()) != -1) {
					return true;
				}
				else if(filterFolder && ((GWTQueueTask)child).getIsFolder())
					return true;
				return false;
			}

		};
	}
	
	/**
	 * 创建任务、日志区域
	 * @return			任务、日志区域
	 */
	private Widget InitListControl() {
		Viewport viewPort = new Viewport();
		viewPort.setLayout(new BorderLayout());

		BorderLayoutData northData = new BorderLayoutData(LayoutRegion.NORTH,
				25);
//		northData.setMargins(new Margins());
		viewPort.add(InitButtonToolWidget(), northData);

		int westWidth =  Math.abs(AppContext.getTabPanel().getSelectedItem().getWidth()/2 -170);
		BorderLayoutData westData = new BorderLayoutData(LayoutRegion.WEST, westWidth);
		westData.setMinSize(100);
		westData.setSplit(true);
		viewPort.add(CreateTaskRegion(), westData);

		BorderLayoutData centerData = new BorderLayoutData(LayoutRegion.CENTER);
		viewPort.add(CreateLogRegion(), centerData);
		
		return viewPort;
	}
	
	/**
	 * 创建任务队列区域
	 * @return		任务队列区域部件
	 */
	private Widget CreateTaskRegion()
	{
		ContentPanel taskPanel = new ContentPanel();
		taskPanel.setHeaderVisible(false);
		taskPanel.setBorders(false);
		
		ListStore<ModelData> store = new ListStore<ModelData>();
		ColumnModel cm = new ColumnModel(GetCommonColumnConfig());
		taskGrid = new Grid<ModelData>(store, cm)
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

		
		
		taskGrid.setAutoWidth(true);
		taskGrid.setAutoExpandColumn(GWTQueueTask.N_TaskName);
		taskGrid.getView().setForceFit(true);
		
		taskGrid.setContextMenu(DefineTreeContextMenu());
		taskGrid.setBorders(false);
		taskGrid.setHeight(height);
		taskGrid.setAutoWidth(true);
		
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
					for (ModelData mdoelData : models) {
						copyModels.add(GWTQueueTask.Copy(mdoelData));
					}
					e.setData(copyModels);
				}
				super.onDragDrop(e);
				taskGrid.reconfigure(taskGrid.getStore(), taskGrid
						.getColumnModel());
			}
		};
		target.setAllowSelfAsSource(true);   
		target.setFeedback(Feedback.INSERT);  
		
//		return taskGrid;
		
		taskPanel.setLayout(new FillLayout());
		taskPanel.add(taskGrid);
		
		return taskPanel;
	}

	/**
	 * 创建日志区域
	 * @return		日志区域部件
	 */
	private Widget CreateLogRegion() {
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

	    GroupingView view = new GroupingView();
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
	    
	    logGrid = new Grid<GWTQueueLog>(store, cm);
	    logGrid.setView(view);
	    logGrid.setBorders(false);

	    ContentPanel logPanel = new ContentPanel();
	    logPanel.setBodyBorder(false);
	    logPanel.setBorders(false);
	    logPanel.setHeaderVisible(false);
	    logPanel.setLayout(new FillLayout());
	    logPanel.add(logGrid);
	    
		return logPanel;
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
		columns.add(column);
		////////////////////
		
		GridCellRenderer<GWTQueueTask> type = new GridCellRenderer<GWTQueueTask>() {
			@Override
			public Object render(final GWTQueueTask model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<GWTQueueTask> store, Grid<GWTQueueTask> grid) {
				//String iconID = "icon" + rowIndex;
//				HtmlContainer html = new HtmlContainer("<span>"
//						+ "<span style = 'margin:0px;padding:0px;' id = '"
//						+ iconID + "' ></span>" + "</span>");
//
//				String iconName = "task_" + model.get(GWTQueueTask.N_Type);
//				IconButton b = new IconButton(iconName);
				//b.setToolTip(model.get(GWTQueueTask.N_TypeCHS).toString());
				//html.add(b, "#" + iconID);				
				//return html;
				return model.get(GWTQueueTask.N_TypeCHS).toString();
			}
		};
		column = new ColumnConfig(GWTQueueTask.N_recount, "类型",35);
		HideColumnMenu(column);
		column.setRenderer(type);
		columns.add(column);
		
		/////////////////////////////
		column = new ColumnConfig(GWTQueueTask.N_TaskName, "名称", 100);//(交易码-案例名)
		HideColumnMenu(column);
		column.setResizable(true);
		columns.add(column);
		/*
		GridCellRenderer<GWTQueueTask> count = new GridCellRenderer<GWTQueueTask>() {
			@Override
			public Object render(final GWTQueueTask model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<GWTQueueTask> store, Grid<GWTQueueTask> grid) {
				final NumberField countField = new NumberField();
				countField.setValue(model.getRecCount());
				countField.setWidth(30);
				countField.setMinValue(1);
				countField.addListener(Events.OnFocus, new Listener<BaseEvent>(){
					@Override
					public void handleEvent(BaseEvent be) {
						if(runState == RunState.Running)
							countField.setReadOnly(true);
						else
							countField.setReadOnly(false);
					}});
				countField.addListener(Events.OnBlur, new Listener<BaseEvent>()
						{
							@Override
							public void handleEvent(BaseEvent be) {
								if(runState == RunState.Running)
									countField.setValue(Integer.valueOf(model.get(GWTQueueTask.N_recount).toString()));
								else
									model.set(GWTQueueTask.N_recount, countField.getValue().intValue());
							}
					
						});
				return countField;
			}
		};
		column = new ColumnConfig(GWTQueueTask.N_recount, "次数", 40);
		HideColumnMenu(column);
		column.setRenderer(count);
		columns.add(column);
		*/
		/*
		GridCellRenderer<GWTQueueTask> state = new GridCellRenderer<GWTQueueTask>() {
			@Override
			public Object render(final GWTQueueTask model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<GWTQueueTask> store, Grid<GWTQueueTask> grid) {
				IconButton ic = new IconButton("taskStatu_"  + model.getStatus());

				if (model.getStatus() != 0) {
				ic.addSelectionListener(new SelectionListener<IconButtonEvent>() {

							@Override
							public void componentSelected(IconButtonEvent ce) {
									List<GWTQueueLog> logList = new ArrayList<GWTQueueLog>();
									int beginIndex = model.getBeginIndex();
									boolean isRun = model.getStatus() == 1;
									int endIndex =isRun ? beginIndex + countIndex : model.getEndIndex();
									for(int i = beginIndex; i< endIndex; i++)
									{
										logList.add(logGrid.getStore().getAt(i));
									}
									logGrid.getSelectionModel().setSelection(logList);
								
							}
						});
				}
				else
				{
					ic.setStyleAttribute("cursor", "point");
				}
				
				return ic;
			}
		};
		column = new ColumnConfig(GWTQueueTask.N_recount, "状态", 40);
		HideColumnMenu(column);
		column.setRenderer(state);
		columns.add(column);
		*/
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
		column.setResizable(false);
		column.setMenuDisabled(true);
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
		
		//往执行日志表插入一条记录,待测试
		String execBn = DateTimeFormat.getFormat("yyMMddHHmmss").format(new Date());
		execBn += Random.nextInt();
		queueService.Insert2DataBase(queueID,GetUserID(),execBn, new AsyncCallback<String>() {

			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub
				
				MessageBox.alert("友情提示", "新建新的执行日志记录失败", null);
			}

			@Override
			public void onSuccess(String result) {
				// TODO Auto-generated method stub
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
	
	/**
	 * 更新日志列表，主要用于滚动条的控件
	 */
	private void RefreshLogGrid()
	{
		
		logGrid.getView().getScrollState();
		GWTQueueLog model = new GWTQueueLog();
		logGrid.getStore().add(model);
		logGrid.getStore().remove(model);
		
	}
	
	/**
	 * 更新任务列表，显示执行过程的滚动条
	 */
	private void RefreshTask()
	{
		/*
		taskGrid.reconfigure(taskGrid.getStore(), taskGrid.getColumnModel());
		*/
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
			return;
		}
		//如果之前点击暂停
		if(runState == RunState.Suspend)
		{
			runTask.End();
			TaskItemStop(IsError(), false);
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
			logGrid.getStore().add(currentLog);
//			logGrid.recalculate();
			
			if(runTask.isCase())
				RunCaseTask();
			else
				RunBusiTask();
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
		final IScriptFlowServiceAsync busiFlowService = ServiceHelper.GetDynamicService("busiFlow", IScriptFlowService.class);
		final String logID = GetUserID() + "_" + queueID + "_" + taskIndex + "_" + countIndex + "_" + executeLogId;
		final int scheduleTime = 1000;
		final Timer time = new Timer() {
			int i = 0;
			int delayTime = 50000;
			@Override
			public void run() {
				i++;
				final Timer self = this;
				busiFlowService.GetExecLog(logID, runTask.getTaskID(), new AsyncCallback<ModelData>(){
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
		
		busiFlowService.ExecScript(GetSysInfo(),logID, runTask.getTaskID(), executeLogId, new AsyncCallback<Boolean>(){
			@Override
			public void onFailure(Throwable caught) {
				PrintBusiLog(null,GWTScriptFlowLog.CreateExecptionLog("获取脚本失败"),false);
			}

			@Override
			public void onSuccess(Boolean haveScript) {
				if(haveScript)
				{
					PrintBusiLog(null,GWTScriptFlowLog.CreateBeginlLog(),true);
					time.scheduleRepeating(scheduleTime);
				}
				else
				{
					PrintBusiLog(null,GWTScriptFlowLog.CreateExecptionLog("脚本为空，无法执行"),false);
				}
			}});
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
				RefreshLogGrid();
			else
				RunNext();
		}
		else
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
	 * 获得队列执行Handler
	 * 
	 * @return 队列执行Handler
	 */
	private SelectionListener<ButtonEvent> ExecHandler() {
		return new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				SaveTask(false);
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
		queueService.SetQueueTask(queueID, taskGrid.getStore().getModels(), GetLoginLogID(),
				new AsyncCallback<Void>() {

					@Override
					public void onFailure(Throwable caught) {
						if(alert)
							MessageBox.alert("错误提示", "保存失败", null);
						
						//TODO:提示
					}

					@Override
					public void onSuccess(Void result) {
						if(alert)
							MessageBox.alert("友情提示", "保存成功", null);
					}

				});
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
						if(be.getButtonClicked().getText().equalsIgnoreCase(Message.messageBox_yes()))
						{
							logGrid.getStore().removeAll();
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
	
	@Override
	public void Exec()
	{
		PageReconfigure();
		Exec(false);
	}
	
	@Override
	public void PageReconfigure()
	{
		/*
		logGrid.reconfigure(logGrid.getStore(), logGrid.getColumnModel());
		taskGrid.reconfigure(taskGrid.getStore(), taskGrid.getColumnModel());
		*/
	}
	
	@Override
	protected boolean NeedRegister()
	{
		if(runState == RunState.NoStart || runState ==  RunState.Stop)
			return false;
		
		return true;
	}
}
