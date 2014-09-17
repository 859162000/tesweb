package com.dc.tes.net.jre14;

import java.io.UnsupportedEncodingException;

import junit.framework.TestCase;

public class TestMessage extends TestCase {

	public void testMessage(){
		Message m = new ReplyMessage(MessageType.MESSAGE);
		
		try {
			//m.put("CHANNELNAME", "ReceiveChannel1");
			//m.put("REQMESSAGE", "00001");
			
			System.out.println("--" + new String(m.Export()));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
