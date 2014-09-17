package com.dc.tes.ui.client.page;

import java.util.ArrayList;
import java.util.List;

import com.dc.tes.ui.client.AppContext;
import com.dc.tes.ui.client.IStatics;
import com.dc.tes.ui.client.IStaticsAsync;
import com.dc.tes.ui.client.MainPage;
import com.dc.tes.ui.client.common.ServiceHelper;
import com.dc.tes.ui.client.model.GWTStatSys;
import com.extjs.gxt.charts.client.Chart;
import com.extjs.gxt.charts.client.event.ChartEvent;
import com.extjs.gxt.charts.client.event.ChartListener;
import com.extjs.gxt.charts.client.model.ChartModel;
import com.extjs.gxt.charts.client.model.charts.LineChart;
import com.extjs.gxt.charts.client.model.charts.PieChart;
import com.extjs.gxt.ui.client.Style.SortDir;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.util.Padding;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.HeaderGroupConfig;
import com.extjs.gxt.ui.client.widget.layout.FillLayout;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayout;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayout.HBoxLayoutAlign;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Widget;

/**
 * 系统级统计报表
 * @author scckobe
 *
 */
public class StatisticSys extends StatisticBase {
	private Chart chartTrend;
	private Grid<GWTStatSys> grid;
	private Chart chartPie;
	IStaticsAsync staticService = null;

	public StatisticSys() {
		staticService = ServiceHelper.GetDynamicService("statistic", IStatics.class);
	}

	@Override
	protected void onRender(Element parent, int index) {
		super.onRender(parent, index);
		this.setLayout(new FillLayout());
		InitPage(new ArrayList<GWTStatSys>());
		LoadSysValue();
//		if (AppContext.getUseMockData()) {
//			InitPage(GWTStatSys.getMock());
//		} else {
//			
//		}

	}

	private void LoadSysValue() {
		if (AppContext.getUseMockData())
			Reconfig(GWTStatSys.getMock());
		else
		{
			staticService.GetSysStatistic(begin, end,
					new AsyncCallback<List<GWTStatSys>>() {
						@Override
						public void onFailure(Throwable caught) {
						}
	
						@Override
						public void onSuccess(List<GWTStatSys> result) {
							Reconfig(result);
						}
					});
		}
	}

	private String FormatInfo(String info) {
		return begin / 100 + "年 " + begin % 100 + "月 至 " + end / 100 + "年"
				+ end % 100 + "月 " + info;
	}

	private void Reconfig(List<GWTStatSys> sysList) {
		ListStore<GWTStatSys> store = grid.getStore();
		store.removeAll();
		store.add(sysList);
		grid.reconfigure(store, grid.getColumnModel());

		List<String> colorList = GeneColorList(sysList.size());

		//各个系统月份走势图模型
		ChartModel trendModel = new ChartModel(FormatInfo("各系统使用情况"),
				"font-size: 14px; font-family: Verdana;");
		trendModel.setBackgroundColour("#ffffff");

		//饼图图表
		PieChart pieChart = new PieChart();
		pieChart.setAlpha(0.5f);
		pieChart.setNoLabels(false);
		pieChart.setTooltip("系统名称：#label#<br>执行次数：#val#<br>执行比例：#percent#");

		//各个系统发起接受次数比例
		ChartModel pieModel = new ChartModel(FormatInfo("各系统使用频率"),
				"font-size: 14px; font-family: Verdana; text-align: center;");
		pieModel.setBackgroundColour("#ffffff");

		int i = 0, maxValue = 10, minValue = 0;
		boolean haveZero = false;
		for (GWTStatSys statSys : sysList) {
			LineChart line = new LineChart();
//			line.setKeys(new Keys("12","3月",14));
//			line.setTooltip("系统名称：#label#");
			line.setColour(colorList.get(i++));
			int count = 0;
			for (Number value : statSys.getListCount()) {
				maxValue = Math.max(maxValue, value.intValue());
				minValue = Math.min(minValue, value.intValue());
				line.addValues(value);
				count += value.intValue();
			}
			
			if(count == 0)
			{
				
				if(haveZero)
				{
					i--;
					continue;
				}
				
				pieChart.addSlices(new PieChart.Slice(0, "其他系统",
						"其他系统"));
				haveZero = true;
			}
			else
			{
				pieChart.addSlices(new PieChart.Slice(count, statSys.getSysName(),
						statSys.getSysName()));
				if (i == 0)
					line.setText("执行次数");
				trendModel.addChartConfig(line);
			}
		}

		//设置走势图的X轴数据排列,Y轴数据排列
		trendModel.setXAxis(GetXAxis());
		trendModel.setYAxis(getYAxis(minValue, maxValue));

		//为饼图添加事件
		ChartListener listener = new ChartListener() {
			public void chartClick(ChartEvent ce) {
				OpenDetail(ce.getDataType().get("label").toString());
			}
		};
		pieChart.addChartListener(listener);
		pieChart.setColours(colorList);
		pieModel.addChartConfig(pieChart);

		chartTrend.setChartModel(trendModel);
		chartPie.setChartModel(pieModel);
	}

