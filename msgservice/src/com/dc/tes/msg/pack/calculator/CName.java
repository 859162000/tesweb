package com.dc.tes.msg.pack.calculator;

import com.dc.tes.dom.MsgItem;
import com.dc.tes.msg.pack.PackContext;
import com.dc.tes.msg.util.Value;

/**
 * 计算节点的名称
 * 
 * @author lijic
 * 
 */
@CalculatorTag("n")
class CName extends Calculator {
	@Override
	protected Value calculate(MsgItem item, PackContext context) {
		return new Value(item.name());
	}
}
