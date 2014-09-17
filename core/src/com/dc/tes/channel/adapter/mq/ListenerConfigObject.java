package com.dc.tes.channel.adapter.mq;

import com.dc.tes.channel.adapter.DefaultListenerConfigObject;
import com.dc.tes.util.RuntimeUtils;

public class ListenerConfigObject extends DefaultListenerConfigObject {
	@Override
	public byte[] Export() {
		return super.config.getBytes(RuntimeUtils.gb2312);
	}
}
