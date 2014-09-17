package com.dc.tes.adapter.secure.decrypt;

import org.bouncycastle.crypto.CryptoException;

import com.dc.tes.adapter.secure.IDecryptAdapterSecure;
import com.dc.tes.adapter.secure.lib.DES4BouncyCastle;

public class DecryptByDES implements IDecryptAdapterSecure{

	public byte[] deCrypt(byte[] msg) {

		byte[] deCryptByte = null;

		// key
		final String keyStr = "publicKeyString";

		DES4BouncyCastle des4BouncyCastle = new DES4BouncyCastle(keyStr);

		try {
			deCryptByte = des4BouncyCastle.decrypt(msg);
		} catch (CryptoException e) {
			e.printStackTrace();
			return null;
		}
		return deCryptByte;
	}

}
