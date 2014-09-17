package com.dc.tes.msg.pack.processor;

import java.util.HashMap;
import java.util.Map;

import com.dc.tes.exception.MsgErr;
import com.dc.tes.exception.TESException;
import com.dc.tes.msg.util.Value;
import com.dc.tes.util.InstanceCreater;

/**
 * 数据输出器(Processor)的基类 从这个类派生出来的类用于将数据以指定的格式输出成字节流
 * 
 * @author lijic
 * 
 */
public abstract class Processor {
	/**
	 * 所有目前支持的Processor的实例的缓存
	 */
	private static HashMap<Character, Processor> s_processors = new HashMap<Character, Processor>();

	/**
	 * 初始化Processor实例缓存
	 */
	static {
		Class<?>[] classes = {
				PBcd.class,
				PByte.class,
				PFloatBigEndian.class,
				PFloatSmallEndian.class,
				PIntBigEndian.class,
				PIntSmallEndian.class,
				PNull.class,
				PString.class };

		for (Class<?> cls : classes)
			if (cls.getSuperclass() == Processor.class && cls.isAnnotationPresent(ProcessorTag.class))
				s_processors.put(cls.getAnnotation(ProcessorTag.class).value(), (Processor) InstanceCreater.CreateInstance(cls));
	}

	/**
	 * 将值输出为字节数组
	 * 
	 * @param value
	 *            要进行格式化输出的值
	 * @param params
	 *            描述输出格式的参数列表
	 * @return 字节数组
	 */
	public static byte[] Process(Value value, char processorChar, Map<String, String> processorParams) {
		return selectProcessor(processorChar).process(value, processorParams);
	}

	/**
	 * 将值组包为字节数组
	 * 
	 * @param value
	 *            要进行格式化输出的值
	 * @param params
	 *            描述输出格式的参数列表
	 * @return 组包出的字节数组
	 */
	protected abstract byte[] process(Value value, Map<String, String> params);

	/**
	 * 获取数据输出器(Processor)
	 * 
	 * @param c
	 *            Processor的标识
	 * @return 指定的Processor
	 */
	private static Processor selectProcessor(char c) {
		if (s_processors.containsKey(c))
			return s_processors.get(c);

		throw new TESException(MsgErr.Pack.ProcessorNotFound, String.valueOf(c));
	}
}
