package com.dc.tes.msg.pack;

import com.dc.tes.dom.MsgItem;
import com.dc.tes.msg.IContext;

/**
 * 组包格式字符串中的段
 * 
 * @author lijic
 * 
 */
abstract class Segment {
	/**
	 * 将该段进行组包
	 * 
	 * @param item
	 *            被组包的报文元素
	 * @param spec
	 *            组包样式定义
	 * @param context
	 *            上下文
	 * @return 对该段进行组包得到的字节流
	 */
	abstract byte[] Pack(MsgItem item, PackSpecification spec, IContext context);
}
