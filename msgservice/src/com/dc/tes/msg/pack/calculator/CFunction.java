package com.dc.tes.msg.pack.calculator;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.dc.tes.dom.MsgItem;
import com.dc.tes.exception.MsgErr;
import com.dc.tes.exception.TESException;
import com.dc.tes.msg.pack.PackContext;
import com.dc.tes.msg.util.Value;

/**
 * 计算外部函数的值
 * 
 * @author lijic
 * 
 */
@CalculatorTag("f")
@PostfixRequiredTag
class CFunction extends Calculator {
	@Override
	protected Value calculate(MsgItem item, PackContext context) {
		String postfix = context.param.postfix;

		if (!context.param.postfix.contains("."))
			throw new TESException(MsgErr.Pack.FunctionNameUnparseable, postfix);

		String className = postfix.substring(0, postfix.lastIndexOf('.'));
		String methodName = postfix.substring(postfix.lastIndexOf('.') + 1);

		Class<?> cls;
		Method method;

		try {
			cls = Class.forName(className);
			method = cls.getMethod(methodName, MsgItem.class, PackContext.class);

			if (method.getReturnType() != Value.class)
				throw new TESException(MsgErr.Pack.FunctionResultTypeError, postfix);

			return (Value) method.invoke(null, item, context);
		} catch (ClassNotFoundException ex) {
			throw new TESException(MsgErr.Pack.ClassNotFound, className);
		} catch (NoSuchMethodException ex) {
			throw new TESException(MsgErr.Pack.FunctionNotFound, postfix);
		} catch (InvocationTargetException ex) {
			throw new TESException(MsgErr.Pack.CallFunctionFail, postfix, ex.getCause());
		} catch (Exception ex) {
			throw new TESException(MsgErr.Pack.CallFunctionFail, postfix, ex);
		}
	}
}
