package com.dc.tes.ui.client;

import java.util.List;

import com.dc.tes.ui.client.model.GWTInterfaceDef;
import com.dc.tes.ui.client.model.GWTInterfaceField;
import com.dc.tes.ui.client.model.GWTPack_Struct;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.google.gwt.user.client.rpc.RemoteService;

public interface IInterfaceService extends RemoteService {

	PagingLoadResult<GWTInterfaceDef> GetInterfaceDefList(
			String systemID, String searchKey, PagingLoadConfig config);

	Boolean SaveOrUpdateInterfaceDef(GWTInterfaceDef interfaceDef, Integer loginLogId);
	
	void DeleteInterfaceDef(List<GWTInterfaceDef> interfaceDefs, Integer loginLogId);
	
	List<GWTInterfaceField> GetInterfaceFields(GWTInterfaceDef interfaceDef);
	
	GWTInterfaceField SaveOrUpdateInterfaceField(GWTInterfaceField field, Integer loginLogId);
	
	void updateFieldSequence(List<GWTInterfaceField> fields);
	
	void DeleteInterfaceField(List<GWTInterfaceField> interfaceFileds, Integer loginLogId);
	
	GWTPack_Struct translateInterfaceToXML(List<GWTInterfaceDef> gwtInterfaceDefs);
}
