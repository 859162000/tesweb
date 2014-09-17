package com.dc.tes.msg.pack.processor;

import java.util.Map;

import org.apache.commons.collections.MapUtils;

import com.dc.tes.exception.MsgErr;
import com.dc.tes.exception.TESException;
import com.dc.tes.msg.util.Value;
import com.dc.tes.util.PackUtils;

/**
 * 将给定的值以小尾整数格式输出 默认输出长度为4字节
 * 
 * @author lijic
 * 
 */
@ProcessorTag('d')
class PIntSmallEndian extends Processor {
	@Override
	protected byte[] process(Value value, Map<String, String> params) {
		int len = MapUtils.getIntValue(params, "len", 4);

		switch (len) {
		case 1:
			return PackUtils.WriteInt8(value.i);
		case 2:
			return PackUtils.WriteInt16(value.i, false);
		case 3:
			return PackUtils.WriteInt24(value.i, false);
		case 4:
			return PackUtils.WriteInt32(value.i, false);
		case 8:
			return PackUtils.WriteInt64(value.i, false);
		default:
			throw new TESException(MsgErr.Pack.InvalidIntLength, String.valueOf(len));
		}
	}
}
