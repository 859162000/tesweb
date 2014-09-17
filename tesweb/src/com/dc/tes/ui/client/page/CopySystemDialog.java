package com.dc.tes.ui.client.page;

import com.dc.tes.ui.client.AppContext;
import com.dc.tes.ui.client.ICopiedSystemService;
import com.dc.tes.ui.client.ICopiedSystemServiceAsync;
import com.dc.tes.ui.client.common.CookieManage;
import com.dc.tes.ui.client.common.ServiceHelper;
import com.dc.tes.ui.client.control.DistTextField;
import com.dc.tes.ui.client.model.GWTCopiedSystem;
import com.dc.tes.ui.client.model.GWTSimuSystem;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.extjs.gxt.ui.client.data.PagingLoader;
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


public class CopySystemDialog extends Window{
	
	int m_iOldSystemId;
	GWTSimuSystem m_gwtSystem = null;
	GWTCopiedSystem m_copiedSystem = null;
	ICopiedSystemServiceAsync m_cpSysService = null;
	
	PagingLoader<PagingLoadResult<ModelData>> loader = null;

	public CopySystemDialog(int iOldSystemId, GWTSimuSystem gwtSystem, PagingLoader<PagingLoadResult<ModelData>> loader)
	{
		m_cpSysService = ServiceHelper.GetDynamicService("copiedSystem", ICopiedSystemService.class);
		this.m_copiedSystem = new GWTCopiedSystem();
		m_iOldSystemId = iOldSystemId;
		m_gwtSystem = gwtSystem;
		this.loader = loader;
		InitDialog();
	}
	
	
	private void InitDialog()
	{
		setSize(500, 340);
		setPlain(true);
		setModal(true);
		setBlinkModal(false);
		setLayout(new FitLayout());

		final FormPanel formPanel = new FormPanel();
		formPanel.setBorders(false);
		formPanel.setBodyBorder(false);
		formPanel.setLabelWidth(55);
		formPanel.setPadding(5);
		formPanel.setHeaderVisible(false);
		
		String labelStyle = "width:70px;";
		FormData formdata = new FormData("95%");
		
		//final DistTextField tfSystemName = new DistTextField(m_copiedSystem,m_copiedSystem.GetSystemName(),"新系统名称");
		final DistTextField tfSystemName = new DistTextField(m_gwtSystem,m_gwtSystem.GetSystemName(),"新系统名称");
		tfSystemName.setLabelStyle(labelStyle);
		tfSystemName.setMaxLength(32);

		formPanel.add(tfSystemName, formdata);
		
		final TextField<String> tfSystemNo = new TextField<String>();
		tfSystemNo.setFieldLabel("新系统号");
		tfSystemNo.setLabelStyle(labelStyle);
		tfSystemNo.setMaxLength(32);
		formPanel.add(tfSystemNo,formdata);
		
	
		Button btnOK = new Button("确定", new SelectionListener<ButtonEvent>() {
			public void componentSelected(ButtonEvent ce) {
				if (!formPanel.isValid()) {
					return;
				}
				m_copiedSystem.SetValue(m_iOldSystemId);
				m_copiedSystem.SetValue(tfSystemName.getValue(), tfSystemNo.getValue());
				m_cpSysService.Save(m_copiedSystem, new AsyncCallback<Boolean>() {

					public void onFailure(Throwable caught) {
						caught.printStackTrace();
						MessageBox.alert("错误信息", "保存失败", null);
					}

					@SuppressWarnings("deprecation")
					public void onSuccess(Boolean suc) {
						if(loader != null)
							loader.load();
						if(suc)
						{
							MessageBox.info("系统复制成功", "系统复制成功！", null);
							close();
							if(loader == null)
							{
								CookieManage.Login();
								AppContext.GetEntryPoint().Login();
							}
						}
						else
						{
							tfSystemName.focus();
							tfSystemName.EnforceValidate();
						}
					}
				});
			}
		});
		addButton(btnOK);

		addButton(new Button("取消", new SelectionListener<ButtonEvent>() {
			@SuppressWarnings("deprecation")
			public void componentSelected(ButtonEvent ce) {
				close();
			}
		}));
		add(formPanel);

		setHeading("复制系统数据为：");
			
		show();
	}
}
