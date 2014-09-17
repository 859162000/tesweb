package com.dc.tes.txcode;

import com.dc.tes.component.ConfigObject;
import com.dc.tes.component.tag.ComponentProperty;

/**
 * 基于左右边界的交易码识别组件的配置对象
 * 
 * @author lijic
 * 
 */
public class BorderRecogniserConfigObject extends ConfigObject {
	private static final long serialVersionUID = 7750275620713311163L;

	/**
	 * 左边界
	 */
	@ComponentProperty(desc = "左边界")
	public String left;
	/**
	 * 右边界
	 */
	@ComponentProperty(desc = "右边界")
	public String right;
	/**
	 * 出现次数
	 */
	@ComponentProperty(desc = "出现次数")
	public int index;
	/**
	 * 编码
	 */
	@ComponentProperty(desc = "编码")
	public String encoding;
}
