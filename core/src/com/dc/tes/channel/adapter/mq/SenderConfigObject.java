package com.dc.tes.channel.adapter.mq;

import com.dc.tes.channel.adapter.DefaultSenderConfigObject;
import com.dc.tes.util.RuntimeUtils;

public class SenderConfigObject extends DefaultSenderConfigObject {
	@Override
	public byte[] Export() {
		return super.config.getBytes(RuntimeUtils.gb2312);
	}
}
