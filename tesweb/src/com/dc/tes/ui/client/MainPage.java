package com.dc.tes.ui.client;

import java.util.List;

import com.dc.tes.ui.client.common.CookieManage;
import com.dc.tes.ui.client.common.CustomerEvent;
import com.dc.tes.ui.client.common.ServiceHelper;
import com.dc.tes.ui.client.enums.CsType;
import com.dc.tes.ui.client.icons.GwtIcons;
import com.dc.tes.ui.client.model.GWTMenu;
import com.dc.tes.ui.client.model.GWTMenuTab;
import com.dc.tes.ui.client.model.GWTProperties;
import com.dc.tes.ui.client.model.GWTSimuSystem;
import com.dc.tes.ui.client.page.AdapterPage;
import com.dc.tes.ui.client.page.BasePage_Register;
import com.dc.tes.ui.client.page.BatchPage;
import com.dc.tes.ui.client.page.LoginLogPage;
import com.dc.tes.ui.client.page.OperationLogPage;
import com.dc.tes.ui.client.page.CaseRunStatisticsPage;
import com.dc.tes.ui.client.page.RecordedCase;
import com.dc.tes.ui.client.page.ScriptFlowPage;
import com.dc.tes.ui.client.page.ChangePWDDialg;
import com.dc.tes.ui.client.page.DbHostPage;
import com.dc.tes.ui.client.page.ExecutePlanPage;
import com.dc.tes.ui.client.page.ExecuteSetPage;
import com.dc.tes.ui.client.page.InterfaceDefPage;
import com.dc.tes.ui.client.page.Login;
import com.dc.tes.ui.client.page.MonitorPage;
import com.dc.tes.ui.client.page.MsgTypePage;
import com.dc.tes.ui.client.page.PersisDataPage;
import com.dc.tes.ui.client.page.QueuePage;
import com.dc.tes.ui.client.page.RecCodePage;
import com.dc.tes.ui.client.page.ResultLogPage;
import com.dc.tes.ui.client.page.SimuStatusMoniPage;
import com.dc.tes.ui.client.page.SimuSystemPage;
import com.dc.tes.ui.client.page.StatisticSys;
import com.dc.tes.ui.client.page.StatisticTran;
import com.dc.tes.ui.client.page.SysDynamicParameterPage;
import com.dc.tes.ui.client.page.SystemLaunchPage;
import com.dc.tes.ui.client.page.TesEnvBuildPage;
import com.dc.tes.ui.client.page.TestRoundPage;
import com.dc.tes.ui.client.page.TranPage;
import com.dc.tes.ui.client.page.UseCasePage;
import com.dc.tes.ui.client.page.UserPage;
import com.dc.tes.ui.client.page.UserSys;
import com.extjs.gxt.themes.client.Access;
import com.extjs.gxt.themes.client.Olive;
import com.extjs.gxt.themes.client.Slate;
import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.Style.SelectionMode;
import com.extjs.gxt.ui.client.data.BaseListLoader;
import com.extjs.gxt.ui.client.data.BaseTreeModel;
import com.extjs.gxt.ui.client.data.ListLoadResult;
import com.extjs.gxt.ui.client.data.LoadEvent;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.ModelIconProvider;
import com.extjs.gxt.ui.client.data.RpcProxy;
import com.extjs.gxt.ui.client.data.TreeModel;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.IconButtonEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.LoadListener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.event.SelectionService;
import com.extjs.gxt.ui.client.image.XImages;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.store.TreeStore;
import com.extjs.gxt.ui.client.util.IconHelper;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.util.ThemeManager;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.HtmlContainer;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.TabItem;
import com.extjs.gxt.ui.client.widget.TabPanel;
import com.extjs.gxt.ui.client.widget.Viewport;
import com.extjs.gxt.ui.client.widget.button.IconButton;
import com.extjs.gxt.ui.client.widget.custom.ThemeSelector;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.layout.FillLayout;
import com.extjs.gxt.ui.client.widget.treepanel.TreePanel;
import com.extjs.gxt.ui.client.widget.treepanel.TreePanelSelectionModel;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * 整个系统的框架页面生成
 * @author scckobe
 * 修改： 2010-01-07 1)针对TabPanel的控制与shenfx用的冲突，做了一定的调整 2)修改header的样式展示
 * 		 2010-01-13 1)解决换用户报错的问题
 * 		 2010-01-25 1）菜单Icon的可配化，删除if else 的判断方式
 */
