package com.dc.tes.adapter.context;

import com.dc.tes.adapter.helper.IReplyAdapterHelper;


/**
 * 
 * 服务端适配器使用,获取与核心进行通信的实例
 * 
 * @author guhb,王春佳
 * 
 * @see 服务端适配器插件使用
 * 
 */
public interface IReplyAdapterEnvContext extends IAdapterEnvContext {

	/**
	 * 获取与核心进行通信的实例
	 * 
	 * @return 响应端适配器插件的帮助对象
	 */
	IReplyAdapterHelper getHelper();
}
