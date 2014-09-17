package com.dc.tes.exception;

/**
 * 由common定义的错误
 * 
 * @author huangzx
 * 
 */
public class CommonErr {
	public static final ErrCode GetStartDirFail = new ErrCode("CMN001", "获取系统当前运行路径出错");
	public static final ErrCode ConvertFromStringFail = new ErrCode("CMN002", "无法将字符串转换为期望的类型");
	public static final ErrCode UnsupportedConversionFromString = new ErrCode("CMN003", "无法将字符串转换为期望的类型_指定的目标类型不被支持");
	public static final ErrCode DynamicCompileFail = new ErrCode("CMN004", "动态编译失败");
	public static final ErrCode DynamicCompileSyntaxError = new ErrCode("CMN005", "动态编译失败_编译目标存在语法错误");
	public static final ErrCode UnsupportedEncoding = new ErrCode("CMN006", "不支持的编码名称");
	public static final ErrCode IllegalHexString = new ErrCode("CMN007", "不是有效的HEX字符串");;
	public static final ErrCode LENGTHOFFIELDERROR = new ErrCode("CMN008", "55域的子域长度有问题，无法被2整除");;

	/** 与创建对象相关的错误 */
	public static class Instantiation {
		public static final ErrCode CreateInstanceFail = new ErrCode("CMNC01", "创建类型实例失败");
		public static final ErrCode ClassNotFound = new ErrCode("CMNC02", "创建类型实例失败_ClassNotFound");
		public static final ErrCode ConstructorNotFound = new ErrCode("CMNC03", "该类所有的构造函数都无法与给定的参数相匹配");
		public static final ErrCode CreateComponentFail = new ErrCode("CMNC04", "创建组件失败");
		public static final ErrCode ComponentTagRequired = new ErrCode("CMNC05", "要创建的组件未使用@ComponentClass进行标记");
		public static final ErrCode FillComponentPropertyFail = new ErrCode("CMNC06", "设置组件的配置属性项时发生错误");
	}

	/** 与IO相关的错误 */
	public static class IO {
		public static final ErrCode FileNotFound = new ErrCode("CMNI01", "文件找不到");
		public static final ErrCode IOReadFail = new ErrCode("CMNI02", "从输入流中读取数据时发生IO错误");
		public static final ErrCode IOWriteFail = new ErrCode("CMNI03", "从输入流中读取数据时发生IO错误");
		public static final ErrCode CloseInputStreamFail = new ErrCode("CMNI04", "关闭输入流时发生IO错误");
		public static final ErrCode CreateFileFail = new ErrCode("CMNI05", "创建文件失败");
		public static final ErrCode CloseOutputStreamFail = new ErrCode("CMNI06", "关闭输出流时发生IO错误");
		public static final ErrCode DeleteTargetNotFound = new ErrCode("CMNI07", "被删除的目标不存在");
		public static final ErrCode DeleteTargetNotDir = new ErrCode("CMNI08", "被删除的目标不是一个目录");
	}

	/** 与数据访问层相关的错误 */
	public static class Dal {
		public static final ErrCode FactoryInitFail = new ErrCode("CMND01", "初始化数据源工厂类时发生错误");
		public static final ErrCode FactoryNotInitialized = new ErrCode("CMND02", "数据层尚未成功初始化");
		public static final ErrCode GetBeanDALFail = new ErrCode("CMND03", "获取数据访问对象时发生错误");
		public static final ErrCode HibernateInitFail = new ErrCode("CMND04", "Hibernate初始化失败");
		public static final ErrCode HibernateNotInitialized = new ErrCode("CMND05", "Hibernate尚未成功初始化");
		public static final ErrCode XmlFail = new ErrCode("CMND06", "读写xml时发生错误");
		public static final ErrCode GetDataFail = new ErrCode("CMND07", "获取数据库对象时发生错误");
	}

	/** 与内部报文处理相关的错误 */
	public static class Net {
		public static final ErrCode ReceiveMessageFail = new ErrCode("CMNM01", "解析接收到的消息时发生错误");
		public static final ErrCode Timeout = new ErrCode("CMNM02", "通讯超时");
		public static final ErrCode UnknownMessageType = new ErrCode("CMNM03", "消息类型无法识别");
	}

	/** 与XML读写相关的错误 */
	public static class Xml {
		public static final ErrCode LoadNullInputStream = new ErrCode("CMNX01", "提供给xml读取器的输入流为null");
		public static final ErrCode LoadNullString = new ErrCode("CMNX02", "提供给xml读取器的字符串为null");
		public static final ErrCode SAXFail = new ErrCode("CMNX03", "xml中存在语法错误");
		public static final ErrCode LoadXmlFail = new ErrCode("CMNX04", "读取xml时发生错误");
		public static final ErrCode XPathSyntaxError = new ErrCode("CMNX05", "xpath语法存在错误");
		public static final ErrCode WriteNullOutputStream = new ErrCode("CMNX06", "用于承载xml的输出流为null");
		public static final ErrCode WriteXmlFail = new ErrCode("CMNX07", "向输出流写入xml时发生错误");
	}
}
