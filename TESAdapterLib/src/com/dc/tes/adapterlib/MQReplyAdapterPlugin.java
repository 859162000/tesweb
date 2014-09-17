package com.dc.tes.adapterlib;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import mq.MqFunc;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.dc.tes.Config;
import com.dc.tes.adapter.IReplyAdapter;
import com.dc.tes.adapter.IReplyAdapterWorker;
import com.dc.tes.adapter.context.IAdapterEnvContext;
import com.dc.tes.adapter.context.IReplyAdapterEnvContext;
import com.dc.tes.adapter.helper.IReplyAdapterHelper;
import com.dc.tes.adapter.secure.AbstractFactory;
import com.dc.tes.adapter.secure.IDecryptAdapterSecure;
import com.dc.tes.adapter.secure.IEncryptAdapterSecure;
import com.dc.tes.data.IRuntimeDAL;
import com.dc.tes.data.RuntimeDAL;
import com.dc.tes.data.model.RecordedCase;
import com.ibm.mq.MQC;
import com.ibm.mq.MQEnvironment;
import com.ibm.mq.MQException;
import com.ibm.mq.MQGetMessageOptions;
import com.ibm.mq.MQMessage;
import com.ibm.mq.MQPutMessageOptions;
import com.ibm.mq.MQQueue;
import com.ibm.mq.MQQueueManager;


public class MQReplyAdapterPlugin implements IReplyAdapter, IReplyAdapterWorker {

	private static Log logger = LogFactory.getLog(MQReplyAdapterPlugin.class);

	private IReplyAdapterEnvContext m_TESEnv = null;
	private static IReplyAdapterHelper m_adpHelper = null;
	
	/** 核心基础配置 */
	public static Config m_config;

	/** 运行时数据访问接口 */
	public static IRuntimeDAL da;
	
	private MQMessage m_InMsg = null;
	private MQQueueManager m_qMgr = null;
	private int m_MQopenOptions = 0;
	private long m_TimeOfAcceptRequest = 0; // 接收到被测系统请求的时间

	// MQ服务器地址
	private static String HOSTNAME = "127.0.0.1";

	// MQ服务器端口
	private static int PORT = 1449;

	 //Queue Magager队列管理器名
	 private static String SIM_QM = "QMPAY2SIMU";

	 // Recorder Queue录制报文队列
	 private static String SIM_RECORDER_QUEUE = "QR_SIMU_RECDR_2";
	 
	 //Request Queue请求报文队列
	 private static String SIM_REQUEST_QUEUE = "QR_SIMU_MBFE1";

	 //Response Queue响应报文队列
	 private static String SIM_RESPONSE_QUEUE = "QL_MBFE_SIMU_1";
	 
	 //MQ通道方式
	 private static String CHANNEL = "CLIENT.QMPAY2SIMU"; //"CH_SIMU_MBFE";

	// MQ的 CCSID
	private static int S_CCSID = 1381;
	
	private static String ENCODING = null;

	// MQ超时时间
	private static int SIM_MQ_TIMEOUT = 30;

	// 业务处理子进程数
	private static int SIM_DEALER_COUNT = 3;

	// 进程休眠时间
	private static int SIM_IDLE_TIMEOUT = 10;

	// 共享内存ID
	private static int SIM_SHMKEY = 123766;

	// 日志级别 DEBUG(3) INF(2) ERROR(1) FATAL(0)
	private static int SIM_RUNLEVEL = 3;

	// 报文类型(0=纯XML报文 1=类HTTP头报文 2=定长二进制头报文)
	private static int SIM_MESSAGE_TYPE = 0;

	// 系统号
	private static String SIM_SYSID = null;

	// 任务号
	private static String SIM_TASKID = null;

	// 应用系统代码
	private static int SIM_APPID = 1111;

	// 接入系统应用系统代码
	private static int SIM_PKG_APP_ID = 11111111;

	// 适配器类型
	private static String SIM_TYPE = null;

	// 核心地址端口
	private static String TESADDR = null;

	// 监控地址端口
	private static int ADAPTERADDR = -1;

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
	
	private Properties m_config_props = null;
	
