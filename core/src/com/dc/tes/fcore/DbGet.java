package com.dc.tes.fcore;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.dc.tes.data.DALFactory;
import com.dc.tes.data.IDAL;
import com.dc.tes.data.db.HibernateDALFactory;
import com.dc.tes.data.model.RecordedCase;
import com.dc.tes.data.model.ScriptFlow;
import com.dc.tes.data.model.Card;
import com.dc.tes.data.model.Case;
import com.dc.tes.data.model.CaseFlow;
import com.dc.tes.data.model.CaseFlowInstance;
import com.dc.tes.data.model.CaseInstance;
import com.dc.tes.data.model.CaseInstanceFieldValue;
import com.dc.tes.data.model.CaseInstanceSqlValue;
import com.dc.tes.data.model.CaseParameterExpectedValue;
import com.dc.tes.data.model.Channel;
import com.dc.tes.data.model.DbHost;
import com.dc.tes.data.model.ExecuteLog;
import com.dc.tes.data.model.ExecutePlan;
import com.dc.tes.data.model.ExecuteSetDirectory;
import com.dc.tes.data.model.ExecuteSetExecutePlan;
import com.dc.tes.data.model.ExecuteSet;
import com.dc.tes.data.model.ExecuteSetTaskItem;
import com.dc.tes.data.model.SysType;
import com.dc.tes.data.model.SystemDynamicParameter;
import com.dc.tes.data.model.TestRound;
import com.dc.tes.data.model.Transaction;
import com.dc.tes.data.model.TransactionDynamicParameter;
import com.dc.tes.data.model.User;
import com.dc.tes.data.model.UserRSystem;
import com.dc.tes.data.op.Op;
import com.dc.tes.dom.MsgDocument;
import com.dc.tes.dom.MsgField;
import com.dc.tes.dom.util.MsgLoader;
import com.dc.tes.util.MD5;


/**
 * SQL查询及数据查询功能
 * 
 * @author Huangzx
 * 
 */

public class DbGet {
	
	private static final Log log = LogFactory.getLog(DbGet.class);
	
	public static SysType m_sysType; 
	public static User m_user; 
	public static int m_iRoundId = 0; 

	
	public static SysType getSystemBySystemName(String sysname) {
		
		SysType sysType = DALFactory.GetBeanDAL(SysType.class).Get(Op.EQ("systemName", sysname));
		return sysType;
	}
	
