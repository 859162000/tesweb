package com.dc.tes.fcore;


import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;

import com.dc.tes.Core;
import com.dc.tes.InMessage;
import com.dc.tes.OutMessage;
import com.dc.tes.TransactionMode;
import com.dc.tes.channel.IChannel;
import com.dc.tes.channel.IListenerChannel;
import com.dc.tes.channel.ISenderChannel;
import com.dc.tes.channel.internal.ILogChannel;
import com.dc.tes.channel.internal.LogFChannel;
import com.dc.tes.channel.internal.UIChannel;
import com.dc.tes.data.DALFactory;
import com.dc.tes.data.IRuntimeDAL;
import com.dc.tes.data.RuntimeDAL;
import com.dc.tes.dom.MsgDocument;
import com.dc.tes.dom.util.MsgLoader;
import com.dc.tes.fcore.msg.IPacker;
import com.dc.tes.fcore.msg.IUnpacker;
import com.dc.tes.fcore.script.MsgContext;
import com.dc.tes.fcore.script.ScriptMachine;
import com.dc.tes.txcode.ITranCodeRecogniser;
import com.dc.tes.util.RuntimeUtils;
import com.dc.tes.util.XmlUtils;
import com.dc.tes.data.model.Case;
import com.dc.tes.data.model.CaseFlow;
import com.dc.tes.data.model.CaseFlowInstance;
import com.dc.tes.data.model.CaseInstance;
import com.dc.tes.data.model.ExecuteLog;
import com.dc.tes.data.model.Transaction;
import com.dc.tes.exception.ErrCode;
import com.dc.tes.exception.TESException;

/**
 * 模拟器功能核心
 * 
 * @author Huangzx
 * 
 */

public class FCore extends Core {
	
	private static final Log log = LogFactory.getLog(FCore.class);

	/**
	 * 初始化模拟器功能核心实例
	 * 
	 * @param instanceName
	 *            实例名称
	 */
	public FCore(String instanceName) {
		super(instanceName);
	}


	/**
	 * 功能核心入口点
	 * 
	 * @param args
	 *            命令行参数
	 */
	public static void main(String[] args) {

		try {
			String instanceName = "";
			if(args.length == 0) {
				Document doc = XmlUtils.LoadXml(RuntimeUtils.OpenResource("base.xml"));
				instanceName = XmlUtils.SelectNodeText(doc, "//config/name");
			} else {
				instanceName = args[0];
			}
			new FCore(instanceName).Start();
		} catch (Exception ex) {
			log.fatal("模拟器功能核心启动失败", ex);
			System.out.println("模拟器功能核心启动失败，错误提示信息：" + ex.getMessage());
			ex.printStackTrace();
			System.exit(-1);
		}
	}

	public static int StartCore() {
		try {
			Document doc = XmlUtils.LoadXml(RuntimeUtils.OpenResource("base.xml"));
			String instanceName = XmlUtils.SelectNodeText(doc, "//config/name");

			new FCore(instanceName).Start();
		} catch (Exception ex) {
			log.fatal("模拟器功能核心启动失败", ex);
			System.exit(-1);
		}
		return com.dc.tes.channel.remote.ChannelServer.LChannleNum();
	}

	
	public void Start() {
		
		super.Start();
	
		DbGet.m_iRoundId = DbGet.GetTestRoundId(this.da.GetSystem().getSystemId());
		DbGet.m_sysType = DbGet.getSysTypeBySysTypeId(this.da.GetSystem().getSystemId());
		
		CaseBusinessProcess.m_core = this;

		ParameterProcess.Start(); //不仅仅是查SQL参数，还查字段参数
		
		//是否需要进行SQL的执行和检查？
		if (0 == this.da.GetSystem().getNeedSqlCheck()) {
			return;
		}
	}
	
	/**
	 *向监控服务送日志，这是全局错误，即任何人都能看到的信息
	 *用于抛出异步通讯时，抛出 得到原案例实例前 的所有错误
	 */
	@Override
	protected void LOG(InMessage in, OutMessage out, TransactionMode mode) throws Exception {

		ILogChannel logChannel = (ILogChannel) this.channels.getChannel("LOG");
		if (mode.equals(TransactionMode.Client))
			logChannel.ReportClientMessage(out, in);
		else
			logChannel.ReportServerMessage(in, out);
	}
	
