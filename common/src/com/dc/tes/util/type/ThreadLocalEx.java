package com.dc.tes.util.type;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 对ThreadLocal的一种简单实现 使用一个大Map，以线程指针为键，ThreadLocalEx变量的Map为值。主要用于跨线程间的ThreadLocal对象访问
 * 
 * @author huangzx
 * 
 * @param <T>
 *            该ThreadLocalEx中存储的对象的类型
 */
public class ThreadLocalEx<T> {
	/**
	 * 所有使用了ThreadLocalEx的线程组成的一张Map
	 */
	private static Map<Thread, Map<Object, Object>> s_map = new LinkedHashMap<Thread, Map<Object, Object>>();

	/**
	 * ThreadLocalEx对象的标识 默认为本对象自身的指针
	 */
	private Object m_key = this;

	/**
	 * 初始化一个ThreadLocalEx对象
	 */
	public ThreadLocalEx() {
	}

	/**
	 * 初始化一个ThreadLocalEx对象
	 * 
	 * @param key
	 *            该ThreadLocalEx对象的标识 该标识用于跨线程访问
	 */
	public ThreadLocalEx(Object key) {
		this.m_key = key;
	}

	/**
	 * 获取与当前线程相关联的值
	 * 
	 * @return ThreadLocalEx变量的值 如果之前未设置过则返回null
	 */
	@SuppressWarnings("unchecked")
	public T get() {
		Map<Object, Object> map = getMap(Thread.currentThread());
		return (T) (map.containsKey(this.m_key) ? map.get(this.m_key) : null);
	}

	/**
	 * 设置与当前线程相关联的值
	 * 
	 * @param value
	 *            ThreadLocalEx对象的值
	 */
	public void set(T value) {
		getMap(Thread.currentThread()).put(this.m_key, value);
	}

	/**
	 * 读取另一个线程的ThreadLocalEx对象的值
	 * 
	 * @param key
	 *            ThreadLocalEx对象的标识
	 * @param t
	 *            要访问的线程
	 * @return 与该线程相关的ThreadLocalEx对象的值
	 */
	@SuppressWarnings("unchecked")
	public static <T> T getCrossThread(Object key, Thread t) {
		Map<Object, Object> map = getMap(t);
		return (T) (map.containsKey(key) ? map.get(key) : null);
	}

	/**
	 * 设置另一个线程的ThreadLocalEx对象的值
	 * 
	 * @param key
	 *            ThreadLocalEx对象的标识
	 * @param value
	 *            与该线程相关的ThreadLocalEx对象的值
	 * @param t
	 *            访问的线程
	 */
	public static void setCrossThread(Object key, Object value, Thread t) {
		getMap(t).put(key, value);
	}

	/**
	 * 工具函数 用于获取与某个特定线程相关联的ThreadLocalEx对象的Map
	 * 
	 * @param t
	 *            线程指针
	 * @return 返回与指定特定线程相关联的ThreadLocalEx对象的Map
	 */
	private static Map<Object, Object> getMap(Thread t) {
		if (!s_map.containsKey(t))
			s_map.put(t, new LinkedHashMap<Object, Object>());

		return s_map.get(t);
	}
}
