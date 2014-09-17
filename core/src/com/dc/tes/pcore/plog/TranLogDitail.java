package com.dc.tes.pcore.plog;

public class TranLogDitail {
	//延时时间等记录的是总数
	private int totalnum = 0;
	private long coredelay = 0;
	private long delay = 0;
	private long usedtime = 0;
	
	
	public void Log(long coredelay,long delay, long usedtime){
		this.totalnum ++;
		//this.coredelay += coredelay;
		this.delay += delay;
		this.usedtime += usedtime;
	}
	
	public void reLog(){
		this.totalnum = 0;
		this.coredelay = 0;
		this.delay = 0;
		this.usedtime = 0;
	}
	
	public int getTotalnum() {
		return totalnum;
	}
	public void setTotalnum(int totalnum) {
		this.totalnum = totalnum;
	}
	public long getCoredelay() {
		return coredelay;
	}
	public void setCoredelay(long coredelay) {
		this.coredelay = coredelay;
	}
	public long getDelay() {
		return delay;
	}
	public void setDelay(long delay) {
		this.delay = delay;
	}
	public long getUsedtime() {
		return usedtime;
	}
	public void setUsedtime(long usedtime) {
		this.usedtime = usedtime;
	}
	

}