	@Override
	protected IRuntimeDAL createRuntimeDAL(String instanceName) throws Exception {
		return new RuntimeDAL(instanceName, this.config);
	}

	@Override
	protected Map<String, IChannel> createDefaultChannels() {
		Map<String, IChannel> channels = new HashMap<String, IChannel>();

		channels.put("UI", UIChannel.instance);
		channels.put("LOG", LogFChannel.instance);

		return channels;
	}


	//跑脚本
	public void RunScript(String code, String name, String tag, String executeLogId, String userId, boolean byapi) throws Exception {
		log.info("开始执行脚本" + name + " {tag:" + tag + "}");
		log.debug(code);

		// 调用脚本执行机执行脚本
		ScriptMachine.Exec(code, null, null, this, null, null, TransactionMode.Client, name, tag ,executeLogId, userId, byapi);

		log.info("脚本执行完毕");
	}
	

	//发起端交易预处理
	@Override
	public void preClientTran(final OutMessage out) throws Exception {
		
		CaseFlow caseFlow = DatabaseDAL.GetCaseFlow(out.caseFlowID);	
		CaseFlowInstance caseFlowInstance = DatabaseDAL.newCaseFlowInstance(caseFlow, out.executeLogID);
		//获取案例
		Case icase = DatabaseDAL.GetNextCaseFromCaseFlow(caseFlow, out.executeLogID, null);	
		FlowLog(out, "正在处理第"+(icase.getSequence()+1)+"个案例");
		
		Transaction tran = DatabaseDAL.GetTransactionByCase(icase);
		
		out.caseID = icase.getCaseId();	
		out.channel = tran.getChannel();
		
		out.data = MsgLoader.LoadXml(icase.getRequestXml());
		
		out.data = PackProcess.prePackProcess(out, null);
		
		IPacker packer = packers.get(out.channel);
		out.bin = packer.Pack(out.data, new MsgContext(tran.getTranCode()));
		
		FlowLog(out,"------组包成功");
		
		//异步需要设置索引
		if(!this.da.isSync()) {
			//索引有可能是动态生成,因此得重新从报文中获取
			//需要留意组包时动态生成的字段是否已经重写入out.data里
			ITranCodeRecogniser tx = this.txcodeRecognisers.get(out.channel);
			out.caseIndex = tx.Recognise(out.data.toString().getBytes());
		}
		
		DatabaseDAL.newCaseInstance(icase, caseFlowInstance, out, null);
	}	 
	
