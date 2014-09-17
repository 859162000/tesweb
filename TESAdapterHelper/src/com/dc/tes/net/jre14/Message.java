package com.dc.tes.net.jre14;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map.Entry;


/**
 * 表示模拟器各部分之间传递的消息报文
 * 适用于jdk1.4.2
 * 
 * byte[] value --> value原样存放
 * String  value--> value.getBytes(m_encoding)存放
 * int value  --> String.valueOf(value).getBytes(m_encoding)存放 
 * 
 * @author guhb
 * 
 */
public class Message {
	
	//private static final Log logger = LogFactory.getLog(Message.class);
	
	private String m_encoding = "utf-8";
	private byte[] m_emptyByteArray = "".getBytes();
	/**
	 * 保存各报文单元的表(String, byte[])
	 */
	LinkedHashMap<String, byte[]> m_items = new LinkedHashMap<String, byte[]>();
	/**
	 * 报文类型
	 */
	private MessageType m_type = null;
	/**
	 * 缓存 用于保存包中的字节
	 */
	private byte[] m_bytes = new byte[1024];

	/**
	 * 初始化一个新的报文
	 * 
	 * @param type
	 *            报文类型
	 */
	public Message(MessageType type) {
		this.m_type = type;
	}
	
	public void SetEncoding(String encoding) {
		this.m_encoding = encoding;
	}

	/**
	 * 使用一个输入流初始化消息 该流在读取结束后将保持打开
	 * 
	 * @param s
	 *            包含消息的输入流
	 * @param timeout
	 *            超时时间
	 * @throws Exception 
	 */
	public Message(InputStream s, int timeout) throws Exception {
		try {
			// 获取报文类型
			String type = new String(read(s, 10, timeout), m_encoding).trim();
			this.m_type = MessageType.valueOf(type);

			// 略过报文总长度
			read(s, 10, timeout);
			// 获取报文实际长度
			int len = Integer.parseInt(new String(read(s, 10, timeout), m_encoding));

			//当前位置
			int pos = 30;

			// 解析出每个单元的内容
			while (pos < len) {
				// 获取名称长度
				int nameLen = Integer.parseInt(new String(read(s, 10, timeout), m_encoding));
				pos += 10;
				// 获取名称
				String name = new String(read(s, nameLen, timeout), m_encoding);
				pos += nameLen;
				// 获取值长度
				int valueLen = Integer.parseInt(new String(read(s, 10, timeout), m_encoding));
				pos += 10;
				// 获取值
				byte[] value = read(s, valueLen, timeout);
				pos += valueLen;

				// 将解析出的名值对存储到自身
				try {
					this.put(name, value);
				} catch (IllegalArgumentException ex) {
					throw new Exception("报文中出现了意外的单元名称：" + name);
				}
			}
		} catch (IOException ex) {
			throw new Exception("从输入流中读取报文时发生IO异常", ex);
		}
	}

