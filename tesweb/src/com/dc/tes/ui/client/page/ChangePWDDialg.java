package com.dc.tes.ui.client.page;

import com.dc.tes.ui.client.IUserService;
import com.dc.tes.ui.client.IUserServiceAsync;
import com.dc.tes.ui.client.common.ServiceHelper;
import com.dc.tes.ui.client.control.RequireTextField;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class ChangePWDDialg extends BasePage {
	IUserServiceAsync userService = null;
	
	public ChangePWDDialg()
	{
		final Window window = new Window();
		window.setHeading("修改密码");
		window.setSize(300, 130);
		window.setPlain(true);
		window.setModal(true);
		window.setBlinkModal(true);
		window.setLayout(new FitLayout());

		final FormPanel formPanel = new FormPanel();
		formPanel.setBorders(false);
		formPanel.setBodyBorder(false);
		formPanel.setLabelWidth(55);
		formPanel.setPadding(5);

		formPanel.setHeaderVisible(false);

		FormData formData = new FormData("85%");
		String labelStyle = "width:80px;";
		

		final RequireTextField tfPwd = new RequireTextField("密码");
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

		Button btnOK = new Button("确定", new SelectionListener<ButtonEvent>() {
			public void componentSelected(ButtonEvent ce) {
				if (!formPanel.isValid())
					return;
				userService = ServiceHelper.GetDynamicService(UserPage.SERVERNAME, IUserService.class);
				userService.UpdatePWD(GetUserID(), tfPwd.getValue(), GetRoleInt(),
						GetLoginLogID(), new AsyncCallback<Void>() {
							public void onFailure(Throwable caught) {
								caught.printStackTrace();
								MessageBox.alert("友情提示", "密码修改失败", null);
							}

							@SuppressWarnings("deprecation")
							public void onSuccess(Void result) {
								MessageBox.alert("友情提示", "密码修改成功", null);
								window.close();
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

		window.show();
	}
}
