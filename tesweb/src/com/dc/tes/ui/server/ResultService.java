package com.dc.tes.ui.server;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.dc.tes.data.DALFactory;
import com.dc.tes.data.IDAL;

import com.dc.tes.data.db.derby.DalFactory;

import com.dc.tes.data.model.CaseFlowInstance;
import com.dc.tes.data.model.ExecuteLog;
import com.dc.tes.data.model.CaseFlow;
import com.dc.tes.data.model.CaseInstance;
import com.dc.tes.data.model.CaseInstanceFieldValue;
import com.dc.tes.data.model.CaseInstanceSqlValue;
import com.dc.tes.data.model.CaseParameterExpectedValue;
import com.dc.tes.data.model.SystemDynamicParameter;
import com.dc.tes.data.model.TestRound;
import com.dc.tes.data.model.Transaction;
import com.dc.tes.data.model.TransactionDynamicParameter;
import com.dc.tes.data.model.User;
import com.dc.tes.data.model.Case;
import com.dc.tes.data.op.Op;
import com.dc.tes.dom.MsgDocument;
import com.dc.tes.dom.util.MsgLoader;
import com.dc.tes.fcore.compare.CompareResult;
import com.dc.tes.fcore.compare.CompareService;
import com.dc.tes.ui.client.IResultService;
import com.dc.tes.ui.client.model.GWTPack_Struct;
import com.dc.tes.ui.client.model.GWTResultCompare;
import com.dc.tes.ui.client.model.GWTResultDetailLog;
import com.dc.tes.ui.client.model.GWTResultLog;
import com.dc.tes.ui.client.model.GWTResultLogMsg;
import com.dc.tes.ui.client.model.GWTTestRound;
import com.dc.tes.ui.client.model.PageStartEnd;
import com.dc.tes.ui.util.ISystemConfig;
import com.dc.tes.ui.util.SystemConfigManager;
import com.dc.tes.ui.util.TranStructTreeUtil;
import com.extjs.gxt.ui.client.data.BasePagingLoadResult;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;


public class ResultService extends RemoteServiceServlet implements IResultService {

	private static final long serialVersionUID = 1L;
	private static final Log log = LogFactory.getLog(ResultService.class);

	private IDAL<ExecuteLog> executeLogDAL = DALFactory.GetBeanDAL(ExecuteLog.class);
	private IDAL<CaseInstance> caseInstanceDAL = DALFactory.GetBeanDAL(CaseInstance.class);
	private IDAL<CaseFlowInstance> cfiDAL = DALFactory.GetBeanDAL(CaseFlowInstance.class);
	private IDAL<User> userDAL = DALFactory.GetBeanDAL(User.class);
	//private IDAL<Case> caseDao = DALFactory.GetBeanDAL(Case.class);


	@Override
	public PagingLoadResult<GWTResultLog> GetList(String sysId, String searchKey, PagingLoadConfig config) {
		
		try {
			List<GWTResultLog> returnList = new ArrayList<GWTResultLog>();
			Op[] conditions;
			int count;
			List<ExecuteLog> lst;
			
			List<Op> conList = new ArrayList<Op>();
			conList.add(Op.EQ("systemId", Integer.parseInt(sysId)));			
			if (config.get("date") != null) {
				String date = config.get("date");
				conList.add(Op.LIKE("createTime", date));
			} 			
			if(config.get("flag") != null){
				if(!config.get("flag").equals("-1")){
					conList.add(Op.EQ(GWTResultLog.N_PassFlag, Integer.parseInt(config.get("flag").toString())));
				}
			}
			if(config.get("user") != null){
				if(!config.get("user").equals("-1")){
					conList.add(Op.EQ("userId", Integer.parseInt(config.get("user").toString())));
				}
			}
			if(config.get("roundId") != null){
				if(Integer.parseInt(config.get("roundId").toString()) != -1){
					conList.add(Op.EQ("roundId", Integer.parseInt(config.get("roundId").toString())));
				}
			}
			
			conditions = new Op[conList.size()];
			for(int i = 0; i < conList.size(); i++){
				conditions[i] = conList.get(i);
			}
			
			if (searchKey.isEmpty()) {
				count = executeLogDAL.Count(conditions);
				PageStartEnd pse = new PageStartEnd(config, count);
				lst = executeLogDAL.List(pse.getStart(), pse.getEnd(), conditions);
			} 
			else {
				String[] properties = {"executeBatchNo", "executeSetName", GWTResultLog.N_CreateTime };
				count = executeLogDAL.MatchCount(searchKey, properties, conditions);
				PageStartEnd pse = new PageStartEnd(config, count);
				lst = executeLogDAL.Match(searchKey, properties, pse.getStart(), pse.getEnd(), conditions);
			}

			// List<GWTResultLog> returnList = new ArrayList<GWTResultLog>();
			for (ExecuteLog executeLog : lst) {
				returnList.add(BeanToModel(executeLog));
			}
			// return null;
			return new BasePagingLoadResult<GWTResultLog>(returnList, config.getOffset(), count);
		}
		catch (Exception ex) {
			log.error(ex, ex);
			throw new RuntimeException(ex);
		}
	}

