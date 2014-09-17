package com.dc.tes.adapter.host.remote;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.dc.tes.adapter.ISenderAdapter;
import com.dc.tes.adapter.host.ISenderAdapterHost;
import com.dc.tes.exception.RemoteHostErr;
import com.dc.tes.exception.TESException;
import com.dc.tes.net.Message;
import com.dc.tes.net.MessageItem;
import com.dc.tes.net.ReplyMessage;

/**
 * 远程发起端适配器宿主
 * 
 * @author lijic
 * 
 */
class RemoteSenderAdapterHost extends RemoteHost implements ISenderAdapterHost {
	private static final Log log = LogFactory.getLog(RemoteSenderAdapterHost.class);

	/**
	 * 本地监听端口
	 */
	private final int localPort;
	/**
	 * 本地Socket服务器
	 */
	private ServerSocket m_server;
	/**
	 * 一个标志 表示Socket服务器是否已经被关闭
	 */
	private boolean m_closeFlag;

	RemoteSenderAdapterHost(ISenderAdapter adapter, String name, String host, int port, int localPort) {
		super(adapter, name, host, port);
		this.localPort = localPort;
	}

	protected void startAdapter(byte[] config) {
		// 启动用于监听核心请求的本地Server
		try {
			this.m_server = new ServerSocket(this.localPort);
		} catch (Exception ex) {
			throw new TESException(RemoteHostErr.LocalServerStartFail, ex);
		}

		// 在新线程进行监听
		new Thread(new Runnable() {
			public void run() {
				while (true) {
					// 监听核心发来的请求
					final Socket socket;
					try {
						socket = RemoteSenderAdapterHost.this.m_server.accept();
					} catch (IOException ex) {
						if (RemoteSenderAdapterHost.this.m_closeFlag)
							; // 如果m_flag为false 则说明此次异常是因为SocketServer被关闭而引发 这是正常情况
						else
							log.error(new TESException(RemoteHostErr.LocalServerAcceptFail, ex).getMessage());

						return;
					}

					// 每个请求起一个新线程
					new Thread(new Runnable() {
						public void run() {
							Message msg;
							try {
								// 接收核心发来的报文
								msg = new Message(socket.getInputStream());
							} catch (Exception ex) {
								log.error(new TESException(RemoteHostErr.LocalServerReadFail, ex));

								try {
									socket.close();
								} catch (Exception ex1) {
									log.error(new TESException(RemoteHostErr.LocalServerCloseSocketFail, ex1));
								}
								return;
							}

							// 调用适配器处理该报文
							byte[] bytes;
							ReplyMessage reply = new ReplyMessage(msg);
							try {
								bytes = ((ISenderAdapter) RemoteSenderAdapterHost.this.adapter).Send(msg.getBytes(MessageItem.AdapterMessage.REQMESSAGE));

								reply.put(MessageItem.AdapterMessage.RESMESSAGE, bytes);
							} catch (Exception ex) {
								ex = new TESException(RemoteHostErr.AdapterSendFail, ex);
								log.warn(ex);
								reply.setEx(ex);
							}

							// 将报文返回给核心
							try {
								socket.getOutputStream().write(reply.Export());
							} catch (Exception ex) {
								log.error(new TESException(RemoteHostErr.LocalServerWriteFail, ex));
							} finally {
								try {
									socket.close();
								} catch (Exception ex1) {
									log.error(new TESException(RemoteHostErr.LocalServerCloseSocketFail, ex1));
								}
							}
						}
					}).start();
				}

			}
		}, "RemoteHostServer").start();

		// 初始化适配器
		((ISenderAdapter) this.adapter).Start(this, config);
	}

	protected void stopAdapter() {
		// 停止适配器
		((ISenderAdapter) this.adapter).Stop();

		// 停止本地Socket服务器
		this.m_closeFlag = true;
		try {
			this.m_server.close();
		} catch (IOException ex) {
			throw new TESException(RemoteHostErr.LocalServerStopFail, ex);
		}
	}
}
