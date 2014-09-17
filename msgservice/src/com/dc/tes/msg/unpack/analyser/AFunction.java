package com.dc.tes.msg.unpack.analyser;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.dc.tes.dom.MsgItem;
import com.dc.tes.exception.MsgErr;
import com.dc.tes.exception.TESException;
import com.dc.tes.msg.unpack.UnpackContext;
import com.dc.tes.msg.util.Value;

/**
 * 计算外部函数的值
 * 
 * @author lijic
 * 
 */
@AnalyserTag("f")
@PostfixRequiredTag
class AFunction extends Analyser {
	@Override
	protected boolean match(Value value, MsgItem item, UnpackContext context) {
		String postfix = context.param.postfix;

		if (!context.param.postfix.contains("."))
			throw new TESException(MsgErr.Pack.FunctionNameUnparseable, postfix);

		String className = postfix.substring(0, postfix.lastIndexOf('.'));
		String methodName = postfix.substring(postfix.lastIndexOf('.') + 1);

		Class<?> cls;
		Method method;

		try {
			cls = Class.forName(className);
			method = cls.getMethod(methodName, MsgItem.class, UnpackContext.class);

			if (method.getReturnType() != Boolean.class && method.getReturnType() != boolean.class)
				throw new TESException(MsgErr.Unpack.FunctionResultTypeError, postfix);

			return (Boolean) method.invoke(null, item, context);
		} catch (ClassNotFoundException ex) {
			throw new TESException(MsgErr.Unpack.ClassNotFound, className);
		} catch (NoSuchMethodException ex) {
			throw new TESException(MsgErr.Unpack.FunctionNotFound, postfix);
		} catch (InvocationTargetException ex) {
			throw new TESException(MsgErr.Unpack.CallFunctionFail, postfix, ex.getCause());
		} catch (Exception ex) {
			throw new TESException(MsgErr.Unpack.CallFunctionFail, postfix, ex);
		}
	}
}
