package com.dc.tes.data;

import org.w3c.dom.Document;

import com.dc.tes.exception.CommonErr;
import com.dc.tes.exception.TESException;
import com.dc.tes.util.InstanceCreater;
import com.dc.tes.util.RuntimeUtils;
import com.dc.tes.util.XmlUtils;

/**
 * 数据源访问接口工厂类
 * 
 * @author huangzx
 * 
 */
public abstract class DALFactory {
	/**
	 * 实际的工厂实例
	 */
	private static DALFactory s_factory;

	/**
	 * 获取一个数据源访问接口 可以使用该接口对指定的bean类型进行增删改查操作
	 * 
	 * @param <T>
	 *            bean的类型
	 * @param beanCls
	 *            bean的类型
	 * @return 用于指定类型的bean的数据源访问接口
	 * @throws Exception
	 */
	public static <T> IDAL<T> GetBeanDAL(Class<T> beanCls) {
		if (beanCls == null)
			throw new TESException(CommonErr.Dal.GetBeanDALFail, "<null>");

		try {
			if (s_factory == null)
				initFactory();

			return s_factory.getBeanDAL(beanCls);
		} catch (Exception ex) {
			throw new TESException(CommonErr.Dal.GetBeanDALFail, beanCls.getName());
		}
	}

	/**
	 * 关闭数据层并做一些清理工作
	 */
	public static void Close() {
		if (s_factory == null)
			throw new TESException(CommonErr.Dal.FactoryNotInitialized);

		s_factory.close();
	}

	/**
	 * 工具函数 用于执行初始化操作
	 * 
	 * @throws Exception
	 */
	private static void initFactory() {
		try {
			Document doc = XmlUtils.LoadXml(RuntimeUtils.OpenResource("base.xml"));

			String dal = XmlUtils.SelectNodeText(doc, "//config/data");
			if (dal == null || dal.isEmpty()) {
				System.out.println("从base.xml读取//config/data失败，请检查配置！");
			}

			s_factory = InstanceCreater.CreateInstance("com.dc.tes.data." + dal + ".DalFactory");
		} catch (Exception ex) {
			throw new TESException(CommonErr.Dal.FactoryInitFail, ex);
		}
	}

	/**
	 * 在派生类中实现时 该函数用于获取一个指定bean类型的数据源接口实例
	 */
	protected abstract <T> IDAL<T> getBeanDAL(Class<T> beanCls) throws Exception;

	/**
	 * 在派生类中实现时 该函数用于关闭数据层并做一些清理工作
	 */
	protected abstract void close();
}
