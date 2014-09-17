package com.dc.tes.fcore.msg;

import com.dc.tes.component.ConfigObject;
import com.dc.tes.component.tag.ComponentProperty;

/**
 * 默认拆包组件的配置对象
 * 
 * @author lijic
 * 
 */
public class DefaultUnpackerConfigObject extends ConfigObject {
	@ComponentProperty
	public String rule;
}
