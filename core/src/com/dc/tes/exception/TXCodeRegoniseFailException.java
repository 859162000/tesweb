package com.dc.tes.exception;

/**
 * 当识别交易码失败时抛出此异常
 * 
 * @author lijic
 * 
 */
public class TXCodeRegoniseFailException extends RuntimeException {
	private static final long serialVersionUID = -4882161984208010121L;

	public TXCodeRegoniseFailException() {
		super();
	}

	public TXCodeRegoniseFailException(String message, Throwable cause) {
		super(message, cause);
	}

	public TXCodeRegoniseFailException(String message) {
		super(message);
	}

	public TXCodeRegoniseFailException(Throwable cause) {
		super(cause);
	}
}
