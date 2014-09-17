package com.dc.tes.ui.util;

import java.io.FileInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;


import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.dc.tes.data.model.Case;
import com.dc.tes.data.model.SysType;
import com.dc.tes.data.model.Transaction;
import com.dc.tes.dom.MsgDocument;
import com.dc.tes.dom.util.MsgLoader;
import com.dc.tes.fcore.script.MsgContext;
import com.dc.tes.msg.MsgService;
import com.dc.tes.msg.pack.PackService;
import com.dc.tes.msg.pack.PackSpecification;
import com.dc.tes.ui.server.CaseService;
import com.dc.tes.ui.server.HelperService;
import com.dc.tes.ui.server.SimuSystemService;
import com.dc.tes.ui.server.TransactionService;
import com.dc.tes.util.RuntimeUtils;

/**
 * 报文结构样式管理类
 * @author shenfx,liji
 * @author scckobe           	 1)根据样例copy  2)根据系统删除文件
 * @author scckobe				 重载获得报文样式配置信息的方法（根据不同级别的数据）
 */

public class SystemConfigManager {
	private static DocumentBuilderFactory s_domFactory = DocumentBuilderFactory.newInstance();
	private final static String configPath = "Config/SysPackStyle/";
	private final static String sampleFilename = "WEB-INF/Sample.xml";
	static {
		s_domFactory.setNamespaceAware(true);
	}

	/**
	 * 获得相应系统的报文结构样式配置信息
	 * @param system	系统名称
	 * @param isClientSimu	1：接收端交易  0：发起端交易
	 * @return			报文结构样式配置信息
	 */
	public static ISystemConfig getConfig(String sysName, int isClientSimu){
		try
		{
			Document doc = getConfigDocument(sysName);
			ISystemConfig config;
			try {
				config = new SystemXmlConfig(doc, isClientSimu, sysName);
				return config;
			} catch (DOMException e) {
				throw new RuntimeException("DOM解析异常");
			} catch (XPathExpressionException e) {
				throw new RuntimeException("XPath异常");
			}
		}
		catch(RuntimeException e)
		{
			e.printStackTrace();
			throw e;
		}
		catch (Exception e) {
			e.printStackTrace();
//			throw new RuntimeException("遇到未处理的异常");
		}
		DefaultConfig defaultConfig = new DefaultConfig();
		return defaultConfig;
	}
	
	/**
	 * 获得报文样式配置信息
	 * @param caseID	案例标识
	 * @return			报文样式配置信息
	 */
	public static ISystemConfig getConfigByCaseID(String caseID, int isClientSimu) 
	{
		Case caseInfo = new CaseService().GetCaseBean(caseID);
		
		if(caseInfo != null)
			return getConfigByTranID(caseInfo.getTransactionId(), isClientSimu);
		else
			throw new RuntimeException("案例信息已删除");
	}
	
	/**
	 * 获得报文样式配置信息
	 * @param tranID	交易标识
	 * @return			报文样式配置信息
	 */	
	public static ISystemConfig getConfigByTranID(String tranID, int isClientSimu)
	{
		Transaction tran = new TransactionService().GetSingle(tranID);
		if(tran == null)
			throw new RuntimeException("交易信息已被删除");
		
		return getConfigBySysID(tran.getSystemId(), isClientSimu);
	}
	
	/**
	 * 获得报文样式配置信息
	 * @param sysID		系统标识
	 * @param isClientSimu	0：接收端交易  1：发起端交易
	 * @return
	 */
	public static ISystemConfig getConfigBySysID(String sysID, int isClientSimu)
	{
		SysType sys = new SimuSystemService().GetSimuSystemSignle(sysID);
		if(sys == null)
			throw new RuntimeException("系统已被删除");
		
		return getConfig(sys.getSystemName(), isClientSimu);
	}

