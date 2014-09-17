package com.dc.tes.msg.pack.calculator;

import com.dc.tes.dom.MsgDocument;
import com.dc.tes.dom.MsgItem;
import com.dc.tes.msg.pack.PackContext;
import com.dc.tes.msg.pack.PackService;
import com.dc.tes.msg.util.Value;
import com.dc.tes.fcore.script.MsgContext;

@CalculatorTag("d")
public class CDelayPack extends Calculator {

	@Override
	protected Value calculate(MsgItem item, PackContext context) {
		// TODO Auto-generated method stub
		
		MsgDocument doc = item.getDocument();
		
		if(doc.getAttribute(PackService.C_InternalAttribute_DelayPackFlag).i
				!= -1) {
			MsgContext msgContext = (MsgContext) context.context;
			
			doc.setAttribute(PackService.C_InternalAttribute_DelayPackFlag, 
					msgContext.posOfMsgContainer);
			doc.setAttribute("dpathOfField", item.dpath());
		}
		
		return new Value(new byte[0]);
	
	}

}
