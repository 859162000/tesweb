package com.dc.tes.channel.local;

import java.lang.reflect.ParameterizedType;

import com.dc.tes.Core;
import com.dc.tes.InMessage;
import com.dc.tes.OutMessage;
import com.dc.tes.adapter.IListenerAdapter;
import com.dc.tes.adapter.IListenerAdapter.IReplyer;
import com.dc.tes.adapter.host.IListenerAdapterHost;
import com.dc.tes.channel.AdapterConfigObject;
import com.dc.tes.channel.IListenerChannel;
import com.dc.tes.component.BaseComponent;
import com.dc.tes.exception.CoreErr;
import com.dc.tes.exception.TESException;
import com.dc.tes.util.InstanceCreater;
import com.dc.tes.util.type.ThreadLocalEx;
import com.dc.tes.util.type.Wrapper;

public abstract class AbstractLocalListenerChannel<A extends IListenerAdapter, T extends AdapterConfigObject> extends BaseComponent<T> implements IListenerChannel, IListenerAdapterHost {
	private Core core;
	private IListenerAdapter adapter;
	private boolean state;

	private static final Object ID_REPLYER = new Object();
	private ThreadLocalEx<IReplyer> replyer = new ThreadLocalEx<IReplyer>(ID_REPLYER);

	@Override
	public void Reply(OutMessage out, Thread original) throws Exception {
		this.Reply(new OutMessage[] { out }, original);
	}

	@Override
	public void Reply(OutMessage[] list, Thread original) throws Exception {
		IReplyer replyer = ThreadLocalEx.getCrossThread(ID_REPLYER, original);
		for (OutMessage msg : list)
			replyer.Reply(msg.bin);
	}

	@Override
	@SuppressWarnings("unchecked")
	public void Start(Core core) throws Exception {
		// 创建适配器实例
		this.adapter = InstanceCreater.CreateInstance((Class<? extends IListenerAdapter>) ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[0]);

		// 在一个新线程中启动这个适配器
		final IListenerAdapterHost _this = this;
		final Wrapper<Exception> adapterStartException = new Wrapper<Exception>();
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					adapter.Start(_this, m_config.Export());
				} catch (Exception ex) {
					adapterStartException.setValue(ex);
				}
			}
		}, this.m_config.configName);
		t.start();

		// 5秒的超时时间
		int time = 0;
		while (time < 5000 && !this.state) {
			Thread.sleep(200);
			time += 200;
		}

		if (adapterStartException.getValue() != null)
			throw new TESException(CoreErr.AdapterStartFail, adapterStartException.getValue());
		if (!this.state) {
			throw new TESException(CoreErr.AdapterStartTimeout);
		}
	}

	@Override
	public void Stop() throws Exception {
		this.adapter.Stop();
		this.state = false;
	}

	
	public void Ready() {
		this.state = true;
	}

	@Override
	public boolean getChannelState() {
		return this.state;
	}

	
	public byte[] SendCoreMessage(byte[] bytes, long receiveTime) {
		final Wrapper<byte[]> buffer = new Wrapper<byte[]>();

		IReplyer replyer = new IReplyer() {
			@Override
			public void Reply(byte[] bytes) {
				buffer.setValue(bytes);
			}
		};

		this.SendCoreMessage(bytes, receiveTime, replyer);

		return buffer.getValue();
	}

	
	public void SendCoreMessage(byte[] bytes, long receiveTime, IReplyer replyer) {
		this.replyer.set(replyer);

		InMessage in = new InMessage();
		in.bin = bytes;
		in.channel = this.m_config.configName;
		in.t = Thread.currentThread();

		this.core.Notify(this, in);
	}
}
