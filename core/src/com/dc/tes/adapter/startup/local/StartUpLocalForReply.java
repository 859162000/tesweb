package com.dc.tes.adapter.startup.local;

import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.dc.tes.adapter.IReplyAdapter;
import com.dc.tes.adapter.framework.DefaultReplyAdapterEnvContext;
import com.dc.tes.adapter.local.ReplyAdapterHelperLocal;

/**
 * 启动"本地通道"(服务端)
 * 
 * @author 王春佳
 * 
 */
public class StartUpLocalForReply implements Runnable {

	private static final Log logger = LogFactory
			.getLog(StartUpLocalForReply.class);

	/**
	 * 该线程是否正在运行: true-运行 false-结束
	 * 
	 * @see 该变量每次读取都从主内存读取,一定程度损耗性能
	 */
	volatile boolean isRunning = true;

	/**
	 * 通信层启动配置信息(ComLayer.config.xml)
	 */
	private final Properties m_props;

	/**
	 * 服务端适配器外部接口,该接口由特定适配器实现,供通信层反向调用
	 */
	private IReplyAdapter m_replyAdapterInstance = null;

	/**
	 * 服务端适配器内部接口,该接口由通信层内部实现,用于通信层与核心进行交互
	 */
	private ReplyAdapterHelperLocal m_adapterHelper = null;

	/**
	 * 适配器插件实例
	 */
	private final String adapterPlugInName;

	/**
	 * 通道名称
	 */
	private final String channelName;

	/**
	 * 构造函数,用于初始化通信层启动的配置信息
	 * 
	 * @param props
	 *            : 通信层启动配置信息(ComLayer.config.xml)
	 */
	protected StartUpLocalForReply(Properties props) {
		this.m_props = props;
		adapterPlugInName = (String) props.get("adapterPlugIn");
		channelName = props.getProperty("CHANNELNAME");
	}

	/**
	 * 根据通信层启动配置信息,启动通信层(服务端)
	 * 
	 * @param props
	 *            : 通信层启动配置信息(ComLayer.config.xml)
	 */
	private void startReplyAdapter(Properties props) {
		try {
			String clsName = "com.dc.tes.adapterlib." + (String) props.get("adapterPlugIn");
			m_replyAdapterInstance = (IReplyAdapter) Class.forName(
					clsName).newInstance();

			// 设定适配器类型,该适配器类型由适配器自己实现
			m_props
					.setProperty("SIMTYPE", m_replyAdapterInstance
							.AdapterType());

			// 初始化通信层与核心交互实例
			m_adapterHelper = new ReplyAdapterHelperLocal(m_props,
					m_replyAdapterInstance);

			// 向核心注册
			byte[] config = m_adapterHelper.reg2TES();

			// 初始化
			if (m_replyAdapterInstance.Init(new DefaultReplyAdapterEnvContext(
					config, m_adapterHelper))) {
				logger.info("响应端适配器初始化成功:" + channelName);

				// 启动外部响应端适配器实现的接口
				m_replyAdapterInstance.Start();
			} else {
				m_adapterHelper.unReg2TES();
				logger.info("响应端适配器初始化失败:" + channelName);
			}

		} catch (InstantiationException e) {
			logger.error("没有找到响应端适配器实例!请检查配置文件。");
		} catch (IllegalAccessException e) {
			logger.error("无法装载响应端适配器实例类!请检查配置文件。[" + e.getMessage() + "]");
		} catch (ClassNotFoundException e) {
			logger.error("找不到响应端适配器实例类!请检查配置文件。[" + e.getMessage() + "]");
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("适配器启动异常。[" + e.getMessage());
		}
	}

	public void run() {
		startReplyAdapter(this.m_props);

		// 使线程继续执行,知道通知该线程后,强行关闭服务端适配器
		while (isRunning && !Thread.currentThread().isInterrupted()) {
			try {
				Thread.currentThread().sleep(1000 * 5);
			} catch (InterruptedException e) {
				break;
			}
		}
		//停止服务端适配器
		shutdownReplyAdapter();
		logger.info("适配器已经退出,通道名称:" + channelName);
	}

	/**
	 * 停止服务端适配器
	 */
	private void shutdownReplyAdapter() {
		if (m_replyAdapterInstance != null) {
			logger.info("适配器正在退出,通道名称:" + channelName);
			m_replyAdapterInstance.Stop();
			this.isRunning = false;
		} else
			logger.error("服务端适配器尚未启动,无法关闭");
	}

}
