package com.dc.tes.channel.internal;

import java.io.IOException;
import java.net.Socket;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.lang.time.FastDateFormat;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.dc.tes.InMessage;
import com.dc.tes.OutMessage;
import com.dc.tes.channel.IAdapterChannel;
import com.dc.tes.fcore.compare.CompareResult;
import com.dc.tes.net.Message;
import com.dc.tes.net.MessageItem;
import com.dc.tes.net.MessageType;

/**
 * 功能核心日志监控通道 该通道与监控服务相连 用于报告运行日志
 * 
 * @author lijic
 * 
 */
public class LogFChannel extends AbstractLogChannel {
	private static final Log log = LogFactory.getLog(LogFChannel.class);
	/**
	 * 功能核心日志监控通道的singleton实例
	 */
	public final static LogFChannel instance = new LogFChannel();

	protected void ConnectMonitor() {
		if (host == null) // host为null 表示不向监控发消息
			return;

		log.info("向监控服务发送注册请求");
		try {
			// 日志通道启动时建立一个与监控服务的连接
			this.m_socket = new Socket(host, port);

			// 每隔30秒向监控服务发一个心跳 第一跳在7000毫秒时进行
			new Timer(true).schedule(new TimerTask() {
				@Override
				public void run() {
					// 新建一个心跳消息
					Message msg = new Message(MessageType.LOGREG);

					// 在消息中添写核心名称信息
					msg.put(MessageItem.LogReg.INSTANCENAME, m_core.instanceName);
					// 在消息中添写核心路径信息
					msg.put(MessageItem.LogReg.INSTANCEPATH, m_core.corepath);
					
					// 在消息中添写适配器状态信息
					String[] adapterStatus = m_core.channels.getChannelNames().toArray(new String[0]);
					StringBuffer buffer = new StringBuffer();
					for (String name : adapterStatus)
						if (m_core.channels.getChannel(name) instanceof IAdapterChannel)
							buffer.append(name + ":" + m_core.channels.getChannel(name).getChannelState() + ",");
					if (buffer.length() != 0)
						buffer.deleteCharAt(buffer.length() - 1);

					msg.put(MessageItem.LogReg.ADAPTERSTATUS, buffer.toString());
					log.info("向监控服务发送注册请求数据" + msg);
					// 如果心跳时出现异常 则说明监控服务端出现问题 则关闭通道 以后不再向监控服务发消息
					if (!send(msg))
						this.cancel();
				}
			}, 7000, 1000 * 30000);
		} catch (IOException ex) {
			// 如果建立连接失败 则说明监控服务端出现问题 则关闭通道 以后不再向监控服务发消息
			log.error("日志监控通道启动失败", ex);
			this.m_socket = null;
		}
	}

	@Override
	public void ReportClientMessage(OutMessage out, InMessage in) {
		if (!super.getChannelState())
			return;

		final Message msg = new Message(MessageType.LOG);
		msg.put(MessageItem.Log.TRANSTATE, 0); // 0 - client
		msg.put(MessageItem.Log.TRANTIME, FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss").format(new Date()));
		msg.put(MessageItem.Log.MSGIN, in.bin);
		msg.put(MessageItem.Log.DATAIN, in.data == null ? "" : in.data.toString());
		msg.put(MessageItem.Log.ERRMSG, in.ex == null ? "" : in.ex.toString());

		new Thread(new Runnable() {
			@Override
			public void run() {
				log.debug("[日志监控消息] " + msg);
				Connected();
				send(msg);
			}
		}).start();
	}

	@Override
	public void ReportServerMessage(InMessage in, OutMessage out) {
		if (!super.getChannelState())
			return;

		final Message msg = new Message(MessageType.LOG);
		msg.put(MessageItem.Log.TRANSTATE, 1); // 1 - server
		msg.put(MessageItem.Log.TRANTIME, FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss").format(new Date()));
		msg.put(MessageItem.Log.TRANCODE, in.tranCode);
		msg.put(MessageItem.Log.MSGIN, in.bin);
		msg.put(MessageItem.Log.DATAIN, in.data == null ? "" : in.data.toString());
		msg.put(MessageItem.Log.MSGOUT, out.bin);
		msg.put(MessageItem.Log.DATAOUT, out.data == null ? "" : out.data.toString());
		msg.put(MessageItem.Log.CASENAME, out.caseName);
		msg.put(MessageItem.Log.ERRMSG, out.ex == null ? "" : out.ex.toString());
		msg.put(MessageItem.Log.COMPARESTATE, in.preserved1 == null ? 0 : ((CompareResult) in.preserved1).getDifference());
		msg.put(MessageItem.Log.CHANNELNAME, in.channel);

		new Thread(new Runnable() {
			@Override
			public void run() {
				log.debug("[日志监控消息] " + msg);
				Connected();
				send(msg);
			}
		}).start();
	}

	public void ReportFlowLogMessage(String flowid, String logid, String msg, int row, int state, boolean iserror) {
		if (!super.getChannelState())
			return;

		final Message m = new Message(MessageType.FLOG);
		m.put(MessageItem.FLog.FLOWID, flowid);
		m.put(MessageItem.FLog.SIGNID, logid);
		m.put(MessageItem.FLog.LOGCONTANT, msg);
		m.put(MessageItem.FLog.SCRIPTROW, row);
		m.put(MessageItem.FLog.STATE, state);
		m.put(MessageItem.FLog.ISERROR, iserror + "");
		m.put(MessageItem.FLog.TIME, FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss").format(new Date()));
		System.out.println("[日志监控消息] " + m.getString(MessageItem.FLog.LOGCONTANT));
		send(m);
		//		new Thread(new Runnable() {
		//			@Override
		//			public void run() {
		//				log.debug("[日志监控消息] " + m);
		//				System.out.println("[日志监控消息] " + m.getString(MessageItem.FLog.LOGCONTANT));
		//				send(m);
		//			}
		//		}).start();
	}
	
}
