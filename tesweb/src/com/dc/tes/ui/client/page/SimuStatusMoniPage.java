package com.dc.tes.ui.client.page;

import java.util.ArrayList;
import java.util.List;

import com.dc.tes.ui.client.AppContext;
import com.dc.tes.ui.client.IMonitorService;
import com.dc.tes.ui.client.IMonitorServiceAsync;
import com.dc.tes.ui.client.ISimuStatus;
import com.dc.tes.ui.client.ISimuStatusAsync;
import com.dc.tes.ui.client.MainPage;
import com.dc.tes.ui.client.common.CustomerEvent;
import com.dc.tes.ui.client.common.ServiceHelper;
import com.dc.tes.ui.client.model.GWTChannelInfo;
import com.dc.tes.ui.client.model.GWTRealTimeLogInfo;
import com.dc.tes.ui.client.model.GWTStatusSys;
import com.extjs.gxt.charts.client.Chart;
import com.extjs.gxt.charts.client.model.ChartModel;
import com.extjs.gxt.charts.client.model.Legend;
import com.extjs.gxt.charts.client.model.Legend.Position;
import com.extjs.gxt.charts.client.model.charts.PieChart;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.data.BaseListLoader;
import com.extjs.gxt.ui.client.data.ListLoadResult;
import com.extjs.gxt.ui.client.data.ListLoader;
import com.extjs.gxt.ui.client.data.LoadEvent;
import com.extjs.gxt.ui.client.data.RpcProxy;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.LoadListener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.util.Padding;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.HtmlContainer;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.Slider;
import com.extjs.gxt.ui.client.widget.TabItem;
import com.extjs.gxt.ui.client.widget.TabPanel;
import com.extjs.gxt.ui.client.widget.TabPanel.TabPosition;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.SliderField;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.layout.FillLayout;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayout;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayout.HBoxLayoutAlign;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Widget;

public class SimuStatusMoniPage extends BasePage {
	
	private ISimuStatusAsync service = null;
	private IMonitorServiceAsync moniService = null;
	private String SERVLETNAME = "simustatus";
	private Grid<GWTRealTimeLogInfo> tranGrid = null;
	private Chart chart = null;
	private PieChart pie = null;
	private HtmlContainer sysInfoPanelHTML = new HtmlContainer();
	private ContentPanel topLeft = null;
	private boolean init = false;
	private boolean clickRefresh = false;
	
	private ListStore<GWTRealTimeLogInfo> caseStore = null;
	private ListStore<GWTRealTimeLogInfo> tranStore = null;
	private ListStore<GWTChannelInfo> channelstore = null;
	
	public SimuStatusMoniPage(){
		
		this.addListener(CustomerEvent.MoniInfoCollect, new Listener<BaseEvent>(){

			@Override
			public void handleEvent(BaseEvent be) {
				GetChannelInfo();
			}
		});
		
		InitService();
		PrepareData();
	}

