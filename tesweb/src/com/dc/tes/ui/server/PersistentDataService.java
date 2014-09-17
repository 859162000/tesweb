package com.dc.tes.ui.server;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.dc.tes.data.DALFactory;
import com.dc.tes.data.IDAL;
import com.dc.tes.data.model.PersistentData;
import com.dc.tes.data.op.Op;
import com.dc.tes.ui.client.IPersistentDataService;
import com.dc.tes.ui.client.model.GWTPersistentData;
import com.dc.tes.ui.client.model.PageStartEnd;
import com.extjs.gxt.ui.client.data.BasePagingLoadResult;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class PersistentDataService  extends RemoteServiceServlet implements IPersistentDataService {
	private static final long serialVersionUID = -8274455600733615682L;
	private static final Log log = LogFactory.getLog(PersistentDataService.class);
	IDAL<PersistentData> dataDao = DALFactory.GetBeanDAL(PersistentData.class);
	
	@Override
	public void DeletePersistentData(List<GWTPersistentData> dataInfo) {
		try {
			for(GWTPersistentData gwtData : dataInfo)
			{
				dataDao.Del(ModelToBean(gwtData));
			}
		} catch (Exception ex) {
			log.error(ex, ex);
			throw new RuntimeException(ex);
		}
	}

	@Override
	public PagingLoadResult<GWTPersistentData> GetGWTPersistentDataList(
			String systemID, String searchInfo, PagingLoadConfig config) {
		try {
			int count;
			List<PersistentData> lst;
			Op op1 = Op.EQ(GWTPersistentData.N_SystemID, systemID);
			if (searchInfo.isEmpty()) {
				count = dataDao.Count(op1);
				PageStartEnd pse = new PageStartEnd(config, count);
				lst = dataDao.List(pse.getStart(), pse.getEnd(),op1);
			} else {
				String[] searchField = new String[]{GWTPersistentData.N_Parameter,GWTPersistentData.N_Curvalue};
				count = dataDao.MatchCount(searchInfo,searchField,op1);
				PageStartEnd pse = new PageStartEnd(config, count);
				lst = dataDao.Match(searchInfo,searchField,pse.getStart(), pse.getEnd(),op1);
			}

			List<GWTPersistentData> returnList = new ArrayList<GWTPersistentData>();
			for (PersistentData data : lst)
				returnList.add(BeanToModel(data));

			return new BasePagingLoadResult<GWTPersistentData>(returnList, config.getOffset(), count);
		} catch (Exception ex) {
			log.error(ex, ex);
			throw new RuntimeException(ex);
		}
	}

	@Override
	public Boolean SavePersistentData(GWTPersistentData dataInfo) {
		try {
			PersistentData data = dataDao.Get(new HelperService().GetDistinctOpArray(dataInfo, dataInfo.getParameter()));
			if (data != null && data.getId().compareTo(dataInfo.getID()) != 0) {
				return false;
			}
			
			data = ModelToBean(dataInfo);
			if (dataInfo.IsNew())
				dataDao.Add(data);
			else
				dataDao.Edit(data);
			return true;
		} catch (Exception ex) {
			log.error(ex, ex);
			throw new RuntimeException(ex);
		}
	}
	
	public GWTPersistentData BeanToModel(PersistentData data) {
		if (data == null)
			return null;
		return new GWTPersistentData(data.getId(),data.getSystemid(),data.getParameter(),data.getCurvalue(),data.getType());
	}

	public static PersistentData ModelToBean(GWTPersistentData gwtData) {
		if (gwtData == null)
			return null;
		PersistentData data = new PersistentData();
		data.setSystemid(gwtData.getSystemID());
		data.setParameter(gwtData.getParameter());
		data.setCurvalue(gwtData.getCurvalue());
		data.setType(gwtData.getType());
		if (!gwtData.IsNew())
			data.setId(gwtData.getID());
		return data;
	}
}
