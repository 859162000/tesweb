package com.dc.tes.msg.pack.calculator;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;

import org.apache.commons.collections.MapUtils;

import com.dc.tes.dom.MsgArray;
import com.dc.tes.dom.MsgField;
import com.dc.tes.dom.MsgItem;
import com.dc.tes.dom.MsgStruct;
import com.dc.tes.exception.MsgErr;
import com.dc.tes.exception.TESException;
import com.dc.tes.msg.pack.PackContext;
import com.dc.tes.msg.pack.Param;
import com.dc.tes.msg.pack.processor.Processor;
import com.dc.tes.msg.util.Value;
import com.dc.tes.util.InstanceCreater;

/**
 * 数据计算器(Calculator) 从这个类派生出来的类用于计算报文的各项数值
 * 
 * @author lijic
 * 
 */
public abstract class Calculator {
	/**
	 * 所有目前支持的Calculator的实例的缓存
	 */
	private static HashMap<String, Calculator> s_calculators = new HashMap<String, Calculator>();

	/**
	 * 初始化Calculator实例缓存
	 */
	static {
		Class<?>[] classes = {
				CAttribute.class,
				CBackspace.class,
				CContext.class,
				CDelayPack.class,
				CFunction.class,
				CMember.class,
				CMemberCount.class,
				CMemberCountRecursive.class,
				CMemberIndex.class,
				CMemberIndexOfParent.class,
				CMemberIndexOfParentParent.class,
				CName.class,
				CNameOfParent.class,
				CNameOfParentParent.class,
				CPath.class,
				CReference.class,
				CScript.class,
				CText.class,
				CValue.class };

		for (Class<?> cls : classes)
			if (cls.getSuperclass() == Calculator.class && cls.isAnnotationPresent(CalculatorTag.class))
				s_calculators.put(cls.getAnnotation(CalculatorTag.class).value(), (Calculator) InstanceCreater.CreateInstance(cls));
	}

	/**
	 * 计算参数表达式
	 * 
	 * @param item
	 *            要被组包的报文元素
	 * @param context
	 *            组包上下文
	 * @return 计算该参数得出的值
	 */
	public static Value Calculate(MsgItem item, PackContext context) {
		if (context.param.num != null)
			return new Value(context.param.num);
		if (context.param.subParams != null) {
			int result = 0;
			for (Param p : context.param.subParams) {
				context.param = p;
				result += Calculate(item, context).i;
			}
			return new Value(result);
		}

		// 计算参数的值
		Value value = selectCalculator(item, context.param).calculate(item, context);

		if (value == null)
			throw new TESException(MsgErr.Pack.NullCalculateResult);

		// 计算参数前缀
		switch (context.param.prefix) {
		case None:
			// 没有长度前缀 直接把计算出的Value对象返回
			return value;
		case LValue:
			// 计算值的长度 将Value对象的length值返回
			return new Value(value.length);
		case LBytes:
			String encoding = MapUtils.getString(context.processorParams, "encoding");		
			try {
				return new Value(value.str.getBytes(encoding).length);
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// 计算字节长度 将计算出的Value对象使用Processor输出成的字节数组的长度返回
			//return new Value(Processor.Process(value, context.processorChar, context.processorParams).length);
		default:
			throw new TESException(MsgErr.Pack.UnsupportedParamPrefix_Calculator_Calculate, context.param.prefix.toString());
		}
	}

	/**
	 * 计算参数
	 * 
	 * @param item
	 *            被计算的节点
	 * @param spec
	 *            组包上下文
	 * @return 计算出的值
	 */
	protected abstract Value calculate(MsgItem item, PackContext context);

	/**
	 * 工具函数 用于选择一个合适的Calculator
	 * 
	 * @param param
	 *            要计算的参数
	 */
	private static Calculator selectCalculator(MsgItem item, Param param) {
		// 判断指定的Calculator是否存在
		if (!s_calculators.containsKey(param.body))
			throw new TESException(MsgErr.Pack.CalculatorNotFound, param.body);

		// 判断是否按照要求提供了后缀
		Calculator calculator = s_calculators.get(param.body);
		Class<? extends Calculator> cls = calculator.getClass();
		if (cls.getAnnotation(PostfixRequiredTag.class) != null && param.postfix == null)
			throw new TESException(MsgErr.Pack.ParamPostfixNotFound, "param: " + param + " item: " + item);

		if (param.postfix == null) {
			// 判断不带后缀的参数是否符合@Usage规则
			UsageTag usage = cls.getAnnotation(UsageTag.class);
			if (usage != null) {
				if (item instanceof MsgField && !usage.field())
					throw new TESException(MsgErr.Pack.ParamShouldNotOnField, "param: " + param + " item: " + item);
				if (item instanceof MsgArray && !usage.array())
					throw new TESException(MsgErr.Pack.ParamShouldNotOnArray, "param: " + param + " item: " + item);
				if (item instanceof MsgStruct && !usage.struct())
					throw new TESException(MsgErr.Pack.ParamShouldNotOnStruct, "param: " + param + " item: " + item);
			}
		} else {
			// 判断带后缀的参数是否符合@UsageWithPostfix规则
			UsageWithPostfixTag usage2 = cls.getAnnotation(UsageWithPostfixTag.class);
			if (usage2 != null) {
				if (item instanceof MsgField && !usage2.field())
					throw new TESException(MsgErr.Pack.ParamWithPostfixShouldNotOnField, "param: " + param + " item: " + item);
				if (item instanceof MsgArray && !usage2.array())
					throw new TESException(MsgErr.Pack.ParamWithPostfixShouldNotOnArray, "param: " + param + " item: " + item);
				if (item instanceof MsgStruct && !usage2.struct())
					throw new TESException(MsgErr.Pack.ParamWithPostfixShouldNotOnStruct, "param: " + param + " item: " + item);
			} else {
				// 当未指定@UsageWithPostfix时，判断参数是否符合带@Usage规则
				UsageTag usage = cls.getAnnotation(UsageTag.class);
				if (usage != null) {
					if (item instanceof MsgField && !usage.field())
						throw new TESException(MsgErr.Pack.ParamShouldNotOnField, "param: " + param + " item: " + item);
					if (item instanceof MsgArray && !usage.array())
						throw new TESException(MsgErr.Pack.ParamShouldNotOnArray, "param: " + param + " item: " + item);
					if (item instanceof MsgStruct && !usage.struct())
						throw new TESException(MsgErr.Pack.ParamShouldNotOnStruct, "param: " + param + " item: " + item);
				}
			}
		}

		return calculator;
	}
}
