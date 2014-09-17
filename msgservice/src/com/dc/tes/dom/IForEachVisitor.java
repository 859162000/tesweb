package com.dc.tes.dom;

/**
 * 此接口定义了当对报文结构进行ForEach()时进行的操作 在遍历出每个报文元素时都会根据情况调用本接口中的方法
 * 
 * @author lijic
 * @see DefaultForEachVisitor, StackForEachVisitor
 * 
 */
public interface IForEachVisitor {
	/**
	 * 文档开始
	 * 
	 * @param doc
	 */
	public void DocStart(MsgDocument doc);

	/**
	 * 文档结束
	 * 
	 * @param doc
	 */
	public 	void DocEnd(MsgDocument doc);

	/**
	 * 结构开始
	 * 
	 * @param stru
	 */
	public 	void StruStart(MsgStruct stru);

	/**
	 * 结构结束
	 * 
	 * @param stru
	 */
	public 	void StruEnd(MsgStruct stru);

	/**
	 * 数组开始
	 * 
	 * @param array
	 */
	public 	void ArrayStart(MsgArray array);

	/**
	 * 数组结束
	 * 
	 * @param array
	 */
	public 	void ArrayEnd(MsgArray array);

	/**
	 * 报文域
	 * 
	 * @param field
	 */
	public 	void Field(MsgField field);
}
