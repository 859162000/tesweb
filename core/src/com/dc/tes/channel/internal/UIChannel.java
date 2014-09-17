package com.dc.tes.channel.internal;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.dc.tes.Core;
import com.dc.tes.InMessage;
import com.dc.tes.OutMessage;
import com.dc.tes.channel.remote.AbstractRemoteChannel;
import com.dc.tes.channel.remote.ChannelServer;
import com.dc.tes.component.ConfigObject;
import com.dc.tes.data.model.ExecuteLog;
import com.dc.tes.dom.util.MsgLoader;
import com.dc.tes.fcore.DbSet;
import com.dc.tes.fcore.FCore;
import com.dc.tes.fcore.DbGet;
import com.dc.tes.net.Message;
import com.dc.tes.net.MessageItem;
import com.dc.tes.net.ReplyMessage;


/**
 * 界面远程管理通道
 * 
 * @author lijic
 */

public class UIChannel extends AbstractRemoteChannel<ConfigObject> {
	
	private static final Log log = LogFactory.getLog(UIChannel.class);
	/**
	 * 界面管理通道的singleton实例
	 */
	public final static UIChannel instance = new UIChannel();

	@Override
	protected void processUI(Message msg, IRemoteReplyer replyer) {
		System.out.println("接收到界面请求");
		switch (msg.getInteger(MessageItem.UI.OP)) {
		case 1:
			// 发起交易（单个案例）
			this.processOP1(msg, replyer);
			return;
		case 2:
			// 刷新配置
			this.processOP2(msg, replyer);
			return;
			//		case 3:
			//			// 查询适配器状态
			//			this.processOP3(msg, resp, replyer);
			//			return;
		case 4: //脚本
			this.processOP4(msg, replyer);
			return;
		case 5: //发起业务流的中断后的断点继续执行
			//this.processOP5(msg, replyer);
			return;
		case 6: //发起业务流
			this.processOP6(msg, replyer);
			return;
		default:
			super.replyUnsupportedOperation(msg, replyer);
		}
	}


	@Override
	public void Start(Core core) throws Exception {
		this.m_core = core;

		ChannelServer.AttachChannel("UI", this);
	}

	@Override
	public void Stop() throws Exception {
		ChannelServer.DetachChannel("UI");
	}

	/**
	 * 发起交易
	 */
	private void processOP1(Message msg, IRemoteReplyer replyer) {
		ReplyMessage resp = new ReplyMessage(msg);

		OutMessage out = new OutMessage();
		out.tranCode = msg.getString(MessageItem.UI.TRANCODE);
		out.caseName = msg.getString(MessageItem.UI.CASENAME);
		out.bin = msg.getBytes(MessageItem.UI.REQMESSAGE);
		out.data = msg.getString(MessageItem.UI.REQDATA) == null ? null
				: MsgLoader.LoadXml(msg.getString(MessageItem.UI.REQDATA));
		out.channel = msg.getString(MessageItem.UI.DESTCHANNEL);
		String executeLogId = msg.getString("EXECUTELOGID");

		Integer iExecuteLogId;
		try {
			iExecuteLogId = Integer.parseInt(executeLogId);
		}
		catch(Exception e) {
			log.error("数据错误，从界面传输过来的数据格式（caseId, executeLogId）与预期不一致！" + e.getMessage());
			return;
		}
		if(!executeLogId.isEmpty()) {
			out.preserved1 = iExecuteLogId;
		}
		ExecuteLog executeLog = DbGet.getExecuteLogByExecuteLogId(iExecuteLogId);
		if (executeLog != null && executeLog.getPassFlag() != null && executeLog.getPassFlag() != 0 && executeLog.getPassFlag() != 2) {
			DbSet.updateExecuteLogStatus(iExecuteLogId, 2); //正在执行中（如果已经失败，则不要更新为“执行中”了）
		}
		
		if (!(this.m_core instanceof Core)) {
			UnsupportedOperationException ex = new UnsupportedOperationException();
			log.error(ex);
			resp.setEx(ex);
		} else
			try {
				InMessage in = (this.m_core).Send(out, 0);
				if(in != null){
					resp.put(MessageItem.UI.RESMESSAGE, in.bin);
					resp.put(MessageItem.UI.RESDATA, in.data == null ? null : in.data.toString());
					resp.put(MessageItem.UI.COMPARERESULT, in.preserved1 == null ? null : in.preserved1.toString());
				}
			} catch (Exception ex) {
				log.error(ex);
				resp.setEx(ex);
			}

		log.info("发起交易执行结果" + resp);
		//向界面返回应答消息
		replyer.Reply(resp);
	}

	/**
	 * 刷新配置
	 */
	private void processOP2(Message req, IRemoteReplyer replyer) {
		try {
			super.m_core.Refresh();
		} catch (Exception ex) {
			ReplyMessage resp = new ReplyMessage(req);
			resp.setEx(ex);
			replyer.Reply(resp);
		}
	}

	//
	//	/**
	//	 * 查询适配器状态
	//	 */
	//	private void processOP3(Message req, ReplyMessage resp, IRemoteReplyer replyer) {
	//		try {
	//			ArrayList<String> adapterStatus = new ArrayList<String>();
	//			for (String channelName : this.m_core.channels.getChannelNames()) {
	//				if (this.m_core.channels.getChannel(channelName) instanceof IAdapterChannel) {
	//					IAdapterChannel _channel = (IAdapterChannel) this.m_core.channels.getChannel(channelName);
	//					adapterStatus.add(String.format("%s%s%s%s", channelName, _channel.getAdapterType(), _channel.getAdapterProtocol(), _channel.getState()));
	//				}
	//			}
	//			resp.put(MessageItem.UI.ADAPTERSTATUS, StringUtils.join(adapterStatus, ";"));
	//		} catch (Exception ex) {
	//			resp.setEx(ex);
	//		}
	//	}

