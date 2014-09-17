package com.dc.tes.ui.client.model;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * 组包必备基础类
 * @author scckobe
 *
 */
public class GWTPackNeed implements IsSerializable {
	private String sysID;
	private String sysName;
	private String sysChanelName;
	private String tranChanelName;
	private String tranCode;
	private boolean isClient;
	
	public GWTPackNeed()
	{
	}
	
	public GWTPackNeed(GWTSimuSystem sysInfo,String tranChanelName,String tranCode)
	{
		this(sysInfo.GetSystemID(),sysInfo.GetSystemName(),sysInfo.GetChanel(),sysInfo.GetIsClient() != 0, tranChanelName,tranCode);
	}
	
	public GWTPackNeed(String sysID,String sysName,String sysChanelName, Boolean isClient, String tranChanelName,String tranCode)
	{
		this.sysID = sysID;
		this.sysName = sysName;
		this.sysChanelName = sysChanelName;
		this.isClient = isClient;
		this.tranChanelName = tranChanelName;
		this.tranCode = tranCode;
	}
	
	public String GetSysID()
	{
		return sysID;
	}
	
	public String GetSysName()
	{
		return sysName;
	}
	
	public String GetSysChanel()
	{
		return sysChanelName;
	}
	
	public String GetTranChanel()
	{
		return tranChanelName;
	}
	
	public String GetTranCode()
	{
		return tranCode;
	}
	
	public boolean GetIsClient()
	{
		return isClient;
	}
}
