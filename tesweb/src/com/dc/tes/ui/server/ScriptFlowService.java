package com.dc.tes.ui.server;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.dc.tes.data.DALFactory;
import com.dc.tes.data.IDAL;
import com.dc.tes.data.model.Case;
import com.dc.tes.data.model.ScriptFlow;
import com.dc.tes.data.model.CaseFlow;
import com.dc.tes.data.model.ExecuteLog;
import com.dc.tes.data.model.SysType;
import com.dc.tes.data.model.Transaction;
import com.dc.tes.data.model.User;
import com.dc.tes.data.op.Op;
import com.dc.tes.monitor.data.Context;
import com.dc.tes.ui.client.IScriptFlowService;
import com.dc.tes.ui.client.model.GWTCaseFlow;
import com.dc.tes.ui.client.model.GWTScriptFlow;
import com.dc.tes.ui.client.model.GWTScriptFlowLog;
import com.dc.tes.ui.client.model.GWTSimuSystem;
import com.dc.tes.ui.client.model.PageStartEnd;
import com.extjs.gxt.ui.client.data.BasePagingLoadResult;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;


public class ScriptFlowService extends RemoteServiceServlet implements IScriptFlowService {
	private static final long serialVersionUID = -7336608153755445918L;
	private static final Log log = LogFactory.getLog(ScriptFlowService.class);
	
	private IDAL<ScriptFlow> scriptFlowDAL = DALFactory.GetBeanDAL(ScriptFlow.class);

	public static GWTScriptFlow BeanToModel(ScriptFlow scriptFlow,boolean setScript) {
		if (scriptFlow == null)
			return null;
		
		GWTScriptFlow gwtScriptFlow = new GWTScriptFlow(scriptFlow.getId(),scriptFlow.getName(),
				scriptFlow.getDescription(),scriptFlow.getSystemid());
		gwtScriptFlow.setIsSet(scriptFlow.getSrcipt());
		if(setScript)
			gwtScriptFlow.setScript(scriptFlow.getSrcipt());
		return gwtScriptFlow;
	}

	private static ScriptFlow ModelToBean(ScriptFlow scriptFlowBean, GWTScriptFlow gwtScriptFlow) {
		if (gwtScriptFlow == null)
			return null;
		ScriptFlow scriptFlow = scriptFlowBean;
		if (scriptFlow == null) {
			scriptFlow = new ScriptFlow();
			scriptFlow.setSrcipt("");
		}

		scriptFlow.setDescription(gwtScriptFlow.getDesc());
		scriptFlow.setSystemid(gwtScriptFlow.getSystemID());
		scriptFlow.setName(gwtScriptFlow.getName());
		
		if (!gwtScriptFlow.IsNew())
			scriptFlow.setId(gwtScriptFlow.getID());
		
		return scriptFlow;
	}
	
	@Override
	public void DeleteScriptFlow(List<GWTScriptFlow> scriptFlowInfoList) {
		try {
			for (int i = 0; i < scriptFlowInfoList.size(); i++) {
				scriptFlowDAL.Del(ModelToBean(null, scriptFlowInfoList.get(i)));
				new QueueService().DeleteBusiTask(scriptFlowInfoList.get(i).getID());
			}
		} catch (Exception ex) {
			log.error(ex, ex);
			throw new RuntimeException(ex);
		}
	}
	@Override
	public PagingLoadResult<GWTScriptFlow> GetList(String sysId,
			String searchKey, PagingLoadConfig config) {
		try {
			Op[] conditions = new Op[] {
					Op.EQ(GWTScriptFlow.N_SystemID, sysId)};

			int count;
			List<ScriptFlow> lst;
			if (searchKey.isEmpty()) {
				count = scriptFlowDAL.Count(conditions);
				PageStartEnd pse = new PageStartEnd(config, count);
				lst = scriptFlowDAL.List(pse.getStart(), pse.getEnd(), conditions);
			} else {
				String[] properties = {
						GWTScriptFlow.N_Name,
						GWTScriptFlow.N_Desc };
				count = scriptFlowDAL.MatchCount(searchKey, properties, conditions);
				PageStartEnd pse = new PageStartEnd(config, count);
				lst = scriptFlowDAL.Match(searchKey, properties, pse.getStart(), pse.getEnd(), conditions);
			}

			List<GWTScriptFlow> returnList = new ArrayList<GWTScriptFlow>();
			for (ScriptFlow scriptFlow : lst) {
				returnList.add(BeanToModel(scriptFlow,false));
			}

			return new BasePagingLoadResult<GWTScriptFlow>(returnList, config.getOffset(), count);
		} catch (Exception ex) {
			log.error(ex, ex);
			throw new RuntimeException(ex);
		}
	}
	
