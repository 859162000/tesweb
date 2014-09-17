package com.dc.tes.fcore.script;

import com.dc.tes.msg.IContext;

/**
 * 拆组包上下文
 * 
 * @author lijic
 * 
 */
public class MsgContext implements IContext {
	private String m_tranCode;
	
	/**
	 * 当前组包数据的位置，为延迟组包提供回退位置
	 */
	public int posOfMsgDocument;
	
	public int posOfMsgContainer;

	public MsgContext(String tranCode) {
		this.m_tranCode = tranCode;
	}

	@Override
	public String getTranCode() {
		return this.m_tranCode;
	}

	public int getPosition() {
		// TODO Auto-generated method stub
		return posOfMsgDocument;
	}
}
