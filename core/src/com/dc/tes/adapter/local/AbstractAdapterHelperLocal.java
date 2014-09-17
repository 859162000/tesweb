package com.dc.tes.adapter.local;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.dc.tes.adapter.helper.IAdapterHelper;
import com.dc.tes.adapter.util.ConfigHelper;
import com.dc.tes.adapter.util.ReplyInterfaceLocal;
import com.dc.tes.net.jre14.Message;
import com.dc.tes.net.jre14.MessageItem;
import com.dc.tes.net.jre14.MessageType;
import com.dc.tes.net.jre14.ReplyMessage;

/**
 * "本地通道"方式 与核心进行数据交互 抽象公共类
 * 
 * @author 王春佳
 * 
 */
public class AbstractAdapterHelperLocal implements IAdapterHelper {

	private static final Log logger = LogFactory
			.getLog(AbstractAdapterHelperLocal.class);

	/**
	 * 通信层配置文件 名值对,ComLayer.config.xml
	 */
	private Properties m_props = null;

	/**
	 * 通道名称
	 */
	protected String m_channelName = ""; // 通道名称

	/**
	 * 注册请求报文结构
	 */
	private final Message m_msgOfReg = new Message(MessageType.REG);

	/**
	 * 注销请求报文结构
	 */
	private final Message m_msgOfUnReg = new Message(MessageType.UNREG);

	/**
	 * 注册响应报文结构
	 */
	private ReplyMessage m_msgOfRegReply = null;

	/**
	 * 初始化通信层配置属性及通道名称
	 * 
	 * @param props
	 *            : 通信层配置属性
	 */
	public AbstractAdapterHelperLocal(Properties props) {
		this.m_props = props;
		this.m_channelName = this.m_props
				.getProperty(MessageItem.AdapterReg.CHANNELNAME);
	}

	// "本地通道"方式,向核心进行注册
	public byte[] reg2TES() {
		try {
			m_msgOfReg.put(MessageItem.AdapterReg.CHANNELNAME,
					this.m_channelName.getBytes(ConfigHelper.getEncoding()));
			m_msgOfReg.put(MessageItem.AdapterReg.SIMTYPE, this.m_props
					.getProperty(MessageItem.AdapterReg.SIMTYPE).getBytes(
							ConfigHelper.getEncoding()));

			List result = new ReplyInterfaceLocal().sendToCoreLocal(m_msgOfReg.Export());
			if (result.size() != 1) {
				logger.error("本地通道方式,向核心注册返回结果出错,预期结果为1条数据,实际数据为:"
						+ result.size());
				return null;
			}
			m_msgOfRegReply = new ReplyMessage(new ByteArrayInputStream(
					(byte[]) result.get(0)));
			if (m_msgOfRegReply.isOK())
				return m_msgOfRegReply.configInfo();
			else
				return m_msgOfRegReply.errorInfo();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			logger.error("向核心注册失败,不支持的编码方式:" + e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("向核心注册失败,Message类型转换异常:" + e.getMessage());
		}
		return null;
		//return Reg2TESMock();
	}

	/**
	 * 注册 桩程序,用于程序内部测试
	 * 
	 * @return
	 */
//	private byte[] Reg2TESMock() {
//		// 自我测试,直接返回 http服务端适配器 名-值 对信息
//		StringBuffer buf = new StringBuffer();
//		buf.append("jettyPort = 10000\n");
//		buf.append("miniThreadNum = 10\n");
//		buf.append("servletUrl = /tes1/httpadapter1\n");
//		buf.append("servletRootUrl = /web\n");
//		buf.append("dynamic_in = 0\n");
//		buf.append("dynamic_out = 0\n");
//		buf.append("dynamic_name = HttpReplyFactory\n");
//
//		return buf.toString().getBytes();
//	}

	// "本地通道"方式,向核心进行注销
	public void unReg2TES() {
		try {
			m_msgOfUnReg.put(MessageItem.AdapterReg.CHANNELNAME,
					this.m_channelName.getBytes(ConfigHelper.getEncoding()));

			// 不需要返回报文
			new ReplyInterfaceLocal().sendToCoreLocal(m_msgOfUnReg.Export());

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			logger.error("向核心注销失败,不支持的编码方式:" + e.getMessage());
		}
	}

}
