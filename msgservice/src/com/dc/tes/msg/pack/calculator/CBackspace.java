package com.dc.tes.msg.pack.calculator;

import com.dc.tes.dom.MsgItem;
import com.dc.tes.exception.MsgErr;
import com.dc.tes.exception.TESException;
import com.dc.tes.msg.pack.PackContext;
import com.dc.tes.msg.util.Value;

/**
 * 从当前已拼好的报文流的末尾截掉一定数量的字节
 * 
 * @author lijic
 * 
 */
@CalculatorTag("b")
@PostfixRequiredTag
class CBackspace extends Calculator {
	@Override
	protected Value calculate(MsgItem item, PackContext context) {
		// 通过设置上下文中backspace项 来标志该段需要回退
		// 对回退段的处理是在PackService中处理的
		try {
			context.backspace = Integer.parseInt(context.param.postfix);
			if (context.backspace <= 0)
				throw new TESException(MsgErr.Pack.BackspaceMustPositive, context.param.postfix);
		} catch (NumberFormatException ex) {
			throw new TESException(MsgErr.Pack.BackspaceUnparseable, context.param.postfix);
		}

		// 返回一个空的字节数组
		return new Value(new byte[context.backspace]);
	}
}
