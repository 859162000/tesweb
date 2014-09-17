package com.dc.tes.ui.server;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.dc.tes.data.DALFactory;
import com.dc.tes.data.IDAL;
import com.dc.tes.data.model.RecordedCase;
import com.dc.tes.data.op.Op;
import com.dc.tes.ui.client.IRecordedCaseService;
import com.dc.tes.ui.client.enums.IDUType;
import com.dc.tes.ui.client.enums.OpType;
import com.dc.tes.ui.client.model.GWTRecordedCase;
import com.dc.tes.ui.client.model.GWTUser;
import com.dc.tes.ui.client.model.PageStartEnd;
import com.extjs.gxt.ui.client.data.BasePagingLoadResult;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class RecordedCaseService extends RemoteServiceServlet implements IRecordedCaseService {
	private static final long serialVersionUID = 4661388826496708731L;
	
	private static final Log log = LogFactory.getLog(RecordedCaseService.class);
	IDAL<RecordedCase> dataDao = DALFactory.GetBeanDAL(RecordedCase.class);
	
	
	/*@Override
	public PagingLoadResult<GWTRecordedCase> GetGWTRecordedCasePageList(String systemID, String searchInfo, PagingLoadConfig config) {
		try {
			int count;
			List<RecordedCase> lst;
			Op op1 = Op.EQ(GWTRecordedCase.N_SystemId, Integer.parseInt(systemID));
			if (searchInfo.isEmpty()) {
				count = dataDao.Count(op1);
				PageStartEnd pse = new PageStartEnd(config, count);
				lst = dataDao.List(pse.getStart(), pse.getEnd(),op1);
			} else {
				String[] searchField = new String[]{GWTRecordedCase.N_RequestMsg};
				count = dataDao.MatchCount(searchInfo,searchField,op1);
				PageStartEnd pse = new PageStartEnd(config, count);
				lst = dataDao.Match(searchInfo,searchField,pse.getStart(), pse.getEnd(),op1);
			}

			LinkedList<GWTRecordedCase> returnList = new LinkedList<GWTRecordedCase>();
			
			for (RecordedCase data : lst)
				returnList.addFirst(BeanToModel(data));

			return new BasePagingLoadResult<GWTRecordedCase>(returnList, config.getOffset(), count);
					
		} catch (Exception ex) {
			log.error(ex, ex);
			throw new RuntimeException(ex);
		}
	}*/
	
	@Override
	public PagingLoadResult<GWTRecordedCase> GetGWTRecordedCasePageList(String sysId, String searchKey, PagingLoadConfig config) {
		
		try {
			List<GWTRecordedCase> returnList = new ArrayList<GWTRecordedCase>();
			Op[] conditions;
			int count;
			List<RecordedCase> lst;
			
			List<Op> conList = new ArrayList<Op>();
			conList.add(Op.EQ("systemId", Integer.parseInt(sysId)));			
			if (config.get("date") != null) {
				String date = config.get("date");
				conList.add(Op.LIKE("createTime", date));
			} 			
			if(config.get("reponseFlag") != null){
				if(!config.get("reponseFlag").equals("-1")){
					conList.add(Op.EQ(GWTRecordedCase.N_ResponseFlag, Integer.parseInt(config.get("reponseFlag").toString())));
				}
			}
			if(config.get("isCased") != null){
				if(!config.get("isCased").equals("-1")){
					conList.add(Op.EQ(GWTRecordedCase.N_IsCased, Integer.parseInt(config.get("isCased").toString())));
				}
			}
			if(config.get("user") != null){
				if(!config.get("user").equals("-1")){
					conList.add(Op.EQ("recordUserId", Integer.parseInt(config.get("user").toString())));
				}
			}
			
			conditions = new Op[conList.size()];
			for(int i = 0; i < conList.size(); i++){
				conditions[i] = conList.get(i);
			}
			
			if (searchKey.isEmpty()) {
				count = dataDao.Count(conditions);
				PageStartEnd pse = new PageStartEnd(config, count);
				lst = dataDao.List(pse.getStart(), pse.getEnd(), conditions);
			} 
			else {
				String[] properties = {"requestMsg", "responseMsg", GWTRecordedCase.N_Memo};
				count = dataDao.MatchCount(searchKey, properties, conditions);
				PageStartEnd pse = new PageStartEnd(config, count);
				lst = dataDao.Match(searchKey, properties, pse.getStart(), pse.getEnd(), conditions);
			}

			for (RecordedCase recrdCase : lst) {
				if (recrdCase != null)
					returnList.add(BeanToModel(recrdCase));
			}

			return new BasePagingLoadResult<GWTRecordedCase>(returnList, config.getOffset(), count);
		}
		catch (Exception ex) {
			log.error(ex, ex);
			throw new RuntimeException(ex);
		}
	}

	private GWTRecordedCase BeanToModel(RecordedCase data) {
		String userName = "";
		if (data.getRecordUserId() >= 0) {
			userName = (new UserService()).GetUserNameByUserId(String.valueOf(data.getRecordUserId()));
		}
		return new GWTRecordedCase(data.getId(),data.getRequestMsg(),data.getResponseMsg(),
				String.valueOf(data.getSystemId()), data.getMemo(), data.getResponseFlag(), data.getIsCased(),
				data.getRecordUserId(), data.getRecordTime(), data.getCreateTime(), userName);
	}

	@Override
	public Boolean SaveRecordedCase(GWTRecordedCase recordedCase, Integer loginLogId) {

		RecordedCase data = ModelToBean(recordedCase);
		String userId = OperationLogService.getLoginLogById(loginLogId).getUserId();
		if(recordedCase.IsNew()){
			data.setRecordTime(new Date());
			data.setRecordUserId(Integer.parseInt(userId));
			dataDao.Add(data);	
			OperationLogService.writeOperationLog(OpType.RecordedCase, IDUType.Insert, 
					data.getId(), data.getRequestMsg(),	"memo", null, data.getRequestMsg(), loginLogId);
		}else{
			RecordedCase oldBean = dataDao.Get(Op.EQ("id", Integer.parseInt(recordedCase.getID())));
			data.setLastModifiedTime(new Date());
			data.setLastModifiedUserId(userId);
			try {
				OperationLogService.writeUpdateOperationLog(OpType.RecordedCase, RecordedCase.class, 
					oldBean.getId(), "ID="+recordedCase.getID(), oldBean, data, loginLogId);
			}
			catch(Exception e) {
				log.error(e, e);
			}
			dataDao.Edit(data);
		}
		return true;
	}

	@Override
	public Boolean DeleteRecordedCase(List<GWTRecordedCase> recordedCaseList, Integer loginLogId) {
	
		for(GWTRecordedCase recordedCase : recordedCaseList) {
			dataDao.Del(ModelToBean(recordedCase));
			try {
				OperationLogService.writeOperationLog(OpType.RecordedCase, IDUType.Delete,
					Integer.parseInt(recordedCase.getID()), recordedCase.getMemo(),
					"requestMsg/recordTime", recordedCase.getRequestMsg()==null?"":recordedCase.getRequestMsg().substring(0,1024), recordedCase.GetRecordTime(), loginLogId);
			}
			catch(Exception e) {
				log.error(e, e);
			}
		}
		
		return true;
	}
	
	@Override
	public List<GWTUser> GetUserList(String sysId) {
		return (new UserService()).GetUserBySystem(sysId);
	}

	private RecordedCase ModelToBean(GWTRecordedCase gwtRecordedCase) {

		RecordedCase bean = new RecordedCase();
		bean.setRequestMsg(gwtRecordedCase.getRequestMsg());
		bean.setResponseMsg(gwtRecordedCase.getResponseMsg());
		bean.setResponseFlag(gwtRecordedCase.getResponseFlag());
		bean.setIsCased(gwtRecordedCase.getIsCased());
		bean.setMemo(gwtRecordedCase.getMemo());
		bean.setCreateTime(gwtRecordedCase.GetCreateTime());
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = new Date();
		try {
			String recordTime = gwtRecordedCase.GetRecordTime();
			date = sdf.parse(recordTime);
		} catch (ParseException e) {
			String recordTime = gwtRecordedCase.GetCreateTime();
			try {
				date = sdf.parse(recordTime);
			} catch (ParseException e1) {
				e1.printStackTrace();
			}
		}
		bean.setRecordTime(date);
		bean.setSystemId(Integer.parseInt(gwtRecordedCase.getSystemID()));
		if (gwtRecordedCase.GetRecordUserId() != null) {
			bean.setRecordUserId(Integer.parseInt(gwtRecordedCase.GetRecordUserId()));
		}
		if(!gwtRecordedCase.IsNew()) {
			bean.setId(Integer.parseInt(gwtRecordedCase.getID()));
		}
		//bean.setRecordTime(gwtRecordedCase.GetRecordTime());
		
		return bean;
	}

	
}
