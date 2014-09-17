package com.dc.tes.util;

import org.apache.commons.lang.StringUtils;

/**
 * 将各种Java数据类型与字节数组进行互相转换的工具类
 * 
 * @author lujs, huangzx
 * 
 */
public final class PackUtils {
	////////////////////
	// int8
	////////////////////

	/**
	 * 从字节数组中读取一个8位的整数
	 * 
	 * @param buff
	 *            字节数组
	 * @param current
	 *            当前位置
	 * @return 读取出的整数
	 */
	public static short ReadInt8(byte[] buff, int current) {
		return buff[current];
	}

	/**
	 * 将一个8位的整数写成字节数组
	 * 
	 * @param value
	 *            8位整数
	 * @return 保存该整数的字节数组
	 */
	public static byte[] WriteInt8(int value) {
		return new byte[] { (byte) value };
	}

	/**
	 * 将一个8位的int写成字节数组
	 * 
	 * @param value
	 *            8位整数
	 * @return 保存该整数的字节数组
	 */
	public static byte[] WriteInt8(short value) {
		return new byte[] { (byte) value };
	}

	/**
	 * 将一个8位的int写成字节数组
	 * 
	 * @param value
	 *            8位整数
	 * @return 保存该整数的字节数组
	 */
	public static byte[] WriteInt8(byte value) {
		return new byte[] { value };
	}

	////////////////////
	// int16
	////////////////////
	/**
	 * 从字节数组中读取一个16位的整数
	 * 
	 * @param buff
	 *            字节数组
	 * @param current
	 *            当前位置
	 * @param endian
	 *            字节序 {true}为big endian {false}为small endian
	 * @return 读取出的整数
	 */
	public static short ReadInt16(byte[] buff, int current, boolean endian) {
		int b1 = buff[current];
		int b2 = buff[current + 1];

		if (b1 < 0)
			b1 += 0x100;
		if (b2 < 0)
			b2 += 0x100;

		if (endian)
			return (short) ((b1 << 8) + b2);
		else
			return (short) ((b2 << 8) + b1);
	}

	/**
	 * 将一个16位的整数写成字节数组
	 * 
	 * @param value
	 *            16位整数
	 * @param endian
	 *            字节序 {true}为big endian {false}为small endian
	 * @return 保存该整数的字节数组
	 */
	public static byte[] WriteInt16(int value, boolean endian) {
		short s = (short) value;
		return WriteInt16(s, endian);
	}

	/**
	 * 将一个16位的整数写成字节数组
	 * 
	 * @param value
	 *            16位整数
	 * @param endian
	 *            字节序 {true}为big endian {false}为small endian
	 * @return 保存该整数的字节数组
	 */
	public static byte[] WriteInt16(short value, boolean endian) {
		byte[] buff = new byte[2];
		byte b1 = (byte) (value >> 8);
		byte b2 = (byte) (value & 0xFF);
		if (endian) {
			buff[0] = b1;
			buff[1] = b2;
		} else {
			buff[0] = b2;
			buff[1] = b1;
		}

		return buff;
	}

	////////////////////
	// int24
	////////////////////
	/**
	 * 从字节数组中读取一个24位的整数
	 * 
	 * @param buff
	 *            字节数组
	 * @param current
	 *            当前位置
	 * @param endian
	 *            字节序 {true}为big endian {false}为small endian
	 * @return 读取出的整数
	 */
	public static int ReadInt24(byte[] buff, int current, boolean endian) {
		int b1 = buff[current];
		int b2 = buff[current + 1];
		int b3 = buff[current + 2];

		if (b1 < 0)
			b1 += 0x100;
		if (b2 < 0)
			b2 += 0x100;
		if (b3 < 0)
			b3 += 0x100;

		if (endian)
			return (b1 << 16) + (b2 << 8) + b3;
		else
			return (b3 << 16) + (b2 << 8) + b1;
	}

