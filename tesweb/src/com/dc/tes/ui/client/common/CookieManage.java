package com.dc.tes.ui.client.common;

import com.dc.tes.ui.client.AppContext;
import com.dc.tes.ui.client.model.GWTUser;
import com.extjs.gxt.ui.client.state.CookieProvider;
import com.extjs.gxt.ui.client.state.Provider;
import com.extjs.gxt.ui.client.state.StateManager;

/**
 * cookie 管理类
 * 用于在用户页面刷新的时候，获得登录信息，以及当前菜单项
 * 
 * @author lujs
 *
 */
public class CookieManage {
	/**
	 * 是否已登录 对应的 key
	 */
	private static String IsLogin = "isLogin";
	/**
	 * 用户ID 对应的 key
	 */
	private static String UserID = "userID";
	/**
	 * 用户名 对应的 key
	 */
	private static String UserName = "UserName";
	/**
	 * 是否是系统管理员  对应的 key
	 */
	private static String IsAdmin = "IsAdmin";

	/**
	 * 当前选择系统ID  对应的 key
	 */
	private static String SimuSystem = "simuSystem";
	/**
	 * 当前菜单项ID   对应的 key
	 */
	private static String MenuID = "menuID";
	/**
	 * 当前用户登录日志ID  对应的key
	 */
	private static String LoginLogID = "loginLogID";
	
	/**
	 * 当前系统类型  对应的key
	 */
	private static String IsSyncComm = "isSyncComm";
	private static String IsClientSimu = "isClientSimu";

	/**
	 * 确定cookied的存放路径、过期时间等基本信息
	 * 需要在EntryPoint初始进来时调用
	 */
	public static void InitStateManager()
	{
		StateManager.get().setProvider(
				new CookieProvider("/", null, null, false));
	}

	/**
	 * 存放登录人员信息
	 * @param user 用户信息对象
	 */
	public static void SetUserInfo(GWTUser user)
	{
		StateManager.get().set(UserID, user.getUserID());
		StateManager.get().set(UserName, user.getUserName());
		StateManager.get().set(IsAdmin, user.getIsAdmin());
	}
	
	public static void SetLoginLogID(String id)
	{
		StateManager.get().set(LoginLogID, id);
	}
	
	/**
	 * 获得用户ID
	 * @return 用户ID
	 */
	public static String GetUserID()
	{
		try
		{
			return StateManager.get().getProvider().getString(UserID);
		}
		catch (Exception e) {
			return "";
		}
	}
	
	/**
	 * 获得用户名
	 * @return 用户名
	 */
	public static String GetUserName()
	{
		try
		{
			return StateManager.get().getProvider().getString(UserName);
		}
		catch (Exception e) {
			return "";
		}
	}
		
	public static int GetIsSyncComm(){
		try{
			return Integer.parseInt(StateManager.get().getProvider().getString(IsSyncComm));
		}catch (Exception e) {
			return 1;
		}
	}
	/**
	 * 设置同步异步类型
	 * @param isSyncComm 
	 */
	public static void SetIsSyncComm(int isSyncComm)
	{
		StateManager.get().set(IsSyncComm, String.valueOf(isSyncComm));
	}
	
	public static int GetIsClientSimu(){
		try{
			return Integer.parseInt(StateManager.get().getProvider().getString(IsClientSimu));
		}catch (Exception e) {
			return 1;
		}
	}

	/**
	 * 设置是否为客户端模拟
	 * @param isClientSimu 
	 */
	public static void SetIsClientSimu(int isClientSimu)
	{
		StateManager.get().set(IsClientSimu, String.valueOf(isClientSimu));
	}
	
	
	/**
	 * 获得当前用户的登录日志ID
	 * @return
	 */
	public static Integer GetLoginLogID(){
		try{
			String loginLogId = StateManager.get().getProvider().getString(LoginLogID);
			if(loginLogId != null && !loginLogId.isEmpty()){
				return Integer.parseInt(loginLogId);
			}else {
				return null;
			}
		}catch (Exception e) {
			return null;
		}
	}
	
	/**
	 * 获得是否是系统管理员
	 * @return 是否是系统管理员
	 */
	public static boolean GetIsAdmin()
	{
		int roleInt = GetRoleInt();
		if(roleInt <= 0)
			return true;
		else
			return false;
	}
	/**
	 * 获得是否是项目管理员
	 * @return 是否是项目管理员
	 */
	public static boolean GetIsPM(){
		int roleInt = GetRoleInt();
		if(roleInt == 2){
			return true;
		}else {
			return false;
		}
	}
	
	/**
	 * 获得是否是管理员
	 * @return 角色编号 -1 超级用户 0-系统管理员 1-测试用户 2-项目管理员
	 */
	public static int GetRoleInt()
	{
		try
		{
			return StateManager.get().getProvider().getInteger(IsAdmin);
		}
		catch (Exception e) {
			return -1;
		}
	}

	
	public static String GetSimuSystemName()
	{
		try	{
			return AppContext.GetEntryPoint().comboSystem.getRawValue();
		}
		catch (Exception e) {
		}
		return "";
	}
	
	/**
	 * 获得当前选择的模拟系统ID
	 * @return 当前选择的模拟系统ID
	 */
	public static String GetSimuSystemID()
	{
		String systemID = StateManager.get().getProvider().getString(SimuSystem);
		return systemID == null ? "" : systemID;
	}
	
	/**
	 * 设置当前选择的模拟系统ID
	 * @param systemID 模拟系统ID
	 */
	public static void SetSimuSystemID(String systemID)
	{
		StateManager.get().set(SimuSystem, systemID);
	}
	
	/**
	 * 存放当前菜单ID
	 * @param MenuID 菜单ID
	 */
	public static void SetMenuItem(String menuID)
	{
		StateManager.get().set(MenuID, menuID);
	}
	
	/**
	 * 获得当前菜单ID
	 * @return 当前菜单ID
	 */
	public static String GetMenuID()
	{
		String menuID =StateManager.get().getProvider().getString(MenuID); 
		return menuID == null ? "" : menuID;
	}
	
	/**
	 * 登录，设置已登录为true
	 */
	public static void Login()
	{
		StateManager.get().set(IsLogin, true);
	}
	
	/**
	 * 获得用户是否已登录过
	 * 用于页面刷新时的判断
	 * @return  是否已登录
	 */
	public static boolean IsLogin()
	{
		Boolean isLogin = StateManager.get().getProvider().getBoolean(IsLogin);
		return isLogin == null ? false : isLogin;
	}
	
	/**
	 * 清除Cookie信息
	 * 在用户推出系统时调用
	 */
	public static void ClearStateManager()
	{
		Provider provider = StateManager.get().getProvider();
		
		provider.clear(UserID);
		provider.clear(UserName);
		provider.clear(IsAdmin);
		provider.clear(SimuSystem);
		provider.clear(MenuID);
		provider.clear(IsClientSimu);
		provider.set(IsLogin, false);
		provider.clear(IsLogin);
		//清除Cookie
		//StateManager.get().setProvider(new CookieProvider("/", new Date(), null, false));
	}
	

	

}
