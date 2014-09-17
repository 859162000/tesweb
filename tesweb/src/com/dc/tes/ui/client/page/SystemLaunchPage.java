package com.dc.tes.ui.client.page;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.dc.tes.ui.client.ILaunchService;
import com.dc.tes.ui.client.ILaunchServiceAsync;
import com.dc.tes.ui.client.IMonitorService;
import com.dc.tes.ui.client.IMonitorServiceAsync;
import com.dc.tes.ui.client.IResultService;
import com.dc.tes.ui.client.IResultServiceAsync;
import com.dc.tes.ui.client.MainPage;
import com.dc.tes.ui.client.common.CustomerEvent;
import com.dc.tes.ui.client.common.ServiceHelper;
import com.dc.tes.ui.client.control.ConfigToolBar;
import com.dc.tes.ui.client.control.GridContentPanel;
import com.dc.tes.ui.client.model.GWTChannelInfo;
import com.dc.tes.ui.client.model.GWTCore;
import com.dc.tes.ui.client.model.GWTResultDetailLog;
import com.dc.tes.ui.client.model.GWTResultLog;
import com.dc.tes.ui.client.model.GWTResultLogMsg;
import com.dc.tes.ui.client.model.GWTSimuSystem;
import com.dc.tes.ui.client.model.GWTStock;
import com.extjs.gxt.charts.client.Chart;
import com.extjs.gxt.charts.client.model.ChartModel;
import com.extjs.gxt.charts.client.model.PieDataProvider;
import com.extjs.gxt.charts.client.model.charts.PieChart;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.binding.FormBinding;
import com.extjs.gxt.ui.client.core.XTemplate;
import com.extjs.gxt.ui.client.data.BaseListLoader;
import com.extjs.gxt.ui.client.data.BasePagingLoader;
import com.extjs.gxt.ui.client.data.ListLoadResult;
import com.extjs.gxt.ui.client.data.ModelComparer;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.extjs.gxt.ui.client.data.RpcProxy;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.GridEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.util.Padding;
import com.extjs.gxt.ui.client.util.Util;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.TabItem;
import com.extjs.gxt.ui.client.widget.TabPanel;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.TextArea;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.layout.FillData;
import com.extjs.gxt.ui.client.widget.layout.FillLayout;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayout;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayout.HBoxLayoutAlign;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.extjs.gxt.ui.client.widget.toolbar.FillToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.PagingToolBar;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Widget;

public class SystemLaunchPage extends BasePage {
	
	private ILaunchServiceAsync launchService;
	private ComboBox<GWTCore> corebox;
	private ListStore<GWTChannelInfo> channelstore = null;
	private IMonitorServiceAsync moniService;
	private IResultServiceAsync resultService;
	
	private ListStore<GWTStock> chartStore;
	
    private final Button start = new Button("启动");
    private final Button stop = new Button("停止");
	
	private boolean running = false;
	
	private ListStore<GWTCore> coreStore;
	private BaseListLoader<ListLoadResult<GWTCore>> loader;
	
	public SystemLaunchPage() {
		launchService = ServiceHelper.GetDynamicService("launchserver", ILaunchService.class);
		moniService = ServiceHelper.GetDynamicService("moniserver", IMonitorService.class);
		resultService = ServiceHelper.GetDynamicService("result", IResultService.class);
		
		this.addListener(CustomerEvent.MoniInfoCollect, new Listener<BaseEvent>(){

			@Override
			public void handleEvent(BaseEvent be) {
				GetChannelInfo();
			}
		});
		
	}
	
