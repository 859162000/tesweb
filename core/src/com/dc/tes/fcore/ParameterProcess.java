package com.dc.tes.fcore;


import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.dc.tes.pcore.PCore;
import com.dc.tes.dom.MsgDocument;
import com.dc.tes.dom.MsgField;
import com.dc.tes.data.DALFactory;
import com.dc.tes.data.IDAL;
import com.dc.tes.data.op.Op;

import com.dc.tes.data.model.Case;
import com.dc.tes.data.model.CaseFlow;
import com.dc.tes.data.model.CaseFlowInstance;
import com.dc.tes.data.model.CaseInstance;
import com.dc.tes.data.model.CaseInstanceSqlValue;
import com.dc.tes.data.model.CaseParameterExpectedValue;
import com.dc.tes.data.model.ParameterDirectory;
import com.dc.tes.data.model.SysType;
import com.dc.tes.data.model.SystemDynamicParameter;
import com.dc.tes.data.model.Transaction;
import com.dc.tes.data.model.DbHost;
import com.dc.tes.data.model.TransactionCatetory;
import com.dc.tes.data.model.TransactionDynamicParameter;


/**
 * 参数处理功能
 * 
 * @author Huangzx
 * 
 */
public class ParameterProcess  {
	
	private static final Log log = LogFactory.getLog(PCore.class);
	
	//主机列表
	private static Map<String, DbHost> m_dbHostList = new HashMap<String, DbHost>();
	//系统内所有交易的动态参数列表<TransactionId, List>
	private static Map<String, List<TransactionDynamicParameter>> m_sysTransParamList = new HashMap<String, List<TransactionDynamicParameter>>();
	//系统内所有交易的分类参数列表<TransactionId, List>
	//字段参数(1: 上传报文中的字段参数，2：下传报文中的字段参数)
	private static Map<String, List<TransactionDynamicParameter>> m_transFieldParamList1 = new HashMap<String, List<TransactionDynamicParameter>>();
	private static Map<String, List<TransactionDynamicParameter>> m_transFieldParamList2 = new HashMap<String, List<TransactionDynamicParameter>>();
	
	//交易数据参数
	private static Map<String, List<TransactionDynamicParameter>> m_transDataParamList = new HashMap<String, List<TransactionDynamicParameter>>();
	//SQL参数
	private static Map<String, List<TransactionDynamicParameter>> m_transSqlParamList = new HashMap<String, List<TransactionDynamicParameter>>();
	//脚本和函数处理类参数
	private static Map<String, List<TransactionDynamicParameter>> m_transScriptParamList = new HashMap<String, List<TransactionDynamicParameter>>();

	//交易的参数获取顺序系列<TransactionId, List<TransactionDynamicParameter>>
	private static Map<String, List<TransactionDynamicParameter>> m_transParamFetchSequenceList = new HashMap<String, List<TransactionDynamicParameter>>();
	//交易的参数获取顺序系列<TransactionId, PARAMETERGETSEQUENCE>
	private static Map<String, String> m_transParamFetchSequenceStr = new HashMap<String, String>();
	
	//当前案例的交易数据实际值列表<系统静态参数名称，参数实际值>
	private static ThreadLocal<Map<String, String>> m_transFixedDataList = new ThreadLocal<Map<String, String>>();
	
	//当前案例的实际参数值列表<TRANSACTIONPARAMETERID，参数实际值>
	private static ThreadLocal<Map<String, String>> m_caseParamValueList = new ThreadLocal<Map<String, String>>();
	
	public static ThreadLocal<Boolean> m_isCaseInFlow = new ThreadLocal<Boolean>();
	public static ThreadLocal<Integer> m_iCasePassFlag = new ThreadLocal<Integer>();
	
	private static final Object m_lock = new Object();
	public static boolean m_isUpdatingParamList = false;
	public static int m_iProcessingParamCount = 0;



	public ParameterProcess() {
		
	}
	

	public static void Start() {
		
		try {
			getDbHostList();
			getSystemDynamicParameterList();
			//getTransactionNonFieldParamFetchSequenceList();
		}
		catch(Exception e) {
			log.error("初始化系统参数时出错，错误提示信息：" + e);
			e.printStackTrace();
		}
		
		try {
			if (checkTransactionParameterIntegerity()) {
				getSystemDynamicParameterList();
				//getTransactionNonFieldParamFetchSequenceList();	
			}
		}
		catch(Exception e) {
			log.error("检查参数完整性时出错，错误提示信息：" + e);
			e.printStackTrace();
		}
	}
	

	//获取主机列表
	private static void getDbHostList() {
		
		//遍历系统内所有的交易
		IDAL<DbHost> dbHostDAL = DALFactory.GetBeanDAL(DbHost.class);
		List<DbHost> dbHostList = dbHostDAL.ListAll(Op.EQ("systemId", DbGet.m_sysType.getSystemId()));
		  
		//逐个获取交易的动态参数
		for(int i = 0; i < dbHostList.size(); i++) {
			DbHost dbHost = dbHostList.get(i);
			m_dbHostList.put(dbHost.getHostid(), dbHost);
		}	
	}
	
	
	//获取系统的非字段参数列表（原始的，未排序）
	private static List<SystemDynamicParameter> getSystemNonFieldParamParamList() {
		try {
			IDAL<SystemDynamicParameter> sysParamDAL = DALFactory.GetBeanDAL(SystemDynamicParameter.class);
			List<SystemDynamicParameter> sysNonFieldParamList = sysParamDAL.ListAll(Op.EQ("systemId", DbGet.m_sysType.getSystemId()), Op.NE("parameterType", "0"));
			return sysNonFieldParamList;
		}
		catch(Exception e){
			log.error("从系统参数表中取数据失败：" + e);
		}
		return null;
	}
	
	
	//获取系统的非字段参数列表（排过序之后的）
	private static List<SystemDynamicParameter> getSystemNonFieldParamFetchSequenceList() {
		
		List<SystemDynamicParameter> sysNonFieldParamList = getSystemNonFieldParamParamList();
		
		int iParamCount = sysNonFieldParamList.size();
		SystemDynamicParameter sysNonFieldParams[] = new SystemDynamicParameter[iParamCount];
		for(int i = 0; i < iParamCount; i++) {
			sysNonFieldParams[i] = sysNonFieldParamList.get(i);
		}
		
		//对sysNonFieldParams进行冒泡排序
		for(int i = 0; i < iParamCount - 1; i++) {
			for(int j = i+1; j < iParamCount; j++) {
				if (DbGet.isParam1DependOnParam2(sysNonFieldParams[i], sysNonFieldParams[j])) {
					SystemDynamicParameter sysParam_temp = sysNonFieldParams[i];
					sysNonFieldParams[i] = sysNonFieldParams[j];
					sysNonFieldParams[j] = sysParam_temp;
				}
			}
		}
		
		String strParameterGetSequence = "";
		
		//重新装入排过的序非字段参数列表
		sysNonFieldParamList.clear();
		for(int i = 0; i < iParamCount; i++) {
			sysNonFieldParamList.add(sysNonFieldParams[i]);
			if (strParameterGetSequence.isEmpty())
				strParameterGetSequence += sysNonFieldParams[i].getName();
			else
				strParameterGetSequence += (" > " + sysNonFieldParams[i].getName());
		}
		
		updateSystemParameterGetSequence(strParameterGetSequence);
		
		return sysNonFieldParamList;
	}
	
	
	private static void updateSystemParameterGetSequence(String strParameterGetSequence) {
		
		IDAL<SysType> sysTypeDAL = DALFactory.GetBeanDAL(SysType.class);
		SysType sysType = sysTypeDAL.Get(Op.EQ("systemId", DbGet.m_sysType.getSystemId()));
		sysType.setParameterGetSequence(strParameterGetSequence);
		sysTypeDAL.Edit(sysType);
	}
	

	private static void updateTransactionParameterGetSequence(String strParameterGetSequence, String sTrasactionId) {
		
		IDAL<Transaction> transDAL = DALFactory.GetBeanDAL(Transaction.class);
		Transaction trans = transDAL.Get(Op.EQ(Transaction.N_TransID, sTrasactionId));
		trans.setParameterGetSequence(strParameterGetSequence);
		transDAL.Edit(trans);
	}
	

	private static boolean checkTransactionParameterIntegerity() {
		
		boolean isParamOmited = false;
		
		//获取系统内所有的交易
		IDAL<Transaction> transDAL = DALFactory.GetBeanDAL(Transaction.class);
		List<Transaction> transList = transDAL.ListAll(Op.EQ("systemId", DbGet.m_sysType.getSystemId()));
	
		//逐个交易进行检查和处理
		for(int i = 0; i < transList.size(); i++) {
			Transaction trans = transList.get(i);
			if (checkOneTransactionParameterIntegerity(trans)) {
				isParamOmited = true;
			}
		}

		return isParamOmited;
	}
	

	//检查一个交易（如果漏参数了则return true）
	private static boolean checkOneTransactionParameterIntegerity(Transaction trans) {
		
		boolean isParamOmited = false;
		
		//交易的SQL类参数列表
		List<TransactionDynamicParameter> transSqlParamList = m_transSqlParamList.get(trans.getTransactionId());
		
		//逐个交易进行检查和处理
		for(int i = 0; i < transSqlParamList.size(); i++) {
			TransactionDynamicParameter transParam = transSqlParamList.get(i);
			if (checkOneParameterIntegerity(transParam, trans)) {
				isParamOmited = true;
			}
		}
		
		//脚本类的参数列表
		List<TransactionDynamicParameter> transScriptParamList = m_transScriptParamList.get(trans.getTransactionId());
		
		//逐个交易进行检查和处理
		for(int i = 0; i < transScriptParamList.size(); i++) {
			TransactionDynamicParameter transParam = transScriptParamList.get(i);
			if (checkOneParameterIntegerity(transParam, trans)) {
				isParamOmited = true;
			}
		}
		
		return isParamOmited;
	}
	
