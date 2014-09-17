package com.dc.tes.adapter.secure.encrypt;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.dc.tes.adapter.secure.IEncryptAdapterSecure;

/**
 * 加密接口实现
 * 
 * @author 王春佳
 * 
 * @see 该类主要用于 安全框架结构 测试
 */
public class EncryptClass3 implements IEncryptAdapterSecure{

	private static Log logger = LogFactory.getLog(EncryptClass3.class);
	
	public byte[] enCrypt(byte[] msg) {
		logger.info("加密实现类: EncryptClass3");
		return msg;
	}

}
