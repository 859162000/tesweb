package com.dc.tes.adapter.helper;

/**
 * 
 * 发起端适配器内部接口,通信层自身来实现
 * 
 * @author 王春佳
 * 
 */
public interface IRequestAdapterHelper extends IAdapterHelper{

	/**
	 * 启动通信层内部服务器
	 * 
	 * @see "远程通道" 开启TCP端口,监听核心请求数据(不同发起端适配器,不同监听端口)
	 * @see "本地通道" 初始化全局MAP,key-通道名称 value-IRequestAdapter实例对象
	 * 
	 */
	public void startServer();

	/**
	 * 停止通信层内部服务器
	 * 
	 * @see "远程通道" 关闭TCP端口,监听核心请求数据(不同发起端适配器,不同监听端口)
	 * @see "本地通道" 释放全局MAP
	 */
	public void stopServer();
}
