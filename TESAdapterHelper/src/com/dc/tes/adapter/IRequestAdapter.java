package com.dc.tes.adapter;

/**
 * 被TES框架调用的发起端适配器接口， 实现向被测系统发起交易并接收返回的报文
 * 
 * 本接口文件需要提供给发起端适配器开发者
 * 
 * @author guhb
 * 
 */
public interface IRequestAdapter extends IAdapter{
	
	/**
	 * 向被测系统发起请求，同步返回应答
	 * 
	 * @param msg
	 *           发向被测系统的真实报文
	 *            
	 * @return  从被测系统返回的报文, null表示出错
	 */
	public byte[] Send(byte[] msg) throws Exception;
	
}
