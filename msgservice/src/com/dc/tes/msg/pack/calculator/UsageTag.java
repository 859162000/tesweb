package com.dc.tes.msg.pack.calculator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用于标记Calculator和Analyser，指定它们在处理参数时可以应用于的报文元素类型。如果不提供该标记则默认为可以应用于任何类型的报文元素
 * 
 * @author lijic
 * 
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@interface UsageTag {
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