@SuppressWarnings("rawtypes")
public class MainPage implements EntryPoint, ValueChangeHandler  {
	public static final GwtIcons ICONS = GWT.create(GwtIcons.class);
	public static final XImages EXTICONS = GWT.create(XImages.class);
	ISimuSystemServiceAsync systemService = null;
	IHelperServiceAsync helperService = null;
	SimuStatusMoniPage simuStatusPage = null;

	private Viewport viewPort;
	private TabPanel tabPanel;
	private String menuProperties;
	private TreePanel<ModelData> tree;
	public ComboBox<GWTSimuSystem> comboSystem;
	private BaseListLoader<ListLoadResult<ModelData>> systemLoader;
	private GWTMenu selectedMenu;
	/**
	 * 当前所选择的系统ID
	 */
	private String systemID;
	/**
	 * 指示标志，用于指示	1)在ComboBox加载时是否重新加载数据
	 *       			2)在Loader的load事件中，判断是否需要重新设置SelectedValue以及为ComboBox加载事件
	 */
	private boolean systemInit = false;

	/**
	 * 所有类的入口点函数
	 */
	public void onModuleLoad() {
		ThemeManager.register(Slate.SLATE);
		ThemeManager.register(Access.ACCESS);
		ThemeManager.register(Olive.OLIVE);
		//GXT.setDefaultTheme(Theme.BLUE, true);
		CookieManage.InitStateManager();
		//CookieManage.ClearStateManager();
		AppContext.SetEntryPoint(this);	
		tabPanel = new TabPanel() {
			@Override
			protected void close(TabItem item) {
				if(this.getItemCount()==1){
					return;
				}
				super.close(item);
			}
			private void removeOrClose(TabItem item)
			{
				Component itemChild = item.getItem(0);			
				if(itemChild != null && itemChild instanceof BasePage_Register)
				{
					close(item);
				}
				else
				{
					remove(item);
				}
//				if(item.isClosable())
//					close(item);
//				else
//					remove(item);
			}
			
			@Override
			public boolean removeAll() {
				int count = getItemCount();
				TabItem current = getSelectedItem();
				for (int i = count - 1; i >= 0; i--) {
					TabItem item = getItem(i);
					if (current != item) {
						removeOrClose(item);
					}
				}
				if (current != null) {
					removeOrClose(current);
				}
				return getItemCount() == 0;
			}
		};
		tabPanel.setTabScroll(true);   
		tabPanel.setCloseContextMenu(true);   
//		tabPanel.setResizeTabs(true);
		
		AppContext.setTabPanel(tabPanel);
		InitMoniClient();
		InitLaunchMoni(); // By ljs
		if (CookieManage.IsLogin()) {
			Login();
		} else
			LoginOut();
		
	}
	
	/**
	 * 退出系统
	 */
	public void LoginOut() {
		InitTabView(false);
		CookieManage.ClearStateManager();
		RootPanel.get().clear();
		RootPanel.get().add(new Login());
	}
	
	/**
	 * 加载Loading页面
	 */
	private void Loading() {
		RootPanel.get().clear();
		RootPanel.get().add(GetLoadingPage());
	}
	
