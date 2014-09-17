package com.dc.tes.fcore.script;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import sun.org.mozilla.javascript.internal.NativeArray;
import sun.org.mozilla.javascript.internal.NativeObject;

import com.dc.tes.InMessage;
import com.dc.tes.OutMessage;
import com.dc.tes.TransactionMode;
import com.dc.tes.channel.IListenerChannel;
import com.dc.tes.channel.ISenderChannel;
import com.dc.tes.channel.internal.LogFChannel;
import com.dc.tes.data.DALFactory;
import com.dc.tes.data.IDAL;
import com.dc.tes.data.model.Case;
import com.dc.tes.data.model.CaseFlow;
import com.dc.tes.data.model.CaseFlowInstance;
import com.dc.tes.data.model.Transaction;
import com.dc.tes.data.op.Op;
import com.dc.tes.dom.util.DomSerializer;
import com.dc.tes.dom.util.MsgLoader;
import com.dc.tes.exception.ChannelNotFoundException;
import com.dc.tes.exception.ChannelTypeNotFoundException;
import com.dc.tes.exception.CoreErr;
import com.dc.tes.exception.TESException;
import com.dc.tes.fcore.DbGet;
import com.dc.tes.fcore.FCore;
import com.dc.tes.fcore.msg.IPacker;

/**
 * 脚本执行上下文环境 该类型的实例将以ENV变量的方式提供给脚本访问
 * 
 * @author lijic
 * 
 */
public class ScriptEnv {
	public final Log log = LogFactory.getLog(ScriptEnv.class);

	/**
	 * 功能核心实例
	 */
	private final FCore core;

	/**
	 * 当前正在处理的交易码
	 */
	public final String tranCode;
	/**
	 * 当前的默认案例名称
	 */
	public final String caseName;
	/**
	 * 当前正在处理的交易类型
	 */
	public final TransactionMode mode;
	/**
	 * 当前交易使用的通道
	 */
	public final String channel;

	/**
	 * 发起端交易类型
	 */
	public final TransactionMode MODE_CLIENT = TransactionMode.Client;
	/**
	 * 接收端交易类型
	 */
	public final TransactionMode MODE_SERVER = TransactionMode.Server;
	
	private String scriptName; //flowid
	private String tag;        //executeLogid
	private String executeLogId;
	private boolean byapi;
	private String userId;

	public String getScriptName() {
		return scriptName;
	}

	public void setScriptName(String scriptName) {
		this.scriptName = scriptName;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}
	
	public void setExecuteLogId(String executeLogId) {
		this.executeLogId = executeLogId;
	}
	
	public void setByApi(boolean byapi) {
		this.byapi = byapi;
	}
	
	public void setUserId(String userId) {
		this.userId = userId;
	}

	/**
	 * 初始化脚本环境
	 * 
	 * @param core
	 *            功能核心实例
	 * @param tranCode
	 *            交易码
	 * @param mode
	 *            交易类型
	 * @param channel
	 *            通道名称
	 */
	public ScriptEnv(FCore core, String tranCode, String caseName, TransactionMode mode, String channel) {
		this.core = core;
		this.tranCode = tranCode;
		this.caseName = caseName;
		this.mode = mode;
		this.channel = channel;
	}
	
	public ScriptEnv(String scriptName, String tag, FCore core) {
		this.scriptName = scriptName;
		this.tag = tag;
		
		this.core = core;
		this.tranCode = null;
		this.caseName = null;
		this.mode = null;
		this.channel = null;
	}
	
