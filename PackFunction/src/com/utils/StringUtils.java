package com.utils;

import java.nio.ByteBuffer;
import java.util.Arrays;


public class StringUtils
{

	/**
	 * 追加字符到指定长度的字符
	 * 
	 * @param srcData
	 *            :原数据
	 * @param alignMode
	 *            :对齐方式
	 * @param paddCharacter
	 *            :填补的字符
	 * @param totalLen
	 *            :填充到的长度
	 * @return
	 */
	public static String padding(String srcData, String alignMode, String paddCharacter, int totalLen)
	{

		if (srcData == null || null == alignMode || null == paddCharacter || totalLen == 0)
		{
			throw new IllegalArgumentException("传入的数据不能为空或0，请检查数据!");
		}

		int paddLen = totalLen - srcData.length();

		StringBuffer paddResultBuffer = new StringBuffer();

		if (alignMode.equalsIgnoreCase("left"))
		{
			for (int i = 0; i < paddLen; i++)
			{
				paddResultBuffer.append(paddCharacter);
			}
			paddResultBuffer.append(srcData);
		} else if (alignMode.equalsIgnoreCase("right"))
		{
			paddResultBuffer.append(srcData);
			for (int i = 0; i < paddLen; i++)
			{
				paddResultBuffer.append(paddCharacter);
			}

		} else
		{
			throw new IllegalArgumentException("paddAlign  is not left or right，please check !");
		}

		return paddResultBuffer.toString();
	}

	public static String XOR(String hexSrcData1, String hexSrcData2)
	{

		if (hexSrcData1.length() != hexSrcData2.length())
		{
			throw new IllegalArgumentException("异或的两个数据长度不相等，请检查数据!");
		}

		byte[] bytes1 = HexBinary.decode(hexSrcData1);

		byte[] bytes2 = HexBinary.decode(hexSrcData2);

		ByteBuffer buffer = ByteBuffer.allocate(bytes2.length);

		for (int i = 0; i < bytes2.length; i++)
		{
			byte temp = (byte) ((int) bytes1[i] ^ (int) bytes2[i]);
			buffer.put(temp);
		}

		return HexBinary.encode(buffer.array());
	}

	/**
	 * 按位取反操作
	 * 
	 * @param hexSrcData
	 * @return
	 */
	public static String reversBytes(String hexSrcData)
	{
		if (null == hexSrcData || hexSrcData.equals("") || hexSrcData.length() == 0)
		{
			throw new IllegalArgumentException("非法的按位取反的数据，请检查数据");
		}

		byte[] srcBytes = HexBinary.decode(hexSrcData);

		ByteBuffer destBuffer = ByteBuffer.allocate(srcBytes.length);

		for (int i = 0; i < srcBytes.length; i++)
		{

			byte temp = (byte) (~(int) srcBytes[i]);

			destBuffer.put(temp);
		}

		return HexBinary.encode(destBuffer.array());
	}

}

