package com.dc.tes.fcore;

import java.io.IOException;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;

import com.dc.tes.Config;
import com.dc.tes.Core;
import com.dc.tes.data.IRuntimeDAL;
import com.dc.tes.data.RuntimeDAL;
import com.dc.tes.data.model.SysType;
import com.dc.tes.data.model.User;
import com.dc.tes.data.model.UserRSystem;
import com.dc.tes.util.RuntimeUtils;
import com.dc.tes.util.XmlUtils;



public class AutoRunner {
	
	private static final Log log = LogFactory.getLog(Core.class);
	

	/** 核心基础配置 */
	public static Config config;

	/** 运行时数据访问接口 */
	public static IRuntimeDAL da;
	
		
	/**
	 * 功能核心入口点
	 * 
	 * @param args
	 *            命令行参数
	 */
	public static void main(String[] args) {
		
		try {
			String instanceName = "";
			//if(args.length == 0) {
				Document doc = XmlUtils.LoadXml(RuntimeUtils.OpenResource("base.xml"));
				instanceName = XmlUtils.SelectNodeText(doc, "//config/name");
			//} else {
				//instanceName = args[0];
			//}
			Init(instanceName);
			Login(args);
		} 
		catch (Exception ex) {
			System.out.println("通用模拟器自动调度程序启动失败！错误提示信息：" + ex.getMessage());
			System.exit(-1);
		}

		//轮次
		//DbGet.m_iRoundId = DbGet.GetTestRoundId(DbGet.m_sysType.getSystemId());
		
		//System.out.println("正在删除无效的系统参数及其交易参数配置数据......");
		//int iInvalidSystemParamCountDeleted = DbSet.deleteInvalidSystemParameter();
		//System.out.println("删除无效的系统参数及其交易参数配置数据完成，共删除 "+iInvalidSystemParamCountDeleted+" 个无效的系统参数。");
		
		/*
		 * System.out.println("正在重新检查系统参数配置......");
		StringBuilder sbInvalidSystemParamCount = new StringBuilder(); 
		int iInvalidParamCountFound = DbGet.CheckSystemParameterConfig(sbInvalidSystemParamCount);
		System.out.println("系统参数配置检查已经完成，发现 " + iInvalidParamCountFound + " 个可能无效的系统参数，涉及" + sbInvalidSystemParamCount.toString() + "个交易参数");
		*/
		
		try {
			while (true) {
				if (0 == DbGet.m_user.getIsAdmin()) { //登录用户为系统管理员，所有项目都适用
					List<SysType> sysList = DbGet.getAllValidSystems();
					if (sysList != null) {
						for (int i=0; i<sysList.size();i++) {
							SysType sysType = sysList.get(i);
							if (DbGet.isSystemHasExecutePlan(sysType.getSystemId())) {
								System.out.println("正在处理项目：" + sysType.getSystemName());
								//DbGet.m_sysType = sysType; 
								DbGet.m_sysType = null;
								RunOneSpecifiedSystem(sysType);
							}
						}
					}
				}
				else {
					RunOneSpecifiedSystem(DbGet.m_sysType);
				}
			}
		} 
		catch (Exception ex) {
			System.out.println("通用模拟器自动调度程序执行失败！错误提示信息：" + ex.getMessage());
			System.exit(-1);
		}
		System.out.println("通用模拟器自动调度程序已退出！");
	}
	
	
	private static void RunOneSpecifiedSystem(SysType sysType) {
		
		int iScheduledTaskCount = Scheduler.Run(sysType);
		int iSleepSeconds = 2;
		if (iScheduledTaskCount > 0) { //有执行计划任务
			iSleepSeconds = 2;
		}
		else { //没有执行到任何任务
			iSleepSeconds = 30;
		}
		
		try {
			Thread.sleep(1000 * iSleepSeconds);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	
	public static void Init(String instanceName) {

		// 初始化核心基础配置
		log.info("初始化基础配置...");
		config = new Config();
		log.info("初始化基础配置成功.");
	
		// 初始化数据访问层
		log.info("初始化数据访问层...");
		try {
			da = createRuntimeDAL(instanceName);
		} catch (Exception e) {
			e.printStackTrace();
		}
		log.info("初始化数据访问层成功.");
		System.out.println();
	}

	
	protected static IRuntimeDAL createRuntimeDAL(String instanceName) throws Exception {
		
		return new RuntimeDAL(instanceName, config);
	}
	
	
	public static void Login(String[] args) {
		
		String sUser = "";
		String sPassword = "";
		
		if (!(args.length == 0 || args.length == 1 || args.length == 2)) {
			System.out.println("命令行参数个数不对，参数个数必须为０个（无参数）、１个（登录用户）或者２个（登录用户、登录密码）");
			System.out.println("程序将退出执行!");
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			System.out.print("程序已退出执行!");
			System.exit(-1); //退出执行
		}
		
		User user = null;
		
		if (args.length > 0) {
			sUser = args[0];
			System.out.println("登录用户为：" + sUser);
			user = DbGet.getUserByUserName(sUser);
			if (user == null) {
				System.out.println("您所输入的登录用户不存在，程序将退出执行，请检查后重新启动！");
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				System.out.print("程序已退出执行!");
				System.exit(-1); //退出执行
			}
			if (args.length > 1) {
				sPassword = args[1];	
				if (!DbGet.isUserPasswordMactched(sUser, sPassword)) {
					System.out.println("登录密码错误，程序将退出，请检查后重新启动！");
					try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					System.out.print("程序已退出执行!");
					System.exit(-1); //退出执行
				}
			}
		}
		
		if (args.length == 0) { //没有任何命令行参数
			System.out.print("请输入登录用户，以回车键确认输入：");
			int i = 0;
			
			try {
				while ((i = System.in.read()) != '\n') {
					sUser += (char) i;
				}
			} catch (IOException e) {
				e.printStackTrace();
				System.out.print("程序已退出执行!");
				System.exit(-1); //退出执行
			}
			sUser = sUser.trim();
			
			int iLoginCount = 0;
			user = DbGet.getUserByUserName(sUser);
			while (user == null) {
				System.out.println("您所输入的登录用户不存在！");
				
				if (++iLoginCount >= 3) {
					System.out.println("您已连续三次输错登录用户，程序将退出执行！");
					try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					System.out.print("程序已退出执行!");
					System.exit(-1); //退出执行
				}
				
				System.out.print("请重新输入登录用户，以回车键确认输入：");
				sUser = "";
				try {
					while ((i = System.in.read()) != '\n') {
						sUser += (char) i;
					}
				} catch (IOException e) {
					e.printStackTrace();
					System.out.print("程序已退出执行!");
					System.exit(-1); //退出执行
				}
				sUser = sUser.trim();
				user = DbGet.getUserByUserName(sUser);
			}
		}
		
		if (args.length == 0 || args.length == 1) {
			int j = 0;
			System.out.print("请输入登录密码，以回车键确认输入：");
			try {
				while ((j = System.in.read()) != '\n') {
					sPassword += (char) j;
				}
			} catch (IOException e) {
				e.printStackTrace();
				System.out.print("程序已退出执行!");
				System.exit(-1); //退出执行
			}
			sPassword = sPassword.trim();
			
			int iPwdCount = 0;
			while (!DbGet.isUserPasswordMactched(sUser, sPassword)) {
				
				if (++iPwdCount >= 3) {
					System.out.println("您已连续三次输错密码，程序将退出执行！");
					try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					System.out.print("程序已退出执行!");
					System.exit(-1); //退出执行
				}
				
				System.out.print("密码错，请重新输入密码，并以回车键确认：");
				sPassword = "";
				try {
					while ((j = System.in.read()) != '\n') {
						sPassword += (char) j;
					}
				} catch (IOException e) {
					e.printStackTrace();
					System.out.print("程序已退出执行!");
					System.exit(-1); //退出执行
				}
				sPassword = sPassword.trim();
			}
		}
		
		if (0 == user.getIsAdmin()) {
			System.out.println("登录用户为系统管理员，所有的项目都适用，所有的项目下自动执行计划都会被调度！");
		}
		else {
			List<UserRSystem> sysList = DbGet.getUserSystemByUserId(user.getId());
			if (sysList == null || sysList.size() <= 0) {
				System.out.println("当前用户不属于任何一个被模拟系统，自动调度执行没有意义，程序将退出执行！");
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				System.out.print("程序已退出执行!");
				System.exit(-1); //退出执行
			}
			else if (sysList.size() == 1) {
				String sSystemId = sysList.get(0).getSystemid();
				DbGet.m_sysType = DbGet.getSysTypeBySysTypeId(sSystemId);
				System.out.println();
				System.out.println("登录成功，当前被模拟系统为：" + DbGet.m_sysType.getSystemName());
			}
			else if (sysList.size() > 1) { //对应多个模拟系统，需要进行选择
				System.out.println();
				System.out.println("系统ID  被模拟系统名称" );
				
				for (int k=0; k<sysList.size(); k++) {
					UserRSystem userSystem = sysList.get(k);
					String sSystemId = userSystem.getSystemid();
					SysType sysType = DbGet.getSysTypeBySysTypeId(sSystemId);
					System.out.printf("%4d    %s", Integer.parseInt(sysType.getSystemId()), sysType.getSystemName());
					System.out.println();
				}
				
				System.out.println();
				System.out.print("请选择一个系统ID，并以回车键确认：");
				
				String sSystemId = "";
				int l = 0;
				try {
					while ((l = System.in.read()) != '\n') {
						sSystemId += (char) l;
					}
				} catch (IOException e) {
					e.printStackTrace();
					System.out.print("程序已退出执行!");
					System.exit(-1); //退出执行
				}	
				sSystemId = sSystemId.trim();
				
				boolean isSelectionOk = false; 
				for (int k=0; k<sysList.size(); k++) {
					UserRSystem userSystem = sysList.get(k);
					if (sSystemId.equals(userSystem.getSystemid())) {
						DbGet.m_sysType = DbGet.getSysTypeBySysTypeId(sSystemId);
						System.out.println();
						System.out.println("登录成功，当前被模拟系统为：" + DbGet.m_sysType.getSystemName());
						isSelectionOk = true;
						break;
					}					
				}
				if (!isSelectionOk) {
					System.out.println("您所选择的系统ID无效，程序将退出执行！");
					try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					System.out.print("程序已退出执行!");
					System.exit(-1); //退出执行
				}
			}
		}

		DbGet.m_user = user;
	
		System.out.println();
		System.out.println("自动调度程序正在执行中......");
		System.out.println();
	}
	
}
