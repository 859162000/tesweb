package com.dc.tes.adapter.remote;

import java.io.IOException;
import java.net.Socket;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.dc.tes.adapter.IReplyAdapterWorker;
import com.dc.tes.adapter.helper.IReplyAdapterHelper;
import com.dc.tes.adapter.util.ConfigHelper;
import com.dc.tes.net.jre14.MessageType;
import com.dc.tes.net.jre14.ReplyMessage;


/**
 * "远程通道"方式,服务端适配器与核心进行数据交互的 实体类
 * 
 * @author guhb,王春佳
 * 
 * @see sendToCore: 向核心发送一笔请求数据,返回一笔响应数据
 * @see sendToCoreWithMultiResponse: 向核心发送一笔请求数据,返回多笔响应数据
 * @see SendToCoreRaw: 界面通过此函数,向核心发送数据
 */
public class DefaultReplyAdapterHelper extends AbstractAdapterHelper implements	IReplyAdapterHelper {
	
	private static Log log = LogFactory.getLog(DefaultReplyAdapterHelper.class);

	//配置信息名值对
	protected Properties m_config_props = null;
	
	public void SetConfigProperty(Properties props) {
		m_config_props = props;
	}

		
	/**
	 * 构造函数
	 * 
	 * @throws Exception
	 *             当无法获得本地配置信息时抛出异常
	 */
	public DefaultReplyAdapterHelper(Properties props) {
		super(props);
	}

	public byte[] sendToCore(byte[] realMsg) {
		if (null == realMsg) {
			log.error("适配器向TES发送请求的消息为空！");
			return null;
		}

		try {
			// 建立到适配器的连接
			Socket socket = new Socket(m_coreIP, m_corePort);

			m_msgOfData.put("CHANNELNAME", m_localChannelName
					.getBytes(ConfigHelper.getEncoding()));
			m_msgOfData.put("REQMESSAGE", realMsg);

			m_msgOfDataReply = AbstractAdapterHelper.SendToCore(socket,
					m_msgOfData.Export(), m_coreIP, m_corePort);

			socket.close();
			log.debug("与核心通信完成，关闭连接.");

		} catch (Exception e) {
			// 应该不会执行到这里?
			m_msgOfDataReply = new ReplyMessage(MessageType.MESSAGE);
			m_msgOfDataReply.setEx(e, "向核心发送数据失败");
			log.error("通道名称编码失败!");
		}

		if (m_msgOfDataReply.isOK()) {
			return m_msgOfDataReply.responseMsg();
		} else {
			log.error("TES提示：" + new String(m_msgOfDataReply.errorInfo()));
			return null;
		}
	}

