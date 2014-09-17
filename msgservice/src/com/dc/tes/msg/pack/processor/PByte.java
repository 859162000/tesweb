package com.dc.tes.msg.pack.processor;

import java.util.Map;

import org.apache.commons.collections.MapUtils;

import com.dc.tes.exception.MsgErr;
import com.dc.tes.exception.TESException;
import com.dc.tes.msg.util.Value;
import com.dc.tes.util.ByteArrayUtils;
import com.dc.tes.util.HexStringUtils;
import com.dc.tes.util.PackUtils;

/**
 * 将给定的值以字节流格式输出 对于给定的字符串值 将按照HexString来理解
 * 
 * @author lijic
 */
@ProcessorTag('B')
class PByte extends Processor {
	@Override
	protected byte[] process(Value value, Map<String, String> params) {
		// 长度 默认0（不限长度）
		int length = MapUtils.getIntValue(params, "len", 0);
		// 填充字节 默认空格 如果长度为0则忽略此设置
		byte fillingByte = MapUtils.getByteValue(params, "fillingByte", (byte) ' ');
		// 对齐方式 默认左对齐 如果长度为0则忽略此设置
		boolean align = MapUtils.getString(params, "align", "left").equalsIgnoreCase("left");
		// 忽略长度 默认不忽略 如果设置此项为true则强制将length设置为0
		boolean ignoreLen = MapUtils.getBooleanValue(params, "ignoreLen", false);

		if (ignoreLen)
			length = 0;

		// 输出字节流
		if (value.value instanceof String) // 如果value是字符串，转成HEX字符串，编译后按照设置输出
			return PackUtils.WriteBytes(HexStringUtils.FromHexString(value.str), length, fillingByte, align);
		if (value.value instanceof byte[])// 如果value是字节数组则直接输出
			return PackUtils.WriteBytes(value.bytes, length, fillingByte, align);
		if (value.value instanceof Integer) 
			return PackUtils.WriteBytes(ByteArrayUtils.int2Byte(value.i), length, fillingByte, align);		
		throw new TESException(MsgErr.Pack.InvalidValue, "processor: %B expect: {hex string or byte array or integer} value: " + value.str);
	}
}
