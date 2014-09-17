package com.dc.tes.ui.server;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.dc.tes.data.DALFactory;
import com.dc.tes.data.IDAL;
import com.dc.tes.data.model.ScriptFlow;
import com.dc.tes.data.model.Case;
import com.dc.tes.data.model.CaseFlow;
import com.dc.tes.data.model.ExecuteLog;
import com.dc.tes.data.model.ExecuteSet;
import com.dc.tes.data.model.ExecuteSetTaskItem;
import com.dc.tes.data.model.Transaction;
import com.dc.tes.data.op.Op;
import com.dc.tes.ui.client.IQueueService;
import com.dc.tes.ui.client.enums.IDUType;
import com.dc.tes.ui.client.enums.OpType;
import com.dc.tes.ui.client.model.GWTScriptFlow;
import com.dc.tes.ui.client.model.GWTCase;
import com.dc.tes.ui.client.model.GWTQueue;
import com.dc.tes.ui.client.model.GWTQueueTask;
import com.dc.tes.ui.client.model.GWTTransaction;
import com.dc.tes.ui.client.model.PageStartEnd;
import com.extjs.gxt.ui.client.data.BasePagingLoadResult;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class QueueService extends RemoteServiceServlet implements IQueueService{
	private static final long serialVersionUID = 6036858504201001403L;
	private static final Log log = LogFactory.getLog(QueueService.class);
	
	private IDAL<ExecuteSet> executeSetDAL = DALFactory.GetBeanDAL(ExecuteSet.class);
	private IDAL<ExecuteSetTaskItem> executeSetTaskItemDAL = DALFactory.GetBeanDAL(ExecuteSetTaskItem.class);
	private IDAL<CaseFlow> caseflowIdal = DALFactory.GetBeanDAL(CaseFlow.class);
	
	protected static GWTQueue BeanToModel(ExecuteSet executeSet) {
		if (executeSet == null)
			return null;
		
		GWTQueue gwtQueueList = new GWTQueue(executeSet.getId(),executeSet.getName(),
				executeSet.getDescription(),executeSet.getSystemId());
		return gwtQueueList;
	}

	protected static ExecuteSet ModelToBean(ExecuteSet queueBean, GWTQueue gwtQueue) {
		if (gwtQueue == null)
			return null;
		ExecuteSet queue = queueBean;
		if (queue == null) {
			queue = new ExecuteSet();
		}

		queue.setDescription(gwtQueue.getDesc());
		queue.setSystemId(gwtQueue.getSystemID());
		queue.setName(gwtQueue.getName());
		
		if (!gwtQueue.IsNew())
			queue.setId(gwtQueue.getID());
		
		return queue;
	}
	
	@Override
	public void DeleteQueue(List<GWTQueue> queueList, Integer loginLogId) {
		try {
			for (int i = 0; i < queueList.size(); i++) {
				ExecuteSet executeSet = ModelToBean(null, queueList.get(i));
				OperationLogService.writeOperationLog(OpType.ExecuteSet, IDUType.Delete, 
						Integer.parseInt(executeSet.getId()), executeSet.getName(),
						"executeSetName", executeSet.getName(), null, loginLogId);
				executeSetDAL.Del(executeSet);
			}
		} catch (Exception ex) {
			log.error(ex, ex);
			throw new RuntimeException(ex);
		}
	}

	@Override
	public PagingLoadResult<GWTQueue> GetList(String sysId, String searchKey,
			PagingLoadConfig config) {
		try {
			Op[] conditions = new Op[] {
					Op.EQ(GWTQueue.N_SystemID, sysId)};

			int count;
			List<ExecuteSet> lst;
			if (searchKey.isEmpty()) {
				count = executeSetDAL.Count(conditions);
				PageStartEnd pse = new PageStartEnd(config, count);
				lst = executeSetDAL.List(pse.getStart(), pse.getEnd(), conditions);
			} else {
				String[] properties = {
						GWTQueue.N_Name,
						GWTQueue.N_Desc };
				count = executeSetDAL.MatchCount(searchKey, properties, conditions);
				PageStartEnd pse = new PageStartEnd(config, count);
				lst = executeSetDAL.Match(searchKey, properties, pse.getStart(), pse.getEnd(), conditions);
			}

			List<GWTQueue> returnList = new ArrayList<GWTQueue>();
			for (ExecuteSet busiFlow : lst) {
				returnList.add(BeanToModel(busiFlow));
			}

			return new BasePagingLoadResult<GWTQueue>(returnList, config.getOffset(), count);
		} catch (Exception ex) {
			log.error(ex, ex);
			throw new RuntimeException(ex);
		}
	}

	@Override
	public List<GWTQueue> GetList(String sysId, String searchKey) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean SaveQueue(GWTQueue gwtQueue, Integer loginLogId) {
		try {
			
			ExecuteSet queue = executeSetDAL.Get(
					new HelperService().GetDistinctOpArray(gwtQueue, gwtQueue.getName()));
			if (queue != null && queue.getId().compareTo(gwtQueue.getID()) != 0)
				return false;
			String userId = OperationLogService.getLoginLogById(loginLogId).getUserId();
			if (gwtQueue.IsNew())
			{
				queue = ModelToBean(null, gwtQueue);
				queue.setCreatedTime(new Date());
				queue.setCreatedUserId(userId);
				executeSetDAL.Add(queue);
				OperationLogService.writeOperationLog(OpType.ExecuteSet, IDUType.Insert, 
						Integer.parseInt(queue.getId()), queue.getName(),
						"executeSetName", null, queue.getName(), loginLogId);
				
			}
			else
			{
				ExecuteSet oldBean = null;
				if(queue == null || (queue != null && queue.getId() != gwtQueue.getID()))
					oldBean = GetSingle(gwtQueue.getID());
				queue = ModelToBean(oldBean, gwtQueue);
				queue.setLastModifiedTime(new Date());
				queue.setLastModifiedUserId(OperationLogService.getLoginLogById(loginLogId).getUserId());
				executeSetDAL.Edit(queue);
				OperationLogService.writeUpdateOperationLog(OpType.ExecuteSet, ExecuteSet.class,
						Integer.parseInt(queue.getId()), oldBean.getName(), oldBean, queue, loginLogId);
			}

			return true;
		} catch (Exception ex) {
			log.error(ex, ex);
			throw new RuntimeException(ex);
		}
	}
	
	/**
	 * 获得任务队列基本信息
	 * @param id		任务队列标识
	 * @return			任务队列基本信息
	 */
	public ExecuteSet GetSingle(String id)
	{
		try {
			return executeSetDAL.Get(Op.EQ(GWTQueue.N_ID, id));
		} catch (Exception ex) {
			log.error(ex, ex);
			throw new RuntimeException(ex);
		}
	}

	@Override
	public GWTQueueTask GetQueueTask(String queueID) {
		try
		{
			IDAL<Case> caseDAL = DALFactory.GetBeanDAL(Case.class);
			IDAL<ScriptFlow> busiFlowDAL = DALFactory.GetBeanDAL(ScriptFlow.class);
			Set<ExecuteSetTaskItem> taskList = GetExecuteSetTaskItemList(queueID);
			GWTQueueTask gwtTaskList = GWTQueueTask.CreateFolderTask("","");
			for(ExecuteSetTaskItem task : taskList){
				GWTQueueTask queueTask = new GWTQueueTask("",task.getRepCount(),task.getType());
				
				if(task.getType()==0){				
					Case casebean = caseDAL.Get(Op.EQ(GWTCase.N_caseId, task.getTaskId()));
					if(casebean!=null){
						queueTask.SetNameAndDesc(casebean.getCaseName(), casebean.getDescription());
						queueTask.SetNo(casebean.getCaseNo());
					} else {
						queueTask.SetNameAndDesc("*****", "");
						queueTask.SetNo("");
					}
					
				}else if(task.getType()==1){
					ScriptFlow scriptFlow = busiFlowDAL.Get(Op.EQ(GWTScriptFlow.N_ID, task.getTaskId()));
					if(scriptFlow!=null){

						CaseFlow caseFlow = caseflowIdal.Get(Op.EQ("scriptFlowId", scriptFlow.getId()));
						if(caseFlow != null) {
							queueTask.set(GWTQueueTask.N_Type, 2);
							queueTask.setTaskID(caseFlow.getId().toString());
							queueTask.SetNameAndDesc(caseFlow.getCaseFlowName(), caseFlow.getDescription());
							queueTask.SetNo(caseFlow.getCaseFlowNo());
						} else {
							queueTask.SetNameAndDesc(scriptFlow.getDescription(), scriptFlow.getName());
							queueTask.SetNo("");
						}
					}
				}else if(task.getType()==2){					
					CaseFlow caseFlow = caseflowIdal.Get(Op.EQ("id", Integer.parseInt(task.getTaskId())));
					if(caseFlow != null) {
						queueTask.setTaskID(caseFlow.getId().toString());
						queueTask.SetNameAndDesc(caseFlow.getCaseFlowName(), caseFlow.getDescription());
						queueTask.SetNo(caseFlow.getCaseFlowNo());
					} else {
						queueTask.SetNameAndDesc("*****", "");
						queueTask.SetNo("");
					}

				}
				gwtTaskList.add(queueTask);
			}
			return gwtTaskList;
		}
		catch (Exception ex) {
			log.error(ex, ex);
			throw new RuntimeException(ex);
		}
	}

	@Override
	public void SetQueueTask(String queueID, List<ModelData> taskList, Integer loginLogId) {
		ExecuteSet queue = GetSingle(queueID);
		try
		{
			Set<ExecuteSetTaskItem> delList = GetExecuteSetTaskItemList(queueID);
			for(ExecuteSetTaskItem task : delList)
			{
				executeSetTaskItemDAL.Del(task);
			}
			
			for(ModelData lst : taskList)
			{
				GWTQueueTask gwtTask = (GWTQueueTask)lst;
				ExecuteSetTaskItem task = new ExecuteSetTaskItem();
				task.setExecuteSet(queue);	
				CaseFlow caseFlow = caseflowIdal.Get(Op.EQ("id",Integer.parseInt(gwtTask.getTaskID())));				
				task.setTaskId(caseFlow.getId().toString());
				task.setRepCount(gwtTask.getRecCount());
				task.setType(gwtTask.getType());
				task.setTransactionId(gwtTask.getTranID());
				
				executeSetTaskItemDAL.Add(task);
			}
			OperationLogService.writeOperationLog(OpType.ExecuteSetItem, IDUType.Update, 
					Integer.parseInt(queueID), queue.getName(),
					"executeSetItems", queue.getName(), "修改执行集队列", loginLogId);
			queue.setLastModifiedTime(new Date());
			queue.setLastModifiedUserId(OperationLogService.getLoginLogById(loginLogId).getUserId());
			executeSetDAL.Edit(queue);
		}
		catch (Exception ex) {
			log.error(ex, ex);
			throw new RuntimeException(ex);
		}
	}
	
	public Set<ExecuteSetTaskItem> GetExecuteSetTaskItemList(String queueID) throws RuntimeException
	{
		try
		{
			return GetSingle(queueID).getTaskItems();
		}
		catch (Exception ex) {
			log.error(ex, ex);
			throw new RuntimeException(ex);
		}
	}
	
	public GWTQueueTask GetGWTQueueTaskList(String sysID,String queueID) throws RuntimeException
	{
		try
		{
			GWTQueueTask taskCollection = GWTQueueTask.CreateFolderTask("","");
			
			Map<String,String> nameMap = new HashMap<String, String>();
			
			//获得业务流信息     加限制，根据批次号
			List<ScriptFlow> sfList;
			if(GetSingle(queueID).getImportBatchNo()==null){
				sfList = new ScriptFlowService().GetList(sysID);
			}else
			 	sfList = new ScriptFlowService().GetList(sysID, GetSingle(queueID).getImportBatchNo());
			GWTQueueTask busiTask = GWTQueueTask.CreateFolderTask("-1","业务流");
			for(ScriptFlow busiflow : sfList)
			{
				GWTQueueTask task = GWTQueueTask.CreateBusiTask(busiflow.getId(),busiflow.getName());
				nameMap.put(task.toString(), busiflow.getName());
				busiTask.add(task);
			}
			taskCollection.add(busiTask);
			
			//获得交易信息
			GWTQueueTask caseTask = GWTQueueTask.CreateFolderTask("","");
			Map<String,GWTTransaction> tranMap = new HashMap<String, GWTTransaction>();
			List<Transaction> tranList = new TransactionService().GetTransDAO(sysID, 0);
			for(Transaction tran : tranList)
			{
				List<Case> caseList;
				if(GetSingle(queueID).getImportBatchNo()==null){
					caseList = new CaseService().GetCaseListByTranID(tran.getTransactionId());
				}else
				//List<Case> caseList = new CaseService().GetCaseListByTranID(tran.getTransactionId());
					caseList = new CaseService().GetCaseListByTranIDAndBatchNo(tran.getTransactionId(), GetSingle(queueID).getImportBatchNo());
				if(caseList.size() == 0)
					continue;
				
				GWTQueueTask tranTask = GWTQueueTask.CreateFolderTask(tran.getTransactionId(),
						tran.getTranCode() + "[" + tran.getTranName() + "]");
				GWTTransaction gwtTran = new GWTTransaction();
				gwtTran.SetEditValue(tran.getTranCode(), "", "", tran.getChannel(),tran.getTransactionId());
				
				for(Case caseInfo : caseList)
				{
					//修改设置任务名称为案例编号加案例名称
					GWTQueueTask task = GWTQueueTask.CreateCaseTask(caseInfo.getCaseId(),caseInfo.getCaseNo()+"_"+caseInfo.getCaseName(), tran.getTranCode() + "--",gwtTran);
					nameMap.put(task.toString(),task.getTaskDesc());
					tranMap.put(caseInfo.getCaseId(), gwtTran);
					tranTask.add(task);
				}
				caseTask.add(tranTask);
			}
			taskCollection.add(caseTask);
			
			//获得任务信息
			Set<ExecuteSetTaskItem> setList = GetSingle(queueID).getTaskItems();
			GWTQueueTask listTask = new GWTQueueTask();
			for(ExecuteSetTaskItem task : setList)
			{
				GWTQueueTask gwtTask = new GWTQueueTask(task.getTaskId(),task.getRepCount(),task.getType());
				//填充名称
				String key = gwtTask.toString();
				if(nameMap.containsKey(key))
					gwtTask.SetNameAndDesc(nameMap.get(key), "");
				else
					gwtTask.SetNameAndDesc("*****", "");
				
				//设置交易信息
				if(gwtTask.isCase())
					gwtTask.setParentTran(tranMap.get(task.getTaskId()));
				
				listTask.add(gwtTask);
//				listTask.insert(gwtTask, 0);
//				childList.add(0, gwtTask);
			}
			taskCollection.add(listTask);
			
			return taskCollection;
		}
		catch (Exception ex) {
			log.error(ex, ex);
			throw new RuntimeException(ex);
		}
	}

	public void DeleteCaseTask(String caseID)
	{
		DeleteTask(GWTQueueTask.N_TaskID,caseID, 0);
	}
	
	public void DeleteTranTask(String tranID)
	{
		DeleteTask("transactionId",tranID, 0);
	}
	
	public void DeleteBusiTask(String flowID)
	{
		DeleteTask(GWTQueueTask.N_TaskID,flowID, 1);
	}
	
	private void DeleteTask(String name,String taskID,int type)
	{
		try
		{
			List<ExecuteSetTaskItem> delList = executeSetTaskItemDAL.ListAll(Op.EQ(name, taskID),
					Op.EQ(GWTQueueTask.N_Type, type));
			
			for(ExecuteSetTaskItem task : delList)
			{
				executeSetTaskItemDAL.Del(task);
			}
		}
		catch (Exception ex) {
			log.error(ex, ex);
			throw new RuntimeException(ex);
		}
	}
	
	

	@Override
	public String Insert2DataBase(String queueID, Integer executeSetID,
			String userID, String execBN, Integer roundId) {
		// TODO Auto-generated method stub
		ExecuteSet queue = DALFactory.GetBeanDAL(ExecuteSet.class).Get(Op.EQ("id", queueID));
		IDAL<ExecuteLog> executeLogDAL = DALFactory.GetBeanDAL(ExecuteLog.class);
		ExecuteLog log = new ExecuteLog();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String now = sdf.format(new Date());
		log.setCreateTime(now);
		log.setExecuteSetId(Integer.parseInt(queueID));
		log.setDescription(queue.getDescription());
		log.setExecuteBatchNo(execBN);
		log.setSystemId(Integer.parseInt(queue.getSystemId()));
		log.setBeginRunTime(new Date());
		log.setPassFlag(3);
		log.setType(0);
		log.setRoundId(roundId);
		log.setExecuteSetDirId(executeSetID);
		log.setExecuteSetName(queue.getName());
		if(userID.equals("Administrator"))
			log.setUserId(0);
		else {
			log.setUserId(Integer.parseInt(userID));
		}
		executeLogDAL.Add(log);
		return String.valueOf(log.getId());
	}
	
	public String Insert2DataBase(String queueID, String userId, String execBN) {
		return Insert2DataBase(queueID, null, userId, execBN, null);
	}

	@Override
	public void updateExecuteLogForSuspendRun(String execBN) {
		// TODO Auto-generated method stub
		IDAL<ExecuteLog> executeLogDAL = DALFactory.GetBeanDAL(ExecuteLog.class);
		ExecuteLog log = executeLogDAL.Get(Op.EQ("executeBatchNo", execBN));
		if(log != null){
			log.setEndRunTime(new Date());
			log.setPassFlag(6);
			long iRunSeconds = (log.getEndRunTime().getTime() - 
					log.getBeginRunTime().getTime()) / 1000;// 除以1000是为了转换成秒
				 
		    if (iRunSeconds >= 0) {
		    	log.setRunDuration(FormatDuration2HHMMSS(iRunSeconds));
		    }
		    executeLogDAL.Edit(log);
				    
		}
	}
	
	public static String FormatDuration2HHMMSS(long iRunSeconds) {
		   
		  long hour = (iRunSeconds / 3600);
		  long min = (iRunSeconds / 60 - hour * 60);
		  long sec = (iRunSeconds - hour * 3600 - min * 60);
		 
		  String strDuration = "";
		  
		  if (hour > 0) {
		   strDuration = hour + "小时" + min + "分" + sec + "秒";
		  } else if (min > 0) {
		   strDuration = min + "分" + sec + "秒";
		  } else {
		   strDuration = sec + "秒";
		  }
		  
		  return strDuration;
		 }



}