	public byte[] sendToCoreWithMultiResponse(byte[] realMsg,
			IReplyAdapterWorker adpWorker) {
		// 计算适配器插件消耗的时间
		long t1 = adpWorker.TimeOfAcceptRequest();
		long t2 = System.currentTimeMillis();
		long usedByAdapterPlugin = t2 - t1;
		log.debug("通信层接到适配器插件调用执行开始之前耗时：" + usedByAdapterPlugin);

		if (null == realMsg) {
			log.error("适配器向TES发送请求的消息为空！");
			return null;
		}
		Socket socket = null;
		try {// 建立到核心的连接
			socket = new Socket(m_coreIP, m_corePort);
			socket.setKeepAlive(true);

			m_msgOfData.put("CHANNELNAME", m_localChannelName.getBytes(ConfigHelper.getEncoding()));
			m_msgOfData.put("REQMESSAGE", realMsg);

			// SendToCore已经读取了第一笔应答报文
			m_msgOfDataReply = AbstractAdapterHelper.SendToCore(socket,
					m_msgOfData.Export(), m_coreIP, m_corePort);

			// 处理延时
			if (m_msgOfDataReply.getBytes("DELAYTIME") != null) {
				long delayTime = m_msgOfDataReply.getInteger("DELAYTIME");
				long inTime = adpWorker.TimeOfAcceptRequest();
				long outTime = System.currentTimeMillis();
				long usedTime = (outTime - inTime); // 毫秒
				log.debug("核心的处理时间：[" + usedTime + "]");
				if (usedTime < delayTime) {
					log.debug("进行延时处理：[" + (delayTime - usedTime) + "]"
							+ delayTime + "-" + usedTime);
					try {
						Thread.currentThread();
						Thread.sleep(delayTime - usedTime);
					} catch (InterruptedException e) {
						log.error("0x0803：适配器延时处理失败！[" + e.getMessage() + "]");
						throw new Exception("");
					}
				}
			}
			// 返回正常数据或者异常null
			if (m_msgOfDataReply.isOK()) {
				adpWorker.Response(m_msgOfDataReply.responseMsg());
				log.debug("收到核心分笔响应数据(seq=" + m_msgOfDataReply.packSeqNo() + "): "
						+ new String(m_msgOfDataReply.responseMsg(), "utf-8"));
				System.out.println("收到核心分笔响应数据(seq=" + m_msgOfDataReply.packSeqNo() + "): "
						+ new String(m_msgOfDataReply.responseMsg(), "utf-8"));
			} else {
				adpWorker.Response(null);
				log.error(new String(m_msgOfDataReply.errorInfo()));
			}

			// 循环多笔响应，从第二笔开始，如果没有多笔则跳过
			while (m_msgOfDataReply.hasMorePack()) {
				log.debug("存在后续响应消息……");
				// 从核心取响应
				m_msgOfDataReply = new ReplyMessage(socket.getInputStream());

				// 处理延时
				if (m_msgOfDataReply.getBytes("DELAYTIME") != null) {
					long delayTime = m_msgOfDataReply.getInteger("DELAYTIME");
					long inTime = adpWorker.TimeOfAcceptRequest();
					long outTime = System.currentTimeMillis();
					long usedTime = outTime - inTime;
					log.debug("核心的处理时间：[" + usedTime + "]");
					if (usedTime < delayTime) {
						log.debug("进行延时处理：[" + (delayTime - usedTime) + "]"
								+ delayTime + "-" + usedTime);
						try {
							Thread.currentThread();
							Thread.sleep(delayTime - usedTime);
						} catch (InterruptedException e) {
							log.error("0x0803：适配器延时处理失败！[" + e.getMessage()
									+ "]");
							throw new Exception("");
						}
					}
				}

				// 返回正常数据或者异常null
				byte[] recvbuf = null;
				if (m_msgOfDataReply.isOK()) {
					recvbuf = m_msgOfDataReply.responseMsg();
					adpWorker.Response(recvbuf);
					log.debug("收到核心分笔响应数据(seq=" + m_msgOfDataReply.packSeqNo()	+ "): " + new String(recvbuf, "utf-8"));
				} else {
					recvbuf = null;
					adpWorker.Response(null);
					log.error(new String(m_msgOfDataReply.errorInfo()));
					break;// UP TO: socket.close();
				}

			}// end while
			socket.close();
			log.debug("与核心通信完成，关闭连接.");
			return m_msgOfDataReply.Export();

		} catch (Exception e) {
			// 应该不会执行到这里?
			log.error("与核心通信失败!" + e.getMessage());
			try {
				socket.close();
			} catch (IOException e1) {
				log.error("与核心通信套接字关闭失败!" + e.getMessage());
			}
		}
		return null;
	}

	/**
	 * 向核心发送原始报文
	 * 
	 * @param rawMsg
	 *            原始消息
	 * 
	 * @return 从核心返回的应答消息, 出错则返回null
	 * 
	 * @see 该函数目前仅供UI调用
	 */
	public byte[] SendToCoreRaw(byte[] rawMsg) {
		try {
			// 建立到适配器的连接
			Socket socket = new Socket(m_coreIP, m_corePort);
			return AbstractAdapterHelper.SendToCore(socket, rawMsg, m_coreIP,
					m_corePort).Export();
		} catch (Exception e) {
			e.printStackTrace();
			log.error("UI 与 核心通信失败:" + e.getMessage());
			return null;
		}
	}

}
