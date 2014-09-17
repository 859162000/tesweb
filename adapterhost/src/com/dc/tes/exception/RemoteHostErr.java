package com.dc.tes.exception;

import com.dc.tes.exception.ErrCode;

/**
 * 由远程通讯层定义的错误码
 * 
 * @author lijic
 * 
 */
public class RemoteHostErr {
	public static final ErrCode ConfigNotFound = new ErrCode("RAH001", "未找到adapter.properties文件，无法加载配置");
	public static final ErrCode LoadConfigIOFail = new ErrCode("RAH002", "在读取adapter.properties文件时发生IO错误");
	public static final ErrCode LoadAdapterClassFail = new ErrCode("RAH003", "读取适配器类(adapter)时发生错误");
	public static final ErrCode LoadAdapterNameFail = new ErrCode("RAH004", "读取适配器名称(name)时发生错误");
	public static final ErrCode LoadCoreAddrFail = new ErrCode("RAH005", "读取核心地址(host)时发生错误");
	public static final ErrCode LoadCorePortFail = new ErrCode("RAH006", "读取核心端口(port)时发生错误");
	public static final ErrCode CorePortMustPositive = new ErrCode("RAH007", "核心端口不能为负数");
	public static final ErrCode LoadLocalPortFail = new ErrCode("RAH008", "读取本地监听端口(localPort)时发生错误");
	public static final ErrCode LocalPortMustPositive = new ErrCode("RAH09", "本地监听端口不能为负数");
	public static final ErrCode CreateAdapterFail = new ErrCode("RAH010", "创建适配器实例时发生错误");
	public static final ErrCode AdapterNotIAdapter = new ErrCode("RAH011", "指定的适配器类型没有实现适配器接口");
	public static final ErrCode LocalServerStartFail = new ErrCode("RAH012", "创建本地Socket服务器时发生错误");
	public static final ErrCode LocalServerStopFail = new ErrCode("RAH013", "停止本地Socket服务器时发生错误");
	public static final ErrCode LocalServerAcceptFail = new ErrCode("RAH014", "监听核心请求(accept)时发生错误");
	public static final ErrCode LocalServerReadFail = new ErrCode("RAH015", "与核心通讯(read)时发生错误");
	public static final ErrCode LocalServerWriteFail = new ErrCode("RAH016", "与核心通讯(write)时发生错误");
	public static final ErrCode LocalServerCloseSocketFail = new ErrCode("RAH017", "关闭Socket时发生错误");
	public static final ErrCode AdapterInitFail = new ErrCode("RAH018", "初始化适配器时发生错误");
	public static final ErrCode AdapterStartFail = new ErrCode("RAH019", "启动适配器时发生错误");
	public static final ErrCode AdapterStopFail = new ErrCode("RAH020", "停止适配器时发生错误");
	public static final ErrCode CoreProcessFail = new ErrCode("RAH021", "核心在处理接收端请求时发生错误");
	public static final ErrCode DelayFail = new ErrCode("RAH022", "执行延时时发生错误");
	public static final ErrCode CoreConnectFail = new ErrCode("RAH023", "建立到核心的连接时发生错误");
	public static final ErrCode CoreReadFail = new ErrCode("RAH024", "与核心通讯(read)时发生错误");
	public static final ErrCode CoreWriteFail = new ErrCode("RAH025", "与核心通讯(write)时发生错误");
	public static final ErrCode CoreCloseSocketFail = new ErrCode("RAH026", "关闭Socket时发生错误");
	public static final ErrCode AdapterSendFail = new ErrCode("RAH027", "发起端适配器与被测系统交互时发生错误");
}
