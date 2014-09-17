package com.dc.tes.ui.client.model;

import java.io.Serializable;

import com.extjs.gxt.ui.client.data.BaseTreeModel;
import com.extjs.gxt.ui.client.data.ModelData;

/**
 * 队列任务实体类
 * @author scckobe
 *
 */
public class GWTQueueTask extends BaseTreeModel implements Serializable{
	private static final long serialVersionUID = -3484062022918958737L;
	/**
	 * 主键标识
	 */
	public static String N_ID = "id";
	/**
	 * 案例、业务流的外键标识
	 */
	public static String N_TaskID = "taskId";
	/**
	 * 关联TASKID类型:0关联案例;1关联业务流,-1交易
	 */
	public static String N_Type = "type";
	/**
	 * 自我执行次数
	 */
	public static String N_recount = "repCount";
	
	/**
	 * 案例、业务流的名称
	 */
	public static String N_TaskName = "taskName";
	/**
	 * 案例、业务流的描述
	 */
	public static String N_TaskDesc = "taskDesc";
	/**
	 * 关联TASKID类型（中文）:0关联案例;1关联业务流
	 */
	public static String N_TypeCHS = "typeCHS";
	/**
	 * 所属交易
	 */
	public static String N_ParentTran = "pTran";
	
	public static String N_No = "no";
	/**
	 * 日志开始行
	 */
	private int logBeginIndex = 0;
	/**
	 * 日志结束行
	 */
	private int logEndIndex = 0;
	/**
	 * 执行状态
	 */
	private int status = 0;
	
	/**
	 * 默认构造函数
	 */
	public GWTQueueTask()
	{
		this("",0,2);
	}
	
	/**
	 * 从数据库中获得的任务通过此构造函数实例化
	 * @param taskID	任务标识
	 * @param recount	重复次数
	 * @param type		任务类型
	 */
	public GWTQueueTask(String taskID,int recount,int type)
	{
		this(taskID,"","",recount,type);
	}

	/**
	 * 构造函数（完整）
	 * @param taskID	任务标识
	 * @param taskName	任务名称
	 * @param taskDesc	任务描述
	 * @param recount	重复次数
	 * @param type		任务类型
	 */
	private GWTQueueTask(String taskID,String taskName,String taskDesc,int recount,int type)
	{
		this.set(N_TaskID,taskID);
		this.set(N_recount, recount);
		this.set(N_Type, type);
		this.set(N_TypeCHS, type == 1 ? "业务流" : "案例数据");
		SetNameAndDesc(taskName,taskDesc);
	}
	
	public void setTaskID(String taskID) {
		this.set(N_TaskID, taskID);
	}
	
	/**
	 * 设置任务名称与描述
	 * @param taskName	名称
	 * @param taskDesc	描述
	 */
	public void SetNameAndDesc(String taskName,String taskDesc)
	{
		this.set(N_TaskName,taskName);
		this.set(N_TaskDesc, taskDesc);
	}
	
	public String getTaskDesc()
	{
		return (String)get(N_TaskDesc) + getTaskName();
	}
	
	/**
	 * 设置案例数据队列的对应交易信息
	 * @param pTran	交易信息
	 */
	public void setParentTran(GWTTransaction pTran)
	{
		if(isCase())
			set(N_ParentTran, pTran);
	}
	
	public void SetNo(String num){
		this.set(N_No, num);
	}
	
	public String GetNo(){
		return this.get(N_No);
	}
	
	/**
	 * 重新设置任务为未执行状态
	 */
	public void Start()
	{
		this.status = 0;
	}
	
	/**
	 * 开始执行任务
	 */
	public void Run()
	{
		this.status = 1;
	}
	
	/**
	 * 结束执行任务
	 */
	public void End()
	{
		this.status = 2;
	}
	
	/**
	 * 结束执行任务
	 */
	public void EndError()
	{
		this.status = 3;
	}
	
	public boolean IsError()
	{
		return getStatus() == 3;
	}
	
	/**
	 * 获得任务执行状态
	 * @return	任务执行状态
	 */
	public int getStatus()
	{
		return this.status;
	}
	
	/**
	 * 设置状态
	 * @param status 状态
	 */
	private void setStatus(int status)
	{
		this.status = status;
	}
	
	/**
	 * 设置日志开始行
	 * @param begin	行号
	 */
	public void setBeginIndex(int begin)
	{
		logBeginIndex = begin;
	}
	
	/**
	 * 设置日志结束行
	 * @param end	行号
	 */
	public void setEndIndex(int end)
	{
		logEndIndex = end;
	}
	
	/**
	 * 获得日志开始行
	 * @return	日志开始行
	 */
	public int getBeginIndex()
	{
		return logBeginIndex;
	}
	
