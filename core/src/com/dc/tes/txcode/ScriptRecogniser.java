package com.dc.tes.txcode;

import java.lang.reflect.Method;

import org.apache.commons.lang.RandomStringUtils;

import com.dc.tes.component.BaseComponent;
import com.dc.tes.component.tag.ComponentClass;
import com.dc.tes.component.tag.ComponentType;
import com.dc.tes.util.RuntimeUtils;

/**
 * 基于脚本的交易码识别组件
 * 
 * @author lijic
 * 
 */
@ComponentClass(desc = "", name = "", type = ComponentType.TXCode)
public class ScriptRecogniser extends BaseComponent<ScriptRecogniserConfigObject> implements ITranCodeRecogniser {
	/**
	 * 指向用于判断交易码的函数的指针
	 */
	private Method m_method;

	@Override
	protected void Initialize() throws Exception {
		// 生成类代码
		StringBuffer code = new StringBuffer();

		code.append(this.m_config.imports);

		String clsName = RandomStringUtils.randomAlphabetic(6);
		code.append("public class ").append(clsName).append("{");
		code.append("public static String Recognise(byte[] bytes) throws Exception{");
		code.append(this.m_config.code);
		code.append("}}");

		// 编译类
		Class<?> cls = RuntimeUtils.CompileClass(code.toString(), clsName);
		// 获取函数指针
		this.m_method = cls.getMethod("Recognise", byte[].class);
	}

	@Override
	public String Recognise(byte[] bytes) throws Exception {
		return (String) this.m_method.invoke(null, bytes);
	}
}
