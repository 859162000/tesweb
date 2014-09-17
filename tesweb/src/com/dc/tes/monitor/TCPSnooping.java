package com.dc.tes.monitor;

import java.io.IOException;
import java.net.ServerSocket;
import com.dc.tes.monitor.data.Config;


/**
 * tcp侦听总线
 * @author songljb
 *
 */
public class TCPSnooping {
	//系统名称\ 系统的log 纪录

	private static ServerSocket socket = null;
	private static boolean listening = false;

	public TCPSnooping() {
		this.init();
	}

	public void init() {
		int port = Config.port;
		try {
			socket = new ServerSocket(port);
		} catch (Exception e) {
			throw new RuntimeException("侦听端口出错,端口" + port + "可能被占用", e);
		}
	}

	public void listening() {
		listening = true;
		try {
			while (listening) {
				new Thread(new DataDialog(socket.accept())).start();
			}
		} catch (Exception e) {
			throw new RuntimeException("侦听过程中出错", e);
		}

	}
	
	public static void close() {
		try {
			if(socket != null) {
				socket.close();
				listening = false;
				System.out.println("关闭监听服务");
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("关闭监听socket失败");
		}
	}
	
	public static void main(String args[]){
		TCPSnooping tt = new TCPSnooping();
		tt.listening();
	}
	

}
