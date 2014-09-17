package com.dc.tes.msg.pack.calculator;

import com.dc.tes.dom.MsgItem;
import com.dc.tes.msg.pack.PackContext;
import com.dc.tes.msg.util.Value;

/**
 * 计算其父节点的父节点位于其容器中的位置
 * 
 * @author lijic
 * 
 */
@CalculatorTag("mipp")
class CMemberIndexOfParentParent extends Calculator {
	@Override
	protected Value calculate(MsgItem item, PackContext context) {
		item = item.parent().parent();
		return new Value(item.parent().indexOf(item));
	}
}
