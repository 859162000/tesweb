package com.dc.tes.ui.client.model;

import java.util.ArrayList;
import java.util.List;

public class GWTMsgType extends GWTComponent {

	private static final long serialVersionUID = 3586103301855821099L;
	
	public static String N_StyleName = "styleName"; //样式名称
	public static String N_Desc = "desc";			//描述
	public static String N_Type = "type";			//拆包or组包
	public static String N_Protocol = "protocol";	//协议类型
	public static String N_Class = "class";			//组件类
	public static String N_Content = "content";		//style配置内容
	
	public static String N_CreatedUserId = "createdUserId";
	public static String N_CreatedTime = "createdTime";
	public static String N_LastModifiedTime = "lastModifiedTime";
	public static String N_LastModifiedUserId = "lastModifiedUserId";
	
	public GWTMsgType(){}
	
	public GWTMsgType(Integer id, String styleName, String desc, String type, String pro, String cls, String content){
		set(N_ComponentId, id);
		set(N_StyleName, styleName);
		set(N_Desc, desc);
		set(N_Type, type);
		set(N_Protocol, pro);
		set(N_Class, cls);
		set(N_Content, content);
	}
	
	@Override
	public List<GWTComponent> GetTestData(){
		
		List<GWTComponent> data = new ArrayList<GWTComponent>();
		
		data.add(new GWTMsgType(0, "xml-ciss", "ciss", "pack", "xml", "com.dc.tes.fcore.msg.DefaultPacker", ""));
		data.add(new GWTMsgType(1, "byte-vgop", "vgop", "unpack", "byte", "com.dc.tes.fcore.msg.DefaultPacker", ""));
		data.add(new GWTMsgType(2, "8583-eaih", "eaih", "pack", "8583", "com.dc.tes.fcore.msg.DefaultPacker", ""));
		
		return data;
	}
	
	@Override
	public GWTComponent GetSingleObjectById(int id){
		List<GWTComponent> data = GetTestData();
		for(GWTComponent msg : data){
			int aid = msg.<Integer>get(N_ComponentId);
			if(aid == id)
				return msg;
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
