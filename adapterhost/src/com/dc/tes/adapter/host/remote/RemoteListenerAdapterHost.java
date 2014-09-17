package com.dc.tes.adapter.host.remote;

import com.dc.tes.adapter.IListenerAdapter;
import com.dc.tes.adapter.IListenerAdapter.IReplyer;
import com.dc.tes.adapter.host.IListenerAdapterHost;
import com.dc.tes.exception.RemoteHostErr;
import com.dc.tes.exception.TESException;
import com.dc.tes.net.Message;
import com.dc.tes.net.MessageItem;
import com.dc.tes.net.MessageType;

/**
 * 远程接收端适配器宿主
 * 
 * @author lijic
 * 
 */
class RemoteListenerAdapterHost extends RemoteHost implements IListenerAdapterHost {
	private ThreadLocal plog = new ThreadLocal();

	/**
	 * 初始化远程接收端适配器宿主
	 * 
	 * @param adapter
	 *            适配器实例对象
	 * @param name
	 *            适配器名称
	 * @param host
	 *            核心地址
	 * @param port
	 *            核心端口
	 */
	RemoteListenerAdapterHost(final IListenerAdapter adapter, String name, String host, int port) {
		super(adapter, name, host, port);
	}

	/**
	 * 向核心发送从被测系统接收到的报文
	 * 
	 * @param bytes
	 *            被测系统发来的报文字节流
	 * @param receiveTime
	 *            接收到该请求时的时间
	 * @return 将向被测系统返回的报文
	 */
	public byte[] SendCoreMessage(byte[] bytes, long receiveTime) {
		final byte[][] buffer = new byte[1][];

		this.SendCoreMessage(bytes, receiveTime, new IReplyer() {
			public void Reply(byte[] bytes) {
				buffer[0] = bytes;
			}
		});

		return buffer[0];
	}

	/**
	 * 向核心发送从被测系统接收到的报文
	 * 
	 * @param bytes
	 *            被测系统发来的报文字节流
	 * @param receiveTime
	 *            接收到该请求时的时间
	 * @param replyer
	 *            响应接口
	 */
	public void SendCoreMessage(byte[] bytes, long receiveTime, IReplyer replyer) {
		// 准备向核心发送的报文
		Message msg = new Message(MessageType.MESSAGE);
		msg.put(MessageItem.AdapterMessage.CHANNELNAME, this.name);
		msg.put(MessageItem.AdapterMessage.REQMESSAGE, bytes);
		msg.put(MessageItem.AdapterMessage.PLOG, (String) this.plog.get()); // 将上一次交易的性能信息送给核心

		// 记录时间戳
		long t1 = receiveTime; // 这是接收到请求的时间

		// 向核心发送消息并获取返回
		Message[] replies = this.send(msg, this.host, this.port);

		// 获取并计算延时时间
		int delay = replies[0].getInteger(MessageItem.AdapterMessage.DELAYTIME);

		long t2 = System.currentTimeMillis(); // 这是核心处理完毕的时间
		long t = delay - (t2 - t1); // 这是应该执行的延时时间

		// 进行延时
		if (t > 0)
			try {
				Thread.sleep(t);
			} catch (InterruptedException ex) {
				throw new TESException(RemoteHostErr.DelayFail, ex);
			}

		// 调用接口 将报文依次返回给适配器
		for (int i = 0; i < replies.length; i++)
			replyer.Reply(replies[i].getBytes(MessageItem.AdapterMessage.RESMESSAGE));

		// 将本次交易的信息记录到plog中 在下次请求的时候带过去
		long t3 = System.currentTimeMillis(); // 交易彻底处理完毕的时间
		String plog = replies[0].getString(MessageItem.AdapterMessage.PLOG);
		plog += String.valueOf(t2 - t1) + "," + String.valueOf(t3 - t1);
		this.plog.set(plog);
	}

	protected void startAdapter(byte[] config) {
		((IListenerAdapter) this.adapter).Start(this, config);
	}

	protected void stopAdapter() {
		((IListenerAdapter) this.adapter).Stop();
	}
}
