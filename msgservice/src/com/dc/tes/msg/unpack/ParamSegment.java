package com.dc.tes.msg.unpack;

import java.nio.charset.Charset;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.dc.tes.dom.MsgContainerUtils;
import com.dc.tes.dom.MsgItem;
import com.dc.tes.exception.MsgErr;
import com.dc.tes.exception.TESException;
import com.dc.tes.msg.IContext;
import com.dc.tes.msg.unpack.analyser.Analyser;
import com.dc.tes.msg.unpack.parser.Parser;
import com.dc.tes.msg.util.Value;
import com.dc.tes.msg.util.FormatStringParser.FormatFragment;
import com.dc.tes.util.ByteArrayUtils;
import com.dc.tes.util.type.Wrapper;

class ParamSegment extends Segment {
	private static final Log log = LogFactory.getLog(ParamSegment.class);

	/**
	 * 与该段相关联的Processor的标志
	 */
	private final char parserChar;
	/**
	 * 与该段相关联的Processor的扩展参数
	 */
	private final Map<String, String> parserParams;

	/**
	 * 该段所属的拆包规则单元
	 */
	private final RuleUnit rule;
	/**
	 * 与该段相关联的参数
	 */
	private final Param param;

	/**
	 * 该参数段的段编号
	 */
	private final int segmentId;
	/**
	 * 该参数段的参数编号
	 */
	private final int paramId;

	/**
	 * 初始化一个参数段
	 */
	ParamSegment(FormatFragment segment, RuleUnit rule, Param param, Charset encoding, int segmentId, int paramId) {
		this.parserChar = segment.dataFormatChar;
		this.parserParams = segment.params;

		this.rule = rule;
		this.param = param;

		this.segmentId = segmentId;
		this.paramId = paramId;

		if (!this.parserParams.containsKey("encoding"))
			this.parserParams.put("encoding", encoding.name());
	}

