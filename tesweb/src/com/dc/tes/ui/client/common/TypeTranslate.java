package com.dc.tes.ui.client.common;

/**
 * 类型转换类,主要用于
 * 数字与boolean类型 的转换
 * 数字与中文 的转换
 * 
 * @author scckobe
 *
 */
public class TypeTranslate {
	/**
	 * boolean类型 到 数字的转换
	 * @param value boolean值
	 * @return fasle:0 ; true:1 
	 */
	public static int BooleanToInt(boolean value)
	{
		if(value)
			return 1;
		else
			return 0;
	}
	
	/**
	 * 数字 到 boolean类型 的转换
	 * @param  value 数子值
	 * @return 0:fasle ; 1:true
	 */
	public static boolean IntToBoolean(int value)
	{
		return value == 1;
	}
	
	/**
	 * 返回用户的角色名称
	 * @param value 数据库存放的值
	 * @return 用户角色(0:系统管理员，1：测试人员， 2：项目管理员)
	 */
	public static String Int_Admin_CHS(int value)
	{
		if(value == 0)
			return "系统管理员";
		else if(value == 1)
			return "测试人员";
		else if(value == 2)
			return "项目管理员";
		
		return "";
	}
	
	/**
	 * "是否" 与 "0,1" 的转换
	 * @param value 数字
	 * @return 返回0，1所对应中文的"是否"
	 */
	public static String Int_Parse_CHS(int value)
	{
		if(value == 1)
			return "是";
		else if(value == 0)
			return "否";
		
		return "";
	}
}