	/**
	 * 使用一个输入流初始化消息 该流在读取结束后将保持打开
	 * 
	 * @param s
	 *            包含消息的输入流
	 * @throws Exception 
	 * @throws IOException
	 */
	public Message(InputStream s) throws Exception {
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
	 * @return 报文单元的值, null=如果不存在name对应的值
	 */
	public byte[] getBytes(String name) {
		if (!this.m_items.containsKey(name))
			return null;
		return (byte[]) this.m_items.get(name);
	}

	/**
	 * 取整数类型的报文单元
	 * 
	 * @param name
	 *            报文单元名称
	 * @return 报文单元的值
	 * @throws UnsupportedEncodingException 
	 * @throws NumberFormatException 
	 */
	public int getInteger(String name) throws NumberFormatException, UnsupportedEncodingException {
		if (!this.m_items.containsKey(name))
			return 0;
		return Integer.parseInt(new String((byte[])this.m_items.get(name), m_encoding));
	}

	/**
	 * 取字符串类型的报文单元
	 * 
	 * @param name
	 *            报文单元名称
	 * @return 报文单元的值
	 * @throws UnsupportedEncodingException 
	 */
	public String getString(String name) throws UnsupportedEncodingException {
		if (!this.m_items.containsKey(name))
			return null;
		return new String((byte[])this.m_items.get(name), m_encoding);
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
		this.m_items.put(name, value != null ? value : m_emptyByteArray);
	}

	/**
	 * 设置整数类型的报文单元
	 * 
	 * @param name
	 *            报文单元名称
	 * @param value
	 *            报文单元的值
	 */
	public void put(String name, int value){
		byte[] bl = null;
		try {
			bl = String.valueOf(value).getBytes(m_encoding);
			this.m_items.put(name, bl);
		} catch (UnsupportedEncodingException e) {			
			e.printStackTrace();
		}
	}

	/**
	 * 设置字符串类型的报文单元
	 * 
	 * @param name
	 *            报文单元名称
	 * @param value
	 *            报文单元的值
	 * @throws UnsupportedEncodingException 
	 */
	public void put(String name, String value) throws UnsupportedEncodingException {
		this.m_items.put(name, value != null ? value.getBytes(m_encoding) : m_emptyByteArray);
	}

	/**
	 * 导出描述此报文的字节流
	 * 
	 * @return 描述此报文的字节流
	 * @throws UnsupportedEncodingException 
	 */
	public byte[] Export() throws UnsupportedEncodingException {
		
		// 报文头10个字节为报文用途
		System.arraycopy(FixLength(this.m_type.name(), 10, ' ', true).getBytes(m_encoding), 0, 
				m_bytes, 0, 10);
		// 报文头10~30字节为报文长度，后面计算出来再填充
		System.arraycopy(FixLength("", 20, '0', true).getBytes(m_encoding), 0, 
				m_bytes, 10, 20);

		// 报文长度
		int len = 30;
		int pos = 30;

		//添加字段内容
		Iterator<String> it = m_items.keySet().iterator();
		while(it.hasNext()) {
			//名称
			String name = (String) it.next();
			// 名称长度
			int nameLen = name.length();
			// 值长度
			int valueLen = ((byte[]) this.m_items.get(name)).length;
			// 计算长度
			len += 10 + nameLen + 10 + valueLen;
			
			while(len > m_bytes.length){
				byte[] bytes = new byte[m_bytes.length * 2];
				System.arraycopy(this.m_bytes, 0, bytes, 0, m_bytes.length);
				this.m_bytes = bytes;
			}

			// 放入名称长度 ,长度10 右对齐 填充'0'
			System.arraycopy(FixLength(String.valueOf(nameLen), 10, '0', true).getBytes(m_encoding), 0, 
					m_bytes, pos, 10);
			pos += 10;
			// 放入名称 
			System.arraycopy(name.getBytes(m_encoding), 0, 
					m_bytes, pos, nameLen);
			pos += nameLen;
			// 放入值长度 ,长度10 右对齐 填充'0'
			System.arraycopy(FixLength(String.valueOf(valueLen), 10, '0', true).getBytes(m_encoding), 0, 
					m_bytes, pos, 10);
			pos += 10;
			// 放入值
			System.arraycopy((byte[])this.m_items.get(name), 0, 
					m_bytes, pos, valueLen);
			pos += valueLen;
		}

		// 插入报文长度
		System.arraycopy(FixLength(String.valueOf(len), 10, '0', true).getBytes(m_encoding), 0, 
				m_bytes, 10, 10);
		System.arraycopy(FixLength(String.valueOf(len), 10, '0', true).getBytes(m_encoding), 0, 
				m_bytes, 20, 10);

		byte[] bytes = new byte[len];
		System.arraycopy(this.m_bytes, 0, bytes, 0, len);
		Arrays.fill(m_bytes,(byte)0);
				
		/*if (logger.isDebugEnabled())
			logger.debug("Message Export is:" + new String(bytes, "GBK"));*/
		
		
		return bytes;
	}

	
	public String toString() {
		StringBuffer buffer = new StringBuffer(this.m_type.name());
		buffer.append(" [\r\n");
		Iterator<Entry<String, byte[]>> it = m_items.entrySet().iterator();
		while(it.hasNext()) {
			Entry<?, ?> entry = (Entry<?, ?>) it.next();
			String name = (String) entry.getKey();
			try {
				buffer.append("  ").append(name).append(" = ").append(new String((byte[]) this.m_items.get(name), m_encoding)).append("\r\n");
			} catch (UnsupportedEncodingException e) {
				buffer.append("报文内容解码失败，" + name + "值编码不符合约定！");
				e.printStackTrace();
			}
		}
		buffer.append("]\r\n");
		return buffer.toString();
	}

	/**
	 * 工具函数 用于从输入流中读取指定数量的字节
	 * 
	 * @param stream
	 *            输入流
	 * @param length
	 *            要读取的字节的数量
	 * @return 从流中读取出的字节
	 * @throws IOException
	 */
	private static byte[] read(InputStream stream, int length, int timeout) throws IOException {
		int t = 0;
		byte[] buffer = new byte[length];
		int pos = 0;
		while (pos < length) {
			int len = stream.read(buffer, pos, length - pos);
			pos += len;

			if (len < 0)
				throw new IOException("read() = -1");
			if (len == 0)
				try {
					Thread.sleep(100);
					t += 100;
					if (t > timeout && timeout > 0)
						throw new IOException("超时了[timeout=" + t + "]");
				} catch (InterruptedException ex) {
					// 这里真的会抛异常吗？
				}
		}

		return buffer;
	}
	
	/**
	 * 工具函数 产生固定长度的字符串。
	 * 如果src的长度比length参数大，返回原始src，否则将在前（或后）填补padding字符。
	 * @param src 源字符串
	 * @param length 期望的长度
	 * @param padding 填补的字符
	 * @param leadingPad =true在最前面填补, =false在最后面填补。
	 * @return 填补以后的字符串
	 */
	public static String FixLength(String src, int length, char padding, boolean leadingPad) {
		if (src == null) {
			src = "";
		}
		if (length <= src.length()) {
			return src;
		}
		StringBuffer sb = new StringBuffer(src);
		for (int i = src.length(), j = length; i < j; i++) {
			if (leadingPad) {
				sb.insert(0, padding);
			} else {
				sb.append(padding);
			}
		}
		return sb.toString();
	} 
}
