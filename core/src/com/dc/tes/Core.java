package com.dc.tes;

import java.io.UnsupportedEncodingException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.dc.tes.ChannelRelatedMap.IObjectFactory;
import com.dc.tes.channel.ChannelList;
import com.dc.tes.channel.IChannel;
import com.dc.tes.channel.IListenerChannel;
import com.dc.tes.channel.ISenderChannel;
import com.dc.tes.channel.internal.LogFChannel;
import com.dc.tes.channel.remote.ChannelServer;
import com.dc.tes.component.ComponentFactory;
import com.dc.tes.component.tag.ComponentType;
import com.dc.tes.data.DALFactory;
import com.dc.tes.data.IRuntimeDAL;
import com.dc.tes.data.model.Case;
import com.dc.tes.data.model.CaseInstance;
import com.dc.tes.data.model.Transaction;
import com.dc.tes.data.op.Op;
import com.dc.tes.exception.CoreErr;
import com.dc.tes.exception.ErrCode;
import com.dc.tes.exception.TESException;
import com.dc.tes.fcore.DatabaseDAL;
import com.dc.tes.fcore.DbGet;
import com.dc.tes.fcore.FCore;
import com.dc.tes.fcore.msg.IPacker;
import com.dc.tes.fcore.msg.IUnpacker;
import com.dc.tes.security.DefaultSecurityProcessor;
import com.dc.tes.security.ISecurityProcessor;
import com.dc.tes.txcode.ITranCodeRecogniser;
import com.dc.tes.util.RuntimeUtils;

/**
 * 模拟器核心
 * 
 * @author lijic
 * @see FCore PCore
 */

public abstract class Core {
	private static final Log log = LogFactory.getLog(Core.class);
	/** 核心实例名称 */
	public final String instanceName;
	/** 运行时数据访问接口 */
	public final IRuntimeDAL da;
	/** 通道列表 */
	public final ChannelList channels;
	/** 远程通道服务器 */
	public final ChannelServer server;
	/** 安全组件列表 */
	public final ChannelRelatedMap<ISecurityProcessor> securityProcessors;
	/** 交易码识别组件列表 */
	public final ChannelRelatedMap<ITranCodeRecogniser> txcodeRecognisers;
	/** 组包组件列表 */
	public final ChannelRelatedMap<IPacker> packers;
	/** 拆包组件列表 */
	public final ChannelRelatedMap<IUnpacker> unpackers;
	/** 定时操作线程*/
	//public final ScheduleTasker schedule;
	/** 核心所在路径*/
	public final String corepath;

	/** 核心基础配置 */
	public final Config config;

	/** 锁列表 当某个交易被等待时，会产生一个对应的锁，当所有对该交易的等待都撤消之后锁会被从列表中移除 */
	public final Map<String, ListenerLock> listenerLocks = new LinkedHashMap<String, ListenerLock>();