	/**
	 * 将一个24位的整数写成字节数组
	 * 
	 * @param value
	 *            24位整数
	 * @param endian
	 *            字节序 {true}为big endian {false}为small endian
	 * @return 保存该整数的字节数组
	 */
	public static byte[] WriteInt24(int value, boolean endian) {
		byte b1 = (byte) (value >> 16);
		byte b2 = (byte) ((value & 0xFF00) >> 8);
		byte b3 = (byte) (value & 0xFF);

		byte[] buff = new byte[3];

		if (endian) {
			buff[0] = b1;
			buff[1] = b2;
			buff[2] = b3;
		} else {
			buff[0] = b3;
			buff[1] = b2;
			buff[2] = b1;
		}

		return buff;
	}

	////////////////////
	// int32
	////////////////////
	/**
	 * 从字节数组中读取一个32位的整数
	 * 
	 * @param buff
	 *            字节数组
	 * @param current
	 *            当前位置
	 * @param endian
	 *            字节序 {true}为big endian {false}为small endian
	 * @return 读取出的整数
	 */
	public static int ReadInt32(byte[] buff, int current, boolean endian) {
		int b1 = buff[current];
		int b2 = buff[current + 1];
		int b3 = buff[current + 2];
		int b4 = buff[current + 3];

		if (b1 < 0)
			b1 += 0x100;
		if (b2 < 0)
			b2 += 0x100;
		if (b3 < 0)
			b3 += 0x100;
		if (b4 < 0)
			b4 += 0x100;

		if (endian)
			return (b1 << 24) + (b2 << 16) + (b3 << 8) + b4;
		else
			return (b4 << 24) + (b3 << 16) + (b2 << 8) + b1;
	}

	/**
	 * 将一个32位的整数写成字节数组
	 * 
	 * @param value
	 *            32位整数
	 * @param endian
	 *            字节序 {true}为big endian {false}为small endian
	 * @return 保存该整数的字节数组
	 */
	public static byte[] WriteInt32(int value, boolean endian) {
		byte b1 = (byte) (value >> 24);
		byte b2 = (byte) ((value & 0xFF0000) >> 16);
		byte b3 = (byte) ((value & 0xFF00) >> 8);
		byte b4 = (byte) (value & 0xFF);

		byte[] buff = new byte[4];

		if (endian) {
			buff[0] = b1;
			buff[1] = b2;
			buff[2] = b3;
			buff[3] = b4;
		} else {
			buff[0] = b4;
			buff[1] = b3;
			buff[2] = b2;
			buff[3] = b1;
		}

		return buff;
	}

	////////////////////
	// int64
	////////////////////
	/**
	 * 从字节数组中读取一个64位的整数
	 * 
	 * @param buff
	 *            字节数组
	 * @param current
	 *            当前位置
	 * @param endian
	 *            字节序 {true}为big endian {false}为small endian
	 * @return 读取出的整数
	 */
	public static long ReadInt64(byte[] buff, int current, boolean endian) {
		long l1 = ReadInt32(buff, current, endian);
		long l2 = ReadInt32(buff, current + 4, endian);

		if (endian)
			return (l1 << 32) + l2;
		else
			return (l2 << 32) + l1;
	}

	/**
	 * 将一个64位的整数写成字节数组
	 * 
	 * @param value
	 *            64位整数
	 * @param endian
	 *            字节序 {true}为big endian {false}为small endian
	 * @return 保存该整数的字节数组
	 */
	public static byte[] WriteInt64(long value, boolean endian) {
		int i1 = (int) (value >> 32);
		int i2 = (int) (value & 0xFFFFFFFF);

		byte[] buff = new byte[8];

		if (endian) {
			System.arraycopy(WriteInt32(i1, endian), 0, buff, 0, 4);
			System.arraycopy(WriteInt32(i2, endian), 0, buff, 4, 4);
		} else {
			System.arraycopy(WriteInt32(i2, endian), 0, buff, 0, 4);
			System.arraycopy(WriteInt32(i1, endian), 0, buff, 4, 4);
		}

		return buff;
	}

