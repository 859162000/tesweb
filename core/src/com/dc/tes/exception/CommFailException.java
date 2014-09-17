package com.dc.tes.exception;

/**
 * 当模拟器与外界的通讯失败时抛出此异常
 * 
 * @author lijic
 * 
 */
public class CommFailException extends RuntimeException {
	private static final long serialVersionUID = -4412237635497528639L;

	public CommFailException() {
		super();
	}

	public CommFailException(String message, Throwable cause) {
		super(message, cause);
	}

	public CommFailException(String message) {
		super(message);
	}

	public CommFailException(Throwable cause) {
		super(cause);
	}

}
