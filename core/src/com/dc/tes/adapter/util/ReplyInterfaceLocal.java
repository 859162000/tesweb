package com.dc.tes.adapter.util;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.dc.tes.channel.localchannel.AbstractLocalProcess;
import com.dc.tes.channel.localchannel.LocalProcessFactory;
import com.dc.tes.net.Message;
import com.dc.tes.net.ReplyMessage;

/**
 * 
 * 服务端适配器"本地通道"方式与核心接口
 * 
 * @author 王春佳
 *
 * @see 该代码由原有通信层拷出,待完善、兼容
 * 
 */
public class ReplyInterfaceLocal {
	private static int g_NO = 0;
	
	private static final Log logger = LogFactory.getLog(ReplyInterfaceLocal.class);
	
	/**
	 * 向核心本地通道发送注册、请求等信息
	 * 
	 * @param requestByte
	 *            : 注册、请求等原始报文
	 * @return List<byte[]> 元素为1个或多个原始响应报文
	 * @author 王春佳
	 */
	public List sendToCoreLocal(byte[] requestByte) {
		logger.debug("本地通道方式,发送到核心的原始数据为:" + new String(requestByte));
		logger.debug("本地通道方式,发送到核心的结构化数据为:"
					+ new Message(new ByteArrayInputStream(requestByte)));
		List resultLst = new ArrayList();

		// 调用核心提供的接口,处理注册、请求、注销等报文
		AbstractLocalProcess lp = LocalProcessFactory.CreateProcess();
//		byte[] bytes = new byte[requestByte.length];
//		System.arraycopy(requestByte, 0, bytes, 0, requestByte.length);
		
		ReplyMessage[] replyMessageArray = lp.process(new Message(
				new ByteArrayInputStream(requestByte)));
		for (int i = 0; i < replyMessageArray.length; i++)
			resultLst.add(replyMessageArray[i].Export());

		if (logger.isDebugEnabled())
			for (int j = 0; j < replyMessageArray.length; j++) {
				logger.debug("本地通道方式,从核心返回的数据顺序号为:" + "[" + j + "]" + "原始数据为:"
						+ new String(replyMessageArray[j].Export()));
				logger.debug("本地通道方式,从核心返回的数据顺序号为:" + "[" + j + "]" + "结构化数据为:"
						+ replyMessageArray[j]);
			}
		return resultLst;
	}
}
