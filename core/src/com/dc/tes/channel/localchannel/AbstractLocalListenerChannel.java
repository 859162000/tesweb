package com.dc.tes.channel.localchannel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.dc.tes.InMessage;
import com.dc.tes.OutMessage;
import com.dc.tes.channel.AdapterConfigObject;
import com.dc.tes.channel.IListenerChannel;
import com.dc.tes.exception.ChannelMismatchException;
import com.dc.tes.net.Message;
import com.dc.tes.net.MessageItem;
import com.dc.tes.net.MessageType;
import com.dc.tes.net.ReplyMessage;
import com.dc.tes.util.type.ThreadLocalEx;

public class AbstractLocalListenerChannel<T extends AdapterConfigObject> extends AbstractLocalChannel<T> implements IListenerChannel {
	private static final Log log = LogFactory.getLog(AbstractLocalListenerChannel.class);

	private static final Object ID_REPLYER = new Object();
	/**
	 * 远程响应接口实例
	 */
	protected ThreadLocalEx<ILocalReplyer> m_replyer = new ThreadLocalEx<ILocalReplyer>(ID_REPLYER);

	//private ILocalReplyer localReplyer = null;
	//private InMessage inmsg = null;

	@Override
	public void Reply(OutMessage out, Thread original) throws Exception {
		// TODO Auto-generated method stub
		this.Reply(new OutMessage[] { out }, original);

	}

	@Override
	public void Reply(OutMessage[] list, Thread original) throws Exception {
		// TODO Auto-generated method stub
		ReplyMessage[] msg = new ReplyMessage[list.length];//= new Message(MessageType.MESSAGE)[list.length]

		for (int i = 0; i < list.length; i++) {

			OutMessage _out = list[i];
			ReplyMessage msgtemp = new ReplyMessage(MessageType.MESSAGE);
			// 建立要向通道远端返回的消息
			msgtemp.put(MessageItem.RESULT, _out.ex==null?"0":_out.ex.errCode);
			msgtemp.put(MessageItem.ERRMSG, _out.ex==null?"":_out.ex.toString());

			msgtemp.put(MessageItem.AdapterMessage.RESMESSAGE, _out.bin);
			msgtemp.put(MessageItem.AdapterMessage.DELAYTIME, _out.delay);
			msgtemp.put(MessageItem.AdapterMessage.PACKSEQ, list.length - 1 - i);
			msgtemp.put(MessageItem.AdapterMessage.PLOG, _out.tranCode + ",0");
			msg[i] = msgtemp;
		}
		this.m_replyer.get().ReplyWithEx(msg);
	}

	public byte[] getAdapterConfig() {
		// TODO Auto-generated method stub
		return this.m_config.Export();
	}

	@Override
	protected void processREG(Message msg, ILocalReplyer replyer) {
		log.info("接到远程接收端适配器注册请求");

		ReplyMessage resp = new ReplyMessage(msg);

		// 检查适配器类型
		String expect = this.getClass().getPackage().getName().substring(this.getClass().getPackage().getName().lastIndexOf('.') + 1) + ".s";
		String actual = msg.getString(MessageItem.AdapterReg.SIMTYPE);

		if (!expect.equals(actual)) {
			// 适配器类型和通道类型不匹配
			Exception ex = new ChannelMismatchException(expect, actual);
			resp.setEx(ex);

			replyer.Reply(new ReplyMessage[] { resp });

			log.info("[" + this.m_config.configName + "]适配器未能成功注册", ex);
		} else {
			// 适配器类型匹配 执行注册过程
			resp.put(MessageItem.AdapterReg.CONFIGINFO, this.getAdapterConfig());

			replyer.Reply(new ReplyMessage[] { resp });

			this.m_state = true;

			log.info("[" + this.m_config.configName + "]适配器已经成功注册");
		}
	}

	@Override
	protected void processUNREG(Message msg, ILocalReplyer replyer) {
		// 适配器注销请求
		log.info("接到远程接收端适配器注销请求");

		ReplyMessage resp = new ReplyMessage(msg);

		this.m_state = false;
		log.info("[" + this.m_config.configName + "]适配器已经注销");
		replyer.Reply(new ReplyMessage[] { resp });
	}

	@Override
	protected void processMESSAGE(Message msg, ILocalReplyer replyer) {
		// 处理适配器交易消息
		InMessage in = new InMessage();
		in.bin = msg.getBytes(MessageItem.AdapterMessage.REQMESSAGE);
		in.channel = msg.getString(MessageItem.AdapterMessage.CHANNELNAME);
		in.plogInfo = msg.getString(MessageItem.AdapterMessage.PLOG);

		this.m_replyer.set(replyer);
		// 通知核心处理这个消息
		this.m_core.Notify(this, in);
	}

}
