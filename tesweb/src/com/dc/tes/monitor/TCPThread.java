package com.dc.tes.monitor;

/**
 * 用于启动tcp的线程
 * @author songljb
 *
 */
public class TCPThread implements Runnable {
	public void run() {
		TCPSnooping tcps = new TCPSnooping();
		tcps.listening();
	}
}
