package com.dc.tes.ui.client;

import java.util.List;

import com.dc.tes.ui.client.model.GWTCore;
import com.google.gwt.user.client.rpc.RemoteService;

public interface ILaunchService extends RemoteService {
	
	List<String> GetLaunchLog();
	
	List<String> GetSenderLog();
	
	List<String> GetReceiverLog();
	
	List<GWTCore> GetCoreConfig();
	
	void LaunchCore(String corepath);
	
	void StopCore(String corepath);

}
