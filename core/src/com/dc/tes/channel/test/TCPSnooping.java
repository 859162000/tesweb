package com.dc.tes.channel.test;

import java.net.ServerSocket;
import java.util.Date;


/**
 * tcp侦听总线
 * @author songljb
 *
 */
public class TCPSnooping {
	//系统名称\ 系统的log 纪录

	private ServerSocket socket = null;
	private boolean listening = false;
	public static String  plog;

	public TCPSnooping() {
		this.init();
	}
	

	public void init() {
		int port = 3344;
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
	
	public static void main(String args[]){
		TCPSnooping tt = new TCPSnooping();
		tt.listening();
	}


	public static String getPlog() {
		return TCPSnooping.plog;
	}


	public static void setPlog(String plog) {
			TCPSnooping.plog = plog;	
	}
	

}
