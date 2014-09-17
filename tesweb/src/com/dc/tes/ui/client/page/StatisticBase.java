package com.dc.tes.ui.client.page;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.dc.tes.ui.client.model.GWTStatTrend;
import com.extjs.gxt.charts.client.model.axis.XAxis;
import com.extjs.gxt.charts.client.model.axis.YAxis;

public class StatisticBase extends BasePage {
	protected final String url = "gxt/chart/open-flash-chart.swf";
	protected int begin = 0;
	protected int end = 0;
	
	@SuppressWarnings("deprecation")
	public StatisticBase()
	{
		int year = new Date().getYear()+1900;
		int month = new Date().getMonth() + 1;
		end =  year * 100 + month;
		if(month == 12)
		{
			begin = year * 100 + 1;
		}
		else
		{
			begin = (year - 1) * 100 + (month+1);
		}
	}
	
	public List<String> GeneColorList(int colorCount)
	{
		List<String> colorList = new ArrayList<String>();
		
		colorList.add("#0f4ca0");
		colorList.add("#ff7800");
		colorList.add("#f9e509");
		colorList.add("#585bee");
		colorList.add("#64f09f");
		colorList.add("#d6f3ef");
		
		colorList.add("#FF0000");
		colorList.add("#00aa00");
		colorList.add("#FF00AA");
		colorList.add("#FF8800");
		colorList.add("#FF00FF");
		colorList.add("#DDEEFF");

		for(int i = 12;i <= colorCount; i++)
		{
			colorList.add("#FFFFFF");
		}
		
		return colorList;
	}
	
	protected YAxis getYAxis(int min,int max)
	{
		if(max == 0)
			max = 10;
		
		YAxis ya = new YAxis();
		max = max / 10 * 10 + 10;
		min = min / 10 * 10;
		ya.setMax(max);
		ya.setMin(min);
		ya.setSteps((max - min)/10);
		
		return ya;
	}
	
	protected XAxis GetXAxis(List<Integer> yearM)
	{
		if(yearM.size() == 0)
			return GetXAxis();
		
		XAxis xa = new XAxis();
		for(Integer month : yearM)
		{
			xa.addLabels(month%100 + "月");
		}
		return xa;
	}
	
	protected XAxis GetXAxis()
	{
		return GetXAxis(GWTStatTrend.getTrendYearM(begin, end));
	}
	
	
	protected List<Number> GetTranTrendMock()
	{
		List<Number> trendList = new ArrayList<Number>();
		
		trendList.add(23);
		trendList.add(231);
		trendList.add(54);
		trendList.add(56);
		trendList.add(78);
		trendList.add(84);
		trendList.add(111);
		trendList.add(78);
		trendList.add(23);
		trendList.add(13);
		trendList.add(56);
		trendList.add(90);
		
		return trendList;
	}
}
