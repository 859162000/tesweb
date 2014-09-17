package com.dc.tes.data.xml;

import org.w3c.dom.Document;

import com.dc.tes.data.DALFactory;
import com.dc.tes.data.IDAL;
import com.dc.tes.exception.CommonErr;
import com.dc.tes.exception.TESException;
import com.dc.tes.util.InstanceCreater;
import com.dc.tes.util.RuntimeUtils;
import com.dc.tes.util.XmlUtils;

/**
 * 创建基于 XML 数据源的接口
 * 
 * @author huangzx
 * 
 */
public class DalFactory extends DALFactory {
	static String root;

	public DalFactory() {
		try {
			Document doc = XmlUtils.LoadXml(RuntimeUtils.OpenResource("base.xml"));
			root = XmlUtils.SelectNodeText(doc, "//config/root");
		} catch (Exception ex) {
			throw new TESException(CommonErr.Dal.FactoryInitFail, ex);
		}
	}

	@Override
	protected <T> IDAL<T> getBeanDAL(Class<T> beanCls) throws Exception {
		try {
			String className = "com.dc.tes.data.xml." + beanCls.getSimpleName() + "Dao";
			return InstanceCreater.CreateInstance(className);
		} catch (Exception ex) {
			throw new TESException(CommonErr.Dal.GetBeanDALFail, beanCls.getClass().getName(), ex);
		}
	}

	@Override
	protected void close() {
		// 目前的xml数据源在关闭时不需任何处理
	}
}
