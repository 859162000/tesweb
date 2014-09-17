package com.dc.tes.ui.client;

import java.util.List;

import com.dc.tes.ui.client.model.GWTExecuteSetDirectory;
import com.dc.tes.ui.client.model.GWTSimuSystem;
import com.extjs.gxt.ui.client.data.ModelData;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface IExecuteSetServiceAsync {
	
	void getExecuteSetTree(GWTSimuSystem system, GWTExecuteSetDirectory gwtExecuteSetDirectory, AsyncCallback<List<ModelData>> callback);
	
	void saveOrUpdateExecuteSet(GWTExecuteSetDirectory gwtExecuteSetDirectory, Integer loginLogId, AsyncCallback<GWTExecuteSetDirectory> callback);

	void deleteSelectedItem(List<ModelData> items, Integer loginLogId, AsyncCallback<Boolean> callback);
}
