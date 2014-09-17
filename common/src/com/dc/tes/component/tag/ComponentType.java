package com.dc.tes.component.tag;

/**
 * 定义组件的类型
 * 
 * @author huangzx
 * 
 */
public enum ComponentType {
	/**
	 * 通道组件 这种组件负责模拟器与外部的交互
	 */
	Channel,
	/**
	 * 交易解析组件 这种组件负责从字节流中解析出交易码
	 */
	TXCode,
	/**
	 * 安全处理组件 这种组件负责处理报文安全
	 */
	Security,
	/**
	 * 组包组件
	 */
	Pack,
	/**
	 * 拆包组件
	 */
	Unpack,
	/**
	 * 业务处理组件
	 */
	Process,
}
