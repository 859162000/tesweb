package com.dc.tes.dom;

/**
 * 对IForEachVisitor的一个默认实现 不进行任何操作
 * 
 * @author lijic
 * 
 */
public class DefaultForEachVisitor implements IForEachVisitor {

	@Override
	public void ArrayEnd(MsgArray array) {		
	}

	@Override
	public void ArrayStart(MsgArray array) {
	}

	@Override
	public void DocEnd(MsgDocument doc) {
	}

	@Override
	public void DocStart(MsgDocument doc) {
	}

	@Override
	public void Field(MsgField field) {
	}

	@Override
	public void StruEnd(MsgStruct stru) {
	}

	@Override
	public void StruStart(MsgStruct stru) {
	}
}
