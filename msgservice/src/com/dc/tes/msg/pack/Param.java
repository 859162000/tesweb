package com.dc.tes.msg.pack;

import java.util.ArrayList;

import org.apache.commons.lang.StringUtils;

/**
 * 表示格式字符串中的参数
 * 
 * @author lijic
 * 
 */
public class Param {
	/**
	 * 长度前缀
	 * 
	 * @author lijic
	 * 
	 */
	public enum Prefix {
		/**
		 * 无长度前缀
		 */
		None,
		/**
		 * 求值的长度
		 */
		LValue,
		/**
		 * 求组包后的字节长度
		 */
		LBytes
	}

	/**
	 * 前缀
	 */
	public final Prefix prefix;
	/**
	 * 本体
	 */
	public final String body;
	/**
	 * 后缀 如果无后缀 则该值为null
	 */
	public final String postfix;

	/**
	 * 如果该参数是一个复合参数 则subParams表示组成该复合参数的子参数的列表 否则subParams为null
	 */
	public final Param[] subParams;
	/**
	 * 如果该参数是一个数值参数 则num表示该数值 否则num为null
	 */
	public final Integer num;

	/**
	 * 参数的描述字符串
	 */
	private final String expr;

	/**
	 * 初始化一个参数对象
	 * 
	 * @param expr
	 *            参数的字符串描述
	 */
	Param(String expr) {
		this.expr = expr;

		boolean isNumberic;

		if (expr.length() > 0 && StringUtils.isNumeric(expr))
			isNumberic = true;
		else if (expr.length() > 1 && expr.startsWith("-") && StringUtils.isNumeric(expr.substring(1)))
			isNumberic = true;
		else
			isNumberic = false;

		if (isNumberic)
			this.num = Integer.parseInt(expr);
		else
			this.num = null;

		if (expr.contains("+")) {
			ArrayList<Param> subParams = new ArrayList<Param>();
			for (String subExpr : expr.split("\\+"))
				subParams.add(new Param(subExpr));
			this.subParams = subParams.toArray(new Param[0]);
		} else
			this.subParams = null;

		// 分析前缀
		if (expr.startsWith("l")) {
			this.prefix = Prefix.LValue;
			expr = expr.substring(1);
		} else if (expr.startsWith("L")) {
			this.prefix = Prefix.LBytes;
			expr = expr.substring(1);
		} else
			this.prefix = Prefix.None;

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
