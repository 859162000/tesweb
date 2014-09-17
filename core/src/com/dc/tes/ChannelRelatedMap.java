package com.dc.tes;

import java.util.HashMap;

import com.dc.tes.channel.ChannelList;
import com.dc.tes.channel.IChannel;

/**
 * 通道关联表 定义了一个与通道关联的对象列表容器
 * 
 * @author lijic
 * 
 * @param <T>
 *            对象的类型
 */
public class ChannelRelatedMap<T> {
	private static final long serialVersionUID = 3443214975686766700L;

	/**
	 * 对象创建接口 用于创建与通道相关联的对象实例
	 * 
	 * @author lijic
	 */
	public interface IObjectFactory<T> {
		/**
		 * 创建与通道相关联的对象实例
		 * 
		 * @param name
		 *            通道名称
		 * @param channel
		 *            通道实例
		 * @return 与通道相关联的对象实例
		 */
		T Create(String name, IChannel channel);
	}

	/**
	 * 对象列表
	 */
	private HashMap<String, T> m_map = new HashMap<String, T>();

	/**
	 * 初始化一个通道关联表
	 * 
	 * @param list
	 *            通道列表
	 * @param factory
	 *            用于创建与通道相关联的对象实例的创建器实例
	 */
	public ChannelRelatedMap(ChannelList list, IObjectFactory<T> factory) {
		for (String name : list.getChannelNames()) {
			T obj = factory.Create(name, list.getChannel(name));
			this.m_map.put(name, obj);
		}
	}

	/**
	 * 获取与通道相关的对象
	 * 
	 * @param channel
	 *            通道名称
	 * @return 与通道相关的对象
	 */
	public T get(String channel) {
		if (this.m_map.containsKey(channel))
			return this.m_map.get(channel);
		else
			return null;
	}
}
