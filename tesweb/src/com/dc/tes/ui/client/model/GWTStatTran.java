package com.dc.tes.ui.client.model;

import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.ui.client.data.BaseModel;
/**
 * 交易级报表统计
 * @author scckcobe
 *
 */
public class GWTStatTran extends BaseModel {
	private static final long serialVersionUID = 3268426759525094841L;
	/**
	 * 交易码
	 */
	public final static String N_TranCode = "TranCode";
	/**
	 * 交易名称
	 */
	public final static String N_TranName = "TranName";
	/**
	 * 交易类型（数字）
	 */
	public final static String N_Type = "Type";
	/**
	 * 交易类型（中文）
	 */
	public final static String N_TypeCHS = "TypeCHS";
	/**
	 * 交易执行次数
	 */
	public final static String N_RunCount = "RunCount";
	/**
	 * 动态数据比例
	 */
	public final static String N_DyRate = "DyRate";
	
	public final static String N_XList = "XList";
	public final static String N_YList = "YList";
	
	public GWTStatTran()
	{
	}
	
	public GWTStatTran(String tranCode,String tranName,int type,int runCount,int scriptCount)
	{
		this.set(N_TranCode, tranCode);
		this.set(N_TranName, tranName);
		this.set(N_Type, type);
		this.set(N_TypeCHS, type == 0 ? "发起端" : "接收端");
		this.set(N_RunCount, runCount);
		if(runCount == 0)
			this.set(N_DyRate, 0);
		else
			this.set(N_DyRate, scriptCount/runCount * 100);
	}
	
	public String getTranCode()
	{
		return get(N_TranCode);
	}
	
	public String getTranName()
	{
		return get(N_TranName);
	}
	
	public int getType()
	{
		return Integer.valueOf(get(N_Type).toString());
	}
	
	public int getCount()
	{
		return Integer.valueOf(get(N_RunCount).toString());
	}
	
	public static List<GWTStatTran> getMockData()
	{
		List<GWTStatTran> tranList = new ArrayList<GWTStatTran>();
		tranList.add(new GWTStatTran("130342","卡片资料查询-GCIH",1,530,0));
		tranList.add(new GWTStatTran("130182","卡片历史查询-PCHI",0,378,23));
		tranList.add(new GWTStatTran("130472","卡片资料查询-GCIH",0,234,56));
		tranList.add(new GWTStatTran("130033","客户基本资料维护-PCMC1",1,143,75));
		tranList.add(new GWTStatTran("130483","卡片资料维护 –GCMH1",0,90,32));
		tranList.add(new GWTStatTran("1109","对公签到",1,55,12));
		tranList.add(new GWTStatTran("1203","补登折交易",1,0,0));
		return tranList;
	}
}
