package com.dc.tes.msg.pack;

import com.dc.tes.dom.MsgItem;
import com.dc.tes.msg.IContext;
import com.dc.tes.msg.util.FormatStringParser.FormatFragment;
import com.dc.tes.util.RuntimeUtils;

/**
 * 文本段
 * 
 * @author lijic
 * 
 */
class TextSegment extends Segment {
	private final byte[] bytes;

	/**
	 * 初始化一个文本段
	 * 
	 * @param fragment
	 *            格式字符串中的片段
	 */
	TextSegment(FormatFragment fragment) {
		this.bytes = fragment.bytes;
	}

	@Override
	byte[] Pack(MsgItem item, PackSpecification spec, IContext context) {
		return this.bytes;
	}

	@Override
	public String toString() {
		return new String(this.bytes, RuntimeUtils.utf8);
	}
}
