import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import com.utils.HexBinary;
import com.utils.StringUtils;


public class Arqc {
	
	//DES加密
	public static String DES(String hexKey, String hexData, int mode) throws Exception
	{
		SecretKey desKey = new SecretKeySpec(HexBinary.decode(hexKey), "DES");

		Cipher cp = Cipher.getInstance("DES/ECB/NoPadding");
		cp.init(mode, desKey);
		byte[] bytes = cp.doFinal(HexBinary.decode(hexData));

		return HexBinary.encode(bytes);
	}
	
	//3DES加密
	public static String DESede(String hexKey, String hexData) throws Exception
	{
		byte[] key16 = HexBinary.decode(hexKey);
		byte[] key24 = new byte[24];
		System.arraycopy(key16, 0, key24, 0, 16);
		System.arraycopy(key16, 0, key24, 16, 8);
		
		SecretKey desKey = new SecretKeySpec(key24, "DESede");

		Cipher cp = Cipher.getInstance("DESede/ECB/NoPadding");
		cp.init(Cipher.ENCRYPT_MODE, desKey);
		byte[] bytes = cp.doFinal(HexBinary.decode(hexData));

		return HexBinary.encode(bytes);
	}
	
	/**
	 * 生成过程密钥   算法参考《数据安全传输控制规范》
	 * 
	 * @param pan 账号
	 * @param panSN 2位账号序列 
	 * @param hexATC 4位16进制 ATC
	 * @param mainKey 主密钥
	 * @return
	 * @throws Exception
	 */
	private static String generateProcesKey(String pan, String panSN, String hexATC, String mainKey) throws Exception
	{
		String D1 = pan + panSN;
		int D1Length = D1.length();

		String D1Right16 = pan.substring(D1Length - 16);

		// 对分散因子取反
		String D2 = StringUtils.reversBytes(D1Right16);

		StringBuffer dispersionBuffer = new StringBuffer();

		dispersionBuffer.append(D1Right16).append(D2);

		// 生成子密钥
		String subKey = DESede(mainKey, dispersionBuffer.toString());

		String paddATC = StringUtils.padding(hexATC, "left", "0", 16);

		String reversATC = StringUtils.reversBytes(hexATC);

		String paddReversATC = StringUtils.padding(reversATC, "left", "0", 16);

		String mergerATC = paddATC + paddReversATC;

		// 生成过程密钥
		String processKey = DESede(subKey, mergerATC);

		return processKey;
	}
	
	/**
	 * 用过程密钥对数据源进行加密
	 * @param processKey
	 * @param macDataSource
	 * @return 返回AC
	 * @throws Exception
	 */
	private static String process(String processKey, String macDataSource) throws Exception
	{

		if (null == processKey || processKey.equals("") 
				|| processKey.length() != 32) {
			throw new IllegalArgumentException("过程密钥不能为空或不够32位!");
		}

		String leftKey = processKey.substring(0, 16);

		String rightKey = processKey.substring(16);

		// 数据源，每组16位hex(8 byte())
		String[] ds = splitData(macDataSource);

		String des = "";

		//每组做DES加密，最后一组做3DES加密
		for (int i = 0; i < ds.length; i++) {
			// 用上一次 DES加密结果对 第 i 组数据做异或，在进行DES加密
			if (i == 0) {
				des = DES(leftKey, ds[i], Cipher.ENCRYPT_MODE).toUpperCase();
			} else {	
				des = StringUtils.XOR(des, ds[i]);
				des = DES(leftKey, des, Cipher.ENCRYPT_MODE).toUpperCase();
			}
		}
		// DES 加密最终结果用processKey后16位解密
		des = DES(rightKey, des, Cipher.DECRYPT_MODE).toUpperCase();
		// 解密后 再用processKey前16位加密
		des = DES(leftKey, des, Cipher.ENCRYPT_MODE).toUpperCase();
		return des;

	}

	private static String[] splitData(String hexMacDataSource)
	{
		int len = 0;

		int modValue = hexMacDataSource.length() % 16;

		if (modValue == 0) {
			// 补上80000000000000
			hexMacDataSource += "80000000000000";
			len = hexMacDataSource.length() / 16;
		} else if (modValue == 14) {
			// 补上80
			hexMacDataSource += "80";
			len = hexMacDataSource.length() / 16;
		} else {
			hexMacDataSource += "80";
			int hexSrcDataLen = hexMacDataSource.length();
			int totalLen = hexSrcDataLen + (16 - modValue - 2);
			hexMacDataSource = StringUtils.padding(hexMacDataSource, "right", "0", totalLen);
			len = hexMacDataSource.length() / 16;
		}

		String[] ds = new String[len];

		for (int i = 0; i < ds.length; i++)
		{
			if (hexMacDataSource.length() >= 16) {
				ds[i] = hexMacDataSource.substring(0, 16);
				hexMacDataSource = hexMacDataSource.substring(16);
			} else {
				throw new IllegalArgumentException("填充的数据非法!");
			}
		}
		return ds;
	}

}
