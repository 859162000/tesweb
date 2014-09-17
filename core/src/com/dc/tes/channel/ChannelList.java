package com.dc.tes.channel;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.dc.tes.Core;
import com.dc.tes.component.ComponentFactory;
import com.dc.tes.component.tag.ComponentType;
import com.dc.tes.data.IRuntimeDAL;
import com.dc.tes.exception.ChannelNotFoundException;
import com.dc.tes.exception.CoreErr;
import com.dc.tes.exception.TESException;

/**
 * 通道列表
 * 
 * @author lijic
 * 
 */
public class ChannelList {
	private static final Log log = LogFactory.getLog(ChannelList.class);

	/**
	 * 通道列表
	 */
	private Map<String, IChannel> m_channels = new LinkedHashMap<String, IChannel>();
	/**
	 * 核心实例
	 */
	private Core m_core;

	/**
	 * 初始化一个通道列表
	 * 
	 * @param core
	 *            核心实例
	 * @param channels
	 *            基础通道列表
	 * @throws Exception
	 */
	public ChannelList(Core core, Map<String, IChannel> channels)
			throws Exception {
		// 首先将基础通道列表中的通道加到通道列表中
		this.m_channels.putAll(channels);

		// 读取通道配置 将配置中列出的通道加到通道列表中
		IRuntimeDAL da = core.da;
		for (String name : da.getComponentConfigNames(ComponentType.Channel)) {
			IChannel channel = ComponentFactory.CreateComponent(da, name);
			this.m_channels.put(name, channel);
		}

		this.m_core = core;
	}

	/**
	 * 根据名称获取指定的通道
	 * 
	 * @param name
	 *            通道名称
	 * @return 指定的通道
	 */
	public IChannel getChannel(String name) {
		if (!this.m_channels.containsKey(name))
			throw new ChannelNotFoundException(name);

		return this.m_channels.get(name);
	}

	/**
	 * 获取通道名称列表
	 * 
	 * @return 返回通道名称列表
	 */
	public Set<String> getChannelNames() {
		return this.m_channels.keySet();
	}

	/**
	 * 启动通道列表中所有通道
	 */
	public void Start() {
		for (String name : this.m_channels.keySet())
			try {
				log.info("启动通道：" + name + " ["
						+ this.m_channels.get(name).getClass().getName() + "]");

				this.m_channels.get(name).Start(this.m_core);
			} catch (Exception ex) {
				log.error(new TESException(CoreErr.ChannelStartFail, name, ex));
			}
	}

	/**
	 * 停止通道列表中所有通道
	 */
	public void Stop() {
		for (String name : this.m_channels.keySet())
			try {
				log.info("停止通道：" + name + " ["
						+ this.m_channels.get(name).getClass().getName() + "]");

				this.m_channels.get(name).Stop();
			} catch (Exception ex) {
				log.error(new TESException(CoreErr.ChannelStopFail, name, ex));
			}
	}
}
