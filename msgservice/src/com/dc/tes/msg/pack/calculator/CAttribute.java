package com.dc.tes.msg.pack.calculator;

import com.dc.tes.dom.MsgItem;
import com.dc.tes.exception.MsgErr;
import com.dc.tes.exception.TESException;
import com.dc.tes.msg.pack.PackContext;
import com.dc.tes.msg.pack.PackService;
import com.dc.tes.msg.util.Value;

/**
 * 计算节点的属性值（考虑了别名）
 * 
 * @author lijic
 * 
 */
@CalculatorTag("a")
@PostfixRequiredTag
class CAttribute extends Calculator {
	@Override
	protected Value calculate(MsgItem item, PackContext context) {
		if (item.getAttribute(context.param.postfix) == null)
			throw new TESException(MsgErr.Pack.AttributeNotFound, "attribute: " + context.param.postfix + " item: " + item);

		Value v = item.getAttribute(context.param.postfix);
		v = PackService.TranslateAlias(v, item, context.spec);
		return v;
	}
}