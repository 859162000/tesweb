package com.dc.tes.adapter.host;

import com.dc.tes.adapter.IListenerAdapter.IReplyer;

/**
 * 接收端适配器宿主 适配器宿主负责接收端适配器的生存期管理和接收端适配器与核心的交互
 * 
 * @author lijic
 * 
 */
public interface IListenerAdapterHost {
	/**
	 * 当适配器成功启动时 应调用此函数向宿主报告一下<br/>
	 * 只有对此函数的第一次调用才有效果 之后的调用被忽略
	 */
	public void Ready();

	/**
	 * 向核心发送从被测系统接收到的报文
	 * 
	 * @param bytes
	 *            被测系统发来的报文字节流
	 * @param receiveTime
	 *            接收到该请求时的时间 用于通信层计算延时时间
	 * @return 将向被测系统返回的报文
	 */
	public byte[] SendCoreMessage(byte[] bytes, long receiveTime);

	/**
	 * 向核心发送从被测系统接收到的报文
	 * 
	 * @param bytes
	 *            被测系统发来的报文字节流
	 * @param receiveTime
	 *            接收到该请求时的时间 用于通信层计算延时时间
	 * @param replyer
	 *            响应接口
	 */
	public void SendCoreMessage(byte[] bytes, long receiveTime, IReplyer replyer);
}
