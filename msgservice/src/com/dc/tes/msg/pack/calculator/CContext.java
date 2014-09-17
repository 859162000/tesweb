package com.dc.tes.msg.pack.calculator;

import com.dc.tes.dom.MsgItem;
import com.dc.tes.exception.MsgErr;
import com.dc.tes.exception.TESException;
import com.dc.tes.msg.pack.PackContext;
import com.dc.tes.msg.util.Value;

/**
 * 计算上下文中的值 如果是MsgDocument类型则继续计算DomPath
 * 
 * @author lijic
 * 
 */
@CalculatorTag("c")
@PostfixRequiredTag
class CContext extends Calculator {
	@Override
	protected Value calculate(MsgItem item, PackContext context) {
		String name = context.param.postfix;

		if (name.equals("tranCode"))
			return new Value(context.context.getTranCode());
		else
			throw new TESException(MsgErr.Pack.ContextNameNotFound, context.param.postfix);
	}
}
