package com.dc.tes.monitor.data;

import java.io.File;
import java.util.Date;

import com.dc.tes.data.DALFactory;
import com.dc.tes.data.IDAL;
import com.dc.tes.data.model.MonitorLog;
import com.dc.tes.data.model.RealtimeLog;
import com.dc.tes.exception.DataException;

public class LogMessage {

	private String sysname;
	private String sysip;
	private String adpflag;
	private String corereginfo;
	private String corepath;
	private LogDetail[] loglist = new LogDetail[Config.MAX_LOG_NUM];//日志列表
	private int logusedtime = 0; //日志完整使用的次数
	private int logpointer = -1; //当前日志写指针指向的位置

	public static void main(String args[]) {
		System.out.println("aa");
	}

	protected LogMessage(String sysn) {
		sysname = sysn;
	}

	public void SetCoreRegInfo(String info){
		corereginfo = info;
	}
	public String GetCoreRegInfo(){
		String temp = corereginfo;
		corereginfo = null;
		return temp;
	}
	
	public String getCorepath() {
		return corepath;
	}
	
	public void setCorepath(String path) {
		corepath = new File(path).getParentFile().getAbsolutePath();
	}
	
	public void addLog(LogDetail log) {
		if (logpointer + 1 == Config.MAX_LOG_NUM) {
			logpointer = 0;
			logusedtime++;
		} else {
			logpointer++;
		}
		loglist[logpointer] = log;
	}

	public void addBDLog(LogDetail log) {
		this.addHLog(log);
		this.addNLog(log);
	}

	private void addNLog(LogDetail log) {
		IDAL<RealtimeLog> monitorDao = DALFactory.GetBeanDAL(RealtimeLog.class);
		RealtimeLog monitorLog = new RealtimeLog();
		monitorLog.setCasename(log.getCASENAME());
		monitorLog.setDatatime(log.getTRANTIME());
		if (log.getERRMSG().length() < 33)
			monitorLog.setErrorflag(log.getERRMSG());
		else
			monitorLog.setErrorflag(log.getERRMSG().substring(0, 32));
		monitorLog.setSysname(this.sysname);
		monitorLog.setSyssign(this.sysip);
		monitorLog.setTrancode(log.getTRANCODE());
		monitorLog.setType(log.getTRANSTATE());
		monitorLog.setHasscript(log.getScript());
		monitorLog.setTranname(log.getTRANNAME());
		monitorLog.setCompareresult(log.getCOMPARE());
		monitorLog.setChannel(log.getChannelname());
		Date data = log.getTRANTIME();
		int y = data.getYear() + 1900;
		int m = data.getMonth() + 1;
		monitorLog.setYearm(y + "" + String.format("%02d", m));
		try {
			monitorDao.Add(monitorLog);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			throw new DataException("添加log到数据库出现异常", e);
		}
	}

	private void addHLog(LogDetail log) {
		IDAL<MonitorLog> monitorDao = DALFactory.GetBeanDAL(MonitorLog.class);
		MonitorLog monitorLog = new MonitorLog();
		monitorLog.setCasename(log.getCASENAME());
		monitorLog.setDatatime(log.getTRANTIME());
		if (log.getERRMSG().length() < 33)
			monitorLog.setErrorflag(log.getERRMSG());
		else
			monitorLog.setErrorflag(log.getERRMSG().substring(0, 32));
		monitorLog.setSysname(this.sysname);
		monitorLog.setSyssign(this.sysip);
		monitorLog.setTrancode(log.getTRANCODE());
		monitorLog.setType(log.getTRANSTATE());
		monitorLog.setHasscript(log.getScript());
		monitorLog.setTranname(log.getTRANNAME());
		monitorLog.setCompareresult(log.getCOMPARE());
		monitorLog.setChannel(log.getChannelname());
		Date data = log.getTRANTIME();
		int y = data.getYear() + 1900;
		int m = data.getMonth() + 1;
		monitorLog.setYearm(y + "" + String.format("%02d", m));
		try {
			monitorDao.Add(monitorLog);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			throw new DataException("添加log到数据库出现异常", e);
		}
	}

	public LogDetail[] getLog(int begid) {
		//System.out.println("界面请求数据,id是:"+begid);
		int logusedtime = this.logusedtime;
		int logpointer = this.logpointer;
		int begnum = 0; // = this.getLogid(begid);
		int totalnum = 0;//= (logpointer - begnum + Config.MAX_LOG_NUM)%Config.MAX_LOG_NUM+1;
		int maxid = logusedtime * Config.MAX_LOG_NUM + logpointer;
		if (begid > maxid || begid < 0) {
			totalnum = 0;
		} else if (begid / Config.MAX_LOG_NUM == logusedtime) {
			begnum = begid % Config.MAX_LOG_NUM;
			totalnum = logpointer - begnum + 1;
		} else {
			begnum = logpointer + 1;
			totalnum = Config.MAX_LOG_NUM;
		}
		//System.out.println("界面请求数据,返回个数:"+totalnum);
		LogDetail[] ld = new LogDetail[totalnum];
		int i = 0;
		while (this.loglist[begnum] != null && i < totalnum) {
			ld[i] = this.loglist[begnum];
			begnum = (begnum + 1) % Config.MAX_LOG_NUM;
			i++;
		}
		return ld;
	}

	public int getmaxid() {
		return logusedtime * Config.MAX_LOG_NUM + logpointer + 1;
	}

	public String getSysname() {
		return sysname;
	}

	public void setSysname(String sysname) {
		this.sysname = sysname;
	}

	public String getSysip() {
		return sysip;
	}

	public void setSysip(String sysip) {
		this.sysip = sysip;
	}

	public String getAdpflag() {
		return adpflag;
	}

	public void setAdpflag(String adpflag) {
		this.adpflag = adpflag;
	}

	public LogDetail[] getLoglist() {
		return loglist;
	}

	public void setLoglist(LogDetail[] loglist) {
		this.loglist = loglist;
	}

	public int getLogusedtime() {
		return logusedtime;
	}

	public void setLogusedtime(int logusedtime) {
		this.logusedtime = logusedtime;
	}

	public int getLogpointer() {
		return logpointer;
	}

	public void setLogpointer(int logpointer) {
		this.logpointer = logpointer;
	}

}
