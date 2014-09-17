package com.dc.tes.fcore;


import java.util.Date;
import java.util.List;
import java.util.Properties;

import com.dc.tes.adapter.remote.DefaultReplyAdapterHelper;
import com.dc.tes.data.DALFactory;
import com.dc.tes.data.IDAL;
import com.dc.tes.data.op.Op;
import com.dc.tes.net.Message;
import com.dc.tes.net.MessageItem;
import com.dc.tes.net.MessageType;

import com.dc.tes.data.model.ScriptFlow;
import com.dc.tes.data.model.Case;
import com.dc.tes.data.model.ExecuteSetExecutePlan;
import com.dc.tes.data.model.ExecuteSet;
import com.dc.tes.data.model.ExecuteSetTaskItem;
import com.dc.tes.data.model.SysType;
import com.dc.tes.data.model.Transaction;


public class Executor {
	

	public SysType m_sysType = null;
	
	public Executor(SysType sysType) {
		m_sysType = sysType;
	}
	
	public void execute(ExecuteSetExecutePlan executeSetExecutePlan) {
	
		IDAL<ExecuteSetExecutePlan> iDAL = DALFactory.GetBeanDAL(ExecuteSetExecutePlan.class);
		
		beginOneQueueListExecutePlan(executeSetExecutePlan, iDAL);
		
		int iExecuteSetDirId = 0;
		try {
			iExecuteSetDirId = Integer.parseInt(executeSetExecutePlan.getExecuteSetDirId());
		}
		catch(Exception e) {
			e.printStackTrace();
			return;
		}
		
		Integer queueId = DbGet.getExecuteSetIdByExecuteSetDirId(iExecuteSetDirId);
		if (queueId == null) {
			return;
		}
		ExecuteSet executeSet = DALFactory.GetBeanDAL(ExecuteSet.class).Get(Op.EQ("id", queueId.toString()));
		if (executeSet == null) {
			return;
		}
		
		int iExecuteLogId = 0;
		try {
			iExecuteLogId = DbSet.Insert2ExecuteLog(iExecuteSetDirId, executeSet, DbGet.m_user.getId(), DbGet.m_user.getName());
		}
		catch(Exception e) {
			System.out.println("插入执行日志表出错！");
			e.printStackTrace();
			return;
		}
		
		executeOneQueuelist(executeSet, iExecuteLogId);
		
		//......
	
		finishOneQueueListExecutePlan(executeSetExecutePlan, iDAL);
	}
	
	
	private void beginOneQueueListExecutePlan(ExecuteSetExecutePlan executeSetExecutePlan, IDAL<ExecuteSetExecutePlan> iDAL) {

		Date now = new Date();
		executeSetExecutePlan.setBeginRunTime(now);
		executeSetExecutePlan.setScheduledRunStatus(-1);	//正在执行中
		iDAL.Edit(executeSetExecutePlan);
	}
	
	private void finishOneQueueListExecutePlan(ExecuteSetExecutePlan executeSetExecutePlan, IDAL<ExecuteSetExecutePlan> iDAL) {
		
		Date now = new Date();
		executeSetExecutePlan.setEndRunTime(now);
		executeSetExecutePlan.setScheduledRunStatus(2);	//执行完成
		iDAL.Edit(executeSetExecutePlan);
	}
	
	
	private void executeOneQueuelist(ExecuteSet executeSet, int iExecuteLogId) {
		
		List<ExecuteSetTaskItem> executeSetTaskItemList = DbGet.getExecuteSetTaskItemByExecuteSet(executeSet);
		
		for (int i=0; i<executeSetTaskItemList.size(); i++) {
			ExecuteSetTaskItem executeSetTaskItem = executeSetTaskItemList.get(i);
			int iRecount = executeSetTaskItem.getRepCount();
			for (int j=0; j<iRecount; j++) {
				exeucteOneExecuteSetTaskItem(executeSet.getId(), executeSetTaskItem, iExecuteLogId, i, j);
			}
		}
	}
	
	private void exeucteOneExecuteSetTaskItem(String queueId, ExecuteSetTaskItem executeSetTaskItem, Integer iExecuteLogId, int iTaskIndex, int iCountIndex) {
		
		int iTaskType = executeSetTaskItem.getType();
		if (iTaskType == 1) { //script
			String logID = DbGet.m_user.getId() + "_" + queueId + "_" + iTaskIndex + "_" + iCountIndex + "_" + iExecuteLogId;
			ScriptFlow scriptFlow = DbGet.getScriptFlowByScriptFlowId(executeSetTaskItem.getTaskId());
			//判断当前用例是否被设置为“暂时不用”?
			if (DbGet.isDisabledCaseFlow(scriptFlow.getSrcipt())) {
				return;
			}
			RunScriptFlow(logID, executeSetTaskItem.getId(), scriptFlow.getSrcipt(), iExecuteLogId.toString());
		}
		else if (iTaskType == 0) { //case
			Case c = DbGet.GetCaseByCaseId(executeSetTaskItem.getTaskId());
			String sTransactionId = c.getTransactionId();
			Transaction trans = DbGet.getTransctionByTransactionId(sTransactionId);
			RunCase(trans, c.getCaseId(), null, c.getRequestXml(), iExecuteLogId.toString());
		}
	}
	
