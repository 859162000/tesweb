package com.dc.tes.msg.pack.calculator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用于标记Calculator和Analyser，指定它们在处理带有后缀的参数时可以应用于的报文元素类型。如果不提供本标记则默认为等同于Usage标记的内容
 * 
 * @author lijic
 * 
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@interface UsageWithPostfixTag {
	/**
	 * 是否可用于字段
	 */
	public boolean field();

	/**
	 * 是否可用于数组
	 */
	public boolean array();

	/**
	 * 是否可用于结构
	 */
	public boolean struct();
}
