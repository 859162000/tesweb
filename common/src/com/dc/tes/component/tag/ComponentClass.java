package com.dc.tes.component.tag;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标明某个类是模拟器组件类
 * 
 * @author huangzx
 * 
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ComponentClass {
	/**
	 * 组件的类型
	 */
	public ComponentType type();

	/**
	 * 组件的名称 仅用于界面显示 可以为空 为空时默认为该类的类名
	 */
	public String name() default "";

	/**
	 * 组件的描述 仅用于界面显示 可以为空 为空时默认为该类的类名
	 */
	public String desc() default "";

}