	/**
	 * 生成加载页面，并且返回给调用者
	 * @return	加载页面
	 */
	private HtmlContainer GetLoadingPage()
	{
		HtmlContainer LoadingContainer = new HtmlContainer();
		StringBuffer sb = new StringBuffer();
		sb.append("<div id='loading'><div class='loading-indicator'>" +
				"<img src='gxt/images/default/shared/large-loading.gif' height='32'/>招商银行通用接口测试工具<br />" +
				"<span id='loading-msg'>信息加载中...</span>" +
				"</div></div>");
		LoadingContainer = new HtmlContainer(sb.toString());
		return LoadingContainer;
	}

	/**
	 * 点击登录之后，进入系统
	 */
	public void Login() {
		systemInit = true;
		Loading();
		viewPort = new Viewport();
		viewPort.setLayout(new BorderLayout());
		InitMainPage();
	}
	
	/**
	 * 画总体页面框架
	 */
	private void InitMainPage()
	{
		InitMenuData();
	}
	
	
	/**
	 * 获得模拟系统信息
	 */
	public void InitSimuSystem()
	{
		systemLoader.load();
		
		comboSystem.removeAllListeners();
		comboSystem.setFireChangeEventOnSetValue(false);
		comboSystem.addListener(Events.Expand, 
				new Listener<BaseEvent>()
				{
					@Override
					public void handleEvent(BaseEvent be) {
						if(systemInit)
						{
							systemInit = false;
							systemLoader.load();
						}
					}
			
				});
		systemLoader.addLoadListener(new LoadListener()
		{
			@SuppressWarnings("unchecked")
			public void loaderLoad(LoadEvent le) {
				if (systemInit) {
					AfterSimuSystemLoad((List<GWTSimuSystem>) le.getData());
				}
			  }
		});
	}
	
	private void AfterSimuSystemLoad(List<GWTSimuSystem> result) {
		systemInit = false;
		int selectedIndex = 0;
		String selectedSystemID = CookieManage.GetSimuSystemID();
		if (!selectedSystemID.isEmpty()) {
			for (int i = 0; i < result.size(); i++) {
				if (result.get(i).GetSystemID().compareTo(selectedSystemID) == 0) {
					selectedIndex = i;
					break;
				}
			}
		}
		
		comboSystem
				.addSelectionChangedListener(new SelectionChangedListener<GWTSimuSystem>() {
					public void selectionChanged(
							SelectionChangedEvent<GWTSimuSystem> event) {
						systemID = event.getSelectedItem().toString();
						CookieManage.SetSimuSystemID(systemID);
						CookieManage.SetIsSyncComm(event.getSelectedItem().GetIsSync());
						CookieManage.SetIsClientSimu(event.getSelectedItem().GetIsClient());
						try
						{
							tabPanel.removeAll();  
							GWTMenu menu = GWTMenu.GetMenuList(menuProperties);
							tree.getStore().removeAll();
							tree.getStore().add(menu.getChildren(),true);
							tree.expandAll();
							String menuID = CookieManage.GetMenuID();
							if (menuID == null || menuID.isEmpty() || selectedMenu == null)
								selectedMenu = (GWTMenu) ((GWTMenu)menu.getChild(0)).getChild(0);
							tree.getSelectionModel().select(selectedMenu, true);

						}
						catch(Exception ex)
						{
							if(tabPanel == null)
								MessageBox.alert("错误信息", "Tab移除出错，Tab为空[1]" , null);
							MessageBox.alert("错误信息", "Tab移除出错，现有tab数为" + tabPanel.getItemCount() , null);
						}
						
						IOperationLogServiceAsync opLogService = ServiceHelper
						.GetDynamicService("operationLog", IOperationLogService.class);
						opLogService.writeLoginLog(CookieManage.GetUserID(),
								CookieManage.GetSimuSystemID(), CookieManage.GetLoginLogID(),
								new AsyncCallback<String>(){

							@Override
							public void onFailure(Throwable caught) {
								// TODO Auto-generated method stub
								caught.printStackTrace();
							}

							@Override
							public void onSuccess(String result) {
								// TODO Auto-generated method stub
								CookieManage.SetLoginLogID(result);
							}					
						});	
					}
				});
		if (selectedIndex >= 0){
			comboSystem.setValue(result.get(selectedIndex));
		}
		else if (!CookieManage.GetIsAdmin())
			LoginOut();
	}
	
