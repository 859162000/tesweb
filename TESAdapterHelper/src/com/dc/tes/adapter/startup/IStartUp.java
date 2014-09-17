package com.dc.tes.adapter.startup;

/**
 * 通信层启动接口
 * 
 * @author 王春佳
 * 
 */
public interface IStartUp {

	/**
	 * 通信层启动接口,该接口的实现分为"本地通道"、"远程通道"
	 * 
	 * @return 通信层及其所带适配器是否启动成功 True-成功 False-失败
	 */
	public boolean startUp();
}