	@Override
	int Match(byte[] bytes, MsgItem item, Wrapper<MsgItem> template, int start, UnpackSpecification spec, IContext context) {
		/*
		 * 尝试推断该参数段的长度
		 */

		int length = -1;

		// 获取长度信息 原则如下
		// 优先使用从报文中取到的长度
		// 如果报文中没有提供长度，则使用模板中提供的长度（如果对应的参数是m或v的话）或由Parser提供的长度
		// 使用Lex在报文中进行匹配，长度为匹配得到的长度
		// 最后在迫不得已的情况下，使用左右边界进行匹配以求得长度

		// 此参数段的数据长度是否已由其它的参数给出
		if (length == -1) {
			Value l = item.getAttribute(MsgContainerUtils.C_InternalDomElementPrefix + "l" + this.param.body, false);
			if (l != Value.empty) {
				try {
					String ll = l.str.trim();
					if(!ll.isEmpty())		
						length = Integer.parseInt(l.str.trim());// 长度信息是使用String存储的
					else
						length = 0;
					if (length < 0)
						throw new TESException(MsgErr.Unpack.AnotherParsedLenMustPositive, "parser: %" + this.parserChar + " param: " + this.param + " l_param: " + l.str);
				} catch (NumberFormatException ex) {
					throw new TESException(MsgErr.Unpack.AnotherParsedLenUnparseable, "parser: %" + this.parserChar + " param: " + this.param + " l_param: " + l.str);
				}
				// 将数据长度换算为字节长度
				length = Parser.Convert(bytes, start, length, this.parserChar, this.parserParams);
			}
		}

		// 此参数段的字节长度是否已由其它的参数给出
		if (length == -1) {
			Value l = item.getAttribute(MsgContainerUtils.C_InternalDomElementPrefix + "L" + this.param.body, false);
			if (l != Value.empty)
				try {
					length = Integer.parseInt(l.str.trim());// 长度信息是使用String存储的
					if (length < 0)
						throw new TESException(MsgErr.Unpack.AnotherParsedLenMustPositive, "parser: %" + this.parserChar + " param: " + this.param + " L_param: " + l.str);
				} catch (NumberFormatException ex) {
					throw new TESException(MsgErr.Unpack.AnotherParsedLenUnparseable, "parser: %" + this.parserChar + " param: " + this.param + " L_param: " + l.str);
				}
		}

		// 看此参数段的数据长度是否由r参数指定过
		if (length == -1) {
			Value refLen = item.getAttribute(MsgContainerUtils.C_InternalDomElementPrefix + "lr." + this.paramId, false);
			if (refLen != Value.empty) {
				try {
					length = Integer.parseInt(refLen.str.trim());// 长度信息是使用String存储的
					if (length < 0)
						throw new TESException(MsgErr.Unpack.ReferenceLenMustPositive, "parser: %" + this.parserChar + " param: " + this.param + "paramId: " + this.paramId + " lr_param: " + refLen.str);
				} catch (NumberFormatException ex) {
					throw new TESException(MsgErr.Unpack.ReferenceLenUnparseable, "parser: %" + this.parserChar + " param: " + this.param + "paramId: " + this.paramId + " lr_param: " + refLen.str);
				}
				// 将数据长度换算为字节长度
				length = Parser.Convert(bytes, start, length, this.parserChar, this.parserParams);
			}
		}

		// 看此参数段的字节长度是否由r参数指定过
		if (length == -1) {
			Value refLen = item.getAttribute(MsgContainerUtils.C_InternalDomElementPrefix + "Lr." + this.paramId, false);
			if (refLen != Value.empty)
				try {
					length = Integer.parseInt(refLen.str.trim());// 长度信息是使用String存储的
					if (length < 0)
						throw new TESException(MsgErr.Unpack.ReferenceLenMustPositive, "parser: %" + this.parserChar + " param: " + this.param + "paramId: " + this.paramId + " Lr_param: " + refLen.str);
				} catch (NumberFormatException ex) {
					throw new TESException(MsgErr.Unpack.ReferenceLenUnparseable, "parser: %" + this.parserChar + " param: " + this.param + "paramId: " + this.paramId + " Lr_param: " + refLen.str);
				}
		}

		// 如果此参数对应的是v或m 则该段的数据长度为对应报文元素的len属性的值
		if (length == -1 && template.getValue() != null) {
			if (this.param.body.equals("v") || this.param.body.equals("m"))
				length = template.getValue().getAttribute("len").i;
			if (length == Integer.MIN_VALUE)
				length = -1;
			if (MapUtils.getBoolean(this.parserParams, "ignoreLen", false))
				length = -1;
			
			//从这里可以看出模板上的len是代表转码后的len
			//比如某个字段返回报文是4个字节,一般填len=4
			//但假如这个字段是中文，那么它代表2个中文,len得填2,填4会截错
			//这里先注释掉
			//将数据长度换算为字节长度
			//length = Parser.Convert(bytes, start, length, this.parserChar, this.parserParams);
		}

		// 看与此参数相对应的Parser是否可以提供长度 该长度为字节长度
		if (length == -1) {
			if (this.parserParams.containsKey("len"))
				try {
					length = Integer.parseInt(this.parserParams.get("len"));
					if (length < 0)
						throw new TESException(MsgErr.Unpack.ParserLenMustPositive, "parser: %" + this.parserChar + " params: " + this.parserParams);
				} catch (NumberFormatException ex) {
					throw new TESException(MsgErr.Unpack.ParserLenUnparseable, "parser: %" + this.parserChar + " params: " + this.parserParams);
				}
		}

		// 如果对该参数指定了词法 则用该词法进行匹配 取出匹配的长度 该长度为字节长度
		if (length == -1)
			if (spec.lexes.containsKey(this.param.body)) {
				Lex lex = spec.lexes.get(this.param.body);

				if (lex.find(bytes, start) == start)
					length = lex.length();
			}

		// 如果此参数的右侧是一个文本段 则对该文本段进行定位以确定自身的字节长度
		if (length == -1) {
			if (this.rule.segments[this.segmentId + 1] instanceof TextSegment) {
				TextSegment nextSegment = (TextSegment) this.rule.segments[this.segmentId + 1];

				// 判断右侧的文本段是不是边界
				if (nextSegment == this.rule.segments[this.rule.segments.length - 1] && nextSegment.bytes.length == 0) {
					length = bytes.length - start;
				} else {
					int nextSegmentPos = ByteArrayUtils.IndexOf(bytes, nextSegment.bytes, start);
					if (nextSegmentPos != -1)
						length = nextSegmentPos - start;
				}
			}
		}

		// 如果已经判断出长度 并且定义了Lex 则检查截取出的段是否与词法相匹配
		if (length != -1 && spec.lexes.containsKey(this.param.body)) {
			Lex lex = spec.lexes.get(this.param.body);
			if (!lex.match(bytes, start, length))
				return -1;
		}

		// 调用Parser解析字节流
		log.debug("Value v = Parser.Parse(bytes, start, length, parserChar, parserParams);");
		log.debug("Value v = Parser.Parse(bytes, " + start + ", " + length + ", " + parserChar + ", " + parserParams + ");");
		Value v = Parser.Parse(bytes, start, length, parserChar, parserParams);
		log.debug("解析出 v = " + v);

		// 建立拆包上下文
		UnpackContext _context = new UnpackContext(param, template.getValue(), bytes, start, length, spec, context);

		// 调用与此参数对应的数据分析器进行分析
		boolean result = Analyser.Analyse(v, item, _context);

		// 如果分析成功则返回上下文中的长度，否则返回-1
		log.debug(result ? _context.length : -1);
		return result ? _context.length : -1;
	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName() + "[%" + this.parserParams + this.parserChar + " ->" + this.param + "]";
	}
}
