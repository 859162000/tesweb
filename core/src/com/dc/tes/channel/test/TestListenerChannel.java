package com.dc.tes.channel.test;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import com.dc.tes.Core;
import com.dc.tes.InMessage;
import com.dc.tes.OutMessage;
import com.dc.tes.channel.IListenerChannel;
import com.dc.tes.component.tag.ComponentClass;
import com.dc.tes.component.tag.ComponentType;

@ComponentClass(type = ComponentType.Channel)
public class TestListenerChannel implements IListenerChannel {
	private Core m_core;

	@Override
	public void Reply(OutMessage out, Thread original) {
	}

	@Override
	public void Reply(OutMessage[] out, Thread original) throws Exception {
	}

	@Override
	public void Start(Core core) throws Exception {
		this.m_core = core;

		final TestListenerChannel _this = this;
		new Timer().schedule(new TimerTask() {
			@Override
			public void run() {
				InMessage in = new InMessage();
				in.channel = "test";
				try {
					in.bin = "0100".getBytes("cp935");
				} catch (IOException ex) {
					ex.printStackTrace();
				}

				m_core.Notify(_this, in);
			}
		}, 500, 10000);
	}

	@Override
	public void Stop() throws Exception {
	}

	@Override
	public boolean getChannelState() {
		return true;
	}
}
