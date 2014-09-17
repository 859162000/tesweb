package com.dc.tes.msg.unpack.filter;

import java.nio.charset.Charset;

import org.w3c.dom.Element;

import com.dc.tes.exception.MsgErr;
import com.dc.tes.exception.TESException;

/**
 * 过滤器 过滤器用于从原始的字节流中去掉一些符合过滤条件的字节 最典型的比如xml中的注释
 * 
 * @author lijic
 * 
 */
public abstract class Filter {
	/**
	 * 创建一个过滤器
	 * 
	 * @param e
	 *            描述这个过滤器的XML节点
	 * @param encoding
	 *            编码
	 * @return 创建好的过滤器
	 */
	public static Filter CreateFilter(Element e, Charset encoding) {
		// 根据method方法判断选用哪种过滤方式
		String method = e.getAttribute("method");
		try {
			if (method.equalsIgnoreCase("text"))
				// 基于文本匹配的过滤器
				return new TextFilter(e, encoding);

			else if (method.equalsIgnoreCase("border"))
				// 基于左右边界的过滤器
				return new BorderFilter(e, encoding);

			else if (method.equalsIgnoreCase("regex"))
				// 基于正则表达式的过滤器
				return new RegexFilter(e, encoding);

			else if (method.equalsIgnoreCase("func"))
				// 基于外部函数的过滤器
				return new FuncFilter(e, encoding);

			else if (method.equalsIgnoreCase("script"))
				// 基于脚本的过滤器
				return new ScriptFilter(e, encoding);

			else
				throw new TESException(MsgErr.Unpack.UnsupportedFilter, method);
		} catch (Exception ex) {
			throw new TESException(MsgErr.Unpack.InitializeFilterFail, method, ex);
		}
	}

	/**
	 * 清理字节流 删除掉所有符合该过滤器描述的字节
	 * 
	 * @param bytes
	 *            要被清理的字节流
	 * @return 清理过的字节流
	 */
	public abstract byte[] Clean(byte[] bytes);
}