package com.dc.tes;

import java.nio.charset.Charset;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.dc.tes.exception.CoreErr;
import com.dc.tes.exception.TESException;
import com.dc.tes.util.RuntimeUtils;
import com.dc.tes.util.XmlUtils;

/**
 * 核心配置项
 * 
 * @author lijic
 * 
 */
public class Config {
	/**
	 * 延迟拆包 当该项被设为true时 只有存在脚本并且脚本用到了IN时（即直到必须使用输入报文中的数据时）才会去拆包
	 */
	public final boolean LAZY_UNPACK;

	/**
	 * 从base.xml中读取通道、拆组包等信息 当该项为设为true时 将从base.xml中读取配置 否则会从数据源中读取相应配置
	 */
	public final boolean CONFIG_FROM_BASEXML;

	/**
	 * 从本地目录中读取拆组包配置 当该项设为true时 将从conf目录下读取拆组包配置 否则会从数据源中读取相应配置
	 */
	public final boolean PACKCONFIG_FROM_LOCAL;

	/**
	 * 核心编码 将使用这个编码来输出日志
	 */
	public final Charset ENCODING;

	public Config() {
		try {
			Document baseXml = XmlUtils.LoadXml(RuntimeUtils.OpenResource("base.xml"));

			Map<String, String> properties = new LinkedHashMap<String, String>();
			List<Element> nodes = XmlUtils.SelectNodes(baseXml, "//config/property");
			for (Element e : nodes)
				properties.put(e.getAttribute("name"), e.getTextContent());

			LAZY_UNPACK = loadProperty("LAZY_UNPACK", properties, boolean.class, false);
			CONFIG_FROM_BASEXML = loadProperty("CONFIG_FROM_BASEXML", properties, boolean.class, false);
			PACKCONFIG_FROM_LOCAL = loadProperty("PACKCONFIG_FROM_LOCAL", properties, boolean.class, false);
			ENCODING = Charset.forName(loadProperty("ENCODING", properties, String.class, "utf-8"));
		} catch (Exception ex) {
			throw new TESException(CoreErr.LoadCoreConfigFail, ex);
		}
	}

	private <T> T loadProperty(String name, Map<String, String> properties, Class<T> cls, T defaultValue) {
		if (properties.containsKey(name))
			return RuntimeUtils.FromString(properties.get(name), cls);
		else
			return defaultValue;
	}
}
