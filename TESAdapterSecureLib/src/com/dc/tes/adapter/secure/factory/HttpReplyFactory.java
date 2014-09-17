package com.dc.tes.adapter.secure.factory;

import com.dc.tes.adapter.secure.AbstractFactory;
import com.dc.tes.adapter.secure.IDecryptAdapterSecure;
import com.dc.tes.adapter.secure.IEncryptAdapterSecure;
import com.dc.tes.adapter.secure.decrypt.DecryptByDES;
import com.dc.tes.adapter.secure.decrypt.DecryptClass1;
import com.dc.tes.adapter.secure.encrypt.EncryptByDES;
import com.dc.tes.adapter.secure.encrypt.EncryptClass1;

public class HttpReplyFactory extends AbstractFactory{

	public IDecryptAdapterSecure getDecryptAdapterSecure() {
		//生成实现 解密接口的类 实例
		return (IDecryptAdapterSecure)new DecryptByDES();
	}

	public IEncryptAdapterSecure getEncryptAdapterSecure() {
		//生成实现 加密接口的类 实例
		return (IEncryptAdapterSecure)new EncryptByDES();
	}

}
