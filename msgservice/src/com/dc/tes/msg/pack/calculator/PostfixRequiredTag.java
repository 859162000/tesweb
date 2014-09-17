package com.dc.tes.msg.pack.calculator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用于标记Calculator和Analyser，指定在使用它们时必须提供参数后缀
 * 
 * @author lijic
 * 
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@interface PostfixRequiredTag {
}
