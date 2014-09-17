package com.dc.tes.util;

import java.util.ArrayList;

/**
 * 工具类 提供了一些用于字节数组的工具函数
 * 
 * @author huangzx
 * 
 */
public class ByteArrayUtils {

	public static boolean Contains(byte[] bytes, byte[] target) {
		return IndexOf(bytes, target, 0) != -1;
	}

	/**
	 * 在给定的字节数组中查找指定的字节序列的出现位置
	 * 
	 * @param bytes
	 *            被搜索的字节数组
	 * @param target
	 *            目标字节序列
	 * @param start
	 *            查找的起点
	 * @return 目标字节序列的出现位置 如果未出现则返回-1
	 */
	public static int IndexOf(byte[] bytes, byte[] target, int start) {
		for (int i = start; i < bytes.length - target.length + 1; i++) {
			boolean match = true;
			for (int j = 0; j < target.length; j++)
				if (bytes[i + j] != target[j]) {
					match = false;
					break;
				}
			if (match)
				return i;
		}
		return -1;
	}

	/**
	 * 从给定的字节数组中截取出一个片段
	 * 
	 * @param bytes
	 *            被截取的字节数组
	 * @param start
	 *            截取的起始位置
	 * @param length
	 *            截取的长度
	 * @return
	 */
	public static byte[] SubArray(byte[] bytes, int start, int length) {
		byte[] result = new byte[length];
		System.arraycopy(bytes, start, result, 0, length);
		return result;
	}

	/**
	 * 使用指定的分隔符分隔给定的字节数组
	 * 
	 * @param bytes
	 *            要进行分隔的字节数组
	 * @param separtor
	 *            分隔符
	 * @return 被分隔符分隔开的原数组
	 */
	public static byte[][] Split(byte[] bytes, byte separtor) {
		return Split(bytes, new byte[] { separtor });
	}

	/**
	 * 使用指定的分隔符分隔给定的字节数组
	 * 
	 * @param bytes
	 *            要进行分隔的字节数组
	 * @param separtor
	 *            分隔符
	 * @return 被分隔符分隔开的原数组
	 */
	public static byte[][] Split(byte[] bytes, byte[] separtor) {
		ArrayList<byte[]> list = new ArrayList<byte[]>();

		int pos1 = 0;
		int pos2;
		while ((pos2 = IndexOf(bytes, separtor, pos1)) != -1) {
			list.add(SubArray(bytes, pos1, pos2 - pos1));
			pos1 = pos2 + separtor.length;
		}
		list.add(SubArray(bytes, pos1, bytes.length - pos1));
		return list.toArray(new byte[0][]);
	}

	public static boolean StartWith(byte[] bytes, byte[] head) {
		if (bytes.length < head.length)
			return false;

		int diff = 0;
		for (int i = 0; i < head.length; i++)
			diff |= bytes[i] ^ head[i];

		return diff == 0;
	}

	public static boolean EndWith(byte[] bytes, byte[] tail) {
		if (bytes.length < tail.length)
			return false;

		int diff = 0;
		for (int i = 0; i < tail.length; i++)
			diff |= bytes[i + bytes.length - tail.length] ^ tail[i];

		return diff == 0;
	}
	
	public static byte[] int2Byte(int num) {
		  byte[] bytes = new byte[4];
		  for (int i = 0; i < 4; i++) {
			  bytes[i] = (byte) (num >>> (24 - i * 8));
		  }
		  return bytes;
	}
}
