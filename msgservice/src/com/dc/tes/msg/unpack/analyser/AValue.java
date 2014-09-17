package com.dc.tes.msg.unpack.analyser;

import com.dc.tes.dom.MsgField;
import com.dc.tes.dom.MsgItem;
import com.dc.tes.msg.unpack.UnpackContext;
import com.dc.tes.msg.util.Value;

@AnalyserTag("v")
class AValue extends Analyser {
	@Override
	protected boolean match(Value value, MsgItem item, UnpackContext context) {
		if (item instanceof MsgField) {
			((MsgField) item).set(value.str);
			return true;
		} else
			return false;
	}
}