	protected void onRender(Element parent, int index) {
		super.onRender(parent, index);
		this.setLayout(new FitLayout());
		final ContentPanel cpMonitr = new ContentPanel();
		
		ToolBar toolBar = new ToolBar();
		toolBar.setAutoHeight(true);
		
		corebox = new ComboBox<GWTCore>();
		corebox.setWidth(175);
		corebox.setEditable(false);
	
		RpcProxy<List<GWTCore>> proxy = new RpcProxy<List<GWTCore>>() {

			@Override
			protected void load(Object loadConfig,
					AsyncCallback<List<GWTCore>> callback) {
				// TODO Auto-generated method stub
				launchService.GetCoreConfig(callback);
			}
			
		};
		
		loader = new BaseListLoader<ListLoadResult<GWTCore>>(proxy);
		coreStore = new ListStore<GWTCore>(loader);
		coreStore.setModelComparer(new ModelComparer<GWTCore>() {

			@Override
			public boolean equals(GWTCore m1, GWTCore m2) {
				// TODO Auto-generated method stub
				return m1.get(GWTCore.N_Path).equals(m2.get(GWTCore.N_Path));
			}
			
		});
		corebox.setFieldLabel("核心");
		corebox.setValueField(GWTCore.N_CoreFullName);
		corebox.setDisplayField(GWTCore.N_CoreFullName);
		corebox.setStore(coreStore);
		
		toolBar.add(corebox);
	    
	    toolBar.add(new FillToolItem());
	    
   
	    start.setIcon(MainPage.ICONS.StartMoni());
	    start.addSelectionListener(new SelectionListener<ButtonEvent>(){
			@Override
			public void componentSelected(ButtonEvent ce) {
				
				String path = corebox.getValue().get(GWTCore.N_CorePath);
				launchService.LaunchCore(path, new AsyncCallback<Void>() {

					@Override
					public void onFailure(Throwable arg0) {
						// TODO Auto-generated method stub
						
					}

					@Override
					public void onSuccess(Void arg0) {
						// TODO Auto-generated method stub
						start.setEnabled(false);
						stop.setEnabled(true);
					}
					
				});
			}});
	    
	    toolBar.add(start);
	    
	    stop.setIcon(MainPage.ICONS.Stop());
	    stop.setEnabled(false);
	    stop.addSelectionListener(new SelectionListener<ButtonEvent>(){
			@Override
			public void componentSelected(ButtonEvent ce) {
				String path = corebox.getValue().get(GWTCore.N_CorePath);
				launchService.StopCore(path,new AsyncCallback<Void>() {

					@Override
					public void onFailure(Throwable arg0) {
						// TODO Auto-generated method stub
						
					}

					@Override
					public void onSuccess(Void arg0) {
						// TODO Auto-generated method stub
						start.setEnabled(true);
						stop.setEnabled(false);
					}
					
				});
			}});
	    
	    toolBar.add(stop);
	    
	    Button reset = new Button("置换");
	    reset.setIcon(MainPage.ICONS.Refresh());
	    reset.addSelectionListener(new SelectionListener<ButtonEvent>(){

			@Override
			public void componentSelected(ButtonEvent ce) {
				// TODO Auto-generated method stub
				if(start.isEnabled()) {
					start.setEnabled(false);
					stop.setEnabled(true);
				} else {
					start.setEnabled(true);
					stop.setEnabled(false);
				}
			}
	    	
	    });
	    toolBar.add(reset);
	    
	    cpMonitr.setHeaderVisible(false);
	    cpMonitr.setTopComponent(toolBar);
	    
	    final BorderLayoutData northData = new BorderLayoutData(LayoutRegion.NORTH, 300);   
	    northData.setHideCollapseTool(true);

	    BorderLayoutData southData = new BorderLayoutData(LayoutRegion.CENTER, 100);  
	    southData.setHideCollapseTool(true); 
	    southData.setMargins(new Margins(3, 0, 0, 0));  

		final ContentPanel infoPanel = new ContentPanel();
		infoPanel.setHeaderVisible(false);
		infoPanel.setBorders(false);
		infoPanel.setWidth("50%");

		infoPanel.add(SetInfo(new GWTCore(""," "," "," "," "," ")));
		
		HBoxLayout hlayout = new HBoxLayout();
		hlayout.setHBoxLayoutAlign(HBoxLayoutAlign.STRETCH);
		hlayout.setPadding(new Padding(4));
		
		LayoutContainer topContainer = new LayoutContainer();
		topContainer.setHeight("50%");
		topContainer.setBorders(false);
		topContainer.setLayout(hlayout);

		topContainer.add(infoPanel);
		
		LayoutContainer splitContainer1 = new LayoutContainer();
		splitContainer1.setBorders(false);
		splitContainer1.setWidth(4);
		topContainer.add(splitContainer1);
		
		ContentPanel topRight = new ContentPanel();
		topRight.setHeaderVisible(false);
		topRight.setWidth("49%");
		topRight.setLayout(new FitLayout());
		topRight.add(GetTopRightPanelContent());
		topContainer.add(topRight);
		
		ContentPanel monitorCp = new ContentPanel();
		monitorCp.setHeading("当天案例通过率");
		monitorCp.setFrame(true); 
		monitorCp.setLayout(new RowLayout(Orientation.HORIZONTAL)); 
	    RowData data = new RowData(.5, 1);   
	    data.setMargins(new Margins(5));   
	
		monitorCp.add(drawGrid(), data);
		monitorCp.add(drawChartPanel(), data);
		
	    cpMonitr.setLayout(new BorderLayout());
		cpMonitr.add(monitorCp, southData);
		cpMonitr.add(topContainer, northData);
		
		add(cpMonitr);
	}
	
