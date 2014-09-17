package com.dc.tes.txcode;

import com.dc.tes.component.ConfigObject;
import com.dc.tes.component.tag.ComponentProperty;

/**
 * 基于绝对位置的交易码识别组件的配置对象
 * 
 * @author lijic
 * 
 */
public class PosRecogniserConfigObject extends ConfigObject {
	private static final long serialVersionUID = 7750275620713311163L;

	@ComponentProperty
	public int start;
	@ComponentProperty
	public int length;
	@ComponentProperty
	public String encoding;
}
