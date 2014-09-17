package com.dc.tes.ui.client.model;

import java.util.ArrayList;
import java.util.List;

/**
 * 交易识别客户端实体类
 * @author shenfx
 *
 */
public class GWTRecCode extends GWTComponent {

	private static final long serialVersionUID = -398419431940421310L;
	
	public static String N_Name = "name";  			//协议
	public static String N_Desc = "desc";			//交易识别描述
	public static String N_Type = "type";			//识别类型
	public static String N_Class = "class";			//组件类
	public static String N_Template = "conf";		
	public static String N_Param1 = "param1";
	public static String N_Param2 = "param2";
	public static String N_Param3 = "param3";
	public static String N_Param4 = "param4";
	public static String N_Param5 = "param5";
	public static String N_Param6 = "param6";
	
	public static String N_CreatedUserId = "createdUserId";
	public static String N_CreatedTime = "createdTime";
	public static String N_LastModifiedTime = "lastModifiedTime";
	public static String N_LastModifiedUserId = "lastModifiedUserId";
	
	public GWTRecCode(){}
	
	public GWTRecCode(Integer id, String name, String desc, String type, String cls, String template){
		set(N_ComponentId, id);
		set(N_Name, name);
		set(N_Desc, desc);
		set(N_Type, type);
		set(N_Class, cls);
		set(N_Template, template);
	}
	
	public GWTRecCode(Integer id, String name, String desc, String type, String cls, String p1, String p2, String p3, String p4, String p5, String p6){
		set(N_ComponentId, id);
		set(N_Name, name);
		set(N_Desc, desc);
		set(N_Type, type);
		set(N_Class, cls);
		set(N_Param1, p1);
		set(N_Param2, p2);
		set(N_Param3, p3);
		set(N_Param4, p4);
		set(N_Param5, p5);
		set(N_Param6, p6);
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

	
	@Override
	public List<GWTComponent> GetTestData(){
		
		String template = "<txcode name='vgop' class='com.dc.tes.txcode.BorderRecogniser' channel='SEND'>" +
				"\r\n\t<left><![CDATA[<ActivityCode>]]></left>" +
				"\r\n\t<right><![CDATA[</ActivityCode>]]></right>" +
				"\r\n\t<encoding>utf-8</encoding>" +
				"\r\n\t<index>0</index>" +
				"\r\n</txcode>";
		
		List<GWTComponent> data = new ArrayList<GWTComponent>();
		
		data.add(new GWTRecCode(0, "ciss", "根据位置识别", "PosRecogniser", "com.dc.tes.txcode.PosRecogniser", template ));
		data.add(new GWTRecCode(1, "vgop", "根据边界识别", "BorderRecogniser", "com.dc.tes.txcode.BorderRecogniser", template ));
		data.add(new GWTRecCode(2, "eaih", "根据正则表达式识别", "RegexRecogniser", "com.dc.tes.txcode.RegexRecogniser", template ));
		data.add(new GWTRecCode(3, "ccbs", "根据自定义函数识别", "FunctionRecogniser", "com.dc.tes.txcode.FunctionRecogniser", template ));
		data.add(new GWTRecCode(4, "ectip", "根据自定义脚本识别", "ScriptRecogniser", "com.dc.tes.txcode.ScriptRecogniser", template ));
		
//		data.add(new GWTRecCode(0, "ciss", "根据位置识别", "PosRecogniser", "com.dc.tes.txcode.PosRecogniser", "start=0", "length=0", "encoding=gb2312", "", "", "" ));
//		data.add(new GWTRecCode(1, "vgop", "根据边界识别", "BorderRecogniser", "com.dc.tes.txcode.BorderRecogniser", "left=<trancode>", "right=</trancode>", "index=0", "encoding=utf-8", "", "" ));
//		data.add(new GWTRecCode(2, "eaih", "根据正则表达式识别", "RegexRecogniser", "com.dc.tes.txcode.RegexRecogniser", "regex=/*w", "group=''", "index=0", "encoding=gb2312", "", "" ));
//		data.add(new GWTRecCode(3, "ccbs", "根据自定义函数识别", "FunctionRecogniser", "com.dc.tes.txcode.FunctionRecogniser", "className=com.dc.tes.txcode.FunctionRecogniser", "funcName=Recogonise()", "", "", "", "" ));
//		data.add(new GWTRecCode(4, "ectip", "根据自定义脚本识别", "ScriptRecogniser", "com.dc.tes.txcode.ScriptRecogniser", "imports=''", "code=''", "", "", "", "" ));
		
		return data;
		
	}
	
	@Override
	public GWTComponent GetSingleObjectById(int id){
		List<GWTComponent> data = GetTestData();
		for(GWTComponent code : data){
			int aid = code.<Integer>get(N_ComponentId);
			if(aid == id)
				return code;
		}
		
		return null;
	}

}
