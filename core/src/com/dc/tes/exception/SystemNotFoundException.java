package com.dc.tes.exception;

/**
 * 当指定的被模拟系统不存在时抛出此异常
 * 
 * @author lijic
 * 
 */
public class SystemNotFoundException extends RuntimeException {
	private static final long serialVersionUID = 2923185645592123543L;

	public SystemNotFoundException(String instanceName) {
		super("指定的被模拟系统不存在：" + instanceName);
	}
}