	/**
	 * 加载表单头的Html
	 */
	private void InitNorthPanel() {
		HtmlContainer north = new HtmlContainer();
		StringBuffer sb = new StringBuffer();
		sb.append("<div style=\"background-image:url(dctheme/Image/dclogobg.jpg); " +
				"background-position:left center;width:100%;height:39px;\" >" +
				"<div style=\"background-image:url(dctheme/Image/dcLogo.png); background-position:left top; " +
				"background-repeat:no-repeat;width:100%;height:39px;padding-right:10px;padding-top:8px;\">" +
				"<div style=\"float:right;\">" +
					"<table border=\"0\" cellspacing=\"0\" cellpadding=\"0\">" +
					"<tr>" +
					"<td width = \"68%\" align=\"right\" nowrap>" +
					"<span style=\"font-size:12px;font-family:宋体; color:#ffffff;\">登录人员：" + CookieManage.GetUserName() + "</span>" +
					"<span style=\"font-size:12px;font-family:宋体; color:#ffffff;\">;当前模拟系统：</span>" +
					"</td>" +
					"<td id='header-theme' style = \"marigin-right:5px;\">" +
					"</td>" +
					"<td id='header-quit'>" +
					"</td>" +				
					"<td id='header-pwd'>" +
					"</td>" +
					"<td id='header-themeselect'>" +
					"</td>" +
					"</tr></table>" +
				"</div></div></div>");
		
		north = new HtmlContainer(sb.toString());
		north.setStateful(false);
		
		// 加载系统列表
		comboSystem = new ComboBox<GWTSimuSystem>();
		comboSystem.setEditable(false);
		comboSystem.setDisplayField(GWTSimuSystem.N_SystemName);
		comboSystem.setValueField(GWTSimuSystem.N_SystemID);
		comboSystem.setStyleAttribute("border", "#ffffff");
		comboSystem.setLoadingText("加载中...");
		north.add(comboSystem, "#header-theme");
		RpcProxy<List<GWTSimuSystem>> proxy = new RpcProxy<List<GWTSimuSystem>>() {
			@Override
			public void load(Object loadConfig,
					AsyncCallback<List<GWTSimuSystem>> callback) {
				systemService = ServiceHelper.GetDynamicService("simuSys", ISimuSystemService.class);
				systemService.GetListByUserID(CookieManage.GetIsAdmin() ? "" : CookieManage
						.GetUserID(), callback);
			}
		};
		 systemLoader = new BaseListLoader<ListLoadResult<ModelData>>(
				proxy);
		ListStore<GWTSimuSystem> store = new ListStore<GWTSimuSystem>(systemLoader); 
		comboSystem.setStore(store);
		

		// 修改密码按钮
		IconButton btnPWD = new IconButton("btn_Pwd");
		north.add(btnPWD, "#header-pwd");
		btnPWD.addSelectionListener(new SelectionListener<IconButtonEvent>() {
			@Override
			public void componentSelected(IconButtonEvent ce) {
				new ChangePWDDialg();
			}
		});
		
//		//帮助按钮
//		IconButton btnHelp = new IconButton("btn_Help");
//		north.add(btnHelp, "#header-help");

		//退出按钮
		IconButton btnQuit = new IconButton("btn_quit");
		btnQuit.addSelectionListener(new SelectionListener<IconButtonEvent>() {
			@Override
			public void componentSelected(IconButtonEvent ce) {
				LoginOut();
			}
		});
		north.add(btnQuit, "#header-quit");
		
		//主题切换
		ThemeSelector themeSelector = new ThemeSelector();
		themeSelector.setWidth(100);
		north.add(themeSelector, "#header-themeselect");

		BorderLayoutData northData = new BorderLayoutData(LayoutRegion.NORTH,
				37);
		northData.setMargins(new Margins());

		viewPort.add(north, northData);
	}
	
