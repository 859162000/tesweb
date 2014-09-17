package com.dc.tes.channel.test;

import com.dc.tes.Core;
import com.dc.tes.InMessage;
import com.dc.tes.OutMessage;
import com.dc.tes.channel.ISenderChannel;
import com.dc.tes.component.tag.ComponentClass;
import com.dc.tes.component.tag.ComponentType;

@ComponentClass(type = ComponentType.Channel)
public class TestSenderChannel implements ISenderChannel {


	@Override
	public InMessage Send(OutMessage out, int timeout) {
		System.out.println(out);

		InMessage in = new InMessage();

		in.bin = out.bin;
		in.channel = out.channel;
		in.tranCode = out.tranCode;

		return in;
	}
	
	@Override
	public void Start(Core core) throws Exception {
	}

	@Override
	public void Stop() throws Exception {
	}

	@Override
	public boolean getChannelState() {
		return true;
	}
}
