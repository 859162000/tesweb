package com.dc.tes.channel.remote;

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

/**
 * 远程接收端通道虚基类
 * 
 * @author lijic
 * 
 * @param <T>
 *            组件配置对象的类型
 * @see DefaultRemoteListenerChannel
 */

public abstract class AbstractRemoteListenerChannel<T extends AdapterConfigObject> extends AbstractRemoteChannel<T> implements IListenerChannel, IRemoteAdapterChannel {
	
	private static final Log log = LogFactory.getLog(AbstractRemoteListenerChannel.class);

	private static final Object ID_REPLYER = new Object();

	/**
	 * 远程响应接口实例
	 */
	protected ThreadLocalEx<IRemoteReplyer> m_replyer = new ThreadLocalEx<IRemoteReplyer>(ID_REPLYER);

	@Override
	public void Reply(OutMessage out, Thread original) throws Exception {
		this.Reply(new OutMessage[] { out }, original);
	}

	@Override
	public void Reply(OutMessage[] list, Thread original) throws Exception {
		// 当核心处理完接收端交易时 会调用本函数向被测系统发送应答报文
		Message msg = new Message(MessageType.MESSAGE);

		for (int i = 0; i < list.length; i++) {

			OutMessage _out = list[i];

			// 建立要向通道远端返回的消息
			msg.put(MessageItem.RESULT, _out.ex == null ? "0" : _out.ex.errCode);
			msg.put(MessageItem.ERRMSG, _out.ex == null ? "" : _out.ex.toString());

			msg.put(MessageItem.AdapterMessage.RESMESSAGE, _out.bin);
			msg.put(MessageItem.AdapterMessage.DELAYTIME, _out.delay);
			msg.put(MessageItem.AdapterMessage.PACKSEQ, list.length - 1 - i);

			IRemoteReplyer replyer = ThreadLocalEx.getCrossThread(ID_REPLYER, original);
			replyer.ReplyWithEx(msg);
		}
	}

	@Override
	public byte[] getAdapterConfig() {
		return this.m_config.Export();
	}

	@Override
	protected void processREG(Message msg, IRemoteReplyer replyer) {
		log.info("接到远程接收端适配器注册请求");

		ReplyMessage resp = new ReplyMessage(msg);

		// 检查适配器类型
		String expect = this.getClass().getPackage().getName().substring(this.getClass().getPackage().getName().lastIndexOf('.') + 1) + ".s";
		String actual = msg.getString(MessageItem.AdapterReg.SIMTYPE);

		if (!expect.equals(actual)) {
			// 适配器类型和通道类型不匹配
			Exception ex = new ChannelMismatchException(expect, actual);
			resp.setEx(ex);
			replyer.Reply(resp);
			log.info("[" + this.m_config.configName + "]适配器未能成功注册", ex);
		} else {
			// 适配器类型匹配 执行注册过程
			//resp.put(MessageItem.AdapterReg.CONFIGINFO, this.getAdapterConfig());
			
			String strSystemId = this.m_core.da.GetSystem().getSystemId();
			String strSystemName = this.m_core.da.GetSystem().getSystemName();
			//String encoding = this.m_core.da.GetSystem().get
			byte[] byteSystemId = null;
			try {
				byteSystemId = ("\nSYSTEMID=" + strSystemId + "\nSYSTEMNAME=" + strSystemName).getBytes("utf-8");
			}
			catch(Exception e) {
				System.out.print(e.getMessage());
			}

			byte[] byteConfig = this.getAdapterConfig();
			
			byte[] byteNewConfig = new byte[byteConfig.length + byteSystemId.length];
			  
			System.arraycopy(byteConfig, 0, byteNewConfig, 0, byteConfig.length);
			System.arraycopy(byteSystemId, 0, byteNewConfig, byteConfig.length, byteSystemId.length);

			resp.put(MessageItem.AdapterReg.CONFIGINFO, byteNewConfig);
			
			replyer.Reply(resp);
			this.m_state = true;
			log.info("[" + this.m_config.configName + "]适配器已经成功注册");
		}
	}

	@Override
	protected void processUNREG(Message msg, IRemoteReplyer replyer) {
		// 适配器注销请求
		log.info("接到远程接收端适配器注销请求");

		ReplyMessage resp = new ReplyMessage(msg);

		this.m_state = false;
		log.info("[" + this.m_config.configName + "]适配器已经注销");
		replyer.Reply(resp);
	}
	

	@Override
	protected void processMESSAGE(Message msg, IRemoteReplyer replyer) {
		// 处理适配器交易消息
		InMessage in = new InMessage();
		in.bin = msg.getBytes(MessageItem.AdapterMessage.REQMESSAGE);
		in.channel = msg.getString(MessageItem.AdapterMessage.CHANNELNAME);

		// 将远程响应接口实例放到线程独立的变量中
		this.m_replyer.set(replyer);
	
		if(!this.m_core.da.isClient()) {
			//服务端，要做报文应答
			this.m_core.Notify(this, in);
			
		} else {
			//客户端，要做报文和SQL参数检查和案例通过与否的判断
			try {
				//直接回复适配器个空报文,
				//因为使用Listen通道是默认 核心 作为 服务端
				//核心假如作为服务端  一定要返回处理完成的报文给被测系统
				this.Reply(new OutMessage(), Thread.currentThread());
				
				//仍交由发起方的后处理模块处理
				this.m_core.postClientTran(in);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
}
