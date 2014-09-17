package com.dc.tes.component;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;

import org.apache.commons.lang.NullArgumentException;

import com.dc.tes.component.tag.ComponentClass;
import com.dc.tes.component.tag.ComponentProperty;
import com.dc.tes.exception.CommonErr;
import com.dc.tes.exception.TESException;
import com.dc.tes.util.InstanceCreater;
import com.dc.tes.util.RuntimeUtils;

/**
 * 组件工厂类 用于获取一个指定类型的组件
 * 
 * @author huangzx
 */
public class ComponentFactory {
	/**
	 * 创建一个指定类型的组件
	 * 
	 * @param <T>
	 *            组件要返回的类型
	 * @param className
	 *            组件的配置名称
	 * @param args
	 *            组件的初始化参数
	 * @return 返回一个指定类型的组件实例
	 */
	@SuppressWarnings("unchecked")
	public static <T> T CreateComponent(IComponentConfigLoader da, String configName, Object... args) {
		if (configName == null || configName.length() == 0)
			throw new NullArgumentException("configName");

		String className = null; // 组件类的类型名称
		Class<?> cls; // 组件类的类型
		T instance; // 组件类的实例

		try {
			// 获取组件的类型
			className = da.getComponentClassName(configName);
			cls = Class.forName(className);

			// 如果组件未使用ComponentClass标记则抛出异常
			ComponentClass _component = cls.getAnnotation(ComponentClass.class);
			if (_component == null)
				throw new TESException(CommonErr.Instantiation.ComponentTagRequired, cls.getName());

			// 获取该组件的一个实例
			instance = (T) InstanceCreater.CreateInstance(className, args);

			// 如果该组件未继承自BaseComponent则直接返回
			if (!(instance instanceof BaseComponent))
				return (T) instance;

			// 创建该组件的配置对象
			Class<? extends ConfigObject> configObjectClass = (Class<? extends ConfigObject>) ((ParameterizedType) cls.getGenericSuperclass()).getActualTypeArguments()[0];
			ConfigObject configObj = configObjectClass.newInstance();
			configObj.configName = configName;

			// 循环填充该配置对象
			for (Field f : configObjectClass.getFields()) {
				ComponentProperty _property = f.getAnnotation(ComponentProperty.class);

				// 如果该域未使用ComponentConfigProperty标记则略过
				if (_property != null) {
					String n = _property.configName().length() == 0 ? f.getName() : _property.configName();

					// 如果该域是一个数组
					if (f.getType().isArray()) {
						// 从数据接口中获取配置信息填充到配置对象中						
						String[] v = da.getPropertyConfigs(configName, n);

						try {
							f.set(configObj, RuntimeUtils.FromString(v, f.getType().getComponentType()));
						} catch (Exception ex) {
							throw new TESException(CommonErr.Instantiation.FillComponentPropertyFail, n, ex);
						}
					} else {
						// 从数据接口中获取配置信息填充到配置对象中
						String v = da.getPropertyConfig(configName, n);
						try {
							f.set(configObj, RuntimeUtils.FromString(v, f.getType()));
						} catch (Exception ex) {
							throw new TESException(CommonErr.Instantiation.FillComponentPropertyFail, n, ex);
						}
					}
				}
			}

			// 把该配置对象设过去		
			((BaseComponent) instance).m_config = configObj;

			// 初始化组件类
			((BaseComponent) instance).Initialize();

			// 返回该组件
			return instance;
		} catch (Exception ex) {
			throw new TESException(CommonErr.Instantiation.CreateComponentFail, configName, ex);
		}
	}
}
