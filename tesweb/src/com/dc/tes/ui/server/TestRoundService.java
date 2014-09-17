package com.dc.tes.ui.server;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.dc.tes.data.DALFactory;
import com.dc.tes.data.IDAL;
import com.dc.tes.data.model.ExecuteLog;
import com.dc.tes.data.model.TestRound;
import com.dc.tes.data.op.Op;
import com.dc.tes.ui.client.ITestRoundService;
import com.dc.tes.ui.client.model.GWTTestRound;
import com.dc.tes.ui.client.model.PageStartEnd;
import com.extjs.gxt.ui.client.data.BasePagingLoadResult;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class TestRoundService extends RemoteServiceServlet implements
		ITestRoundService {
	/**
	 * 
	 */
	private static final long serialVersionUID = -9205402343195322785L;
	private static final Log log = LogFactory.getLog(TestRoundService.class);
	IDAL<TestRound> roundDAL = DALFactory.GetBeanDAL(TestRound.class);
	
	private GWTTestRound BeanToModel(TestRound data) {
		// TODO Auto-generated method stub
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");
		return new GWTTestRound(data.getRoundId(), data.getSystemId(), data.getRoundNo(), data.getRoundName(), data.getDescription(),
				sdf.format(data.getStartDate()), sdf.format(data.getEndDate()), data.getCurrentRoundFlag());
	}
	
	private TestRound ModelToBean(TestRound testRound, GWTTestRound gwt){
		
		TestRound bean = null;
		if(testRound != null){
			bean = testRound;
		}else{
			bean = new TestRound();
			if(!gwt.isNew()){
				bean.setRoundId(gwt.GetRoundID());
			}
		}
		bean.setRoundNo(gwt.GetRoundNo());
		bean.setRoundName(gwt.GetRoundName());
		bean.setDescription(gwt.GetDesc());
		bean.setSystemId(gwt.GetSystemID());
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");
			bean.setStartDate(sdf.parse(gwt.GetStartDate()));
			bean.setEndDate(sdf.parse(gwt.GetEndDate()));			
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		bean.setCurrentRoundFlag(gwt.GetCurrentRoundFlag());
		return bean;
		
	}
	
	@Override
	public PagingLoadResult<GWTTestRound> GetTestRoundList(String systemID,
			String searchKey, PagingLoadConfig config) {
		// TODO Auto-generated method stub
		try{
			int count;
			List<TestRound> lst;
			Op condition[] = {Op.EQ(GWTTestRound.N_SystemId, systemID)};
			if(searchKey.isEmpty()){
				count = roundDAL.Count(condition);
				PageStartEnd pse = new PageStartEnd(config, count);
				lst = roundDAL.List(GWTTestRound.N_RoundNo, true, pse.getStart(), pse.getEnd(), condition);
			}else{
				String[] searchField = new String[]{
						GWTTestRound.N_RoundName,
						GWTTestRound.N_Desc
				};
				count = roundDAL.MatchCount(searchKey, searchField, condition);
				PageStartEnd pse = new PageStartEnd(config, count);
				lst = roundDAL.Match(searchKey, searchField, pse.getStart(), pse.getEnd(), condition);
			}
			List<GWTTestRound> result = new ArrayList<GWTTestRound>();
			for(TestRound data : lst){
				result.add(BeanToModel(data));
			}
			return  new BasePagingLoadResult<GWTTestRound>(result, config.getOffset(), count);
		}catch (Exception e) {
			log.error(e, e);
			throw new RuntimeException(e);
		}
				
	}

	

	@Override
	public Boolean SaveOrUpdateTestRound(GWTTestRound gwtTestRound) {
		// TODO Auto-generated method stub
		TestRound data = null;
		data = roundDAL.Get(new HelperService().GetDistinctOpArray(gwtTestRound, gwtTestRound.GetRoundName()));
		if(data!=null && (gwtTestRound.isNew()||(data.getRoundId().intValue() != gwtTestRound.GetRoundID().intValue()))){
			return false;
		}
		data = ModelToBean(data, gwtTestRound);
		if(data.getCurrentRoundFlag()==1){
			try{
				TestRound t = roundDAL.Get(Op.EQ(GWTTestRound.N_SystemId, data.getSystemId()), 
						Op.EQ(GWTTestRound.N_CurrentRoundFlag, 1));
				if(t != null){
					t.setCurrentRoundFlag(0);
					roundDAL.Edit(t);
				}
			}catch (Exception e) {
				log.error(e, e);
				throw new RuntimeException(e);
			}
		}
		if(gwtTestRound.isNew()){
			roundDAL.Add(data);
		}else{
			roundDAL.Edit(data);
		}
		return true;
	}

	@Override
	public Boolean DeleteTestRound(List<GWTTestRound> gwtTestRounds) {
		// TODO Auto-generated method stub
		try{
			for(GWTTestRound round : gwtTestRounds){
				TestRound tr = ModelToBean(null, round);
				IDAL<ExecuteLog> elogDAL = DALFactory.GetBeanDAL(ExecuteLog.class);
				List<ExecuteLog> list = elogDAL.ListAll(Op.EQ("roundId", tr.getRoundId()));
				for(ExecuteLog log : list){
					elogDAL.Del(log);
				}
				roundDAL.Del(tr);
			}
		}catch (Exception e) {
			log.error(e, e);
			throw new RuntimeException(e);
		}
		return true;
	}

	@Override
	public List<GWTTestRound> GetTestRounds(String systemID) {
		// TODO Auto-generated method stub
		List<TestRound> list = roundDAL.ListAll(GWTTestRound.N_RoundNo, true,
				Op.EQ(GWTTestRound.N_SystemId, systemID));
		List<GWTTestRound> result = new ArrayList<GWTTestRound>();
		for(TestRound tr : list){
			result.add(BeanToModel(tr));
		}
		return result;
	}

}
