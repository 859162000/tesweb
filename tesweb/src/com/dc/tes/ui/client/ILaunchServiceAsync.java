package com.dc.tes.ui.client;

import java.util.List;

import com.dc.tes.ui.client.model.GWTCore;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface ILaunchServiceAsync {
	
	void GetLaunchLog(AsyncCallback<List<String>> callback);
	
	void GetSenderLog(AsyncCallback<List<String>> callback);
	
	void GetReceiverLog(AsyncCallback<List<String>> callback);
	
	void GetCoreConfig(AsyncCallback<List<GWTCore>> callback);
	
	void LaunchCore(String corepath, AsyncCallback<?> callback);
	
	void StopCore(String corepath, AsyncCallback<?> callback);

}
