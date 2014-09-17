package com.dc.tes.channel.test;

import java.io.IOException;
import java.net.Socket;
import java.util.Date;

import com.dc.tes.channel.localchannel.AbstractLocalProcess;
import com.dc.tes.channel.localchannel.LocalProcessFactory;
import com.dc.tes.net.Message;
import com.dc.tes.net.MessageItem;
import com.dc.tes.net.MessageType;


/**
 * 数据访问会话,长连接
 * @author songljb
 *
 */
public class DataDialog implements Runnable {
	private Socket socket = null;
	private String sysname;

	public DataDialog(Socket s) {
		socket = s;
	}

	public void run() {
		try {
			System.out.println("接收到核心消息");
			Date d1 = new Date();
			    Message req = new Message(socket.getInputStream());
			    req.put(MessageItem.AdapterMessage.PLOG, TCPSnooping.getPlog());
				AbstractLocalProcess lp = LocalProcessFactory.CreateProcess();

				System.out.println(new String(req.Export()));
				Message[] bm = lp.process(req);
//				System.out.println(new String(bm[0].Export()));
				long millis = bm[0].getInteger(MessageItem.AdapterMessage.DELAYTIME);
				
				String plog = bm[0].getString(MessageItem.AdapterMessage.PLOG);

				Date d2 = new Date();

				long s = (d2.getTime() - d1.getTime());
				Thread.sleep(millis -s);
				TCPSnooping.setPlog(plog+","+millis+","+s);
				System.out.println(plog);
				socket.getOutputStream().write(bm[0].Export());
				socket.close();

		} catch (Exception e) {//断开连接时,将系统删除
			e.printStackTrace();
		} finally {
			// 关闭socket
			try {
				if (socket != null)
					socket.close();
			} catch (IOException ex) {
				ex.printStackTrace();
				throw new RuntimeException("通道连接关闭失败", ex);
			}
		}
	}
}