	protected void onRender(Element parent, int index) {
		super.onRender(parent, index);
		this.setLayout(new FillLayout());
		
		ContentPanel bodyPanel = new ContentPanel();
		bodyPanel.setHeaderVisible(false);
		bodyPanel.setBorders(false);
		bodyPanel.setBodyBorder(false);
		bodyPanel.setLayout(new FillLayout());
		
		HBoxLayout hlayout = new HBoxLayout();
		hlayout.setHBoxLayoutAlign(HBoxLayoutAlign.STRETCH);
		hlayout.setPadding(new Padding(4));
		HBoxLayout hlayout1 = new HBoxLayout();
		hlayout1.setHBoxLayoutAlign(HBoxLayoutAlign.STRETCH);
		hlayout1.setPadding(new Padding(0,4,4,4));
		
		LayoutContainer topContainer = new LayoutContainer();
		topContainer.setHeight("50%");
		topContainer.setBorders(false);
		topContainer.setLayout(hlayout);
		
		LayoutContainer bottomContainer = new LayoutContainer();
		bottomContainer.setHeight("50%");
		bottomContainer.setBorders(false);
		bottomContainer.setLayout(hlayout1);
		
		topLeft = new ContentPanel();
		topLeft.setHeading("模拟器配置信息");
		topLeft.setWidth("50%");
		topLeft.add(sysInfoPanelHTML);
		topContainer.add(topLeft);
		
		LayoutContainer splitContainer1 = new LayoutContainer();
		splitContainer1.setBorders(false);
		splitContainer1.setWidth(4);
		topContainer.add(splitContainer1);
		
		ContentPanel topRight = new ContentPanel();
		topRight.setHeading("适配器状态");
		topRight.setWidth("49%");
		topRight.setLayout(new FitLayout());
		topRight.add(GetTopRightPanelContent());
		topContainer.add(topRight);
		
		ContentPanel bottomLeft = new ContentPanel();
		bottomLeft.setHeading("常用交易列表(Top 10)");
		bottomLeft.setWidth("50%");
		bottomLeft.setLayout(new FillLayout());
		bottomLeft.add(GetBottomLeftPanelContent());
		bottomContainer.add(bottomLeft);
		
		LayoutContainer splitContainer2 = new LayoutContainer();
		splitContainer2.setBorders(false);
		splitContainer2.setWidth(4);
		bottomContainer.add(splitContainer2);
		
		ContentPanel bottomRight = new ContentPanel();
		bottomRight.setHeading("最新(发起/响应)案例(Top 10)");
		bottomRight.setLayout(new FitLayout());
		bottomRight.setWidth("49%");
		bottomRight.add(GetCaseCountGrid());
		bottomContainer.add(bottomRight);
		
		bodyPanel.add(topContainer);
		bodyPanel.add(bottomContainer);
		
		bodyPanel.setBottomComponent(GetToolBar());
		
		add(bodyPanel);
	}
	
	private void GetTopLeftPanelContent(GWTStatusSys sysInfo){
		StringBuffer sb = new StringBuffer();
		sb.append("<table border='0' width='100%'>"
				+"<tr><td colpan='3' class='tes-table-header'><img Style='vertical-align:middle' src='gxt/images/cus/apply.png' />&nbsp;&nbsp;<b>" + sysInfo.get(GWTStatusSys.N_IP).toString() +"</b></tr>"
				+"<tr><td rowspan='5' Style='width:24px'><img src='gxt/images/cus/tes1.png' /></td><td class='tes-table-name'>实例名：</td><td class='tes-table-value'>" + sysInfo.get(GWTStatusSys.N_SYSNAME).toString() +"</td></tr>"
				+"<tr><td class='tes-table-name'>ip地址：</td><td class='tes-table-value'>" + sysInfo.get(GWTStatusSys.N_IP) +"</td></tr>"
				+"<tr><td class='tes-table-name'>端口：</td><td class='tes-table-value'>" + sysInfo.get(GWTStatusSys.N_PORT) +"</td></tr>"
				+"<tr><td class='tes-table-name'>默认通道：</td><td class='tes-table-value'>" + sysInfo.get(GWTStatusSys.N_CHANNEL) +"</td></tr>"
				+"<tr><td class='tes-table-name'>监控刷新频率：</td><td id='tdTime' class='tes-table-value'></td></tr>"
				+"<tr><td class='tes-table-name'>发起方交易总数:</td><td></td><td class='tes-table-value'>" + sysInfo.get(GWTStatusSys.N_CLIENTTRANCOUNT).toString() +"</td></tr>"
				+"<tr><td class='tes-table-name'>接收方交易总数:</td><td></td><td class='tes-table-value'>" + sysInfo.get(GWTStatusSys.N_SERVERTRANCOUNT).toString() +"</td></tr>"
				+"<tr><td class='tes-table-name'>发起方案例总数：</td><td></td><td class='tes-table-value'>" + sysInfo.get(GWTStatusSys.N_CLIENTCASECOUNT).toString() +"</td></tr>"
				+"<tr><td class='tes-table-name'>接收方案例总数：</td><td></td><td class='tes-table-value'>" + sysInfo.get(GWTStatusSys.N_SERVERCASECOUNT).toString() +"</td></tr>"
				+"</table>");
		sysInfoPanelHTML.setHtml(sb.toString());
		
		if(!init){
			Slider slider = new Slider();
			slider.setWidth(60);
		    slider.setMinValue(0);
		    slider.setMaxValue(20);
		    slider.setIncrement(5);
		    slider.setValue(5);
		    slider.setMessage("每  {0} 秒");

		    SliderField sf = new SliderField(slider);
		    
		    sysInfoPanelHTML.add(sf, "#tdTime");
		    init = true;
		}
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
		column.setHeader("适配器名称");
	    column.setWidth(200);
	    columns.add(column);
	    
//	    column = new ColumnConfig();
//	    column.setId(GWTChannelInfo.N_IsClient);
//		column.setHeader("类型");
//	    column.setWidth(80);
//	    columns.add(column);
//	    
//	    column = new ColumnConfig();
//	    column.setId(GWTChannelInfo.N_ChannelType);
//		column.setHeader("通讯协议");
//	    column.setWidth(80);
//	    columns.add(column);
//	    
//	    column = new ColumnConfig();
//	    column.setId(GWTChannelInfo.N_TranCount);
//		column.setHeader("累计收/发次数");
//	    column.setWidth(80);
//	    column.setAlignment(HorizontalAlignment.CENTER);
//	    columns.add(column);
		
		ColumnModel cm = new ColumnModel(columns);
		
		channelstore = new ListStore<GWTChannelInfo>();
//		channelstore.add(new GWTChannelInfo("adapter1", "接收", "Tuxedo", 1, 362));
//		channelstore.add(new GWTChannelInfo("adapter2", "接收", "MQ", 1, 119));
//		channelstore.add(new GWTChannelInfo("adapter3", "发起", "HTTP", -1, 0));
		
		Grid<GWTChannelInfo> gird = new Grid<GWTChannelInfo>(channelstore, cm);
		gird.setAutoExpandColumn(GWTChannelInfo.N_ChannelName);
		gird.getView().setForceFit(true);
		
		return gird;
	}

