package com.dc.tes.channel.remote;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.dc.tes.InMessage;
import com.dc.tes.OutMessage;
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
public abstract class AbstractRemoteSenderChannel<T extends AdapterConfigObject> extends AbstractRemoteChannel<T> implements ISenderChannel, IRemoteAdapterChannel {
	
	private static final Log log = LogFactory.getLog(AbstractRemoteSenderChannel.class);

	protected String adapterHost;
	protected int adapterPort;
	

	@Override
	public InMessage Send(OutMessage out, int timeout) throws Exception  {
		
		if(!this.m_state) {
			throw new TESException(CoreErr.ChanndelNotOpen,out.channel);
		}

		//准备向适配器发送的报文
		Message _out = new Message(MessageType.MESSAGE);
		_out.put(MessageItem.AdapterMessage.REQMESSAGE, out.bin);
		
		if (null == this.adapterHost) {
			log.error("发送适配器的配置有问题：对方IP地址不能为空！");
			return null;
		}
		if (0 == this.adapterPort) {
			log.error("发送适配器的配置有问题：对方端口号不能为0！");
			return null;
		}
			
		//转发适配器
		Socket socket = null;
		try {
			socket = new Socket(this.adapterHost, this.adapterPort);
			socket.getOutputStream().write(_out.Export());	
		} catch (UnknownHostException e) {
			throw new TESException(CoreErr.ConnectError,"地址: "+this.adapterPort+" 端口: "+this.adapterPort);
		} catch (IOException e) {
			throw new TESException(CoreErr.SendError,"地址: "+this.adapterPort+" 端口: "+this.adapterPort);
		}
		
		this.m_core.FlowLog(out,"------发送报文成功,等待报文返回");			
		
		//同步通讯等待返回
		if(this.m_core.da.isSync()) {
			InputStream is = null;
			try {
				is = socket.getInputStream();		
				Message _in = new Message(is, timeout);
				// 获取从适配器接收到的报文	
				InMessage in = new InMessage();
				in.bin = _in.getBytes(MessageItem.AdapterMessage.RESMESSAGE);
				in.channel = out.channel;
				in.tranCode = out.tranCode;
				in.caseID = out.caseID;
				in.executeLogID = out.executeLogID;
				in.caseFlowID = out.caseFlowID;
				in.caseIndex = out.caseIndex;
				in.ex = (_in.getString(MessageItem.RESULT).charAt(0) != '0') ? new TESException(
						CoreErr.AdapterSendFail, _in.getString(MessageItem.ERRMSG))
						: null;					
				return in;
			} catch (IOException e) {
				throw new TESException(CoreErr.ReadError,"地址: "+this.adapterPort+" 端口: "+this.adapterPort);
			}
		}
		
		return null;
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
		String expect = this.getClass().getPackage().getName().substring(this.getClass().getPackage().getName().lastIndexOf('.') + 1) + ".c";
		String actual = msg.getString(MessageItem.AdapterReg.SIMTYPE);

		if (!expect.equals(actual)) {
			// 适配器类型和通道类型不匹配
			Exception ex = new ChannelMismatchException(expect, actual);
			resp.setEx(ex);

			replyer.Reply(resp);

			log.info("[" + this.m_config.configName + "]适配器未能成功注册", ex);
		} else {
			// 适配器类型匹配 执行注册过程
			this.adapterHost = msg.getString(MessageItem.REMOTE_HOST); // 记录适配器的ip
			this.adapterPort = msg.getInteger("PORT"); // 记录适配器的端口
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
}
