package com.dc.tes.msg.unpack.parser;

import java.util.Map;

import com.dc.tes.exception.MsgErr;
import com.dc.tes.exception.TESException;
import com.dc.tes.msg.util.Value;
import com.dc.tes.util.PackUtils;

@ParserTag('F')
class PFloatBigEndian extends Parser {
	@Override
	protected Value parse(byte[] bytes, int start, int length, Map<String, String> params) {
		try {
			switch (length) {
			case 4:
				return new Value(String.valueOf(PackUtils.ReadFloat(bytes, start, true)));
			case 8:
				return new Value(String.valueOf(PackUtils.ReadDouble(bytes, start, true)));
			default:
				throw new TESException(MsgErr.Unpack.UnsupportedFloatLength, String.valueOf(length));
			}
		} catch (Exception ex) {
			throw new TESException(MsgErr.Unpack.UnpackFloatFail, ex);
		}
	}
}
