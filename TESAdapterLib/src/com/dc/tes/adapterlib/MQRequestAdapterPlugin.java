package com.dc.tes.adapterlib;

import java.io.UnsupportedEncodingException;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.Random;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


import com.dc.tes.Config;
import com.dc.tes.adapter.IRequestAdapter;
import com.dc.tes.adapter.context.IAdapterEnvContext;
import com.dc.tes.adapter.context.IRequestAdapterEnvContext;
import com.dc.tes.data.IRuntimeDAL;
import com.dc.tes.data.RuntimeDAL;

import com.ibm.mq.MQC;
import com.ibm.mq.MQEnvironment;
import com.ibm.mq.MQException;
import com.ibm.mq.MQGetMessageOptions;
import com.ibm.mq.MQMessage;
import com.ibm.mq.MQPutMessageOptions;
import com.ibm.mq.MQQueue;
import com.ibm.mq.MQQueueManager;



public class MQRequestAdapterPlugin  implements IRequestAdapter{
	
	private static Log logger = LogFactory.getLog(MQRequestAdapterPlugin.class);

	private IRequestAdapterEnvContext m_TESEnv = null;
	
	/** 核心基础配置 */
	public static Config m_config;

	/** 运行时数据访问接口 */
	public static IRuntimeDAL da;
	
	private static int m_iMQPutCount = 0;

	// Queue Magager队列管理器名
	private static String SIM_QM = "QMPAY2SIMU";

	// Recorder Queue录制报文队列
	private static String SIM_RECORDER_QUEUE = "QL_RECDR_SIMU_1";
	
	// Request Queue请求报文队列
	private static String SIM_REQUEST_QUEUE = "QR_SIMU_MBFE6";

	// Response Queue响应报文队列
	private static String SIM_RESPONSE_QUEUE = "QL_MBFE_SIMU_6";

	// MQ通道方式
	private static String CHANNEL = "CLIENT.QMPAY2SIMU"; // "CH_SIMU_MBFE";

	/* MQ服务器地址 */
	// #MQSVRIP=128.192.34.34
	private static String MQSVRIP = "127.0.0.1";

	/* MQ监听端口 */
	private static int MQLSRPORT = 1449;

	/* MQ通讯方式 */
	private static String MQCOMMTYPE = "TCP";

	/* 超时时间（秒） */
	private static int MQTIMEOUT = 30;

	/* 字符编码 */
	private static int CCSID = 819;
	
	private static String m_ENCODING = "utf-8";

	// 是否调用安全服务处理接收报文,解密操作,0否 1是
	private boolean m_needDecrypt = false;

	// 是否调用安全服务处理返回报文,加密操作,0否 1是
	private boolean m_needEncrypt = false;

	private String m_CryptClsName = "";

	private String m_secureFactoryPackage = "com.dc.tes.adapter.secure.factory.";

	//处于处于录制状态
	private boolean m_RECORDING = false;
	private int m_SystemId = 0;
	private String m_SystemName = "";
	private int m_RecordUserId = 0;

	private Properties m_config_props = null;
	
	@Override
	public Properties GetAdapterConfigProperties() {
		return m_config_props;
	}
	
	public byte[] Send(byte[] msg) throws Exception {

		String strMsgData = new String(msg, m_ENCODING);
		
		String backmsg = "";
		try {
			backmsg = RequestToMQ(MQSVRIP, MQLSRPORT, SIM_QM, SIM_REQUEST_QUEUE, SIM_RESPONSE_QUEUE, SIM_QM, CHANNEL, CCSID, msg);
		}
		catch(Exception e) {
			System.out.println(e.getMessage());
			backmsg = "从MQ取应答消息失败！异常提示信息：" + e.getMessage();
		}
		
		if (this.m_RECORDING) {
			if (m_config == null) {
				InitDbConnection(this.m_SystemName);
			}
			//写入录制信息
			DbOp.InsertRecordedCase(m_SystemId, m_RecordUserId, strMsgData);
		}
		
		return backmsg.getBytes();
	}
	

