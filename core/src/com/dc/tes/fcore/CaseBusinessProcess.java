package com.dc.tes.fcore;


import java.nio.charset.Charset;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.dc.tes.InMessage;
import com.dc.tes.OutMessage;
import com.dc.tes.data.DALFactory;
import com.dc.tes.data.IDAL;
import com.dc.tes.data.op.Op;
import com.dc.tes.dom.MsgDocument;
import com.dc.tes.dom.util.MsgLoader;
import com.dc.tes.fcore.compare.CompareResult;
import com.dc.tes.fcore.compare.CompareService;
import com.dc.tes.util.RuntimeUtils;
import com.dc.tes.data.model.ScriptFlow;
import com.dc.tes.data.model.Case;
import com.dc.tes.data.model.CaseFlow;
import com.dc.tes.data.model.CaseFlowInstance;
import com.dc.tes.data.model.CaseInstance;
import com.dc.tes.data.model.CaseParameterExpectedValue;
import com.dc.tes.data.model.ExecuteLog;
import com.dc.tes.data.model.ExecuteSet;
import com.dc.tes.data.model.ExecuteSetTaskItem;
import com.dc.tes.data.model.Transaction;



/**
 * 模拟器功能核心业务处理模块
 * 
 * @author Huangzx
 * 
 */
public class CaseBusinessProcess {
	
	private static final Log log = LogFactory.getLog(FCore.class);
	
	public static FCore m_core;
	
	private static int m_iSystemMaxTimeOutSet4AllTransactions = -1;
	
	public static IDAL<CaseInstance> ciDAL = DALFactory.GetBeanDAL(CaseInstance.class);
	
	public static ThreadLocal<Boolean> m_isMsgItemCompareOk = new ThreadLocal<Boolean>();

