package com.dc.tes.adapterapi;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;

public class Adapter2Tes {

	public static byte[] messageInit() throws Exception {
		return new byte[1024];
	}

	public static byte[] addContent(byte[] message,
			String name, byte[] content) throws Exception{
		return new byte[1024];
	}

	public static byte[] readContent(byte[] message, String name)
	throws Exception{
		return Adapter2Tes.readFromFile(name);
	}

	public static byte[] reg2tes(byte[] regC) throws Exception{
		return new byte[1024];
	}

	public static byte[] unreg2tes() throws Exception{
		return new byte[1024];
	}
	
	public static byte[] sendContent(byte[] sendC) throws Exception{
		return new byte[1024];
	}

	public static byte[] tcpSend(byte[] message, String IP, int port) throws Exception{
		return new byte[1024];
	}

	public static byte[] readFromFile(String fileName) throws Exception{
		fileName = System.getProperty("testfilename");
		byte[] content = null;
		try{
			BufferedInputStream in = new BufferedInputStream(new FileInputStream(fileName));
			ByteArrayOutputStream out = new ByteArrayOutputStream(64*1024);

			byte[] temp = new byte[1024];
			int size = 0;
			while ((size = in.read(temp)) != -1) {
				out.write(temp, 0, size);
			}
			in.close();
			content = out.toByteArray();
		}catch(Exception e){
			throw new Exception("0x0D15：读取文件["+fileName+"]时出现异常！["+e.getMessage()+"]");
		}
		return content;
	}

}
