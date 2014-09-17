package com.dc.tes.adapter;

import com.dc.tes.adapter.host.IListenerAdapterHost;

/**
 * 接收端适配器接口
 * 
 * @author lijic
 * 
 */
public interface IListenerAdapter {
	/**
	 * 回复接口 该接口由通讯层调用，将核心生成的响应消息送给适配器，由适配器送给被测系统
	 * 
	 * @author lijic
	 * 
	 */
	public interface IReplyer {
		/**
		 * 向被测系统返回响应报文
		 * 
		 * @param bytes
		 *            报文字节流
		 */
		public void Reply(byte[] bytes);
	}

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
	public void Start(IListenerAdapterHost host, byte[] config);

	/**
	 * 停止适配器
	 * <p>
	 * 对该函数的调用将位于一个独立的专门用于停止该适配器的线程中（和对Start()的调用不在同一线程）<br/>
	 * 成功停止后应当将当前线程和执行Start()的线程的执行点都释放出来
	 * </p>
	 */
	public void Stop();
}
