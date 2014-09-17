package com.dc.tes.msg.pack.calculator;

import com.dc.tes.dom.MsgItem;
import com.dc.tes.exception.MsgErr;
import com.dc.tes.exception.TESException;
import com.dc.tes.msg.pack.PackContext;
import com.dc.tes.msg.pack.PackService;
import com.dc.tes.msg.util.Value;

/**
 * 计算对某个参数计算出的字节流的引用
 * 
 * @author lijic
 * 
 */
@CalculatorTag("r")
@PostfixRequiredTag
class CReference extends Calculator {
	@Override
	protected Value calculate(MsgItem item, PackContext context) {
		int refIndex;
		try {
			refIndex = Integer.parseInt(context.param.postfix);
		} catch (NumberFormatException ex) {
			throw new TESException(MsgErr.Pack.RefIndexNotInteger, context.param.postfix);
		}

		if (refIndex < 0)
			throw new TESException(MsgErr.Pack.RefIndexMustPositive, context.param.postfix);

		return new Value(PackService.CalcReference(item, refIndex, context));
	}
}
