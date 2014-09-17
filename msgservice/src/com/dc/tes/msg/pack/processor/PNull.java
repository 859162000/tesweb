package com.dc.tes.msg.pack.processor;

import java.util.Map;

import com.dc.tes.msg.util.Value;

/**
 * 忽略掉传入的值 不对其进行格式化输出<br/>
 * 此Processor一般与f参数或s参数配套 用于在组包之前或之后进行一些额外操作
 * 
 * @author lijic
 * 
 */
@ProcessorTag('n')
class PNull extends Processor {
	@Override
	protected byte[] process(Value value, Map<String, String> params) {
		return new byte[0];
	}
}
