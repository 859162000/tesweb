package com.dc.tes.adapter.host.remote;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.dc.tes.adapter.IListenerAdapter;
import com.dc.tes.adapter.ISenderAdapter;
import com.dc.tes.exception.RemoteHostErr;
import com.dc.tes.exception.TESException;
import com.dc.tes.net.Message;
import com.dc.tes.net.MessageItem;
import com.dc.tes.net.MessageType;
import com.dc.tes.util.InstanceCreater;
import com.dc.tes.util.RuntimeUtils;

/**
 * 远程适配器宿主的启动入口点
 * 
 * @author lijic
 * 
 */
abstract class RemoteHost {
	private static final Log log = LogFactory.getLog(RemoteHost.class);

	/**
	 * 适配器实例
	 */
	protected final Object adapter;
	/**
	 * 适配器名称
	 */
	protected final String name;
	/**
	 * 核心地址
	 */
	protected final String host;
	/**
	 * 核心端口
	 */
	protected final int port;

	/**
	 * 适配器状态
	 */
	protected boolean state;

	/**
	 * 初始化一个远程适配器宿主
	 * 
	 * @param adapter
	 *            适配器实例
	 * @param name
	 *            适配器名称
	 * @param host
	 *            核心地址
	 * @param port
	 *            核心端口
	 */
	RemoteHost(final Object adapter, final String name, final String host, final int port) {
		this.adapter = adapter;
		this.name = name;
		this.host = host;
		this.port = port;

		log.info("正在启动远程接收端通讯层...");

		// 适配器类型
		String pkg = adapter.getClass().getPackage().getName();
		String type = pkg.substring(pkg.lastIndexOf('.') + 1);
		if (adapter instanceof IListenerAdapter)
			type += ".s";
		else
			type += ".c";

		// 向核心注册
		log.info("正在向核心注册...");
		Message msg = new Message(MessageType.REG);
		msg.put(MessageItem.AdapterReg.CHANNELNAME, name);
		msg.put(MessageItem.AdapterReg.SIMTYPE, type);
		this.prepareRegMessage(msg);

		// 向核心发送消息并获取返回
		Message reply = this.send(msg, host, port)[0];

		// 注册程序关闭事件的监听器
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
			public void run() {
				try {
					log.info("正在关闭通讯层...");

					log.info("正在关闭适配器...");
					try {
						RemoteHost.this.stopAdapter();
					} catch (Exception ex) {
						log.error(new TESException(RemoteHostErr.AdapterStopFail, ex));
					}
					log.info("适配器已关闭.");

					log.info("正在从核心注销...");
					Message msg = new Message(MessageType.UNREG);
					msg.put(MessageItem.AdapterUnreg.CHANNELNAME, name);
					RemoteHost.this.send(msg, host, port);

					log.info("通讯层已关闭.");
				} catch (Exception ex) {
					log.error("关闭通讯层发生异常");
					log.error(ex);
				}
			}
		}));

		log.info("通讯层初始化成功.");

		log.info("正在启动适配器...");
		// 初始化适配器
		final byte[] config = reply.getBytes(MessageItem.AdapterReg.CONFIGINFO);

		// 启动适配器
		Thread t = new Thread(new Runnable() {
			public void run() {
				try {
					RemoteHost.this.startAdapter(config);
				} catch (Exception ex) {
					log.fatal(new TESException(RemoteHostErr.AdapterStartFail, ex));
					System.exit(-1);
				}
			}
		}, "adapterMain");
		t.start();

		try {
			t.join(5000);
		} catch (InterruptedException ex) {
			;// 在此处不可能发生异常
		}
		if (this.state)
			log.info("适配器启动成功...");
		else {
			log.error("适配器启动超时");
			System.exit(-1);
		}
	}

	/**
	 * 当适配器成功启动后会调用该函数进行报告
	 */
	public void Ready() {
		this.state = true;
	}

	/**
	 * 在派生类中重写时 该函数用于对注册报文进行一些修改以便适应具体情况
	 */
	protected void prepareRegMessage(Message msg) {
	}

	/**
	 * 在派生类中实现时 该函数用于启动适配器
	 */
	protected abstract void startAdapter(byte[] config);

	/**
	 * 在派生类中实现时 该函数用于停止适配器
	 */
	protected abstract void stopAdapter();

	/**
	 * 工具函数 用于向核心发送报文
	 * 
	 * @param msg
	 *            报文
	 * @param host
	 *            核心地址
	 * @param port
	 *            核心端口
	 * @return 核心返回的报文列表
	 */
	protected Message[] send(Message msg, String host, int port) {
		log.debug("MSG -> core " + msg);

		Socket socket = null;
		try {
			// 建立到核心的连接
			socket = new Socket(host, port);
		} catch (Exception ex) {
			throw new TESException(RemoteHostErr.CoreConnectFail, "addr: " + host + ":" + port, ex);
		}

		// 向核心发送报文
		try {
			socket.getOutputStream().write(msg.Export());
			socket.getOutputStream().flush();
		} catch (Exception ex) {
			try {
				socket.close();
			} catch (Exception ex1) {
				log.error(new TESException(RemoteHostErr.CoreCloseSocketFail, ex1));
			}

			throw new TESException(RemoteHostErr.CoreWriteFail, ex);
		}

		// 建立一个列表 用于保存从核心返回的报文列表
		ArrayList lst = new ArrayList();

		// 从核心读取返回报文
		try {
			do {
				// 从流中读取内部报文实例
				Message _msg = new Message(socket.getInputStream());

				log.debug("MSG <- core " + _msg);

				// 判断是否出错
				if (!_msg.getString(MessageItem.RESULT).startsWith("0"))
					// 如果核心报错则通常抛异常的方式将这个错误传给适配器
					throw new TESException(RemoteHostErr.CoreProcessFail, _msg.getString(MessageItem.ERRMSG));

				// 将成功读取到的报文加入到报文列表中
				lst.add(_msg);
				// 如果流里还有东西 则继续读
			} while (socket.getInputStream().available() > 0);
		} catch (Exception ex) {
			throw new TESException(RemoteHostErr.CoreReadFail, ex);
		} finally {
			try {
				socket.close();
			} catch (IOException ex) {
				log.error(new TESException(RemoteHostErr.CoreCloseSocketFail, ex));
			}
		}

		// 将列表中的东西转为数组
		Message[] result = new Message[lst.size()];
		for (int i = 0; i < lst.size(); i++)
			result[i] = (Message) lst.get(i);

		// 将接收到的报文列表返回给调用者
		return result;
	}

	/**
	 * 入口点
	 */
	public static void main(String[] args) {
		// false = Listener, true = Sender
		boolean adapterType;

		try {
			// 打开adapter.properties
			InputStream s = RuntimeUtils.OpenResource("adapter.properties");
			if (s == null)
				throw new TESException(RemoteHostErr.ConfigNotFound);

			Properties p = new Properties();
			try {
				p.load(s);
			} catch (Exception ex) {
				throw new TESException(RemoteHostErr.LoadConfigIOFail, ex);
			}

			// 读取adapter.properties
			Object adapter;
			String name;
			String host;
			int port;
			int localPort = 0;

			// 读取adapter
			String className = p.getProperty("adapter");
			if (className == null || className.length() == 0)
				throw new TESException(RemoteHostErr.LoadAdapterClassFail, className);
			try {
				// 创建适配器实例
				adapter = InstanceCreater.CreateInstance(className);

				// 判断适配器类型
				if (adapter instanceof IListenerAdapter)
					adapterType = false;
				else if (adapter instanceof ISenderAdapter)
					adapterType = true;
				else
					throw new TESException(RemoteHostErr.AdapterNotIAdapter, className);
			} catch (Exception ex) {
				throw new TESException(RemoteHostErr.CreateAdapterFail, ex);
			}

			// name
			name = p.getProperty("name");
			if (name == null || name.length() == 0)
				throw new TESException(RemoteHostErr.LoadAdapterNameFail, name);

			// host
			host = p.getProperty("host");
			if (host == null || host.length() == 0)
				throw new TESException(RemoteHostErr.LoadCoreAddrFail, host);

			// port
			String _port = p.getProperty("port");
			try {
				port = Integer.parseInt(_port);
				if (port < 0)
					throw new TESException(RemoteHostErr.CorePortMustPositive, _port);
			} catch (NumberFormatException ex) {
				throw new TESException(RemoteHostErr.LoadCorePortFail, _port);
			}
			// localPort
			if (adapterType) {
				String _localPort = p.getProperty("localPort");
				try {
					localPort = Integer.parseInt(_localPort);
					if (port < 0)
						throw new TESException(RemoteHostErr.LocalPortMustPositive, _localPort);
				} catch (NumberFormatException ex) {
					throw new TESException(RemoteHostErr.LoadLocalPortFail, _localPort);
				}
			}
			// 启动远程适配器宿主
			if (adapterType)
				new RemoteSenderAdapterHost((ISenderAdapter) adapter, name, host, port, localPort);
			else
				new RemoteListenerAdapterHost((IListenerAdapter) adapter, name, host, port);
		} catch (Throwable ex) {
			log.fatal(ex);
			System.exit(-1);
		}
	}
}
