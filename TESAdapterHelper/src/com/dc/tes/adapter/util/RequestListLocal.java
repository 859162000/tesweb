package com.dc.tes.adapter.util;

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.dc.tes.adapter.IRequestAdapter;

/**
 * 
 * "本地通道"方式,发起端适配器通道名称与适配器对应列表
 * 
 * @author 王春佳
 * 
 */
public class RequestListLocal {

	private static final Log logger = LogFactory.getLog(RequestListLocal.class);

	/**
	 * 通道名称与发起端适配器插件实例Map key-通道名称 value-发起端适配器实例
	 */
	private static Map channelList = new LinkedHashMap();

	/**
	 * 向对应列表中添加数据
	 * 
	 * @param channelName
	 *            ：通道名称
	 * @param adapterInst
	 *            ：发起端适配器实例
	 */
	public static void setChannelListItem(String channelName,
			IRequestAdapter adapterInst) {
		if (!channelList.containsKey(channelName)) {
			channelList.put(channelName, adapterInst);
		} else {
			logger.error("向发起端适配器通道名称与适配器对应列表插入数据失败,该通道名称已存在:" + channelName);
		}
	}

	/**
	 * 从对应列表中取出数据
	 * 
	 * @param channelName
	 *            : 通道名称
	 * @return 发起端适配器实例
	 */
	public static IRequestAdapter getChannelListItem(String channelName) {
		if (channelList.containsKey(channelName)) {
			return (IRequestAdapter) channelList.get(channelName);
		} else {
			logger.error("从发起端适配器通道名称与适配器对应列表取出数据失败,该通道名称不存在:" + channelName);
		}
		return null;
	}

	/**
	 * 清空对应列表中的数据
	 * 
	 * @see "本地通道"方式,发起端适配器,停止通信层内部服务时调用
	 */
	public static void clearChannelListItem() {
		channelList.clear();
	}

}
