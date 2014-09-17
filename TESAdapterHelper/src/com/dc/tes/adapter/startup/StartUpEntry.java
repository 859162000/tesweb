package com.dc.tes.adapter.startup;

import com.dc.tes.adapter.startup.remote.StartUpRemote;

/**
 * 
 * 通信层"远程通道"启动入口
 * 
 * @author 王春佳
 *
 */
public class StartUpEntry {

	/**
	 * 通信层"远程通道"启动入口
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		//"远程通道"启动
		IStartUp localAdapter = new StartUpRemote();
		localAdapter.startUp();
	}

}
