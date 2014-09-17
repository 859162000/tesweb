package com.dc.tes.msg.unpack;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.w3c.dom.Element;

import com.dc.tes.dom.MsgItem;
import com.dc.tes.exception.MsgErr;
import com.dc.tes.exception.TESException;
import com.dc.tes.util.RuntimeUtils;
import com.dc.tes.util.XmlUtils;

/**
 * 组包脚本
 * 
 * @author lijic
 * 
 */
class UnpackScript {
	/**
	 * 脚本名称
	 */
	final String name;
	/**
	 * 一个指针 指向该脚本的函数入口
	 */
	private final Method method;

	/**
	 * 初始化一篇组包脚本
	 * 
	 * @param e
	 */
	UnpackScript(Element e) {
		this.name = e.getAttribute("name");

		if (this.name.length() == 0)
			throw new TESException(MsgErr.Unpack.ScriptNameNull);

		// 脚本本身即是一段java代码，本段代码的作用即是将其套在一个动态生成的类中，编译并产生一个实例，并将本实例的method方法指向这个新实例的Exec方法
		try {
			// 生成代码
			StringBuffer code = new StringBuffer();
			code.append("import com.dc.tes.dom.*;\r\n");
			code.append("import com.dc.tes.dom.util.*;\r\n");
			code.append("import com.dc.tes.exception.*;\r\n");
			code.append("import com.dc.tes.msg.*;\r\n");
			code.append("import com.dc.tes.msg.unpack.*;\r\n");
			code.append("import com.dc.tes.msg.util.*;\r\n");
			code.append("import com.dc.tes.util.*;\r\n");
			code.append("import com.dc.tes.util.type.*;\r\n");

			for (String extPackage : XmlUtils.SelectNodeListText(e, "import"))
				if (extPackage != null && extPackage.length() != 0)
					code.append("import " + extPackage + ";\r\n");

			code.append("public class " + this.name + " {\r\n");
			code.append("public static boolean Exec(MsgItem item, UnpackContext context){\r\n");
			code.append(XmlUtils.getNodeText(e));
			code.append("}\r\n");
			code.append("}");

			// 动态编译
			Class<?> c = RuntimeUtils.CompileClass(code.toString(), this.name);

			// 获取其Exec方法
			this.method = c.getMethod("Exec", MsgItem.class, UnpackContext.class);
		} catch (Exception ex) {
			throw new TESException(MsgErr.Pack.ScriptCompileFail, ex);
		}
	}

	/**
	 * 执行组包脚本
	 * 
	 * @param item
	 *            被组包的报文元素
	 * @param context
	 *            上下文
	 * @return 脚本执行结果
	 */
	boolean Exec(MsgItem item, UnpackContext context) {
		try {
			return (Boolean) this.method.invoke(null, item, context);
		} catch (InvocationTargetException ex) {
			throw new TESException(MsgErr.Unpack.ScriptExecFail, ex.getCause());
		} catch (Exception ex) {
			throw new TESException(MsgErr.Unpack.ScriptExecFail, ex);
		}
	}
}
