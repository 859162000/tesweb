package com.dc.tes.gta;

/**
 * TCP适配器属性存储类
 * @author Conan
 *
 */
public class TcpProperty {
	/**
	 * 适配器要模拟的类型，客户端或服务端
	 */
	public String simtype = null;
	
	/**
	 * 适配器要进行绑定的网卡IP地址
	 */
	public String ip = null;
	
	/**
	 * 适配器要进行监听的端口
	 */
	public int port = -1;
	
	/**
	 * 是否需要协商端口
	 */
	public int getportfirst = -1;
	
	/**
	 * 是否为长连接形式
	 * 0，采用TCP短连接模式
	 * 1，采用TCP长连接模式
	 */
	public int islast = -1;
	
	/**
	 * 是否为定长报文
	 * 大于零的值表示为定长报文，且报文长度为该值
	 * 小于等于零的值表示为变长报文
	 */
	public int isfix = -1;
	
	/**
	 * 表示为了要获取变长报文的长度信息需要预先接收的长度
	 */
	public int len4len = -1; 
	
	/**
	 * 报文长度信息在报文中的开始位置
	 */
	public int lenstart = -1;

	/**
	 * 报文长度信息的长度
	 */
	public int lenlen = -1;
	
	/**
	 * 是否需要与核心进行交互
	 */
	public int need2core = -1;
	
	/**
	 * 是否需要返回报文
	 */
	public int needback = -1;
	
	/**
	 * 是否每次返回固定报文
	 */
	public int fixback = -1;
	
	/**
	 * 返回报文路径
	 */
	public String fixbackpath = null;
	
	/**
	 * 返回报文
	 */
	public byte[] backmessage = null;
}
