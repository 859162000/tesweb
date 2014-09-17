package com.dc.tes.ui.server;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.dc.tes.data.DALFactory;
import com.dc.tes.data.IDAL;
import com.dc.tes.data.model.LoginLog;
import com.dc.tes.data.model.User;
import com.dc.tes.data.op.Op;
import com.dc.tes.ui.client.ILoginLogService;
import com.dc.tes.ui.client.model.GWTLoginLog;
import com.dc.tes.ui.client.model.PageStartEnd;
import com.extjs.gxt.ui.client.data.BasePagingLoadResult;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class LoginLogService extends RemoteServiceServlet implements ILoginLogService {
	private static final long serialVersionUID = -87422973442883876L;
	
	IDAL<LoginLog> loginLogDAL = DALFactory.GetBeanDAL(LoginLog.class);
	IDAL<User> userDAL = DALFactory.GetBeanDAL(User.class);
	
	@Override
	public PagingLoadResult<GWTLoginLog> GetList(String sysId,
			String searchKey, PagingLoadConfig config) {
		// TODO Auto-generated method stub
		ArrayList<GWTLoginLog> returnList = new ArrayList<GWTLoginLog>();
		List<LoginLog> lst = null;
		
		try {		
			Op[] conditions = {Op.EQ(GWTLoginLog.N_SystemID, sysId)};
			int count = 0;
			if(searchKey.isEmpty()) {
				count = loginLogDAL.Count(conditions);
				PageStartEnd pse = new PageStartEnd(config, count);
				lst = loginLogDAL.List(pse.getStart(), pse.getEnd(),conditions);
			} else {
				String[] properties = {GWTLoginLog.N_ID,GWTLoginLog.N_UserName };
				count = loginLogDAL.MatchCount(searchKey, properties, conditions);
				PageStartEnd pse = new PageStartEnd(config, count);
				lst = loginLogDAL.Match(searchKey, properties,
						pse.getStart(), pse.getEnd(), conditions);
			}
			
			for(LoginLog log : lst) {
				returnList.add(BeanToModel(log));
			}
			return new BasePagingLoadResult<GWTLoginLog>(returnList,
					config.getOffset(), count);
		} catch(Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	private GWTLoginLog BeanToModel(LoginLog log) {
		// TODO Auto-generated method stub
		if(log == null)
			return null;
		User user = userDAL.Get(Op.EQ("id", log.getUserId()));
		String loginTime = log.getLoginTime()!=null? log.getLoginTime().toString():"";
		String logoutTime = log.getLogoutTime()!=null? log.getLogoutTime().toString():"";
		
		GWTLoginLog gwt = new GWTLoginLog(log.getId().toString(),log.getSystemId(),
				log.getUserId(),user.getName(),log.getIpAddress(),
				log.getMachineName(),log.getLoginCount().toString(),
				loginTime,logoutTime,log.getMemo());
		if(!loginTime.isEmpty()) {
			Date outTime = log.getLogoutTime();
			if(outTime == null)
				outTime = new Date();
			long iRunSeconds = (outTime.getTime() - 
				log.getLoginTime().getTime()) / 1000;            // 除以1000是为了转换成秒
		
			gwt.setDuration(FormatDuration2HHMMSS(iRunSeconds));
		}
		return gwt;
	}
	
	private String FormatDuration2HHMMSS(long iRunSeconds) {
		   
		  long hour = (iRunSeconds / 3600);
		  long min = (iRunSeconds / 60 - hour * 60);
		  long sec = (iRunSeconds - hour * 3600 - min * 60);
		 
		  String strDuration = "";
		  
		  if (hour > 0) {
			  strDuration = hour + "小时" + min + "分" + sec + "秒";
		  } else if (min > 0) {
			  strDuration = min + "分" + sec + "秒";
		  } else {
			  strDuration = sec + "秒";
		  }
		  
		  return strDuration;
     }

	@Override
	public void deleteLoginLog(List<GWTLoginLog> loginLogs) {
		// TODO Auto-generated method stub
		try{
			for(GWTLoginLog log : loginLogs){
				LoginLog loginLog = new LoginLog();
				loginLog.setId(Integer.parseInt(log.getID()));
				loginLog.setSystemId(log.get(GWTLoginLog.N_SystemID).toString());
				loginLog.setUserId(log.get(GWTLoginLog.N_UserID).toString());
				loginLogDAL.Del(loginLog);
			}
		}catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}


}