	private Widget GetBottomLeftPanelContent(){
		
		tranGrid = GetTranCountGrid();
		
		TabPanel tab = new TabPanel();
		tab.setWidth(200);
		tab.setBorders(false);
		tab.setBodyBorder(false);
		tab.setTabPosition(TabPosition.BOTTOM);
		
		TabItem chartView = new TabItem("图表视图");
		chartView.add(GetTranChart());
		tab.add(chartView);
		
		TabItem dataView = new TabItem("数据视图");
		dataView.setLayout(new FillLayout());
		dataView.add(tranGrid);
		tab.add(dataView);
		
		return tab;
	}
	
	private Grid<GWTRealTimeLogInfo> GetTranCountGrid(){
		List<ColumnConfig> columns = new ArrayList<ColumnConfig>();
		
		ColumnConfig column = new ColumnConfig();
		column.setId(GWTRealTimeLogInfo.N_TRANCODE);
		column.setHeader("交易码");
	    column.setWidth(60);
	    columns.add(column);
	    
	    column = new ColumnConfig();
		column.setId(GWTRealTimeLogInfo.N_TRANNAME);
		column.setHeader("交易名称");
	    column.setWidth(140);
	    columns.add(column);
	    
	    column = new ColumnConfig();
	    column.setId(GWTRealTimeLogInfo.N_TYPE);
		column.setHeader("类型");
	    column.setWidth(80);
	    columns.add(column);
	    
	    column = new ColumnConfig();
	    column.setId(GWTRealTimeLogInfo.N_TRANCOUNT);
	    column.setAlignment(HorizontalAlignment.CENTER);
		column.setHeader("请求/响应次数");
	    column.setWidth(80);
	    columns.add(column);
	    
	    column = new ColumnConfig();
	    column.setId(GWTRealTimeLogInfo.N_PRECENT);
		column.setHeader("使用率");
	    column.setWidth(80);
	    column.setAlignment(HorizontalAlignment.CENTER);
	    columns.add(column);
		
		ColumnModel cm = new ColumnModel(columns);
		
		Grid<GWTRealTimeLogInfo> grid = new Grid<GWTRealTimeLogInfo>(tranStore, cm);
		grid.getView().setForceFit(true);
		
		return grid;
	}
	