	//处理案例的参数和业务流
	public static void processCaseBusiness(InMessage inMsg, CaseInstance ci) throws Exception {

		//获取应答报文的编码方式
		String encoding = "utf-8";
		if (DbGet.m_sysType.getEncoding4ResponseMsg() != null && !DbGet.m_sysType.getEncoding4ResponseMsg().isEmpty()) {
			encoding = DbGet.m_sysType.getEncoding4ResponseMsg();
		}
		
		String strResponseMsg = new String(inMsg.bin, encoding);

		//非xml文本，以16进制方式打印，兼容不可见字符
		if(!strResponseMsg.startsWith("<?xml")) {
			if(!strResponseMsg.contains("<?xml"))
				strResponseMsg = RuntimeUtils.PrintHex(inMsg.bin, Charset.forName(encoding));
		}
		
		//写入收到的应答报文
		ci.setResponseMsg(strResponseMsg);
		ci.setReceivedReplayFlag(1);
		if (inMsg.data != null) {
			ci.setResponseXml(inMsg.data.toString());
		}
		else {
			System.out.println("[警告]应答报文为空！");
		}
		ciDAL.Edit(ci);
		
		log.debug("案例["+ci.getCaseName()+ci.getCaseNo()+"]的返回报文: "+strResponseMsg);
		
		m_core.FlowLog(inMsg, "------开始预期值比对");
		
		CompareResult cr  = null;
		//比较预期结果
		String expect = ci.getExpectedXml();
		if (!StringUtils.isEmpty(expect)) {
			// 进行结果比对
			MsgDocument expectDoc = MsgLoader.LoadXml(expect);
			cr = CompareService.CompareDocument(expectDoc, inMsg.data);
			log.info("预期结果比对结果：" + cr);
		}
		
		CaseBusinessProcess.m_isMsgItemCompareOk.set(true); 
		if (cr != null && cr.getDifference() > 0) {
			CaseBusinessProcess.m_isMsgItemCompareOk.set(false);
		}
		
		ParameterProcess.putTransactionFixedData(ci);
		CaseFlowInstance cfi = ci.getCaseFlowInstance();
		if (cfi != null) {
			ParameterProcess.putBusiFlowFixedData(cfi);
			ParameterProcess.m_isCaseInFlow.set(true);
		} else {
			ParameterProcess.m_isCaseInFlow.set(false);
		}

		//先预设为通过，遇到任何异常条件则改为失败
		ParameterProcess.m_iCasePassFlag.set(1);
		
		Map<String, CaseParameterExpectedValue> caseExpectedParamValueList = null;
		caseExpectedParamValueList = DbGet.getCaseFieldExpectedParamValue(ci.getCaseId().toString());
		
		//插入报文字段表
		try	{ //返回报文中的字段（做过参数化的）
			ParameterProcess.insertInstanceFieldParams2(inMsg.data, ci, ciDAL, caseExpectedParamValueList);
		}
		catch (Exception e)	{
			log.error("插入报文字段值错误！" + e);
		}	
		
		Case icase = DatabaseDAL.GetCase(ci.getCaseId());
		Transaction tran = DatabaseDAL.GetTransactionByCase(icase);
		
		ParameterProcess.putCaseParamInRequestMsgPacket(tran, ci);
		ParameterProcess.putTransactionFixedData(tran, icase);
		
		try {
			if (1 == DbGet.m_sysType.getNeedSqlCheck())	{
				//处理交易参数
				ParameterProcess.processCaseParameters(ci, tran, caseExpectedParamValueList);
			}
			ParameterProcess.compareExpectedParameters(ci, caseExpectedParamValueList);
		}
		catch(Exception e) {
			System.out.print("处理案例的业务参数出错，错误提示信息：");
			System.out.println(e.getMessage());
		}
		
		//案例执行成功与否
		if (m_isMsgItemCompareOk.get() && 1 == ParameterProcess.m_iCasePassFlag.get()) { 
			ci.setCasePassFlag(1);
		}
		else {
			ci.setCasePassFlag(0);
		}
		ci.setEndRunTime(new Date());
		ciDAL.Edit(ci); 
		
		//静态案例的通过与否标志也要进行设置
		//静态案例的通过与否标志也要进行设置
		if (m_isMsgItemCompareOk.get() && 1 == ParameterProcess.m_iCasePassFlag.get()) {
			icase.setFlag(1);
		}
		else {
			icase.setFlag(0);
		}
		IDAL<Case> cDAL = DALFactory.GetBeanDAL(Case.class);
		cDAL.Edit(icase);

		m_core.FlowLog(inMsg, "------预期值比对完成");
		
		if (ParameterProcess.m_isCaseInFlow.get()) { //处理业务流
			if ((!m_isMsgItemCompareOk.get() || 0 == ParameterProcess.m_iCasePassFlag.get()) && cfi != null) {
				cfi.setCaseFlowPassFlag(0); //业务流失败
				//cfi.setEndTime(new Date());
				IDAL<CaseFlowInstance> cfiDAL = DALFactory.GetBeanDAL(CaseFlowInstance.class);
				cfiDAL.Edit(cfi);
			}
			//处理业务流（发起业务流内的下一个案例）
			if (processCaseFlow(ci)) {
				return; //有后续案例在处理，不急着做checkExecuteLogEndStatus
			}
		}
			
		if (!m_isMsgItemCompareOk.get() || 0 == ParameterProcess.m_iCasePassFlag.get() || !ParameterProcess.m_isCaseInFlow.get() || (ParameterProcess.m_isCaseInFlow.get() && isCaseFlowEnd(ci))) { //处理执行集结束标志
			checkExecuteLogEndStatus(ci);
			m_core.flowLog(ci.getCaseFlowInstance().getCaseFlowId().toString(), ci.getExecuteLogId().toString(), "业务流处理完毕", 0, 1, true);
		}
	}
	
	
	//有后续案例并成功发送，则return true，否则return false;
	private static boolean processCaseFlow(CaseInstance ci) throws Exception {
		
		//业务流实例
		CaseFlowInstance cfi = ci.getCaseFlowInstance();
		IDAL<CaseFlowInstance> cfiDAL = DALFactory.GetBeanDAL(CaseFlowInstance.class);

		CaseFlow caseFlow = DatabaseDAL.GetCaseFlow(cfi.getCaseFlowId().toString());
		
		//下一个案例存在吗？
		Case c = DatabaseDAL.GetNextCaseFromCaseFlow(caseFlow, ci.getExecuteLogId().toString(), ci);
		if (c == null) { // 没有找到下个案例，业务流结束
			cfi.setEndTime(new Date());
			if (cfi.getBeginTime() != null && cfi.getEndTime() != null) {

				// 计算执行集的执行时长
				long iRunSeconds = (cfi.getEndTime().getTime() - cfi
						.getBeginTime().getTime()) / 1000;// 除以1000是为了转换成秒

				if (iRunSeconds >= 0) {
					cfi.setRunDuration(Utility.FormatDuration2HHMMSS(iRunSeconds));
				}
			}
			
			try {
				setCaseFlowPassFlag(ci, cfi, caseFlow, cfiDAL);
			} catch (Exception ex) {
				log.error(ex);
			}
			
			cfiDAL.Edit(cfi);
			return false;
		}
		
		sendSpecifiedCase(cfi, ci);
		return true;
	}
	
	
	public static void sendSpecifiedCase(CaseFlowInstance cfi, CaseInstance ci) throws Exception {
		
		//发送下个案例出去	
		OutMessage out = new OutMessage();		
		out.caseFlowID = cfi.getCaseFlowId().toString();
		out.executeLogID = ci.getExecuteLogId().toString();	
		
		m_core.Send(out, 0);
	
	}
	


