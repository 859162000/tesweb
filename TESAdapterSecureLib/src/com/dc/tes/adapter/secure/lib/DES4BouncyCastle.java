package com.dc.tes.adapter.secure.lib;

import org.bouncycastle.crypto.BufferedBlockCipher;
import org.bouncycastle.crypto.CryptoException;
import org.bouncycastle.crypto.engines.BlowfishEngine;
import org.bouncycastle.crypto.modes.CBCBlockCipher;
import org.bouncycastle.crypto.modes.PaddedBlockCipher;
import org.bouncycastle.crypto.params.KeyParameter;

public class DES4BouncyCastle {

	private BufferedBlockCipher cipher;
	private KeyParameter key;

	// 初始化加密引擎.
	// 数组key的长度至少应该是8个字节.
	public DES4BouncyCastle(byte[] key) {
		cipher = new PaddedBlockCipher(new CBCBlockCipher(new BlowfishEngine()));
		this.key = new KeyParameter(key);
	}

	public DES4BouncyCastle(String key) {
		this(key.getBytes());
	}

	// 做加密解密的具体工作
	private byte[] callCipher(byte[] data) throws CryptoException {
		int size = cipher.getOutputSize(data.length);
		byte[] result = new byte[size];
		int olen = cipher.processBytes(data, 0, data.length, result, 0);
		olen += cipher.doFinal(result, olen);

		if (olen < size) {
			byte[] tmp = new byte[olen];
			System.arraycopy(result, 0, tmp, 0, olen);
			result = tmp;
		}

		return result;
	}

	// 加密任意的字节数组，以字节数组的方式返回被加密的数据
	public synchronized byte[] encrypt(byte[] data) throws CryptoException {
		if (data == null || data.length == 0) {
			return new byte[0];
		}

		cipher.init(true, key);
		return callCipher(data);
	}

	// 加密一个字符串
	public byte[] encryptString(String data) throws CryptoException {
		if (data == null || data.length() == 0) {
			return new byte[0];
		}

		return encrypt(data.getBytes());
	}

	// 解密一个字节数组
	public synchronized byte[] decrypt(byte[] data) throws CryptoException {
		if (data == null || data.length == 0) {
			return new byte[0];
		}

		cipher.init(false, key);
		return callCipher(data);
	}

	// 解密一个字符串
	public String decryptString(byte[] data) throws CryptoException {
		if (data == null || data.length == 0) {
			return "";
		}

		return new String(decrypt(data));
	}

}
