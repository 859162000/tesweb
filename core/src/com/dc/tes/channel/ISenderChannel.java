package com.dc.tes.channel;

import com.dc.tes.InMessage;
import com.dc.tes.OutMessage;

/**
 * 发起端通道 发起端通道用于向被测系统发起交易并接收返回的报文
 * 
 * @author lijic
 * 
 */
public interface ISenderChannel extends IAdapterChannel {
	/**
	 * 发起请求
	 * 
	 * @param out
	 *            发向被测系统的报文
	 * @param timeout
	 *            超时时间
	 * @return 从被测系统返回的报文
	 */
	public InMessage Send(OutMessage out, int timeout) throws Exception;
	
}
