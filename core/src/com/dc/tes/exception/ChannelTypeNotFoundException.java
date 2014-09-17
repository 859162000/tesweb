package com.dc.tes.exception;

/**
 * 当脚本中输入不存在指定的通道类型时抛出此异常
 * 
 * @author adey
 * 
 */
public class ChannelTypeNotFoundException extends RuntimeException{	
	private static final long serialVersionUID = 1L;
	
	public ChannelTypeNotFoundException(String type){
		super("输入的通道类型｛" + type + "｝有误，请输入Send或Recv");
	}

}
