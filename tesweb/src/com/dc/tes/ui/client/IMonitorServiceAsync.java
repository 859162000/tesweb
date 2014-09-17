package com.dc.tes.ui.client;

import java.util.List;

import com.dc.tes.ui.client.model.GWTChannelInfo;
import com.dc.tes.ui.client.model.GWTMoniLogDetail;
import com.google.gwt.user.client.rpc.AsyncCallback;


public interface IMonitorServiceAsync {
	void GetMoniLogDetail(String sysname, int begid, AsyncCallback<List<GWTMoniLogDetail>> callback);
	
	void GetChannelList(String sysname, AsyncCallback<List<GWTChannelInfo>> callback);
}
