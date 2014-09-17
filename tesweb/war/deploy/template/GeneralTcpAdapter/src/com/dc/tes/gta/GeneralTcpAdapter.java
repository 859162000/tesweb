package com.dc.tes.gta;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 通用TCP适配器
 * @author Conan
 *
 */
public class GeneralTcpAdapter {
	/**
	 * 日志对象
	 */
	public static Log log = LogFactory.getLog(GeneralTcpAdapter.class);
	
	/**
	 * 主函数
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			TcpServer4Adapter t4a = new TcpServer4Adapter();
			t4a.start();
		} catch (Exception e) {
			log.error(e.getMessage());
		}
	}

}
