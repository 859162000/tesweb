package com.dc.tes.ui.client.page;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.dc.tes.ui.client.IUserService;
import com.dc.tes.ui.client.IUserServiceAsync;
import com.dc.tes.ui.client.common.ServiceHelper;
import com.dc.tes.ui.client.control.ConfigToolBar;
import com.dc.tes.ui.client.control.DistTextField;
import com.dc.tes.ui.client.control.FormContentPanel;
import com.dc.tes.ui.client.control.GridContentPanel;
import com.dc.tes.ui.client.model.GWTStock;
import com.dc.tes.ui.client.model.GWTUser;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.extjs.gxt.ui.client.data.RpcProxy;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.extjs.gxt.ui.client.widget.toolbar.FillToolItem;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class UserPage extends BasePage {
	IUserServiceAsync userService = null;
	GWTUser EditUser = null;

	GridContentPanel<GWTUser> panel;
	FormContentPanel<GWTUser> detailPanel;
	ConfigToolBar bottomBar;
	public static String SERVERNAME = "user";

	public UserPage() {
	}

	@Override
	protected void onRender(Element parent, int index) {
		super.onRender(parent, index);

		userService = ServiceHelper.GetDynamicService(SERVERNAME, IUserService.class);

		panel = new GridContentPanel<GWTUser>();
		RpcProxy<PagingLoadResult<GWTUser>> proxy = new RpcProxy<PagingLoadResult<GWTUser>>() {
			@Override
			public void load(Object loadConfig,
					AsyncCallback<PagingLoadResult<GWTUser>> callback) {
				userService.GetUserList(panel.GetSearchCondition(),
						(PagingLoadConfig) loadConfig, callback);
			}
		};

		panel.setProxy(proxy);
		panel.setColumns(GetColumnConfig());
		panel.DrowSearchBar();
		panel.DrowGridView();

		bottomBar = new ConfigToolBar();
		bottomBar.initPageToolBar(panel.getLoader());
		bottomBar.AddWidget(new FillToolItem());
		bottomBar.AddNewBtn("btnAdd", AddHandler());
		bottomBar.AddEditBtn("btnEdit", EditHandler());
		bottomBar.AddDelBtn("btnDel", DelHandler());
		panel.setBottomBar(bottomBar);
		InitBtnConfigBar(bottomBar);
		add(panel);

		detailPanel = new FormContentPanel<GWTUser>();
		detailPanel.setBindInfo(GetDetailHashMap());
		panel.setDetailForm(detailPanel);
		add(detailPanel);
	}

	/**
	 * 获得Grid的列配置列表
	 * 
	 * @return Grid的列配置列表
	 */
	private List<ColumnConfig> GetColumnConfig() {
		List<ColumnConfig> columns = new ArrayList<ColumnConfig>();

		columns.add(new ColumnConfig(GWTUser.N_name, "用户名", 150));
		columns.add(new ColumnConfig(GWTUser.N_description, "用户说明", 150));
		columns.add(new ColumnConfig(GWTUser.N_isAdmin_CHS, "角色", 250));

		return columns;
	}

	/**
	 * 获得详细信息绑定的Hash列表
	 * 
	 * @return Map<String,String> 对应 Map<对应绑定的值名称,字段显示名称>
	 */
	public Map<String, String> GetDetailHashMap() {
		Map<String, String> detailMap = new LinkedHashMap<String, String>();
		detailMap.put(GWTUser.N_name, "用户名");
		detailMap.put(GWTUser.N_description, "用户说明");
		detailMap.put(GWTUser.N_isAdmin_CHS, "角色");
		return detailMap;
	}

	private void CreateEditForm() {
		final Window window = new Window();

		window.setSize(320, 250);
		window.setPlain(true);
		window.setModal(true);
		window.setBlinkModal(false);
		window.setLayout(new FitLayout());

		final FormPanel formPanel = new FormPanel();
		formPanel.setBorders(false);
		formPanel.setBodyBorder(false);
		formPanel.setLabelWidth(55);
		formPanel.setPadding(5);

		formPanel.setHeaderVisible(false);

		FormData formData = new FormData("85%");
		String labelStyle = "width:80px;";
		final DistTextField tfUserName = new DistTextField(EditUser, EditUser
				.getUserName(), "用户名");
		tfUserName.setLabelStyle(labelStyle);
		tfUserName.setMaxLength(32);
		formPanel.add(tfUserName, formData);
		
		final DistTextField tfUserDesc = new DistTextField(EditUser, EditUser
				.getDescription(), "用户说明");
		tfUserDesc.setLabelStyle(labelStyle);
		tfUserDesc.setMaxLength(32);
		formPanel.add(tfUserDesc, formData);

		final TextField<String> tfPwd =  new TextField<String>();
		tfPwd.setFieldLabel("密码");
		if(EditUser.IsNew()){
			tfPwd.setAllowBlank(false);
		}else{
			tfPwd.setToolTip("如不修改密码则不填写");
		}
		tfPwd.setMaxLength(16);
		tfPwd.setLabelStyle(labelStyle);
		tfPwd.setPassword(true);

		final TextField<String> tfPwdComfrim = new TextField<String>() {
			@Override
			protected boolean validateValue(String value) {
				if (!value.equals(tfPwd.getRawValue())) {
					markInvalid("与密码不一致");
					return false;
				}
				return true;
			}
		};
		tfPwdComfrim.setFieldLabel("确认密码");
		tfPwdComfrim.setLabelStyle(labelStyle);
		tfPwdComfrim.setAllowBlank(false);
		tfPwdComfrim.setPassword(true);
		tfPwdComfrim.setSelectOnFocus(true);

		formPanel.add(tfPwd, formData);
		formPanel.add(tfPwdComfrim, formData);

		final ComboBox<GWTStock> cbIsAdmin = new ComboBox<GWTStock>();
		cbIsAdmin.setFieldLabel("用户角色");
		cbIsAdmin.setLabelStyle(labelStyle);
		cbIsAdmin.setStyleAttribute("text-align", "left");
		cbIsAdmin.setTriggerAction(TriggerAction.ALL);
		cbIsAdmin.setEditable(false);
		cbIsAdmin.setDisplayField(GWTStock.N_Name);
		cbIsAdmin.setValueField(GWTStock.N_Pos);
		ListStore<GWTStock> store = new ListStore<GWTStock>();
		if(IsAdmin()){
			store.add(new GWTStock("系统管理员", "0"));
		}
		store.add(new GWTStock("测试人员", "1"));
		store.add(new GWTStock("项目管理员", "2"));
		cbIsAdmin.setStore(store);
		formPanel.add(cbIsAdmin, formData);

		Button btnOK = new Button("确定", new SelectionListener<ButtonEvent>() {
			public void componentSelected(ButtonEvent ce) {
				if (!formPanel.isValid())
					return;
				if(!EditUser.IsNew()){
					if(EditUser.getIsAdmin()==0 && IsAdmin()==false){
						MessageBox.alert("错误提示", "对不起，您没有权限修改系统管理员用户信息", null);
						return ;
					}
				}
				EditUser.SetValue(tfUserName.getValue(), tfUserDesc.getValue(), tfPwd.getValue(),
						Integer.parseInt(cbIsAdmin.getValue().getPos()));
				
				userService.SaveUser(EditUser, GetLoginLogID(), new AsyncCallback<Boolean>() {
					public void onFailure(Throwable caught) {
						caught.printStackTrace();
						MessageBox.alert("错误信息", "保存失败", null);
					}

					
					public void onSuccess(Boolean suc) {
						panel.loaderReLoad(EditUser.IsNew());
						if (suc)
							window.hide();
						else {
							tfUserName.focus();
							tfUserName.EnforceValidate();
						}
					}
				});
			}
		});
		window.addButton(btnOK);

		window.addButton(new Button("取消", new SelectionListener<ButtonEvent>() {
			@SuppressWarnings("deprecation")
			public void componentSelected(ButtonEvent ce) {
				window.close();
			}
		}));
		window.add(formPanel);

		if (EditUser.IsNew()) {
			window.setHeading("新增用户");
		} else { //编辑用户
			tfUserName.setValue(EditUser.getUserName());
			tfUserDesc.setValue(EditUser.getDescription());
			//tfPwd.setValue(EditUser.getPassword());
			//tfPwdComfrim.setValue(EditUser.getPassword());
			cbIsAdmin.setValue(store.findModel(GWTStock.N_Pos, String.valueOf(EditUser.getIsAdmin())));
			window.setHeading("编辑用户");
		}

		window.show();
	}

	private SelectionListener<ButtonEvent> AddHandler() {
		return new SelectionListener<ButtonEvent>() {
			public void componentSelected(ButtonEvent ce) {
				EditUser = new GWTUser();
				CreateEditForm();
			}
		};
	}

	private SelectionListener<ButtonEvent> EditHandler() {
		return new SelectionListener<ButtonEvent>() {
			public void componentSelected(ButtonEvent ce) {
				List<GWTUser> selectedItems = panel.getDataGrid()
						.getSelectionModel().getSelectedItems();
				if (selectedItems.size() != 1) {
					MessageBox.alert("Alert", "请选择一个案例进行编辑", null);
					return;
				}
				EditUser = selectedItems.get(0);
				CreateEditForm();
			}
		};
	}

	private SelectionListener<ButtonEvent> DelHandler() {
		return new SelectionListener<ButtonEvent>() {
			public void componentSelected(ButtonEvent ce) {
				final List<GWTUser> delList = panel.getSelection();
				String CurrentID = GetUserID();
				for (int i = 0; i < delList.size(); i++) {
					if (CurrentID.compareTo(delList.get(i).toString()) == 0) {
						MessageBox.alert("友情提示", "无法删除本人，请重新选择再删除！", null);
						return;
					}
				}
				MessageBox.confirm("提示信息", "是否确认删除",
						new Listener<MessageBoxEvent>() {
							public void handleEvent(MessageBoxEvent be) {
								Button msgBtn = be.getButtonClicked();
								if (msgBtn.getText().equalsIgnoreCase("Yes"))
									userService.DeleteUser(
											panel.getSelection(), GetLoginLogID(),
											new AsyncCallback<Void>() {
												public void onFailure(
														Throwable caught) {
													caught.printStackTrace();
													MessageBox.alert("错误提示",
															"删除失败", null);
												}

												public void onSuccess(Void obj) {
													panel.reloadGrid();
												}
											});
							}
						});
			}
		};
	}

}