	@Override
	public Properties GetAdapterConfigProperties() {
		return m_config_props;
	}

	
	public boolean Init(IAdapterEnvContext tesENV) {
		// 获取配置信息
		m_TESEnv = (IReplyAdapterEnvContext) tesENV;
		m_adpHelper = m_TESEnv.getHelper();

		// 处理 核心返回的注册信息
		Properties props = ConfigHelper.getConfig(m_TESEnv.getEvnContext());
		m_config_props = props;

		// 校验必要的初始化信息 是否存在
		String[] keys = new String[] { "CHANNEL", "HOSTNAME", "PORT", "SIM.QM",
				"SIM.REQUEST.QUEUE", "SIM.RESPONSE.QUEUE", "CCSID" };
		if (!ConfigHelper.chkProperKey(props, keys))
			return false;

		if (props.containsKey("CHANNEL"))
			this.CHANNEL = (String) props.get("CHANNEL");
		if (props.containsKey("HOSTNAME"))
			this.HOSTNAME = (String) props.get("HOSTNAME");
		if (props.containsKey("PORT"))
			this.PORT = Integer.parseInt((String) props.get("PORT"));
		if (props.containsKey("SIM.QM"))
			this.SIM_QM = (String) props.get("SIM.QM");
		if(props.containsKey("SIM.RECORDER.QUEUE"))
			this.SIM_RECORDER_QUEUE = (String) props.get("SIM.RECORDER.QUEUE");
		if (props.containsKey("SIM.REQUEST.QUEUE"))
			this.SIM_REQUEST_QUEUE = (String) props.get("SIM.REQUEST.QUEUE");
		if (props.containsKey("SIM.RESPONSE.QUEUE"))
			this.SIM_RESPONSE_QUEUE = (String) props.get("SIM.RESPONSE.QUEUE");
		if (props.containsKey("CCSID"))
			this.S_CCSID = Integer.parseInt((String) props.get("CCSID"));
		
		if(props.containsKey("RECORDING"))
			this.m_RECORDING = Integer.parseInt((String) props.getProperty("RECORDING"))==1;
		
		if(props.containsKey("SYSTEMID"))
			this.m_SystemId = Integer.parseInt((String) props.get("SYSTEMID"));
		if(props.containsKey("SYSTEMNAME")) {
			m_SystemName = (String) props.get("SYSTEMNAME");		
			try {
				this.m_SystemName = new String(m_SystemName.getBytes("ISO-8859-1"), "utf-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} 
		}
		
		if (props.containsKey("dynamic_in"))
			this.m_needDecrypt = Integer.parseInt((String) props.getProperty("dynamic_in")) == 1;
		if (props.containsKey("dynamic_out"))
			this.m_needEncrypt = Integer.parseInt((String) props.getProperty("dynamic_out")) == 1;
		if (props.containsKey("dynamic_name"))
			this.m_CryptClsName = this.m_secureFactoryPackage + props.getProperty("dynamic_name");

		if (props.containsKey("ENCODING"))
			this.ENCODING = (String) props.get("ENCODING");
		
		if (m_RECORDING && m_config == null) {
			InitDbConnection(this.m_SystemName);
			if (this.ENCODING == null) {
				this.ENCODING = da.GetSystem().getEncoding4ResponseMsg();
			}
		}
		if (this.ENCODING == null) {
			this.ENCODING = "utf-8";
		}
		
		if (m_RECORDING && m_config == null) {
			InitDbConnection(this.m_SystemName);
		}
		
		logger.info("MQ响应端适配器插件" + this.getClass().getName() + "初始化完成.");
		return true;
	}

	public void Start() {
		// TODO Auto-generated method stub
		while (true) {
			try {
				ResponseToMQ(HOSTNAME, PORT, SIM_QM, SIM_RESPONSE_QUEUE, CHANNEL, S_CCSID);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	
	public void ResponseToMQ(String hostName, int port, String qmName, String qName, String channel, int ccsid) {

		MQQueue queue = null;
		try {
			/* 设置MQEnvironment 属性以便客户机连接 */
			MQEnvironment.hostname = hostName;
			MQEnvironment.port = port;
			MQEnvironment.CCSID = ccsid;
			MQEnvironment.channel = channel;
			MQEnvironment.properties.put(MQC.TRANSPORT_PROPERTY, MQC.TRANSPORT_MQSERIES);

			/* 连接到队列管理器 */
			m_qMgr = new MQQueueManager(qmName);
			
			/*
			 * 设置打开选项以便打开用于输出的队列，如果队列管理器停止，我们也 已设置了选项去应对不成功情况
			 */
			m_MQopenOptions = MQC.MQOO_INPUT_SHARED	| MQC.MQOO_FAIL_IF_QUIESCING;

			/* 打开队列 */
			queue = m_qMgr.accessQueue(qName, m_MQopenOptions, null, null, null);
			
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

			// 启动消息循环
			while (true) {
				/* 创建MQMessage 类 */
				MQMessage inMsg = new MQMessage();
				inMsg.characterSet = ccsid;

				/* 从队列到队列缓冲区获取消息 */
 				queue.get(inMsg, gmo); //此语句执行完成之后，队列中将不再有消息

				m_TimeOfAcceptRequest = System.currentTimeMillis();

				/* 从消息读用户数据 */
				byte[] buff = new byte[inMsg.getMessageLength()];
				inMsg.readFully(buff);

				// 安全处理，解密
				byte[] decryptedData = buff;
				if (this.m_needDecrypt) {
					logger.debug("开始解密……");
					IDecryptAdapterSecure decrypter = AbstractFactory.getInstance(this.m_CryptClsName).getDecryptAdapterSecure();
					decryptedData = decrypter.deCrypt(buff);
					if (decryptedData == null) {// 解密失败
						logger.error("解密失败");
						break;
					}
					logger.debug("解密完毕.");
				}

				String msgString = new String(decryptedData, ENCODING);
				logger.info(" The Message from the Queue is : " + msgString);
				System.out.println(" The Message from the Queue is : " + msgString);
				inMsg.replyToQueueName = SIM_REQUEST_QUEUE; //qName;
				inMsg.replyToQueueManagerName = qmName;
				if (m_RECORDING) {
					RecordedCase rc = DbOp.getLastInsertedRecordedCases(m_SystemId);
					if (rc != null) {
						DbOp.UpdateRecordedCase(rc, new String(msgString));
					}
				}
				
				m_InMsg = inMsg;
				m_qMgr.commit();
				
				if (!m_RECORDING) {
					m_adpHelper.sendToCoreWithMultiResponse(decryptedData, this);
				}
				else {
					InsertRecorderMQResponse(buff);
				}
			}
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
			if (m_qMgr != null) {
				try {
					m_qMgr.disconnect();
				} catch (MQException e) {
					e.printStackTrace();
				}
			}
		}
	}

	//在获取到队列报文之后，给远端的peer queue一个回复
	public void Response(byte[] outData) {
		try {
			/* 检查看消息是否属于类型请求消息并对该请求回复 */
			if (m_InMsg.messageFlags == MQC.MQMT_REQUEST) {
				if (null == outData || outData.length == 0) {
					//logger.error("TES处理返回响应端的信息表示模拟器发现异常！");
					return; // 让被测系统连接超时
				}

				if (logger.isDebugEnabled())
					System.out.println("向被测系统发送TES响应消息：" + new String(outData));

				// 安全处理，加密
				byte[] outRawMsg = outData;
				if (this.m_needEncrypt) {
					logger.debug("开始加密……");
					IEncryptAdapterSecure encrypter = AbstractFactory.getInstance(this.m_CryptClsName).getEncryptAdapterSecure();
					outRawMsg = encrypter.enCrypt(outData);
					if (outRawMsg == null) {// 解密失败
						logger.error("加密失败");
						return;
					}
					logger.debug("加密完毕.");
				}

				logger.info("Preparing To Reply To the Request ");
				String replyQueueName = m_InMsg.replyToQueueName;
				if (replyQueueName != null) {
					replyQueueName = replyQueueName.trim();
				}
				String replyToQueueManagerName = m_InMsg.replyToQueueManagerName;
				if (replyToQueueManagerName != null) {
					replyToQueueManagerName = replyToQueueManagerName.trim();
				}
				m_MQopenOptions = MQC.MQOO_OUTPUT | MQC.MQOO_FAIL_IF_QUIESCING;
				MQQueue respQueue;
				respQueue = m_qMgr.accessQueue(replyQueueName, m_MQopenOptions,	replyToQueueManagerName, null, null);
				MQMessage respMessage = new MQMessage();
				respMessage.correlationId = m_InMsg.messageId;
				MQPutMessageOptions pmo = new MQPutMessageOptions();
				respMessage.format = MQC.MQFMT_STRING;
				respMessage.messageFlags = MQC.MQMT_REPLY;

				// 返回响应
				String response = new String(outData);
				respMessage.characterSet = this.S_CCSID;
				respMessage.writeString(response);
				respQueue.put(respMessage, pmo); //空消息不能put，否则会出异常
				logger.info("The response Successfully send: " + response);
				m_qMgr.commit();
				respQueue.close();
			} else
				// 如果不commit直接退出的话，取出的消息会在一段时间后恢复回队列中
				m_qMgr.commit();
		} catch (MQException e) {
			e.printStackTrace();
			logger.error("MQ服务器通信异常：" + e.getLocalizedMessage());
		} catch (IOException e) {
			e.printStackTrace();
			logger.error("MQ服务端适配器异常：" + e.getLocalizedMessage());
		}
	}
	
	
	private void InsertRecorderMQResponse(byte[] data) throws Exception {
		
		try {
			
			/* 设置MQEnvironment 属性以便客户机连接 */
			MQEnvironment.hostname = HOSTNAME;
			MQEnvironment.port = PORT;
			MQEnvironment.CCSID = S_CCSID;
			MQEnvironment.channel = CHANNEL;
			MQEnvironment.properties.put(MQC.TRANSPORT_PROPERTY, MQC.TRANSPORT_MQSERIES);

			/* 连接到队列管理器 */
			MQQueueManager qMgr = new MQQueueManager(SIM_QM);

			/* 设置打开选项以便打开用于输出的队列，如果队列管理器停止，我们也已设置了选项去应对不成功情况 */
			int openOptions = MQC.MQOO_OUTPUT | MQC.MQOO_FAIL_IF_QUIESCING;

			/* 打开打开队列 */
			
			MQQueue queue = qMgr.accessQueue(SIM_RECORDER_QUEUE, openOptions, null, null,	null);

			/* 设置放置消息选项，我们将使用默认设置 */
			MQPutMessageOptions pmo = new MQPutMessageOptions();
			pmo.options = pmo.options + MQC.MQPMO_NEW_MSG_ID;
			pmo.options = pmo.options + MQC.MQPMO_SYNCPOINT;

			/* 创建消息缓冲区 */
			MQMessage outMsg = new MQMessage();

			/* 设置MQMD 格式字段 */
			outMsg.format = MQC.MQFMT_STRING;
			outMsg.messageFlags = MQC.MQMT_REQUEST;
			outMsg.replyToQueueName = SIM_RECORDER_QUEUE;
			outMsg.replyToQueueManagerName = SIM_QM;
			outMsg.characterSet = S_CCSID;
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmmssSSS");
			String strDateTime = sdf.format(new Date());
			outMsg.messageId = strDateTime.getBytes();
			String strCorrId = "CNAPS2_005" + MQRequestAdapterPlugin.GetOneRandomDigitStringOf10Bit();
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

			logger.info(" The message has been Successfully put:" + new String(data, ENCODING));
			System.out.println(" The message has been Successfully put:" + new String(data, ENCODING));

			return;
		
		} catch (Exception ex) {
			System.out.println("从MQ取应答消息失败:" + ex.getMessage());
			throw ex;
		}
	}

	public void Stop() {
		// TODO Auto-generated method stub
		logger.warn("无法停止MQ服务端适配器.");
	}

	public long TimeOfAcceptRequest() {
		// TODO Auto-generated method stub
		return m_TimeOfAcceptRequest;
	}

	public String AdapterType() {
		// TODO Auto-generated method stub
		return "mq.s";
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
	
	
	public static void main(String[] args) {
		
		MQReplyAdapterPlugin mqPlugin = new MQReplyAdapterPlugin();

		try {
			mqPlugin.Start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


}
