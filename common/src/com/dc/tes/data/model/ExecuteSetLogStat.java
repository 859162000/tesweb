package com.dc.tes.data.model;

import java.io.Serializable;

import com.dc.tes.data.model.tag.BeanIdName;

/**
 * ExecuteSetLogStat: 队列执行日志统计表 JavaBean 映射
 * 
 * @author huangzx
 * 
 */
@BeanIdName("id")
public class ExecuteSetLogStat implements Serializable {
	private static final long serialVersionUID = -6353385023956853332L;

	private String id; // ID

	private ExecuteSet executeSet;	//关联 任务队列
	
	private String begintime; 	// 开始时间
	private String endtime; 	// 结束时间
	private String busistat; 	// 业务流统计数据
	private String busilog; 	// 业务流执行日志
	private String casestat; 	// 案例统计数据
	private String caselog; 	// 案例执行日志

	/**
	 * 	获取 关联 任务队列
	 * @return 任务队列
	 */
	public ExecuteSet getExecuteSet() {
		return executeSet;
	}
	
	/**
	 * 设置 关联 任务队列
	 * @param queuelist 任务队列
	 */
	public void setExecuteSet(ExecuteSet executeSet) {
		this.executeSet = executeSet;
	}
	
	/**
	 * 获取 ID
	 * @return ID
	 */
	public String getId() {
		return id;
	}

	/**
	 * 设置 ID
	 * @param id ID
	 */
	public void setId(String id) {
		this.id = id;
	}


	/**
	 * 获取  开始时间
	 * @return 开始时间
	 */
	public String getBegintime() {
		return begintime;
	}

	/**
	 * 设置 开始时间
	 * @param begintime 开始时间
	 */ 
	public void setBegintime(String begintime) {
		this.begintime = begintime;
	}
	
	/**
	 * 获取  结束时间
	 * @return 结束时间
	 */
	public String getEndtime() {
		return endtime;
	}

	/**
	 * 设置 结束时间
	 * @param endtime 结束时间
	 */ 
	public void setEndtime(String endtime) {
		this.endtime = endtime;
	}
	
	/**
	 * 获取  业务流执行日志
	 * @return 业务流执行日志
	 */
	public String getBusilog() {
		return busilog;
	}

	/**
	 * 设置 业务流执行日志
	 * @param busilog 业务流执行日志
	 */ 
	public void setBusilog(String busilog) {
		this.busilog = busilog;
	}

	/**
	 * 获取  业务流统计数据
	 * @return 业务流统计数据
	 */
	public String getBusistat() {
		return busistat;
	}

	/**
	 * 设置 业务流统计数据
	 * @param busistat 业务流统计数据
	 */ 
	public void setBusistat(String busistat) {
		this.busistat = busistat;
	}
	
	/**
	 * 获取  案例执行日志
	 * @return 案例执行日志
	 */
	public String getCaselog() {
		return caselog;
	}

	/**
	 * 设置 案例执行日志
	 * @param caselog 案例执行日志
	 */ 
	public void setCaselog(String caselog) {
		this.caselog = caselog;
	}
	
	/**
	 * 获取  案例统计数据
	 * @return 案例统计数据
	 */
	public String getCasestat() {
		return casestat;
	}

	/**
	 * 设置 案例统计数据
	 * @param casestat 案例统计数据
	 */ 
	public void setCasestat(String casestat) {
		this.casestat = casestat;
	}
}
