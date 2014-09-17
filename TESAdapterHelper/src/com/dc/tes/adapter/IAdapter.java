package com.dc.tes.adapter;

import java.util.Properties;

import com.dc.tes.adapter.context.IAdapterEnvContext;

/**
 * 适配器插件的基类接口
 * 
 * @author guhb,王春佳
 * 
 * @see 外部适配器器必须实现该接口,从而实现适配器与核心的数据交互
 */
public interface IAdapter {

	/**
	 * 适配器初始化
	 * 
	 * @param tesENV
	 *            : 适配器运行所需要的初始化环境数据
	 * 
	 * @return =true表示成功，=false表示失败
	 */
	public boolean Init(IAdapterEnvContext tesENV);

	/**
	 * 获取"适配器类型"
	 * 
	 * @see 类型格式如下: 【协议】.【发起/接收】 例如，tcp.s;udp.c
	 * 
	 * @see 该接口由特定适配器实现,用于标示适配器自身身份,用于核心License校验
	 */
	public String AdapterType();
	
	//获取适配器的配置属性
	public Properties GetAdapterConfigProperties();
		
}
