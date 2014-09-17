package com.dc.tes.monitor;

import java.io.IOException;
import java.net.Socket;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.dc.tes.monitor.data.Config;
import com.dc.tes.monitor.data.Context;
import com.dc.tes.monitor.data.FlowDetail;
import com.dc.tes.monitor.data.LogDetail;
import com.dc.tes.monitor.data.LogMessage;
import com.dc.tes.net.Message;
import com.dc.tes.net.MessageItem;
import com.dc.tes.ui.client.model.GWTScriptFlowLog;

/**
 * 数据访问会话,长连接
 * @author songljb
 *
 */
public class DataDialog implements Runnable {
	private static final Log log = LogFactory.getLog(DataDialog.class);
	private Socket socket = null;
	private String sysname;

	public DataDialog(Socket s) {
		socket = s;
	}

	public void run() {
		boolean isconnect = socket.isConnected();
		try {
			System.out.println("接收到核心消息");
			while (isconnect) {
				Message req = new Message(socket.getInputStream());
				if(!Config.run) {
					socket.close();
					break;
				}
				switch (req.getType()) {
				case LOGREG:
					System.out.println("LOGREG");
					sysname = req.getString(MessageItem.LogReg.INSTANCENAME);
					String corepath = req.getString(MessageItem.LogReg.INSTANCEPATH);
					String sysip = socket.getRemoteSocketAddress().toString();
					String adpflag = req.getString(MessageItem.LogReg.ADAPTERSTATUS);
					LogMessage lmsg = Context.getLogMsg(sysname);
					lmsg.setSysname(sysname);
					lmsg.setCorepath(corepath);
					lmsg.setSysip(sysip);
					lmsg.setAdpflag(adpflag);
					lmsg.SetCoreRegInfo(sysname);
					break;
				case LOG:
					System.out.println("LOG");
					LogDetail log = new LogDetail();
					log.setTRANSTATE(	req.getInteger(	MessageItem.Log.TRANSTATE));
					log.setTRANTIME(	req.getString(	MessageItem.Log.TRANTIME));
//					log.setTRANCODE(	req.getString(	MessageItem.Log.TRANCODE));
//					log.setCASENAME(	req.getString(	MessageItem.Log.CASENAME));
					log.setMSGIN(		req.getBytes(	MessageItem.Log.MSGIN));
//					log.setMSGOUT(		req.getBytes(	MessageItem.Log.MSGOUT));
					log.setDATAIN(		req.getString(	MessageItem.Log.DATAIN));
//					log.setDATAOUT(		req.getString(	MessageItem.Log.DATAOUT));
					log.setERRMSG(		req.getString(	MessageItem.Log.ERRMSG));
//					log.setCOMPARE(		req.getString(	MessageItem.Log.COMPARESTATE));
//					log.setChannelname(		req.getString(	MessageItem.Log.CHANNELNAME));
					//log.puttranname(	sysname);//设置交易名称及脚本状态
					LogMessage logmsg = Context.getLogMsg(sysname);
					logmsg.addLog(log);
					logmsg.addBDLog(log);
					break;
				case FLOG:
					System.out.println("FLOG");
					FlowDetail flog = new FlowDetail();
					flog.setFLOWID(req.getString(MessageItem.FLog.FLOWID));
					flog.setLOGID(req.getString(MessageItem.FLog.SIGNID));
					flog.setSCRIPTROW(req.getString(MessageItem.FLog.SCRIPTROW));
					flog.setLOGCONTANT(req.getString(MessageItem.FLog.LOGCONTANT));
					flog.setSTATE(req.getString(MessageItem.FLog.STATE));
					flog.setISERROR(req.getString(MessageItem.FLog.ISERROR));
					flog.setTime(req.getString(MessageItem.FLog.TIME));
					Context.addFlowLog(flog);
					break;
				default:
					throw new UnsupportedOperationException();
				}

			}

		} catch (Exception e) {//断开连接时,将系统删除
			Context.delLogMsg(sysname);
		} finally {
			// 关闭socket
			try {
				if (socket != null)
					socket.close();
			} catch (IOException ex) {
				ex.printStackTrace();
				throw new RuntimeException("通道连接关闭失败", ex);
			}
		}
	}
}
