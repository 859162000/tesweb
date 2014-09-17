package com.dc.tes.net;

/**
 * 表示模拟器各部分之间传递的报文的类型
 * 
 * @author huangzx
 * 
 */
public enum MessageType {
	/**
	 * 适配器注册消息
	 */
	REG,
	/**
	 * 适配器注销消息
	 */
	UNREG,
	/**
	 * 适配器交易消息
	 */
	MESSAGE,
	/**
	 * 界面控制消息
	 */
	UI,
	/**
	 * 日志监控注册消息
	 */
	LOGREG,
	/**
	 * 日志流水消息
	 */
	LOG,
	/**
	 * 业务流日志
	 */
	FLOG,
	/**
	 * 性能日志
	 */
	PLOG,
	/**
	 * License服务
	 */
	LICENSE,
}
