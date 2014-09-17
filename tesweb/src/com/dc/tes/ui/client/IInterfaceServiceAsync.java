package com.dc.tes.ui.client;

import java.util.List;

import com.dc.tes.ui.client.model.GWTInterfaceDef;
import com.dc.tes.ui.client.model.GWTInterfaceField;
import com.dc.tes.ui.client.model.GWTPack_Struct;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface IInterfaceServiceAsync {

	void GetInterfaceDefList(
			String systemID, String searchKey, PagingLoadConfig config, AsyncCallback<PagingLoadResult<GWTInterfaceDef>> callback);

	void SaveOrUpdateInterfaceDef(GWTInterfaceDef interfaceDef, Integer loginLogId, AsyncCallback<Boolean> callback);
	
	void DeleteInterfaceDef(List<GWTInterfaceDef> interfaceDefs, Integer loginLogId, AsyncCallback<?> callback);
	
	void GetInterfaceFields(GWTInterfaceDef interfaceDef, AsyncCallback<List<GWTInterfaceField>> callback);
	
	void SaveOrUpdateInterfaceField(GWTInterfaceField field, Integer loginLogId, AsyncCallback<GWTInterfaceField> callback);
	
	void updateFieldSequence(List<GWTInterfaceField> fields, AsyncCallback<?> callback);
	
	void DeleteInterfaceField(List<GWTInterfaceField> interfaceFileds, Integer loginLogId, AsyncCallback<?> callback);
	
	void translateInterfaceToXML(List<GWTInterfaceDef> gwtInterfaceDefs, AsyncCallback<GWTPack_Struct> callback);
}
