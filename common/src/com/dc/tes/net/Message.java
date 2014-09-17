package com.dc.tes.net;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.LinkedHashMap;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.SystemUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.dc.tes.exception.CommonErr;
import com.dc.tes.exception.TESException;
import com.dc.tes.util.RuntimeUtils;
import com.dc.tes.util.type.BytePackage;

/**
 * 表示模拟器各部分之间传递的消息报文 具体格式参照陈勇写的文档
 * 
 * @author huangzx
 * 
 */
public class Message {
	private Log log = LogFactory.getLog(Message.class);

	private static final String encoding = RuntimeUtils.utf8.name();

	/**
	 * 保存各报文单元的表
	 */
	LinkedHashMap<String, byte[]> m_items = new LinkedHashMap<String, byte[]>();
	/**
	 * 报文类型
	 */
	private MessageType m_type;

	/**
	 * 初始化一个新的报文
	 * 
	 * @param type
	 *            报文类型
	 */
	public Message(MessageType type) {
		this.m_type = type;
	}

	/**
	 * 使用一个输入流初始化消息 该流在读取结束后将保持打开
	 * 
	 * @param s
	 *            包含消息的输入流
	 * @param timeout
	 *            超时时间 单位为毫秒 为0表示永远不超时
	 */
	public Message(InputStream s, int timeout) {
		log.trace("开始从输入流中读取报文...");
		BytePackage p = new BytePackage();
		byte[] buffer;
		try {
			// 获取报文类型
			p.Append(buffer = read(s, 10, timeout));
			String type = new String(buffer, encoding).trim();
			log.trace("报文类型：" + type);
			try {
				this.m_type = Enum.valueOf(MessageType.class, type);
			} catch (IllegalArgumentException ex) {
				throw new TESException(CommonErr.Net.UnknownMessageType, type);
			}

			// 略过报文总长度
			p.Append(buffer = read(s, 10, timeout));
			log.trace("略过报文总长度");

			// 获取报文实际长度
			p.Append(buffer = read(s, 10, timeout));
			int len = Integer.parseInt(new String(buffer, encoding));
			log.trace("报文总长度为" + len);

			//当前位置
			int pos = 30;

			// 解析出每个单元的内容
			while (pos < len) {
				// 获取名称长度
				p.Append(buffer = read(s, 10, timeout));
				int nameLen = Integer.parseInt(new String(buffer, encoding));
				pos += 10;

				// 获取名称
				p.Append(buffer = read(s, nameLen, timeout));
				String name = new String(buffer, encoding);
				pos += nameLen;
				log.trace("NAME >> " + name);

				// 获取值长度
				p.Append(buffer = read(s, 10, timeout));
				int valueLen = Integer.parseInt(new String(buffer, encoding));
				pos += 10;

				// 获取值
				p.Append(buffer = read(s, valueLen, timeout));
				byte[] value = buffer;
				pos += valueLen;
				log.trace("VALUE > " + new String(value, encoding));

				// 将解析出的名值对存储到自身
				this.put(name, value);
			}
		} catch (Exception ex) {
			throw new TESException(CommonErr.Net.ReceiveMessageFail, RuntimeUtils.PrintHex(p.Export(), RuntimeUtils.utf8));
		}

		log.trace("报文为" + SystemUtils.LINE_SEPARATOR + RuntimeUtils.PrintHex(p.Export(), RuntimeUtils.utf8));
	}

	/**
	 * 使用一个输入流初始化消息 该流在读取结束后将保持打开
	 * 
	 * @param s
	 *            包含消息的输入流
	 * @throws IOException
	 */
	public Message(InputStream s) {
		this(s, 0);
	}

	/**
	 * 获取该消息的类型
	 * 
	 * @return 返回该消息的类型
	 */
	public MessageType getType() {
		return this.m_type;
	}

	/**
	 * 取字节数组类型的报文单元
	 * 
	 * @param name
	 *            报文单元名称
	 * @return 报文单元的值 如果报文中不存在指定的单元则返回null
	 */
	public byte[] getBytes(String name) {
		if (!this.m_items.containsKey(name))
			return null;
		return this.m_items.get(name);
	}

	/**
	 * 取整数类型的报文单元
	 * 
	 * @param name
	 *            报文单元名称
	 * @return 报文单元的值 如果报文中不存在指定的单元则返回0
	 */
	public int getInteger(String name) {
		if (!this.m_items.containsKey(name))
			return 0;

		try {
			return Integer.parseInt(new String(this.m_items.get(name), encoding));
		} catch (UnsupportedEncodingException ex) {
			throw new TESException(CommonErr.UnsupportedEncoding, encoding);
		}
	}

	/**
	 * 取字符串类型的报文单元
	 * 
	 * @param name
	 *            报文单元名称
	 * @return 报文单元的值 如果报文中不存在指定的单元则返回null
	 */
	public String getString(String name) {
		if (!this.m_items.containsKey(name))
			return null;
		try {
			return new String(this.m_items.get(name), encoding);
		} catch (UnsupportedEncodingException ex) {
			throw new TESException(CommonErr.UnsupportedEncoding, encoding);
		}
	}

