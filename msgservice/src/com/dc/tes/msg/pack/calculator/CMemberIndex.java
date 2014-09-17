package com.dc.tes.msg.pack.calculator;

import com.dc.tes.dom.MsgItem;
import com.dc.tes.msg.pack.PackContext;
import com.dc.tes.msg.util.Value;

/**
 * 计算当前节点在父节点中的位置
 * 
 * @author NuclearG
 * 
 */
@CalculatorTag("mi")
class CMemberIndex extends Calculator {
	@Override
	protected Value calculate(MsgItem item, PackContext context) {
		return new Value(item.parent().indexOf(item));
	}
}
