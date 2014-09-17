package com.dc.tes.gha;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import com.dc.tes.adapterapi.Adapter2Tes;

/**
 * 测试用TCP SERVER
 * 将接收到的报文存入"REQ"字段后进行返回
 * @author Conan
 *
 */
public class TcpServerStub implements Runnable {
	/**
	 * 监听套接字
	 */
	private ServerSocket ssk;

	/**
	 * 通讯套接字
	 */
	private Socket sk;

	/**
	 * 要返回的消息体
	 */
	private byte[] backContent;

	/**
	 * 构造函数
	 * @param ssk	监听套接字
	 * @param len	待接收消息的长度
	 * @param backContent	为初始化的消息体
	 */
	public TcpServerStub(ServerSocket ssk, byte[] backContent) {
		this.ssk = ssk;
		this.backContent = backContent;
	}

	/**
	 * 按给定长度接收消息
	 */
	public void run(){
		try {
			int i = 0;
			while(i<2)
			{
				//建立连接
				this.sk = this.ssk.accept();
				this.sk.setTcpNoDelay(true);
				this.sk.setKeepAlive(true);
				InputStream ips = sk.getInputStream();
				OutputStream out = sk.getOutputStream();
				//接收报文
				byte[] buff; // 接收缓冲区
				byte[] trans; // 转换缓冲区
				int nsize, tsize, recvlen;

				nsize = 0;
				tsize = 30;
				trans = new byte[tsize];
				while (nsize < tsize) {
					recvlen = ips.read(trans, nsize, tsize - nsize);
					nsize += recvlen;
					if (recvlen <= 0) {
						return;
					}
				}
				buff = new byte[10];
				System.arraycopy(trans, 10, buff, 0, 10);
				tsize = Integer.parseInt(new String(buff));
				buff = new byte[tsize];
				System.arraycopy(trans, 0, buff, 0, 30);
				while (nsize < tsize) {
					recvlen = ips.read(buff, nsize, tsize - nsize);
					nsize += recvlen;
					if (recvlen <= 0) {
						return;
					}
				}
				//将接收到的报文存入“REQ字段”并进行返回
//				this.backContent = Adapter2Tes.addContent(this.backContent, "REQ", buff);
				out.write(this.backContent);
				i++;
			}
			this.sk.close();
			this.ssk.close();

		} catch (Exception e) {
			e.printStackTrace();
			return;
		}	
	}
}
