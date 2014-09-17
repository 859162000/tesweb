package com.dc.tes;

import org.apache.commons.lang.SystemUtils;

import com.dc.tes.dom.MsgDocument;
import com.dc.tes.exception.TESException;
import com.dc.tes.util.RuntimeUtils;

/**
 * 表示模拟器处理的输入报文
 * 
 * @author lijic
 * 
 */
public class InMessage {
	/**
	 * 报文的二进制形式
	 */
	public byte[] bin;
	/**
	 * 报文的数据 在未解析或无法解析时为null
	 */
	public MsgDocument data;
	/**
	 * 该报文所属的交易
	 */
	public String tranCode;
	/**
	 * 接收该报文时使用的通道名称
	 */
	public String channel;

	/**
	 * 该请求是否已经被回复过 当该变量不为true则表示当前报文已经被回复过
	 */
	public boolean replyFlag;

	/**
	 * 处理被监听的交易时 该域表示接收到接收端请求的线程 该线程和处理该请求的线程不是一个
	 */
	public Thread t;

	/**
	 * 适配器返回的异常
	 */
	public TESException ex;

	/**
	 * 性能监控的日志
	 */
	public String plogInfo;

	/**
	 * 预留1<br/>
	 * 已知用途：发起端交易的结果比对
	 */
	public Object preserved1;
	/**
	 * 预留2<br/>
	 */
	public Object preserved2;
	
	/**
	 * 数据库的执行日志ID
	 */
	public String executeLogID;
	
	public String caseFlowID;
	
	/**
	 * 数据库的案例ID
	 */
	public String caseID;
	
	/**
	 * 案例索引
	 */
	public String caseIndex;
	
	

	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("[IN MESSAGE]").append(SystemUtils.LINE_SEPARATOR);
		buffer.append("Channel=").append(this.channel).append(SystemUtils.LINE_SEPARATOR);
		buffer.append("TranCode=").append(this.tranCode).append(SystemUtils.LINE_SEPARATOR);
		buffer.append("Data=").append(SystemUtils.LINE_SEPARATOR).append(this.data).append(SystemUtils.LINE_SEPARATOR);
		buffer.append("Bin=").append(SystemUtils.LINE_SEPARATOR).append(RuntimeUtils.PrintHex(this.bin, RuntimeUtils.utf8)).append(SystemUtils.LINE_SEPARATOR);
		buffer.append("ReplyFlag=").append(this.replyFlag).append(SystemUtils.LINE_SEPARATOR);
		buffer.append("Ex=").append(this.ex).append(SystemUtils.LINE_SEPARATOR);
		return buffer.toString();
	}
}
