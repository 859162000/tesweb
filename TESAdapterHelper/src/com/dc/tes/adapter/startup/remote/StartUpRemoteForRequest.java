package com.dc.tes.adapter.startup.remote;

import java.nio.channels.ClosedByInterruptException;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.dc.tes.adapter.IRequestAdapter;
import com.dc.tes.adapter.framework.DefaultRequestAdapterEnvContext;
import com.dc.tes.adapter.remote.DefaultRequestAdapterHelper;
import com.dc.tes.adapter.util.ConfigHelper;
import com.dc.tes.adapterlib.*; //Needed!



/**
 * 启动"远程通道"(客户端)
 * 
 * @author 王春佳
 * 
 */
public class StartUpRemoteForRequest extends Thread {

	private static final Log log = LogFactory.getLog(StartUpRemoteForRequest.class);

	/**
	 * 通信层启动配置信息(ComLayer.config.xml)
	 */
	private Properties m_props = null;

	/**
	 * 发起端适配器
	 */
	DefaultRequestAdapterHelper m_adpterHelper = null;

	public StartUpRemoteForRequest(Properties prop) throws Exception {
		if (!((String) prop.get("adapterType")).toUpperCase().equals("REQUEST"))
			throw new Exception("适配器类型" + prop.get("adapterType") + "非法!");
		m_props = prop;
	}

	public void run() {
		startAdapter(m_props);
		// 等待 适配器插件自动退出
		try {
			while (!Thread.interrupted()) {
				Thread.sleep(500);
			}
		} catch (InterruptedException e) {
			log.debug("接收到退出指令，通知适配器退出……");
			shutdownAdapter();
		}
	}

	private void shutdownAdapter() {
		if (m_adpterHelper != null) {
			// 通知核心下线
			m_adpterHelper.unReg2TES();

			m_adpterHelper.stopServer();
		}
		log.info("发起端适配器" + m_props.getProperty("CHANNELNAME") + "关闭成功.");
	}

	private void startAdapter(Properties props) {
		String adapterName = props.getProperty("CHANNELNAME");

		IRequestAdapter adapterInstance = null;
		try {
			String clsName = "com.dc.tes.adapterlib." + (String) props.get("adapterPlugIn");
			adapterInstance = (IRequestAdapter) ConfigHelper.class.getClassLoader().loadClass(clsName).newInstance();
			System.out.println("成功装载发起端适配器插件" + clsName);

			// 用于License检验
			m_props.setProperty("SIMTYPE", adapterInstance.AdapterType());

			m_adpterHelper = new DefaultRequestAdapterHelper(m_props, adapterInstance);

			// 先注册
			byte[] config = m_adpterHelper.reg2TES();

			// 初始化
			if (adapterInstance.Init(new DefaultRequestAdapterEnvContext(config))) {
				m_adpterHelper.SetConfigProperty(adapterInstance.GetAdapterConfigProperties());
				System.out.println("发起端适配器" + adapterName + "初始化Init成功.");

				log.info("发起端适配器插件" + adapterName + "初始化Init成功.");
				log.error("发起端适配器插件" + adapterName + "初始化Init成功.");

				// 启动发起端适配器
				m_adpterHelper.startServer();
			} else {
				m_adpterHelper.unReg2TES();
				// 直接退出程序，无需调用m_requestAdapterHelper.stopServer()

				System.out.println("发起端适配器插件" + adapterName + "初始化Init失败.");
				log.error("发起端适配器插件" + adapterName + "初始化Init失败.");
			}

		} catch (InstantiationException e) {
			log.error("没有找到适配器实例!请检查配置文件。");
			System.out.println("没有找到适配器实例!请检查配置文件。");
			// 直接退出程序，无需调用stopServer
		} catch (IllegalAccessException e) {
			log.error("无法装载适配器实例类!请检查配置文件。[" + e.getMessage() + "]");
			System.out.println("无法装载适配器实例类!请检查配置文件。[" + e.getMessage() + "]");
			// 直接退出程序，无需调用stopServer
		} catch (ClassNotFoundException e) {
			log.error("找不到适配器实例类!请检查配置文件。[" + e.getMessage() + "]");
			System.out.println("找不到适配器实例类!请检查配置文件。[" + e.getMessage() + "]");
			// 直接退出程序，无需调用stopServer
		} catch (ClosedByInterruptException e) {
			// 关闭响应端适配器
			shutdownAdapter();
			log.info("发起端适配器" + adapterName + "被关闭");
			System.out.println("发起端适配器" + adapterName + "被关闭");
		} catch (Exception e) {
			// 关闭响应端适配器
			shutdownAdapter();
			log.error("适配器" + adapterName + "启动失败![" + e.getMessage() + "]");
			System.out.println("适配器" + adapterName + "启动失败![" + e.getMessage() + "]");
		}

		System.out.println("发起端适配器" + adapterName + "启动完成.");
		log.info("发起端适配器" + adapterName + "启动完成.");
	}
}
