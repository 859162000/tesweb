package com.dc.tes.pcore.plog;

import java.util.List;

import com.dc.tes.data.CacheRuntimeDAL;

public class PLogServer {

	
	static LogContant[] logca;
	private static int usingLog = 0;
	static int LogSize = 2;//预备区大小
	static List<String> tranlist ;
	static int perTime = 5000;//采样间隔 5'
	
	public  static void StartPLog(CacheRuntimeDAL da){
		PLogServer.logca = new LogContant[PLogServer.LogSize];
//		PLogServer.tranlist = da.getCaseNames(tranCode, mode);
		for(String trancode :PLogServer.tranlist){
			for(int i =0; i < PLogServer.logca.length; i++){
				PLogServer.logca[i].createLog(trancode);
			}
		}
	}
	private static int getNextUseNum(){
		return (PLogServer.usingLog+1)%PLogServer.LogSize;
	}
	
	public static synchronized void setLogUsingNum() {
		int next = PLogServer.getNextUseNum();
		if(PLogServer.logca[next].isReaded()){
			PLogServer.usingLog = PLogServer.getNextUseNum();
		}
	}
	
	public static  int getLogUseNum() {
		return PLogServer.usingLog;
	}
	
	
	
}
