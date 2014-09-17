package com.dc.tes.data.delay;

import java.util.Map;

import com.dc.tes.data.RuntimeTranDAL;

public class SysDelay <T extends DelayTime> implements IDelay {

	private T delaytime;
	@Override
	public long getDelayTime(String tranCode) {
		// TODO Auto-generated method stub
		return this.delaytime.getDelayTime();
	}

	@Override
	public void setDelayTime(long sysDelayMin, long sysDelayMax,
			Map<String, RuntimeTranDAL> dal) {
		// TODO Auto-generated method stub
		this.delaytime =  DelayTimeFactory.createDelayTime(sysDelayMin, sysDelayMax);
	}

}
