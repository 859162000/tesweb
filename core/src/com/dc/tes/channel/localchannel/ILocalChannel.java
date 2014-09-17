package com.dc.tes.channel.localchannel;

import com.dc.tes.channel.IChannel;
import com.dc.tes.net.Message;
import com.dc.tes.net.ReplyMessage;

public interface ILocalChannel extends IChannel{
	public interface ILocalReplyer {

		public void ReplyWithEx(ReplyMessage[] req) throws Exception;

		public void Reply(ReplyMessage[] req);
	}

	/**
	 * 处理通道远端送来的请求
	 * 
	 * @param msg
	 *            远端传来的消息
	 * @param replyer
	 *            远程响应接口
	 */
	public void Process(Message msg, ILocalReplyer replyer);
	

}
