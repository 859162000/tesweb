package com.dc.tes.net;

import com.dc.tes.exception.ErrCode;
import com.dc.tes.exception.TESException;

/**
 * 回复消息 该类用于简化回复消息的创建工作 具体格式参照陈勇写的文档
 * 
 * @author huangzx
 * 
 */
public class ReplyMessage extends Message {
	/**
	 * 初始化一个回复消息
	 * 
	 * @param msg
	 *            请求消息 将从这个请求消息中获取消息的类型信息
	 */
	public ReplyMessage(Message msg) {
		super(msg.getType());

		super.put(MessageItem.RESULT, 0);
		super.put(MessageItem.ERRMSG, "");
	}

	/**
	 * 初始化一个回复消息
	 * 
	 * @param msg
	 *            请求消息 将从这个请求消息中获取消息的类型信息
	 */
	public ReplyMessage(MessageType msgtype) {
		super(msgtype);

		super.put(MessageItem.RESULT, 0);
		super.put(MessageItem.ERRMSG, "");
	}

	/**
	 * 设置异常信息
	 * 
	 * @param ex
	 *            异常信息
	 */
	public void setEx(Throwable ex) {
		if (!(ex instanceof TESException))
			this.setEx(new TESException(ErrCode.UNKNOWN, ex));

		TESException e = (TESException) ex;
		super.put(MessageItem.RESULT, e.errCode);
		super.put(MessageItem.ERRMSG, e.getMessage());
	}
}
