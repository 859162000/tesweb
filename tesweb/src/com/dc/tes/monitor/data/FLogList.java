package com.dc.tes.monitor.data;

import java.util.Date;
import java.util.HashMap;

import com.dc.tes.ui.client.model.GWTScriptFlowLog;

//业务流日志，同个业务流只有一个对象
public class FLogList {
	private String flowID;
	//            <本次执行该业务流的日志ID,本次的所有日志>
	public HashMap<String, GWTScriptFlowLog> loglist = new HashMap<String, GWTScriptFlowLog>();
	
	public FLogList(String sid){
		this.flowID = sid;
	}

	public void addLog(FlowDetail flog){
		//得到本次执行该业务流的日志情况
		GWTScriptFlowLog log = this.loglist.get(flog.getLOGID());
		if(log == null){
			log = new GWTScriptFlowLog();
		}
		//添加详细日志 比如  "开始执行业务流"，"正在执行第一条案例" 等等
		log.addLogDetail(flog.getSCRIPTROW(), flog.getLOGCONTANT(), flog.getISERROR(),flog.getTime());
		log.setLstGetTime(new Date());
		log.setStatus(flog.getSTATE());
		this.loglist.put(flog.getLOGID(), log);
	}
	
	public GWTScriptFlowLog getFlowLog(String ID){
		GWTScriptFlowLog log =this.loglist.get(ID);
		if(log == null)
			return null;
		this.loglist.remove(ID);
		return log;
	}
}
