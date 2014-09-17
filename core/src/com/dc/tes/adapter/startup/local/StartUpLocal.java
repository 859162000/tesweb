package com.dc.tes.adapter.startup.local;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.dc.tes.adapter.startup.IStartUp;
import com.dc.tes.adapter.util.ConfigHelper;

/**
 * 
 * 通信层启动 "本地通道"
 * 
 * @author 王春佳
 * 
 */
public class StartUpLocal implements IStartUp {

	private static final Log logger = LogFactory.getLog(StartUpLocal.class);

	/**
	 * 本地通道适配器列表 键-通道名 值-该通道启动线程
	 */
	private static Map m_adapterTable = new LinkedHashMap();
	
	/**
	 * JVM停止时,停止相关适配器
	 */
	private void addShutdownHook() {
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {

			public void run() {
				System.out.println("addShutdownHook is running...");
				// JVM停止时,执行该方法
				Iterator it = m_adapterTable.keySet().iterator();
				while (it.hasNext()) {
					String channelName = (String) it.next();
					Thread t = (Thread) m_adapterTable.get(channelName);
					t.interrupt();
					
					m_adapterTable.remove(channelName);
				}
			}

		}));
	}

	/**
	 * 按照ComLayer.config.xml的配置信息,启动适配器
	 * 
	 * @param AdapterConfigList
	 *            : 每个适配器配置信息的properties
	 */
	private void startChannel(List AdapterConfigList) {

		// Properties校验时的必要配置信息
		String[] keys = { "adapterType", "adapterPlugIn", "CHANNELNAME" };

		// 过滤出正确配置信息的本地通道列表,存储到m_adapterTable
		for (Iterator i = AdapterConfigList.iterator(); i.hasNext();) {
			Properties props = (Properties) i.next();
			if (!ConfigHelper.chkProperKey(props, keys)) {
				logger.error("适配器配置文件格式不正确，缺少必备项.");
				continue;
			}
			String channelName = props.getProperty("CHANNELNAME");
			if (m_adapterTable.containsKey(channelName))
				logger.error("通道名称重复,后续的同名通道将直接抛出" + channelName);
			else
				m_adapterTable.put(channelName, props);
		}

		// 按照配置信息启动适配器
		for (Iterator i = m_adapterTable.values().iterator(); i.hasNext();) {
			Properties props = (Properties) i.next();
			String channelName = props.getProperty("CHANNELNAME");
			String adapterType = props.getProperty("adapterType");

			if (adapterType.toLowerCase().equals("reply")) {
				Thread tServer = new Thread(new StartUpLocalForReply(props));
				m_adapterTable.put(channelName, tServer); // 覆盖原有值
				tServer.setDaemon(false);
				tServer.start();

			} else if (adapterType.toLowerCase().equals("request")) {
				Thread tClient = new Thread(new StartUpLocalForRequest(props));
				m_adapterTable.put(channelName, tClient);
				tClient.setDaemon(false);
				tClient.start();
			}
		}
	}

	public boolean startUp() {
		System.out.println("本地函数调用方式,启动通信层开始");
		logger.info("本地函数调用方式,启动通信层开始...");

		addShutdownHook();

		List adapterConfigList = ConfigHelper.getXMLConfig();
		new StartUpLocal().startChannel(adapterConfigList);

		logger.info("适配器个数:" + m_adapterTable.size());

		// try{
		// while (m_adapterTable.size()> 0)
		// Thread.sleep(1000*60);
		// }catch(Exception e){
		// e.printStackTrace();
		// }

		logger.info("本地函数调用方式,启动通信层结束...");

		// System.out.println("强行退出前");
		// System.exit(0);

		return true;
	}

}
