package com.dc.tes.msg.unpack;

import com.dc.tes.dom.MsgItem;
import com.dc.tes.msg.IContext;
import com.dc.tes.msg.util.FormatStringParser.FormatFragment;
import com.dc.tes.util.RuntimeUtils;
import com.dc.tes.util.type.Wrapper;

/**
 * 文本段
 * 
 * @author lijic
 * 
 */
class TextSegment extends Segment {
	final byte[] bytes;

	/**
	 * 初始化一个文本段
	 * 
	 * @param fragment
	 *            格式字符串中的文本片段
	 */
	TextSegment(FormatFragment fragment) {
		this.bytes = fragment.bytes;
	}

	@Override
	int Match(byte[] bytes, MsgItem item, Wrapper<MsgItem> template, int start, UnpackSpecification spec, IContext context) {
		// 如果剩余的字节还没有当前文本段的长度长 则认为匹配失败
		if (bytes.length - start < this.bytes.length)
			return -1;

		// 将字节按位进行比对 如果不一致则认为匹配失败
		for (int i = 0; i < this.bytes.length; i++)
			if (bytes[start + i] != this.bytes[i])
				return -1;

		// 成功匹配
		return this.bytes.length;
	}

	@Override
	public String toString() {
		
		return new String(this.bytes, RuntimeUtils.utf8);
	}
}
