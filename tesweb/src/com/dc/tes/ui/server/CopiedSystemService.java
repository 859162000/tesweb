package com.dc.tes.ui.server;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.dc.tes.data.DALFactory;
import com.dc.tes.data.IDAL;
import com.dc.tes.data.model.CopiedSystem;
import com.dc.tes.ui.client.model.GWTCopiedSystem;
import com.dc.tes.ui.client.ICopiedSystemService;
import com.dc.tes.ui.util.SystemConfigManager;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;


public class CopiedSystemService extends RemoteServiceServlet implements ICopiedSystemService {

	private static final long serialVersionUID = -2012767766102092336L;
	private static final Log log = LogFactory.getLog(CopiedSystemService.class);


	@Override
	public Boolean Save(GWTCopiedSystem gwtCopiedSystem) {
		try {
			IDAL<CopiedSystem> copiedSysDal = DALFactory.GetBeanDAL(CopiedSystem.class);
			/*
			//系统名称的唯一性检查
			CopiedSystem copiedSystem = copiedSysDal.Get(new HelperService().GetDistinctOpArray(gwtCopiedSystem, gwtCopiedSystem.GetSystemName()));
			if (copiedSystem != null && copiedSystem.getId() != 0) {
				return false;
			}
			*/
			CopiedSystem copiedSystem = ModelToBean(gwtCopiedSystem);
			copiedSysDal.Add(copiedSystem);
			SystemConfigManager.copyConfig(gwtCopiedSystem.GetSystemName());
			
			return true;
		} catch (Exception ex) {
			log.error(ex, ex);
			throw new RuntimeException(ex);
		}
	}
	
	// CopiedSystem -> GWTCopiedSystem 
	public GWTCopiedSystem BeanToModel(CopiedSystem copiedSystem) {
		if (copiedSystem == null)
			return null;
		return new GWTCopiedSystem(copiedSystem.getId(), copiedSystem.getSystemName(), 
				copiedSystem.getSystemNo(), copiedSystem.getOldSystemId(), copiedSystem.getNewSystemId());
	}

	// GWTCopiedSystem -> CopiedSystem 
	public static CopiedSystem ModelToBean(GWTCopiedSystem gwtCopiedSystem) {
		if (gwtCopiedSystem == null)
			return null;
		CopiedSystem copiedSystem = new CopiedSystem();
		copiedSystem.setSystemName(gwtCopiedSystem.GetSystemName());
		copiedSystem.setSystemNo(gwtCopiedSystem.GetSystemNo());
		copiedSystem.setOldSystemId(gwtCopiedSystem.GetOldSystemID());
		copiedSystem.setId(gwtCopiedSystem.GetID());
		return copiedSystem;
	}

}