	/**
	 * 加载功能菜单
	 */
	private void InitMenuData() {
//		Properties properties = new Properties();
		helperService = ServiceHelper.GetDynamicService("helper", IHelperService.class);
		helperService.GetTESConfig(AppContext.GetConfigRoot(),
				new AsyncCallback<List<GWTProperties>>() {
					@Override
					public void onFailure(Throwable caught) {
						AlertAndLogOut("功能菜单加载失败，请与维护人员联系");
					}

					@Override
					public void onSuccess(List<GWTProperties> properties) {
						try {
							InitNorthPanel();
							menuProperties = AppContext.StoreConfig(properties);
							InitMenuUI(menuProperties);
							InitTabView(true);
							RootPanel.get().clear();
							RootPanel.get().add(viewPort);
							tree.expandAll();
							InitSimuSystem();
							
						} catch (Exception ex) {
							AlertAndLogOut("页面初始化失败，请与维护人员联系");
						}
					}

				});
	}
	
	@SuppressWarnings("unchecked")
	private void InitMenuUI(String menuStr)
	{
		ContentPanel west = new ContentPanel();
		west.setHeading("功能导航");
		//json序列化菜单信息
	//	GWTMenu menu = GWTMenu.GetMenuList(menuStr);
		final TreeStore<ModelData> store = new TreeStore<ModelData>();
	//	store.add(menu.getChildren(),true);
		
		tree = new TreePanel<ModelData>(store) {
			
			@Override
			public void setSelectionModel(TreePanelSelectionModel<ModelData> sm) {
				sm = new TreePanelSelectionModel<ModelData>(){
					protected void doSingleSelect(ModelData model, boolean supressEvent) {
					    if(((BaseTreeModel)model).getChildCount() != 0)
					    {
					    	//将父节点的选择操作，转化为伸缩操作
					    	TreeNode node = findNode(model);
					    	if(node.isExpanded())
					    		tree.setExpanded(model, false);
					    	else
					    		tree.setExpanded(model, true);
					    	return ;
					    }
					    super.doSingleSelect(model, supressEvent);
					  }
					
//					@Override
//					public void handleEvent(TreePanelEvent be) {
//						if(be.getChild() != null)
//							return;
//						super.handleEvent(be);
//					  }
					
					@Override
					  protected void onSelectChange(ModelData model, boolean select) {
						if(((BaseTreeModel)model).getChildCount() != 0)
							return;
						super.onSelectChange(model, select);
					  }
				};
				
				//设置树形只能单选操作
				sm.setSelectionMode(SelectionMode.SINGLE);
				super.setSelectionModel(sm);
			}
		};
		//解决多选问题
//		tree.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		tree.setDisplayProperty(GWTMenu.Desc);
		tree.setIconProvider(new ModelIconProvider<ModelData>() {
					@Override
					public AbstractImagePrototype getIcon(
							ModelData item) {
						GWTMenu menuItem = (GWTMenu)item;
						if(menuItem.getChildCount() == 0)
							return IconHelper.createPath(menuItem.getIcon());
						
						return IconHelper.createPath(menuItem.getIcon(),28,28);
					}
				});
//		TreePanelSelectionModel<ModelData>new TreePanelSelectionModel<ModelData>()
//		tree.setSelectionModel();

		tree.setWidth(250);
		tree.setAutoLoad(true);
		//tree.setAutoSelect(true);

		SelectionService.get().addListener(MenuChangeListener());
		SelectionService.get().unregister(tree.getSelectionModel());
		SelectionService.get().register(tree.getSelectionModel());
		
		String initToken = History.getToken();
		if (initToken.length() == 0) {
		      History.newItem("1");
		    }

		History.addValueChangeHandler(this);
		History.fireCurrentHistoryState();
		west.add(tree);
		
		BorderLayoutData westData = new BorderLayoutData(LayoutRegion.WEST, 200);
		westData.setSplit(true);
		westData.setCollapsible(true);
		westData.setMargins(new Margins(5));
		viewPort.add(west, westData);
		
		
	}
	
