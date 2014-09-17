/**
 * 
 */
package com.dc.tes.ui.server;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.dc.tes.data.DALFactory;
import com.dc.tes.data.IDAL;
import com.dc.tes.data.model.Transaction;
import com.dc.tes.data.model.TransactionCatetory;
import com.dc.tes.data.op.Op;
import com.dc.tes.dom.MsgDocument;
import com.dc.tes.ui.client.IClientTransaction;
import com.dc.tes.ui.client.enums.IDUType;
import com.dc.tes.ui.client.enums.OpType;
import com.dc.tes.ui.client.model.GWTPack_Struct;
import com.dc.tes.ui.client.model.GWTSimuSystem;
import com.dc.tes.ui.client.model.GWTTransaction;
import com.dc.tes.ui.client.model.PageStartEnd;
import com.dc.tes.ui.util.ISystemConfig;
import com.dc.tes.ui.util.SystemConfigManager;
import com.dc.tes.ui.util.TranStructTreeUtil;
import com.extjs.gxt.ui.client.data.BasePagingLoadResult;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * @author shenfx
 * 
 */

public class TransactionService extends RemoteServiceServlet implements IClientTransaction {
	
	private static final Log log = LogFactory.getLog(TransactionService.class);
	private static final long serialVersionUID = 2037041114560323866L;

	private IDAL<Transaction> tranDAL = DALFactory.GetBeanDAL(Transaction.class);

	public static GWTTransaction BeanToModel(Transaction tran) {
		if (tran == null)
			return null;
		GWTTransaction gwtTran = new GWTTransaction(tran.getTransactionId(), tran.getSystemId(), tran.getIsClientSimu(),
				tran.getTranCode(), tran.getTranName(),
				tran.getDescription(), tran.getFlag(), 
				tran.getScript(), tran.getCategory(),tran.getChannel());
		gwtTran.SetPackConfig(tran.getRequestStruct(),tran.getResponseStruct());
		gwtTran.SetSqlDelayTime(String.valueOf(tran.getSqlDelayTime()));
		if(tran.getTransactionCategoryId()!=null){
			IDAL<TransactionCatetory> tcDAL = DALFactory.GetBeanDAL(TransactionCatetory.class);
			TransactionCatetory transactionCatetory = tcDAL.Get(Op.EQ("id", tran.getTransactionCategoryId()));
			if(transactionCatetory!=null){
				gwtTran.setTranCategoryID(transactionCatetory.getId());
				gwtTran.setTranCateName(transactionCatetory.getCategoryName());
			}
		}
		return gwtTran;
	}

	private static Transaction ModelToBean(Transaction tranBean, GWTTransaction gwtTran) {
		if (gwtTran == null)
			return null;
		Transaction tran = tranBean;
		if (tran == null) {
			tran = new Transaction();
			tran.setRequestStruct("");
			tran.setResponseStruct("");
		}
		
		tran.setCategory(gwtTran.GetCategory());
		tran.setDescription(gwtTran.getDesc());
		tran.setFlag(gwtTran.GetFlag());
		tran.setIsClientSimu(gwtTran.GetMode());
		tran.setScript(gwtTran.getScript());
		tran.setSystemId(gwtTran.getSystemID());
		tran.setTranCode(gwtTran.getTranCode());
		tran.setTranName(gwtTran.getTranName());
		tran.setChannel(gwtTran.GetChanel());
		tran.setTransactionCategoryId(gwtTran.getTranCategoryID()==null?"":gwtTran.getTranCategoryID());
		tran.setSqlDelayTime(Integer.parseInt(
				gwtTran.GetSqlDelayTime().isEmpty()?"0":gwtTran.GetSqlDelayTime()));
		if (!gwtTran.getTranID().isEmpty())
			tran.setTransactionId(gwtTran.getTranID());
		return tran;
	}

