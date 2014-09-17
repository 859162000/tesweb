package com.dc.tes.ui.util;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.CDATA;
import org.jdom.Content;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;

import com.dc.tes.data.DALFactory;
import com.dc.tes.data.IDAL;
import com.dc.tes.data.model.Adapter;
import com.dc.tes.data.model.Channel;
import com.dc.tes.data.model.MsgPacker;
import com.dc.tes.data.model.SysType;
import com.dc.tes.data.model.TransRecognizer;
import com.dc.tes.data.op.Op;
import com.dc.tes.ui.client.enums.CsType;
import com.dc.tes.ui.client.model.GWTSimuSystem;
import com.dc.tes.ui.server.HelperService;

public class SystemDeploy {

	private static final Log log = LogFactory.getLog(SystemDeploy.class);
	
	private String classesPath = "";
	private String webBasePath = "";
	private String dbFileName = "base.xml";
	private String webFileName = "web.xml";
	
	private String remoteSenderClass = "com.dc.tes.channel.adapter.%s.RemoteSender";
	private String remoteListenerClass = "com.dc.tes.channel.adapter.%s.RemoteListener";
	
	SAXBuilder saxBuilder = new SAXBuilder();
	IDAL<Channel> channelDao = DALFactory.GetBeanDAL(Channel.class);
	IDAL<SysType> systemDao = DALFactory.GetBeanDAL(SysType.class);
	LinkedHashMap<String, String> adapterMap = new LinkedHashMap<String, String>();
	
	//数据库配置
	class DbConfig{
		public String data = "";
		public String ConnUrl = "";
		public String UserName = "";
		public String Password = "";
		
		public DbConfig(String data, String url, String uid, String pwd){
			this.data = data;
			this.ConnUrl = url;
			this.UserName = uid;
			this.Password = pwd;
		}
	}
	
	//监控服务配置
	class MoniConfig{
		public String ip = "";
		public String port = "";
		
		public MoniConfig(String ip, String port){
			this.ip = ip;
			this.port = port;
		}
	}
	
	class DeployConfig{
		public String sysName = "";
		public String baseXmlContent = "";
		
		public DeployConfig(String sysName, String baseXml){
			this.sysName = sysName;
			this.baseXmlContent = baseXml;
		}
	}
	
