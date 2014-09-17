package com.dc.tes.msg.unpack.analyser;

import com.dc.tes.dom.MsgContainer;
import com.dc.tes.dom.MsgItem;
import com.dc.tes.msg.unpack.UnpackContext;
import com.dc.tes.msg.unpack.UnpackService;
import com.dc.tes.msg.util.Value;

@AnalyserTag("m")
public class AMember extends Analyser {
	@Override
	protected boolean match(Value value, MsgItem item, UnpackContext context) {
		if (!(item instanceof MsgContainer))
			return false;
		if (context.template != null && !(context.template instanceof MsgContainer))
			return false;

		int len = UnpackService.UnpackMember(context.bytes, context.pos, context.length, (MsgContainer) item, (MsgContainer) context.template, context.spec, context.context);

		if (len != -1) {
			context.length = len;
			return true;
		} else
			return false;
	}
}
