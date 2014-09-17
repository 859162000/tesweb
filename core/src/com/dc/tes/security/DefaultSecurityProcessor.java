package com.dc.tes.security;

import com.dc.tes.Core;
import com.dc.tes.component.BaseComponent;
import com.dc.tes.component.ConfigObject;
import com.dc.tes.component.tag.ComponentClass;
import com.dc.tes.component.tag.ComponentType;
import com.dc.tes.dom.MsgDocument;

/**
 * 默认的安全处理组件 该组件不做任何安全处理
 * 
 * @author lijic
 * 
 */
@ComponentClass(type = ComponentType.Security)
public class DefaultSecurityProcessor extends BaseComponent<ConfigObject> implements ISecurityProcessor {

	@Override
	public byte[] DecryptAll(Core core, byte[] msg) {
		// 将报文原样返回 不做任何加密处理
		return msg;
	}

	@Override
	public byte[] DecryptAll(Core core, byte[] msg, String tranCode) {
		// 将报文原样返回 不做任何加密处理
		return msg;
	}

	@Override
	public MsgDocument DecryptData(Core core, byte[] msg, String tranCode, MsgDocument doc) {
		// 将报文原样返回 不做任何加密处理
		return doc;
	}

	@Override
	public MsgDocument EncryptData(Core core, String tranCode, MsgDocument doc) {
		// 将报文原样返回 不做任何加密处理
		return doc;
	}

	@Override
	public byte[] EncryptAll(Core core, String tranCode, MsgDocument doc, byte[] msg) {
		// 将报文原样返回 不做任何加密处理
		return msg;
	}

}
