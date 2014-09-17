package com.dc.tes.adapter.util;

/**
 * 
 * PLOG字段,通信层对其处理时,需加锁
 * 
 * @author 王春佳
 * 
 */
public class PlogData {

	private static String m_plogDataFromCore = ""; // 核心发送给通信层的PLOG结构:交易码,核心处理时间
	private static long m_responseTime = 0; // 响应时间
	private static long m_executeTime = 0; // 处理时间

	/**
	 * 获取 核心发送给通信层的PLOG结构
	 * 
	 * @return 核心发送给通信层的PLOG结构
	 */
	public String getM_plogDataFromCore() {
		return m_plogDataFromCore;
	}

	/**
	 * 设置 核心发送给通信层的PLOG结构
	 * 
	 * @param dataFromCore
	 *            核心发送给通信层的PLOG结构
	 */
	public void setM_plogDataFromCore(String dataFromCore) {
		m_plogDataFromCore = dataFromCore;
	}

	/**
	 * 获取 响应时间
	 * 
	 * @return 响应时间
	 */
	public long getM_responseTime() {
		return m_responseTime;
	}

	/**
	 * 设置 响应时间
	 * 
	 * @param time
	 *            响应时间
	 */
	public void setM_responseTime(long time) {
		m_responseTime = time;
	}

	/**
	 * 获取 处理时间
	 * 
	 * @return 处理时间
	 */
	public long getM_executeTime() {
		return m_executeTime;
	}

	/**
	 * 设置 处理时间
	 * 
	 * @param time
	 *            处理时间
	 */
	public void setM_executeTime(long time) {
		m_executeTime = time;
	}

	/**
	 * 获取PLOG字段
	 * 
	 * @see PLOG结构:交易码,核心处理时间,响应时间,处理时间
	 */
	public String getPlog() {
		// 第一次向核心发送数据
		if ("".equals(this.m_plogDataFromCore))
			return "";

		// JDK1.6 效率最快
		return m_plogDataFromCore + "," + m_responseTime + "," + m_executeTime;
	}

}
