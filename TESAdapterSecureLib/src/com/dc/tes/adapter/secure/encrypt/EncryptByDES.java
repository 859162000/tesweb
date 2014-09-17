package com.dc.tes.adapter.secure.encrypt;

import org.bouncycastle.crypto.CryptoException;

import com.dc.tes.adapter.secure.IEncryptAdapterSecure;
import com.dc.tes.adapter.secure.lib.DES4BouncyCastle;

public class EncryptByDES implements IEncryptAdapterSecure {

	public byte[] enCrypt(byte[] msg) {
		byte[] enCryptByte = null;

		// key
		final String keyStr = "publicKeyString";

		DES4BouncyCastle des4BouncyCastle = new DES4BouncyCastle(keyStr);

		try {
			enCryptByte = des4BouncyCastle.encrypt(msg);
		} catch (CryptoException e) {
			e.printStackTrace();
			return null;
		}
		return enCryptByte;
	}

}