	private Properties FormatSysProperty()
	{
		Properties properties = new Properties();
		properties.put("coreIP", m_sysType.getIpadress());
		properties.put("corePort", String.valueOf(m_sysType.getPortnum()));
		properties.put("CHANNELNAME", "UI");
		properties.put("SIMTYPE", "c");
		
		return properties;
	}
	
	
	/**
	 * 执行业务流
	 * 
	 * @param logID
	 *            日志标识
	 * @param flowid
	 *            业务流标识
	 * @param Script
	 *            脚本内容
	 */
	private void RunScriptFlow(String logID, String queueTaskId, String Script, String executeLogId) {
		
		final Message sendMsg = new Message(MessageType.UI);
		
		sendMsg.put(MessageItem.UI.OP, 4);
		sendMsg.put("TAG", logID);
		sendMsg.put("NAME", queueTaskId);
		sendMsg.put("CODE", Script);
		sendMsg.put("CHANNELNAME", "UI");
		
		// 新加入执行日志ID
		sendMsg.put("EXECUTELOGID", executeLogId);
		sendMsg.put("API", 0);

		System.out.println("将要发送消息为：" + sendMsg);
		
		try {
			try {
				Thread t = new Thread(new Runnable() {
					public void run() {
						new DefaultReplyAdapterHelper(FormatSysProperty()).SendToCoreRaw(sendMsg.Export());
					}
				});
				t.start();
			} catch (Exception e) {
				e.printStackTrace();
				throw new Exception("模拟器执行出现异常");
			}

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	
	/**
	 * 执行案例
	 * @param sysInfo		系统信息 必须提供
	 * @param tranInfo		对应交易信息 必须提供
	 * @param caseId		案例名称 必须提供
	 * @param REQMESSAGE	向被测系统发的报文    可选
	 * @param REQDATA		向被测系统发的数据（未经过组包的） 可选
	 * @return				案例执行结果
	 */
	
	private void RunCase(Transaction trans, String caseId, byte[] REQMESSAGE, String REQDATA, String executeLogId) {
	
		final Message sendMsg = new Message(MessageType.UI);
		
		IDAL<Case> caseDAL = DALFactory.GetBeanDAL(Case.class);
		Case casebean = caseDAL.Get(Op.EQ("id", caseId));
		if (casebean.getBreakPointFlag()!=null && casebean.getBreakPointFlag()==1) {
			sendMsg.put(MessageItem.UI.OP, 5);
		}
		else {
			sendMsg.put(MessageItem.UI.OP, 1);
		}
		
		sendMsg.put(MessageItem.UI.TRANCODE, trans.getTranCode());
		sendMsg.put("CHANNELNAME", "UI");
		//设置通道名称，继承关系
		String chanelName = "SEND";
		if (!(trans.getChannel() == null || trans.getChannel().isEmpty())) {
			String tranChanelName = trans.getChannel();
			//若通道已不存在
			if (!DbGet.isChannelExist(m_sysType.getSystemId(), tranChanelName)) {
				System.out.println("该案例所属交易的通道：" + tranChanelName + ",不存在，无法发起");
				return;
			}
			chanelName = tranChanelName;
		}
		else {
			if (m_sysType.getChannel() != null && !m_sysType.getChannel().isEmpty()) {
				chanelName = m_sysType.getChannel();
			}
		}
		sendMsg.put("DESTCHANNEL", chanelName);
		
		if (caseId != null && !caseId.isEmpty()) {
			sendMsg.put(MessageItem.UI.CASENAME, caseId);
		}
		
		if (REQMESSAGE != null) {
			sendMsg.put(MessageItem.UI.REQMESSAGE, REQMESSAGE);
		}
			
		if (REQDATA != null && !REQDATA.isEmpty()) {
			sendMsg.put(MessageItem.UI.REQDATA, REQDATA);
		}
		
		sendMsg.put("EXECUTELOGID", executeLogId);
		System.out.println("将要发送消息：" + sendMsg);
		
		try {
			try {
				Thread t = new Thread(new Runnable() {
					public void run() {
						new DefaultReplyAdapterHelper(FormatSysProperty()).SendToCoreRaw(sendMsg.Export());
					}
				});
				t.start();
			} catch (Exception e) {
				e.printStackTrace();
				throw new Exception("案例执行出现异常");
			}
		} 
		catch (Exception e) {
			System.out.println("案例执行出现异常，请与相关人员联系");
			e.printStackTrace();
		}
		return;
	}
	
	
}
