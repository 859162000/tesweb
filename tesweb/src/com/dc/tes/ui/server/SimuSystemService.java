package com.dc.tes.ui.server;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.dc.tes.data.DALFactory;
import com.dc.tes.data.IDAL;
import com.dc.tes.data.model.Channel;
import com.dc.tes.data.model.MsgPacker;
import com.dc.tes.data.model.SysType;
import com.dc.tes.data.model.User;
import com.dc.tes.data.model.UserRSystem;
import com.dc.tes.data.op.Op;
import com.dc.tes.ui.client.ISimuSystemService;
import com.dc.tes.ui.client.enums.IDUType;
import com.dc.tes.ui.client.enums.OpType;
import com.dc.tes.ui.client.model.GWTComponent;
import com.dc.tes.ui.client.model.GWTMsgType;
import com.dc.tes.ui.client.model.GWTSimuSystem;
import com.dc.tes.ui.client.model.PageStartEnd;
import com.dc.tes.ui.util.SystemConfigManager;
import com.dc.tes.ui.util.SystemDeploy;
import com.extjs.gxt.ui.client.data.BasePagingLoadResult;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class SimuSystemService extends RemoteServiceServlet implements ISimuSystemService {

	private static final long serialVersionUID = -2012767766102092336L;
	private static final Log log = LogFactory.getLog(SimuSystemService.class);

	IDAL<SysType> systemDao = DALFactory.GetBeanDAL(SysType.class);

	@Override
	public boolean Save(GWTSimuSystem simuSystem,  Integer loginLogId) {
		try {
			SysType simuSystemBean = systemDao.Get(new HelperService().GetDistinctOpArray(simuSystem, simuSystem.GetSystemName()));
			String userId = OperationLogService.getLoginLogById(loginLogId).getUserId();
			if (simuSystemBean != null && simuSystemBean.getSystemId().compareTo(simuSystem.GetSystemID()) != 0) {
				return false;
			}
			
			if (simuSystem.IsNew())
			{
				simuSystemBean = ModelToBean(null, simuSystem);
				simuSystemBean.setIsParamModified(1);
				simuSystemBean.setCreatedTime(new Date());
				simuSystemBean.setCreatedUserId(userId);
				systemDao.Add(simuSystemBean);
				//写入操作日志
				OperationLogService.writeOperationLog(OpType.SysType, IDUType.Insert, 
						Integer.parseInt(simuSystemBean.getSystemId()), simuSystemBean.getSystemName(),
						"systemName", null, simuSystemBean.getSystemName(), loginLogId);
				SystemConfigManager.copyConfig(simuSystem.GetSystemName());
			}
			else
			{
				simuSystemBean = systemDao.Get(Op.EQ(GWTSimuSystem.N_SystemID, simuSystem.GetSystemID()));
				String oldName = simuSystemBean != null ? simuSystemBean.getSystemName() : "";
				SysType newSimuSystemBean = ModelToBean(simuSystemBean, simuSystem);
				OperationLogService.writeUpdateOperationLog(OpType.SysType, SysType.class, 
						Integer.parseInt(simuSystemBean.getSystemId()), simuSystemBean.getSystemName(),
						simuSystemBean, newSimuSystemBean, loginLogId);
				newSimuSystemBean.setIsParamModified(1);
				newSimuSystemBean.setLastModifiedTime(new Date());
				newSimuSystemBean.setLastModifiedUserId(userId);
				systemDao.Edit(newSimuSystemBean);
				SystemConfigManager.renameConfig(oldName,simuSystem.GetSystemName());
				final SysType sys = simuSystemBean;
				new Runnable() {				
					@Override
					public void run() {
						// TODO Auto-generated method stub
						List<Channel> channels = DALFactory.GetBeanDAL(Channel.class).ListAll
							(Op.EQ("systemId", sys.getSystemId()));
						new SystemDeploy().SaveBaseConfig(sys.getSystemId(), channels, sys.getChannel());
					}
				}.run();
			}
			return true;
		} catch (Exception ex) {
			log.error(ex, ex);
			throw new RuntimeException(ex);
		}
	}

	@Override
	public void Delete(List<GWTSimuSystem> simuSystemList, Integer loginLogId) {
		try {
			for (int i = 0; i < simuSystemList.size(); i++) {
				systemDao.Del(ModelToBean(null, simuSystemList.get(i)));
				OperationLogService.writeOperationLog(OpType.SysType, IDUType.Delete,
						Integer.parseInt(simuSystemList.get(i).GetSystemID()), simuSystemList.get(i).GetSystemName(),
						"systemName", simuSystemList.get(i).GetSystemName(), null, loginLogId);
				SystemConfigManager.delConfig(simuSystemList.get(i).GetSystemName());
			}
		} catch (Exception ex) {
			log.error(ex, ex);
			throw new RuntimeException(ex);
		}
	}

	@Override
	public GWTSimuSystem GetInfo(String systemID) {
		try {
			return BeanToModel(GetSimuSystemSignle(systemID));
		} catch (Exception ex) {
			log.error(ex, ex);
			throw new RuntimeException(ex);
		}
	}

	public SysType GetSimuSystemSignle(String systemID)
	{
		try {
			return systemDao.Get(Op.EQ(GWTSimuSystem.N_SystemID, systemID));
		} catch (Exception ex) {
			log.error(ex, ex);
			throw new RuntimeException(ex);
		}
	}
	
	@Override
	public PagingLoadResult<GWTSimuSystem> GetList(String userID, String searchKey, PagingLoadConfig config) {
		try {
			int count;
			List<SysType> lst;
			if (searchKey.isEmpty()) {
				count = systemDao.Count();
				PageStartEnd pse = new PageStartEnd(config, count);
				if(userID.equals("Administrator"))
					lst = systemDao.List(pse.getStart(), pse.getEnd());
				else{
					User user = DALFactory.GetBeanDAL(User.class).Get(Op.EQ("id", userID));
					if(user.getIsAdmin()==0){
						lst = systemDao.List(pse.getStart(), pse.getEnd());
					}else{
						List<UserRSystem> userRSystems =  new UserSysService()
								.GetRelateByUserID(user.getId());
						String[] sys = new String[userRSystems.size()];
						for(int i=0; i<userRSystems.size(); i++){
							sys[i] = userRSystems.get(i).getSystemid();
						}
						lst = systemDao.List(pse.getStart(), pse.getEnd(), Op.IN("systemId", sys));
					}
				}
			} else {
				String[] searchField = new String[]{GWTSimuSystem.N_SystemNo,GWTSimuSystem.N_SystemName,GWTSimuSystem.N_Desc};
				count = systemDao.MatchCount(searchKey,searchField);
				PageStartEnd pse = new PageStartEnd(config, count);
				
				if(userID.equals("Administrator"))
					lst = systemDao.Match(searchKey,searchField,pse.getStart(), pse.getEnd());
				else{
					User user = DALFactory.GetBeanDAL(User.class).Get(Op.EQ("id", userID));
					if(user.getIsAdmin()==0){
						lst = systemDao.Match(searchKey,searchField,pse.getStart(), pse.getEnd());
					}else{
						List<UserRSystem> userRSystems = new UserSysService()
								.GetRelateByUserID(user.getId());
						String[] sys = new String[userRSystems.size()];
						for(int i=0; i<userRSystems.size(); i++){
							sys[i] = userRSystems.get(i).getSystemid();
						}
						lst = systemDao.Match(searchKey,searchField,pse.getStart(), pse.getEnd(), Op.IN("systemId", sys));
					}
				}
			}

			List<GWTSimuSystem> returnList = new ArrayList<GWTSimuSystem>();
			for (SysType sys : lst)
				returnList.add(BeanToModel(sys));

			return new BasePagingLoadResult<GWTSimuSystem>(returnList, config.getOffset(), count);
		} catch (Exception ex) {
			log.error(ex, ex);
			throw new RuntimeException(ex);
		}
	}

	@Override
	public List<GWTSimuSystem> GetAllList()  {
		try {
			List<SysType> getList = systemDao.ListAll();
			List<GWTSimuSystem> returnList = new ArrayList<GWTSimuSystem>();
			for (int i = 0; i < getList.size(); i++) {
				returnList.add(BeanToModel(getList.get(i)));
			}
			return returnList;
		} catch (Exception ex) {
			log.error(ex, ex);
			throw new RuntimeException(ex);
		}
	}

	@Override
	public List<GWTSimuSystem> GetListByUserID(String userID)  {
		try {
			List<SysType> getList = systemDao.ListAll();
			List<GWTSimuSystem> returnList = new ArrayList<GWTSimuSystem>();
			if(userID.isEmpty())
			{
				for (SysType beanSystem : getList)
						returnList.add(BeanToModel(beanSystem));
			}
			else {
				List<UserRSystem> relateList = new UserSysService()
						.GetRelateByUserID(userID);
				List<String> sysIDList = new ArrayList<String>();
				for (UserRSystem relate : relateList)
					sysIDList.add(relate.getSystemid());

				for (SysType beanSystem : getList)
					if (sysIDList.indexOf(beanSystem.getSystemId()) >= 0)
						returnList.add(BeanToModel(beanSystem));
			}
			return returnList;
		} catch (Exception ex) {
			log.error(ex, ex);
			throw new RuntimeException(ex);
		}
	}

	public GWTSimuSystem BeanToModel(SysType system) {
		if (system == null)
			return null;
		return new GWTSimuSystem(system.getSystemId(), system.getSystemNo(), 
				system.getSystemName(), system.getDesc(),
				system.getIpadress(), system.getPortnum(), system.getChannel(),
				system.getFlag(), system.getNeedSqlCheck(), system.getTransactionTimeOut(),
				system.getSqlGetMethod(), system.getSqlGetDbAddr(),
				system.getEncoding4RequestMsg(), system.getEncoding4ResponseMsg(),
				system.getIsClientSimu(),system.getIsSyncComm(),system.getUseSameResponseStruct(), 
				String.valueOf(system.getResponseMode()), 
				system.getReqMsgUnpackerId()==null?"":system.getReqMsgUnpackerId(),
				system.getResMsgUnpackerId()==null?"":system.getResMsgUnpackerId());
	}

	public static SysType ModelToBean(SysType simuSystemBean, GWTSimuSystem system) {
		if (system == null)
			return null;	
		SysType sysType = new SysType();
		if (!system.GetSystemID().isEmpty())
			sysType.setSystemId(system.GetSystemID());
		if(simuSystemBean != null){
			sysType = simuSystemBean;
		}
		sysType.setDesc(system.GetDesc());
		sysType.setFlag(system.GetFlag());
		sysType.setSystemName(system.GetSystemName());
		sysType.setSystemNo(system.GetSystemNo());
		sysType.setIpadress(system.GetIP());
		sysType.setPortnum(system.GetPort());
		sysType.setChannel(system.GetChanel());
		sysType.setNeedSqlCheck(system.GetNeedSqlCheck());
		sysType.setTransactionTimeOut(system.GetTransactionTimeOut());
		sysType.setSqlGetMethod(system.GetSqlGetMethod());
		sysType.setSqlGetDbAddr(system.GetSqlGetDbAddr());
		sysType.setEncoding4RequestMsg(system.GetEncoding4RequestMsg());
		sysType.setEncoding4ResponseMsg(system.GeteEnoding4ResponseMsg());
		sysType.setIsClientSimu(system.GetIsClient());
		sysType.setIsSyncComm(system.GetIsSync());
		sysType.setUseSameResponseStruct(system.GetUseSameStruct());
		String strResponseMode = system.GetResponseMode();
		int iResponseMode = 0;
		if (strResponseMode != null && !strResponseMode.isEmpty()) {
			iResponseMode = Integer.parseInt(strResponseMode);
		}
		sysType.setResponseMode(iResponseMode);
		sysType.setReqMsgUnpackerId(system.GetReqUnPackerID());
		sysType.setResMsgUnpackerId(system.GetResUnPackerID());
		return sysType;
	}

	@Override
	public List<GWTMsgType> getUnPackerList() {
		// TODO Auto-generated method stub
		
		List<MsgPacker> list = DALFactory.GetBeanDAL(MsgPacker.class)
				.ListAll(Op.EQ(GWTMsgType.N_Type, 1));
		List<GWTMsgType> results = new ArrayList<GWTMsgType>();
		for(MsgPacker mp : list){
			results.add(BeanToModel(mp));
		}
		return results;
	}
	
	private GWTMsgType BeanToModel(MsgPacker msgPacker) {
		if (msgPacker == null)return null;
		
		GWTMsgType comp = new GWTMsgType();
		comp.set(GWTComponent.N_ComponentId, msgPacker.getId());
		comp.set(GWTMsgType.N_StyleName, msgPacker.getStylename()); 		//报文样式名称
		comp.set(GWTMsgType.N_Type, msgPacker.getType()); 				//组包标示:0组包;1拆包
		comp.set(GWTMsgType.N_Protocol, msgPacker.getMessagetype());		//
		comp.set(GWTMsgType.N_Class, msgPacker.getClassname());			//组件类
		comp.set(GWTMsgType.N_Content, msgPacker.getContent());			//Style样式内容
		
		return comp;
	}

}
