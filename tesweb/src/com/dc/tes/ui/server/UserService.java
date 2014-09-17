package com.dc.tes.ui.server;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.dc.tes.data.DALFactory;
import com.dc.tes.data.IDAL;
import com.dc.tes.data.model.User;
import com.dc.tes.data.model.UserRSystem;
import com.dc.tes.data.op.Op;
import com.dc.tes.ui.client.IUserService;
import com.dc.tes.ui.client.enums.IDUType;
import com.dc.tes.ui.client.enums.OpType;
import com.dc.tes.ui.client.model.GWTUser;
import com.dc.tes.ui.client.model.GWTUserSys;
import com.dc.tes.ui.client.model.PageStartEnd;
import com.dc.tes.util.MD5;
import com.extjs.gxt.ui.client.data.BasePagingLoadResult;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * 用户管理服务类
 * @author scckobe
 *
 */
public class UserService extends RemoteServiceServlet implements IUserService {
	private static final Log log = LogFactory.getLog(UserService.class);

	/**
	 * 序列化版本号
	 */
	private static final long serialVersionUID = 5950426841663541660L;

	/**
	 * 用户管理 数据访问接口
	 */
	IDAL<User> userDao = DALFactory.GetBeanDAL(User.class);

	@Override
	public void DeleteUser(List<GWTUser> userList, Integer loginLogId) {
		try {
			for (int i = 0; i < userList.size(); i++) {
				OperationLogService.writeOperationLog(OpType.User, IDUType.Delete, 
						Integer.parseInt(userList.get(i).getUserID()), userList.get(i).getUserName(),
						"userName", userList.get(i).getUserName(), null, loginLogId);
				userDao.Del(ModelToBean(userList.get(i)));
			}
		} catch (Exception ex) {
			log.error(ex, ex);
			throw new RuntimeException(ex);
		}
	}

	@Override
	public GWTUser GetUserInfo(String userID) {
		try {
			return BeanToModel(GetUserInfoBean(userID));
		} catch (Exception ex) {
			log.error(ex, ex);
			throw new RuntimeException(ex);
		}
	}
	
	private User GetUserInfoBean(String userID) throws Exception 
	{
		return userDao.Get(Op.EQ(GWTUser.N_id, userID));
	}
	
	@Override
	public List<GWTUser> GetUserByRole(int role) {
		try {
			List<User> lst = userDao.ListAll(Op.IN(GWTUser.N_isAdmin, new Integer[]{role, 2}));

			List<GWTUser> returnList = new ArrayList<GWTUser>();

			for (User u : lst)
				returnList.add(BeanToModel(u));
			return returnList;
		} catch (Exception ex) {
			log.error(ex, ex);
			throw new RuntimeException(ex);
		}
	}

	@Override
	public GWTUser GetUserInfo(String userName, String password) {
		try {
			String pwd = MD5.encode(password);
			User userInfo = userDao.Get(Op.EQ(GWTUser.N_name, userName), Op.EQ(GWTUser.N_password, pwd));
			GWTUser gwtUser = null;
			//如果能获得用户信息，则直接返回
			if(userInfo !=  null)
				gwtUser =  BeanToModel(userInfo);
			//否则从配置文件中查看是否为超级用户
			else
			{
				if(userName.compareTo("Administrator") == 0)
				{
					//TODO:总出现空格，先这样做处理，以后找问题
					String txtPwd = new HelperService().GetAdministratorPWD().trim();
					//txtPwd = txtPwd.substring(1);
					
					if(password.compareTo(txtPwd) == 0)
					{
						//构造超级用户实体对象
						gwtUser = new GWTUser(userName,"超级用户","超级用户", "", -1,1);
					}
				}
			}		
			return gwtUser;
		} catch (Exception ex) {
			log.error(ex, ex);
			throw new RuntimeException(ex);
		}
	}

	@Override
	public PagingLoadResult<GWTUser> GetUserList(String searchInfo, PagingLoadConfig config) {
		try {
			int count;
			List<User> lst;
			if (searchInfo.isEmpty()) {
				count = userDao.Count();
				PageStartEnd pse = new PageStartEnd(config, count);
				lst = userDao.List(pse.getStart(), pse.getEnd());
			} else {
				String[] properties = {GWTUser.N_name};
				count = userDao.MatchCount(searchInfo, properties);
				PageStartEnd pse = new PageStartEnd(config, count);
				lst = userDao.Match(searchInfo, properties, pse.getStart(), pse.getEnd());
			}

			List<GWTUser> returnList = new ArrayList<GWTUser>();

			for (User u : lst)
				returnList.add(BeanToModel(u));

			return new BasePagingLoadResult<GWTUser>(returnList, config.getOffset(), count);
		} catch (Exception ex) {
			log.error(ex, ex);
			throw new RuntimeException(ex);
		}
	}

