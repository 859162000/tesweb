package com.dc.tes.msg.unpack.analyser;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用于标记某个类为Analyser并指定它们的标记字符串。标记字符串是指在格式字符串的参数的主体部分
 * 
 * @author lijic
 * 
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@interface AnalyserTag {
	/**
	 * 标记字符串
	 */
	String value();
}
