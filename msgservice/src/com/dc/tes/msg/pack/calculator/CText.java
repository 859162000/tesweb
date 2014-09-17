package com.dc.tes.msg.pack.calculator;

import com.dc.tes.dom.MsgItem;
import com.dc.tes.msg.pack.PackContext;
import com.dc.tes.msg.util.Value;

/**
 * 计算纯文本 提供该计算器的目地是为了支持当某一段文本的编码与报文的其它部分不一致时的报文组包 此时可以针对此参数专门指定该段文本的编码
 * 
 * @author lijic
 * 
 */
@CalculatorTag("t")
@PostfixRequiredTag
class CText extends Calculator {
	@Override
	protected Value calculate(MsgItem item, PackContext context) {
		return new Value(context.param.postfix);
	}
}
