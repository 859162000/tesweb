package com.dc.tes.ui.client.model;


import com.extjs.gxt.ui.client.data.BaseModelData;
import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * 存储服务端配置信息
 * @author scckobe
 *
 */
public class GWTProperties extends BaseModelData implements IsSerializable{
	private static final long serialVersionUID = -8831661058814942764L;
	
	public GWTProperties(){
	}
	
	public GWTProperties(Object key,Object value)
	{
		this.set("Key", key);
		this.set("Value", value);
	}
	
	public Object GetKey()
	{
		return get("Key");
	}
	
	public Object GetValue()
	{
		return get("Value");
	}
}