	private Widget drawChartPanel() {
		// TODO Auto-generated method stub
		
		ContentPanel piePanel = new ContentPanel();
		piePanel.setHeaderVisible(false);
		piePanel.setBorders(false);
		piePanel.setBodyBorder(false);
		piePanel.setWidth("50%");
		
		PieChart pieChart = new PieChart();
		pieChart.setAlpha(1.0f);
		pieChart.setNoLabels(false);
		pieChart.setTooltip("#label#<br>案例数：#val#<br>比例：#percent#");
		ChartModel pieModel = new ChartModel("当天案例通过率",
				"font-size: 14px; font-family: Verdana; text-align: center;");
		pieModel.setBackgroundColour("#ffffff");
		PieDataProvider dataProvider = new PieDataProvider(GWTStock.N_Pos, GWTStock.N_Name, GWTStock.N_Name);
		chartStore = new ListStore<GWTStock>();
		dataProvider.bind(chartStore);
		pieChart.setDataProvider(dataProvider);
		
//		List<String> colorList = new StatisticBase().GeneColorList(4);
//		pieChart.setColours(colorList);
		pieChart.setColours("#ff0000", "#00aa00", "#0000ff", "#ff9900", "#ff00ff"); 
		pieModel.addChartConfig(pieChart);
		
		String url = "gxt/chart/open-flash-chart.swf";
		Chart chartPie = new Chart(url);
		chartPie.setBorders(false);
		chartPie.setChartModel(pieModel);
		piePanel.add(chartPie);
		piePanel.setBorders(true);
		
		piePanel.addListener(Events.Attach,new Listener<BaseEvent>() {

			@Override
			public void handleEvent(BaseEvent be) {
				// TODO Auto-generated method stub
				loadData();
			}
			
		});
		
		return piePanel;
	}
	