	private SelectionChangedListener<TreeModel> MenuChangeListener()
	{
		return new SelectionChangedListener<TreeModel>() {
			@Override
			public void selectionChanged(
					SelectionChangedEvent<TreeModel> event) {
				List<TreeModel> sel = event
						.getSelection();
				if (sel.size() > 0) {
					TreeModel m = (TreeModel) event
							.getSelection().get(0);
					if (m != null
							&& m instanceof GWTMenu) {
						if (!m.isLeaf()) {
							event.setCancelled(true);
							return;
						}
						else if(((GWTMenu) m).getURL().isEmpty())
						{
							selectedMenu = (GWTMenu) m;
							History.newItem(selectedMenu.GetID());
							DrawTab();							
						}
						else
						{
							event.setCancelled(true);
							String URL= GWT.getHostPageBaseURL() + ((GWTMenu) m).getURL(); 
							com.google.gwt.user.client.Window.open(URL, "", "");
							return;
						}
					}
				}
			}
		};
	}

	/**
	 * 初始化TabContainer
	 * @param needAdd 是否需要添加到ViewPort中
	 * 初始化加载时 传入true
	 * 退出时      传入false,只做清理工作
	 */
	private void InitTabView(boolean needAdd) {
		if(tabPanel.getItemCount() != 0)
			tabPanel.removeAll();
		
		if(!needAdd)
			return;
		TabItem item = new TabItem();
		item.add(GetLoadingPage());
		item.setText("功能加载中...");
		tabPanel.add(item);

		BorderLayoutData centerData = new BorderLayoutData(LayoutRegion.CENTER);
		centerData.setMargins(new Margins(5, 5, 5, 5));

		viewPort.add(tabPanel, centerData);
	}

	/**
	 * 画Tab容器
	 */
	private void DrawTab() {
		try {
			if(selectedMenu == null) 
				return ;
			if(tabPanel.getItemCount() != 0 )
				if(selectedMenu.GetID().compareTo(CookieManage.GetMenuID()) == 0 
						&& systemID.compareTo(CookieManage.GetSimuSystemID()) == 0)
					return;
				
			CookieManage.SetMenuItem(selectedMenu.GetID());
			
//			try
//			{
//				//tabPanel.removeAll();  //修改为选择其它节点不关闭原有节点    by xuat
//			}
//			catch(Exception ex)
//			{
//				if(tabPanel == null)
//					MessageBox.alert("错误信息", "Tab移除出错，Tab为空[1]" , null);
//				MessageBox.alert("错误信息", "Tab移除出错，现有tab数为" + tabPanel.getItemCount() , null);
//			}
			
			List<GWTMenuTab> tabList = selectedMenu.GetTabList();
			if(tabList.size() == 0)
				return;
			else
				AddTabItem(tabList);
		} catch (Exception e) {
			e.printStackTrace();
			MessageBox.alert("错误信息", "本功能实现出错", null);
		}
	}

	/**
	 * 为TabContainer添加Item项
	 * @param tabList GWTMenuTab列表
	 */
	public void AddTabItem(List<GWTMenuTab> tabList)
	{
		simuStatusPage = null;
		
		for(int i = 0; i< tabList.size(); i++)
		{
			try {
				GWTMenuTab menuTab = tabList.get(i);
				AddTabItem(menuTab.GetOpenClassName(),menuTab.GetTabTitle(),GetContainer(menuTab.GetOpenClassName()),menuTab.GetCanClose());
			} catch (Exception e) {
				e.printStackTrace();
				MessageBox.alert("错误信息", "AddTabItem(List<GWTMenuTab> tabList)", null);
			}
		}
	}
	
