package com.dc.tes.exception;

/**
 * 当模拟器中不存在指定的通道时抛出此异常
 * 
 * @author lijic
 * 
 */
public class ChannelNotFoundException extends RuntimeException {
	private static final long serialVersionUID = -4366781454631733348L;

	public ChannelNotFoundException(String channel) {
		super("指定的通道[" + channel + "]不存在");
	}

	public ChannelNotFoundException(String channel, boolean isSenderChannel) {
		super("指定的通道[" + channel + "]不是期望的" + (isSenderChannel ? "发起端" : "接收端") + "通道");
	}
}