	//检查交易的一个参数，看看其所包含的子参数是否已经在参数列表中存在
	private static boolean checkOneParameterIntegerity(TransactionDynamicParameter transParam, Transaction trans) {

		boolean isParamOmited = false;
		
		List<TransactionDynamicParameter> transAllParamList = m_sysTransParamList.get(transParam.getTransactionId());
		
		SystemDynamicParameter systemParam = transParam.getSystemParameter();
		if (systemParam == null) {
			return false;
		}

		String strParameterType = systemParam.getParameterType();
		if (strParameterType == null || strParameterType.isEmpty()) {
			return false;
		}
		int iParameterType = Integer.parseInt(strParameterType);
		if (iParameterType == 0) { //报文字段类参数，不可能再包含子参数了
			return false;
		}
		else if (iParameterType == 1) { //SQL类参数
			String strSql = systemParam.getParameterExpression();
	
			// 提取where后面的参数（where后面的第一个逗号开始为参数）
			String strUpperSql = strSql.toUpperCase();
			int iPosOfWhere = strUpperSql.indexOf("WHERE");
			if (iPosOfWhere <= 0) { // 没有WHERE，不存在任何要替换的参数
				return false;
			}
	
			String strWhereSql = strSql.substring(iPosOfWhere);
			int iPosOfComma = strWhereSql.indexOf(",");
			if (iPosOfComma <= 0) { // 没有逗号，不存在任何要替换的参数
				return false;
			}
	
			// 纯粹的参数
			String pureSqlParameters = strSql.substring(iPosOfWhere + iPosOfComma + 1); // 第一个为,
			if (pureSqlParameters == null || pureSqlParameters.isEmpty()) {
				return false;
			}
			pureSqlParameters = pureSqlParameters.trim();
			if (pureSqlParameters == null || pureSqlParameters.isEmpty()) {
				return false;
			}
			
			//去掉";"（如有）
			pureSqlParameters = pureSqlParameters.replace(";", "");
			//去掉")"（如有）
			pureSqlParameters = pureSqlParameters.replace(")", "");
			if (pureSqlParameters == null || pureSqlParameters.isEmpty()) {
				return false;
			}
			
			String[] params = pureSqlParameters.split(",");
			if (params.length <= 0) {
				return false;
			}
	
			for (int i = 0; i < params.length; i++) { // 去掉空格
				if (params[i] != null) {
					params[i] = params[i].trim();
					if (params[i] == null || params[i].isEmpty()) {
						continue;
					}
					int iPosOfDot = params[i].indexOf(".");
					if (iPosOfDot > 0) { //1.[ACTABLENAME], 1.[TRXSPSEQ]
						continue;
					}
					if (!DbGet.isParamNameInList(params[i], transAllParamList)) { //确实是少参数了!
						SystemDynamicParameter systemParameter = DbGet.getSystemParameterByParameterName(params[i], DbGet.m_sysType.getSystemId());
						if (systemParameter == null) {
							continue;
						}
						TransactionDynamicParameter newTransParam = AddOneSystemParameter4Transaction(systemParameter, trans);
						if (newTransParam == null) {
							continue;
						}
						//在列表中加入这个参数
						transAllParamList.add(newTransParam);
						m_sysTransParamList.put(trans.getTransactionId(), transAllParamList);
						isParamOmited = true;
						return checkOneParameterIntegerity(newTransParam, trans);
					}
				}
			}
		}
		else if (iParameterType == 3) { //3：函数处理类参数
			String strParamExpression = systemParam.getParameterExpression();
			int iPosOfSubString = strParamExpression.toUpperCase().indexOf(".SUBSTRING");
			if (iPosOfSubString > 0) {
				String strParamName = strParamExpression.substring(0, iPosOfSubString);
				if (strParamName == null || strParamName.isEmpty()) {
					return false;
				}
				strParamName = strParamName.trim();
				if (strParamName == null || strParamName.isEmpty()) {
					return false;
				}
				int iPosOfDot = strParamName.indexOf(".");
				if (iPosOfDot > 0) { //1.[ACTABLENAME], 1.[TRXSPSEQ]
					return false;
				}
				if (!DbGet.isParamNameInList(strParamName, transAllParamList)) { //确实是少参数了!
					SystemDynamicParameter systemParameter = DbGet.getSystemParameterByParameterName(strParamName, DbGet.m_sysType.getSystemId());
					if (systemParameter == null) {
						return false;
					}
					TransactionDynamicParameter newTransParam = AddOneSystemParameter4Transaction(systemParameter, trans);
					if (newTransParam == null) {
						return false;
					}
					
					//在列表中加入这个参数
					transAllParamList.add(newTransParam);
					m_sysTransParamList.put(trans.getTransactionId(), transAllParamList);
					isParamOmited = true;
					return checkOneParameterIntegerity(newTransParam, trans);
				}
			}
		}
		else if (iParameterType == 4) { //条件分支类参数
			String strScriptExpression = systemParam.getParameterExpression();
			String[] returnSegments = strScriptExpression.split(";");
			if (returnSegments.length == 0) { //参数个数错误
				return false;
			}
			for (int i=0; i<returnSegments.length; i++) {
				if (returnSegments[i] != null && !returnSegments[i].isEmpty()) {
					returnSegments[i] = returnSegments[i].trim();
					
					//returnSegments[i] = returnSegments[i].replace("if ", "");
					returnSegments[i] = returnSegments[i].replace("else ", "");
					
					String[] paramSegments = returnSegments[i].split("return");
					if (paramSegments.length == 0) { //语句中没有return?
						continue;
					}
					for (int j=0; j<paramSegments.length; j++) {
						if (paramSegments[j] != null && !paramSegments[j].isEmpty()) {
							paramSegments[j] = paramSegments[j].trim();
							if (paramSegments[j] == null || paramSegments[j].isEmpty()) {
								continue;
							}
							if (paramSegments[j].contains("if ")) { // if 语句
								//去掉if， else if
								paramSegments[j] = paramSegments[j].replace("if ", "");
								paramSegments[j] = paramSegments[j].replace("else ", "");
								//去掉左右括号
								if (paramSegments[j].charAt(paramSegments[j].length() - 1) == ')') {
									paramSegments[j] = paramSegments[j].substring(0, paramSegments[j].length());
								}
								if (paramSegments[j].charAt(0) == '(') {
									paramSegments[j] = paramSegments[j].substring(1);
								}
								//处理 == 
								String[] compareSegments = paramSegments[j].split("==");
								if (compareSegments.length == 0) { //if 语句 没有==
									continue;
								}
								for (int k=0; k<compareSegments.length; k++) {
									if (compareSegments[k] == null || compareSegments[k].isEmpty()) {
										continue;
									}
									compareSegments[k] = compareSegments[k].trim();
									if (compareSegments[k] == null || compareSegments[k].isEmpty()) {
										continue;
									}

									String[] addSegments = compareSegments[k].split("\\+");
									if (addSegments.length == 0) { //没有 + 号
										if (!compareSegments[k].substring(0,1).equals("\"")) { //没有引号，为子参数
											String strParamName = compareSegments[k]; 
											int iPosOfDot = strParamName.indexOf(".");
											if (iPosOfDot > 0) { //1.[ACTABLENAME], 1.[TRXSPSEQ]
												continue;
											}
											if (!DbGet.isParamNameInList(strParamName, transAllParamList)) { //确实是少参数了!
												SystemDynamicParameter systemParameter = DbGet.getSystemParameterByParameterName(strParamName, DbGet.m_sysType.getSystemId());
												if (systemParameter == null) {
													continue;
												}
												TransactionDynamicParameter newTransParam = AddOneSystemParameter4Transaction(systemParameter, trans);
												if (newTransParam == null) {
													continue;
												}
												//在列表中加入这个参数
												transAllParamList.add(newTransParam);
												m_sysTransParamList.put(trans.getTransactionId(), transAllParamList);
												isParamOmited = true;
												return checkOneParameterIntegerity(newTransParam, trans);
											}	
										}
									}
									else for (int l=0; l<addSegments.length; l++) {
										if (addSegments[l] == null || addSegments[l].isEmpty()) {
											continue;
										}  
										addSegments[l] = addSegments[l].trim();
										if (addSegments[l] == null || addSegments[l].isEmpty()) {
											continue;
										}
										if (!addSegments[l].substring(0,1).equals("\"")) { //没有引号，为子参数
											String strParamName = addSegments[l];
											int iPosOfDot = strParamName.indexOf(".");
											if (iPosOfDot > 0) { //1.[ACTABLENAME], 1.[TRXSPSEQ]
												continue;
											}
											if (!DbGet.isParamNameInList(strParamName, transAllParamList)) { //确实是少参数了!
												SystemDynamicParameter systemParameter = DbGet.getSystemParameterByParameterName(strParamName, DbGet.m_sysType.getSystemId());
												if (systemParameter == null) {
													continue;
												}
												TransactionDynamicParameter newTransParam = AddOneSystemParameter4Transaction(systemParameter, trans);
												if (newTransParam == null) {
													continue;
												}
												//在列表中加入这个参数
												transAllParamList.add(newTransParam);
												m_sysTransParamList.put(trans.getTransactionId(), transAllParamList);
												isParamOmited = true;
												return checkOneParameterIntegerity(newTransParam, trans);
											}	
										}
									}
								}
							} //if
							else { //else语句
								//return后面的语句
								String[] addSegments = paramSegments[j].split("\\+");
								if (addSegments.length == 0) { //没有 + 号
									if (!paramSegments[j].substring(0,1).equals("\"")) { //没有引号，为子参数
										String strParamName = paramSegments[j];
										int iPosOfDot = strParamName.indexOf(".");
										if (iPosOfDot > 0) { //1.[ACTABLENAME], 1.[TRXSPSEQ]
											continue;
										}
										if (!DbGet.isParamNameInList(strParamName, transAllParamList)) { //确实是少参数了!
											SystemDynamicParameter systemParameter = DbGet.getSystemParameterByParameterName(strParamName, DbGet.m_sysType.getSystemId());
											if (systemParameter == null) {
												continue;
											}
											TransactionDynamicParameter newTransParam = AddOneSystemParameter4Transaction(systemParameter, trans);
											if (newTransParam == null) {
												continue;
											}
											//在列表中加入这个参数
											transAllParamList.add(newTransParam);
											m_sysTransParamList.put(trans.getTransactionId(), transAllParamList);
											isParamOmited = true;											
											return checkOneParameterIntegerity(newTransParam, trans);
										}	
									}
								}
								else for (int l=0; l<addSegments.length; l++) {
									if (addSegments[l] == null || addSegments[l].isEmpty()) {
										continue;
									}  
									addSegments[l] = addSegments[l].trim();
									if (addSegments[l] == null || addSegments[l].isEmpty()) {
										continue;
									}
									if (!addSegments[l].substring(0,1).equals("\"")) { //没有引号，为子参数
										String strParamName = addSegments[l];
										int iPosOfDot = strParamName.indexOf(".");
										if (iPosOfDot > 0) { //1.[ACTABLENAME], 1.[TRXSPSEQ]
											continue;
										}
										if (!DbGet.isParamNameInList(strParamName, transAllParamList)) { //确实是少参数了!
											SystemDynamicParameter systemParameter = DbGet.getSystemParameterByParameterName(strParamName, DbGet.m_sysType.getSystemId());
											if (systemParameter == null) {
												continue;
											}
											TransactionDynamicParameter newTransParam = AddOneSystemParameter4Transaction(systemParameter, trans);
											if (newTransParam == null) {
												continue;
											}
											//在列表中加入这个参数
											transAllParamList.add(newTransParam);
											m_sysTransParamList.put(trans.getTransactionId(), transAllParamList);
											isParamOmited = true;											
											return checkOneParameterIntegerity(newTransParam, trans);
										}	
									}
								}
							}
						}
					}
				}
			}
		}
		
		return isParamOmited;
	}
	
	
	private static TransactionDynamicParameter AddOneSystemParameter4Transaction(SystemDynamicParameter systemParameter, Transaction trans) {
		
		IDAL<TransactionDynamicParameter> trnPrmDAL = DALFactory.GetBeanDAL(TransactionDynamicParameter.class);
			
		TransactionDynamicParameter transParam = new TransactionDynamicParameter();
		transParam.setModifyTime(new Date());
		transParam.setTransactionId(trans.getTransactionId());
		transParam.setUserId("0");
		transParam.setSystemParameter(systemParameter);
		trnPrmDAL.Add(transParam);
		
		return transParam;
	}
	

