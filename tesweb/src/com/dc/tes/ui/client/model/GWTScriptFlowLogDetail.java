package com.dc.tes.ui.client.model;

import java.io.Serializable;
import java.util.Date;

import com.extjs.gxt.ui.client.data.BaseModelData;
import com.google.gwt.i18n.client.DateTimeFormat;

/**
 * 业务流日志详细信息类
 * @author scckobe
 *
 */
public class GWTScriptFlowLogDetail  extends BaseModelData implements Serializable  {
	private static final long serialVersionUID = 5679782329259366970L;
	
	/**
	 * 日志所属行号
	 */
	private int rowIndex = 0;
	/**
	 * 日志信息
	 */
	private String logInfo = "";
	/**
	 * 日志类型 true:正常执行打印出来的日志 false:出现异常打印出来的日志
	 */
	private boolean sucess = false;
	/**
	 * 日志打印时间
	 */
	private Date time;
	
	public GWTScriptFlowLogDetail()
	{
		this(0,"",false);
	}
	
	/**
	 * 
	 * @param rowIndex  日志所属行号
	 * @param logInfo	日志信息
	 * @param sucess	日志类型
	 */
	public GWTScriptFlowLogDetail(int rowIndex,String logInfo,boolean sucess)
	{
		this(rowIndex,logInfo,sucess,new Date());
	}
	
	/**
	 * 
	 * @param rowIndex  日志所属行号
	 * @param logInfo	日志信息
	 * @param sucess	日志类型
	 */
	public GWTScriptFlowLogDetail(int rowIndex,String logInfo,boolean sucess,Date time)
	{
		this.rowIndex = rowIndex;
		this.logInfo = logInfo;
		this.sucess = sucess;
		this.time = time;
	}
	
	public String getLogInfo()
	{
		return logInfo;
	}
	
	public int getRowIndex()
	{
		return rowIndex;
	}
	
	public boolean getIsSucess()
	{
		return sucess;
	}
	
	public String getTime()
	{
		return DateTimeFormat.getFormat("hh:mm:ss:SSS").format(time).toString();
	}
	
	@Override
	 public String toString() {
		return "";
	 }
}
