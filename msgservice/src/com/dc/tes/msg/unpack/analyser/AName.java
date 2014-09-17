package com.dc.tes.msg.unpack.analyser;

import com.dc.tes.dom.MsgItem;
import com.dc.tes.msg.unpack.UnpackContext;
import com.dc.tes.msg.util.Value;

@AnalyserTag("n")
class AName extends Analyser {
	@Override
	protected boolean match(Value value, MsgItem item, UnpackContext context) {
		if (value == null)
			return false;

		Value n = item.getAttribute("name");
		if (n != Value.empty && !n.str.equals(value.str))
			return false;

		item.setAttribute("name", new Value(value));

		if (context.template != null && !context.template.name().equals(value.str))
			context.template = context.template.parent().get(value.str);
		return true;
	}
}
