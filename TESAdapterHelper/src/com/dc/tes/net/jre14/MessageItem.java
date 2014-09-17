package com.dc.tes.net.jre14;

/**
 * 定义核心与适配器交互数据报文样式
 * 适用于jdk1.4.2
 * @author 王春佳
 * 
 */
public class MessageItem {

	/**
	 * 操作结果 [int] 0=成功 非0=失败
	 */
	public static final String RESULT = "RESULT";
	/**
	 * 错误描述 [String]
	 */
	public static final String ERRMSG = "ERRMSG";
	
	/**
	 * 定义适配器向核心的注册消息中的元素
	 * 
	 * @author 王春佳
	 * 
	 */
	public static final class AdapterReg {
		/**
		 * 通道名称 [toCore][String]
		 */
		public static final String CHANNELNAME = "CHANNELNAME";
		/**
		 * 通道类型 [toCore][String]
		 */
		public static final String SIMTYPE = "SIMTYPE";
		/**
		 * 发起端通道的监听端口 [toCore][int]
		 */
		public static final String PORT = "PORT";
		/**
		 * 发起端通道的监听地址 [toCore][String]
		 */
		public static final String HOST = "HOST";
		/**
		 * 适配器配置信息 [toAdapter][String]
		 */
		public static final String CONFIGINFO = "CONFIGINFO";
	}
	
	/**
	 * 定义适配器向核心的注销消息中的元素
	 * 
	 * @author 王春佳
	 * 
	 */
	public static final class AdapterUnreg {
		/**
		 * 通道名称 [toCore][String]
		 */
		public static final String CHANNELNAME = "CHANNELNAME";
	}
	
	/**
	 * 定义适配器和核心之间报文数据消息中的元素
	 * 
	 * @author 王春佳
	 * 
	 */
	public static final class AdapterMessage {
		/**
		 * 请求报文 [toCore(接收端交易), toAdapter(发起端交易)][byte[]]
		 */
		public static final String REQMESSAGE = "REQMESSAGE";
		/**
		 * 响应报文 [toCore(发起端交易), toAdapter(接收端交易)][byte[]]
		 */
		public static final String RESMESSAGE = "RESMESSAGE";
		/**
		 * 通道名称 [toCore][String]
		 */
		public static final String CHANNELNAME = "CHANNELNAME";
		/**
		 * 延时时间 [toAdapter][int]
		 */
		public static final String DELAYTIME = "DELAYTIME";
		/**
		 * 返回包顺序号 非0表示还有后续的需要向被测系统返回的报文 [toAdapter][int]
		 */
		public static final String PACKSEQ = "PACKSEQ";
		/**
		 * 性能监控记录 [toCore][toAdapter][String]
		 */
		public static final String PLOG="PLOG";
	}
}






