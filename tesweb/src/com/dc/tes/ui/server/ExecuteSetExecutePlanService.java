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
import com.dc.tes.data.model.ExecuteSetDirectory;
import com.dc.tes.data.model.ExecuteSetExecutePlan;
import com.dc.tes.data.model.User;
import com.dc.tes.data.op.Op;
import com.dc.tes.ui.client.IExecuteSetExecutePlanService;
import com.dc.tes.ui.client.model.GWTExecutePlan;
import com.dc.tes.ui.client.model.GWTExecuteSetDirectory;
import com.dc.tes.ui.client.model.GWTExecuteSetExecutePlan;
import com.dc.tes.ui.client.model.GWTUser;
import com.dc.tes.ui.client.model.PageStartEnd;
import com.extjs.gxt.ui.client.data.BasePagingLoadResult;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class ExecuteSetExecutePlanService extends RemoteServiceServlet implements
		IExecuteSetExecutePlanService {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3946560389835593188L;
	IDAL<ExecuteSetExecutePlan> esExecutePlanDAL = DALFactory.GetBeanDAL(ExecuteSetExecutePlan.class);
	private static final Log log = LogFactory.getLog(ExecuteSetExecutePlanService.class);
	
	private GWTExecuteSetExecutePlan BeanToModel(ExecuteSetExecutePlan bean){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String beginRunTime = bean.getBeginRunTime() == null ? "" : sdf.format(bean.getBeginRunTime());
		String endRunTime = bean.getEndRunTime() == null ? "" : sdf.format(bean.getEndRunTime());
		String addTime = sdf.format(bean.getAddTime());
		GWTExecuteSetExecutePlan gwtExecuteSetExecutePlan = new GWTExecuteSetExecutePlan(bean.getId().toString(),
				bean.getExecuteSetDirId(), bean.getExecutePlanId().toString(), 
				bean.getSystemId(), bean.getAddUserId(), String.valueOf(bean.getScheduledRunStatus()),
				beginRunTime, endRunTime, addTime);
		String execSetName = DALFactory.GetBeanDAL(ExecuteSetDirectory.class).Get(
				Op.EQ(GWTExecuteSetDirectory.N_ID, Integer.parseInt(bean.getExecuteSetDirId()))).getName();
		String execPlanName = DALFactory.GetBeanDAL(ExecutePlan.class).Get(Op.EQ(GWTExecutePlan.N_ID, bean.getExecutePlanId())).getName();
		String userName = DALFactory.GetBeanDAL(User.class).Get(Op.EQ(GWTUser.N_id, bean.getAddUserId())).getName();
		gwtExecuteSetExecutePlan.SetExecPlanName(execPlanName);
		gwtExecuteSetExecutePlan.SetExecuteSetName(execSetName);
		gwtExecuteSetExecutePlan.SetUserName(userName);
		return gwtExecuteSetExecutePlan;
	}
	
	private ExecuteSetExecutePlan ModelToBean(ExecuteSetExecutePlan bean, GWTExecuteSetExecutePlan model){
		ExecuteSetExecutePlan plan;
		if(bean != null){
			plan = bean;
		}else{
			plan = new ExecuteSetExecutePlan();
			if(!model.isNew()){
				plan.setId(Integer.parseInt(model.GetID()));
			}
		}	
		plan.setAddUserId(model.GetAddUserID());
		plan.setExecuteSetDirId(model.GetExecuteSetID());
		plan.setExecutePlanId(Integer.parseInt(model.GetExecutePlanID()));
		plan.setSystemId(model.GetSystemID());	
		plan.setScheduledRunStatus(Integer.parseInt(model.GetScheduledRunStatus()));
		
		return plan;
	}
	
	@Override
	public PagingLoadResult<GWTExecuteSetExecutePlan> GetExecuteSetExecutePlanList(
			String execPlanID, String systemID, String searchKey, PagingLoadConfig config) {
		// TODO Auto-generated method stub
		try{
			int count;
			List<ExecuteSetExecutePlan> lst;
			List<Op> ops = new ArrayList<Op>();
			ops.add(Op.EQ(GWTExecuteSetExecutePlan.N_SystemID, systemID));			
			if(!execPlanID.isEmpty()){
				ops.add(Op.EQ(GWTExecuteSetExecutePlan.N_ExecutePlanID, Integer.parseInt(execPlanID)));
			}
			Op[] condition = new Op[ops.size()];
			for(int i=0; i<ops.size(); i++){    //把集合类转成数组
				condition[i] = ops.get(i);
			}
			if(searchKey.isEmpty()){
				count = esExecutePlanDAL.Count(condition);
				PageStartEnd pse = new PageStartEnd(config, count);
				lst = esExecutePlanDAL.List(GWTExecuteSetExecutePlan.N_ID, true, pse.getStart(), pse.getEnd(), condition);
			}else{
				String[] searchField = new String[]{			
				};
				count = esExecutePlanDAL.MatchCount(searchKey, searchField, condition);
				PageStartEnd pse = new PageStartEnd(config, count);
				lst = esExecutePlanDAL.Match(searchKey, searchField, pse.getStart(), pse.getEnd(), condition);
			}
			List<GWTExecuteSetExecutePlan> result = new ArrayList<GWTExecuteSetExecutePlan>();
			for(ExecuteSetExecutePlan data : lst){
				result.add(BeanToModel(data));
			}
			return  new BasePagingLoadResult<GWTExecuteSetExecutePlan>(result, config.getOffset(), count);
		}catch (Exception e) {
			log.error(e, e);
			throw new RuntimeException(e);
		}
	}

	@Override
	public Boolean SaveOrUpdateExecuteSetExecutePlan(
			GWTExecuteSetExecutePlan gwtExecuteSetExecutePlan) {
		// TODO Auto-generated method stub
		ExecuteSetExecutePlan old = null;
		if(!gwtExecuteSetExecutePlan.isNew()){//如果不为新增
			old = esExecutePlanDAL.Get(Op.EQ(GWTExecuteSetExecutePlan.N_ID, Integer.parseInt(gwtExecuteSetExecutePlan.GetID())));				
			if(gwtExecuteSetExecutePlan.GetExecutePlanID().equals("-1")){
				//计划任务ID为-1，表示删除原有计划任务
				esExecutePlanDAL.Del(old);
				return true;
			}
		}
		ExecuteSetExecutePlan data = ModelToBean(old, gwtExecuteSetExecutePlan);
		if(data.getAddUserId().equals("Administrator")){
			data.setAddUserId("0");
		}
		if(gwtExecuteSetExecutePlan.isNew()){
			data.setAddTime(new Date());
			esExecutePlanDAL.Add(data);
		}else{
			esExecutePlanDAL.Edit(data);
		}
		return true;
	}

	@Override
	public Boolean DeleteExecuteSetExecutePlan(
			List<GWTExecuteSetExecutePlan> gwtExecuteSetExecutePlans) {
		// TODO Auto-generated method stub
		try{
			for(GWTExecuteSetExecutePlan plan : gwtExecuteSetExecutePlans){
				esExecutePlanDAL.Del(ModelToBean(null, plan));
			}
		}catch (Exception e) {
			log.error(e, e);
			throw new RuntimeException(e);
		}
		return true;
	}

	@Override
	public GWTExecuteSetExecutePlan GetExecSetExecPlan(String execSetId) {
		// TODO Auto-generated method stub
		ExecuteSetExecutePlan data = esExecutePlanDAL.Get(Op.EQ(GWTExecuteSetExecutePlan.N_ExecuteSetID, execSetId));
		if(data == null){
			return null;
		}else{
			return BeanToModel(data);
		}
	}

}
