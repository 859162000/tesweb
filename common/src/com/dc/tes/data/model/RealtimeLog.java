package com.dc.tes.data.model;

import java.io.Serializable;
import java.util.Date;

import com.dc.tes.data.model.tag.BeanIdName;

/**
 * 
 * RealtimeLog : 实时监控日志 JavaBean映射类
 * 
 * @author huangzx
 * 
 */
@BeanIdName("id")
public class RealtimeLog implements Serializable {

	private static final long serialVersionUID = 1L;

	private String id; //监控日志 ID
	private Date datatime; //时间
	private String sysname; //核心名称
	private String syssign; //系统标示
	private String trancode; //交易编码
	private String casename; //案例名称
	private String errorflag; //错误信息
	private int type; // 客户端、服务端标示

	private int compareresult;//对比结果字段  0：成功；1：失败
	private String tranname; //交易名称
	private int hasscript; //是否有脚本 0：没有；1：有脚本

	private String yearm; //存储年月   例如:200910
	
	private String channel;	// 通道名称
	
	/**
	 * 获取 监控日志 ID
	 * 
	 * @return 监控日志 ID
	 */
	public String getId() {
		return id;
	}

	/**
	 * 设置 监控日志 ID
	 * 
	 * @param id
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * 获取时间
	 * 
	 * @return 时间
	 */
	public Date getDatatime() {
		return datatime;
	}

	/**
	 * 设置 时间
	 * 
	 * @param datatime
	 *            ：时间
	 */
	public void setDatatime(Date datatime) {
		this.datatime = datatime;
	}

	/**
	 * 获取 核心名称
	 * 
	 * @return
	 */
	public String getSysname() {
		return sysname;
	}

	/**
	 * 设置 核心名称
	 * 
	 * @param sysname
	 */
	public void setSysname(String sysname) {
		this.sysname = sysname;
	}

	/**
	 * 获取 系统标示
	 * 
	 * @return
	 */
	public String getSyssign() {
		return syssign;
	}

	/**
	 * 设置 系统标示
	 * 
	 * @param syssign
	 */
	public void setSyssign(String syssign) {
		this.syssign = syssign;
	}

	/**
	 * 获取 交易编码
	 * 
	 * @return
	 */
	public String getTrancode() {
		return trancode;
	}

	/**
	 * 设置 交易编码
	 * 
	 * @param trancode
	 */
	public void setTrancode(String trancode) {
		this.trancode = trancode;
	}

	/**
	 * 获取 案例名称
	 * 
	 * @return 案例名称
	 */
	public String getCasename() {
		return casename;
	}

	/**
	 * 设置 案例名称
	 * 
	 * @param casename
	 *            案例名称
	 */
	public void setCasename(String casename) {
		this.casename = casename;
	}

	/**
	 * 获取 错误信息
	 * 
	 * @return 错误信息
	 */
	public String getErrorflag() {
		return errorflag;
	}

	/**
	 * 设置 错误信息
	 * 
	 * @param errorflag
	 *            错误信息
	 */
	public void setErrorflag(String errorflag) {
		this.errorflag = errorflag;
	}

	/**
	 * 获取 客户端、服务端标示
	 * 
	 * @return 客户端、服务端标示
	 */
	public int getType() {
		return type;
	}

	/**
	 * 设置 客户端、服务端标示
	 * 
	 * @param type
	 *            ：客户端、服务端标示
	 */
	public void setType(int type) {
		this.type = type;
	}

	/**
	 * 获取比对结果
	 * 
	 * @return 比对结果 0=成功 1=失败
	 */
	public int getCompareresult() {
		return compareresult;
	}

	/**
	 * 设置比对结果
	 * 
	 * @param compareresult
	 *            比对结果 0=成功 1=失败
	 */
	public void setCompareresult(int compareresult) {
		this.compareresult = compareresult;
	}

	/**
	 * 获取 交易名称
	 * 
	 * @return 交易名称
	 */
	public String getTranname() {
		return tranname;
	}

	/**
	 * 设置 交易名称
	 * 
	 * @param tranname
	 *            交易名称
	 */
	public void setTranname(String tranname) {
		this.tranname = tranname;
	}

	/**
	 * 获取 是否有脚本 标示
	 * 
	 * @return 是否有脚本 0=没有 1=有
	 */
	public int getHasscript() {
		return hasscript;
	}

	/**
	 * 设置 是否有脚本 标示
	 * 
	 * @param hasscript
	 *            是否有脚本 0=没有 1=有
	 */
	public void setHasscript(int hasscript) {
		this.hasscript = hasscript;
	}

	/**
	 * 获取 年月字符串
	 * @return 年月字符串  例如:200912
	 */
	public String getYearm() {
		return yearm;
	}

	/**
	 * 设置 年月字符串
	 * @param yearm 年月字符串  例如:200912
	 */
	public void setYearm(String yearm) {
		this.yearm = yearm;
	}
	
	/**
	 * 获取 通道名称
	 * @return  通道名称
	 */
	public String getChannel() {
		return channel;
	}

	/**
	 * 设置 通道名称
	 * @param channel 通道名称
	 */
	public void setChannel(String channel) {
		this.channel = channel;
	}
	
}
