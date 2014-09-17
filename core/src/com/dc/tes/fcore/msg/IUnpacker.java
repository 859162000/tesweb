package com.dc.tes.fcore.msg;

import com.dc.tes.Core;
import com.dc.tes.dom.MsgDocument;
import com.dc.tes.msg.IContext;

/**
 * 拆包接口
 * 
 * @author lijic
 * 
 */
public interface IUnpacker {
	/**
	 * 初始化
	 * 
	 * @param core
	 *            核心实例
	 */
	public void Initialize(Core core);

	/**
	 * 拆包
	 * 
	 * @param bytes
	 *            待拆包的字节流
	 * @param template
	 *            拆包模板
	 * @param context
	 *            上下文
	 * @return 拆包出的数据
	 */
	public MsgDocument Unpack(byte[] bytes, MsgDocument template, IContext context);
}
