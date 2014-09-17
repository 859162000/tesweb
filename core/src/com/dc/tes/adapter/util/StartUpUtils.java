package com.dc.tes.adapter.util;

import com.dc.tes.adapter.startup.IStartUp;
import com.dc.tes.adapter.startup.local.StartUpLocal;
import com.dc.tes.adapter.startup.remote.StartUpRemote;

/**
 * 通信层启动接口 工具类
 * 
 * @author 王春佳
 * 
 */
public class StartUpUtils {
	/**
	 * 获取 通信层启动接口实例
	 * 
	 * @param type
	 *            : 启动类型,该值必须为"remote"、"local"中的一种,不区分大小写
	 * @return 通信层启动接口实例,若启动类型参数错误,则返回NULL
	 * 
	 * @see 核心启动通信层,既可以启动"本地通道"、也可以启用"远程通道"
	 */
	public static IStartUp getIStartUpInst(String type) {
		if (type.toLowerCase().equals("local"))
			return new StartUpLocal();
		else if (type.toLowerCase().equals("remote"))
			return new StartUpRemote();
		else
			return null;
	}
}
