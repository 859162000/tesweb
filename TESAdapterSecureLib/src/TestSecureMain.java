import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.dc.tes.adapter.secure.AbstractFactory;
import com.dc.tes.adapter.secure.IDecryptAdapterSecure;
import com.dc.tes.adapter.secure.IEncryptAdapterSecure;

public class TestSecureMain {

	private static Log logger = LogFactory.getLog(TestSecureMain.class);

	public static void main(String[] args) {
		String httpClass = "com.dc.tes.adapter.secure.factory.HttpReplyFactory";

		byte[] encryptByte = "加解密测试字符串hello".getBytes();

		logger.info("Http适配器 加密开始..." + new String(encryptByte));
		IEncryptAdapterSecure iEncrypt = AbstractFactory.getInstance(httpClass)
				.getEncryptAdapterSecure();
		byte[] encryptedByte = iEncrypt.enCrypt(encryptByte);
		logger.info("Http适配器 加密结束..." + new String(encryptedByte));

		logger.info("Http适配器 解密开始..." + new String(encryptedByte));
		IDecryptAdapterSecure iDecrypt = AbstractFactory.getInstance(httpClass)
				.getDecryptAdapterSecure();
		byte[] decryptedByte = iDecrypt.deCrypt(encryptedByte);
		logger.info("Http适配器 解密结束..." + new String(decryptedByte));

	}

}
