package com.dc.tes.ui.client;

import java.util.List;

import com.dc.tes.ui.client.model.GWTStatSys;
import com.dc.tes.ui.client.model.GWTStatTran;
import com.dc.tes.ui.client.model.GWTStatTrend;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface IStaticsAsync {
	void GetSysStatistic(int begin,int end, AsyncCallback<List<GWTStatSys>> callback);
	void GetTranStatistic(String sysName,int begin,int end, AsyncCallback<List<GWTStatTran>> callback);
	void GetTrendStatistic(String sysName,String tranCode,String tranName,
			int type,int begin,int end, AsyncCallback<List<GWTStatTrend>> callback);
}
