package com.dc.tes.adapter.lib;

import java.net.Socket;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.dc.tes.adapter.IRequestAdapter;
import com.dc.tes.adapter.remote.DefaultRequestAdapterServerWorker;


public class MQRecorderLib extends Thread {

	public final static Log log = LogFactory.getLog(TCPRecorderLib.class);
	
	protected static IRequestAdapter m_adapterPluginInstance = null;
	

 


	
	

	
	public MQRecorderLib(Properties props, IRequestAdapter api) {
		super();
		m_adapterPluginInstance = api;
		Init(props);
	}
	
	public void run() {
		String adptyp = m_adapterPluginInstance.AdapterType();
		if (!adptyp.equals("mq.c")) { //作为发送端的辅助工具，替发送端接收请求报文
			return;
		}
		
		try {
			new DefaultRequestAdapterServerWorker(null, null, m_adapterPluginInstance, true).start();
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}

	
	/*
	 * 检查配置信息是否完整
	 * 
	 * 
	 */
	public boolean Init(Properties props) {
	
		return true;
	}
	
}