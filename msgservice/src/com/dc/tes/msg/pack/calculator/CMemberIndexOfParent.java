package com.dc.tes.msg.pack.calculator;

import com.dc.tes.dom.MsgItem;
import com.dc.tes.msg.pack.PackContext;
import com.dc.tes.msg.util.Value;

/**
 * 计算父节点在其容器中的位置
 * 
 * @author lijic
 * 
 */
@CalculatorTag("mip")
class CMemberIndexOfParent extends Calculator {
	@Override
	protected Value calculate(MsgItem item, PackContext context) {
		item = item.parent();
		return new Value(item.parent().indexOf(item));
	}
}
