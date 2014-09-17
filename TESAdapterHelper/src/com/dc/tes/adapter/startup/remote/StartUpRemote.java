package com.dc.tes.adapter.startup.remote;

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
 * 通信层启动 "远程通道"
 * 
 * @author 王春佳
 * 
 */
public class StartUpRemote implements IStartUp {

	private static Log logger = LogFactory.getLog(StartUpRemote.class);

	private static Map g_AdapterWorkerTable = new LinkedHashMap();

	/**
	 * 通知本地适配器插件工作线程终止运行
	 * 
	 * @param channelName : 通道名称
	 */
	private static synchronized void ShutdownAdapter(String channelName) {
		if (g_AdapterWorkerTable.containsKey(channelName)) {

			if (g_AdapterWorkerTable.get(channelName) != null) {

				((Thread) g_AdapterWorkerTable.get(channelName)).interrupt();

				g_AdapterWorkerTable.remove(channelName);
			}
		}
	}

	private void start(List AdapterConfigList) {

		// 注册程序关闭事件的监听器
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
			public void run() {
				try {
					// 停止所有适配器
					logger.info("通知适配器退出...");
					Object[] adapterNameArray = g_AdapterWorkerTable.keySet()
							.toArray();
					for (int i = 0; i < adapterNameArray.length; i++) {
						ShutdownAdapter((String) adapterNameArray[i]);
					}
					logger.info("通知完毕.");

				} catch (Exception ex) {
					logger.error("关闭适配器时发生异常", ex);
				}
			}
		}));

		// 根据每个适配器的配置项是否完整，过滤出合法的适配器允许启动
		String[] keys = new String[] { "adapterType", "adapterPlugIn",
				"CHANNELNAME", "coreIP", "corePort" };

		for (Iterator i = AdapterConfigList.iterator(); i.hasNext();) {
			Properties props = (Properties) i.next();
			if (!ConfigHelper.chkProperKey(props, keys)) {
				logger.error("适配器配置文件格式不正确，缺少必备项.");
				continue;
			}

			String adapterType = props.getProperty("adapterType");
			String adapterName = props.getProperty("CHANNELNAME");
			if (adapterType.toLowerCase().endsWith("request")) {
				if (!props.containsKey("UpPort")
				// ||!props.containsKey("host")
				) {
					logger.error("发起端适配器" + adapterName + "配置格式不正确，缺少必备项.");
					continue;
				}
			}

			if (g_AdapterWorkerTable.containsKey(adapterName)) {
				logger.error("不允许同名的适配器.[" + adapterName + "]");
				// System.exit(0);
			} else
				g_AdapterWorkerTable.put(adapterName, props);
		}

		// 按照配置文件中合法的配置项执行启动，不讲顺序
		for (Iterator i = g_AdapterWorkerTable.values().iterator(); i.hasNext();) {
			Properties props = (Properties) i.next();
			String adapterName = props.getProperty("CHANNELNAME");
			String adapterType = props.getProperty("adapterType");
			try {
				if (adapterType.toLowerCase().endsWith("reply")) {
					StartUpRemoteForReply worker = new StartUpRemoteForReply(props);
					g_AdapterWorkerTable.put(adapterName, worker);
					worker.start();
				} else {
					StartUpRemoteForRequest worker = new StartUpRemoteForRequest(props);
					g_AdapterWorkerTable.put(adapterName, worker);
					worker.start();
				}
				logger.info("适配器" + adapterName + "启动成功.");
			} catch (Exception e) {
				logger.error("适配器" + adapterName + "启动失败!" + e.getMessage());
			}
		}
	}

	public boolean startUp() {

		List AdapterConfigList = ConfigHelper.getXMLConfig();

		new StartUpRemote().start(AdapterConfigList);

		// 等待 所有适配器自动退出
		try {
			while (g_AdapterWorkerTable.size() > 0) {
				//System.out.println("适配器个数：" + g_AdapterWorkerTable.size());
				Thread.sleep(500);
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
			logger.error("适配器没有完全退出但是却被异常终止了.");
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("适配器被异常终止了.");
		}
		logger.info("TES适配器完全退出.");

		return true; // 此句话无法返回
	}

}
