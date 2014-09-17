package com.dc.tes.msg.pack.calculator;

import com.dc.tes.dom.MsgItem;
import com.dc.tes.msg.pack.PackContext;
import com.dc.tes.msg.pack.PackService;
import com.dc.tes.msg.util.Value;

/**
 * 计算脚本运行结果
 * 
 * @author lijic
 * 
 */
@CalculatorTag("s")
@PostfixRequiredTag
class CScript extends Calculator {
	@Override
	protected Value calculate(MsgItem item, PackContext context) {
		String name = context.param.postfix;

		return PackService.ExecScript(name, item, context);
	}
}
