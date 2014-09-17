package com.dc.tes.data.delay;

import java.util.Map;

import com.dc.tes.data.RuntimeTranDAL;

public class MixDelay <T extends DelayTime> implements IDelay {

	private IDelay sysdelay;
	private IDelay trandelay;

	@Override
	public void setDelayTime(long sysDelayMin, long sysDelayMax,
			Map<String, RuntimeTranDAL> dal) {
		// TODO Auto-generated method stub
		sysdelay = new SysDelay<T>();
		trandelay = new TranDelay<T>();
		sysdelay.setDelayTime(sysDelayMin, sysDelayMax, dal);
		trandelay.setDelayTime(sysDelayMin, sysDelayMax, dal);
	}

	@Override
	public long getDelayTime(String tranCode) {
		// TODO Auto-generated method stub
		return this.sysdelay.getDelayTime(tranCode) + 
				this.trandelay.getDelayTime(tranCode);
	}


}
