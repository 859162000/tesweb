package com.dc.tes.adapter.context;

/**
 * 
 * 适配器配置信息 基类接口
 * 
 * @author guhb,王春佳
 * 
 * @see 适配器插件使用,获取适配器初始化的配置信息
 * 
 */
public interface IAdapterEnvContext {
	/**
	 * 
	 * 适配器通过该方法,获取注册响应信息
	 * 
	 * @return 适配器初始化配置信息
	 * 
	 */
	public byte[] getEvnContext();

}
