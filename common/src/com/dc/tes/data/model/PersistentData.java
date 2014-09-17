package com.dc.tes.data.model;

import java.io.Serializable;

import com.dc.tes.data.model.tag.BeanIdName;

/**
 * PERSISTENTDATA: 持久化数据 JavaBean映射类
 * 
 * 
 * @author huangzx
 * 
 */
@BeanIdName("id")
public class PersistentData implements Serializable {

	private static final long serialVersionUID = 1L;

	private String id; // 持久化数据表 ID
	private String systemid; // 关联的系统 ID
	private String parameter; // 参数名称
	private String curvalue; // 当前值
	private int type; // 参数类型

	public PersistentData() {

	}

	public PersistentData(String id, String systemid, String parameter, String curvalue, int type) {
		this.id = id;
		this.systemid = systemid;
		this.parameter = parameter;
		this.curvalue = curvalue;
		this.type = type;
	}

	/**
	 * 获取 持久化数据 ID
	 * 
	 * @return ID
	 */
	public String getId() {
		return id;
	}

	/**
	 * 设置 持久化数据 ID,一般由 Hibernate完成
	 * 
	 * @param id
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * 获取 关联系统ID
	 * 
	 * @return 系统ID
	 */
	public String getSystemid() {
		return systemid;
	}

	/**
	 * 设置 关联系统ID
	 * 
	 * @param systemid
	 *            ：系统ID
	 */
	public void setSystemid(String systemid) {
		this.systemid = systemid;
	}

	/**
	 * 获取 参数名称
	 * 
	 * @return 参数名称
	 */
	public String getParameter() {
		return parameter;
	}

	/**
	 * 设置 参数名称
	 * 
	 * @param parameter
	 *            ：参数名称
	 */
	public void setParameter(String parameter) {
		this.parameter = parameter;
	}

	/**
	 * 获取 当前值
	 * 
	 * @return 当前值
	 */
	public String getCurvalue() {
		return curvalue;
	}

	/**
	 * 设置 当前值
	 * 
	 * @param curvalue
	 *            ：当前值
	 */
	public void setCurvalue(String curvalue) {
		this.curvalue = curvalue;
	}

	/**
	 * 获取 参数类型
	 * 
	 * @return 参数类型 0——String(字符) 、 1——Number(数字)
	 */
	public int getType() {
		return type;
	}

	/**
	 * 设置 参数类型
	 * 
	 * @param type
	 *            ：参数类型 0——String(字符) 、 1——Number(数字)
	 */
	public void setType(int type) {
		this.type = type;
	}
}
