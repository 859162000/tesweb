package com.dc.tes.data;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import com.dc.tes.Config;
import com.dc.tes.component.tag.ComponentType;
import com.dc.tes.data.model.SysType;
import com.dc.tes.data.op.EQ;
import com.dc.tes.data.op.Op;
import com.dc.tes.exception.CoreErr;
import com.dc.tes.exception.TESException;
import com.dc.tes.util.RuntimeUtils;
import com.dc.tes.util.XmlUtils;

/**
 * 运行时数据源访问接口基类 该基类提供了对基础配置的访问功能
 * 
 * @author lijic
 * 
 */
public abstract class BaseRuntimeDAL implements IRuntimeDAL {
	/**
	 * 当base.xml中的某个组件没有提供name时的默认前缀 此时该组件的名称为C_INTERNAL_PREFIX+组件的类型
	 */
	private static final String C_INTERNAL_PREFIX = "$$tes_internal$$";
	/**
	 * 基础配置
	 */
	private Document baseXml;

	/**
	 * 被模拟系统名称
	 */
	protected final String instanceName;
	/**
	 * 被模拟系统id
	 */
	protected final String instanceId;
	/**
	 * 被模拟系统实例
	 */
	protected final SysType instance;

	/**
	 * 核心基础配置
	 */
	protected final Config config;

	/**
	 *初始化数据访问层
	 * 
	 * @param instanceName
	 *            核心名称
	 * @param config
	 *            核心基础配置
	 * @throws Exception
	 */
	public BaseRuntimeDAL(String instanceName, Config config) throws Exception {
		try {
			// 初始化系统实例
			this.instanceName = instanceName;
			this.instance = DALFactory.GetBeanDAL(SysType.class).Get(Op.EQ("systemName", instanceName));
			if (this.instance == null)
				throw new TESException(CoreErr.SystemNotFound, instanceName);
			this.instanceId = this.instance.getSystemId();

			// 初始化基础配置
			this.config = config;
			if (config.CONFIG_FROM_BASEXML)
				baseXml = XmlUtils.LoadXml(RuntimeUtils.OpenResource("base.xml"));
			else
				baseXml = XmlUtils.LoadXml(this.instance.getBasecfg());
		} catch (Exception ex) {
			throw new TESException(CoreErr.InitDalFail, ex);
		}
	}

	// 组件配置相关

	@Override
	public List<String> getComponentConfigNames(ComponentType type, EQ... conditions) {
		try {
			String xpath = "//config/" + type.toString().toLowerCase();
			StringBuffer postfix = new StringBuffer();
			for (EQ eq : conditions)
				postfix.append("[@").append(eq.n).append("='").append(eq.v).append("']");

			List<Node> nodes = XmlUtils.SelectNodes(baseXml, xpath + postfix.toString());
			if (nodes.size() == 0)
				nodes = XmlUtils.SelectNodes(baseXml, xpath);

			ArrayList<String> configNames = new ArrayList<String>();
			for (Node n : nodes) {
				String configName = XmlUtils.SelectNodeText(n, "@name");
				if (configName.length() == 0)
					configName = C_INTERNAL_PREFIX + type.name().toLowerCase();

				configNames.add(configName);
			}

			return configNames;
		} catch (Exception ex) {
			throw new TESException(CoreErr.LoadComponentConfigFail, "func: getComponentConfigNames() type: " + type + " conditions:" + ArrayUtils.toString(conditions));
		}
	}

	@Override
	public String getComponentClassName(String configName) {
		try {
			String xpath;
			if (configName.startsWith(C_INTERNAL_PREFIX))
				xpath = "//config/" + configName.substring(C_INTERNAL_PREFIX.length()) + "/@class";
			else
				xpath = "//config/*[@name='" + configName + "']/@class";

			return XmlUtils.SelectNodeText(baseXml, xpath);
		} catch (Exception ex) {
			throw new TESException(CoreErr.LoadComponentConfigFail, "func: getComponentClassName() configName: " + configName);
		}
	}

	@Override
	public String getPropertyConfig(String entityName, String propName) {
		try {
			if (entityName.startsWith(C_INTERNAL_PREFIX))
				return XmlUtils.SelectNodeText(baseXml, "//config/" + entityName.substring(C_INTERNAL_PREFIX.length()) + "/" + propName);
			else
				return XmlUtils.SelectNodeText(baseXml, "//config/*[@name='" + entityName + "']/" + propName);
		} catch (Exception ex) {
			throw new TESException(CoreErr.LoadComponentConfigFail, "func: getPropertyConfig() entityName: " + entityName + " propName: " + propName);
		}
	}

	@Override
	public String[] getPropertyConfigs(String entityName, String propName) {
		try {
			if (entityName.startsWith(C_INTERNAL_PREFIX))
				return XmlUtils.SelectNodeListText(baseXml, "//config/" + entityName.substring(C_INTERNAL_PREFIX.length()) + "/" + propName).toArray(new String[0]);
			else
				return XmlUtils.SelectNodeListText(baseXml, "//config/*[@name='" + entityName + "']/" + propName).toArray(new String[0]);
		} catch (Exception ex) {
			throw new TESException(CoreErr.LoadComponentConfigFail, "func: getPropertyConfigs() entityName: " + entityName + " propName: " + propName);
		}
	}
}
