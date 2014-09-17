package com.dc.tes.adapter;

import com.dc.tes.adapter.host.ISenderAdapterHost;

/**
 * 发起端适配器接口
 * 
 * @author lijic
 * 
 */
public interface ISenderAdapter {
	/**
	 * 启动适配器
	 * <p>
	 * 该函数可以将线程执行点保留在函数内 但在完成启动工作后需要调用host的Ready()函数通知宿主
	 * </p>
	 * 
	 * @param host
	 *            适配器宿主
	 * @param config
	 *            配置信息
	 */
	public void Start(ISenderAdapterHost host, byte[] config);

	/**
	 * 停止适配器
	 * <p>
	 * 对该函数的调用将位于一个独立的专门用于停止该适配器的线程中（和对Start()的调用不在同一线程）<br/>
	 * 成功停止后应当将当前线程和执行Start()的线程的执行点都释放出来
	 * </p>
	 */
	public void Stop();

	/**
	 * 向被测系统发送报文
	 * 
	 * @param bytes
	 *            要向被测系统发送的报文字节流
	 * @return 从被测系统返回的响应
	 * @exception Exception
	 */
	public byte[] Send(byte[] bytes);
}
