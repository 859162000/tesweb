package com.dc.tes.channel.internal;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.lang.NotImplementedException;

import com.dc.tes.InMessage;
import com.dc.tes.OutMessage;
import com.dc.tes.net.Message;
import com.dc.tes.net.MessageItem;
import com.dc.tes.net.MessageType;
import com.dc.tes.pcore.plog.*;
import com.dc.tes.pcore.plog.monitor.CpuAndEms;

/**
 * 性能核心日志监控通道 该通道与监控服务相连 用于报告运行日志
 * 
 * @author lijic
 * 
 */
public class LogPChannel extends AbstractLogChannel {
	/**
	 * 性能核心日志监控通道的singleton实例
	 */
	public final static LogPChannel instance = new LogPChannel();

	@Override
	public void ReportClientMessage(OutMessage out, InMessage in) {
		
		throw new NotImplementedException();
	}

	@Override
	public void ReportServerMessage(InMessage in, OutMessage out) {
		if (host == null) // host为null 表示不向监控发消息
			return;
		final String plogInfo = in.plogInfo;
		if(plogInfo.length()<=0)
			return ;
		new Thread(new Runnable() {
			@Override
			public void run() {
				PLogServ.AddLog(plogInfo);
			}
		}).start();
		new Thread(new Runnable() {
			@Override
			public void run() {
				if(!isConnected()){
					ConnectMonitor();
				}
			}
		}).start();
	}

	public void ConnectMonitor() {
		if (host == null) // host为null 表示不向监控发消息
			return;

		if(PLogServ.disTime < 0){
			return ;
		}
		this.Connected();
		new Timer(true).schedule(new TimerTask() {
				@Override
				public void run() {
						Message msg = new Message(MessageType.PLOG);
						msg.put(MessageItem.PLog.INSTANCENAME, m_core.instanceName);
					//	msg.put(MessageItem.PLog.COLLECTDATE, FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss").format(new Date()));
						msg.put(MessageItem.PLog.CPU, CpuAndEms.getCPU());
						msg.put(MessageItem.PLog.RAM, (int)CpuAndEms.getEMS());
						msg.put(MessageItem.PLog.DURATION,PLogServ.disTime);						
						msg.put(MessageItem.PLog.TRANDATA, PLogServ.GetLog());
						System.out.println(new Date()+": "+new String(msg.Export()));
						if (!send(msg))
							this.cancel();
				}
			}, 500, 1000 * PLogServ.disTime);
	}
}
