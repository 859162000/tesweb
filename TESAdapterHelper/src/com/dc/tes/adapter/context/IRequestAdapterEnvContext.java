package com.dc.tes.adapter.context;


/**
 * 
 * 发起端适配器使用,本接口无特殊方法,仅通过该接口调用父接口方法
 * 
 * @author guhb,王春佳
 * 
 * @see 该接口仅用于过渡使用
 */
public interface IRequestAdapterEnvContext extends IAdapterEnvContext {
	/**
	 * 获得运行时帮助对象，用于同TES通信
	 * 
	 * @return 发起端适配器插件的帮助对象
	 */
//	IRequestAdapterHelper getHelper();
}
