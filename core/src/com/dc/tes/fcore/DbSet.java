package com.dc.tes.fcore;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.dc.tes.OutMessage;
import com.dc.tes.data.DALFactory;
import com.dc.tes.data.IDAL;
import com.dc.tes.data.model.Card;
import com.dc.tes.data.model.Case;
import com.dc.tes.data.model.CaseFlowInstance;
import com.dc.tes.data.model.CaseInstance;
import com.dc.tes.data.model.CaseInstanceFieldValue;
import com.dc.tes.data.model.CaseParameterExpectedValue;
import com.dc.tes.data.model.CommMsgLog;
import com.dc.tes.data.model.ExecuteLog;
import com.dc.tes.data.model.ExecuteSet;
import com.dc.tes.data.model.SystemDynamicParameter;
import com.dc.tes.data.model.Transaction;
import com.dc.tes.data.model.TransactionDynamicParameter;
import com.dc.tes.data.op.Op;
import com.dc.tes.dom.MsgDocument;
import com.dc.tes.dom.MsgField;
import com.dc.tes.dom.util.MsgLoader;
import com.dc.tes.fcore.script.ScriptEnv;
import com.dc.tes.msg.util.Value;


/**
 * SQL更新功能
 * 
 * @author Huangzx
 * 
 */

public class DbSet {
	
	public enum Status {  
		PASS , FAIL , RUNNING , 
	}
	
	/**
	 * 日志状态更新
	 * @param executeLogId
	 * @param passFlag
	 */
	public static void updateExecuteLogStatus(Integer executeLogId, int passFlag) {
		
		IDAL<ExecuteLog> logDAL = DALFactory.GetBeanDAL(ExecuteLog.class);
		ExecuteLog executeLog = logDAL.Get(Op.EQ("id", executeLogId));
		if (executeLog == null)
			return;
		executeLog.setPassFlag(passFlag); //正在执行
		logDAL.Edit(executeLog);
	}
	
	public static void InsertCommMsgLog(String transactionId, Integer caseInstanceId, Integer executeLogId, String caseName, String strReqMsg) {
		
		//插入当日收发日志表  要得到发送是否成功信息比较困难
		CommMsgLog comm = new CommMsgLog();
		Transaction tran = DALFactory.GetBeanDAL(Transaction.class).Get(Op.EQ("transactionId", transactionId));
		comm.setTransactionName(tran.getTranName());
		comm.setCaseInstanceId(caseInstanceId);
		comm.setMsgContent(strReqMsg);
		comm.setCaseName(caseName);	
		comm.setSendTime(new Date());
		comm.setSendStatus("1");
		comm.setDirection("1");
		comm.setExecuteLogId(executeLogId);
		IDAL<CommMsgLog> commDAL = DALFactory.GetBeanDAL(CommMsgLog.class);
		commDAL.Add(comm);
	}
	
