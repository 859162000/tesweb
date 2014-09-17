package com.dc.tes.data.delay;

import java.util.LinkedHashMap;
import java.util.Map;

import com.dc.tes.data.RuntimeTranDAL;

public class TranDelay  <T extends DelayTime>implements IDelay{

	protected Map<String, T> delay ;
	
	@Override
	public void setDelayTime(long sysDelayMin, long sysDelayMax,
			Map<String, RuntimeTranDAL> dal) {
		// TODO Auto-generated method stub
		delay =  new LinkedHashMap<String, T>();
		for(String tranCode : dal.keySet()){
			RuntimeTranDAL temp = dal.get(tranCode);
			T delaytime = DelayTimeFactory.createDelayTime(temp.getMinDelay(),temp.getMaxDelay());
			this.delay.put(tranCode, delaytime);
		}
	}

	@Override
	public long getDelayTime(String tranCode) {
		// TODO Auto-generated method stub
		return this.delay.get(tranCode).getDelayTime();
	}

}
