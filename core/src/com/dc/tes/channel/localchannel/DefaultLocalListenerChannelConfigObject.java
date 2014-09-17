package com.dc.tes.channel.localchannel;

import com.dc.tes.component.ConfigObject;
import com.dc.tes.component.tag.ComponentProperty;

/**
 * 默认本地接收端通道的配置对象
 * 
 * @author lijic
 */
public class DefaultLocalListenerChannelConfigObject extends ConfigObject {
	@ComponentProperty
	public String adapter;
	@ComponentProperty(multiline = true)
	public String config;
}
