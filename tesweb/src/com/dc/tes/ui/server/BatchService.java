package com.dc.tes.ui.server;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;
import org.hibernate.Session;

import com.dc.tes.data.DALFactory;
import com.dc.tes.data.IDAL;
import com.dc.tes.data.db.HibernateUtils;
import com.dc.tes.data.model.CaseImportBatch;
import com.dc.tes.data.model.ScriptFlow;
import com.dc.tes.data.model.Card;
import com.dc.tes.data.model.Case;
import com.dc.tes.data.model.CaseFlow;
import com.dc.tes.data.model.ExecuteSet;
import com.dc.tes.data.model.Transaction;
import com.dc.tes.data.model.User;
import com.dc.tes.data.op.Op;
import com.dc.tes.ui.client.IBatchService;
import com.dc.tes.ui.client.model.GWTBatchNo;
import com.dc.tes.ui.client.model.GWTScriptFlow;
import com.dc.tes.ui.client.model.GWTCard;
import com.dc.tes.ui.client.model.GWTCase;
import com.dc.tes.ui.client.model.GWTCaseFlow;
import com.dc.tes.ui.client.model.GWTSimuSystem;
import com.dc.tes.ui.client.model.GWTTransaction;
import com.dc.tes.ui.client.model.GWTUser;
import com.dc.tes.ui.client.model.PageStartEnd;
import com.dc.tes.ui.util.XmlPackUtil;
import com.extjs.gxt.ui.client.data.BasePagingLoadResult;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class BatchService extends RemoteServiceServlet implements IBatchService {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8036875462486534420L;

	private static final Log log = LogFactory.getLog(BatchService.class);
	
	private IDAL<CaseImportBatch> caseImportBatchDAL = DALFactory.GetBeanDAL(CaseImportBatch.class);
	private IDAL<Case> caseDAL = DALFactory.GetBeanDAL(Case.class);
	private IDAL<Card> cardDAL = DALFactory.GetBeanDAL(Card.class);
	private static IDAL<CaseFlow> caseFlowDAL = DALFactory.GetBeanDAL(CaseFlow.class);
	
	
	private static GWTBatchNo BeanToModel(CaseImportBatch batch){
		if(batch == null)
			return null;
		IDAL<User> userIdal = DALFactory.GetBeanDAL(User.class);
		String userName = userIdal.Get(Op.EQ(GWTUser.N_id, batch.getUserId().toString())).getName();
		GWTBatchNo gwtBatchNo = new GWTBatchNo(batch.getId(), batch.getSystemId(),
				userName, batch.getBatchNo(), batch.getDescription(),
				batch.getImportTime().toString());
		return gwtBatchNo;
	}
	
	private static GWTCard BeanToModel(Card card){
		if(card == null)
			return null;
		GWTCard gwtCard = new GWTCard(card.getId().toString(), card.getDbHost(), card.getSubBankNo(),
				card.getSubsidiaryNo(), card.getImportBatchNo(), card.getSequence(), 
				card.getCardNumber(), card.getCardType(), card.getCardPwd(), card.getCardStatus(),
				card.getVaildUntil(), card.getTrack2(), card.getTrack3(), 
				card.getMagnetiIcStripe(), card.getCvcCod(), card.getDescription());
		return gwtCard;
	}
	

	public static GWTCaseFlow BeanToModel(CaseFlow caseFlow) {
		// TODO Auto-generated method stub
		if(caseFlow == null){
			return null;
		}
		IDAL<User> userIdal = DALFactory.GetBeanDAL(User.class);
		String userName = userIdal.Get(Op.EQ(GWTUser.N_id, caseFlow.getCreatedUserId().toString())).getName();
		GWTCaseFlow gwtCaseFlow = new GWTCaseFlow(caseFlow.getId().toString(),
				caseFlow.getCaseFlowName(), caseFlow.getCaseFlowNo(),
				caseFlow.getDescription(), caseFlow.getSystemId().toString(),
				caseFlow.getCreatedUserId().toString(), userName, caseFlow.getCreatedTime().toString());
		gwtCaseFlow.SetExtraValue(caseFlow.getBreakPointFlag()==null?null:caseFlow.getBreakPointFlag().toString(),
				caseFlow.getCaseFlowPath(), caseFlow.getDirectoryId()==null?null:caseFlow.getDirectoryId().toString(),
				caseFlow.getDesigner(), caseFlow.getCaseFlowStep(), caseFlow.getPreConditions(), caseFlow.getExpectedResult(),
				caseFlow.getCaseType(), caseFlow.getCaseProperty(), caseFlow.getPriority(), caseFlow.getDesignTime(), caseFlow.getMemo());
		
//		IDAL<ScriptFlow> busiflowIdal = DALFactory.GetBeanDAL(ScriptFlow.class);
//		ScriptFlow scriptFlow = busiflowIdal.Get(Op.EQ(GWTScriptFlow.N_ID, caseFlow.getScriptFlowId()));
//		GWTScriptFlow gwtScriptFlow = ScriptFlowService.BeanToModel(scriptFlow, false);
//		gwtCaseFlow.SetDisabledFlag(caseFlow.getDisabledFlag());
//		gwtCaseFlow.SetPassFlag(caseFlow.getPassFlag());
//		gwtCaseFlow.setScriptFlow(gwtScriptFlow);
		return gwtCaseFlow;
	}
	
	public static CaseFlow ModelToBean(CaseFlow caseFlow, GWTCaseFlow gwtCaseFlow){
		if(gwtCaseFlow == null){
			return null;
		}
		CaseFlow cf = caseFlow;
		if(cf == null){
			cf = new CaseFlow();
			if(gwtCaseFlow.GetID()!=null){
				cf = caseFlowDAL.Get(Op.EQ("id", Integer.parseInt(gwtCaseFlow.GetID())));				
			}else{
				cf.setCreatedTime(new Date());
				cf.setImportBatchNo(new Date().toString());
			}
		}
		cf.setBreakPointFlag(gwtCaseFlow.GetBreakPointFlag()==null? null:Integer.parseInt(gwtCaseFlow.GetBreakPointFlag()));
		cf.setCaseFlowName(gwtCaseFlow.GetName());
		cf.setCaseFlowNo(gwtCaseFlow.GetCaseFlowNo());
		cf.setCaseFlowPath(gwtCaseFlow.GetCaseFlowPath());
		cf.setCaseFlowStep(gwtCaseFlow.GetCaseFlowStep());
		cf.setCaseProperty(gwtCaseFlow.GetCaseProperty());
		cf.setCaseType(gwtCaseFlow.GetCaseType());
		cf.setDescription(gwtCaseFlow.GetDesc());
		cf.setSystemId(Integer.parseInt(gwtCaseFlow.GetSystemID()));
		cf.setDirectoryId(Integer.parseInt(gwtCaseFlow.GetDirectoryID()));
		cf.setExpectedResult(gwtCaseFlow.GetExpectedResult());
		cf.setPreConditions(gwtCaseFlow.GetPreConditions());
		cf.setPriority(gwtCaseFlow.GetPriority());
		cf.setDisabledFlag(gwtCaseFlow.GetDisabledFlag());
		cf.setDesigner(gwtCaseFlow.GetDesigner());
		cf.setDesignTime(gwtCaseFlow.GetDesignTime());
		cf.setMemo(gwtCaseFlow.GetMemo());
		return cf;
	}
	
	private static Card ModelToBean(Card cardBean, GWTCard gwtCard){
		if(gwtCard == null){
			return null;
		}
		Card card = cardBean;
		if(card == null){
			card = new Card();			
		}
		card.setDbHost(gwtCard.getCmbHost());
		card.setCardNumber(gwtCard.getCardNo());
		card.setCardPwd(gwtCard.getCardPwd());
		card.setCardStatus(gwtCard.getCardStatus());
		card.setCardType(gwtCard.getCardType());
		card.setSequence(Integer.parseInt(gwtCard.getSequence()));
		card.setVaildUntil(gwtCard.getVaildUntil());
		card.setTrack2(gwtCard.getTrack2());
		card.setTrack3(gwtCard.getTrack3());
		card.setSubBankNo(gwtCard.getSubBankNo());
		card.setSubsidiaryNo(gwtCard.getSubsidiaryNo());
		card.setImportBatchNo(gwtCard.getImportBatchNo());
		if(!gwtCard.GetCardID().isEmpty())
			card.setId(Integer.parseInt(gwtCard.GetCardID()));
		return card;
	}
	@Override
	public PagingLoadResult<GWTBatchNo> GetList(String sysId, String searchKey, PagingLoadConfig config) {
		try{
			Op[] conditions = new Op[] {
					Op.EQ(GWTBatchNo.N_SystemID, Integer.parseInt(sysId))
			};
			int count;
			List<CaseImportBatch> lst;
			if(searchKey.isEmpty()){
				count = caseImportBatchDAL.Count(conditions);
				PageStartEnd pse = new PageStartEnd(config, count);
				lst = caseImportBatchDAL.List(pse.getStart(), pse.getEnd(), conditions);
			}else{
				String[] properties = {
						GWTBatchNo.N_BatchNO,
						GWTBatchNo.N_Desc};
				count = caseImportBatchDAL.MatchCount(searchKey, properties, conditions);
				PageStartEnd pse = new PageStartEnd(config, count);
				lst = caseImportBatchDAL.Match(searchKey, properties, pse.getStart(), pse.getEnd(), conditions);
			}
			
			List<GWTBatchNo> returnList = new ArrayList<GWTBatchNo>();
			for(CaseImportBatch batch : lst){
				returnList.add(BeanToModel(batch));
			}

			return new BasePagingLoadResult<GWTBatchNo>(returnList, config.getOffset(), count);
	}catch (Exception ex) {
		log.error(ex, ex);
		throw new RuntimeException(ex);
	}
	}
	@Override
	public void DeleteBatch(List<GWTBatchNo> selection) {
		try{
			for(GWTBatchNo batch : selection){
				CaseImportBatch _batch = caseImportBatchDAL.Get(Op.EQ("id", batch.GetID()));
				List<Card> cardList = cardDAL.ListAll(Op.EQ(GWTCard.N_importBatchNo, batch.GetImportBatchNO()));
				for(Card card: cardList){
					cardDAL.Del(card);
				}
				caseImportBatchDAL.Del(_batch);
			}
			
		}catch (Exception ex) {
			log.error(ex, ex);
			throw new RuntimeException(ex);
		}
		
	}
	@Override
	public PagingLoadResult<GWTCase> GetDetailList(String batchID, 
			String searchKey, boolean isDisplayAll, PagingLoadConfig config) {
		try{
			Op[] conditions = new Op[] {
			//	Op.EQ(GWTCase.N_importBatchNo, batchID),
			};
			int count;
			List<Case> lst;
			if(isDisplayAll){
					
				if(searchKey.isEmpty()){
					count = caseDAL.Count(conditions);
					PageStartEnd pse = new PageStartEnd(config, count);
					lst = caseDAL.List(pse.getStart(), pse.getEnd(), conditions);
				}else{
					String[] properties = {
							GWTCase.N_caseName,
							GWTCase.N_caseNo};
					count = caseDAL.MatchCount(searchKey, properties, conditions);
					PageStartEnd pse = new PageStartEnd(config, count);
					lst = caseDAL.Match(searchKey, properties, pse.getStart(), pse.getEnd(), conditions);
				}
				List<GWTCase> returnList = new ArrayList<GWTCase>();
				for(Case case1: lst){
					returnList.add(CaseService.BeanToModel(case1));
				}
				
				
				return new BasePagingLoadResult<GWTCase>(returnList, config.getOffset(), count);
			}else{
				List<GWTCase> returnList = new ArrayList<GWTCase>();
				Session session = HibernateUtils.GetSession();
				String sql = "SELECT * FROM t_case WHERE IMPORTBATCHNO='" + batchID + "'";
				if(!searchKey.isEmpty()){
					sql += " AND (CASENAME LIKE '%" + searchKey + "%' OR CASENO LIKE '%" +
						searchKey + "%')";
				}
				sql += " AND CASEID NOT IN ( SELECT CASEID FROM t_flowcases ) " +
					"LIMIT " + config.getOffset() + ", " + config.getLimit();
				
				Query query = session.createSQLQuery(sql);
				List<?> sqlList = query.list();
				for (int i = 0 ; i < sqlList.size() ; i ++)
				{
					Object[] obj = (Object[])sqlList.get(i);
					Case casebean = new Case();
					casebean.setCaseId(obj[0].toString());
					casebean.setCaseName(obj[1].toString());
					casebean.setCaseNo(obj[2].toString());
					casebean.setTransactionId(obj[3].toString());
					casebean.setCaseId(obj[4].toString());
					casebean.setRequestXml(obj[5].toString());
					casebean.setImportBatchNo(obj[8].toString());
					if(obj[9]!=null)
						casebean.setAmount((Float)obj[9]);
					casebean.setIsParseable((Integer)obj[10]);
					casebean.setFlag((Integer)obj[11]);
					casebean.setIsdefault((Integer)obj[12]);
					//casebean.setExpectedField39(obj[13].toString());
					casebean.setDescription(obj[21].toString());
					returnList.add(CaseService.BeanToModel(casebean));
				}
				return new BasePagingLoadResult<GWTCase>(returnList, config.getOffset(), returnList.size());
			}
		}catch (Exception ex) {
			log.error(ex, ex);
			throw new RuntimeException(ex);
		}
		
	}
	@Override
	public void DeleteBatchCascade(List<GWTBatchNo> selection) {
		// TODO Auto-generated method stub
		try{
			for(GWTBatchNo batch : selection){
				CaseImportBatch _batch = caseImportBatchDAL.Get(Op.EQ("id", batch.GetID()));
				List<Card> cardList = cardDAL.ListAll(Op.EQ(GWTCard.N_importBatchNo, batch.GetImportBatchNO()));
				for(Card card: cardList){ //删除卡信息
					cardDAL.Del(card);
				}
//				List<Case> cases = caseDAL.ListAll(Op.EQ(GWTCase.N_importBatchNo, _batch.getBatchNo()));
//				for(Case case1 : cases){ //删除案例
//					caseDAL.Del(case1);
//				}
//				IDAL<CaseFlow> caseflowIdal = DALFactory.GetBeanDAL(CaseFlow.class);
//				List<CaseFlow> caseFlows = caseflowIdal.ListAll(Op.EQ(GWTCase.N_importBatchNo, _batch.getBatchNo()));
//				for(CaseFlow caseFlow : caseFlows){
//					caseflowIdal.Del(caseFlow);    //删除业务流
//				}
//				IDAL<ExecuteSet> queueIdal = DALFactory.GetBeanDAL(ExecuteSet.class);
//				ExecuteSet qList = queueIdal.Get(Op.EQ(GWTCase.N_importBatchNo, _batch.getBatchNo()));
//				if(qList!=null)
//					queueIdal.Del(qList);       //删除任务队列
//				caseImportBatchDAL.Del(_batch);    //最后删除批次
			}
			
		}catch (Exception ex) {
			log.error(ex, ex);
			throw new RuntimeException(ex);
		}
		
	}
	@Override
	public List<GWTCard> GetCardList(String importBatchNo) {
		// TODO Auto-generated method stub
		try{
			IDAL<Card> cardIdal = DALFactory.GetBeanDAL(Card.class);
			List<Card> lst = cardIdal.ListAll(Op.EQ(GWTCard.N_importBatchNo, importBatchNo));
			List<GWTCard> list = new ArrayList<GWTCard>();
			for(Card card : lst){
				list.add(BeanToModel(card));
			}		
			return list;
		}catch (Exception ex) {
			// TODO: handle exception
			log.error(ex, ex);
			throw new RuntimeException(ex);
		}
	}

	@Override
	public List<GWTTransaction> GetTranInfoList(String sysId) {
		try {
			IDAL<Transaction> tranIdal = DALFactory.GetBeanDAL(Transaction.class);
			List<Transaction> lst = tranIdal.ListAll(GWTTransaction.N_TranName, true, 
					Op.EQ(GWTTransaction.N_SystemID, sysId)); //0 -> 1
			List<GWTTransaction> list = new ArrayList<GWTTransaction>();
			for(Transaction tran : lst){
				list.add(TransactionService.BeanToModel(tran));
			}
			return list;
		} catch (Exception ex) {
			// TODO: handle exception
			log.error(ex, ex);
			throw new RuntimeException(ex);
		}
	}

	@Override
	public PagingLoadResult<GWTCard> GetCardList(String batchNo,
			String searchKey, PagingLoadConfig config) {
		try{
			Op[] conditions = new Op[] {
//				Op.EQ(GWTCase.N_importBatchNo, batchNo)
			};
			int count;
			List<Card> lst;
			if(searchKey.isEmpty()){
				count = cardDAL.Count(conditions);
				PageStartEnd pse = new PageStartEnd(config, count);
				lst = cardDAL.List(pse.getStart(), pse.getEnd(), conditions);
			}else{
				String[] properties = {
						GWTCard.N_cardNo,
						GWTCard.N_cmbHost,
						GWTCard.N_cardPwd,
						GWTCard.N_cardStatus,
						GWTCard.N_cardType,
						GWTCard.N_subBankNo,
						GWTCard.N_subsidiaryNo,
						GWTCard.N_vaildUntil};
				count = cardDAL.MatchCount(searchKey, properties, conditions);
				PageStartEnd pse = new PageStartEnd(config, count);
				lst = cardDAL.Match(searchKey, properties, pse.getStart(), pse.getEnd(), conditions);
			}
			List<GWTCard> returnList = new ArrayList<GWTCard>();
			for(Card card1: lst){
				returnList.add(BeanToModel(card1));
			}
			return new BasePagingLoadResult<GWTCard>(returnList, config.getOffset(), count);
		}catch (Exception ex) {
			log.error(ex, ex);
			throw new RuntimeException(ex);
		}
	}

	@Override
	public void DeleteCard(List<GWTCard> selection) {
		// TODO Auto-generated method stub
		try{
			for(GWTCard gwtCard : selection){
				Card card = cardDAL.Get(Op.EQ("id", Integer.parseInt(gwtCard.GetCardID())));
				cardDAL.Del(card);
			}
			
		}catch (Exception ex) {
			log.error(ex, ex);
			throw new RuntimeException(ex);
		}
	}

	@Override
	public boolean SaveCard(GWTSimuSystem sysInfo, GWTCard gwtCard) {
		// TODO Auto-generated method stub
		try {
			Card cardBean = cardDAL.Get(new HelperService().GetDistinctOpArray(gwtCard, gwtCard.getCardNo()));
			if(cardBean != null && cardBean.getId().toString().compareTo(gwtCard.GetCardID()) != 0){
				return false;
			}
			if(gwtCard.IsNew()){
				cardBean = ModelToBean(null, gwtCard);
				cardDAL.Add(cardBean);
			}
			else{
				if(cardBean == null || (cardBean != null && 
						cardBean.getId()!= Integer.parseInt(gwtCard.GetCardID())))
					cardBean = cardDAL.Get(Op.EQ(GWTCard.N_ID, Integer.parseInt(gwtCard.GetCardID())));
				cardBean = ModelToBean(cardBean, gwtCard);
				cardDAL.Edit(cardBean);
//				List<Case> caseList = caseDAL.ListAll(Op.EQ(GWTCase.N_importBatchNo, cardBean.getImportBatchNo()),
//						Op.EQ("cardId", cardBean.getId()));
//				for(Case caseBean : caseList){
//					caseBean.setRequestXml(XmlPackUtil.PackXmlContent(caseBean, cardBean));
//					caseDAL.Edit(caseBean);
//				}			
			}
			new HelperService().SendToBack(sysInfo);
			return true;
		} catch (Exception ex) {
			// TODO: handle exception
			log.error(ex, ex);
			throw new RuntimeException(ex);
		}
	}

	@Override
	public PagingLoadResult<GWTCaseFlow> GetCaseFlowList(String batchNo,
			String searchKey, PagingLoadConfig config) {
		try{
			Op[] conditions = new Op[] {
//				Op.EQ(GWTCaseFlow.N_ImportBatchNo, batchNo)
			};
			int count;
			List<CaseFlow> lst;
			if(searchKey.isEmpty()){
				count = caseFlowDAL.Count(conditions);
				PageStartEnd pse = new PageStartEnd(config, count);
				lst = caseFlowDAL.List(pse.getStart(), pse.getEnd(), conditions);
			}else{
				String[] properties = {						
						GWTCaseFlow.N_Name,
						GWTCaseFlow.N_CaseFlowNo,
						GWTCaseFlow.N_CreateTime};
				count = caseFlowDAL.MatchCount(searchKey, properties, conditions);
				PageStartEnd pse = new PageStartEnd(config, count);
				lst = caseFlowDAL.Match(searchKey, properties, pse.getStart(), pse.getEnd(), conditions);
			}
			List<GWTCaseFlow> returnList = new ArrayList<GWTCaseFlow>();
			for(CaseFlow caseFlow: lst){
				returnList.add(BeanToModel(caseFlow));
			}
			return new BasePagingLoadResult<GWTCaseFlow>(returnList, config.getOffset(), count);
		}catch (Exception ex) {
			log.error(ex, ex);
			throw new RuntimeException(ex);
		}
	}


	@Override
	public void DeleteCaseFlow(List<GWTCaseFlow> selection,boolean isCasCade) {
		// TODO Auto-generated method stub
		try{
			for(GWTCaseFlow gwtCaseFlow : selection){
				CaseFlow caseFlow = caseFlowDAL.Get(Op.EQ("id", Integer.parseInt(gwtCaseFlow.GetID())));
				if(isCasCade){
					List<Case> caselist = caseDAL.ListAll(Op.EQ("caseFlow", caseFlow));
					for(Case caseBean : caselist){			
						caseDAL.Del(caseBean);
					}
				}
				caseFlowDAL.Del(caseFlow);
			}
			
		}catch (Exception ex) {
			log.error(ex, ex);
			throw new RuntimeException(ex);
		}
	}

	@Override
	public PagingLoadResult<GWTCase> GetFlowCases(
			GWTCaseFlow caseFlow, PagingLoadConfig config) {
		// TODO Auto-generated method stub
		try{
			Op[] conditions = new Op[] {
				Op.EQ("caseFlow.id", Integer.parseInt(caseFlow.GetID()))
			};
			int count;
			List<Case> list = new ArrayList<Case>();
			count = caseDAL.Count(conditions);
			PageStartEnd pse = new PageStartEnd(config, count);
			list = caseDAL.List(GWTCase.N_Sequence, true, pse.getStart(), pse.getEnd(), conditions);
			List<GWTCase> returnList = new ArrayList<GWTCase>();
			for(Case case1: list){
				returnList.add(CaseService.BeanToModel(case1));
			}
			return new BasePagingLoadResult<GWTCase>(returnList, config.getOffset(), count);
		}catch (Exception ex) {
			log.error(ex, ex);
			throw new RuntimeException(ex);
		}
		
	}
	
	

}
