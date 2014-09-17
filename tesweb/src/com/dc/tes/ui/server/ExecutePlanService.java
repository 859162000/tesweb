package com.dc.tes.ui.server;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.dc.tes.data.DALFactory;
import com.dc.tes.data.IDAL;
import com.dc.tes.data.model.ExecutePlan;
import com.dc.tes.data.op.Op;
import com.dc.tes.ui.client.IExecutePlanService;
import com.dc.tes.ui.client.model.GWTExecutePlan;
import com.dc.tes.ui.client.model.PageStartEnd;
import com.extjs.gxt.ui.client.data.BasePagingLoadResult;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class ExecutePlanService extends RemoteServiceServlet implements
		IExecutePlanService {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2312889895604562431L;
	IDAL<ExecutePlan> executePlanDAL = DALFactory.GetBeanDAL(ExecutePlan.class);
	private static final Log log = LogFactory.getLog(ExecutePlanService.class);
	@Override
	public PagingLoadResult<GWTExecutePlan> GetExecutePlanList(String systemID,
			String searchKey, PagingLoadConfig config) {
		// TODO Auto-generated method stub
		
		try{
			int count;
			List<ExecutePlan> lst;
			Op condition[] = {Op.EQ(GWTExecutePlan.N_SystemID, systemID)};
			if(searchKey.isEmpty()){
				count = executePlanDAL.Count(condition);
				PageStartEnd pse = new PageStartEnd(config, count);
				lst = executePlanDAL.List(GWTExecutePlan.N_ID, true, pse.getStart(), pse.getEnd(), condition);
			}else{
				String[] searchField = new String[]{
						GWTExecutePlan.N_Name,
						GWTExecutePlan.N_Desc
				};
				count = executePlanDAL.MatchCount(searchKey, searchField, condition);
				PageStartEnd pse = new PageStartEnd(config, count);
				lst = executePlanDAL.Match(searchKey, searchField, pse.getStart(), pse.getEnd(), condition);
			}
			List<GWTExecutePlan> result = new ArrayList<GWTExecutePlan>();
			for(ExecutePlan data : lst){
				result.add(BeanToModel(data));
			}
			return  new BasePagingLoadResult<GWTExecutePlan>(result, config.getOffset(), count);
		}catch (Exception e) {
			log.error(e, e);
			throw new RuntimeException(e);
		}
	}

	private GWTExecutePlan BeanToModel(ExecutePlan data) {
		// TODO Auto-generated method stub
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		return new GWTExecutePlan(data.getId().toString(), data.getName(), 
				data.getDescription(), data.getSystemId(), data.getCreatedUserId(),
				String.valueOf(data.getScheduleRunMode()), data.getScheduleRunWeekDay(), 
				data.getScheduleRunHour(), sdf.format(data.getCreatedTime()), String.valueOf(data.getStatus()));
	}
	
	private ExecutePlan ModelToBean(ExecutePlan executePlan, GWTExecutePlan gwt) {
		// TODO Auto-generated method stub
		ExecutePlan bean = null;
		if(executePlan != null){
			bean = executePlan;
		}else{
			bean = new ExecutePlan();
			if(!gwt.isNew()){
				bean.setId(Integer.parseInt(gwt.GetID()));
			}
		}
		if(gwt.GetCreateUserId().equals("Administrator")){
			bean.setCreatedUserId("0");
		}else{
			bean.setCreatedUserId(gwt.GetCreateUserId());
		}
		
		bean.setDescription(gwt.GetDesc());
		bean.setName(gwt.GetName());
		bean.setScheduleRunHour(gwt.GetScheduleRunHour());
		bean.setScheduleRunMode(Integer.parseInt(gwt.GetScheduleRunMode()));
		bean.setScheduleRunWeekDay(gwt.GetScheduleRunWeekday());
		bean.setSystemId(gwt.GetSystemID());
		bean.setStatus(gwt.GetStatus());
		return bean;
	}

	@Override
	public Boolean SaveOrUpdateExecutePlan(GWTExecutePlan gwtExecutePlan) {
		// TODO Auto-generated method stub
		ExecutePlan old = null;
		old = executePlanDAL.Get(new HelperService().GetDistinctOpArray(gwtExecutePlan, gwtExecutePlan.GetName()));
		if(old!=null && (gwtExecutePlan.isNew()||(old.getId().intValue() != Integer.parseInt(gwtExecutePlan.GetID())))){
			return false;
		}
		if(!gwtExecutePlan.isNew())
			old = executePlanDAL.Get(Op.EQ(GWTExecutePlan.N_ID, Integer.parseInt(gwtExecutePlan.GetID())));
		ExecutePlan data = ModelToBean(old, gwtExecutePlan);
		if(gwtExecutePlan.isNew()){
			data.setCreatedTime(new Date());
			executePlanDAL.Add(data);
		}else{
			executePlanDAL.Edit(data);
		}
		return true;
	}

	

	@Override
	public Boolean DeleteExecutePlan(List<GWTExecutePlan> gwtExecutePlans) {
		// TODO Auto-generated method stub
		try{
			for(GWTExecutePlan plan : gwtExecutePlans){
				executePlanDAL.Del(ModelToBean(null, plan));
			}
		}catch (Exception e) {
			log.error(e, e);
			throw new RuntimeException(e);
		}
		return true;
	}

	@Override
	public List<GWTExecutePlan> GetExecPlans(String systemID) {
		// TODO Auto-generated method stub
		List<ExecutePlan> lst = executePlanDAL.ListAll(GWTExecutePlan.N_ID, true, Op.EQ(GWTExecutePlan.N_SystemID, systemID));
		List<GWTExecutePlan> result = new ArrayList<GWTExecutePlan>();
		for(ExecutePlan executePlan : lst){
			result.add(BeanToModel(executePlan));
		}	
		return result;
	}

}
