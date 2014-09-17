package com.dc.tes.ui.client.model;

import java.util.List;
import java.util.ArrayList;

import com.extjs.gxt.ui.client.data.BaseModel;
import com.google.gwt.user.client.Random;

/**
 * 系统或者交易走势图实体模型
 * @author scckobe
 *
 */
public class GWTStatTrend  extends BaseModel {
	private static final long serialVersionUID = -4440226587777250869L;
	public static final String N_YearM = "YearM";
	public static final String N_Value = "Value";
	public GWTStatTrend()
	{
		this(0,0);
	}
	
	public GWTStatTrend(int yearM,int value)
	{
		set(N_YearM,yearM);
		SetValue(value);
	}
	
	public void SetValue(int value)
	{
		set(N_Value, value);
	}

	public int getYearM()
	{
		return Integer.valueOf(get(N_YearM).toString());
	}
	
	public int getValue()
	{
		return Integer.valueOf(get(N_Value).toString());
	}
	
	public static List<GWTStatTrend> getTrendList(int begin,int end)
	{
		List<GWTStatTrend> trendList = new ArrayList<GWTStatTrend>();
		
		//获得趋势列表，值先默认为0
		List<Integer> countList = GWTStatTrend.getTrendYearM(begin, end);
		for(Integer intValue : countList)
		{
			trendList.add(new GWTStatTrend(intValue,0));
		}
		
		return trendList;
	}
	
	public static List<Integer> getTrendYearM(int begin,int end)
	{
		List<Integer> trendList = new ArrayList<Integer>();
		for(int i = begin; i <= end ; i++)
		{
			int year = i/100;
			int month = i%100;
			if(month == 0)
				continue;
			else if(month > 12)
			{
				month = year * 100;
				continue;
			}
			trendList.add(year % 100 * 100 + month);
		}
		return trendList;
	}
	
	public static List<GWTStatTrend> getMock(int count,int begin,int end)
	{
		List<GWTStatTrend> trendList = getTrendList(begin,end);
		int trendSize = trendList.size();
		int aveValue = count/trendSize;
		
		for(GWTStatTrend trend : trendList)
		{
			int value = Math.abs(Random.nextInt()%aveValue);
			trend.SetValue(value);
			count -= value;
		}
		
		trendList.get(Math.abs(Random.nextInt())%12).SetValue(count);
		return trendList;
	}
}
