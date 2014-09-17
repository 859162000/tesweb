package com.dc.tes.component;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * 组件配置对象的基类 所有组件的配置对象都应从此类继承
 * 
 * @author huangzx
 * 
 */
public class ConfigObject {
	/**
	 * 当前组件的配置名称
	 */
	public String configName;

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
}
