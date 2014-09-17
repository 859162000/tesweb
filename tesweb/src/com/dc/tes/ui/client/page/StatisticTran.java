package com.dc.tes.ui.client.page;

import java.util.ArrayList;
import java.util.List;

import com.dc.tes.ui.client.AppContext;
import com.dc.tes.ui.client.IStatics;
import com.dc.tes.ui.client.IStaticsAsync;
import com.dc.tes.ui.client.common.CookieManage;
import com.dc.tes.ui.client.common.ServiceHelper;
import com.dc.tes.ui.client.model.GWTStatTran;
import com.dc.tes.ui.client.model.GWTStatTrend;
import com.extjs.gxt.charts.client.Chart;
import com.extjs.gxt.charts.client.model.ChartModel;
import com.extjs.gxt.charts.client.model.axis.XAxis;
import com.extjs.gxt.charts.client.model.charts.BarChart;
import com.extjs.gxt.charts.client.model.charts.LineChart;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.util.Padding;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.layout.FillLayout;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayout;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayout.HBoxLayoutAlign;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Widget;

public class StatisticTran extends StatisticBase {
	private String sysName;
	private Grid<GWTStatTran> grid;
	private Chart chartBar;
	private Chart chartTrend;
	IStaticsAsync staticService = null;
	
	public StatisticTran()
	{	
		this(CookieManage.GetSimuSystemName());
	}
	
	public StatisticTran(String sysName)
	{	
		staticService = ServiceHelper.GetDynamicService("statistic", IStatics.class);
		this.sysName = sysName;
	}
	
	@Override
	protected void onRender(Element parent, int index) {
		super.onRender(parent, index);
		this.setLayout(new FillLayout());
		InitPage(new ArrayList<GWTStatTran>());
		LoadTranValue();
	}
	
	private void LoadTranValue()
	{
		if(AppContext.getUseMockData())
			Reconfig(GWTStatTran.getMockData());
		else
		{
			staticService.GetTranStatistic(sysName,0, 0, new AsyncCallback<List<GWTStatTran>>()
					{
						@Override
						public void onFailure(Throwable caught) {
						}

						@Override
						public void onSuccess(List<GWTStatTran> result) {
							Reconfig(result);
						}
					});
		}
	}
	
	private void LoadTrendInfo(final String tranCode,String tranName,int type,int runCount)
	{
		if(AppContext.getUseMockData())
			ConstructTrendChar(tranCode,GWTStatTrend.getMock(runCount, begin,end));
		else
		{
			staticService.GetTrendStatistic(sysName, tranCode, tranName, 
					type, begin, end, 
					new AsyncCallback<List<GWTStatTrend>>()
					{
					@Override
					public void onFailure(Throwable caught) {
					}
	
					@Override
					public void onSuccess(List<GWTStatTrend> result) {
						ConstructTrendChar(tranCode,result);
					}
			});
		}
	}
	
	private void Reconfig(List<GWTStatTran> tranList)
	{
		ListStore<GWTStatTran> store = grid.getStore();
		store.add(tranList);
		grid.reconfigure(store, grid.getColumnModel());
		if(tranList.size() > 0)
		{
			List<GWTStatTran> tran = new ArrayList<GWTStatTran>();
			tran.add(tranList.get(0));
			grid.getSelectionModel().setSelection(tran);
		}
		chartBar.setChartModel(getBarModel(tranList));
	}
	
	private ChartModel getBarModel(List<GWTStatTran> tranList)
	{
		//前10交易的柱状图
		ChartModel barModel = new ChartModel("常用交易Top10",
				"font-size: 14px; font-family: Verdana;");
		barModel.setBackgroundColour("#fefefe"); 
//		barModel.setYAxisLabelStyle(100, "ebebeb");

		//前10交易的柱状图表
		BarChart bar = new BarChart();  
//		bar.set
	    bar.setColour("#84a7da"); 
	    
	    List<String> codeList = new ArrayList<String>();
	    int min = 0, max = 0;
	    for(GWTStatTran tran : tranList)
	    {
	    	int value = tran.getCount();
			min = Math.min(min, value);
			max = Math.max(max, value);
	    	codeList.add(tran.getTranCode());
	    	bar.addValues(value);
	    }
	    for(int i = 11; i > tranList.size(); i--)
	    {
	    	codeList.add("");
	    	bar.addValues(0);
	    }
	    barModel.addChartConfig(bar);
	    
	    XAxis xa = new XAxis();
		xa.setLabels(codeList);
	    barModel.setXAxis(xa);
	    barModel.setYAxis(getYAxis(min, max));
	    
	    return barModel;
	}
	