	public SystemDeploy(){
		classesPath = Thread.currentThread().getContextClassLoader().getResource("/").getPath();
		try {
			classesPath = java.net.URLDecoder.decode(classesPath,"utf-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}     
		webBasePath = new HelperService().GetRootPath();
	}
	
	@SuppressWarnings("unchecked")
	public DeployConfig SaveBaseConfig(String sysId, List<Channel> dataList, String defChannelName){
		
		DeployConfig deployConfig = null;
		SysType sys = null;
		adapterMap.clear();
		
		try{
			sys = systemDao.Get(Op.EQ(GWTSimuSystem.N_SystemID, sysId));
			sys.setChannel(defChannelName);
			systemDao.Edit(sys);
		}catch(Exception ex){
			log.error("读取系统信息失败");
		}
		DbConfig dbConfig = GetDbConfig();
		MoniConfig moniConfig = GetMoniConfig();
		
		Element root = new Element("config");
		Element baseroot = new Element("config");
		//Element adapterroot = new Element("config");
		
		//name
		root.addContent(new Element("name").setText(sys.getSystemName()));
		baseroot.addContent(new Element("name").setText(sys.getSystemName()));
		//port
		root.addContent(new Element("port").setText(String.valueOf(sys.getPortnum())));
		baseroot.addContent(new Element("port").setText(String.valueOf(sys.getPortnum())));
		//data
		root.addContent(new Element("data").setText(dbConfig.data));
		baseroot.addContent(new Element("data").setText(dbConfig.data));
		//conn
		root.addContent(new Element("conn").setText(dbConfig.ConnUrl));
		baseroot.addContent(new Element("conn").setText(dbConfig.ConnUrl));
		//username
		root.addContent(new Element("username").setText(dbConfig.UserName));
		baseroot.addContent(new Element("username").setText(dbConfig.UserName));
		//password
		root.addContent(new Element("password").setText(dbConfig.Password));
		baseroot.addContent(new Element("password").setText(dbConfig.Password));
		//monitor
		Element moni = new Element("monitor");
		//ip
		moni.addContent(new Element("host").setText(moniConfig.ip));
		//port
		moni.addContent(new Element("port").setText(moniConfig.port));
		root.addContent(moni);
		Element baseMoni = (Element)moni.clone();
		baseroot.addContent(baseMoni);
		
		//channel
		for(Channel channel : dataList){
			Element cElement = new Element("channel");
			
			//name
			cElement.setAttribute("name", channel.getName());
			
			Adapter adapter = channel.getAdapter();
			if(adapter != null){
				
				String adapterName = channel.getName() + "_" + adapter.getProtocoltype();
				Element adapterroot = new Element("config");
				
				Element aElement = new Element("Adapter");
				
				if(adapter.getCstype() == CsType.Client.getDbValue()){
					
					adapterName += "_" + channel.getSendAdapterIP();
					
					//核心配置
					//class
					String classStr = "";
					if(adapter.getProtocoltype().toLowerCase().indexOf("mq") >= 0){
						classStr = String.format(remoteSenderClass, "mq");
					//	adapterMap.put(adapterName, channel.getAdaptercfginfo());
					}
					else if(adapter.getProtocoltype().toLowerCase().indexOf("tuxedo") >= 0){
						classStr = String.format(remoteSenderClass, "tuxedo");
				//		adapterMap.put(adapterName, channel.getAdaptercfginfo());
					}
					else
						classStr = String.format(remoteSenderClass, adapter.getProtocoltype().toLowerCase());
					cElement.setAttribute("class", classStr);
			
					//适配器配置
					//adapterType
					aElement.addContent(new Element("adapterType").setText("REQUEST"));
					//host
					aElement.addContent(new Element("host").setText(sys.getIpadress()));
					//UpPort
					aElement.addContent(new Element("UpPort").setText(String.valueOf(channel.getSendAdapterPort())));
				}else{
					aElement.addContent(new Element("adapterType").setText("REPLY"));
					//class
					String classStr = "";
					if(adapter.getProtocoltype().toLowerCase().indexOf("mq") >= 0){
						classStr = String.format(remoteListenerClass, "mq");
					//	adapterMap.put(adapterName, channel.getAdaptercfginfo());
					}
					else if(adapter.getProtocoltype().toLowerCase().indexOf("tuxedo") >= 0){
						classStr = String.format(remoteListenerClass, "tuxedo");
					//	adapterMap.put(adapterName, channel.getAdaptercfginfo());
					}
					else
						classStr = String.format(remoteListenerClass, adapter.getProtocoltype().toLowerCase());
					cElement.setAttribute("class", classStr);
				}
				
				//config
				CDATA cdata = new CDATA(channel.getAdapterCfgInfo());
				cElement.addContent(new Element("config").addContent(cdata));
				
				root.addContent(cElement);
				
				//adapterPlugIn
				aElement.addContent(new Element("adapterPlugIn").setText(adapter.getPluginname()));
				//CHANNELNAME
				aElement.addContent(new Element("CHANNELNAME").setText(channel.getName()));
				//coreIP
				aElement.addContent(new Element("coreIP").setText(sys.getIpadress()));
				//corePort
				aElement.addContent(new Element("corePort").setText(String.valueOf(sys.getPortnum())));
				
				adapterroot.addContent(aElement);
				
				Document adapterDoc = new Document(adapterroot);
				XMLOutputter domstream = new XMLOutputter();
				String output = domstream.outputString(adapterDoc);
				if(!adapterMap.containsKey(adapterName))
					adapterMap.put(adapterName, output);
			}
			
			//txcode
			TransRecognizer reccode = channel.getTransRecognizer();
			if(reccode != null){
				Element rElement = new Element("txcode");
				//name
				rElement.setAttribute("name", reccode.getName() + "_" + RandomStringUtils.randomAlphabetic(6));
				//class
				rElement.setAttribute("class", reccode.getClassname());
				//channel
				rElement.setAttribute("channel", channel.getName());
				
				try {
					InputStream is = new ByteArrayInputStream(channel.getRecognizerCfgInfo().getBytes());
					Document doc = saxBuilder.build(is);
					List<Content> contentList = (List<Content>)doc.getRootElement().getChildren();
					for(Content c : contentList){
						Content clone = (Content)c.clone();
						clone.detach();
						rElement.addContent(clone);
					}
					
				} catch (JDOMException e) {
					e.printStackTrace();
					log.error("交易识别配置参数解析失败");
				} catch (Exception e) {
					e.printStackTrace();
					log.error("交易识别配置参数解析失败");
				}
				
				root.addContent(rElement);
			}
			
			//pack
			MsgPacker pack = channel.getPackChannel();
			if(pack != null){
				Element pElement = new Element("pack");
				//name
				pElement.setAttribute("name", "pack_" + RandomStringUtils.randomAlphabetic(6));
				//class
				pElement.setAttribute("class", pack.getClassname());
				//channel
				pElement.setAttribute("channel", channel.getName());
				//style
				pElement.addContent(new Element("style").setText(pack.getStylename()));
				
				root.addContent(pElement);
			}
			
			//unpack
			MsgPacker unpack = channel.getUnpackChannel();
			if(unpack != null){
				Element pElement = new Element("unpack");
				//name
				pElement.setAttribute("name", "unpack_" + RandomStringUtils.randomAlphabetic(6));
				//class
				pElement.setAttribute("class", unpack.getClassname());
				//channel
				pElement.setAttribute("channel", channel.getName());
				//rule
				pElement.addContent(new Element("rule").setText(unpack.getStylename()));
				
				root.addContent(pElement);
			}
		}
		
		Document baseDoc = new Document(root);
		Document coreDoc = new Document(baseroot);
		//Document adapterDoc = new Document(adapterroot);
		XMLOutputter domstream = new XMLOutputter();
		String content = domstream.outputString(baseDoc);
		String coreBase = domstream.outputString(coreDoc);
		//String comLayer = domstream.outputString(adapterDoc);
//		System.out.println(content);
//		log.info(content);
		
		try {
			sys.setBasecfg(content);
			systemDao.Edit(sys);
		} catch (Exception e) {
			e.printStackTrace();
			log.error("系统base.xml保存失败");
		}
		
		deployConfig = new DeployConfig(sys.getSystemName(), coreBase);
		return deployConfig;
	}
	
	public String Deploy(String sysId, List<Channel> dataList, String defChannelName){
		
		DeployConfig deployConfig = this.SaveBaseConfig(sysId, dataList, defChannelName);
		
		String src = webBasePath + "deploy\\template\\";
		String desc = webBasePath + "deploy\\";
		String srcCorePath = src + "core";
		String srcAdapterPath = src + "adapter";
		
		try {
			FileSystem fs = new FileSystem();
			
			String dataStr = new SimpleDateFormat("yyyyMMdd_hhmmss").format(new Date()).toString();
			desc += dataStr + "_" + deployConfig.sysName + "\\";
			File dir = new File(desc);
			if(!dir.exists())
				dir.mkdir();
			String descCorePath = desc + "core";
			//新式共享一套
			//fs.xCopy(srcCorePath, descCorePath);
			new File(descCorePath).mkdir();
			String baseXmlPath = desc + "\\core\\base.xml";
			File baseXML = new File(baseXmlPath);
			baseXML.createNewFile();
			FileOutputStream os = new FileOutputStream(baseXmlPath);
			os.write(deployConfig.baseXmlContent.getBytes("utf-8"));
			os.flush();
			os.close();
			
			//动态创建run.bat等
			createRun(deployConfig.sysName,descCorePath);
			
			log.info("核心部署完成");
			
			//每个适配器放在一个单独的文件夹里
			for(Entry<String, String> entity : adapterMap.entrySet()){
			
				String descAdapterPath = desc + entity.getKey();
//				
//				if(entity.getKey().toLowerCase().indexOf("mqclient") >= 0){
//					srcAdapterPath = src + "mqclient";
//					
//					fs.xCopy(srcAdapterPath, descAdapterPath);
//					
//					String cltMQPath = descAdapterPath + "\\agent\\etc\\CltMQ.cfg";
//					File CltMQ = new File(cltMQPath);
//					CltMQ.createNewFile();
//					
//					os = new FileOutputStream(cltMQPath);
//					os.write(entity.getValue().getBytes("utf-8"));
//					os.flush();
//					os.close();
//					
//				}else if(entity.getKey().toLowerCase().indexOf("mqserver") >= 0){
//					srcAdapterPath = src + "mqserver";
//					
//					fs.xCopy(srcAdapterPath, descAdapterPath);
//					
//					String cltMQPath = descAdapterPath + "\\mq\\etc\\MQadapter.cfg";
//					File CltMQ = new File(cltMQPath);
//					CltMQ.createNewFile();
//					
//					os = new FileOutputStream(cltMQPath);
//					os.write(entity.getValue().getBytes("utf-8"));
//					os.flush();
//					os.close();
//					
//				}else if(entity.getKey().toLowerCase().indexOf("tuxedoclient") >= 0){
//					srcAdapterPath = src + "tuxedoclient";
//
//					
//					fs.xCopy(srcAdapterPath, descAdapterPath);
//					
//					String cltTuxPath = descAdapterPath + "\\agent\\etc\\CltTux.cfg";
//					File CltTux = new File(cltTuxPath);
//					CltTux.createNewFile();
//					
//					os = new FileOutputStream(cltTuxPath);
//					os.write(entity.getValue().getBytes("utf-8"));
//					os.flush();
//					os.close();
//				}else if(entity.getKey().toLowerCase().indexOf("tuxedoserver") >= 0){
//					srcAdapterPath = src + "tuxedoserver";
//
//					
//					fs.xCopy(srcAdapterPath, descAdapterPath);
//					
//					String srvTuxPath = descAdapterPath + "\\tux\\etc\\TUXadapter.cfg";
//					File SrvTux = new File(srvTuxPath);
//					SrvTux.createNewFile();
//					
//					os = new FileOutputStream(srvTuxPath);
//					os.write(entity.getValue().getBytes("utf-8"));
//					os.flush();
//					os.close();
//				}else{
					srcAdapterPath = src + "adapter";
				
					//fs.xCopy(srcAdapterPath, descAdapterPath);
					new File(descAdapterPath).mkdir();
					String comLayerPath = descAdapterPath + "\\ComLayer.config.xml";
					
					File comLayerFile = new File(comLayerPath);
					comLayerFile.createNewFile();
					
					os = new FileOutputStream(comLayerPath);
					os.write(entity.getValue().getBytes("utf-8"));
					os.flush();
					os.close();
					
					createStartAdpater(descAdapterPath,deployConfig.sysName+"_"+entity.getKey());
				}
//			}
			log.info("适配器部署完成");
			
			return desc;
			
		} catch (Exception e) {
			log.error("文件拷贝失败");
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}


	private void createStartAdpater(String descAdapterPath, String adpaterName) throws IOException {
		// TODO Auto-generated method stub
		//旧式启动文件
		String startadapterPath = descAdapterPath + "\\startAdapters_old.bat";
		String content_old = "@echo off \r\ntitle "+adpaterName+"\r\nset cp=.;\r\nsetlocal enabledelayedexpansion\r\n" +
                         "for /f \"delims=\" %%i in ('dir /b /a-d *.jar') do set cp=!cp!%%i;\r\n\r\n" + 
                         "java -Dlog4j.configuration=file:log4j.properties  -classpath %cp% com.dc.tes.adapter.startup.StartUpEntry\r\n\r\n" +
                         "pause";
		content_old = content_old.replace("##", adpaterName);
		File startadapter = new File(startadapterPath);
		startadapter.createNewFile();
		FileOutputStream os = new FileOutputStream(startadapterPath);
		os.write(content_old.getBytes("gbk"));
		os.flush();
		os.close();
		
		//新式启动文件
		startadapterPath = descAdapterPath + "\\startAdapters.bat";
		String content = "@echo off\r\ntitle "+adpaterName+"\r\nset cp=..\\..\r\n"+
						"setlocal enabledelayedexpansion\r\n"+
						"for /f \"delims=\" %%i in ('dir /b /a-d %cp%\\adapterlib\\*.jar') do set cp=!cp!;%cp%\\adapterlib\\%%i;\r\n\r\n"+
						"java -cp %cp% com.dc.tes.adapter.startup.StartUpEntry\r\n\r\n"+
						"pause";
		startadapter = new File(startadapterPath);
		startadapter.createNewFile();
		os = new FileOutputStream(startadapterPath);
		os.write(content.getBytes("gbk"));
		os.flush();
		os.close();
		
	}

	private void createRun(String sysName, String descCorePath) throws IOException {
		// TODO Auto-generated method stub
		
		//startcore.bat
		String startcorePath = descCorePath+"\\startcore_old.bat";
		String content = "@echo off \r\ntitle "+sysName+"\r\nset cp=.\r\nsetlocal enabledelayedexpansion\r\n" +
				"for /f \"delims=\" %%i in ('dir /b /a-d lib\\*.jar') do set cp=!cp!;lib\\%%i\r\n\r\n" +
				"java -cp %cp% com.dc.tes.fcore.FCore \r\n\r\npause";

		File startcore = new File(startcorePath);
		startcore.createNewFile();
		FileOutputStream os = new FileOutputStream(startcorePath);
		os.write(content.getBytes("gbk"));
		os.flush();
		os.close();
		
		startcorePath = descCorePath+"\\startcore.bat";
		content = "@echo off \r\ntitle "+sysName+"\r\nset cp=..\\..\r\n"+
				   "setlocal enabledelayedexpansion\r\n"+
				   "for /f \"delims=\" %%i in ('dir /b /a-d %cp%\\corelib\\*.jar') do set cp=!cp!;%cp%\\corelib\\%%i\r\n\r\n"+
				   "java -cp %cp% com.dc.tes.fcore.FCore "+sysName+"\r\n\r\n"+
				   "pause";

		startcore = new File(startcorePath);
		startcore.createNewFile();
		os = new FileOutputStream(startcorePath);
		os.write(content.getBytes("gbk"));
		os.flush();
		os.close();
		
		//autoStart
		startcorePath = descCorePath+"\\autoschedule.bat";
		content = "@echo off \r\ntitle "+sysName+"\r\nset cp=..\\..\r\n"+
				   "setlocal enabledelayedexpansion\r\n"+
				   "for /f \"delims=\" %%i in ('dir /b /a-d %cp%\\corelib\\*.jar') do set cp=!cp!;%cp%\\corelib\\%%i\r\n\r\n"+
				   "java -cp %cp% com.dc.tes.fcore.AutoRunner "+sysName+"\r\n\r\n"+
				   "pause";

		startcore = new File(startcorePath);
		startcore.createNewFile();
		os = new FileOutputStream(startcorePath);
		os.write(content.getBytes("gbk"));
		os.flush();
		os.close();
		
		//run.bat 
		String runPath = descCorePath+"\\run.bat";
		StringBuilder sb = new StringBuilder();
		sb.append("echo 正在启动核心...... \r\nstart startcore.bat \r\nping /n 10 127.1 >nul \r\n");
		
		String template = "echo 正在启动适配器...... \r\ncd .. \r\ncd ## \r\nstart startAdapters.bat\r\nping /n 8 127.1 >nul\r\n";
		for(Entry<String, String> entity : adapterMap.entrySet()){
			String newContent = template.replaceAll("##", entity.getKey());
			sb.append(newContent);
		}
		sb.append("exit");
		
		File run = new File(runPath);
		run.createNewFile();
		os = new FileOutputStream(runPath);
		os.write(sb.toString().getBytes("gbk"));
		os.flush();
		os.close();
		
		//stop.bat
		String stopPath = descCorePath + "\\stop.bat";
		sb = new StringBuilder();
		template = "taskkill /FI \"WINDOWTITLE eq ##\"\r\n";
		String template2 = "taskkill /FI \"WINDOWTITLE eq 管理员:  ##\"\r\n";
		sb.append(template.replaceAll("##", sysName));
		sb.append(template2.replaceAll("##", sysName));
		
		for(Entry<String, String> entity : adapterMap.entrySet()){
			sb.append(template.replaceAll("##", sysName+"_"+entity.getKey()));
			sb.append(template2.replaceAll("##", sysName+"_"+entity.getKey()));
			
		}
		sb.append("exit");
		
		File stop = new File(stopPath);
		stop.createNewFile();
		os = new FileOutputStream(stopPath);
		os.write(sb.toString().getBytes("gbk"));
		os.flush();
		os.close();
	}

	private DbConfig GetDbConfig(){
		
		try{
			 String dbConfigPath = classesPath + dbFileName;
			 System.out.println(dbConfigPath);
			 File file = new File(dbConfigPath);
			 if(!file.exists()){
				 String errMsg = String.format("未找到数据库配置文件，文件位置：%s", dbConfigPath);
				 log.error(errMsg);
			 }
			 
			 
			 Document doc = saxBuilder.build(file);
			 Element root = doc.getRootElement();
			 
			 String data = root.getChildText("data");
			 String url = root.getChildText("conn");
			 String uid = root.getChildText("username");
			 String pwd = root.getChildText("password");
			 
			 return new DbConfig(data, url, uid, pwd);
			 
		 }
		catch(NullPointerException ex){
			String errMsg = String.format("数据库配置信息所在文件路径为空：%s", ex.getMessage());
			 log.error(errMsg);
		}
		catch(JDOMException ex){
			String errMsg = String.format("数据库配置信息所在文件格式不正确：%s", ex.getMessage());
			 log.error(errMsg);
		}
		catch(Exception ex){
			 String errMsg = String.format("读取数据库配置信息发生异常：%s", ex.getMessage());
			 log.error(errMsg);
		 }
		 
		return null;
	}
	
	@SuppressWarnings("unchecked")
	private MoniConfig GetMoniConfig(){
		
		try{
			 String temp = classesPath.substring(0, classesPath.length() - 1);
			 String web_infPath = classesPath.substring(0, temp.lastIndexOf("/") + 1);
			 String webConfigPath = web_infPath + webFileName;

			 System.out.println(webConfigPath);
			 File file = new File(webConfigPath);
			 if(!file.exists()){
				 String errMsg = String.format("未找到web.xml配置文件，文件位置：%s", webConfigPath);
				 log.error(errMsg);
			 }
			 String ip = "";
			 String port = "";
			 
			 Document doc = saxBuilder.build(file);
			 Element root = doc.getRootElement();
			 
		     Namespace ns = Namespace.getNamespace("http://java.sun.com/xml/ns/javaee");
			 List<Element> servlets = (List<Element>)root.getChildren("servlet",ns);
			 for(Element child : servlets){
				 if(child.getChildText("servlet-name",ns).equals("MonitorServlet")){
					 List<Element> params = (List<Element>)child.getChildren("init-param",ns);
					 for(Element param : params){
						 if(param.getChildText("param-name",ns).equals("monitorPort")){
							 port = param.getChildText("param-value",ns);
							 break;
						 }
					 }
				 }
			 }
			 
			 InetAddress address = InetAddress.getLocalHost();
			 ip = address.getHostAddress();
			 
			 return new MoniConfig(ip, port);
			 
		 }

		catch(NullPointerException ex){
			String errMsg = String.format("监控服务配置信息所在文件路径为空：%s", ex.getMessage());
			 log.error(errMsg);
		}
		catch(JDOMException ex){
			String errMsg = String.format("监控服务配置信息所在文件格式不正确：%s", ex.getMessage());
			 log.error(errMsg);
		}catch(Exception ex){
			 String errMsg = String.format("读取数据库配置信息发生异常：%s", ex.getMessage());
			 log.error(errMsg);
		 }
		 
		return null;
	}
}
