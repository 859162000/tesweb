package com.dc.tes.exception;

/**
 * 当License验证失败时将抛出此异常
 * 
 * @author lijic
 * 
 */
public class LicenseException extends RuntimeException {
	private static final long serialVersionUID = 2632705720242746856L;

	public LicenseException() {
		super();
	}

	public LicenseException(String message, Throwable cause) {
		super(message, cause);
	}

	public LicenseException(String message) {
		super(message);
	}

	public LicenseException(Throwable cause) {
		super(cause);
	}
}
