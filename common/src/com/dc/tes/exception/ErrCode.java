package com.dc.tes.exception;

/**
 * 错误码列表
 * 
 * @author huangzx
 * 
 */
public class ErrCode {
	/**
	 * 错误码 该值是一个长度为6的字符串
	 */
	public final String code;
	/**
	 * 错误描述
	 */
	public final String desc;

	/**
	 * 定义一个错误码
	 * 
	 * @param code
	 *            6位的错误码
	 * @param desc
	 *            错误说明
	 */
	ErrCode(String code, String desc) {
		this.code = code;
		this.desc = desc;
	}

	/**
	 * 未预料到的错误
	 */
	public static final ErrCode UNKNOWN = new ErrCode("UNKNOW", "未预料到的错误");
}
