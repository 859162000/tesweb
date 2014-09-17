package com.dc.tes.exception;

import com.dc.tes.TransactionMode;

/**
 * 当指定的交易不存在时抛出此异常
 * 
 * @author lijic
 * 
 */
public class TranNotFoundException extends RuntimeException {
	private static final long serialVersionUID = 2923185645592123543L;

	public TranNotFoundException(String tranCode, TransactionMode mode) {
		super("指定的" + mode + "交易不存在或交易下案例数据为空,交易码为：" + tranCode);
	}
}
