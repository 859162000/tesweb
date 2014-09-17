package com.dc.tes.fcore;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;

import com.dc.tes.Config;
import com.dc.tes.Core;
import com.dc.tes.data.DALFactory;
import com.dc.tes.data.IDAL;
import com.dc.tes.data.IRuntimeDAL;
import com.dc.tes.data.RuntimeDAL;
import com.dc.tes.data.model.Case;
import com.dc.tes.data.model.CaseFlow;
import com.dc.tes.data.model.CaseFlowInstance;
import com.dc.tes.data.model.CaseInstance;
import com.dc.tes.data.model.CaseParameterExpectedValue;
import com.dc.tes.data.model.CaseRunStatistics;
import com.dc.tes.data.model.CaseRunUserStats;
import com.dc.tes.data.model.ExecuteLog;
import com.dc.tes.data.model.FactorChangeStatistics;
import com.dc.tes.data.model.FactorChangeUserStats;
import com.dc.tes.data.model.SysType;
import com.dc.tes.data.model.SystemDynamicParameter;
import com.dc.tes.data.model.Transaction;
import com.dc.tes.data.model.TransactionDynamicParameter;
import com.dc.tes.data.model.User;
import com.dc.tes.data.model.UserRSystem;
import com.dc.tes.data.op.Op;
import com.dc.tes.util.RuntimeUtils;
import com.dc.tes.util.XmlUtils;



public class RunStat {
	
	public static class Case_Run_UserStats {
		int RunUserId = 0;
		int TotalRunCaseFlowCount=0;
		int TotalPassedCaseFlowCount=0;
		int TotalRunCaseCount=0;
		float CaseFlowPassRate=0;
		Date FirstRunTime=null;
		Date LastRunTime=null;
		int	CreatedCaseCount = 0;
		int	CreatedCaseFlowCount = 0;
		int	CreatedSysParamCount = 0;
		int	CreatedTransactionCount = 0;
		int	ModifiedCaseCount = 0;
		int	ModifiedCaseFlowCount = 0;
		int	ModifiedSysParamCount = 0;
		int	ModifiedTransactionCount = 0;
	}
	
	public static class StatsConfig {
		static boolean Stat_Specified_Period = false;
		static boolean autostat_on = false;
		static boolean autostat_daily = false;
		static boolean autostat_monthly = false;
		static boolean autostat_quaterly = false;
		static boolean autostat_halfyearly = false;
		static boolean autostat_yearly = false;
		static boolean Stat_All_Sys = false;
	}
	
	//用户执行用例统计列表
	private static Map<String, Case_Run_UserStats> m_userStatsList = new HashMap<String, Case_Run_UserStats>();
		
	private static final Log log = LogFactory.getLog(Core.class);

	/** 核心基础配置 */
	public static Config config;

	/** 运行时数据访问接口 */
	public static IRuntimeDAL da;
	
	
    /*public enum StartupTag
    {
        _v, _r
    };*/
	
    public static boolean[] m_isStartupTagOn = { false, false };  //启动选项
    
    public static String m_strBeginDay;              //起始日期
    public static String m_strEndDay;                //截止日期
    public static String m_strYearMon;               //统计月份

    public static CaseRunStatistics m_crs = null;
    public static SysType m_SpecifiedSysType = null;

    public static String m_InstanceName;              //核心应用名称
    public static int m_iSystemId = 0;
    public static boolean m_bInitFlag = false;
		

    /**
	 * 统计入口点
	 * 
	 * @param args
	 *            命令行参数
	 */
	public static void main(String[] args) {
		
		try {
			ProcessMainArgs(args);		
			//Login(args);
		} 
		catch (Exception ex) {
			System.out.println("通用模拟器后台统计程序启动失败！错误提示信息：" + ex.getMessage());
			System.exit(-1);
		}

        if (m_isStartupTagOn[0]) {
            display_version();
            System.out.println("\n按回车键退出...");
			Scanner in = new Scanner(System.in);
			String readLine = in.nextLine();
            return;
        }
        
        //获取系统配置
        GetStaticalConfigurations();

   		if (!Init(m_InstanceName)) {
   			System.out.println("数据库初始化出错，请确保提供一个有效的系统名称<SpecifiedSystemName>（即使不统计指定系统也要提供一个有效的系统名称）。");
   			System.out.println("程序已退出执行。");
   			return ;
   		}

    	//需要统计指定月份
        if (StatsConfig.Stat_Specified_Period) {
        	GetSpecifiedPeriodStaticalConfigurations();
        	statsQualifiedSystem(1);
        }
        
        //需要自动调度执行
        if (StatsConfig.autostat_on) {
        	//计划任务执行    
            //SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
            //Date date = new Date();
           
        	//昨天是几号？
            String strPreviousDate = getPreviousDate();
            
            //按天统计？
            if (StatsConfig.autostat_daily) {
            	m_strEndDay = strPreviousDate;
            	m_strBeginDay = m_strEndDay;
                m_strYearMon = m_strEndDay;
                statsQualifiedSystem(2);
            }
            
            //当月的最后一天？
            if (isLastDayOfMonthJustPast()) {
            	strPreviousDate = getPreviousDate();
                //String strCurrentDate = sdf.format(date);               
                m_strEndDay = strPreviousDate;
            	m_strYearMon = m_strEndDay.substring(0, 6);
                if (StatsConfig.autostat_monthly) {
                	//当月
                    m_strBeginDay = m_strYearMon + "01";
                	//tryStatOneGivenPeriod(iEntry);
                    statsQualifiedSystem(2);
                }
                //季度的最后一个月？
                if (isLastMonthOfQuater(m_strYearMon)) {
                	if (StatsConfig.autostat_quaterly) {
                		//当季
	                    m_strBeginDay = GetFirstDayOfCurrentQuater();
	                    m_strYearMon = GetQuaterByGivenYearMon(m_strYearMon).toString();
	                    //tryStatOneGivenPeriod(iEntry);
	                    statsQualifiedSystem(2);
	                }
                	if (StatsConfig.autostat_halfyearly && isHalfYearEnd(m_strEndDay)) {
                		//半年数据统计
	                    m_strBeginDay = GetFirstDayOfCurrentHalfYear(m_strEndDay);
	                    m_strYearMon = GetHalfYear(m_strEndDay);
	                    //tryStatOneGivenPeriod(iEntry);
	                    statsQualifiedSystem(2);
	                }
                	if (StatsConfig.autostat_yearly && isYearEnd(m_strEndDay)) {
                		//全年数据统计
	                    //m_strEndDay = strCurrentDate;
	                    m_strYearMon = m_strEndDay.substring(0, 4);
	                    m_strBeginDay = m_strYearMon + "0101";
	                    //tryStatOneGivenPeriod(iEntry);
	                    statsQualifiedSystem(2);
	                }
                }
            } //if
        } //if
        
		System.out.println("通用模拟器后台统计程序已退出！");
	}
	
	
	public static void statsQualifiedSystem(int iEntry) {
		
    	if (!StatsConfig.Stat_All_Sys) {
    		//只统计指定的项目
    		m_SpecifiedSysType = DbGet.getSystemBySystemName(m_InstanceName);
    		if (m_SpecifiedSysType == null) {
    			System.out.println("指定的系统名称[" + m_InstanceName + "]不存在， 请检查！");
    			System.out.println("程序已退出执行。");
    			return;
    		}
    		m_InstanceName = m_SpecifiedSysType.getSystemName();
    		m_iSystemId = Integer.parseInt(m_SpecifiedSysType.getSystemId());
        	tryStatOneGivenPeriod(iEntry);        		
    	}
    	else {
    		List<SysType> sysList = DbGet.getAllValidSystems();
    		if (sysList != null) {
    			for (int i=0; i<sysList.size(); i++) {
    				SysType sysType = sysList.get(i);
    				m_InstanceName = sysType.getSystemName();
    				m_iSystemId = Integer.parseInt(sysType.getSystemId());
    				tryStatOneGivenPeriod(iEntry);
    			}
    		}
    	}
	}
	
