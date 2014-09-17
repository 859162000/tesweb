package com.dc.tes.channel.remote;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.dc.tes.channel.remote.IRemoteChannel.IRemoteReplyer;
import com.dc.tes.exception.ChannelNotFoundException;
import com.dc.tes.net.Message;
import com.dc.tes.net.MessageItem;
import com.dc.tes.net.MessageType;
import com.dc.tes.util.RuntimeUtils;

/**
 * 用于处理远程适配器请求的线程
 * 
 * @author lijic
 * 
 */
public class ChannelServerProcessThread extends Thread implements IRemoteReplyer {
	private static final Log log = LogFactory.getLog(ChannelServerProcessThread.class);

	/**
	 * 操作的Socket
	 */
	private Socket m_socket;

	/**
	 * 初始化一条用于处理远程适配器请求的线程
	 * 
	 * @param socket
	 *            在其上进行操作的Socket
	 */
	public ChannelServerProcessThread(Socket socket) {
		this.m_socket = socket;
	}

	@Override
	public void run() {
		try {
			log.debug("接到远程通道消息：" + this.m_socket.getRemoteSocketAddress());

			InputStream is = this.m_socket.getInputStream();
			if (is == null) {
				log.debug("接收到的远程通道消息为空！");
				return;
			}
			
			// 接收请求报文
			Message msg = new Message(is);

			msg.put(MessageItem.REMOTE_HOST, this.m_socket.getInetAddress().getHostAddress());
			msg.put(MessageItem.REMOTE_PORT, this.m_socket.getPort());

			// 取通道
			String channelName = msg.getString(MessageItem.AdapterMessage.CHANNELNAME);
			if (!ChannelServer.s_channels.containsKey(channelName)) {
				// 如果通道不存在 则返回一段错误消息 并抛出异常
				ChannelNotFoundException ex = new ChannelNotFoundException(channelName);
				Message errReply = new Message(MessageType.REG);
				errReply.put(MessageItem.RESULT, 1);
				errReply.put(MessageItem.ERRMSG, RuntimeUtils.PrintEx(ex));
				this.m_socket.getOutputStream().write(errReply.Export());
				throw ex;
			}
			IRemoteChannel channel = ChannelServer.s_channels.get(channelName);

			// 处理请求
			channel.Process(msg, this);
		} catch (Exception ex) {
			log.error("处理远程通道消息时发生异常", ex);
			try {
				this.m_socket.close();
			} catch (IOException ex1) {
				log.error("关闭远程通道Socket时发生异常", ex1);
			}
		}
	}

	@Override
	public void ReplyWithEx(Message msg) throws Exception {
		log.debug("向通道返回的报文：" + msg);
		log.debug(RuntimeUtils.PrintHex(msg.Export(), RuntimeUtils.utf8));

		try {
			// 向socket中写入返回报文
			// 如果写的过程中出现异常 则把该异常交给调用本接口的类去处理
			// 这样如果向适配器送响应的过程中发生异常 可以被核心或其它部分捕获并处理
			this.m_socket.getOutputStream().write(msg.Export());
			this.m_socket.getOutputStream().flush();
		} catch (Exception ex) {
			log.debug("因为发生异常，与适配器的连接被关闭");
			ex.printStackTrace();
			this.m_socket.close();
			this.m_socket = null;
			throw ex;
		} finally {
			// 如果PACKSEQ==0 则关掉socket
			// PACKSEQ不等于0表示还有后续的回复报文 此时需要继续向流中写
			if (msg.getInteger(MessageItem.AdapterMessage.PACKSEQ) == 0)
				if (this.m_socket != null)
					;//this.m_socket.close();
		}
	}

	@Override
	public void Reply(Message msg) {
		log.debug("向通道返回的报文：" + msg);

		try {
			// 向socket中写入返回报文
			// 如果写的过程中出现异常 则把该异常抓掉 并记日志
			this.m_socket.getOutputStream().write(msg.Export());
			this.m_socket.getOutputStream().flush();
		} catch (Exception ex) {
			log.error("向远程通道中写入时发生异常", ex);

			try {
				log.debug("因为发生异常，与适配器的连接被关闭");
				this.m_socket.close();
			} catch (Exception ex1) {
				log.error("关闭远程通道Socket连接时发生异常", ex1);
			}
			this.m_socket = null;
		} finally {
			// 如果PACKSEQ==0 则关掉socket 
			// PACKSEQ不等于0表示还有后续的回复报文 此时需要继续向流中写
			if (msg.getInteger(MessageItem.AdapterMessage.PACKSEQ) == 0)
				try {
					if (this.m_socket != null)
						;//this.m_socket.close();
				} catch (Exception ex) {
					log.error("关闭远程通道Socket连接时发生异常", ex);
				}
		}
	}
}
