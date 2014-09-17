package com.dc.tes.msg.pack.calculator;

import com.dc.tes.dom.MsgField;
import com.dc.tes.dom.MsgItem;
import com.dc.tes.exception.MsgErr;
import com.dc.tes.exception.TESException;
import com.dc.tes.msg.pack.PackContext;
import com.dc.tes.msg.util.Value;

/**
 * 计算域的值 如果后缀了DPath，则计算相对于当前节点的DPath的域的值
 * 
 * @author lijic
 * 
 */
@CalculatorTag("v")
@UsageTag(array = false, field = true, struct = false)
@UsageWithPostfixTag(array = true, field = true, struct = true)
class CValue extends Calculator {
	@Override
	protected Value calculate(MsgItem item, PackContext context) {
		MsgField field;

		// 如果v参数带有后缀 则取由后缀指定的节点
		if (context.param.postfix == null)
			field = (MsgField) item;
		else {
			field = item.SelectSingleField(context.param.postfix);
			if (field == null)
				throw new TESException(MsgErr.Pack.VTargetNull, "target: " + context.param.postfix + " item: " + item);
		}

		// 将域本身的属性添加到Processor的参数列表 Processor原有的参数的优先级为高
		for (String k : item.getAttributes().keySet())
			if (!context.processorParams.containsKey(k))
				context.processorParams.put(k, item.getAttribute(k).str);

		return new Value(field.value());
	}
}