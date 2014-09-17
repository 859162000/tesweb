package com.dc.tes.exception;

/**
 * 下载异常
 * @author huangzx
 *
 */
public class DownLoadException extends RuntimeException {
	private static final long serialVersionUID = 3024259610006194134L;

	public DownLoadException() {
		super();
	}

	public DownLoadException(String message, Throwable cause) {
		super(message, cause);
	}

	public DownLoadException(String message) {
		super(message);
	}

	public DownLoadException(Throwable cause) {
		super(cause);
	}

	@Override
	public String getMessage()
	{
		return "error:" + super.getMessage();
	}
}
