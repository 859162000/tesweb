package com.dc.tes.adapter.startup.remote;

import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.dc.tes.adapter.IReplyAdapter;
import com.dc.tes.adapter.framework.DefaultReplyAdapterEnvContext;
import com.dc.tes.adapter.remote.DefaultReplyAdapterHelper;
import com.dc.tes.adapter.util.ConfigHelper;

/**
 * 启动"远程通道"(服务端)
 * 
 * @author 王春佳
 * 
 */
public class StartUpRemoteForReply extends Thread {
	
	private static final Log log = LogFactory.getLog(StartUpRemoteForReply.class);

	private DefaultReplyAdapterHelper m_adpterHelper = null;

	private Properties m_props = null;

	IReplyAdapter m_replyAdapterInstance = null;

	public StartUpRemoteForReply(Properties prop) throws Exception {
		if (!((String) prop.get("adapterType")).toUpperCase().equals("REPLY"))
			throw new Exception("响应端适配器类型" + prop.get("adapterType") + "非法!");

		m_props = prop;
	}

	public void run() {
		startAdapter(m_props);

		// 等待 适配器插件自动退出
		try {
			while (!Thread.currentThread().isInterrupted()) {
				Thread.sleep(500);
			}
		} catch (InterruptedException e) {
			log.debug("接收到退出指令，通知适配器退出……");
			shutdownAdapter();
		}
	}

	private void shutdownAdapter() {
		// 关闭响应端适配器
		if (null != m_replyAdapterInstance) {
			// 通知核心
			if (m_adpterHelper != null)
				m_adpterHelper.unReg2TES();

			m_replyAdapterInstance.Stop();
		}
		log.info("响应端适配器" + m_props.getProperty("CHANNELNAME") + "关闭.");
	}

	/**
	 * "远程通道"方式,服务端适配器启动
	 * @param props 通信层启动配置信息(ComLayer.config.xml)
	 */
	public void startAdapter(Properties props) {
		String adapterName = props.getProperty("CHANNELNAME");
		try {
			String clsName = "com.dc.tes.adapterlib." + (String) props.get("adapterPlugIn");
			m_replyAdapterInstance = (IReplyAdapter) ConfigHelper.class
					.getClassLoader().loadClass(clsName).newInstance();

			System.out.println("成功装载响应端适配器" + clsName);
			log.error("成功装载响应端适配器" + clsName);

			// 用于License检验
			m_props.setProperty("SIMTYPE", m_replyAdapterInstance.AdapterType());

			m_adpterHelper = new DefaultReplyAdapterHelper(m_props);

			// 先注册
			byte[] config = m_adpterHelper.reg2TES();

			// 初始化
			if (m_replyAdapterInstance.Init(new DefaultReplyAdapterEnvContext(config, m_adpterHelper))) {
				m_adpterHelper.SetConfigProperty(m_replyAdapterInstance.GetAdapterConfigProperties());
				System.out.println("响应端适配器" + adapterName + "初始化Init成功.");
				log.info("响应端适配器" + adapterName + "初始化Init成功.");

				// 启动响应端适配器
				m_replyAdapterInstance.Start();

			} else {
				m_adpterHelper.unReg2TES();
				// 直接退出程序，无需调用m_replyAdapterInstance.Stop()

				System.out.println("响应端适配器" + adapterName + "初始化Init失败.");
				log.error("响应端适配器" + adapterName + "初始化Init失败.");
			}
		} catch (InstantiationException e) {
			log.error("没有找到响应端适配器实例!请检查配置文件。");
			System.out.println("没有找到响应端适配器实例!请检查配置文件。");
		} catch (IllegalAccessException e) {
			log.error("无法装载响应端适配器实例类!请检查配置文件。[" + e.getMessage() + "]");
			System.out.println("无法装载响应端适配器实例类!请检查配置文件。[" + e.getMessage() + "]");
		} catch (ClassNotFoundException e) {
			log.error("找不到响应端适配器实例类!请检查配置文件。[" + e.getMessage() + "]");
			System.out.println("找不到响应端适配器实例类!请检查配置文件。[" + e.getMessage() + "]");
		} catch (Exception e) {
			log.error("适配器启动异常。[" + e.getMessage());
			System.out.println("适配器启动异常。[" + e.getMessage());
		}

		System.out.println("响应端适配器" + adapterName + "启动完成.");
		log.info("响应端适配器" + adapterName + "启动完成.");
	}
}