	/**
	 * 跑业务流
	 * 
	 * @param batchNo
	 *             批次号           
	 * @param caseFlowNo
	 *             业务流编号
	 * @throws Exception
	 */	
	public void run_caseFlow(String batchNo, String caseFlowNo) throws Exception {
		
		//根据业务流编号找到该业务流的ID
		IDAL<CaseFlow> caseflowDAL = DALFactory.GetBeanDAL(CaseFlow.class);
		
		CaseFlow caseflow = caseflowDAL.Get(Op.EQ("importBatchNo", batchNo),Op.EQ("caseFlowNo", caseFlowNo));
		if (caseflow == null) {
			log.error("根据[importBatchNo=" + batchNo + ", caseFlowNo=" + caseFlowNo + "]获取不到业务流！");
			return;
		}
		
		//找到该业务流下的第一个案例
		Case c = null;
		try {
			c = DALFactory.GetBeanDAL(Case.class).Get(Op.EQ("caseFlow", caseflow), Op.EQ("sequence", 0));
			if (c == null) {
				log.error("根据[caseFlowId=" + caseflow.getId() + "]获取不到业务流的第一个案例！");
				return;
			}
		}
		catch(Exception e) {
			System.out.print("查找业务流下的第一个案例[执行序号为0的]失败，错误提示信息：" + e.getMessage());
			return;
		}
	
		//新增一条业务流实例
		CaseFlowInstance cfi = new CaseFlowInstance();
		cfi.setCaseFlowName(caseflow.getCaseFlowName());
		cfi.setCaseFlowId(caseflow.getId());
		cfi.setCaseFlowNo(caseFlowNo);
		cfi.setCreateTime(new Date());
		cfi.setBeginTime(new Date());
		cfi.setCaseFlowPassFlag(0);
		cfi.setExecuteLogId(Integer.valueOf(executeLogId));
		if (DbGet.m_iRoundId > 0) {
			cfi.setRoundId(DbGet.m_iRoundId);
		}
		//给业务流实例表添加一条记录
		IDAL<CaseFlowInstance> cfiDAL = DALFactory.GetBeanDAL(CaseFlowInstance.class);
		cfiDAL.Add(cfi);


		//获取交易码
		Transaction tran = DALFactory.GetBeanDAL(Transaction.class).Get(Op.EQ("transactionId", c.getTransactionId()));
		String tranCode = tran.getTranCode();
		
		//获取交易通道
		String channel = tran.getChannel();
		
		OutMessage out = new OutMessage();
		//交易的请求报文模板
		try {
			out.data = MsgLoader.LoadXml(c.getRequestXml());
		}
		catch(Exception e) {
			System.out.println("装载xml报文出错："+e.getMessage());
			return;
		}
		out.tranCode = tranCode;
		out.channel = channel;
		//改存caseId来唯一确定案例
		out.caseName = c.getCaseId();
		out.preserved1 = Integer.valueOf(executeLogId);
		
		//组包前报文的预处理(报文内有函数)
		//out.data = core.prePackProcess(out);
		
		// 获取组包组件
		IPacker packer = core.packers.get(channel);
		try 
		{
			// 组包
			out.bin = packer.Pack(out.data, new MsgContext(tranCode));
		}
		catch(Exception e) {
			System.out.println("组包错误：" + e.getMessage());
			return;
		}
		
		ISenderChannel _channel = (ISenderChannel) this.core.channels.getChannel(out.channel);
			
		//保存业务流实例id
		out.preserved2 = cfi.getId();
		
		//提供打印详细日志
		out.preserved3 = this;
		
		out.userId = userId;
		
		out.byapi = byapi;
		
		try 
		{
			//发送
			_channel.Send(out, 0);
		}
		catch(Exception e) {
			System.out.println("发送错误：" + e.getMessage());
			return;
		}
	}

	/**
	 * 随机获取TCP通道名称
	 * 
	 * @param type
	 *             通道类型： Send or Recv
	 * @return 通道名称
	 * @throws Exception
	 */
	public String getRandomTcpChannel(String type) throws Exception {
		Map<Integer, String> channelNameList = new LinkedHashMap<Integer,String>();
		
		int i = 0;
		
		if(type.equalsIgnoreCase("send")){
			for(String channelName : this.core.channels.getChannelNames()){
				if(this.core.channels.getChannel(channelName) instanceof com.dc.tes.channel.adapter.tcp.RemoteSender){					
					channelNameList.put(i,channelName);	
					i++;
				}		
			}
		} else if (type.equalsIgnoreCase("recv")){
			for(String channelName : this.core.channels.getChannelNames()){
				if(this.core.channels.getChannel(channelName) instanceof com.dc.tes.channel.adapter.tcp.RemoteListener){					
					channelNameList.put(i,channelName);	
					i++;
				}		
			}
		}
		else 
			throw new ChannelTypeNotFoundException(type);
		Random rd = new Random();
		return channelNameList.get(rd.nextInt(i));
	}	
	