	/**
	 * 初始化核心实例
	 * 
	 * @param instanceName
	 *            核心实例名称
	 * @throws Exception
	 */
	protected Core(String instanceName) {
		try {
			this.instanceName = instanceName;

			this.corepath = this.getClass().getClassLoader().getResource("").toURI().getPath();
			
			log.info("初始化" + this.getClass().getSimpleName() + "[" + instanceName + "]...");

			// 初始化核心基础配置
			log.info("初始化基础配置...");
			this.config = new Config();
			log.info("初始化基础配置成功.");

			// 初始化数据访问层
			log.info("初始化数据访问层...");
			this.da = this.createRuntimeDAL(instanceName);
			log.info("初始化数据访问层成功.");

			// 初始化通道服务器
			log.info("初始化通道服务器...");
			this.server = new ChannelServer();
			log.info("初始化通道服务器成功.");

			// 初始化通道列表
			log.info("初始化通道列表...");
			this.channels = new ChannelList(this, this.createDefaultChannels());
			log.info("初始化通道列表成功.");

			// 初始化交易码识别组件
			log.info("初始化交易码识别组件...");
			this.txcodeRecognisers = new ChannelRelatedMap<ITranCodeRecogniser>(this.channels, new IObjectFactory<ITranCodeRecogniser>() {
				@Override
				public ITranCodeRecogniser Create(String name, IChannel channel) {
					// 只有接收端通道需要配置交易识别组件
//					if (!(channel instanceof IListenerChannel))
//						return null;
					try {
						ITranCodeRecogniser tx = createChannelReleatedObject(ComponentType.TXCode, name);
						if (tx == null)
							throw new TESException(CoreErr.TxCodeComponentConfigNotFound, name);

						return tx;
					} catch (Exception ex) {
						throw new TESException(CoreErr.TxCodeComponentInitFail, ex);
					}
				}
			});
			log.info("初始化交易码识别组件成功.");

			// 初始化安全组件
			log.info("初始化安全组件...");
			this.securityProcessors = new ChannelRelatedMap<ISecurityProcessor>(this.channels, new IObjectFactory<ISecurityProcessor>() {
				@Override
				public ISecurityProcessor Create(String name, IChannel channel) {
					try {
						ISecurityProcessor sec = createChannelReleatedObject(ComponentType.Security, name);
						if (sec == null)
							return new DefaultSecurityProcessor();

						return sec;
					} catch (Exception ex) {
						throw new TESException(CoreErr.SecurityComponentInitFail, ex);
					}
				}
			});
			log.info("初始化安全组件成功.");

			// 初始化组包组件和拆包组件
			log.info("初始化报文组件...");
			final Core _this = this;
			this.packers = new ChannelRelatedMap<IPacker>(this.channels, new IObjectFactory<IPacker>() {
				@Override
				public IPacker Create(String name, IChannel channel) {
					try {
						IPacker packer = createChannelReleatedObject(ComponentType.Pack, name);
						if (packer == null)
							throw new TESException(CoreErr.PackComponentConfigNotFound, name);

						packer.Initialize(_this);
						return packer;
					} catch (Exception ex) {
						throw new TESException(CoreErr.PackComponentInitFail, ex);
					}
				}
			});
			
			this.unpackers = new ChannelRelatedMap<IUnpacker>(this.channels, new IObjectFactory<IUnpacker>() {
				@Override
				public IUnpacker Create(String name, IChannel channel) {
					try {
						IUnpacker unpacker = createChannelReleatedObject(ComponentType.Unpack, name);
						if (unpacker == null){
							//throw new TESException(CoreErr.UnpackComponentConfigNotFound, name);
						}else
							unpacker.Initialize(_this);
						return unpacker;
					} catch (Exception ex) {
						throw new TESException(CoreErr.UnpackComponentInitFail, ex);
					}
				}
			});
			log.info("初始化报文组件成功.");
			
			// 初始化安全组件
//			log.info("初始化定时任务线程...");
//			this.schedule = new ScheduleTasker();
//			log.info("初始化定时任务线程成功...");
		} catch (Exception ex) {
			throw new TESException(CoreErr.InitFail, ex);
		}
	}

	/**
	 * 启动核心
	 * 
	 * @throws Exception
	 */
	public void Start() {
		try {
			log.info("初始化成功，正在启动" + this.getClass().getSimpleName() + "...");

			//String result = License.CheckLicense(this);
			//if (result.length() != 0)
			//	log.warn(new TESException(CoreErr.License.LicenseVerifyWarn, result));

			// 注册程序关闭事件的监听器
			Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						// 停止所有通道
						log.info("正在停止所有通道...");
						channels.Stop();

						// 关闭数据层
						log.info("正在关闭数据层...");
						DALFactory.Close();

						// 停止通道服务器
						log.info("正在停止通道服务器...");
						server.Stop();
						
//						log.info("正在停止定时任务线程...");
//						schedule.Shutdown();

						log.info("核心已经停止.");
					} catch (Exception ex) {
						log.error(new TESException(CoreErr.StopFail, ex));
					}
				}
			}));

			// 启动所有通道
			log.info("正在启动所有通道...");
			this.channels.Start();

			// 启动通道服务器
			log.info("正在启动通道服务器...");
			this.server.Start(da.GetSystem(), config);
			
