package com.dc.tes.ui.client.page;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dc.tes.ui.client.AppContext;
import com.dc.tes.ui.client.common.CookieManage;
import com.dc.tes.ui.client.control.IButtonConfig;
import com.dc.tes.ui.client.icons.GwtIcons;
import com.dc.tes.ui.client.model.GWTButtonConfig;
import com.dc.tes.ui.client.model.GWTSimuSystem;
import com.extjs.gxt.ui.client.GXT;
import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.data.JsonLoadResultReader;
import com.extjs.gxt.ui.client.data.ListLoadResult;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.ModelType;
import com.extjs.gxt.ui.client.image.XImages;
import com.extjs.gxt.ui.client.messages.XMessages;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.layout.FillLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.Element;

/**
 * 基础页面
 * @author sckcobe
 *
 */
public class BasePage extends LayoutContainer {
	public static final GwtIcons ICONS = GWT.create(GwtIcons.class);
	/**
	 * GWT的默认按钮图标
	 */
	public static final XImages EXTICONS = GWT.create(XImages.class);
	/**
	 * GWT的默认提示信息，比如MessageBox的按钮名称等
	 */
	public static final XMessages Message = GXT.MESSAGES;
	private String parentClassName = "";
	
	public BasePage()
	{
		//获得子类的类名（不包含包名）
		parentClassName = this.getClass().toString();
		parentClassName = parentClassName.substring(parentClassName.lastIndexOf(".")+1);
	}
	
	@Override
	protected void onRender(Element parent, int index) {
		super.onRender(parent, index);
		this.setLayout(new FillLayout());
	}
	
	protected boolean HaveLogin()
	{
		return CookieManage.IsLogin();
	}
	
	protected boolean IsAdmin()
	{
		return CookieManage.GetIsAdmin();
	}
	/**
	 * 是否为项目管理员
	 * @return
	 */
	protected boolean IsPM() {
		return CookieManage.GetIsPM();
	}
	
	protected String GetUserID()
	{
		return CookieManage.GetUserID();
	}
	
	protected String GetSystemID()
	{
		return CookieManage.GetSimuSystemID();
	}
	
	protected String GetSystemName() 
	{
		return CookieManage.GetSimuSystemName();
	}
	
	protected Integer GetLoginLogID() {
		return CookieManage.GetLoginLogID();
	}
	
	public GWTSimuSystem GetSysInfo()
	{
		if(AppContext.GetEntryPoint().comboSystem.getSelection().size() == 0)
			return null;
		return AppContext.GetEntryPoint().comboSystem.getSelection().get(0);
	}
	
	protected int GetIsClientSimu()
	{
		GWTSimuSystem gwtSimuSystem = this.GetSysInfo();
		if (gwtSimuSystem == null) {
			return 1;
		}
		return gwtSimuSystem.GetIsClient();
	}
	
	protected int GetRoleInt() {
		return CookieManage.GetRoleInt();
	}
	
	protected void Login()
	{
		CookieManage.Login();
		AppContext.GetEntryPoint().Login();
	}
	
	protected String GetVersionTypeStr()
	{
		return AppContext.GetVersion().toString();
	}
	
	public Component GetSelf()
	{
		return this;
	}
	
	@SuppressWarnings("unchecked")
	public void InitBtnConfigBar(final IButtonConfig configBar)
	{
		final String registerID = AppContext.GetBtnConfigID(parentClassName);
		if(Registry.get(registerID) != null)
			configBar.ButtonInit((Map<String,GWTButtonConfig>)Registry.get(registerID));
		
		try
		{
			final ModelType type = new ModelType();
			type.setRoot("butonConfig");   
		    type.addField(GWTButtonConfig.N_Name, GWTButtonConfig.N_Name);   
		    type.addField(GWTButtonConfig.N_Desc, GWTButtonConfig.N_Desc);
		    type.addField(GWTButtonConfig.N_Type, GWTButtonConfig.N_Type);   
		    type.addField(GWTButtonConfig.N_Visible, GWTButtonConfig.N_Visible);   
		    type.addField(GWTButtonConfig.N_Enable, GWTButtonConfig.N_Enable); 

			RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, GWT
					.getHostPageBaseURL() + AppContext.GetVersionConfigRoot() + parentClassName + ".json");
			
			builder.setCallback(new RequestCallback(){
	              public void onError(Request request, Throwable exception) {
	              }

	              public void onResponseReceived(Request request, Response response) {
	                  if(200 == response.getStatusCode()){
	                	  Map<String,GWTButtonConfig> btnConfigMap = new HashMap<String,GWTButtonConfig>();
	                	  try
	                	  {
	                		  JsonLoadResultReader<ListLoadResult<ModelData>> reader = 
	  	              			new JsonLoadResultReader<ListLoadResult<ModelData>>(type);
	  	                	  List<ModelData> result = reader.read(null, response.getText()).getData();
	  	                	  for (int i = 0; i < result.size(); i++) {
	  							GWTButtonConfig config = new GWTButtonConfig(result
	  									.get(i));
	  							btnConfigMap.put(config.GetName(), config);
	  	                	  }
	  	                	  Registry.register(registerID, btnConfigMap);
	  	                	  
	                	  }
	                	  catch(Exception ex)
	                	  {
	                		  
	                	  }
	                	  configBar.ButtonInit(btnConfigMap);
	                  }else{
	                  }
	              }

	          });
			  builder.send();
		}
		catch(Exception e)
		{
		}
	}
	
//	protected BaseListLoader<ListLoadResult<ModelData>> GetButtonConfig()
//	{
//		ModelType type = new ModelType();
//		type.setRoot("butonConfig");   
//	    type.addField(GWTButtonConfig.N_Name, GWTButtonConfig.N_Name);   
//	    type.addField(GWTButtonConfig.N_Desc, GWTButtonConfig.N_Desc);
//	    type.addField("type", "type");   
//	    type.addField(GWTButtonConfig.N_Visible, GWTButtonConfig.N_Visible);   
//	    type.addField(GWTButtonConfig.N_Enable, GWTButtonConfig.N_Enable); 
//
//		RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, GWT
//				.getHostPageBaseURL() + AppContext.GetConfigRoot() + parentClassName + ".json");
//		HttpProxy<String> proxy = new HttpProxy<String>(builder);
//
//		JsonLoadResultReader<ListLoadResult<ModelData>> reader = 
//			new JsonLoadResultReader<ListLoadResult<ModelData>>(type);
//		BaseListLoader<ListLoadResult<ModelData>> loader = 
//			new BaseListLoader<ListLoadResult<ModelData>>(proxy, reader);
//		ListStore<ModelData> store = new ListStore<ModelData>(loader);
//		return loader;
//	}
	
}

