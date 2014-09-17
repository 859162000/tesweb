package com.dc.tes.component;

import java.util.List;

import com.dc.tes.component.tag.ComponentType;
import com.dc.tes.data.op.EQ;

/**
 * 
 * @author huangzx
 * 
 */
public interface IComponentConfigLoader {
	/**
	 * 获取被当前模拟器使用的指定类型的组件类的配置名称列表
	 * 
	 * @param type
	 *            组件类型
	 * @param conditions
	 *            组件的限定条件
	 * @return 当前支持的指定类型的组件类的配置名称列表
	 */
	public List<String> getComponentConfigNames(ComponentType type, EQ... conditions);

	/**
	 * 获取一个指定的组件的类型名称
	 * 
	 * @param conditions
	 * 
	 * @param entityName
	 *            组件的配置名称
	 * @return 该组件的类型
	 */
	public String getComponentClassName(String configName);

	/**
	 * 获取组件配置信息
	 * 
	 * @param entityName
	 *            实体名称
	 * @param propName
	 *            属性名称
	 * @return 指定属性的配置数据
	 */
	public String getPropertyConfig(String entityName, String propName);

	/**
	 * 获取组件配置信息的数组
	 * 
	 * @param configName
	 *            实体名称
	 * @param n
	 *            属性名称
	 * @return 指定属性的配置数据数组
	 */
	public String[] getPropertyConfigs(String configName, String n);
}
