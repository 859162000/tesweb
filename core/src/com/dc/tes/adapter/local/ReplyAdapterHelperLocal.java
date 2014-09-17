package com.dc.tes.adapter.local;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.dc.tes.adapter.IReplyAdapter;
import com.dc.tes.adapter.IReplyAdapterWorker;
import com.dc.tes.adapter.helper.IReplyAdapterHelper;
import com.dc.tes.adapter.util.PlogData;
import com.dc.tes.adapter.util.ReplyInterfaceLocal;
import com.dc.tes.net.jre14.Message;
import com.dc.tes.net.jre14.MessageItem;
import com.dc.tes.net.jre14.MessageType;
import com.dc.tes.net.jre14.ReplyMessage;

/**
 * "本地通道"方式 与核心进行数据交互,服务端实现类
 * 
 * @author 王春佳
 * 
 */
public class ReplyAdapterHelperLocal extends AbstractAdapterHelperLocal
		implements IReplyAdapterHelper {

	/**
	 * 数据请求报文结构
	 */
//	private final Message m_msgOfData = new Message(MessageType.MESSAGE);

	/**
	 * 数据响应报文结构
	 */
//	private ReplyMessage m_msgOfDataReply = null;

	/**
	 * 服务端适配器 接口,通过该接口获取"适配器接收到被测系统请求的时间点"
	 */
	private IReplyAdapter m_iReplyAdapter = null;

	/**
	 * 日志对象
	 */
	private static final Log logger = LogFactory
			.getLog(ReplyAdapterHelperLocal.class);
	
	/**
	 * 性能监控字段PLOG
	 */
	private static PlogData pLogDataReq = null;
	
	static {
		if (pLogDataReq == null)
			pLogDataReq = new PlogData();
	}

	/**
	 * 初始化通信层配置属性
	 * 
	 * @param props
	 *            : 通信层配置属性
	 */
	public ReplyAdapterHelperLocal(Properties props, IReplyAdapter iReplyAdapter) {
		super(props);
		this.m_iReplyAdapter = iReplyAdapter;
	}

	// "本地通道"方式,向核心发送数据请求报文,响应报文唯一
	public byte[] sendToCore(byte[] realMsg) {

		Message l_msgOfData = new Message(MessageType.MESSAGE);
		try {
			// 添加 通道
			l_msgOfData.put(MessageItem.AdapterReg.CHANNELNAME,
					super.m_channelName);

			// 添加 请求报文体
			l_msgOfData.put(MessageItem.AdapterMessage.REQMESSAGE, realMsg);

			// 添加 PLOG字段
			//PlogData pLogDataReq = new PlogData(); // 请求PLOG
			String logDataReq = "";
			synchronized (pLogDataReq) {
				// 获取PLOG字段
				logDataReq = pLogDataReq.getPlog();
			}
			l_msgOfData.put(MessageItem.AdapterMessage.PLOG, logDataReq);

			// 通过"本地通道"方式,调用核心函数,向核心发送请求数据
			List result = new ReplyInterfaceLocal().sendToCoreLocal(new String(l_msgOfData.Export()).getBytes());
			if (result.size() != 1) {
				logger.error("本地通道方式,向核心发送请求报文返回结果出错,预期结果为1条数据,实际数据为:"
						+ result.size());
				return null;
			}

			// 获取核心响应报文
			ReplyMessage l_msgOfDataReply = new ReplyMessage(new ByteArrayInputStream(
					(byte[]) result.get(0)));

			// 处理核心反馈的部分PLOG字段,响应时间、处理时间
			String logDataRes = l_msgOfDataReply.pLogMsg();
			long delayTime = l_msgOfDataReply.delayMilliSeconds(); // 核心返回的延迟时间
			long timeOfAcceptRequest = m_iReplyAdapter.TimeOfAcceptRequest();// 适配器接收到被测系统请求的时间点
			long currentTime = System.currentTimeMillis(); // 当前时间

			// 处理时间
			// 误差:忽略了适配处理的时间,以及该代码行下面所有的代码时间,待改进
			long executeTime = currentTime - timeOfAcceptRequest;

			// 进行延迟(理论上适配器做延迟,误差最小)
			if (executeTime < delayTime) {// 进行延迟
				logger.debug("延迟时间为:" + (delayTime - executeTime));
				Thread.currentThread();
				Thread.sleep(delayTime - executeTime);
			}

			long currentTimeDelay = System.currentTimeMillis(); // 当前时间
			long responseTime = currentTimeDelay - timeOfAcceptRequest; // 响应时间

			// 添加PLOG字段内容，为了最小误差，此处应改另启动线程处理，待该进
			PlogData pLogDataRes = new PlogData(); // 响应PLOG
			synchronized (pLogDataRes) {
				pLogDataRes.setM_plogDataFromCore(logDataRes);
				pLogDataRes.setM_executeTime(executeTime);
				pLogDataRes.setM_responseTime(responseTime);
			}

			// 将响应报文反馈给特定的适配器
			if (l_msgOfDataReply.isOK())
				return l_msgOfDataReply.responseMsg();
			else {
				logger.error("本地通道方式,返回响应报文出错:"	+ new String(l_msgOfDataReply.errorInfo()));
				return null;
			}

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			logger.error("向核心发送请求报文,不支持的编码方式:" + e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("向核心发送请求报文,Message类型转换异常:" + e.getMessage());
		}

		return null;
	}

	// "本地通道"方式,向核心发送数据请求报文,响应报文多条
	public byte[] sendToCoreWithMultiResponse(byte[] realMsg,
			IReplyAdapterWorker adpWorker) {
		Message l_msgOfData = new Message(MessageType.MESSAGE);
		// 添加 通道
		try {
			l_msgOfData.put(MessageItem.AdapterReg.CHANNELNAME,
					super.m_channelName);

			// 添加 请求报文体
			l_msgOfData.put(MessageItem.AdapterMessage.REQMESSAGE, realMsg);

			// 添加 PLOG字段
			PlogData pLogDataReq = new PlogData(); // 请求PLOG
			String logDataReq = "";
			synchronized (pLogDataReq) {
				// 获取PLOG字段
				logDataReq = pLogDataReq.getPlog();
			}
			l_msgOfData.put(MessageItem.AdapterMessage.PLOG, logDataReq);

			// 通过"本地通道"方式,调用核心函数,向核心发送请求数据
			List result = new ReplyInterfaceLocal().sendToCoreLocal(l_msgOfData.Export());
			if (result.size() < 1) {
				logger.error("本地通道方式,向核心发送请求报文返回结果出错,预期结果为大于或等于1条数据,实际数据为:"
						+ result.size());
				adpWorker.Response(null); // 告诉适配器出现错误
				return null;
			}

			long timeOfAcceptRequest = adpWorker.TimeOfAcceptRequest();// 适配器接收到被测系统请求的时间点

			// 处理核心反馈的部分PLOG字段,响应时间、处理时间
			String logDataRes = "";

			// 循环读取每一个响应数据
			long timeOfSleep = 0; // 多次睡眠时间的总和
			for (Iterator i = result.iterator(); i.hasNext();) {
				ReplyMessage l_msgOfDataReply = new ReplyMessage(new ByteArrayInputStream(
						(byte[]) i.next()));
				
				if(!l_msgOfDataReply.isOK()){
					adpWorker.Response(null);
					logger.error("本地通道方式,多笔返回响应报文出错:"
							+ new String(l_msgOfDataReply.errorInfo()));
					break; // 出现错误后,不继续执行
				}
				else{
					logDataRes = l_msgOfDataReply.pLogMsg();
					// 处理延迟
					long currentTime = System.currentTimeMillis(); // 当前时间
					long delayTime = l_msgOfDataReply.delayMilliSeconds(); // 核心返回的延迟时间
					long executeTime = currentTime - timeOfAcceptRequest; // 本笔响应的执行时间
					if (executeTime < delayTime) {// 进行延迟
						logger.debug("延迟时间为:" + (delayTime - executeTime));
						Thread.currentThread();
						Thread.sleep(delayTime - executeTime);
						timeOfSleep += delayTime - executeTime;
					}
					adpWorker.Response(l_msgOfDataReply.responseMsg());
				}
			}

			// 添加PLOG字段
			long currentTimeDelay = System.currentTimeMillis(); // 当前时间
			long responseTime = currentTimeDelay - timeOfAcceptRequest; // 响应时间
			long executeTime = responseTime - timeOfSleep;	//执行时间
			
			PlogData pLogDataRes = new PlogData(); // 响应PLOG
			synchronized (pLogDataRes) {
				pLogDataRes.setM_plogDataFromCore(logDataRes);
				pLogDataRes.setM_executeTime(executeTime);
				pLogDataRes.setM_responseTime(responseTime);
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			logger.error("向核心发送请求报文,不支持的编码方式:" + e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("向核心发送请求报文,Message类型转换异常:" + e.getMessage());
		}

		return null;
	}

}