	@Override
	public void UpdatePWD(String userID, String pwd, int roleType, Integer loginLogId) {
		try {
			//保存文件信息
			if (roleType == -1) {
				pwd = MD5.encode(pwd);
				new HelperService().SaveAdministratorPWD(pwd);
			} else {
				User userInfo = GetUserInfoBean(userID);
				String oldPwd = userInfo.getPassword();
				userInfo.setPassword(MD5.encode(pwd));
				String opUserId = OperationLogService.getLoginLogById(loginLogId).getUserId();
				userInfo.setLastModifiedTime(new Date());
				userInfo.setLastModifiedUserId(opUserId);
				OperationLogService.writeOperationLog(OpType.User, IDUType.Update, 
						Integer.parseInt(userInfo.getId()), userInfo.getName(),
						"password", oldPwd, userInfo.getPassword(), loginLogId);
				userDao.Edit(userInfo);
			}
		} catch (Exception ex) {
			log.error(ex, ex);
			throw new RuntimeException(ex);
		}
	}
	
	@Override
	public Boolean SaveUser(GWTUser userInfo, Integer loginLogId) {
		try {
			User oldUserBean = userDao.Get(new HelperService().GetDistinctOpArray(userInfo, userInfo.getUserName()));
			if (oldUserBean != null && oldUserBean.getId().compareTo(userInfo.getUserID()) != 0) {
				return false;
			}
			User userBean = ModelToBean(userInfo);	
			String opUserId = OperationLogService.getLoginLogById(loginLogId).getUserId();
			if (userInfo.IsNew()){
				userBean.setPassword(MD5.encode(userBean.getPassword()));
				userBean.setCreatedTime(new Date());
				userBean.setCreatedUserId(opUserId);
				userDao.Add(userBean);
				OperationLogService.writeOperationLog(OpType.User, IDUType.Insert, 
						Integer.parseInt(userInfo.getUserID()), userInfo.getUserName(),
						"userName", null, userInfo.getUserName(), loginLogId);
			}else{
				oldUserBean = userDao.Get(Op.EQ("id", userBean.getId()));
				if(userBean.getPassword()==null){
					userBean.setPassword(oldUserBean.getPassword());
				}else{
					userBean.setPassword(MD5.encode(userBean.getPassword()));
				}
				userBean.setLastModifiedTime(new Date());
				userBean.setLastModifiedUserId(opUserId);
				OperationLogService.writeUpdateOperationLog(OpType.User, User.class, 
						Integer.parseInt(oldUserBean.getId()), oldUserBean.getName(),
						oldUserBean, userBean, loginLogId);
				userDao.Edit(userBean);
			}
			return true;
		} catch (Exception ex) {
			log.error(ex, ex);
			throw new RuntimeException(ex);
		}
	}

	public GWTUser BeanToModel(User userInfo) {
		if (userInfo == null)
			return null;	
		return new GWTUser(userInfo.getId(), userInfo.getName(), userInfo.getDescription(), "", userInfo.getIsAdmin(), userInfo.getFlag());
	}

	public static User ModelToBean(GWTUser gwtUserInfo) {
		if (gwtUserInfo == null)
			return null;
		User userInfo = new User();
		userInfo.setFlag(gwtUserInfo.getFlag());
		userInfo.setIsAdmin(gwtUserInfo.getIsAdmin());
		userInfo.setName(gwtUserInfo.getUserName());
		userInfo.setDescription(gwtUserInfo.getDescription());
		userInfo.setPassword(gwtUserInfo.getPassword());
		if (!gwtUserInfo.IsNew())
			userInfo.setId(gwtUserInfo.getUserID());
		return userInfo;
	}

	@Override
	public List<GWTUser> GetUserBySystem(String sysId) {
		// TODO Auto-generated method stub
		IDAL<UserRSystem> userRSysDAL = DALFactory.GetBeanDAL(UserRSystem.class);
		List<UserRSystem> userRSystems = userRSysDAL.ListAll(Op.EQ(GWTUserSys.N_SysID, sysId));
		List<User> users = new ArrayList<User>();
		users.addAll(userDao.ListAll(Op.IN("isAdmin",new Integer[]{0,2})));
		for(UserRSystem urs : userRSystems){
			users.add(userDao.Get(Op.EQ(GWTUser.N_id, urs.getUserid())));
		}
		List<GWTUser> gwtUsers = new ArrayList<GWTUser>();
		for(User user : users){
			gwtUsers.add(BeanToModel(user));
		}
		return gwtUsers;
	}
	
	public String GetUserNameByUserId(String userId) {
		IDAL<User> userDAL = DALFactory.GetBeanDAL(User.class);
		User user = userDAL.Get(Op.EQ(GWTUser.N_id, userId));
		if (user != null) {
			return user.getName();
		}
		return null;
	}
	
	
	public static void main(String[] args) {
		String pwd = MD5.encode("dc1234");
		System.out.println(pwd);
	}
}