	/**
	 * 求余
	 * 
	 * @param channelCount
	 *            交易码
	 * @return 余数
	 * @throws Exception
	 */
	public String random_channel_no(String totalChannelCount) throws Exception {
		log.debug("SCRIPT mod channelCount=" + totalChannelCount);
		
		int n = Integer.parseInt(totalChannelCount);
		Random r = new Random();
		int rdm = r.nextInt(10);
		int iMod = rdm % n; 
		int iChannel = iMod + 1;
		return Integer.toString(iChannel);
	}
	


	
	/**
	 * 读取指定的案例
	 * 
	 * @param tranCode
	 *            交易码
	 * @param caseName
	 *            案例名称
	 * @param mode
	 *            交易类型
	 * @return 案例的报文数据的json形式 如果案例无法解析 则返回案例名称
	 * @throws Exception
	 */
	public String load(String tranCode, String caseName, TransactionMode mode) throws Exception {
		log.debug("SCRIPT loadCase tranCode=" + tranCode + " caseName= " + caseName);

		// 取指定的案例的报文数据
		Transaction tran = this.core.da.GetTran(tranCode, mode);
		Case c = this.core.da.GetCase(caseName, tran);

		// 如果该报文不可解析 则返回一个空结构 
		if (c.getIsParseable() == 0)
			return "{'.nodata':true}";
		else
			// 否则返回报文数据的json表示
			return DomSerializer.SerializeToJson(MsgLoader.LoadXml(c.getRequestXml()));
	}
	
	/**
	 * 读取指定的案例
	 * 
	 * @param tranCode
	 *            交易码
	 * @param caseNo
	 *            案例名称
	 * @param mode
	 *            交易类型
	 * @return 案例的报文数据的json形式 如果案例无法解析 则返回案例名称
	 * @throws Exception
	 */
	public String loadByCaseNo(String tranCode, String caseNo, TransactionMode mode) throws Exception {
		log.debug("SCRIPT loadCase tranCode=" + tranCode + " caseNo= " + caseNo);

		// 取指定的案例的报文数据
		Transaction tran = this.core.da.GetTran(tranCode, mode);
		Case c = GetCase(caseNo, tran);

		// 如果该报文不可解析 则返回一个空结构 
		if (c.getIsParseable() == 0)
			return "{'.nodata':true}";
		else
			// 否则返回报文数据的json表示
			return DomSerializer.SerializeToJson(MsgLoader.LoadXml(c.getRequestXml()));
	}

	private Case GetCase(String caseNo, Transaction tran) {
		Case c = DALFactory.GetBeanDAL(Case.class).Get(Op.EQ("transactionId", tran.getTransactionId()), Op.EQ("caseNo", caseNo));
		if (c == null)
			throw new TESException(CoreErr.CaseNotFound, "caseNo: " + caseNo + " tranCode: " + tran.getTranCode() + " tranMode: " + (tran.getIsClientSimu() == 0 ? TransactionMode.Client : TransactionMode.Server));
		return c;
	}

	/**
	 * 向被测系统发送指定的案例
	 * 
	 * @param msg
	 *            要发送的报文
	 * @param channel
	 *            使用的通道名称
	 * @param mode
	 *            交易类型
	 * @param timeout
	 *            超时时间 单位为毫秒
	 * @return 从被测系统返回的报文
	 * @throws Exception
	 */
	public Object send(NativeObject msg, TransactionMode mode, String channel, int timeout) throws Exception {
		// 取通道 顺便判断给定的通道名在系统中是否存在
		ISenderChannel _channel;
		try {
			_channel = (ISenderChannel) this.core.channels.getChannel(channel);
		} catch (ClassCastException ex) {
			throw new ChannelNotFoundException(channel, true);
		}

		// 准备向被测系统发送的报文
		OutMessage out = ScriptMachine.prepareMessage(msg, this.core, mode);

		// 调用核心进行发起
		final InMessage in = _channel.Send(out, timeout);
        if(in == null)
        {
        	log.info("Send返回的报文为空，无需继续进行报文的解析和执行！");
        	return null;
        }
		
		// 将接收到的报文解析后返回给脚本执行机
		return ScriptMachine.parseMessage(in, this.core, mode);
	}

	/**
	 * 监听被测系统发来的请求
	 * 
	 * @param tranCode
	 *            要监听的交易码
	 * @param channel
	 *            在其上进行监听的通道
	 * @param timeout
	 *            超时时间 单位毫秒
	 * @return 接收到的请求 或为null
	 * @throws Exception
	 */
	public Object listen(String tranCode, String channel, int timeout) throws Exception {
		// 判断要进行监听的交易码在系统中是否存在
		this.core.da.GetTran(tranCode, TransactionMode.Server);
		// 判断要进行监听的通道在系统中是否存在
		if (channel != null)
			if (!(this.core.channels.getChannel(channel) instanceof IListenerChannel))
				throw new ChannelNotFoundException(channel, false);

		// 调用核心进行监听 此时当前线程被阻塞
		this.stateLog("正在侦听" + tranCode, 0);
		InMessage msg = this.core.Listen(tranCode, timeout);

		if (msg != null)
			// 将接收到的报文解析后返回给脚本执行机
			return ScriptMachine.parseMessage(msg, this.core, TransactionMode.Server);
		else
			return null;
	}

