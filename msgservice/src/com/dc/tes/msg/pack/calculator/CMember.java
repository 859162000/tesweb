package com.dc.tes.msg.pack.calculator;

import com.dc.tes.dom.MsgContainer;
import com.dc.tes.dom.MsgItem;
import com.dc.tes.exception.MsgErr;
import com.dc.tes.exception.TESException;
import com.dc.tes.msg.pack.PackContext;
import com.dc.tes.msg.pack.PackService;
import com.dc.tes.msg.util.Value;

/**
 * 计算子节点的输出字节流
 * 
 * @author lijic
 * 
 */
@CalculatorTag("m")
@UsageTag(array = true, field = false, struct = true)
@UsageWithPostfixTag(array = true, field = true, struct = true)
class CMember extends Calculator {
	@Override
	protected Value calculate(MsgItem item, PackContext context) {
		MsgContainer container = (MsgContainer) item;

		// 如果m参数带有后缀 则取由后缀指定的节点
		if (context.param.postfix != null) {
			MsgItem _item = item.SelectSingleNode(context.param.postfix);
			if (_item == null)
				throw new TESException(MsgErr.Pack.MTargetNull, "target: " + context.param.postfix + " item: " + item);
			if (!(_item instanceof MsgContainer))
				throw new TESException(MsgErr.Pack.MTargetNotContainer, "target: " + context.param.postfix + " item: " + item);

			container = (MsgContainer) _item;
		}

		// 将节点本身的属性添加到Processor的参数列表 Processor原有的参数的优先级为高
		for (String k : item.getAttributes().keySet())
			if (!context.processorParams.containsKey(k))
				context.processorParams.put(k, item.getAttribute(k).str);

		return PackService.PackMember(container, context);
	}
}
