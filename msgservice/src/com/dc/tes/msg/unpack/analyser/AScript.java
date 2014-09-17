package com.dc.tes.msg.unpack.analyser;

import com.dc.tes.dom.MsgItem;
import com.dc.tes.msg.unpack.UnpackContext;
import com.dc.tes.msg.unpack.UnpackService;
import com.dc.tes.msg.util.Value;

@AnalyserTag("s")
@PostfixRequiredTag
class AScript extends Analyser {
	@Override
	protected boolean match(Value value, MsgItem item, UnpackContext context) {
		String script = context.param.postfix;
		return UnpackService.ExecUnpackScript(script, value, item, context);
	}

}
