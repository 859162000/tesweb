package com.dc.tes.util;

import java.lang.reflect.Constructor;
import java.util.Arrays;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.ClassUtils;

import com.dc.tes.exception.CommonErr;
import com.dc.tes.exception.TESException;

/**
 * 工具类 用于创建一个指定类型的对象
 * 
 * @author huangzx
 */
public class InstanceCreater {
	/**
	 * 创建一个指定类型的对象
	 * 
	 * @param <T>
	 *            期望获得的类型
	 * @param className
	 *            类型的名称
	 * @return 类型的实例
	 */
	@SuppressWarnings("unchecked")
	public static <T> T CreateInstance(String className) {
		return (T) CreateInstance(className, ArrayUtils.EMPTY_OBJECT_ARRAY);
	}

	/**
	 * 创建一个指定类型的对象
	 * 
	 * @param <T>
	 *            期望获得的类型
	 * @param cls
	 *            类型
	 * @return 类型的实例
	 */
	public static <T> T CreateInstance(Class<? extends T> cls) {
		return CreateInstance(cls, ArrayUtils.EMPTY_OBJECT_ARRAY);
	}

	/**
	 * 创建一个指定类型的对象 可以向该类型的构造函数提供参数
	 * 
	 * @param <T>
	 *            期望获得的类型
	 * @param className
	 *            类型的名称
	 * @param args
	 *            初始化参数（传给构造函数的参数）
	 * @return 类型的实例
	 */
	@SuppressWarnings("unchecked")
	public static <T> T CreateInstance(String className, Object... args) {
		try {
			Class<T> cls = (Class<T>) Class.forName(className);
			return CreateInstance(cls, args);
		} catch (ClassNotFoundException e) {
			throw new TESException(CommonErr.Instantiation.ClassNotFound, className);
		}
	}

	/**
	 * 创建一个指定类型的对象 可以向该类型的构造函数提供参数
	 * 
	 * @param <T>
	 *            期望获得的类型
	 * @param cls
	 *            类型
	 * @param args
	 *            初始化参数（传给构造函数的参数）
	 * @return 类型的实例
	 */
	@SuppressWarnings("unchecked")
	public static <T> T CreateInstance(Class<? extends T> cls, Object... args) {
		// 如果cls为null 则抛出异常
		if (cls == null)
			throw new TESException(CommonErr.Instantiation.CreateInstanceFail, "<null>");

		// 获得构造函数的参数类型列表
		Class<?>[] types = new Class<?>[args.length];
		for (int i = 0; i < args.length; i++)
			types[i] = args[i].getClass();

		// 首先去找与参数类型列表完全匹配的构造函数 如果找到则返回其调用结果
		try {
			try {
				Constructor<? extends T> constructor = cls.getConstructor(types);

				return constructor.newInstance(args);
			} catch (NoSuchMethodException ex) {
				// 该类不存在与指定参数完全匹配的构造函数 遍历该类的所有构造函数 找出一个匹配的
				for (Constructor<?> constructor : cls.getDeclaredConstructors())
					if (ClassUtils.isAssignable(types, constructor.getParameterTypes())) {
						constructor.setAccessible(true);
						return (T) constructor.newInstance(args);
					}

				// 该类所有的构造函数都无法与给定的参数相匹配 则创建失败 抛异常
				throw new TESException(CommonErr.Instantiation.ConstructorNotFound, cls.getName() + " args: " + Arrays.toString(args));
			}
		} catch (Exception ex) {
			throw new TESException(CommonErr.Instantiation.CreateInstanceFail, cls.getName(), ex);
		}
	}
}
