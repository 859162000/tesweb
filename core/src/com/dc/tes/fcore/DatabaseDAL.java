package com.dc.tes.fcore;

import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.dc.tes.InMessage;
import com.dc.tes.OutMessage;
import com.dc.tes.data.DALFactory;
import com.dc.tes.data.IDAL;
import com.dc.tes.data.model.Case;
import com.dc.tes.data.model.CaseFlow;
import com.dc.tes.data.model.CaseFlowInstance;
import com.dc.tes.data.model.CaseInstance;
import com.dc.tes.data.model.ExecuteLog;
import com.dc.tes.data.model.Transaction;
import com.dc.tes.data.model.User;
import com.dc.tes.data.op.Op;
import com.dc.tes.exception.CoreErr;
import com.dc.tes.exception.TESException;
import com.dc.tes.net.DBEnum;
import com.dc.tes.util.RuntimeUtils;

public class DatabaseDAL {
		
	public static int m_iAdminUserId = -1;
	public static IDAL<User> userDAL = DALFactory.GetBeanDAL(User.class);
	public static IDAL<CaseFlowInstance> cfiDAL = DALFactory.GetBeanDAL(CaseFlowInstance.class);
	public static IDAL<CaseFlow> cfDAL = DALFactory.GetBeanDAL(CaseFlow.class);
	public static IDAL<Case> caseDAL = DALFactory.GetBeanDAL(Case.class);
	public static IDAL<Transaction> tranDAL = DALFactory.GetBeanDAL(Transaction.class);
	public static IDAL<CaseInstance> ciDAL = DALFactory.GetBeanDAL(CaseInstance.class);
	public static IDAL<ExecuteLog> elDAL = DALFactory.GetBeanDAL(ExecuteLog.class);
	
	
	public static Case newCase(Transaction trans) {
		
		Case c = new Case();
		
		c.setTransactionId(trans.getTransactionId());
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		java.util.Date datetime = new java.util.Date();
		String strDateTime=sdf.format(datetime);
		
		c.setCaseName(trans.getTranName() + "_服务端自动生成的默认案例");
		c.setCaseNo(trans.getTranCode() + "_" + strDateTime);
		c.setIsParseable(1);
		c.setFlag(1);
		c.setIsdefault(1);
		c.setDescription("服务端自动生成的默认案例");
		c.setRequestXml(trans.getRequestStruct());
		c.setResponseXml(trans.getResponseStruct());
		c.setExpectedXml(trans.getRequestStruct());
		
		caseDAL.Add(c);
		
		return c;
	}
	
	public static int getAdminUserId() {
		User user = userDAL.Get(Op.EQ("name", "Admin"));
		String sUserId = user.getId();
		return Integer.parseInt(sUserId);
	}

