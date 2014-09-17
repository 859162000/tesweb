package com.dc.tes;

/**
 * 定义核心在等待请求时使用的锁
 * 
 * @author lijic
 * 
 */
public class ListenerLock {
	/**
	 * 锁计数 当该计数为0时销毁当前锁
	 */
	public int count;
	/**
	 * 接收到的报文
	 */
	public InMessage msg;	
	/**
	 * 待发的报文
	 */
	public OutMessage outmsg;
	/**
	 * 接收该请求的线程
	 */
	public Thread t;
}