	@Override
	public Boolean SaveTran(GWTSimuSystem sysInfo,GWTTransaction tran, Integer loginLogId) {
		try {
			String userId = OperationLogService.getLoginLogById(loginLogId).getUserId();
			Transaction tranBean = tranDAL.Get(new HelperService().GetDistinctOpArray(tran, tran.getTranName()));
			if (tranBean != null && tranBean.getTransactionId().compareTo(tran.getTranID()) != 0)
				return false;
			
			if (tran.IsNew())
			{
				tranBean = ModelToBean(null, tran);
				tranBean.setCreatedTime(new Date());
				tranBean.setCreatedUserId(userId);
				tranDAL.Add(tranBean);
				OperationLogService.writeOperationLog(OpType.Transaction, IDUType.Insert, 
						Integer.parseInt(tranBean.getTransactionId()), tranBean.getTranName(),
						"tranName", null, tranBean.getTranName(), loginLogId);				
			}
			else
			{				
				if(tranBean == null || (tranBean != null && tranBean.getTransactionId() != tran.getTranID()))
					tranBean = GetSingle(tran.getTranID());
				tranBean = ModelToBean(tranBean, tran);
				tranBean.setLastModifiedTime(new Date());
				tranBean.setLastModifiedUserId(userId);
				Transaction oldTran = GetSingle(tran.getTranID());
				OperationLogService.writeUpdateOperationLog(OpType.Transaction, Transaction.class, 
						Integer.parseInt(oldTran.getTransactionId()), oldTran.getTranName(),
						oldTran, tranBean, loginLogId);
				tranDAL.Edit(tranBean);
			}
			new HelperService().SendToBack(sysInfo);

			return true;
		} catch (Exception ex) {
			log.error(ex, ex);
			throw new RuntimeException(ex);
		}
	}
	
	@Override
	public void DeleteTran(GWTSimuSystem sysInfo,List<GWTTransaction> tranList, Integer loginLogId) {
		try {
			for(GWTTransaction tran : tranList){
				tranDAL.Del(ModelToBean(null, tran));
				OperationLogService.writeOperationLog(OpType.Transaction, IDUType.Delete, 
						Integer.parseInt(tran.getTranID()), tran.getTranName(),
						"tranName", tran.getTranName(), null, loginLogId);
				new QueueService().DeleteTranTask(tran.getTranID());
			}
			
			new HelperService().SendToBack(sysInfo);
		} catch (Exception ex) {
			log.error(ex, ex);
			throw new RuntimeException(ex);
		}
	}
	
	@Override
	public PagingLoadResult<GWTTransaction> GetList(String sysId, int isClient, String searchKey, PagingLoadConfig config) {
		try {
			Op[] conditions = new Op[] {
					Op.EQ(GWTTransaction.N_SystemID, sysId),
					Op.EQ(GWTTransaction.N_IsClientSimu, isClient) };

			int count;
			List<Transaction> lst;
			if (searchKey.isEmpty()) {
				count = tranDAL.Count(conditions);
				PageStartEnd pse = new PageStartEnd(config, count);
				lst = tranDAL.List(GWTTransaction.N_TranCode, true, pse.getStart(), pse.getEnd(), conditions);
			} else {
				String[] properties = {
						GWTTransaction.N_TranCode,
						GWTTransaction.N_TranName,
						GWTTransaction.N_Desc };
				count = tranDAL.MatchCount(searchKey, properties, conditions);
				PageStartEnd pse = new PageStartEnd(config, count);
				lst = tranDAL.Match(searchKey, properties, pse.getStart(), pse.getEnd(), conditions);
			}

			List<GWTTransaction> returnList = new ArrayList<GWTTransaction>();
			for (Transaction tran : lst) {
				returnList.add(BeanToModel(tran));
			}

			return new BasePagingLoadResult<GWTTransaction>(returnList, config.getOffset(), count);
		} catch (Exception ex) {
			log.error(ex, ex);
			throw new RuntimeException(ex);
		}
	}
	
	@Override
	public String GetScript(String tranID) {
		Transaction tran = GetSingle(tranID);
		if(tran != null)
			return tran.getScript();
		return "";
	}

	@Override
	public void UpdateScript(String tranID, String Script) {
		try
		{
			Transaction tran = GetSingle(tranID);
			if(tran != null)
			{
				tran.setScript(Script);
				tranDAL.Edit(tran);
			}
			else
			{
				throw new Exception("交易已不存在");
			}
		}catch (Exception ex) {
			log.error(ex, ex);
			throw new RuntimeException(ex);
		}
	}

