package com.dc.tes.data.model;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.dc.tes.data.model.tag.BeanIdName;

/**
 * 
 * Adapter:适配器 JavaBean映射类
 * 
 * @author huangzx
 *
 */
@BeanIdName("id")
public class Adapter implements Serializable{

	private static final long serialVersionUID = -8489954812240487262L;

	private String id;	// Id
	private String protocoltype;	//协议类型  例如: HTTP TCP 
	private String description;	//描述信息
	private int cstype;	//适配器类型  0:发起方适配器、1:接收方适配器
	private String pluginname;	//适配器插件名称
	private String cfginfo;	//适配器配置信息,配置模版
	private String createdUserId;
	private Date createdTime; 
	private Date lastModifiedTime;
	private String lastModifiedUserId;
	
	
	private Set<Channel> channel = new HashSet<Channel>(0);	//适配器关联的 channel 集合
	
	/**
	 * 	获取  适配器关联的 channel 集合
	 * @return 适配器关联的 channel 集合
	 */
	public Set<Channel> getChannel() {
		return channel;
	}

	/**
	 * 设置 适配器关联的 channel 集合
	 * @param channel 适配器关联的 channel 集合
	 */
	public void setChannel(Set<Channel> channel) {
		this.channel = channel;
	}

	/**
	 * 获取 适配器ID
	 * @return 适配器ID
	 */
	public String getId() {
		return id;
	}
	
	/**
	 * 设置 适配器ID
	 * @param id 适配器ID
	 */
	public void setId(String id) {
		this.id = id;
	}
	
	/**
	 * 获取 协议类型
	 * @return HTTP、TCP等
	 */
	public String getProtocoltype() {
		return protocoltype;
	}
	
	/**
	 * 设置 协议类型
	 * @param protocoltype HTTP、TCP等
	 */
	public void setProtocoltype(String protocoltype) {
		this.protocoltype = protocoltype;
	}
	
	/**
	 * 获取 描述信息
	 * @return 描述信息
	 */
	public String getDescription() {
		return description;
	}
	
	/**
	 * 设置  描述信息
	 * @param description  描述信息
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	
	/**
	 * 获取 适配器类型
	 * @return  0:发起方适配器、1:接收方适配器
	 */
	public int getCstype() {
		return cstype;
	}
	
	/**
	 * 设置 适配器类型
	 * @param cstype 0:发起方适配器、1:接收方适配器
	 */
	public void setCstype(int cstype) {
		this.cstype = cstype;
	}
	
	/**
	 * 获取 适配器插件名称
	 * @return 适配器插件名称
	 */
	public String getPluginname() {
		return pluginname;
	}
	
	/**
	 * 设置 适配器插件名称
	 * @param pluginname 适配器插件名称
	 */
	public void setPluginname(String pluginname) {
		this.pluginname = pluginname;
	}
	
	/**
	 * 获取 适配器配置信息,配置模版
	 * @return  适配器配置信息,配置模版
	 */
	public String getCfginfo() {
		return cfginfo;
	}
	
	/**
	 * 设置 适配器配置信息,配置模版
	 * @param cfginfo 适配器配置信息,配置模版
	 */
	public void setCfginfo(String cfginfo) {
		this.cfginfo = cfginfo;
	}

	public void setCreatedUserId(String createdUserId) {
		this.createdUserId = createdUserId;
	}

	public String getCreatedUserId() {
		return createdUserId;
	}

	public void setCreatedTime(Date createdTime) {
		this.createdTime = createdTime;
	}

	public Date getCreatedTime() {
		return createdTime;
	}

	public void setLastModifiedTime(Date lastModifiedTime) {
		this.lastModifiedTime = lastModifiedTime;
	}

	public Date getLastModifiedTime() {
		return lastModifiedTime;
	}

	public void setLastModifiedUserId(String lastModifiedUserId) {
		this.lastModifiedUserId = lastModifiedUserId;
	}

	public String getLastModifiedUserId() {
		return lastModifiedUserId;
	}
	
	
}
