package com.dc.tes.adapter.startup.local;

import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.dc.tes.adapter.IRequestAdapter;
import com.dc.tes.adapter.framework.DefaultRequestAdapterEnvContext;
import com.dc.tes.adapter.local.RequestAdapterHelperLocal;
import com.dc.tes.adapter.util.RequestListLocal;

/**
 * 
 * 启动"本地通道"(发起端)
 * 
 * @author 王春佳
 *
 */
public class StartUpLocalForRequest implements Runnable {

	private static final Log logger = LogFactory
			.getLog(StartUpLocalForRequest.class);

	/**
	 * 通信层启动配置信息(ComLayer.config.xml)
	 */
	private Properties m_props = null;

	/**
	 * 发起端适配器
	 */
	RequestAdapterHelperLocal m_adpterHelper = null;

	/**
	 * 适配器插件实例
	 */
	private final String adapterPlugInName;

	/**
	 * 通道名称
	 */
	private final String channelName;

	public StartUpLocalForRequest(Properties props) {
		this.m_props = props;
		adapterPlugInName = (String) props.get("adapterPlugIn");
		channelName = props.getProperty("CHANNELNAME");
	}

	public void run() {
		startAdapter(this.m_props);
		
		while(!Thread.currentThread().isInterrupted()){
			try {
				Thread.currentThread().sleep(1000*5);
			} catch (InterruptedException e) {
				break;
			}
		}
		
		//停止发起端适配器
		shutdownReplyAdapter();
		logger.info("适配器已经退出,通道名称:" + channelName);
	}
	
	/**
	 * 停止发起端适配器
	 */
	private void shutdownReplyAdapter(){
		if(m_adpterHelper != null){
			logger.info("适配器正在退出,通道名称:" + channelName);
			m_adpterHelper.stopServer();
		}else{
			logger.error("发起端适配器尚未启动,无法关闭");
		}
		
	}

	/**
	 * 
	 * 启动通信层(发起端)
	 * 
	 * @param props 通信层启动配置信息(ComLayer.config.xml)
	 */
	private void startAdapter(Properties props) {
		// 发起端适配器实例
		IRequestAdapter requestAdapterInst = null;
		try {
			requestAdapterInst = (IRequestAdapter) Class.forName(
					adapterPlugInName).newInstance();

			// 设定适配器类型,该适配器类型由适配器自己实现
			this.m_props.put("SIMTYPE", requestAdapterInst.AdapterType());

			// 初始化通信层与核心交互实例
			m_adpterHelper = new RequestAdapterHelperLocal(this.m_props,
					channelName, requestAdapterInst);

			// 向核心注册
			byte[] config = m_adpterHelper.reg2TES();

			// 初始化
			if (requestAdapterInst.Init(new DefaultRequestAdapterEnvContext(
					config))) {
				logger.info("发起端适配器插件" + channelName + "初始化Init成功.");

				// 启动发起端适配器
				m_adpterHelper.startServer();

			} else {
				m_adpterHelper.unReg2TES();
				logger.info("发起端适配器插件" + channelName + "初始化Init失败.");
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

}
