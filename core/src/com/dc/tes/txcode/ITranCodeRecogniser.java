package com.dc.tes.txcode;

/**
 * 交易识别组件接口
 * 
 * @author lijic
 * 
 */
public interface ITranCodeRecogniser {
	/**
	 * 从给定的字节流中解析出交易码
	 * 
	 * @param bytes
	 *            报文字节流
	 * @return 解析出的交易码
	 * @throws Exception 
	 */
	public String Recognise(byte[] bytes) throws Exception;
}
