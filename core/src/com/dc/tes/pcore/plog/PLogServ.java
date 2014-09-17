package com.dc.tes.pcore.plog;

import java.util.Set;

public class PLogServ {

	public static LogContant logList;
	public static String sysName;
	public static int disTime = 5;
	
	public static void CreatePLog(String sysName, Set<String> tranList){

		PLogServ.sysName = sysName;
		PLogServ.logList= new LogContant();
		for(String tranCode: tranList){
			PLogServ.logList.createLog(tranCode);
		}
	}
	public static void CreatePLog(String sysName){
		PLogServ.sysName = sysName;
		PLogServ.logList= new LogContant();
	}
	
	public static void AddLog(String loginfo){
		PLogServ.logList.log(loginfo);
	}
	public static String GetLog(){
		StringBuffer s = new StringBuffer();
		synchronized (logList){
			for(String tranCode: logList.plogs.keySet()){
				TranLogDitail temp = logList.plogs.get(tranCode);
				s.append(tranCode);
				s.append(",");
				s.append(temp.getTotalnum());
				long num = temp.getTotalnum()==0?1:temp.getTotalnum();
				s.append(",");
				s.append(temp.getDelay()/num);
				s.append(",");
				s.append(temp.getUsedtime()/num);
				s.append("|");
				temp.reLog();
			}
		}
		
		return s.toString();
	}
}
