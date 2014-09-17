package com.dc.tes.channel.remote;

import com.dc.tes.channel.IChannel;
import com.dc.tes.net.Message;

/**
 * 远程通道 远程通道是指与核心分离部署的通道 该接口被ChannelServer调用
 * 
 * @author lijic
 * 
 */
public interface IRemoteChannel extends IChannel {
	/**
	 * 远程响应接口 该接口供远程通道使用，用于将响应报文送回给通道另一端
	 * 
	 * @author lijic
	 * 
	 */
	public interface IRemoteReplyer {
		/**
		 * 将响应报文送给通道远端 通信过程中产生的异常会扔给调用者
		 * 
		 * @param req
		 *            响应
		 * @throws Exception
		 */
		public void ReplyWithEx(Message req) throws Exception;

		/**
		 * 将响应报文送给通道远端 通信过程中产生的异常会被catch掉并打到日志中
		 * 
		 * @param req
		 *            响应
		 */
		public void Reply(Message req);
	}

	/**
	 * 处理通道远端送来的请求
	 * 
	 * @param msg
	 *            远端传来的消息
	 * @param replyer
	 *            远程响应接口
	 */
	public void Process(Message msg, IRemoteReplyer replyer);
}
