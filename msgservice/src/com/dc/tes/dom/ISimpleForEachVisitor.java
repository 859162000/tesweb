package com.dc.tes.dom;

/**
 * 此接口定义了当对报文结构进行ForEach()时进行的操作
 * 
 * @author lijic
 * 
 */
public interface ISimpleForEachVisitor {
	/**
	 * 当对报文结构进行ForEachSimple()操作时，这个枚举定义了事件源
	 * 
	 * @author lijic
	 * 
	 */
	public enum ForEachSource {
		DocStart, DocEnd,

		StruStart, StruEnd,

		ArrayStart, ArrayEnd,

		Field,
	}

	/**
	 * 当调用ForEachSimple()方法时将进行的操作
	 * 
	 * @param source
	 *            调用方法的原因
	 * @param item
	 *            当前元素
	 */
	public void Visit(ForEachSource source, MsgItem item);
}
