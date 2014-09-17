package com.dc.tes.ui.util;

import java.util.ArrayList;
import java.util.List;

import com.dc.tes.ui.client.model.MsgAttribute;


public class DefaultConfig implements ISystemConfig {
	
	@Override
	public byte[] getEmptyReqStru() {
		return "<?xml version=\"1.0\" encoding=\"gb2312\"?><msg/>".getBytes();
	}

	@Override
	public byte[] getEmptyRespStru() {
		return "<?xml version=\"1.0\" encoding=\"gb2312\"?><msg/>".getBytes();
	}

	@Override
	public List<MsgAttribute> getReqFieldAttributes() {
		ArrayList<MsgAttribute> atts = new ArrayList<MsgAttribute>();

		atts.add(new MsgAttribute("name", "名称", "", "", "100"));
		atts.add(new MsgAttribute("desc", "描述", "", "", "100"));

		return atts;
	}

	@Override
	public List<MsgAttribute> getReqStructAttributes() {
		ArrayList<MsgAttribute> atts = new ArrayList<MsgAttribute>();

		atts.add(new MsgAttribute("name", "名称", "", "", "100"));
		atts.add(new MsgAttribute("desc", "描述", "", "", "100"));

		return atts;
	}

	@Override
	public List<MsgAttribute> getRespFieldAttributes() {
		ArrayList<MsgAttribute> atts = new ArrayList<MsgAttribute>();

		atts.add(new MsgAttribute("name", "名称", "", "", "100"));
		atts.add(new MsgAttribute("desc", "描述", "", "", "100"));

		return atts;
	}

	@Override
	public List<MsgAttribute> getRespStructAttributes() {
		ArrayList<MsgAttribute> atts = new ArrayList<MsgAttribute>();

		atts.add(new MsgAttribute("name", "名称", "", "", "100"));
		atts.add(new MsgAttribute("desc", "描述", "", "", "100"));

		return atts;
	}

	@Override
	public int getIsClientSimu() {
		return 1;
	}

	@Override
	public String getSystemName() {
		return "默认系统配置";
	}
}
