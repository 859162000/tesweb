package com.dc.tes.ui.client;

import java.util.List;

import com.dc.tes.ui.client.model.GWTChannelInfo;
import com.dc.tes.ui.client.model.GWTMoniLogDetail;
import com.google.gwt.user.client.rpc.RemoteService;


public interface IMonitorService extends RemoteService {
	List<GWTMoniLogDetail> GetMoniLogDetail(String sysname, int begid);
	
	List<GWTChannelInfo> GetChannelList(String sysname);
}
