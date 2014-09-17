package com.dc.tes.component;

/**
 * 定义了模拟器组件的基类 所有的模拟器组件都应从此基类派生
 * 
 * @author huangzx
 * 
 */
public abstract class BaseComponent<T extends ConfigObject> {
	/**
	 * 当前组件的配置对象
	 */
	protected T m_config;

	/**
	 * 初始化组件 在派生类实现时 所有的初始化工作都应该在此完成 而不是在构造函数中
	 */
	protected void Initialize() throws Exception {
	}
}