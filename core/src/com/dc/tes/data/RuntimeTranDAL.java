package com.dc.tes.data;

import java.util.List;

import com.dc.tes.data.model.Case;

public class RuntimeTranDAL {
	private byte[] casedal;
	private long minDelay;
	private long maxDelay;

	public RuntimeTranDAL(List<Case> caselist,long minDelay, long maxDelay){
		this.minDelay = minDelay;
		this.maxDelay = maxDelay;
		for (Case c : caselist){
			if(c.getIsdefault()==0){
				this.casedal = c.getRequestMsg();
			}
		}
		if(this.casedal==null){
			this.casedal = caselist.get(0).getRequestMsg();
		}
	}
	
	public byte[] getCasedal() {
		return casedal;
	}

	public void setCasedal(byte[] casedal) {
		this.casedal = casedal;
	}

	public long getMinDelay() {
		return minDelay;
	}

	public void setMinDelay(long minDelay) {
		this.minDelay = minDelay;
	}

	public long getMaxDelay() {
		return maxDelay;
	}

	public void setMaxDelay(long maxDelay) {
		this.maxDelay = maxDelay;
	}

}
