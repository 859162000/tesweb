package com.dc.tes.txcode;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import com.dc.tes.component.BaseComponent;
import com.dc.tes.component.tag.ComponentClass;
import com.dc.tes.component.tag.ComponentType;

/**
 * 基于外部函数的交易码识别组件
 * 
 * @author lijic
 * 
 */
@ComponentClass(type = ComponentType.TXCode)
public class FunctionRecogniser extends BaseComponent<FunctionRecogniserConfigObject> implements ITranCodeRecogniser {
	/**
	 * 指向用于判断交易码的函数的指针
	 */
	private Method m_method;

	@Override
	protected void Initialize() throws Exception {
		// 找到指定的类
		Class<?> cls = Class.forName(this.m_config.className);
		// 找到指定的方法
		this.m_method = cls.getMethod(this.m_config.funcName, byte[].class);
		// 判断方法是否为静态方法
		if (!Modifier.isStatic(this.m_method.getModifiers()))
			throw new NoSuchMethodException("找到了指定的方法[" + this.m_config.className + "." + this.m_config.funcName + "], 但该方法不是静态方法");
		// 判断方法的返回值类型是否为字符串
		if (this.m_method.getReturnType() != String.class)
			throw new NoSuchMethodException("找到了指定的方法[" + this.m_config.className + "." + this.m_config.funcName + "], 但该方法的返回值类型不是String,而是" + this.m_method.getReturnType());
	}

	@Override
	public String Recognise(byte[] bytes) throws Exception {
		return (String) this.m_method.invoke(null, bytes);
	}
}
