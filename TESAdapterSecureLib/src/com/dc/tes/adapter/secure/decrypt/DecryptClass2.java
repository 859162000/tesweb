package com.dc.tes.adapter.secure.decrypt;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.dc.tes.adapter.secure.IDecryptAdapterSecure;

/**
 * 解密接口实现
 * 
 * @author 王春佳
 * 
 * @see
 *  该类主要用于 安全框架结构 测试
 */
public class DecryptClass2 implements IDecryptAdapterSecure{

	private static Log logger = LogFactory.getLog(DecryptClass2.class);
	
	public byte[] deCrypt(byte[] msg) {
		logger.info("解密实现类: DecryptClass2");
		return msg;
	}

}