	public static int getSystemIdBySystemName(String sysname) {
		
		SysType sys = DALFactory.GetBeanDAL(SysType.class).Get(Op.EQ("systemName", sysname));
		if (sys == null) {
			return 0;
		}
		String strSystemId = sys.getSystemId();
		int iSystemId = 0;
		try {
			iSystemId = Integer.parseInt(strSystemId);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		return iSystemId;
	}
	
	public static List<SysType> getAllValidSystems() {
		
		List<SysType> sysList = DALFactory.GetBeanDAL(SysType.class).ListAll(Op.EQ("flag", 1));

		return sysList;
	}
	
	
	public static String getRecordedResonseMsgByRequestMsg(String requestMsg) {
		
		List<RecordedCase> rcList = DALFactory.GetBeanDAL(RecordedCase.class).ListAll(Op.EQ("requestMsg", requestMsg));
		if (rcList == null) {
			return null;
		}
		/*if (rcList.get(0) == null) {
			return null;
		}*/
		for (int i=0; i < rcList.size(); i++) {
			RecordedCase rc = rcList.get(i);
			if (rc != null && rc.getResponseMsg() != null)
				return rc.getResponseMsg();
		}
		return null;
	}
	
	

	public static CaseInstance getCaseInstacneByRequestMsg(String requestMsg) {
		
		List<CaseInstance> ciList = DALFactory.GetBeanDAL(CaseInstance.class).ListAll(Op.EQ("requestMsg", requestMsg));
		if (ciList == null) {
			return null;
		}
		if (ciList.get(0) == null) {
			return null;
		}
		for (int i=0; i < ciList.size(); i++) {
			CaseInstance ci = ciList.get(i);
			if (ci != null && ci.getResponseMsg() != null)
			return ci;
		}
		return null;
	}

	public static String getCaseInstacneResonseMsgByRequestMsg(String requestMsg) {
		
		List<CaseInstance> ciList = DALFactory.GetBeanDAL(CaseInstance.class).ListAll(Op.EQ("requestMsg", requestMsg));
		if (ciList == null) {
			return null;
		}
		if (ciList.get(0) == null) {
			return null;
		}
		for (int i=0; i < ciList.size(); i++) {
			CaseInstance ci = ciList.get(i);
			if (ci != null && ci.getResponseMsg() != null)
			return ci.getResponseMsg();
		}
		return null;
	}
	
	public static boolean isSystemHasExecutePlan(String strSystemId) {
		
		List<ExecutePlan> executePlanList = DALFactory.GetBeanDAL(ExecutePlan.class).ListAll(Op.EQ("systemId", strSystemId), Op.EQ("status", 1));
		if (executePlanList != null && executePlanList.size() > 0) {
			return true;
		}
		return false;
	}
	
	public static boolean isParamNameInList(String strParamName,
			List<TransactionDynamicParameter> transAllParamList) {
		
		String[] strFixedDataParamNameList = {
				"[CASEINSTANCEID]", 
				"[CASEID]", 
				"[CASENO]", 
				"[CASENAME]", 
				"[IMPORTBATCHNO]", 
				"[BUSINESSFLOWID]", 
				"[BUSINESSFLOWNO]", 
				"[BUSINESSFLOWNAME]", 
				"[TRANSACTIONID]", 
				"[TRANSACTIONCODE]", 
				"[TRANSACTIONNAME]", 
				"[TRANSACTIONCATEGORYID]", 
				"[TRANSACTION_CATEGORY]", 
				"[CASEFLOWSTEP]"
		};
		
		for (int i=0; i<strFixedDataParamNameList.length; i++) {
			if (strFixedDataParamNameList[i].equals(strParamName)) {
				return true;
			}
		}
		
		if (transAllParamList == null) {
			return false;
		}
		
		for (int i=0; i<transAllParamList.size(); i++) {
			TransactionDynamicParameter transParam = transAllParamList.get(i);
			if (transParam == null) {
				return false;
			}
			SystemDynamicParameter systemParam = transParam.getSystemParameter();
			if (systemParam == null) {
				return false;
			}
			if (systemParam.getName().contains(strParamName)) {
				return true;
			}
		}
		
		return false;	
	}
	

		
	public static boolean isParam1DependOnParam2(SystemDynamicParameter sysParam1, SystemDynamicParameter sysParam2) {
		//1依赖于2，掉个个
		
		//String strParamName1 = sysParam1.getParameterName();
		String strParamName2 = sysParam2.getName();
		String strExpression1 = sysParam1.getParameterExpression();
		//String strExpression2 = sysParam2.getParameterExpression();
		
		int iParameterType = Integer.parseInt(sysParam1.getParameterType());
		if (iParameterType == 1) { //SQL类参数，去掉FROM前面的select语句段
			 //提取FROM后面的参数
			 String strUpperSql = strExpression1.toUpperCase();
			 int iPosOfFrom =strUpperSql.indexOf("FROM");
			 if (iPosOfFrom > 0) {
				 strExpression1 = strExpression1.substring(iPosOfFrom + 4);		 
			 }
		}
		
		if (strExpression1.contains(strParamName2))
			return true;

		return false;
	}
	
	
	public static Case GetCaseByCaseId(String sCaseId) {
		
		Case c = DALFactory.GetBeanDAL(Case.class).Get(Op.EQ("caseId", sCaseId));
		return c;
	}
	
	public static boolean isCaseInFlow(int iCaseId) {
		
		Case c = DALFactory.GetBeanDAL(Case.class).Get(Op.EQ("caseId", String.valueOf(iCaseId)));
		CaseFlow caseFlow = c.getCaseFlow();
		if (caseFlow != null && caseFlow.getId() > 0)
			return true;
		
		return false;
	}
	

	public static CaseInstance getInterupptedFlowCaseInstance(int iExecuteLogId, int iCaseId) {
		
		//定位CaseInstance
		IDAL<CaseInstance> ciDAL = DALFactory.GetBeanDAL(CaseInstance.class);
		CaseInstance ci = ciDAL.Get(Op.EQ("caseId", iCaseId), Op.EQ("executeLogId",  iExecuteLogId));
		if (ci == null) {
			return null;
		}

		//清除断点
		if (1 == ci.getBreakPointFlag()) { //当前案例实例中是否有断点？
			ci.setBreakPointFlag(0);
			ci.setCasePassFlag(2);
			ci.setBeginRunTime(new Date());
		}
		ci.setCasePassFlag(2); //已发送
	
		ciDAL.Edit(ci);
		return ci;
	}


	
	@SuppressWarnings("unchecked")
	public static String GetInternalSqlQueryingResult(String strSql) {
		
		String strSqlResult = "";
		
		try {
			IDAL<SysType> bealDal = HibernateDALFactory.GetBeanDAL(SysType.class);
			List<String> strSqlResultList = (List<String>) bealDal.sqlQuery(strSql);

			if (strSqlResultList.size() <= 0) {
				return "";
			}
			strSqlResult = strSqlResultList.get(0);
		} catch (Exception e) {
			System.out.print(e.getMessage());
		}

		return strSqlResult;
	}

	
	//找案例的预期值设置记录
	public static Map<String, CaseParameterExpectedValue> getCaseFieldExpectedParamValue(String  strCaseId) {
		
		Map<String, CaseParameterExpectedValue> caseExpectedParamValueList = new HashMap<String, CaseParameterExpectedValue>();
		
		IDAL<CaseParameterExpectedValue> cpevDAL = DALFactory.GetBeanDAL(CaseParameterExpectedValue.class);
		//获取案例下的所有的预期值设置记录
		List<CaseParameterExpectedValue> cpevList = cpevDAL.ListAll(Op.EQ("caseId", strCaseId));
		
		if (cpevList == null) {
			return null;
		}
		
		for (int i=0; i<cpevList.size(); i++) {
			CaseParameterExpectedValue cpev = cpevList.get(i);
			if (cpev != null && cpev.getExpectedValue() != null) {
				TransactionDynamicParameter transParam = cpev.getTransParameter();
				if (transParam != null) {
					SystemDynamicParameter sysParam = transParam.getSystemParameter();
					if (sysParam != null) {
						//caseExpectedParamValueList.put(sysParam.getName(), cpev);
						caseExpectedParamValueList.put(transParam.getId(), cpev);
					}
				}
			}
		}
		
		return caseExpectedParamValueList;
	}
	
	//根据给定的参数名和案例实例查找并返回具体的参数值
	public static String getCaseInstanceParameterValue(String strParam, TransactionDynamicParameter transParam, CaseInstance caseInstance) {

		//先查字段参数
		IDAL<CaseInstanceFieldValue> cifvDAL = DALFactory.GetBeanDAL(CaseInstanceFieldValue.class);
		CaseInstanceFieldValue cifv = cifvDAL.Get(Op.EQ("caseInstanceId", caseInstance.getId()), Op.EQ("transParameter", transParam));
		if (cifv != null) {
			return cifv.getMsgFieldValue();
		}
		
		//查不到，再查SQL参数
		int iStep = caseInstance.getSequence();
		int iPos = strParam.indexOf(".");
		if (iPos > 0) {
			String strStep = strParam.substring(0, iPos);
			if (!Utility.isDigitString(strStep)) {
				log.error("预期值设置有误，请检查：" + strParam);
				return strParam;
			}
			iStep = Integer.parseInt(strStep);
		}
		
		IDAL<CaseInstanceSqlValue> cisvDAL = DALFactory.GetBeanDAL(CaseInstanceSqlValue.class);
		CaseInstanceSqlValue cisv = cisvDAL.Get(Op.EQ("caseInstanceId", caseInstance.getId()), Op.EQ("transParameter", transParam), Op.EQ("caseFlowStep", iStep));
		if (cisv != null) {
			return cisv.getRealValue();
		}
		
		return "";
	}

	
	//根据给定的参数名和案例实例查找并返回具体的参数值
	/*public static String getCaseInstanceParameterValue(String strParam, CaseInstance caseInstance) {

		//先查字段参数
		IDAL<CaseInstanceFieldValue> cifvDAL = DALFactory.GetBeanDAL(CaseInstanceFieldValue.class);
		CaseInstanceFieldValue cifv = cifvDAL.Get(Op.EQ("caseInstanceId", caseInstance.getId()), Op.EQ("msgFieldName", strParam));
		if (cifv != null) {
			return cifv.getMsgFieldValue();
		}
		log.error("参数: "+strParam +"没在数据库中");
		
		//预期值表达式里 出现了完整的参数表达式 而 不是 它的参数名     by ljs
		try {
			MsgDocument inDoc = MsgLoader.LoadXml(caseInstance.getResponseXml());
			String msgFieldValue = ((MsgField)inDoc.SelectSingleField(strParam)).value();
			if(msgFieldValue != null)
				return msgFieldValue;
		} catch (Exception e) {
			log.error("解析预期值表达式:没有获取到报文字段 "+strParam);
		}

		
		//查不到，再查SQL参数
		int iStep = caseInstance.getSequence();
		int iPos = strParam.indexOf(".");
		if (iPos > 0) {
			String strStep = strParam.substring(0, iPos);
			if (!Utility.isDigitString(strStep)) {
				log.error("预期值设置有误，请检查：" + strParam);
				return strParam;
			}
			iStep = Integer.parseInt(strStep);
		}
		
		IDAL<CaseInstanceSqlValue> cisvDAL = DALFactory.GetBeanDAL(CaseInstanceSqlValue.class);
		CaseInstanceSqlValue cisv = cisvDAL.Get(Op.EQ("caseInstanceId", caseInstance.getId()), Op.EQ("parameterName", strParam), Op.EQ("caseFlowStep", iStep));
		if (cisv != null) {
			return cisv.getRealValue();
		}
		
		return strParam;
	}*/
	
	//根据已知的 系统参数定义来获取 交易参数定义
	public static TransactionDynamicParameter getTransactionParameterBySystemParameter(SystemDynamicParameter systemParam, TransactionDynamicParameter[] transNonFieldAllParams) {
		//逐个处理非字段参数
		for (int i = 0; i < transNonFieldAllParams.length; i++) {
			SystemDynamicParameter nonFieldSystemParam = transNonFieldAllParams[i].getSystemParameter();
			if (systemParam != null && nonFieldSystemParam != null && systemParam.getId().equals(nonFieldSystemParam.getId()))
			{
				return transNonFieldAllParams[i];
			}
		}

		return null;
	}

	//获取单个交易的交易参数列表
	public static List<TransactionDynamicParameter> getTransactionParameterList(String sTransactionId) {
			
		//遍历交易所有的参数
		IDAL<TransactionDynamicParameter> transParamDAL = DALFactory.GetBeanDAL(TransactionDynamicParameter.class);
		List<TransactionDynamicParameter> transParamList = transParamDAL.ListAll(Op.EQ("transactionId", sTransactionId));
	  	
		return transParamList;
	}
	
	
	public static List<TransactionDynamicParameter> getTransactionParameterList(Transaction trans) {
		
		return getTransactionParameterList(trans.getTransactionId());
	}
	
	public static TransactionDynamicParameter getTransactionDynamicParameter(String sTransactionId, String strParam, String strSystemId) {
		
		String strTransactionParameterId = getTransactionParameterIdByParameterName(strParam, sTransactionId, strSystemId);
		if (strTransactionParameterId == null || strTransactionParameterId.isEmpty())
			return null;
		
		IDAL<TransactionDynamicParameter> transParamDAL = DALFactory.GetBeanDAL(TransactionDynamicParameter.class);
		TransactionDynamicParameter transactionDynamicParameter = transParamDAL.Get(Op.EQ("id", strTransactionParameterId));
		return transactionDynamicParameter;
	}
	
	public static String getTransactionParameterIdByParameterName(String strParameterName, String TransactionId, String strSystemId) {
		
		SystemDynamicParameter systemDynamicParameter = getSystemParameterByParameterName(strParameterName, strSystemId);
		if (systemDynamicParameter == null) {
			return null;
		}
		return getTransactionParameterIdBySystemParameter(TransactionId, systemDynamicParameter);
	}
	
	// 根据系统参数名称获取系统参数
	public static SystemDynamicParameter getSystemParameterByParameterName(String strParameterName, String strSystemId) {

		IDAL<SystemDynamicParameter> sysParamDAL = DALFactory.GetBeanDAL(SystemDynamicParameter.class);
		SystemDynamicParameter systemDynamicParameter = sysParamDAL.Get(Op.EQ("systemId", strSystemId), Op.EQ("name", strParameterName));
		return systemDynamicParameter;
	}

	
	// 根据系统参数名称获取系统参数ID
	public static String getTransactionParameterIdBySystemParameter(String TransactionId, SystemDynamicParameter systemDynamicParameter) {
		
		IDAL<TransactionDynamicParameter> transParamDAL = DALFactory.GetBeanDAL(TransactionDynamicParameter.class);
		TransactionDynamicParameter transactionDynamicParameter = transParamDAL.Get(Op.EQ("transactionId", TransactionId), Op.EQ("systemParameter", systemDynamicParameter));
		if (transactionDynamicParameter == null)
			return null;
		return transactionDynamicParameter.getId();
	}
	

	//获取执行集根据给定的执行集ID
	public static ExecuteLog getExecuteLogByExecuteLogId(int iExecuteLogId) {
		
		IDAL<ExecuteLog> cfDAL = DALFactory.GetBeanDAL(ExecuteLog.class);
		ExecuteLog executeLog = cfDAL.Get(Op.EQ("id", iExecuteLogId));
		return executeLog;
	}

	//获取业务流根据给定的业务流ID
	public static CaseFlow getCaseFlowByCaseFlowId(int iCaseFlowId) {
		
		IDAL<CaseFlow> cfDAL = DALFactory.GetBeanDAL(CaseFlow.class);
		CaseFlow caseFlow = cfDAL.Get(Op.EQ("id", iCaseFlowId));
		return caseFlow;
	}
	

	public static Transaction getResponseTransctionByTranCode(String N_TranCode, String systemId) {
		
		IDAL<Transaction> transDAL = DALFactory.GetBeanDAL(Transaction.class);
		List<Transaction> transList = transDAL.ListAll(Op.EQ(Transaction.N_SystemID, systemId), Op.EQ(Transaction.N_TranCode, N_TranCode));
		if (transList == null || transList.size() == 0) {
			return null;
		}
		for (int i=0; i< transList.size(); i++) {
			Transaction trans = transList.get(i);
			if (trans != null && trans.getResponseStruct() != null) {
				return trans;
			}
		}
		return null;
	}
	

	public static Case getTransctionDefaultCase(String transactionId) {
		
		IDAL<Case> caseDAL = DALFactory.GetBeanDAL(Case.class);
		List<Case> caseList = caseDAL.ListAll(Op.EQ("transactionId", transactionId), Op.EQ("isdefault", 1));
		if (caseList == null || caseList.size() == 0) {
			return null;
		}
		for (int i=0; i< caseList.size(); i++) {
			Case c = caseList.get(i);
			if (c != null && c.getResponseMsg() != null) {
				return c;
			}
		}
		return caseList.get(0);
	}
	
	public static Case getTransctionNonDefaultCase(String transactionId) {
		
		IDAL<Case> caseDAL = DALFactory.GetBeanDAL(Case.class);
		List<Case> caseList = caseDAL.ListAll(Op.EQ("transactionId", transactionId), Op.EQ("isdefault", 0));
		if (caseList == null || caseList.size() == 0) {
			return null;
		}
		for (int i=0; i< caseList.size(); i++) {
			Case c = caseList.get(i);
			if (c != null && c.getResponseMsg() != null) {
				return c;
			}
		}
		return caseList.get(0);
	}

	//获取给定业务流第x步的业务流实例
	public static Transaction getTransctionByTransactionId(String sTransactionId) {
		
		IDAL<Transaction> transDAL = DALFactory.GetBeanDAL(Transaction.class);
		Transaction trans = transDAL.Get(Op.EQ("transactionId", sTransactionId));
		return trans;
	}

	//获取给定业务流第x步的业务流实例
	public static CaseFlowInstance getCaseFlowInstanceByFlowInstanceId(int iCaseFlowInstanceId) {
		
		IDAL<CaseFlowInstance> cfiDAL = DALFactory.GetBeanDAL(CaseFlowInstance.class);
		CaseFlowInstance cfi = cfiDAL.Get(Op.EQ("id", iCaseFlowInstanceId));
		return cfi;
	}
	
	public static CaseFlowInstance getCaseFlowInstanceByExecuteCase(int iExecuteLogId, int iCaseFlowId) {
		
		IDAL<CaseFlowInstance> cfiDAL = DALFactory.GetBeanDAL(CaseFlowInstance.class);
		CaseFlowInstance cfi = cfiDAL.Get(Op.EQ("executeLogId", iExecuteLogId), Op.EQ("caseFlowId", iCaseFlowId));
		return cfi;
	}
	
	//获取给定的ExecuteLogId和 CaseId获取CaseInstance
	public static CaseInstance getCaseInstanceByExecuteCase(int iExecuteLogId, int iCaseId) {
			
		IDAL<CaseInstance> ciDAL = DALFactory.GetBeanDAL(CaseInstance.class);
		CaseInstance ci = ciDAL.Get(Op.EQ("caseId", iCaseId), Op.EQ("executeLogId", iExecuteLogId));
		return ci;
	}
	
	//获取给定的ExecuteLogId、CaseFlowInstance,Sequence获取CaseInstance
	public static CaseInstance getCaseInstanceByExecuteSequence(int iExecuteLogId, CaseFlowInstance cfi,int Sequence) {
			
		IDAL<CaseInstance> ciDAL = DALFactory.GetBeanDAL(CaseInstance.class);
		CaseInstance ci = ciDAL.Get(Op.EQ("executeLogId", iExecuteLogId), Op.EQ("caseFlowInstance", cfi),Op.EQ("sequence", Sequence));
		return ci;
	}
	
	//获取给定业务流第x步的案例实例
	public static CaseInstance getCaseInstanceByFlowCase(int iCaseFlowInstanceId, Case c) {
		
		//业务流实例
		CaseFlowInstance cfi = getCaseFlowInstanceByFlowInstanceId(iCaseFlowInstanceId);
		
		IDAL<CaseInstance> ciDAL = DALFactory.GetBeanDAL(CaseInstance.class);
		CaseInstance ci = ciDAL.Get(Op.EQ("caseId", Integer.parseInt(c.getCaseId())), Op.EQ("caseFlowInstance",  cfi));
		return ci;
	}
	
	//获取业务流第x步的案例
	public static Case getFlowCaseByStepNo(CaseFlow caseFlow, int iCaseStep) {
		
		IDAL<Case> caseDAL = DALFactory.GetBeanDAL(Case.class);
		Case c = caseDAL.Get(Op.EQ("caseFlow", caseFlow), Op.EQ("sequence", iCaseStep));
		return c;
	}
	
	
	public static String getTransactionParameterValue(TransactionDynamicParameter transParam, int iCaseInstanceId, int iPreviousCaseStep) {

		if (transParam == null || transParam.getSystemParameter() == null || transParam.getSystemParameter().getParameterType() == null) {
			return null;
		}
		
		String strPreviousParamValue = "";
		
		int iParameterType = Integer.parseInt(transParam.getSystemParameter().getParameterType());
		if (iParameterType == 1)
		{	//SQL参数
			IDAL<CaseInstanceSqlValue> cisvDAL = DALFactory.GetBeanDAL(CaseInstanceSqlValue.class);
			CaseInstanceSqlValue caseInstParamValue = cisvDAL.Get(Op.EQ("caseInstanceId", iCaseInstanceId), Op.EQ("transParameter", transParam), Op.EQ("caseFlowStep", iPreviousCaseStep));
			if (caseInstParamValue == null) {
				return null;
			}
			strPreviousParamValue = caseInstParamValue.getRealValue();
		}
		else { //非SQL参数
			IDAL<CaseInstanceFieldValue> cifvDAL = DALFactory.GetBeanDAL(CaseInstanceFieldValue.class);
			CaseInstanceFieldValue caseInstParamValue = cifvDAL.Get(Op.EQ("caseInstanceId", iCaseInstanceId), Op.EQ("transParameter", transParam));
			if (caseInstParamValue == null) {
				return null;
			}
			strPreviousParamValue = caseInstParamValue.getMsgFieldValue();
		}
		
		return strPreviousParamValue;
	}
	
		
	

	public static void getCardHostIpAddress(StringBuffer sbHostIpAddress, StringBuffer sbPortNum,  String strDbHost, String systemId)
	{
		String strHostIpAddress = ""; 
		String strPortNum = "8888";
		
		//清空数据先
		sbHostIpAddress.delete(0, sbHostIpAddress.length());
		sbPortNum.delete(0, sbPortNum.length());
		
		if (strDbHost != null && !strDbHost.isEmpty()) {
			//定位主机
			strDbHost = strDbHost.toUpperCase();
			IDAL<DbHost> dbHostDAL = DALFactory.GetBeanDAL(DbHost.class);
			DbHost dbHost = dbHostDAL.Get(Op.EQ("dbHostName", strDbHost), Op.EQ("systemId", systemId));
	
			//取主机IP
			strHostIpAddress = dbHost.getIpaddress();

			//取主机端口号	
			int iPortNum = dbHost.getPortnum();
			strPortNum = String.valueOf(iPortNum);
		}

		sbHostIpAddress.append(strHostIpAddress);
		sbPortNum.append(strPortNum);
	}
	
	

	public static DbHost getDbHostByCardId(int iCardId) {
		
		IDAL<Card> cardDal = HibernateDALFactory.GetBeanDAL(Card.class);
		Card card = cardDal.Get(Op.EQ("id", iCardId));
		if (card == null) {
			return null;
		}
		String dbHostName = card.getDbHost();
		
		IDAL<DbHost> hostDal = HibernateDALFactory.GetBeanDAL(DbHost.class);
		DbHost dbHost = hostDal.Get(Op.EQ("dbHostName", dbHostName));
		return dbHost;
	}
	
	
	public static DbHost getDbHostByIpAddr(String sIpAddr, String strSystemId) {
				
		IDAL<DbHost> hostDal = HibernateDALFactory.GetBeanDAL(DbHost.class);
		DbHost dbHost = hostDal.Get(Op.EQ("systemId", strSystemId), Op.EQ("ipaddress", sIpAddr));
		return dbHost;
	}
	

	
	//单个的案例的最大超时时间
	public static int getCaseTransactionTimeOut(CaseInstance ci) {
		
		return getTransactionTimeOut(ci); //+20
	}

	
	// 业务流的最大超时时间
	public static int getFlowTransactionTimeOut(CaseInstance ci, CaseFlowInstance cfi) {

		int iNeedSqlCheck = 0;
		SysType sysType = getSysTypeByCaseInstance(ci);
		if (sysType != null) {
			iNeedSqlCheck = sysType.getNeedSqlCheck();			
		}
		
		//系统默认的超时时间
		int iTransactionTimeOut = getTransactionTimeOut(ci);
		int iTotalSqlDelayTime = 0;
		int iFlowTimeOut = 0;
		
		IDAL<CaseFlow> cfDAL = DALFactory.GetBeanDAL(CaseFlow.class);
		CaseFlow caseFlow = cfDAL.Get(Op.EQ("id", cfi.getCaseFlowId())); 
				
		IDAL<Case> caseDAL = DALFactory.GetBeanDAL(Case.class);
		List<Case> caseList = caseDAL.ListAll(Op.EQ("caseFlow", caseFlow));
		for (int i=0; i<caseList.size(); i++) {
			Case c = caseList.get(i);
			IDAL<Transaction> transDAL = DALFactory.GetBeanDAL(Transaction.class);
			Transaction trans = transDAL.Get(Op.EQ("transactionId", c.getTransactionId()));
			iTotalSqlDelayTime += trans.getSqlDelayTime();
		}
		
		//iFlowTimeOut = (caseList.size() / 2) * iTransactionTimeOut + 2 * caseList.size();
		iFlowTimeOut = caseList.size() * iTransactionTimeOut;
		
		if (1 == iNeedSqlCheck) {
			iFlowTimeOut += iTotalSqlDelayTime;
		}
		
		return iFlowTimeOut;
	}
	
	private static int getTransactionTimeOut(CaseInstance ci) {
		
		int iTransactionTimeOut = 10;
		
		SysType sysType = getSysTypeByCaseInstance(ci);
		if (sysType != null) {
			iTransactionTimeOut = getSystemDefaultTimeOut(sysType);			
		}
		
		return iTransactionTimeOut;
	}
	

	private static int getSystemDefaultTimeOut(SysType sysType) {
	
		return sysType.getTransactionTimeOut();			
	}
	
	
	private static SysType getSysTypeByCaseInstance(CaseInstance ci) {
			
		IDAL<Case> caseDAL = DALFactory.GetBeanDAL(Case.class);
		Case c = caseDAL.Get(Op.EQ("caseId", ci.getCaseId().toString()));
		
		if (c != null) {	
			IDAL<Transaction> transDAL = DALFactory.GetBeanDAL(Transaction.class);
			Transaction trans = transDAL.Get(Op.EQ("transactionId", c.getTransactionId()));
			if (trans != null) {
				IDAL<SysType> ciDAL = DALFactory.GetBeanDAL(SysType.class);
				SysType sysType = ciDAL.Get(Op.EQ("systemId", trans.getSystemId()));
				return sysType;
			}
		}
		
		return null;
	}
	
	public static Transaction getTransactionByCaseID(String caseid) {
		Case casebean = DALFactory.GetBeanDAL(Case.class).Get(Op.EQ("caseId", caseid));
		Transaction t = DALFactory.GetBeanDAL(Transaction.class).Get(Op.EQ(Transaction.N_TransID, casebean.getTransactionId()));
		return t;
	}
	

	//new added for autorun
	
	public static int GetTestRoundId(String sSystemId) {
		
		List<TestRound> testRoundList = getCurrentTestRound(sSystemId);
		if (testRoundList != null && testRoundList.size() > 0) {
			if (testRoundList.size() > 1) {
				System.out.println("轮次配置错误，当前系统中存在两条“是否为当前轮次”配置为true的纪录，请检查轮次配置数据！");
				return 0;
			}
			if (testRoundList.get(0) != null) {
				return testRoundList.get(0).getRoundId();
			}
		}
		return 0;
	}
	
	public static List<TestRound> getCurrentTestRound(String sSystemId) {
		
		IDAL<TestRound> iDAL = DALFactory.GetBeanDAL(TestRound.class);
		List<TestRound> testRoundList = iDAL.ListAll(Op.EQ("systemId", sSystemId), Op.EQ("currentRoundFlag", 1));
				
		return testRoundList;
	}
	
	public static User getUserByUserName(String sUserName) {
		
		IDAL<User> userDAL = DALFactory.GetBeanDAL(User.class);
		User u = userDAL.Get(Op.EQ("name", sUserName));
		
		if (u != null) {	
			return u;
		}
		
		return null;
	}
	
	public static boolean isUserPasswordMactched(String sUserName, String sPassWord) {
		
		String strEncodedPwd = MD5.encode(sPassWord);
		IDAL<User> userDAL = DALFactory.GetBeanDAL(User.class);
		User u = userDAL.Get(Op.EQ("name", sUserName), Op.EQ("password", strEncodedPwd));
		
		if (u != null) {	
			return true;
		}
		else { 
			return false;
		}
	}
	
	public static List<UserRSystem> getUserSystemByUserId(String userId) {
		
		IDAL<UserRSystem> usDAL = DALFactory.GetBeanDAL(UserRSystem.class);
		List<UserRSystem> urS = usDAL.ListAll(Op.EQ("userid", userId));
		
		if (urS != null) {	
			return urS;
		}
		
		return null;
	}

	public static SysType getSysTypeBySysTypeId(String sSysTypeId) {
		
		IDAL<SysType> sysDAL = DALFactory.GetBeanDAL(SysType.class);
		SysType sysType = sysDAL.Get(Op.EQ("systemId", sSysTypeId));
		if (sysType.getEncoding4RequestMsg() == null || sysType.getEncoding4RequestMsg().isEmpty()) {
			sysType.setEncoding4RequestMsg("utf-8");
		}
		if (sysType.getEncoding4ResponseMsg() == null || sysType.getEncoding4ResponseMsg().isEmpty()) {
			sysType.setEncoding4ResponseMsg("utf-8");
		}
		return sysType;
	}
	
	
	public static List<ExecutePlan> getExecutePlan(String sSysTypeId, int iScheduleMode) {
		
		try {
			IDAL<ExecutePlan> execPlanDAL = DALFactory.GetBeanDAL(ExecutePlan.class);
			List<ExecutePlan> executePlanList = execPlanDAL.ListAll(Op.EQ("systemId", sSysTypeId), Op.EQ("scheduleRunMode", iScheduleMode), Op.EQ("status", 1));
			return executePlanList;
		}
		catch(Exception e) {
			System.out.println(e.getMessage());
		}
		return null;
	}
	

	public static List<ExecuteSetExecutePlan> getExecuteSetExecutePlan(Integer iExecutePlanId) {
		
		try {
			IDAL<ExecuteSetExecutePlan> executeSetExecutePlan = DALFactory.GetBeanDAL(ExecuteSetExecutePlan.class);
			List<ExecuteSetExecutePlan> executeSetExecutePlanList = executeSetExecutePlan.ListAll(Op.EQ("executePlanId", iExecutePlanId));
			return executeSetExecutePlanList;
		}
		catch(Exception e) {
			System.out.println(e.getMessage());
		}
		return null;
	}
	

	public static Integer getExecuteSetIdByExecuteSetDirId(Integer iExecuteSetDirId) {
		
		IDAL<ExecuteSetDirectory> iDAL = DALFactory.GetBeanDAL(ExecuteSetDirectory.class);
		ExecuteSetDirectory t = iDAL.Get(Op.EQ("id", iExecuteSetDirId));
		
		if (t != null) {	
			return t.getExecuteSetId();
		}
		
		return null;
	}
	
	public static List<ExecuteSetTaskItem> getExecuteSetTaskItemByExecuteSet(ExecuteSet executeSet) {
		
		try {
			IDAL<ExecuteSetTaskItem> iDAL = DALFactory.GetBeanDAL(ExecuteSetTaskItem.class);
			List<ExecuteSetTaskItem> executeSetTaskItemList = iDAL.ListAll(Op.EQ("executeSet", executeSet));
			return executeSetTaskItemList;
		}
		catch(Exception e) {
			System.out.println(e.getMessage());
		}
		return null;
	}

	public static ScriptFlow getScriptFlowByScriptFlowId(String id) {
		
		IDAL<ScriptFlow> iDAL = DALFactory.GetBeanDAL(ScriptFlow.class);
		ScriptFlow scriptFlow = iDAL.Get(Op.EQ("id", id));
		return scriptFlow;
	}
	
	
	public static boolean isChannelExist(String sSystemId, String sChannelName) {
		
		IDAL<Channel> userDAL = DALFactory.GetBeanDAL(Channel.class);
		Channel channel = userDAL.Get(Op.EQ("systemId", sSystemId), Op.EQ("name", sChannelName));
		
		if (channel != null) {	
			return true;
		}
		else { 
			return false;
		}
	}
	

	public static boolean isDisabledCaseFlow(String script) {
		
		if (script == null || script.isEmpty()) {
			return false;
		}
		if (!script.contains("run_caseflow")) {
			return false;
		}
		
		String params[] = script.split(",");
		if (params == null || params.length != 2) {
			return false;
		}
		
		String caseFlowNo = params[1].replace("\"", "");
		if (caseFlowNo == null || caseFlowNo.isEmpty()) {
			return false;
		}
		caseFlowNo = caseFlowNo.replace(")", "");
		if (caseFlowNo == null || caseFlowNo.isEmpty()) {
			return false;
		}
		
		IDAL<CaseFlow> cfDAL = DALFactory.GetBeanDAL(CaseFlow.class);
		CaseFlow caseFlow = cfDAL.Get(Op.EQ("caseFlowNo", caseFlowNo));
		
		if (caseFlow != null && 1 == caseFlow.getDisabledFlag()) {	
			return true;
		}
		
		return false;
	}
	
	
	public static boolean IsTransParamValid(TransactionDynamicParameter transParam, SystemDynamicParameter sysParam, FileOutputStream out) {
		
		String sTransactionId = transParam.getTransactionId();
		IDAL<Transaction> transDAL = DALFactory.GetBeanDAL(Transaction.class);
		Transaction trans = transDAL.Get(Op.EQ(Transaction.N_TransID, sTransactionId));
	
		String strTransMsgStruct = "";
		int paramFromMsgSrc = sysParam.getParamFromMsgSrc();
		if (paramFromMsgSrc == 2) { //2：从下传报文中获取
			strTransMsgStruct = trans.getResponseStruct();
			if (1 == m_sysType.getUseSameResponseStruct()) {
				strTransMsgStruct = m_sysType.getResponseStruct(); 
			}
		}
		else if (paramFromMsgSrc == 1) { //1：从上传报文中获取
			strTransMsgStruct = trans.getRequestStruct();
		}
		if (strTransMsgStruct == null || strTransMsgStruct.isEmpty()) { //没有定义报文
			return false;
		}
		String strParameterExpression = sysParam.getParameterExpression();
		if (strParameterExpression == null || strParameterExpression.isEmpty()) {
			return false;
		}
		String parameterExpression = strParameterExpression.replace(".i.", ".0.");
		parameterExpression = parameterExpression.replace(".n.", ".0.");
		
		MsgDocument msgDoc = MsgLoader.LoadXml(strTransMsgStruct);
		try {
			//报文参数，从报文中获取实际值
			String msgFieldValue = ((MsgField)msgDoc.SelectSingleField(parameterExpression)).value();
			return true;
		}
		catch(Exception e) {
			System.out.println("获取参数出错，交易名称［" + trans.getTranName() + "］，参数名称［" + sysParam.getName() + "］，参数表达式［" + strParameterExpression + "］错误提示信息：" + e.getMessage());
			StringBuffer row = new StringBuffer();
			row.append("交易名称［" + trans.getTranName() + "］，参数名称［" + sysParam.getName() + "］，参数表达式［" + strParameterExpression + "］");
			row.append("\n");
			try {
				out.write(row.toString().getBytes());
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			//WriteOneLine2File(file, row.toString());
			return false;
		}
	}
	
	public static int CheckOneSystemParameterConfig(SystemDynamicParameter sysParam, FileOutputStream out) {
		
		int iInvalidTransParamCount = 0; 
		
		IDAL<TransactionDynamicParameter> transParamDAL = DALFactory.GetBeanDAL(TransactionDynamicParameter.class);
		List<TransactionDynamicParameter> transParamList = transParamDAL.ListAll(Op.EQ("systemParameter", sysParam));
		if (transParamList != null) {
			for (int i=0; i<transParamList.size(); i++) {
				TransactionDynamicParameter transParam = transParamList.get(i);
				if (!IsTransParamValid(transParam, sysParam, out)) {
					//transParamDAL.Del(transParam);
					iInvalidTransParamCount ++;
				}
			}
		}
		return iInvalidTransParamCount;
	}
	
	
	public static int CheckSystemParameterConfig(StringBuilder sbInvalidSystemParamCount) {
		
		int iInvalidSystemParamCount = 0, iTotalInvalidTransParamCount = 0;
		
		//删除文件（如有）
		File file = new File("PossibleInvaildSystemParameters" + m_sysType.getSystemId() + ".txt");
		if (file.exists()) {
			try { 	  
				file.delete();
			}		 
			catch(Exception e) {
				e.printStackTrace();
			}
		}
		//创建文件
		if (!file.exists()) {
			try { 	  
				file.createNewFile();
			}		 
			catch(Exception e) {
				e.printStackTrace();
			}
		}

		FileOutputStream out = null;
		try {
			out = new FileOutputStream(file, true);
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		
		IDAL<SystemDynamicParameter> sysParamDAL = DALFactory.GetBeanDAL(SystemDynamicParameter.class);
		List<SystemDynamicParameter> sysParamList = sysParamDAL.ListAll(Op.EQ("systemId", DbGet.m_sysType.getSystemId()), Op.EQ("isValid", "1"));
		if (sysParamList != null) {
			for (int i=0; i<sysParamList.size(); i++) {
				SystemDynamicParameter sysParam = sysParamList.get(i);
				String parameterType = sysParam.getParameterType();
				int iParameterType = 0;
				try {
					iParameterType = Integer.parseInt(parameterType);
				}
				catch(Exception e) {
					System.out.println(e.getMessage());
					continue;
				}
				if (iParameterType != 0) { //目前只检查报文类参数
					continue;
				}
				//String strParamExpr = sysParam.getParameterExpression();
				int iInvalidTransParamCount = CheckOneSystemParameterConfig(sysParam, out);
				if (iInvalidTransParamCount > 0) {
					//DbSet.updateSystemParameter2Invalid(sysParam);
					iInvalidSystemParamCount ++;
				}
				iTotalInvalidTransParamCount += iInvalidTransParamCount;
			}
		}

		if (sbInvalidSystemParamCount != null && sbInvalidSystemParamCount.length() > 0) {
			sbInvalidSystemParamCount.delete(0, sbInvalidSystemParamCount.length());
		}

		sbInvalidSystemParamCount.append(iTotalInvalidTransParamCount);

 
		String sumStr = "系统参数配置检查共发现 " + iInvalidSystemParamCount + " 个可能无效的系统参数，涉及" + iTotalInvalidTransParamCount + "个交易参数";
		try {
			out.write(sumStr.getBytes());
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return iInvalidSystemParamCount;
	}
	
	public static void WriteOneLine2File(File file, String strLine) {
		try {        	
			FileWriter out = new FileWriter(file);        
			out.write(strLine);
			out.close(); 
		}      
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
		
}