	private void InitPage(List<GWTStatSys> sysList) {
		//走势图图标
		chartTrend = new Chart(url);
		chartTrend.setBorders(false);

		//饼图图表
		chartPie = new Chart(url);
		chartPie.setBorders(false);

		//列表
		ListStore<GWTStatSys> store = new ListStore<GWTStatSys>();
		store.setSortField(GWTStatSys.N_SysName);
		store.setSortDir(SortDir.DESC);
		store.add(sysList);

		grid = new Grid<GWTStatSys>(store, GetColumnModel());
		grid.setBorders(false);
		grid.setAutoExpandColumn(GWTStatSys.N_SysName);
		grid.getView().setForceFit(true);
		grid.setHeight("100%");

		Reconfig(sysList);

		ContentPanel trendPanel = new ContentPanel();
		trendPanel.setHeaderVisible(false);
		trendPanel.setBorders(false);
		trendPanel.setWidth("50%");
		trendPanel.add(chartTrend);

		ContentPanel piePanel = new ContentPanel();
		piePanel.setHeaderVisible(false);
		piePanel.setBorders(false);
		piePanel.setWidth("50%");
		piePanel.add(chartPie);

		ContentPanel gridPanel = new ContentPanel();
		gridPanel.setHeaderVisible(false);
		gridPanel.setBorders(false);
		gridPanel.setWidth("100%");
		gridPanel.setLayout(new FillLayout());
		gridPanel.add(grid);

		InitLayOut(trendPanel, piePanel, gridPanel);
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

		bodyPanel.setBottomComponent(GetToolBar());

		add(bodyPanel);
	}

	private Component GetToolBar() {

		ToolBar toolBar = new ToolBar();

		Button btnStartNewMoni = new Button("开始统计");
		btnStartNewMoni.setIcon(MainPage.ICONS.StartMoni());
		btnStartNewMoni.addSelectionListener(btnListener());
		toolBar.add(btnStartNewMoni);

		Button btnRefresh = new Button("刷新", btnListener());
		btnRefresh.setIcon(MainPage.ICONS.Refresh());
		toolBar.add(btnRefresh);

		return toolBar;
	}

	private SelectionListener<ButtonEvent> btnListener() {
		return new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				LoadSysValue();
			}
		};
	}

	private void OpenDetail(String tabName) {
		AppContext.GetEntryPoint().AddTabItem("StatTran" + tabName,
				"交易统计[" + tabName + "]", new StatisticTran(tabName));
	}

	private ColumnModel GetColumnModel() {
		List<ColumnConfig> configs = new ArrayList<ColumnConfig>();

		configs.add(getConfig(GWTStatSys.N_SysName, "模拟系统名称"));
		configs.add(getConfig(GWTStatSys.N_TranSend, "发起端"));
		configs.add(getConfig(GWTStatSys.N_TranRecv, "接收端"));
		configs.add(getConfig(GWTStatSys.N_TranCount, "总数"));
		configs.add(getConfig(GWTStatSys.N_SendCount, "发起次数"));
		configs.add(getConfig(GWTStatSys.N_RecvCount, "响应次数"));
		configs.add(getConfig(GWTStatSys.N_RunCount, "总数"));

		ColumnModel cm = new ColumnModel(configs);
		cm.addHeaderGroup(0, 1, new HeaderGroupConfig("已配交易个数", 1, 3));
		cm.addHeaderGroup(0, 4, new HeaderGroupConfig("使用次数", 1, 3));
		return cm;
	}

	private ColumnConfig getConfig(String id, String name) {
		ColumnConfig column = new ColumnConfig();
		column.setId(id);
		column.setHeader(name);
		column.setWidth(100);
		column.setMenuDisabled(true);
		return column;
	}
}