	//判断“业务流结束了吗”？
	private static boolean isCaseFlowEnd(CaseInstance ci) {
		
		//案例实例所对应的业务流实例
		CaseFlowInstance cfi = ci.getCaseFlowInstance();
		if (cfi == null) {
			return true;
		}
		
		//业务流实例所对应的业务流定义
		int iCaseFlowId = cfi.getCaseFlowId();
		IDAL<CaseFlow> flowDAL = DALFactory.GetBeanDAL(CaseFlow.class);
		CaseFlow caseFlow = flowDAL.Get(Op.EQ("id", iCaseFlowId));
		if (caseFlow == null) {
			return true;
		}
		//业务流下所对应的所有案例
		IDAL<Case> cDAL = DALFactory.GetBeanDAL(Case.class);
		List<Case> c = cDAL.ListAll(Op.EQ("caseFlow", caseFlow));
		if (c == null) {
			return true;
		}
		for (int i=0; i<c.size(); i++) {
			Case cc = c.get(i);
			//存在序号更大的案例（更后面的案例）？
			if (cc.getSequence() > ci.getSequence()) { //后面还有案例呢！业务流没有结束
				return false; 
			}
		}
		
		//已经是最后一个案例，业务流结束了！
		return true; 
	}
	
	
	//判断并设置案例的“通过与否”标志
	private static void setCaseFlowPassFlag(CaseInstance ci, CaseFlowInstance cfi,
			CaseFlow caseFlow, IDAL<CaseFlowInstance> cfiDAL) {
		
		int iFlowPassFlag = 1;

		if (!m_isMsgItemCompareOk.get() || 0 == ParameterProcess.m_iCasePassFlag.get()) {//最后一步
			iFlowPassFlag = 0;
		}
		else {
			IDAL<CaseInstance> ciDAL = DALFactory.GetBeanDAL(CaseInstance.class);
			List<CaseInstance> ciList = ciDAL.ListAll(Op.EQ("caseFlowInstance", cfi), Op.NE("id", ci.getId()));
			for (int i = 0; i < ciList.size(); i++) { //前面几步
				CaseInstance cii = ciList.get(i);
				if (0 == cii.getCasePassFlag()) {
					iFlowPassFlag = 0;
					break;
				}
			}
		}
		cfi.setCaseFlowPassFlag(iFlowPassFlag);
		cfiDAL.Edit(cfi);
		
		//静态业务流案例的执行状态也更新一下
		caseFlow.setPassFlag(iFlowPassFlag);
		DALFactory.GetBeanDAL(CaseFlow.class).Edit(caseFlow);
	}
	
	
	//判断执行集的“通过与否”标志
	private static int getExecuteSetTaskItemPassFlag(List<ExecuteSetTaskItem> executeSetTaskItemList, ExecuteLog executeLog) {
		
		int iPassFlag = 1;

		
		//遍历执行集下所有的执行元素（业务流用例）
		for (int i=0; i<executeSetTaskItemList.size();i++) {
			ExecuteSetTaskItem qt = executeSetTaskItemList.get(i);
			if (qt.getTaskId() == null) {
				return 0;
			}
			int iTaskId = Integer.parseInt(qt.getTaskId());
			int iTaskType = qt.getType();
			if (iTaskType == 0) {//案例
				IDAL<Case> caseDAL = DALFactory.GetBeanDAL(Case.class);
				Case c = caseDAL.Get(Op.EQ("id", String.valueOf(iTaskId)));
				if (c == null) {
					return 0;
				}
				IDAL<CaseInstance> ciDAL = DALFactory.GetBeanDAL(CaseInstance.class);
				CaseInstance ci = ciDAL.Get(Op.EQ("caseId", Integer.parseInt(c.getCaseId())), Op.EQ("executeLogId", executeLog.getId()));
				if (ci == null) {
					return 0;
				}
				int iCasePassFlag = ci.getCasePassFlag();
				if (4 == iCasePassFlag) { //中断
					return 4;
				}
				else if (1 != iCasePassFlag) {//否则，执行集执行失败
					return 0;
				}
			}
			if (iTaskType == 1) {//脚本
				IDAL<ScriptFlow> bfDAL = DALFactory.GetBeanDAL(ScriptFlow.class);
				ScriptFlow bf = bfDAL.Get(Op.EQ("id", String.valueOf(iTaskId)));
				if (bf == null) {
					return 0;
				}
				String strFlowName[] = bf.getName().split("_"); 
				IDAL<CaseFlow> cfDAL = DALFactory.GetBeanDAL(CaseFlow.class);
				CaseFlow cf = cfDAL.Get(Op.EQ("importBatchNo", strFlowName[0]), Op.EQ("caseFlowNo", strFlowName[1]));
				if (cf == null) {
					return 0;
				}
				IDAL<CaseFlowInstance> cfiDAL = DALFactory.GetBeanDAL(CaseFlowInstance.class);
				CaseFlowInstance cfi = cfiDAL.Get(Op.EQ("caseFlowId", cf.getId()), Op.EQ("executeLogId", executeLog.getId()));
				if (cfi == null) {
					return 0;
				}
				int iCaseFlowPassFlag = cfi.getCaseFlowPassFlag();
				if (4 == iCaseFlowPassFlag) { //中断
					return 4;
				}
				else if (1 != iCaseFlowPassFlag) {//否则，执行集执行失败
					return 0;
				}
			}
			else if (iTaskType == 2) {//业务流
				IDAL<CaseFlow> cfDAL = DALFactory.GetBeanDAL(CaseFlow.class);
				CaseFlow cf = cfDAL.Get(Op.EQ("id", iTaskId));
				if (cf == null) {
					return 0;
				}
				IDAL<CaseFlowInstance> cfiDAL = DALFactory.GetBeanDAL(CaseFlowInstance.class);
				CaseFlowInstance cfi = cfiDAL.Get(Op.EQ("caseFlowId", cf.getId()), Op.EQ("executeLogId", executeLog.getId()));
				if (cfi == null) {
					return 0;
				}
				int iCaseFlowPassFlag = cfi.getCaseFlowPassFlag();
				if (4 == iCaseFlowPassFlag) { //中断
					return 4;
				}
				else if (1 != iCaseFlowPassFlag) {
					return 0;
				}
			}
		}
	
		return iPassFlag;
	}
	
	
	private static void checkExecuteLogEndStatus(CaseInstance ci)  {
		
		if (ci == null) { //案例实例为空？
			return;
		}
		
		int iExecuteLogId = ci.getExecuteLogId();
		IDAL<ExecuteLog> logDAL = DALFactory.GetBeanDAL(ExecuteLog.class);
		ExecuteLog executeLog = logDAL.Get(Op.EQ("id", iExecuteLogId));
		
		if (executeLog == null) {//执行集肯定是有的，一般不会空
			return;
		}
		
		//执行所对应的任务队列是否存在？
		Integer iExecuteSetId = executeLog.getExecuteSetId();
		if (iExecuteSetId == null) { //不属于任务队列，可能是单个案例或单个的业务流
			if (ParameterProcess.m_isCaseInFlow.get()) { //孤零零的业务流
				CaseFlowInstance cfi = ci.getCaseFlowInstance();
				if (cfi != null) {
					if (2 != cfi.getCaseFlowPassFlag()) { //业务流还正在执行就不要赋值了
						int iPassFlag = cfi.getCaseFlowPassFlag();
						executeLog.setPassFlag(iPassFlag); //执行集的状态就等于业务流的执行状态
						logDAL.Edit(executeLog);	
					}
				}
			}
			else { //孤零零的单个的案例
				if (m_isMsgItemCompareOk.get() && 1 == ParameterProcess.m_iCasePassFlag.get()) {
					executeLog.setPassFlag(1);
				}
				else {
					executeLog.setPassFlag(0);
				}
				logDAL.Edit(executeLog);	
			}
			
			//设置ExecuteLog.EndRunTime	
			Date endRuntime = executeLog.getEndRunTime();
			Date timeNow = new Date();
			if (endRuntime == null) {
				executeLog.setEndRunTime(timeNow);
				logDAL.Edit(executeLog);
			}
			else if (endRuntime.before(timeNow)) {
				executeLog.setEndRunTime(timeNow);
				logDAL.Edit(executeLog);
			}

			// 计算执行集的执行时长
			if (executeLog.getBeginRunTime() != null && executeLog.getEndRunTime() != null) {

				long iRunSeconds = (executeLog.getEndRunTime().getTime() - executeLog
						.getBeginRunTime().getTime()) / 1000;// 除以1000是为了转换成秒

				if (iRunSeconds >= 0) {
					executeLog.setRunDuration(Utility.FormatDuration2HHMMSS(iRunSeconds));
					logDAL.Edit(executeLog);
				}
			}
			
			//单个案例或者单个业务流的执行，不存在任务队列，直接返回
			return;
		}
		
		//执行集为任务队列的情况：
		

		//获取当前执行集所对应的任务队列
		IDAL<ExecuteSet> executeSetDAL = DALFactory.GetBeanDAL(ExecuteSet.class);
		ExecuteSet executeSet = executeSetDAL.Get(Op.EQ("id", iExecuteSetId.toString()));
		if (executeSet == null) {
			return;
		}
	
		if (!m_isMsgItemCompareOk.get() || 0 == ParameterProcess.m_iCasePassFlag.get()) { //案例本身是没有通过的
			if (0 != executeLog.getPassFlag()) { //改执行集的执行状态为失败
				executeLog.setPassFlag(0);
				logDAL.Edit(executeLog);
			}
			//return;
		}
		
		int iQueueTaskItemPassFlag = 0;

		//找到当前案例所对应的任务队列元素ID（业务流ID或案例ID）
		CaseFlow caseFlow = null;
		int iQuestListItemId = 0;
		if (ParameterProcess.m_isCaseInFlow.get()) { //当前队列元素中的案例实际上是属于一个业务流的
			CaseFlowInstance cfi = ci.getCaseFlowInstance();
			if (cfi == null) { //业务流实例为空？
				return;
			}
			iQueueTaskItemPassFlag = cfi.getCaseFlowPassFlag();
			if (0 == iQueueTaskItemPassFlag) { //业务流是没有通过的，提前就可以判断执行集失败了
				if (0 != executeLog.getPassFlag()) { //改执行状态
					executeLog.setPassFlag(0);
					logDAL.Edit(executeLog);
					//return;
				}
			}
			iQuestListItemId = cfi.getCaseFlowId();
			IDAL<CaseFlow> cfDAL = DALFactory.GetBeanDAL(CaseFlow.class);
			caseFlow = cfDAL.Get(Op.EQ("id", iQuestListItemId));
		}
		else { //最后一个队列元素属于单个案例
			iQuestListItemId = ci.getCaseId();
		}
		
		
		//获取当前案例或业务流所定义的任务队列元素ID
		IDAL<ExecuteSetTaskItem> executeSetTaskItemDAL = DALFactory.GetBeanDAL(ExecuteSetTaskItem.class);
		ExecuteSetTaskItem executeSetTaskItem = null;
		if (ParameterProcess.m_isCaseInFlow.get()) { //最后一个队列元素属于业务流（可能是 业务流本身 也可能是 脚本业务流）
			//业务流
			executeSetTaskItem = executeSetTaskItemDAL.Get(Op.EQ("executeSet", executeSet),
					Op.EQ("taskId", String.valueOf(iQuestListItemId)), Op.EQ("type", 2));
			if (executeSetTaskItem == null && caseFlow != null) { //脚本？
				IDAL<ScriptFlow> scriptFlowDAL = DALFactory.GetBeanDAL(ScriptFlow.class);
				ScriptFlow scriptFlow = scriptFlowDAL.Get(Op.EQ("id", caseFlow.getScriptFlowId()));
				if (scriptFlow != null) {//交易脚本
					executeSetTaskItem = executeSetTaskItemDAL.Get(Op.EQ("executeSet", executeSet),
							Op.EQ("taskId", scriptFlow.getId().toString()), Op.EQ("type", 1));
				}
			}
		}
		else { //最后一个队列元素属于案例
			executeSetTaskItem = executeSetTaskItemDAL.Get(Op.EQ("executeSet", executeSet), Op.EQ("taskId", String.valueOf(iQuestListItemId)), Op.EQ("type", 0));
			if (m_isMsgItemCompareOk.get() && 1 == ParameterProcess.m_iCasePassFlag.get()) {
				iQueueTaskItemPassFlag = 1;
			}
			else {
				iQueueTaskItemPassFlag = 0;
			}
		}
		
		//获取当前这一笔队列元素所对应的queueTaskId，目前多数都是交易脚本 t_busiflow.ID
		if (executeSetTaskItem == null) {
			return;
		}
		if (executeSetTaskItem.getId() == null) {
			return;
		}
		int iQueueTaskId = -1;
		try {
			iQueueTaskId = Integer.parseInt(executeSetTaskItem.getId());
		} catch (Exception e) {
			return;
		}
		if (iQueueTaskId <= 0) {
			return;
		}
			
		//队列中所有的元素并进行判断
		List<ExecuteSetTaskItem> executeSetTaskItemList = executeSetTaskItemDAL.ListAll(Op.EQ("executeSet", executeSet));
		if (executeSetTaskItemList == null) {
			return;
		}
		//当前要素是否属于执行队列的最后一个元素？
		boolean isLastTaskItem = true;
		for (int i=0; i<executeSetTaskItemList.size();i++) {
			ExecuteSetTaskItem qt = executeSetTaskItemList.get(i);
			if (qt.getId() != null) {
				int iqueTaskId = Integer.parseInt(qt.getId());
				if (iqueTaskId > iQueueTaskId) { //任务队列的后面还有要执行的任务呢！
					isLastTaskItem = false;
					return;	//暂不进行执行集执行状态的更新
				}
			}
		}
		
		//队列中的最后一个执行元素
		if (isLastTaskItem) {
			
			//每一个执行集都需要重新开始计时
			int iExecuteSetTotalWaitedTime = 0;

			//检查并等待是否执行集下所有的业务流用例都已经执行完成？
			makeSureAllTaskItemsFinished(executeSetTaskItemList, executeLog, iExecuteSetTotalWaitedTime);
		
			//设置ExecuteLog.EndRunTime	
			Date endRuntime = executeLog.getEndRunTime();
			Date timeNow = new Date();
			if (endRuntime == null) {
				executeLog.setEndRunTime(timeNow);
				logDAL.Edit(executeLog);
			}
			else if (endRuntime.before(timeNow)) {
				executeLog.setEndRunTime(timeNow);
				logDAL.Edit(executeLog);
			}

			// 计算执行集的执行时长
			if (executeLog.getBeginRunTime() != null && executeLog.getEndRunTime() != null) {

				long iRunSeconds = (executeLog.getEndRunTime().getTime() - executeLog
						.getBeginRunTime().getTime()) / 1000;// 除以1000是为了转换成秒

				if (iRunSeconds >= 0) {
					executeLog.setRunDuration(Utility.FormatDuration2HHMMSS(iRunSeconds));
					logDAL.Edit(executeLog);
				}
			}
			if (0 == executeLog.getPassFlag()) { //已经有案例失败了! 不用再判了
				return;
			}
			int iPassFlag = 0; //最后一个案例是否通过？
			if (m_isMsgItemCompareOk.get() && 1 == ParameterProcess.m_iCasePassFlag.get() && ParameterProcess.m_isCaseInFlow.get()) { //最后一个案例是通过的，而且 最后一个队列元素属于业务流
				iPassFlag = getExecuteSetTaskItemPassFlag(executeSetTaskItemList, executeLog);
			}
			executeLog.setPassFlag(iPassFlag); //通过了!
			logDAL.Edit(executeLog);
		}
	}
	