	public static void putTransactionFixedData(CaseInstance caseInstance) {
		getThreadLocalTransFixed().put("CASEINSTANCEID", caseInstance.getId().toString());
		getThreadLocalTransFixed().put("CASEID", caseInstance.getCaseId().toString());
		getThreadLocalTransFixed().put("CASENO", caseInstance.getCaseNo());
		getThreadLocalTransFixed().put("CASENAME", caseInstance.getCaseName());
		getThreadLocalTransFixed().put("IMPORTBATCHNO", caseInstance.getImportBatchNo());
	}
	
	public static void putBusiFlowFixedData(CaseFlowInstance cfi) {
	
		getThreadLocalTransFixed().put("BUSINESSFLOWID", cfi.getId().toString());
		getThreadLocalTransFixed().put("BUSINESSFLOWID", cfi.getCaseFlowId().toString());
		getThreadLocalTransFixed().put("BUSINESSFLOWNO", cfi.getCaseFlowNo());
		getThreadLocalTransFixed().put("BUSINESSFLOWNAME", cfi.getCaseFlowName());
	}
	
	public static void putTransactionFixedData(Transaction trans, Case c) {
	
		//设置固定参数
		getThreadLocalTransFixed().put("TRANSACTIONID", trans.getTransactionId().toString());
		getThreadLocalTransFixed().put("TRANSACTIONCODE", trans.getTranCode());
		getThreadLocalTransFixed().put("TRANSACTIONNAME", trans.getTranName());
		getThreadLocalTransFixed().put("TRANSACTIONCATEGORYID", trans.getTransactionCategoryId());
		
		TransactionCatetory transCategory = DALFactory.GetBeanDAL(TransactionCatetory.class).Get(Op.EQ("id", trans.getTransactionCategoryId()));
		if (transCategory != null)
			getThreadLocalTransFixed().put("TRANSACTION_CATEGORY", transCategory.getCategoryName());
		else
			getThreadLocalTransFixed().put("TRANSACTION_CATEGORY", "");
		
		if (m_isCaseInFlow.get()) {
			getThreadLocalTransFixed().put("CASEFLOWSTEP", String.valueOf(c.getSequence()));
		}
	}
	
	
	//把上传报文中的参数获取到并放入m_caseParamValueList中
	public static void putCaseParamInRequestMsgPacket(Transaction trans, CaseInstance ci) {
		
		List<TransactionDynamicParameter> transFieldParamList1 = m_transFieldParamList1.get(trans.getTransactionId());

		if (transFieldParamList1 == null) {
			return;
		}
		for(int i = 0; i < transFieldParamList1.size(); i++) {
			TransactionDynamicParameter transParam = transFieldParamList1.get(i);
			SystemDynamicParameter systemDynamicParameter = transParam.getSystemParameter();
			String msgFieldValue = DbGet.getCaseInstanceParameterValue(systemDynamicParameter.getName(), transParam, ci);
			//设置固定参数
			getThreadLocalCaseParam().put(transParam.getId(), msgFieldValue);
		}		
	}
	
	
	//获取交易的非字段参数的计算顺序
	public static void getTransactionNonFieldParamFetchSequenceList() {
		
		//synchronized (m_lock) {
					
		//获取系统的非字段参数列表，并进行排序 （字段参数肯定是优先获取的！案例数据参数  > 字段参数 > SQL参数 > 脚本和函数类的参数）
		List<SystemDynamicParameter> sysNonFieldParamList = getSystemNonFieldParamFetchSequenceList();
		
		//获取系统内所有交易
		IDAL<Transaction> transDAL = DALFactory.GetBeanDAL(Transaction.class);
		List<Transaction> allTransactions = transDAL.ListAll(Op.EQ(Transaction.N_SystemID, DbGet.m_sysType.getSystemId()));


		//逐个获取交易的动态参数，计算其 参数获取顺序
		for(int i = 0; i < allTransactions.size(); i++) {
			Transaction trans = allTransactions.get(i);
			String sTransactionId = trans.getTransactionId();
			StringBuilder sbParameterGetSequence = new StringBuilder(); 
			List<TransactionDynamicParameter> transNonFieldParamList = getNonFieldParamFetchSequenceList4OneTransaction(sTransactionId, sysNonFieldParamList, sbParameterGetSequence);
			//列表
			m_transParamFetchSequenceList.put(sTransactionId, transNonFieldParamList);
			//字符串
			m_transParamFetchSequenceStr.put(sTransactionId, sbParameterGetSequence.toString());
			//回写交易的ParameterGetSequence字段
			updateTransactionParameterGetSequence(sbParameterGetSequence.toString(), sTransactionId);
		}
			
		//}
	}

	//获取指定交易的 非字段参数的取值顺序列表（ParamFetchSequenceList）
	private static List<TransactionDynamicParameter> getNonFieldParamFetchSequenceList4OneTransaction(
			String sTransactionId, List<SystemDynamicParameter> sysNonFieldParamList, StringBuilder sbParameterGetSequence) {

		//当前交易的 非字段参数的取值顺序列表
		List<TransactionDynamicParameter> transNonFieldParamList = new ArrayList<TransactionDynamicParameter>();
			
		List<TransactionDynamicParameter> transDataParamList = m_transDataParamList.get(sTransactionId);
		List<TransactionDynamicParameter> transScriptParamList = m_transScriptParamList.get(sTransactionId);
		List<TransactionDynamicParameter> transSqlParamList = m_transSqlParamList.get(sTransactionId);
		
		int iTransDataParamCount = 0; 
		if (transDataParamList != null) {
			iTransDataParamCount = transDataParamList.size(); 
		}
		
		int iTransScriptParamCount = 0;
		if (transScriptParamList != null) {
			iTransScriptParamCount = transScriptParamList.size(); 
		}
		
		int iTransSqlParamCount = 0;
		if (transSqlParamList != null) {
			iTransSqlParamCount = transSqlParamList.size(); 
		}
			
		int iTransNonFieldParamCount = iTransDataParamCount + iTransScriptParamCount + iTransSqlParamCount;
		
		TransactionDynamicParameter[] transNonFieldAllParams = new TransactionDynamicParameter[iTransNonFieldParamCount];

		for(int i = 0; i < transScriptParamList.size(); i++) {
			transNonFieldAllParams[i] = transScriptParamList.get(i);
		}
		for(int i = 0; i < transDataParamList.size(); i++) {
			transNonFieldAllParams[transScriptParamList.size() + i] = transDataParamList.get(i);
		}
		for(int i = 0; i < transSqlParamList.size(); i++) {
			transNonFieldAllParams[transScriptParamList.size() + transDataParamList.size() + i] = transSqlParamList.get(i);
		}

		for(int i = 0; i < sysNonFieldParamList.size(); i++) {
			SystemDynamicParameter systemParam = sysNonFieldParamList.get(i);
			if (systemParam == null) {
				continue;
			}
			//根据已知的 系统参数定义来获取 交易参数定义
			TransactionDynamicParameter transParam = DbGet.getTransactionParameterBySystemParameter(systemParam, transNonFieldAllParams);
			if (transParam != null) {
				//增加进列表
				transNonFieldParamList.add(transParam);
				//取值顺序的字符表达式
				if (sbParameterGetSequence.length() <= 0) //第一个参数
					sbParameterGetSequence.append(systemParam.getName());
				else
					sbParameterGetSequence.append(" > " + systemParam.getName());
			}
		}	
		
		// 返回 非字段参数的取值顺序列表（ParamFetchSequenceList）
		return transNonFieldParamList;
	}
	
	
	/*private static boolean isUpdatingParamList() {
		
		synchronized (m_lock) {
			if (m_isUpdatingParamList) {
				return true; //还正在处理呢！
			}
			else { 
				return false; //处理完成了，没在处理了！
			}
		}
	}*/
	
	
	private static boolean isCaseParamProcessFinished() {
		//第一，一定要等，否则，案例正在处理参数你却去更新参数，读写并行，是会出错的！
		//第二，等到了机会时，一定要马上执行更新操作，别在给其它线程机会去做案例参数处理；更新完了再给它们机会
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		synchronized (m_lock) {
			if (m_iProcessingParamCount > 0) { //如果有案例正在进行参数处理，则等待其执行完成
				return false; //案例参数还在处理呢！
			}
			else {	//等到了，立刻执行，别给其它的案例处理线程先锁的机会
				m_isUpdatingParamList = true;
				try {
					getSystemDynamicParameterList_Action();
				}
				catch(Exception e) {
					e.printStackTrace();
				}
				m_isUpdatingParamList = false;
				return true; //处理完成了！
			}
		}
	}
	
	
	//获取系统动态参数列表
	public static void getSystemDynamicParameterList() {
			
		while (isCaseParamProcessFinished()) {
			//参数处理完了！
			break;
		}
		/*synchronized (m_lock) {
			if (m_iProcessingParamCount > 0) { //如果有案例正在进行参数处理，则等待其执行完成
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			} 
		}*/
	}
	
	public static void getSystemDynamicParameterList_Action() {
		
		//synchronized (m_lock) {

		//m_isUpdatingParamList = true;
				
		//获取系统内所有交易的参数列表（放置在m_sysTransParamList中）
		getAllTransactionParameterList();
		
		for (String TransactionId : m_sysTransParamList.keySet()) {
							
			List<TransactionDynamicParameter> transFieldParamList1 = new ArrayList<TransactionDynamicParameter>();
			List<TransactionDynamicParameter> transFieldParamList2 = new ArrayList<TransactionDynamicParameter>();
			List<TransactionDynamicParameter> transDataParamList = new ArrayList<TransactionDynamicParameter>();
			List<TransactionDynamicParameter> transSqlParamList = new ArrayList<TransactionDynamicParameter>();
			List<TransactionDynamicParameter> transScriptParamList = new ArrayList<TransactionDynamicParameter>();
			
			//一个交易的所有参数的列表
			List<TransactionDynamicParameter> transParamList = m_sysTransParamList.get(TransactionId);
			if (transParamList == null) {
				continue;
			}
			for(int i = 0; i < transParamList.size(); i++) {
				TransactionDynamicParameter transParam = transParamList.get(i);
				String sParameterType = transParam.getSystemParameter().getParameterType();
				int iParameterType = Integer.parseInt(sParameterType);
				if (iParameterType == 0) {//字段参数
					if (1 == transParam.getSystemParameter().getParamFromMsgSrc()) { //从上传报文中获取
						transFieldParamList1.add(transParam);
					}
					else { //从下传报文中获取
						transFieldParamList2.add(transParam);
					}
				}
				else if (iParameterType == 1) {//SQL类参数
					transSqlParamList.add(transParam);
				}
				else if (iParameterType == 2) {//交易数据类参数
					transDataParamList.add(transParam);
				}
				else { //脚本处理类参数
					transScriptParamList.add(transParam);
				}
			}
			
			//交易的各项参数列表
			m_transFieldParamList1.put(TransactionId, transFieldParamList1);
			m_transFieldParamList2.put(TransactionId, transFieldParamList2);
			
			m_transDataParamList.put(TransactionId, transDataParamList);
			m_transScriptParamList.put(TransactionId, transScriptParamList);
			m_transSqlParamList.put(TransactionId, transSqlParamList);
		} //end for
		
		getTransactionNonFieldParamFetchSequenceList();
		//m_isUpdatingParamList = false;
		//}
	}

