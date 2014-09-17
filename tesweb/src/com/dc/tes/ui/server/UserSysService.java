package com.dc.tes.ui.server;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.dc.tes.data.DALFactory;
import com.dc.tes.data.IDAL;
import com.dc.tes.data.model.UserRSystem;
import com.dc.tes.data.op.Op;
import com.dc.tes.ui.client.IUserSysService;
import com.dc.tes.ui.client.model.GWTUser;
import com.dc.tes.ui.client.model.GWTUserSys;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class UserSysService extends RemoteServiceServlet implements IUserSysService {

	private static final Log log = LogFactory.getLog(UserSysService.class);
	private static final long serialVersionUID = 8822909767680884253L;
	/**
	 * 用户-模拟系统关系管理 数据访问接口
	 */
	IDAL<UserRSystem> userSysDao = DALFactory.GetBeanDAL(UserRSystem.class);

	@Override
	public List<GWTUserSys> GetUserList(String sysID, String searchInfo) {
		try {
			List<UserRSystem> getList = userSysDao.ListAll(Op.EQ(GWTUserSys.N_SysID, sysID));
			List<GWTUserSys> returnList = new ArrayList<GWTUserSys>();

			for (UserRSystem u : getList)
				returnList.add(BeanToModel(u));
			return returnList;
		} catch (Exception ex) {
			log.error(ex, ex);
			throw new RuntimeException(ex);
		}
	}

	@Override
	public void SaveRelation(String systemID, List<GWTUser> saveList) {
		try {
			List<UserRSystem> getList = userSysDao.ListAll(Op.EQ(GWTUserSys.N_SysID, systemID));
			if(getList != null)
				for (UserRSystem us : getList)
					userSysDao.Del(us);
			//userSysDao.sqlExec("delete from t_userrsystem where SystemID = " + systemID);
			for (GWTUser user : saveList) {
				UserRSystem bean = new UserRSystem();
				bean.setSystemid(systemID);
				bean.setUserid(user.getUserID());
				userSysDao.Add(bean);
			}
		} catch (Exception ex) {
			log.error(ex, ex);
			throw new RuntimeException(ex);
		}
	}

	@Override
	public void AddRelation(String sysID, String userID) {
		try {
			UserRSystem bean = new UserRSystem();

			bean.setSystemid(sysID);
			bean.setUserid(userID);
			userSysDao.Add(bean);

		} catch (Exception ex) {
			log.error(ex, ex);
			throw new RuntimeException(ex);
		}
	}

	@Override
	public PagingLoadResult<GWTUserSys> GetUserList(String sysID, String searchInfo, PagingLoadConfig config) {
		return null;
	}

	@Override
	public void RemoveRelation(List<GWTUserSys> delList) {

	}

	public List<UserRSystem> GetRelateByUserID(String userID) {
		try {
			return userSysDao.ListAll(Op.EQ(GWTUserSys.N_UserID, userID));

		} catch (Exception ex) {
			log.error(ex, ex);
			throw new RuntimeException(ex);
		}
	}

	public GWTUserSys BeanToModel(UserRSystem bean) {
		if (bean == null)
			return null;
		return new GWTUserSys(bean.getId(), bean.getUserid(), bean.getSystemid());
	}

	public UserRSystem ModelToBean(GWTUserSys model) {
		UserRSystem bean = new UserRSystem();

		bean.setSystemid(model.GetSysID());
		bean.setUserid(model.GetUserID());
		if (!model.IsNew())
			bean.setId(model.GetID());
		return bean;
	}

}
