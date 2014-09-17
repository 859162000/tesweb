package com.dc.tes.msg.util;

import org.apache.commons.lang.StringUtils;

import com.dc.tes.exception.MsgErr;
import com.dc.tes.exception.TESException;

/**
 * 表示报文结构中的属性值
 * <p>
 * 对String、int、boolean和byte[]上种类型进行了封装
 * </p>
 * <p>
 * 该类为不可变的，其中的值不可改变。若要改变报文结构中某个属性的值，可以将该属性的值设为一个新的Value对象
 * </p>
 * 
 * @author lijic
 * 
 */
public class Value {
	/**
	 *表示一个为空的Value对象 其value为一个空字符串
	 */
	public final static Value empty = new Value("");

	/**
	 * 该Value对象中保存的值
	 */
	public final Object value;
	/**
	 * 该Value对象中保存的值的整数形式 无法解释为整数时该值为Integer.MIN_VALUE
	 */
	public final int i;
	/**
	 * 该Value对象中保存的值的字节数组形式 无法解释为字节数组时该值为null
	 */
	public final byte[] bytes;
	/**
	 * 该Value对象中保存的值的字符串形式 无法解释为字节数组时该值为null
	 */
	public final String str;
	/**
	 * 该Value对象中保存的值的布尔形式 无法解释为布尔值时该值为false
	 */
	public final Boolean bool;

	/**
	 * 该Value对象中保存的值的长度
	 * <p>
	 * 对于整数是指十进制数字的个数，对于字节数组是指字节数，对于字符串是指字符串长度
	 * </p>
	 */
	public final int length;

	/**
	 * 使用整数初始化一个Value对象
	 * 
	 * @param value
	 *            整数值
	 */
	public Value(int value) {
		this.value = value;
		this.length = String.valueOf(value).length();

		this.i = value;
		this.bytes = null;
		this.str = String.valueOf(value);
		this.bool = value != 0;
	}

	/**
	 * 使用字节数组初始化一个Value对象
	 * 
	 * @param value
	 *            字节数组
	 */
	public Value(byte[] value) {
		if (value == null)
			throw new TESException(MsgErr.CreateNullValueObject);

		this.value = value;
		this.length = value.length;

		this.i = Integer.MIN_VALUE;
		this.bytes = value;
		this.str = null;
		this.bool = false;
	}

	/**
	 * 使用字符串初始化一个Value对象
	 * 
	 * @param value
	 *            字符串
	 */
	public Value(String value) {
		if (value == null)
			throw new TESException(MsgErr.CreateNullValueObject);

		this.value = value;
		this.length = value.length();

		Integer intValue;

		try {
			if (value.length() > 0 && StringUtils.isNumeric(value))
				intValue = Integer.parseInt(value);
			else if (value.length() > 1 && value.startsWith("-") && StringUtils.isNumeric(value.substring(1)))
				intValue = -Integer.parseInt(value.substring(1));
			else
				intValue = Integer.MIN_VALUE;
		} catch (NumberFormatException ex) {
			intValue = Integer.MIN_VALUE;
		}

		this.i = intValue;
		this.bytes = null;
		this.str = value;
		this.bool = value.equalsIgnoreCase("true");
	}

	/**
	 * 使用布尔值初始化一个Value对象
	 * 
	 * @param value
	 *            字节数组
	 */
	public Value(boolean value) {
		this.value = value;
		this.length = String.valueOf(value).length();

		this.i = value ? 1 : 0;
		this.bytes = null;
		this.str = String.valueOf(value);
		this.bool = value;
	}

	/**
	 * 使用另一个Value对象初始化一个Value对象
	 * 
	 * @param value
	 *            Value对象
	 */
	public Value(Value value) {
		if (value == null)
			throw new TESException(MsgErr.CreateNullValueObject);

		this.value = value.value;
		this.length = value.length;

		this.i = value.i;
		this.bytes = value.bytes;
		this.str = value.str;
		this.bool = value.bool;
	}

	@Override
	public String toString() {
		return this.value.toString();
	}

	///////////////////////////////////
	// 下面的代码是为了与旧代码兼容
	///////////////////////////////////
	/**
	 * use ".str" instead. will be delete at 2010/4/30
	 */
	@Deprecated
	public String getStr() {
		return this.str;
	}

	/**
	 * use ".bytes" instead. will be delete at 2010/4/30
	 */
	@Deprecated
	public byte[] getBytes() {
		return this.bytes;
	}

	/**
	 * use ".i" instead. will be delete at 2010/4/30
	 */
	@Deprecated
	public int getInt() {
		return this.i;
	}

	/**
	 * this enum will be delete at 2010/4/30
	 */
	@Deprecated
	public static enum ValueType {
		String, ByteArray, Int
	}

	/**
	 * this method will be delete at 2010/4/30
	 */
	@Deprecated
	public ValueType getType() {
		if (this.value instanceof byte[])
			return ValueType.ByteArray;
		if (this.value instanceof String)
			return ValueType.String;
		if (this.value instanceof Integer)
			return ValueType.Int;
		throw new UnsupportedOperationException();
	}

	/**
	 * use ".length" instead. will be delete at 2010/4/30
	 */
	@Deprecated
	public int length(Object o) {
		return this.length;
	}

	/**
	 * using "X == Value.empty" instead. will be delete at 2010/4/30
	 */
	public boolean isEmpty() {
		return this == Value.empty;
	}

}
