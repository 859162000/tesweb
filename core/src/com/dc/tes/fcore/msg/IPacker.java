package com.dc.tes.fcore.msg;

import com.dc.tes.Core;
import com.dc.tes.dom.MsgDocument;
import com.dc.tes.msg.IContext;

/**
 * 组包接口
 * 
 * @author lijic
 * 
 */
public interface IPacker {
	/**
	 * 初始化
	 * 
	 * @param core
	 *            核心实例
	 */
	public void Initialize(Core core);

	/**
	 * 组包
	 * 
	 * @param doc
	 *            待组包的数据
	 * @param tranCode
	 *            上下文
	 * @return 组包出的字节流
	 */
	public byte[] Pack(MsgDocument doc, IContext context);
}
