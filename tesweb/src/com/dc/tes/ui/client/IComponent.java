package com.dc.tes.ui.client;

import java.util.List;

import com.dc.tes.ui.client.enums.ComponentEnum;
import com.dc.tes.ui.client.model.GWTChannel;
import com.dc.tes.ui.client.model.GWTComponent;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.google.gwt.user.client.rpc.RemoteService;

public interface IComponent extends RemoteService {

	PagingLoadResult<GWTComponent> GetComponentListByType(String searchKey, PagingLoadConfig config, ComponentEnum compEnum);
	
	List<GWTComponent> GetComponentListByType(ComponentEnum compEnum);
	
	void AddNewComponent(GWTComponent comp);
	
	void UpdateComponent(GWTComponent comp);
	
	void DeletComponent(List<GWTComponent> complist);
	
	List<GWTChannel> GetChannelListBySystemId(String sysId);
	
	void SaveChannelList(String sysId, List<GWTChannel> channelist, String defChannelName);
	
	String DeploySystem(String sysId, String defChannelName);
	
	boolean IsChannelExist(String sysId, String channelName);
}
