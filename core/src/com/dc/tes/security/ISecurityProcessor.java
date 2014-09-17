package com.dc.tes.security;

import com.dc.tes.Core;
import com.dc.tes.dom.MsgDocument;

/**
 * 安全接口
 * 
 * @author lijic
 * 
 */
public interface ISecurityProcessor {
	/**
	 * 对报文进行解密处理 当报文刚从适配器送来时会调用此函数 <br/>
	 * 仅在处理接收端交易时调用
	 * 
	 * @param core
	 *            核心实例
	 * @param msg
	 *            送来的字节流
	 * @return 经过安全处理过的字节流
	 */
	public byte[] DecryptAll(Core core, byte[] msg);

	/**
	 * 对报文进行解密处理 当报文将被拆包时会调用此函数
	 * 
	 * @param core
	 *            核心实例
	 * @param msg
	 *            送来的字节流
	 * @param tranCode
	 *            交易码
	 * @return 经过安全处理过的字节流
	 */
	public byte[] DecryptAll(Core core, byte[] msg, String tranCode);

	/**
	 * 对报文进行解密处理 当报文被成功拆包后会调用此函数
	 * 
	 * @param core
	 *            核心实例
	 * @param msg
	 *            送来的字节流
	 * @param tranCode
	 *            交易码
	 * @param doc
	 *            拆出的报文数据
	 * @return 经过安全处理的报文数据
	 */
	public MsgDocument DecryptData(Core core, byte[] msg, String tranCode, MsgDocument doc);

	/**
	 * 对报文进行加密处理 当报文将被组包时会调用此函数
	 * 
	 * @param core
	 *            核心实例
	 * @param tranCode
	 *            交易码
	 * @param doc
	 *            报文数据
	 * @return 经过安全处理的报文数据
	 */
	public MsgDocument EncryptData(Core core, String tranCode, MsgDocument doc);

	/**
	 * 对报文进行加密处理 当报文即将送往被测系统时会调用此函数
	 * 
	 * @param core
	 *            核心实例
	 * @param tranCode
	 *            交易码
	 * @param doc
	 *            经过安全处理的报文数据或为null
	 * @param msg
	 *            即将送往被测系统的字节流
	 * @return 经过安全处理的字节流
	 */
	public byte[] EncryptAll(Core core, String tranCode, MsgDocument doc, byte[] msg);
}
