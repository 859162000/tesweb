package com.dc.tes.data.model;

import java.io.Serializable;
import java.util.Date;

import com.dc.tes.data.model.tag.BeanIdName;

/**
 * 
 * PERFORMANCELOG :持久化数据 JavaBean映射类
 * 
 * @author huangzx
 * 
 */
@BeanIdName("id")
public class PerformanceLog implements Serializable {

	private static final long serialVersionUID = -84086335010729457L;

	private String id; // 性能监控日志表ID
	private String systemname; // 系统名称
	private String delaytime; // 延迟场景
	private Date begintime; // 开始时间
	private Date endtime; // 结束时间
	private float avgtps; // 平均TPS
	private float maxtps; // 最高TPS
	private float mintps; // 最低TPS
	private int avgdelay; // 平均延迟
	private int maxdelay; // 最高延迟
	private int mindelay; // 最低延迟
	private int avgcpu; // 平均CPU
	private int maxcpu; // 最高CPU
	private int mincpu; // 最低CPU
	private String sysdata; // 系统级别采集数据
	private String trandata; // 交易级别采集数据

	/**
	 * 获取 性能监控日志表ID
	 * 
	 * @return 性能监控日志表ID
	 */
	public String getId() {
		return id;
	}

	/**
	 * 设置 性能监控日志表ID
	 * 
	 * @param id
	 *            : 性能监控日志表ID
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * 获取 系统名称
	 * 
	 * @return 系统名称
	 */
	public String getSystemname() {
		return systemname;
	}

	/**
	 * 设置 系统名称
	 * 
	 * @param systemname
	 *            : 系统名称
	 */
	public void setSystemname(String systemname) {
		this.systemname = systemname;
	}

	/**
	 * 获取 延迟场景
	 * 
	 * @return 延迟场景
	 */
	public String getDelaytime() {
		return delaytime;
	}

	/**
	 * 设置 延迟场景
	 * 
	 * @param delaytime
	 *            延迟场景
	 */
	public void setDelaytime(String delaytime) {
		this.delaytime = delaytime;
	}

	/**
	 * 获取 开始时间
	 * 
	 * @return 开始时间
	 */
	public Date getBegintime() {
		return begintime;
	}

	/**
	 * 设置 开始时间
	 * 
	 * @param begintime
	 *            开始时间
	 */
	public void setBegintime(Date begintime) {
		this.begintime = begintime;
	}

	/**
	 * 获取 结束时间
	 * 
	 * @return 结束时间
	 */
	public Date getEndtime() {
		return endtime;
	}

	/**
	 * 设置 结束时间
	 * 
	 * @param endtime
	 *            结束时间
	 */
	public void setEndtime(Date endtime) {
		this.endtime = endtime;
	}

	/**
	 * 获取 平均TPS
	 * 
	 * @return 平均TPS
	 */
	public float getAvgtps() {
		return avgtps;
	}

	/**
	 * 设置 平均TPS
	 * 
	 * @param avgtps
	 *            平均TPS
	 */
	public void setAvgtps(float avgtps) {
		this.avgtps = avgtps;
	}

	/**
	 * 获取 最高TPS
	 * 
	 * @return 最高TPS
	 */
	public float getMaxtps() {
		return maxtps;
	}

	/**
	 * 设置 最高TPS
	 * 
	 * @param maxtps
	 *            最高TPS
	 */
	public void setMaxtps(float maxtps) {
		this.maxtps = maxtps;
	}

	/**
	 * 获取 最低TPS
	 * 
	 * @return 最低TPS
	 */
	public float getMintps() {
		return mintps;
	}

	/**
	 * 设置 最低TPS
	 * 
	 * @param mintps
	 *            最低TPS
	 */
	public void setMintps(float mintps) {
		this.mintps = mintps;
	}

	/**
	 * 获取 平均延迟
	 * 
	 * @return 平均延迟
	 */
	public int getAvgdelay() {
		return avgdelay;
	}

	/**
	 * 设置 平均延迟
	 * 
	 * @param avgdelay
	 *            平均延迟
	 */
	public void setAvgdelay(int avgdelay) {
		this.avgdelay = avgdelay;
	}

	/**
	 * 获取 最高延迟
	 * 
	 * @return 最高延迟
	 */
	public int getMaxdelay() {
		return maxdelay;
	}

	/**
	 * 设置 最高延迟
	 * 
	 * @param maxdelay
	 *            最高延迟
	 */
	public void setMaxdelay(int maxdelay) {
		this.maxdelay = maxdelay;
	}

	/**
	 * 获取 最低延迟
	 * 
	 * @return 最低延迟
	 */
	public int getMindelay() {
		return mindelay;
	}

	/**
	 * 设置 最低延迟
	 * 
	 * @param mindelay
	 *            最低延迟
	 */
	public void setMindelay(int mindelay) {
		this.mindelay = mindelay;
	}

	/**
	 * 获取 平均CPU
	 * 
	 * @return 平均CPU
	 */
	public int getAvgcpu() {
		return avgcpu;
	}

	/**
	 * 设置 平均CPU
	 * 
	 * @param avgcpu
	 *            平均CPU
	 */
	public void setAvgcpu(int avgcpu) {
		this.avgcpu = avgcpu;
	}

	/**
	 * 获取 最高CPU
	 * 
	 * @return 最高CPU
	 */
	public int getMaxcpu() {
		return maxcpu;
	}

	/**
	 * 设置 最高CPU
	 * 
	 * @param maxcpu
	 *            最高CPU
	 */
	public void setMaxcpu(int maxcpu) {
		this.maxcpu = maxcpu;
	}

	/**
	 * 获取 最低CPU
	 * 
	 * @return 最低CPU
	 */
	public int getMincpu() {
		return mincpu;
	}

	/**
	 * 设置 最低CPU
	 * 
	 * @param mincpu
	 *            最低CPU
	 */
	public void setMincpu(int mincpu) {
		this.mincpu = mincpu;
	}

	/**
	 * 获取 系统级别采集数据
	 * @return 系统级别采集数据
	 */
	public String getSysdata() {
		return sysdata;
	}

	/**
	 * 设置 系统级别采集数据
	 * @param sysdata 系统级别采集数据
	 */
	public void setSysdata(String sysdata) {
		this.sysdata = sysdata;
	}

	/**
	 * 获取 交易级别采集数据
	 * @return 交易级别采集数据
	 */
	public String getTrandata() {
		return trandata;
	}

	/**
	 * 设置 交易级别采集数据
	 * @param trandata 交易级别采集数据
	 */
	public void setTrandata(String trandata) {
		this.trandata = trandata;
	}

}