	/**
	 * 为TabContainer添加Item项
	 * 
	 * @param ID Item项的ID
	 * @param title Item项的描述信息
	 * @param form  Item所包含的部件
	 */
	public void AddTabItem(String ID, String title, LayoutContainer form) {
		try {
			AddTabItem(ID,title,form,true);
		} catch (Exception e) {
			e.printStackTrace();
			MessageBox.alert("错误信息", "AddTabItem(String ID, String title, LayoutContainer form)", null);
		}
	}
	
	public boolean HaveTabItemID(String ID)
	{
		return tabPanel.getItemByItemId(ID) != null;
	}
	
	/**
	 * 为TabContainer添加Item项
	 * 
	 * @param ID Item项的ID
	 * @param title Item项的描述信息
	 * @param form  Item所包含的部件
	 * @param closable 是否可关闭
	 */
	public void AddTabItem(String ID, String title, LayoutContainer form,
			boolean closable) {
		if (HaveTabItemID(ID)) {
			tabPanel.setSelection(tabPanel.getItemByItemId(ID));
		} else {
			TabItem item = new TabItem(title);
			item.setId(ID);
			item.setLayout(new FillLayout());
			//item.setClosable(closable);
			item.setClosable(true);  //所有tab改为可关闭
			item.add(form);
			tabPanel.add(item);
			tabPanel.setSelection(item);
		}
	}
	
	/**
	 * 根据关键字获得部件内容
	 * 
	 * @param className
	 *            类名称
	 * @return 部件内容
	 */
	private LayoutContainer GetContainer(String className) {
		if (className.compareTo("SimuSys") == 0)
			return new SimuSystemPage();
		else if (className.compareTo("User") == 0)
			return new UserPage();
		else if (className.compareTo("TranRecv") == 0)
			return new TranPage(CsType.Server);
		else if (className.compareTo("TranSend") == 0)
			return new TranPage(CsType.Client);
		else if (className.compareTo("ScriptFlow") == 0)
		{
			return new ScriptFlowPage();
		}
		else if (className.compareTo("Queue") == 0)
		{
			return new QueuePage();
		}
		else if(className.compareTo("DbHost") == 0)
			return new DbHostPage();
		else if(className.compareTo("RecordedCase") == 0)
			return new RecordedCase();
		else if(className.compareTo("SysPara") == 0) 
			return new SysDynamicParameterPage();
		else if(className.compareTo("Result") == 0)
			return new ResultLogPage();
		else if(className.compareTo("Batch") == 0)
			return new BatchPage();
		else if(className.compareTo("UserSys") == 0)
			return new UserSys();
		else if(className.compareTo("pData") == 0)
			return new PersisDataPage();
		else if(className.compareTo("Launch") == 0) {
			if(AppContext.GetLaunchPage() == null){
				AppContext.SetLaunchPage(new SystemLaunchPage());
			}
			return AppContext.GetLaunchPage();
		}
		else if(className.compareTo("SimuStatus") == 0){
			simuStatusPage = new SimuStatusMoniPage();
			return simuStatusPage;
		}
		else if(className.compareTo("Adapter") == 0)
			return new AdapterPage();
		else if(className.compareTo("MsgType") == 0)
			return new MsgTypePage();
		else if(className.compareTo("TransRecognizer") == 0)
			return new RecCodePage();
		else if(className.compareTo("TesEnvBuild") == 0)
			return new TesEnvBuildPage();
		else if(className.compareTo("Monitor") == 0){
			//监控页面需要维护之前的监控信息，因此不需要每次点击菜单都重新初始化，。
			if(AppContext.GetMonitorPage() == null){
				AppContext.SetMonitorPage(new MonitorPage());
			}
			return AppContext.GetMonitorPage();
		}
		else if(className.compareTo("StatSys") == 0)
					return new StatisticSys();
		else if(className.compareTo("StatTran") == 0)
			return new StatisticTran();
		else if(className.compareTo("UseCase") == 0)
			if(AppContext.GetRegisterPage(className)==null)
				return new UseCasePage();
			else {
				return AppContext.GetRegisterPage(className);
			}
		else if(className.compareTo("ExecuteSet") == 0)
			return new ExecuteSetPage();
		else if(className.compareTo("TestRound") == 0)
			return new TestRoundPage();
		else if(className.compareTo("ExecutePlan") == 0)
			return new ExecutePlanPage();
		else if(className.compareTo("Interface") == 0)
			return new InterfaceDefPage();
		else if(className.compareTo("LoginLog") == 0)
			return new LoginLogPage();
		else if(className.compareTo("OperationLog") == 0)
			return new OperationLogPage("");
		else if(className.compareTo("CaseRunStatistics") == 0)
			return new CaseRunStatisticsPage(false);
		else if(className.compareTo("FactorChangeStatistics") == 0)
			return new CaseRunStatisticsPage(true);
		else
			return new LayoutContainer();
//		return new Login();
	}

//	/**
//	 * 获得树形节点的Icon图像
//	 * @param iconName icon名称
//	 * @return icon图像
//	 */
//	private AbstractImagePrototype GetIcon1(String iconName) {
//		if (iconName.compareTo("menuSimuSystem") == 0)
//			return ICONS.menuSimuSystem();
//		else if (iconName.compareTo("menuUser") == 0)
//			return ICONS.menuUser();
//		else if (iconName.compareTo("menuUserSys") == 0)
//			return ICONS.menuUserSystem();
//		else if (iconName.compareTo("menuTranRecv") == 0)
//			return ICONS.menuTranRecv();
//		else if (iconName.compareTo("menuTranSend") == 0)
//			return ICONS.menuTranSend();
//		else if (iconName.compareTo("Monitor") == 0)
//			return ICONS.Monitor();
//		else if(iconName.compareTo("pData") == 0)
//		{
//			return ICONS.menuPData();
//		}
//		else
//			return EXTICONS.tree_folder();
//	}
	
//	/**
//	 * 获得树形节点的Icon图像
//	 * @param iconName icon名称
//	 * @return icon图像
//	 */
//	private AbstractImagePrototype GetIcon(String iconName) {
//		return IconHelper.createPath("dctheme/Image/menu/" + iconName);
//	}

