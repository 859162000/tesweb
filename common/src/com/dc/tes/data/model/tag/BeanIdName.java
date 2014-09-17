package com.dc.tes.data.model.tag;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用于标识bean类型的主键的名称
 * 
 * @author huangzx
 * 
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface BeanIdName {
	/**
	 * 主键属性名称
	 */
	public String value();
}
