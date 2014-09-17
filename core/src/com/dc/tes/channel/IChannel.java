package com.dc.tes.channel;

import com.dc.tes.Core;

/**
 * 通道 通道用于与核心交互
 * 
 * @author lijic
 */
public interface IChannel {
	/**
	 * 启动通道 通道在成功启动后必须将控制权交出
	 * 
	 * @param core
	 *            核心实例
	 * @throws Exception
	 */
	public void Start(Core core) throws Exception;

	/**
	 * 停止通道 通道在成功停止后必须将控制权交出
	 * 
	 * @throws Exception
	 */
	public void Stop() throws Exception;

	/**
	 * 获取通道状态
	 * 
	 * @return 返回通道状态
	 */
	public boolean getChannelState();
}
