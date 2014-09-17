package com.dc.tes.data.model;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.dc.tes.data.model.tag.BeanIdName;

/**
 * 
 * TransRecognizer:交易识别 JavaBean映射类
 * 
 * @author huangzx
 *
 */
@BeanIdName("id")
public class TransRecognizer implements Serializable{

	private static final long serialVersionUID = 7479579364057989266L;

	private String id;	//id
	private String name; //名称
	private String description; //描述
	private String type; // 交易识别类型:固定位置;边界;正则;函数;脚本
	private String classname; // 交易识别类名
//	private String parameter1; // 参数1  
//	private String parameter2; // 参数2
//	private String parameter3; // 参数3
//	private String parameter4; // 参数4
//	private String parameter5; // 参数5  大字段
//	private String parameter6; // 参数6  大字段
	
	private String cfginfo;	//交易识别配置信息,配置模版
	
	private Set<Channel> channel = new HashSet<Channel>(0);	//交易识别 关联的 channel 集合
	
	private String createdUserId;
	private Date createdTime; 
	private Date lastModifiedTime;
	private String lastModifiedUserId;
	
	/**
	 * 	获取 交易识别 关联的 channel 集合
	 * @return 交易识别 关联的 channel 集合
	 */
	public Set<Channel> getChannel() {
		return channel;
	}

	/**
	 * 设置 交易识别 关联的 channel 集合
	 * @param channel 交易识别 关联的 channel 集合
	 */
	public void setChannel(Set<Channel> channel) {
		this.channel = channel;
	}

	/**
	 * 获取 交易识别ID
	 * @return  交易识别ID
	 */
	public String getId() {
		return id;
	}
	
	/**
	 * 设置 交易识别ID
	 * @param id 交易识别ID
	 */
	public void setId(String id) {
		this.id = id;
	}
	
	/**
	 * 获取 名称
	 * @return 名称
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * 设置 名称
	 * @param name 名称
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * 获取 描述信息
	 * @return 描述信息
	 */
	public String getDescription() {
		return description;
	}
	
	/**
	 * 设置 描述信息
	 * @param description 描述信息
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	
	/**
	 * 获取 交易识别类型
	 * @return 固定位置;边界;正则;函数;脚本
	 */
	public String getType() {
		return type;
	}
	
	/**
	 * 设置 交易识别类型
	 * @param type 固定位置;边界;正则;函数;脚本
	 */
	public void setType(String type) {
		this.type = type;
	}
	
	/**
	 * 获取 交易识别类名
	 * @return 交易识别类名
	 */
	public String getClassname() {
		return classname;
	}
	
	/**
	 * 设置 交易识别类名
	 * @param classname 交易识别类名
	 */
	public void setClassname(String classname) {
		this.classname = classname;
	}

	/**
	 * 获取 交易识别配置信息,配置模版
	 * @return 交易识别配置信息,配置模版
	 */
	public String getCfginfo() {
		return cfginfo;
	}

	/**
	 * 设置 交易识别配置信息,配置模版
	 * @param cfginfo 交易识别配置信息,配置模版
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
	
	/**
	 * 获取 参数1
	 * @return 参数1
	 */
//	public String getParameter1() {
//		return parameter1;
//	}
	
	/**
	 * 设置 参数1
	 * @param parameter1 参数1
	 */
//	public void setParameter1(String parameter1) {
//		this.parameter1 = parameter1;
//	}
	
	/**
	 * 获取 参数2
	 * @return 参数2
	 */
//	public String getParameter2() {
//		return parameter2;
//	}
	
	/**
	 * 设置 参数2
	 * @param parameter2 参数2
	 */
//	public void setParameter2(String parameter2) {
//		this.parameter2 = parameter2;
//	}
	
	/**
	 * 获取 参数3
	 * @return 参数3
	 */
//	public String getParameter3() {
//		return parameter3;
//	}
	
	/**
	 * 设置 参数3
	 * @param parameter3 参数3
	 */
//	public void setParameter3(String parameter3) {
//		this.parameter3 = parameter3;
//	}
	
	/**
	 * 获取 参数4
	 * @return 参数4
	 */
//	public String getParameter4() {
//		return parameter4;
//	}
	
	/**
	 * 设置 参数4
	 * @param parameter4 参数4
	 */
//	public void setParameter4(String parameter4) {
//		this.parameter4 = parameter4;
//	}
	
	/**
	 * 获取 参数5
	 * @return 参数5 大字段
	 */
//	public String getParameter5() {
//		return parameter5;
//	}
	
	/**
	 * 设置 参数5
	 * @param parameter5 参数5 大字段
	 */
//	public void setParameter5(String parameter5) {
//		this.parameter5 = parameter5;
//	}
	
	/**
	 * 获取 参数6
	 * @return 参数6 大字段
	 */
//	public String getParameter6() {
//		return parameter6;
//	}
	
	/**
	 * 设置 参数6
	 * @param parameter6 参数6 大字段
	 */
//	public void setParameter6(String parameter6) {
//		this.parameter6 = parameter6;
//	}
	
	
	
}
