package com.dc.tes.msg.unpack.parser;

import java.util.Map;

import com.dc.tes.exception.MsgErr;
import com.dc.tes.exception.TESException;
import com.dc.tes.msg.util.Value;
import com.dc.tes.util.PackUtils;

@ParserTag('D')
class PIntBigEndian extends Parser {
	@Override
	protected Value parse(byte[] bytes, int start, int length, Map<String, String> params) {
		try {
			switch (length) {
			case 1:
				return new Value(PackUtils.ReadInt8(bytes, start));
			case 2:
				return new Value(PackUtils.ReadInt16(bytes, start, true));
			case 3:
				return new Value(PackUtils.ReadInt24(bytes, start, true));
			case 4:
				return new Value(PackUtils.ReadInt32(bytes, start, true));
			case 8:
				return new Value((int) PackUtils.ReadInt64(bytes, start, true));
			default:
				throw new TESException(MsgErr.Unpack.UnsupportedIntLength, String.valueOf(length));
			}
		} catch (Exception ex) {
			throw new TESException(MsgErr.Unpack.UnpackIntFail, ex);
		}
	}
}