	private GWTResultLog BeanToModel(ExecuteLog executeLog) {
		if (executeLog == null)
			return null;
		String ID = executeLog.getId() == null ? "" : executeLog.getId()
				.toString();
		String QUEUELISTID = executeLog.getExecuteSetId() == null ? ""
				: executeLog.getExecuteSetId().toString();
		String EXECUTEBATCHNO = executeLog.getExecuteBatchNo();
		String DESCRIPTION = executeLog.getDescription();
		String USERID = executeLog.getUserId() == null ? "" : executeLog
				.getUserId().toString();
		String SYSTEMID = executeLog.getSystemId() == null ? "" : executeLog
				.getSystemId().toString();
		;
		String CREATETIME = executeLog.getCreateTime() == null ? ""
				: executeLog.getCreateTime();
		// 如果QUEUELISTID 不为空，则进行查找对应的名称，并填进去
		String strQueueListName = executeLog.getExecuteSetName();

		// 如果用户ID不为空，则查找用户名称
		String UsrName = "";
		if (USERID != "") {
			try {

				Op[] conditions = new Op[] { Op.EQ("id", (USERID)) };
				List<User> lst = userDAL.ListAll(conditions);
				if (lst.size() != 0) {
					for (User user : lst) {
						UsrName = user.getName(); // 用户名称
					}
				}
				// gwtResultLogList.set("N_QUEUELISTNAME", strQueueListName);
			} catch (Exception ex) {
				log.error(ex, ex);
			}
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String endRunTime = executeLog.getEndRunTime() == null ? ""
				:sdf.format(executeLog.getEndRunTime());

		GWTResultLog gwtResultLogList = new GWTResultLog(ID, QUEUELISTID,
				EXECUTEBATCHNO, DESCRIPTION, USERID, SYSTEMID, CREATETIME,
				strQueueListName, UsrName, endRunTime, executeLog.getType());
		String flag = executeLog.getPassFlag()==null?"":executeLog.getPassFlag().toString();
		gwtResultLogList.setPassFlag(flag);
		gwtResultLogList.setRunDuration(executeLog.getRunDuration());
		return gwtResultLogList;
	}

	
	public PagingLoadResult<GWTResultDetailLog> GetDetailList(
			String executeLogId, String searchKey, PagingLoadConfig config, Integer flag) {

		try {
			int count = 0;
			List<Op> conList = new ArrayList<Op>();
			conList.add( Op.EQ("executeLogId",
						Integer.parseInt(executeLogId)));
			
			count = cfiDAL.Count(conList.get(0));
			if(count != 0){ // 当执行结果里存在caseFlowInstance，则表示此执行记录为用例，而非单独的用例步骤
				
				if(flag != -1 && flag != 10){
					conList.add(Op.EQ("caseFlowPassFlag",  flag));
				}else if(flag == 10){ //其它状态，即不为通过，失败，超时的状态
					Integer[] status = {2,3,4};
					conList.add(Op.IN("caseFlowPassFlag", status));
				}
				Op[] conditions = new Op[conList.size()];
				for(int i = 0; i < conList.size(); i++){
					conditions[i] = conList.get(i);
				}
				List<CaseFlowInstance> lst;
				if (searchKey.isEmpty()) {
					count = cfiDAL.Count(conditions);
					PageStartEnd pse = new PageStartEnd(config, count);
					lst = cfiDAL.List(pse.getStart(), pse.getEnd(),
							conditions);
				}else {
					
					String[] properties = { "caseFlowNo", "caseFlowName"};
					count = cfiDAL.MatchCount(searchKey, properties,
							conditions);
					PageStartEnd pse = new PageStartEnd(config, count);
					lst = cfiDAL.Match(searchKey, properties,
							pse.getStart(), pse.getEnd(), conditions);
				}
				List<GWTResultDetailLog> returnList = new ArrayList<GWTResultDetailLog>();
				for (CaseFlowInstance caseFlowInstance : lst) {
					returnList.add(BeanToModel(caseFlowInstance));
				}
				// return null;
				return new BasePagingLoadResult<GWTResultDetailLog>(returnList,
						config.getOffset(), count);
			}else{//当count = 0的时候
				if(flag != -1){
					conList.add(Op.EQ("casePassFlag",  flag));
				}
				Op[] conditions = new Op[conList.size()];
				for(int i = 0; i < conList.size(); i++){
					conditions[i] = conList.get(i);
				}
				List<CaseInstance> lst;
				if (searchKey.isEmpty()) {
					count = caseInstanceDAL.Count(conditions);
					PageStartEnd pse = new PageStartEnd(config, count);
					lst = caseInstanceDAL.List(pse.getStart(), pse.getEnd(),
							conditions);
				}else {
					
					String[] properties = { "caseNo", "caseName"};
					count = caseInstanceDAL.MatchCount(searchKey, properties,
							conditions);
					PageStartEnd pse = new PageStartEnd(config, count);
					lst = caseInstanceDAL.Match(searchKey, properties,
							pse.getStart(), pse.getEnd(), conditions);
				}
				List<GWTResultDetailLog> returnList = new ArrayList<GWTResultDetailLog>();
				for (CaseInstance caseInstance : lst) {
					returnList.add(BeanToModel(caseInstance));
				}
				// return null;
				return new BasePagingLoadResult<GWTResultDetailLog>(returnList,
						config.getOffset(), count);
			}
			
		} catch (Exception ex) {
			log.error(ex, ex);
			throw new RuntimeException(ex);
		}
	}
	

	public static GWTResultDetailLog BeanToModel(CaseInstance caseInstanceInfo) {
		if (caseInstanceInfo == null)
			return null;
		
		GWTResultDetailLog gwtResultDetail = null;
		try {
			IDAL<Transaction> tranDal = DALFactory.GetBeanDAL(Transaction.class);
			Transaction tran = tranDal.Get(Op.EQ(Transaction.N_TransID, caseInstanceInfo.getTransactionId().toString()));
			Case casebean = DalFactory.GetBeanDAL(Case.class).Get(Op.EQ("caseId", caseInstanceInfo.getCaseId().toString()));
			CaseFlow caseFlow = casebean.getCaseFlow();  //获取业务流
			gwtResultDetail = new GWTResultDetailLog(
					caseInstanceInfo.getId().toString(), caseInstanceInfo
							.getCaseId().toString(), tran.getTransactionId().toString(),
							caseInstanceInfo
							.getExecuteLogId().toString(),
					caseInstanceInfo.getDbHost(),
					caseInstanceInfo.getCaseFlowInstance() == null ? ""
							: caseInstanceInfo.getCaseFlowInstance().getId()
									.toString(),
					caseInstanceInfo.getField37(),
					caseInstanceInfo.getCasePassFlag() == null ? ""
							: caseInstanceInfo.getCasePassFlag().toString(),
					caseInstanceInfo.getReceivedReplayFlag() == null ? ""
							: caseInstanceInfo.getReceivedReplayFlag().toString(),
					caseInstanceInfo.getValue4NextCase(),
					caseInstanceInfo.getRequestXml(),
					caseInstanceInfo.getExpectedXml(),
					caseInstanceInfo.getResponseMsg() == null ? ""
							: caseInstanceInfo.getResponseMsg(),
										
					caseFlow == null?"":caseFlow.getId().toString(),
					caseInstanceInfo.getCaseFlowInstance() == null ? ""
							: caseInstanceInfo.getCaseFlowInstance()
									.getCaseFlowPassFlag().toString(),
									
					caseFlow == null?"":caseFlow.getCaseFlowName(),
					caseFlow == null?"":caseFlow.getCaseFlowNo(),

					caseInstanceInfo.getCaseName(), caseInstanceInfo.getCaseNo(),
					caseInstanceInfo.getCardNumber(), caseInstanceInfo.getAmount(),
					caseInstanceInfo.getBreakPointFlag()==null?"0":caseInstanceInfo.getBreakPointFlag().toString(),
					caseInstanceInfo.getRequestMsg()==null? "":caseInstanceInfo.getRequestMsg(),
					caseInstanceInfo.getSequence()==null ? "" : caseInstanceInfo.getSequence().toString()

			);
			gwtResultDetail.setGwtCaseFlow(BatchService.BeanToModel(caseFlow));
			if(tran != null)
				gwtResultDetail.SetTranName(tran.getTranName());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return gwtResultDetail;
	}


	@Override
	public void DeleteResult(List<GWTResultLog> selection) {
		// 一行一行的进行删除
		// String name = "";
		try {

			for (GWTResultLog result : selection) {
				List<ExecuteLog> delList = executeLogDAL.ListAll(Op.EQ("id",
						Integer.parseInt(result.getID())));
				for (ExecuteLog execute : delList) {
					executeLogDAL.Del(execute);
				}
			}
		} catch (Exception ex) {
			log.error(ex, ex);
			throw new RuntimeException(ex);
		}
	}

	
	@Override
	public List<GWTResultCompare> GetCompareList( //获取参数比对结果
			GWTResultDetailLog gwtResultDetailLog) {
		// TODO Auto-generated method stub
		IDAL<CaseInstanceFieldValue> fieldValueDAL = DALFactory
				.GetBeanDAL(CaseInstanceFieldValue.class);
		IDAL<CaseInstanceSqlValue> sqlValueDAL = DALFactory
				.GetBeanDAL(CaseInstanceSqlValue.class);
		IDAL<CaseParameterExpectedValue> caseParamExpValDAL = DALFactory
				.GetBeanDAL(CaseParameterExpectedValue.class);
		IDAL<TransactionDynamicParameter> tranParamDAL = DALFactory
				.GetBeanDAL(TransactionDynamicParameter.class);

		List<GWTResultCompare> gwtResultCompares = new ArrayList<GWTResultCompare>();
		List<TransactionDynamicParameter> transParams = tranParamDAL.ListAll(Op
				.EQ("transactionId", gwtResultDetailLog.getTransactionId()));
		for (TransactionDynamicParameter tranParam : transParams) {//遍历该交易类型下的所有交易参数
			if (!tranParam.getSystemParameter().getDisplayFlag().equals("1")) {
				continue; //参数为不可见时继续
			}
			CaseParameterExpectedValue caseParam = caseParamExpValDAL.Get( //获取参数的预期值
					Op.EQ("caseId", gwtResultDetailLog.GetCaseID()),
					Op.EQ("transParameter", tranParam)); 
			
			SystemDynamicParameter sysParam = tranParam.getSystemParameter();   
			
			GWTResultCompare gwtResultCompare = new GWTResultCompare();
			gwtResultCompare.SetCaseName(gwtResultDetailLog.GetCaseName());
			gwtResultCompare.SetParamName(sysParam.getName());
			gwtResultCompare.SetParamDesc(sysParam.getDesc());
			gwtResultCompare.SetParamType(getTypeCHS(sysParam
					.getParameterType()));
			gwtResultCompare.SetCompareCondition(sysParam.getCompareCondition());

			String defExpVal = sysParam.getDefaultExpectedValue();
			if(caseParam==null && defExpVal !=null && !defExpVal.isEmpty()){
				gwtResultCompare.SetExpVal(defExpVal);
			}
			if (sysParam.getParameterType().equals("1")) {   //SQL参数
				List<CaseInstanceSqlValue> caseInsSqlVals = sqlValueDAL
						.ListAll(Op.EQ("caseInstanceId",
								Integer.parseInt(gwtResultDetailLog.getID())),
								Op.EQ("transParameter",
										tranParam));
				CaseInstanceSqlValue caseInsSqlVal = null;
				if (caseInsSqlVals != null && !caseInsSqlVals.isEmpty()) {
					caseInsSqlVal = caseInsSqlVals.get(0);
				}
				if (caseInsSqlVal != null) {										
					gwtResultCompare.SetExpVal(caseInsSqlVal.getExpectedValue());
					gwtResultCompare.SetRealVal(caseInsSqlVal.getRealValue());
					gwtResultCompare.SetRealSql(caseInsSqlVal.getRealSql());
				}
			} else if (sysParam.getParameterType().equals("0")) {  //报文类参数
				CaseInstanceFieldValue caseInstanceFieldValue = fieldValueDAL
						.Get(Op.EQ("caseInstanceId", Integer.parseInt(gwtResultDetailLog.getID())),
								Op.EQ("transParameter",
										tranParam));
				if (caseInstanceFieldValue != null) {
					gwtResultCompare.SetExpVal(caseInstanceFieldValue.getExpectedValue());
					gwtResultCompare.SetRealVal(caseInstanceFieldValue
							.getMsgFieldValue());
				}
			}  //end if			
			gwtResultCompares.add(gwtResultCompare);
		}
		for(GWTResultCompare gwtResultCompare : gwtResultCompares){
			if(gwtResultCompare.GetExpVal()!=null && !gwtResultCompare.GetExpVal().isEmpty()){
				if(gwtResultCompare.GetCompareCondition().equals("0")){  //匹配条件为完全一致
					if(gwtResultCompare.GetExpVal().equalsIgnoreCase(gwtResultCompare.GetRealVal())){
						gwtResultCompare.SetIsEqual("是");
					}else{
						gwtResultCompare.SetIsEqual("否");
					}
				}else if(gwtResultCompare.GetCompareCondition().equals("1")){ //匹配条件为实际值包含预期值
					if(gwtResultCompare.GetRealVal().contains(gwtResultCompare.GetExpVal())){
						gwtResultCompare.SetIsEqual("是");
					}else{
						gwtResultCompare.SetIsEqual("否");
					}
				}else{   //匹配条件为预期值包含实际值
					if(gwtResultCompare.GetExpVal().contains(gwtResultCompare.GetRealVal())){
						gwtResultCompare.SetIsEqual("是");
					}else{
						gwtResultCompare.SetIsEqual("否");
					}
				}
			}
		}
		return gwtResultCompares;
	}

	private static String getTypeCHS(String type) {
		switch (Integer.parseInt(type)) {
		case 0:
			return "报文参数";
		case 1:
			return "SQL参数";
		case 2:
			return "交易数据类参数";
		case 3:
			return "函数处理类参数";
		case 4:
			return "条件分支类参数";
		default:
			return type;
		}
	}

	@Override
	public PagingLoadResult<GWTResultDetailLog> GetDetailList2(
			String caseFlowInstanceId, String searchKey, PagingLoadConfig config) {
		// TODO Auto-generated method stub
		try {
			int count = 0;
			List<CaseInstance> lst;
			List<Op> conList = new ArrayList<Op>();
			conList.add(Op.EQ("caseFlowInstance.id",  Integer.parseInt(caseFlowInstanceId)));
			if(config.get("flag") != null){
				if(!config.get("flag").equals("-1")){
					conList.add(Op.EQ("casePassFlag", 
							Integer.parseInt(config.get("flag").toString())));
				}
			}
			Op[] conditions = new Op[conList.size()];
			for(int i = 0; i < conList.size(); i++){
				conditions[i] = conList.get(i);
			}
			if (searchKey.isEmpty()) {
				//count = caseDao.Count(conditions2);
				//count = caseFlowInstanceDAL.Count(conditions1);
				count = caseInstanceDAL.Count(conditions);
				PageStartEnd pse = new PageStartEnd(config, count);
				lst = caseInstanceDAL.List("sequence", true, pse.getStart(), pse.getEnd(), conditions);
			} else {
				String[] properties = { "caseName","caseNo","importBatchNo","amount","cardNumber" };
				count = caseInstanceDAL.MatchCount(searchKey, properties, conditions);
				PageStartEnd pse = new PageStartEnd(config, count);
				lst = caseInstanceDAL.Match(searchKey, properties, pse.getStart(), pse
						.getEnd(), conditions);
			}

			List<GWTResultDetailLog> returnList = new ArrayList<GWTResultDetailLog>();
			for (CaseInstance c : lst)
				returnList.add(BeanToModel(c));

			return new BasePagingLoadResult<GWTResultDetailLog>(returnList, config
					.getOffset(), count);
		} catch (Exception ex) {
			log.error(ex, ex);
			throw new RuntimeException(ex);
		}
	}
	
	private GWTResultDetailLog BeanToModel(CaseFlowInstance caseFlowInstance){
		GWTResultDetailLog gwt = new GWTResultDetailLog();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		gwt.set(GWTResultDetailLog.N_ID, caseFlowInstance.getId().toString());
		gwt.set(GWTResultDetailLog.N_CaseFlowInstanceID, caseFlowInstance.getId().toString());
		gwt.set(GWTResultDetailLog.N_CaseFlowName, caseFlowInstance.getCaseFlowName());
		gwt.set(GWTResultDetailLog.N_CaseFlowID, caseFlowInstance.getCaseFlowId().toString());
		gwt.set(GWTResultDetailLog.N_CaseFlowNo, caseFlowInstance.getCaseFlowNo());
		gwt.set(GWTResultDetailLog.N_BeginRunTime,caseFlowInstance.getBeginTime() == null ? "" : sdf.format(caseFlowInstance.getBeginTime()));
		gwt.set(GWTResultDetailLog.N_EndRuntime, caseFlowInstance.getEndTime() == null ? "" : sdf.format(caseFlowInstance.getEndTime()));
		gwt.set(GWTResultDetailLog.N_CaseFlowPassFlag, caseFlowInstance.getCaseFlowPassFlag().toString());
		gwt.SetRoundID(caseFlowInstance.getRoundId());
		TestRound tRound = DALFactory.GetBeanDAL(TestRound.class).Get(Op.EQ(GWTTestRound.N_RoundID, caseFlowInstance.getRoundId()));
		gwt.SetRoundName(tRound == null ? "":tRound.getRoundName());
		CaseFlow caseFlow = DALFactory.GetBeanDAL(CaseFlow.class).Get(Op.EQ("id", caseFlowInstance.getCaseFlowId()));
		gwt.setGwtCaseFlow(BatchService.BeanToModel(caseFlow));
		return gwt;
	}

	@Override
	public PagingLoadResult<GWTResultDetailLog> GetResultListByCaseFlow(
			String caseFlowId, String searchKey, PagingLoadConfig config) {
		// TODO Auto-generated method stub
		try {
			int count = 0;
			List<Op> conList = new ArrayList<Op>();
			conList.add( Op.EQ("caseFlowId",
						Integer.parseInt(caseFlowId)));
			
			if(config.get("flag") != null){
				if(!config.get("flag").equals("-1")){
					conList.add(Op.EQ("caseFlowPassFlag", 
							Integer.parseInt(config.get("flag").toString())));
				}
			}
			if(config.get("roundId") != null){
				if((Integer)config.get("roundId") != -1){
					conList.add(Op.EQ("roundId", 
						Integer.parseInt(config.get("roundId").toString())));
				}
			}
			Op[] conditions = new Op[conList.size()];
			for(int i = 0; i < conList.size(); i++){
				conditions[i] = conList.get(i);
			}
			List<CaseFlowInstance> lst;
			if (searchKey.isEmpty()) {
				count = cfiDAL.Count(conditions);
				PageStartEnd pse = new PageStartEnd(config, count);
				lst = cfiDAL.List(pse.getStart(), pse.getEnd(),
						conditions);
			}else {
				
				String[] properties = { "caseFlowNo", "caseFlowName"};
				count = cfiDAL.MatchCount(searchKey, properties,
						conditions);
				PageStartEnd pse = new PageStartEnd(config, count);
				lst = cfiDAL.Match(searchKey, properties,
						pse.getStart(), pse.getEnd(), conditions);
			}
			List<GWTResultDetailLog> returnList = new ArrayList<GWTResultDetailLog>();
			for (CaseFlowInstance caseFlowInstance : lst) {
				returnList.add(BeanToModel(caseFlowInstance));
			}
			// return null;
			return new BasePagingLoadResult<GWTResultDetailLog>(returnList,
					config.getOffset(), count);			
			
		} catch (Exception ex) {
			log.error(ex, ex);
			throw new RuntimeException(ex);
		}	
	}
	

	@Override
	public void DeleteCaseFlowInstance(List<GWTResultDetailLog> selections) {
		// TODO Auto-generated method stub
		if(selections == null){
			return;
		}
		for(GWTResultDetailLog gwt: selections){
			CaseFlowInstance ins = ModelToBean(gwt);
			List<CaseInstance> caseInstances = caseInstanceDAL.ListAll(Op.EQ("caseFlowInstance.id", ins.getId()));
			for(CaseInstance ci : caseInstances){
				caseInstanceDAL.Del(ci);
			}
			cfiDAL.Del(ins);
		}
	}
	
	private CaseFlowInstance ModelToBean(GWTResultDetailLog log){
		CaseFlowInstance cfi = cfiDAL.Get(Op.EQ("id", Integer.parseInt(log.GetCaseFlowInstanceID())));
		return cfi;
	}

	@Override
	public List<GWTPack_Struct> GetResultContent(GWTResultDetailLog gwtResultDetailLog) {
		
		GWTPack_Struct [] lst = new  GWTPack_Struct[2];
		CaseInstance ci = caseInstanceDAL.Get(Op.EQ("id", Integer.parseInt(gwtResultDetailLog.getID())));
		ISystemConfig config = SystemConfigManager.getConfigByTranID(gwtResultDetailLog.getTransactionId(), 1);
		if(ci.getRequestXml()!=null && !ci.getRequestXml().isEmpty()){
			GWTPack_Struct root = new GWTPack_Struct(
					"请求报文数据" + String.format("[%s]", ci.getCaseName()));
			GWTPack_Struct reqStruct = TranStructTreeUtil.GetGWTTreeRoot(ci.getRequestXml(), false, root, config);
			lst[0] = reqStruct;
		}
		if(ci.getResponseXml()!=null && !ci.getResponseXml().isEmpty()){
			GWTPack_Struct root = new GWTPack_Struct(
					"响应报文数据" + String.format("[%s]", ci.getCaseName()));
			GWTPack_Struct resStruct = TranStructTreeUtil.GetGWTTreeRoot(ci.getResponseXml(), true, root, config);
			lst[1] = resStruct;
		}	
		List<GWTPack_Struct> list = new ArrayList<GWTPack_Struct>();
		list.add(lst[0]);
		list.add(lst[1]);
		return list;
	}

	@Override
	public GWTResultLogMsg GetResultLogMsg(GWTResultLog log) {
		// TODO Auto-generated method stub
		List<CaseFlowInstance> cfiList = cfiDAL.ListAll(Op.EQ("executeLogId", Integer.parseInt(log.getID())));
		int caseCount = cfiList.size();
		int passCaseCount = 0; 
		int failedCaseCount = 0;
		int timeOutCaseCount = 0;
		int otherCaseCount = 0;
		for(CaseFlowInstance cfi : cfiList){
			if(cfi.getCaseFlowPassFlag().intValue() == 1){
				passCaseCount++;
			}else if(cfi.getCaseFlowPassFlag().intValue() == 0){
				failedCaseCount++;
			}else if(cfi.getCaseFlowPassFlag().intValue() == 5){
				timeOutCaseCount++;
			}
		}
		otherCaseCount = caseCount-passCaseCount-failedCaseCount-timeOutCaseCount;
		Double passrate = passCaseCount*100.0/caseCount;
		NumberFormat  formater  =  java.text.DecimalFormat.getInstance();
		formater.setMaximumFractionDigits(2);
		formater.setMinimumFractionDigits(2);  
		String passRate = formater.format(passrate)+"%";
		
		return new GWTResultLogMsg(Integer.parseInt(log.getID()), caseCount, passCaseCount,
				failedCaseCount, timeOutCaseCount, otherCaseCount, passRate);
	}

	@Override
	public GWTResultDetailLog GetCaseFlowInstance(String executeLogId, String caseFlowId) {
		// TODO Auto-generated method stub
		try{
			CaseFlowInstance cfi = cfiDAL.Get(Op.EQ("executeLogId", Integer.parseInt(executeLogId)),
					Op.EQ("caseFlowId", Integer.parseInt(caseFlowId)));		
			if(cfi == null){
				return null;
			}else
				return BeanToModel(cfi);
		}catch (Exception ex) {
			// TODO: handle exception
			log.error(ex, ex);
			throw new RuntimeException(ex);
		}		
	}

	@Override
	public GWTResultLog GetResultLog(String executeLogId) {
		// TODO Auto-generated method stub
		try{
			ExecuteLog log = executeLogDAL.Get(Op.EQ("id", Integer.parseInt(executeLogId)));
			if(log == null){
				return null;
			}else {
				return BeanToModel(log);
			}
		}catch (Exception ex) {
			// TODO: handle exception
			log.error(ex, ex);
			throw new RuntimeException(ex);
		}		
	}

	
	@Override
	/**
	 * 获取案例执行的结果比对报文内容。
	 * @param gwtResultDetailLog
	 * @return GWTPack_Struct 包含预期值与实际值的XML报文
	 */
	public GWTPack_Struct GetCompareResult(GWTResultDetailLog gwtResultDetailLog) {

		CaseInstance ci = caseInstanceDAL.Get(Op.EQ("id", Integer.parseInt(gwtResultDetailLog.getID())));
		ISystemConfig config = SystemConfigManager.getConfigByTranID(gwtResultDetailLog.getTransactionId(), 1);
		String expect = ci.getExpectedXml();
		String resContent = ci.getResponseXml();
		if(expect != null && resContent != null){
			InputStream dinputStream = new ByteArrayInputStream(expect.getBytes());
			MsgDocument doc = MsgLoader.Load(dinputStream);
			InputStream dinputStream2 = new ByteArrayInputStream(resContent.getBytes());
			MsgDocument doc2 = MsgLoader.Load(dinputStream2);
			
			//doc 期望,doc2 真实的
			CompareResult cr = CompareService.CompareDocument(doc, doc2);
			
			String copResult = cr.toString();
			//注意页面处理没有比对结果的情况
			GWTPack_Struct root = null;
			if(copResult != null && !copResult.isEmpty())
			{
				root = TranStructTreeUtil.GetCompResultRoot(copResult,
					ci.getCaseName(), config);
			}
			return root;
		}
		else{
			return null;
		}
	}

	@Override
	public GWTResultLogMsg GetTodayResultLogMsg(String systemID) {
		// TODO Auto-generated method stub
		DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		
		Date today = new Date();	
		try {
			String s = sdf.format(today);
			today = sdf.parse(s);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		List<CaseFlowInstance> cfiList = cfiDAL.ListAll(Op.EQ("systemId", systemID), Op.GE("createTime", today));
		
		int caseCount = cfiList.size();
		int passCaseCount = 0; 
		int failedCaseCount = 0;
		int timeOutCaseCount = 0;
		int otherCaseCount = 0;
		for(CaseFlowInstance cfi : cfiList){
			if(cfi.getCaseFlowPassFlag().intValue() == 1){
				passCaseCount++;
			}else if(cfi.getCaseFlowPassFlag().intValue() == 0){
				failedCaseCount++;
			}else if(cfi.getCaseFlowPassFlag().intValue() == 5){
				timeOutCaseCount++;
			}
		}
		otherCaseCount = caseCount-passCaseCount-failedCaseCount-timeOutCaseCount;
		Double passrate = passCaseCount*100.0/caseCount;
		NumberFormat  formater  =  java.text.DecimalFormat.getInstance();
		formater.setMaximumFractionDigits(2);
		formater.setMinimumFractionDigits(2);  
		String passRate = formater.format(passrate)+"%";
		
		return new GWTResultLogMsg(0, caseCount, passCaseCount,
				failedCaseCount, timeOutCaseCount, otherCaseCount, passRate);
	}
	
}