//			log.info("正在启动定时任务线程...");
//			this.schedule.Start();

			log.info(this.getClass().getSimpleName() + "启动成功.");
		} catch (Exception ex) {
			throw new TESException(CoreErr.StartFail, ex);
		}
	}

	
	/**
	 * 通知核心有通道接到了需要处理的接收端交易
	 * 
	 * @param channel
	 *            接到消息的通道
	 * @param in
	 *            接到的消息
	 */
	public void Notify(IListenerChannel channel, InMessage in) {
		
		log.info("开始处理接收端交易...");
		log.info(in);
		
		// 进行全报文解密
		ISecurityProcessor sec = this.securityProcessors.get(in.channel);
		try {
			in.bin = sec.DecryptAll(this, in.bin);
		} catch (Exception ex) {
			throw new TESException(CoreErr.Security.DecryptAllFail, "bytes: " + RuntimeUtils.PrintHex(in.bin, this.config.ENCODING), ex);
		}

		OutMessage out = new OutMessage();

		int iResponseMode = this.da.GetSystem().getResponseMode();

		// 处理请求
		if (iResponseMode == 1 || iResponseMode == 0) { //1--根据交易应答报文解析返回, 0--使用默认案例的应答报文返回
			try {
				// 解析交易码
				int isClientSimu = this.da.GetSystem().getIsClientSimu();
				String encoding = this.da.GetSystem().getEncoding4RequestMsg();
				if (isClientSimu == 1) { //发送端
					encoding = this.da.GetSystem().getEncoding4ResponseMsg();
				}
				String msgStr = new String(in.bin, encoding);
				System.out.println(msgStr);
				ITranCodeRecogniser tx = this.txcodeRecognisers.get(in.channel);
				String tranCode = in.tranCode = tx.Recognise(in.bin);
				
				log.info("解析出交易码[" + tranCode + "]");
				
				out.tranCode = tranCode;
	
				// 解析出交易码后进行安全处理
				try {
					in.bin = sec.DecryptAll(this, in.bin, tranCode);
				} catch (Exception ex) {
					throw new TESException(CoreErr.Security.DecryptAll2Fail, "tranCode: " + tranCode + " bytes: " + RuntimeUtils.PrintHex(in.bin, this.config.ENCODING), ex);
				}
	
				StringBuilder sbSendChannelName = new StringBuilder();
				//判断是否有listen锁
				if (this.notifyListeners(in)) {
					out = null;
					log.info("该请求被监听，不进行结果返回");
				} else {
					// 进行核心处理
					out = this.processServerTran(in, channel, sbSendChannelName);
					log.info("交易处理完毕,交易码[" + tranCode + "]，返回结果：" + out);
				}
				
				//如果处理结果为null 则说明这个请求被某个监听线程拿去做处理了 此处不需要进行任何处理
				//需返回个空报文避免接收端一直等待。 	   
				if (out == null) {		    
				   channel.Reply(new OutMessage(), Thread.currentThread());
				   return;
				}
				
				if (out.bin == null) {
					if (out.data == null) {
						throw new TESException(CoreErr.EmptyServerTranResult, "tranCode: " + tranCode);
					}
					// 对报文数据进行加密
					try {
						out.data = sec.EncryptData(this, tranCode, out.data);
					} catch (Exception ex) {
						throw new TESException(CoreErr.Security.EncryptDataFail, "tranCode: " + tranCode + " data: " + out.data, ex);
					}
				}
	
				// 对报文字节流进行加密
				try {
					out.bin = sec.EncryptAll(this, tranCode, out.data, out.bin);
				} catch (Exception ex) {
					throw new TESException(CoreErr.Security.EncryptDataFail, "tranCode: " + tranCode + " data: " + out.data + " bytes: " + RuntimeUtils.PrintHex(in.bin, this.config.ENCODING), ex);
				}
			} catch (Exception ex) {
				log.error("交易处理失败", ex);
				out = new OutMessage();
				out.ex = (ex instanceof TESException) ? (TESException) ex : new TESException(ErrCode.UNKNOWN, ex);
			}
		}
		else if (iResponseMode == 2) { //2--根据录制报文匹配返回
			String requestMsg = "";
			try {
				requestMsg = new String(in.bin, da.GetSystem().getEncoding4RequestMsg());
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			String responseMsg = DbGet.getRecordedResonseMsgByRequestMsg(requestMsg);
			if (responseMsg != null) {
				out.bin = responseMsg.getBytes();
			}
		}
		else if (iResponseMode == 3) { //3--根据案例实例匹配返回报文
			String requestMsg = "";
			try {
				requestMsg = new String(in.bin, da.GetSystem().getEncoding4RequestMsg());
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			CaseInstance ci = DbGet.getCaseInstacneByRequestMsg(requestMsg);
			int iResponseCaseId = ci.getCaseId();
			Case c = DbGet.GetCaseByCaseId(String.valueOf(iResponseCaseId));
			Transaction trans = DbGet.getTransactionByCaseID(String.valueOf(iResponseCaseId));
			out.channel = trans.getChannel();
			out.caseFlowID = c.getCaseFlow().getId().toString();
			out.caseID= c.getCaseId();
			out.executeLogID = ci.getExecuteLogId().toString();
			String responseMsg = ci.getResponseMsg();
			if (responseMsg != null) {
				out.bin = responseMsg.getBytes();
			}
			if (!this.da.isSync() /*&& sbSendChannelName != null*/) { // 异步的
				try {
					Send(out, 0);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		
		// 返回响应
		if (out != null/* && out.bin != null*/) {
			try {			
				log.info("返回数据" + out);
				channel.Reply(out, Thread.currentThread());

				// 向监控服务送日志
				LOG(in, out, TransactionMode.Server);
			} catch (Exception ex) {
				log.error("向被测系统返回响应报文时发生异常", ex);
			}
		}
	}
	

	/**
	 * 通知在这个交易上等待的锁
	 * 
	 * @param in
	 *            请求报文
	 * @return 当前交易上是否有正在等待的线程
	 */
	protected boolean notifyListeners(InMessage in) {
		// 判断当前交易上是否有锁 如果有锁则表示有线程在等待
		if (!this.listenerLocks.containsKey(in.tranCode))
			// 该交易上没有锁，返回false，表示该交易走正常的接收端处理流程。
			return false;
		else {
			// 该交易上有锁

			// 拿到该锁
			ListenerLock lock = this.listenerLocks.get(in.tranCode);
			synchronized (lock) {
				// 设置锁中的msg信息为接到的报文
				lock.msg = in;
				// 将当前线程设置到锁中 以便之后的跨线程处理（在当前情况下接收该请求的和处理该请求的不是一个线程）
				lock.t = Thread.currentThread();
				// 调用该锁的notify()，唤醒在锁上等待的线程
				lock.notify();
			}
			// 返回true，表示该请求已被等待的线程处理掉了，不需要再走正常的接收端处理流程
			return true;
		}
	}

	/**
	 * 在派生类实现时 此函数用于提供接收端请求的处理流程
	 * 
	 * @param in
	 *            请求
	 * @param out
	 *            响应
	 * @return 要向被测系统返回的响应报文 如果不需要响应 则返回null
	 */
	protected abstract OutMessage processServerTran(InMessage in, IListenerChannel channel, StringBuilder outChannelName) throws Exception;

	/**
	 * 通知核心处理发起端交易
	 * 
	 * @param out
	 *            向被测系统发送的请求报文
	 * @param timeout
	 *            超时时间
	 * @return 从被测系统接收到的返回报文
	 * @throws Exception
	 */
	public InMessage Send(OutMessage out, int timeout) throws Exception {
		log.info("开始处理发起端交易...");

		InMessage in = new InMessage();

		try {
			this.preClientTran(out);
			log.info("预处理后的报文");
			log.info(out);
			
			ISenderChannel _channel = (ISenderChannel) this.channels.getChannel(out.channel);
				
			log.info("向被测系统发交易");
		
			in = _channel.Send(out, timeout);
		} catch (Exception ex) {
			ex = (ex instanceof TESException) ? (TESException) ex : new TESException(ErrCode.UNKNOWN, ex);
			log.error(ex.getMessage());
			System.out.println(ex.getMessage());
			ErrorLog(out, ex.getMessage());	
			throw ex;
		}

		if(this.da.isSync()) {		
			if(in != null){
				log.info("接收到被测系统的响应：" + in);
				log.info("开始对返回报文进行后处理");
				//in.tranCode = out.tranCode;
				postClientTran(in);
			}else{
				log.info("没有收到被测系统的返回信息");
			}
		}
		log.info("发起端交易处理完毕");
		return in;
	}

	/**
	 * 在派生类实现时 此函数用于提供对将要发起的请求的处理流程
	 * 
	 * @param out
	 *            请求
	 * @throws Exception
	 */
	public abstract void preClientTran(OutMessage out) throws Exception;

	/**
	 * 在派生类实现时 此函数用于提供对响应的处理流程
	 * 
	 * @param in
	 *            响应
	 */
	public abstract void postClientTran(InMessage in);

	/**
	 * 等待对某交易的请求 在超时时间内该调用将被阻塞
	 * 
	 * @param tranCode
	 *            被等待的交易码
	 * @param timeout
	 *            超时时间
	 * @return 被测系统发来的指定交易的报文 如果超时则返回null
	 * @throws Exception
	 */
	public InMessage Listen(String tranCode, int timeout) throws Exception {
		log.info("Listen [" + tranCode + "] timeout=" + timeout);

		// 判断指定交易上是否已经有线程在等待，如果没有线程在等待则创建一个锁
		if (!this.listenerLocks.containsKey(tranCode))
			this.listenerLocks.put(tranCode, new ListenerLock());

		// 得到这个交易码上的锁对象
		ListenerLock lock = this.listenerLocks.get(tranCode);
		InMessage msg;

		// 锁住这个锁
		synchronized (lock) {
			// 锁计数+1
			lock.count++;

			// 在该锁上等待
			// 此时该线程被阻塞，控制权交给系统，直到该锁被notify()或超时。对该锁调用notify()的代码位于notifyListeners()中。
			lock.wait(timeout);

			// 线程被唤醒或超时，此时将锁中的信息接收下来
			msg = lock.msg; // msg表示被测系统发来的报文
			if (msg != null)
				msg.t = lock.t;// t表示接到该请求的线程 该线程和当前线程有可能不是一个

			// 锁计数-1
			lock.count--;
			// 如果锁计数归0，则表示已经没有线程在等待这个交易，此时从列表中移除（销毁掉）这个锁
			// 锁被销毁后，对该交易的请求将走普通的接收端处理流程
			if (lock.count == 0)
				this.listenerLocks.remove(tranCode);
		}

		log.info("Tran [" + tranCode + "] " + msg == null ? "timeout" : "received");
		return msg;
	}

	/**
	 * 退回某个接收端请求
	 * 
	 * @param msg
	 *            被退回的请求
	 */
	public void Reject(InMessage msg) {
		log.info("Reject " + msg);
		this.Notify((IListenerChannel) this.channels.getChannel(msg.channel), msg);
	}

	/**
	 * 对某个接收端请求进行响应
	 * 
	 * @param in
	 * @param outs
	 */
	public void Reply(InMessage in, OutMessage[] outs) {
	}

	/**
	 * 通知核心刷新自身信息
	 * 
	 * @throws Exception
	 */
	public void Refresh() throws Exception {
		this.da.Refresh();
	}

	/**
	 * 在派生类中实现时 此方法用于获取核心必须的基础通道实例列表
	 * 
	 * @return 核心必须 的基础通道实例列表
	 */
	protected abstract Map<String, IChannel> createDefaultChannels();

	/**
	 * 在派生类中实现时 此方法用于获取运行时数据访问接口实例
	 * 
	 * @param instanceName
	 *            接口名称
	 * @return 一个可用的运行时数据访问接口实例
	 * @throws Exception
	 */
	protected abstract IRuntimeDAL createRuntimeDAL(String instanceName) throws Exception;

	/**
	 * 工具函数 用于创建与通道相关的组件实例
	 * 
	 * @param <T>
	 *            组件类
	 * @param type
	 *            组件的类型
	 * @param channel
	 *            通道名称
	 * @return 组件类实例
	 */
	protected <T> T createChannelReleatedObject(ComponentType type, String channel) {
		List<String> list = da.getComponentConfigNames(type, Op.EQ("channel", channel));
		if (list.size() == 0)
			list = da.getComponentConfigNames(type);

		if (list.size() == 0)
			return null;

		return ComponentFactory.CreateComponent(da, list.get(0));
	}

	protected abstract void LOG(InMessage in, OutMessage out, TransactionMode mode) throws Exception;
	
	/**
	 * 发送给界面的业务流日志(注意是业务流)
	 * @param flowid   业务流ID
	 * @param logid	     执行日志ID
	 * @param msg
	 * @param row
	 * @param state  0:开始   1:结束
	 * @param iserror 错误false 正常true
	 */
	public void flowLog(String flowid, String logid, String msg, int row, int state, boolean iserror) {
		LogFChannel logchannel = (LogFChannel) this.channels.getChannel("LOG");
		logchannel.ReportFlowLogMessage(flowid, logid, msg, row, state, iserror);
	}
	
	/**
	 * 发送业务流过程日志
	 * @param obj  OutMessage或InMessage对象
	 * @param msg  信息
	 */
	public void FlowLog(Object obj, String msg) {
		String caseFlowID = "";
		String executeLogID = "";
		if(obj instanceof OutMessage) {
			caseFlowID = ((OutMessage)obj).caseFlowID;
			executeLogID = ((OutMessage)obj).executeLogID;
		} else if (obj instanceof InMessage) {
			caseFlowID = ((InMessage)obj).caseFlowID;
			executeLogID = ((InMessage)obj).executeLogID;
		}		
		flowLog(caseFlowID, executeLogID, msg, 0, 0, true);
	}
	
	/**
	 * 发送业务流错误日志,会终止界面的业务流日志刷新
	 * @param obj  OutMessage或InMessage对象
	 * @param msg  信息
	 */
	public void ErrorLog(Object obj, String msg){
		String caseFlowID = "";
		String executeLogID = "";
		String caseID = "";
		if(obj instanceof OutMessage) {
			caseFlowID = ((OutMessage)obj).caseFlowID;
			executeLogID = ((OutMessage)obj).executeLogID;
			caseID = ((OutMessage)obj).caseID;
		} else if (obj instanceof InMessage) {
			caseFlowID = ((InMessage)obj).caseFlowID;
			executeLogID = ((InMessage)obj).executeLogID;
			caseID = ((InMessage)obj).caseID;
		}		
		flowLog(caseFlowID, executeLogID, msg, 0, 1, false);
		flowLog(caseFlowID, executeLogID, "业务流异常终止", 0, 1, false);
		
		if(caseID.isEmpty()) {
			return;
		}
		
		DatabaseDAL.SetTimeOut(caseID, executeLogID);
	}

	
	public void stop(){
		try {
			// 停止所有通道
			log.info("正在停止所有通道...");
			channels.Stop();

			// 关闭数据层
			log.info("正在关闭数据层...");
			DALFactory.Close();

			// 停止通道服务器
			log.info("正在停止通道服务器...");
			server.Stop();
			
//			log.info("正在停止定时任务线程...");
//			schedule.Shutdown();

			log.info("核心已经停止.");
		} catch (Exception ex) {
			log.error(new TESException(CoreErr.StopFail, ex));
		}
	}
}
