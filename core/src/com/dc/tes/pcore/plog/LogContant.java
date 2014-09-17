package com.dc.tes.pcore.plog;

import java.util.LinkedHashMap;
import java.util.Map;

public class LogContant {
	protected Map<String, TranLogDitail> plogs = new LinkedHashMap<String, TranLogDitail>();
	private boolean isReaded = false;
	public void log(String loginfo){
		//String.split(”.”);//
		String [] temp = loginfo.split(",");
		if(temp.length != 4){
			return ;
		}
		String trancode = temp[0];
		long coredelay = Long.parseLong(temp[1]);
		long delay = Long.parseLong(temp[2]);
		long usedtime = Long.parseLong(temp[3]);
		TranLogDitail tlog = plogs.get(trancode);
		if(null==tlog){
			//交易列表中不存在
			tlog = new TranLogDitail();
			this.plogs.put(trancode, tlog);
		}
		synchronized (tlog) {
			tlog.Log(coredelay, delay, usedtime);
		}
	}

	public void createLog(String name){
		TranLogDitail tlog = new TranLogDitail();
		this.plogs.put(name, tlog);
	}
	public void reLog(String name){
		TranLogDitail tlog = this.plogs.get(name);
		if(null !=tlog){
			tlog.reLog();
		}
	}

	public boolean isReaded() {
		return isReaded;
	}

	public void setReaded(boolean isReaded) {
		this.isReaded = isReaded;
	}
}
