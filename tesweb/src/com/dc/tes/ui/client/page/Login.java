package com.dc.tes.ui.client.page;


import java.util.ArrayList;
import java.util.List;

import com.dc.tes.ui.client.ISimuSystemService;
import com.dc.tes.ui.client.ISimuSystemServiceAsync;
import com.dc.tes.ui.client.IUserService;
import com.dc.tes.ui.client.IUserServiceAsync;
import com.dc.tes.ui.client.common.CookieManage;
import com.dc.tes.ui.client.common.ServiceHelper;
import com.dc.tes.ui.client.control.RequireTextField;
import com.dc.tes.ui.client.model.GWTSimuSystem;
import com.dc.tes.ui.client.model.GWTUser;
import com.extjs.gxt.ui.client.Style;
import com.extjs.gxt.ui.client.data.BaseListLoader;
import com.extjs.gxt.ui.client.data.ListLoader;
import com.extjs.gxt.ui.client.data.RpcProxy;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.IconButtonEvent;
import com.extjs.gxt.ui.client.event.KeyListener;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.HtmlContainer;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.button.IconButton;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.RowNumberer;
import com.extjs.gxt.ui.client.widget.layout.FillLayout;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class Login extends BasePage {
	ISimuSystemServiceAsync systemService = null;
	IUserServiceAsync userService = null;
	RequireTextField loginName;
	RequireTextField loginPWD;
	IconButton loginBtn = new IconButton();
	public Login()
	{
		CookieManage.ClearStateManager();
	}
	
	@Override
	protected void onRender(Element parent, int index) {
		super.onRender(parent, index);
		 HtmlLoginGu();
	}
	

	private void HtmlLoginGu()
	{
		StringBuffer sb = new StringBuffer();
		sb.append("<table style = 'width:100%;height:100%;' background=\"dctheme/Image/login_bg.png\"" +
				"leftmargin=\"0\" topmargin=\"0\" marginwidth=\"0\" marginheight=\"0\">" +
				"<tr><td valign=\"top\">" +
				"<table width=\"831px\" height=\"391px\" border=\"0\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\"" +
				" style=\"margin-top:110px;\"><tr>" +
				"<td valign=\"top\" >" +
					"<table width=\"400\" border=\"0\" cellspacing=\"0\" cellpadding=\"10\" " +
					"style=\"margin-top:340px;margin-left:366px;\"><tr>" +
						"<td width=\"200\" align=\"right\" height=\"35px\">" +
						"<span style=\"font-size:12px;font-family:宋体; color:#000000;\">用户名:</span></td>" +
						"<td width=\"132\"  id = 'tdloginName'></td>" +
						"<td width=\"50\"></td>"+
						"<td align=\"right\" height=\"35px\">" +
						"<span style=\"font-size:12px;font-family:宋体; color:#000000;\">密&nbsp;&nbsp;码:</span></td>" +
						"<td id = 'tdloginPWD'></td>" +
					"</tr>" +
					"<tr>" +
						"<td>&nbsp;</td>" +
						"<td id = 'tdLgoin'></td>" +
					"</tr>" +
					"</table>" +
				"</td></tr></table></tr></table>");

		final HtmlContainer login = new HtmlContainer(sb.toString());
		loginName = new RequireTextField("登录名");
		loginName.setEmptyText("请输入用户名");
		loginName.setWidth(122);
//		loginName.setStyleAttribute("border", "#ffffff");
//		loginName.setStyleAttribute("solid", "1px");
//		loginName.setStyleAttribute("background-color", "#3e74a3");
//		loginName.setStyleAttribute("color", "#ffffff");
		login.add(loginName, "#tdloginName");

		loginPWD = new RequireTextField("密码");
		loginPWD.setEmptyText("请输入登录密码");
		loginPWD.setWidth(122);
//		loginPWD.setStyleAttribute("border", "#ffffff");
//		loginPWD.setStyleAttribute("solid", "1px");
//		loginPWD.setStyleAttribute("background-color", "#3e74a3");
//		loginPWD.setStyleAttribute("color", "#ffffff");
		loginPWD.setPassword(true);
		login.add(loginPWD, "#tdloginPWD");
		
		loginBtn.setStyleName("loginBtn");
		loginBtn.addSelectionListener(IconLoginListerner());
		login.add(loginBtn, "#tdLgoin");
		
		loginName.addKeyListener(ButtonKeyListerner());
		loginPWD.addKeyListener(ButtonKeyListerner());
		
		this.add(login);
	}
	
	private SelectionListener<IconButtonEvent> IconLoginListerner()
	{
		return new SelectionListener<IconButtonEvent>() {   
		      @Override  
		      public void componentSelected(final IconButtonEvent ce) {
		    	  CommitUserInfo();
		      }  
		    };
	}
	
	private void CommitUserInfo() {
		if (!loginName.isValid() || !loginPWD.isValid())
			return;

		SetLoginEnabled(false);
		userService = ServiceHelper.GetDynamicService(UserPage.SERVERNAME, IUserService.class);
		userService.GetUserInfo(loginName.getValue(),
				loginPWD.getValue(), new AsyncCallback<GWTUser>() {
					@Override
					public void onFailure(Throwable caught) {
						MessageBox.alert("通信失败", "与服务器通信失败，请与维护人员联系", null);
						SetLoginEnabled(true);
					}

					@Override
					public void onSuccess(GWTUser result) {
						if (result == null) {
							MessageBox.alert("登录失败", "登录名不存在或密码错误，请重新输入", null);
							loginName.focus();
							SetLoginEnabled(true);
						} else {
							CookieManage.SetUserInfo(result);
							if (!IsAdmin())
								SelectedSystem(result.getUserID());
							else
								Login();
							SetLoginEnabled(true);
						}
					}
				});
	}

	@SuppressWarnings("unchecked")
	private void SelectedSystem(final String userID)
	{
		final Window window = new Window();
		window.setHeading("模拟系统选择");
		window.setSize(470, 280);
		window.setPlain(true);
		window.setModal(true);
		window.setBlinkModal(true);
		
		final Button btnOK = new Button("确定");
		
		RpcProxy<List<GWTSimuSystem>> proxy = new RpcProxy<List<GWTSimuSystem>>()
		{
			@Override
			protected void load(Object loadConfig,
					AsyncCallback<List<GWTSimuSystem>> callback) {
				systemService = ServiceHelper.GetDynamicService("simuSys", ISimuSystemService.class);
				systemService.GetListByUserID(userID,callback);
			}
		};
		final ListLoader<GWTSimuSystem> loader = new BaseListLoader(proxy);   
		
		List<ColumnConfig> columns = new ArrayList<ColumnConfig>();
		columns.add(new RowNumberer());
		columns.add(new ColumnConfig(GWTSimuSystem.N_SystemNo, "系统号", 100));
		columns.add(new ColumnConfig(GWTSimuSystem.N_SystemName, "系统名称", 100));
		ColumnConfig conf = new ColumnConfig(GWTSimuSystem.N_Desc, "备注", 200);
		conf.setSortable(false);
		columns.add(conf);
		
		ColumnModel cm = new ColumnModel(columns);
		ListStore<GWTSimuSystem> store = new ListStore<GWTSimuSystem>(loader);
		loader.load(null);
		
		final Grid<GWTSimuSystem> dataGrid = new Grid<GWTSimuSystem>(store, cm);
		dataGrid.setLoadMask(true);
		dataGrid.setBorders(false);
		dataGrid.setHeight(280);
		
		dataGrid.getView().setEmptyText("您尚未可使用的模拟系统，无法登陆，请与管理员联系");  
		dataGrid.getSelectionModel().addListener(
				Events.SelectionChange,
				new Listener<SelectionChangedEvent<GWTSimuSystem>>() {
					public void handleEvent(
							SelectionChangedEvent<GWTSimuSystem> be) {
						if(be.getSelection().size()==0)
							btnOK.disable();
						else
							btnOK.enable();
					}
				});

		btnOK.disable();
		btnOK.addSelectionListener(new SelectionListener<ButtonEvent>() {
			public void componentSelected(ButtonEvent ce) {
				window.hide();
				CookieManage.SetSimuSystemID(dataGrid.getSelectionModel().getSelectedItem().GetSystemID());
				Login();
			}
		});
		window.addButton(btnOK);
		window.addButton(new Button("取消", new SelectionListener<ButtonEvent>() {
			public void componentSelected(ButtonEvent ce) {
				window.hide();
			}
		}));
		
		window.setLayout(new FillLayout());
		window.setScrollMode(Style.Scroll.NONE);
		window.add(dataGrid);
		window.show();
	}
	
	private void SetLoginEnabled(boolean enabled)
	{
		loginBtn.setEnabled(enabled);
		loginName.setEnabled(enabled);
		loginPWD.setEnabled(enabled);
	}
	
	protected KeyListener KeyListerner()
	{
		return new KeyListener()
		{
			@Override
			public void handleEvent(ComponentEvent e) 
			{
				if( e.getKeyCode() == 13)
				{
					if(loginBtn.isEnabled())
						loginBtn.fireEvent(Events.Select);
				}
			}
		};
	}
	
	private KeyListener ButtonKeyListerner()
	{
		return new KeyListener()
		{
			@Override
			public void handleEvent(ComponentEvent e) 
			{
				if( e.getKeyCode() == 13)
				{
					if(loginBtn.isEnabled())
						loginBtn.fireEvent(Events.Select);
				}
			}
		};
	}
}