	/**
	 * 根据系统名称获得报文样式文档
	 * @param system	系统名称
	 * @return			报文样式文档
	 */
	public static Document getConfigDocument(String system) throws RuntimeException {
		String filename = GetConfigFileName(system);
		FileInputStream stream;
		try {
			stream = new FileInputStream(filename);
		} catch (FileNotFoundException e) {
			throw new RuntimeException("系统对应的报文样式不存在");
		}
		
		//若取不到文件流，从默认配置文件获取文件流
		if (stream == null) {
			try {
				stream = new FileInputStream(new HelperService().GetRootPath() + sampleFilename);
			} catch (FileNotFoundException e) {
				throw new RuntimeException("默认报文样式不存在");
			}
		}
		if(stream != null)
		{
			DocumentBuilder builder;
			try {
				builder = s_domFactory.newDocumentBuilder();
			} catch (ParserConfigurationException e) {
				throw new RuntimeException("XML文档创建失败");
			}
			Document doc;
			try {
				doc = builder.parse(stream);
			} catch (SAXException e) {
				throw new RuntimeException("SAX解析出错");
			} catch (IOException e) {
				throw new RuntimeException("IO读取失败");
			}
			return doc;
		}
		return null;
	}
	
//	/**
//	 * 调用组包
//	 * @param system 系统名称
//	 * @param isRes true： 组包 false：拆包
//	 * @param xmlContent xml文件内容
//	 * @return
//	 */
//	public static byte[] PackSpecification1(String system,boolean isRes,String xmlContent) {
//		try
//		{
//			//从对应文件获取文件流
//			String filename = new HelperService().GetRootPath() + configPath 
//				+ system +  (isRes ? ".style" : ".rule") + ".xml";
//			FileInputStream stream = new FileInputStream(filename);
//			PackSpecification spec = PackService.LoadPackSpecification(stream);
//			MsgDocument doc = MsgLoader.LoadXml(xmlContent);
//			return PackService.PackDocument(doc, spec, null, null);
//		}
//		catch (Exception e) {
//			System.out.println("组包失败：系统为:" + system);
//			e.printStackTrace();
//			
//		}
//		return null;
//	}
	
	public static byte[] PackSpecification(String specStr,String xmlContent,final String tranCode) {
		try
		{
			System.out.println(specStr);
			PackSpecification spec = MsgService.LoadPackSpecification(specStr);
			MsgDocument doc = MsgLoader.LoadXml(xmlContent);
//			return PackService.PackDocument(doc, spec, new IContext(){@Override
//			public String getTranCode() {
//				return tranCode;
//			}});
			
			return MsgService.Pack(doc, spec, new MsgContext(tranCode));
		}
		catch (Exception e) {
//			System.out.println("组包失败：系统为:" + system);
			e.printStackTrace();
			
		}
		return null;
	}
	
	/**
	 * 根据系统名称 拷贝样式文件
	 * @param system 系统名称
	 * @throws IOException 
	 */
	public static void copyConfig(String system) throws IOException
	{
		File sourceFile = new File(new HelperService().GetRootPath() + sampleFilename);
		File desFile = new File(GetConfigFileName(system));
		RuntimeUtils.WriteFile(desFile, RuntimeUtils.ReadFile(sourceFile));
	}
	
	/**
	 * 根据系统名称 拷贝样式文件
	 * @param system 系统名称
	 * @throws IOException 
	 */
	public static void renameConfig(String oldsystem,String newSystem) throws IOException
	{
		if(oldsystem.compareTo(newSystem) == 0)
			return;
		File sourceFile = new File(GetConfigFileName(oldsystem));
		File destFile = new File(GetConfigFileName(newSystem));
		sourceFile.renameTo(destFile);
	}
	
	/**
	 * 根据系统名称 删除样式文件
	 * @param system 系统名称
	 * @throws Exception 无法获得根路径
	 */
	public static void delConfig(String system)
	{
		File f = new File(GetConfigFileName(system));
		f.delete();
	}
	
	/**
	 * 获得配置文件名称
	 * @param system 系统名称
	 * @return 配置文件路径名称
	 */
	private static String GetConfigFileName(String system) 
	{
		try {
			return new HelperService().GetRootPath() + configPath + system + ".xml";
		} catch (Exception e) {
			e.printStackTrace();
		}
		return RuntimeUtils.MapFile(sampleFilename).getName();
	}
	
	/**
	 * 获得默认报文样式内容
	 * @return 默认报文样式内容
	 */
	@SuppressWarnings("unused")
	private static String GetDefaultConfig()
	{
		try	{
			FileInputStream stream = new FileInputStream(new HelperService().GetRootPath() + sampleFilename);		
			if (stream != null) {
				DocumentBuilder builder = s_domFactory.newDocumentBuilder();
				Document doc = builder.parse(stream);

				if(doc != null)
				{
					return doc.toString();
				}
			}
		}
		catch(Exception ex)	{
		}
		
		return "";
	}
}
