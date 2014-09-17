package com.dc.tes.msg.pack.processor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用于标记某个类为Processor并指定它们的标记字符。标记字符是指在格式字符串中的%x中的x
 * 
 * @author lijic
 * 
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@interface ProcessorTag {
	/**
	 * 标记字符
	 */
	char value();
}
