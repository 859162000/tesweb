package com.dc.tes.exception;

/**
 * 当模拟器中不存在指定的通道时抛出此异常
 * 
 * @author lijic
 * 
 */
public class ChannelMismatchException extends RuntimeException {
	private static final long serialVersionUID = 5665959937389651732L;

	public ChannelMismatchException(String expect, String actual) {
		super("适配器与通道的类型不匹配：期望类型{" + expect + "} 实际适配器类型{" + actual + "}");
	}
}