	/**
	 * 设置字节数组类型的报文单元
	 * 
	 * @param name
	 *            报文单元名称
	 * @param value
	 *            报文单元的值
	 */
	public void put(String name, byte[] value) {
		this.m_items.put(name, value != null ? value : ArrayUtils.EMPTY_BYTE_ARRAY);
	}

	public void put(String name, StringBuffer value) {
		this.put(name, value.toString());
	}

	/**
	 * 设置整数类型的报文单元
	 * 
	 * @param name
	 *            报文单元名称
	 * @param value
	 *            报文单元的值
	 */
	public void put(String name, int value) {
		try {
			this.m_items.put(name, String.valueOf(value).getBytes(encoding));
		} catch (UnsupportedEncodingException ex) {
			throw new TESException(CommonErr.UnsupportedEncoding, encoding);
		}
	}

	/**
	 * 设置整数类型的报文单元
	 * 
	 * @param name
	 *            报文单元名称
	 * @param value
	 *            报文单元的值
	 */
	public void put(String name, long value) {
		try {
			this.m_items.put(name, String.valueOf(value).getBytes(encoding));
		} catch (UnsupportedEncodingException ex) {
			throw new TESException(CommonErr.UnsupportedEncoding, encoding);
		}
	}

	/**
	 * 设置字符串类型的报文单元
	 * 
	 * @param name
	 *            报文单元名称
	 * @param value
	 *            报文单元的值
	 */
	public void put(String name, String value) {
		try {
			this.m_items.put(name, value != null ? value.getBytes(encoding) : ArrayUtils.EMPTY_BYTE_ARRAY);
		} catch (UnsupportedEncodingException ex) {
			throw new TESException(CommonErr.UnsupportedEncoding, encoding);
		}
	}

	/**
	 * 导出描述此报文的字节流
	 * 
	 * @return 描述此报文的字节流
	 */
	public byte[] Export() {
		try {
			BytePackage p = new BytePackage();

			// 报文头10个字节为报文用途
			String str1 = StringUtils.leftPad(this.m_type.name(), 10);
			p.Append(str1.getBytes(encoding));
			//p.Append(StringUtils.leftPad(this.m_type.name(), 10).getBytes(encoding));

			// 报文长度
			int len = 30;

			//添加字段内容
			for (String name : this.m_items.keySet()) {
				// 名称长度
				int nameLen = name.length();
				// 值长度
				int valueLen = this.m_items.get(name).length;

				// 放入名称长度 长度10 右对齐 填充'0'
				p.Append(StringUtils.leftPad(String.valueOf(nameLen), 10, '0').getBytes(encoding));
				// 放入名称 utf-8编码
				p.Append(name.getBytes(encoding));
				// 放入值长度 长度10 右对齐 填充'0'
				p.Append(StringUtils.leftPad(String.valueOf(valueLen), 10, '0').getBytes(encoding));
				// 放入值
				p.Append(this.m_items.get(name));

				// 计算长度
				len += 10 + nameLen + 10 + valueLen;
			}

			// 插入报文长度
			p.Insert(StringUtils.leftPad(String.valueOf(len), 10, '0').getBytes(encoding), 10);
			p.Insert(StringUtils.leftPad(String.valueOf(len), 10, '0').getBytes(encoding), 10);
			log.info("将要发送的报文：" + new String(p.Export()));

			return p.Export();
		} catch (UnsupportedEncodingException ex) {
			throw new TESException(CommonErr.UnsupportedEncoding, encoding);
		}
	}

	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer(this.m_type.toString());
		buffer.append(" [").append(SystemUtils.LINE_SEPARATOR);
		for (String name : this.m_items.keySet())
			try {
				buffer.append("  ").append(name).append(" = ").append(new String(this.m_items.get(name), encoding)).append(SystemUtils.LINE_SEPARATOR);
			} catch (UnsupportedEncodingException ex) {
				throw new TESException(CommonErr.UnsupportedEncoding, encoding);
			}

		buffer.append("]");
		return buffer.toString();
	}

	/**
	 * 工具函数 用于从输入流中读取指定数量的字节
	 * 
	 * @param stream
	 *            输入流
	 * @param length
	 *            要读取的字节的数量
	 * @param timeout
	 *            超时时间 单位为毫秒 为0表示永远不超时
	 * @return 从流中读取出的字节
	 */
	private static byte[] read(InputStream stream, int length, int timeout) {
		try {
			int t = 0;
			byte[] buffer = new byte[length];
			int pos = 0;
			while (pos < length) {
				int l = stream.read(buffer, pos, length - pos);
				pos += l;

				if (l < 0)
					throw new IOException("read() = -1");
				if (l == 0)
					try {
						Thread.sleep(100);
						t += 100;
						if (t > timeout && timeout > 0)
							throw new TESException(CommonErr.Net.Timeout);
					} catch (InterruptedException ex) {
						// 这里真的会抛异常吗？
					}
			}

			return buffer;
		} catch (IOException ex) {
			throw new TESException(CommonErr.IO.IOReadFail, ex);
		}
	}
}
