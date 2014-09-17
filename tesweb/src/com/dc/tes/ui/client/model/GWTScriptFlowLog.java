package com.dc.tes.ui.client.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.extjs.gxt.ui.client.data.BaseModelData;

/**
 * 业务流监控日志信息
 * @author scckobe
 *
 */
public class GWTScriptFlowLog extends BaseModelData implements Serializable {
	private static final long serialVersionUID = -5854687475654981786L;
	
	/**
	 * 业务流执行状态
	 * 0：执行中
	 * 1：已结束
	 */
	private int status = 0;
	/**
	 * 上次获取时间
	 */
	private Date lstGetTime;
	/**
	 * 执行结果
	 */
	private String result;
	/**
	 * 全部日志信息
	 */
	private List<GWTScriptFlowLogDetail> logList;
	
	public GWTScriptFlowLog(){
		this(-1,new Date(),new ArrayList<GWTScriptFlowLogDetail>());
	}
	
	private GWTScriptFlowLog(int status,Date lstGetTime,List<GWTScriptFlowLogDetail> logList){
		this.status = status;
		this.lstGetTime = lstGetTime;
		this.logList = logList;
	}
	
	public static GWTScriptFlowLog CreateExecptionLog(String errorLog)
	{
		GWTScriptFlowLog logInfo = new GWTScriptFlowLog();
		logInfo.addLogDetail(1, errorLog, false);
		logInfo.Stop();
		return logInfo; 
	}
	
	public static GWTScriptFlowLog CreateDualLog()
	{
		return CreateExecptionLog("异步延迟超时，请确认核心已启动");
	}
	
	public static GWTScriptFlowLog CreateBeginlLog()
	{
		GWTScriptFlowLog logInfo = new GWTScriptFlowLog();
		logInfo.addLogDetail(1, "业务流已往核心发送，请等待执行...", true);
		return logInfo; 
	}
	
	/**
	 * 查看业务流是否已执行完
	 * @return	是否已结束
	 */
	public boolean isEnd()
	{
		return getStatus() == 1;
	}
	
	/**
	 * 查看业务流是否正在执行
	 * @return	是否正在执行
	 */
	public boolean isRunning()
	{
		return getStatus() == 0;
	}
	
	/**
	 * 停止监控，回归最初状态
	 */
	public void Stop()
	{
		setStatus(1);
	}
	
	/**
	 * 设置执行状态
	 * @param status	执行状态
	 */
	public void setStatus(int status)
	{
		this.status = status;
	}
	
	/**
	 * 获得执行状态
	 * @return	执行状态
	 */
	public int getStatus()
	{
		return status;
	}
	
	/**
	 * 设置执行结果
	 * @param result	执行结果
	 */
	public void setResult(String result)
	{
		this.result = result;
	}
	
	/**
	 * 获得最近获取时间
	 * @return	最近获取时间
	 */
	public Date getLstGetTime()
	{
		return lstGetTime;
	}
	
	/**
	 * 设置最近获取时间
	 * @param lstGetTime	最近获取时间
	 */
	public void setLstGetTime(Date lstGetTime)
	{
		this.lstGetTime = lstGetTime;
	}
	
	/**
	 * 获得执行结果
	 * @return	执行结果
	 */
	public String getResult()
	{
		return result;
	}
	
	/**
	 * 添加执行日志 rowIndex已废
	 */
	public void addLogDetail(int rowIndex,String logInfo,boolean sucess)
	{
		addLogDetail(rowIndex,logInfo,sucess,new Date());
	}
	
	/**
	 * 添加执行日志   rowIndex已废 
	 */
	public void addLogDetail(int rowIndex,String logInfo,boolean sucess,Date time)
	{
		if(logList == null)
			logList = new ArrayList<GWTScriptFlowLogDetail>();
		
		logList.add(new GWTScriptFlowLogDetail(rowIndex,logInfo,sucess,time));
		if(sucess)
			setStatus(0);
		else
			Stop();
	}
	
	public List<GWTScriptFlowLogDetail> getLogDetail()
	{
		return logList;
	}

	private static int MockIndex = -1;
	public void MockInfo()
	{
		MockIndex = MockIndex % 4;
		MockIndex ++;
		addLogDetail(MockIndex, "执行第" + MockIndex + "行", MockIndex != 5,new Date());
		for(int i = 1 ; i <= MockIndex; i ++)
			addLogDetail(MockIndex, "执行第" + MockIndex + "." + i + "行",MockIndex != 5, new Date());
		
		if(MockIndex == 3)
			Stop();
	}
}
