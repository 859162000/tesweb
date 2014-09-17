package com.dc.tes.channel.adapter.http;

import com.dc.tes.channel.adapter.tcp.SenderConfigObject;
import com.dc.tes.channel.localchannel.AbstractLocalSenderChannel;
import com.dc.tes.component.tag.ComponentClass;
import com.dc.tes.component.tag.ComponentType;

@ComponentClass(type = ComponentType.Channel)
public class LocalSender extends AbstractLocalSenderChannel<SenderConfigObject> {

}
