package com.dc.tes.channel.test;

import java.awt.Frame;

import javax.swing.JFrame;

import com.dc.tes.Core;
import com.dc.tes.channel.IChannel;
import com.dc.tes.component.tag.ComponentClass;
import com.dc.tes.component.tag.ComponentType;

@ComponentClass(type = ComponentType.Channel)
public class TestUIChannel implements IChannel {
	private Core m_core;
	private JFrame m_ui;

	@Override
	public void Start(Core core) {
		this.m_core = core;
		this.m_ui = new TestUI(this.m_core);
	}

	@Override
	public void Stop() throws Exception {
		this.m_ui.dispose();
	}

	@Override
	public boolean getChannelState() {
		return this.m_ui.getState() == Frame.NORMAL;
	}
}