	//脚本处理
	private void processOP4(Message req, IRemoteReplyer replyer) {
		ReplyMessage resp = new ReplyMessage(req);
		
		try {
			String code = req.getString("CODE");
			String name = req.getString("NAME");
			String tag = req.getString("TAG");
			String executeLogId = req.getString("EXECUTELOGID");
			String api = req.getString("API");
			String userId = req.getString("USERID");
			
			Integer iExecuteLogId;
			try {
				iExecuteLogId = Integer.parseInt(executeLogId);
			}
			catch(Exception e) {
				log.error("数据错误，从界面传输过来的数据格式（caseId, executeLogId）与预期不一致！" + e.getMessage());
				return;
			}
			ExecuteLog executeLog = DbGet.getExecuteLogByExecuteLogId(iExecuteLogId);
			if (executeLog != null && executeLog.getPassFlag() != null && executeLog.getPassFlag() != 0 && executeLog.getPassFlag() != 2) {
				DbSet.updateExecuteLogStatus(iExecuteLogId, 2); //正在执行中（如果已经失败，则不要更新为“执行中”了）
			}
			
			((FCore) this.m_core).RunScript(code, name, tag, executeLogId, userId, api.equals("1")?true:false);
			
			replyer.Reply(resp); 
		} catch (Exception ex) {
			resp.setEx(ex);
			replyer.Reply(resp);
		}
	}
	
	
//	//业务流的断点继续处理
//	private void processOP5(Message msg, IRemoteReplyer replyer) {
//
//		ReplyMessage resp = new ReplyMessage(msg);
//		
//		String caseId = msg.getString("CASENAME");
//		String executeLogId = msg.getString("EXECUTELOGID");
//		int iCaseId, iExecuteLogId;
//		try {
//			iCaseId = Integer.parseInt(caseId);
//			iExecuteLogId = Integer.parseInt(executeLogId);
//		}
//		catch(Exception e) {
//			log.error("数据错误，从界面传输过来的数据格式（caseId, executeLogId）与预期不一致！" + e.getMessage());
//			return;
//		}
//		
//		//获取CaseInstance
//		CaseInstance ci = DbGet.getInterupptedFlowCaseInstance(iExecuteLogId, iCaseId);
//		if (ci == null) {
//			return;
//		}
//		//获取CaseFlowInstance
//		CaseFlowInstance cfi = ci.getCaseFlowInstance();
//		if (cfi == null) {
//			return;
//		}
//		
//		//获取Case
//		IDAL<Case> caseDAL = DALFactory.GetBeanDAL(Case.class);
//		Case c = caseDAL.Get(Op.EQ("caseId", caseId));
//		if (c == null) {
//			return;
//		}
//
//		OutMessage out = new OutMessage();
//		out.tranCode = msg.getString(MessageItem.UI.TRANCODE);
//		out.caseName = msg.getString(MessageItem.UI.CASENAME);
//		out.bin = msg.getBytes(MessageItem.UI.REQMESSAGE);
//		out.data = msg.getString(MessageItem.UI.REQDATA) == null ? null : MsgLoader.LoadXml(msg.getString(MessageItem.UI.REQDATA));
//		out.channel = msg.getString(MessageItem.UI.DESTCHANNEL);
//
//		if(!executeLogId.isEmpty()) {
//			out.preserved1 = Integer.parseInt(executeLogId);
//		}
//
//		if (!(this.m_core instanceof Core)) {
//			UnsupportedOperationException ex = new UnsupportedOperationException();
//			log.error(ex);
//			resp.setEx(ex);
//		} else
//			try {
//				//ISenderChannel _channel = (ISenderChannel) (this.m_core).channels.getChannel(out.channel);
//				//InMessage in = _channel.Send(out, 0);
//				//InMessage in = (this.m_core).Send(out, 0);
//				InMessage in ; 
//				//	sendSpecifiedCase(cfi, ci, c);
//				if(in != null){
//					resp.put(MessageItem.UI.RESMESSAGE, in.bin);
//					resp.put(MessageItem.UI.RESDATA, in.data == null ? null : in.data.toString());
//					resp.put(MessageItem.UI.COMPARERESULT, in.preserved1 == null ? null : in.preserved1.toString());
//				}
//			} catch (Exception ex) {
//				log.error(ex);
//				resp.setEx(ex);
//			}
//
//		log.info("发起交易执行结果" + resp);
//		//向界面返回应答消息
//		replyer.Reply(resp); //?
//	}
	
	//业务流处理
	private void processOP6(Message msg, IRemoteReplyer replyer) {
		
		ReplyMessage resp = new ReplyMessage(msg);
		
		OutMessage out = new OutMessage();
		out.caseFlowID = msg.getString(MessageItem.UI.CASEFLOWID);
		out.executeLogID = msg.getString(MessageItem.UI.EXECUTELOGID);
		
		m_core.FlowLog(out, "核心开始处理业务流");
		try {
			this.m_core.Send(out, 0);	
		}catch(Exception ex) {
			ex.printStackTrace();
		} finally {
			replyer.Reply(resp);
		}
	}
	
}
