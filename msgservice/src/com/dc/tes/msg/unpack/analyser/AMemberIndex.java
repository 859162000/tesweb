package com.dc.tes.msg.unpack.analyser;

import com.dc.tes.dom.MsgContainerUtils;
import com.dc.tes.dom.MsgItem;
import com.dc.tes.msg.unpack.UnpackContext;
import com.dc.tes.msg.util.Value;

@AnalyserTag("mi")
class AMemberIndex extends Analyser {
	@Override
	protected boolean match(Value value, MsgItem item, UnpackContext context) {
		if (value.i == Integer.MIN_VALUE)
			return false;

		item.setAttribute("isarray", true);
		item.setAttribute(MsgContainerUtils.C_ArrayIndex, value);
		return true;
	}
}
