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
import com.dc.tes.msg.unpack.UnpackSpecification;
import com.dc.tes.util.RuntimeUtils;

/**
 * 默认拆包组件 根据rule进行拆包
 * 
 * @author lijic
 * 
 */
@ComponentClass(type = ComponentType.Unpack)
public class DefaultUnpacker extends BaseComponent<DefaultUnpackerConfigObject> implements IUnpacker {
	/**
	 * 拆包规则
	 */
	private UnpackSpecification spec;

	@Override
	public void Initialize(Core core) {
		if (core.config.PACKCONFIG_FROM_LOCAL) {
			InputStream s = RuntimeUtils.OpenResource("conf/" + this.m_config.rule + ".rule.xml");

			this.spec = MsgService.LoadUnpackSpecification(s);
		} else {
			String rule = DALFactory.GetBeanDAL(MsgPacker.class).Get(Op.EQ("stylename", this.m_config.rule), Op.EQ("classname", this.getClass().getName())).getContent();

			this.spec = MsgService.LoadUnpackSpecification(rule);
		}
	}

	@Override
	public MsgDocument Unpack(byte[] bytes, MsgDocument template, IContext context) {
		// 拆包
		return MsgService.Unpack(bytes, template, this.spec, context);
	}
}
