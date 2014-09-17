package com.dc.tes.msg.pack.calculator;

import com.dc.tes.dom.MsgItem;
import com.dc.tes.msg.pack.PackContext;
import com.dc.tes.msg.util.Value;

/**
 * 计算节点的DPath
 * 
 * @author lijic
 * 
 */
@CalculatorTag("p")
class CPath extends Calculator {
	@Override
	protected Value calculate(MsgItem item, PackContext context) {
		return new Value(item.dpath());
	}
}
