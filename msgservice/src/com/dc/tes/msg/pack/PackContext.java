package com.dc.tes.msg.pack;

import java.util.Map;

import com.dc.tes.msg.IContext;

/**
 * 组包上下文
 * 
 * @author lijic
 * 
 */
public class PackContext {
	/**
	 * 组包样式定义
	 */
	public final PackSpecification spec;
	/**
	 * 上下文
	 */
	public final IContext context;
	/**
	 * 全局上下文
	 */
	private IContext m_globalContext;
	/**
	 * 当前参数
	 */
	public Param param;
	/**
	 * 应用于当前参数的Processor的标志
	 */
	public final char processorChar;
	/**
	 * 应用于当前参数的Processor的参数列表
	 */
	public final Map<String, String> processorParams;
	/**
	 * 应回退的字节数 当该值不为0时 Calculator的计算结果将被忽略
	 */
	public int backspace;


	/**
	 * 初始化一个组包上下文对象
	 */
	public PackContext(PackSpecification spec, IContext context, Param param, char processorChar, Map<String, String> processorParams) {
		this.spec = spec;
		this.context = context;
		this.param = param;

		this.processorChar = processorChar;
		this.processorParams = processorParams;
	}
	/**
	 * getGlobalContext
	 * @return
	 */
	public IContext getGlobalContext()
	{
		return m_globalContext;
	}
	/**
	 * setGlobalContext
	 * @param context
	 */
	public void setGlobalContext(IContext context)
	{
		m_globalContext = context;
	}
}
