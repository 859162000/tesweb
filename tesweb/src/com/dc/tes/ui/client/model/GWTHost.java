package com.dc.tes.ui.client.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import com.extjs.gxt.ui.client.data.BaseModelData;

public class GWTHost extends BaseModelData implements Serializable,
		IDistValidate {

	private static final long serialVersionUID = 1820111209625071921L;

	public static String N_Hostid = "hostid";
	public static String N_DbHost = "dbHostName"; // 主机名
	public static String N_Ipaddress = "ipaddress"; // ip地址
	public static String N_Portnum = "portnum"; // 端口号
	public static String N_Description = "description"; // 说明
	public static String N_SystemId = "systemId";
	public static String N_IsLongConn = "isLongConn"; // 是否为长连接
	public static String N_IsLongConnStr = "isLongConnStr";
	public static String N_DbType = "dbType";
	public static String N_DbName = "dbName";
	public static String N_DbUser = "dbUser";
	public static String N_DbPwd = "dbPwd";
	public static String N_OsType = "osType";

	public static String N_CreatedUserId = "createdUserId";
	public static String N_CreatedTime = "createdTime";
	public static String N_LastModifiedTime = "lastModifiedTime";
	public static String N_LastModifiedUserId = "lastModifiedUserId";

	public GWTHost() {

	}

	public GWTHost(String hostid) {
		this(hostid, "", "", "", "", "", 0, "", "", "", "", "");
	}

	public GWTHost(String hostid, String systemid) {
		this(hostid, "", "", "", systemid, "", 0, "", "", "", "", "");
	}

	public GWTHost(String hostid, String cmbhost, String ipaddress,
			String portnum, String systemid, String description,
			int isLongConn, String dbType, String dbName, String dbUser,
			String dbPwd, String osType) {
		
		this.set(N_Hostid, hostid);
		this.set(N_SystemId, systemid);
		this.SetValue(cmbhost, ipaddress, portnum, description, isLongConn,
				dbType, dbName, dbUser, dbPwd, osType);
	}

	public void SetValue(String cmbhost, String ipaddress, String portnum,
			String description, int isLongConn, String dbType, String dbName,
			String dbUser, String dbPwd, String osType) {
		this.set(N_DbHost, cmbhost);
		this.set(N_Ipaddress, ipaddress);
		this.set(N_Portnum, portnum);
		this.set(N_Description, description == null ? "" : description);
		this.set(N_IsLongConn, isLongConn);
		this.set(N_IsLongConnStr, isLongConn == 1 ? "是" : "否");
		this.set(N_DbType, dbType);
		this.set(N_DbName, dbName);
		this.set(N_DbUser, dbUser);
		this.set(N_DbPwd, dbPwd);
		this.set(N_OsType, osType);
	}

	public GWTHost(String hostid, String ipaddress, String port) {
		this(hostid, "", ipaddress, port, "", "", 0, "", "", "", "", "");
	}

	public String getID() {
		return this.get(N_Hostid).toString();
	}

	public boolean IsNew() {
		return getID().isEmpty();
	}

	public String getDbHost() {
		return this.get(N_DbHost).toString();
	}

	public String getOsType() {
		return this.get(N_OsType);
	}

	public String getIpAddress() {
		return this.get(N_Ipaddress).toString();
	}

	public String getPortnum() {
		return this.get(N_Portnum).toString();
	}

	public String getSystemID() {
		return this.get(N_SystemId).toString();
	}

	public String getDescription() {
		return this.get(N_Description).toString();
	}

	public int getIsLongConn() {
		return Integer.parseInt(get(N_IsLongConn).toString());
	}

	public String getIsLongConnStr() {
		return getIsLongConn() == 1 ? "是" : "否";
	}

	public String getDbType() {
		return get(N_DbType);
	}

	public String getDbName() {
		return get(N_DbName);
	}

	public String getDbUser() {
		return get(N_DbUser);
	}

	public String getDbPwd() {
		return get(N_DbPwd);
	}

	public void SetCreatedUserId(String createdUserId) {
		this.set(N_CreatedUserId, createdUserId);
	}

	public String GetCreatedUserId() {
		return this.get(N_CreatedUserId);
	}

	public void SetCreatedTime(String createdTime) {
		this.set(N_CreatedTime, createdTime);
	}

	public String GetCreatedTime() {
		return this.get(N_CreatedTime);
	}

	public void SetLastModifiedTime(String lastModifiedTime) {
		this.set(N_LastModifiedTime, lastModifiedTime);
	}

	public String GetLastModifiedTime() {
		return this.get(N_LastModifiedTime);
	}

	public void SetLastModifiedUserId(String lastModifiedUserId) {
		this.set(N_LastModifiedUserId, lastModifiedUserId);
	}

	public String GetLastModifiedUserId() {
		return this.get(N_LastModifiedUserId);
	}

	@Override
	public String GetTableName() {
		return "DbHost";
	}

	@Override
	public Map<String, Object> GetFieldValuePair(String validateValue) {

		Map<String, Object> fieldValuePair = new HashMap<String, Object>();
		fieldValuePair.put(N_SystemId, get(N_SystemId));
		fieldValuePair.put(N_DbHost, validateValue);
		return fieldValuePair;
	}

}
