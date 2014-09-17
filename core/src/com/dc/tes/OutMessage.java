package com.dc.tes;

import org.apache.commons.lang.SystemUtils;

import com.dc.tes.dom.MsgDocument;
import com.dc.tes.exception.TESException;
import com.dc.tes.util.RuntimeUtils;

/**
 * 表示模拟器处理的输出报文
 * 
 * @author lijic
 * 
 */
public class OutMessage {
	/**
	 * 报文的二进制形式
	 */
	public byte[] bin;
	/**
	 * 报文的数据 如果该报文无法解析则为null
	 */
	public MsgDocument data;
	/**
	 * 该报文所属的交易
	 */
	public String tranCode;
	/**
	 * 该报文对应的案例名称 如果该报文不对应于某个特定案例（例如界面发起的交易）则为null
	 */
	public String caseName;
	/**
	 * 发送或接收该报文使用的通道名称
	 */
	public String channel;
	/**
	 * 在送出该报文时在适配器端应执行的延时时间
	 */
	public long delay;

	/**
	 * 处理过程产生的异常
	 */
	public TESException ex;

	/**
	 * 预留1
	 */
	public Object preserved1;
	/**
	 * 预留2
	 */
	public Object preserved2;
	/**
	 * 预留3
	 */
	public Object preserved3;
	
	public boolean byapi;
	
	public String userId;
	
	public String caseID;
	
	public String caseFlowID;
	
	public String executeLogID;
	
	/**
	 * 案例在数据库的索引字段
	 */
	public String caseIndex;
	

	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("[OUT MESSAGE]").append(SystemUtils.LINE_SEPARATOR);
		buffer.append("Channel=").append(this.channel).append(SystemUtils.LINE_SEPARATOR);
		buffer.append("TranCode=").append(this.tranCode).append(SystemUtils.LINE_SEPARATOR);
		buffer.append("CaseName=").append(this.caseName).append(SystemUtils.LINE_SEPARATOR);
		buffer.append("Delay=").append(this.delay).append(SystemUtils.LINE_SEPARATOR);
		buffer.append("Data=").append(SystemUtils.LINE_SEPARATOR).append(this.data).append(SystemUtils.LINE_SEPARATOR);
		buffer.append("Bin=").append(SystemUtils.LINE_SEPARATOR).append(RuntimeUtils.PrintHex(this.bin, RuntimeUtils.utf8)).append(SystemUtils.LINE_SEPARATOR);
		buffer.append("Ex=").append(this.ex).append(SystemUtils.LINE_SEPARATOR);
		return buffer.toString();
	}
}
