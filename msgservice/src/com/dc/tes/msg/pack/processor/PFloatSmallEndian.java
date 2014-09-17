package com.dc.tes.msg.pack.processor;

import java.util.Map;

import org.apache.commons.collections.MapUtils;

import com.dc.tes.exception.MsgErr;
import com.dc.tes.exception.TESException;
import com.dc.tes.msg.util.Value;
import com.dc.tes.util.PackUtils;

/**
 * 将给定的值以小尾IEEE浮点数格式输出 默认输出长度4字节
 * 
 * @author lijic
 * 
 */
@ProcessorTag('f')
class PFloatSmallEndian extends Processor {
	@Override
	protected byte[] process(Value value, Map<String, String> params) {
		double v;
		try {
			v = Double.parseDouble(value.str);
		} catch (Exception ex) {
			throw new TESException(MsgErr.Pack.InvalidValue,"processor: %f expect: {float} value: "+ value.str);
		}

		// 输出
		int len = MapUtils.getIntValue(params, "len", 4);
		switch (len) {
		case 4:
			return PackUtils.WriteFloat((float) v, false);
		case 8:
			return PackUtils.WriteDouble(v, false);
		default:
			throw new TESException(MsgErr.Pack.InvalidFloatLength, String.valueOf(len));
		}
	}
}