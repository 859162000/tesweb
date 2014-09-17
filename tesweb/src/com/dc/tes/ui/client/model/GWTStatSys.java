package com.dc.tes.ui.client.model;

import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.ui.client.data.BaseModel;

/**
 * 报表统计 系统级
 * @author scckobe
 *
 */
public class GWTStatSys extends BaseModel {
	private static final long serialVersionUID = -2587542574908754569L;
	/**
	 * 系统名称
	 */
	public final static String N_SysName = "SysName";
	/**
	 * 每月份的总执行数（Number列表）
	 */
	public final static String N_TrendInfo = "TrendInfo";
	/**
	 * 总执行数
	 */
	public final static String N_RunCount = "RunCount";
	/**
	 * 总发起次数
	 */
	public final static String N_SendCount = "SendCount";
	/**
	 * 总响应次数
	 */
	public final static String N_RecvCount = "RecvCount";
	/**
	 * 总交易数
	 */
	public final static String N_TranCount = "TranCount";
	/**
	 * 发起端交易数
	 */
	public final static String N_TranSend = "TranSend";
	/**
	 * 接收端交易数
	 */
	public final static String N_TranRecv = "TranRecv";
	public GWTStatSys(){
		this("");
	}
	
	public GWTStatSys(String sysName){
		set(N_SysName,sysName);
		descTranInfo(0,0);
		clearStatistic();
		descStatistic();
	}
	
	public void descTranInfo(int tranCount, int tranrecv)
	{
		set(N_TranSend,tranCount - tranrecv);
		set(N_TranRecv,tranrecv);
		set(N_TranCount,tranCount);
	}
	
	public String getSysName(){
		return get(N_SysName);
	}
	
	private List<Number> TrendCount = new ArrayList<Number>();
	private int RunCount = 0;
	private int RecvCount = 0;
	private int SendCount = 0;
	
	public void clearStatistic()
	{
		TrendCount = new ArrayList<Number>();
		for(int i = 0; i< 12; i++)
			TrendCount.add(0);
		RunCount = 0;
		RecvCount = 0;
		SendCount = 0;
	}
	
	public void descStatistic()
	{
		this.set(N_TrendInfo,TrendCount);
		this.set(N_RunCount,RunCount);
		this.set(N_SendCount,SendCount);
		this.set(N_RecvCount,RecvCount);
	}
	
	public void addStatistic(int index,int runCount,int recvCount)
	{
		RecvCount += recvCount;
		SendCount += (runCount - recvCount);
		
		TrendCount.set(index, runCount);
		RunCount += runCount;
	}
	
	@SuppressWarnings("unchecked")
	public List<Number> getListCount(){
		return (List<Number>)get(N_TrendInfo);
	}
	
	private void setListCount(List<Integer> runCount,List<Integer> recvCount)
	{
		clearStatistic();
		for(int i = 0; i<runCount.size(); i++)
		{
			int num1 = runCount.get(i);
			int num2 = recvCount.get(i);
			addStatistic(i,Math.max(num1, num2),Math.min(num1, num2));
		}
		descStatistic();
	}
	
	public static List<GWTStatSys> getMock()
	{
		List<GWTStatSys> sysInfo = new ArrayList<GWTStatSys>();
		
		GWTStatSys statSys = new GWTStatSys("CISS");
		statSys.descTranInfo(67,23);
		statSys.setListCount(getList0(),getList1());
		sysInfo.add(statSys);
		
		statSys = new GWTStatSys("EAIH");
		statSys.descTranInfo(30,18);
		statSys.setListCount(getList2(), getList3());
		sysInfo.add(statSys);
		
		statSys = new GWTStatSys("中文系统");
		statSys.descTranInfo(98,54);
		statSys.setListCount(getList4(), getList5());
		sysInfo.add(statSys);
		
		return sysInfo;
	}
	
	private static List<Integer> getList0()
	{
		List<Integer> listCont = new ArrayList<Integer>();
		listCont.add(234);
		listCont.add(243);
		listCont.add(12);
		listCont.add(0);
		listCont.add(234);
		listCont.add(34);
		listCont.add(56);
		listCont.add(34);
		listCont.add(67);
		listCont.add(353);
		listCont.add(130);
		listCont.add(385);
		
		return listCont;
	}
	
	private static List<Integer> getList1()
	{
		List<Integer> listCont = new ArrayList<Integer>();
		listCont.add(123);
		listCont.add(508);
		listCont.add(89);
		listCont.add(490);
		listCont.add(65);
		listCont.add(345);
		listCont.add(64);
		listCont.add(89);
		listCont.add(153);
		listCont.add(349);
		listCont.add(67);
		listCont.add(110);
		
		return listCont;
	}
	
	private static List<Integer> getList2()
	{
		List<Integer> listCont = new ArrayList<Integer>();
		listCont.add(23);
		listCont.add(34);
		listCont.add(543);
		listCont.add(23);
		listCont.add(87);
		listCont.add(285);
		listCont.add(148);
		listCont.add(15);
		listCont.add(110);
		listCont.add(56);
		listCont.add(45);
		listCont.add(85);
		
		return listCont;
	}
	
	private static List<Integer> getList3()
	{
		List<Integer> listCont = new ArrayList<Integer>();
		listCont.add(203);
		listCont.add(45);
		listCont.add(63);
		listCont.add(89);
		listCont.add(21);
		listCont.add(0);
		listCont.add(119);
		listCont.add(232);
		listCont.add(45);
		listCont.add(32);
		listCont.add(99);
		listCont.add(356);
		
		return listCont;
	}
	
	private static List<Integer> getList4()
	{
		List<Integer> listCont = new ArrayList<Integer>();
		listCont.add(51);
		listCont.add(13);
		listCont.add(23);
		listCont.add(34);
		listCont.add(43);
		listCont.add(21);
		listCont.add(0);
		listCont.add(31);
		listCont.add(44);
		listCont.add(67);
		listCont.add(50);
		listCont.add(40);
		
		return listCont;
	}
	
	private static List<Integer> getList5()
	{
		List<Integer> listCont = new ArrayList<Integer>();
		listCont.add(0);
		listCont.add(0);
		listCont.add(0);
		listCont.add(0);
		listCont.add(0);
		listCont.add(0);
		listCont.add(0);
		listCont.add(0);
		listCont.add(0);
		listCont.add(0);
		listCont.add(0);
		listCont.add(0);
		
		return listCont;
	}
}