	////////////////////
	// float
	////////////////////
	/**
	 * 从字节数组读取一个32位浮点数
	 * 
	 * @param buff
	 *            字节数组
	 * @param current
	 *            当前位置
	 * @param endian
	 *            字节序 {true}为big endian {false}为small endian
	 * @return 读取出的浮点数
	 */
	public static float ReadFloat(byte[] buff, int current, boolean endian) {
		int val = ReadInt32(buff, current, endian);
		return Float.intBitsToFloat(val);
	}

	/**
	 * 将一个32位的浮点数写成字节数组
	 * 
	 * @param value
	 *            32位浮点数
	 * @param endian
	 *            字节序 {true}为big endian {false}为small endian
	 * @return 保存该浮点数的字节数组
	 */
	public static byte[] WriteFloat(float value, boolean endian) {
		int val = Float.floatToIntBits(value);
		return WriteInt32(val, endian);
	}

	////////////////////
	// double
	////////////////////

	/**
	 * 从字节数组读取一个64位浮点数
	 * 
	 * @param buff
	 *            字节数组
	 * @param current
	 *            当前位置
	 * @param endian
	 *            字节序 {true}为big endian {false}为small endian
	 * @return 读取出的浮点数
	 */
	public static double ReadDouble(byte[] buff, int current, boolean endian) {
		long val = ReadInt64(buff, current, endian);
		return Double.longBitsToDouble(val);
	}

	/**
	 * 将一个64位的浮点数写成字节数组
	 * 
	 * @param value
	 *            64位浮点数
	 * @param endian
	 *            字节序 {true}为big endian {false}为small endian
	 * @return 保存该浮点数的字节数组
	 */
	public static byte[] WriteDouble(double value, boolean endian) {
		long val = Double.doubleToLongBits(value);
		return WriteInt64(val, endian);
	}

	////////////////////
	// bcd
	////////////////////

	/**
	 * 从字节数组中读取一个以BCD码存储的数字
	 * 
	 * @param bytes
	 *            字节数组
	 * @param current
	 *            当前位置
	 * @param length
	 *            字节长度
	 * @param align
	 *            对齐方式 {true}为左对齐 {false}为右对齐
	 * @param acturalLen
	 *            当align指定为左对齐时 需要一个额外的参数指出该数字实际的长度（阿拉伯数字个数） 以免将最后用于占位的0作为数字的一部分 当指定为右对齐时该参数被忽略
	 * @return 读取出的数字
	 */
	public static String ReadBCD(byte[] bytes, int current, int length, boolean align, int acturalLen) {
		StringBuffer buffer = new StringBuffer();

		// flag=true表示当前应该读取字节的前4位
		// flag=false表示当前应该读取字节的后4位
		boolean flag = true;
		int pos = current;
		int len = align ? acturalLen : length * 2;

		for (int i = 0; i < len; i++) {
			int n = bytes[pos] & (flag ? 0xf0 : 0x0f);
			buffer.append(flag ? n >> 4 : n);

			pos += flag ? 0 : 1;
			flag = !flag;
		}

		while (buffer.length() > 0 && buffer.charAt(0) == '0')
			buffer.deleteCharAt(0);
		if (buffer.length() == 0)
			buffer.append(0);

		return buffer.toString();
	}

	/**
	 * 将一个数字以BCD码格式写成字节数组
	 * 
	 * @param num
	 *            数字
	 * @param length
	 *            字节长度 该参数可以为0 为0时表示输出的字节数为其自然的长度 !!注意!!BCD码的字节数是数字长度的一半
	 * @param align
	 *            对齐方式 {true}为左对齐 {false}为右对齐
	 * @return
	 */
	public static byte[] WriteBCD(String num, int length, boolean align) {
		if (!StringUtils.isNumeric(num))
			throw new IllegalArgumentException("无法将字符串[" + num + "]转为BCD码");

		if (length == 0)
			length = num.length() / 2 + num.length() % 2;

		byte[] bytes = new byte[length];

		// flag=true表示当前应该写到字节的前4位
		// flag=false表示当前应该写到字节的后4位
		boolean flag = align ? true : num.length() % 2 == 0; // 左对齐时 应该从前4位开始写 右对齐时 如果num的长度为偶数 则应该从前4位开始写 否则从后4位开始写
		// pos表示当前写入的字节的位置
		int pos = align ? 0 : length - (num.length() / 2 + num.length() % 2);

		for (int i = 0; i < num.length(); i++) {
			byte n = (byte) (num.charAt(i) - '0');
			bytes[pos] |= flag ? n << 4 : n;

			pos += flag ? 0 : 1;
			flag = !flag;
		}

		return bytes;
	}

