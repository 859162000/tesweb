package com.dc.tes.util.type;

import java.util.Stack;

/**
 * 对Stack进行的扩展，支持pre()操作和bottom()操作
 * 
 * @author huangzx
 * 
 * @param <E>
 *            栈中元素的类型
 */
public class StackEx<E> extends Stack<E> {
	private static final long serialVersionUID = 809373194996247784L;

	/**
	 * 取栈顶元素之下的元素
	 * 
	 * @return 返回栈顶元素之下的元素 如果不存在这个元素则返回null
	 */
	public E pre() {
		return this.size() < 2 ? null : this.elementAt(this.size() - 2);
	}

	/**
	 * 取栈底元素
	 * 
	 * @return 返回栈底元素 如果栈为空则返回null
	 */
	public E bottom() {
		return this.size() == 0 ? null : this.elementAt(0);
	}
}
