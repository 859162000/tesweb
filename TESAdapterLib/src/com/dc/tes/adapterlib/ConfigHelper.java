package com.dc.tes.adapterlib;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


public class ConfigHelper {

	private static Log log = LogFactory.getLog(ConfigHelper.class);
	
	/**
	 * 工具方法 获取字节流中的（名-值）对
	 * 
	 * @param content
	 *            字节流
	 * @return 配置内容中的名值对集合
	 */
	public static Properties getConfig(byte[] content) {
		Properties props = new Properties();

		try {
			InputStream in = new BufferedInputStream(new ByteArrayInputStream(
					content));
			props.load(in);

			log.debug("读取配置流信息：" + props.toString());
		} catch (Exception e) {
			props = new Properties();
			log.error("配置流无法解析！[" + e.getMessage() + "]");
		}

		return props;
	}
	
	/**
	 * 判断 keys 中的每个key,是否在 prop中存在;用于适配器初始化校验
	 * 
	 * @param prop
	 *            : 目标Properties对象
	 * @param keys
	 *            : key名称数组
	 * @return false:至少有一个key在prop中不存在 true:所有的key都存在
	 * @author 王春佳
	 */
	public static boolean chkProperKey(Properties prop, String[] keys) {
		boolean existKey = true;

		for (int i = 0; i < keys.length; i++) {
			if (!prop.containsKey(keys[i])){
				log.error("适配器必要的初始化信息不存在:" + keys[i]);
				return false;
			}
				
		}

		return existKey;
	}
}
