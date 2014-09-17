package com.dc.tes.msg.pack.calculator;

import com.dc.tes.dom.MsgContainer;
import com.dc.tes.dom.MsgItem;
import com.dc.tes.msg.pack.PackContext;
import com.dc.tes.msg.util.Value;

/**
 * 计算子节点的数量 考虑memberCount和memberPlus
 * 
 * @author lijic
 * 
 */
@CalculatorTag("mc")
@UsageTag(array = true, field = false, struct = true)
class CMemberCount extends Calculator {
	@Override
	protected Value calculate(MsgItem item, PackContext context) {
		return new Value(((MsgContainer) item).size());
	}
}