	/**
	 * 获得日志结束行
	 * @return	日志结束行
	 */
	public int getEndIndex()
	{
		return logEndIndex;
	}
	
	/**
	 * 获得任务名称
	 * @return	任务名称
	 */
	public String getTaskName()
	{
		return (String)get(N_TaskName);
	}
	
	/**
	 * 获得案例任务对应的交易信息
	 * @return
	 */
	public GWTTransaction getParentTran()
	{
		return (GWTTransaction)get(N_ParentTran);
	}
	
	/**
	 * 获得任务标识
	 * @return
	 */
	public String getTaskID()
	{
		return (String)get(N_TaskID);
	}
	
	/**
	 * 获得任务对应交易ID
	 * @return	任务对应交易ID(业务流任务返回空)
	 */
	public String getTranID()
	{
		if(get(N_ParentTran) != null)
			return ((GWTTransaction)get(N_ParentTran)).getTranID();
		return null;
	}
	
	/**
	 * 获得循环次数
	 * @return	循环次数
	 */
	public int getRecCount()
	{
		return Integer.valueOf(get(N_recount).toString());
	}
	
	/**
	 * @return :0关联案例;1关联业务流，2文件夹
	 */
	public int getType()
	{
		return Integer.valueOf(get(N_Type).toString());
	}
	
	/**
	 * 是否是案例
	 * @return	是否是案例
	 */
	public boolean isCase()
	{
		return getType() == 0;
	}
	
	/**
	 * 是否是文件夹队列
	 * @return	是否是文件夹队列
	 */
	public boolean getIsFolder()
	{
		return getType() == 2;
	}
	
	@Override
	public String toString()
	{
		return getTaskID() + "_" + getType();
	}

	/**
	 * 创建文件夹（父）任务
	 * @param taskID	任务标识
	 * @param taskName	任务名称
	 * @return			队列任务
	 */
	public static GWTQueueTask CreateFolderTask(String taskID,String taskName)
	{
		return new GWTQueueTask(taskID,taskName,"",0,2);
	}
	
	/**
	 * 创建业务流任务
	 * @param flowID	业务流标识
	 * @param flowName	业务流名称
	 * @return			队列任务
	 */
	public static GWTQueueTask CreateBusiTask(String flowID,String flowName)
	{
		return new GWTQueueTask(flowID,flowName,"",1,2);
	}
	
	/**
	 * 添加案例
	 * @param caseID	案例标识
	 * @param caseName	案例名称
	 * @param caseDesc	案例描述
	 * @param pTran		所属交易
	 */
	public static GWTQueueTask CreateCaseTask(String caseID, String caseName,String caseDesc,GWTTransaction pTran) {
		GWTQueueTask caseTask = new GWTQueueTask(caseID, caseName,caseDesc, 1,0);
		caseTask.setParentTran(pTran);
		return caseTask;
	}
	
	/**
	 * 任务拷贝
	 * @param source	源队列任务
	 * @return			新队列任务
	 */
	public static GWTQueueTask Copy(ModelData source)
	{
		return Copy(source,false);
	}
	
	/**
	 * 任务拷贝
	 * @param source	源队列任务
	 * @param addChild	添加子任务
	 * @return			新队列任务
	 */
	public static GWTQueueTask Copy(ModelData source,boolean addChild)
	{
		GWTQueueTask sourceData = (GWTQueueTask)source;
		
		GWTQueueTask copyData = new GWTQueueTask();
		copyData.setBeginIndex(sourceData.getBeginIndex());
		copyData.setEndIndex(sourceData.getEndIndex());
		copyData.setStatus(sourceData.getStatus());
		
		for(String pro : source.getPropertyNames())
		{
			copyData.set(pro, source.get(pro));
		}
		//copyData.set(GWTQueueTask.N_TaskName, sourceData.getTaskDesc());
		return copyData;
	}
	/**
	 * 任务拷贝
	 * @param source	源队列任务
	 * @return			新队列任务
	 */
	public static ModelData CopyFromUC(ModelData modelData) {
		// TODO Auto-generated method stub
		GWTCaseFlow caseFlow = (GWTCaseFlow)modelData;
//		GWTQueueTask sourceData = CreateBusiTask(caseFlow.getScriptFlow().getID(),
//				caseFlow.GetName());
		GWTQueueTask sourceData = CreateBusiTask(caseFlow.GetID(),
		caseFlow.GetName());
		sourceData.SetNo(caseFlow.GetCaseFlowNo());
		sourceData.set(N_TaskDesc, caseFlow.GetDesc());
		return Copy(sourceData, false);
	}
}
