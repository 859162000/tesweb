package com.dc.tes.monitor.data;

public class Config {

	//log最大数目
	public static  int MAX_LOG_NUM = 10;
	//传送给界面的最大数据量
	public static  int MAX_SEND_SIZE = 2*1024*1024;
	//监控服务侦听端口
	public static  int port = 8889;
	//监听线程开关
	public static boolean run = true;
	
	public static int outtime = 60*60*1000;

	public static int getMAX_LOG_NUM() {
		return MAX_LOG_NUM;
	}

	public static void setMAX_LOG_NUM(int max_log_num) {
		MAX_LOG_NUM = max_log_num;
	}

	public static int getMAX_SEND_SIZE() {
		return MAX_SEND_SIZE;
	}

	public static void setMAX_SEND_SIZE(int max_send_size) {
		MAX_SEND_SIZE = max_send_size;
	}

	public static int getPort() {
		return port;
	}

	public static void setPort(int port) {
		Config.port = port;
	}

	
	
}
