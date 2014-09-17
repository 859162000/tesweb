package com.dc.tes.adapter.secure;

/**
 * 
 * 适配器 加密接口
 * 
 * @author 王春佳
 * @see
 *  核心发送的请求明文到该适配器,该适配器调用本加解密接口加密请求报文,将加密后的报文转发给外部系统
 */
public interface IEncryptAdapterSecure extends IAdapterSecure {

	/**
	 * 对发送的原始报文进行加密
	 * 
	 * @param msg
	 *            发送的原始报文
	 * @return 加密后的密文;如果加密失败,返回null
	 */
	public byte[] enCrypt(byte[] msg);
}
