package com.dc.tes.channel.localchannel;

public class LocalProcessFactory {

	public static AbstractLocalProcess CreateProcess(){
		return new LocalProcess();
	}
	
	public static void main(String[]args){
		AbstractLocalProcess lp = LocalProcessFactory.CreateProcess();
	}
}
