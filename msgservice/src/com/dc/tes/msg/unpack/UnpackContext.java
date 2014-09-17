package com.dc.tes.msg.unpack;

import com.dc.tes.dom.MsgItem;
import com.dc.tes.msg.IContext;

/**
 * 拆包上下文
 * 
 * @author lijic
 * 
 */
public class UnpackContext {
	/**
	 * 拆包规则定义
	 */
	public final UnpackSpecification spec;
	/**
	 * 当前参数
	 */
	public final Param param;
	/**
	 * 全局上下文
	 */
	public final IContext context;

	/**
	 * 模板
	 */
	public MsgItem template;

	/**
	 * 报文字节流
	 */
	public final byte[] bytes;
	/**
	 * 当前位置{获取或设置}
	 */
	public int pos;
	/**
	 * 当前段的长度{获取或设置}
	 */
	public int length;

	/**
	 * 初始化一个拆包上下文对象
	 */
	UnpackContext(Param param, MsgItem template, byte[] bytes, int pos, int length, UnpackSpecification spec, IContext context) {
		this.spec = spec;
		this.context = context;

		this.template = template;

		this.param = param;

		this.bytes = bytes;
		this.pos = pos;
		this.length = length;
	}
}