	//获取系统内所有交易的参数列表
	private static void getAllTransactionParameterList() {
		
		//遍历系统内所有的交易
		IDAL<Transaction> transDAL = DALFactory.GetBeanDAL(Transaction.class);
		List<Transaction> allTransactions = transDAL.ListAll(Op.EQ(Transaction.N_SystemID, DbGet.m_sysType.getSystemId()));
		  
		//逐个获取交易的动态参数
		for(int i = 0; i < allTransactions.size(); i++) {
			Transaction trans = allTransactions.get(i);
			//获取单个交易的参数列表
			List<TransactionDynamicParameter> transParamList = DbGet.getTransactionParameterList(trans.getTransactionId());
			//加入到Map中
			m_sysTransParamList.put(trans.getTransactionId(), transParamList);
		}	
	}
	
		

	// 插入案例实例的字段参数（从上传报文获取的）
	public static void insertInstanceFieldParams1(MsgDocument msgDoc, CaseInstance caseInstance, IDAL<CaseInstance> ciDAL,
			Map<String, CaseParameterExpectedValue> caseExpectedParamValueList) {
	
		insertInstanceFieldParams(msgDoc, caseInstance, ciDAL, caseExpectedParamValueList, 1);
	}
	
	// 插入案例实例的字段参数（从下传报文获取的）
	public static void insertInstanceFieldParams2(MsgDocument msgDoc, CaseInstance caseInstance, IDAL<CaseInstance> ciDAL,
			Map<String, CaseParameterExpectedValue> caseExpectedParamValueList) {
	
		insertInstanceFieldParams(msgDoc, caseInstance, ciDAL, caseExpectedParamValueList, 2);
	}
	
	// 插入案例实例的字段参数
	public static void insertInstanceFieldParams(MsgDocument msgDoc, CaseInstance caseInstance, IDAL<CaseInstance> ciDAL,
			Map<String, CaseParameterExpectedValue> caseExpectedParamValueList, int InOut) {

		String sTransactionId = caseInstance.getTransactionId().toString();
		List<TransactionDynamicParameter> transFieldParamList;
		
		if (InOut == 1) {
			transFieldParamList = m_transFieldParamList1.get(sTransactionId);
		}
		else {
			transFieldParamList = m_transFieldParamList2.get(sTransactionId);
		}

		if (transFieldParamList == null) {
			return;
		}
		
		// 逐个获取交易的动态参数
		for (int i = 0; i < transFieldParamList.size(); i++) {
			TransactionDynamicParameter transFieldParam = transFieldParamList.get(i);
			if (transFieldParam == null) {
				continue;
			}
			SystemDynamicParameter systemDynamicParameter = transFieldParam.getSystemParameter();
			if (systemDynamicParameter == null) {
				continue;
			}
			String parameterExpression = systemDynamicParameter.getParameterExpression();
			if (parameterExpression == null) {
				continue;
			}
			String msgFieldValue = "";
			//获取
			try {
				if (parameterExpression.contains(".i.")) {
					parameterExpression = parameterExpression.replace(".i.", ".0.");
				} 
				//报文参数，从报文中获取实际值
				msgFieldValue = ((MsgField)msgDoc.SelectSingleField(parameterExpression)).value();
			}
			catch(Exception e) {
				log.error("没有获取到报文字段 "+systemDynamicParameter.getName()+": "+parameterExpression);
			}

			if (InOut == 2) { //上传时报文字段参数没有必要保留
				//参数值在内存中保存起来
				getThreadLocalCaseParam().put(transFieldParam.getId(), msgFieldValue);
			}
				
			DbSet.insertOneFieldValue(caseInstance.getId(), transFieldParam, systemDynamicParameter.getName(), msgFieldValue, "", 0);
			
		}
	}
	
	//参数预期值比较,只比较填写了预期值的参数   
	public static void compareExpectedParameters(CaseInstance caseInstance, 
			Map<String, CaseParameterExpectedValue> caseExpectedParamValueList) {
		
		String strExpectedValue = "";
		
		if (caseExpectedParamValueList != null) {
			Iterator<String> it =  caseExpectedParamValueList.keySet().iterator();
			while(it.hasNext()) {
				String TransactionParameterID = it.next();
				CaseParameterExpectedValue cpev = caseExpectedParamValueList.get(TransactionParameterID);
				TransactionDynamicParameter transParameter = cpev.getTransParameter();
				//取回实际参数值
				String msgFieldValue = getThreadLocalCaseParam().get(TransactionParameterID);
				
				if (cpev != null) {
					int iExpectedValueType = cpev.getExpectedValueType();
					strExpectedValue = cpev.getExpectedValue();
					if (iExpectedValueType == 1) { //预期值为表达式
						strExpectedValue = calculateExpectedValue(strExpectedValue, caseInstance, transParameter);
					}
					if (!StringUtils.isBlank(strExpectedValue)) { //预期值存在				
						String strCompareCondition = cpev.getTransParameter().getSystemParameter().getCompareCondition();
						int iCompareCondition = 0;	//比较条件
						if (strCompareCondition != null && !strCompareCondition.isEmpty()) {
							iCompareCondition = Integer.parseInt(strCompareCondition);
						}
				
						compareExpectedRealValue(iCompareCondition, strExpectedValue, msgFieldValue);
					}
				}
			
				//插入报文实际参数表
				String strParamName = "";
				try {
					strParamName = cpev.getTransParameter().getSystemParameter().getName();
				}
				catch(Exception e) {
					System.out.println("获取系统参数名称出错了，错误提示信息：" + e.getMessage());
				}
				
				log.debug("比较参数 " + strParamName + " 实际值为 ");
				log.debug(!StringUtils.isBlank(msgFieldValue)? msgFieldValue :"--");
				log.debug("预期值为 ");
				log.debug(!StringUtils.isBlank(strExpectedValue)? strExpectedValue :"--");
	
				DbSet.insertOneFieldValue(caseInstance.getId(), cpev.getTransParameter(), strParamName, msgFieldValue, strExpectedValue, 0);
			}
		}
	}


	//处理案例的参数
	public static void processCaseParameters(CaseInstance caseInstance, Transaction trans,
			Map<String, CaseParameterExpectedValue> caseExpectedParamValueList) {

		synchronized (m_lock) {
			m_iProcessingParamCount++;
		}
						
		try	{
			//获取交易的参数顺序列表
			List<TransactionDynamicParameter> transParamSequenceList = m_transParamFetchSequenceList.get(trans.getTransactionId());
			
			printTransactionParamSequenceList(transParamSequenceList);
			
			Integer iSQLDelayTime = trans.getSqlDelayTime();
			if (iSQLDelayTime != null && iSQLDelayTime > 0) {
				try {
					System.out.println("交易名称：" + trans.getTranName() + "，SQL查询前等待时间：" + String.valueOf(iSQLDelayTime) + "秒");
					Thread.sleep(1000 * iSQLDelayTime);
					System.out.println("休眠 " + String.valueOf(iSQLDelayTime) + "秒 结束！");
				} catch (InterruptedException e) {
					log.error(e);
				}
			}
			
			//按顺序逐个处理当前交易参数
			for (int i=0; i<transParamSequenceList.size(); i++) {
				//交易参数
				TransactionDynamicParameter transactionParameter = transParamSequenceList.get(i);
				//处理指定的参数
				processOneTransactionParameter(caseInstance, trans, transactionParameter, caseExpectedParamValueList);
			}
			
			//回溯处理前驱交易的需回溯参数
			if (m_isCaseInFlow.get()) { 
				int iCurrentCaseStepInFlow = caseInstance.getSequence(); 
				for (int iPreviousStep = 0; iPreviousStep < iCurrentCaseStepInFlow; iPreviousStep++) {
					processOnePreviousStep(iPreviousStep, caseInstance.getSequence(), caseInstance.getCaseFlowInstance(), 
							caseInstance.getCaseFlowInstance().getCaseFlowId());
				}
			}
		}
		catch(Exception e) {
			log.error("参数处理过程中遇到了错误，错误提示信息：" + e);
		}
		
		synchronized (m_lock) {
			m_iProcessingParamCount--;
			if (m_iProcessingParamCount < 0) {
				m_iProcessingParamCount = 0;
				log.error("程序逻辑错误：m_iProcessingParamCount=" + m_iProcessingParamCount);
			}
		}
	}
	
	//处理前一交易的参数
	private static void processOnePreviousStep(int iPreviousStep, int iCurrentStep, CaseFlowInstance cfi, int iCaseFlowId) {
		
		//前一交易是什么交易？先获取交易信息(Transaction trans)
		IDAL<CaseFlow> flowDAL = DALFactory.GetBeanDAL(CaseFlow.class);
		CaseFlow caseFlow = flowDAL.Get(Op.EQ("id", iCaseFlowId));
		if (caseFlow == null || cfi == null) {
			return;
		}
		
		//获取业务流实例
		int iCaseFlowInstanceId = cfi.getId();
		//获取案例
		Case c = DbGet.getFlowCaseByStepNo(caseFlow, iPreviousStep);
		if (c == null) {
			return;
		}
		//获取案例实例
		CaseInstance ci = DbGet.getCaseInstanceByFlowCase(iCaseFlowInstanceId, c);
		if (ci == null) {
			return;
		}
		//获取（前x）交易
		Transaction trans = DbGet.getTransctionByTransactionId(c.getTransactionId());
		if (trans == null) {
			return;
		}
		//获取那些需要重新计算的参数列表
		List<TransactionDynamicParameter> transReFetchParamList = getTransactionReFetchParamList(trans);
		if (transReFetchParamList == null) {
			return;
		}
		
		//对那些需要重新获取的参数列表进行排序
		transReFetchParamList = reSortTransactionReFetchParamList(transReFetchParamList, trans);
		for (int i=0; i<transReFetchParamList.size();i++) {
			TransactionDynamicParameter transReFetchParam = transReFetchParamList.get(i);
			if (transReFetchParam == null) {
				continue;
			}
			ReFetchOneSqlParam(iPreviousStep, iCurrentStep, transReFetchParam, ci, trans);
		}
	}
	
	//对那些需要重新获取的参数列表进行排序	
	private static List<TransactionDynamicParameter> reSortTransactionReFetchParamList(List<TransactionDynamicParameter> transReFetchParamList, Transaction trans) {
	
		List<TransactionDynamicParameter> newTransParamList = new ArrayList<TransactionDynamicParameter>();
		List<TransactionDynamicParameter> transParamFetchSequenceList = m_transParamFetchSequenceList.get(trans.getTransactionId());
		
		for (int i=0; i<transParamFetchSequenceList.size(); i++) {
			TransactionDynamicParameter transParam = transParamFetchSequenceList.get(i);
			if (transReFetchParamList.contains(transParam)) {
				newTransParamList.add(transParam);
			}
		}
		
		return newTransParamList;
	}
	
