package com.dc.tes.txcode;

import com.dc.tes.component.ConfigObject;
import com.dc.tes.component.tag.ComponentProperty;

/**
 * 基于脚本的交易码识别组件的配置对象
 * 
 * @author lijic
 * 
 */
public class ScriptRecogniserConfigObject extends ConfigObject {
	private static final long serialVersionUID = 7750275620713311163L;

	@ComponentProperty(multiline = true)
	public String imports;
	@ComponentProperty(multiline = true)
	public String code;
}
