package com.dc.tes.data.model;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.dc.tes.data.model.tag.BeanIdName;

/**
 * MsgPacker:拆、组包 JavaBean映射类
 * 
 * @author huangzx
 *
 */

@BeanIdName("id")
public class MsgPacker implements Serializable{

	private static final long serialVersionUID = -8010818698988373959L;
	
	private String id;	// id
	private String stylename; // 样式名称
	private int type;	// 拆组包标示:0组包;1拆包
	private String messagetype;	//报文类型: XML\8583\定长等
	private String classname; // 拆组包类名
	private String content; // 拆组包样式内容
	
	private Set<Channel> packchannel = new HashSet<Channel>(0);	//组包 关联的channel集合
	private Set<Channel> unpackchannel = new HashSet<Channel>(0);	//拆包 关联的channel集合
	
	private String createdUserId;
	private Date createdTime; 
	private Date lastModifiedTime;
	private String lastModifiedUserId;
	
	/**
	 * 	获取 组包 关联的channel集合
	 * @return 组包 关联的channel集合
	 */
	public Set<Channel> getPackchannel() {
		return packchannel;
	}

	/**
	 * 设置 组包 关联的channel集合
	 * @param packchannel 组包 关联的channel集合
	 */
	public void setPackchannel(Set<Channel> packchannel) {
		this.packchannel = packchannel;
	}

	/**
	 * 获取 拆包 关联的channel集合
	 * @return 拆包 关联的channel集合
	 */
	public Set<Channel> getUnpackchannel() {
		return unpackchannel;
	}

	/**
	 * 设置 拆包 关联的channel集合
	 * @param unpackchannel 拆包 关联的channel集合
	 */
	public void setUnpackchannel(Set<Channel> unpackchannel) {
		this.unpackchannel = unpackchannel;
	}

	/**
	 * 获取  ID
	 * @return ID
	 */
	public String getId() {
		return id;
	}
	
	/**
	 * 设置 ID
	 * @param id ID
	 */
	public void setId(String id) {
		this.id = id;
	}
	
	/**
	 * 获取 样式名称
	 * @return 样式名称
	 */
	public String getStylename() {
		return stylename;
	}
	
	/**
	 * 设置 样式名称
	 * @param stylename 样式名称
	 */
	public void setStylename(String stylename) {
		this.stylename = stylename;
	}
	
	/**
	 * 获取 拆组包标示
	 * @return 0组包;1拆包
	 */
	public int getType() {
		return type;
	}
	
	/**
	 * 设置 拆组包标示
	 * @param type 0组包;1拆包
	 */
	public void setType(int type) {
		this.type = type;
	}
	
	/**
	 * 获取 报文类型
	 * @return XML\8583\定长等
	 */
	public String getMessagetype() {
		return messagetype;
	}
	
	/**
	 * 设置 报文类型
	 * @param messagetype XML\8583\定长等
	 */
	public void setMessagetype(String messagetype) {
		this.messagetype = messagetype;
	}
	
	/**
	 * 获取 拆组包类名
	 * @return 拆组包类名
	 */
	public String getClassname() {
		return classname;
	}
	
	/**
	 * 设置 拆组包类名
	 * @param classname 拆组包类名
	 */
	public void setClassname(String classname) {
		this.classname = classname;
	}
	
	/**
	 * 获取 拆组包样式内容
	 * @return 拆组包样式内容
	 */
	public String getContent() {
		return content;
	}
	
	/**
	 * 设置 拆组包样式内容
	 * @param content 拆组包样式内容
	 */
	public void setContent(String content) {
		this.content = content;
	}

	/**
	 * @param createdUserId the createdUserId to set
	 */
	public void setCreatedUserId(String createdUserId) {
		this.createdUserId = createdUserId;
	}

	/**
	 * @return the createdUserId
	 */
	public String getCreatedUserId() {
		return createdUserId;
	}

	/**
	 * @param createdTime the createdTime to set
	 */
	public void setCreatedTime(Date createdTime) {
		this.createdTime = createdTime;
	}

	/**
	 * @return the createdTime
	 */
	public Date getCreatedTime() {
		return createdTime;
	}

	/**
	 * @param lastModifiedTime the lastModifiedTime to set
	 */
	public void setLastModifiedTime(Date lastModifiedTime) {
		this.lastModifiedTime = lastModifiedTime;
	}

	/**
	 * @return the lastModifiedTime
	 */
	public Date getLastModifiedTime() {
		return lastModifiedTime;
	}

	/**
	 * @param lastModifiedUserId the lastModifiedUserId to set
	 */
	public void setLastModifiedUserId(String lastModifiedUserId) {
		this.lastModifiedUserId = lastModifiedUserId;
	}

	/**
	 * @return the lastModifiedUserId
	 */
	public String getLastModifiedUserId() {
		return lastModifiedUserId;
	}
	
	
}




