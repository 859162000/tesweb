package com.dc.tes.msg.unpack;

/**
 * 表示格式字符串中的参数
 * 
 * @author lijic
 * 
 */
public class Param {
	/**
	 * 本体
	 */
	public final String body;
	/**
	 * 后缀 如果无后缀 则该值为null
	 */
	public final String postfix;

	/**
	 * 参数的描述字符串
	 */
	public final String expr;

	/**
	 * 初始化一个参数对象
	 * 
	 * @param expr
	 *            参数的字符串描述
	 */
	Param(String expr) {
		this.expr = expr;

		// 分析后缀
		if (expr.contains(".")) {
			String postfix = expr.substring(expr.indexOf('.') + 1);
			if (postfix.length() == 0)
				postfix = null;
			this.postfix = postfix;
			expr = expr.substring(0, expr.indexOf('.'));
		} else
			this.postfix = null;

		// 分析参数本体
		this.body = expr;
	}

	@Override
	public String toString() {
		return this.expr;
	}
}
