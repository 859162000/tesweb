package com.dc.tes.ui.server;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.dc.tes.monitor.data.Context;
import com.dc.tes.monitor.data.LogDetail;
import com.dc.tes.monitor.data.LogMessage;
import com.dc.tes.ui.client.IMonitorService;
import com.dc.tes.ui.client.model.GWTChannelInfo;
import com.dc.tes.ui.client.model.GWTMoniLogDetail;
import com.dc.tes.util.RuntimeUtils;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class MonitorService extends RemoteServiceServlet implements IMonitorService {

	private static final long serialVersionUID = 6945727431717173480L;

	@SuppressWarnings({ "deprecation" })
	@Override
	public List<GWTMoniLogDetail> GetMoniLogDetail(String sysname, int begid) {
		
		List<GWTMoniLogDetail> list = new ArrayList<GWTMoniLogDetail>();
		
		LogMessage manager = Context.getLogMsg(sysname);
//		String coreRegInfo = manager.GetCoreRegInfo();
//		if(coreRegInfo != null && !coreRegInfo.equals("")){
//			GWTMoniLogDetail detail = new GWTMoniLogDetail();
//			detail.set(GWTMoniLogDetail.N_REGINFO, coreRegInfo);
//			list.add(detail);
//			return list;
//		}
		
		LogDetail[] logs = manager.getLog(begid);
		if(logs == null || logs.length == 0){
			GWTMoniLogDetail detail = new GWTMoniLogDetail();
			detail.set(GWTMoniLogDetail.N_SYSNAME, null);
			detail.set(GWTMoniLogDetail.N_LASTID, manager.getmaxid());
			list.add(detail);
			return list;
		}
		
		GWTMoniLogDetail detail = null;
		
		for(LogDetail item : logs){
			detail = new GWTMoniLogDetail();
			detail.set(GWTMoniLogDetail.N_SYSNAME, sysname);
//			detail.set(GWTMoniLogDetail.N_CASENAME, item.getCASENAME());
//			detail.set(GWTMoniLogDetail.N_CHANNEL, item.getChannelname());
			detail.set(GWTMoniLogDetail.N_DATAIN, item.getDATAIN());
//			detail.set(GWTMoniLogDetail.N_DATAOUT, item.getDATAOUT());
			detail.set(GWTMoniLogDetail.N_ERRMSG, item.getERRMSG());
			
			String strResponseMsg = new String(item.getMSGIN());		
			//非xml文本，以16进制方式打印，兼容不可见字符
			if(!strResponseMsg.startsWith("<?xml")) {
				if(!strResponseMsg.contains("<?xml"))
					strResponseMsg = RuntimeUtils.PrintHex(item.getMSGIN(), Charset
							.forName("GBK"));
			}
			
			detail.set(GWTMoniLogDetail.N_MSGIN, strResponseMsg);
//			detail.set(GWTMoniLogDetail.N_MSGOUT, new String(item.getMSGOUT()));
//			detail.set(GWTMoniLogDetail.N_TRANCODE, item.getTRANCODE());
//			detail.set(GWTMoniLogDetail.N_TRANNAME, item.getTRANNAME());
			detail.set(GWTMoniLogDetail.N_TRANSTATE, item.getTRANSTATE());
			detail.set(GWTMoniLogDetail.N_TRANTIME, item.getTRANTIME().toLocaleString());
			detail.set(GWTMoniLogDetail.N_LASTID, manager.getmaxid());
			list.add(detail);
		}
		
		return list;
	}
	
	public List<GWTChannelInfo> GetChannelList(String sysname){
		
		LogMessage manager = Context.getLogMsg(sysname);
		String adapterInfo = manager.getAdpflag();
		//System.out.println(adapterInfo);
		
		if(adapterInfo == null || adapterInfo.isEmpty())
			return null;
		
		List<GWTChannelInfo> returnList = new ArrayList<GWTChannelInfo>();
		List<String> channelList = Arrays.asList(adapterInfo.split(","));
		
		//增加个核心状态
		GWTChannelInfo core = new GWTChannelInfo();
		core.set(GWTChannelInfo.N_ChannelName, manager.getSysname()+"核心");
		core.set(GWTChannelInfo.N_Status, 1);				
		core.set(GWTChannelInfo.N_ChannelType, manager.getCorepath());
		returnList.add(core);
		
		for(String channelStr : channelList){
			
			String[] values = channelStr.split(":");
			String name = values[0];
			boolean status = Boolean.parseBoolean(values[1]);
			
			GWTChannelInfo info = new GWTChannelInfo();
			info.set(GWTChannelInfo.N_ChannelName, name);
			info.set(GWTChannelInfo.N_Status, status ? 1 : -1);
			
			returnList.add(info);
		}

		return returnList;
	}

}
