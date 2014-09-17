package com.dc.tes.adapter.plugins;

/**
 * 适配器插件类
 * @author Conan
 *
 */
public class AdapterPlugins {
	//根据存入报文获取端口信息
	public static String getCommAddr(byte[] message){
		return "//127.0.0.1:9999";
	}
}
