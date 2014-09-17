package com.dc.tes.adapter;

/**
 * 支持多笔应答的适配器必须实现的接口
 * 
 * @author guhb
 *
 */
public interface IReplyAdapterWorker {
	/**
	 * 可以使用本方法实现对同一个socket的多次应答
	 * 
	 * @param realPackMsg 单笔应答给被测系统的真实报文，null表示TES处理出现异常
	 */
	public void Response(byte[] realPackMsg); //分笔应答给被测系统
	
	/**
	 * 
	 * @return 适配器接收到被测系统请求的时间
	 */
	public long TimeOfAcceptRequest();
}
