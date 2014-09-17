package com.dc.tes.data.delay;

public class DelayTimeFactory {


	/**
	 * 创建计算延时时间的组建
	 * @param flag
	 * @return
	 */
	public static <T> T createDelay(int flag){
		switch(flag){
		case 0:
			return (T)new SysDelay();
		case 1:
			return (T)new TranDelay();
		case 2:
			return (T)new MixDelay();
		default:
			//类型找不到
			return null;
		}
	}
	/**
	 * 创建延时时间
	 * @param min 最小延时
	 * @param max 最大延时
	 * @return
	 */
	public static  <T> T createDelayTime(long min, long max){
		if(min == max){
			return (T) new FixDelayTime(min);
		}else if(max > min ){
			return (T) new RandomDelayTime(min,max);
		}else{
			//延时时间设置出错
			return null;
		}
	}
}
