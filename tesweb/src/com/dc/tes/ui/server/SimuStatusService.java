package com.dc.tes.ui.server;

import java.math.BigInteger;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.dc.tes.data.DALFactory;
import com.dc.tes.data.IDAL;
import com.dc.tes.data.model.RealtimeLog;
import com.dc.tes.data.model.SysType;
import com.dc.tes.data.model.Transaction;
import com.dc.tes.data.op.Op;
import com.dc.tes.ui.client.ISimuStatus;
import com.dc.tes.ui.client.model.GWTRealTimeLogInfo;
import com.dc.tes.ui.client.model.GWTStatusSys;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class SimuStatusService extends RemoteServiceServlet implements ISimuStatus {

	private static final long serialVersionUID = 2454016388460334096L;

	private static final Log log = LogFactory.getLog(SimuStatusService.class);
	
	private IDAL<RealtimeLog> dal = DALFactory.GetBeanDAL(RealtimeLog.class);
	
	@SuppressWarnings({ "unchecked", "deprecation" })
	@Override
	public List<GWTRealTimeLogInfo> GetRecentCaseList(String sysName) {
		
		List<GWTRealTimeLogInfo> dataList = null;
		
		try {
//			String query = String.format("select casename, datetime, trancode, type from t_realtimelog where sysname = '%s' order by datetime desc limit 0,10", sysName);
//			List<Object> objList = (List<Object>)dal.sqlQuery(query);
			
			String query = String.format("select casename, datetime, trancode, type from realtimelog where sysname = '%s' order by datetime desc", sysName);
			List<Object> objList = (List<Object>)dal.sqlQuery(query, 0, 9);
			//System.out.println("Case  ========   " + String.valueOf(objList.size()));
			
			dataList = new ArrayList<GWTRealTimeLogInfo>();
			for(int i = 0; i < objList.size(); i++){
				Object[] values = (Object[])(objList.get(i));
				GWTRealTimeLogInfo dataitem = new GWTRealTimeLogInfo();
				dataitem.set(GWTRealTimeLogInfo.N_CASENAME, values[0].toString());
				dataitem.set(GWTRealTimeLogInfo.N_DATETIME, ((Date)values[1]).toLocaleString());
				dataitem.set(GWTRealTimeLogInfo.N_TRANCODE, values[2].toString());
				dataitem.set(GWTRealTimeLogInfo.N_TYPE, (Integer)values[3] == 0 ? "发起" : "接收");
				dataList.add(dataitem);
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getStackTrace());
		}
		
		return dataList;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<GWTRealTimeLogInfo> GetUsefulTranList(String sysName) {
		
		List<GWTRealTimeLogInfo> dataList = null;
		double totleCount = 0;
		try {
			//获取交易总条数，用于计算百分比
			String query = String.format("select count(1) from realtimelog where sysname='%s'", sysName);
			List<Object> objList = (List<Object>)dal.sqlQuery(query);
			//System.out.println("Tran  ========   " + String.valueOf(objList.size()));
			if(objList.get(0) instanceof Integer)
				totleCount = ((Integer)objList.get(0)).doubleValue();
			else
				totleCount = ((BigInteger)objList.get(0)).doubleValue();
			if(totleCount == 0)return new ArrayList<GWTRealTimeLogInfo>();
			
			//读取常用交易
			query = String.format("select trancode, tranname, type, count(1) as trancount from realtimelog where sysname='%s' group by trancode, tranname, type order by trancount desc", sysName);
			objList = (List<Object>)dal.sqlQuery(query, 0, 9);
			dataList = new ArrayList<GWTRealTimeLogInfo>();
			for(int i = 0; i < objList.size(); i++){
				Object[] values = (Object[])(objList.get(i));
				GWTRealTimeLogInfo dataitem = new GWTRealTimeLogInfo();
				dataitem.set(GWTRealTimeLogInfo.N_TRANCODE, values[0].toString());
				dataitem.set(GWTRealTimeLogInfo.N_TRANNAME, values[1].toString());
				dataitem.set(GWTRealTimeLogInfo.N_TYPE, (Integer)values[2] == 0 ? "发起" : "接收");
				double trancount = 0;
				if(values[3] instanceof Integer){
					trancount = ((Integer)values[3]).doubleValue();
				}else
					trancount = ((BigInteger)values[3]).doubleValue();
				
				String trancountStr = "";
				if(values[3] instanceof Integer){
					trancountStr = String.valueOf(((Integer)values[3]));
				}else
					trancountStr = String.valueOf(((BigInteger)values[3]).intValue());
				dataitem.set(GWTRealTimeLogInfo.N_TRANCOUNT, trancountStr);
				DecimalFormat format = new DecimalFormat("0.00");
				String precent = format.format(trancount / totleCount);
				if(precent.startsWith("1"))
					precent = "100%";
				else
					precent = precent.substring(2) + "%";
				dataitem.set(GWTRealTimeLogInfo.N_PRECENT, precent);
				dataList.add(dataitem);
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getStackTrace());
		}
		
		return dataList;
	}

	@SuppressWarnings("unchecked")
	public GWTStatusSys GetSystemStatusInfo(String sysId){
		
		IDAL<SysType> sysDAL = DALFactory.GetBeanDAL(SysType.class);
		IDAL<Transaction> tranDAL = DALFactory.GetBeanDAL(Transaction.class);
		
		GWTStatusSys item = new GWTStatusSys();
		
		try {
			//获取系统信息
			SysType system = sysDAL.Get(Op.EQ(Transaction.N_SystemID, sysId));
			
			String query = "select count(1) casename from cases c inner join transaction t on c.transactionid = t.transactionid where t.isMode = %s and t.systemid = %s";

			//服务端案例总数
			List<Object> objList = (List<Object>)dal.sqlQuery(String.format(query, 1, sysId));
			int serverCaseCount = 0;
			if(objList.get(0) instanceof Integer){
				serverCaseCount = ((Integer)objList.get(0));
			}else{
				serverCaseCount = ((BigInteger)objList.get(0)).intValue();
			}
			
			//客户端案例总数
			int clientCaseCount = 0;
			 objList = (List<Object>)dal.sqlQuery(String.format(query, 0, sysId));
			if(objList.get(0) instanceof Integer){
				clientCaseCount = ((Integer)objList.get(0));
			}else{
				clientCaseCount = ((BigInteger)objList.get(0)).intValue();
			}
			
			int serverTranCount = tranDAL.Count(Op.EQ(Transaction.N_SystemID, sysId), Op.EQ(Transaction.N_IsClientSimu, 0));
			int clientTranCount = tranDAL.Count(Op.EQ(Transaction.N_SystemID, sysId), Op.EQ(Transaction.N_IsClientSimu, 1));
			
			//转换为客户端bean.
			item.set(GWTStatusSys.N_SYSNAME, system.getSystemName());
			item.set(GWTStatusSys.N_IP, system.getIpadress());
			item.set(GWTStatusSys.N_PORT, String.valueOf(system.getPortnum()));
			item.set(GWTStatusSys.N_CHANNEL, system.getChannel());
			item.set(GWTStatusSys.N_SERVERCASECOUNT, serverCaseCount);
			item.set(GWTStatusSys.N_CLIENTCASECOUNT, clientCaseCount);
			item.set(GWTStatusSys.N_SERVERTRANCOUNT, serverTranCount);
			item.set(GWTStatusSys.N_CLIENTTRANCOUNT, clientTranCount);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return item;
	}

	@Override
	public void ClearMoniData(String sysName) {
		String query = String.format("delete from realtimelog where sysname = '%s'", sysName);
		try {
			dal.sqlExec(query);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