	//检查案例实例的执行状态
	private static void makeSureAllTaskItemsFinished(List<ExecuteSetTaskItem> executeSetTaskItemList, ExecuteLog executeLog, Integer iTotalWaitedTime) {
			
		IDAL<Case> caseDAL = DALFactory.GetBeanDAL(Case.class);
		IDAL<CaseInstance> ciDAL = DALFactory.GetBeanDAL(CaseInstance.class);
		IDAL<CaseFlowInstance> cfiDAL = DALFactory.GetBeanDAL(CaseFlowInstance.class);
		
		for (int i=0; i<executeSetTaskItemList.size();i++) {
			ExecuteSetTaskItem qt = executeSetTaskItemList.get(i);
			if (qt.getTaskId() == null) {//数据不完整,没法检查
				continue;
			}
			int iTaskId = Integer.parseInt(qt.getTaskId());
			int iTaskType = qt.getType();
			if (iTaskType == 0) {//案例			
				Case c = caseDAL.Get(Op.EQ("id", String.valueOf(iTaskId)));
				if (c == null) { //数据不完整,没法检查
					continue;
				}
				CaseInstance ci = ciDAL.Get(Op.EQ("caseId", Integer.parseInt(c.getCaseId())), Op.EQ("executeLogId", executeLog.getId()));
				if (ci == null) { //还没有实例化,没法检查
					continue;
				}
				if (2 == ci.getCasePassFlag()) { //正在执行中
					iTotalWaitedTime += makeSureCaseFinished(ci, ciDAL, cfiDAL, iTotalWaitedTime);
					continue;
				}
			}
			if (iTaskType == 1) {//脚本
				IDAL<ScriptFlow> bfDAL = DALFactory.GetBeanDAL(ScriptFlow.class);
				ScriptFlow bf = bfDAL.Get(Op.EQ("id", String.valueOf(iTaskId)));
				if (bf == null) { //数据不完整,没法检查
					continue;
				}
				String strFlowName[] = bf.getName().split("_"); 
				IDAL<CaseFlow> cfDAL = DALFactory.GetBeanDAL(CaseFlow.class);
				CaseFlow cf = cfDAL.Get(Op.EQ("importBatchNo", strFlowName[0]), Op.EQ("caseFlowNo", strFlowName[1]));
				if (cf == null) { //数据不完整,没法检查
					continue;
				}
				CaseFlowInstance cfi = cfiDAL.Get(Op.EQ("caseFlowId", cf.getId()), Op.EQ("executeLogId", executeLog.getId()));
				if (cfi == null) { //还没有实例化,没法检查
					continue;
				}
				
				if (2 == cfi.getCaseFlowPassFlag()) {//正在执行中,未完成
					iTotalWaitedTime += makeSureCaseFlowFinished(cfi, ciDAL, cfiDAL, iTotalWaitedTime);
					continue;
				}
			}
			else if (iTaskType == 2) {//业务流
				IDAL<CaseFlow> cfDAL = DALFactory.GetBeanDAL(CaseFlow.class);
				CaseFlow cf = cfDAL.Get(Op.EQ("id", iTaskId));
				if (cf == null) { //数据不完整,没法检查
					continue;
				}
				CaseFlowInstance cfi = cfiDAL.Get(Op.EQ("caseFlowId", cf.getId()), Op.EQ("executeLogId", executeLog.getId()));
				if (cfi == null) { //还没有实例化,没法检查
					continue;
				}
				if (2 == cfi.getCaseFlowPassFlag()) {//正在执行中,未完成
					iTotalWaitedTime += makeSureCaseFlowFinished(cfi, ciDAL, cfiDAL, iTotalWaitedTime);
					continue;
				}
			}
		}
	}
	
