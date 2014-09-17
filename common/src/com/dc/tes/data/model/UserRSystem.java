package com.dc.tes.data.model;

import java.io.Serializable;

import com.dc.tes.data.model.tag.BeanIdName;

/**
 * UserRSystem: 用户与系统关联 bean
 * 
 * @author huangzx
 * 
 */
@BeanIdName("id")
public class UserRSystem implements Serializable {

	private static final long serialVersionUID = 1L;

	private String id;
	//private User userid;
	//private SysType systemid;
	private String userid;
	private String systemid;

	public UserRSystem() {

	}

	public UserRSystem(String id, String userid, String systemid) {
		this.id = id;
		this.userid = userid;
		this.systemid = systemid;
	}

	/**
	 * 获取关联表ID
	 * 
	 * @return 关联表ID
	 */
	public String getId() {
		return id;
	}

	/**
	 * 设置关联表Id (该字段由 hibernate来维护)
	 * 
	 * @param id
	 *            ：ID
	 */
	public void setId(String id) {
		this.id = id;
	}

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public String getSystemid() {
		return systemid;
	}

	public void setSystemid(String systemid) {
		this.systemid = systemid;
	}

	/**
	 * 获取用户bean
	 * 
	 * @return
	 */
	/*public User getUserid() {
		return userid;
	}*/

	/**
	 * 设置用户bean
	 * 
	 * @param userid
	 */
	/*public void setUserid(User userid) {
		this.userid = userid;
	}*/

	/**
	 * 获取系统bean
	 * 
	 * @return
	 */
	/*public SysType getSystemid() {
		return systemid;
	}*/

	/**
	 * 设置系统bean
	 * 
	 * @param systemid
	 */
	/*public void setSystemid(SysType systemid) {
		this.systemid = systemid;
	}*/

}
