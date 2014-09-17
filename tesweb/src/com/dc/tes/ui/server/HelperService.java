package com.dc.tes.ui.server;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.dc.tes.adapter.remote.DefaultReplyAdapterHelper;
import com.dc.tes.data.DALFactory;
import com.dc.tes.data.IDAL;
import com.dc.tes.data.model.Case;
import com.dc.tes.data.op.Op;
import com.dc.tes.net.Message;
import com.dc.tes.net.MessageItem;
import com.dc.tes.net.MessageType;
import com.dc.tes.net.jre14.ReplyMessage;
import com.dc.tes.ui.client.AppContext;
import com.dc.tes.ui.client.IHelperService;
import com.dc.tes.ui.client.enums.VersionType;
import com.dc.tes.ui.client.model.GWTCompareResult;
import com.dc.tes.ui.client.model.GWTPack_Struct;
import com.dc.tes.ui.client.model.GWTProperties;
import com.dc.tes.ui.client.model.GWTSimuSystem;
import com.dc.tes.ui.client.model.GWTTransaction;
import com.dc.tes.ui.client.model.IDistValidate;
import com.dc.tes.ui.util.ISystemConfig;
import com.dc.tes.ui.util.SystemConfigManager;
import com.dc.tes.ui.util.TranStructTreeUtil;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * 服务类
 * 1) 实现服务类异步接口（具体功能参见：接口说明）
 * 2) 获得超级用户的密码
 * 3) 保存超级用户密码
 * 4) 根据传入值，获得唯一性验证所需的关系符表达式数组
 * 5) 通知后台更新列表
 * 6) 获得war包（应用程序）根目录的绝对路径
 * 
 * @author scckobe
 *
 */
