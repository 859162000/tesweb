package com.dc.tes.channel;

import com.dc.tes.OutMessage;

/**
 * 接收端通道 接收端通道用于接收并处理被测系统发来的请求
 * 
 * @author lijic
 * 
 */
public interface IListenerChannel extends IAdapterChannel {
	/**
	 * 向被测系统返回响应报文
	 * 
	 * @param out
	 *            向被测系统返回的响应报文
	 * @param original
	 *            接到该请求的线程
	 * @throws Exception
	 */
	public void Reply(OutMessage out, Thread original) throws Exception;

	/**
	 * 向被测系统返回多个响应报文
	 * 
	 * @param list
	 *            向被测系统返回的响应报文列表
	 * @param original
	 *            接到该请求的线程
	 * @throws Exception
	 */
	public void Reply(OutMessage[] list, Thread original) throws Exception;
}
