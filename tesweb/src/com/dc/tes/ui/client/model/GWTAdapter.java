package com.dc.tes.ui.client.model;

import java.util.ArrayList;
import java.util.List;

public class GWTAdapter extends GWTComponent {

	private static final long serialVersionUID = 3904929388749602811L;
	
	public static String N_Protocol = "protocol";  			//协议
	public static String N_Desc = "desc";					//适配器描述
	public static String N_CsType = "csType";				//client/server 适配器类型
	public static String N_PlugIn = "plugin";				//插件类
	public static String N_ConfigTemplate = "confTemp";		//默认配置模板
	public static String N_CreatedUserId = "createdUserId";
	public static String N_CreatedTime = "createdTime";
	public static String N_LastModifiedTime = "lastModifiedTime";
	public static String N_LastModifiedUserId = "lastModifiedUserId";
	public GWTAdapter(){}
	
	public GWTAdapter(Integer id, String pro, String desc, String csType, String plugin, String temp){
		set(N_ComponentId, id);
		set(N_Protocol, pro);
		set(N_Desc, desc);
		set(N_CsType, csType);
		set(N_PlugIn, plugin);
		set(N_ConfigTemplate, temp);
	}
	
	
	
	@Override
	public List<GWTComponent> GetTestData(){
		
		String tempConfig = "#适配器要进行绑定的网卡IP地址，服务端模拟时必须"
							+"\r\nIP=127.0.0.1"
							+"\r\n#适配器要进行监听的端口，服务端模拟时必须"
							+"\r\nPORT=9999"
							+"\r\n#是否为长连接形式"
							+"\r\nISLAST=0"
							+"\r\n#是否为定长报文，大于零的值表示为定长报文，且报文长度为该值，小于等于零的值表示为变长报文"
							+"\r\nISFIX=0"
							+"\r\n#表示为了要获取变长报文的长度信息需要预先接收的长度"
							+"\r\nLEN4LEN=10"
							+"\r\n#报文长度信息在报文中的开始位置"
							+"\r\nLENSTART=0"
							+"\r\n#报文长度信息的长度"
							+"\r\nLENLEN=10"
							+"\r\n#是否需要与核心进行交互，若设置为0则直接读本地报文文件进行返回"
							+"\r\nNEED2CORE=1"
							+"\r\n#是否需要返回报文，主要针对异步通讯模式下，如果异步模式下无需在只读连接下返回则设置为0"
							+"\r\nNEEDBACK=1"
							+"\r\n#每次是否返回固定报文"
							+"\r\nFIXBACK=0";
		
		List<GWTComponent> data = new ArrayList<GWTComponent>();
		
		data.add(new GWTAdapter(0, "HTTP", "HTTP-CLIENT", "SEND", "HTTPRequestAdapterPlugin", tempConfig));
		data.add(new GWTAdapter(1, "HTTP", "HTTP-SERVER", "RECEIVE", "HTTPReplyAdapterPlugin", tempConfig));
		data.add(new GWTAdapter(2, "SOAP", "SOAP-CLIENT", "SEND", "SOAPRequestAdapterPlugin", tempConfig));
		data.add(new GWTAdapter(3, "SOAP", "SOAP-SERVER", "RECEIVE", "SOAPReplyAdapterPlugin", tempConfig));
		data.add(new GWTAdapter(4, "TCP", "TCP-CLIENT", "SEND", "TCPRequestAdapterPlugin", tempConfig));
		data.add(new GWTAdapter(5, "TCP", "TCP-SERVER", "RECEIVE", "TCPReplyAdapterPlugin", tempConfig));
		data.add(new GWTAdapter(6, "MQ", "HTTP-CLIENT", "SEND", "MQRequestAdapterPlugin", tempConfig));
		data.add(new GWTAdapter(7, "MQ", "HTTP-SERVER", "RECEIVE", "MQReplyAdapterPlugin", tempConfig));
		data.add(new GWTAdapter(6, "TUXEDO", "TUXEDO-CLIENT", "SEND", "TuxedoRequestAdapterPlugin", tempConfig));
		data.add(new GWTAdapter(7, "TUXEDO", "TUXEDO-SERVER", "RECEIVE", "TuxedoReplyAdapterPlugin", tempConfig));
		
		return data;
	}
	
	@Override
	public GWTComponent GetSingleObjectById(int id){
		List<GWTComponent> data = GetTestData();
		for(GWTComponent adapter : data){
			int aid = adapter.<Integer>get(N_ComponentId);
			
			if(aid == id)
				return adapter;
		}
		
		return null;
	}
	
	public void SetCreatedUserId(String createdUserId){
		this.set(N_CreatedUserId, createdUserId);
	}
	
	public String GetCreatedUserId(){
		return this.get(N_CreatedUserId);
	}
	
	public void SetCreatedTime(String createdTime){
		this.set(N_CreatedTime, createdTime);
	}
	
	public String GetCreatedTime(){
		return this.get(N_CreatedTime);
	}
	
	public void SetLastModifiedTime(String lastModifiedTime){
		this.set(N_LastModifiedTime, lastModifiedTime);
	}
	
	public String GetLastModifiedTime(){
		return this.get(N_LastModifiedTime);
	}
	
	public void SetLastModifiedUserId(String lastModifiedUserId){
		this.set(N_LastModifiedUserId, lastModifiedUserId);
	}
	
	public String GetLastModifiedUserId(){
		return this.get(N_LastModifiedUserId);
	}

}
