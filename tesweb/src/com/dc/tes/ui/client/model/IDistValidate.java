package com.dc.tes.ui.client.model;

import java.util.Map;

/**
 * 
 * 输入值唯一性验证接口
 * 
 * @author scckobe
 *
 */
public interface IDistValidate {
	/**
	 * 获得需要验证的表名
	 * 
	 * @return
	 */
	String GetTableName();
	/**
	 * 获得需要验证的字段名值对字典
	 * 
	 * @param validateValue 被验证的值
	 * @return Map<字段名称,字段值>
	 */
	Map<String,Object> GetFieldValuePair(String validateValue);
}
