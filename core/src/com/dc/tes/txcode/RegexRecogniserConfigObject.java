package com.dc.tes.txcode;

import com.dc.tes.component.ConfigObject;
import com.dc.tes.component.tag.ComponentProperty;

/**
 * 基于正则表达式的交易码识别组件的配置对象
 * 
 * @author lijic
 * 
 */
public class RegexRecogniserConfigObject extends ConfigObject {
	private static final long serialVersionUID = 2673151726383344767L;

	@ComponentProperty
	public String regex;
	@ComponentProperty
	public int group;
	@ComponentProperty
	public int index;
	@ComponentProperty
	public String encoding;
}
