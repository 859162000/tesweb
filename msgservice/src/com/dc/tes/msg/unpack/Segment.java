package com.dc.tes.msg.unpack;

import com.dc.tes.dom.MsgItem;
import com.dc.tes.msg.IContext;
import com.dc.tes.util.type.Wrapper;

/**
 * 拆包格式字符串中的段
 * 
 * @author lijic
 * 
 */
abstract class Segment {
	/**
	 * 用当前段对报文字节流进行匹配
	 * 
	 * @param bytes
	 *            报文字节流
	 * @param item
	 *            正被拆包的元素
	 * @param context
	 *            拆包上下文
	 * @return 返回匹配的字节数 如果匹配失败则返回-1
	 */
	abstract int Match(byte[] bytes, MsgItem item, Wrapper<MsgItem> template, int start, UnpackSpecification spec, IContext context);
}
