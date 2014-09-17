package com.dc.tes.ui.server;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.dc.tes.data.DALFactory;
import com.dc.tes.data.IDAL;
import com.dc.tes.data.model.DbHost;
import com.dc.tes.data.op.Op;
import com.dc.tes.ui.client.IDbHostService;
import com.dc.tes.ui.client.enums.IDUType;
import com.dc.tes.ui.client.enums.OpType;
import com.dc.tes.ui.client.model.GWTHost;
import com.dc.tes.ui.client.model.PageStartEnd;
import com.extjs.gxt.ui.client.data.BasePagingLoadResult;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class DbHostService extends RemoteServiceServlet implements IDbHostService {
	private static final long serialVersionUID = 4661388826496708731L;
	
	private static final Log log = LogFactory.getLog(DbHostService.class);
	IDAL<DbHost> dataDao = DALFactory.GetBeanDAL(DbHost.class);
	
	@Override
	public PagingLoadResult<GWTHost> GetGWTSysDynamicParaPageList(
			String systemID, String searchInfo, PagingLoadConfig config) {
		// TODO Auto-generated method stub
		try {
			int count;
			List<DbHost> lst;
			Op op1 = Op.EQ(GWTHost.N_SystemId, systemID);
			if (searchInfo.isEmpty()) {
				count = dataDao.Count(op1);
				PageStartEnd pse = new PageStartEnd(config, count);
				lst = dataDao.List(pse.getStart(), pse.getEnd(),op1);
			} else {
				String[] searchField = new String[]{GWTHost.N_DbHost};
				count = dataDao.MatchCount(searchInfo,searchField,op1);
				PageStartEnd pse = new PageStartEnd(config, count);
				lst = dataDao.Match(searchInfo,searchField,pse.getStart(), pse.getEnd(),op1);
			}

			LinkedList<GWTHost> returnList = new LinkedList<GWTHost>();
			
			for (DbHost data : lst)
				returnList.addFirst(BeanToModel(data));

			return new BasePagingLoadResult<GWTHost>(returnList, config.getOffset(), count);
		} catch (Exception ex) {
			log.error(ex, ex);
			throw new RuntimeException(ex);
		}
	}

	private GWTHost BeanToModel(DbHost data) {
		// TODO Auto-generated method stub
		return new GWTHost(data.getHostid(),data.getDbHostName(),data.getIpaddress(),
				String.valueOf(data.getPortnum()),data.getSystemId(),data.getDescription(),
				data.getIsLongConn(), data.getDbType(), data.getDbName(), data.getDbUser(), 
				data.getDbPwd(), data.getOsType());
	}

	@Override
	public Boolean SaveHost(GWTHost host, Integer loginLogId) {
		// TODO Auto-generated method stub
		DbHost data = dataDao.Get(new HelperService().GetDistinctOpArray(host, host.getDbHost()));
		if (data != null && data.getHostid().compareTo(host.getID()) != 0) {
			return false;
		}
		
		data = ModelToBean(host);
		String userId = OperationLogService.getLoginLogById(loginLogId).getUserId();
		if(host.IsNew()){
			data.setCreatedTime(new Date());
			data.setCreatedUserId(userId);
			dataDao.Add(data);	
			OperationLogService.writeOperationLog(OpType.DbHost, IDUType.Insert, 
					Integer.parseInt(data.getHostid()), data.getDbHostName(),
					"dbHostName", null, data.getDbHostName(), loginLogId);
		}else{
			DbHost oldBean = dataDao.Get(Op.EQ("hostid", host.getID()));
			data.setLastModifiedTime(new Date());
			data.setLastModifiedUserId(userId);
			OperationLogService.writeUpdateOperationLog(OpType.DbHost, DbHost.class, 
					Integer.parseInt(oldBean.getHostid()), oldBean.getDbHostName(), oldBean, data, loginLogId);
			dataDao.Edit(data);
		}
		return true;
	}

	@Override
	public Boolean DeleteHost(List<GWTHost> hostList, Integer loginLogId) {
		// TODO Auto-generated method stub	
		
		for(GWTHost host : hostList) {
			dataDao.Del(ModelToBean(host));
			OperationLogService.writeOperationLog(OpType.DbHost, IDUType.Delete,
					Integer.parseInt(host.getID()), host.getDbHost(),
					"dbHostName", host.getDbHost(), null, loginLogId);
		}
		
		return true;
		
	}

	private DbHost ModelToBean(GWTHost host) {
		// TODO Auto-generated method stub
		DbHost bean = new DbHost();
		bean.setDbHostName(host.getDbHost());
		bean.setIpaddress(host.getIpAddress());
		bean.setPortnum(Integer.valueOf(host.getPortnum()));
		bean.setDescription(host.getDescription());
		bean.setSystemId(host.getSystemID());
		bean.setIsLongConn(host.getIsLongConn());
		bean.setDbType(host.getDbType());
		bean.setDbName(host.getDbName());
		bean.setDbUser(host.getDbUser());
		bean.setDbPwd(host.getDbPwd());
		bean.setOsType(host.getOsType());
		if(!host.IsNew())
			bean.setHostid(host.getID());
		
		return bean;
	}

}
