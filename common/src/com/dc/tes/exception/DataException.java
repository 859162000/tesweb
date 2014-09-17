package com.dc.tes.exception;

/**
 * 当数据层读写失败时抛出此异常
 * 
 * @author huangzx
 * 
 */
public class DataException extends RuntimeException {
	private static final long serialVersionUID = 5405088260942540282L;

	public DataException() {
		super();
	}

	public DataException(String message, Throwable cause) {
		super(message, cause);
	}

	public DataException(String message) {
		super(message);
	}

	public DataException(Throwable cause) {
		super(cause);
	}
}
