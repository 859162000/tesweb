package com.dc.tes.msg.pack.processor;

import java.util.Map;

import org.apache.commons.collections.MapUtils;

import com.dc.tes.msg.util.Value;
import com.dc.tes.util.PackUtils;

/**
 * 将给定的值以压缩BCD码格式输出
 * 
 * @author lijic
 */
@ProcessorTag('b')
class PBcd extends Processor {
	@Override
	protected byte[] process(Value value, Map<String, String> params) {
		// 长度
		int length = MapUtils.getIntValue(params, "len", 0);
		length = (length + 1) / 2;
		// 对齐方式	
		boolean align = MapUtils.getString(params, "align", "right").equalsIgnoreCase("left");
		// 忽略长度
		boolean ignoreLen = MapUtils.getString(params, "ignoreLen", "false").equalsIgnoreCase("true");

		if (ignoreLen)
			length = 0;

		return PackUtils.WriteBCD(value.str, length, align);
	}
}
