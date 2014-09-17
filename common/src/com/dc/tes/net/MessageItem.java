package com.dc.tes.net;

/**
 * 定义核心与适配器传输的报文中各个单元的名称
 * 
 * @author huangzx
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
	 * 表示一个从远程通道传来的消息的远端Socket地址 [String] 该报文单元仅在核心内部使用
	 */
	public static final String REMOTE_HOST = "_remote_host";
	/**
	 * 表示一个从远程通道传来的消息的远端Socket端口 [int] 该报文单元仅在核心内部使用
	 */
	public static final String REMOTE_PORT = "_remote_port";

	/**
	 * 定义适配器向核心的注册消息中的元素
	 * 
	 * @author huangzx
	 * 
	 */
	public final class AdapterReg {
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
		 * 适配器配置信息 [toAdapter][String]
		 */
		public static final String CONFIGINFO = "CONFIGINFO";
	}

	/**
	 * 定义适配器向核心的注销消息中的元素
	 * 
	 * @author huangzx
	 * 
	 */
	public final class AdapterUnreg {
		/**
		 * 通道名称 [toCore][String]
		 */
		public static final String CHANNELNAME = "CHANNELNAME";
	}

	/**
	 * 定义适配器和核心之间传递的报文数据消息中的元素
	 * 
	 * @author huangzx
	 * 
	 */
	public final class AdapterMessage {
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

	/**
	 * 定义界面和核心之间传递的控制命令消息中的元素
	 * 
	 * @author huangzx
	 * 
	 */
	public final class UI {
		/**
		 * 界面命令 [toCore][int]
		 */
		public static final String OP = "OP";

		/**
		 * 交易码[toCore][String]
		 */
		public static final String TRANCODE = "TRANCODE";
		/**
		 * 案例名称[toCore][String]
		 */
		public static final String CASENAME = "CASENAME";
		/**
		 * 请求报文[toCore][byte[]]
		 */
		public static final String REQMESSAGE = "REQMESSAGE";
		/**
		 * 请求报文数据[toCore][String]
		 */
		public static final String REQDATA = "REQDATA";
		/**
		 * 发起通道[toCore][String]
		 */
		public static final String DESTCHANNEL = "DESTCHANNEL";
		/**
		 * 响应报文[toUI][byte[]]
		 */
		public static final String RESMESSAGE = "RESMESSAGE";
		/**
		 * 响应报文数据[toUI][String]
		 */
		public static final String RESDATA = "RESDATA";
		/**
		 * 报文比对结果[toUI][String]
		 */
		public static final String COMPARERESULT = "COMPARERESULT";
		/**
		 * 适配器状态[toUI][String]
		 */
		public static final String ADAPTERSTATUS = "ADAPTERSTATUS"; 
		/**
		 * 业务流编号
		 */
		public static final String CASEFLOWID = "CASEFLOWID";
		/**
		 * 执行日志编号
		 */
		public static final String EXECUTELOGID = "EXECUTELOGID";
	}
	/**
	 * 定义核心和监控服务之间传递的监控注册消息中的元素
	 * 
	 * @author huangzx
	 * 
	 */
	public final class LogReg {
		/**
		 * 核心名称 [toMonitor][String]
		 */
		public static final String INSTANCENAME = "INSTANCENAME";
		/**
		 * 核心路径 [toMonitor][String]
		 */
		public static final String INSTANCEPATH = "INSTANCEPATH";
		/**
		 * 适配器状态 [toMonitor][String]
		 */
		public static final String ADAPTERSTATUS = "ADAPTERSTATUS";
	}

	/**
	 * 
	 * @author songlj
	 *
	 */
	public final class PLog {
		/**
		 * 核心名称 [toMonitor][String]
		 */
		public static final String INSTANCENAME = "INSTANCENAME";
		/**
		 * CPU实用情况[toMonitor][String]
		 */
		public static final String CPU= "CPU";
		/**
		 * 内存使用情况[toMonitor][String]
		 */
		public static final String RAM= "RAM";
		/**
		 * 采样间隔[toMonitor][int]
		 */
		public static final String DURATION= "DURATION";
		/**
		 * 交易级数据
		 */
		public static final String TRANDATA= "TRANDATA";
		
	}
	
	/**
	 * 业务流日志
	 * 
	 * @author songlj
	 * 
	 */
	public final class FLog {
		/**
		 * 局话的行号\ 如果是打印信息,加入监控内容、业务流id、标志id、是否是三监控开始结束标志信息
		 */

		/**
		 * 以下字段依次是: 业务流id\标志\行号\日志内容\开始结束标志\是否是错误标志
		 */
		public static final String FLOWID = "FLOWID";
		public static final String SIGNID = "SIGNID";
		public static final String SCRIPTROW = "SCRIPTROW";
		public static final String LOGCONTANT = "LOGCONTANT";
		public static final String STATE = "STATE";//0表示成功,1 失败
		public static final String ISERROR = "ISERROR";
		public static final String TIME = "TIME";

	}

	/**
	 * 定义核心和监控服务之间传递的流水日志消息中的元素
	 * 
	 * @author huangzx
	 * 
	 */
	public final class Log {
		/**
		 * 交易对应的模拟器名称 [toMonitor][int]
		 */
		public static final String CHANNELNAME = "CHANNELNAME";
		/**
		 * 交易比对结果 [toMonitor][int]
		 */
		public static final String COMPARESTATE = "COMPARESTATE";
		/**
		 * 交易服务段客户端的标志 [toMonitor][int]
		 */
		public static final String TRANSTATE = "TRANSTATE";
		/**
		 * 交易开始处理时间 [toMonitor][String]
		 */
		public static final String TRANTIME = "TRANTIME";
		/**
		 * 交易码 [toMonitor][String]
		 */
		public static final String TRANCODE = "TRANCODE";
		/**
		 * 执行的案例 [toMonitor][String]
		 */
		public static final String CASENAME = "CASENAME";
		/**
		 * 输入报文字节 [toMonitor][byte[]]
		 */
		public static final String MSGIN = "MSGIN";
		/**
		 * 输出报文字节 [toMonitor][byte[]]
		 */
		public static final String MSGOUT = "MSGOUT";
		/**
		 * 输入报文数据 [toMonitor][String]
		 */
		public static final String DATAIN = "DATAIN";
		/**
		 * 输出报文数据 [toMonitor][String]
		 */
		public static final String DATAOUT = "DATAOUT";
		/**
		 * 处理交易的过程中产生的错误信息 [toMonitor][String]
		 */
		public static final String ERRMSG = "ERRMSG";
	}
	
	/**
	 * LICENSE数据
	 * 
	 * @author songlj
	 * 
	 */
	public final class LICENSE {
		/**
		 * 注册核心时，核心发送的字段是：SysName,Port,0；返回LicenseFile,available
		 * 心跳数据：SysName,Port,1；返回available
		 */

		/**
		 * 以下字段依次是: SysName,Port,LicenseFile,available,sign,msg
		 *  			系统名称，端口，license文件，该核心是否可启动,sign标志是新注册的核心还是心跳请求
		 */
		public static final String SYSNAME = "SYSNAME";
		public static final String PORT = "PORT";
		public static final String LICENSEFILE = "LICENSEFILE";
		public static final String AVAILABLE = "AVAILABLE";//0表示核心不能启动，1 核心可以启动
		public static final String SIGN = "SIGN";//0表示注册核心,1 心跳数据
		public static final String MSG = "MSG";//相关信息（过期或个数限制）
	}
}
