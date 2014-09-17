package com.dc.tes.dom;

import com.dc.tes.util.type.StackEx;

/**
 * 对IForEachVisitor的一个实现 维护一个m_containers内部栈对象
 * 
 * @author lijic
 * 
 */
public class StackForEachVisitor extends DefaultForEachVisitor {
	/**
	 * 容器栈 记录了当前节点的各个父节点
	 */
	protected StackEx<MsgContainer> m_containers = new StackEx<MsgContainer>();

	@Override
	public void DocStart(MsgDocument doc) {
		this.m_containers.push(doc);
	}

	@Override
	public void DocEnd(MsgDocument doc) {
		// 不进行任何操作 让栈里留有至少一个对象 以保证与旧代码的兼容性
	}

	@Override
	public void StruStart(MsgStruct stru) {
		this.m_containers.push(stru);
	}

	@Override
	public void StruEnd(MsgStruct stru) {
		this.m_containers.pop();
	}

	@Override
	public void ArrayStart(MsgArray array) {
		this.m_containers.push(array);
	}

	@Override
	public void ArrayEnd(MsgArray array) {
		this.m_containers.pop();
	}
}
