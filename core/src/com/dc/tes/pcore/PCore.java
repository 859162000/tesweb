package com.dc.tes.pcore;

//
//import com.dc.tes.adapter.startup.IStartUp;
//import com.dc.tes.adapter.startup.StartUpUtils;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;

import com.dc.tes.Core;
import com.dc.tes.InMessage;
import com.dc.tes.OutMessage;
import com.dc.tes.TransactionMode;
import com.dc.tes.adapter.startup.IStartUp;
import com.dc.tes.adapter.util.StartUpUtils;
import com.dc.tes.channel.IChannel;
import com.dc.tes.channel.IListenerChannel;
import com.dc.tes.channel.internal.ILogChannel;
import com.dc.tes.channel.internal.LogPChannel;
import com.dc.tes.channel.internal.UIChannel;
import com.dc.tes.channel.test.TCPSnooping;
import com.dc.tes.data.CacheRuntimeDAL;
import com.dc.tes.data.IRuntimeDAL;
import com.dc.tes.data.model.Case;
import com.dc.tes.data.model.Transaction;
import com.dc.tes.util.RuntimeUtils;
import com.dc.tes.util.XmlUtils;

/**
 * 模拟器性能核心
 * 
 * @author lijic
 * 
 */
public class PCore extends Core {
	private static final Log log = LogFactory.getLog(PCore.class);

	/**
	 * 初始化模拟器性能核心实例
	 * 
	 * @param instanceName
	 *            核心实例名称
	 * @throws Exception
	 */
	public PCore(String instanceName) {
		super(instanceName);
	}

	@Override
	protected OutMessage processServerTran(InMessage in, IListenerChannel channel, StringBuilder sendChannel) {
		// 采用最简单的方式 返回该交易下的默认案例
		Transaction tran = this.da.GetTran(in.tranCode, TransactionMode.Server);
		Case c = this.da.GetDefaultCase(tran);

		OutMessage out = new OutMessage();
		out.tranCode = in.tranCode;
		out.channel = in.channel;
		out.bin = c.getResponseMsg();
		out.delay = this.da.GetDelayTIme(tran);
		log.info("延时时间" + out.delay);
		return out;
	}

	@Override
	public void preClientTran(OutMessage out) {
		//判断是否提供了bin 如果提供了bin则将bin送出
		if (out.bin != null)
			return;
		else {
			// 如果没有提供bin 则将case的二进制数据送出
			if (out.caseName != null) {
				Transaction tran = this.da.GetTran(out.tranCode, TransactionMode.Client);
				String caseName = this.da.ListCases(tran).get(0);
				Case c = this.da.GetCase(caseName, tran);

				out.bin = c.getRequestMsg();
			} else
				// 未提供caseName 无法确定要发送的数据
				throw new UnsupportedOperationException("未提供caseName，无法确定要发送的报文");
		}
	}

	@Override
	public void postClientTran(InMessage in) {
		// 性能核心不对返回报文进行任何处理
	}

	@Override
	protected IRuntimeDAL createRuntimeDAL(String instanceName) throws Exception {
		return new CacheRuntimeDAL(instanceName, this.config);
	}

	@Override
	protected Map<String, IChannel> createDefaultChannels() {
		Map<String, IChannel> channels = new HashMap<String, IChannel>();

		channels.put("UI", UIChannel.instance);
		channels.put("LOG", LogPChannel.instance);

		return channels;
	}

	/**
	 * 性能核心入口点
	 * 
	 * @param args
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
			new PCore(instanceName).Start();
			//			TestLocalChannel.REG();
			//			TestLocalChannel.MSG();
			//			TestLocalChannel.UNREG();

			//本地适配器启动
			IStartUp localAdapter = StartUpUtils.getIStartUpInst("local");
			localAdapter.startUp();
			//			
			TCPSnooping tt = new TCPSnooping();
			tt.listening();
		} catch (Exception ex) {
			log.fatal("模拟器性能核心启动失败", ex);
			System.exit(-1);
		}
	}

	public static int StartCore() {
		try {
			Document doc = XmlUtils.LoadXml(RuntimeUtils.OpenResource("base.xml"));
			String instanceName = XmlUtils.SelectNodeText(doc, "//config/name");

			new PCore(instanceName).Start();

			//			TestLocalChannel.REG();
			//			TestLocalChannel.MSG();
			//			TestLocalChannel.UNREG();

			//			//本地适配器启动
			//			IStartUp localAdapter = StartUpUtils.getIStartUpInst("local");
			//			localAdapter.startUp();
			//			
			//			TCPSnooping tt = new TCPSnooping();
			//			tt.listening();
		} catch (Exception ex) {
			log.fatal("模拟器性能核心启动失败", ex);
			System.exit(-1);
		}
		return com.dc.tes.channel.remote.ChannelServer.LChannleNum();
	}

	@Override
	protected void LOG(InMessage in, OutMessage out, TransactionMode mode) throws Exception {
		// TODO Auto-generated method stub
		//记录性能监控日志
		ILogChannel logChannel = (ILogChannel) this.channels.getChannel("LOG");
		if (mode.equals(TransactionMode.Client))
			logChannel.ReportClientMessage(out, in);
		else
			logChannel.ReportServerMessage(in, out);
	}
}
