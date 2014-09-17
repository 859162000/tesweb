package com.dc.tes.ui.client.page;

import java.util.ArrayList;
import java.util.List;

import com.dc.tes.ui.client.IUserService;
import com.dc.tes.ui.client.IUserServiceAsync;
import com.dc.tes.ui.client.IUserSysService;
import com.dc.tes.ui.client.IUserSysServiceAsync;
import com.dc.tes.ui.client.MainPage;
import com.dc.tes.ui.client.common.ServiceHelper;
import com.dc.tes.ui.client.model.GWTUser;
import com.dc.tes.ui.client.model.GWTUserSys;
import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.IconButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.HtmlContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.Viewport;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.button.IconButton;
import com.extjs.gxt.ui.client.widget.form.ListField;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class UserSys extends BasePage{
	ListField<GWTUser> lfSelected = new ListField<GWTUser>();
	ListField<GWTUser> lfUnSelected = new ListField<GWTUser>();
	List<GWTUser> userList = new ArrayList<GWTUser>();
	IUserServiceAsync userService = null;
	IUserSysServiceAsync userSysService = null;
	
	public UserSys()
	{
	}
	
	private void InitUserInfo()
	{
		userService = ServiceHelper.GetDynamicService(UserPage.SERVERNAME, IUserService.class);
		userService.GetUserByRole(1, new AsyncCallback<List<GWTUser>>()
				{
					@Override
					public void onFailure(Throwable caught) {
					}

					@Override
					public void onSuccess(List<GWTUser> result) {
						userList = result;
						SetUserInfo();
					}
				});
	}
	
	@Override
	protected void onRender(Element parent, int index) {
		super.onRender(parent, index);
		
		StringBuffer sb = new StringBuffer();
		sb.append("<div style=\"width:550px;height:700px;margin:15 15 15 15;padding:0 0 0 0;\">" +
				"<div style=\"height:39px; background-image:url(dctheme/Image/userSys/left-arc.gif);" +
					" background-position:left center;width:100%;padding:0 0 0 0;\">" +
				"<div style=\"height:39px; background-image:url(dctheme/Image/userSys/right-arc.gif); " +
					"background-position:right center;background-repeat:no-repeat;width:100%;" +
					"padding-top:10px;font:14px 宋体;\">" +
					"<div style=\"width:210px;float:left;margin-left:20px;\">" +
						"<IMG SRC=\"dctheme/Image/userSys/user1.gif\" WIDTH=\"20\" HEIGHT=\"19\" BORDER=0 align=\"absmiddle\">" +
						"已分配用户</div>" +
					"<div style=\"width:210px;float:right;margin-right:20px;\">" +
						"<IMG SRC=\"dctheme/Image/userSys/user2.gif\" WIDTH=\"20\" HEIGHT=\"19\" BORDER=0 align=\"absmiddle\">" +
						"未分配用户</div>" +
				 "</div></div>" +
				 "<div style=\"width:100%;float:left;border-left:#c1c9d3 1px solid;border-right:#c1c9d3 1px solid;" +
				 "background-color:#edf2fa;padding:10 10 10 10;\">" +
				 "<TABLE width=\"100%\" border=\"0\" cellpadding=\"3\" cellspacing=\"0\">" +
				 "<TR>" +
				 "<TD width=\"43%\" id = \"tdSelected\"></TD>" +
				 "<TD width=\"14%\" align=\"center\" id = \"tdBtn\"></TD>" +
				 "<TD width=\"43%\"  id = \"tdUnSelected\"></TD>" +
				 "</TR></TABLE></div>" +
				 "<div style=\"height:2px; background-image:url(dctheme/Image/userSys/left-bottom.gif); " +
				 "background-position:left center;width:100%;padding:0 0 0 0;text-align:right;\">" +
				 "<IMG  SRC=\"dctheme/Image/userSys/right-bottom.gif\" WIDTH=\"2\" HEIGHT=\"2\" BORDER=0 align=\"absmiddle\"></div>");

		HtmlContainer page = new HtmlContainer(sb.toString());
		
		lfSelected = new ListField<GWTUser>();
		lfSelected.setStore(new ListStore<GWTUser>());
		lfSelected.setDisplayField(GWTUser.N_name);
		lfSelected.setHeight(450);
		lfSelected.setWidth("100%");
		lfSelected.setEmptyText("尚未为该系统分配用户，请添加");
		page.add(lfSelected, "#tdSelected");
		
		page.add(GetButtonPanel(), "#tdBtn");
		
		lfUnSelected = new ListField<GWTUser>();
		lfUnSelected.setStore(new ListStore<GWTUser>());
		lfUnSelected.setDisplayField(GWTUser.N_name);
		lfUnSelected.setHeight(450);
		lfUnSelected.setWidth("100%");
		page.add(lfUnSelected, "#tdUnSelected");
		
		ToolBar topBar = new ToolBar();
		Button btnSave = new Button("保存");
		btnSave.setIcon(MainPage.ICONS.StructSave());
		btnSave.addSelectionListener(SaveHandler());
		topBar.add(btnSave);
		
		Viewport view1 = new Viewport();
	    
	    BorderLayoutData menuData = new BorderLayoutData(LayoutRegion.NORTH);
	    view1.add(topBar,menuData);
	    
		BorderLayoutData centerData = new BorderLayoutData(LayoutRegion.CENTER);
		view1.add(page, centerData);
		add(view1);
		InitUserInfo();
	}
	
	private Component GetButtonPanel()
	{
		StringBuffer sb = new StringBuffer();
		
		sb.append("<Span id = \"s_All\"></span>" +
				 "<br><Span id = \"s_Sin\"></span>" +
				 "<br><Span id = \"us_Sin\"></span>" +
				 "<br><Span id = \"us_All\"></span>");

		HtmlContainer btnHtml = new HtmlContainer(sb.toString());
		
		btnHtml.add(new IconButton("SelAllIcon",AddAllHandler()), "#s_All");
		btnHtml.add(new IconButton("SelSingIcon",AddHandler()), "#s_Sin");
		btnHtml.add(new IconButton("UnSelSingIcon",RemoveHandler()), "#us_Sin");
		btnHtml.add(new IconButton("UnSelAllIcon",RemoveAllHandler()), "#us_All");
		
		return btnHtml;
	}
	
	private void SetUserInfo()
	{
		userSysService = ServiceHelper.GetDynamicService("userSys", IUserSysService.class);
		userSysService.GetUserList(GetSystemID(), "", new AsyncCallback<List<GWTUserSys>>()
				{
					@Override
					public void onFailure(Throwable caught) {
					}

					@Override
					public void onSuccess(List<GWTUserSys> result) {
						List<String> selectedList = new ArrayList<String>();
						for (GWTUserSys sys : result)
							selectedList.add(sys.GetUserID());

						for (GWTUser user : userList) {
							if (selectedList.indexOf(user.getUserID()) == -1) 
								lfUnSelected.getStore().add(user);
							else
								lfSelected.getStore().add(user);
						}
					}
				});
	}
	
	private SelectionListener<IconButtonEvent> AddHandler() {
		return new SelectionListener<IconButtonEvent>() {
			public void componentSelected(IconButtonEvent ce) {
				List<GWTUser> dragList = lfUnSelected.getSelection();
				for (GWTUser model : dragList) {
					lfUnSelected.getStore().remove(model);
			        }
				lfSelected.getStore().add(dragList);
			}
		};
	}
	
	private SelectionListener<IconButtonEvent> AddAllHandler() {
		return new SelectionListener<IconButtonEvent>() {
			public void componentSelected(IconButtonEvent ce) {
				List<GWTUser> dragList = lfUnSelected.getStore().getModels();
				lfSelected.getStore().add(dragList);
				lfUnSelected.getStore().removeAll();
			}
		};
	}
	
	private SelectionListener<IconButtonEvent> RemoveHandler() {
		return new SelectionListener<IconButtonEvent>() {
			public void componentSelected(IconButtonEvent ce) {
				List<GWTUser> dragList = lfSelected.getSelection();
				for (GWTUser model : dragList) {
					lfSelected.getStore().remove(model);
			        }
				lfUnSelected.getStore().add(dragList);
			}
		};
	}
	
	private SelectionListener<IconButtonEvent> RemoveAllHandler() {
		return new SelectionListener<IconButtonEvent>() {
			public void componentSelected(IconButtonEvent ce) {
				List<GWTUser> dragList = lfSelected.getStore().getModels();
				lfSelected.getStore().removeAll();
				lfUnSelected.getStore().add(dragList);
			}
		};
	}
	
	private SelectionListener<ButtonEvent> SaveHandler()
	{
		return new SelectionListener<ButtonEvent>() {
			public void componentSelected(ButtonEvent ce) {
				List<GWTUser> saveList = lfSelected.getStore().getModels();
				userSysService = ServiceHelper.GetDynamicService("userSys", IUserSysService.class);
				userSysService.SaveRelation(GetSystemID(), saveList, new AsyncCallback<Void>()
						{
							@Override
							public void onFailure(Throwable caught) {
								MessageBox.alert("友情提示", "保存失败", null);
							}

							@Override
							public void onSuccess(Void result) {
								MessageBox.alert("友情提示", "保存成功", null);
							}
						});
			}
		};
	}
}
