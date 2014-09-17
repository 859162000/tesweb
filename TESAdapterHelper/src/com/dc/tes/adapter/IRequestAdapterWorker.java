package com.dc.tes.adapter;


/**
 * 对一笔发出请求支持多笔同步应答的适配器必须实现的接口
 * 
 * @author guhb
 *
 */
public interface IRequestAdapterWorker {
	/**
	 * 
	 * @return true=是最后一笔应答，false=还有后续应答
	 */
	public boolean IsLast();
	
	
	/**
	 * 可以使用本方法实现对同一个socket收取多次同步应答
	 * 
	 * @param realPackMsg 单笔应答给被测系统的真实报文，null表示TES处理出现异常
	 */
	public byte[] GetResponse(); //被测系统分笔应答适配器，然后适配器分笔应答给TES
	
	/**
	 * 
	 * @return 适配器接收到被测系统请求的时间
	 */
	public long TimeOfAcceptResponse();
}
