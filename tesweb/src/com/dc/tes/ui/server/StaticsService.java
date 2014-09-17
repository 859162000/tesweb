package com.dc.tes.ui.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.dc.tes.data.DALFactory;
import com.dc.tes.data.IDAL;
import com.dc.tes.data.model.MonitorLog;
import com.dc.tes.ui.client.IStatics;
import com.dc.tes.ui.client.model.GWTStatSys;
import com.dc.tes.ui.client.model.GWTStatTran;
import com.dc.tes.ui.client.model.GWTStatTrend;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class StaticsService extends RemoteServiceServlet implements IStatics {
	private static final long serialVersionUID = 7046660132435642800L;
	private static final Log log = LogFactory.getLog(StaticsService.class);
	IDAL<MonitorLog> logDao = DALFactory.GetBeanDAL(MonitorLog.class);
	
	@SuppressWarnings("unchecked")
	@Override
	public List<GWTStatSys> GetSysStatistic(int begin, int end){
		List<GWTStatSys> returnList = new ArrayList<GWTStatSys>();
		//临时保存各个系统存放的位置
		Map<String,Integer> nameDict = new HashMap<String,Integer>();
		
		//获得现有系统列表（统计交易数量）
		try
		{
			List<Object> tranCountList = (List<Object>)logDao.sqlQuery(
					"select s.SYSTEMNAME,count(1),sum(ISCLIENTSIMU) " +
					"from t_systype s" +
					" left join  t_transaction t on s.SYSTEMID = t.SYSTEMID " +
					"group by s.SYSTEMNAME");
			for(Object obj : tranCountList)
			{
				Object[] objArray = (Object[])obj;
				String sysName = objArray[0].toString();
				GWTStatSys statSys = new GWTStatSys(sysName);
				int sum = 0;
				if(objArray[2] != null)
					sum = Integer.parseInt(objArray[2].toString());
				statSys.descTranInfo(Integer.parseInt(objArray[1].toString()),sum );
				
				nameDict.put(sysName, returnList.size());
				returnList.add(statSys);
			}
			
			//获得日志文件中各系统运行情况
			List<Object> RunCountList = (List<Object>)logDao.sqlQuery(
					" select SYSNAME,YEARM,count(1) ,sum(type) from t_monitorlog " +
					" where YEARM >= " + "'" + begin + "'" + " and YEARM <= " + "'" + end + "'" +
					" group by  YEARM,SYSNAME");
			for(Object obj : RunCountList)
			{
				Object[] objArray = (Object[])obj;
				String sysName = objArray[0].toString();
				int yearM = Integer.parseInt(objArray[1].toString());
				
				GWTStatSys statSys = new GWTStatSys(sysName);
				if(nameDict.containsKey(sysName))
					statSys = returnList.get(nameDict.get(sysName));
				else
				{
					statSys = new GWTStatSys(sysName);
					
					nameDict.put(sysName, returnList.size());
					returnList.add(statSys);
				}
				int setInex = getIndex(begin,end,yearM);
				statSys.addStatistic(setInex, Integer.parseInt(objArray[2].toString()), Integer.parseInt(objArray[3].toString()));
			}
		}
		catch (Exception e) {
			log.error(e);
			e.printStackTrace();
		}
		
		for(GWTStatSys sys : returnList)
			sys.descStatistic();
		return returnList;
	}
	
	private int getIndex(int begin,int end,int yearM)
	{
		return 12 - Math.min(end - yearM, yearM - begin) - 1;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<GWTStatTran> GetTranStatistic(String sysName, int begin, int end) {
		List<GWTStatTran> tranList = new ArrayList<GWTStatTran>();
		
		//获得现有系统列表（统计交易数量）
		try
		{
			List<Object> tranCountList = (List<Object>)logDao.sqlQuery(
					" select TRANCODE,TRANNAME,TYPE,count(1) as Cnt ,sum(HASSCRIPT) as dyCnt from t_monitorlog " +
					" where SYSNAME = '" + sysName + "'" +
//					" and YEARM >= " + begin + " and YEARM <= " + end +
					" group by  TRANCODE,TRANNAME,TYPE " +
					" order by Cnt DESC ",0,9);
			for(Object obj : tranCountList)
			{
				Object[] objArray = (Object[])obj;
				tranList.add(new GWTStatTran(objArray[0].toString(),objArray[1].toString(),
						Integer.valueOf(objArray[2].toString()),Integer.valueOf(objArray[3].toString()),
						Integer.valueOf(objArray[4].toString())));
			}
		}
		catch (Exception e) {
		}
		
		return tranList;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<GWTStatTrend> GetTrendStatistic(String sysName,
			String tranCode, String tranName, int type, int begin, int end) {
		List<GWTStatTrend> trendList = GWTStatTrend.getTrendList(begin,end);
		
		try
		{
			List<Object> tranCountList = (List<Object>)logDao.sqlQuery(
					" select YEARM,count(1) as Cnt from t_monitorlog " +
					" where SYSNAME = '" + sysName + "'" +
					" and TRANCODE = '" + tranCode +
					"' and TRANNAME = '" + tranName +
					"' and TYPE = " + type +
					" and YEARM >= '" + begin + "' and YEARM <= '" + end + "'" +
					" group by  YEARM ");
			for(Object obj : tranCountList)
			{
				Object[] objArray = (Object[])obj;
				int yearM = Integer.valueOf(objArray[0].toString());
				int count = Integer.valueOf(objArray[1].toString());
				
				trendList.get(getIndex(begin,end,yearM)).SetValue(count);
			}
		}
		catch (Exception e) {
		}
		
		return trendList;
	}

	
}
