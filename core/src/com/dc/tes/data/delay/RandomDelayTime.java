package com.dc.tes.data.delay;

public class RandomDelayTime implements DelayTime {

	private long min;
	private long max;
	public long getDelayTime(){
		return  (long) (min+(Math.random()*(max-min)));
	}
	public RandomDelayTime(long min, long max){
		this.max = max;
		this.min = min;
	}
}