	private static void ReFetchOneSqlParam(int iPreviousStep, int iCurrentStep, TransactionDynamicParameter transReFetchParam, CaseInstance ci, Transaction trans) {
		
		SystemDynamicParameter systemParameter = transReFetchParam.getSystemParameter();
		if (systemParameter == null) {
			return;
		}
		int iReFetchMethod = systemParameter.getRefetchMethod();
		if (iReFetchMethod == 0 || iReFetchMethod == 2) { //使用原来的老参数进行查询
			String realSql = getRealSqlByTransactionParameter(transReFetchParam, ci.getId());
		
			//执行SQL语句
			String realValue = GetSqlQueryingResult(realSql, ci.getCardId(), systemParameter);
			
			//案例实例SQL参数值记录
			CaseInstanceSqlValue caseInstanceSqlValue = new CaseInstanceSqlValue();
			caseInstanceSqlValue.setCaseInstanceId(ci.getId());
			caseInstanceSqlValue.setCaseFlowStep(iCurrentStep);
			caseInstanceSqlValue.setIsCurrentStep(0); //不是当前步骤，是回溯步骤
			caseInstanceSqlValue.setTransParameter(transReFetchParam);
			caseInstanceSqlValue.setRealSql(realSql);
			caseInstanceSqlValue.setRealValue(realValue);
			caseInstanceSqlValue.setParameterName(systemParameter.getName());
	
			//案例实例SQL参数表
			IDAL<CaseInstanceSqlValue> ciSqlValueDAL = DALFactory.GetBeanDAL(CaseInstanceSqlValue.class);
			//插入案例实例SQL参数记录
			ciSqlValueDAL.Add(caseInstanceSqlValue);
		}
		if (iReFetchMethod == 1 || iReFetchMethod == 2) { //使用新参数进行查询
		
		}
	}
	
	private static String getRealSqlByTransactionParameter(TransactionDynamicParameter transReFetchParam, int iCaseInstanceId) {
		
		IDAL<CaseInstanceSqlValue> cisvDAL = DALFactory.GetBeanDAL(CaseInstanceSqlValue.class);
		CaseInstanceSqlValue cisv = cisvDAL.Get(
				Op.EQ("caseInstanceId", iCaseInstanceId),
				Op.EQ("transParameter", transReFetchParam),
				Op.EQ("isCurrentStep", 1));
		if (cisv == null) {
			return null;
		}
		return cisv.getRealSql();
	}
	
	private static List<TransactionDynamicParameter> getTransactionReFetchParamList(Transaction trans) {
		
		List<TransactionDynamicParameter> transReFetchParamList = new ArrayList<TransactionDynamicParameter>(); 
		//获取所有的参数（属于当前交易的）
		List<TransactionDynamicParameter> transSqlParamList = m_transSqlParamList.get(trans.getTransactionId());
		List<TransactionDynamicParameter> transScriptParamList = m_transScriptParamList.get(trans.getTransactionId());
		List<TransactionDynamicParameter> transNonMsgFieldParamList = transSqlParamList; 
		transNonMsgFieldParamList.addAll(transScriptParamList);

		for (int i=0; i<transNonMsgFieldParamList.size(); i++) {
			TransactionDynamicParameter transParam = transNonMsgFieldParamList.get(i);
			if (transParam.getSystemParameter().getRefetchFlag() == 1) { //需要回溯获取的参数
				transReFetchParamList.add(transParam);
			}
		}
		return transReFetchParamList;
	}
	
	
	
	//处理案例的一个参数
	private static void processOneTransactionParameter(CaseInstance caseInstance, Transaction trans, 
			TransactionDynamicParameter transactionParameter,
			Map<String, CaseParameterExpectedValue> caseExpectedParamValueList) {
		
		SystemDynamicParameter systemParameter = transactionParameter.getSystemParameter();
		
		//获取参数表达式
		String strExpression = systemParameter.getParameterExpression();
		if (strExpression == null || strExpression.isEmpty()) { //参数表达式为空，无法继续处理
			return;
		}
		strExpression = strExpression.trim();
		
		//检查2.[2A3TXCDTAP]等参数，看看是否为多余的参数
		if (strExpression.length() > 2){
			int iPosOfDot = strExpression.indexOf(".");
			if (iPosOfDot > 0) { //存在 . ，可能是2.[2A3TXCDTAP]，也可能是lib.table
				int iPosOfComma = strExpression.indexOf(",");
				if (iPosOfComma > 0) { //存在 , 也就是存在参数			
					String strParamStr = strExpression.substring(iPosOfComma);
					if (strParamStr != null && !strParamStr.isEmpty()) {
						String[] subStringSegments = strParamStr.split(",");
						for (int i=0; i<subStringSegments.length; i++) {
							String strParam = subStringSegments[i];
							if (strParam == null)
								continue;
							strParam = strParam.trim();
							if (strParam.length() <=0 )
								continue;
							int iPosOfDotInParam = strParam.indexOf(".");
							if (iPosOfDotInParam > 0) {
								String strPreviousStep = strParam.substring(0, iPosOfDotInParam);
								if (Utility.isDigitString(strPreviousStep) && strPreviousStep.length() < 3){ //是否为数字串？
									int iPreviousStep = Integer.parseInt(strPreviousStep);
									if (!m_isCaseInFlow.get()) { //当前案例不属于业务流，当前参数为不需要处理的参数
										return;
									}
									else {//当前案例属于业务流
										Integer iCurrentCaseStepInFlow = caseInstance.getSequence();
										if (iPreviousStep > iCurrentCaseStepInFlow) {
											return;
										}
									}
								}						
							}
						}
					}
				}
			}
		}
		
		//替换掉SQL语句中的$CaseId等常量
		strExpression = replaceFixedTransactionDatas(strExpression);
		
		//获取参数类型
		String strParameterType = systemParameter.getParameterType();
		if (strParameterType == null || strParameterType.isEmpty()) { //参数类型为空，无法继续处理
			return;
		}
		int iParameterType = Integer.parseInt(strParameterType);
		if (iParameterType == 1) {//sql参数
			processOneSqlParams(caseInstance, trans, transactionParameter, systemParameter, strExpression, caseExpectedParamValueList);
		}
		else if (iParameterType == 3) {//函数参数
			processOneFunctionParams(caseInstance, trans, transactionParameter, systemParameter, strExpression);
		}
		else if (iParameterType == 4) {//条件分支参数
			processOneIfElseParams(caseInstance, trans, transactionParameter, systemParameter, strExpression);
		}
	}
	
	//获取给定参数的值（给定参数名，可能含路径，也可能只有参数名称）
	private static String getParseParameterValueByParamPathName(String strParsedParamPathName, Transaction trans) {

		TransactionDynamicParameter transParam = getTransParameterByParameterName(strParsedParamPathName, trans.getTransactionId());
		if (transParam == null) {
			return null;
		}
		return getThreadLocalCaseParam().get(transParam.getId());
	}
	
	public static TransactionDynamicParameter getTransParameterByParameterName(String strParsedParamPathName, String sTransactionId) {
		
		ParameterDirectory sysParamDirectory = null;
		String strParamName = strParsedParamPathName;
		
		//是否存在参数路径？
		int iPos = strParsedParamPathName.lastIndexOf("\\");
		if (iPos >= 0) {
			//路径
			String strParamDirPath = strParsedParamPathName.substring(0, iPos - 1);
			//参数名
			strParamName = strParsedParamPathName.substring(iPos);
			//获取参数所在的目录Id
			IDAL<ParameterDirectory> pdDAL = DALFactory.GetBeanDAL(ParameterDirectory.class);
			sysParamDirectory = pdDAL.Get(Op.EQ("systemId", DbGet.m_sysType.getSystemId()), Op.EQ("path", strParamDirPath));
		}

		//给定参数（路径\参数名）所对应的 系统参数（system_dynamic_parameter）是哪一个？
		IDAL<SystemDynamicParameter> sysParamDAL = DALFactory.GetBeanDAL(SystemDynamicParameter.class);
		SystemDynamicParameter sysParam = null;
		if (sysParamDirectory != null) { //挂在某一个参数树下的
			sysParam = sysParamDAL.Get(Op.EQ("systemId", DbGet.m_sysType.getSystemId()), Op.EQ("name", strParamName), Op.EQ("directoryId", sysParamDirectory.getId()));
		}
		else { //不具有参数树
			//按名获取系统内所有的参数（ 系统动态参数，可能存在两个以上同名的参数）
			List<SystemDynamicParameter> sysParamList = sysParamDAL.ListAll(Op.EQ("systemId", DbGet.m_sysType.getSystemId()), Op.EQ("name", strParamName));
			if (sysParamList != null) {
				//交易的所有参数列表
				List<TransactionDynamicParameter> transParamList = m_sysTransParamList.get(sTransactionId);			
				if (transParamList != null) {
					//交易的参数列表不空
					for(int i = 0; i < sysParamList.size(); i++) {
						//逐个看：参数是否被交易所引用？
						SystemDynamicParameter systemParam = sysParamList.get(i);
						for(int j = 0; j < transParamList.size(); j++) {
							TransactionDynamicParameter transParam = transParamList.get(j);
							if (systemParam == transParam.getSystemParameter()) { //交易的参数 == 系统参数?
								sysParam = systemParam;
								//return getThreadLocalCaseParam().get(transParam.getId());
								break; //两重for循环
							}
						}
					}
				}
				else {
					sysParam = sysParamList.get(0);
				}
			}
		}
		if (sysParam == null) {
			return null;
		}
		
		//系统参数 所对应的 交易参数（transaction_dynamic_parameter）是哪一个？
		IDAL<TransactionDynamicParameter> transParamDAL = DALFactory.GetBeanDAL(TransactionDynamicParameter.class);
		TransactionDynamicParameter transParam = transParamDAL.Get(Op.EQ("transactionId", sTransactionId), Op.EQ("systemParameter", sysParam));
		return transParam;
		/*if (transParam == null) {
			return null;
		}*/
		//这个参数已经是有值的了：
		//return getThreadLocalCaseParam().get(transParam.getId());
	}
	
	
	//处理案例的脚本类参数
	private static void processOneFunctionParams(CaseInstance caseInstance, Transaction trans,
			TransactionDynamicParameter transactionParameter, SystemDynamicParameter systemParameter, String strScriptExpression) {
	
		if (strScriptExpression == null || strScriptExpression.isEmpty()) {
			return;
		}
		strScriptExpression = strScriptExpression.trim();
		
		int iCaseId = caseInstance.getCaseId();
		if (m_isCaseInFlow.get() && DbGet.isCaseInFlow(iCaseId) && caseInstance.getSequence() > 0) { //0为第一步
			CaseFlowInstance cfi =  caseInstance.getCaseFlowInstance();
			strScriptExpression = replacePreviousCaseParams(strScriptExpression, caseInstance, cfi);
		}
		
		String strUpperScriptExpression = strScriptExpression.toUpperCase();
		int iPos = strUpperScriptExpression.indexOf(".SUBSTRING");
		if (iPos > 0)
		{
			String strParsedParamPathName = strUpperScriptExpression.substring(0, iPos);
			//String strParsedParamValue = getThreadLocalCaseParam().get(strParsedParamPathName);
			String strParsedParamValue = getParseParameterValueByParamPathName(strParsedParamPathName, trans);
			String strSubStrParamStr = strUpperScriptExpression.substring(iPos + ".SUBSTRING".length() + 1); //去掉前面的 (
			strSubStrParamStr = strSubStrParamStr.substring(0, strSubStrParamStr.length() - 1); //去掉末尾的 )
			
			String strExpressionFinalValue = ""; 
			
			String[] subStringParams = strSubStrParamStr.split(",");
			if (subStringParams.length <= 0 || subStringParams.length > 2) { //参数个数错误
				return;
			}
			else if (subStringParams.length == 1) {
				int i = Integer.parseInt(subStringParams[0]);
				if (strParsedParamValue != null) {
					strExpressionFinalValue = strParsedParamValue.substring(i);
				}
			}
			else if (subStringParams.length == 2) {
				int i = Integer.parseInt(subStringParams[0]);
				int j = Integer.parseInt(subStringParams[1]);
				if (strParsedParamValue != null) {
					strExpressionFinalValue = strParsedParamValue.substring(i,j);
				}
			}
			
			//插入字段参数值表
			String strParameterType = systemParameter.getParameterType();
			int iParameterType = Integer.parseInt(strParameterType);
			DbSet.insertOneFieldValue(caseInstance.getId(), transactionParameter, systemParameter.getName(), strExpressionFinalValue, "", iParameterType);
			getThreadLocalCaseParam().put(transactionParameter.getId(), strExpressionFinalValue);
		}		
	}
	

