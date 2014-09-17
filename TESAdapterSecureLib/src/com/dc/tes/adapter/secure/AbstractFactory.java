package com.dc.tes.adapter.secure;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 
 * 抽象工厂,处理加解密接口的实现类
 * 
 * @author 王春佳
 *
 */
public abstract class AbstractFactory {
	
	private static Log logger = LogFactory.getLog(AbstractFactory.class);
	
	/**
	 * 创建 抽象工厂实例
	 * @param className  实现工厂名称
	 * @return 抽象工厂实例
	 */
	public static AbstractFactory getInstance(String className){
		AbstractFactory abstractFactory = null;
		
		try {
			abstractFactory = (AbstractFactory) Class.forName(className).newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
			logger.error("创建类的实例失败:" + className);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			logger.error("访问错误:" + className);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			logger.error("未找到类:" + className);
		}
		
		return abstractFactory;
	};
	
	/**
	 * 获取 适配器 解密接口
	 * @return
	 */
	public abstract IDecryptAdapterSecure getDecryptAdapterSecure();
	
	/**
	 * 获取 适配器 加密接口
	 * @return
	 */
	public abstract IEncryptAdapterSecure getEncryptAdapterSecure();
}
