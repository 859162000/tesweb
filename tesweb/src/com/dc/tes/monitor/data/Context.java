package com.dc.tes.monitor.data;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.dc.tes.data.DALFactory;
import com.dc.tes.data.IDAL;
import com.dc.tes.data.model.RealtimeLog;
import com.dc.tes.data.op.Op;
import com.dc.tes.exception.DataException;
import com.dc.tes.ui.client.model.GWTScriptFlowLog;



/**
 * 用于存放日志数据
 * @author songljb
 *
 */
public class Context {

	//系统的日志列表
	private static HashMap<String, LogMessage> logmsg = new HashMap<String, LogMessage>();

	//系统列表,记录系统是否需要记录实时日志
	private static HashMap<String, RuntimeLogFlag> sysflag = new HashMap<String, RuntimeLogFlag>();

	//业务流日志                        <业务流标记,该业务流下曾经记录下的所有日志>
	private static HashMap<String, FLogList> flog = new HashMap<String, FLogList>();
	
	//核心日志
	private static ArrayList<String> corelog = new ArrayList<String>();
	
	private static ArrayList<String> senderlog = new ArrayList<String>();
	
	private static ArrayList<String> receiverlog = new ArrayList<String>();
	
	/**
	 * 开始记录实时监控
	 * @param sysname
	 */
	public static void startFlag(String sysname) {
		Context.setflag(sysname, 1);
	}

	/**
	 * 获取系统的实时状态
	 * @param sysname
	 * @return
	 */
	public static int getFlag(String sysname) {
		int flag = 0;
		if (Context.sysflag.get(sysname) != null) {
			flag = Context.sysflag.get(sysname).getSysflag();
		}
		return flag;
	}

	/**
	 * 关闭实时状态
	 * @param sysname
	 */
	public static void closeFlag(String sysname) {
		Context.setflag(sysname, 0);
	}

	/**
	 * 删除实时记录
	 * @param sysname
	 */
	public static void cleanRuntimeLog(String sysname) {
		IDAL<RealtimeLog> monitorDao = DALFactory.GetBeanDAL(RealtimeLog.class);
		Op[] op = new Op[1];
		op[0] = Op.EQ("systemName", sysname);
		try {
			List<RealtimeLog> list = monitorDao.ListAll(op);
			for (int i = 0; i < list.size(); i++) {
				monitorDao.Del(list.get(i));
			}
		} catch (Exception e) {
			throw new DataException("从数据库删除系统对应的实时数据出错" + sysname, e);
		}
	}

	/**
	 * 改变系统的实时状态
	 * @param sysname
	 * @param flag
	 */
	private static void setflag(String sysname, int flag) {
		if (Context.sysflag.get(sysname) != null) {
			Context.sysflag.get(sysname).setSysflag(flag);
		} else {
			RuntimeLogFlag rf = new RuntimeLogFlag();
			rf.setSysflag(flag);
			Context.sysflag.put(sysname, rf);
		}
	}

	/**
	 * 判断是否有该系统的log,如果没有,那么该系统现在没有和监控服务连接
	 * @param sysname
	 * @return
	 */
	public static boolean checkLog(String sysname) {
		if (Context.logmsg.get(sysname) == null) {
			return false;
		} else {
			return true;
		}
	}

	public static LogMessage getLogMsg(String sysname) {
		if (Context.logmsg.get(sysname) == null) {
			LogMessage lmsg = new LogMessage(sysname);
			Context.logmsg.put(sysname, lmsg);
			return lmsg;
		} else {
			return logmsg.get(sysname);
		}
	}

	/**
	 * 获取日志
	 * @param sysname系统名称
	 * @param begid  起始id
	 * @param lastId 用于返回最大id值
	 * @return 日志数组
	 */
	public static LogDetail[] getLog(String sysname, int begid, int lastId) {
		LogMessage manager = Context.getLogMsg(sysname);
		LogDetail[] logs = manager.getLog(begid);
		lastId = manager.getmaxid();
		return logs;
	}

	/**
	 * 添加LOg
	 * @param sysname
	 * @param lmsg
	 */
	public static void putLogMsg(String sysname, LogMessage lmsg) {
		Context.logmsg.put(sysname, lmsg);
	}

	/**
	 * 删除LOG
	 * @param sysname
	 */
	public static void delLogMsg(String sysname) {
		Context.logmsg.remove(sysname);
	}
	
	/**
	 * 添加业务流日志
	 * @param flog
	 */
	public static void addFlowLog(FlowDetail flog){
		FLogList loglist =Context.flog.get(flog.getFLOWID());
		if(loglist==null){
			loglist = new FLogList(flog.getFLOWID());
		}
		loglist.addLog(flog);
		Context.flog.put(flog.getFLOWID(), loglist);
		new Thread(new Runnable() {
			public void run() {
				for(String flowid:Context.flog.keySet()){
					FLogList floglist = Context.flog.get(flowid);
					for(String id: floglist.loglist.keySet()){
						GWTScriptFlowLog log = floglist.loglist.get(id);
						Date time = log.getLstGetTime();
						Date now = new Date();
						if((now.getTime() - time.getTime())>Config.outtime)
							floglist.loglist.remove(id);
					}
				}
			}
		}).start();
	}
	public static GWTScriptFlowLog getFlowLog(String FlowID,String ID){
		FLogList loglist =Context.flog.get(FlowID);
		if(loglist == null)
			return null;
		return loglist.getFlowLog(ID);
	}
	
	public static ArrayList<String> getCoreLog() {
		/*
		ArrayList<String> log = new ArrayList<String>();
		if(corelog.size() != 0)
			log = new ArrayList<String>(corelog.subList(coreindex, corelog.size()));			
		coreindex = corelog.size();*/
		return corelog;
	}
	
	public static void addCoreLog(String log) {
		if(corelog.size() > 500) {
			corelog.clear();
		}
		if(log.length() < 20000){
			corelog.add(log);
		}
		 
	}
	
	public static ArrayList<String> getSenderLog() {
		/*
		ArrayList<String> log = new ArrayList<String>();
		if(senderlog.size() != 0)
			log = new ArrayList<String>(senderlog.subList(senderindex, senderlog.size()));			
		senderindex = senderlog.size();*/
		return senderlog;
	}
	
	public static void addSenderLog(String log) {
		if(senderlog.size() > 100) {
			senderlog.clear();
		}
		senderlog.add(log); 
	}
	
	public static ArrayList<String> getReceiverLog() {
		/*
		ArrayList<String> log = new ArrayList<String>();
		if(receiverlog.size() != 0)
			log = new ArrayList<String>(receiverlog.subList(receiverindex, receiverlog.size()));			
		receiverindex = receiverlog.size();*/
		return receiverlog;
	}
	
	public static void addReceiverLog(String log) {
		if(receiverlog.size() > 100) {
			receiverlog.clear();
		}
		receiverlog.add(log); 
	}
	
	public static void clearLog() {
		corelog.clear();
		senderlog.clear();
		receiverlog.clear();
	}
	
	
}