	public static ExecuteLog newExecuteLog(Integer systemId) {
	
		ExecuteLog log = new ExecuteLog();
		
		log.setSystemId(systemId);
		log.setType(2);
		if (DbGet.m_iRoundId > 0) {
			log.setRoundId(DbGet.m_iRoundId);
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		java.util.Date date = new java.util.Date();
		String strDate=sdf.format(date);
		log.setCreateTime(strDate);
		log.setBeginRunTime(date);
		if (m_iAdminUserId < 0) {
			m_iAdminUserId = getAdminUserId();
		}
		log.setUserId(m_iAdminUserId);
		
		elDAL.Add(log);
		
		return log;
	}
	
	
	public static CaseFlow GetCaseFlow(String caseFlowID) {
		
		return cfDAL.Get(Op.EQ(DBEnum.CaseFlow.ID, Integer.parseInt(caseFlowID)));
	}
	
	public static CaseFlowInstance newCaseFlowInstance(CaseFlow caseFlow, String executeLogID) {
		
		CaseFlowInstance cfi = cfiDAL.Get(Op.EQ(DBEnum.CaseFlowInstance.CASEFLOWID, caseFlow.getId()),
				Op.EQ(DBEnum.CaseFlowInstance.EXECUTELOGID, Integer.parseInt(executeLogID)));
		
		if(cfi != null) {
			return cfi;
		}
		
		cfi = new CaseFlowInstance();
		cfi.setCaseFlowName(caseFlow.getCaseFlowName());
		cfi.setCaseFlowId(caseFlow.getId());
		cfi.setCaseFlowNo(caseFlow.getCaseFlowNo());
		cfi.setCreateTime(new Date());
		cfi.setBeginTime(new Date());
		cfi.setCaseFlowPassFlag(0);
		cfi.setExecuteLogId(Integer.valueOf(executeLogID));
		cfi.setCaseFlowPassFlag(2);
		cfi.setSystemId(caseFlow.getSystemId().toString());
		if (DbGet.m_iRoundId > 0) {
			cfi.setRoundId(DbGet.m_iRoundId);
		}
		
		cfiDAL.Add(cfi);
		
		return cfi;
	}
	
	public static Case GetFirstCaseFromCaseFlow(CaseFlow caseFlow) {
		
		return caseDAL.Get(Op.EQ(DBEnum.Case.CASEFLOW, caseFlow), Op.EQ(DBEnum.Case.SEQUENCE, 0));
	}
	
	public static Case GetNextCaseFromCaseFlow(CaseFlow caseFlow, String executeLogID,CaseInstance ci) {
		
		int sequence;
		if(ci == null) {
			//通过找业务流实例表下已经有多少个案例实例来确定下个案例
			//好像不太好？但是可以有利于代码规整,待定
			CaseFlowInstance cfi = cfiDAL.Get(Op.EQ(DBEnum.CaseFlowInstance.CASEFLOWID, caseFlow.getId()),
					Op.EQ(DBEnum.CaseFlowInstance.EXECUTELOGID, Integer.parseInt(executeLogID)));
			sequence = ciDAL.Count(Op.EQ(DBEnum.CaseInstance.CASEFLOWINSTANCE, cfi));		
		} else {
			sequence = ci.getSequence() + 1;
		}
		Case c = caseDAL.Get(Op.EQ(DBEnum.Case.CASEFLOW, caseFlow), Op.EQ(DBEnum.Case.SEQUENCE, sequence));
		
//		if(c == null)
//			throw new TESException(CoreErr.CaseInFlowNotFound,"案例步骤"+sequence);
		
		return c;
	}

	public static Transaction GetTransactionByCase(Case icase) {
		
		return tranDAL.Get(Op.EQ(DBEnum.Transaction.ID, icase.getTransactionId()));
	}

	/**
	 * 新增案例实例
	 * @param icase 案例
	 * @param out 
	 */
	public static CaseInstance newCaseInstance(Case icase,CaseFlowInstance cfi, OutMessage out, InMessage in) {
		// TODO Auto-generated method stub
		
		CaseInstance ci = new CaseInstance();
		ci.setCaseId(Integer.parseInt(icase.getCaseId()));
		ci.setBeginRunTime(new Date());
		ci.setCaseName(icase.getCaseName());
		ci.setCaseNo(icase.getCaseNo());
		ci.setSequence(icase.getSequence());
		ci.setTransactionId(Integer.parseInt(icase.getTransactionId()));
		if(icase.getAmount() != null) {
			ci.setAmount(Float.toString(icase.getAmount()));
		}
		ci.setCardId(icase.getCardId());
		ci.setExecuteLogId(Integer.parseInt(out.executeLogID));
		ci.setCaseFlowInstance(cfi);
		if(out.caseIndex != null)
			ci.setField37(out.caseIndex);
		
		ci.setReceivedReplayFlag(0);
		//ci.setImportBatchNo(icase.getImportBatchNo());
		//ci.setRequestXml(out.data.toString());
		ci.setRequestMsg(icase.getRequestXml());
		
		/*
		//获取应答报文的编码方式
		String encoding = "utf-8";
		
		if (out.bin != null) {
			if (in != null) { //接收端模拟
				//请求报文
				if (DbGet.m_sysType.getEncoding4RequestMsg() != null && !DbGet.m_sysType.getEncoding4RequestMsg().isEmpty()) {
					encoding = DbGet.m_sysType.getEncoding4RequestMsg();
				}
				String strInMsg = new String(in.bin,  Charset.forName(encoding));
				//非xml文本，以16进制方式打印，兼容不可见字符
				if(!strInMsg.startsWith("<?xml")) {
					if(!strInMsg.contains("<?xml")) {
						strInMsg = RuntimeUtils.PrintHex(out.bin, Charset.forName(encoding));
					}
				}
				ci.setRequestMsg(strInMsg);
				//应答报文
				if (DbGet.m_sysType.getEncoding4ResponseMsg() != null && !DbGet.m_sysType.getEncoding4ResponseMsg().isEmpty()) {
					encoding = DbGet.m_sysType.getEncoding4ResponseMsg();
				}
				String strOutMsg = new String(out.bin,  Charset.forName(encoding));
				//非xml文本，以16进制方式打印，兼容不可见字符
				if(!strOutMsg.startsWith("<?xml")) {
					if(!strOutMsg.contains("<?xml")) {
						strOutMsg = RuntimeUtils.PrintHex(out.bin, Charset.forName(encoding));
					}
				}
				ci.setResponseMsg(strOutMsg);
				ci.setCasePassFlag(1); //通过
			}
			else { //发起端模拟
				//请求报文
				if (DbGet.m_sysType.getEncoding4RequestMsg() != null && !DbGet.m_sysType.getEncoding4RequestMsg().isEmpty()) {
					encoding = DbGet.m_sysType.getEncoding4RequestMsg();
				}
				String strOutMsg = new String(out.bin,  Charset.forName(encoding));
				//非xml文本，以16进制方式打印，兼容不可见字符
				if(!strOutMsg.startsWith("<?xml")) {
					if(!strOutMsg.contains("<?xml")) {
						strOutMsg = RuntimeUtils.PrintHex(out.bin, Charset.forName(encoding));
					}
				}
				ci.setRequestMsg(strOutMsg);
				ci.setCasePassFlag(2); //正在执行
			}
		}*/
		
		SetCaseInstanceMsg(ci, icase, cfi, out, in);
		
		ciDAL.Add(ci);
		return ci;
	}
	
	public static void SetCaseInstanceMsg(CaseInstance ci, Case icase,CaseFlowInstance cfi, OutMessage out, InMessage in) {
		
		//获取应答报文的编码方式
		String encoding = "utf-8";
		
		if (out.bin != null) {
			if (in != null) { //接收端模拟
				//请求报文
				if (DbGet.m_sysType.getEncoding4RequestMsg() != null && !DbGet.m_sysType.getEncoding4RequestMsg().isEmpty()) {
					encoding = DbGet.m_sysType.getEncoding4RequestMsg();
				}
				String strInMsg = new String(in.bin,  Charset.forName(encoding));
				//非xml文本，以16进制方式打印，兼容不可见字符
				if(!strInMsg.startsWith("<?xml")) {
					if(!strInMsg.contains("<?xml")) {
						strInMsg = RuntimeUtils.PrintHex(out.bin, Charset.forName(encoding));
					}
				}
				ci.setRequestMsg(strInMsg);
				//应答报文
				if (DbGet.m_sysType.getEncoding4ResponseMsg() != null && !DbGet.m_sysType.getEncoding4ResponseMsg().isEmpty()) {
					encoding = DbGet.m_sysType.getEncoding4ResponseMsg();
				}
				String strOutMsg = new String(out.bin,  Charset.forName(encoding));
				//非xml文本，以16进制方式打印，兼容不可见字符
				if(!strOutMsg.startsWith("<?xml")) {
					if(!strOutMsg.contains("<?xml")) {
						strOutMsg = RuntimeUtils.PrintHex(out.bin, Charset.forName(encoding));
					}
				}
				ci.setResponseMsg(strOutMsg);
				ci.setCasePassFlag(1); //通过
			}
			else { //发起端模拟
				//请求报文
				if (DbGet.m_sysType.getEncoding4RequestMsg() != null && !DbGet.m_sysType.getEncoding4RequestMsg().isEmpty()) {
					encoding = DbGet.m_sysType.getEncoding4RequestMsg();
				}
				String strOutMsg = new String(out.bin,  Charset.forName(encoding));
				//非xml文本，以16进制方式打印，兼容不可见字符
				if(!strOutMsg.startsWith("<?xml")) {
					if(!strOutMsg.contains("<?xml")) {
						strOutMsg = RuntimeUtils.PrintHex(out.bin, Charset.forName(encoding));
					}
				}
				ci.setRequestMsg(strOutMsg);
				ci.setCasePassFlag(2); //正在执行
				ci.setExpectedXml(icase.getExpectedXml());
			}
		}
	} 
	
	/**
	 * 通过索引来获取案例实例(索引其实是有可能出现重复的现象,因此取最新的记录)
	 * @param index
	 * @param ciDAL
	 * @return
	 */
	public static CaseInstance GetCaseInstance(String index) throws Exception {
		
		List<CaseInstance> caseList = ciDAL.ListAll(DBEnum.CaseInstance.ID, false, 
				Op.EQ(DBEnum.CaseInstance.CASEINDEX, index),
				Op.EQ(DBEnum.CaseInstance.RECEIVEDREPLAYFLAG, 0));
		
		if(caseList.size() == 0)
			throw new TESException(CoreErr.CaseInstanceNotFound,"案例流水号: "+index);
		
		return caseList.get(0);
	}
	
	public static Case GetCase(int caseID) {
		
		return caseDAL.Get(Op.EQ(DBEnum.Case.ID, String.valueOf(caseID)));	
	}
	
	public static void SetTimeOut(String caseID, String executeLogID) {
		
		CaseInstance ci = ciDAL.Get(Op.EQ(DBEnum.CaseInstance.CASEID, Integer.parseInt(caseID)),
				Op.EQ(DBEnum.CaseInstance.EXECUTELOGID, Integer.parseInt(executeLogID)));
		ci.setCasePassFlag(7);
		ciDAL.Edit(ci);
		CaseFlowInstance cfi = ci.getCaseFlowInstance();
		cfi.setCaseFlowPassFlag(7);			
		cfiDAL.Edit(cfi);
		ExecuteLog log = elDAL.Get(Op.EQ(DBEnum.ExecuteLog.ID, Integer.parseInt(executeLogID)));
		log.setPassFlag(7);
		elDAL.Edit(log);
	}


}
