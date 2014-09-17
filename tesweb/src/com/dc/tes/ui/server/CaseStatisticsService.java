package com.dc.tes.ui.server;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.dc.tes.data.DALFactory;
import com.dc.tes.data.IDAL;
import com.dc.tes.data.model.CaseRunStatistics;
import com.dc.tes.data.model.CaseRunUserStats;
import com.dc.tes.data.model.User;
import com.dc.tes.data.op.Op;
import com.dc.tes.ui.client.ICaseStatisticsService;
import com.dc.tes.ui.client.model.GWTCaseRunStatistics;
import com.dc.tes.ui.client.model.GWTCaseRunUserStats;
import com.dc.tes.ui.client.model.PageStartEnd;
import com.extjs.gxt.ui.client.data.BasePagingLoadResult;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class CaseStatisticsService extends RemoteServiceServlet implements ICaseStatisticsService {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4418163727989690694L;
	private static final Log log = LogFactory.getLog(CaseStatisticsService.class);
	private static IDAL<CaseRunStatistics> caseRunStatisticsDAL = DALFactory.GetBeanDAL(CaseRunStatistics.class);
	private static IDAL<CaseRunUserStats> userStatsDAL = DALFactory.GetBeanDAL(CaseRunUserStats.class);
	
	private GWTCaseRunStatistics BeanToModel(CaseRunStatistics caseRunStatistics, boolean isFactorChange){
		if(!isFactorChange)
			return new GWTCaseRunStatistics(caseRunStatistics.getCaseRunStatisticsId().toString(), 
					caseRunStatistics.getStatMonth(), caseRunStatistics.getStatStartDay(), 
					caseRunStatistics.getStatEndDay(), caseRunStatistics.getTotalRunCaseFlowCount(),
					caseRunStatistics.getTotalRunCaseCount(), caseRunStatistics.getTotalRunUserCount(), 
					caseRunStatistics.getTotalPassedCaseFlowCount(),
					caseRunStatistics.getCaseFlowPassRate(), caseRunStatistics.getStatIpAddress(),
					caseRunStatistics.getStatHostName(), caseRunStatistics.getStatUserId(), 
					caseRunStatistics.getStatTime(), caseRunStatistics.getFirstRunTime(), 
					caseRunStatistics.getLastRunTime(), caseRunStatistics.getMemo());
		else
			return new GWTCaseRunStatistics(caseRunStatistics.getCaseRunStatisticsId().toString(), 
					caseRunStatistics.getStatMonth(), caseRunStatistics.getStatStartDay(), caseRunStatistics.getStatEndDay(),
					caseRunStatistics.getCreatedTransactionCount(), caseRunStatistics.getCreatedCaseFlowCount(),
					caseRunStatistics.getCreatedCaseCount(), caseRunStatistics.getCreatedSysParamCount(), 
					caseRunStatistics.getModifiedTransactionCount(), caseRunStatistics.getModifiedCaseFlowCount(), 
					caseRunStatistics.getModifiedCaseCount(), caseRunStatistics.getModifiedSysParamCount(),
					caseRunStatistics.getStatUserId(), caseRunStatistics.getStatTime(), caseRunStatistics.getMemo());
	}
	
	private GWTCaseRunUserStats BeanToModel(CaseRunUserStats crus, boolean isFactorChange){
		IDAL<User> userDAL = DALFactory.GetBeanDAL(User.class);
		User user = userDAL.Get(Op.EQ("id", crus.getRunUserId().toString()));
		if(!isFactorChange){
			return new GWTCaseRunUserStats(crus.getCaseRunStatistics().getCaseRunStatisticsId(), 
					user.getName(), crus.getTotalRunCaseFlowCount(), crus.getTotalRunCaseCount(),
					crus.getTotalPassedCaseFlowCount(), crus.getCaseFlowPassRate(),
					crus.getFirstRunTime(), crus.getLastRunTime(), crus.getMemo());
		}else{
			return new GWTCaseRunUserStats(crus.getCaseRunStatistics().getCaseRunStatisticsId(),
					user.getName(), crus.getCreatedTransactionCount(), crus.getCreatedCaseFlowCount(), 
					crus.getCreatedCaseCount(), crus.getCreatedSysParamCount(), crus.getModifiedTransactionCount(),
					crus.getModifiedCaseFlowCount(), crus.getModifiedCaseCount(), crus.getModifiedSysParamCount(),
					crus.getMemo());
		}
		
	}
	
	@Override
	public PagingLoadResult<GWTCaseRunStatistics> getCaseRunStatisticsList(
			String systemId, String searchKey, PagingLoadConfig config, boolean isFactorChange) {
		// TODO Auto-generated method stub
		try {
			int count;
			List<CaseRunStatistics> lst;
			Op[] conditions = new Op[] { Op.EQ(GWTCaseRunStatistics.N_SystemId, Integer.parseInt(systemId)) };
			if (searchKey.isEmpty()) {
				count = caseRunStatisticsDAL.Count(conditions);
				PageStartEnd pse = new PageStartEnd(config, count);
				lst = caseRunStatisticsDAL.List(pse.getStart(), pse.getEnd(), conditions);
			} else {
				String[] properties = { GWTCaseRunStatistics.N_StatStartDay };
				count = caseRunStatisticsDAL.MatchCount(searchKey, properties, conditions);
				PageStartEnd pse = new PageStartEnd(config, count);
				lst = caseRunStatisticsDAL.Match(searchKey, properties, pse.getStart(), pse
						.getEnd(), conditions);
			}

			List<GWTCaseRunStatistics> returnList = new ArrayList<GWTCaseRunStatistics>();
			for (CaseRunStatistics c : lst)
				returnList.add(BeanToModel(c, isFactorChange));
				
			return new BasePagingLoadResult<GWTCaseRunStatistics>(returnList, config
					.getOffset(), count);
		} catch (Exception ex) {
			log.error(ex, ex);
			throw new RuntimeException(ex);
		}
	}

	@Override
	public PagingLoadResult<GWTCaseRunUserStats> getCaseRunUserStatList(
			GWTCaseRunStatistics statistics, String searchKey,
			PagingLoadConfig config, boolean isFactorChange) {
		// TODO Auto-generated method stub
		try {
			int count;
			List<CaseRunUserStats> lst;
			Op[] conditions = new Op[] { Op.EQ("caseRunStatistics.caseRunStatisticsId", statistics.getCaseRunStatisticsID()) };
			if (searchKey.isEmpty()) {
				count = userStatsDAL.Count(conditions);
				PageStartEnd pse = new PageStartEnd(config, count);
				lst = userStatsDAL.List(pse.getStart(), pse.getEnd(), conditions);
			} else {
				String[] properties = {};
				count = userStatsDAL.MatchCount(searchKey, properties, conditions);
				PageStartEnd pse = new PageStartEnd(config, count);
				lst = userStatsDAL.Match(searchKey, properties, pse.getStart(), pse
						.getEnd(), conditions);
			}

			List<GWTCaseRunUserStats> returnList = new ArrayList<GWTCaseRunUserStats>();
			for (CaseRunUserStats c : lst)
				returnList.add(BeanToModel(c, isFactorChange));
				
			return new BasePagingLoadResult<GWTCaseRunUserStats>(returnList, config
					.getOffset(), count);
		} catch (Exception ex) {
			log.error(ex, ex);
			throw new RuntimeException(ex);
		}
	}

	@Override
	public void deleteCaseRunStatistics(List<GWTCaseRunStatistics> statisticsList) {
		// TODO Auto-generated method stub
		try{
			for(GWTCaseRunStatistics statistics : statisticsList){
				CaseRunStatistics caseRunStatistics = new CaseRunStatistics();
				caseRunStatistics.setCaseRunStatisticsId(statistics.getCaseRunStatisticsID());
				caseRunStatisticsDAL.Del(caseRunStatistics);
			}
		}catch (Exception ex) {
			log.error(ex, ex);
			throw new RuntimeException(ex);
		}
		
	}

}
