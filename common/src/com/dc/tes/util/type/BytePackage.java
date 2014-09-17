package com.dc.tes.util.type;

import com.dc.tes.util.ByteArrayUtils;

/**
 * 字节包 对字节数组的一个包装 提供一些方便的方法
 * 
 * @author huangzx
 * 
 */
public class BytePackage {
	/**
	 * 缓存 用于保存包中的字节 此数组的长度总是大于等于实际的长度
	 */
	private byte[] m_bytes = new byte[128];
	/**
	 * 包中的实际字节长度
	 */
	private int m_length;

	/**
	 * 创建一个字节包
	 */
	public BytePackage() {
	}

	/**
	 * 用给定的字节数组创建一个字节包
	 * 
	 * @param bytes
	 *            包中的字节
	 */
	public BytePackage(byte[] bytes) {
		this.Append(bytes);
	}

	/**
	 * 向包中添加字节数组
	 * 
	 * @param bytes
	 *            要添加的字节数组 如果该值为null则不会发生任何事情
	 */
	public void Append(byte[] bytes) {
		this.Append(bytes, 0, bytes.length);
	}

	/**
	 * 向包中添加字节数组
	 * 
	 * @param bytes
	 *            要添加的字节数组 如果该值为null则不会发生任何事情
	 * @param start
	 *            要添加的字节的起始位置
	 * @param length
	 *            要添加的字节的个数
	 */
	public void Append(byte[] bytes, int start, int length) {
		if (bytes == null)
			return;

		this.ensureLength(this.m_length + length);

		System.arraycopy(bytes, start, this.m_bytes, this.m_length, length);
		this.m_length += length;
	}

	/**
	 * 向包中的指定位置插入字节数组
	 * 
	 * @param bytes
	 *            要插入的字节数组 如果该值为null则不会发生任何事情
	 * @param pos
	 *            要插入的位置
	 */
	public void Insert(byte[] bytes, int pos) {
		this.Insert(bytes, 0, bytes.length, pos);
	}

	/**
	 * 向包中的指定位置插入字节数组
	 * 
	 * @param bytes
	 *            要插入的字节数组 如果该值为null则不会发生任何事情
	 * @param start
	 *            要插入的字节的起始位置
	 * @param length
	 *            要插入的字节的个数
	 * @param pos
	 *            要插入的位置
	 */
	public void Insert(byte[] bytes, int start, int length, int pos) {
		if (bytes == null)
			return;

		this.ensureLength(this.m_length + length);

		System.arraycopy(this.m_bytes, pos, this.m_bytes, pos + length, this.m_length - pos);
		System.arraycopy(bytes, start, this.m_bytes, pos, length);

		this.m_length += length;
	}

	/**
	 * 获取包中保存的字节数组
	 * 
	 * @return 返回包中保存的字节数组的一个副本
	 */
	public byte[] getBytes() {
		return this.getBytes(0, this.m_length);
	}

	public byte[] Export() {
		return this.getBytes();
	}

	public byte[] getBytes(int start, int len) {
		byte[] bytes = new byte[len];
		System.arraycopy(this.m_bytes, start, bytes, 0, len);
		return bytes;
	}

	/**
	 * 获取包中的字节长度
	 * 
	 * @return 包中的字节长度
	 */
	public int getLength() {
		return this.m_length;
	}

	/**
	 * 从包的末尾删除指定的字节数
	 * 
	 * @param backspace
	 *            要删除的字节数
	 */
	public void Backspace(int backspace) {
		this.m_length -= backspace;
	}

	/**
	 * 查找指定的字节序列的出现位置
	 * 
	 * @param target
	 *            目标字节序列
	 * @param start
	 *            查找的起点
	 * @return 标字节序列的出现位置 如果未出现则返回-1
	 */
	public int IndexOf(byte[] target, int start) {
		int pos = ByteArrayUtils.IndexOf(this.m_bytes, target, start);
		if (pos > this.m_length)
			pos = -1;

		return pos;
	}

	/**
	 * 删除掉指定位置的字节 删除成功后后续字节前移 包的长度减1
	 * 
	 * @param pos
	 *            要删除的字节所处的位置
	 */
	public void Delete(int pos) {
		this.Delete(pos, 1);
	}

	/**
	 * 删除掉指定位置的字节 删除成功后后续字节前移 包的长度减length
	 * 
	 * @param start
	 *            要删除的起始位置
	 * @param length
	 *            要删除的字节数量
	 */
	public void Delete(int start, int length) {
		if (length > this.m_length - start)
			length = this.m_length - start;

		System.arraycopy(this.m_bytes, start + length, this.m_bytes, start, this.m_length - length);

		this.m_length -= length;
	}
	
	/**
	 * 替换指定位置的字节
	 * @param start
	 * 			要替换的起始位置
	 * @param length
	 * 			要替换的长度
	 * @param target
	 * 			要替换成的字节
	 */         
	public void Replace(int start, int length, byte[] target) {
		for(int i=0; i<length; i++) {
			this.m_bytes[start] = target[i];
		}	
	}

	/**
	 * 从指定位置处截断 从指定位置开始的字节都会被删除
	 * 
	 * @param pos
	 *            截断的位置 如果该值大于包的长度则不会发生任何事情
	 */
	public void TruncationAt(int pos) {
		if (pos < this.m_length)
			this.m_length = pos;
	}

	/**
	 * 工具函数 用于确保m_bytes的长度至少为len
	 * 
	 * @param len
	 *            m_bytes的长度底限
	 */
	private void ensureLength(int len) {
		if (this.m_bytes.length < len) {
			byte[] bytes = new byte[len * 2];
			System.arraycopy(this.m_bytes, 0, bytes, 0, this.m_length);
			this.m_bytes = bytes;
		}
	}
}
