package com.dc.tes.adapter;

/**
 * 被TES框架调用的响应端适配器接口， 被动接收被测系统发起交易并转发给TES
 * 
 * 本接口文件需要提供给"响应端适配器"开发者
 * 
 * @author guhb,王春佳
 * 
 */
public interface IReplyAdapter extends IAdapter {

	/**
	 * 启动对外服务器, 执行初始化Init后被TES框架调用
	 */
	void Start();

	/**
	 * 关闭对外服务器,
	 */
	void Stop();

	/**
	 * @return 适配器接收到被测系统请求的时间点
	 * 
	 * @author 王春佳
	 * 
	 * @see 延迟操作在通信层处理(暂定),后期看适配器性能(处理时间),再决定是否将延迟放入适配器
	 * 
	 */
	public long TimeOfAcceptRequest();
}
