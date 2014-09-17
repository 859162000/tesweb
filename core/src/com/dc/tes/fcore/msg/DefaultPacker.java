package com.dc.tes.fcore.msg;

import java.io.InputStream;

import com.dc.tes.Core;
import com.dc.tes.component.BaseComponent;
import com.dc.tes.component.tag.ComponentClass;
import com.dc.tes.component.tag.ComponentType;
import com.dc.tes.data.DALFactory;
import com.dc.tes.data.model.MsgPacker;
import com.dc.tes.data.op.Op;
import com.dc.tes.dom.MsgDocument;
import com.dc.tes.msg.IContext;
import com.dc.tes.msg.MsgService;
import com.dc.tes.msg.pack.PackSpecification;
import com.dc.tes.util.RuntimeUtils;

/**
 * 默认组包组件 根据style进行组包
 * 
 * @author lijic
 * 
 */
@ComponentClass(type = ComponentType.Pack)
public class DefaultPacker extends BaseComponent<DefaultPackerConfigObject> implements IPacker {
	/**
	 * 组包样式
	 */
	private PackSpecification spec;

	@Override
	public void Initialize(Core core) {
		if (core.config.PACKCONFIG_FROM_LOCAL) {
			InputStream s = RuntimeUtils.OpenResource("conf/" + this.m_config.style + ".style.xml");

			this.spec = MsgService.LoadPackSpecification(s);
		} else {
			String style = DALFactory.GetBeanDAL(MsgPacker.class).Get(Op.EQ("stylename", this.m_config.style), Op.EQ("classname", this.getClass().getName())).getContent();

			this.spec = MsgService.LoadPackSpecification(style);
		}
	}

	@Override
	public byte[] Pack(MsgDocument doc, IContext context) {
		// 组包
		return MsgService.Pack(doc, this.spec, context);
	}
}