	//确保业务流都已经完成（出现了超时则等待）
	private static int makeSureCaseFlowFinished(CaseFlowInstance cfi, IDAL<CaseInstance> ciDAL, IDAL<CaseFlowInstance> cfiDAL, int iTotalWaitedTime) {
		
		int iCaseFlowMoreWaited = 0;
		
		if (cfi == null) {
			return 0;
		}

		//遍历业务流实例下的所有案例实例
		List<CaseInstance> ciList = ciDAL.ListAll(Op.EQ("caseFlowInstance", cfi));
		for (int i = 0; i < ciList.size(); i++) { 
			CaseInstance ci = ciList.get(i);
			if (ci != null && 2 == ci.getCasePassFlag()) { //案例正在执行中
				iCaseFlowMoreWaited += makeSureCaseFinished(ci, ciDAL, cfiDAL, iTotalWaitedTime);
				iTotalWaitedTime += iCaseFlowMoreWaited;
			}
		}

		return iCaseFlowMoreWaited;
	}
	
	//确保案例已经完成（出现了超时则等待）
	private static int makeSureCaseFinished(CaseInstance ci, IDAL<CaseInstance> ciDAL, IDAL<CaseFlowInstance> cfiDAL, int iTotalWaitedTime) {
		
		int iCaseMoreWaited = 0;
		if (m_iSystemMaxTimeOutSet4AllTransactions < 0) {
			m_iSystemMaxTimeOutSet4AllTransactions = DbGet.getCaseTransactionTimeOut(ci);
		}
		if (iTotalWaitedTime < m_iSystemMaxTimeOutSet4AllTransactions) {
			iCaseMoreWaited = m_iSystemMaxTimeOutSet4AllTransactions - iTotalWaitedTime;
			try {
				Thread.currentThread();
				Thread.sleep(iCaseMoreWaited * 1000);
				iTotalWaitedTime += iCaseMoreWaited;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		if (2 == ci.getCasePassFlag()) { //等待了足够长的时候之后，还是正在执行中
			ci.setCasePassFlag(5); //案例超时了
			ciDAL.Edit(ci);
			CaseFlowInstance cfi = ci.getCaseFlowInstance();
			if (cfi != null && cfi.getCaseFlowPassFlag() != 0) {
				cfi.setCaseFlowPassFlag(0); //业务流未通过
				cfiDAL.Edit(cfi);
			}
		}
		
		return iCaseMoreWaited;
	}
	
	
}
