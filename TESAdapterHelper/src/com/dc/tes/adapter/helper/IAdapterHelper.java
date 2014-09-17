package com.dc.tes.adapter.helper;

/**
 * 适配器内部基类接口,通信层自身来实现
 *  
 * @author guhb,王春佳
 *
 */
public interface IAdapterHelper {
	
	/**
	 * 向核心进行注销
	 */
	void unReg2TES();
	
	/**
	 * 向核心进行注册
	 * 
	 * @return 成功则返回注册应答,响应消息为适配器初始化配置数据.
	 * 		   失败则返回错误提示。
	 */
	byte[] reg2TES();
	
}
