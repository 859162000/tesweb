package com.dc.tes.channel.internal;

import com.dc.tes.InMessage;
import com.dc.tes.OutMessage;
import com.dc.tes.channel.IChannel;

/**
 * 日志监控通道 日志监控通道用于向监控服务报告流水日志和监控信息
 * 全局日志，暂时没用到  by lujs
 * @author lijic
 * 
 */
public interface ILogChannel extends IChannel {
	/**
	 * 报告发起端交易流水信息
	 * 
	 * @param out
	 *            输出报文
	 * @param in
	 *            输入报文
	 */
	public void ReportClientMessage(OutMessage out, InMessage in);

	/**
	 * 报告服务端交易流水信息
	 * 
	 * @param in
	 *            输入报文
	 * @param out
	 *            输出报文
	 */
	public void ReportServerMessage(InMessage in, OutMessage out);

}
