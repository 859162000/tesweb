package com.dc.tes.channel.adapter;

import com.dc.tes.channel.AdapterConfigObject;
import com.dc.tes.component.tag.ComponentProperty;
import com.dc.tes.util.RuntimeUtils;

/**
 * 发起端通道的默认配置对象
 * 
 * @author lijic
 */
public class DefaultSenderConfigObject extends AdapterConfigObject {
	@ComponentProperty(multiline = true)
	public String config;

	@Override
	public byte[] Export() {
		return this.config.getBytes(RuntimeUtils.utf8);
	}
}
