package com.dc.tes.adapter.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.dc.tes.adapter.IRequestAdapter;
import com.dc.tes.net.Message;
import com.dc.tes.net.MessageItem;
import com.dc.tes.net.MessageType;
import com.dc.tes.net.ReplyMessage;

/* 
 * 核心发送的请求报文结构:
 * 报文头: MESSAGE
 * 报文体: 
 * 		REQMESSAGE : 请求消息体
 *  	CHANNELNAME : 通道名称
 *  
 *  
 * 通信层返回的响应报文结构: 
 * 报文头: MESSAGE 
 * 报文体: 
 * 		RESULT : 0-成功 非0-失败
 *      RESMESSAGE : 响应消息体
 *      ERRMSG : 错误信息
 */

/**
 * 发起端适配器"本地通道"方式与核心接口
 * 
 * @author 王春佳
 * 
 */
public class RequestInterfaceLocal {

	private static final Log logger = LogFactory
			.getLog(RequestInterfaceLocal.class);

	/**
	 * 核心向通信层"本地通道"方式发送消息
	 * 
	 * @param msg
	 *            : 核心发送的请求报文
	 * @return 通信层返回的响应报文
	 */
	public static Message sendToAdapterLocal(Message msg) {

		// 获取ChannelName字段
		String channelName = msg
				.getString(MessageItem.AdapterMessage.CHANNELNAME);

		// 适配器返回的响应报文
		byte[] responseByte = null;

		// 返回给核心的报文对象
		ReplyMessage replyMessage = new ReplyMessage(MessageType.MESSAGE);

		// 获取发起端适配器插件实例
		IRequestAdapter adapterInst = RequestListLocal
				.getChannelListItem(channelName);
		if (adapterInst != null) {
			// 获取发送给适配器的请求报文
			byte[] requestByte = msg
					.getBytes(MessageItem.AdapterMessage.REQMESSAGE);

			try {
				responseByte = adapterInst.Send(requestByte);
				replyMessage.put("RESMESSAGE", responseByte);

				return replyMessage;
			} catch (Exception e) {
				logger.error("发起端适配器发送数据失败" + e);
				replyMessage.put("RESULT", 1);
				replyMessage.put("RESMESSAGE", "");
				replyMessage.setEx(e);

				return replyMessage;
			}
		} else {
			logger.error("本地通道方式,获取发起端适配器插件实例出错" + adapterInst);
		}

		return null;
	}
}