	public List<ScriptFlow> GetList(String sysId) {
		try
		{
			return scriptFlowDAL.ListAll(Op.EQ(GWTScriptFlow.N_SystemID, sysId));
		}
		catch (Exception e) {
		}
		return null;
	}
	public List<ScriptFlow> GetList(String sysId, String batchNo) {
		try
		{
			return scriptFlowDAL.ListAll(Op.EQ(GWTScriptFlow.N_SystemID, sysId),Op.LIKE(GWTScriptFlow.N_Name, batchNo));
		}
		catch (Exception e) {
		}
		return null;
	}
	
	@Override
	public String GetScript(String id) {
		ScriptFlow flow = GetSingle(id);
		if(flow != null)
			return flow.getSrcipt();
		return "";
	}
	
	@Override
	public boolean SaveScriptFlow(GWTScriptFlow gwtScriptFlow) {
		try {
			ScriptFlow scriptFlow = scriptFlowDAL.Get(
					new HelperService().GetDistinctOpArray(gwtScriptFlow, gwtScriptFlow.getName()));
			if (scriptFlow != null && scriptFlow.getId().compareTo(gwtScriptFlow.getID()) != 0)
				return false;
			
			if (gwtScriptFlow.IsNew())
			{
				scriptFlow = ModelToBean(null, gwtScriptFlow);
				scriptFlowDAL.Add(scriptFlow);
			}
			else
			{
				if(scriptFlow == null || (scriptFlow != null && scriptFlow.getId() != gwtScriptFlow.getID()))
					scriptFlow = GetSingle(gwtScriptFlow.getID());
				scriptFlow = ModelToBean(scriptFlow, gwtScriptFlow);
				scriptFlowDAL.Edit(scriptFlow);
			}

			return true;
		} catch (Exception ex) {
			log.error(ex, ex);
			throw new RuntimeException(ex);
		}
	}
	
	@Override
	public void UpdateScript(String id, String Script) {
		try
		{
			ScriptFlow flow = GetSingle(id);
			if(flow != null)
			{
				flow.setSrcipt(Script);
				scriptFlowDAL.Edit(flow);
			}
			else
			{
				throw new Exception("业务流已被删除");
			}
		}catch (Exception ex) {
			log.error(ex, ex);
			throw new RuntimeException(ex);
		}
	}
	
	/**
	 * 获得业务流基本信息
	 * @param id	业务流标识
	 * @return			业务流基本信息
	 */
	public ScriptFlow GetSingle(String id)
	{
		try {
			return scriptFlowDAL.Get(Op.EQ(GWTScriptFlow.N_ID, id));
		} catch (Exception ex) {
			log.error(ex, ex);
			throw new RuntimeException(ex);
		}
	}

	@Override
	public void ExecScript(GWTSimuSystem sysInfo,String logID, String flowid,
			String Script, String executeLogId) {
		new HelperService().RunFlow(sysInfo, logID, flowid, Script, executeLogId);
	}
	
	@Override
	public boolean ExecScript(GWTSimuSystem sysInfo,String logID, String flowid, String executeLogId) {
		String script = GetScript(flowid);
		if(script == null || script.isEmpty())
			return false;
		ExecScript(sysInfo,logID, flowid, script, executeLogId);
		return true;
	}

	@Override
	public GWTScriptFlowLog GetExecLog(String logID, String flowid) {
//		GWTScriptFlowLog logInfo = new GWTScriptFlowLog();
//		logInfo.MockInfo();
//		
//		return logInfo;
		return Context.getFlowLog(flowid,logID);
	}
	
