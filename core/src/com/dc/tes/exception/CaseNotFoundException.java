package com.dc.tes.exception;

/**
 * 当指定的案例不存在时抛出此异常
 * 
 * @author lijic
 * 
 */
public class CaseNotFoundException extends RuntimeException {
	private static final long serialVersionUID = 2923185645592123543L;

	public CaseNotFoundException(String tranCode) {
		super("指定的" + tranCode + "交易下没有案例");
	}

	public CaseNotFoundException(String tranCode, String caseName) {
		super("指定的" + tranCode + "交易下的案例不存在：" + caseName);
	}
}