	//处理案例的条件分支类参数
	private static void processOneIfElseParams(CaseInstance caseInstance, Transaction trans,
			TransactionDynamicParameter transactionParameter, SystemDynamicParameter systemParameter, String strScriptExpression) {
	
		strScriptExpression = strScriptExpression.trim();
		
		if (m_isCaseInFlow.get() && caseInstance.getSequence() > 0) { //0为第一步
			CaseFlowInstance cfi =  caseInstance.getCaseFlowInstance();
			strScriptExpression = replacePreviousCaseParams(strScriptExpression, caseInstance, cfi);
		}
			
		String matchedReturnExpression;
		String[] returnSegments = strScriptExpression.split(";");
		if (returnSegments.length == 0) { //参数个数错误
			return;
		}
		else if (returnSegments.length == 1) { //只有一个return
			matchedReturnExpression = returnSegments[0]; 
		}
		else {
			matchedReturnExpression = getMatchedReturnExperssion(returnSegments, trans);
		}
		matchedReturnExpression = matchedReturnExpression.trim();
		
		String strFinalValue = calcMatchedReturnExpression(matchedReturnExpression, trans);
		//插入
		String strParameterType = systemParameter.getParameterType();
		int iParameterType = Integer.parseInt(strParameterType);
		DbSet.insertOneFieldValue(caseInstance.getId(), transactionParameter, systemParameter.getName(), strFinalValue, "", iParameterType);
		getThreadLocalCaseParam().put(transactionParameter.getId(), strFinalValue);
	}
	
	//计算条件分支表达式最后应该返回的值
	private static String calcMatchedReturnExpression(String matchedReturnExpression, Transaction trans) {
		
		String strFinalValue = "";
			
		String[] subStringSegments = matchedReturnExpression.split("\\+");
		for (int i=0; i<subStringSegments.length; i++)
		{
			String strParam = subStringSegments[i].trim();
			if (strParam.substring(0,1).equals("\"")) {//是常量
				strFinalValue += strParam.replace("\"", ""); 
			}
			else { //是已知的参数
				String strParsedParamValue = getParseParameterValueByParamPathName(strParam, trans);
				if (strParsedParamValue != null) {
					strFinalValue += strParsedParamValue; 
				}
				/* if (getThreadLocalCaseParam().get(strParam) != null) {
					strFinalValue += getThreadLocalCaseParam().get(strParam);
				} */
			}
		}
		
		return strFinalValue;
	}
	
	
	private static String getMatchedReturnExperssion(String[] returnSegments, Transaction trans) {
		
		for (int i=0; i<returnSegments.length; i++) {
			int iPosOfReturn = returnSegments[i].indexOf("return");
			if (iPosOfReturn < 0)
			{	//不存在return语句
				log.error("脚本表达式错误：缺少return语句");
				return null;
			}
			String ifCondition = returnSegments[i].substring(0, iPosOfReturn);
			int iPosOfIf = ifCondition.indexOf("if");
			int iPosOfElse = ifCondition.indexOf("else");
			if (i==returnSegments.length-1 && iPosOfElse >= 0 && iPosOfIf < 0) { //最后一句，存在else，不存在if （不是else if 语句）
				continue;
			}
			ifCondition = ifCondition.substring(iPosOfIf + "if".length());
			if (isIfConditionMatched(ifCondition, trans))	{
				return returnSegments[i].substring(iPosOfReturn + "return ".length());
			}
		}
		
		//前面都未匹配上
		String elseReturn = returnSegments[returnSegments.length - 1];
		int iPosOfElse = elseReturn.indexOf("else");
		if (iPosOfElse < 0) { //最后一句，还不是else语句
			return null;
		}
		int iPosOfReturn = elseReturn.indexOf("return");
		if (iPosOfReturn < 0)
		{	//不存在return语句
			log.error("脚本表达式错误：缺少return语句");
			return null;
		}
		else { //返回 else return 后面的语句
			return elseReturn.substring(iPosOfReturn + "return ".length());
		}
	}

	
	private static boolean isIfConditionMatched(String  ifCondition, Transaction trans) {
		
		String strLeftValue, strRightValue;
		int iPosOfLeftBracket = ifCondition.indexOf("(");
		int iPosOfRightBracket = ifCondition.indexOf(")");
		int iPosOfDoubleEqualSign = ifCondition.indexOf("==");
		String strLeftParam = ifCondition.substring(iPosOfLeftBracket+1, iPosOfDoubleEqualSign - 1).trim();
		String strRightParam = ifCondition.substring(iPosOfDoubleEqualSign+2, iPosOfRightBracket).trim();
		//左值
		if (strLeftParam.indexOf("\"") < 0) { //为参数
			//strLeftValue = getThreadLocalCaseParam().get(strLeftParam);
			strLeftValue = getParseParameterValueByParamPathName(strLeftParam, trans);
		}
		else {
			strLeftValue = strLeftParam.replace("\"", "");
		}
		//右值
		if (strRightParam.indexOf("\"") < 0) { //为参数
			//strRightValue = getThreadLocalCaseParam().get(strRightParam);
			strRightValue = getParseParameterValueByParamPathName(strRightParam, trans);
		}
		else {
			strRightValue = strRightParam.replace("\"", "");
		}
		if (strLeftValue != null && strLeftValue.equals(strRightValue))
			return true;

		return false;
	}
	

	//处理一个sql参数
	private static void processOneSqlParams(CaseInstance caseInstance,	Transaction trans,
			TransactionDynamicParameter transactionParameter, SystemDynamicParameter systemParameter, 
			String strSql, Map<String, CaseParameterExpectedValue> caseExpectedParamValueList) {

		Integer iCaseFlowStep = caseInstance.getSequence();

		int iCaseInstacneId = caseInstance.getId();
		int isCurrentStep = 1;
		
		String strNewSql = strSql;
		
		if (m_isCaseInFlow.get() && caseInstance.getSequence() > 0) { //0为第一步
			CaseFlowInstance cfi = caseInstance.getCaseFlowInstance();
			strNewSql = replacePreviousCaseParams(strNewSql, caseInstance, cfi);
		}
		

		//替换到SQL语句中的已经解析过的参数
		strNewSql = replaceParsedParamValues(strNewSql, trans);
		
		//解析SQL语句
		String realSql = parseSql(strNewSql);	
		
		//执行SQL语句
		String realValue = GetSqlQueryingResult(realSql, caseInstance.getCardId(), systemParameter);
 
		String strExpectedValue= ""; 
		if (1 == m_iCasePassFlag.get() && caseExpectedParamValueList != null) {
			//比较预期值和实际值是否一致？
			CaseParameterExpectedValue cepv = caseExpectedParamValueList.get(systemParameter.getName());
			if (cepv != null) {
				int iExpectedValueType = cepv.getExpectedValueType();
				strExpectedValue = cepv.getExpectedValue();
				if (iExpectedValueType == 1) { //预期值为表达式
					strExpectedValue = calculateExpectedValue(strExpectedValue, caseInstance, cepv.getTransParameter());
				}
				if (strExpectedValue != null) { //预期值存在
					String strCompareCondition = systemParameter.getCompareCondition();
					int iCompareCondition = 0;	//比较条件
					if (strCompareCondition != null && !strCompareCondition.isEmpty()) {
						iCompareCondition = Integer.parseInt(strCompareCondition);
					}
					/*//[参数]用括号括起来了
					if (strExpectedValue.contains("[") && strExpectedValue.contains("]")) {
						strExpectedValue = replaceParamInExpectedValue(strExpectedValue, caseInstance);
					}*/
					compareExpectedRealValue(iCompareCondition, strExpectedValue, realValue);
				}
			}
		}
		
		try {
			insertOneSqlValue(iCaseInstacneId, iCaseFlowStep, isCurrentStep, realSql, realValue, strExpectedValue, transactionParameter, systemParameter);
		}
		catch(Exception e) {
			log.error("insertOneSqlValue失败，错误信息：" + e.getMessage());
		}
	}
	
	private static String calculateExpectedValue(String strExpectedValueExpr, CaseInstance ci, TransactionDynamicParameter transParameter) {
		
		StringBuilder sbParamExpression = new StringBuilder();
		sbParamExpression.append(strExpectedValueExpr);
		StringBuilder sbOneParamExpr = new StringBuilder();
		String strParsedFinalValueStr = strExpectedValueExpr.toString();
		//截取一个参数出来（从左到右）
		while (true == getOneParamExpression(sbParamExpression, sbOneParamExpr)) {
			if (sbOneParamExpr != null) {
				//获取该参数的参数值
				String strOneParamValue = getOneParamExprValue(sbOneParamExpr.toString(), ci, transParameter);
				strParsedFinalValueStr = strParsedFinalValueStr.replace(sbOneParamExpr.toString(), strOneParamValue);
			}
			if (sbParamExpression == null || sbParamExpression.length() <= 0) {
				break;
			}
		}
		String strFinalCalcedValue = strParsedFinalValueStr;
		if (strParsedFinalValueStr.contains("\"")) {//字符串的相加
			strFinalCalcedValue = strFinalCalcedValue.replace("\"", "");
			strFinalCalcedValue = strFinalCalcedValue.replace("+", "");
		}
		else { //数字的相加
			//strParsedFinalValueStr = strParsedFinalValueStr.replace(" ", ""); //KEVIN KANG -> KEVINKANG
			strParsedFinalValueStr = strParsedFinalValueStr.trim();
			strFinalCalcedValue = Arithmetic.calculate(strParsedFinalValueStr);
		}
		return strFinalCalcedValue;
	}
	
	

	//从表达式串中获取一个单个的表达式
	private static boolean getOneParamExpression(StringBuilder sbParamExpression, StringBuilder sbOneParamExpr) {	

		if (sbParamExpression == null)
			return false;
		String strParamExpression = sbParamExpression.toString();
		if (strParamExpression.isEmpty()) {
			return false;
		}
		strParamExpression = strParamExpression.trim();
		if (strParamExpression.isEmpty()) {
			return false;
		}
		char[] charParamExpression = strParamExpression.toCharArray();
		boolean seporatorFound = false;
		int i=0;
		for (i=0; i<charParamExpression.length; i++) { //空格不算
			if (charParamExpression[i] == '+' || charParamExpression[i] == '-' || charParamExpression[i] == '*' || charParamExpression[i] == '/') {
				seporatorFound = true;
				break;
			}
		}
		
		if (sbOneParamExpr != null && sbOneParamExpr.length() > 0) {
			sbOneParamExpr.delete(0, sbOneParamExpr.length());
		}
		
		if (i==charParamExpression.length) { //遍历到了最后一位字符了，都没有发现+-*/
			//最后一个串，其实也是一个表达式
			sbOneParamExpr.append(sbParamExpression);
			sbParamExpression.delete(0, sbParamExpression.length());
			seporatorFound = true;
		}
		else {
			//截取参数表达式
			String strOneParamExpr = sbParamExpression.substring(0, i);
			if (strOneParamExpr != null && !strOneParamExpr.isEmpty()) {
				sbOneParamExpr.append(strOneParamExpr.trim());
			}
			else {
				sbOneParamExpr = null;
				seporatorFound = false;
			}
			
			//后面剩下的串
			String strUnParsedExprStr = sbParamExpression.substring(i+1);
			if (strUnParsedExprStr != null && !strUnParsedExprStr.isEmpty()) {
				sbParamExpression.delete(0, sbParamExpression.length());
				sbParamExpression.append(strUnParsedExprStr);
			}
			else {
				sbParamExpression = null;
			}
		}
	
		return seporatorFound;
	}
	
