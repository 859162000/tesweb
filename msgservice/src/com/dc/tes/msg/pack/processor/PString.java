package com.dc.tes.msg.pack.processor;

import java.util.Map;

import org.apache.commons.collections.MapUtils;

import com.dc.tes.exception.MsgErr;
import com.dc.tes.exception.TESException;
import com.dc.tes.msg.util.Value;
import com.dc.tes.util.PackUtils;

/**
 * 将给定的值以字符串形式输出 支持encoding、len、fillingChar、fillingByte、align、ignoreLen参数
 * 
 * @author lijic
 * 
 */
@ProcessorTag('s')
class PString extends Processor {
	@Override
	protected byte[] process(Value value, Map<String, String> params) {
		// 编码
		String encoding = MapUtils.getString(params, "encoding");
		// 长度 默认0（不限长度）
		int length = 0;
		if(params.get("len")!=null &&!params.get("len").isEmpty())		
			length = MapUtils.getIntValue(params, "len", 0);
		// 填充字符 默认空格 如果长度为0则忽略此设置
		char fillingChar = MapUtils.getString(params, "fillingChar", " ").charAt(0);
		// 填充字节 默认空格 如果长度为0则忽略此设置
		byte fillingByte = MapUtils.getByteValue(params, "fillingByte", (byte) ' ');
		// 对齐方式 默认左对齐 如果长度为0则忽略此设置
		boolean align = MapUtils.getString(params, "align", "left").equalsIgnoreCase("left");
		// 忽略长度 默认不忽略 如果设置此项为true则强制将length设置为0		
		boolean ignoreLen = MapUtils.getBooleanValue(params, "ignoreLen", false);
		// 整数进制 只在要输出的数据为整数时起作用
		int radix = MapUtils.getIntValue(params, "radix", 10);

		if (ignoreLen)
			length = 0;

		if (fillingChar == '\\') {
			String s = MapUtils.getString(params, "fillingChar", " ");
			if (!s.equals("\\"))
				fillingChar = (char) Integer.parseInt(s.substring(1));
		}

		// 输出
		if (value.value instanceof byte[])// 如果value是字节数组则直接输出
			return PackUtils.WriteBytes(value.bytes, length, fillingByte, align);
		if (value.value instanceof Integer) // 如果value是整数 则转成合适的进制后输出
			return PackUtils.WriteString(Integer.toString(value.i, radix), length, encoding, fillingChar, align);
		if (value.value instanceof Boolean) // 如果value是布尔值，按照设置转为字节数组后输出
			return PackUtils.WriteString(value.str, length, encoding, fillingChar, align);
		if (value.value instanceof String) // 如果value是字符串，按照设置转为字节数组后输出
			return PackUtils.WriteString(value.str, length, encoding, fillingChar, align);

		throw new TESException(MsgErr.Pack.InvalidValue, "processor: %s {fail on unknown value type} value:" + value.toString());
	}
}