	private Widget GetCaseCountGrid(){
		List<ColumnConfig> columns = new ArrayList<ColumnConfig>();
		
		ColumnConfig column = new ColumnConfig();
		column.setId(GWTRealTimeLogInfo.N_CASENAME);
		column.setHeader("案例名称");
	    column.setWidth(100);
	    columns.add(column);
	    
	    column = new ColumnConfig();
		column.setId(GWTRealTimeLogInfo.N_TRANCODE);
		column.setHeader("交易代码");
	    column.setWidth(120);
	    columns.add(column);
	    
	    column = new ColumnConfig();
	    column.setId(GWTRealTimeLogInfo.N_TYPE);
		column.setHeader("类型");
	    column.setWidth(80);
	    columns.add(column);
	    
	    column = new ColumnConfig();
	    column.setId(GWTRealTimeLogInfo.N_DATETIME);
		column.setHeader("发起/响应时间");
	    column.setWidth(120);
	    columns.add(column);
	    
		ColumnModel cm = new ColumnModel(columns);
		
		Grid<GWTRealTimeLogInfo> grid = new Grid<GWTRealTimeLogInfo>(caseStore, cm);
		grid.getView().setForceFit(true);
		
		return grid;
	}
	
	private Widget GetTranChart() {
		
		chart = new Chart(AppContext.getFlashUrl());
	    
		ChartModel cd = new ChartModel("",
				"font-size: 14px; font-family: Verdana;");
		cd.setBackgroundColour("#ffffff");
		
		Legend lg = new Legend(Position.RIGHT, true);
	    lg.setPadding(10);
	    cd.setLegend(lg);
	    
		pie = new PieChart();
		pie.setAlpha(0.5f);
		pie.setTooltip("#label# <br>count: #val# #percent#");
		pie.setAnimate(false);
		pie.setAlphaHighlight(true);
		pie.setGradientFill(true);
		pie.setColours("#ff0000", "#00aa00", "#0000ff", "#ff9900", "#ff00ff");

		if(tranGrid != null){
			List<GWTRealTimeLogInfo> tranList = tranGrid.getStore().getModels();
			//图形化仅显示前五条交易，否则交易过多容易引起视觉疲劳
			for(int i = 0; i < tranList.size() && i < 5; i++){
				GWTRealTimeLogInfo item = tranList.get(i);
				int tranCount = Integer.parseInt(item.get(GWTRealTimeLogInfo.N_TRANCOUNT).toString());
				String tranCode = item.get(GWTRealTimeLogInfo.N_TRANCODE);
				String tranName = item.get(GWTRealTimeLogInfo.N_TRANNAME);
				pie.addSlices(new PieChart.Slice(tranCount, tranCode, tranName));
			}
		}

		cd.addChartConfig(pie);
		chart.setChartModel(cd);
		
		return chart;
	}
	
	
	private Component GetToolBar(){
		
		ToolBar toolBar = new ToolBar();
		
		Button btnStartNewMoni = new Button("开始新监控");
		btnStartNewMoni.setIcon(MainPage.ICONS.StartMoni());
		btnStartNewMoni.addSelectionListener(new SelectionListener<ButtonEvent>(){

			@Override
			public void componentSelected(ButtonEvent ce) {
				MessageBox.confirm("提示", "该操作将清空之前的监控数据，是否继续?", new Listener<MessageBoxEvent>(){

					@Override
					public void handleEvent(MessageBoxEvent be) {
						if(be.getButtonClicked().getText().toLowerCase().equals("yes")){
							if(AppContext.getUseMockData())return;
							service.ClearMoniData(GetSystemName(), new AsyncCallback<Void>(){

								@Override
								public void onFailure(Throwable caught) {
								}

								@Override
								public void onSuccess(Void result) {
									tranStore.getLoader().load();
									caseStore.getLoader().load();
								}
							});
						}
					}
				} );
			}
		});
		toolBar.add(btnStartNewMoni);
		
		Button btnRefresh = new Button("刷新");
		btnRefresh.setIcon(MainPage.ICONS.Refresh());
		btnRefresh.addSelectionListener(new SelectionListener<ButtonEvent>(){

			@Override
			public void componentSelected(ButtonEvent ce) {
				clickRefresh = true;
				tranStore.getLoader().load();
				caseStore.getLoader().load();
			}
			
		});
		toolBar.add(btnRefresh);
		
		return toolBar;
	}
	
