package com.dc.tes.ui.client.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.ui.client.data.BaseModelData;

public class GWTRealTimeLogInfo extends BaseModelData implements Serializable {

	private static final long serialVersionUID = 7231775962795225331L;

	public static String N_DATETIME = "datatime";
	public static String N_TRANCODE = "trancode";
	public static String N_CASENAME = "casename";
	public static String N_TYPE = "type";
	public static String N_TRANNAME = "tranname";
	public static String N_TRANCOUNT = "trancount";
	public static String N_PRECENT = "precent";
	
	public GWTRealTimeLogInfo(){}
	
	public GWTRealTimeLogInfo(String trancode, String tranName, String type, int count, String precent){
		set(N_TRANCODE, trancode);
		set(N_TRANNAME, tranName);
		set(N_TYPE, type);
		set(N_TRANCOUNT, count);
		set(N_PRECENT, precent);
	}
	
	public GWTRealTimeLogInfo(String caseName, String tranCode, String type, String date){
		set(N_CASENAME, caseName);
		set(N_TRANCODE, tranCode);
		set(N_TYPE, type);
		set(N_DATETIME, date);
	}
	
	public static List<GWTRealTimeLogInfo> getUsefulTranMockData(){
		List<GWTRealTimeLogInfo> arrayList = new ArrayList<GWTRealTimeLogInfo>();
		arrayList.add(new GWTRealTimeLogInfo("130342", "卡片资料查询-GCIH", "接收", 585, "41%"));
		arrayList.add(new GWTRealTimeLogInfo("130182", "卡片历史查询-PCHI", "接收", 365, "23%"));
		arrayList.add(new GWTRealTimeLogInfo("130472", "卡片资料查询-GCIH", "接收", 291, "17%"));
		arrayList.add(new GWTRealTimeLogInfo("130033", "客户基本资料维护-PCMC1", "接收", 137, "9%"));
		arrayList.add(new GWTRealTimeLogInfo("130483", "卡片资料维护 –GCMH1", "接收", 64, "4%"));
		return arrayList;
	}
	
	public static List<GWTRealTimeLogInfo> getRecentCaseMockData(){
		List<GWTRealTimeLogInfo> arrayList = new ArrayList<GWTRealTimeLogInfo>();
		arrayList.add(new GWTRealTimeLogInfo("case1", "130342", "接收", "2010-1-10 19:25:32"));
		arrayList.add(new GWTRealTimeLogInfo("case2", "130182", "接收", "2010-1-10 18:23:32"));
		arrayList.add(new GWTRealTimeLogInfo("case3", "130472", "接收", "2010-1-10 18:22:11"));
		arrayList.add(new GWTRealTimeLogInfo("case4", "130033", "接收", "2010-1-10 16:25:01"));
		arrayList.add(new GWTRealTimeLogInfo("case5", "130483", "接收", "2010-1-10 11:46:48"));
		arrayList.add(new GWTRealTimeLogInfo("case6", "130483", "接收", "2010-1-10 09:53:21"));
		arrayList.add(new GWTRealTimeLogInfo("case7", "130987", "接收", "2010-1-10 07:41:14"));
		arrayList.add(new GWTRealTimeLogInfo("case8", "114023", "接收", "2010-1-10 07:52:37"));
		arrayList.add(new GWTRealTimeLogInfo("case9", "130483", "接收", "2010-1-10 06:07:42"));
		arrayList.add(new GWTRealTimeLogInfo("case10", "167092", "接收", "2010-1-09 05:46:44"));
		return arrayList;
	}
}
