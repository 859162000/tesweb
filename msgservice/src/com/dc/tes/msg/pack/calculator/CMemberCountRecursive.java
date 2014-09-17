package com.dc.tes.msg.pack.calculator;

import com.dc.tes.dom.DefaultForEachVisitor;
import com.dc.tes.dom.MsgField;
import com.dc.tes.dom.MsgItem;
import com.dc.tes.msg.pack.PackContext;
import com.dc.tes.msg.util.Value;
import com.dc.tes.util.type.Wrapper;

/**
 * 递归计算所有子节点的数量
 * 
 * @author lijic
 * 
 */
@CalculatorTag("mcr")
@UsageTag(array = true, field = false, struct = true)
class CMemberCountRecursive extends Calculator {
	@Override
	protected Value calculate(MsgItem item, PackContext context) {
		final Wrapper<Integer> fields = new Wrapper<Integer>(0);
		item.ForEach(new DefaultForEachVisitor() {
			@Override
			public void Field(MsgField field) {
				fields.setValue(fields.getValue() + 1);
			}
		});

		return new Value(fields.getValue());
	}
}