	public static boolean Init(String instanceName) {

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
		m_bInitFlag = true;

		return true;
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
		
		List<UserRSystem> sysList = DbGet.getUserSystemByUserId(user.getId());
		if (sysList == null || sysList.size() <= 0) {
			System.out.println("当前用户不属于任何一个被模拟系统，统计没有意义，程序将退出执行！");
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

		DbGet.m_user = user;
	
		System.out.println();
		System.out.println("后台统计程序正在执行中......");
		System.out.println();
	}
	

    private static void ProcessMainArgs(String[] args)
    {
        //处理输入参数
        for (int i = 0; i < args.length; i++)
        {
            String tag_para = args[i].toString();
            if (tag_para.length() == 1) {
            	
            }
            else if (tag_para.length() > 2)
            {
                display_usage();
                System.out.println("\n按回车键退出...");
                Scanner in = new Scanner(System.in);
                String readLine = in.nextLine();  
                System.exit(-1);
            }
            String tag = tag_para.substring(0, 2);
            if (tag.toLowerCase().equals("-v"))
            	m_isStartupTagOn[0] = true;
            else if (tag.toLowerCase().equals("-r"))
            	m_isStartupTagOn[1] = true;
            /*else if (tag.toLowerCase().equals("-a"))
            	m_isStartupTagOn[2] = true;
            else if (tag.toLowerCase().equals("-r"))
            	m_isStartupTagOn[3] = true;*/
			else {
				display_usage();
				System.out.println("\n按回车键退出...");
				Scanner in = new Scanner(System.in);
				String readLine = in.nextLine();
				System.exit(-1);
			}
        }
    }

    public static void display_usage()
    {
        System.out.println("通用模拟器后台统计程序的用法说明：\n");
        System.out.println("-v：显示程序版本信息");
        System.out.println("无参数 ：按照配置文件中的配置信息生成后台统计数据");
        System.out.println("-r：重新统计当期的案例执行和要素变更数据");
        //System.out.println("-a：自动统计当时（当天、当月和当季）的数据");
        //System.out.println("不带参数：直接统计当期的案例执行和要素变更数据");
    }
    
    public static void display_version()
    {
        String strBasicVersion = "1.2.3";
       System.out.println("程序版本号：" + strBasicVersion);
    }
    
    

    public static boolean checkBeginDay()
    {
        if (m_strBeginDay == null || m_strBeginDay.trim() == "")
        {
        	System.out.println("统计区间的起始日期不能为空！");
        	//System.exit(-1); //退出执行
        	return false;
        }
        try
        {
        	SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        	Date date = sdf.parse(m_strBeginDay);
        }
        catch (Exception e)
        {
            System.out.println("给定的参数[统计起始日期]为日期无效！" + e.getMessage());
            System.out.println("日期的格式必须为yyyymmdd，如20130801");
            return false;
        }

        return true;
    }
    
    public static boolean checkEndDay()
    {
        if (m_strEndDay == null || m_strEndDay.trim() == "")
        {
        	System.out.println("统计区间的截止日期不能为空！");
        	//System.exit(-1); //退出执行
        	return false;
        }
        try
        {
        	SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        	Date date = sdf.parse(m_strEndDay);
        }
        catch (Exception e)
        {
            System.out.println("给定的参数[统计截止日期]为日期无效！" + e.getMessage());
            System.out.println("日期的格式必须为yyyymmdd，如20130801");
            return false;
        }

        return true;
    }

    //1: 配置了且正确；0：未配置；-1：配置格式错误
    public static int checkStaticalMonth()
    {
        if (m_strYearMon == null || m_strYearMon.trim() == "")
        {
            return 0;
        }
        String strYear = m_strYearMon.substring(0, 4);
        int iYear = 2013;
        try
        {
            iYear = Integer.parseInt(strYear);
        }
        catch (Exception e)
        {
            System.out.println("请输入有效的统计月份，格式为yyyymm，如201308");
            return -1;
        }
        if (!(iYear >= 2013 && iYear <= 2099))
        {
            System.out.println("请输入有效的统计月份，格式为yyyymm，如201308；有效的年份区间为2013~2099");
            return -1;
        }
        //月份检查
        String strMon = m_strYearMon.substring(4,6);
        if (strMon == null || strMon == "")
        {
            System.out.println("请输入有效的统计月份，格式为yyyymm，如201308");
            return -1;
        }
        strMon = strMon.toUpperCase();
        if (strMon == "Q1" || strMon == "Q2" || strMon == "Q3" || strMon == "Q4")
        {
            return 1;
        }
        int iMon = 1;
        try
        {
            iMon = Integer.parseInt(strMon);
        }
        catch (Exception e)
        {
            System.out.println("请输入有效的统计月份，格式为yyyymm，如201308");
            return -1;
        }
        if (!(iMon >= 1 && iMon <= 12))
        {
            System.out.println("请输入有效的统计月份，格式为yyyymm，如201308；有效的月份区间为01~12");
            return -1;
        }
        if (m_strYearMon.length() > 6)
        {
            boolean isDateValid = Utility.isValidDate(m_strYearMon);
            if (!isDateValid)
            {
                System.out.println("请输入有效的统计月份，格式为yyyymm，如201308；有效的年份区间为2013~2099");
                return -1;
            }
        }
        return 1;
    }

    public static boolean isValidYearMonth(String strYearMonth)
    {
        String strYear = strYearMonth.substring(0, 4);
        int iYear = 2011;
        try
        {
            iYear = Integer.parseInt(strYear);
        }
        catch (Exception ex) 
        {
            System.out.println("请输入有效的统计月份，格式为yyyymm[如201101]或者yyyymmdd[如20110127]");
            return false;
        }
        if (!(iYear >= 2010 && iYear <= 2019))
        {
            System.out.println("请输入有效的统计月份，格式为yyyymm，如201101；有效的年份区间为2010~2019");
            return false;
        }
        //月份检查
        String strMon = strYearMonth.substring(4, 2);
        int iMon = 1;
        try
        {
            iMon = Integer.parseInt(strMon);
        }
        catch (Exception ex) 
        {
            System.out.println("请输入有效的统计月份，格式为yyyymm，如201101");
            return false;
        }
        if (!(iMon >= 1 && iMon <= 12))
        {
            System.out.println("请输入有效的统计月份，格式为yyyymm，如201101；有效的月份区间为01~12");
            return false;
        }
        if (strYearMonth.length() > 6)
        {
            boolean isDateValid = Utility.isValidDate(strYearMonth);
            if (!isDateValid)
            {
                System.out.println("请输入有效的统计月份，格式为yyyymm，如201101；有效的年份区间为2010~2019");
                return false;
            }
        }
        return true;
    }
    
    private static String GetHalfYear(String strEndDate) {
    	String strYear = strEndDate.substring(0, 4);
    	if (strEndDate.endsWith("0630"))
    		return strYear + "H1";
    	else if (strEndDate.endsWith("1231"))
    		return strYear + "H2";
    	return null;
    }

    private static boolean isYearEnd(String strDate) {
    	if (strDate.endsWith("1231")) {
    		return true;
    	}
    	return false;
    }
    
    private static boolean isHalfYearEnd(String strDate) {
    	if (strDate.endsWith("0630") || strDate.endsWith("1231")) {
    		return true;
    	}
    	return false;
    }
    
    private static boolean isLastMonthOfQuater(String strYearMonth) {
    
        if (strYearMonth == null || strYearMonth.isEmpty())
        {
            return false;
        }
        String strMonth = strYearMonth.substring(4, 6); //
        if (strMonth == null || strMonth.isEmpty())
        {
            return false;
        }
        int iMonth = 0;
        try {
        	iMonth = Integer.parseInt(strMonth);
        }
        catch(Exception e) {
        	e.printStackTrace();
        	return false;
        }
        int iQuater = iMonth % 3;
        if (iQuater == 0)
        {
            return true;
        }
       
    	return false;
    }
    
    
    public static void GetStaticalConfigurations()
    {
        //获取统计参数
    	Document doc = XmlUtils.LoadXml(RuntimeUtils.OpenResource("stats.xml"));
    	m_InstanceName = XmlUtils.SelectNodeText(doc, "//config/SpecifiedSystemName");
    	if (m_InstanceName == null || m_InstanceName.isEmpty()) {
    		System.out.println("系统名称<SpecifiedSystemName>不能为空（即使不统计指定系统也要提供一个有效的系统名称）。");
    		System.out.println("程序已退出执行。");
    		System.exit(-1);
    	}
    	String statSpecifiedPeriod = XmlUtils.SelectNodeText(doc, "//config/Stat_Specified_Period");
    	if (statSpecifiedPeriod != null && !statSpecifiedPeriod.isEmpty() && statSpecifiedPeriod.toLowerCase().equals("true")) {
    		StatsConfig.Stat_Specified_Period = true;
    	}
    	String statAllSys = XmlUtils.SelectNodeText(doc, "//config/Stat_All_Systems");
    	if (statAllSys != null && !statAllSys.isEmpty() && statAllSys.toLowerCase().equals("true")) {
    		StatsConfig.Stat_All_Sys = true;
    	}
    	String autostat_on = XmlUtils.SelectNodeText(doc, "//config/autostat_on");
    	if (autostat_on != null && !autostat_on.isEmpty() && autostat_on.toLowerCase().equals("true")) {
    		StatsConfig.autostat_on = true;
    	}
    	String autostat_daily = XmlUtils.SelectNodeText(doc, "//config/autostat_daily");
    	if (autostat_daily != null && !autostat_daily.isEmpty() && autostat_daily.toLowerCase().equals("true")) {
    		StatsConfig.autostat_daily = true;
    	}
    	String autostat_monthly = XmlUtils.SelectNodeText(doc, "//config/autostat_monthly");
    	if (autostat_monthly != null && !autostat_monthly.isEmpty() && autostat_monthly.toLowerCase().equals("true")) {
    		StatsConfig.autostat_monthly = true;
    	}
    	String autostat_quaterly = XmlUtils.SelectNodeText(doc, "//config/autostat_quaterly");
    	if (autostat_quaterly != null && !autostat_quaterly.isEmpty() && autostat_quaterly.toLowerCase().equals("true")) {
    		StatsConfig.autostat_quaterly = true;
    	}
    	String autostat_halfyearly = XmlUtils.SelectNodeText(doc, "//config/autostat_halfyearly");
    	if (autostat_halfyearly != null && !autostat_halfyearly.isEmpty() && autostat_halfyearly.toLowerCase().equals("true")) {
    		StatsConfig.autostat_halfyearly = true;
    	}
    	String autostat_yearly = XmlUtils.SelectNodeText(doc, "//config/autostat_yearly");
    	if (autostat_yearly != null && !autostat_yearly.isEmpty() && autostat_yearly.toLowerCase().equals("true")) {
    		StatsConfig.autostat_yearly = true;
    	}
    }
    
    public static void GetSpecifiedPeriodStaticalConfigurations()
    {
        //获取统计参数
    	Document doc = XmlUtils.LoadXml(RuntimeUtils.OpenResource("stats.xml"));
    	m_strBeginDay = XmlUtils.SelectNodeText(doc, "//config/StatBeginDay");
		if (!checkBeginDay()) {
			System.exit(-1);
		}
		m_strEndDay = XmlUtils.SelectNodeText(doc, "//config/StatEndDay");
		if (!checkEndDay()) {
			System.exit(-1);
		}
		m_strYearMon = XmlUtils.SelectNodeText(doc, "//config/StatMonth");
		int iRet = checkStaticalMonth();
		if (iRet != 1) {
			System.exit(-1);	
		}
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		Date dtBeginDate = null;
		try {
			dtBeginDate = sdf.parse(m_strBeginDay);
		} catch (ParseException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		Date dtEndDate = null;
		try {
			dtEndDate = sdf.parse(m_strEndDay);
		} catch (ParseException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		
        //比较两个日期
		if (dtEndDate.before(dtBeginDate)) {
			System.out.println("截止日期不能早于起始日期，请检查配置文件中的输入参数！");
            System.out.println("请按回车键退出程序的执行...");
			Scanner in = new Scanner(System.in);
			String readLine = in.nextLine();
            System.exit(-1);
        }

        iRet = checkStaticalMonth();
        if (iRet == 0)
        {
        	System.out.println("统计区间和起始日期/截止日期不匹配，请检查配置文件中的输入参数！");
        	System.out.println("请按回车键退出程序的执行...");
			Scanner in = new Scanner(System.in);
			String readLine = in.nextLine();
            return;
        }
    } 
    
	
    //加一天之后看看是否为1号？
    public static boolean isLastDayOfMonthJustPast()
    {
    	Calendar sysCalendar = Calendar.getInstance(); 	   	   	
    	int iDay = sysCalendar.get(Calendar.DATE);   	
        if (iDay == 1) {
            return true;
        }
        return false;
    }

    public static String GetFirstDayOfCurrentHalfYear(String strEndDate) {
    	String strYear = strEndDate.substring(0, 4);
    	if (strEndDate.endsWith("0630")) {
    		return strYear + "0101";
    	}
    	else if (strEndDate.endsWith("1231")) {
    		return strYear + "0701";
    	}
    	return null;
    }   
    
    //昨天是几号？
    public static String getPreviousDate()
    {
    	Calendar sysCalendar = Calendar.getInstance();
        // 减1天:
    	sysCalendar.add(Calendar.DATE, -1);
    	int iMonth = sysCalendar.get(Calendar.MONTH)+1;
    	String strMonth = String.valueOf(iMonth); 
    	if (strMonth.length() < 2) {
    		strMonth = "0" + strMonth;
    	}
    	int iDate = sysCalendar.get(Calendar.DATE);
    	String strDate = String.valueOf(iDate); 
    	if (strDate.length() < 2) {
    		strDate = "0" + strDate;
    	}
    	String strYear = String.valueOf(sysCalendar.get(Calendar.YEAR));
        return strYear + strMonth + strDate;
    }
    
    public static String GetFirstDayOfCurrentQuater()
    {
    	Calendar sysCalendar = Calendar.getInstance();

        // 加1天:
    	sysCalendar.add(Calendar.DATE, 1);
    	
        // -3个月:
    	sysCalendar.add(Calendar.MONTH, -3);
 	
    	int iMonth = sysCalendar.get(Calendar.MONTH)+1;
    	String strMonth = String.valueOf(iMonth); 
    	if (strMonth.length() < 2) {
    		strMonth = "0" + strMonth;
    	}
    	int iDate = sysCalendar.get(Calendar.DATE);
    	String strDate = String.valueOf(iDate); 
    	if (strDate.length() < 2) {
    		strDate = "0" + strDate;
    	}
    	
    	String strYear = String.valueOf(sysCalendar.get(Calendar.YEAR));
        return strYear + strMonth + strDate;
    }

    
    
    public static String GetQuaterByGivenYearMon(String strYearMonth)
    {
        if (strYearMonth == null || strYearMonth.isEmpty())
        {
            return null;
        }
        String strMonth = strYearMonth.substring(4, 6).trim();
        if (strMonth == null || strMonth.isEmpty())
        {
            return null;
        }
        
        int iMonth = 0;
        try {
        	iMonth = Integer.parseInt(strMonth);
        }
        catch(Exception e) {
        	e.printStackTrace();
        	return null;
        }
        
        int iQuater = iMonth / 3;
        String Qx = "Q" + String.valueOf(iQuater);
        String strYear = strYearMonth.substring(0, 4);
        String strYearQx = strYear + Qx;
        return strYearQx;
    }

  
	public static int getCaseRunStatisticsIdByStatMonth(String strStatMonth) {
		
		IDAL<CaseRunStatistics> iDAL = DALFactory.GetBeanDAL(CaseRunStatistics.class);
		CaseRunStatistics crs = iDAL.Get(Op.EQ("systemId", m_iSystemId), Op.EQ("statMonth", strStatMonth));
		
		if (crs != null) {	
			return crs.getCaseRunStatisticsId();
		}
		
		return 0;
	}
	
	public static CaseRunStatistics getCaseRunStatisticsByStatMonth(String strStatMonth) {
		
		IDAL<CaseRunStatistics> iDAL = DALFactory.GetBeanDAL(CaseRunStatistics.class);
		CaseRunStatistics crs = iDAL.Get(Op.EQ("systemId", m_iSystemId), Op.EQ("statMonth", strStatMonth));
		
		return crs;
	}
	
    //删除执行统计表
    public static void DeleteCaseRunStatistics(CaseRunStatistics crs)
    {
		IDAL<CaseRunStatistics> iDAL = DALFactory.GetBeanDAL(CaseRunStatistics.class);
		//CaseRunStatistics crs = iDAL.Get(Op.EQ("caseRunStatisticsId", iCaseRunStatisticsId));
		if (crs != null) {	
			iDAL.Del(crs);
		}
    }
	
	
    //删除要素变更统计表
    public static void DeleteFactorChangeStatistics(int iFactorChangeStatisticsId)
    {
		IDAL<FactorChangeStatistics> iDAL = DALFactory.GetBeanDAL(FactorChangeStatistics.class);
		FactorChangeStatistics fcs = iDAL.Get(Op.EQ("id", iFactorChangeStatisticsId));
		
		if (fcs != null) {	
			iDAL.Del(fcs);
		}
    }
    
    public static void UpdateCaseRunStatistics(CaseRunStatistics crs)
    {
		crs.setStatStatus(2); // 完成
		IDAL<CaseRunStatistics> iDAL = DALFactory.GetBeanDAL(CaseRunStatistics.class);
		iDAL.Add(crs);
    }
    
    //回写Case_Run_Statistics
    public static void UpdateCaseRunStatistics(CaseRunStatistics crs, Case_Run_UserStats crus_sum, int iTotalRunUserCount)
    {
		crs.setFirstRunTime(crus_sum.FirstRunTime);
		crs.setLastRunTime(crus_sum.LastRunTime);
		crs.setTotalRunCaseFlowCount(crus_sum.TotalRunCaseFlowCount);
		crs.setTotalRunCaseCount(crus_sum.TotalRunCaseCount);
		crs.setTotalRunUserCount(iTotalRunUserCount);
		crs.setTotalPassedCaseFlowCount(crus_sum.TotalPassedCaseFlowCount);
		if (crus_sum.TotalRunCaseFlowCount > 0) {
			//保留两位小数
			float passRate = crus_sum.CaseFlowPassRate * 100;
			BigDecimal b = new BigDecimal(passRate); 
			passRate = b.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
			String strPassRate = String.valueOf(passRate) + "%";
			crs.setCaseFlowPassRate(strPassRate);
		}
		else {
			crs.setCaseFlowPassRate("");
		}

		crs.setCreatedCaseCount(crus_sum.CreatedCaseCount);
		crs.setCreatedCaseFlowCount(crus_sum.CreatedCaseFlowCount);
		crs.setCreatedSysParamCount(crus_sum.CreatedSysParamCount);
		crs.setCreatedTransactionCount(crus_sum.CreatedTransactionCount);
		
		crs.setModifiedCaseCount(crus_sum.ModifiedCaseCount);
		crs.setModifiedCaseFlowCount(crus_sum.ModifiedCaseFlowCount);
		crs.setModifiedSysParamCount(crus_sum.ModifiedSysParamCount);
		crs.setModifiedTransactionCount(crus_sum.ModifiedTransactionCount);

		//crs.setStatTime(new Date());
		crs.setStatStatus(2); // 完成
		
		IDAL<CaseRunStatistics> iDAL = DALFactory.GetBeanDAL(CaseRunStatistics.class);
		iDAL.Add(crs);
    }
    
    //插入Case_Run_Statistics
    public static CaseRunStatistics InsertCaseRunStatistics()
    {
		CaseRunStatistics crs = new CaseRunStatistics();
		crs.setStatStartDay(m_strBeginDay);
		crs.setStatEndDay(m_strEndDay);
		crs.setStatMonth(m_strYearMon);
		crs.setSystemId(m_iSystemId);
		crs.setTotalRunCaseFlowCount(0);
		crs.setTotalRunCaseCount(0);
		crs.setTotalRunUserCount(0);
		crs.setTotalPassedCaseFlowCount(0);
		crs.setCaseFlowPassRate("");
		crs.setStatTime(new Date());
		crs.setStatStatus(-1);
		crs.setStatIpAddress(Utility.GetLocalIPAddress());
		crs.setStatHostName(Utility.GetLocalHostName());
		
		crs.setCreatedCaseCount(0);
		crs.setCreatedCaseFlowCount(0);
		crs.setCreatedSysParamCount(0);
		crs.setCreatedTransactionCount(0);
		crs.setModifiedCaseCount(0);
		crs.setModifiedCaseFlowCount(0);
		crs.setModifiedSysParamCount(0);
		crs.setModifiedTransactionCount(0);
		
		IDAL<CaseRunStatistics> iDAL = DALFactory.GetBeanDAL(CaseRunStatistics.class);
		iDAL.Add(crs);
		
		return crs;
    }

	
    //插入Case_Run_UserStats
    public static void InsertCaseRunUserStat(CaseRunStatistics caseRunStatistics, Case_Run_UserStats cr_us)
    {
		CaseRunUserStats crus = new CaseRunUserStats();
		
		crus.setCaseRunStatistics(caseRunStatistics);
		crus.setRunUserId(cr_us.RunUserId);
		crus.setTotalRunCaseFlowCount(cr_us.TotalRunCaseFlowCount);
		crus.setTotalRunCaseCount(cr_us.TotalRunCaseCount);
		crus.setTotalPassedCaseFlowCount(cr_us.TotalPassedCaseFlowCount);
		if (cr_us.TotalRunCaseFlowCount > 0) {
			//保留两位小数
			float passRate = cr_us.CaseFlowPassRate * 100;
			BigDecimal b = new BigDecimal(passRate); 
			passRate = b.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
			String strPassRate = String.valueOf(passRate) + "%";
			crus.setCaseFlowPassRate(strPassRate);
		}
		else {
			crus.setCaseFlowPassRate("");
		}
		crus.setFirstRunTime(cr_us.FirstRunTime);
		crus.setLastRunTime(cr_us.LastRunTime);
		
		crus.setCreatedCaseCount(cr_us.CreatedCaseCount);
		crus.setCreatedCaseFlowCount(cr_us.CreatedCaseFlowCount);
		crus.setCreatedSysParamCount(cr_us.CreatedSysParamCount);
		crus.setCreatedTransactionCount(cr_us.CreatedTransactionCount);
		
		crus.setModifiedCaseCount(cr_us.ModifiedCaseCount);
		crus.setModifiedCaseFlowCount(cr_us.ModifiedCaseCount);
		crus.setModifiedSysParamCount(cr_us.ModifiedSysParamCount);
		crus.setModifiedTransactionCount(cr_us.ModifiedTransactionCount);
		
		IDAL<CaseRunUserStats> iDAL = DALFactory.GetBeanDAL(CaseRunUserStats.class);
		iDAL.Add(crus);
    }
    
    
    public static void InsertFactorChangeStatistics()
    {
		FactorChangeStatistics fcs = new FactorChangeStatistics();
		
		fcs.setStatStartDay(m_strBeginDay);
		fcs.setStatEndDay(m_strEndDay);
		fcs.setStatMonth(m_strYearMon);
		fcs.setSystemId(m_iSystemId);
		
		fcs.setCreatedTransactionCount(0);
		fcs.setCreatedCaseFlowCount(0);
		fcs.setCreatedCaseCount(0);
		fcs.setCreatedSysParamCount(0);
		fcs.setModifiedTransactionCount(0);
		fcs.setModifiedCaseFlowCount(0);
		fcs.setModifiedCaseCount(0);
		fcs.setModifiedSysParamCount(0);

		fcs.setStatTime(new Date());
		
		IDAL<FactorChangeStatistics> iDAL = DALFactory.GetBeanDAL(FactorChangeStatistics.class);
		iDAL.Add(fcs);
    }
	

    public static void InsertFactorChangeUserStat(FactorChangeStatistics factorChangeStatistics, int iOpUserId)
    {
    	FactorChangeUserStats fcus = new FactorChangeUserStats();
		
    	fcus.setFactorChangeStatistics(factorChangeStatistics);
    	fcus.setOpUserId(iOpUserId);
		fcus.setCreatedTransactionCount(0);
		fcus.setCreatedCaseFlowCount(0);
		fcus.setCreatedCaseCount(0);
		fcus.setCreatedSysParamCount(0);
		fcus.setModifiedTransactionCount(0);
		fcus.setModifiedCaseFlowCount(0);
		fcus.setModifiedCaseCount(0);
		fcus.setModifiedSysParamCount(0);

		IDAL<FactorChangeUserStats> iDAL = DALFactory.GetBeanDAL(FactorChangeUserStats.class);
		iDAL.Add(fcus);
    }
    

    public static CaseRunStatistics CaseRunStatInit()
    {
        if (m_crs != null)
        {
            DeleteCaseRunStatistics(m_crs);
        }
        CaseRunStatistics crs = InsertCaseRunStatistics();
        return crs;
    }
    

    public static void MonthlyStatInit()
    {
    	if (m_crs != null)
        {
            DeleteCaseRunStatistics(m_crs);
            //DeleteFactorChangeStatistics(m_iCaseRunStatisticsId);
        }
        try {
        	InsertCaseRunStatistics();
        }
        catch(Exception e) {
        	System.out.println("插入Case_Run_UserStats报错，错误提示信息：" + e.getMessage());
        	e.printStackTrace();
        	return;
        }
        
        try {
        	InsertFactorChangeStatistics();
        }
        catch(Exception e) {
        	System.out.println("插入Factor_Change_Statistics报错，错误提示信息：" + e.getMessage());
        	e.printStackTrace();
        	return;
        }

        return;
    }

    private static void tryStatOneGivenPeriod(int iEntry) {
    	  	  	
    	try {
    		StatOneGivenPeriod(iEntry);
		} 
		catch (Exception ex) {
			System.out.println("通用模拟器后台统计程序执行失败！错误提示信息：" + ex.getMessage());
			System.exit(-1);
		}
    }
    
    private static void StatOneGivenPeriod(int iEntry)
    {  	
    	if (m_iSystemId <= 0) {
    		return;
    	}
    	
        //m_iCaseRunStatisticsId = getCaseRunStatisticsIdByStatMonth(m_strYearMon);
        m_crs = getCaseRunStatisticsByStatMonth(m_strYearMon);

        //重新执行的条件1
        boolean bNeedReStat = (iEntry == 1 && (m_SpecifiedSysType != null && m_SpecifiedSysType.getSystemId().equals(String.valueOf(m_iSystemId))));
        //重新执行的条件2
        if (!bNeedReStat) {
        	bNeedReStat = (iEntry == 2 && m_isStartupTagOn[1] && !(StatsConfig.Stat_All_Sys && m_SpecifiedSysType != null && m_SpecifiedSysType.getSystemId().equals(String.valueOf(m_iSystemId))));
        }
        
        //统计完成了的，不要再统计了（除非是指定的项目，或者是启动了使用了强制重新执行选择项）
        if (m_crs != null && m_crs.getStatStatus() == 2 && !bNeedReStat) {
        	//既不是指定项目，又没有说要强制重新执行
        	//if (m_SpecifiedSysType != null && !m_SpecifiedSysType.getSystemId().equals(String.valueOf(m_iSystemId)) && !m_isStartupTagOn[1]) {
        		System.out.println("系统[" + m_InstanceName + "]在指定时期[" + m_strYearMon + "]的统计数据已经生成过，不再重新生成，如果要重新生成，请使用'-r'启动选择项");
            	return;
        	//}
        }

    	System.out.println("-----------------------------------------");
    	System.out.println("    正在统计项目：" + m_InstanceName);
    	System.out.println("-----------------------------------------");
    	
        /*if (m_isStartupTagOn[3])   //Delete
        {
            if (m_iCaseRunStatisticsId <= 0)
            {
                System.out.println("配置文件中所指定统计月份的统计数据并不存在，删除操作无需进行！");
                System.out.println("\n按回车键退出...");
    			Scanner in = new Scanner(System.in);
    			String readLine = in.nextLine();
                return;
            }
            else {
                System.out.println("是否确定要删除指定月份的统计数据？请选择（Y/N）\nY：确定删除\nN：退出删除...");
    			Scanner in = new Scanner(System.in);
    			String readLine = in.nextLine();
                if (readLine != null && readLine.substring(0,1).toUpperCase().equals("Y"))
                {
                    System.out.println("统计已经退出！");
                    return;
                }
                else
                {   //删除当月的统计信息
                    DeleteCaseRunStatistics(m_iCaseRunStatisticsId);
                    DeleteFactorChangeStatistics(m_iCaseRunStatisticsId);

                    System.out.println("删除操作已经完成！");
                    System.out.println("\n按回车键退出...");
        			Scanner in2 = new Scanner(System.in);
        			String readLine2 = in2.nextLine();
                    return;
                }
            }
        }*/
       
        System.out.println("统计起始日期：" + m_strBeginDay + "，截止日期：" + m_strEndDay + "，统计月份：" + m_strYearMon);
        System.out.println("统计正在进行中，请稍候...");
        System.out.println();

        /*if (m_iCaseRunStatisticsId <= 0)
        {
            MonthlyStatInit();
        }*/
          
        CaseRunStatistics crs = CaseRunStatInit();
        	
		Case_Run_UserStats crus_sum = new Case_Run_UserStats();
		
		try {
			StatAllUsers(crs, crus_sum);
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}

        //GetMonthlyStaticalDatas(iTotalProcCount, iTotalCaseCount, iTotalNodeCount, iTotalUserCount);
        
        String strMsg = "在统计区间内（" + m_strBeginDay + " ~ " + m_strEndDay + "），\n 总共执行过的用例总数为：" + crus_sum.TotalRunCaseFlowCount;
        if (crus_sum.TotalRunCaseFlowCount > 0) {
        	strMsg += "，用例通过率为：" + String.valueOf(crus_sum.CaseFlowPassRate * 100) + "%";
        }
        
        System.out.println(strMsg);

        System.out.println();
        System.out.println("本期[" + m_strYearMon + "]统计已经执行完成！");

        /*if (!(m_isStartupTagOn[0]))
        {
            System.out.println("\n按回车键退出...");
			Scanner in = new Scanner(System.in);
			String readLine = in.nextLine();
        }*/
    }//StatOneGivenPeriod
    
    
    private static List<CaseFlowInstance> getCaseflowInstanceByExecuteLog(int iExecuteLogId) {
	
    	IDAL<CaseFlowInstance> iDAL = DALFactory.GetBeanDAL(CaseFlowInstance.class);
		List<CaseFlowInstance> caseFlowInstanceList = iDAL.ListAll(Op.EQ("executeLogId", iExecuteLogId));
		return caseFlowInstanceList;
    }
    
    private static List<CaseInstance> getCaseInstanceByExecuteLog(int iExecuteLogId) {
    	
    	IDAL<CaseInstance> iDAL = DALFactory.GetBeanDAL(CaseInstance.class);
		List<CaseInstance> ciList = iDAL.ListAll(Op.EQ("executeLogId", iExecuteLogId));
		return ciList;
    }
    
    private static List<CaseInstance> getCaseInstanceByCaseFlowInstance(CaseFlowInstance cfi) {
    	
    	IDAL<CaseInstance> iDAL = DALFactory.GetBeanDAL(CaseInstance.class);
		List<CaseInstance> ciList = iDAL.ListAll(Op.EQ("caseFlowInstance", cfi));
		return ciList;
    }
    
    
    private static void StatAllUsers(CaseRunStatistics crs, Case_Run_UserStats crus_sum) {
		
    	//Get beginDate & endDate
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
    	Date beginDate = null;
    	Date endDate = null;
    	try {
			beginDate = sdf.parse(m_strBeginDay);
		} catch (ParseException e) {
			e.printStackTrace();
			return;
		}
    	try {
    		Calendar sysCalendar = Calendar.getInstance();
    		// 加1天:
        	sysCalendar.add(Calendar.DATE, 1);
        	int iMonth = sysCalendar.get(Calendar.MONTH)+1;
        	String strMonth = String.valueOf(iMonth); 
        	if (strMonth.length() < 2) {
        		strMonth = "0" + strMonth;
        	}
        	int iDate = sysCalendar.get(Calendar.DATE);
        	String strDate = String.valueOf(iDate); 
        	if (strDate.length() < 2) {
        		strDate = "0" + strDate;
        	}
        	String strYear = String.valueOf(sysCalendar.get(Calendar.YEAR));
            String strNextDate = strYear + strMonth + strDate;
			endDate = sdf.parse(strNextDate);
		} catch (ParseException e) {
			e.printStackTrace();
			return;
		}

    	StatCaseRunData(crs, crus_sum, beginDate, endDate);
    	StatItemChgData(crs, crus_sum, beginDate, endDate);
    	StatSumData(crs, crus_sum);
    }
    

    private static void StatCaseRunData(CaseRunStatistics crs, Case_Run_UserStats crus_sum, Date beginDate, Date endDate) {
    	
    	System.out.println("正在统计用例执行情况数据......");
    	
    	//用例执行数据统计
    	IDAL<ExecuteLog> iDAL = DALFactory.GetBeanDAL(ExecuteLog.class);
    	List<ExecuteLog> executeLogList = iDAL.ListAll(Op.EQ("systemId", m_iSystemId), Op.GE("beginRunTime", beginDate), Op.LT("beginRunTime", endDate));
		if (executeLogList == null || executeLogList.size() == 0) {
			//UpdateCaseRunStatistics(crs);
			return;
		}
		for (int i=0; i < executeLogList.size(); i++) {
			ExecuteLog executeLog = executeLogList.get(i);
			int iRunUserId = executeLog.getUserId();
			Case_Run_UserStats cr_us = m_userStatsList.get(String.valueOf(iRunUserId));
			if (cr_us == null) {
				cr_us = new Case_Run_UserStats();
				cr_us.RunUserId = iRunUserId;
			}
			Integer iExcueteLogType = executeLog.getType();
			if (iExcueteLogType == null) {
				continue;
			}
			if (iExcueteLogType == 1 || iExcueteLogType == 0) { //业务流用例或执行集
				List<CaseFlowInstance> cfiList = getCaseflowInstanceByExecuteLog(executeLog.getId());
				if (cfiList == null) {
					continue;
				}
				for (int j=0; j<cfiList.size();j++) {
					CaseFlowInstance cfi = cfiList.get(j);
					cr_us.TotalRunCaseFlowCount++;
					if (cfi.getCaseFlowPassFlag() == 1) {
						cr_us.TotalPassedCaseFlowCount++;
					}
					if (cfi.getBeginTime() != null) {
						if (cr_us.FirstRunTime != null) {
							if (cfi.getBeginTime().before(cr_us.FirstRunTime)) {
								cr_us.FirstRunTime = cfi.getBeginTime();
							} 
							if (cfi.getBeginTime().after(cr_us.LastRunTime)) {
								cr_us.LastRunTime = cfi.getBeginTime();
							} 
						}
						else {
							cr_us.FirstRunTime = cfi.getBeginTime();
							cr_us.LastRunTime = cfi.getBeginTime();
						}
					}
					List<CaseInstance> ciList = getCaseInstanceByCaseFlowInstance(cfi);
					for (int k=0; k<ciList.size();k++) {
						cr_us.TotalRunCaseCount++;
					}
				}
			}
			else if (iExcueteLogType == 2) { //步骤
				List<CaseInstance> ciList = getCaseInstanceByExecuteLog(executeLog.getId());
				if (ciList == null) {
					continue;
				}
				for (int j=0; j<ciList.size();j++) {
					cr_us.TotalRunCaseCount++;
					if (executeLog.getBeginRunTime() != null) {
						if (cr_us.FirstRunTime != null) {
							if (executeLog.getBeginRunTime().before(cr_us.FirstRunTime)) {
								cr_us.FirstRunTime = executeLog.getBeginRunTime();
							} 
							if (executeLog.getBeginRunTime().after(cr_us.LastRunTime)) {
								cr_us.LastRunTime = executeLog.getBeginRunTime();
							} 
						}
						else {
							cr_us.FirstRunTime = executeLog.getBeginRunTime();
							cr_us.LastRunTime = executeLog.getBeginRunTime();
						}
					}
				}
			}
			m_userStatsList.remove(String.valueOf(iRunUserId));
			m_userStatsList.put(String.valueOf(iRunUserId), cr_us);
		}//for    	
		System.out.println("用例执行情况数据统计完成。");
		System.out.println();
    }	
	
    private static void StatSumData(CaseRunStatistics crs, Case_Run_UserStats crus_sum) {
    	
    	System.out.println("正在对用户统计数据进行汇总......");
    	
		//按执行用户写入统计数据
		if (m_userStatsList != null) {
			Iterator<String> it = m_userStatsList.keySet().iterator();
			while(it.hasNext()) {
				String strUserId = it.next();
				Case_Run_UserStats cr_us = m_userStatsList.get(strUserId);		
				if (cr_us != null) {
					if (cr_us.TotalRunCaseFlowCount > 0) {//计算通过率
						cr_us.CaseFlowPassRate = ((float)cr_us.TotalPassedCaseFlowCount / (float)cr_us.TotalRunCaseFlowCount);
						BigDecimal b = new BigDecimal(cr_us.CaseFlowPassRate); 
						cr_us.CaseFlowPassRate = b.setScale(4, BigDecimal.ROUND_HALF_UP).floatValue();
					}
					try {
						InsertCaseRunUserStat(crs, cr_us);
					} catch (Exception e) {
						System.out.println("InsertCaseRunUserStat失败，错误提示信息：" + e.getMessage());
						e.printStackTrace();
					}
				}
			}
		}

		//所有用户一起汇总
		if (m_userStatsList != null) {
			Iterator<String> it = m_userStatsList.keySet().iterator();
			//按用户进行遍历
			while(it.hasNext()) {
				String strUserId = it.next();
				Case_Run_UserStats cr_us = m_userStatsList.get(strUserId);		
				if (cr_us != null) {
					crus_sum.TotalRunCaseFlowCount += cr_us.TotalRunCaseFlowCount;
					crus_sum.TotalPassedCaseFlowCount += cr_us.TotalPassedCaseFlowCount;
					crus_sum.TotalRunCaseCount += cr_us.TotalRunCaseCount;
					if (cr_us.FirstRunTime != null) {
						if (crus_sum.FirstRunTime != null) {
							if (cr_us.FirstRunTime.before(crus_sum.FirstRunTime)) {
								crus_sum.FirstRunTime = cr_us.FirstRunTime; 
							}
							if (cr_us.LastRunTime.after(crus_sum.LastRunTime)) {
								crus_sum.LastRunTime = cr_us.LastRunTime; 
							}
						} 
						else {
							crus_sum.FirstRunTime = cr_us.FirstRunTime;
							crus_sum.LastRunTime = cr_us.LastRunTime;
						}
					}
					crus_sum.CreatedCaseCount += cr_us.CreatedCaseCount;
					crus_sum.CreatedCaseFlowCount += cr_us.CreatedCaseFlowCount;
					crus_sum.CreatedSysParamCount += cr_us.CreatedSysParamCount;
					crus_sum.CreatedTransactionCount += cr_us.CreatedTransactionCount;
					crus_sum.ModifiedCaseCount += cr_us.ModifiedCaseCount;
					crus_sum.ModifiedCaseFlowCount += cr_us.ModifiedCaseFlowCount;
					crus_sum.ModifiedSysParamCount += cr_us.ModifiedSysParamCount;
					crus_sum.ModifiedTransactionCount += cr_us.ModifiedTransactionCount;
				}
			}
			//计算总的通过率（所有用户）
			if (crus_sum.TotalRunCaseFlowCount > 0) {
				crus_sum.CaseFlowPassRate = ((float)crus_sum.TotalPassedCaseFlowCount / (float)crus_sum.TotalRunCaseFlowCount);
				BigDecimal b = new BigDecimal(crus_sum.CaseFlowPassRate); 
				crus_sum.CaseFlowPassRate = b.setScale(4, BigDecimal.ROUND_HALF_UP).floatValue(); 
			}
		}
		
		try {
			UpdateCaseRunStatistics(crs, crus_sum, m_userStatsList.size());
		} catch (Exception e) {
			System.out.println("UpdateCaseRunStatistics失败，错误提示信息：" + e.getMessage());
			e.printStackTrace();
		}
		System.out.println("用户统计数据汇总完成。");
		System.out.println();
    }
    
    
    //要素变更统计
    private static void StatItemChgData(CaseRunStatistics crs, Case_Run_UserStats crus_sum, Date beginDate, Date endDate) {
    	
    	System.out.println("正在统计要素变更情况数据......");
		System.out.println();
    	System.out.println("正在统计交易数据......");
		System.out.println();
    	System.out.println("正在统计新创建的交易...");
    	//交易数据统计
    	IDAL<Transaction> iDAL = DALFactory.GetBeanDAL(Transaction.class);
    	//新创建的交易
    	List<Transaction> newCreatedTransList = iDAL.ListAll(Op.EQ("systemId", String.valueOf(m_iSystemId)), Op.GE("createdTime", beginDate), Op.LT("createdTime", endDate));
		if (newCreatedTransList != null) {
			for (int i=0; i < newCreatedTransList.size(); i++) {
				Transaction trans = newCreatedTransList.get(i);
				String sRunUserId = trans.getCreatedUserId();
				if (sRunUserId == null || sRunUserId.isEmpty()) {
					continue;
				}
				int iRunUserId = Integer.parseInt(sRunUserId);
				Case_Run_UserStats cr_us = m_userStatsList.get(sRunUserId);
				if (cr_us == null) {
					cr_us = new Case_Run_UserStats();
					cr_us.RunUserId = iRunUserId;
				}
				cr_us.CreatedTransactionCount += 1;
				m_userStatsList.remove(sRunUserId);
				m_userStatsList.put(sRunUserId, cr_us);
			}//for    	
		}
		System.out.println("统计新创建的交易完成。");
		System.out.println();
    	System.out.println("正在统计修改的交易...");
    	//修改的交易
    	List<Transaction> modifiedTransList = iDAL.ListAll(Op.EQ("systemId", String.valueOf(m_iSystemId)), Op.GE("lastModifiedTime", beginDate), Op.LT("lastModifiedTime", endDate));
		if (modifiedTransList != null) {
			for (int i=0; i < modifiedTransList.size(); i++) {
				Transaction trans = modifiedTransList.get(i);
				String sRunUserId = trans.getLastModifiedUserId();
				if (sRunUserId == null || sRunUserId.isEmpty()) {
					continue;
				}
				int iRunUserId = Integer.parseInt(sRunUserId);
				Case_Run_UserStats cr_us = m_userStatsList.get(sRunUserId);
				if (cr_us == null) {
					cr_us = new Case_Run_UserStats();
					cr_us.RunUserId = iRunUserId;
				}
				cr_us.ModifiedTransactionCount += 1;
				m_userStatsList.remove(sRunUserId);
				m_userStatsList.put(sRunUserId, cr_us);
			}//for    	
		}
		System.out.println("统计修改的交易完成。");
		System.out.println();
		
		System.out.println("正在统计用例数据......");
		System.out.println();
		
		System.out.println("正在统计新创建的用例...");
    	//用例数据统计
    	IDAL<CaseFlow> cfDAL = DALFactory.GetBeanDAL(CaseFlow.class);
    	//新创建的用例
    	List<CaseFlow> newCreatedCaseFlowList = cfDAL.ListAll(Op.EQ("systemId", m_iSystemId), Op.GE("createdTime", beginDate), Op.LT("createdTime", endDate));
		if (newCreatedCaseFlowList != null) {
			for (int i=0; i < newCreatedCaseFlowList.size(); i++) {
				CaseFlow cf = newCreatedCaseFlowList.get(i);
				String sRunUserId = cf.getCreatedUserId();
				int iRunUserId = Integer.parseInt(sRunUserId);
				Case_Run_UserStats cr_us = m_userStatsList.get(sRunUserId);
				if (cr_us == null) {
					cr_us = new Case_Run_UserStats();
					cr_us.RunUserId = iRunUserId;
				}
				cr_us.CreatedCaseFlowCount += 1;
				m_userStatsList.remove(sRunUserId);
				m_userStatsList.put(sRunUserId, cr_us);
			}//for    	
		}
		System.out.println("统计修改的用例完成。");
		System.out.println();
		System.out.println("正在统计修改的用例...");
    	//修改的用例
    	List<CaseFlow> modifiedCaseFlowList = cfDAL.ListAll(Op.EQ("systemId", m_iSystemId), Op.GE("lastModifiedTime", beginDate), Op.LT("lastModifiedTime", endDate));
		if (modifiedCaseFlowList != null) {
			for (int i=0; i < modifiedCaseFlowList.size(); i++) {
				CaseFlow cf = modifiedCaseFlowList.get(i);
				String sRunUserId = cf.getLastModifiedUserId();
				if (sRunUserId == null || sRunUserId.isEmpty()) {
					continue;
				}
				int iRunUserId = Integer.parseInt(sRunUserId);
				Case_Run_UserStats cr_us = m_userStatsList.get(sRunUserId);
				if (cr_us == null) {
					cr_us = new Case_Run_UserStats();
					cr_us.RunUserId = iRunUserId;
				}
				cr_us.ModifiedTransactionCount += 1;
				m_userStatsList.remove(sRunUserId);
				m_userStatsList.put(sRunUserId, cr_us);
			}//for    	
		}
		System.out.println("统计修改的用例完成。");
		System.out.println();
		
		System.out.println("正在统计案例数据......");
		System.out.println();
		
		System.out.println("正在统计新创建的案例...");
		//案例数据统计
    	IDAL<Case> cDAL = DALFactory.GetBeanDAL(Case.class);
    	//新创建的案例
    	List<Case> newCreatedCaseList = cDAL.ListAll(Op.GE("createdTime", beginDate), Op.LT("createdTime", endDate));
		if (newCreatedCaseList != null) {
			for (int i=0; i < newCreatedCaseList.size(); i++) {
				Case c = newCreatedCaseList.get(i);
				CaseFlow cf = c.getCaseFlow();
				if (cf != null && cf.getSystemId() == m_iSystemId) { //案例是否属于当前系统？					
					String sRunUserId = c.getCreatedUserId();
					int iRunUserId = Integer.parseInt(sRunUserId);
					Case_Run_UserStats cr_us = m_userStatsList.get(sRunUserId);
					if (cr_us == null) {
						cr_us = new Case_Run_UserStats();
						cr_us.RunUserId = iRunUserId;
					}
					cr_us.CreatedCaseCount += 1;
					m_userStatsList.remove(sRunUserId);
					m_userStatsList.put(sRunUserId, cr_us);
				}
			}//for    	
		}
		System.out.println("统计新创建的案例完成。");
		System.out.println();
		System.out.println("正在统计修改的案例...");
    	//修改的案例
    	List<Case> modifiedCaseList = cDAL.ListAll(Op.GE("lastModifiedTime", beginDate), Op.LT("lastModifiedTime", endDate));
		if (modifiedCaseList != null) {
			for (int i=0; i < modifiedCaseList.size(); i++) {
				Case c = modifiedCaseList.get(i);
				CaseFlow cf = c.getCaseFlow();
				if (cf != null && cf.getSystemId() == m_iSystemId) { //案例是否属于当前系统？
					String sRunUserId = c.getLastModifiedUserId();
					if (sRunUserId == null || sRunUserId.isEmpty()) {
						continue;
					}
					int iRunUserId = Integer.parseInt(sRunUserId);
					Case_Run_UserStats cr_us = m_userStatsList.get(sRunUserId);
					if (cr_us == null) {
						cr_us = new Case_Run_UserStats();
						cr_us.RunUserId = iRunUserId;
					}
					cr_us.ModifiedCaseCount += 1;
					m_userStatsList.remove(sRunUserId);
					m_userStatsList.put(sRunUserId, cr_us);
				}
			}//for    	
		}
		System.out.println("统计修改的案例完成。");
		System.out.println();
		
		System.out.println("正在统计参数数据......");
		System.out.println();
		
		System.out.println("正在统计新创建的参数...");
		//参数数据统计
    	IDAL<SystemDynamicParameter> sysParamDAL = DALFactory.GetBeanDAL(SystemDynamicParameter.class);
    	//新创建的参数
    	List<SystemDynamicParameter> newCreatedSysParamList = sysParamDAL.ListAll(Op.EQ("systemId", String.valueOf(m_iSystemId)), Op.GE("createdTime", beginDate), Op.LT("createdTime", endDate));
		if (newCreatedSysParamList != null) {
			for (int i=0; i < newCreatedSysParamList.size(); i++) {
				SystemDynamicParameter cf = newCreatedSysParamList.get(i);
				String sRunUserId = cf.getCreatedUserId();
				int iRunUserId = Integer.parseInt(sRunUserId);
				Case_Run_UserStats cr_us = m_userStatsList.get(sRunUserId);
				if (cr_us == null) {
					cr_us = new Case_Run_UserStats();
					cr_us.RunUserId = iRunUserId;
				}
				cr_us.CreatedSysParamCount += 1;
				m_userStatsList.remove(sRunUserId);
				m_userStatsList.put(sRunUserId, cr_us);
			}//for    	
		}
		System.out.println("统计新创建的参数完成。");
		System.out.println();
		System.out.println("正在统计修改的参数...");
    	//修改的参数
    	List<SystemDynamicParameter> modifiedSysParamList = sysParamDAL.ListAll(Op.EQ("systemId", String.valueOf(m_iSystemId)), Op.GE("lastModifiedTime", beginDate), Op.LT("lastModifiedTime", endDate));
		if (modifiedSysParamList != null) {
			for (int i=0; i < modifiedSysParamList.size(); i++) {
				SystemDynamicParameter cf = modifiedSysParamList.get(i);
				String sRunUserId = cf.getLastModifiedUserId();
				if (sRunUserId == null || sRunUserId.isEmpty()) {
					continue;
				}
				int iRunUserId = Integer.parseInt(sRunUserId);
				Case_Run_UserStats cr_us = m_userStatsList.get(sRunUserId);
				if (cr_us == null) {
					cr_us = new Case_Run_UserStats();
					cr_us.RunUserId = iRunUserId;
				}
				cr_us.CreatedSysParamCount += 1;
				m_userStatsList.remove(sRunUserId);
				m_userStatsList.put(sRunUserId, cr_us);
			}//for    	
		}
		System.out.println("统计修改的参数完成。");
		System.out.println();
		
		System.out.println("正在统计参数预期值数据......");
		System.out.println();
		
		System.out.println("正在统计新创建的参数预期值...");
		//参数预期值数据统计
    	IDAL<CaseParameterExpectedValue> parExpValDAL = DALFactory.GetBeanDAL(CaseParameterExpectedValue.class);
    	//新创建的参数预期值
    	List<CaseParameterExpectedValue> newCreatedParExpValList = parExpValDAL.ListAll(Op.GE("createdTime", beginDate), Op.LT("createdTime", endDate));
		if (newCreatedParExpValList != null) {
			for (int i=0; i < newCreatedParExpValList.size(); i++) {
				CaseParameterExpectedValue cpev = newCreatedParExpValList.get(i);
				TransactionDynamicParameter transParam = cpev.getTransParameter();
				if (transParam != null && transParam.getSystemParameter() != null && transParam.getSystemParameter().getSystemId().equals(String.valueOf(m_iSystemId))) { 
					String sRunUserId = cpev.getCreatedUserId();
					int iRunUserId = Integer.parseInt(sRunUserId);
					Case_Run_UserStats cr_us = m_userStatsList.get(sRunUserId);
					if (cr_us == null) {
						cr_us = new Case_Run_UserStats();
						cr_us.RunUserId = iRunUserId;
					}
					cr_us.CreatedSysParamCount += 1;
					m_userStatsList.remove(sRunUserId);
					m_userStatsList.put(sRunUserId, cr_us);
				}
			}//for    	
		}
		System.out.println("统计新创建的参数预期值完成。");
		System.out.println();
		System.out.println("正在统计修改的参数预期值...");
    	//修改的参数预期值
    	List<CaseParameterExpectedValue> modifiedCaseParameterExpectedValueList = parExpValDAL.ListAll(Op.GE("lastModifiedTime", beginDate), Op.LT("lastModifiedTime", endDate));
		if (modifiedCaseParameterExpectedValueList != null) {
			for (int i=0; i < modifiedCaseParameterExpectedValueList.size(); i++) {
				CaseParameterExpectedValue cpev = modifiedCaseParameterExpectedValueList.get(i);
				TransactionDynamicParameter transParam = cpev.getTransParameter();
				if (transParam != null && transParam.getSystemParameter() != null && transParam.getSystemParameter().getSystemId().equals(String.valueOf(m_iSystemId))) {
					String sRunUserId = cpev.getLastModifiedUserId();
					if (sRunUserId == null || sRunUserId.isEmpty()) {
						continue;
					}
					int iRunUserId = Integer.parseInt(sRunUserId);
					Case_Run_UserStats cr_us = m_userStatsList.get(sRunUserId);
					if (cr_us == null) {
						cr_us = new Case_Run_UserStats();
						cr_us.RunUserId = iRunUserId;
					}
					cr_us.CreatedSysParamCount += 1;
					m_userStatsList.remove(sRunUserId);
					m_userStatsList.put(sRunUserId, cr_us);
				}
			}//for    	
		}
		System.out.println("统计修改的参数预期值完成。");
		System.out.println();
		System.out.println("要素变更情况数据统计完成。");
		System.out.println();
    }
    
}
