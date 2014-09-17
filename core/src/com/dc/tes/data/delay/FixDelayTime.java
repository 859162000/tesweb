package com.dc.tes.data.delay;

public class FixDelayTime implements DelayTime{

	private long delay;
	public long getDelayTime(){
		return this.delay;
	}
	public FixDelayTime(long delay){
		this.delay = delay;
	}
}