	////////////////////
	// string
	////////////////////
	/**
	 * 
	 * @param buff
	 * @param current
	 * @param length
	 * @param encoding
	 * @param fill
	 * @param left_align
	 * @return
	 */
	public static String ReadString(byte[] buff, int current, int length, String encoding, char fill, boolean left_align) {
		if ("EBCDIC".equalsIgnoreCase(encoding))
			encoding = "cp935";
		String str = "";
		try {
			str = new String(buff, current, length, encoding);
		} catch (java.io.UnsupportedEncodingException e) {
			try {
				str = new String(buff, current, length, "ISO8859_1");
			} catch (java.io.UnsupportedEncodingException e1) {
				;
			}
		}
		int len = str.length();
		int start = 0;
		int end = len - 1;
		if (left_align)
			while (end >= 0 && str.charAt(end) == fill)
				end--;
		else
			while (start < len && str.charAt(start) == fill)
				start++;
		return str.substring(start, end + 1);
	}

	/**
	 * 
	 * @param value
	 * @param length
	 * @param encoding
	 * @param fill
	 * @param left_align
	 * @return
	 */
	public static byte[] WriteString(String value, int length, String encoding, char fill, boolean left_align) {
		if (value == null)
			value = "";
		if (encoding == null)
			encoding = "gbk";
		else
			encoding = encoding.toLowerCase();

		boolean ebcdic = false;
		boolean gbk = false;

		if ("ebcdic".equals(encoding)) {
			encoding = "cp935";
			ebcdic = true;
		} else if (encoding.startsWith("gb"))
			gbk = true;

		byte[] bv = null;

		if(value.equals("\\n")) {
			bv = new byte[1];
			bv[0] = 10;
			return bv;
		}
		
		if(value.equals("\\r")) {
			bv = new byte[1];
			bv[0] = 13;
			return bv;
		}
		
		if(value.equals("\\r\\n")) {
			bv = new byte[2];
			bv[0] = 13;
			bv[1] = 10;
			return bv;
		}
		
		if(value.equals("\\n\\r")) {
			bv = new byte[2];
			bv[0] = 10;
			bv[1] = 13;
			return bv;
		}
			
		try {
			bv = value.getBytes(encoding);
		} catch (java.io.UnsupportedEncodingException e) {
			try {
				bv = value.getBytes("ISO8859_1");
			} catch (java.io.UnsupportedEncodingException e1) {
				;
			}
		}

		int len = bv.length;
		int end = len - 1;

		// 根据length进行字符串的截断
		if (length != 0 && len > length) {
			/*
			 * 
			 * 字符串太长，需要截断
			 * 
			 * 需要考虑
			 * 
			 * 1. 中文不能出现半个汉字
			 * 
			 * 2. 中文EBCDIC中的OE和0F必须匹配
			 * 
			 */
			int diff = len - length;
			boolean in_gb_ebc = false;

			while (diff > 0 && end >= 0) {
				byte b = bv[end];
				if (b == 0x0F) { // 中文EBCDIC结束符
					if (ebcdic)
						in_gb_ebc = true;
					else
						diff--;
				} else if (b == 0x0E) { // 中文EBCDIC开始符
					if (ebcdic) {
						in_gb_ebc = false;
						diff -= 2;
					} else
						diff--;
				} else if (in_gb_ebc) {
					end--; // 多减一个字节，不能将中文拆开
					diff -= 2;
				} else if ((b & 0x80) != 0 && gbk) { // 中文字符
					end--; // 多减一个字节，不能将中文拆开
					diff -= 2;
				} else
					diff--;
				end--;
			}
			/*
			 * 
			 * 处理最后一个字节是否为中文EBCDIC的标志符
			 * 
			 */
			if (in_gb_ebc)
				if (bv[end] == 0x0E)
					end--;
				else {
					end++;
					bv[end] = 0x0F;
				}
		}

		/*
		 * 
		 * 设置end为实际需要复制的字节长度
		 * 
		 */
		end++;
		if (end < 0)
			end = 0;

		// 转换填充字符
		String fillstr = String.valueOf(new char[] { fill });
		byte fillbyte = (byte) fill;
		try {
			byte fillbv[] = fillstr.getBytes(encoding);
			fillbyte = fillbv[0];
		} catch (java.io.UnsupportedEncodingException e) {
			;
		}

		byte[] buff = new byte[length != 0 ? length : end];
		int current = 0;

		// 根据填充方式进行字节复制
		if (left_align) {
			if (end > 0) {
				System.arraycopy(bv, 0, buff, current, end);
				current += end;
			}
			if (length != 0)
				for (int i = 0; i < length - end; i++)
					buff[i + current] = fillbyte;
		} else {
			if (length != 0)
				for (int i = 0; i < length - end; i++)
					buff[i + current] = fillbyte;
			else
				length = end;

			if (end > 0) {
				System.arraycopy(bv, 0, buff, current + length - end, end);
				current += end;
			}
		}

		return buff;
	}

