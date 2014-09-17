package com.dc.tes.channel.local;

import java.lang.reflect.ParameterizedType;

import com.dc.tes.Core;
import com.dc.tes.InMessage;
import com.dc.tes.OutMessage;
import com.dc.tes.adapter.ISenderAdapter;
import com.dc.tes.adapter.host.ISenderAdapterHost;
import com.dc.tes.channel.AdapterConfigObject;
import com.dc.tes.channel.ISenderChannel;
import com.dc.tes.component.BaseComponent;
import com.dc.tes.exception.CoreErr;
import com.dc.tes.exception.TESException;
import com.dc.tes.util.InstanceCreater;
import com.dc.tes.util.type.Wrapper;

public abstract class AbstractLocalSenderChannel<A extends ISenderAdapter, T extends AdapterConfigObject> extends BaseComponent<T> implements ISenderChannel, ISenderAdapterHost {
	/**
	 * 适配器实例
	 */
	private ISenderAdapter adapter;
	/**
	 * 通道状态
	 */
	private boolean state;

	@Override
	public InMessage Send(OutMessage out, int timeout) throws Exception {
		byte[] bytes = null;
		try {
			//bytes = adapter.Send(out.bin);
		} catch (Exception ex) {
			throw new TESException(CoreErr.AdapterSendFail, ex);
		}

		InMessage msg = new InMessage();
		msg.bin = bytes;
		msg.channel = this.m_config.configName;
		msg.t = Thread.currentThread();
		return msg;
	}

	@Override
	@SuppressWarnings("unchecked")
	public void Start(Core core) throws Exception {
		this.adapter = InstanceCreater.CreateInstance((Class<? extends ISenderAdapter>) ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[0]);

		// 在一个新线程中启动这个适配器
		final ISenderAdapterHost _this = this;
		final Wrapper<Exception> adapterStartException = new Wrapper<Exception>();
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					//adapter.Start(_this, m_config.Export());
				} catch (Exception ex) {
					adapterStartException.setValue(ex);
				}
			}
		}, this.m_config.configName);
		t.start();

		int time = 0;
		while (time < 5000 && !this.state) {
			Thread.sleep(200);
			time += 200;
		}

		if (adapterStartException.getValue() != null)
			throw new RuntimeException("adapter start fail", adapterStartException.getValue());
		if (!this.state) {
			throw new RuntimeException("adapter start timeout");
		}
	}

	@Override
	public void Stop() throws Exception {
		this.adapter.Stop();
		this.state = false;
	}
/*
	@Override
	public void Ready() {
		this.state = true;
	}*/

	@Override
	public boolean getChannelState() {
		return state;
	}
}
