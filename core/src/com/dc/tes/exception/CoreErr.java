package com.dc.tes.exception;

/**
 * 由核心定义的错误码
 * 
 * @author lijic
 * 
 */
public class CoreErr {
	public static final ErrCode InitFail = new ErrCode("COR001", "初始化核心失败");
	public static final ErrCode StartFail = new ErrCode("COR002", "启动核心失败");
	public static final ErrCode StopFail = new ErrCode("COR003", "停止核心时发生错误");

	public static final ErrCode LoadCoreConfigFail = new ErrCode("COR004", "读取核心基础配置时发生错误");
	public static final ErrCode InitDalFail = new ErrCode("COR004", "初始化数据访问层时发生错误");
	public static final ErrCode InitFDalFail = new ErrCode("COR005", "初始化数据访问层(f)时发生错误");
	public static final ErrCode InitPDalFail = new ErrCode("COR006", "初始化数据访问层(p)时发生错误");
	public static final ErrCode LoadComponentConfigFail = new ErrCode("COR007", "读取组件配置时发生错误");

	public static final ErrCode TxCodeComponentConfigNotFound = new ErrCode("COR008", "未找到与通道相关的交易识别组件的配置");
	public static final ErrCode TxCodeComponentInitFail = new ErrCode("COR009", "交易识别组件初始化失败");
	public static final ErrCode SecurityComponentConfigNotFound = new ErrCode("COR0010", "未找到与通道相关的安全组件的配置");
	public static final ErrCode SecurityComponentInitFail = new ErrCode("COR0011", "安全组件初始化失败");
	public static final ErrCode PackComponentConfigNotFound = new ErrCode("COR0012", "未找到与通道相关的组包组件的配置");
	public static final ErrCode PackComponentInitFail = new ErrCode("COR0013", "组包组件初始化失败");
	public static final ErrCode UnpackComponentConfigNotFound = new ErrCode("COR014", "未找到与通道相关的拆包组件的配置");
	public static final ErrCode UnpackComponentInitFail = new ErrCode("COR015", "拆包组件初始化失败");

	public static final ErrCode ChannelStartFail = new ErrCode("COR016", "启动通道时发生错误");
	public static final ErrCode ChannelStopFail = new ErrCode("COR017", "停止通道时发生错误");
	public static final ErrCode ChanndelNotOpen = new ErrCode("COR023","通道没有启动");
	public static final ErrCode AdapterStartFail = new ErrCode("COR018", "启动适配器时发生错误");
	public static final ErrCode AdapterStartTimeout = new ErrCode("COR019", "启动适配器超时");
	public static final ErrCode AdapterStopFail = new ErrCode("COR020", "停止适配器时发生错误");
	public static final ErrCode AdapterSendFail = new ErrCode("COR021", "发起端适配器与被测系统交互时发生错误");
	public static final ErrCode DelayFail = new ErrCode("COR022", "执行延时时发生错误");

	public static final ErrCode EmptyServerTranResult = new ErrCode("COR015", "接收端交易的执行结果为空");

	public static final ErrCode SystemNotFound = new ErrCode("COR12", "未找到对应于当前模拟器实例的系统名称");
	public static final ErrCode TranNotFound = new ErrCode("COR013", "交易不存在");
	public static final ErrCode InvalidTranMode = new ErrCode("COR014", "交易类型不正确");
	public static final ErrCode CaseNotFound = new ErrCode("COR014", "案例不存在");
	public static final ErrCode CaseInFlowNotFound = new ErrCode("COR014", "业务流中的案例不存在");
	public static final ErrCode CaseInstanceNotFound = new ErrCode("COR014", "原案例实例不存在");
	public static final ErrCode TranNoCase = new ErrCode("COR014", "交易下没有配置案例");
	public static final ErrCode TranNoDefaultCase = new ErrCode("COR014", "交易下没有指定默认案例");
	public static final ErrCode ChannelNotFound = new ErrCode("COR015", "通道不存在");
	public static final ErrCode UnsupportedDelayType = new ErrCode("COR015", "不支持的延时类型");
	
	public static final ErrCode ConnectError = new ErrCode("COR16","建立适配器连接失败");
	public static final ErrCode SendError = new ErrCode("COR17","发送报文失败");
	public static final ErrCode ReadError = new ErrCode("COR18","读取返回报文失败");

	/** 与License相关的错误 */
	public static class License {
		public static final ErrCode LoadLicenseFail = new ErrCode("LIC001", "读取License时发生错误");
		public static final ErrCode LicenseVerifyFail = new ErrCode("LIC002", "License验证失败");
		public static final ErrCode LicenseVerifyWarn = new ErrCode("LIC003", "License验证警告");
	}

	/** 与交易码识别相关的错误 */
	public static class TxCode {
	}

	/** 与安全处理相关的错误 */
	public static class Security {
		public static final ErrCode DecryptAllFail = new ErrCode("CORS01", "进行全报文解密时发生错误");
		public static final ErrCode DecryptAll2Fail = new ErrCode("CORS02", "进行全报文解密时发生错误");
		public static final ErrCode DecryptDataFail = new ErrCode("CORS03", "对报文数据进行解密时发生错误");
		public static final ErrCode EncryptDataFail = new ErrCode("CORS04", "对报文数据进行加密时发生错误");
		public static final ErrCode EncryptAllFail = new ErrCode("CORS05", "进行全报文加密时发生错误");
	}

	/** 与功能核心相关的错误 */
	public static class FCore {
		public static final ErrCode StartFail = new ErrCode("CORF01", "启动功能核心失败");
	}

	/** 与性能核心相关的错误 */
	public static class PCore {
		public static final ErrCode StartFail = new ErrCode("CORP01", "启动性能核心失败");
	}
}