	public String Insert2DataBase(GWTScriptFlow scriptFlow, String userName) {

		IDAL<ExecuteLog> executeLogDAL = DALFactory.GetBeanDAL(ExecuteLog.class);
		ExecuteLog log = new ExecuteLog();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = new Date();
		String now = sdf.format(date);
		log.setCreateTime(now);
		log.setBeginRunTime(date);
		log.setDescription(scriptFlow.getDesc());
		log.setType(1);
		log.setSystemId(Integer.parseInt(scriptFlow.getSystemID()));
		if(userName.equals("Administrator"))
			log.setUserId(0);
		else {
			String userId = DALFactory.GetBeanDAL(User.class).Get(Op.EQ("id", userName)).getId();
			log.setUserId(Integer.parseInt(userId));
		}
		executeLogDAL.Add(log);
		
		SysType sysType = DALFactory.GetBeanDAL(SysType.class).Get(Op.EQ("systemId", scriptFlow.getSystemID()));		
		int iTransactionTimeOut = sysType.getTransactionTimeOut();
		return String.valueOf(log.getId()) + ";"+ iTransactionTimeOut;
	}

	@Override
	public boolean ExecScript(GWTSimuSystem sysInfo, String logID,
			String flowid, String executeLogId, String userId,  boolean byApi) {
		// TODO Auto-generated method stub
		String script = GetScript(flowid);
		//userId = userId + executeLogId;
		CaseFlow caseFlow = DALFactory.GetBeanDAL(CaseFlow.class).Get(Op.EQ("scriptFlowId", flowid));
		if(script == null || script.isEmpty() || caseFlow.getDisabledFlag()==1)
			return false;
		new HelperService().RunFlow(sysInfo, logID, flowid, script, executeLogId, userId, byApi);
		return true;
	}


	@Override
	public boolean ExecCaseFlow(GWTSimuSystem sysInfo, String flowID,
			String logID) {
		// TODO Auto-generated method stub
		new HelperService().RunCaseFlow(sysInfo, flowID, logID);
		return true;
	}

	@Override
	public String GetExecIDAndDelayTime(GWTCaseFlow caseFlow, String userName) {
		// TODO Auto-generated method stub
		IDAL<ExecuteLog> executeLogDAL = DALFactory.GetBeanDAL(ExecuteLog.class);
		ExecuteLog log = new ExecuteLog();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = new Date();
		String now = sdf.format(date);
		log.setCreateTime(now);
		log.setBeginRunTime(date);
		log.setDescription(caseFlow.GetDesc());
		log.setType(1);
		log.setSystemId(Integer.parseInt(caseFlow.GetSystemID()));
		if(userName.equals("Administrator"))
			log.setUserId(0);
		else {
			String userId = DALFactory.GetBeanDAL(User.class).Get(Op.EQ("id", userName)).getId();
			log.setUserId(Integer.parseInt(userId));
		}
		executeLogDAL.Add(log);

		String delayTime = getFlowTransactionTimeOut(caseFlow.GetSystemID(),caseFlow.GetID());
		
		return String.valueOf(log.getId()) + ";"+ delayTime;
			
	}
	
	@Override
	public int GetDelayTime(String systemID, String casFlowID) {
		// TODO Auto-generated method stub
	
		String timeout = getFlowTransactionTimeOut(systemID, casFlowID);
		
		return Integer.parseInt(timeout);
	}
	
	//获取业务流超时
	private String getFlowTransactionTimeOut(String sysID, String caseFlowID) {
		SysType sysType = DALFactory.GetBeanDAL(SysType.class).Get(Op.EQ("systemId", sysID));
		
		int iTransactionTimeOut = sysType.getTransactionTimeOut();
		IDAL<CaseFlow> cfDAL = DALFactory.GetBeanDAL(CaseFlow.class);
		CaseFlow caseFlow = cfDAL.Get(Op.EQ("id", Integer.parseInt(caseFlowID))); 
		
		int iTotalSqlDelayTime = 0;
		int iFlowTimeOut = 0; 
				
		IDAL<Case> caseDAL = DALFactory.GetBeanDAL(Case.class);
		List<Case> caseList = caseDAL.ListAll(Op.EQ("caseFlow", caseFlow));
		for (int i=0; i<caseList.size(); i++) {
			Case c = caseList.get(i);
			IDAL<Transaction> transDAL = DALFactory.GetBeanDAL(Transaction.class);
			Transaction trans = transDAL.Get(Op.EQ("transactionId", c.getTransactionId()));
			iTotalSqlDelayTime += trans.getSqlDelayTime();
		}
		
		iFlowTimeOut = caseList.size() * iTransactionTimeOut;
		
		if (1 == sysType.getNeedSqlCheck()) {
			iFlowTimeOut += iTotalSqlDelayTime;
		}
		
		return String.valueOf(iFlowTimeOut*1000);
	}


}