	//获取一个指定参数的具体参数值
	private static String getOneParamExprValue(String sbParamExpression, CaseInstance ci, TransactionDynamicParameter transParameter) {

		if (sbParamExpression == null || sbParamExpression.isEmpty()){
			return null;
		}
		
		//假如本来就是个数字则直接返回   by ljs
		if (Utility.isDigitString(sbParamExpression))
			return sbParamExpression;
		
		String strPureParamName = sbParamExpression.toString();
		
		int iPosOfDot = sbParamExpression.indexOf(".");
		int iStep = -1;
		if (iPosOfDot > 0) {
			String strStep = sbParamExpression.substring(0, iPosOfDot);
			if (strStep != null && !strStep.isEmpty()) {
				strStep = strStep.trim();
			}
			if (Utility.isDigitString(strStep)) {
				strPureParamName = sbParamExpression.substring(iPosOfDot + 1);
				try {
					iStep = Integer.parseInt(strStep);
				}
				catch(Exception e) {
					System.out.println(e.getMessage());
					return sbParamExpression;
				}
			}
			ci = DbGet.getCaseInstanceByExecuteSequence(ci.getExecuteLogId(), ci.getCaseFlowInstance(),iStep);
		}
 
		if (strPureParamName == null || strPureParamName.isEmpty()) {
			return null;
		}
		
		return DbGet.getCaseInstanceParameterValue(strPureParamName, transParameter, ci);
	}
	
		
	/*private static String replaceParamInExpectedValue(String strExpectedValue, CaseInstance caseInstance) {
		
		 String[] params = strExpectedValue.split("]");
		 if (params.length <= 0) {
			 return strExpectedValue; 
		 }
		 
		 String strNewValue = "";
		 for (int i=0; i<params.length; i++) {	
			 if (params[i].contains("[")) {
				 int iPos = params[i].indexOf("[");
				 String strParam = params[i].substring(iPos);
				 strParam += "]";
				 String strValue = DbGet.getCaseInstanceParameterValue(strParam, caseInstance);
				 strNewValue += params[i].substring(0, iPos) + strValue;
			 }
			 else {
				 strNewValue += params[i];
			 } 			 
		 }
		
		return strNewValue;
	}*/
	
	
	private static void compareExpectedRealValue(int iCompareCondition, String strExpectedValue, String realValue) {
		
		
		if (strExpectedValue == null || strExpectedValue.isEmpty()) {
			//如果预期值为空，则不用比了，直接返回（认为案例是通过的！）
			return;
		}
		//else 预期值非空
		
		if (realValue == null || realValue.isEmpty()) {
			//实际值为空，而预期值非空
			m_iCasePassFlag.set(0);
			return;
		}
		//else 实际值不空了
		
		if (0 == iCompareCondition) { // 0: 完全一样
			if (!strExpectedValue.equals(realValue)) {
				m_iCasePassFlag.set(0);
			}
		}
		else if (1 == iCompareCondition) { // 1: 实际值中包含有预期值
			if (!realValue.contains(strExpectedValue)) {
				m_iCasePassFlag.set(0);
			}
		}
		else if (2 == iCompareCondition) { // 2: 预期值中包含有实际值
			if (!strExpectedValue.contains(realValue)) {
				m_iCasePassFlag.set(0);
			}
		}
	}
	
	
	private static void insertOneSqlValue(int iCaseInstacneId, Integer iCaseFlowStep,
			int isCurrentStep, String realSql, String realValue, String expectedValue,
			TransactionDynamicParameter transactionParameter, SystemDynamicParameter systemParameter) {
		//插入案例实例SQL参数表

		//案例实例SQL参数值记录
		CaseInstanceSqlValue caseInstanceSqlValue = new CaseInstanceSqlValue();
		caseInstanceSqlValue.setCaseInstanceId(iCaseInstacneId);
		caseInstanceSqlValue.setIsCurrentStep(isCurrentStep);
		if (iCaseFlowStep != null && iCaseFlowStep != -1) {
			caseInstanceSqlValue.setCaseFlowStep(iCaseFlowStep);
		}
		caseInstanceSqlValue.setTransParameter(transactionParameter);
		caseInstanceSqlValue.setRealSql(realSql);
		caseInstanceSqlValue.setRealValue(realValue);
		caseInstanceSqlValue.setExpectedValue(expectedValue);
		caseInstanceSqlValue.setParameterName(systemParameter.getName());

		//案例实例SQL参数表
		IDAL<CaseInstanceSqlValue> ciSqlValueDAL = DALFactory.GetBeanDAL(CaseInstanceSqlValue.class);
		//插入案例实例SQL参数记录
		ciSqlValueDAL.Add(caseInstanceSqlValue);
		//String strParameterName = systemParameter.getName();
		getThreadLocalCaseParam().put(transactionParameter.getId(), realValue);
	}


	private static String replacePreviousCaseParams(String strSql, CaseInstance caseInstance, CaseFlowInstance cfi) {
			
		 //提取where后面的参数（where后面的第一个逗号开始为参数）
		 String strUpperSql = strSql.toUpperCase();
		 int iPosOfWhere =strUpperSql.indexOf("WHERE");
		 if (iPosOfWhere <= 0) { //没有WHERE，不存在任何要替换的参数
			 return strSql;
		 }

		 String strWhereSql = strSql.substring(iPosOfWhere);
		 String pureSqlStatement = strSql.substring(0,iPosOfWhere);
		 
		 int iPosOfComma = strWhereSql.indexOf(",");
		 if (iPosOfComma <= 0) { //没有逗号，不存在任何要替换的参数
			 return strSql;
		 }
		 //从where到其后的第一个逗号之间的那一段SQL语句
		 pureSqlStatement += strSql.substring(iPosOfWhere, iPosOfWhere+iPosOfComma);
				 
		 String pureSqlParameters = strSql.substring(iPosOfWhere+iPosOfComma+1); //第一个为,
		 String[] params = pureSqlParameters.split(",");
		 if (params.length <= 0) {
			 return strSql; 
		 }
		 
		 for (int i=0; i<params.length; i++) {	//去掉空格
			 String strParam = params[i].trim(); 
			 params[i] = strParam;
		 }

		//逐步处理（遍历前面所有的案例步骤） 
		int iCurrentCaseStep = caseInstance.getSequence();
		for (int iPreviousCaseStep = 0; iPreviousCaseStep <= iCurrentCaseStep; iPreviousCaseStep++) {
			String sniDot = "-" + String.valueOf(iPreviousCaseStep) + ".";
			if (pureSqlParameters.indexOf(sniDot) >= 0) { //以负号的方式来表示前面第几步
				//转换成不带负号的步骤应该是：
				int iPreviousStepNo = iCurrentCaseStep - iPreviousCaseStep;
				String siDot = String.valueOf(iPreviousStepNo) + ".";
				pureSqlParameters = pureSqlParameters.replace(sniDot, siDot);
				pureSqlParameters = replacePreviousStepCaseParam(pureSqlParameters, cfi, iPreviousStepNo, siDot, params);
			}
			else { //没有负号（-1.[Param1]）
				String siDot = String.valueOf(iPreviousCaseStep) + ".";
				if (iPreviousCaseStep == iCurrentCaseStep) { //使用本步骤的参数，移除掉前面的2.
					pureSqlParameters = pureSqlParameters.replace(siDot, "");
				}
				else if (pureSqlParameters.indexOf(siDot) >= 0) {
					pureSqlParameters = replacePreviousStepCaseParam(pureSqlParameters, cfi, iPreviousCaseStep, siDot, params);
				}
			}
		}
	
		String strNewSql = pureSqlStatement + ", " + pureSqlParameters;
		return strNewSql;
	}
	
	
	private static String replacePreviousStepCaseParam(String strPureParams, CaseFlowInstance cfi, 
			int iPreviousCaseStep, String siDot, String[] params) {
		
		 for (int i=0; i<params.length; i++) {
			 String strParam = params[i].trim();
			 if (strParam.contains(siDot)) {
				 strPureParams = replaceOnePreviousStepParam(strPureParams, siDot, strParam.substring(2), cfi, iPreviousCaseStep);
			 }
		 }
		
		return strPureParams;
	}
	

