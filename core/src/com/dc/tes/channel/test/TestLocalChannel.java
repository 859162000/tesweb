package com.dc.tes.channel.test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import com.dc.tes.channel.localchannel.*;
import com.dc.tes.net.*;


public class TestLocalChannel {

	public static void REG(){
		Message msg = new Message(MessageType.REG);
		msg.put(MessageItem.AdapterReg.CHANNELNAME, "Receive");
		msg.put(MessageItem.AdapterReg.SIMTYPE, "tcp.s");
		AbstractLocalProcess lp = LocalProcessFactory.CreateProcess();
		Message[] b = lp.process(msg);
		System.out.println(new String(b[0].Export()));
	}

	public static void UNREG(){
		Message msg = new Message(MessageType.UNREG);
		msg.put(MessageItem.AdapterUnreg.CHANNELNAME, "Receive");
		AbstractLocalProcess lp = LocalProcessFactory.CreateProcess();
		Message[] b = lp.process(msg);
		System.out.println(new String(b[0].Export()));
	}
	

	public static void MSG(){
		Message msg = new Message(MessageType.MESSAGE);
		msg.put(MessageItem.AdapterMessage.CHANNELNAME, "Receive");
		msg.put(MessageItem.AdapterMessage.REQMESSAGE, "00000 <trancode>001</trancode>00000000000000");
		msg.put(MessageItem.AdapterMessage.PLOG, "002,0,100,200");
		System.out.println(new String(msg.Export()));
		AbstractLocalProcess lp = LocalProcessFactory.CreateProcess();
		Message[] b = lp.process(msg);
		System.out.println(new String(b[0].Export()));
	}
	public static void main(String args[]){
		//TestLocalChannel.MSG();
//		String s = "a,d,c,fd,fsd,";
//		String []b = s.split(",");
//		int i = b.length;
//		long fm = Runtime.getRuntime().freeMemory();
		Message msg = new Message(MessageType.UI);
		msg.put("CHANNELNAME", "UI");
		msg.put(MessageItem.UI.TRANCODE,"001");
		msg.put(MessageItem.UI.CASENAME,"");
		msg.put(MessageItem.UI.REQMESSAGE,"".getBytes());
		msg.put(MessageItem.UI.REQDATA,"");
		msg.put(MessageItem.UI.OP, 1);
		msg.put(MessageItem.UI.DESTCHANNEL,"Reply");
		String s = new String(msg.Export());
//		Runtime.getRuntime()
	}
}
