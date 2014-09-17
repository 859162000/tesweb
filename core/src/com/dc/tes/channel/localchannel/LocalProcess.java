package com.dc.tes.channel.localchannel;

import com.dc.tes.channel.localchannel.ILocalChannel.ILocalReplyer;
import com.dc.tes.channel.remote.ChannelServer;
import com.dc.tes.exception.ChannelNotFoundException;
import com.dc.tes.net.Message;
import com.dc.tes.net.MessageItem;
import com.dc.tes.net.MessageType;
import com.dc.tes.net.ReplyMessage;
import com.dc.tes.util.RuntimeUtils;

public class LocalProcess implements AbstractLocalProcess , ILocalReplyer  {
	private ReplyMessage[] responseMsg = null;
	@Override
	public ReplyMessage[] process(Message request) {
		
		String channelName = request.getString(MessageItem.AdapterMessage.CHANNELNAME);
		if (!ChannelServer.LChannelcontainsKey(channelName)) {
			// 如果通道不存在 则返回一段错误消息 并抛出异常
			ChannelNotFoundException ex = new ChannelNotFoundException(channelName);
			ReplyMessage errReply = new ReplyMessage(MessageType.REG);
			errReply.put(MessageItem.RESULT, 1);
			errReply.put(MessageItem.ERRMSG, RuntimeUtils.PrintEx(ex));
			return new ReplyMessage[]{errReply};
		}
		ILocalChannel channel = ChannelServer.LChannelGet(channelName);
		
		// 处理请求
		channel.Process(request,this);
		
		return this.responseMsg;
	}

	@Override
	public void Reply(ReplyMessage[] req) {
		// TODO Auto-generated method stub
		this.responseMsg = req;
	}

	@Override
	public void ReplyWithEx(ReplyMessage[] req) throws Exception {
		// TODO Auto-generated method stub
		this.responseMsg = req;
	}

}