	private static String replaceOnePreviousStepParam(String strPureParams, String siDot, String strParam, CaseFlowInstance cfi, int iPreviousCaseStep) {

		if (strPureParams == null) {
			return null;
		}
		
		int iCaseFlowId = cfi.getCaseFlowId();
		CaseFlow caseFlow = DbGet.getCaseFlowByCaseFlowId(iCaseFlowId);
		
		//获取先前的案例
		Case pc = DbGet.getFlowCaseByStepNo(caseFlow, iPreviousCaseStep);
		//获取先前案例的实例
		CaseInstance pci = DbGet.getCaseInstanceByFlowCase(cfi.getId(), pc);
		//获取先前案例对应的交易
		Transaction ptrans = DbGet.getTransctionByTransactionId(pc.getTransactionId());
		
		TransactionDynamicParameter transParam = DbGet.getTransactionDynamicParameter(ptrans.getTransactionId(), strParam, DbGet.m_sysType.getSystemId());
		String strValue = DbGet.getTransactionParameterValue(transParam, pci.getId(), iPreviousCaseStep);
		if (strValue == null) {
			return strPureParams;
		}
		
		//两边加引号
		if (strValue != null &&  strValue.indexOf(0) != '"' ) {
			strValue = "\"" + strValue + "\"";  
		}
		
		strPureParams = strPureParams.replace(siDot + strParam, strValue);

		return strPureParams;
	}
	
	
	private static String replaceParsedParamValues(String strSql, Transaction trans) {
		
		String strNewSql = strSql;
		int iPosOfComma = strSql.indexOf(",");
		if (iPosOfComma < 0) {
			return strSql;
		}
		//strSql = "SELECT CTF_IDC FROM COR.M3PRCERT WHERE CLT_PID IN ( select CLT_PID FROM COR.M3PRINDB WHERE CLT_org_num='%s', 客户号M3ADSRII);";
		//先替换掉逗号之后的所有参数
		String strParamStrAfterComma = strSql.substring(iPosOfComma + 1);
		String strParams[] = strParamStrAfterComma.split(",");
		for(int i=0; i<strParams.length; i++) {
			String strParam = strParams[i].trim();
			//一定是参数吗？
			if (strParam == null || strParam.isEmpty() || strParam.substring(0,1) == "\"")	{ //常量
				continue;
			}
			//String strParam0 = strParam; 
			strParam = strParam.replace(")", "");
			strParam = strParam.replace(";", "");
			if (isTransactionParameter(strParam, trans.getTransactionId())) {
				//获取参数值
				String strParseParamValue = getParseParameterValueByParamPathName(strParam, trans);
				//if (getThreadLocalCaseParam().get(strParam) != null) {
				if (strParseParamValue != null) {
					String strParamValue = ""; //在SQL语句中左右要加引号，表示为常量
					strParamValue += "\"";
					//strParamValue += getThreadLocalCaseParam().get(strParam);
					strParamValue += strParseParamValue;
					strParamValue += "\"";
					//替换
					strNewSql = strNewSql.replace(strParam, strParamValue);
					/*if (strParam0.contains(")")) {
						strNewSql += ")";
					}*/
				}
			}
		}
		
		strNewSql = replaceParamValueInTableName(strNewSql, trans);
		return strNewSql;
	}
	
	
	//替换到表名参数
	private static String replaceParamValueInTableName(String strSql, Transaction trans) {

		String strNewSql = strSql.toUpperCase();

		int iPosOfFrom = strNewSql.indexOf("FROM");
		if (iPosOfFrom <= 0) { // 没有From，不存在任何要替换的参数
			return strSql;
		}

		String strFromSql = strSql.substring(iPosOfFrom);

		int iPosOfWhere = strFromSql.toUpperCase().indexOf("WHERE");
		if (iPosOfWhere <= 0) { // 没有WHERE，不存在任何要替换的参数
			return strSql;
		}

		// 从FROM到WHERE之间的那一段SQL语句
		String strTableNameStr = strSql.substring(iPosOfFrom + "FROM".length(), iPosOfFrom + iPosOfWhere);
		strTableNameStr = strTableNameStr.trim();

		String[] tableNames = strTableNameStr.split(",");
		for (int i = 0; i < tableNames.length; i++) {
			String strTableName = tableNames[i].trim();
			int iPosOfDot1 = strTableName.indexOf(".");
			int iPosOfDot2 = strTableName.indexOf("/");
			int iPosOfDot = -1;
			if (iPosOfDot1 >= iPosOfDot2) {
				iPosOfDot = iPosOfDot1;
			} else {
				iPosOfDot = iPosOfDot2;
			}
			if (iPosOfDot > 0) { // 去掉前面的C3DTA.
				strTableName = strTableName.substring(iPosOfDot+1);
				if (isTransactionParameter(strTableName, trans.getTransactionId())) {
					// 获取参数值
					//String strParamValue = getThreadLocalCaseParam().get(strTableName);
					String strParamValue = getParseParameterValueByParamPathName(strTableName, trans);
					// 替换
					strNewSql = strNewSql.replace(strTableName, strParamValue);
				}
			}
		}

		return strNewSql;
	}
	
			
	private static String replaceFixedTransactionDatas(String strSql) {
		
		String strNewSql = strSql;
		String[] fixedParams = strSql.split(",");
		for (int i=1; i<fixedParams.length; i++)
		{	//第一个不是参数
			String strParam = fixedParams[i].trim();
			if (strParam.substring(0,1).equals("$")) {//是交易的预设值
				strNewSql = replaceOneFixedTransactionDataParam(strNewSql, strParam);
			}
		}
		return strNewSql;
	}
	
	//把SQL语句的交易数据参数给替换掉
	private static String replaceOneFixedTransactionDataParam(String strSql, String strParam) {

		String strTransParamName = strParam.substring(1); //第一个为$
		String strTransDataValue = ""; //在SQL语句中左右要加引号，表示为常量
		strTransDataValue += "\"";
		strTransDataValue += getThreadLocalTransFixed().get(strTransParamName.toUpperCase());
		strTransDataValue += "\""; 
		String strNewSql = strSql.replace(strParam, strTransDataValue);
			
		return  strNewSql;
	}
	
	
	private static boolean isTransactionParameter(String strParam, String sTransactionId) {
		
		List<TransactionDynamicParameter> transParamList = m_sysTransParamList.get(sTransactionId);
		
		if (transParamList == null)
			return false;
		
		for(int i = 0; i < transParamList.size(); i++) {
			TransactionDynamicParameter transParam = transParamList.get(i);
			SystemDynamicParameter systemParam = transParam.getSystemParameter();
			if (systemParam == null)
				continue;
			String sSystemParameterName = systemParam.getName();
			if (sSystemParameterName != null && sSystemParameterName.equals(strParam)) {
				return true;
			}
		}
		
		return false;
	}
	
	
	//解析SQL语句
	private static String parseSql(String strSql) {

		// 提取where后面的参数（where后面的第一个逗号开始为参数）
		String strUpperSql = strSql.toUpperCase();
		int iPosOfWhere = strUpperSql.indexOf("WHERE");
		if (iPosOfWhere <= 0) { // 没有逗号，不存在任何要替换的参数
			return strSql;
		}

		String strWhereSql = strSql.substring(iPosOfWhere);
		String pureSqlStatement = strSql.substring(0, iPosOfWhere);

		int iPosOfComma = strWhereSql.indexOf(",");
		if (iPosOfComma <= 0) { // 没有逗号，不存在任何要替换的参数
			return strSql;
		}
		// 从where到其后的第一个逗号之间的那一段SQL语句
		pureSqlStatement += strSql.substring(iPosOfWhere, iPosOfWhere + iPosOfComma);

		String pureSqlParameters = strSql.substring(iPosOfWhere + iPosOfComma + 1); // 第一个为 ,
		String[] params = pureSqlParameters.split(",");
		int count = params.length;
		if (count <= 0) { // ,后面没有参数
			return strSql;
		}

		for (int i = 0; i < params.length; i++) {
			String strTemp = params[i].trim();
			params[i] = strTemp;
		}

		// 查找%位置，共有count个%
		int[] index = new int[count];
		int j = 0;
		for (int i = 0; i < count; i++) {
			index[i] = pureSqlStatement.indexOf('%', j);
			j = index[i] + 1;
		}

		String[] value = new String[count];
		for (int i = 0; i < count; i++) {
			String param = params[i].trim(); // 第一个为 ,已经去掉
			int iPos = param.indexOf('"');
			if (iPos != -1) {
				int iPos2 = param.indexOf('"', iPos + 1);
				value[i] = param.substring(iPos + 1, iPos2);
				continue;
			}
		}

		StringBuilder sbSql = new StringBuilder(pureSqlStatement);
		int len = 0;
		for (int i = 0; i < count; i++) {
			try {
				// 每次替换%s时，总长度都会变化会增加 实际值长度-2个长度
				sbSql.replace(index[i] + len, index[i] + 2 + len, value[i]);
				len += value[i].length() - 2;
			} catch (Exception e) {
				log.error(e.getMessage());
			}
		}
		
		if (pureSqlParameters != null && !pureSqlParameters.isEmpty()) {
			pureSqlParameters=pureSqlParameters.trim();
		}
		if (pureSqlParameters.charAt(pureSqlParameters.length() - 1) == ';') {
			pureSqlParameters = pureSqlParameters.substring(0, pureSqlParameters.length() - 1);
		}
		int iRightBracketsCount = 0;
		while (pureSqlParameters.charAt(pureSqlParameters.length() - 1) == ')') {
			iRightBracketsCount ++;
			pureSqlParameters = pureSqlParameters.substring(0, pureSqlParameters.length() - 1);
		}
		for (int i=0; i<iRightBracketsCount; i++) {
			sbSql.append(")");
		}
		/*if (pureSqlParameters.contains(")")) { //参数后面包含有右括号的！
			sbSql.append(")");
		}*/

		return sbSql.toString();
	}
	


	public static String GetSqlQueryingResult(String strSql,
			Integer iCardId, SystemDynamicParameter systemParameter) {

		int iParameterHostType = systemParameter.getParameterHostType();

		String strIpAddress = "";
		int iPortNum = 0;
		if (iParameterHostType == 1) { // 1：指定机器
			String strDbHostId = systemParameter.getParameterHostId();
			DbHost dbHost = m_dbHostList.get(strDbHostId);
			strIpAddress = dbHost.getIpaddress();
			iPortNum = dbHost.getPortnum();
		}
		else if (iParameterHostType == 0) { // 1：当前机器
			return DbGet.GetInternalSqlQueryingResult(strSql);
		} else {
			if (iParameterHostType == 3) { // 默认的数据库主机
				strIpAddress = DbGet.m_sysType.getSqlGetDbAddr();
				DbHost dbHost = DbGet.getDbHostByIpAddr(strIpAddress, DbGet.m_sysType.getSystemId());
				iPortNum = dbHost.getPortnum();
			} else if (iParameterHostType == 2) { // 2：由所使用的卡信息来指定'
				DbHost dbHost = DbGet.getDbHostByCardId(iCardId);
				iPortNum = dbHost.getPortnum();
			}
		}

		String strQueryResult = "";
		if (DbGet.m_sysType.getSqlGetMethod() == 0) { // 400 TCP SOCKET
			StringBuilder sbRecCnt = new StringBuilder();
			try {
				strQueryResult = SqlQuerySocket.get400QueryResult(log,
						DbGet.m_sysType.getSystemId(), strSql, strIpAddress, iPortNum, sbRecCnt);
			} catch (Exception e) {
				log.error("400查询出错，错误提示信息：" + e.getMessage());
				log.error("主机IP：" + strIpAddress + "，SQL语句：" + strSql);
				return null;
			}
			if (strQueryResult != null && !strQueryResult.isEmpty()
					&& Integer.parseInt(sbRecCnt.toString()) == 1) {
				if (strQueryResult.charAt(0) == '{'
						&& strQueryResult.charAt(strQueryResult.length() - 1) == '}') {
					strQueryResult = strQueryResult.substring(1,
							strQueryResult.length() - 1);
					strQueryResult = strQueryResult.trim(); // 去掉空格
				}
			}
		} 
		else if (DbGet.m_sysType.getSqlGetMethod() == 1) { // JDBC
			try {
				strQueryResult = SqlQueryJdbc.getJdbcQueryResult(strSql, strIpAddress);
			} catch (Exception e) {
				log.error("jdbc查询出错，错误提示信息：" + e.getMessage());
				log.error("数据库主机IP：" + strIpAddress + "，SQL语句：" + strSql);
				return null;
			}
		}
		return strQueryResult;

	}
		
	private static void printTransactionParamSequenceList(List<TransactionDynamicParameter> transParamSequenceList) {

		String strTransParamSequenceListStr = "";
		for (int i=0; i<transParamSequenceList.size(); i++) {
			TransactionDynamicParameter transactionParameter = transParamSequenceList.get(i);
			if (transactionParameter != null) {
				strTransParamSequenceListStr += transactionParameter.getSystemParameter().getName() + " > ";
			}
		}
		
		System.out.println(strTransParamSequenceListStr);
	}
	
	
	public static Map<String, String> getThreadLocalCaseParam() {
		Map<String, String> item = m_caseParamValueList.get();
		if(item == null) {
			//这样可保证不为null
			item = new HashMap<String, String>();
			m_caseParamValueList.set(item);
		}
		return item;
	}

	
	public static Map<String, String> getThreadLocalTransFixed() {
		Map<String, String> item = m_transFixedDataList.get();
		if(item == null) {
			//这样可保证不为null
			item = new HashMap<String, String>();
			m_transFixedDataList.set(item);
		}
		return item;
	}


}