	private void InitService() {
		service = ServiceHelper.GetDynamicService(this.SERVLETNAME, ISimuStatus.class);
		moniService = ServiceHelper.GetDynamicService("moniserver", IMonitorService.class);
	}
	
	private void PrepareData(){
		
		if(AppContext.getUseMockData()){
			caseStore = new ListStore<GWTRealTimeLogInfo>();
			caseStore.add(GWTRealTimeLogInfo.getRecentCaseMockData());
		}else{
			RpcProxy<List<GWTRealTimeLogInfo>> dataProxy = new RpcProxy<List<GWTRealTimeLogInfo>>(){

				@Override
				protected void load(Object loadConfig,
						AsyncCallback<List<GWTRealTimeLogInfo>> callback) {
					service.GetRecentCaseList(GetSystemName(), callback);
				}
				
			};
			
			ListLoader<ListLoadResult<GWTRealTimeLogInfo>> loader = new BaseListLoader<ListLoadResult<GWTRealTimeLogInfo>>(dataProxy);
			caseStore = new ListStore<GWTRealTimeLogInfo>(loader);
			caseStore.getLoader().load();
		}
		
		//使用写死数据用作暂时
		if(AppContext.getUseMockData()){
			tranStore = new ListStore<GWTRealTimeLogInfo>();
			tranStore.add(GWTRealTimeLogInfo.getUsefulTranMockData());
			
		}else{//从后台读取真实数据
			RpcProxy<List<GWTRealTimeLogInfo>> dataProxy = new RpcProxy<List<GWTRealTimeLogInfo>>(){

				@Override
				protected void load(Object loadConfig,
						AsyncCallback<List<GWTRealTimeLogInfo>> callback) {
					service.GetUsefulTranList(GetSystemName(), callback);
				}
			};
			
			ListLoader<ListLoadResult<GWTRealTimeLogInfo>> loader = new BaseListLoader<ListLoadResult<GWTRealTimeLogInfo>>(dataProxy);
			loader.addLoadListener(new LoadListener(){
				@SuppressWarnings("unchecked")
				@Override
				public void loaderLoad(LoadEvent le) {
					if(clickRefresh){
						clickRefresh = false;
						return;
					}
					//super.loaderLoad(le);
					if(tranGrid != null && pie != null){
						
						pie.getValues().clear();
						
						List<GWTRealTimeLogInfo> tranList = (List<GWTRealTimeLogInfo>)le.getData();
						//图形化仅显示前五条交易，否则交易过多容易引起视觉疲劳
						for(int i = 0; i < tranList.size() && i < 5; i++){
							GWTRealTimeLogInfo item = tranList.get(i);
							int tranCount = Integer.parseInt(item.get(GWTRealTimeLogInfo.N_TRANCOUNT).toString());
							String tranCode = item.get(GWTRealTimeLogInfo.N_TRANCODE);
							String tranName = item.get(GWTRealTimeLogInfo.N_TRANNAME);
							pie.addSlices(new PieChart.Slice(tranCount, tranCode, tranName));
						}
					}
					
					chart.recalculate();
					chart.refresh();
				}
			});
			tranStore = new ListStore<GWTRealTimeLogInfo>(loader);
			tranStore.getLoader().load();
		}
		
		if(AppContext.getUseMockData()){
			GetTopLeftPanelContent(GWTStatusSys.GetMockData());
		}else{
			service.GetSystemStatusInfo(GetSystemID(), new AsyncCallback<GWTStatusSys>(){

				@Override
				public void onFailure(Throwable caught) {
					GetTopLeftPanelContent(GWTStatusSys.GetMockData());
				}

				@Override
				public void onSuccess(GWTStatusSys result) {
					GetTopLeftPanelContent(result);
				}
			});
		}
	}
	
	private void GetChannelInfo(){
		moniService.GetChannelList(this.GetSystemName(), new AsyncCallback<List<GWTChannelInfo>>(){

			@Override
			public void onFailure(Throwable caught) {
			}

			@Override
			public void onSuccess(List<GWTChannelInfo> result) {
				if(channelstore == null)return;
				channelstore.removeAll();
				if(result == null)return;
				channelstore.add(result);
			}
			
		});
	}
}