	//拆解请求包（发起方模式）
	public CaseInstance unPackInMsg4Client(InMessage in) {
		
		CaseInstance ci = new CaseInstance();
		
		// 如果没有指定延迟拆包 则在接到返回报文后进行拆包
		if (!this.config.LAZY_UNPACK) {
			log.info("将收到结果进行拆包");

			boolean bParseTranCodeBeforeUnPackInMsgOk = false;
			// 这个地方要按照系统定义来决定使用何种拆包模板（SysType.RESPONSESTRUCT VS.
			// Transaction.RESPONSESTRUCT）
			String templateInXML = "";
			if (da.GetSystem().getUseSameResponseStruct() == 1) { //所有交易都使用同一响应模板
				templateInXML = this.da.GetSystem().getResponseStruct();
			}
			else {//每个交易都使用自己的响应模板，各不相同
				// 获取拆包模板
				if (this.da.isSync()) {
					// 同步通讯下，能够获取到当前交易是什么
					int executeLogID = 0;
					if (in.executeLogID != null) {
						executeLogID = Integer.parseInt(in.executeLogID);
					}
					int caseID = 0;
					if (in.caseID != null) {
						caseID = Integer.parseInt(in.caseID);
					}
					ci = DbGet.getCaseInstanceByExecuteCase(executeLogID, caseID);
					if (ci == null) {
						return ci;
					}
					String sTransactionId = ci.getTransactionId().toString();
					Transaction tran = DbGet.getTransctionByTransactionId(sTransactionId);
					if (tran == null) {
						return ci;
					}
					in.tranCode =  tran.getTranCode();
					templateInXML = tran.getResponseStruct();
				} 
				else { //异步方式，先要获取交易吗，再确定交易是什么
					// 解析交易码
					int isClientSimu = this.da.GetSystem().getIsClientSimu();
					String encoding = this.da.GetSystem().getEncoding4RequestMsg();
					if (isClientSimu != 0) { //发送端
						encoding = this.da.GetSystem().getEncoding4ResponseMsg();
					}
					String msgStr = "";
					try {
						msgStr = new String(in.bin, encoding);
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
					System.out.println("接收到的应答报文为：" + msgStr);
					byte[] inMsgByte = msgStr.getBytes();
					ITranCodeRecogniser tx = this.txcodeRecognisers.get(in.channel);
					String tranCode = "";
					try {
						tranCode = in.tranCode = tx.Recognise(inMsgByte);
						log.info("解析出交易码[" + tranCode + "]");
						
						//根据请求交易码找到交易定义
						Transaction trans = DbGet.getResponseTransctionByTranCode(in.tranCode, da.GetSystem().getSystemId());
						if (trans == null) {
							return null;
						}
						templateInXML = trans.getResponseStruct();
						//异步通讯下，有理由相信同个系统可以用同个模板进行拆包
						if (templateInXML == null) {
							templateInXML = this.da.GetSystem().getResponseStruct();
						}
						if (templateInXML == null) {
							System.out.println("获取交易的应答报文模板失败，请检查交易和系统配置！");
							return null;
						}
						bParseTranCodeBeforeUnPackInMsgOk = true;
					} catch (Exception e) {
						bParseTranCodeBeforeUnPackInMsgOk = false;
						e.printStackTrace();
					} //in.bin;				
				}
			}

			try {
				if (templateInXML == null || templateInXML.isEmpty()) {
					System.out.println("[配置错误]提示信息：交易模板为空，请检查系统和交易的配置是否正确！");
					return null;
				}
				MsgDocument templateIn = MsgLoader.LoadXml(templateInXML);
				IUnpacker unpacker = unpackers.get(in.channel);
				//拆包
				in.data = unpacker.Unpack(in.bin, templateIn, new MsgContext(in.tranCode));

				// 异步通讯需要通过解析出报文的流水号来从数据库中取原发送案例实例
				if (!this.da.isSync()) {
					if (!bParseTranCodeBeforeUnPackInMsgOk) {
						ITranCodeRecogniser tx = this.txcodeRecognisers.get(in.channel);
						in.caseIndex = tx.Recognise(in.data.toString().getBytes());
						//in.caseIndex = tx.Recognise(in.bin);
						ci = DatabaseDAL.GetCaseInstance(in.caseIndex);
						in.caseFlowID = ci.getCaseFlowInstance().getCaseFlowId().toString();
						in.executeLogID = ci.getExecuteLogId().toString();
						in.caseID = ci.getCaseId().toString();
					}
				}
			} catch (Exception ex) {
				in.ex = (ex instanceof TESException) ? (TESException) ex
						: new TESException(ErrCode.UNKNOWN, ex);
				if (!this.da.isSync()) {
					// 拆包前异步通讯发生的错误 只能全局抛出
					// 只在此处使用该方法，因为是全局日志，所以如果太多其他信息的话会比较难区分
					try {
						this.LOG(in, null, TransactionMode.Client);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						log.error("传送界面日志失败 ");
						e.printStackTrace();
					}
				} else {
					ErrorLog(in, in.ex.getMessage());
				}
				in.ex.printStackTrace();
				return ci;
			}
		}
		return ci;
	}
	
	
	//拆解请求包（接收方模式）
	public void unPackInMsg4Server(InMessage in, Transaction trans) {
		// 如果没有指定延迟拆包 则在接到返回报文后进行拆包
		if (!this.config.LAZY_UNPACK) {
			log.info("将收到结果进行拆包");
			// 这个地方要按照系统定义来决定使用何种拆包模板（SysType.RESPONSESTRUCT VS.
			// Transaction.RESPONSESTRUCT）
			String templateInXML = "";
			if (da.GetSystem().getUseSameResponseStruct() == 1) { //所有交易都使用同一响应模板
				templateInXML = this.da.GetSystem().getResponseStruct();
			}
			else {//每个交易都使用自己的响应模板，各不相同
				// 获取拆包模板（对请求报文拆包，使用请求报文模板）
				templateInXML = trans.getRequestStruct(); //getResponseStruct
			}

			try {
				//拆包
				MsgDocument templateIn = MsgLoader.LoadXml(templateInXML);
				IUnpacker unpacker = unpackers.get(in.channel);
				in.data = unpacker.Unpack(in.bin, templateIn, new MsgContext(trans.getTranCode()));
			} catch (Exception ex) {
				in.ex = (ex instanceof TESException) ? (TESException) ex
						: new TESException(ErrCode.UNKNOWN, ex);
				if (!this.da.isSync()) {
					// 拆包前异步通讯发生的错误 只能全局抛出
					// 只在此处使用该方法，因为是全局日志，所以如果太多其他信息的话会比较难区分
					try {
						this.LOG(in, null, TransactionMode.Client);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						log.error("传送界面日志失败 ");
						e.printStackTrace();
					}
				} else {
					ErrorLog(in, in.ex.getMessage());
				}
				in.ex.printStackTrace();
				return;
			}
		}
	}
	
	//后处理(发起方模式)
	@Override
	public void postClientTran(InMessage in) {
		
		//CaseInstance ci = new CaseInstance();
		CaseInstance ci = unPackInMsg4Client(in);
		
		FlowLog(in, "------成功收到返回报文");
		
		//刷新一遍参数
		this.da.Refresh();
			
		try {	
			CaseBusinessProcess.processCaseBusiness(in, ci);
		} catch(Exception ex) {
			System.out.print("处理案例业务流程出错");
			System.out.println(ex.getMessage());
			ex = (ex instanceof TESException) ? (TESException) ex : new TESException(ErrCode.UNKNOWN, ex);
			ErrorLog(in, ex.getMessage());
		}
	}
	
	//服务器端的业务处理(接收方模式)
	@Override
	protected OutMessage processServerTran(InMessage in, IListenerChannel channel, StringBuilder outChannelName) throws Exception {

		OutMessage out = new OutMessage();
		
		int iResponseMode = da.GetSystem().getResponseMode();
		
		//根据请求交易码找到交易定义
		Transaction trans = DbGet.getResponseTransctionByTranCode(in.tranCode, da.GetSystem().getSystemId());
		if (trans == null) {
			return null;
		}
		// 判断该交易是否存在脚本 如果存在脚本则执行脚本 否则执行默认策略
		String script = trans.getScript();
		
		outChannelName.append(trans.getChannel());
		
		//获取到一个案例
		Case c = DbGet.getTransctionDefaultCase(trans.getTransactionId());
		if (c == null) {
			c = DbGet.getTransctionNonDefaultCase(trans.getTransactionId());
			if (c == null) {//insert
				c = DatabaseDAL.newCase(trans);
			}
			else {//update
				c.setIsdefault(1);
				DALFactory.GetBeanDAL(Case.class).Edit(c);
			}
		}
		
		//InsertExecuteLog & CaseInstance
		ExecuteLog executeLog = null;
		if (out.executeLogID != null) {
			executeLog = DbGet.getExecuteLogByExecuteLogId(Integer.parseInt(out.executeLogID));	
		}
		if (out.executeLogID == null || executeLog == null) {
			executeLog = DatabaseDAL.newExecuteLog(Integer.parseInt(da.GetSystem().getSystemId()));
		}
		out.executeLogID = String.valueOf(executeLog.getId());
		//int iCaseFlowId = c.getCaseFlow().getId();
		//out.caseFlowID = String.valueOf(iCaseFlowId);
		//CaseFlowInstance cfi = DbGet.getCaseFlowInstanceByExecuteCase(executeLog.getId(), iCaseFlowId);
		//if (cfi == null) {
		//	cfi = DatabaseDAL.newCaseFlowInstance(DbGet.getCaseFlowByCaseFlowId(iCaseFlowId), out.executeLogID);	
		//}
		//应答通道
		out.channel = trans.getChannel();
		out.caseID = c.getCaseId();
		out.caseName = c.getCaseName();
		out.bin = c.getResponseMsg();
		if (c.getIsParseable() == 1)
			out.data = MsgLoader.LoadXml(c.getResponseXml());

		//对请求报文进行拆包并获取in.data
		unPackInMsg4Server(in, trans);
		
		CaseInstance ci = DatabaseDAL.newCaseInstance(c, null, out, in);
		log.info("开始判断是否执行脚本");
		if (script == null || script.trim().length() == 0) {
			log.debug("该交易没有脚本");
		} else {
			// 运行脚本
			log.debug("开始执行脚本...");
			log.debug(script);

			ScriptMachine.Exec(script, in, out, this, in.tranCode, in.channel, 
					TransactionMode.Server, null, null, in.executeLogID, "", false);

			log.debug("脚本执行完毕.");

			// 在脚本中可以对IN变量进行reply()操作，在这种情况下运行到此处时当前交易已经被响应，所以不需要再进行响应操作，这种情况下返回null				
			return in.replyFlag ? null : out;
		}
		
		in.executeLogID = ci.getExecuteLogId().toString();
		//in.caseFlowID = ci.getCaseFlowInstance().getCaseFlowId().toString();
		//in.caseFlowID = String.valueOf(iCaseFlowId);
		in.caseID = ci.getCaseId().toString();
		
		if (iResponseMode == 0) { //默认案例应答
			
			//找到默认的应答案例
			//Case c = DbGet.getTransctionDefaultCase(trans.getTransactionId());
			//out.caseID = c.getCaseId();
			//out.caseName = c.getCaseName();
			/*if (c.getResponseMsg() != null) {
				out.bin = c.getResponseMsg();
			}
			else {*/
				//应答模板
				if (c.getResponseXml() != null) {
					out.data = MsgLoader.LoadXml(c.getResponseXml());
				}
				else {
					out.data = MsgLoader.LoadXml(c.getRequestXml());
					//return null;
				}
				//设置默认值
				out.data = PackProcess.setDefaultValue(out);
				//对应答模板进行处理
				out.data = PackProcess.prePackProcess(out, in);
				//是同步还是异步通讯?
				IPacker packer = packers.get(in.channel);
				if (this.da.GetSystem().getIsSyncComm() == 0) { //异步通讯
					packer = packers.get(out.channel);
					if (packer == null) {
						packer = packers.get(in.channel);
					}					
				}
				if (packer == null) {
					return null;
				}
				//应答报文组包
				out.bin = packer.Pack(out.data, new MsgContext(trans.getTranCode()));
			//}
		} 
		else if (iResponseMode == 1) { //根据交易应答报文解析返回
			if (in.caseID != null) {
				out.caseID = in.caseID;
			}
			//应答模板
			out.data = MsgLoader.LoadXml(trans.getResponseStruct());
			//设置默认值
			out.data = PackProcess.setDefaultValue(out);
			//对应答模板进行处理
			out.data = PackProcess.prePackProcess(out, in);
			//应答报文组包
			//是同步还是异步通讯?
			IPacker packer = packers.get(in.channel);
			if (this.da.GetSystem().getIsSyncComm() == 0) { //异步通讯
				packer = packers.get(out.channel);
				if (packer == null) {
					packer = packers.get(in.channel);
				}					
			}
			if (packer == null) {
				return null;
			}
			if (trans.getTranCode() != null) {
				out.bin = packer.Pack(out.data, new MsgContext(trans.getTranCode()));
			}
			else {
				return null;
			}
		}
		else {
			System.out.println("无效的应答模式，请检查系统配置！");
			return null;			
		}		
		
		try {
			if (this.da.GetSystem().getIsSyncComm() == 0) { //异步通讯
				ISenderChannel _channel = (ISenderChannel) this.channels.getChannel(out.channel);
				_channel.Send(out, 0);
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}

		executeLog.setPassFlag(1);
		executeLog.setEndRunTime(new Date());
		DatabaseDAL.elDAL.Edit(executeLog);
		//cfi.setCaseFlowPassFlag(1);
		//cfi.setEndTime(new Date());
		//DatabaseDAL.cfiDAL.Edit(cfi);
		DatabaseDAL.SetCaseInstanceMsg(ci, c, null, out, in);
		DatabaseDAL.ciDAL.Edit(ci);
		return out;
	}
	
}
