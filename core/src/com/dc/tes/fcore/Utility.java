package com.dc.tes.fcore;

import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class Utility {
	
	public static String FormatDuration2HHMMSS(long iRunSeconds) {
			
		long hour = (iRunSeconds / 3600);
		long min = (iRunSeconds / 60 - hour * 60);
		long sec = (iRunSeconds - hour * 3600 - min * 60);
	
		String strDuration = "";
		
		if (hour > 0) {
			strDuration = hour + "小时" + min + "分" + sec + "秒";
		} else if (min > 0) {
			strDuration = min + "分" + sec + "秒";
		} else {
			strDuration = sec + "秒";
		}
		
		return strDuration;
	}
	
	
	public static boolean isDigitalExpression(String inStr) {
		
		if (!inStr.contains("+") && !inStr.contains("-") && !inStr.contains("*") && !inStr.contains("/")) {
			return false;
		}
		
		return false;
	}

	public static boolean isDigitalExpressionString(String inStr) {
			
		if (inStr == null || inStr.isEmpty()) {
			return false;
		}
		inStr = inStr.trim();
		if (inStr == null || inStr.isEmpty()) {
			return false;
		}
		
		if (!inStr.contains("+") && !inStr.contains("-") && !inStr.contains("*") && !inStr.contains("/")) {
			return false;
		}

		char[] chInStr = inStr.toCharArray();
		for (int i=0; i < chInStr.length; i++) {
			if (!((chInStr[i] >= '0' && chInStr[i] <= '9') || chInStr[i] == '+' || chInStr[i] == '-' || chInStr[i] == '*' || chInStr[i] == '/' || chInStr[i] == '.' || chInStr[i] == ' ')) {
				return false;
			}
		}
		return true;
	}
	
	public static boolean isDigitString(String inStr) {
			
		if (inStr == null || inStr.isEmpty()) {
			return false;
		}
		inStr = inStr.trim();
		if (inStr == null || inStr.isEmpty()) {
			return false;
		}
		
		char[] chInStr = inStr.toCharArray();
		for (int i=0; i < chInStr.length; i++) {
			if (chInStr[i] < '0' || chInStr[i] > '9') {
				return false;
			}
		}
		return true;
	}
	
	public static boolean isDigitChar(char ch) {
		
		if (ch < '0' || ch > '9') {
			return false;
		}
		else {
			return true;
		}
	}

	public static byte[] subbyte(byte[] in_byte, int iFrom, int iLength) {
		
		byte[] out_byte = new byte[iLength];

		for (int i=0; i<iLength; i++) {
			out_byte[i] = in_byte[iFrom + i];
		}

		return out_byte;
	}

	public static String getXmlEncoding(String strMsg) {
		int iPosOfEncoding = strMsg.indexOf("encoding");
		String enCodingStr = strMsg.substring(iPosOfEncoding + "encoding".length());
		int iPosOfQuotationMark1  = enCodingStr.indexOf("\"");
		enCodingStr = enCodingStr.substring(iPosOfQuotationMark1 + 1);
		int iPosOfQuotationMark2  = enCodingStr.indexOf("\"");
		String encoding = enCodingStr.substring(0, iPosOfQuotationMark2);
		return encoding;
	}
	
	
	public static String getWKEMsgId(byte[] inMsg) {
		
		//报文头[Header]长为205
		if (inMsg.length <= 205) {
			return null;
		}
		//取报文头
		byte[] inMsgHeaderBytes = Utility.subbyte(inMsg, 0, 205);
		if (inMsgHeaderBytes == null) {
			return null;
		}
		//取接口个数
		byte[] inIntfCountBytes = Utility.subbyte(inMsgHeaderBytes, 203, 2);
		if (inIntfCountBytes == null) {
			return null;
		}
		String strJobIntfCount = new String(inIntfCountBytes);
		
		int iJobIntfCount = 0;
		try {
			iJobIntfCount = Integer.parseInt(strJobIntfCount);
		}
		catch(Exception e) {
			e.printStackTrace();
			return null;
		}
		//作业接口定义部分
		byte[] JobIntfByte = Utility.subbyte(inMsg, 205, iJobIntfCount * 20);
		if (JobIntfByte == null) {
			return null;
		}
		byte[] JobContentByte = Utility.subbyte(inMsg, 205 + iJobIntfCount * 20, inMsg.length - 205 - iJobIntfCount * 20);
		if (JobContentByte == null) {
			return null;
		}

		//编码方式
		String responseMsgEncoding = DbGet.m_sysType.getEncoding4ResponseMsg();
		
		//KPMSGHDRX.Len = 235
		int iKPMSGHDRX = 0, iKPMSGHDRXLen = 235;
		for (int i = 0; i<iJobIntfCount; i++) {//跳过“KPMSGHDRX”之前的所有接口定义部分
			byte[] OneJobIntfByte = Utility.subbyte(inMsg, 205 + i * 20, 20);
			/*String strOneJobIntf = "";
			try {
				strOneJobIntf = new String(OneJobIntfByte, responseMsgEncoding);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}*/
			byte[] FMTCOD_Byte = Utility.subbyte(OneJobIntfByte, 0, 10);
			byte[] RECCNT_Byte = Utility.subbyte(OneJobIntfByte, 10, 4);
			byte[] RECLEN_Byte = Utility.subbyte(OneJobIntfByte, 14, 6);
			
			String FMTCOD_Name = "";
			try {
				FMTCOD_Name = new String(FMTCOD_Byte, responseMsgEncoding);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			if (FMTCOD_Name != null) {
				FMTCOD_Name = FMTCOD_Name.trim();
			}
			if (FMTCOD_Name.compareTo("KPMSGHDRX") == 0) {
				break;
			}
			
			String strRecordCount = "";
			try {
				strRecordCount = new String(RECCNT_Byte, responseMsgEncoding);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			String strRecordLength = "";
			try {
				strRecordLength = new String(RECLEN_Byte, responseMsgEncoding);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			int iRecordCount = 0;
			try {
				iRecordCount = Integer.parseInt(strRecordCount);
			}
			catch(Exception e) {
				e.printStackTrace();
				return null;
			}
			int iRecordLength = 0;
			try {
				iRecordLength = Integer.parseInt(strRecordLength);
			}
			catch(Exception e) {
				e.printStackTrace();
				return null;
			}
			iKPMSGHDRX += iRecordLength * iRecordCount;
		}
		if (JobContentByte.length < iKPMSGHDRX + iKPMSGHDRXLen - 1) {
			return null;
		}
		//iKPMSGHDRX为接口“KPMSGHDRX”的起始位置
		byte[] JobKPMSGHDRXByte = Utility.subbyte(JobContentByte, iKPMSGHDRX, iKPMSGHDRXLen);
		/*String strJobKPMSGHDRX = "";
		try {
			strJobKPMSGHDRX = new String(JobKPMSGHDRXByte, responseMsgEncoding);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}*/
		
		byte[] xCmmIdnByte = Utility.subbyte(JobKPMSGHDRXByte, 75, 20);
		String xCmmIdn = "";
		try {
			xCmmIdn = new String(xCmmIdnByte, responseMsgEncoding);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		return xCmmIdn;
	}

	  
	public static String GetLocalIPAddress()
	{ 
		InetAddress addr;
		try {
			addr = InetAddress.getLocalHost();
			String ip = addr.getHostAddress().toString();//获得本机IP
			//String address = addr.getHostName().toString();//获得本机名称
			return ip;
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static String GetLocalHostName()
	{ 
		InetAddress addr;
		try {
			addr = InetAddress.getLocalHost();
			//String ip = addr.getHostAddress().toString();//获得本机IP
			String hostName = addr.getHostName().toString();//获得本机名称
			return hostName;
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		return null;
	}
	
    public static boolean isValidDate(String strDate)
    {
        if (strDate == null || strDate.trim() == "")
        {
            return false;
        }
        try
        {
        	SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        	Date date = sdf.parse(strDate);
        }
        catch (Exception e)
        {
            return false;
        }
        return true;
    }
    
    
	public static void main(String[] args) {

		try {

			boolean isDigit = isDigitString("2");

		} catch (Exception ex) {
			System.out.println(ex.getMessage());

		}
	}
	
	
}
