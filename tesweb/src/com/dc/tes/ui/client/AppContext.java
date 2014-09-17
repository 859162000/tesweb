package com.dc.tes.ui.client;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dc.tes.ui.client.enums.VersionType;
import com.dc.tes.ui.client.model.GWTButtonConfig;
import com.dc.tes.ui.client.model.GWTProperties;
import com.dc.tes.ui.client.page.MonitorPage;
import com.dc.tes.ui.client.page.BasePage_Register;
import com.dc.tes.ui.client.page.SystemLaunchPage;
import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.data.JsonLoadResultReader;
import com.extjs.gxt.ui.client.data.ListLoadResult;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.ModelType;
import com.extjs.gxt.ui.client.widget.TabPanel;
import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.Response;


/**
 * 应用上下文类（帮助类）
 * 1）存储，获取当前选择的系统名称
 * 
 * @author scckobe
 *
 */
public class AppContext {
	private static final String EntryName = "entryName";
	private static final String versionName = "Version";
	public static final String MenuConfig = "menuConfig";
	private static final String UseMockData = "UseMockData";
	private static boolean allowScroll = true;
	private static boolean allowNotify = true;
	
	private static TabPanel tabPanel = null;
	private static MonitorPage monitorPage = null;
	private static SystemLaunchPage launchPage = null;
	private static Map<String,BasePage_Register> regiterPageList = new HashMap<String, BasePage_Register>();

	public static void setAllowScroll(boolean allow){
		allowScroll = allow;
	}
	
	public static boolean getAllowScroll(){
		return allowScroll;
	}
	
	public static void setAllowNotify(boolean allow){
		allowNotify = allow;
	}
	
	public static boolean getAllowNotify(){
		return allowNotify;
	}
	
	public static MainPage GetEntryPoint()
	{
		return (MainPage)Registry.get(EntryName);
	}
	
	public static void SetEntryPoint(MainPage entryPoint)
	{
		Registry.register(EntryName, entryPoint);
	}
	
	public static void SetVersion(String version)
	{
		Registry.register(versionName, version);
	}
	
	public static String GetConfigRoot()
	{
		return "Config/";
	}
	
	public static String GetVersionConfigRoot()
	{
		return "Config/" + GetVersion().toString() + "/";
	}
	
	public static String GetBtnConfigID(String className)
	{
		return "btnConfig" + className;
	}
	
	public static String StoreConfig(List<GWTProperties> propList)
	{
		String menuStr = "";
		for(GWTProperties property : propList)
		{
			String key = property.GetKey().toString();
			if(key.compareToIgnoreCase(MenuConfig) == 0)
				menuStr = property.GetValue().toString();
			else
				Registry.register(key, property.GetValue());
		}
			
		return menuStr;
	}
	
	public static BasePage_Register GetRegisterPage(String registerID)
	{
		return regiterPageList.get(registerID);
	}
	
	public static void RegisterPage(String registerID,BasePage_Register page)
	{
		if(!regiterPageList.containsKey(registerID))
			regiterPageList.put(registerID, page);
	}
	
	public static void UnRegisterPage(String registerID)
	{
		regiterPageList.remove(registerID);
	}
	
	
	
	@SuppressWarnings("unchecked")
	public static Map<String,GWTButtonConfig> GetBtnConfig(String className)
	{
		final String registerID = GetBtnConfigID(className);
		if(Registry.get(registerID) != null)
			return (Map<String,GWTButtonConfig>)Registry.get(registerID);
		
		final Map<String,GWTButtonConfig> btnConfigMap = new HashMap<String,GWTButtonConfig>();
		try
		{
			final ModelType type = new ModelType();
			type.setRoot("butonConfig");   
		    type.addField(GWTButtonConfig.N_Name, GWTButtonConfig.N_Name);   
		    type.addField(GWTButtonConfig.N_Desc, GWTButtonConfig.N_Desc);
		    type.addField("type", "type");   
		    type.addField(GWTButtonConfig.N_Visible, GWTButtonConfig.N_Visible);   
		    type.addField(GWTButtonConfig.N_Enable, GWTButtonConfig.N_Enable); 

			RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, GWT
					.getHostPageBaseURL() + GetVersionConfigRoot() + className + ".json");
			
			builder.setCallback(new RequestCallback(){
	              public void onError(Request request, Throwable exception) {
	              }

	              public void onResponseReceived(Request request, Response response) {
	                  if(200 == response.getStatusCode()){
	                	  JsonLoadResultReader<ListLoadResult<ModelData>> reader = 
	              			new JsonLoadResultReader<ListLoadResult<ModelData>>(type);
	                	  List<ModelData> result = reader.read(null, response.getText()).getData();
	                	  for (int i = 0; i < result.size(); i++) {
							GWTButtonConfig config = new GWTButtonConfig(result
									.get(i));
							btnConfigMap.put(config.GetName(), config);
	                	  }
	                	Registry.register(registerID, btnConfigMap);
	                  }else{
	                  }
	              }

	          });
			  builder.send();
		}
		catch(Exception e)
		{
		}
		return btnConfigMap;
	}

	
	public static void setTabPanel(TabPanel tabPanel) {
		AppContext.tabPanel = tabPanel;
	}

	public static TabPanel getTabPanel() {
		return tabPanel;
	}
	
	public static String getCurrentTabId() {
		if(tabPanel.getSelectedItem() != null)
			return tabPanel.getSelectedItem().getTabPanel().getId();
		return "";
	}
	
	public static MonitorPage GetMonitorPage() {
		return monitorPage;
	}
	
	public static void SetMonitorPage(MonitorPage page) {
		monitorPage = page;
	}
	
	public static SystemLaunchPage GetLaunchPage() {
		return launchPage;
	}
	
	public static void SetLaunchPage(SystemLaunchPage page) {
		launchPage = page;
	}
	
	public static String getFlashUrl(){
		return "gxt/chart/open-flash-chart.swf";
	}
	
	public static boolean getUseMockData(){
		return Boolean.valueOf(Registry.get(UseMockData).toString());
	}
	
	public static VersionType GetVersion()
	{
		try
		{
			return VersionType.valueOf(Registry.get(versionName).toString());
		}
		catch (Exception e) {
		}
		return VersionType.Fprouter;
	}
}
