package com.dc.tes.ui.util;

import java.util.List;

import com.dc.tes.ui.client.model.MsgAttribute;

public interface ISystemConfig {
	public byte[] getEmptyReqStru();

	public byte[] getEmptyRespStru();

	public List<MsgAttribute> getReqFieldAttributes();

	public List<MsgAttribute> getReqStructAttributes();

	public List<MsgAttribute> getRespFieldAttributes();

	public List<MsgAttribute> getRespStructAttributes();
	
	public String getSystemName();
	
	public int getIsClientSimu();
}
