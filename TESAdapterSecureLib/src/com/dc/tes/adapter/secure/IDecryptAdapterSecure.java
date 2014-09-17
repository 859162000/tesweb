package com.dc.tes.adapter.secure;

/**
 * 
 * 适配器 解密接口
 * 
 * @author 王春佳
 * 
 * @see 外部系统发送加密报文到该适配器，该适配器调用本加解密接口解析加密报文，将解析后的报文转发给核心
 */
public interface IDecryptAdapterSecure extends IAdapterSecure {

	/**
	 * 对接收的原始报文进行解密
	 * 
	 * @param msg
	 *            接收的原始报文
	 * @return 解密后的明文;如果解密失败,返回null
	 */
	public byte[] deCrypt(byte[] msg);
}
