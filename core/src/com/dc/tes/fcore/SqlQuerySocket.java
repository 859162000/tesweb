package com.dc.tes.fcore;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;

import com.dc.tes.data.DALFactory;
import com.dc.tes.data.IDAL;
import com.dc.tes.data.model.DbHost;
import com.dc.tes.data.op.Op;


/**
 * TCP Socket方式的SQL查询功能
 * 
 * @author Huangzx
 * 
 */
public class SqlQuerySocket {

	//SOCKET长连接<IP, Socket>
	private static Map<String, Socket> m_socket = new HashMap<String, Socket>();
	
	
	//查询400主机
	public static String get400QueryResult(Log log, String sSystemId, String strSql, String strHostIp, int iPortNum, StringBuilder sbRecCnt) {
	
		int iIsLongConnection = 1;
		IDAL<DbHost> hostDAL = DALFactory.GetBeanDAL(DbHost.class);
		DbHost dbHost = hostDAL.Get(Op.EQ("systemId", sSystemId), Op.EQ("ipaddress", strHostIp));
		if (dbHost != null) {
			iIsLongConnection = dbHost.getIsLongConn();
		}
		
		if (strSql == null || strSql.isEmpty())	{
			log.info("查询400主机信息时SQL语句不能为空");
			return null;
		}
		
		if (strHostIp == null || strHostIp.isEmpty())	{
			log.info("400主机IP不能为空");
			return null;
		}
		
		//isValidSqlCheck()
		log.info("要查询的SQL语句：" + strSql + " [HostIp: " + strHostIp + "]");
		
		final String str400CharSet = "GBK";
		final int LEN_OF_RECORD_BYTES = 4;
		final int LEN_OF_RECNUM_BYTES = 2;
		
		int iLen = strSql.length();
		String strLen = String.format("%04d", iLen);

		strSql = strLen + strSql;

		String strFieldData = "";
		
		//获取或者创建SOCKET长连接
		Socket socket = m_socket.get(strHostIp);
		if (socket == null || !socket.isConnected()) {
			try {
				socket = new Socket(strHostIp, iPortNum);
				m_socket.put(strHostIp, socket);
			} catch (UnknownHostException e) {
				log.error("创建到400主机socket连接出现未知异常！[主机IP："  + strHostIp + "]");
				e.printStackTrace();
			} catch (IOException e) {
				log.error("创建到400主机socket连接出现IO异常！[主机IP："  + strHostIp + "]");
				e.printStackTrace();
			}
			if (socket == null || !socket.isConnected()) {
				log.error("创建到400主机socket连接失败！[主机IP："  + strHostIp + "]");
				return null;
			}
		}

		//发送并接收和处理SOCKET报文数据
		try {
			byte[] byteSql = strSql.getBytes(str400CharSet);
			socket.getOutputStream().write(byteSql);
			InputStream in = socket.getInputStream();
	
			byte[] byte400 = new byte[4096];
			//考虑连接超时问题
			int iNum400 = in.read(byte400);
			if (iNum400 <= LEN_OF_RECORD_BYTES + LEN_OF_RECNUM_BYTES) {
				log.error("400查询结果为空");
				return null;
			}

			byte[] realByteFirstRead = new byte[iNum400];
			System.arraycopy(byte400,0,realByteFirstRead,0,iNum400); //去掉尾部的未知符号
			String strFirstRead = new String(realByteFirstRead, str400CharSet);
			String str400 = strFirstRead;
			//System.out.println(str400);
			
			String strPacketLen = str400.substring(0, LEN_OF_RECORD_BYTES);
			int iPacketLen = Integer.parseInt(strPacketLen);

			//继续读缓冲区
			byte[] byteNewRead = new byte[4096];
			while (iNum400 < iPacketLen + 4) {
				int iByteCount = 0;
				try {
					iByteCount = in.read(byteNewRead);
				} catch (Exception rbe) {
					rbe.printStackTrace();
				}
				if (iByteCount <= 0) {
					break;
				}
				iNum400 += iByteCount;
				byte[] realByteNewRead = new byte[iByteCount];
				System.arraycopy(byteNewRead, 0, realByteNewRead, 0, iByteCount);
				String strNewRead = new String(realByteNewRead, str400CharSet);
				str400 += strNewRead;
			}
			//最终的缓冲区报文
			String strPacket = str400.substring(LEN_OF_RECORD_BYTES);
			String strRecNum = strPacket.substring(0, LEN_OF_RECNUM_BYTES);
			int iRecNum = Integer.parseInt(strRecNum);
			sbRecCnt.append(String.valueOf(iRecNum));
			String strSqlResult = strPacket.substring(LEN_OF_RECNUM_BYTES);
			for (int i = 0; i < iRecNum; i++) {
				String strRecLen = strSqlResult.substring(0,
						LEN_OF_RECORD_BYTES);
				if (strRecLen == null || strRecLen.isEmpty()) {
					return null;
				}
				int iRecLen = 0;
				try {
					iRecLen = Integer.parseInt(strRecLen);
				} catch (Exception eip) {
					System.out.println("[" + strRecLen + "]转化为整数时出错了!");
					eip.printStackTrace();
					return strFieldData;
				}
				strSqlResult = strSqlResult.substring(LEN_OF_RECORD_BYTES);
				String strRecData = CHNSubstring(strSqlResult, iRecLen,
						"Unicode");
				// System.out.println(strRecData);
				String s1 = "{";
				String s2 = "}";
				if (strFieldData == null || strFieldData.isEmpty())
					strFieldData = s1;
				else
					strFieldData = strFieldData.concat(s1);
				strFieldData = strFieldData.concat(strRecData);
				strFieldData = strFieldData.concat(s2);
				if (i == iRecNum - 1) {
					break;
				}
				String strTemp = strSqlResult.substring(strRecData.length());
				strSqlResult = strTemp;
				if (strSqlResult == null || strSqlResult.trim().length() == 0
						|| strSqlResult.isEmpty()) {
					System.out.println("主机下传数据不完整");
				}
			}
		} 
		catch (Exception e) {
			log.error("连接400进行TCP查询出错，IP地址：" + strHostIp);
			e.printStackTrace();
		}
		finally {
			if (0 == iIsLongConnection) { //短连接
				if (socket != null) {
					try {
						socket.close();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				socket = null;
			}
		}

		return strFieldData;
	}
	
	
	public static String CHNSubstring(String s, int length, String strCharSet) throws Exception {
		byte[] bytes = s.getBytes(strCharSet); //("Unicode");
		int n = 0; // 表示当前的字节数
		int i = 2; // 要截取的字节数，从第3个字节开始
		for (; i < bytes.length && n < length; i++) {
			// 奇数位置，如3、5、7等，为UCS2编码中两个字节的第二个字节
			if (i % 2 == 1) {
				n++; // 在UCS2第二个字节时n加1
			} else {
				// 当UCS2编码的第一个字节不等于0时，该UCS2字符为汉字，一个汉字算两个字节
				if (bytes[i] != 0) {
					n++;
				}
			}
		}
		// 如果i为奇数时，处理成偶数
		if (i % 2 == 1) {
			// 该UCS2字符是汉字时，去掉这个截一半的汉字
			if (bytes[i - 1] != 0)
				i = i - 1;
			// 该UCS2字符是字母或数字，则保留该字符
			else
				i = i + 1;
		}
		return new String(bytes, 0, i, strCharSet);
	}
	
}
