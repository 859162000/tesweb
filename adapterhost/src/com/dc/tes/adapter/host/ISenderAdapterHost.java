package com.dc.tes.adapter.host;

/**
 * 发起端适配器宿主 适配器宿主负责接收端适配器的生存期管理和接收端适配器与核心的交互
 * 
 * @author lijic
 * 
 */
public interface ISenderAdapterHost {
	/**
	 * 当适配器成功启动时 应调用此函数向宿主报告一下<br/>
	 * 只有对此函数的第一次调用才有效果 之后的调用被忽略
	 */
	public void Ready();
}
