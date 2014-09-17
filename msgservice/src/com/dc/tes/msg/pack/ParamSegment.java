package com.dc.tes.msg.pack;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import com.dc.tes.dom.MsgItem;
import com.dc.tes.exception.MsgErr;
import com.dc.tes.exception.TESException;
import com.dc.tes.msg.IContext;
import com.dc.tes.msg.pack.calculator.Calculator;
import com.dc.tes.msg.pack.processor.Processor;
import com.dc.tes.msg.util.Value;
import com.dc.tes.msg.util.FormatStringParser.FormatFragment;

/**
 * 参数段
 * 
 * @author lijic
 * 
 */
class ParamSegment extends Segment {
	/**
	 * 与该段相关联的Processor的标志
	 */
	private final char processorChar;
	/**
	 * 与该段相关联的Processor的扩展参数
	 */
	private final Map<String, String> processorParams;
	/**
	 * 与该段相关联的参数
	 */
	final Param param;
	/**
	 * 回退长度 如果该段是一个回退段 则该值表示其回退长度 否则该值为0
	 * <p>
	 * 这个长度是在组包的过程中由Calculator计算出来并动态地填写到这个域中的
	 * </p>
	 */
	int backspace;

	/**
	 * 初始化一个参数段
	 * 
	 * @param fragment
	 *            格式字符串中的片段
	 * @param param
	 *            参数
	 * @param encoding
	 *            报文的默认编码
	 */
	ParamSegment(FormatFragment fragment, Param param, Charset encoding) {
		this.processorChar = fragment.dataFormatChar;
		this.processorParams = fragment.params;
		this.param = param;

		if (!this.processorParams.containsKey("encoding"))
			this.processorParams.put("encoding", encoding.name());
	}

	@Override
	byte[] Pack(MsgItem item, PackSpecification spec, IContext context) {
		// 重置回退长度
		this.backspace = 0;

		// 准备组包上下文
		PackContext _context = new PackContext(spec, context, this.param, this.processorChar, new HashMap<String, String>(this.processorParams));

		// 调用Calculator计算本段的值
		Value value = Calculator.Calculate(item, _context);

		// 如果需要回退，则将回退长度记录下来
		this.backspace = _context.backspace;
		if (this.backspace < 0)
			throw new TESException(MsgErr.Pack.BackspaceMustPositive, "backspace: " + this.backspace + " item: " + item);

		// 如果回退长度不为0，则表示当前段不参与组包过程
		if (_context.backspace != 0)
			return new byte[0];

		// 调用Processor对值进行组包，得到本段对应的字节流并返回
		return Processor.Process(value, this.processorChar, _context.processorParams);
	}

	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append(this.param).append(" -> %").append(this.processorParams).append(this.processorChar);
		return buffer.toString();
	}
}