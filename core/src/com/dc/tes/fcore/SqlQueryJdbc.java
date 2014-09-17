package com.dc.tes.fcore;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dc.tes.data.DALFactory;
import com.dc.tes.data.IDAL;
import com.dc.tes.data.model.DbHost;
import com.dc.tes.data.op.Op;



public class SqlQueryJdbc {

	private static Map<String, DbHost> m_dbhost = null; ;
	
	
	public static void getDbHostList() {
		
		IDAL<DbHost> hostDAL = DALFactory.GetBeanDAL(DbHost.class);
		List<DbHost> dbHostList = hostDAL.ListAll(Op.EQ("systemId", DbGet.m_sysType.getSystemId()));
		
		for(int i = 0; i < dbHostList.size(); i++) {
			DbHost host = dbHostList.get(i);
			m_dbhost.put(host.getIpaddress(), host);
		}		
	} 
	
	
	public static String getJdbcQueryResult(String strSql, String sIpAddr) {
		
		String strAllRows = "";
		
		if (m_dbhost == null) {
			m_dbhost = new HashMap<String, DbHost>();
			getDbHostList();
		}
		
		DbHost dbHost = m_dbhost.get(sIpAddr);
		if (dbHost == null || dbHost.getDbType() == null) {
			return "";
		}
		
		String sDbType = dbHost.getDbType().toString();
		
		try {
			String url = "";
			if (sDbType.equals("DB2")) {
				if (dbHost.getOsType().equals("AS400")) {
					java.sql.DriverManager.registerDriver(new com.ibm.as400.access.AS400JDBCDriver());
					url = "jdbc:" + "As400" + "://" + sIpAddr + ";naming=sql;errors=full";
				}
				else if (dbHost.getOsType().equals("RS6000")) {
					java.sql.DriverManager.registerDriver(new com.ibm.db2.jcc.DB2Driver());
					url = "jdbc:" + "db2" + "://" + sIpAddr + ":" + dbHost.getPortnum() + "/" + dbHost.getDbName();
				}
			}
			else if (sDbType.equals("MYSQL")) {
				Class.forName("com.mysql.jdbc.Driver");
				url = "jdbc:mysql://" + sIpAddr + ":" + dbHost.getPortnum() + "/" + dbHost.getDbName() + "?useUnicode=true&characterEncoding=utf8";
			}
			else if (sDbType.equals("ORACLE")) {
			}
			else if (sDbType.equals("SQLSERVER")) {
			}
			
			Connection conn = DriverManager.getConnection(url, dbHost.getDbUser(), dbHost.getDbPwd());
			PreparedStatement stmt = conn.prepareStatement(strSql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

			ResultSet rs = stmt.executeQuery();
					
			int colCount = rs.getMetaData().getColumnCount();
			int rowCount = 0;
			
			try {
				rs.last();
				rowCount = rs.getRow();
			} 
			catch (Exception e) {
				e.printStackTrace();
			}
			
			if (rowCount <=0 ) {
				return null;
			}

			rs.first();
			String strOneRow = "";
			if (rowCount >= 1) { //第一行
				strOneRow += rs.getString(1); // 第一列
				if (colCount > 1) { // 有多列
					for (int j = 2; j <= colCount; j++) {
						strOneRow += ("|" + rs.getString(j));
					}
				}
				strAllRows = strOneRow;
			} 
			if (rowCount > 1) { //有多行
				strAllRows = ("{" + strOneRow + "}");
				while (rs.next()) {
					strOneRow = "";
					strOneRow += rs.getString(1); // 第一列
					if (colCount > 1) { // 有多列
						for (int j = 2; j <= colCount; j++) {
							strOneRow += ("|" + rs.getString(j));
						}
					}
					strAllRows += ("{" + strOneRow + "}");
				}
			}
			
			stmt.close();
			conn.close();
		} catch (SQLException ex) {
			ex.printStackTrace();
		} catch (java.lang.Exception ex) {
			ex.printStackTrace();
		}
		
		return strAllRows;
	}


	public static void querySql() {
		String sql = "SELECT CLT_ORG_NUM FROM COR.M3PRINDB WHERE CLT_ORG_NUM='7556411347'";
		String sIpAddr = "99.8.46.200"; 
		String res = getJdbcQueryResult(sql, sIpAddr);
		System.out.println(res);
	}
	
}
