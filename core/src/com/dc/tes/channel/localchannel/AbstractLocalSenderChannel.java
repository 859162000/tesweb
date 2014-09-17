package com.dc.tes.channel.localchannel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.dc.tes.InMessage;
import com.dc.tes.OutMessage;
import com.dc.tes.adapter.util.RequestInterfaceLocal;
import com.dc.tes.channel.AdapterConfigObject;
import com.dc.tes.channel.ISenderChannel;
import com.dc.tes.exception.ChannelMismatchException;
import com.dc.tes.exception.CoreErr;
import com.dc.tes.exception.TESException;
import com.dc.tes.net.Message;
import com.dc.tes.net.MessageItem;
import com.dc.tes.net.MessageType;
import com.dc.tes.net.ReplyMessage;

/**
 * 远程发起端通道虚基类
 * 
 * @author lijic
 * 
 * @param <T>
 *            组件配置对象的类型
 * @see DefaultRemoteListenerChannel
 */
public abstract class AbstractLocalSenderChannel<T extends AdapterConfigObject> extends AbstractLocalChannel<T> implements ISenderChannel {
	private static final Log log = LogFactory.getLog(AbstractLocalSenderChannel.class);


	@Override
	public InMessage Send(OutMessage out, int timeout) throws Exception {
		// 准备向适配器发送的报文
		Message _out = new Message(MessageType.MESSAGE);
		_out.put(MessageItem.AdapterMessage.REQMESSAGE, out.bin);
		_out.put(MessageItem.AdapterMessage.CHANNELNAME, this.m_config.configName);
		// 向适配器发报文
		Message _in = this.SendToLocalAdapter(_out);
		// 从适配器取响应

		// 获取从适配器接收到的报文
		String config = this.m_config.toString();
		if(!config.contains("NEEDBACK=0")){
			InMessage in = new InMessage();
			in.bin = _in.getBytes(MessageItem.AdapterMessage.RESMESSAGE);
			in.channel = this.m_config.configName;
			in.tranCode = out.tranCode;
			in.ex = (_in.getString(MessageItem.RESULT).charAt(0) != '0') ? new TESException(CoreErr.AdapterSendFail, _in.getString(MessageItem.ERRMSG)) : null;
	
			return in;
		}else return null;
	}

	public Message SendToLocalAdapter(Message msg) {
		return RequestInterfaceLocal.sendToAdapterLocal(msg);
	}

	public byte[] getAdapterConfig() {
		return this.m_config.Export();
	}

	@Override
	protected void processREG(Message msg, ILocalReplyer replyer) {
		log.info("接到远程接收端适配器注册请求");

		ReplyMessage resp = new ReplyMessage(msg);

		// 检查适配器类型
		String expect = this.getClass().getPackage().getName().substring(this.getClass().getPackage().getName().lastIndexOf('.') + 1) + ".c";
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
}