	public String AdapterType() {
		return "mq.c";
	}
	

	public boolean Init(IAdapterEnvContext tesENV) {
		// 获取配置信息
		m_TESEnv = (IRequestAdapterEnvContext) tesENV;
		//m_adpHelper = m_TESEnv.getHelper();

		// 处理 核心返回的注册信息
		Properties props = ConfigHelper.getConfig(m_TESEnv.getEvnContext());
		m_config_props = props;

		// 校验必要的初始化信息 是否存在
		String[] keys = new String[] { "CHANNEL", "MQSVRIP", "MQLSRPORT", "SIM.QM", "SIM.REQUEST.QUEUE", "SIM.RESPONSE.QUEUE", "CCSID" };
		if (!ConfigHelper.chkProperKey(props, keys))
			return false;

		if(props.containsKey("CHANNEL"))
			this.CHANNEL = (String) props.get("CHANNEL");
		if(props.containsKey("MQSVRIP"))
			this.MQSVRIP = (String) props.get("MQSVRIP");
		if(props.containsKey("MQLSRPORT"))
			this.MQLSRPORT = Integer.parseInt((String) props.get("MQLSRPORT"));
		if(props.containsKey("MQCOMMTYPE"))
			this.MQCOMMTYPE = (String) props.get("MQCOMMTYPE");
		if(props.containsKey("SIM.QM"))
			this.SIM_QM = (String) props.get("SIM.QM");
		if(props.containsKey("RECORDER.QUEUE"))
			this.SIM_RECORDER_QUEUE = (String) props.get("SIM.RECORDER.QUEUE");
		if(props.containsKey("SIM.REQUEST.QUEUE"))
			this.SIM_REQUEST_QUEUE = (String) props.get("SIM.REQUEST.QUEUE");
		if(props.containsKey("SIM.RESPONSE.QUEUE"))
			this.SIM_RESPONSE_QUEUE = (String) props.get("SIM.RESPONSE.QUEUE");
		if(props.containsKey("CCSID"))
			this.CCSID = Integer.parseInt((String) props.get("CCSID"));
		
		if(props.containsKey("RECORDING"))
			this.m_RECORDING = Integer.parseInt((String) props.getProperty("RECORDING"))==1;
		
		if(props.containsKey("SYSTEMID"))
			this.m_SystemId = Integer.parseInt((String) props.get("SYSTEMID"));
		if(props.containsKey("SYSTEMNAME")) {
			this.m_SystemName = (String) props.get("SYSTEMNAME");		
			try {
				this.m_SystemName = new String(m_SystemName.getBytes("ISO-8859-1"), "utf-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} 
		}
		
		if (props.containsKey("ENCODING"))
			this.m_ENCODING = (String) props.get("ENCODING");
		
		if(props.containsKey("dynamic_in"))
			this.m_needDecrypt = Integer.parseInt((String) props.getProperty("dynamic_in"))==1;
		if(props.containsKey("dynamic_out"))
			this.m_needEncrypt = Integer.parseInt((String) props.getProperty("dynamic_out"))==1;
		if(props.containsKey("dynamic_name"))
			this.m_CryptClsName = this.m_secureFactoryPackage + props.getProperty("dynamic_name");

		if (m_RECORDING && m_config == null) {
			InitDbConnection(this.m_SystemName);
			if (this.m_ENCODING == null) {
				this.m_ENCODING = da.GetSystem().getEncoding4RequestMsg();
			}
		}
		if (this.m_ENCODING == null) {
			this.m_ENCODING = "utf-8";
		}
		
		if (m_RECORDING && m_config == null) {
			InitDbConnection(this.m_SystemName);
		}
		
		logger.info("MQ响应端适配器插件" + this.getClass().getName() + "初始化完成.");
		return true;
	}
	
	
	private String RequestToMQ(String hostName, int port, String reqQMName,
			String reqQName, String respQName, String respQMName,
			String channel, int CCSID, byte[] data) throws Exception {
		
		try {
			if (logger.isDebugEnabled())
				logger.debug(hostName + ":" + port + " sendto:" + reqQMName + "."
						+ reqQName + " recvfrom:" + respQMName + respQName
						+ " by:" + channel + " Content:" + data);
			
			/* 设置MQEnvironment 属性以便客户机连接 */
			MQEnvironment.hostname = hostName;
			MQEnvironment.port = port;
			MQEnvironment.channel = channel;
			MQEnvironment.CCSID = CCSID;
			MQEnvironment.properties.put(MQC.TRANSPORT_PROPERTY, MQC.TRANSPORT_MQSERIES);

			/* 连接到队列管理器 */
			MQQueueManager qMgr = new MQQueueManager(reqQMName);

			/* 设置打开选项以便打开用于输出的队列，如果队列管理器停止，我们也已设置了选项去应对不成功情况 */
			int openOptions = MQC.MQOO_OUTPUT | MQC.MQOO_FAIL_IF_QUIESCING;

			/* 打开打开队列 */
			MQQueue queue = qMgr.accessQueue(reqQName, openOptions, null, null,	null);

			/* 设置放置消息选项，我们将使用默认设置 */
			MQPutMessageOptions pmo = new MQPutMessageOptions();
			pmo.options = pmo.options + MQC.MQPMO_NEW_MSG_ID;
			pmo.options = pmo.options + MQC.MQPMO_SYNCPOINT;

			/* 创建消息缓冲区 */
			MQMessage outMsg = new MQMessage();

			/* 设置MQMD 格式字段 */
			outMsg.format = MQC.MQFMT_STRING;
			outMsg.messageFlags = MQC.MQMT_REQUEST;
			outMsg.replyToQueueName = respQName;
			outMsg.replyToQueueManagerName = respQMName;
			outMsg.characterSet = CCSID;
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmmssSSS");
			String strDateTime = sdf.format(new Date());
			outMsg.messageId = strDateTime.getBytes();
			String strCorrId = "CNAPS2_005" + GetOneRandomDigitStringOf10Bit();
			outMsg.correlationId = strCorrId.getBytes();

			/* 准备用户数据消息 */
			outMsg.write(data);

			/* 在队列上放置消息 */
			queue.put(outMsg, pmo);

			/* 提交事务 */
			qMgr.commit();
			
			/* 关闭请求队列 */
			queue.close();
			
			qMgr.disconnect();

			logger.info(" The message has been Successfully put:" + new String(data, m_ENCODING));
			System.out.println(" The message has been Successfully put:" + new String(data, m_ENCODING));

			m_iMQPutCount++;
			String reponseMsg = "共有 " + m_iMQPutCount + " 条消息写入了 MQ: " + hostName + ":" + port + " [ " + reqQMName + "." + reqQName + " ]";
			return reponseMsg;

			/*
			// 设置打开选项以便队列响应 
			openOptions = MQC.MQOO_INPUT_SHARED | MQC.MQOO_FAIL_IF_QUIESCING;
			MQQueue respQueue = qMgr.accessQueue(respQName, openOptions, null, null, null);
			MQMessage respMessage = new MQMessage();
			MQGetMessageOptions gmo = new MQGetMessageOptions();

			// 在同步点控制下获取消息 
			gmo.options = gmo.options + MQC.MQGMO_SYNCPOINT;
			gmo.options = gmo.options + MQC.MQGMO_WAIT;
			gmo.matchOptions = MQC.MQMO_MATCH_CORREL_ID;
			gmo.waitInterval = 10000;
			respMessage.correlationId = outMsg.messageId;

			
			// 获取响应消息 
			respMessage.characterSet = CCSID;
			respQueue.get(respMessage, gmo);
			byte[] buff = new byte[respMessage.getMessageLength()];
			respMessage.readFully(buff);
			String response = new String(buff);
			logger.info("The response message is : " + response);
			System.out.println(" The response message is : " + response);
			
			qMgr.commit();
			respQueue.close();
			qMgr.disconnect();
			*/

			//return response;
			//return "";
		} catch (Exception ex) {
			System.out.println("从MQ取应答消息失败:" + ex.getMessage());
			throw ex;
		}
	}
	
	
	public byte[] ReadFromRecorderMQ() {

		MQQueue queue = null;
		try {
			
			/* 设置MQEnvironment 属性以便客户机连接 */
			MQEnvironment.hostname = MQSVRIP;
			MQEnvironment.port = MQLSRPORT;
			
			MQEnvironment.CCSID = CCSID;
			MQEnvironment.channel = CHANNEL;
			MQEnvironment.properties.put(MQC.TRANSPORT_PROPERTY, MQC.TRANSPORT_MQSERIES);

			/* 连接到队列管理器 */
			MQQueueManager qMgr = new MQQueueManager(SIM_QM);
			
			/*
			 * 设置打开选项以便打开用于输出的队列，如果队列管理器停止，我们也 已设置了选项去应对不成功情况
			 */
			int iQopenOptions = MQC.MQOO_INPUT_SHARED	| MQC.MQOO_FAIL_IF_QUIESCING;

			/* 打开队列 */
			queue = qMgr.accessQueue(SIM_RECORDER_QUEUE, iQopenOptions, null, null, null);
			
			/* 设置放置消息选项 */
			MQGetMessageOptions gmo = new MQGetMessageOptions();

			/* 在同步点控制下取消息 */
			gmo.options = gmo.options + MQC.MQGMO_SYNCPOINT;

			/* 如果队列上没有消息则等待 */
			gmo.options = gmo.options + MQC.MQGMO_WAIT;

			/* 如果队列管理器停止则失败 */
			gmo.options = gmo.options + MQC.MQGMO_FAIL_IF_QUIESCING;

			/* 设置等待的时间限制 */
			gmo.waitInterval = -1;

			/* 创建MQMessage 类 */
			MQMessage inMsg = new MQMessage();
			inMsg.characterSet = CCSID;

			/* 从队列到队列缓冲区获取消息 */
			queue.get(inMsg, gmo); //此语句执行完成之后，队列中将不再有消息

			/* 从消息读用户数据 */
			byte[] buff = new byte[inMsg.getMessageLength()];
			inMsg.readFully(buff);
			return buff;
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			if (queue != null) {
				try {
					queue.close();
				} catch (MQException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}
	
	
	public static String GetOneRandomDigitStringOf10Bit() {
		Random r = new Random();
		int i = r.nextInt(99999);
		int j = r.nextInt(99999);
		return NumberFormat5(i) + NumberFormat5(j);
	}


    public static String NumberFormat5(int i) {

	        //得到一个NumberFormat的实例
	        NumberFormat nf = NumberFormat.getInstance();
	        //设置是否使用分组
	        nf.setGroupingUsed(false);
	        //设置最大整数位数
	        nf.setMaximumIntegerDigits(5);
	        //设置最小整数位数    
	        nf.setMinimumIntegerDigits(5);
	        //输出测试语句
	        //System.out.println(nf.format(i));
	        return nf.format(i); 
	}

    
    public void InitDbConnection(String instanceName) {

		// 初始化核心基础配置
		logger.info("初始化基础配置...");
		m_config = new Config();
		logger.info("初始化基础配置成功.");
	
		// 初始化数据访问层
		logger.info("初始化数据访问层...");
		try {
			da = createRuntimeDAL(instanceName);
		} catch (Exception e) {
			e.printStackTrace();
		}
		logger.info("初始化数据访问层成功.");
		//System.out.println();
	}
	
	protected static IRuntimeDAL createRuntimeDAL(String instanceName) throws Exception {
		
		return new RuntimeDAL(instanceName, m_config);
	}

	/*public static void main(String[] args) {
		
		MQRequestAdapterPlugin mqPlugin = new MQRequestAdapterPlugin();

		String str1 = "abcdefg123456";
		byte[] byte1 = str1.getBytes(); 
	
		try {
			mqPlugin.Send(byte1);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}*/

}