	/**
	 * 对指定的请求进行回复
	 * 
	 * @param channel
	 *            该请求使用的通道
	 * @param t
	 *            接收该请求的线程 如果为null则表示在当前线程上进行回复 这种情况适用于reply(IN, xxx)
	 * @param msg
	 *            回复的报文
	 * @param delay
	 *            延时时间
	 * @throws Exception
	 */
	public void reply(String channel, Thread t, NativeObject msg, int delay) throws Exception {
		// 取通道 这个通道名是在接收到这个请求时得到的 并非用户填写 所以不需要判断
		IListenerChannel _channel = (IListenerChannel) this.core.channels.getChannel(channel);

		// 准备向被测系统返回的报文
		OutMessage out = ScriptMachine.prepareMessage(msg, this.core, TransactionMode.Server);
		// 设置延时时间
		out.delay = delay;
		
		// 执行回复过程
		_channel.Reply(out, t == null ? Thread.currentThread() : t);
	}

	/**
	 * 使用多个报文对指定的请求进行回复
	 * 
	 * @param channel
	 *            该请求使用的通道
	 * @param t
	 *            接收该请求的线程 如果为null则表示在当前线程上进行回复 这种情况适用于reply(IN, xxx)
	 * @param array
	 *            回复的报文组成的数组
	 * @param delay
	 *            延时时间
	 * @throws Exception
	 */
	public void reply(String channel, Thread t, NativeArray array, int delay) throws Exception {
		// 取通道 这个通道名是在接收到这个请求时得到的 并非用户填写 所以不需要判断
		IListenerChannel _channel = (IListenerChannel) this.core.channels.getChannel(channel);

		// 返回报文的列表
		List<OutMessage> list = new ArrayList<OutMessage>();

		// 准备向被测系统返回的报文
		for (int i = 0; i < array.getLength(); i++) {
			OutMessage out = ScriptMachine.prepareMessage((NativeObject) array.get(i, null), this.core, TransactionMode.Server);
			out.delay = delay;
			list.add(out);
		}

		// 执行回复过程
		_channel.Reply(list.toArray(new OutMessage[0]), t == null ? Thread.currentThread() : t);
	}

	/**
	 * 退回某个请求
	 * 
	 * @param msg
	 *            要退回的请求
	 * @throws Exception
	 */
	public void reject(InMessage msg) throws Exception {
		this.core.Reject(msg);
	}

	/**
	 * 格式化日期
	 * 
	 * @param pattern
	 *            日期时间格式
	 * @param date
	 *            要被格式化的日期
	 * @return 日期时间字符串
	 */
	public String time(String pattern, Date date) {
		return new SimpleDateFormat(pattern).format(date);
	}

	/**
	 * 发送log日志到监控服务
	 * 
	 * @param scriptName
	 * @param tag
	 * @param msg
	 * @param row
	 * @param state
	 * @param iserror
	 */
	private void sendLog(String scriptName, String tag, String msg, int row, int state, boolean iserror) {
		LogFChannel logchannel = (LogFChannel) this.core.channels.getChannel("LOG");
		logchannel.ReportFlowLogMessage(scriptName, tag, msg, row, state, iserror);
	}

	/**
	 * 发送mlog函数的内容，提供给js使用
	 * 
	 * @param logmsg
	 */
	public void flowLog(String logmsg) {
		log.info(logmsg);
		this.sendLog(scriptName, tag, logmsg, 0, 0, true);
	}

	/**
	 * 状态日志
	 * 
	 * @param logmsg
	 * @param state
	 */
	public void stateLog(String logmsg, int state) {
		this.sendLog(scriptName, tag, logmsg, 0, state, true);
	}

	/**
	 * 出错日志
	 * 
	 * @param logmsg
	 * @param row
	 */
	public void errorLog(String logmsg, int row) {
		this.sendLog(scriptName, tag, logmsg, row, 1, false);
	}


	
}
