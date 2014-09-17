package com.dc.tes.ui.client;

import java.util.List;

import com.dc.tes.ui.client.model.GWTStatSys;
import com.dc.tes.ui.client.model.GWTStatTran;
import com.dc.tes.ui.client.model.GWTStatTrend;
import com.google.gwt.user.client.rpc.RemoteService;

public interface IStatics extends RemoteService {
	List<GWTStatSys> GetSysStatistic(int begin,int end);
	List<GWTStatTran> GetTranStatistic(String sysName,int begin,int end);
	List<GWTStatTrend> GetTrendStatistic(String sysName,String tranCode,String tranName,
			int type,int begin,int end);
}
