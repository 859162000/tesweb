package com.dc.tes.msg.unpack.analyser;

import java.util.HashMap;

import com.dc.tes.dom.MsgContainerUtils;
import com.dc.tes.dom.MsgItem;
import com.dc.tes.msg.unpack.UnpackContext;
import com.dc.tes.msg.util.Value;
import com.dc.tes.util.InstanceCreater;

/**
 * 数据分析器 总体上相当于编译原理中的语法分析器 负责将Value对象中的值写入到MsgItem中
 * 
 * @author lijic
 * 
 */
public abstract class Analyser {
	/**
	 * 所有目前支持的Analyser的实例的缓存
	 */
	private static HashMap<String, Analyser> s_analysers = new HashMap<String, Analyser>();

	/**
	 * 初始化Analyser实例缓存
	 */
	static {
		Class<?>[] classes = {
				AFunction.class,
				AMember.class,
				AMemberIndex.class,
				AName.class,
				AScript.class,
				AValue.class };

		for (Class<?> cls : classes)
			if (cls.getSuperclass() == Analyser.class && cls.isAnnotationPresent(AnalyserTag.class))
				s_analysers.put(cls.getAnnotation(AnalyserTag.class).value(), (Analyser) InstanceCreater.CreateInstance(cls));
	}

	/**
	 * 解析给定的字节流
	 * 
	 * @param value
	 *            由Parser解析出的值
	 * @param item
	 *            存放解析出的数据的报文元素
	 * @param context
	 *            拆包上下文
	 * @return 返回用掉的字节数 如果尝试失败 则返回-1
	 */
	public static boolean Analyse(Value value, MsgItem item, UnpackContext context) {
		if (value == null)
			return false;

		item.setAttribute(MsgContainerUtils.C_InternalDomElementPrefix + context.param.expr, value);

		// 选择匹配的DataAnalyser
		if (s_analysers.containsKey(context.param.body)) {
			Analyser analyser = s_analysers.get(context.param.body);
			return analyser.match(value, item, context);
		}

		return true;
	}

	/**
	 * 在派生类中实现时 该函数用于匹配报文字节流
	 * 
	 * @param bytes
	 *            被解析的字节流
	 * @param item
	 *            存放解析出的数据的报文元素
	 * @param length
	 *            通过推断得出的长度
	 * @param context
	 *            拆包上下文
	 * @return 返回用掉的字节数 如果尝试失败 则返回-1
	 */
	protected abstract boolean match(Value value, MsgItem item, UnpackContext context);
}
