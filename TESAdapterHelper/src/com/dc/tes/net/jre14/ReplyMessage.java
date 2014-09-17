package com.dc.tes.net.jre14;

import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 初始化 响应消息
 * 
 * 适用于jdk1.4.2
 * 
 * @author 王春佳
 * 
 */
public class ReplyMessage extends Message {

	private static final Log logger = LogFactory.getLog(ReplyMessage.class);

	/**
	 * 使用一个输入流初始化消息体
	 * 
	 * @param s
	 *            : 输入流
	 * @throws Exception
	 */
	public ReplyMessage(InputStream s) throws Exception {
		super(s);
	}

	/**
	 * 初始化一个响应消息,自动添加正确的"处理结果"、空的"错误消息"2个字段
	 * 
	 * @param msg
	 *            响应消息
	 * @see 使用该构造函数初始化,响应消息有3个初始化字段:报文头(REG\MESSAGE等)、RESULT、ERRMSG
	 */
	// public ReplyMessage(Message msg) {
	// super(msg.getType());
	//
	// super.put("RESULT", 0);
	// super.put("ERRMSG", "".getBytes());
	// }
	/**
	 * 初始化一个响应消息,自动添加正确的"处理结果"、空的"错误消息"2个字段
	 * 
	 * @param message
	 *            : 报文类型
	 * 
	 * @see 使用该构造函数初始化,响应消息有3个初始化字段:报文头(REG\MESSAGE等)、RESULT、ERRMSG
	 */
	public ReplyMessage(MessageType message) {
		super(message);

		super.put("RESULT", 0);
		super.put("ERRMSG", "".getBytes());
	}

	/**
	 * 针对返回消息判断处理结果是否成功
	 * 
	 * @return =true表示处理成功，否则失败
	 */
	public boolean isOK() {
		try {
			return null == m_items.get("RESULT") ? false : 0 == Integer
					.parseInt(new String((byte[]) m_items.get("RESULT")));

		} catch (NumberFormatException ex) {
			return false;
		}
	}

	/**
	 * 报文序号
	 * 
	 * @return 报文序号
	 */
	public int packSeqNo() {
		try {
			return null == m_items.get("PACKSEQ") ? 0 : Integer
					.parseInt(new String((byte[]) m_items.get("PACKSEQ")));
		} catch (NumberFormatException ex) {
			return 0;
		}
	}

	/**
	 * 
	 * @return true=多笔应答，false=单笔
	 */
	public boolean hasMorePack() {
		return packSeqNo() > 0;
	}

	/**
	 * 错误信息
	 * 
	 * @return 当isOK=false时返回错误信息，否则返回空串""
	 */
	public byte[] errorInfo() {
		return (byte[]) m_items.get("ERRMSG");
	}

	/**
	 * 适配器配置信息
	 * 
	 * @return 当前适配器在TES核心端的配置信息
	 */
	public byte[] configInfo() {
		return (byte[]) m_items.get("CONFIGINFO");
	}

	/**
	 * 延迟时间
	 * 
	 * @return 报文中包含的延迟时间（毫秒）
	 */
	public int delayMilliSeconds() {
		int i = 0;
		try {
			i = Integer.parseInt(new String((byte[]) m_items.get("DELAYTIME")));
		} catch (Exception ex) {
			i = 0;
			logger.warn("延迟参数值无效.");
		}
		return i;
	}

	/**
	 * 响应报文
	 * 
	 * @return 报文中的给被测系统的应答报文明文
	 */
	public byte[] responseMsg() {
		return (byte[]) m_items.get("RESMESSAGE");
	}

	/**
	 * 性能监控字段
	 * @return 性能监控字段
	 */
	public String pLogMsg() {
		return new String((byte[]) m_items.get(MessageItem.AdapterMessage.PLOG));
	}

	/**
	 * 设置异常信息
	 * 
	 * @param ex
	 *            捕获的异常
	 * @param msg
	 *            附加的信息
	 */
	public void setEx(Throwable ex, String msg) {
		super.put("RESULT", -1);

		StringWriter w = new StringWriter();
		ex.printStackTrace(new PrintWriter(w));
		super.put("ERRMSG", (msg + "[" + w.toString() + "]").getBytes());
	}
}
