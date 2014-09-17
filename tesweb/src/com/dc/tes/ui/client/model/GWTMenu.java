package com.dc.tes.ui.client.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.dc.tes.ui.client.common.CookieManage;
import com.extjs.gxt.ui.client.data.BaseTreeModel;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;

/**
 * 菜单实体类
 * @author scckobe
 *
 */
public class GWTMenu extends BaseTreeModel implements Serializable {
	private static final long serialVersionUID = -8192891178992417374L;
	
	/**
	 * 菜单项描述
	 */
	public static String Desc = "Desc";
	/**
	 * 菜单项ID，加载时自动生成
	 */
	public static String ID = "ID";
	/**
	 * 菜单项Icon名称
	 */
	public static String IconName = "Icon";
	/**
	 *  菜单打开的URL
	 */
	private static String URL = "URL";
	/**
	 * 菜单所应该打开的Tab列表
	 */
	public static String TabItem = "TabItem";
	/**
	 *  菜单ID临时变量
	 */
	private static int menuID = 0;
	
	/**
	 * 默认构造函数（序列化必须的）
	 */
	private GWTMenu()
	{
	}
	
	/**
	 * 构造函数（虚有，不允许外部调用）
	 * @param menuDesc 菜单项描述
	 * @param menuIcon 菜单项Icon名称
	 */
	private GWTMenu(int id,String menuDesc,String menuIcon,String menuUrl)
	{
		this.set(ID,id);
		this.set(Desc, menuDesc);
		this.set(IconName, menuIcon);
		this.set(URL, menuUrl);
	}
	
	/**
	 * 为菜单添加他所对应的Tab列表
	 * @param jsonTabArray json中的Tab数组对象
	 */
	private void AddTabItem(JSONArray jsonTabArray)
	{
		if(jsonTabArray == null || jsonTabArray.size() == 0)
			return;
		List<GWTMenuTab> tabList = new ArrayList<GWTMenuTab>();
		for(int i = 0; i < jsonTabArray.size() ; i++)
		{
			tabList.add(new GWTMenuTab(jsonTabArray.get(i).isObject()));
		}
		this.set(TabItem,tabList);
	}
	
	/**
	 * 获得菜单ID
	 * @return 菜单ID
	 */
	public String GetID()
	{
		return get(ID).toString();
	}
	
	/**
	 * 获得菜单项名称 
	 * @return 菜单项名称
	 */
	public String getDesc()
	{
		return get(Desc);
	}
	
	/**
	 * 获得菜单Icon
	 * @return 菜单Icon
	 */
	public String getIcon()
	{
		return get(IconName);
	}
	
	/**
	 * 获得菜单项URL
	 * @return 菜单项URL
	 */
	public String getURL()
	{
		return get(URL);
	}
	
	/**
	 * 获得菜单对应打开的Tab列表
	 * @return Tab列表
	 */
	@SuppressWarnings("unchecked")
	public List<GWTMenuTab> GetTabList()
	{
		try
		{
			return (List<GWTMenuTab>)get(TabItem);
		}
		catch(Exception ex)
		{
			return new ArrayList<GWTMenuTab>();
		}
	}
	
	
	/**
	 * 静态函数：根据输入的Json字符串，解析成一个完整的菜单树
	 * @param menuStr Json字符串
	 * @return 菜单树形根节点
	 */
	public static GWTMenu GetMenuList(String menuStr)
	{
		//解析菜单
		JSONValue obj1 = JSONParser.parse(menuStr);
		JSONArray menuList = obj1.isArray();
		GWTMenu menu = new GWTMenu();
		for(int i = 0 ; i < menuList.size() ; i++)
		{
			AddGWTMenu(menu,menuList.get(i));
		}
		return menu;
	}
	
	/**
	 * 添加子菜单
	 * @param parentMenu 父菜单
	 * @param menuItem   Json对象
	 */
	private static void AddGWTMenu(GWTMenu parentMenu,JSONValue menuItem)
	{
		JSONObject itemObj = menuItem.isObject();

		if(NeedAdd(GetString(itemObj,"Role"))&&IsClient(GetString(itemObj, "Type")))
		{
			GWTMenu menu = new GWTMenu(menuID++,GetString(itemObj,Desc),
					GetString(itemObj,IconName),GetString(itemObj,URL));
			
			menu.AddTabItem(itemObj.get(TabItem).isArray());
			JSONArray childList = itemObj.get("Child").isArray();
			if (childList != null) {
				for (int i = 0; i < childList.size(); i++) {
					AddGWTMenu(menu, childList.get(i));
				}
			}
			parentMenu.children.add(menu);
		}
	}
	

	private static String GetString(JSONObject itemObj,String property)
	{
		JSONValue jValue = itemObj.get(property);
		if(jValue == null)
			return "";
		return jValue.isString().stringValue();
	}
	
	/**
	 * 判断当前菜单项是否符合权限要求
	 * @param roleName json数据中的Role字符串
	 * @return true：符合 false：不符合
	 */
	private static boolean NeedAdd(String roleName)
	{
		int index = CookieManage.GetRoleInt();
		if(index == -1){
			index++;
		}
		return roleName.charAt(index) == '1';
	}
	
	private static boolean IsClient(String type) {
		// TODO Auto-generated method stub
		if(type.isEmpty() || CookieManage.GetIsClientSimu() == 2){
			return true;
		}
		if(type.equals("client") && CookieManage.GetIsClientSimu() == 1){
			return true;
		}
		if(type.equals("recv") && CookieManage.GetIsClientSimu() == 0){
			return true;
		}
		return false;
	}
}
