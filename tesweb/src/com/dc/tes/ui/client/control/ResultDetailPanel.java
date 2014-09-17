package com.dc.tes.ui.client.control;

import java.util.List;

import com.dc.tes.ui.client.IResultService;
import com.dc.tes.ui.client.IResultServiceAsync;
import com.dc.tes.ui.client.common.ServiceHelper;
import com.dc.tes.ui.client.model.GWTResultLog;
import com.dc.tes.ui.client.model.GWTResultLogMsg;
import com.dc.tes.ui.client.model.GWTStock;
import com.dc.tes.ui.client.page.StatisticBase;
import com.extjs.gxt.charts.client.Chart;
import com.extjs.gxt.charts.client.model.ChartModel;
import com.extjs.gxt.charts.client.model.PieDataProvider;
import com.extjs.gxt.charts.client.model.charts.PieChart;
import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.core.XTemplate;
import com.extjs.gxt.ui.client.data.ChangeEvent;
import com.extjs.gxt.ui.client.data.ChangeListener;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.util.Util;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;

public class ResultDetailPanel extends ContentPanel implements ICascadePanel {
	protected final String url = "gxt/chart/open-flash-chart.swf";
	private GWTResultLog record = null;
	private GWTResultLogMsg resultLogMsg = null;
	private Chart chartPie;	
	private ListStore<GWTStock> store;
	IResultServiceAsync resultService = ServiceHelper.GetDynamicService("result", IResultService.class);
	@Override
	public void showCascadePanel(ModelData modelData) {
		// TODO Auto-generated method stub
		if(modelData == null || ((GWTResultLog)modelData).getExecuteSetName().isEmpty()){ //执行集名称不为空时才显示
			this.setVisible(false);
		}else{
			this.setVisible(true);
			this.record = (GWTResultLog)modelData;		
			loadData();			
		}
	}
	
