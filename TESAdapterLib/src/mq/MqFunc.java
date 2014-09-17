package mq;

import java.util.HashMap;
import java.util.LinkedList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ibm.mq.MQC;
import com.ibm.mq.MQEnvironment;
import com.ibm.mq.MQGetMessageOptions;
import com.ibm.mq.MQMessage;
import com.ibm.mq.MQPutMessageOptions;
import com.ibm.mq.MQQueue;
import com.ibm.mq.MQQueueManager;


/**
 * MQ工具类
 * 
 * @author NuclearG
 * 
 */

class MsgHead {

	private static final String S_MSGID_TOKEN_START = "<MsgID>";
	private static final String S_MSGID_TOKEN_END = "</MsgID>";
	private static final String S_MSGNO_TOKEN_START = "<MsgNo>";
	private static final String S_MSGNO_TOKEN_END = "</MsgNo>";
	
	private String m_org_data = null;
	private String m_msgNo;
	private String m_msgID;
	
	public MsgHead(String data){
		m_org_data = data;

		int bgOfMsgNo = data.indexOf(S_MSGNO_TOKEN_START);
		int edOfMsgNo = data.indexOf(S_MSGNO_TOKEN_END);
		m_msgNo = data.substring(bgOfMsgNo, edOfMsgNo);
		
		int bgOfMsgID = data.indexOf(S_MSGID_TOKEN_START);
		int edOfMsgID = data.indexOf(S_MSGID_TOKEN_END);
		m_msgID = data.substring(bgOfMsgID, edOfMsgID);		
		
	}
	
	public String GetMsgID(){
		return m_msgID;
	}
	
	public String GetMsgNo(){
		return m_msgNo;
	}
}

public class MqFunc {
	private static final Log log = LogFactory.getLog(MqFunc.class);
	
	
	
	public static final int S_CCSID = 1381;
	public static HashMap S_MSG2T_MAP = new HashMap();//调度用(消息ID, 当前线程)<String, Thread>
	public static HashMap S_T2MSGLIST_MAP = new HashMap();//查询用(消息ID, 当前线程)<Thread, Queue<String>>
	
	public static void Put(String hostName, int port, String qmName,
			String qName, String channel, String data) throws Exception {
		try {
			/* 设置MQEnvironment 属性以便客户机连接 */
			MQEnvironment.hostname = hostName;
			MQEnvironment.port = port;
			MQEnvironment.channel = channel;
			MQEnvironment.CCSID = S_CCSID;
			MQEnvironment.properties.put(MQC.TRANSPORT_PROPERTY,
					MQC.TRANSPORT_MQSERIES);
			/* 连接到队列管理器 */
			MQQueueManager qMgr = new MQQueueManager(qmName);

			/* 设置打开选项以便打开用于输出的队列，如果队列管理器正在停止，我们也已设置了选项去应对不成功情况。 */
			int openOptions = MQC.MQOO_OUTPUT | MQC.MQOO_FAIL_IF_QUIESCING;

			/* 打开队列 */
			MQQueue queue = qMgr.accessQueue(qName, openOptions, null, null,
					null);

			/* 设置放置消息选项我们将使用默认设置 */
			MQPutMessageOptions pmo = new MQPutMessageOptions();

			/* 创建消息，MQMessage 类包含实际消息数据的数据缓冲区，和描述消息的所有MQMD 参数 */

			/* 创建消息缓冲区 */
			MQMessage outMsg = new MQMessage();

			/* 设置MQMD 格式字段 */
			outMsg.format = MQC.MQFMT_STRING;
			
			MsgHead mhObject = new MsgHead(data);			
			outMsg.correlationId = mhObject.GetMsgID().getBytes();
				

			/* 准备用户数据消息 */
			outMsg.writeString(data);

			/* 在队列上放置消息 */
			queue.put(outMsg, pmo);
			Thread currThread = Thread.currentThread();
			synchronized(S_MSG2T_MAP){
				S_MSG2T_MAP.put(mhObject.GetMsgID(), currThread);
			}
			synchronized(S_T2MSGLIST_MAP){
				if(!S_T2MSGLIST_MAP.containsKey(currThread)){
					S_T2MSGLIST_MAP.put(currThread, new LinkedList());//LinkedList<String>
				}
				
				((LinkedList)S_T2MSGLIST_MAP.get(currThread)).add(mhObject.GetMsgID());
			}
			
			log.info(" The sending message id:" + outMsg.correlationId + "！\n");

			/* 提交事务处理 */
			qMgr.commit();

			log.info(" The message has been Successfully put！\n");

			/* 关闭队列和队列管理器对象 */
			queue.close();
			qMgr.disconnect();
		} catch (Exception ex) {
			throw ex;
		}
	}

