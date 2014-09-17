package com.dc.tes.ui.client;

import java.util.List;

import com.dc.tes.ui.client.model.GWTExecuteSetDirectory;
import com.dc.tes.ui.client.model.GWTSimuSystem;
import com.extjs.gxt.ui.client.data.ModelData;
import com.google.gwt.user.client.rpc.RemoteService;

public interface IExecuteSetService extends RemoteService {
	
	List<ModelData> getExecuteSetTree(GWTSimuSystem system, GWTExecuteSetDirectory gwtExecuteSetDirectory);
	
	GWTExecuteSetDirectory saveOrUpdateExecuteSet(GWTExecuteSetDirectory gwtExecuteSetDirectory, Integer loginLogId);

	boolean deleteSelectedItem(List<ModelData> items, Integer loginLogId);
}