public class HelperService extends RemoteServiceServlet implements
		IHelperService {

	private static final Log log = LogFactory.getLog(HelperService.class);
	private static final long serialVersionUID = 396211960987465947L;
	
	/**
	 * 菜单文件名
	 */
	private static String menuFileName = "menu.json";
	/**
	 * 超级用户密码文件名
	 */
	private static String pwdFileName = "AdminPWD.dat";
	/**
	 * 核心IP，端口文件名
	 */
//	private static String coreFileName = "CoreIPPort.txt";

	@Override
	public boolean IsNameDistinct(IDistValidate validator, String validateValue){
		try {
			Op[] opArray = GetDistinctOpArray(validator, validateValue);
			return DALFactory.GetBeanDAL(
					Class.forName("com.dc.tes.data.model."
							+ validator.GetTableName())).Count(opArray) == 0;
		} catch (Exception ex) {
			log.error(ex, ex);
			ex.printStackTrace();
		}
		return false;
	}
	
	@Override
	public List<GWTProperties> GetTESConfig(String confRoot) throws Exception 
	{
		List<GWTProperties> propList = new ArrayList<GWTProperties>();
		try
		{
			Properties properties = GetProperties(confRoot);
			String versionName = properties.getProperty("Version", VersionType.Pstub.toString());
			log.info("版本名称:" + versionName);
			
			for(Object key : properties.keySet())
				propList.add(new GWTProperties(key,properties.get(key)));
			propList.add(new GWTProperties(AppContext.MenuConfig,
					GetTxtFileInfo(confRoot + versionName + "/" + menuFileName)));
		}
		catch (Exception e) {
			log.error(e, e);
			e.printStackTrace();
		}
		return propList;
	}
	
	/**
	 * 获得界面配置整体配置信息
	 * @return	配置信息名值对
	 */
	public Properties GetProperties(String confRoot) throws Exception 
	{
		Properties properties = new Properties();
		properties.load(GetFileReader(confRoot + "TESConfig.dat"));
		return properties;
	}
	
	/**
	 * 获得超级用户的密码
	 * @return 超级用户的密码
	 * 		   暂时不需要加密解密
	 * 
	 * @throws Exception 获取失败异常
	 */
	public String GetAdministratorPWD() throws Exception
	{
		log.info("超级用户密码文件:");
		return GetTxtFileInfo(pwdFileName);
	}
	
	/**
	 * 保存超级用户密码
	 * @param pwd 超级用户密码（暂时不加密）
	 */
	public void SaveAdministratorPWD(String pwd) throws Exception
	{
		SaveFileInfo(pwdFileName,pwd);
	}
	
	/**
	 *  根据传入值，获得唯一性验证所需的关系符表达式数组
	 * @param validator     唯一性验证接口
	 * @param validateValue 被验证值
	 * @return  关系符表达式数组
	 */
	public Op[] GetDistinctOpArray(IDistValidate validator, String validateValue) {
		List<Op> OpList = new ArrayList<Op>();

		Map<String, Object> fieldValuePair = validator
				.GetFieldValuePair(validateValue);
		for (String key : fieldValuePair.keySet()) {
			OpList.add(Op.EQ(key, fieldValuePair.get(key)));
		}

		Op[] opArray = new Op[OpList.size()];
		OpList.toArray(opArray);
		return opArray;
	}
	
	private Properties FormatSysProperty(GWTSimuSystem sysInfo)
	{
		Properties properties = new Properties();
		properties.put("coreIP", sysInfo.GetIP());
		properties.put("corePort", String.valueOf(sysInfo.GetPort()));
		properties.put("CHANNELNAME", "UI");
		properties.put("SIMTYPE", "c");
		
		
		return properties;
	}
	
	/**
	 * 通知后台更新列表
	 * 现在被调用情况：案例数据 的 增删改
	 *              交易数据 的   删改
	 * @param sysInfo	系统信息
	 * @return			是否更新成功
	 */
	protected boolean SendToBack(final GWTSimuSystem sysInfo) {
		try {
//			final Message sendMsg = new Message(MessageType.UI);
//			sendMsg.put(MessageItem.UI.OP, 2);
//			
//			Thread t = new Thread(
//					new Runnable(){
//						public void run(){
//							new DefaultReplyAdapterHelper(FormatSysProperty(sysInfo)).
//								SendToCoreRaw(sendMsg.Export());
//						}
//					});
//			t.start();
			
			return true;
		} catch (Exception e) {
			log.error(e);
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * 执行案例
	 * @param sysInfo		系统信息 必须提供
	 * @param tranInfo		对应交易信息 必须提供
	 * @param caseId		案例名称 必须提供
	 * @param REQMESSAGE	向被测系统发的报文	可选
	 * @param REQDATA		向被测系统发的数据（未经过组包的） 可选
	 * @return				案例执行结果
	 */
	public GWTCompareResult RunCase(GWTSimuSystem sysInfo,GWTTransaction tranInfo,String caseId,byte[] REQMESSAGE,String REQDATA, String executeLogId)
	{
		GWTCompareResult result = new GWTCompareResult();
		
		Message sendMsg = new Message(MessageType.UI);
		IDAL<Case> caseDAL = DALFactory.GetBeanDAL(Case.class);
		Case casebean = caseDAL.Get(Op.EQ("id", caseId));
		if(casebean.getBreakPointFlag()!=null && casebean.getBreakPointFlag()==1)
			sendMsg.put(MessageItem.UI.OP, 5);
		else {
			sendMsg.put(MessageItem.UI.OP, 1);
		}
		
		sendMsg.put(MessageItem.UI.TRANCODE, tranInfo.getTranCode());
		sendMsg.put("CHANNELNAME", "UI");
		//设置通道名称，继承关系
		String chanelName = "SEND";
		if(!(tranInfo.GetChanel() == null || tranInfo.GetChanel().isEmpty()))
		{
			String tranChanelName = tranInfo.GetChanel();
			//若通道已不存在
			if(!new ComponentService().IsChannelExist(sysInfo.GetSystemID(), tranChanelName))
			{
				result.setErrorMsg("该案例所属交易的通道：" + tranChanelName + ",不存在，无法发起");
				return result;
			}
			chanelName = tranChanelName;
		}
		else
		{
			if(!(sysInfo.GetChanel() == null || sysInfo.GetChanel().isEmpty()))
				chanelName = sysInfo.GetChanel();
		}
		sendMsg.put("DESTCHANNEL", chanelName);
		
		if(caseId != null && !caseId.isEmpty())
			sendMsg.put(MessageItem.UI.CASENAME, caseId);
		
		if(REQMESSAGE != null)
			sendMsg.put(MessageItem.UI.REQMESSAGE, REQMESSAGE);
			
		if(REQDATA != null && !REQDATA.isEmpty())
			sendMsg.put(MessageItem.UI.REQDATA, REQDATA);
		
		sendMsg.put("EXECUTELOGID", executeLogId);
		log.debug("将要发送消息：" + sendMsg);
		
		
		try {
			byte[] byteMsg;
			try
			{
				byteMsg = new DefaultReplyAdapterHelper(FormatSysProperty(sysInfo)).SendToCoreRaw(sendMsg.Export());
			}
			catch (Exception e) {
				log.error(e);
				e.printStackTrace();
				result.setErrorMsg("模拟器执行出现异常");
				return result;
			}
			
			//返回null，证明核心没有启动
			if(byteMsg == null)
			{
				result.setErrorMsg("模拟器实例尚未启动，请与相关人员联系");
				return result;
			}
			InputStream stream = new ByteArrayInputStream(byteMsg);
			ReplyMessage replyMsg = null;
			try
			{
				replyMsg = new ReplyMessage(stream);
			}
			catch (Exception e) {
				result.setBoolResult(false);
				result.setErrorMsg(e.getMessage());
				return result;
			}
			
			result.setBoolResult(replyMsg.isOK());
			
			if(replyMsg.isOK())
			{
				ISystemConfig config = SystemConfigManager.getConfig(sysInfo.GetSystemName(), 0);
				String copResult = replyMsg.getString(MessageItem.UI.COMPARERESULT);
				
				//注意页面处理没有比对结果的情况
				GWTPack_Struct root = null;
				if(copResult != null && !copResult.isEmpty())
				{
					root = TranStructTreeUtil.GetCompResultRoot(copResult,
						caseId, config);
				}
				result.setCompareResult(root);
			}
			else
			{
				result.setErrorMsg(replyMsg.getString("ERRMSG"));
			}
			
		} catch (Exception e) {
			result.setErrorMsg("收到预期结果，解析出错，请与相关人员联系");
			e.printStackTrace();
			log.error(e,e);
		}
		return result;
	}
	public void aa(){
		
	}
	//by ljs
	public void RunCaseFlow(final GWTSimuSystem sysInfo,String caseFlowID, String executeLogID) {
		final Message sendMsg = new Message(MessageType.UI);
		sendMsg.put(MessageItem.UI.OP,6);
		sendMsg.put(MessageItem.UI.CASEFLOWID, caseFlowID);
		sendMsg.put(MessageItem.UI.EXECUTELOGID, executeLogID);
		sendMsg.put("CHANNELNAME", "UI");
		
		log.debug("将要发送消息：" + sendMsg);
		try {
			try
			{
				Thread t = new Thread(
						new Runnable(){
							public void run(){
								new DefaultReplyAdapterHelper(FormatSysProperty(sysInfo)).
									SendToCoreRaw(sendMsg.Export());
							}
						});
				t.start();

			}
			catch (Exception e) {
				log.error(e);
				e.printStackTrace();
				throw new Exception("模拟器执行出现异常");
			}
			
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * 执行业务流
	 * @param sysInfo	当前系统标识
	 * @param logID		日志标识
	 * @param flowid	业务流标识
	 * @param Script	脚本内容
	 */
	public void RunFlow(final GWTSimuSystem sysInfo,String logID, String flowid,String Script, String executeLogId)
	{		
		final Message sendMsg = new Message(MessageType.UI);
		sendMsg.put(MessageItem.UI.OP,4);
		sendMsg.put("TAG",logID);
		sendMsg.put("NAME",flowid);
		sendMsg.put("CODE",Script);
		sendMsg.put("CHANNELNAME", "UI");
		//新加入执行日志ID
		sendMsg.put("EXECUTELOGID", executeLogId);
		sendMsg.put("API", 0);
		
		log.debug("将要发送消息：" + sendMsg);
		try {
			try
			{
				Thread t = new Thread(
						new Runnable(){
							public void run(){
								new DefaultReplyAdapterHelper(FormatSysProperty(sysInfo)).
									SendToCoreRaw(sendMsg.Export());
							}
						});
				t.start();
//				new DefaultReplyAdapterHelper(FormatSysProperty(sysInfo)).
//				SendToCoreRaw(sendMsg.Export());
			}
			catch (Exception e) {
				log.error(e);
				e.printStackTrace();
				throw new Exception("模拟器执行出现异常");
			}
			
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * 获得war包（应用程序）根目录的绝对路径
	 * @return 应用程序根目录的绝对路径
	 */
	public String GetRootPath()
	{
		String path ;
		try {
			path = getClass().getProtectionDomain().getCodeSource()
					.getLocation().toURI().getPath();
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
		if (path.indexOf("WEB-INF/classes") > 0) {
			path = path.substring(0, path.indexOf("WEB-INF/classes"));
		} else {
//			throw new Exception("路径获取错误");
		}
		return path;
	}

	/**
	 * 文件保存
	 * @param fileName      保存的文件名
	 * @param fileContent   保存的文件内容
	 * @throws Exception
	 */
	private void SaveFileInfo(String fileName,String fileContent)  throws Exception 
	{
		File file = new File(GetRootPath() + fileName);
		log.info("文件保存:" + file.getPath());
		FileWriter fw = null;
		try
		{
			fw = new FileWriter(file);
			fw.write(fileContent);
			fw.close();
		}
		catch(Exception ex)
		{
			
		}
		finally
		{
			if(fw != null)
				fw.close();
		}
	}
	
	public InputStreamReader GetFileReader(String fileName) throws UnsupportedEncodingException, FileNotFoundException
	{
		File file = new File(GetRootPath() + fileName);
		log.info("文件名称:" + file.getPath());
		System.out.print(file.getPath());
		
		return new InputStreamReader (new FileInputStream(file),"UTF-8");
	}
	
	/**
	 * 获得文本文件，包括普通的txt,json,dat等文件的内容
	 * @return 文件内容
	 */
	public String GetTxtFileInfo(String fileName)  throws Exception 
	{
		InputStreamReader streamReader = null;
		BufferedReader bufferReader = null;
		StringBuilder fileStr = new StringBuilder();
		try {
			streamReader = GetFileReader(fileName);
			bufferReader=new BufferedReader(streamReader);
			
			String tempString = null;
			while ((tempString = bufferReader.readLine()) != null) {
				if(tempString.trim().isEmpty())
					continue;
				fileStr.append(tempString);
			}
			streamReader.close();
			bufferReader.close();
		} catch (Exception ex) {
			log.error(ex, ex);
			throw ex;
		} 
		finally {
			if (bufferReader != null) {
				try {
					bufferReader.close();
				} catch (IOException e1) {
				}
			}
		}
		return fileStr.toString();
	}
	
	/**
	 * 创建一个临时文件并返回文件对象
	 * @param prefix 前最
	 * @param suffix 后缀名
	 * @return 临时文件对象
	 * @throws IOException IO异常
	 */
	public File CreateTempFile(String prefix, String suffix) throws IOException
	{
		File root = new File(GetRootPath() + "temp/");
		return File.createTempFile(prefix, suffix, root);
	}
	
	/**
	 * 根据文件名称，获得对应文件的输入文件流
	 * @param fileName 临时文件名称
	 * @return  临时文件流
	 * @throws FileNotFoundException 
	 */
	public InputStream GetTempStream(String fileName) throws FileNotFoundException
	{
		File file = new File(GetRootPath() + "temp/" + fileName);
		return new FileInputStream(file);
	}

	
	/**
	 * 执行业务流，调API
	 * @param sysInfo
	 * @param logID
	 * @param flowid
	 * @param script
	 * @param executeLogId
	 * @param userId
	 * @param byApi
	 */
	public void RunFlow(final GWTSimuSystem sysInfo, String logID, String flowid,
			String script, String executeLogId, String userId, boolean byApi) {
		
		final Message sendMsg = new Message(MessageType.UI);
		sendMsg.put(MessageItem.UI.OP,4);
		sendMsg.put("TAG",logID);
		sendMsg.put("NAME",flowid);
		sendMsg.put("CODE",script);
		sendMsg.put("CHANNELNAME", "UI");
		sendMsg.put("USERID", userId);
		sendMsg.put("API", byApi?"1":"0");
		//新加入执行日志ID
		sendMsg.put("EXECUTELOGID", executeLogId);
		
		log.debug("将要发送消息：" + sendMsg);
		try {
			try
			{
				Thread t = new Thread(
						new Runnable(){
							public void run(){
								new DefaultReplyAdapterHelper(FormatSysProperty(sysInfo)).
									SendToCoreRaw(sendMsg.Export());
							}
						});
				t.start();
//				new DefaultReplyAdapterHelper(FormatSysProperty(sysInfo)).
//				SendToCoreRaw(sendMsg.Export());
			}
			catch (Exception e) {
				log.error(e);
				e.printStackTrace();
				throw new Exception("模拟器执行出现异常");
			}
			
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
//	private List<Object> GetIPandPort()
//	{
//		String IP = "127.0.0.1";
//		int Port = 8080;
//		try {
//			String path = GetRootPath();
//
//			File file = new File(path + coreFileName);
//			log.info(file.getPath());
//			BufferedReader reader = null;
//			try {
//				reader = new BufferedReader(new FileReader(file));
//				String tempString = null;
//				while ((tempString = reader.readLine()) != null) {
//					log.info(tempString);
//					String[] splitValue = tempString.split(" ");
//					IP = splitValue[0];
//					Port = Integer.valueOf(splitValue[1]);
//					break;
//				}
//				reader.close();
//			} catch (Exception e) {
//				e.printStackTrace();
//			} finally {
//				if (reader != null) {
//					try {
//						reader.close();
//					} catch (IOException e1) {
//					}
//				}
//			}
//		} catch (Exception e) {
//			log.error(e.getMessage());
//		}
//		List<Object> ipAndPort = new ArrayList<Object>();
//		ipAndPort.add(IP);
//		ipAndPort.add(Port);
//		
//		return ipAndPort;
//	}

}
