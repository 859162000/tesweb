package com.dc.tes.channel.remote;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.dc.tes.Core;
import com.dc.tes.component.BaseComponent;
import com.dc.tes.component.ConfigObject;
import com.dc.tes.net.Message;
import com.dc.tes.net.ReplyMessage;

/**
 * 远程通道虚基类
 * 
 * @author lijic
 * 
 * @param <T>
 *            组件配置对象的类型
 * @see DefaultRemoteListenerChannel
 */
public abstract class AbstractRemoteChannel<T extends ConfigObject> extends BaseComponent<T> implements IRemoteChannel {
	private static final Log log = LogFactory.getLog(AbstractRemoteChannel.class);
	/**
	 * 通道状态
	 */
	protected boolean m_state;
	/**
	 * 核心实例
	 */
	protected Core m_core;

	@Override
	public boolean getChannelState() {
		return this.m_state;
	}

	@Override
	public void Start(Core core) throws Exception {
		this.m_core = core;

		// 向远程通道服务器进行注册
		ChannelServer.AttachChannel(this.m_config.configName, this);
	}

	@Override
	public void Stop() throws Exception {
		// 向远程通道服务器进行注销
		ChannelServer.DetachChannel(this.m_config.configName);
	}

	@Override
	public void Process(Message msg, IRemoteReplyer replyer) {
		log.debug("接到远程通道消息：" + msg);

		switch (msg.getType()) {
		case REG:
			this.processREG(msg, replyer);
			break;
		case UNREG:
			this.processUNREG(msg, replyer);
			break;
		case MESSAGE:
			this.processMESSAGE(msg, replyer);
			break;
		case UI:
			this.processUI(msg, replyer);
			break;
		default:
			this.replyUnsupportedOperation(msg, replyer);
			break;
		}
	}

	/**
	 * 默认的REG消息处理函数
	 * 
	 * @param msg
	 *            请求消息
	 * @param replyer
	 *            远程响应接口
	 */
	protected void processREG(Message msg, IRemoteReplyer replyer) {
		this.replyUnsupportedOperation(msg, replyer);
	}

	/**
	 * 默认的UNREG消息处理函数
	 * 
	 * @param msg
	 *            请求消息
	 * @param replyer
	 *            远程响应接口
	 */
	protected void processUNREG(Message msg, IRemoteReplyer replyer) {
		this.replyUnsupportedOperation(msg, replyer);
	}

	/**
	 * 默认的MESSAGE消息处理函数
	 * 
	 * @param msg
	 *            请求消息
	 * @param replyer
	 *            远程响应接口
	 */
	protected void processMESSAGE(Message msg, IRemoteReplyer replyer) {
		this.replyUnsupportedOperation(msg, replyer);
	}

	/**
	 * 默认的UI消息处理函数
	 * 
	 * @param msg
	 *            请求消息
	 * @param replyer
	 *            远程响应接口
	 */
	protected void processUI(Message msg, IRemoteReplyer replyer) {
		this.replyUnsupportedOperation(msg, replyer);
	}

	/**
	 * 工具函数 用于向远程响应接口返回一条"当前通道不支持XX操作"的错误信息
	 * 
	 * @param msg
	 *            请求消息
	 * @param replyer
	 *            远程响应接口
	 */
	protected void replyUnsupportedOperation(Message msg, IRemoteReplyer replyer) {
		Exception ex = new UnsupportedOperationException("当前通道[" + this.m_config.configName + "]不支持" + msg.getType().toString() + "类型的请求消息");
		ReplyMessage resp = new ReplyMessage(msg);
		resp.setEx(ex);

		log.error(ex);

		replyer.Reply(resp);
	}
}
