package com.dc.tes.ui.client.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.extjs.gxt.ui.client.data.BaseModel;
import com.extjs.gxt.ui.client.data.ModelData;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.rpc.IsSerializable;

public class GWTQueueLog extends BaseModel implements IsSerializable {
	public static final long serialVersionUID = 6491182413435295316L;
	public static String N_GroupName = "groupName";
	public static String N_Type = "type";
	
	public static String N_IsSucess = "sucess";
	public static String N_LogList = "logList";
	public static String N_LogDetail = "logDetail";
	public static String N_LogMsg = "logDetail";
	public static String N_Status = "status";
	public static String N_Order = "order";
	
	public static int order = 0;
	public GWTQueueLog()
	{
		this(new GWTQueueTask(),-1);
	}
	
	public GWTQueueLog(GWTQueueTask task,int rowIndex)
	{
//		String groupName = "顺序号:" + (rowIndex + 1) + "，任务名称：" + task.getTaskName();
		this.set(N_GroupName, rowIndex);
		this.set(N_Type,task.getType());
		set(N_IsSucess,false);
		set(N_Status,1);
		set(N_Order,order++);
		
		List<String> logList = new ArrayList<String>();
		if(getIsCase())
			logList.add(FormatLog("开始执行"));
		set(N_LogList,logList);
		
		
		this.SetLogDetail(false, "",(task.getType() == 1 ? 
				new GWTScriptFlowLog() : new GWTCompareResult()),false);
	}
	
	public boolean getIsSuccess()
	{
		return Boolean.valueOf(get(N_IsSucess).toString());
	}
	
	public boolean getIsEnd()
	{
		return getStatus() == 3;
	}
	
	public boolean getIsCase()
	{
		return getType() == 0;
	}
	
	public int getStatus()
	{
		return Integer.valueOf(get(N_Status).toString());
	}
	
	public int getType()
	{
		return Integer.valueOf(get(N_Type).toString());
	}
	
	private String FormatLog(String msg)
	{
		return "<span style = \"width:100px;\">" + GetTimeNow() + ":</span><span>" + msg + "</span>";
	}
	
	private String GetTimeNow()
	{
//		yyyy-MM-dd 
		return DateTimeFormat.getFormat("hh:mm:ss SSS").format(new Date()).toString();
	}
	
	public GWTPack_Struct getCompareResult()
	{
		try
		{
			if(getIsCase())
				return (GWTPack_Struct)get(N_LogDetail);
		}
		catch (Exception e) {
		}
		return null;
	}
	
	public boolean SetLogDetail(boolean sucess,String errorMsg,ModelData log,boolean sync)
	{
		this.set(N_IsSucess, sucess);
		if(errorMsg != null && !errorMsg.trim().isEmpty())
			GetLogList().add(FormatLog(errorMsg));
		
		if(log != null)
		{
			this.set(N_LogDetail, log);
			
			if(log instanceof GWTScriptFlowLog)
			{
				GWTScriptFlowLog busiLog = (GWTScriptFlowLog)log;
				for(GWTScriptFlowLogDetail detail : busiLog.getLogDetail())
				{
					GetLogList().add(FormatLog(detail.getLogInfo()));
					if(!detail.getIsSucess())
					{
						this.set(N_IsSucess, false);
						this.set(N_Status, 3);
					}
				}
			}
		}
		
		if(sync)
		{
			if(sucess)
			{
				if(log == null)
				{
					GetLogList().add(FormatLog("执行完成,但无预期结果比对"));
					this.set(N_IsSucess, false);
				}
				else
					GetLogList().add(FormatLog("执行完成"));
			}
			this.set(N_Status, 3);
		}
		return getIsSuccess();
	}
	
	@SuppressWarnings("unchecked")
	public List<String> GetLogList()
	{
		return (List<String>)get(N_LogList);
	}
}
