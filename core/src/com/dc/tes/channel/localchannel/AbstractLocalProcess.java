package com.dc.tes.channel.localchannel;

import com.dc.tes.net.Message;
import com.dc.tes.net.ReplyMessage;

public interface AbstractLocalProcess {
	public ReplyMessage[] process(Message request);
}
