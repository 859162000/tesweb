package com.dc.tes.channel.remote;

import com.dc.tes.channel.IAdapterChannel;

/**
 * 远程通道 远程通道是指与核心分离部署的通道 该接口被ChannelServer调用
 * 
 * @author lijic
 * 
 */
public interface IRemoteAdapterChannel extends IRemoteChannel, IAdapterChannel {
	/**
	 * 获取适配器配置
	 * 
	 * @return 适配器配置
	 */
	public byte[] getAdapterConfig();
}