	////////////////////
	// byte[]
	////////////////////

	/**
	 * 
	 * @param value
	 * @param current
	 * @param length
	 * @param fillingByte
	 * @param alignLeft
	 * @return
	 */
	public static byte[] ReadBytes(byte[] value, int current, int length, byte fillingByte, boolean alignLeft) {
		if (value == null)
			return new byte[0];

		byte[] buff = new byte[length];
		System.arraycopy(value, current, buff, 0, length);

		return buff;
	}

	/**
	 * 
	 * @param value
	 * @param length
	 * @param fillingByte
	 * @param alignLeft
	 * @return
	 */
	public static byte[] WriteBytes(byte[] value, int length, byte fillingByte, boolean alignLeft) {
		if (value == null)
			return new byte[0];

		int len = value.length;

		if (length == 0)
			return value;

		byte[] buff = new byte[length];
		if (alignLeft) {
			if (len <= length) {
				System.arraycopy(value, 0, buff, 0, len);
				for (int i = len; i < length; i++)
					buff[i] = fillingByte;
			} else
				System.arraycopy(value, 0, buff, 0, length);
		} else if (len <= length) {
			System.arraycopy(value, 0, buff, length - len, len);
			for (int i = 0; i < length - len; i++)
				buff[i] = fillingByte;
		} else
			System.arraycopy(value, len - length, buff, 0, length);
		return buff;
	}
	
	
	////////////////////
	// bcd
	////////////////////

	/**
	 * 从字节数组中读取一个以BCD码存储的数字(不删除数字前的0）
	 * 
	 * @param bytes
	 *            字节数组
	 * @param current
	 *            当前位置
	 * @param length
	 *            字节长度
	 * @param align
	 *            对齐方式 {true}为左对齐 {false}为右对齐
	 * @param acturalLen
	 *            当align指定为左对齐时 需要一个额外的参数指出该数字实际的长度（阿拉伯数字个数） 以免将最后用于占位的0作为数字的一部分 当指定为右对齐时该参数被忽略
	 * @return 读取出的数字
	 */
	public static String ReadBCD2(byte[] bytes, int current, int length, boolean align, int acturalLen) {
		StringBuffer buffer = new StringBuffer();

		// flag=true表示当前应该读取字节的前4位
		// flag=false表示当前应该读取字节的后4位
		boolean flag = true;
		int pos = current;
		int len = align ? acturalLen : length * 2;

		for (int i = 0; i < len; i++) {
			int n = bytes[pos] & (flag ? 0xf0 : 0x0f);
			buffer.append(flag ? n >> 4 : n);

			pos += flag ? 0 : 1;
			flag = !flag;
		}

		if (buffer.length() == 0)
			buffer.append(0);

		return buffer.toString();
	}
}