	@Override
	public GWTPack_Struct GetTreeRoot(String tranId, boolean isRes) {
		try {
			Transaction tran = tranDAL.Get(Op.EQ(GWTTransaction.N_TransID, tranId));

			ISystemConfig config = SystemConfigManager.getConfigBySysID(tran.getSystemId(), tran.getIsClientSimu());

			return TranStructTreeUtil.GetGWTTreeRoot(tran,isRes, config);

		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	@Override
	public void SaveTreeRoot(String tranId, boolean isRes, GWTPack_Struct root, Integer loginLogId) {
		try {
			Transaction tran = tranDAL.Get(Op.EQ(GWTTransaction.N_TransID, tranId));

			if (root.getChildCount() == 0) {
				if (isRes)
					tran.setResponseStruct("");
				else
					tran.setRequestStruct("");
				tranDAL.Edit(tran);

				return;
			}

			ISystemConfig config = SystemConfigManager.getConfigBySysID(tran.getSystemId(),tran.getIsClientSimu());
			MsgDocument doc = TranStructTreeUtil.GetMsgDocument(root, isRes, config, 1);
			if (isRes){
				tran.setResponseStruct(doc.toString());
				OperationLogService.writeOperationLog(OpType.ExpectedMessage, IDUType.Update,
						Integer.parseInt(tran.getTransactionId()), tran.getTranName(),
						"responseStruct", tran.getTranName(), "修改请求报文", loginLogId);
			}else{
				tran.setRequestStruct(doc.toString());
				OperationLogService.writeOperationLog(OpType.RequestMessage, IDUType.Update,
						Integer.parseInt(tran.getTransactionId()), tran.getTranName(),
						"requestStruct", tran.getTranName(), "修改请求报文", loginLogId);
			}	
			tranDAL.Edit(tran);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * 添加交易信息
	 * @param tran	交易信息
	 * @return		交易信息
	 * @throws Exception
	 */
	public Transaction AddNewTran(Transaction tran, Integer loginLogId) throws Exception {
		String userId = OperationLogService.getLoginLogById(loginLogId).getUserId();
		tran.setCreatedTime(new Date());
		tran.setCreatedUserId(userId);
		tranDAL.Add(tran);
		OperationLogService.writeOperationLog(OpType.Transaction, IDUType.Import, 
				Integer.parseInt(tran.getTransactionId()), tran.getTranName(),
				"tranName", null, tran.getTranName(), loginLogId);
		return tran;
	}
	
	/**
	 * 修改交易信息
	 * @param tran	交易信息
	 * @throws Exception
	 */
	public void EditTran(Transaction tran, Integer loginLogId) throws Exception {
		String userId = OperationLogService.getLoginLogById(loginLogId).getUserId();
		tran.setLastModifiedTime(new Date());
		tran.setLastModifiedUserId(userId);
		OperationLogService.writeOperationLog(OpType.Transaction, IDUType.Import,
				Integer.parseInt(tran.getTransactionId()), tran.getTranName(),
				"tranName", tran.getTranName(), tran.getTranName(), loginLogId);
		tranDAL.Edit(tran);
	}

	/**
	 * 根据系统标识和客户端标识，获得对应的所有交易信息
	 * @param systemID	系统标识
	 * @param isClientSimu	客户端标识 	0：接收端  1：发起端
	 * @return
	 */
	public List<Transaction> GetTransDAO(String systemID,int isClientSimu){
		List<Transaction> ls = new ArrayList<Transaction>();
		try {
			ls = tranDAL.ListAll(Op.EQ(GWTTransaction.N_SystemID, systemID),
					Op.EQ(GWTTransaction.N_IsClientSimu, isClientSimu));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ls;
	}

	/**
	 * 获得交易基本信息
	 * @param tranId	交易标识
	 * @return			交易基本信息
	 */
	public Transaction GetSingle(String tranId)
	{
		try {
			return tranDAL.Get(Op.EQ(GWTTransaction.N_TransID, tranId));
		} catch (Exception ex) {
			log.error(ex, ex);
			throw new RuntimeException(ex);
		}
	}
	
	/**
	 * 获得交易基本信息
	 * @param tranId	交易标识
	 * @return			交易基本信息
	 */
	public Transaction GetSingleByName(String tranName, int isClientSimu, String sysId)
	{
		try {
			return tranDAL.Get(Op.EQ(GWTTransaction.N_TranName, tranName), 
					Op.EQ(GWTTransaction.N_IsClientSimu, isClientSimu), Op.EQ(GWTTransaction.N_SystemID, sysId));
		} catch (Exception ex) {
			log.error(ex, ex);
			throw new RuntimeException(ex);
		}
	}
}
