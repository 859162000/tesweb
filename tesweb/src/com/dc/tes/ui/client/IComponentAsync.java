package com.dc.tes.ui.client;

import java.util.List;

import com.dc.tes.ui.client.enums.ComponentEnum;
import com.dc.tes.ui.client.model.GWTChannel;
import com.dc.tes.ui.client.model.GWTComponent;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface IComponentAsync {

	void GetComponentListByType(String searchKey, PagingLoadConfig config, ComponentEnum compEnum, AsyncCallback<PagingLoadResult<GWTComponent>> callback);
	
	void GetComponentListByType(ComponentEnum compEnum, AsyncCallback<List<GWTComponent>> callback);
	
	void AddNewComponent(GWTComponent comp, AsyncCallback<?> callback);
	
	void UpdateComponent(GWTComponent comp, AsyncCallback<?> callback);
	
	void DeletComponent(List<GWTComponent> complist, AsyncCallback<?> callback);
	
	void GetChannelListBySystemId(String sysId, AsyncCallback<List<GWTChannel>> callback);
	
	void SaveChannelList(String sysId, List<GWTChannel> channelist, String defChannelName, AsyncCallback<?> callback);
	
	void DeploySystem(String sysId, String defChannelName, AsyncCallback<String> callback);
	
	void IsChannelExist(String sysId, String channelName, AsyncCallback<Boolean> callback);
}