	/**
	 * 弹出错误友情提示信息，并退出系统
	 * @param alertMsg 友情提示信息
	 */
	private void AlertAndLogOut(String alertMsg) {
		MessageBox.alert("友情提示", alertMsg, new Listener<MessageBoxEvent>() {
			@Override
			public void handleEvent(MessageBoxEvent be) {
				LoginOut();
			}
		});
	}
	
	/**
	 * 重载系统信息,只是做个标志，让下次在Expand时重新加载loader
	 */
	public void ReloadSystem() {
		systemInit = true;
	}
	
	private void InitMoniClient() {
		AppContext.SetMonitorPage(new MonitorPage());
		Timer time = new Timer() {

			@Override
			public void run() {
				if (AppContext.GetMonitorPage() != null)
					AppContext.GetMonitorPage().CollectLogInfo();
				if (simuStatusPage != null){
					simuStatusPage.fireEvent(CustomerEvent.MoniInfoCollect);
				}
			}
		};	
		time.scheduleRepeating(5000);	
		
	}
	
	private void InitLaunchMoni() {
		AppContext.SetLaunchPage(new SystemLaunchPage());
		Timer time = new Timer() {

			@Override
			public void run() {
				if (AppContext.GetLaunchPage() != null){
					AppContext.GetLaunchPage().fireEvent(CustomerEvent.MoniInfoCollect);
				}
			}
		};	
		time.scheduleRepeating(5000);	
	}

	@Override
	public void onValueChange(ValueChangeEvent event) {
		// TODO Auto-generated method stub
			// TODO Auto-generated method stub
			String historyToken = event.getValue().toString();			
			if(!historyToken.isEmpty()){			
				ModelData modelData = tree.getStore().findModel(GWTMenu.ID, Integer.parseInt(historyToken));
				tree.getSelectionModel().select(true, modelData);	
			}
	}

	
	
}
