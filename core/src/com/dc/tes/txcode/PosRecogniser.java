package com.dc.tes.txcode;

import java.nio.charset.Charset;

import com.dc.tes.component.BaseComponent;
import com.dc.tes.component.tag.ComponentClass;
import com.dc.tes.component.tag.ComponentType;

/**
 * 基于绝对位置的交易码识别组件
 * 
 * @author lijic
 * 
 */
@ComponentClass(type = ComponentType.TXCode)
public class PosRecogniser extends BaseComponent<PosRecogniserConfigObject> implements ITranCodeRecogniser {
	private Charset m_encoding;

	@Override
	protected void Initialize() throws Exception {
		this.m_encoding = Charset.forName(this.m_config.encoding);
	}
	
	@Override
	public String Recognise(byte[] bytes) throws Exception {
		byte[] txcode = new byte[this.m_config.length];
		System.arraycopy(bytes, this.m_config.start, txcode, 0, this.m_config.length);
		return new String(txcode, this.m_encoding);
	}
}
