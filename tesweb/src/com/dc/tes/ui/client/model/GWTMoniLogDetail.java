package com.dc.tes.ui.client.model;

import java.io.Serializable;

import com.extjs.gxt.ui.client.data.BaseModelData;

public class GWTMoniLogDetail extends BaseModelData implements Serializable {

	private static final long serialVersionUID = 3903390855653033313L;
	
	public static String N_SYSNAME = "sysName"; //系统名称
	public static String N_TRANSTATE = "tranState"; //客户端服务器端标识  0:client/1:server
	public static String N_TRANTIME = "tranTime"; //报文发起/响应时间
	public static String N_CHANNEL = "channel"; //通道名称
	public static String N_TRANCODE = "tranCode"; //交易码
	public static String N_TRANNAME = "tranName"; //交易名称
	public static String N_CASENAME = "caseName"; //案例名称
	public static String N_MSGIN = "msgIn"; //输入原始报文
	public static String N_MSGOUT = "msgOut"; //输出原始报文
	public static String N_DATAIN = "dataIn"; //输出原始报文
	public static String N_DATAOUT = "dataOut"; //输出原始报文
	public static String N_ERRMSG = "errorMsg"; //错误记录
	public static String N_LASTID = "lastId";
	public static String N_REGINFO = "regInfo"; //核心注册消息
}
