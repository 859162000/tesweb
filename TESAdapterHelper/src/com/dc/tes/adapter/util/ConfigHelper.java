package com.dc.tes.adapter.util;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.*;
import org.dom4j.io.*;


/**
 * 工具类：专门处理所有本地配置信息
 * 
 * @author guhb
 * 
 * @see 该类中引用的Message使用comom中的jdk1.6版本
 * @see 可能存在的性能瓶颈:与核心进行交互的数据,转换成byte[]后,使用Message1.4的版本进行转换
 */
public class ConfigHelper {
	private static Log logger = LogFactory.getLog(ConfigHelper.class);

	// private static final String m_filepath = "ComLayer.config";
	private static final String m_xmlfilepath = "ComLayer.config.xml";

	/**
	 * 获取适配器运行的当前路径
	 */
	// private static final String startDir;
	/**
	 * 获取适配器运行的classpath当前路径
	 */
	// private static final String classDir;
	// static {
	// File directory = new File(".");
	// directory.getCanonicalPath();
	// classDir = new File(Thread.currentThread().getContextClassLoader()
	// .getResource(".").getPath()).getPath();
	// startDir = new
	// File(Thread.currentThread().getContextClassLoader().getResource(".").getPath()).getParent();
	// startDir = new File(Thread.currentThread().getContextClassLoader()
	// .getResource(".").getPath()).getPath();
	// }
	/**
	 * 将相对于适配器根路径的相对路径映射为文件系统的绝对路径
	 * 
	 * @param path
	 *            相对路径
	 * @return 绝对路径
	 */
	public static String MapPath(String path) {
		String currDir = path;
		try {
			currDir = new File(".").getCanonicalPath();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
		}
		if (path == null || path.length() == 0)
			return currDir;

		return path.startsWith("/") || path.startsWith("\\") ? currDir + path
				: currDir + File.separator + path;
	}

	/**
	 * 检查是否存在本地文件
	 */
	// public ConfigHelper() {
	//
	// }
	public static String getEncoding() {
		return "utf-8";
	}

	
	/**
	 * 工具方法 获取字节流中的（名-值）对
	 * 
	 * @param content
	 *            字节流
	 * @return 配置内容中的名值对集合
	 */
	public static Properties getConfig(byte[] content) {
		Properties props = new Properties();

		try {
			InputStream in = new BufferedInputStream(new ByteArrayInputStream(content));
			props.load(in);
			//log.debug("读取配置流信息：" + props.toString());
		} catch (Exception e) {
			props = new Properties();
			//log.error("配置流无法解析！[" + e.getMessage() + "]");
		}

		return props;
	}
	
	/**
	 * 工具方法 获取本地配置文件中的（名-值）对
	 * 
	 * @param filepath
	 *            本地配置文件名称
	 * @return 配置内容中的名值对集合
	 */
	// public static Properties getConfig() {
	// Properties props = new Properties();
	//
	// String filePath = null;
	// try {
	// filePath = MapPath(m_filepath);
	// InputStream in = new BufferedInputStream(new FileInputStream(
	// filePath));
	// props.load(in);
	//
	// log.debug("读取配置文件: " + filePath + "成功获得信息：" + props.toString());
	// } catch (Exception e) {
	// props = new Properties();
	// log.error("配置文件：" + filePath + "无法解析！[" + e.getMessage() + "]");
	// }
	//
	// return props;
	// }
	/**
	 * 根据本地的XML配置文件初始化
	 * 
	 * @return List<Properties>
	 */
	public static List getXMLConfig() {

		LinkedList list = new LinkedList();

		String filePath = null;
		try {
			filePath = MapPath(m_xmlfilepath);
			Document doc = new SAXReader().read(new File(filePath));
			Element root = doc.getRootElement();
			for (Iterator i = root.elementIterator("Adapter"); i.hasNext();) {
				Element adapterNode = (Element) i.next();
				Properties props = new Properties();
				for (Iterator j = adapterNode.elementIterator(); j.hasNext();) {
					Element adpAttrNode = (Element) j.next();
					props.put(adpAttrNode.getName(), adpAttrNode.getText());
				}

				logger.debug("发现适配器: " + props);
				list.add(props);
			}

			logger.debug("读取配置文件: " + filePath + "成功.");
		} catch (Exception e) {
			logger.error("配置文件：" + filePath + "无法解析！[" + e.getMessage() + "]");
		}

		return list;
	}

	/**
	 * 判断 keys 中的每个key,是否在 prop中存在;用于适配器初始化校验
	 * 
	 * @param prop
	 *            : 目标Properties对象
	 * @param keys
	 *            : key名称数组
	 * @return false:至少有一个key在prop中不存在 true:所有的key都存在
	 * @author 王春佳
	 */
	public static boolean chkProperKey(Properties prop, String[] keys) {
		boolean existKey = true;

		for (int i = 0; i < keys.length; i++) {
			if (!prop.containsKey(keys[i])) {
				logger.error("适配器必要的初始化信息不存在:" + keys[i]);
				return false;
			}
		}
		return existKey;
	}

	
	
	/**
	 * 向核心本地通道发送注册   桩程序
	 * @param requestByte : 注册报文
	 * @return 注册返回报文List<byte>
	 */
//	private static List sendToCoreLocalMock_Reg(){
		// 适配器注册信息
//		StringBuffer buf = new StringBuffer();
//		buf.append("jettyPort = 10000\n");
//		buf.append("miniThreadNum = 10\n");
//		buf.append("servletUrl = /tes1/httpadapter1\n");
//		buf.append("servletRootUrl = /web\n");
//		buf.append("dynamic_in = 0\n");
//		buf.append("dynamic_out = 0\n");
//		buf.append("dynamic_name = HttpReplyFactory\n");
//		
//		
//		List resultLst = new ArrayList();
//		
//		ReplyMessage rpmsg1 = new ReplyMessage(MessageType.REG);
//		rpmsg1.put("CONFIGINFO", buf.toString().getBytes());
//		
//		System.out.println("mock is:" + new String(rpmsg1.Export()));
//		
//		resultLst.add(rpmsg1.Export());
//		
//		return resultLst;
//	}
	
	

}