	public static String Get(String hostName, int port, String qmName,
			String qName, String channel) throws Exception {
		try {
			/* 设置 MQEnvironment 属性以便客户机连接 */
			MQEnvironment.hostname = hostName;
			MQEnvironment.port = port;
			MQEnvironment.CCSID = S_CCSID;
			MQEnvironment.channel = channel;

			MQEnvironment.properties.put(MQC.TRANSPORT_PROPERTY,
					MQC.TRANSPORT_MQSERIES);

			/* 连接到队列管理器 */
			MQQueueManager qMgr = new MQQueueManager(qmName);

			/*
			 * 设置打开选项以便打开用于输出的队列，如果队列管理器停止，我们也 已设置了选项去应对不成功情况
			 */
			int openOptions = MQC.MQOO_INPUT_SHARED
					| MQC.MQOO_FAIL_IF_QUIESCING;

			/* 打开队列 */
			MQQueue queue = qMgr.accessQueue(qName, openOptions, null, null,
					null);

			/* 设置放置消息选项 */
			MQGetMessageOptions gmo = new MQGetMessageOptions();

			/* 在同步点控制下获取消息 */
			gmo.options = gmo.options + MQC.MQGMO_SYNCPOINT;

			/* 如果在队列上没有消息则等待 */
			gmo.options = gmo.options + MQC.MQGMO_WAIT;

			/* 如果队列管理器停顿则失败 */
			gmo.options = gmo.options + MQC.MQGMO_FAIL_IF_QUIESCING;

			/* 设置等待的时间限制 */
			gmo.waitInterval = 3000;

			/* 创建MQMessage 类 */
			MQMessage inMsg = new MQMessage();

			Thread currThread = Thread.currentThread();
			synchronized(S_T2MSGLIST_MAP){
				if(S_T2MSGLIST_MAP.containsKey(currThread)){
					//取队列的第一条消息
					inMsg.correlationId = ((String)((LinkedList)S_T2MSGLIST_MAP.get(currThread)).remove(0)).getBytes();
					
					if(((LinkedList)S_T2MSGLIST_MAP.get(currThread)).isEmpty()){
						S_T2MSGLIST_MAP.remove(currThread);
					}
				}					
			}
			synchronized(S_MSG2T_MAP){
				S_MSG2T_MAP.remove(inMsg.correlationId);
			}
			

			/* 从队列到消息缓冲区获取消息 */			
			queue.get(inMsg, gmo);
			
			/* 从消息读取用户数据 */
			String msgString = inMsg.readString(inMsg.getMessageLength());
			log.info(" The Message from the Queue is : " + msgString);

			/* 提交事务 */
			qMgr.commit();

			/* 关闭队列和队列管理器对象 */
			queue.close();
			qMgr.disconnect();

			return msgString;
		} catch (Exception ex) {
			throw ex;
		}
	}

	public static void LoopGet(String hostName, int port, String qmName,
			String qName, String channel, IDataProcessor processor)
			throws Exception {
		try {
			/* 设置 MQEnvironment 属性以便客户机连接 */
			MQEnvironment.hostname = hostName;
			MQEnvironment.port = port;
			MQEnvironment.CCSID = S_CCSID;
			MQEnvironment.channel = channel;

			MQEnvironment.properties.put(MQC.TRANSPORT_PROPERTY,
					MQC.TRANSPORT_MQSERIES);

			/* 连接到队列管理器 */
			MQQueueManager qMgr = new MQQueueManager(qmName);

			/*
			 * 设置打开选项以便打开用于输出的队列，如果队列管理器停止，我们也 已设置了选项去应对不成功情况
			 */
			int openOptions = MQC.MQOO_INPUT_SHARED
					| MQC.MQOO_FAIL_IF_QUIESCING;

			/* 打开队列 */
			MQQueue queue = qMgr.accessQueue(qName, openOptions, null, null,
					null);

			/* 设置放置消息选项 */
			MQGetMessageOptions gmo = new MQGetMessageOptions();

			/* 在同步点控制下获取消息 */
			gmo.options = gmo.options + MQC.MQGMO_SYNCPOINT;

			/* 如果在队列上没有消息则等待 */
			gmo.options = gmo.options + MQC.MQGMO_WAIT;

			/* 如果队列管理器停顿则失败 */
			gmo.options = gmo.options + MQC.MQGMO_FAIL_IF_QUIESCING;

			/* 设置等待的时间限制 */
			gmo.waitInterval = 3000;

			while (true) {
				/* 创建MQMessage 类 */
				MQMessage inMsg = new MQMessage();
				Thread currThread = Thread.currentThread();
				synchronized(S_T2MSGLIST_MAP){
					if(S_T2MSGLIST_MAP.containsKey(currThread)){
						//取队列的第一条匹配消息
						inMsg.correlationId = ((String)((LinkedList)S_T2MSGLIST_MAP.get(currThread)).remove(0)).getBytes();
						
						if(((LinkedList)S_T2MSGLIST_MAP.get(currThread)).isEmpty()){
							S_T2MSGLIST_MAP.remove(currThread);
						}
					}					
				}
				synchronized(S_MSG2T_MAP){
					S_MSG2T_MAP.remove(inMsg.correlationId);
				}

				/* 从队列到消息缓冲区获取消息 */
				queue.get(inMsg, gmo);

				/* 从消息读取用户数据 */
				String msgString = inMsg.readString(inMsg.getMessageLength());
				log.info(" The Message from the Queue is : " + msgString);

				processor.ProcessData(msgString);

				/* 提交事务 */
				qMgr.commit();
			}

			// 除非强行退出，否则消息循环不会结束。这两句话永远不可能被执行
			// queue.close();
			// qMgr.disconnect();
		} catch (Exception ex) {
			throw ex;
		}
	}

	


	public interface IDataProcessor {
		public String ProcessData(String data);
	}
}
