package com.dc.tes.component.tag;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标明配置对象中的某个公共域是
 * 
 * @author huangzx
 * 
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ComponentProperty {
	/**
	 * 属性的配置名称 即读写该属性时应提供的名称 不提供时默认为该域的名称
	 */
	public String configName() default "";

	/**
	 * 属性的名称 仅用于界面显示
	 */
	public String name() default "";

	/**
	 * 属性的描述 仅用于界面显示
	 */
	public String desc() default "";

	/**
	 * 属性的默认值 仅用于界面显示
	 */
	public String defaultValue() default "";

	/**
	 * 属性的值应该符合的正则表达式规则 仅用于界面显示
	 */
	public String regex() default ".*";

	/**
	 * 属性的可选值列表 仅用于界面显示
	 */
	public String[] possibleValues() default {};

	/**
	 * 该属性是否由多行组成 仅用于界面显示
	 */
	public boolean multiline() default false;
}