	public static void insertOneFieldValue(int iCaseInstanceId,
			TransactionDynamicParameter transParameter, String msgFieldName,
			String msgFieldValue, String expectedValue, int iParameterType) {

		//插入案例实例字段参数表
		IDAL<CaseInstanceFieldValue> instFieldValueDAL = DALFactory.GetBeanDAL(CaseInstanceFieldValue.class);
		
		//获取报文的字段与其对应的预期值分开来保存     by ljs
		CaseInstanceFieldValue caseInstanceFieldValue = instFieldValueDAL.Get(Op.EQ("caseInstanceId", iCaseInstanceId),Op.EQ("transParameter", transParameter));
		if(caseInstanceFieldValue == null) {
			caseInstanceFieldValue = new CaseInstanceFieldValue();
			caseInstanceFieldValue.setCaseInstanceId(iCaseInstanceId);
			caseInstanceFieldValue.setTransParameter(transParameter);
			caseInstanceFieldValue.setMsgFieldName(msgFieldName);
			caseInstanceFieldValue.setMsgFieldValue(msgFieldValue);	
			caseInstanceFieldValue.setParameterType(iParameterType);
			instFieldValueDAL.Add(caseInstanceFieldValue);
		} else {
			caseInstanceFieldValue.setExpectedValue(expectedValue);
			instFieldValueDAL.Edit(caseInstanceFieldValue);
		}
	}
	
	
	//返回boolean: 是否存在断点，需要中断？
	public static boolean insert2CaseInstance(OutMessage out) {
		//插入案例实例表
		boolean isBreakPointFlagOn = false;
	
		IDAL<Case> caseDAL = DALFactory.GetBeanDAL(Case.class);
		Case c = caseDAL.Get(Op.EQ("caseId", out.caseName));
		if (c == null) {
			//log.error("找不到对应[CaseId=" + out.caseName + "]的案例");
			return false;
		}
				
		CaseInstance ci = new CaseInstance();
		ci.setBeginRunTime(new Date());
		ci.setCaseName(c.getCaseName());
		ci.setCaseNo(c.getCaseNo());
		ci.setSequence(c.getSequence());
		ci.setTransactionId(Integer.parseInt(c.getTransactionId()));
		if(c.getAmount() != null) {
			ci.setAmount(Float.toString(c.getAmount()));
		}
		ci.setCardId(c.getCardId());
		Integer iExecuteLogId = (Integer) out.preserved1;
		ci.setExecuteLogId(iExecuteLogId);
		if (c.getBreakPointFlag() != null && c.getBreakPointFlag() == 1) {
			ci.setBreakPointFlag(1);
			isBreakPointFlagOn = true;
		}
		
		IDAL<CaseFlowInstance> cfiDAL = DALFactory.GetBeanDAL(CaseFlowInstance.class);
		CaseFlowInstance cfi = cfiDAL.Get(Op.EQ("id", (Integer) out.preserved2));
		if (cfi != null) {
			ci.setCaseFlowInstance(cfi);
			if (c.getSequence() == 0) { //业务流的第一个案例
				cfi.setCaseFlowPassFlag(2); //业务流正在执行
			}
			cfiDAL.Edit(cfi);
		}
		
		try	{
			IDAL<Card> cardDAL = DALFactory.GetBeanDAL(Card.class);
			Card card = cardDAL.Get(Op.EQ("id", c.getCardId()));
			if (card != null) {
				ci.setCardNumber(card.getCardNumber());
				ci.setDbHost(card.getDbHost());
			}
		}
		catch(Exception e)	{
			//log.error("数据不完整，根据CardId[" + c.getCardId() + "]找对应的卡号是出错，错误提示信息：" + e.getMessage());
			return false;
		}
		
		try	{ //保存来自业务流脚本的信息，为打印日志作准备
			if(out.preserved3 != null) {
				ScriptEnv env = (ScriptEnv) out.preserved3;
				ci.setScriptName(env.getScriptName());
				ci.setTag(env.getTag());
			}
			ci.setReceivedReplayFlag(0);
			ci.setImportBatchNo(c.getImportBatchNo());
			ci.setCaseId(Integer.parseInt(c.getCaseId()));
			ci.setRequestXml(out.data.toString());
			ci.setExpectedXml(c.getExpectedXml());
			if (!isBreakPointFlagOn) {
				ci.setCasePassFlag(2); //2代表已经发送
			}
		}
		catch(Exception e) {
			//log.error("设置案例实例字段信息错误：" + eSetCaseInstFld.getMessage());
			return false;
		}
	
		if (DbGet.m_sysType == null) {//debug
			System.out.println("DbGet.m_sysType is null!");
		}
		if (DbGet.m_sysType.getSystemName() == null) {//debug
			System.out.println("DbGet.m_sysType.getSystemName() is null!");
		}
		
		if ("二代支付前置机".equals(DbGet.m_sysType.getSystemName())) {
			
			byte[] byte_MsgId = Utility.subbyte(out.bin, 78, 20);
			String MsgId = new String(byte_MsgId);
			if(MsgId != null && !MsgId.isEmpty()) {
				ci.setField37(MsgId);		
				IDAL<CaseInstance> ciDAL = DALFactory.GetBeanDAL(CaseInstance.class);
				ciDAL.Edit(ci);
			}
		}
		else if ("二代支付前置机提出报文转换".equals(DbGet.m_sysType.getSystemName())) {
			
			String MsgId = Utility.getWKEMsgId(out.bin);
			if (MsgId == null || MsgId.isEmpty()) {
				return false;
			}
			if(MsgId != null && !MsgId.isEmpty()) {
				ci.setField37(MsgId);		
				IDAL<CaseInstance> ciDAL = DALFactory.GetBeanDAL(CaseInstance.class);
				ciDAL.Edit(ci);
			}
		}
		else if ("银联-收单平台".equals(DbGet.m_sysType.getSystemName()) ||
				"信用卡第三方支付".equals(DbGet.m_sysType.getSystemName())) {
			
			if(out.data.getAttribute("b37") != Value.empty) {
				ci.setField37(new String(out.data.getAttribute("b37").bytes));
			}
			
			if(out.data.getAttribute("b38") != Value.empty) {
				ci.setField38(new String(out.data.getAttribute("b38").bytes));
			}
			
			//构造和设置90域
			try {
				String tranCode = String.format("%04d", Integer.parseInt(out.tranCode));
				String b7 = new String(out.data.getAttribute("b7").bytes);
				String b11 = new String(out.data.getAttribute("b11").bytes);
				MsgField msgFld = (MsgField)out.data.get("b32");
				if (msgFld == null) {
					return isBreakPointFlagOn;
				}
				String b32 = msgFld.value();
				if (b32 == null || b32.isEmpty()) {
					return isBreakPointFlagOn;
				}
				b32 = String.format("%011d", Integer.parseInt(b32));
						
				msgFld = (MsgField)out.data.get("b33");
				if (msgFld == null) {
					return isBreakPointFlagOn;
				}
				String b33 = msgFld.value();
				if (b33 == null || b33.isEmpty()) {
					return isBreakPointFlagOn;
				}
				b33 = String.format("%011d", Integer.parseInt(b33));		
	
				// 拼装90域
				String field90 = tranCode + b11 + b7 + b32 + b33;
				ci.setValue4NextCase(field90);
			} 
			catch (Exception ex) {
				System.out.print("缺少组合90域的字段" + ex.getMessage());
			}
		}
	
		
		//获取应答报文的编码方式
		String requestMsgEncoding = DbGet.m_sysType.getEncoding4RequestMsg();

		//应答报文的中文字符串
		String strRequestMsg = "";
		
		try {
			strRequestMsg = new String(out.bin, requestMsgEncoding);
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		
		//在系统配置中没有指定编码方式？
		/*if (DbGet.m_sysType.getEncoding4RequestMsg() == null || DbGet.m_sysType.getEncoding4RequestMsg().isEmpty()) {
			if ("<?xml".equals(strRequestMsg.substring(0, 5))) { //xml文件，做一个编码的转换
				requestMsgEncoding = Utility.getXmlEncoding(strRequestMsg);			
				try {
					strRequestMsg = new String(out.bin, requestMsgEncoding);
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			}
		}*/
		//非xml文本，以16进制方式打印，兼容不可见字符
//		if(!strRequestMsg.startsWith("<?xml")) {
//			strRequestMsg = RuntimeUtils.PrintHex(out.bin, Charset
//					.forName(requestMsgEncoding));
//		} 

		ci.setRequestMsg(strRequestMsg);
		//插入CaseInstance表
		IDAL<CaseInstance> ciDAL = DALFactory.GetBeanDAL(CaseInstance.class);
		ciDAL.Add(ci);
		

		if (!isBreakPointFlagOn) { //没有断点
			try {
				//插入当日收发日志表  要得到发送是否成功信息比较困难
				InsertCommMsgLog(c.getTransactionId(), ci.getId(), (Integer) out.preserved1, c.getCaseName(), strRequestMsg);
			}
			catch (Exception e) {
				System.out.println("InsertCommMsgLog 出错：" + e.getMessage());
				e.printStackTrace();
			}
		}
	
		
		//插入报文字段参数
		try	{
			ParameterProcess.insertInstanceFieldParams1(out.data, ci, ciDAL, null);
		}
		catch (Exception e)	{
			System.out.print("插入报文字段表失败！");
			return isBreakPointFlagOn;
		}
		
		return isBreakPointFlagOn;
	}
	
	//在xml报文中，用 新值 去替换 旧值，并回写相应的字段记录
	public static void replaceCaseXmlMsgContent(String strCaseId, String strItemContent, String oldFieldValue, String newFieldValue) {
		
		String strOldItemValue = ">" + oldFieldValue + "</field>";
		String strNewItemValue = ">" + newFieldValue + "</field>";
		String strNewItemContent = strItemContent.replace(strOldItemValue, strNewItemValue);
		IDAL<Case> caseDAL = DALFactory.GetBeanDAL(Case.class);
		Case c = caseDAL.Get(Op.EQ("caseId", strCaseId));
		if (c == null) {
			System.out.println("找不到对应[CaseId=" + strCaseId + "]的案例");
			return;
		}
		String strXmlContent = c.getRequestXml();
		String strNewXmlContent = strXmlContent.replace(strItemContent, strNewItemContent); 
		c.setRequestXml(strNewXmlContent);
		caseDAL.Edit(c);
	}
	

	public static MsgDocument replaceMsgItemInMsgDoc(String strItemContent, String oldFieldValue, String newFieldValue, MsgDocument msgDoc) {
		
		String strOldItemValue = ">" + oldFieldValue + "</field>";
		String strNewItemValue = ">" + newFieldValue + "</field>";
		String strNewItemContent = strItemContent.replace(strOldItemValue, strNewItemValue);
		String strXmlContent = msgDoc.toString(); 
		String strNewXmlContent = strXmlContent.replace(strItemContent, strNewItemContent);
		MsgDocument newMsgDoc = MsgLoader.LoadXml(strNewXmlContent);
		return newMsgDoc;
	}
	

	/*public static void replaceCaseInstanceXmlContent(int iExecuteLogId, int iCaseId, String strItemContent, String oldFieldValue, String newFieldValue) {
		
		String strOldItemValue = ">" + oldFieldValue + "</field>";
		String strNewItemValue = ">" + newFieldValue + "</field>";
		String strNewItemContent = strItemContent.replace(strOldItemValue, strNewItemValue);
		
		CaseInstance ci = DbGet.getCaseInstanceByExecuteCase(iExecuteLogId, iCaseId);
		if (ci == null) {
			System.out.println("找不到对应[ExecuteLogId=" + iExecuteLogId + ", CaseId=" + iCaseId + "]的案例");
			return;
		}
		String strXmlContent = ci.getRequestXml();
		String strNewXmlContent = strXmlContent.replace(strItemContent, strNewItemContent); 
		ci.setResponseXml(strNewXmlContent);
		IDAL<CaseInstance> ciDAL = DALFactory.GetBeanDAL(CaseInstance.class);
		ciDAL.Edit(ci);
	}*/
	

	//added for AutoRunner
	/*public static void updateQueueListLastScheduledRunTime(QueueListExecutePlan queueListExecutePlan) {
		
		IDAL<ExecuteLog> logDAL = DALFactory.GetBeanDAL(ExecuteLog.class);
		ExecuteLog executeLog = logDAL.Get(Op.EQ("id", executeLogId));
		if (executeLog == null)
			return;
		executeLog.setPassFlag(passFlag); //正在执行
		logDAL.Edit(executeLog);
	}*/
	
	
	public static int Insert2ExecuteLog(int iExecuteSetDirId, ExecuteSet executeSet, String userId, String userName) {
		
		//执行批次号
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		String now = sdf.format(new Date());
		String execBN = userName + now;
		
	
		IDAL<ExecuteLog> executeLogDAL = DALFactory.GetBeanDAL(ExecuteLog.class);
		ExecuteLog log = new ExecuteLog();
		
		//生成时间
		sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		now = sdf.format(new Date());
		log.setCreateTime(now);

		log.setExecuteSetName(executeSet.getName());
		log.setExecuteSetId(Integer.parseInt(executeSet.getId()));
		log.setDescription(executeSet.getDescription());

		//轮次
		if (DbGet.m_iRoundId > 0) {
			log.setRoundId(DbGet.m_iRoundId);
		}
		//执行批次号
		log.setExecuteBatchNo(execBN);
		log.setSystemId(Integer.parseInt(DbGet.m_sysType.getSystemId()));
		log.setBeginRunTime(new Date());
		log.setPassFlag(2); //正在执行中
		log.setExecuteSetDirId(iExecuteSetDirId);
		log.setUserId(Integer.parseInt(userId));
		executeLogDAL.Add(log);
		return log.getId();
	}
	
	
	//给定一个系统参数，删除项目内所有无效的交易参数
	public static void deleteCaseParameterExpectedValue(TransactionDynamicParameter transParam) {
		
		IDAL<CaseParameterExpectedValue> cpevDAL = DALFactory.GetBeanDAL(CaseParameterExpectedValue.class);
		List<CaseParameterExpectedValue> caseParamExpectedValueList = cpevDAL.ListAll(Op.EQ("transParameter", transParam));
		if (caseParamExpectedValueList != null) {
			for (int i=0; i<caseParamExpectedValueList.size(); i++) {
				CaseParameterExpectedValue cpev = caseParamExpectedValueList.get(i);
				String sCaseId = cpev.getCaseId();
				Transaction trans = DbGet.getTransactionByCaseID(sCaseId);
				if (trans.getSystemId().equals(DbGet.m_sysType.getSystemId())) { //属于同一个系统的案例
					//删除案例的动态参数预期值设置（case_parameter_expected_value）
					cpevDAL.Del(cpev);
				}
			}
		}	
	}
	
	//给定一个系统参数，删除项目内所有无效的交易参数
	public static int deleteInvalidTransactionParameter(SystemDynamicParameter systemParameter) {
		
		int iDelCount = 0;
		IDAL<TransactionDynamicParameter> transParamDAL = DALFactory.GetBeanDAL(TransactionDynamicParameter.class);
		List<TransactionDynamicParameter> transParamList = transParamDAL.ListAll(Op.EQ("systemParameter", systemParameter));
		if (transParamList != null) {
			for (int i=0; i<transParamList.size(); i++) {
				TransactionDynamicParameter transParam = transParamList.get(i);
				//先删除案例的动态参数预期值设置（case_parameter_expected_value）
				deleteCaseParameterExpectedValue(transParam);
				transParamDAL.Del(transParam);
				iDelCount ++;
			}
		}
		
		return iDelCount;
	}
	

	//删除项目内所有无效的系统参数
	public static int deleteInvalidSystemParameter() {
		
		int iDelCount = 0;
		IDAL<SystemDynamicParameter> sysParamDAL = DALFactory.GetBeanDAL(SystemDynamicParameter.class);

		List<SystemDynamicParameter> sysParamList = sysParamDAL.ListAll(Op.EQ("systemId", DbGet.m_sysType.getSystemId()), Op.EQ("isValid", "0"));
		if (sysParamList != null) {
			for (int i=0; i<sysParamList.size(); i++) {
				SystemDynamicParameter sysParam = sysParamList.get(i);
				//先删除交易参数
				deleteInvalidTransactionParameter(sysParam);
				//再删除系统参数
				sysParamDAL.Del(sysParam);
				iDelCount ++;
			}
		}
		
		return iDelCount;
	}
	
	public static void updateSystemParameter2Invalid(SystemDynamicParameter sysParam) {

		IDAL<SystemDynamicParameter> sysParamDAL = DALFactory.GetBeanDAL(SystemDynamicParameter.class);
		sysParam.setIsValid("0");
		sysParamDAL.Edit(sysParam);
	}
	
}