	private void loadData() {
		// TODO Auto-generated method stub
			
		resultService.GetTodayResultLogMsg(GetSystemID(), new AsyncCallback<GWTResultLogMsg>() {
			
			@Override
			public void onSuccess(GWTResultLogMsg result) {
				// TODO Auto-generated method stub
				chartStore.removeAll();
				chartStore.add(new GWTStock("执行失败案例", result.GetFailedCaseCount().toString()));
				chartStore.add(new GWTStock("执行通过案例", result.GetPassCaseCount().toString()));				
				chartStore.add(new GWTStock("执行超时案例", result.GetTimeOutCaseCount().toString()));
				chartStore.add(new GWTStock("其它状态案例", result.GetOtherCaseCount().toString()));
				chartStore.commitChanges();
			}
			
			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub
				caught.printStackTrace();
				MessageBox.alert("错误提示", "获取执行日志信息失败！", null);
			}
		});
	}
	
	private ContentPanel drawGrid() {

		RpcProxy<PagingLoadResult<GWTResultLog>> proxy = new RpcProxy<PagingLoadResult<GWTResultLog>>() {
			@Override
			public void load(Object loadConfig,
					AsyncCallback<PagingLoadResult<GWTResultLog>> callback) {

				PagingLoadConfig config = ((PagingLoadConfig)loadConfig);
				String date = DateTimeFormat.getFormat(
						"yyyy-MM-dd").format(
						new Date());						
				config.set("date", date);
				resultService.GetList(GetSystemID(),"",
						config, callback);
			}
		};
		final BasePagingLoader<PagingLoadResult<GWTResultLog>> loader = new BasePagingLoader<PagingLoadResult<GWTResultLog>>(proxy);
		ListStore<GWTResultLog> store = new ListStore<GWTResultLog>(loader);
		
		Grid<GWTResultLog> grid = new Grid<GWTResultLog>(store,GetColumnConfig());
		grid.setLoadMask(true);
		grid.addListener(Events.Attach, new Listener<GridEvent<GWTResultLog>>() {

			@Override
			public void handleEvent(GridEvent<GWTResultLog> be) {
				// TODO Auto-generated method stub
				loader.load();
			}
			
		});
		
		ContentPanel panel = new ContentPanel();
		panel.setHeaderVisible(false);
		panel.setLayout(new FitLayout());
		panel.setBorders(true);
		panel.add(grid);
		PagingToolBar toolBar = new PagingToolBar(20) {
			  
			public void refresh() {
				super.refresh();
				loadData();
			}
		};   
		toolBar.bind(loader);

		panel.setBottomComponent(toolBar);
		return panel;

//	    GridContentPanel<GWTResultLog> panel = new GridContentPanel<GWTResultLog>();
//		panel.setProxy(proxy);
//		panel.setColumns(GetColumnConfig());
//		panel.DrowGridView(GWTResultLog.N_UserName,false,true);
//
//		panel.setBorders(true);
//		final PagingToolBar toolBar = new PagingToolBar(20);   
//		toolBar.bind(panel.getLoader());
//		panel.setBottomComponent(toolBar);
//		return panel;
	}
	
	private ColumnModel GetColumnConfig() {
		// TODO Auto-generated method stub
		List<ColumnConfig> columns = new ArrayList<ColumnConfig>();
		columns.add(new ColumnConfig(GWTResultLog.N_UserName, "执行用户", 70));
		columns.add(new ColumnConfig(GWTResultLog.N_CreateTime, "执行开始时间", 125));
		columns.add(new ColumnConfig(GWTResultLog.N_EndRunTime, "执行结束时间", 125));
		columns.add(new ColumnConfig(GWTResultLog.N_RunDuration, "执行时长", 80));
		columns.add(GetRenderColumn(GWTResultLog.N_PassFlag, "执行结果", 70));
		
		return new ColumnModel(columns);
	}
	
	private ColumnConfig GetRenderColumn(String id, String name,
			int width) {
		GridCellRenderer<GWTResultLog> gridRender = new GridCellRenderer<GWTResultLog>(){

			@Override
			public Object render(GWTResultLog model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<GWTResultLog> store, Grid<GWTResultLog> grid) {
				// TODO Auto-generated method stub
				if(model.getPassFlag().isEmpty()){
					return "";
				}
				if(model.getPassFlag().equals("1"))
					return "<span style='color:" + "green" + "'>" + "通过" + "</span>";
				else if (model.getPassFlag().equals("0")){
					 return "<span style='color:" + "red" + "'>" + "失败" + "</span>";
				}else if (model.getPassFlag().equals("2")){
					 return "<span style='color:" + "blue" + "'>" + "正在执行中" + "</span>";
				}else if (model.getPassFlag().equals("3")){
					 return "<span style='color:" + "blue" + "'>" + "未执行" + "</span>";
				}else if (model.getPassFlag().equals("4")){
					 return "<span style='color:" + "blue" + "'>" + "执行中断" + "</span>";
				}else if (model.getPassFlag().equals("5")){
					 return "<span style='color:" + "red" + "'>" + "超时" + "</span>";
				}else if (model.getPassFlag().equals("6")){
					 return "<span style='color:" + "blue" + "'>" + "中止执行" + "</span>";
				}else if (model.getPassFlag().equals("7")){
					 return "<span style='color:" + "red" + "'>" + "异常终断" + "</span>";
				}else{
					return "<span style='color:" + "black" + "'>" + "失败" + "</span>";
				}
			}		
		};
		ColumnConfig columnConfig = new ColumnConfig(id, name, width);
		columnConfig.setRenderer(gridRender);
		return columnConfig;
	}

	private FormPanel SetInfo(final GWTCore coreinfo) {
		FormPanel cp = new FormPanel();
		cp.setHeaderVisible(false);
		cp.setBodyBorder(false);
		cp.setBorders(false);
		cp.setScrollMode(Scroll.AUTO);
		cp.setFrame(false);
		
		String tableHead = "<table width=\"95%\" style=\"border:1px solid #cad9ea;color:#666; table-layout:fixed;"
			+ "empty-cells:show; border-collapse: collapse; margin:0 auto;font-size:12px;\">";
		String tdLabel = "<td width=\"17%\" style=\"font-size:12px; border:1px solid #cad9ea;padding:0 1em 0;background-color:#f5fafe; text-align:left;min-height:20px;\">";
		String tdContent = "<td width=\"36%\" style=\"font-size:12px; border:1px solid #cad9ea;padding:0 1em 0; min-height:20px;\">";
		String tdContentConb = "<td width=\"36%\" colspan=\"3\" style=\"font-size:12px; border:1px solid #cad9ea;padding:0 1em 0; min-height:20px;\">";
		String tdEnd = "</td>";
		String tableEnd = "</table>";
		
		StringBuffer htmlStr = new StringBuffer();
		htmlStr.append(tableHead);
		htmlStr.append("<tr>" + tdLabel + "核心名称" + tdEnd);		
		htmlStr.append(tdContent + "{corename}" + tdEnd);
		htmlStr.append(tdLabel + "创建时间" + tdEnd);
		htmlStr.append(tdContent + "{createtime}" + tdEnd + "</tr>");
		htmlStr.append("<tr>" + tdLabel + "发送适配器" + tdEnd);
		htmlStr.append(tdContent + "{sender}" + tdEnd);
		htmlStr.append(tdLabel + "接收适配器" + tdEnd);
		htmlStr.append(tdContent + "{receiver}" + tdEnd + "</tr>");
		
		htmlStr.append("<tr>" + tdLabel + "路径" + tdEnd);
		htmlStr.append(tdContentConb + "{path}");
		htmlStr.append(tableEnd);
		
	    final XTemplate template = XTemplate.create(htmlStr.toString());  
	    
	    final Html html = new Html(htmlStr.toString());
	    
	    template.overwrite(html.getElement(), Util.getJsObject(coreinfo));
	    
	    cp.add(html);
	    
		corebox.addSelectionChangedListener(new SelectionChangedListener<GWTCore>(){
			@Override
			public void selectionChanged(SelectionChangedEvent<GWTCore> se) {
				// TODO Auto-generated method stub
				template.overwrite(html.getElement(), Util.getJsObject(se.getSelectedItem()));  
			}
		});			
		
	    FormBinding binding = new FormBinding(cp);   
	  
	    binding.autoBind();  
	    binding.bind(coreinfo);
		return cp;
	}
	
	private Widget GetTopRightPanelContent(){
		
		GridCellRenderer<GWTChannelInfo> statusRender = new GridCellRenderer<GWTChannelInfo>(){

			@Override
			public Object render(GWTChannelInfo model, String property,
					com.extjs.gxt.ui.client.widget.grid.ColumnData config,
					int rowIndex, int colIndex,
					ListStore<GWTChannelInfo> store, Grid<GWTChannelInfo> grid) {
				if(model.get(property) == null)return"";
				int status =  model.<Integer>get(property);
				String icon = "graylight";
				if(status == 1)
					icon = "greenlight";
				else if(status == -1)
					icon = "redlight";
				else
					icon = "yellowlight";
				return "<span><img src='gxt/images/cus/" + icon + ".png' /></span>";
			}
			
		};
		
		List<ColumnConfig> columns = new ArrayList<ColumnConfig>();
		
		ColumnConfig column = new ColumnConfig();
		column.setId(GWTChannelInfo.N_Status);
		column.setAlignment(HorizontalAlignment.CENTER);
		column.setHeader("状态");
		column.setRenderer(statusRender);
	    column.setWidth(60);
	    columns.add(column);
	    
	    column = new ColumnConfig();
		column.setId(GWTChannelInfo.N_ChannelName);
		column.setHeader("组件名称");
	    column.setWidth(200);
	    columns.add(column);
		
		ColumnModel cm = new ColumnModel(columns);
		
		channelstore = new ListStore<GWTChannelInfo>();
		
		Grid<GWTChannelInfo> gird = new Grid<GWTChannelInfo>(channelstore, cm);
		gird.setAutoExpandColumn(GWTChannelInfo.N_ChannelName);
		gird.getView().setForceFit(true);
		
		return gird;
	}
	
		
	private void GetChannelInfo(){
		moniService.GetChannelList(this.GetSystemName(), new AsyncCallback<List<GWTChannelInfo>>(){

			@Override
			public void onFailure(Throwable caught) {
			
			}

			@Override
			public void onSuccess(List<GWTChannelInfo> result) {
				if(channelstore == null)
					return;
				channelstore.removeAll();
				if(result == null) {
					//暂假设通道不存在时，核心也没在运行
					start.setEnabled(true);
					stop.setEnabled(false);
					running = false;
					return;
				}
				//来到这里代表 核心已经运行
				if(!running) {
					start.setEnabled(false);
					stop.setEnabled(true);
					running = true;
				}
				
				loader.load();
				//暂时核心无法知道自己的路径，先取消
				//int index = corebox.getStore().indexOf(new GWTCore("",result.get(0).get(GWTChannelInfo.N_ChannelType).toString(),"","","",""));
				//corebox.setValue(corebox.getStore().getAt(index));
				
				channelstore.add(result);
			}
			
		});
	}
	
}
