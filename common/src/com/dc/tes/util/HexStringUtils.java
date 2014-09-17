package com.dc.tes.util;

import org.apache.commons.lang.ArrayUtils;

import com.dc.tes.exception.CommonErr;
import com.dc.tes.exception.TESException;

/**
 * 工具类 提供了将字节流和HEX字符串相互转化的工具函数
 * 
 * @author huangzx
 * 
 */
public class HexStringUtils {

	//以空格区分的16进制，如:B1 AC
	public static byte[] FromHexStringWithSpace(String str) {
		if (str == null || str.length() == 0)
			return ArrayUtils.EMPTY_BYTE_ARRAY;

		String[] byteStrings = str.split(" ");
		byte[] bytes = new byte[byteStrings.length];

		for (int i = 0; i < byteStrings.length; i++) {
			if (byteStrings[i].length() != 2)
				throw new TESException(CommonErr.IllegalHexString, str);
			try {
				bytes[i] = (byte) Integer.parseInt(byteStrings[i], 16);
			} catch (Exception ex) {
				throw new TESException(CommonErr.IllegalHexString, str);
			}
		}

		return bytes;
	}
	
    //不需要以空格区分的16进制，如：B1AC
	/**
	 * 从HEX字符串中解析出字节流
	 * 
	 * @param str
	 *            被解析的HEX字符串
	 * @return 被HEX字符串描述的字节流
	 */
	public static byte[] FromHexString(String str) {
		if (str == null || str.length() == 0)
			return ArrayUtils.EMPTY_BYTE_ARRAY;

		if(str.length()%2 != 0)
			throw new TESException(CommonErr.IllegalHexString, str);
			
		int n = str.length() / 2;
		byte[] bytes = new byte[n];
		
		for (int i = 0; i < n; i++) {
			try {
				bytes[i] = (byte) Integer.parseInt(str.substring(i*2, i*2+2), 16);
			} catch (Exception ex) {
				throw new TESException(CommonErr.IllegalHexString, str);
			}
		}

		return bytes;
	}
	

	/**
	 * 将字节流转换为HEX字符串形式
	 * 
	 * @param bytes
	 *            被解析的HEX字符串
	 * @return 描述给定字节流的被HEX字符串描述的字节流
	 */
	public static String ToHexString(byte[] bytes) {
		StringBuffer buffer = new StringBuffer();

		for (byte b : bytes) {
			int m = b >> 4 & 0x0F;
			int n = b & 0x0F;

			//buffer.append(' ');
			buffer.append((char) (m > 9 ? 'A' + m - 10 : '0' + m));
			buffer.append((char) (n > 9 ? 'A' + n - 10 : '0' + n));
		}
		//return buffer.substring(1);
		return buffer.toString();
	}
}
