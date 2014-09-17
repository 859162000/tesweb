package com.dc.tes.adapter.helper;

import com.dc.tes.adapter.IReplyAdapterWorker;

/**
 * 接收端适配器内部接口,通信层自身来实现
 * 
 * @author guhb,王春佳
 * 
 */
public interface IReplyAdapterHelper extends IAdapterHelper{
	/**
	 * 向核心转发来自被测系统的同步请求，报文参数为从被测系统获得的原始报文。
	 * 
	 * @param realMsg
	 * 			适配器插件接收到被测系统请求的原始报文
	 * @return
	 * 			TES处理后返回给被测系统的结果报文，如果通信失败则返回null
	 */
	public byte[] sendToCore(byte[] realMsg);
	
	/**
	 * 向核心转发来自被测系统的异步请求，报文参数为从被测系统获得的原始报文。
	 * 
	 * @param realMsg
	 * 			适配器插件接收到被测系统请求的原始报文
	 * 
	 */
	public byte[] sendToCoreWithMultiResponse(byte[] realMsg, IReplyAdapterWorker adpWorker);	
}