	private void loadData() {
		// TODO Auto-generated method stub
		resultService.GetResultLogMsg(record, new AsyncCallback<GWTResultLogMsg>() {
			
			@Override
			public void onSuccess(GWTResultLogMsg result) {
				// TODO Auto-generated method stub
				resultLogMsg.SetCaseCount(result.GetCaseCount());
				resultLogMsg.SetPassCaseCount(result.GetPassCaseCount());
				resultLogMsg.SetFailedCaseCount(result.GetFailedCaseCount());
				resultLogMsg.SetTimeOutCaseCount(result.GetTimeOutCaseCount());
				resultLogMsg.SetOtherCaseCount(result.GetOtherCaseCount());
				resultLogMsg.SetPassRate(result.GetPassRate());
				store.removeAll();
				store.add(new GWTStock("执行失败案例", resultLogMsg.GetFailedCaseCount().toString()));
				store.add(new GWTStock("执行通过案例", resultLogMsg.GetPassCaseCount().toString()));				
				store.add(new GWTStock("执行超时案例", resultLogMsg.GetTimeOutCaseCount().toString()));
				store.add(new GWTStock("其它状态案例", resultLogMsg.GetOtherCaseCount().toString()));
				store.commitChanges();
			}
			
			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub
				caught.printStackTrace();
				MessageBox.alert("错误提示", "获取执行日志信息失败！", null);
			}
		});
	}
	

	public ResultDetailPanel(){
		this.setHeaderVisible(false);
		this.setBorders(false);
		this.setBodyBorder(false);
		this.setWidth("100%");
		this.setScrollMode(Scroll.AUTOY);	
		BorderLayout layout = new BorderLayout();
		this.setLayout(layout);
		resultLogMsg = new GWTResultLogMsg(0, 0, 0, 0, 0, 0, "");
		BorderLayoutData westData = new BorderLayoutData(LayoutRegion.WEST, 350);
		westData.setFloatable(false);
		this.add(drawMsgTable(), westData);
		
		BorderLayoutData centerData = new BorderLayoutData(LayoutRegion.CENTER);
		this.add(drawChartPanel(), centerData);
		this.hide();
	}

	private Widget drawChartPanel() {
		// TODO Auto-generated method stub
		ContentPanel piePanel = new ContentPanel();
		piePanel.setHeaderVisible(false);
		piePanel.setBorders(false);
		piePanel.setBodyBorder(false);
		piePanel.setWidth("50%");
		
		PieChart pieChart = new PieChart();
		pieChart.setAlpha(0.5f);
		pieChart.setNoLabels(false);
		pieChart.setTooltip("#label#<br>案例数：#val#<br>比例：#percent#");
		ChartModel pieModel = new ChartModel("执行结果比例",
				"font-size: 14px; font-family: Verdana; text-align: center;");
		pieModel.setBackgroundColour("#ffffff");
		PieDataProvider dataProvider = new PieDataProvider(GWTStock.N_Pos, GWTStock.N_Name, GWTStock.N_Name);
		store = new ListStore<GWTStock>();
		dataProvider.bind(store);

		pieChart.setDataProvider(dataProvider);

		List<String> colorList = new StatisticBase().GeneColorList(4);
		pieChart.setColours(colorList);
		pieModel.addChartConfig(pieChart);
		
		chartPie = new Chart(url);
		chartPie.setBorders(false);
		chartPie.setChartModel(pieModel);
		piePanel.add(chartPie);
		
//		ChartListener listener = new ChartListener() {
//			public void chartClick(ChartEvent ce) {
//				String label = ce.getDataType().get("label").toString();
//				BasePage page = new ResultDetailPage(record.getID(),
//						false, labelToInt(label));
//				String tabId = "queueTask" + record.getID();
//				String tabTitle = "["
//						+ record.getExecuteSetName() + "]" + "执行结果";
//				AppContext.GetEntryPoint().AddTabItem(tabId, tabTitle, page);
//				
//			}
//
//			private Integer labelToInt(String label) {
//				// TODO Auto-generated method stub
//				if(label.equals("执行失败案例")){
//					return 0;
//				}else if(label.equals("执行通过案例")){
//					return 1;
//				}else if(label.equals("执行超时案例")){
//					return 5;
//				}else{
//					return 10;
//				}
//			}
//		};
//		pieChart.addChartListener(listener);
		return piePanel;
	}

	private Widget drawMsgTable() {
		// TODO Auto-generated method stub
		ContentPanel cp = new ContentPanel();
		cp.setWidth("40%");
		cp.setHeaderVisible(false);
		cp.setBodyBorder(false);
		cp.setBorders(false);
		cp.setScrollMode(Scroll.AUTO);
		cp.setFrame(false);
		

		String tableHead = "<table width=\"95%\" style=\"border:1px solid #cad9ea;color:#666; table-layout:fixed;"
				+ "empty-cells:show; border-collapse: collapse; margin:5 5 auto;font-size:14px;\">";
		String tdLabel = "<td width=\"40%\" style=\"font-size:14px; border:1px solid #cad9ea;padding:0 1em 0;"
			    + "background-color:#f5fafe; text-align:right;min-height:30px;\">";
		String tdContent = "<td width=\"60%\" style=\"font-size:14px; border:1px solid #cad9ea;padding:0 1em 0; min-height:30px;\">";
		String tdEnd = "</td>";
		String tableEnd = "</table>";

		StringBuffer htmlStr = new StringBuffer();
		htmlStr.append(tableHead);
		htmlStr.append("<tr>" + tdLabel + "执行案例数" + tdEnd);
		htmlStr.append(tdContent + "{"+GWTResultLogMsg.N_CaseCount+"}"+ tdEnd);
		htmlStr.append("<tr>" + tdLabel + "通过案例数" + tdEnd);
		htmlStr.append(tdContent + "{"+GWTResultLogMsg.N_PassCaseCount+"}" + tdEnd + "</tr>");
		htmlStr.append("<tr>" + tdLabel + "失败案例数" + tdEnd);
		htmlStr.append(tdContent + "{"+GWTResultLogMsg.N_FailedCaseCount+"}" + tdEnd + "</tr>");
		htmlStr.append("<tr>" + tdLabel + "超时案例数" + tdEnd);
		htmlStr.append(tdContent + "{"+GWTResultLogMsg.N_TimeOutCaseCount+"}" + tdEnd + "</tr>");
		htmlStr.append("<tr>" + tdLabel + "其它状态案例数" + tdEnd);
		htmlStr.append(tdContent + "{"+GWTResultLogMsg.N_OtherCaseCount+"}" + tdEnd + "</tr>");
		htmlStr.append("<tr>" + tdLabel + "通过率" + tdEnd);
		htmlStr.append(tdContent + "{"+GWTResultLogMsg.N_PassRate +"}" + tdEnd + "</tr>");
		
		htmlStr.append(tableEnd);
		
		final XTemplate template = XTemplate.create(htmlStr.toString());  
	    final HTML html = new HTML();    
	    
		     
		template.overwrite(html.getElement(), Util.getJsObject(resultLogMsg));  
		resultLogMsg.addChangeListener(new ChangeListener() {  
		      public void modelChanged(ChangeEvent event) {  
		        template.overwrite(html.getElement(), Util.getJsObject(resultLogMsg));  
		      }  
		    });

		cp.add(html);	
		return cp;
	}
}
