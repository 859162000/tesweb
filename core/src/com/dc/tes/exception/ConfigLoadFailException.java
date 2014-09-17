package com.dc.tes.exception;

/**
 * 当读取基础配置失败时抛出此异常
 * 
 * @author lijic
 * 
 */
public class ConfigLoadFailException extends RuntimeException {
	private static final long serialVersionUID = 992794119250628904L;

	public ConfigLoadFailException() {
		super();
	}

	public ConfigLoadFailException(String message, Throwable cause) {
		super(message, cause);
	}

	public ConfigLoadFailException(String message) {
		super(message);
	}

	public ConfigLoadFailException(Throwable cause) {
		super(cause);
	}
}