	private void InitPage(List<GWTStatTran> tranList)
	{
		ListStore<GWTStatTran> store = new ListStore<GWTStatTran>();
		store.add(tranList);
		
		chartBar = new Chart(url);
		chartBar.setBorders(false);
		chartBar.setChartModel(getBarModel(tranList));

		ContentPanel barPanel = new ContentPanel();
		barPanel.setHeaderVisible(false);
		barPanel.setBorders(false);
		barPanel.setWidth("50%");
		barPanel.add(chartBar);
		
		//某一交易执行历史
		final ContentPanel trendPanel = new ContentPanel();
		trendPanel.setBorders(false);
		trendPanel.setHeaderVisible(false);
		trendPanel.setWidth("50%");
		chartTrend = new Chart(url);
		trendPanel.add(ConstructTrendChar("",new ArrayList<GWTStatTrend>()));
		
		grid = new Grid<GWTStatTran>(store, GetColumnModel());   
	    grid.setBorders(false);
	    grid.getView().setForceFit(true);
	    grid.getSelectionModel().addListener(
				Events.SelectionChange,
				new Listener<SelectionChangedEvent<GWTStatTran>>() {
					public void handleEvent(
							SelectionChangedEvent<GWTStatTran> be) {
						GWTStatTran tran = be.getSelectedItem();
						LoadTrendInfo(tran.getTranCode(), tran.getTranName(), 
								tran.getType(),tran.getCount());
					}
				});
	    ContentPanel gridPanel = new ContentPanel();
	    gridPanel.setHeading("常用交易Top10");
	    gridPanel.setLayout(new FillLayout());
	    gridPanel.add(grid);
		
		InitLayOut(barPanel,trendPanel,gridPanel);
	}
	
	private Chart ConstructTrendChar(String tranCode,List<GWTStatTrend> trendList)
	{
		//各个系统月份走势图模型
		ChartModel cmTrend = new ChartModel("交易执行历史",
				"font-size: 14px; font-family: Verdana;");
		cmTrend.setBackgroundColour("#ffffff");
		LineChart line = new LineChart();
		line.setText("执行次数");
		line.setColour("#84a7da");
		
		int min = 0, max = 0;
		for(GWTStatTrend trend : trendList)
		{
			int value = trend.getValue();
			min = Math.min(min, value);
			max = Math.max(max, value);
			line.addValues(value);
		}
		cmTrend.addChartConfig(line);
		//设置走势图的X轴数据排列
		cmTrend.setXAxis(GetXAxis());
		//设置走势图的Y轴数据排列
		cmTrend.setYAxis(getYAxis(min, max));
		
		chartTrend.setChartModel(cmTrend);

		return chartTrend;
	}
	
	private void InitLayOut(Widget left, Widget right, Widget single) {
		HBoxLayout hlayout = new HBoxLayout();
		hlayout.setHBoxLayoutAlign(HBoxLayoutAlign.STRETCH);
		hlayout.setPadding(new Padding(4));
		HBoxLayout hlayout1 = new HBoxLayout();
		hlayout1.setHBoxLayoutAlign(HBoxLayoutAlign.STRETCH);
		hlayout1.setPadding(new Padding(0, 4, 4, 4));

		LayoutContainer topContainer = new LayoutContainer();
		topContainer.setHeight("50%");
		topContainer.setBorders(false);
		topContainer.setLayout(hlayout);

		LayoutContainer bottomContainer = new LayoutContainer();
		bottomContainer.setHeight("50%");
		bottomContainer.setBorders(false);
		bottomContainer.setLayout(hlayout1);

		LayoutContainer splitContainer1 = new LayoutContainer();
		splitContainer1.setBorders(false);
		splitContainer1.setWidth(4);
		topContainer.add(splitContainer1);
		
		topContainer.add(single);
		bottomContainer.add(left);
		bottomContainer.add(splitContainer1);
		bottomContainer.add(right);

		ContentPanel bodyPanel = new ContentPanel();
		bodyPanel.setHeaderVisible(false);
		bodyPanel.setBorders(false);
		bodyPanel.setBodyBorder(false);
		bodyPanel.setLayout(new FillLayout());

		bodyPanel.add(topContainer);
		bodyPanel.add(bottomContainer);

//		bodyPanel.setBottomComponent(GetToolBar());

		add(bodyPanel);
	}
	
	private ColumnConfig getConfig(String id,String name)
	{
		ColumnConfig column = new ColumnConfig();
	    column.setId(id);
	    column.setHeader(name);
	    column.setWidth(100);
	    column.setMenuDisabled(true);
	    return column;
	}
	
	private ColumnModel GetColumnModel()
	{
		List<ColumnConfig> configs = new ArrayList<ColumnConfig>(); 
	    
	    configs.add(getConfig(GWTStatTran.N_TranCode,"交易代码"));   
	    configs.add(getConfig(GWTStatTran.N_TranName, "交易名称"));  
	    configs.add(getConfig(GWTStatTran.N_TypeCHS, "交易类型"));  
	    configs.add(getConfig(GWTStatTran.N_RunCount, "执行总数(次)"));  
	    configs.add(getConfig(GWTStatTran.N_DyRate, "动态数据比例(%)"));  
	  
	    ColumnModel cm = new ColumnModel(configs);
	    return cm;
	}
}